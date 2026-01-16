package com.bank.filetransfer.service;

import com.bank.filetransfer.exception.FileNotFoundExceptionCustom;
import com.bank.filetransfer.exception.FileStorageException;
import com.bank.filetransfer.util.FileMetadataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Ensure upload directory exists on startup
     */
    @PostConstruct
    public void init() {
        getUploadPath();
        log.info("Upload directory ready at: {}", Paths.get(uploadDir).toAbsolutePath());
    }

    /**
     * Always ensure directory exists (creates if missing)
     */
    private Path getUploadPath() {
        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(path); // ✅ creates folder if not exists
            return path;
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public void save(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Uploaded file is empty");
        }

        try {
            Path target = getUploadPath().resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            log.info("Uploaded: {} ({} bytes)", file.getOriginalFilename(), file.getSize());

        } catch (IOException e) {
            throw new FileStorageException("Failed to upload file: " + file.getOriginalFilename(), e);
        }
    }

    public List<FileMetadataDto> list() {

        try {
            return Files.list(getUploadPath())
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            BasicFileAttributes attrs =
                                    Files.readAttributes(path, BasicFileAttributes.class);

                            String fileName = path.getFileName().toString();
                            long expirySeconds = 10 * 60; // 10 minutes

                            long createdMillis = attrs.creationTime().toMillis();
                            long nowMillis = System.currentTimeMillis();

                            long remainingSeconds = Math.max(
                                    0,
                                    expirySeconds - ((nowMillis - createdMillis) / 1000)
                            );

                            double sizeMB = Math.round(
                                    (attrs.size() / (1024.0 * 1024.0)) * 100.0
                            ) / 100.0;

                            LocalDateTime uploadedAt =
                                    attrs.creationTime()
                                            .toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDateTime();

                            return new FileMetadataDto(
                                    fileName,
                                    sizeMB,
                                    uploadedAt,
                                    remainingSeconds
                            );

                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

        } catch (IOException e) {
            throw new FileStorageException("Failed to list files", e);
        }
    }

    public Resource load(String filename) {
        try {
            Path path = getUploadPath().resolve(filename);

            if (!Files.exists(path)) {
                throw new FileNotFoundExceptionCustom("File not found: " + filename);
            }

            return new UrlResource(path.toUri());

        } catch (MalformedURLException e) {
            throw new FileStorageException("Failed to load file: " + filename, e);
        }
    }

    public void delete(String filename) {
        try {
            Files.deleteIfExists(getUploadPath().resolve(filename));
            log.info("File deleted: {}", filename);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", filename);
        }
    }

    public void cleanup(int expiryMinutes) {
        long cutoff = System.currentTimeMillis() - expiryMinutes * 60 * 1000;

        try {
            Files.list(getUploadPath())
                    .filter(p -> p.toFile().lastModified() < cutoff)
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                            log.info("Expired file deleted: {}", p.getFileName());
                        } catch (IOException e) {
                            log.warn("Failed to delete expired file: {}", p.getFileName());
                        }
                    });
        } catch (IOException e) {
            log.error("Cleanup job failed", e);
        }
    }

    public ResponseEntity<StreamingResponseBody> downloadAndDelete(String filename) {

        Path filePath = getUploadPath().resolve(filename);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundExceptionCustom("File not found: " + filename);
        }

        StreamingResponseBody stream = outputStream -> {
            try (InputStream inputStream = Files.newInputStream(filePath)) {
                inputStream.transferTo(outputStream);
                outputStream.flush();

                log.info("File downloaded successfully: {}", filename);

            } catch (IOException ex) {
                log.error("Error while downloading file: {}", filename, ex);
                throw new FileStorageException("Error occurred while downloading file");

            } finally {
                try {
                    Files.deleteIfExists(filePath);
                    log.info("File deleted after download: {}", filename);
                } catch (IOException e) {
                    log.warn("Failed to delete file after download: {}", filename);
                }
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header("X-Download-Status", "SUCCESS")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    }
}

package com.bank.filetransfer.controller;

import com.bank.filetransfer.service.FileStorageService;
import com.bank.filetransfer.util.FileMetadataDto;
import com.bank.filetransfer.util.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {


    private final FileStorageService service;


    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {

        service.save(file);

        double sizeMB = Math.round((file.getSize() / (1024.0 * 1024.0)) * 100.0) / 100.0;

        FileUploadResponse response = new FileUploadResponse(
                file.getOriginalFilename(),
                sizeMB,
                "File uploaded successfully"
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/list")
    public ResponseEntity<List<FileMetadataDto>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable String filename) {
        return service.downloadAndDelete(filename);
    }
}

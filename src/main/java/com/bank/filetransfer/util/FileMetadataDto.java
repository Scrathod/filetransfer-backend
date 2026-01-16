package com.bank.filetransfer.util;

import java.time.LocalDateTime;

public class FileMetadataDto {

    private String fileName;
    private double sizeMB;
    private LocalDateTime uploadedAt;
    private long expiresInSeconds;

    public FileMetadataDto(String fileName, double sizeMB, LocalDateTime uploadedAt,long expiresInSeconds) {
        this.fileName = fileName;
        this.sizeMB = sizeMB;
        this.uploadedAt = uploadedAt;
        this.expiresInSeconds = expiresInSeconds;
    }
    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public String getFileName() {
        return fileName;
    }

    public double getSizeMB() {
        return sizeMB;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}
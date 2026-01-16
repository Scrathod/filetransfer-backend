package com.bank.filetransfer.util;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class FileUploadResponse {

    private String fileName;
    private double fileSizeMB;
    private String message;
    private LocalDateTime timestamp;

    public FileUploadResponse(String fileName, double fileSizeMB, String message) {
        this.fileName = fileName;
        this.fileSizeMB = fileSizeMB;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}

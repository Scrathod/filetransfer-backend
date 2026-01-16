package com.bank.filetransfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(FileNotFoundExceptionCustom.class)
    public ResponseEntity<ApiError> handle404(Exception ex) {
        return new ResponseEntity<>(new ApiError(ex.getMessage(), "NOT_FOUND", 404, LocalDateTime.now()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle500(Exception ex) {
        return new ResponseEntity<>(new ApiError("Server error", "ERROR", 500, LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("message", "File size exceeded. Max allowed is 80MB")
        );
    }
}

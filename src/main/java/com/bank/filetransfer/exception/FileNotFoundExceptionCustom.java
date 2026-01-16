package com.bank.filetransfer.exception;

public class FileNotFoundExceptionCustom extends RuntimeException {

    public FileNotFoundExceptionCustom(String message) {
        super(message);
    }

    public FileNotFoundExceptionCustom(String message, Throwable cause) {
        super(message, cause);
    }
}
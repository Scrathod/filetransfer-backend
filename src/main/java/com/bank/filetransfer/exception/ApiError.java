package com.bank.filetransfer.exception;

import java.time.LocalDateTime;

public record ApiError(String message, String error, int status, LocalDateTime time) {}

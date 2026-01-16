package com.bank.filetransfer.schedular;

import com.bank.filetransfer.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileCleanupScheduler {


    private final FileStorageService service;


    @Value("${file.expiry-minutes}")
    private int expiry;


    @Scheduled(fixedRate = 600000)
    public void run() {
        service.cleanup(expiry);
    }
}
package com.bank.filetransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FiletransferApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiletransferApplication.class, args);
	}

}

package com.example.csvexport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CsvExportApplication {
    public static void main(String[] args) {
        SpringApplication.run(CsvExportApplication.class, args);
    }
}

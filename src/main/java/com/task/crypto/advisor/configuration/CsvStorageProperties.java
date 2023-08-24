package com.task.crypto.advisor.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "csv")
public record CsvStorageProperties(String uploadDir) {
}

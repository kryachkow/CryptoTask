package com.task.crypto.advisor.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "csv")
public record CsvStorageProperties(String initialDir, String targetDir) {
}

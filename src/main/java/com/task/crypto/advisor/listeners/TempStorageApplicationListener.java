package com.task.crypto.advisor.listeners;

import com.task.crypto.advisor.configurations.CsvStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;


@RequiredArgsConstructor
@Component
@Slf4j
public class TempStorageApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final CsvStorageProperties csvStorageProperties;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (Files.exists(Path.of(csvStorageProperties.targetDir()))) {
            return;
        }
        log.info("Creating temporary resource folder.");
        try {
            ResourcePatternResolver resourcePatResolver = new PathMatchingResourcePatternResolver();
            Resource[] csvResources = resourcePatResolver.getResources("classpath*:" + csvStorageProperties.initialDir() + "/*.csv");
            Path tempDirectory = Files.createDirectory(Path.of(csvStorageProperties.targetDir()));
            for (Resource resource : csvResources) {
                Files.copy(resource.getInputStream(), tempDirectory.resolve(Objects.requireNonNull(resource.getFilename())));
            }
        } catch (IOException e) {
            log.error("Couldn't obtain resource files and place them in temporary directory", e);
            throw new RuntimeException("Couldn't obtain resource files and place them in temporary directory", e);
        }
        log.info("Temporary recourse folder successfully created and initial files uploaded.");

    }

}

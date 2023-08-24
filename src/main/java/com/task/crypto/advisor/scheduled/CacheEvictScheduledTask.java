package com.task.crypto.advisor.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheEvictScheduledTask {

    private final CacheManager cacheManager;

    @Value("${application.cache.requestCount}")
    private String requestCountCacheName;

    @Scheduled(fixedRate = 60000) // Run every minute
    public void evictCache() {
        log.info("Evicting request rate caches");
        Objects.requireNonNull(cacheManager.getCache(requestCountCacheName)).clear();
    }
}


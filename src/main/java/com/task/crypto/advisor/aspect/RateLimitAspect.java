package com.task.crypto.advisor.aspect;

import com.google.common.util.concurrent.RateLimiter;
import com.task.crypto.advisor.aspect.annotation.RateLimited;
import com.task.crypto.advisor.exception.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.Objects;
import java.util.Optional;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {

    private static final int REQUEST_LIMIT = 5;
    private final CacheManager cacheManager;
    @Value("${application.cache.requestCount}")
    private String requestCountCacheName;

    @Pointcut("@within(rateLimited) || @annotation(rateLimited)")
    public void rateLimitedClassOrMethod(RateLimited rateLimited) {
    }

    @Before(value = "@within(rateLimited) || @annotation(rateLimited)", argNames = "rateLimited")
    public void checkRateLimit(RateLimited rateLimited) {
        log.info("I ma trying");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = getIpAddress(request);
        int requestCount = getRequestCount(ipAddress);
        if (requestCount >= REQUEST_LIMIT) {
            throw new RateLimitException("Rate limit exceeded for IP: " + ipAddress);
        }
        incrementRequestCount(ipAddress);
    }

    public int getRequestCount(String ipAddress) {
        return Optional
                .ofNullable(cacheManager.getCache(requestCountCacheName))
                .map(cache -> cache.get(ipAddress, Integer.class))
                .orElse(0);
    }

    public void incrementRequestCount(String ipAddress) {
        log.info("Incrementing request count for '{}' ip address", ipAddress);
        int currentCount = getRequestCount(ipAddress);
        Objects.requireNonNull(cacheManager.getCache(requestCountCacheName)).put(ipAddress, currentCount + 1);
    }


    private String getIpAddress(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || remoteAddr.isBlank()) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

}

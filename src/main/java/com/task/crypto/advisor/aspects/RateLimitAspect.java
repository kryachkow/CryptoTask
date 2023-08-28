package com.task.crypto.advisor.aspects;

import com.task.crypto.advisor.aspects.annotations.RateLimited;
import com.task.crypto.advisor.exceptions.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {

    private static final int REQUEST_LIMIT = 5;
    private static final String X_FORWARDED_HEADER = "X-FORWARDED-FOR";
    private final CacheManager cacheManager;
    @Value("${application.cache.requestCount}")
    private String requestCountCacheName;

    @Pointcut("@within(rateLimited) || @annotation(rateLimited)")
    public void rateLimitedClassOrMethod(RateLimited rateLimited) {
    }

    @Before(value = "@within(rateLimited) || @annotation(rateLimited)", argNames = "rateLimited")
    public void checkRateLimit(RateLimited rateLimited) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = getIpAddress(request);
        AtomicInteger requestCount = getRequestCount(ipAddress);
        if (requestCount.get() >= REQUEST_LIMIT) {
            log.error("Rate limit exceeded for IP: " + ipAddress);
            throw new RateLimitException("Rate limit exceeded for IP: " + ipAddress);
        }
        requestCount.incrementAndGet();
    }

    public AtomicInteger getRequestCount(String ipAddress) {
        return Optional
                .ofNullable(cacheManager.getCache(requestCountCacheName))
                .map(cache -> cache.get(ipAddress, AtomicInteger.class))
                .orElseGet(() -> {
                    AtomicInteger returnAtomic = new AtomicInteger(0);
                    Objects.requireNonNull(cacheManager.getCache(requestCountCacheName)).put(ipAddress, returnAtomic);
                    return returnAtomic;
                });
    }

    private String getIpAddress(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader(X_FORWARDED_HEADER);
            if (remoteAddr == null || remoteAddr.isBlank()) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

}

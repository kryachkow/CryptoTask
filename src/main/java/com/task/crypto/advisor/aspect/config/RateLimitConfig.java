package com.task.crypto.advisor.aspect.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class RateLimitConfig {

    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(1.0);
    }
}

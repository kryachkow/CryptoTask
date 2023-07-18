package com.task.crypto.advisor.aspect;

import com.google.common.util.concurrent.RateLimiter;
import com.task.crypto.advisor.aspect.annotation.RateLimited;
import com.task.crypto.advisor.exception.RateLimitException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@RequiredArgsConstructor
public class RateLimitAspect {
    private final RateLimiter rateLimiter;


    @Pointcut("@within(rateLimited) || @annotation(rateLimited)")
    public void rateLimitedClassOrMethod(RateLimited rateLimited) {
    }

    @Before(value = "rateLimitedClassOrMethod(rateLimited)", argNames = "rateLimited")
    public void checkRateLimit(RateLimited rateLimited) {
        if(!rateLimiter.tryAcquire()){
            throw new RateLimitException("Too many requests");
        }
    }

}

package com.task.crypto.advisor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "Too many request attempts, try again later")
public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }

    public RateLimitException(String message, Exception cause) {
        super(message, cause);
    }
}

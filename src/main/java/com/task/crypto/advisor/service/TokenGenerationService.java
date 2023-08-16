package com.task.crypto.advisor.service;

import org.springframework.security.core.Authentication;

public interface TokenGenerationService {
    public String generateToken(Authentication authentication);
}

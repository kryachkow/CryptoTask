package com.task.crypto.advisor.controller;

import com.task.crypto.advisor.aspect.annotation.RateLimited;
import com.task.crypto.advisor.service.TokenGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RateLimited
public class AuthController {

    private final TokenGenerationService tokenGenerationService;

    @PostMapping("/token")
    public String token(Authentication authentication) {
        return tokenGenerationService.generateToken(authentication);
    }
}

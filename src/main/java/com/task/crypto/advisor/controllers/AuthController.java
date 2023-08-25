package com.task.crypto.advisor.controllers;

import com.task.crypto.advisor.aspects.annotations.RateLimited;
import com.task.crypto.advisor.services.TokenGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * The `AuthController` class handles http-basic authentication.
 **/
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RateLimited
@Slf4j
public class AuthController {

    private final TokenGenerationService tokenGenerationService;

    /**
     * Generates an authentication token for the provided authentication details.
     *
     * @param authentication The authentication details of the authorized user(username and password).
     * @return The generated authentication JWT.
     */
    @PostMapping("/token")
    public String token(Authentication authentication) {
        return tokenGenerationService.generateToken(authentication);
    }
}

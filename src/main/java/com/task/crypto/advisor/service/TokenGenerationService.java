package com.task.crypto.advisor.service;

import org.springframework.security.core.Authentication;

/**
 * The `TokenGenerationService` interface outlines a method for generating authentication tokens.
 * It provides a method to create an authentication token based on the provided authentication details.
 */
public interface TokenGenerationService {

    /**
     * Generates an authentication token for the given authentication details.
     *
     * @param authentication The authentication details of the authorized user.
     * @return A string representing the generated authentication token.
     */
    String generateToken(Authentication authentication);
}

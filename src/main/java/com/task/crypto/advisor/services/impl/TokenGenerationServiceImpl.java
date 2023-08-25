package com.task.crypto.advisor.services.impl;

import com.task.crypto.advisor.services.TokenGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenGenerationServiceImpl implements TokenGenerationService {

    private final JwtEncoder encoder;

    @Override
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String authorities = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claimsSet = JwtClaimsSet
                .builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(4, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", authorities)
                .build();
        return encoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }
}

package com.safelearn.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.safelearn.backend.domain.user.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class TokenService {

    private final String SECRET = "123456";

    public String createToken(User user) {
        try {
            var method = Algorithm.HMAC256(SECRET);
            return JWT.create()
                    .withIssuer("safelearn")
                    .withSubject(String.valueOf(user.getId()))
                    .withExpiresAt(expDate())
                    .sign(method);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Token creation failed.", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var method = Algorithm.HMAC256(SECRET);
            return JWT.require(method)
                    .withIssuer("safelearn")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token is not valid.");
        }
    }

    private Instant expDate() {
        return LocalDateTime.now().plusHours(48).toInstant(ZoneOffset.of("-03:00"));
    }

}

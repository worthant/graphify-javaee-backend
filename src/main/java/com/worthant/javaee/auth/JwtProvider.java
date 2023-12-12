package com.worthant.javaee.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.worthant.javaee.config.SecurityConfig;
import com.worthant.javaee.exceptions.ConfigurationException;
import com.worthant.javaee.exceptions.ServerException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class JwtProvider {
    public String generateToken(String username) throws ServerException {
        try {
            String secretKey = SecurityConfig.getJwtSecretKey();
            return JWT.create()
                    .withSubject(username)
                    .sign(Algorithm.HMAC256(secretKey));
        } catch (ConfigurationException e) {
            log.error("Error generating token: {}", e.getMessage());
            throw new ServerException("Internal server error.", e);
        }
    }
}

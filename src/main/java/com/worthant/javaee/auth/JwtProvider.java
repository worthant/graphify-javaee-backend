package com.worthant.javaee.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.worthant.javaee.Role;
import com.worthant.javaee.config.SecurityConfig;
import com.worthant.javaee.exceptions.ConfigurationException;
import com.worthant.javaee.exceptions.ServerException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@ApplicationScoped
public class JwtProvider {
    public String generateToken(String username, Role role, Long userId, String email) throws ServerException {
        try {
            String secretKey = SecurityConfig.getJwtSecretKey();
            return JWT.create()
                    .withSubject(username)
                    .withClaim("userId", userId)
                    .withClaim("role", role.toString())
                    .withClaim("email", email)
                    // Set expiry to 15 minutes
                    .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                    .sign(Algorithm.HMAC256(secretKey));
        } catch (ConfigurationException e) {
            log.error("Error generating token: {}", e.getMessage());
            throw new ServerException("Internal server error.", e);
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getSubject();
        } catch (JWTDecodeException exception){
            log.error("Error decoding token: {}", exception.getMessage());
            return null;
        }
    }

    public Role getRoleFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            String roleStr = jwt.getClaim("role").asString();
            return Role.valueOf(roleStr);
        } catch (JWTDecodeException | IllegalArgumentException exception){
            log.error("Error decoding role from token: {}", exception.getMessage());
            return null;
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asLong();
        } catch (JWTDecodeException exception) {
            log.error("Error decoding token: {}", exception.getMessage());
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Date expirationTime = jwt.getExpiresAt();
            return expirationTime != null && expirationTime.before(new Date());
        } catch (JWTDecodeException exception) {
            log.error("Error decoding token: {}", exception.getMessage());
            return true; // Consider an undecodable token as expired/invalid
        }
    }


}


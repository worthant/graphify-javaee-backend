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
                    .withExpiresAt(Instant.now().plus(25, ChronoUnit.MINUTES))
                    .sign(Algorithm.HMAC256(secretKey));
        } catch (ConfigurationException e) {
            log.error("Error generating token: {}", e.getMessage());
            throw new ServerException("Internal server error.", e);
        }
    }

    public String getEmailFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("email").asString();
        } catch (JWTDecodeException exception) {
            log.error("Error decoding token: {}", exception.getMessage());
            return null;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getSubject();
        } catch (JWTDecodeException exception) {
            log.error("Error decoding token: {}", exception.getMessage());
            return null;
        }
    }

    public Role getRoleFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            String roleStr = jwt.getClaim("role").asString();
            return Role.valueOf(roleStr);
        } catch (JWTDecodeException | IllegalArgumentException exception) {
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

    public static String getTimeUntilExpiration(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Date expirationTime = jwt.getExpiresAt();
            if (expirationTime != null) {
                long diff = expirationTime.getTime() - new Date().getTime();
                if (diff > 0) {
                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000) % 24;
                    long diffDays = diff / (24 * 60 * 60 * 1000);
                    return String.format("%d days, %d hours, %d minutes, %d seconds (%d milliseconds)",
                            diffDays, diffHours, diffMinutes, diffSeconds, diff);
                }
            }
        } catch (JWTDecodeException exception) {
            log.error("Error decoding token: {}", exception.getMessage());
        }
        return "Expired";
    }

    public static void main(String[] args) {
        // testing
        String token =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJib3JpczEyMzQ1NiIsInVzZXJJZCI6MTEsInJvbGUiOiJVU0VSIiwiZW1haWwiOiJiX2R2b3JraW5Abml1aXRtby5ydSIsImV4cCI6MTcwNTI2NDA3NX0.l7yW_Cwvtqzt6IxZYI-kd2U2cD_E6BVMyiywtuICTsU";
        System.out.println(getTimeUntilExpiration(token));
    }
}


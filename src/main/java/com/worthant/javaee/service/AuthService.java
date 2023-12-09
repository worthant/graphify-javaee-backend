package com.worthant.javaee.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.worthant.javaee.config.SecurityConfig;
import com.worthant.javaee.exceptions.ApplicationException;
import com.worthant.javaee.exceptions.ConfigurationException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;


@Stateless
@Slf4j
public class AuthService {

    @PersistenceContext
    private EntityManager em;

    public String registerUser(String username, String password) throws ApplicationException {
        try {
            String secretKey = SecurityConfig.getJwtSecretKey();
            String token = JWT.create()
                    .withSubject(username)
                    .sign(Algorithm.HMAC256(secretKey));
            return token;
        } catch (ConfigurationException e) {
            log.error("Error during user registration: {}", e.getMessage());
            throw new ApplicationException("Internal server error.", e);
        }
    }
}


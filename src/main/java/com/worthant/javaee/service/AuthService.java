package com.worthant.javaee.service;

import com.worthant.javaee.Role;
import com.worthant.javaee.auth.JwtProvider;
import com.worthant.javaee.auth.PasswordHasher;
import com.worthant.javaee.dao.UserDAO;
import com.worthant.javaee.dto.UserDTO;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.entity.UserSettingsEntity;
import com.worthant.javaee.exceptions.AuthenticationException;
import com.worthant.javaee.exceptions.ServerException;
import com.worthant.javaee.exceptions.UserExistsException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


@Stateless
@Slf4j
public class AuthService {

    @EJB
    private UserDAO userDAO;

    @Inject
    private JwtProvider jwtProvider;

    public String registerUser(String username, String password) throws UserExistsException, ServerException {
        // Check if the user exists
        if (userDAO.findByUsername(username).isPresent()) {
            throw new UserExistsException("User already exists: " + username);
        }

        // Create new user logic
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword(PasswordHasher.hashPassword(password.toCharArray())); // Hash password before setting
        newUser.setRole(Role.USER); // Default role
        userDAO.createUser(newUser);

        log.info("Successfully added user: {}", newUser);

        UserSettingsEntity settings = new UserSettingsEntity();
        settings.setUser(newUser);
        settings.setTheme("light");
        // TODO: Persist settings...

        return jwtProvider.generateToken(username);
    }

    public String authenticateUser(String username, String password) throws AuthenticationException, ServerException {
        Optional<UserEntity> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (PasswordHasher.checkPassword(password.toCharArray(), user.getPassword())) {
                return jwtProvider.generateToken(username);
            } else {
                throw new AuthenticationException("Password is incorrect");
            }
        }
        throw new AuthenticationException("There is nohomo with this username");
    }

}


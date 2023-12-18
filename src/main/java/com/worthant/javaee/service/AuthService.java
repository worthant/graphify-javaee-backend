package com.worthant.javaee.service;

import com.worthant.javaee.Role;
import com.worthant.javaee.auth.JwtProvider;
import com.worthant.javaee.auth.PasswordHasher;
import com.worthant.javaee.dao.UserDAO;
import com.worthant.javaee.dto.UserDTO;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.entity.UserSessionEntity;
import com.worthant.javaee.entity.UserSettingsEntity;
import com.worthant.javaee.exceptions.AuthenticationException;
import com.worthant.javaee.exceptions.ServerException;
import com.worthant.javaee.exceptions.UserExistsException;
import com.worthant.javaee.exceptions.UserNotFoundException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;


@Stateless
@Slf4j
public class AuthService {

    @EJB
    private UserDAO userDAO;

    @Inject
    private JwtProvider jwtProvider;

    public String registerUser(String username, String password) throws UserExistsException, ServerException, UserNotFoundException {
        // Check if the user exists
        if (userDAO.findByUsername(username).isPresent()) {
            throw new UserExistsException("User already exists: " + username);
        }

        UserEntity newUser = UserEntity.builder().username(username)
                .password(PasswordHasher.hashPassword(password.toCharArray()))
                .role(Role.USER).build();

        UserEntity createdUser = userDAO.createUser(newUser);

        log.info("Successfully added user: {}", createdUser);

        UserSettingsEntity settings = UserSettingsEntity.builder().user(newUser).theme("light").build();
        // TODO: Persist settings...

        String token = jwtProvider.generateToken(createdUser.getUsername(), Role.USER, createdUser.getId());
        userDAO.startNewSession(jwtProvider.getUserIdFromToken(token));

        return token;
    }

    public String authenticateUser(String username, String password) throws AuthenticationException, ServerException, UserNotFoundException {
        Optional<UserEntity> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (PasswordHasher.checkPassword(password.toCharArray(), user.getPassword())) {
                String token = jwtProvider.generateToken(user.getUsername(), Role.USER, user.getId());
                userDAO.startNewSession(jwtProvider.getUserIdFromToken(token));
                return token;
            } else {
                throw new AuthenticationException("Password is incorrect");
            }
        }
        throw new AuthenticationException("There is nohomo with this username");
    }

    public String authenticateAdmin(String username, String password) throws AuthenticationException, ServerException {
        Optional<UserEntity> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (PasswordHasher.checkPassword(password.toCharArray(), user.getPassword()) && user.getRole().equals(Role.ADMIN)) {
                return jwtProvider.generateToken(username, Role.ADMIN, user.getId());
            } else {
                throw new AuthenticationException("Invalid credentials for admin access");
            }
        }
        throw new AuthenticationException("Admin user not found");
    }

    public void saveSessionOnLogout(Long userId) throws UserNotFoundException {
        userDAO.endSession(userId);
    }

}


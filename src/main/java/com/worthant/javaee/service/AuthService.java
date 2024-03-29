package com.worthant.javaee.service;

import com.worthant.javaee.Role;
import com.worthant.javaee.auth.JwtProvider;
import com.worthant.javaee.auth.PasswordHasher;
import com.worthant.javaee.dao.UserDAO;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.entity.UserSettingsEntity;
import com.worthant.javaee.exceptions.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Optional;

import static com.worthant.javaee.utils.EmailSender.*;


@Stateless
@Slf4j
public class AuthService {

    @EJB
    private UserDAO userDAO;

    @Inject
    private JwtProvider jwtProvider;

    public String registerUser(String username, String password, String email) throws UserExistsException, ServerException, UserNotFoundException, InvalidEmailException {
        if (userDAO.findByUsername(username).isPresent()) {
            throw new UserExistsException("User already exists: " + username);
        }

        UserEntity newUser = UserEntity.builder()
                .username(username)
                .email(email)
                .password(PasswordHasher.hashPassword(password.toCharArray()))
                .role(Role.USER)
                .build();

        UserEntity createdUser = userDAO.createUser(newUser);

        log.info("Trying to send message to: {}", newUser.getEmail());
        sendSignUpEmail(newUser.getEmail(), username, password);

        log.info("Successfully added user: {}", createdUser);

        UserSettingsEntity settings = UserSettingsEntity.builder().user(newUser).theme("light").build();
        // TODO: Persist settings...

        String token = jwtProvider.generateToken(createdUser.getUsername(), Role.USER, createdUser.getId(), createdUser.getEmail());
        userDAO.startNewSession(jwtProvider.getUserIdFromToken(token));

        return token;
    }

    public String authenticateUser(String email, String password) throws AuthenticationException, ServerException, UserNotFoundException {
        Optional<UserEntity> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (PasswordHasher.checkPassword(password.toCharArray(), user.getPassword())) {
                String token = jwtProvider.generateToken(user.getUsername(), Role.USER, user.getId(), user.getEmail());
                userDAO.startNewSession(jwtProvider.getUserIdFromToken(token));
                return token;
            } else {
                throw new AuthenticationException("Password is incorrect");
            }
        }
        throw new AuthenticationException("There is nohomo with this email");
    }

    public String authenticateAdmin(String username, String password) throws AuthenticationException, ServerException {
        Optional<UserEntity> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (PasswordHasher.checkPassword(password.toCharArray(), user.getPassword()) && user.getRole().equals(Role.ADMIN)) {
                return jwtProvider.generateToken(username, Role.ADMIN, user.getId(), user.getEmail());
            } else {
                throw new AuthenticationException("Invalid credentials for admin access");
            }
        }
        throw new AuthenticationException("Admin user not found");
    }

    public void endSession(Long userId) throws UserNotFoundException {
        userDAO.endSession(userId);
    }

    public void remindPassword(String email) throws UserNotFoundException, ServerException {
        Optional<UserEntity> userEntityOptional = userDAO.findByEmail(email);
        if (userEntityOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        UserEntity userEntity = userEntityOptional.get();

        // Generate a random temporary password
        String temporaryPassword = RandomStringUtils.randomAlphanumeric(10);
        String hashedPassword = PasswordHasher.hashPassword(temporaryPassword.toCharArray());

        // Update user entity with new hashed password
        userEntity.setPassword(hashedPassword);
        userDAO.updateUser(userEntity);

        sendPasswordResetEmail(email, temporaryPassword);
    }
}


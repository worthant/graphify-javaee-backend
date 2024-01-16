package com.worthant.javaee.service;

import com.worthant.javaee.Role;
import com.worthant.javaee.auth.PasswordHasher;
import com.worthant.javaee.dao.PointDAO;
import com.worthant.javaee.dao.UserDAO;
import com.worthant.javaee.dto.PasswordDTO;
import com.worthant.javaee.dto.PointDTO;
import com.worthant.javaee.entity.PointEntity;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.exceptions.AuthenticationException;
import com.worthant.javaee.exceptions.PointNotFoundException;
import com.worthant.javaee.exceptions.ServerException;
import com.worthant.javaee.exceptions.UserNotFoundException;
import com.worthant.javaee.utils.AreaChecker;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static com.worthant.javaee.utils.EmailSender.sendPasswordChangeEmail;
import static com.worthant.javaee.utils.EmailSender.sendPasswordResetEmail;

@Stateless
@Slf4j
public class UserService {
    @EJB
    private UserDAO userDAO;

    @EJB
    private PointDAO pointDAO;

    public List<PointDTO> getUserPoints(Long userId) throws UserNotFoundException {
        List<PointEntity> points = pointDAO.getPointsByUserId(userId);
        return points.stream()
                .map(p -> new PointDTO(p.getX(), p.getY(), p.getR(), p.isResult()))
                .collect(Collectors.toList());
    }

    public PointDTO addUserPoint(Long userId, PointDTO pointDTO) throws UserNotFoundException {
        UserEntity user = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean isInsideArea = AreaChecker.isInArea(pointDTO.getX(), pointDTO.getY(), pointDTO.getR());
        PointEntity pointEntity = PointEntity.builder()
                .x(pointDTO.getX())
                .y(pointDTO.getY())
                .r(pointDTO.getR())
                .result(isInsideArea)
                .user(user)
                .build();

        pointDAO.addPointByUserId(userId, pointEntity);
        PointDTO point = PointDTO.builder()
                .x(pointEntity.getX())
                .y(pointEntity.getY())
                .r(pointEntity.getR())
                .result(pointEntity.isResult())
                .build();
        return point;
    }

    public void deleteUserPoints(Long userId) throws UserNotFoundException {
        pointDAO.removeAllPointsByUserId(userId);
    }

    public void deleteSinglePoint(Long userId, PointDTO pointDTO) throws UserNotFoundException, PointNotFoundException {
        pointDAO.removePointByUserId(userId, pointDTO);
    }

    public void changePassword(Long userId, String email, PasswordDTO passwordDTO) throws UserNotFoundException, ServerException, IllegalStateException {
        if (passwordDTO == null || passwordDTO.getPassword() == null) {
            throw new IllegalStateException("Invalid JSON or password data provided");
        }

        UserEntity user = userDAO.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        String newPassword = passwordDTO.getPassword();
        user.setPassword(PasswordHasher.hashPassword(newPassword.toCharArray()));
        userDAO.updateUser(user);
        sendPasswordChangeEmail(email, newPassword);
    }

    public List<PointDTO> redrawAllPoints(Long userId, double newRadius) throws UserNotFoundException {
        pointDAO.updatePointsRadius(userId, newRadius);
        List<PointEntity> userPoints = pointDAO.getPointsByUserId(userId);
        return userPoints.stream()
                .map(p -> new PointDTO(p.getX(), p.getY(), p.getR(), p.isResult()))
                .collect(Collectors.toList());
    }

    public void updateLastActivity(Long userId) {
        userDAO.updateLastActivity(userId);
    }

}

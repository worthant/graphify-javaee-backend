package com.worthant.javaee.service;

import com.worthant.javaee.Role;
import com.worthant.javaee.dao.PointDAO;
import com.worthant.javaee.dao.UserDAO;
import com.worthant.javaee.dto.ExtendedUserDTO;
import com.worthant.javaee.dto.PointDTO;
import com.worthant.javaee.dto.SessionsDTO;
import com.worthant.javaee.dto.UserDTO;
import com.worthant.javaee.entity.PointEntity;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.exceptions.UserNotFoundException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Slf4j
public class AdminService {
    @EJB
    private UserDAO userDAO;

    @EJB
    private PointDAO pointDAO;

    public List<ExtendedUserDTO> getAllUsers() {
        List<UserEntity> users = userDAO.getAllUsers();
        return users.stream()
                .map(user -> ExtendedUserDTO.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .username(user.getUsername())
                        .build())
                .collect(Collectors.toList());
    }

    public void promoteToAdmin(Long userId) throws UserNotFoundException {
        userDAO.promoteToAdminById(userId);
    }

    public List<PointDTO> getUserPoints(Long userId) throws UserNotFoundException {
        List<PointEntity> points = pointDAO.getPointsByUserId(userId);
        return points.stream()
                .map(p -> PointDTO.builder()
                        .x(p.getX())
                        .y(p.getY())
                        .r(p.getR())
                        .result(p.isResult()).build())
                .collect(Collectors.toList());
    }

    public void removeUser(Long userId) throws UserNotFoundException {
        UserEntity user = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        userDAO.removeUser(user);
    }

    public List<SessionsDTO> getUserSessions(Long userId) throws UserNotFoundException {
        return userDAO.getUserSessions(userId);
    }

    public long getNumberOfUserPoints(Long userId) throws UserNotFoundException {
        return pointDAO.getNumberOfUserPoints(userId);
    }

    public Duration getLastUserSessionDuration(Long userId) throws UserNotFoundException {
        List<SessionsDTO> sessions = userDAO.getUserSessions(userId);

        if (sessions.isEmpty()) {
            throw new UserNotFoundException("No sessions found for user with ID " + userId);
        }

        SessionsDTO lastSession = sessions.get(sessions.size() - 1);

        LocalDateTime start = lastSession.getSessionStart();
        LocalDateTime end = lastSession.getSessionEnd();

        return Duration.between(start, end);
    }

}

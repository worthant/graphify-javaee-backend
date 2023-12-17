package com.worthant.javaee.service;

import com.worthant.javaee.Role;
import com.worthant.javaee.dao.PointDAO;
import com.worthant.javaee.dao.UserDAO;
import com.worthant.javaee.dto.PointDTO;
import com.worthant.javaee.dto.SessionsDTO;
import com.worthant.javaee.dto.UserDTO;
import com.worthant.javaee.entity.PointEntity;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.exceptions.UserNotFoundException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Slf4j
public class AdminService {
    @EJB
    private UserDAO userDAO;

    @EJB
    private PointDAO pointDAO;

    public List<UserDTO> getAllUsers() {
        List<UserEntity> users = userDAO.getAllUsers();
        return users.stream()
                .map(user -> UserDTO.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .build())
                .collect(Collectors.toList());
    }

    // TODO: Include logic to calculate session lengths if available
    public List<SessionsDTO> getUserSessions() {
        return List.of(SessionsDTO.builder().build());
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
}

package com.worthant.javaee.service;

import com.worthant.javaee.Role;
import com.worthant.javaee.dao.PointDAO;
import com.worthant.javaee.dao.UserDAO;
import com.worthant.javaee.dto.PointDTO;
import com.worthant.javaee.entity.PointEntity;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.exceptions.AuthenticationException;
import com.worthant.javaee.exceptions.PointNotFoundException;
import com.worthant.javaee.exceptions.UserNotFoundException;
import com.worthant.javaee.utils.AreaChecker;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

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

    public void addUserPoint(Long userId, PointDTO pointDTO) throws UserNotFoundException {
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
    }

    public void deleteUserPoints(Long userId) throws UserNotFoundException {
        pointDAO.removeAllPointsByUserId(userId);
    }

    public void deleteSinglePoint(Long userId, PointDTO pointDTO) throws UserNotFoundException, PointNotFoundException {
        pointDAO.removePointByUserId(userId, pointDTO);
    }
}

package com.worthant.javaee.dao;

import com.worthant.javaee.dto.PointDTO;
import com.worthant.javaee.entity.PointEntity;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.exceptions.PointNotFoundException;
import com.worthant.javaee.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface PointDAO {
    List<PointEntity> getPointsByUserId(Long userId) throws UserNotFoundException;
    void addPointByUserId(Long userId, PointEntity point) throws UserNotFoundException;
    void removePointByUserId(Long userId, PointDTO pointDTO) throws UserNotFoundException, PointNotFoundException;
    void removePointsByUserId(Long userId, List<PointEntity> points) throws UserNotFoundException;
    void removeAllPointsByUserId(Long userId) throws UserNotFoundException;
    void updatePointsRadius(Long userId, double newRadius) throws UserNotFoundException;
    Optional<UserEntity> findById(Long userId);

    long getNumberOfUserPoints(Long userId) throws UserNotFoundException;
}

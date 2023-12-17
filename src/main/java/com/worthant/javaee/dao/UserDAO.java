package com.worthant.javaee.dao;

import com.worthant.javaee.dto.UserDTO;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.exceptions.ServerException;
import com.worthant.javaee.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findById(Long userId);
    UserEntity createUser(UserEntity user) throws ServerException;

    List<UserEntity> getAllUsers();

    void promoteToAdminById(Long id) throws UserNotFoundException;
}

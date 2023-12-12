package com.worthant.javaee.dao;

import com.worthant.javaee.dto.UserDTO;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.exceptions.ServerException;

import java.util.Optional;

public interface UserDAO {
    Optional<UserEntity> findByUsername(String username);
    void createUser(UserEntity user) throws ServerException;
}

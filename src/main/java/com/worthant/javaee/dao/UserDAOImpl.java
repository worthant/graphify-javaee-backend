package com.worthant.javaee.dao;

import com.worthant.javaee.Role;
import com.worthant.javaee.dto.UserDTO;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.exceptions.ServerException;
import com.worthant.javaee.exceptions.UserNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@Stateless
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        TypedQuery<UserEntity> query = entityManager
                .createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst();
    }

    @Override
    public Optional<UserEntity> findById(Long userId) {
        UserEntity user = entityManager.find(UserEntity.class, userId);
        return Optional.ofNullable(user);
    }

    @Override
    public UserEntity createUser(UserEntity user) throws ServerException {
        try {
            entityManager.persist(user);
            entityManager.flush();
            return user;
        } catch (Exception e) {
            throw new ServerException("Error creating User: ", e);
        }
    }

    @Override
    public List<UserEntity> getAllUsers() {
        TypedQuery<UserEntity> query = entityManager
                .createQuery("SELECT u FROM UserEntity u", UserEntity.class);
        return query.getResultList();
    }

    @Override
    public void promoteToAdminById(Long id) throws UserNotFoundException {
        UserEntity user = findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));
        user.setRole(Role.ADMIN);
        entityManager.merge(user);
    }

}

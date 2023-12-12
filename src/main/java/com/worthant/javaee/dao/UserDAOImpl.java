package com.worthant.javaee.dao;

import com.worthant.javaee.dto.UserDTO;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.exceptions.ServerException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

@Stateless
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<UserEntity> findByUsername(String username) {
        TypedQuery<UserEntity> query = entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst();
    }

    public void createUser(UserEntity user) throws ServerException {
        try {
            entityManager.persist(user);
        } catch (Exception e) {
            throw new ServerException("Error creating User: ", e);
        }
    }

    // Implement other methods...
}

package com.worthant.javaee.dao;

import com.worthant.javaee.Role;
import com.worthant.javaee.dto.SessionsDTO;
import com.worthant.javaee.dto.UserDTO;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.entity.UserSessionEntity;
import com.worthant.javaee.exceptions.ServerException;
import com.worthant.javaee.exceptions.UserNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                .createQuery("SELECT u FROM UserEntity u WHERE u.role = 'USER'", UserEntity.class);
        return query.getResultList();
    }

    @Override
    public void promoteToAdminById(Long id) throws UserNotFoundException {
        UserEntity user = findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));
        user.setRole(Role.ADMIN);
        entityManager.merge(user);
    }

    @Override
    public void updateUser(UserEntity user) throws ServerException {
        try {
            entityManager.merge(user);
        } catch (Exception e) {
            throw new ServerException("Error updating User: ", e);
        }
    }

    @Override
    public void removeUser(UserEntity user) {
        entityManager.remove(user);
    }

    @Override
    public List<SessionsDTO> getUserSessions(Long userId) throws UserNotFoundException {
        findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        TypedQuery<UserSessionEntity> query = entityManager.createQuery(
                "SELECT s FROM UserSessionEntity s WHERE s.user.id = :userId", UserSessionEntity.class);
        query.setParameter("userId", userId);

        List<UserSessionEntity> sessionEntities = query.getResultList();
        List<SessionsDTO> sessionDTOs = new ArrayList<>();
        for (UserSessionEntity sessionEntity : sessionEntities) {
            sessionDTOs.add(new SessionsDTO(sessionEntity.getSessionStart(), sessionEntity.getSessionEnd()));
        }
        return sessionDTOs;
    }

    @Override
    public void startNewSession(Long userId) throws UserNotFoundException {
        UserEntity user = findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        UserSessionEntity session = UserSessionEntity.builder()
                .user(user)
                .sessionStart(LocalDateTime.now())
                .build();
        entityManager.persist(session);
    }

    @Override
    public void endSession(Long userId) throws UserNotFoundException {
        findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        UserSessionEntity lastSession = entityManager.createQuery(
                        "SELECT s FROM UserSessionEntity s WHERE s.user.id = :userId ORDER BY s.sessionStart DESC",
                        UserSessionEntity.class)
                .setParameter("userId", userId)
                .setMaxResults(1)
                .getSingleResult();

        lastSession.setSessionEnd(LocalDateTime.now());
        entityManager.merge(lastSession);
    }

}

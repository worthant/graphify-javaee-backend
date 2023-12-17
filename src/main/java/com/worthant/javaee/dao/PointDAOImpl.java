package com.worthant.javaee.dao;

import com.worthant.javaee.dto.PointDTO;
import com.worthant.javaee.entity.PointEntity;
import com.worthant.javaee.entity.UserEntity;
import com.worthant.javaee.exceptions.PointNotFoundException;
import com.worthant.javaee.exceptions.UserNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Stateless
public class PointDAOImpl implements PointDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PointEntity> getPointsByUserId(Long userId) throws UserNotFoundException {
        findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        return entityManager.createQuery("SELECT p FROM PointEntity p WHERE p.user.id = :userId", PointEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }


    // TODO: User this in UserService to add Points
    // TODO: Also remember to check if the point is in or out in the UserService
    @Override
    public void addPointByUserId(Long userId, PointEntity point) throws UserNotFoundException {
        UserEntity user = findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        point.setUser(user);
        entityManager.persist(point);
    }


    @Override
    public void removePointByUserId(Long userId, PointDTO pointDTO) throws UserNotFoundException, PointNotFoundException {
        UserEntity user = findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));

        PointEntity pointToDelete = entityManager
                .createQuery("SELECT p FROM PointEntity p WHERE p.user = :user AND p.x = :x AND p.y = :y AND p.r = :r AND p.result = :result", PointEntity.class)
                .setParameter("user", user)
                .setParameter("x", pointDTO.getX())
                .setParameter("y", pointDTO.getY())
                .setParameter("r", pointDTO.getR())
                .setParameter("result", pointDTO.isResult())
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new PointNotFoundException("Point not found"));

        entityManager.remove(pointToDelete);
    }


    @Override
    public void removePointsByUserId(Long userId, List<PointEntity> points) throws UserNotFoundException {
        // TODO: User it, if needed to remove selected points, which would be pretty handy
    }


    @Override
    public void removeAllPointsByUserId(Long userId) throws UserNotFoundException {
        findById(userId).orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        entityManager.createQuery("DELETE FROM PointEntity p WHERE p.user.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    public Optional<UserEntity> findById(Long userId) {
        UserEntity user = entityManager.find(UserEntity.class, userId);
        return Optional.ofNullable(user);
    }


}

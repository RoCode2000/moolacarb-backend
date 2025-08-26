package com.uow.moolacarb.service;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.uow.moolacarb.model.User;
import com.uow.moolacarb.repository.UserJdbcRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserJdbcRepository repo;

    public UserService(UserJdbcRepository repo){
        this.repo = repo;
    }

    public void create(User u){
        if (u.getPremium() == null || u.getPremium().isBlank()) u.setPremium("F");
        if (u.getUserStatus() == null || u.getUserStatus().isBlank()) u.setUserStatus("A");
        if (u.getCreatedDate() == null) u.setCreatedDate(LocalDateTime.now());
        repo.insert(u);
    }

    public void update(User u) {
        repo.update(u);
    }

    public User findByEmailAndLoginType(String email, String loginType){
        return repo.findByEmailAndLoginType(email, loginType);
    }

    public User findByFirebaseId(String firebaseId) {
        return repo.findByFirebaseId(firebaseId);
    }

    public long userCount(String type) {
        return repo.userCount(type);
    }

    public List<User> getUsers(Integer limit) {
        return repo.getUsers(limit);
    }

    public User updateStatus(String userId, String status){
        User existing = repo.findById(userId);
        if (existing == null) {
            throw new NoSuchElementException("User not found");
        }

        String desiredDb = "Banned".equalsIgnoreCase(status) ? "B" :
                       "Active".equalsIgnoreCase(status) ? "A" : null;
        if (desiredDb == null) throw new EntityNotFoundException("Status not available");


        String currentDb = existing.getUserStatus(); // e.g., "A" or "B"
        if (!desiredDb.equalsIgnoreCase(currentDb)) {
            int rows = repo.updateUserStatus(userId, status);
            if (rows == 0) throw new IllegalStateException("Update failed");
            existing.setUserStatus(desiredDb); // keep consistent with DB
        }
        return existing;
    }
}

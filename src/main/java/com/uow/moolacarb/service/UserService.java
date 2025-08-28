package com.uow.moolacarb.service;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.uow.moolacarb.DataTransferObject.UserUpdateRequest;
import com.uow.moolacarb.model.User;
import com.uow.moolacarb.repository.UserJdbcRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserJdbcRepository repo;

    public UserService(UserJdbcRepository repo) {
        this.repo = repo;
    }

    public void create(User u) {
        if (u.getPremium() == null || u.getPremium().isBlank())
            u.setPremium("F");
        if (u.getUserStatus() == null || u.getUserStatus().isBlank())
            u.setUserStatus("A");
        if (u.getCreatedDate() == null)
            u.setCreatedDate(LocalDateTime.now());
        repo.insert(u);
    }

    public void update(User u) {
        repo.update(u);
    }

    public User findByEmailAndLoginType(String email, String loginType) {
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

    public List<User> getPremiumUsers(Integer limit) {
        return repo.getPremiumUsers(limit);
    }

    // TODO update has no return since i will re-call the page
    public User updateUser(String userId, UserUpdateRequest req) {
        User existing = repo.findById(userId);
        if (existing == null) {
            throw new NoSuchElementException("User not found");
        }

        // Normalize & validate (examples)
        String newStatus = normalizeStatus(req.getStatus()); // returns "A"/"B" or null if not provided
        String newPremium = normalizePremium(req.getPremium()); // returns "P"/"F" or null if not provided

        if (req.getStatus() != null && newStatus == null)
            throw new IllegalArgumentException("Invalid status");
        if (req.getPremium() != null && newPremium == null)
            throw new IllegalArgumentException("Invalid premium");

        // Only update what was provided & changed
        boolean changed = false;
        if (newStatus != null && !newStatus.equalsIgnoreCase(existing.getUserStatus())) {
            repo.updateStatus(userId, newStatus);
            existing.setUserStatus(newStatus);
            changed = true;
        }
        if (newPremium != null && !newPremium.equalsIgnoreCase(existing.getPremium())) {
            repo.updatePremium(userId, newPremium);
            existing.setPremium(newPremium);
            changed = true;
        }
        return existing;
    }

    private String normalizeStatus(String s) {
        if (s == null)
            return null;
        if ("A".equalsIgnoreCase(s) || "Active".equalsIgnoreCase(s))
            return "A";
        if ("B".equalsIgnoreCase(s) || "Banned".equalsIgnoreCase(s))
            return "B";
        return null;
    }

    private String normalizePremium(String p) {
        if (p == null)
            return null;
        if ("P".equalsIgnoreCase(p) || "Premium".equalsIgnoreCase(p))
            return "P";
        if ("F".equalsIgnoreCase(p) || "Free".equalsIgnoreCase(p))
            return "F";
        return null;
    }
}

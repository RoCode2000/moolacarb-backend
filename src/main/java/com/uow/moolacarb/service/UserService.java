package com.uow.moolacarb.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.uow.moolacarb.DataTransferObject.UserUpdateRequest;
import com.uow.moolacarb.model.User;
import com.uow.moolacarb.repository.HeightHistoryRepository;
import com.uow.moolacarb.repository.UserJdbcRepository;
import com.uow.moolacarb.repository.WeightHistoryRepository;

@Service
public class UserService {
    private final UserJdbcRepository repo;
    private final WeightHistoryRepository weightRepo;
    private final HeightHistoryRepository heightRepo;

    public UserService(UserJdbcRepository repo,
                       WeightHistoryRepository weightRepo,
                       HeightHistoryRepository heightRepo) {
        this.repo = repo;
        this.weightRepo = weightRepo;
        this.heightRepo = heightRepo;
    }

    public void create(User u) {
        if (u.getPremium() == null || u.getPremium().isBlank())
            u.setPremium("P");
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

    public User updateUser(String userId, UserUpdateRequest req) {
        User existing = repo.findById(userId);
        if (existing == null) {
            throw new NoSuchElementException("User not found");
        }

        String newStatus = normalizeStatus(req.getStatus());
        String newPremium = normalizePremium(req.getPremium());

        if (req.getStatus() != null && newStatus == null)
            throw new IllegalArgumentException("Invalid status");
        if (req.getPremium() != null && newPremium == null)
            throw new IllegalArgumentException("Invalid premium");

        if (newStatus != null && !newStatus.equalsIgnoreCase(existing.getUserStatus())) {
            repo.updateStatus(userId, newStatus);
            existing.setUserStatus(newStatus);
        }
        if (newPremium != null && !newPremium.equalsIgnoreCase(existing.getPremium())) {
            repo.updatePremium(userId, newPremium);
            existing.setPremium(newPremium);
        }
        return existing;
    }

    private String normalizeStatus(String s) {
        if (s == null) return null;
        if ("A".equalsIgnoreCase(s) || "Active".equalsIgnoreCase(s)) return "A";
        if ("B".equalsIgnoreCase(s) || "Banned".equalsIgnoreCase(s)) return "B";
        return null;
    }

    private String normalizePremium(String p) {
        if (p == null) return null;
        if ("P".equalsIgnoreCase(p) || "Premium".equalsIgnoreCase(p)) return "P";
        if ("F".equalsIgnoreCase(p) || "Free".equalsIgnoreCase(p)) return "F";
        return null;
    }

public User updateOnboarding(User user, Float weight, Float height) {
    User existing = repo.findByFirebaseId(user.getFirebaseId());
    if (existing == null) {
        throw new NoSuchElementException("User not found with firebaseId: " + user.getFirebaseId());
    }

    // Update base user table
    repo.updateOnboarding(
        user.getFirebaseId(),
        user.getGender(),
        user.getDob(),
        user.getExercise(),
        user.getGoals(),
        user.getTimeframe()
    );

    // Insert weight if provided
    if (weight != null) {
        weightRepo.insert(user.getFirebaseId(), weight);
    }

    // Insert height if provided
    if (height != null) {
        heightRepo.insert(user.getFirebaseId(), height);
    }

    // Update in-memory user object
    existing.setGender(user.getGender());
    existing.setDob(user.getDob());
    existing.setExercise(user.getExercise());
    existing.setGoals(user.getGoals());
    existing.setTimeframe(user.getTimeframe());

    return existing;
}

}

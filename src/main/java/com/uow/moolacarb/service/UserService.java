package com.uow.moolacarb.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.uow.moolacarb.model.User;
import com.uow.moolacarb.repository.UserJdbcRepository;

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

    public boolean findByEmailAndLoginType(String email, String loginType){
        return repo.existsByEmailAndLoginType(email, loginType);
    }
}

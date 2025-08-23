package com.uow.moolacarb.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uow.moolacarb.model.MealLog;
import com.uow.moolacarb.repository.MealLogRepository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Service
public class MealLogService {
    private final MealLogRepository repo;
    private final JdbcTemplate jdbc;

    public MealLogService(MealLogRepository repo, JdbcTemplate jdbc) {
        this.repo = repo;
        this.jdbc = jdbc;
    }

    public List<MealLog> listByFirebase(String firebaseId) {
        return repo.findByFirebaseId(firebaseId);
    }

    public MealLog create(MealLog m) {
        return repo.save(m);
    }

    @Transactional
    public MealLog createByFirebase(String firebaseId, MealLog payload) {
        final String sql = """
            INSERT INTO meallog (foodsConsumed, timeConsumed, remarks, calories, userId)
            SELECT ?, ?, ?, ?, u.userId
            FROM `user` u
            WHERE u.firebaseId = ?
        """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, payload.getFoodsConsumed());
            ps.setTimestamp(2, Timestamp.valueOf(payload.getTimeConsumed())); // LocalDateTime -> DATETIME
            ps.setString(3, payload.getRemarks());
            if (payload.getCalories() != null) ps.setInt(4, payload.getCalories()); else ps.setObject(4, null);
            ps.setString(5, firebaseId);
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) {
            throw new IllegalStateException("Insert failed (unknown firebaseId?): " + firebaseId);
        }
        return repo.findById(key.intValue()).orElseThrow();
    }

    public MealLog update(Integer id, MealLog payload) {
        MealLog m = repo.findById(id).orElseThrow();
        m.setFoodsConsumed(payload.getFoodsConsumed());
        m.setCalories(payload.getCalories());
        m.setTimeConsumed(payload.getTimeConsumed());
        m.setRemarks(payload.getRemarks());
        return repo.save(m);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}

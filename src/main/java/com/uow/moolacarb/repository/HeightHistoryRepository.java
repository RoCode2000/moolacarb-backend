// src/main/java/com/uow/moolacarb/repository/HeightHistoryRepository.java
package com.uow.moolacarb.repository;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.uow.moolacarb.model.HeightHistory;

@Repository
public class HeightHistoryRepository {

    private final JdbcTemplate jdbc;

    public HeightHistoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Insert new height record, mark old ones as not current
    public int insert(String firebaseId, float height) {
        jdbc.update("UPDATE heightHistory SET isCurrent='N' WHERE firebaseId=? AND isCurrent='Y'", firebaseId);
        return jdbc.update(
            "INSERT INTO heightHistory (firebaseId, height, lastUpdated, isCurrent) VALUES (?, ?, NOW(), 'Y')",
            firebaseId, height
        );
    }

    // Get the latest (current) height; return null if not found
    public HeightHistory getCurrent(String firebaseId) {
        final String sql = "SELECT * FROM heightHistory WHERE firebaseId=? AND isCurrent='Y'";
        try {
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(HeightHistory.class), firebaseId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Get full history (newest first)
    public List<HeightHistory> getHistory(String firebaseId) {
        final String sql = "SELECT * FROM heightHistory WHERE firebaseId=? ORDER BY lastUpdated DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(HeightHistory.class), firebaseId);
    }
}

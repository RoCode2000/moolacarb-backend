package com.uow.moolacarb.repository;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.uow.moolacarb.model.WeightHistory;

@Repository
public class WeightHistoryRepository {

    private final JdbcTemplate jdbc;

    public WeightHistoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Insert new weight record, mark old ones as not current
    public int insert(String firebaseId, float weight) {
        // Set any old "isCurrent" entries for this user to N
        jdbc.update("UPDATE weightHistory SET isCurrent='N' WHERE firebaseId=? AND isCurrent='Y'", firebaseId);

        // Insert new record as current
        return jdbc.update(
            "INSERT INTO weightHistory (firebaseId, weight, lastUpdated, isCurrent) VALUES (?, ?, NOW(), 'Y')",
            firebaseId, weight
        );
    }

    // Get the latest weight
    public WeightHistory getCurrent(String firebaseId) {
        String sql = "SELECT * FROM weightHistory WHERE firebaseId=? AND isCurrent='Y'";
        try {
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(WeightHistory.class), firebaseId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Get the full history
    public List<WeightHistory> getHistory(String firebaseId) {
        String sql = "SELECT * FROM weightHistory WHERE firebaseId=? ORDER BY lastUpdated DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(WeightHistory.class), firebaseId);
    }
}

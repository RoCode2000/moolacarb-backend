package com.uow.moolacarb.repository;

import java.util.List;

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
        // Set old records to not current
        jdbc.update("UPDATE heightHistory SET isCurrent='N' WHERE firebaseId=? AND isCurrent='Y'", firebaseId);

        // Insert new record
        return jdbc.update(
            "INSERT INTO heightHistory (firebaseId, height, lastUpdated, isCurrent) VALUES (?, ?, NOW(), 'Y')",
            firebaseId, height
        );
    }

    // Get the latest height
    public HeightHistory getCurrent(String firebaseId) {
        String sql = "SELECT * FROM heightHistory WHERE firebaseId=? AND isCurrent='Y'";
        return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(HeightHistory.class), firebaseId);
    }

    // Get full history
    public List<HeightHistory> getHistory(String firebaseId) {
        String sql = "SELECT * FROM heightHistory WHERE firebaseId=? ORDER BY lastUpdated DESC";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(HeightHistory.class), firebaseId);
    }
}

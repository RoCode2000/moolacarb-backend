package com.uow.moolacarb.repository;

import com.uow.moolacarb.model.TestUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TestUserJdbcRepository {

    private final JdbcTemplate jdbc;

    public TestUserJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<TestUser> userMapper = (rs, rowNum) -> {
        TestUser user = new TestUser();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    };

    public List<TestUser> findAll() {
        return jdbc.query("SELECT * FROM test_users", userMapper);
    }

    public List<TestUser> findByName(String name) {
        return jdbc.query("SELECT * FROM test_users WHERE name = ?", userMapper, name);
    }

    public int insert(String name) {
        return jdbc.update("INSERT INTO test_users (name) VALUES (?)", name);
    }

    public int deleteById(int id) {
        return jdbc.update("DELETE FROM test_users WHERE id = ?", id);
    }
}

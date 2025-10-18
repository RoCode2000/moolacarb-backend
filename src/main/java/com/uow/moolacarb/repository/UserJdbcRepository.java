package com.uow.moolacarb.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.uow.moolacarb.model.User;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Repository
public class UserJdbcRepository {

    private final JdbcTemplate jdbc;

    public UserJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User u = new User();

            u.setUserId(rs.getString("userId"));
            u.setFirstName(rs.getString("firstName"));
            u.setLastName(rs.getString("lastName"));
            u.setUsername(rs.getString("username"));
            u.setPasswordHash(rs.getString("passwordHash"));
            u.setEmail(rs.getString("email"));
            u.setGender(rs.getString("gender"));

            if (rs.getTimestamp("DOB") != null) {
                u.setDob(rs.getTimestamp("DOB").toLocalDateTime());
            }

            u.setGoals(rs.getString("goals"));
            u.setTimeframe(rs.getInt("timeframe"));
            if (rs.wasNull()) {
                u.setTimeframe(null);
            }

            u.setExercise(rs.getString("exercise"));
            u.setPremium(rs.getString("premium"));
            u.setUserStatus(rs.getString("userStatus"));
            u.setFirebaseId(rs.getString("firebaseId"));

            if (rs.getTimestamp("createdDate") != null) {
                u.setCreatedDate(rs.getTimestamp("createdDate").toLocalDateTime());
            }

            return u;
        }
    }

    public int insert(User u) {
        try {
            return jdbc.update(
                    "INSERT INTO `user` " +
                            "(userId, firstName, lastName, username, passwordHash, email, gender, DOB, goals, timeframe, exercise, premium, userStatus, firebaseId, createdDate, loginMethod) "
                            +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    u.getUserId(),
                    u.getFirstName(),
                    u.getLastName(),
                    u.getUsername(),
                    u.getPasswordHash(),
                    u.getEmail(),
                    u.getGender(),
                    u.getDob(),
                    u.getGoals(),
                    u.getTimeframe(),
                    u.getExercise(),
                    u.getPremium(),
                    u.getUserStatus(),
                    u.getFirebaseId(),
                    u.getCreatedDate(),
                    u.getLoginMethod());
        } catch (Exception e) {
            e.printStackTrace(); // <-- This will print the real SQL error
            return 0;
        }

    }

    public int update(User u) {
        return jdbc.update(
                "UPDATE user SET firstName=?, lastName=?, username=?, passwordHash=?, email=?, gender=?, DOB=?, goals=?, timeframe=?, exercise=?, premium=?, userStatus=?, firebaseId=?, createdDate=?, loginMethod=? "
                        +
                        "WHERE userId=?",
                u.getFirstName(),
                u.getLastName(),
                u.getUsername(),
                u.getPasswordHash(),
                u.getEmail(),
                u.getGender(),
                u.getDob(),
                u.getGoals(),
                u.getTimeframe(),
                u.getExercise(),
                u.getPremium(),
                u.getUserStatus(),
                u.getFirebaseId(),
                u.getCreatedDate(),
                u.getLoginMethod(),
                u.getUserId());
    }

    public User findByEmailAndLoginType(String email, String loginType) {
        try {
            String sql = "SELECT * FROM `user` WHERE email = ? AND loginMethod = ?";
            return jdbc.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(User.class),
                    email,
                    loginType);
        } catch (EmptyResultDataAccessException e) {
            // No user found
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findByFirebaseId(String firebaseId) {
        try {
            String sql = "SELECT * FROM `user` WHERE firebaseId = ?";
            return jdbc.queryForObject(
                    sql,
                    new UserRowMapper(),
                    firebaseId);
        } catch (EmptyResultDataAccessException e) {
            // No user found
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // SQL INJECTION POTENTIAL, DEFAULT SHOULD RETURN EXCEPTION
    public long userCount(String type) {
        String sql;

        switch (type.toLowerCase()) {
            case "premium":
                sql = "SELECT COUNT(*) FROM `user` WHERE premium = 'P'";
                break;
            case "free":
                sql = "SELECT COUNT(*) FROM `user` WHERE premium = 'F'";
                break;
            case "banned":
                sql = "SELECT COUNT(*) FROM `user` WHERE userStatus = 'B'";
                break;
            default: // "all"
                sql = "SELECT COUNT(*) FROM `user`";
                break;
        }

        return jdbc.queryForObject(sql, Long.class);
    }

    public List<User> getUsers(Integer limit) {
        String sql = "SELECT * FROM `user` ORDER BY createdDate DESC";
        if (limit != null && limit > 0) {
            sql += " LIMIT ?";
            return jdbc.query(sql, new UserRowMapper(), limit);
        } else {
            return jdbc.query(sql, new UserRowMapper());
        }
    }

    public List<User> getPremiumUsers(Integer limit) {
        String sql = "SELECT * FROM `user` WHERE premium = 'P' ORDER BY createdDate DESC";
        if (limit != null && limit > 0) {
            sql += " LIMIT ?";
            return jdbc.query(sql, new UserRowMapper(), limit);
        } else {
            return jdbc.query(sql, new UserRowMapper());
        }
    }

    public int updateStatus(String userId, String status) {
        String sql = "UPDATE `user` SET userStatus = ? WHERE userId = ?";
        return jdbc.update(sql, status, userId);
    }

    public int updatePremium(String userId, String premium) {
        String sql = "UPDATE `user` SET premium = ? WHERE userId = ?";
        return jdbc.update(sql, premium, userId);
    }

    public User findById(String userId) {
        String sql = "SELECT * FROM `user` WHERE userId = ?";
        return jdbc.query(sql, new UserRowMapper(), userId).stream().findFirst().orElse(null);
    }

public void updateOnboarding(String firebaseId, String gender, LocalDateTime dob, String exercise, String goals, Integer timeframe) {
    String sql = "UPDATE user SET gender=?, dob=?, exercise=?, goals=?, timeframe=? WHERE firebaseId=?";
    jdbc.update(sql, gender, dob, exercise, goals, timeframe, firebaseId);
}

}

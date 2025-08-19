package com.uow.moolacarb.repository;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.uow.moolacarb.model.User; 

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
            "(userId, firstName, lastName, username, passwordHash, email, gender, DOB, goals, timeframe, exercise, premium, userStatus, firebaseId, createdDate, loginMethod) " +
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
            u.getLoginMethod()  
            );
        } catch (Exception e) {
            e.printStackTrace(); // <-- This will print the real SQL error
            return 0;
        }
        
    }

    public int update(User u) {
        return jdbc.update(
            "UPDATE user SET firstName=?, lastName=?, username=?, passwordHash=?, email=?, gender=?, DOB=?, goals=?, timeframe=?, exercise=?, premium=?, userStatus=?, firebaseId=?, createdDate=?, loginMethod=? " +
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
            u.getUserId()
        );
    }

    public boolean existsByEmailAndLoginType(String email, String loginType) {
        try {
            String sql = "SELECT COUNT(*) FROM `user` WHERE email = ? AND loginMethod = ?";
            Integer count = jdbc.queryForObject(sql, Integer.class, email, loginType);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}

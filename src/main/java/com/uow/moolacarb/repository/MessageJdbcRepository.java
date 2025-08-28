package com.uow.moolacarb.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.uow.moolacarb.model.Message;
import com.uow.moolacarb.model.User;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Repository
public class MessageJdbcRepository {
    
    private final JdbcTemplate jdbc;

    public MessageJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public class MessageRowMapper implements RowMapper<Message> {
        @Override
        public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
            Message m = new Message();

            m.setMessageId(rs.getString("messageId"));
            m.setFirstName(rs.getString("firstName"));
            m.setLastName(rs.getString("lastName"));
            m.setEmail(rs.getString("email"));
            m.setSubject(rs.getString("smbject"));
            m.setMessage(rs.getString("message"));
            
            if (rs.getTimestamp("createdAt") != null) {
                m.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            }

            return m;
        }
    }

    public int insert(Message m) {
        try {
            return jdbc.update(
            "INSERT INTO `messages` " +
            "(messageId, firstName, lastName, email, subject, message, createdAt) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)",
            m.getMessageId(),
            m.getFirstName(),
            m.getLastName(),
            m.getEmail(),
            m.getSubject(),
            m.getMessage(),         
            m.getCreatedAt()
            );
        } catch (Exception e) {
            e.printStackTrace(); // <-- This will print the real SQL error
            return 0;
        }
        
    }

    public int update(Message m) {
        return jdbc.update(
            "UPDATE messages SET firstName=?, lastName=?, email=?, subject=?, message=?, createdAt=?, status=?, reply=? " +
            "WHERE messageId=?",
            m.getFirstName(),
            m.getLastName(),
            m.getEmail(),
            m.getSubject(),
            m.getMessage(), 
            m.getCreatedAt(),
            m.getStatus(),
            m.getReply(),
            m.getMessageId()
        );
    }
}

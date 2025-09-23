package com.uow.moolacarb.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.uow.moolacarb.model.Message;
import com.uow.moolacarb.repository.MessageJdbcRepository.MessageRowMapper;

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
            m.setSubject(rs.getString("subject"));
            m.setMessage(rs.getString("message"));

            if (rs.getTimestamp("createdAt") != null) {
                m.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            }

            m.setStatus(rs.getString("status"));
            m.setReply(rs.getString("reply"));

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
                    m.getCreatedAt());
        } catch (Exception e) {
            e.printStackTrace(); // <-- This will print the real SQL error
            return 0;
        }

    }

    public int update(Message m) {
        return jdbc.update(
                "UPDATE messages SET firstName=?, lastName=?, email=?, subject=?, message=?, createdAt=?, status=?, reply=? "
                        +
                        "WHERE messageId=?",
                m.getFirstName(),
                m.getLastName(),
                m.getEmail(),
                m.getSubject(),
                m.getMessage(),
                m.getCreatedAt(),
                m.getStatus(),
                m.getReply(),
                m.getMessageId());
    }

    public List<Message> getMessages(Integer limit) {
        String sql = "SELECT * FROM `messages` ORDER BY createdAt DESC";
        if (limit != null && limit > 0) {
            sql += " LIMIT ?";
            return jdbc.query(sql, new MessageRowMapper(), limit);
        } else {
            return jdbc.query(sql, new MessageRowMapper());
        }
    }

    // TODO inconsistent return type
    public int delete(Integer id) {
        return jdbc.update(
                "DELETE from messages WHERE messageId=?",
                id);
    }

    public Message getMessageById(Integer id) {
        String sql = "SELECT * FROM messages WHERE messageId = ?";
        return jdbc.query(sql, new MessageRowMapper(), id).stream().findFirst().orElse(null);
    }

    // TODO inconsistent return type
    public int updateReply(String id, String reply, String status) {
        String sql = "UPDATE messages " +
                "SET reply = ?, status = ? " + 
                "WHERE messageId = ?";
        return jdbc.update(sql, reply, status, id);
    }
}

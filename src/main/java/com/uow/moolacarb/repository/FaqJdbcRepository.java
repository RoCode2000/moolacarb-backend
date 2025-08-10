package com.uow.moolacarb.repository;

import com.uow.moolacarb.model.Faq;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.List;

@Repository
public class FaqJdbcRepository {

    private final JdbcTemplate jdbc;

    public FaqJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static class FaqRowMapper implements RowMapper<Faq> {
        @Override
        public Faq mapRow(ResultSet rs, int rowNum) throws SQLException {
            Faq f = new Faq();
            f.setFaqId(rs.getLong("faqId"));
            f.setQuestion(rs.getString("question"));
            f.setAnswer(rs.getString("answer"));
            f.setStatus(rs.getString("status"));
            f.setCategory(rs.getString("category"));
            // Convert TIMESTAMP to Instant (UTC)
            if (rs.getTimestamp("createdAt") != null) {
                f.setCreatedAt(rs.getTimestamp("createdAt").toInstant());
            }
            if (rs.getTimestamp("updatedAt") != null) {
                f.setUpdatedAt(rs.getTimestamp("updatedAt").toInstant());
            }
            return f;
        }
    }

    public List<Faq> findActive(String category) {
        if (category == null || category.isBlank()) {
            return jdbc.query(
                "SELECT faqId, question, answer, status, category, createdAt, updatedAt " +
                "FROM faq WHERE status = 'A' ORDER BY faqId ASC",
                new FaqRowMapper()
            );
        }
        return jdbc.query(
            "SELECT faqId, question, answer, status, category, createdAt, updatedAt " +
            "FROM faq WHERE status = 'A' AND category = ? ORDER BY faqId ASC",
            new FaqRowMapper(),
            category
        );
    }

    public int insert(Faq f) {
        return jdbc.update(
            "INSERT INTO faq (question, answer, status, category) VALUES (?, ?, ?, ?)",
            f.getQuestion(), f.getAnswer(), f.getStatus(), f.getCategory()
        );
    }

    public int update(Faq f) {
        return jdbc.update(
            "UPDATE faq SET question=?, answer=?, status=?, category=? WHERE faqId=?",
            f.getQuestion(), f.getAnswer(), f.getStatus(), f.getCategory(), f.getFaqId()
        );
    }

    public int deleteById(Long id) {
        return jdbc.update("DELETE FROM faq WHERE faqId=?", id);
    }
}

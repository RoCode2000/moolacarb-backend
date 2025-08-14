package com.uow.moolacarb.repository;

import com.uow.moolacarb.model.FeatureConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FeatureConfigJdbcRepository {
  private final JdbcTemplate jdbc;
  public FeatureConfigJdbcRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  private static class Map implements RowMapper<FeatureConfig> {
    @Override public FeatureConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
      FeatureConfig f = new FeatureConfig();
      f.setFeatureConfigId(rs.getLong("featureConfigId"));
      f.setUserType(rs.getString("userType"));
      f.setFeature(rs.getString("feature"));
      f.setDescription(rs.getString("description"));
      int lim = rs.getInt("usageLimit");
      f.setUsageLimit(rs.wasNull() ? null : lim);
      f.setStatus(rs.getString("status"));
      f.setDisplayOrder(rs.getInt("displayOrder"));
      if (rs.getTimestamp("createdAt") != null) f.setCreatedAt(rs.getTimestamp("createdAt").toInstant());
      if (rs.getTimestamp("updatedAt") != null) f.setUpdatedAt(rs.getTimestamp("updatedAt").toInstant());
      return f;
    }
  }

  /** FREE plan: FREE + ON */
  public List<FeatureConfig> listForFree() {
    String sql = """
      SELECT featureConfigId, userType, feature, description, usageLimit, status,
             displayOrder, createdAt, updatedAt
      FROM featureConfig
      WHERE status='ON' AND userType='FREE'
      ORDER BY displayOrder ASC, feature ASC
    """;
    return jdbc.query(sql, new Map());
  }

  /** PREMIUM = PREMIUM overrides + FREE rows without a PREMIUM override. */
  public List<FeatureConfig> listForPremium() {
    String sql = """
      SELECT p.featureConfigId, p.userType, p.feature, p.description, p.usageLimit, p.status,
             p.displayOrder, p.createdAt, p.updatedAt
      FROM featureConfig p
      WHERE p.userType='PREMIUM' AND p.status='ON'
      UNION ALL
      SELECT f.featureConfigId, f.userType, f.feature, f.description, f.usageLimit, f.status,
             f.displayOrder, f.createdAt, f.updatedAt
      FROM featureConfig f
      LEFT JOIN featureConfig p2
        ON p2.userType='PREMIUM' AND p2.status='ON' AND p2.feature = f.feature
      WHERE f.userType='FREE' AND f.status='ON' AND p2.feature IS NULL
      ORDER BY displayOrder ASC, feature ASC
    """;
    return jdbc.query(sql, new Map());
  }

  public List<FeatureConfig> listForUserType(String userType) {
    return "PREMIUM".equalsIgnoreCase(userType) ? listForPremium() : listForFree();
  }
}

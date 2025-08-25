package com.uow.moolacarb.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.uow.moolacarb.model.FavouriteRecipe;
import com.uow.moolacarb.model.User;

@Repository
public class FavouriteRecipeJdbcRepository {
    private final JdbcTemplate jdbc;
    public FavouriteRecipeJdbcRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public class FavouriteRecipeMapper implements RowMapper<FavouriteRecipe> {
        @Override
        public FavouriteRecipe mapRow(ResultSet rs, int rowNum) throws SQLException {
            FavouriteRecipe f = new FavouriteRecipe();
            f.setFavouriteRecipeId(rs.getString("favouriteRecipeId"));
            f.setRecipeId(rs.getString("recipeId"));
            f.setUserId(rs.getString("userId"));
            f.setIsFavourite(rs.getString("isFavourite"));
            f.setCreatedDate(rs.getTimestamp("createdDate").toLocalDateTime());
            return f;
        }
    }

    public List<FavouriteRecipe> listActiveByUserId(String userId) {
        String sql = """
            SELECT favouriteRecipeId, recipeId, userId, isFavourite, createdDate
            FROM favouriteRecipes
            WHERE userId= ? AND isFavourite = 'Y'
        """;
        return jdbc.query(sql, new FavouriteRecipeMapper(), userId);
    }

    public List<FavouriteRecipe> checkIfExists(String userId, String recipeId) {
        String sql = """
            SELECT favouriteRecipeId, recipeId, userId, isFavourite, createdDate
            FROM favouriteRecipes
            WHERE userId= ? AND recipeId = ?
        """;
        return jdbc.query(sql, new FavouriteRecipeMapper(), userId, recipeId);
    }

    public int insert(FavouriteRecipe r) {
        try {
            return jdbc.update(
            "INSERT INTO `favouriteRecipes` " +
            "(recipeId, userId, isFavourite, createdDate) " +
            "VALUES (?, ?, 'Y', ?)",
            r.getRecipeId(),
            r.getUserId(),
            r.getCreatedDate()
            );
        } catch (Exception e) {
            e.printStackTrace(); // <-- This will print the real SQL error
            return 0;
        }
        
    }

    public int toggleFavourite(String recipeId, String userId) {
        String sql = """
            UPDATE favouriteRecipes
            SET isFavourite = CASE 
                                WHEN isFavourite = 'Y' THEN 'N' 
                                ELSE 'Y' 
                            END
            WHERE recipeId = ? AND userId = ?
        """;

        try {
            return jdbc.update(sql, recipeId, userId);
        } catch (Exception e) {
            e.printStackTrace(); // Logs any SQL error
            return 0;
        }
    }



    
}

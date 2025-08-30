package com.uow.moolacarb.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.uow.moolacarb.model.Recipe;

@Repository
public class RecipeJdbcRepository {
  private final JdbcTemplate jdbc;
  public RecipeJdbcRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  private static class RecipeRowMapper implements RowMapper<Recipe> {
    @Override
    public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
      Recipe r = new Recipe();
      r.setRecipeId(rs.getString("recipeId"));
      r.setTitle(rs.getString("title"));
      r.setServing(rs.getInt("serving"));
      r.setIngredients(rs.getString("ingredients"));
      r.setInstructions(rs.getString("instructions"));
      r.setCalories(rs.getFloat("calories"));
      r.setCarbohydrates(rs.getFloat("carbohydrates"));
      r.setProtein(rs.getFloat("protein"));
      r.setFat(rs.getFloat("fat"));
      r.setSaturatedFat(rs.getFloat("saturatedFat"));
      r.setSodium(rs.getFloat("sodium"));
      r.setCholesterol(rs.getFloat("cholesterol"));
      r.setPotassium(rs.getFloat("potassium"));
      r.setStatus(rs.getString("status"));
      r.setAuthor(rs.getString("author"));
      r.setPrepTime(rs.getInt("prepTime"));
      r.setCookTime(rs.getInt("cookTime"));
      r.setRestingTime(rs.getInt("restingTime"));
      r.setCuisine(rs.getString("cuisine"));
      r.setDescription(rs.getString("description"));
      r.setMealType(rs.getString("mealType"));
      r.setOverallRating(rs.getFloat("overallRating"));
      r.setImageLink(rs.getString("imageLink"));
      r.setImageBinary(rs.getBytes("imageBinary"));
      return r;
    }
  }

  /** Retrieve all recipes */
  public List<Recipe> listAllActive() {
    String sql = """
      SELECT recipeId, title, serving, ingredients, instructions,
             calories, carbohydrates, protein, fat, saturatedFat, sodium,
             cholesterol, potassium, status, author, prepTime, cookTime,
             restingTime, cuisine, description, mealType, overallRating, imageLink, imageBinary
      FROM recipe
      WHERE status = 'A'
      ORDER BY recipeId ASC
    """;
    return jdbc.query(sql, new RecipeRowMapper());
  }

  /** Create a new recipe */
  public int create(Recipe r) {
    String sql = """
      INSERT INTO recipe (title, serving, ingredients, instructions, calories,
                          carbohydrates, protein, fat, saturatedFat, sodium,
                          cholesterol, potassium, status, author, prepTime,
                          cookTime, restingTime, cuisine, description, mealType,
                          overallRating, imageLink, imageBinary)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;
    return jdbc.update(sql,
      r.getTitle(),
      r.getServing(),
      r.getIngredients(),
      r.getInstructions(),
      r.getCalories(),
      r.getCarbohydrates(),
      r.getProtein(),
      r.getFat(),
      r.getSaturatedFat(),
      r.getSodium(),
      r.getCholesterol(),
      r.getPotassium(),
      r.getStatus(),
      r.getAuthor(),
      r.getPrepTime(),
      r.getCookTime(),
      r.getRestingTime(),
      r.getCuisine(),
      r.getDescription(),
      r.getMealType(),
      r.getOverallRating(),
      r.getImageLink(),
      r.getImageBinary()
    );
  }

  /** Delete a recipe by ID */
  public int deleteRecipe(int recipeId) {
    String sql = """
      UPDATE recipe
      SET status = 'I', updatedAt = CURRENT_TIMESTAMP
      WHERE recipeId = ? AND status <> 'I' 
    """;
    return jdbc.update(sql, recipeId);
  }

  public Recipe findById(String recipeId) {
        String sql = """
            SELECT recipeId, title, serving, ingredients, instructions, calories,
                carbohydrates, protein, fat, saturatedFat, sodium, cholesterol,
                potassium, status, author, prepTime, cookTime, restingTime,
                cuisine, description, mealType, overallRating, imageLink, imageBinary
            FROM recipe
            WHERE recipeId = ?
        """;
        try {
            return jdbc.queryForObject(sql, new RecipeRowMapper(), recipeId);
        } catch (EmptyResultDataAccessException e) {
            return null; 
        }
    }

  public List<Recipe> listAllRecipesByUser(String userId) {
    String sql = """
      SELECT recipeId, title, serving, ingredients, instructions,
             calories, carbohydrates, protein, fat, saturatedFat, sodium,
             cholesterol, potassium, status, author, prepTime, cookTime,
             restingTime, cuisine, description, mealType, overallRating, imageLink, imageBinary
      FROM recipe
      WHERE author = ?
    """;
    return jdbc.query(sql, new BeanPropertyRowMapper<>(Recipe.class), userId);
  }

    public long countAllActive() {
      String sql = "SELECT COUNT(*) FROM recipe where status='A'";
      return jdbc.queryForObject(sql, Long.class);
    }

    public List<Recipe> getActiveRecipes(Integer limit) {
        String sql = "SELECT * FROM `recipe` WHERE status = 'A'";
        if (limit != null && limit > 0) {
            sql += " LIMIT ?";
            return jdbc.query(sql, new RecipeRowMapper(), limit);
        } else {
            return jdbc.query(sql, new RecipeRowMapper());
        }
    }
}

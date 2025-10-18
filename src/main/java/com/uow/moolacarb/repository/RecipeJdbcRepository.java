package com.uow.moolacarb.repository;

import com.uow.moolacarb.DataTransferObject.RecipeCard;
import com.uow.moolacarb.model.Recipe;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class RecipeJdbcRepository {

  private final JdbcTemplate jdbc;

  public RecipeJdbcRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  // ---------- Row mappers ----------
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

  /** Lightweight card for UI (id, title, kcal, imageLink). */
  private static class RecipeCardRowMapper implements RowMapper<RecipeCard> {
    @Override
    public RecipeCard mapRow(ResultSet rs, int rowNum) throws SQLException {
      String id = String.valueOf(rs.getInt("recipeId"));
      String title = rs.getString("title");
      int kcal = Math.round(rs.getFloat("kcal_per_serving"));
      String imageLink = rs.getString("imageLink");
      return new RecipeCard(id, title, kcal, imageLink);
    }
  }

  // ---------- Existing teammate APIs (kept intact) ----------
  /** Retrieve all recipes (active). */
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
        r.getImageBinary());
  }

  /** Soft delete a recipe by ID */
  public int deleteRecipe(int recipeId) {
    String sql = """
          UPDATE recipe
          SET status = 'I', updatedAt = CURRENT_TIMESTAMP
          WHERE recipeId = ? AND status <> 'I'
        """;
    return jdbc.update(sql, recipeId);
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
  
  public List<Recipe> getAllRecipes(Integer limit) {
    String sql = "SELECT * FROM `recipe`";
    if (limit != null && limit > 0) {
      sql += " LIMIT ?";
      return jdbc.query(sql, new RecipeRowMapper(), limit);
    } else {
      return jdbc.query(sql, new RecipeRowMapper());
    }
  }

  public Recipe findById(String recipeId) {
    String sql = "SELECT * FROM `recipe` WHERE recipeId = ?";
    try {
      return jdbc.query(sql, new RecipeRowMapper(), recipeId).stream().findFirst().orElse(null);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public int updateStatus(String recipeId, String status) {
    String sql = "UPDATE `recipe` SET status = ? WHERE recipeId = ?";
    return jdbc.update(sql, status, recipeId);
  }

  public long countAllActive() {
    String sql = "SELECT COUNT(*) FROM recipe WHERE status='A'";
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

  // ---------- Existing additive utility (kept) ----------
  /**
   * Returns random recipe cards filtered by kcal range and excluding selected
   * meal types.
   */
  public List<RecipeCard> findRandomCardsByKcalRangeExcludingTypes(
      int kcalMin, int kcalMax, int limit, List<String> excludedMealTypes) {
    String excludeTypesSql = (excludedMealTypes == null || excludedMealTypes.isEmpty())
        ? ""
        : " AND (mealType IS NULL OR LOWER(mealType) NOT IN (" +
            String.join(",", excludedMealTypes.stream().map(t -> "?").toList()) + "))";

    String sql = """
          SELECT
            recipeId,
            title,
            imageLink,
            calories AS kcal_per_serving
          FROM recipe
          WHERE status = 'A'
            AND calories IS NOT NULL
            AND calories BETWEEN ? AND ?
        """ + excludeTypesSql + """
          ORDER BY RAND()
          LIMIT ?
        """;

    List<Object> args = new ArrayList<>();
    args.add(kcalMin);
    args.add(kcalMax);
    if (excludedMealTypes != null && !excludedMealTypes.isEmpty()) {
      for (String t : excludedMealTypes)
        args.add(t.toLowerCase(Locale.ROOT));
    }
    args.add(limit);

    return jdbc.query(sql, args.toArray(), new RecipeCardRowMapper());
  }

  // ---------- NEW: profile-matched random picker ----------
  /**
   * Random recipe cards matched to profile-like filters:
   * - Optional kcalMin/kcalMax (null = ignore)
   * - Optional allowed cuisines (case-insensitive)
   * - Optional allowed meal types (case-insensitive)
   * Returns up to `limit` in random order.
   */
  public List<RecipeCard> findRandomCardsByProfile(
      Integer kcalMin,
      Integer kcalMax,
      List<String> allowedCuisines,
      List<String> allowedMealTypes,
      int limit) {
    StringBuilder sql = new StringBuilder("""
          SELECT
            recipeId,
            title,
            imageLink,
            calories AS kcal_per_serving
          FROM recipe
          WHERE status = 'A'
            AND calories IS NOT NULL
        """);

    List<Object> args = new ArrayList<>();

    // kcal band (optional)
    if (kcalMin != null && kcalMax != null) {
      sql.append(" AND calories BETWEEN ? AND ? ");
      args.add(kcalMin);
      args.add(kcalMax);
    } else if (kcalMin != null) {
      sql.append(" AND calories >= ? ");
      args.add(kcalMin);
    } else if (kcalMax != null) {
      sql.append(" AND calories <= ? ");
      args.add(kcalMax);
    }

    // cuisines (optional allow-list)
    if (allowedCuisines != null && !allowedCuisines.isEmpty()) {
      String marks = String.join(",", Collections.nCopies(allowedCuisines.size(), "?"));
      sql.append(" AND (cuisine IS NOT NULL AND LOWER(cuisine) IN (").append(marks).append(")) ");
      for (String c : allowedCuisines)
        args.add(c.toLowerCase(Locale.ROOT));
    }

    // meal types (optional allow-list)
    if (allowedMealTypes != null && !allowedMealTypes.isEmpty()) {
      String marks = String.join(",", Collections.nCopies(allowedMealTypes.size(), "?"));
      sql.append(" AND (mealType IS NOT NULL AND LOWER(mealType) IN (").append(marks).append(")) ");
      for (String t : allowedMealTypes)
        args.add(t.toLowerCase(Locale.ROOT));
    }

    sql.append(" ORDER BY RAND() LIMIT ? ");
    args.add(limit);

    return jdbc.query(sql.toString(), args.toArray(), new RecipeCardRowMapper());
  }
}

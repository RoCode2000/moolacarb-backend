package com.uow.moolacarb.controller;

import com.uow.moolacarb.DataTransferObject.CalorieGoalResponse;
import com.uow.moolacarb.DataTransferObject.RecipeCard;
import com.uow.moolacarb.model.HeightHistory;
import com.uow.moolacarb.model.User;
import com.uow.moolacarb.model.WeightHistory;
import com.uow.moolacarb.repository.HeightHistoryRepository;
import com.uow.moolacarb.repository.RecipeJdbcRepository;
import com.uow.moolacarb.repository.WeightHistoryRepository;
import com.uow.moolacarb.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Calorie goal + lightweight recipe recommendations.
 * - Keeps teammate code intact elsewhere.
 * - Uses Mifflin–St Jeor + PAL for daily target.
 * - Recommendations: returns 3 random recipes, optionally bounded by kcal band and profile allow-lists.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/calorie-goal")
public class CalorieGoalController {

  // ---- PAL factors ----
  private static final double PAL_SEDENTARY = 1.20;
  private static final double PAL_LIGHT     = 1.375;
  private static final double PAL_MODERATE  = 1.55;
  private static final double PAL_VERY      = 1.725;
  private static final double PAL_EXTRA     = 1.90;

  // ---- Goal multipliers ----
  private static final double CUT_FACTOR   = 0.85; // LOSE  (-15%)
  private static final double BULK_FACTOR  = 1.10; // GAIN/BUILD_MUSCLE (+10%)
  private static final double MAINT_FACTOR = 1.00; // MAINTAIN/IMPROVE

  private final UserService userService;
  private final WeightHistoryRepository weightRepo;
  private final HeightHistoryRepository heightRepo;
  private final RecipeJdbcRepository recipeRepo;

  public CalorieGoalController(UserService userService,
                               WeightHistoryRepository weightRepo,
                               HeightHistoryRepository heightRepo,
                               RecipeJdbcRepository recipeRepo) {
    this.userService = userService;
    this.weightRepo = weightRepo;
    this.heightRepo = heightRepo;
    this.recipeRepo = recipeRepo;
  }

  // --------- Public endpoints ---------

  /** GET /api/calorie-goal/today?firebaseId=... */
  @GetMapping("/today")
  public CalorieGoalResponse today(@RequestParam String firebaseId) {
    int dailyTarget = computeDailyTarget(firebaseId);
    String goal = normalizeGoal(userService.findByFirebaseId(firebaseId).getGoals());
    return new CalorieGoalResponse(dailyTarget, goal);
  }

  /**
   * GET /api/calorie-goal/recommendations?firebaseId=...&count=3
   * Returns random recipe cards loosely matched to the profile.
   * - Keeps it visually to 3 items.
   * - Uses a simple per-meal kcal band; pass nulls to go fully random.
   * - If you have profile prefs (cuisine/mealType), wire them into the allow-lists below.
   */
  @GetMapping("/recommendations")
  public List<RecipeCard> recommend(
      @RequestParam String firebaseId,
      @RequestParam(required = false, defaultValue = "3") int count
  ) {
    if (count <= 0) return List.of();
    if (count != 3) count = 3;

    // Rough per-meal band based on daily target. If you want PURE random, set both to null.
    int target = computeDailyTarget(firebaseId);
    int perMeal = Math.max(250, target / 3);
    Integer kcalMin = Math.max(0, perMeal - 150);
    Integer kcalMax = perMeal + 150;

    // Optional: pull profile prefs here (replace with your own sources/fields).
    List<String> allowedCuisines = getPreferredCuisines(firebaseId);   // e.g., ["asian","thai"]
    List<String> allowedMealTypes = getPreferredMealTypes(firebaseId); // e.g., ["breakfast","lunch","dinner"]

    // If you want it fully random irrespective of calories, flip these to null:
    // kcalMin = null; kcalMax = null;

    return recipeRepo.findRandomCardsByProfile(
        kcalMin, kcalMax,
        emptyToNull(allowedCuisines),
        emptyToNull(allowedMealTypes),
        count
    );
  }

  // --------- Internal helpers ---------

  /** Compute daily target using Mifflin–St Jeor + PAL + goal multiplier. */
  private int computeDailyTarget(String firebaseId) {
    User user = userService.findByFirebaseId(firebaseId);
    if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

    WeightHistory w = weightRepo.getCurrent(firebaseId);
    HeightHistory h = heightRepo.getCurrent(firebaseId);
    if (w == null || h == null || user.getDob() == null || user.getGender() == null) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Missing inputs (height/weight/dob/gender)");
    }

    double weightKg = w.getWeight();
    double heightCm = h.getHeight();
    int age = (int) ChronoUnit.YEARS.between(user.getDob().toLocalDate(), LocalDate.now(ZoneId.of("UTC")));
    boolean isMale = "male".equalsIgnoreCase(user.getGender());

    // BMR: Mifflin–St Jeor
    double bmr = 10 * weightKg + 6.25 * heightCm - 5 * age + (isMale ? 5 : -161);

    double pal = mapExerciseToPAL(user.getExercise());
    double tdee = bmr * pal;

    String goal = normalizeGoal(user.getGoals());
    double factor = switch (goal) {
      case "LOSE" -> CUT_FACTOR;
      case "GAIN", "BUILD_MUSCLE" -> BULK_FACTOR;
      default -> MAINT_FACTOR; // MAINTAIN/IMPROVE
    };

    return (int) Math.round(tdee * factor);
  }

  /** Normalize goals from canonical or legacy inputs. */
  private static String normalizeGoal(String goal) {
    if (goal == null) return "MAINTAIN";
    String g = goal.trim().toUpperCase(Locale.ROOT);

    if (g.equals("LOSE") || g.equals("MAINTAIN") || g.equals("BUILD_MUSCLE") || g.equals("GAIN")) return g;

    String gl = goal.toLowerCase(Locale.ROOT);
    if (gl.contains("lose")) return "LOSE";
    if (gl.contains("muscle") || gl.contains("gain")) return "BUILD_MUSCLE";
    if (gl.contains("maintain") || gl.contains("improve")) return "MAINTAIN";

    return "MAINTAIN";
  }

  /** Map activity/exercise (canonical or legacy) to PAL. */
  private static double mapExerciseToPAL(String exercise) {
    if (exercise == null || exercise.isBlank()) return PAL_MODERATE;
    String e = exercise.trim().toLowerCase(Locale.ROOT);

    switch (e) {
      case "sedentary": return PAL_SEDENTARY;
      case "light":     return PAL_LIGHT;
      case "moderate":  return PAL_MODERATE;
      case "very":      return PAL_VERY;
      case "extra":     return PAL_EXTRA;
      default: break;
    }
    if (e.contains("never") || e.contains("none") || e.contains("sedentary")) return PAL_SEDENTARY;
    if (e.contains("1–2") || e.contains("1-2") || e.contains("light"))        return PAL_LIGHT;
    if (e.contains("3–5") || e.contains("3-5") || e.contains("moderate"))     return PAL_MODERATE;
    if (e.contains("daily") || e.contains("very"))                             return PAL_VERY;
    if (e.contains("extra"))                                                   return PAL_EXTRA;

    return PAL_MODERATE;
  }

  // ---- Profile hooks (replace with real implementations if you have them) ----

  private List<String> getPreferredCuisines(String firebaseId) {
    // TODO: wire to your user profile prefs. Return null/empty to ignore.
    return null;
  }

  private List<String> getPreferredMealTypes(String firebaseId) {
    // TODO: wire to your user profile prefs. Return null/empty to ignore.
    return null;
  }

  private static <T> List<T> emptyToNull(List<T> list) {
    return (list == null || list.isEmpty()) ? null : list;
  }
}

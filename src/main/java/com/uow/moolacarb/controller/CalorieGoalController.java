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
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Calorie goal + lightweight recipe recommendations.
 * - Keeps your teammate's code intact elsewhere (repos/controllers/services).
 * - Uses Mifflin–St Jeor + PAL.
 * - Recommendations: picks 3 random meal-like recipes whose kcal sum ~ daily target.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/calorie-goal")
public class CalorieGoalController {

  // ---- PAL factors (FAO/WHO/UNU-like commonly used set) ----
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
   * Returns 3 recipe cards whose combined kcal is near the user's daily target.
   * Does NOT subtract consumed; just aims for ~daily target total as requested.
   */
  @GetMapping("/recommendations")
  public List<RecipeCard> recommend(
      @RequestParam String firebaseId,
      @RequestParam(required = false, defaultValue = "3") int count
  ) {
    if (count <= 0) return List.of();
    if (count != 3) count = 3; // tuned for 3 cards visually

    int target = computeDailyTarget(firebaseId);

    // 1) Pull a random candidate pool (exclude drinks/desserts)
    //    kcal band to represent "meal-like" items; adjust freely
    int kcalMin = 350, kcalMax = 900;
    int poolSize = 100;
    List<String> excludedTypes = List.of("drink", "beverage", "dessert");

    List<RecipeCard> pool = recipeRepo.findRandomCardsByKcalRangeExcludingTypes(
        kcalMin, kcalMax, poolSize, excludedTypes
    );

    // 2) Try to find 3 that sum close to target; widen window progressively
    Collections.shuffle(pool, seededRandomFor(firebaseId)); // different users/days see different combos
    int[] windows = new int[]{50, 100, 150, 200, 300};

    for (int win : windows) {
      List<RecipeCard> triple = findTripleCloseToTarget(pool, target, win);
      if (triple != null) return triple;
    }

    // 3) Fallback: just return any 3 random from pool
    return pool.size() >= 3 ? pool.subList(0, 3) : List.of();
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

    // Canonical
    if (g.equals("LOSE") || g.equals("MAINTAIN") || g.equals("BUILD_MUSCLE") || g.equals("GAIN")) return g;
    // Your FE also uses MUSCLE / IMPROVE; map them:
    if (g.equals("MUSCLE"))  return "BUILD_MUSCLE";
    if (g.equals("IMPROVE")) return "MAINTAIN";

    // Legacy phrases
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

    // Canonical keys from your FE: sedentary|light|moderate|very|extra
    switch (e) {
      case "sedentary": return PAL_SEDENTARY;
      case "light":     return PAL_LIGHT;
      case "moderate":  return PAL_MODERATE;
      case "very":      return PAL_VERY;
      case "extra":     return PAL_EXTRA;
    }

    // Legacy/phrase fallback
    if (e.contains("never") || e.contains("none") || e.contains("sedentary")) return PAL_SEDENTARY;
    if (e.contains("1–2") || e.contains("1-2") || e.contains("light"))        return PAL_LIGHT;
    if (e.contains("3–5") || e.contains("3-5") || e.contains("moderate"))     return PAL_MODERATE;
    if (e.contains("daily") || e.contains("very"))                             return PAL_VERY;
    if (e.contains("extra"))                                                   return PAL_EXTRA;

    return PAL_MODERATE;
  }

  /**
   * Find 3 items whose kcal sum lies within [target - window, target + window].
   * If none found, return the closest triple.
   */
  private List<RecipeCard> findTripleCloseToTarget(List<RecipeCard> pool, int target, int window) {
    if (pool == null || pool.size() < 3) return null;

    // Sort a copy by kcal for 2-pointer approach
    List<RecipeCard> arr = new ArrayList<>(pool);
    arr.sort(Comparator.comparingInt(RecipeCard::kcal));

    int n = arr.size();
    int bestDiff = Integer.MAX_VALUE;
    int[] bestIdx = null;

    for (int i = 0; i < n - 2; i++) {
      int left = i + 1, right = n - 1;
      while (left < right) {
        int sum = arr.get(i).kcal() + arr.get(left).kcal() + arr.get(right).kcal();
        int diff = Math.abs(sum - target);

        if (diff <= window) {
          return List.of(arr.get(i), arr.get(left), arr.get(right));
        }
        if (diff < bestDiff) {
          bestDiff = diff;
          bestIdx = new int[]{i, left, right};
        }
        if (sum < target) left++; else right--;
      }
    }

    if (bestIdx != null) {
      return List.of(arr.get(bestIdx[0]), arr.get(bestIdx[1]), arr.get(bestIdx[2]));
    }
    return null;
  }

  /** Seeded random so each user gets a stable daily shuffle; different users differ. */
  private Random seededRandomFor(String firebaseId) {
    long day = java.time.LocalDate.now(java.time.ZoneOffset.UTC).toEpochDay();
    long timestamp = System.nanoTime();  // Adds more randomness
    return new Random(Objects.hash(firebaseId, day, timestamp));
  }

}

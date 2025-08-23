package com.uow.moolacarb.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "meallog")
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mealLogId")
    private Integer mealLogId;

    @Column(name = "foodsConsumed", nullable = false)
    private String foodsConsumed;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "timeConsumed", nullable = false)   // <— DATETIME in DB
    private LocalDateTime timeConsumed;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "userId", nullable = false)
    private String userId;

    public Integer getMealLogId() { return mealLogId; }
    public void setMealLogId(Integer mealLogId) { this.mealLogId = mealLogId; }

    public String getFoodsConsumed() { return foodsConsumed; }
    public void setFoodsConsumed(String foodsConsumed) { this.foodsConsumed = foodsConsumed; }

    public LocalDateTime getTimeConsumed() { return timeConsumed; }
    public void setTimeConsumed(LocalDateTime timeConsumed) { this.timeConsumed = timeConsumed; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}

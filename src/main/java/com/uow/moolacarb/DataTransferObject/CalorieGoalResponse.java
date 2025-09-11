package com.uow.moolacarb.DataTransferObject;

public record CalorieGoalResponse(
    int dailyTarget,
    String goalType
) {}

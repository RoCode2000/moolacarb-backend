package com.uow.moolacarb.DataTransferObject;

public record RecipeCard(
    String id,        
    String title,
    int kcal,         
    String imageLink,
    float carbohydrates,
    float protein,
    float fat
) {}
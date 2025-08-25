package com.uow.moolacarb.model;

import java.time.LocalDateTime;

public class FavouriteRecipe {
    private String favouriteRecipeId;
    private String recipeId;
    private String userId;
    private String isFavourite; 
    private LocalDateTime createdDate;

    
    public FavouriteRecipe() {
    }

    
    public FavouriteRecipe(String favouriteRecipeId, String recipeId, String userId, String isFavourite, LocalDateTime createdDate) {
        this.favouriteRecipeId = favouriteRecipeId;
        this.recipeId = recipeId;
        this.userId = userId;
        this.isFavourite = isFavourite;
        this.createdDate = createdDate;
    }

    
    public String getFavouriteRecipeId() {
        return favouriteRecipeId;
    }

    public void setFavouriteRecipeId(String favouriteRecipeId) {
        this.favouriteRecipeId = favouriteRecipeId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "FavouriteRecipe{" +
                "favouriteRecipeId=" + favouriteRecipeId +
                ", recipeId=" + recipeId +
                ", userId='" + userId + '\'' +
                ", isFavourite='" + isFavourite + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }

}

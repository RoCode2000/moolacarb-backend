package com.uow.moolacarb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uow.moolacarb.model.FavouriteRecipe;
import com.uow.moolacarb.repository.FavouriteRecipeJdbcRepository;

@Service
public class FavouriteRecipeService {
    private final FavouriteRecipeJdbcRepository repo;

    public FavouriteRecipeService (FavouriteRecipeJdbcRepository repo){
        this.repo = repo;
    }

    public List<FavouriteRecipe> findActiveByUser(String userId){
        return repo.listActiveByUserId(userId);
    }

    public boolean checkExisting(String userId, String recipeId) {
        return !repo.checkIfExists(userId, recipeId).isEmpty();
    }

    public void create(FavouriteRecipe r){
        repo.insert(r);
    }

    public void toggle(String recipeId, String userId){
        repo.toggleFavourite(recipeId, userId);
    }
}

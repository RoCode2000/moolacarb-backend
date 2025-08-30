package com.uow.moolacarb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uow.moolacarb.model.Recipe;
import com.uow.moolacarb.repository.RecipeJdbcRepository;

@Service
public class RecipeService {
    private final RecipeJdbcRepository repo;

    public RecipeService(RecipeJdbcRepository repo){
        this.repo = repo;
    }

    public List<Recipe> findAllActive(){
        return repo.listAllActive();
    }

    public Recipe searchById(String recipeId){
        return repo.findById(recipeId);
    }

    public void create(Recipe recipe){
        repo.create(recipe);
    }

    public List<Recipe> getRecipesByUser(String userId){
        return repo.listAllRecipesByUser(userId);
    }
}

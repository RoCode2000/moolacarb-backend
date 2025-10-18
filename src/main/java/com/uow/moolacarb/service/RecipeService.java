package com.uow.moolacarb.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.uow.moolacarb.DataTransferObject.RecipeUpdateRequest;
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

    public List<Recipe> getActiveRecipes(Integer limit) {
        return repo.getActiveRecipes(limit);
    }

    public void create(Recipe recipe){
        repo.create(recipe);
    }

    public List<Recipe> getRecipesByUser(String userId){
        return repo.listAllRecipesByUser(userId);
    }

    public List<Recipe> getAllRecipes(Integer limit){
        return repo.getAllRecipes(limit);
    }

    // TODO update has no return since i will re-call the page
    public Recipe updateRecipe(String recipeId, RecipeUpdateRequest req) {
        Recipe existing = repo.findById(recipeId);
        if (existing == null) {
            throw new NoSuchElementException("Recipe not found");
        }

        // Normalize & validate (examples)
        String newStatus = normalizeStatus(req.getStatus()); // returns "A"/"B" or null if not provided

        if (req.getStatus() != null && newStatus == null)
            throw new IllegalArgumentException("Invalid status");

        // Only update what was provided & changed
        boolean changed = false;
        if (newStatus != null && !newStatus.equalsIgnoreCase(existing.getStatus())) {
            repo.updateStatus(recipeId, newStatus);
            existing.setStatus(newStatus);
            changed = true;
        }
        return existing;
    }

    private String normalizeStatus(String s) {
        if (s == null)
            return null;
        if ("A".equalsIgnoreCase(s) || "Active".equalsIgnoreCase(s))
            return "A";
        if ("I".equalsIgnoreCase(s) || "Inactive".equalsIgnoreCase(s))
            return "I";
        return null;
    }
}

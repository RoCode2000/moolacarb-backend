package com.uow.moolacarb.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uow.moolacarb.model.FavouriteRecipe;
import com.uow.moolacarb.model.Recipe;
import com.uow.moolacarb.service.FavouriteRecipeService;
import com.uow.moolacarb.service.RecipeService;

import org.springframework.web.bind.annotation.PostMapping;



@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/fav")
public class FavouriteRecipeController {
    private final FavouriteRecipeService service;
    private final RecipeService service2;

    public FavouriteRecipeController(FavouriteRecipeService service, RecipeService service2){
        this.service = service;
        this.service2 = service2;
    }

    @PostMapping("/active")
    public List<Recipe> getActiveList(@RequestBody Map<String, String> request) {

        List<FavouriteRecipe> activeFavourites = this.service.findActiveByUser(request.get("userId"));
        List<Recipe> recipes = new ArrayList<>();
        for (FavouriteRecipe fav : activeFavourites) {
            Recipe recipe = service2.searchById(fav.getRecipeId());
            if (recipe != null) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    @PostMapping("/toggle")
    public String toggle(@RequestBody Map<String, String> request) {
        
        if (service.checkExisting(request.get("userId"), request.get("recipeId"))) {
            service.toggle(request.get("recipeId"), request.get("userId"));
            return "toggled";
        } else {
            FavouriteRecipe r = new FavouriteRecipe();
            r.setUserId(request.get("userId"));
            r.setRecipeId(request.get("recipeId"));
            r.setCreatedDate(LocalDateTime.now());
            service.create(r);
            return "created";
        }
    }
    
}

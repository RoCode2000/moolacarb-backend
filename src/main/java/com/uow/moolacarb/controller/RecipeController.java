package com.uow.moolacarb.controller;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uow.moolacarb.model.Recipe;
import com.uow.moolacarb.service.RecipeService;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    private final RecipeService service;

    public RecipeController(RecipeService service){
        this.service = service;
    }

    @GetMapping("/active")
    public List<Recipe> getActiveList() {
        return this.service.findAllActive();
    }

    @PostMapping("/add")
    public String addRecipe(@RequestBody Map<String, String> request) {
        
        Recipe recipe = new Recipe();
        if(!request.get("author").equals("admin")){
            recipe.setStatus("P");
        } else recipe.setStatus("A");

        recipe.setTitle((String) request.get("title"));
        recipe.setServing(Integer.parseInt((String) request.get("serving")));
        recipe.setIngredients((String) request.get("ingredients"));
        recipe.setInstructions((String) request.get("instructions"));
        recipe.setCalories(Float.parseFloat((String) request.get("calories")));
        recipe.setCarbohydrates(Float.parseFloat((String) request.get("carbohydrates")));
        recipe.setProtein(Float.parseFloat((String) request.get("protein")));
        recipe.setFat(Float.parseFloat((String) request.get("fat")));
        recipe.setSaturatedFat(Float.parseFloat((String) request.get("saturatedFat")));
        recipe.setSodium(Float.parseFloat((String) request.get("sodium")));
        recipe.setCholesterol(Float.parseFloat((String) request.get("cholesterol")));
        recipe.setPotassium(Float.parseFloat((String) request.get("potassium")));
        recipe.setAuthor((String) request.get("author"));
        recipe.setPrepTime(Integer.parseInt((String) request.get("prepTime")));
        recipe.setCookTime(Integer.parseInt((String) request.get("cookTime")));
        recipe.setRestingTime(Integer.parseInt((String) request.get("restingTime")));
        recipe.setCuisine((String) request.get("cuisine"));
        recipe.setDescription((String) request.get("description"));
        recipe.setMealType((String) request.get("mealType"));
        recipe.setOverallRating(Float.parseFloat((String) request.get("overallRating")));
        recipe.setImageLink((String) request.get("imageLink"));

        // For imageBinary, assuming request.get("imageBinary") is a byte[]
        recipe.setImageBinary(Base64.getDecoder().decode(request.get("imageBinary")));

        this.service.create(recipe);
        return "created recipe";
    }

    @GetMapping("/{userId}")
    public List<Recipe> getRecipesByUser(@PathVariable String userId) {
        return this.service.getRecipesByUser(userId); 
    }
    
}

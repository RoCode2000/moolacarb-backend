package com.uow.moolacarb.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
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

    @GetMapping("/countActive")
    public Long countAllActive() {
        return this.service.countAllActive();
    }
}

package com.uow.moolacarb.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uow.moolacarb.model.WeightHistory;
import com.uow.moolacarb.service.WeightHistoryService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/weight")
public class WeightHistoryController {

    private final WeightHistoryService service;

    public WeightHistoryController(WeightHistoryService service) {
        this.service = service;
    }

    // Add new weight
    @PostMapping("/add")
    public ResponseEntity<String> addWeight(
            @RequestParam String firebaseId,
            @RequestParam float weight) {
        service.addWeight(firebaseId, weight);
        return ResponseEntity.ok("Weight added successfully");
    }

    // Get current weight
    @GetMapping("/current/{firebaseId}")
    public ResponseEntity<WeightHistory> getCurrentWeight(@PathVariable String firebaseId) {
        return ResponseEntity.ok(service.getCurrentWeight(firebaseId));
    }

    // Get full weight history
    @GetMapping("/history/{firebaseId}")
    public ResponseEntity<List<WeightHistory>> getWeightHistory(@PathVariable String firebaseId) {
        return ResponseEntity.ok(service.getWeightHistory(firebaseId));
    }
}

package com.uow.moolacarb.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uow.moolacarb.model.HeightHistory;
import com.uow.moolacarb.service.HeightHistoryService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/height")
public class HeightHistoryController {

    private final HeightHistoryService service;

    public HeightHistoryController(HeightHistoryService service) {
        this.service = service;
    }

    // Add new height
    @PostMapping("/add")
    public ResponseEntity<String> addHeight(
            @RequestParam String firebaseId,
            @RequestParam float height) {
        service.addHeight(firebaseId, height);
        return ResponseEntity.ok("Height added successfully");
    }

    // Get current height
    @GetMapping("/current/{firebaseId}")
    public ResponseEntity<HeightHistory> getCurrentHeight(@PathVariable String firebaseId) {
        return ResponseEntity.ok(service.getCurrentHeight(firebaseId));
    }

    // Get full height history
    @GetMapping("/history/{firebaseId}")
    public ResponseEntity<List<HeightHistory>> getHeightHistory(@PathVariable String firebaseId) {
        return ResponseEntity.ok(service.getHeightHistory(firebaseId));
    }
}

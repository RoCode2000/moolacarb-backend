// src/main/java/com/uow/moolacarb/controller/MealLogController.java
package com.uow.moolacarb.controller;

import org.springframework.web.bind.annotation.*;
import com.uow.moolacarb.model.MealLog;
import com.uow.moolacarb.service.MealLogService;
import java.util.List;

@RestController
@RequestMapping("/api/meallogs")
@CrossOrigin(origins = "*")
public class MealLogController {
    private final MealLogService service;

    public MealLogController(MealLogService service) {
        this.service = service;
    }

    @GetMapping("/by-firebase/{firebaseId}")
    public List<MealLog> listByFirebase(@PathVariable String firebaseId) {
        return service.listByFirebase(firebaseId);
    }

    @PostMapping("/by-firebase/{firebaseId}")
    public MealLog createByFirebase(@PathVariable String firebaseId, @RequestBody MealLog body) {
        return service.createByFirebase(firebaseId, body);
    }

    @PutMapping("/{id}")
    public MealLog update(@PathVariable Integer id, @RequestBody MealLog body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

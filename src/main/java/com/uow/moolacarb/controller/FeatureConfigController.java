package com.uow.moolacarb.controller;

import com.uow.moolacarb.model.FeatureConfig;
import com.uow.moolacarb.service.FeatureConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // tighten for production
@RestController
@RequestMapping("/api/features")
public class FeatureConfigController {
  private final FeatureConfigService service;
  public FeatureConfigController(FeatureConfigService service) { this.service = service; }

  // GET /api/features?userType=FREE | PREMIUM
  @GetMapping
  public List<FeatureConfig> get(@RequestParam String userType) {
    return service.list(userType);
  }
}

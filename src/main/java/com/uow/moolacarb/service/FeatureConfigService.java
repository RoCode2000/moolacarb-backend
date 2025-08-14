package com.uow.moolacarb.service;

import com.uow.moolacarb.model.FeatureConfig;
import com.uow.moolacarb.repository.FeatureConfigJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureConfigService {
  private final FeatureConfigJdbcRepository repo;
  public FeatureConfigService(FeatureConfigJdbcRepository repo) { this.repo = repo; }

  public List<FeatureConfig> list(String userType) {
    return repo.listForUserType(userType);
  }
}

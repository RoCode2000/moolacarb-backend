package com.uow.moolacarb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uow.moolacarb.model.WeightHistory;
import com.uow.moolacarb.repository.WeightHistoryRepository;

@Service
public class WeightHistoryService {

    private final WeightHistoryRepository repo;

    public WeightHistoryService(WeightHistoryRepository repo) {
        this.repo = repo;
    }

    public void addWeight(String firebaseId, float weight) {
        repo.insert(firebaseId, weight);
    }

    public WeightHistory getCurrentWeight(String firebaseId) {
        return repo.getCurrent(firebaseId);
    }

    public List<WeightHistory> getWeightHistory(String firebaseId) {
        return repo.getHistory(firebaseId);
    }
}

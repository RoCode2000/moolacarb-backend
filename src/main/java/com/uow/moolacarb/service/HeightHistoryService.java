package com.uow.moolacarb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uow.moolacarb.model.HeightHistory;
import com.uow.moolacarb.repository.HeightHistoryRepository;

@Service
public class HeightHistoryService {

    private final HeightHistoryRepository repo;

    public HeightHistoryService(HeightHistoryRepository repo) {
        this.repo = repo;
    }

    public void addHeight(String firebaseId, float height) {
        repo.insert(firebaseId, height);
    }

    public HeightHistory getCurrentHeight(String firebaseId) {
        return repo.getCurrent(firebaseId);
    }

    public List<HeightHistory> getHeightHistory(String firebaseId) {
        return repo.getHistory(firebaseId);
    }
}

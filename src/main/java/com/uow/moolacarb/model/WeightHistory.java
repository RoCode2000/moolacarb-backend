package com.uow.moolacarb.model;

import java.time.LocalDateTime;

public class WeightHistory {
    private int weightHistoryId;
    private String firebaseId;
    private float weight;
    private LocalDateTime lastUpdated;
    private String isCurrent; // "Y"/"N"

    public WeightHistory() {} // required for BeanPropertyRowMapper/Jackson

    public int getWeightHistoryId() { return weightHistoryId; }
    public void setWeightHistoryId(int weightHistoryId) { this.weightHistoryId = weightHistoryId; }

    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getIsCurrent() { return isCurrent; }
    public void setIsCurrent(String isCurrent) { this.isCurrent = isCurrent; }
}

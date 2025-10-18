// src/main/java/com/uow/moolacarb/model/HeightHistory.java
package com.uow.moolacarb.model;

import java.time.LocalDateTime;

public class HeightHistory {
    private int heightHistoryId;
    private String firebaseId;
    private float height;
    private LocalDateTime lastUpdated;
    private String isCurrent; // "Y" / "N"

    public HeightHistory() {
        // no-args constructor required by BeanPropertyRowMapper & Jackson
    }

    public int getHeightHistoryId() {
        return heightHistoryId;
    }

    public void setHeightHistoryId(int heightHistoryId) {
        this.heightHistoryId = heightHistoryId;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(String isCurrent) {
        this.isCurrent = isCurrent;
    }
}

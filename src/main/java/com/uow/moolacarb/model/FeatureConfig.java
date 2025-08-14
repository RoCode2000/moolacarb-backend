package com.uow.moolacarb.model;

import java.time.Instant;

public class FeatureConfig {
  private Long featureConfigId;
  private String userType;     // FREE | PREMIUM
  private String feature;      // e.g., barcodeScanner
  private String description;
  private Integer usageLimit;  // null = unlimited
  private String status;       // ON | OFF
  private Integer displayOrder;
  private Instant createdAt;
  private Instant updatedAt;

  public Long getFeatureConfigId() { return featureConfigId; }
  public void setFeatureConfigId(Long v) { this.featureConfigId = v; }
  public String getUserType() { return userType; }
  public void setUserType(String v) { this.userType = v; }
  public String getFeature() { return feature; }
  public void setFeature(String v) { this.feature = v; }
  public String getDescription() { return description; }
  public void setDescription(String v) { this.description = v; }
  public Integer getUsageLimit() { return usageLimit; }
  public void setUsageLimit(Integer v) { this.usageLimit = v; }
  public String getStatus() { return status; }
  public void setStatus(String v) { this.status = v; }
  public Integer getDisplayOrder() { return displayOrder; }
  public void setDisplayOrder(Integer v) { this.displayOrder = v; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant v) { this.createdAt = v; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant v) { this.updatedAt = v; }
}

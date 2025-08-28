package com.uow.moolacarb.DataTransferObject;

public class UserUpdateRequest {
    private String status;
    private String premium; 

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPremium() { return premium; }
    public void setPremium(String premium) { this.premium = premium; }
}
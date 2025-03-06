package com.example.ecotrack.WasteManagement;

public class PickupRequest {
    private String requestId;
    private String userId;
    private String location;
    private String wasteType;
    private float weight;
    private String pickupDate;
    private String pickupTime;
    private String notes;
    private String status;
    private long createdAt;
    private String lastComment;

    // Empty constructor needed for Firebase
    public PickupRequest() {
    }

    public PickupRequest(String requestId, String userId, String location, String wasteType,
                         float weight, String pickupDate, String pickupTime,
                         String notes, String status, long createdAt) {
        this.requestId = requestId;
        this.userId = userId;
        this.location = location;
        this.wasteType = wasteType;
        this.weight = weight;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.notes = notes;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastComment() {
        return lastComment;
    }

    public void setLastComment(String lastComment) {
        this.lastComment = lastComment;
    }
}
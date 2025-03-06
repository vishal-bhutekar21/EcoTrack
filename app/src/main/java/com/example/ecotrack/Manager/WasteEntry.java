package com.example.ecotrack.Manager;

public class WasteEntry {
    private String id;
    private String amount;
    private String date;
    private String imageUrl;
    private String recyclable;
    private String subType;
    private String wasteType;

    // Constructor
    public WasteEntry() {
        // Required for Firebase
    }

    public WasteEntry(String id, String amount, String date, String imageUrl,
                      String recyclable, String subType, String wasteType) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.imageUrl = imageUrl;
        this.recyclable = recyclable;
        this.subType = subType;
        this.wasteType = wasteType;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getRecyclable() { return recyclable; }
    public void setRecyclable(String recyclable) { this.recyclable = recyclable; }
    public String getSubType() { return subType; }
    public void setSubType(String subType) { this.subType = subType; }
    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }
}
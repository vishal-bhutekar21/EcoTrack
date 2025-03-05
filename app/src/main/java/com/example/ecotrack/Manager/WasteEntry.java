package com.example.ecotrack.Manager;

public class WasteEntry {
    private String key; // Unique key from Firebase
    private String amount, date, recyclable, subType, wasteType, imageUrl;

    public WasteEntry() {
        // Default constructor required for Firebase
    }

    public WasteEntry(String key, String amount, String date, String recyclable, String subType, String wasteType, String imageUrl) {
        this.key = key;
        this.amount = amount;
        this.date = date;
        this.recyclable = recyclable;
        this.subType = subType;
        this.wasteType = wasteType;
        this.imageUrl = imageUrl;
    }

    // Getter and Setter for key
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getRecyclable() {
        return recyclable;
    }

    public String getSubType() {
        return subType;
    }

    public String getWasteType() {
        return wasteType;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

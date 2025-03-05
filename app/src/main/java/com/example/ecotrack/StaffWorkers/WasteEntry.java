package com.example.ecotrack.StaffWorkers;

public class WasteEntry {
    private String wasteType, subType, amount, recyclable, date, imageUrl;

    public WasteEntry() {
        // Default constructor required for Firebase
    }

    public WasteEntry(String wasteType, String subType, String amount, String recyclable, String date, String imageUrl) {
        this.wasteType = wasteType;
        this.subType = subType;
        this.amount = amount;
        this.recyclable = recyclable;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public String getWasteType() {
        return wasteType;
    }

    public String getSubType() {
        return subType;
    }

    public String getAmount() {
        return amount;
    }

    public String getRecyclable() {
        return recyclable;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}


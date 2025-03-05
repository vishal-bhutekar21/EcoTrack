package com.example.ecotrack.Manager;

public class WasteEntry {
    private String amount, date, imageUrl, recyclable, subType, wasteType;

    public WasteEntry() {
        // Empty constructor required for Firebase
    }

    public WasteEntry(String amount, String date, String imageUrl, String recyclable, String subType, String wasteType) {
        this.amount = amount;
        this.date = date;
        this.imageUrl = imageUrl;
        this.recyclable = recyclable;
        this.subType = subType;
        this.wasteType = wasteType;
    }

    public String getAmount() { return amount; }
    public String getDate() { return date; }
    public String getImageUrl() { return imageUrl; }
    public String getRecyclable() { return recyclable; }
    public String getSubType() { return subType; }
    public String getWasteType() { return wasteType; }
}

package com.example.ecotrack.WasteManagement;

public class WastePickupRequest {
    private String id;
    private String wasteType;
    private String location;
    private String status;
    private long timestamp;
    private int amount;  // Added amount field

    public WastePickupRequest() {}

    public WastePickupRequest(String id, String wasteType, String location, String status, long timestamp, int amount) {
        this.id = id;
        this.wasteType = wasteType;
        this.location = location;
        this.status = status;
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
    }
    public String getWasteType() { return wasteType; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public int getAmount() { return amount; }  // Getter for amount
}

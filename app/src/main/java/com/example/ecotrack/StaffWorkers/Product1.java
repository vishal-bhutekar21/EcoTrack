package com.example.ecotrack.Admin;

import java.util.Date;

public class Product1 {
    private String productId; // New field for Firebase ID
    private String batchNo, productName, productType, status;
    private Date expiryDate;
    private int remainingDays;

    // Default constructor for Firebase
    public Product1() { }

    public Product1(String productId, String batchNo, String productName, String productType, Date expiryDate, int remainingDays, String status) {
        this.productId = productId;
        this.batchNo = batchNo;
        this.productName = productName;
        this.productType = productType;
        this.expiryDate = expiryDate;
        this.remainingDays = remainingDays;
        this.status = status;
    }

    public String getProductId() { return productId; }
    public String getBatchNo() { return batchNo; }
    public String getProductName() { return productName; }
    public String getProductType() { return productType; }
    public Date getExpiryDate() { return expiryDate; }
    public int getRemainingDays() { return remainingDays; }
    public String getStatus() { return status; }

    public void setProductId(String productId) { this.productId = productId; }
    public void setStatus(String status) { this.status = status; }
}

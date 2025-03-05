package com.example.ecotrack.Admin;

import java.util.Date;

public class Product {
    private String batchNo, productName, productType;
    private Date expiryDate;
    private int remainingDays;

    public Product(String batchNo, String productName, String productType, Date expiryDate, int remainingDays) {
        this.batchNo = batchNo;
        this.productName = productName;
        this.productType = productType;
        this.expiryDate = expiryDate;
        this.remainingDays = remainingDays;
    }

    public String getBatchNo() { return batchNo; }
    public String getProductName() { return productName; }
    public String getProductType() { return productType; }
    public Date getExpiryDate() { return expiryDate; }
    public int getRemainingDays() { return remainingDays; }
}

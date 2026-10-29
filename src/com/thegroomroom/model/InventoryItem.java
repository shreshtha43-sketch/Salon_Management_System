package com.thegroomroom.model;

import java.sql.Timestamp;

public class InventoryItem {
    private int id;
    private String productName;
    private String category;
    private int stockQuantity;
    private double unitPrice;
    private double costPrice;
    private int alertThreshold;
    private String supplierInfo;
    private Timestamp lastRestocked;

    public InventoryItem() {}

    public InventoryItem(int id, String productName, String category, int stockQuantity, double unitPrice, double costPrice, int alertThreshold, String supplierInfo, Timestamp lastRestocked) {
        this.id = id;
        this.productName = productName;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.unitPrice = unitPrice;
        this.costPrice = costPrice;
        this.alertThreshold = alertThreshold;
        this.supplierInfo = supplierInfo;
        this.lastRestocked = lastRestocked;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getCostPrice() { return costPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }

    public int getAlertThreshold() { return alertThreshold; }
    public void setAlertThreshold(int alertThreshold) { this.alertThreshold = alertThreshold; }

    public String getSupplierInfo() { return supplierInfo; }
    public void setSupplierInfo(String supplierInfo) { this.supplierInfo = supplierInfo; }

    public Timestamp getLastRestocked() { return lastRestocked; }
    public void setLastRestocked(Timestamp lastRestocked) { this.lastRestocked = lastRestocked; }

    public boolean isLowStock() {
        return stockQuantity <= alertThreshold;
    }
}

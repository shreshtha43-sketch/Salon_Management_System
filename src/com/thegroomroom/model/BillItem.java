package com.thegroomroom.model;

public class BillItem {
    private int id;
    private int billId;
    private Integer serviceId;
    private String itemName;
    private String itemType; // SERVICE, PRODUCT
    private int quantity;
    private double price;

    public BillItem() {}

    public BillItem(int id, int billId, Integer serviceId, String itemName, String itemType, int quantity, double price) {
        this.id = id;
        this.billId = billId;
        this.serviceId = serviceId;
        this.itemName = itemName;
        this.itemType = itemType;
        this.quantity = quantity;
        this.price = price;
    }

    public BillItem(String itemName, String itemType, int quantity, double price) {
        this.itemName = itemName;
        this.itemType = itemType;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getTotal() { return quantity * price; }
}

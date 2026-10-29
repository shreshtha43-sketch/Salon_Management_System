package com.thegroomroom.model;

public class ServiceItem {
    private int id;
    private String category; // HAIR, BEARD, SPA_DETOX, FACIAL_CLEANUP, BRIDAL_GROOM
    private String name;
    private int durationMinutes;
    private double price;
    private String description;
    private boolean active;

    public ServiceItem() {}

    public ServiceItem(int id, String category, String name, int durationMinutes, double price, String description, boolean active) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.description = description;
        this.active = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getFormattedPrice() {
        return String.format("₹%.2f", price);
    }

    @Override
    public String toString() {
        return name + " (" + getFormattedPrice() + ")";
    }
}

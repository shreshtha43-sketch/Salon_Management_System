package com.thegroomroom.model;

public class Barber {
    private int id;
    private Integer userId;
    private String name;
    private String specialization;
    private int chairNumber;
    private double commissionRate;
    private String phone;
    private String status; // AVAILABLE, IN_SERVICE, ON_BREAK, OFF_DUTY
    private int totalCuts;

    public Barber() {}

    public Barber(int id, Integer userId, String name, String specialization, int chairNumber, double commissionRate, String phone, String status, int totalCuts) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.specialization = specialization;
        this.chairNumber = chairNumber;
        this.commissionRate = commissionRate;
        this.phone = phone;
        this.status = status;
        this.totalCuts = totalCuts;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getChairNumber() { return chairNumber; }
    public void setChairNumber(int chairNumber) { this.chairNumber = chairNumber; }

    public double getCommissionRate() { return commissionRate; }
    public void setCommissionRate(double commissionRate) { this.commissionRate = commissionRate; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalCuts() { return totalCuts; }
    public void setTotalCuts(int totalCuts) { this.totalCuts = totalCuts; }

    @Override
    public String toString() {
        return name + " (Chair " + chairNumber + ")";
    }
}

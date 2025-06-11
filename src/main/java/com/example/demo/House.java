package com.example.demo;

import java.time.LocalDate;

public class House {
    private String name;
    private String location;
    private double price;
    private int capacity;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;

    private int houseID;
    private double avgRating;
    public int getId() { return houseID; }

    public House(int houseID, String name, String location, double price, int capacity,
                 LocalDate startDate, LocalDate endDate, boolean isActive) {
        this.houseID = houseID;
        this.name = name;
        this.location = location;
        this.price = price;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }
    public House(int houseID, String name, String location, double price) {
        this.houseID = houseID;
        this.name = name;
        this.location = location;
        this.price=price;
    }

    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean getIsActive() { return isActive; }
    public int getCapacity() { return capacity; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public double getPrice() { return price; }
    public double getAverageRating() { return avgRating; }
    public void setAverageRating(double averageRating) { this.avgRating = averageRating;}
}
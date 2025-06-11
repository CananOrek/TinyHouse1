package com.example.demo;

import java.time.LocalDate;

public class HouseAvailability {
    private LocalDate startDate;
    private LocalDate endDate;

    public HouseAvailability(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}
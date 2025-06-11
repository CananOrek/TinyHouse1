package com.example.demo;

import javafx.fxml.FXML;

import java.time.LocalDate;

public class Reservation {
    private int reservationID;
    private String tenantName;
    private String houseName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private String paymentStatus;
    private boolean isConfirmed;
    private double totalAmount;
    private LocalDate paymentDate;

    // 8 parametreli constructor (Ev Sahibi & Admin için)
    public Reservation(int reservationID, String tenantName, String houseName,
                       LocalDate checkInDate, LocalDate checkOutDate,
                       String status, String paymentStatus, boolean isConfirmed) {
        this.reservationID = reservationID;
        this.tenantName = tenantName;
        this.houseName = houseName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.isConfirmed = isConfirmed;
    }

    public Reservation(int reservationID, String houseName, String tenantName,
                       double totalAmount, String paymentStatus, LocalDate paymentDate) {
        this.reservationID = reservationID;
        this.houseName = houseName;
        this.tenantName = tenantName;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    // 6 parametreli constructor (Kiracı için)
    public Reservation(int reservationID, String houseName,
                       LocalDate checkInDate, LocalDate checkOutDate,
                       String status,double totalAmount) {
        this.reservationID = reservationID;
        this.houseName = houseName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }


    // Getter'lar
    public int getReservationID() {
        return reservationID;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getHouseName() {
        return houseName;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus=paymentStatus;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public Boolean getConfirmed() {
        return isConfirmed;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }
}

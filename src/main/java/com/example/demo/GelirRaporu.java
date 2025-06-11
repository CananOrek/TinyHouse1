package com.example.demo;

import java.time.LocalDate;

public class GelirRaporu {
    private String evAdi;
    private double tutar;
    private LocalDate tarih;

    public GelirRaporu(String evAdi, double tutar, LocalDate tarih) {
        this.evAdi = evAdi;
        this.tutar = tutar;
        this.tarih = tarih;
    }

    public String getEvAdi() {
        return evAdi;
    }

    public double getTutar() {
        return tutar;
    }

    public LocalDate getTarih() {
        return tarih;
    }
}

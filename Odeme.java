package com.example.demo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Odeme {
    private StringProperty kullanici;
    private StringProperty ev;
    private StringProperty tutar;
    private StringProperty tarih;

    public Odeme(String kullanici, String ev, String tutar, String tarih) {
        this.kullanici = new SimpleStringProperty(kullanici);
        this.ev = new SimpleStringProperty(ev);
        this.tutar = new SimpleStringProperty(tutar);
        this.tarih = new SimpleStringProperty(tarih);
    }

    public StringProperty kullaniciProperty() { return kullanici; }
    public StringProperty evProperty() { return ev; }
    public StringProperty tutarProperty() { return tutar; }
    public StringProperty tarihProperty() { return tarih; }

    public String getTutar() {
        return tutar.get();
    }
}

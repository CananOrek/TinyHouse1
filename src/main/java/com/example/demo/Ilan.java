package com.example.demo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

    public class Ilan {
        private StringProperty evSahibi;
        private StringProperty evAdi;
        private StringProperty konum;
        private StringProperty fiyat;
        private StringProperty tarih;

        public Ilan(String evSahibi, String evAdi, String konum, String fiyat, String tarih) {
            this.evSahibi = new SimpleStringProperty(evSahibi);
            this.evAdi = new SimpleStringProperty(evAdi);
            this.konum = new SimpleStringProperty(konum);
            this.fiyat = new SimpleStringProperty(fiyat);
            this.tarih = new SimpleStringProperty(tarih);
        }

        public StringProperty evSahibiProperty() { return evSahibi; }
        public StringProperty evAdiProperty() { return evAdi; }
        public StringProperty konumProperty() { return konum; }
        public StringProperty fiyatProperty() { return fiyat; }
        public StringProperty tarihProperty() { return tarih; }
        public String getEvAdi() { return evAdi.get(); }
        public String getKonum() { return konum.get(); }

        public void setFiyat(String fiyat) {
            this.fiyat.set(fiyat);
        }
    }



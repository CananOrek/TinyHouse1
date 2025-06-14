package com.example.demo;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.Stage;


import java.io.IOException;


public class EvSahibiController {

    @FXML
    private Button yorumlarButonu;
    @FXML
    private Button rezervasyonButonu;
    @FXML
    private Button btnOdeme;



    @FXML
    private void goToReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi_rezervasyonlar.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) rezervasyonButonu.getScene().getWindow(); // herhangi bir sahne elemanı yerine kullanılabilir
            stage.setScene(scene);
            stage.setTitle("Rezervasyonlar");
        } catch (IOException e) {
            // e.printStackTrace();
            showAlert("Hata", "Ekran yüklenemedi: " + e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    @FXML
    private void goToComments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi_yorumlar.fxml"));
            Stage stage = (Stage) yorumlarButonu.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            // e.printStackTrace();
            showAlert("Hata", "Yorumlar ekranı yüklenemedi: " + e.getMessage());
        }
    }

    @FXML
    private Button btnRezervasyon;

    @FXML
    private void handleReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EvSahibiIlanlar.fxml"));
            Stage stage = (Stage) btnRezervasyon.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            showAlert("Hata", "Rezervasyonlar ekranı açılamadı: " + e.getMessage());
        }
    }

    @FXML
    private void handlePayments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi_odeme.fxml"));
            Stage stage = (Stage) btnOdeme.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            showAlert("Hata", "Ödeme bilgileri ekranı açılamadı: " + e.getMessage());
        }
    }

    @FXML
    private Button gelirRaporuButton;

    @FXML
    private void gelirRaporunaGit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi_gelir_raporu.fxml")); // <== burada dosya adı doğru olmalı
            Stage stage = (Stage) gelirRaporuButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            showAlert("Ekran Hatası", "Gelir Raporu ekranı açılamadı: " + e.getMessage());
        }
    }
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Çıkış");
        alert.setHeaderText(null);
        alert.setContentText("Çıkış yapılıyor...");
        alert.showAndWait();


        System.exit(0);
    }


}

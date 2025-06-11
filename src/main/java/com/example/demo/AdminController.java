package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    @FXML
    private Button logoutButton;

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Çıkış");
        alert.setHeaderText(null);
        alert.setContentText("Çıkış yapılıyor...");
        alert.showAndWait();


        System.exit(0);
    }

    @FXML
    private void goToUserManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_user_management.fxml"));
            Scene scene = new Scene(loader.load());

            // Yeni sahne oluştur
            Stage stage = new Stage();
            stage.setTitle("Kullanıcı Yönetimi");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Kullanıcı yönetimi ekranı yüklenemedi: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}

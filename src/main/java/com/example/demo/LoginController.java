package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    public static int currentUserId = -1;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try (Connection conn = Database.getConnection()) {

            String sql = "SELECT UserID, RoleID FROM Users WHERE Email = ? AND Password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("UserID");
                int roleId = rs.getInt("RoleID");

                switch (roleId) {
                    case 1:
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
                            Parent root = loader.load();
                            Scene adminScene = new Scene(root);
                            Stage stage = (Stage) emailField.getScene().getWindow();
                            stage.setScene(adminScene);
                            stage.setTitle("Admin Paneli");
                        } catch (IOException e) {
                            showAlert("Yükleme Hatası", "Admin paneli yüklenemedi: " + e.getMessage());
                        }
                        break;
                    case 2:
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi.fxml"));
                            Parent root = loader.load();
                            Scene scene = new Scene(root);
                            Stage stage = (Stage) emailField.getScene().getWindow();
                            stage.setScene(scene);
                            stage.setTitle("Ev Sahibi Paneli");
                        } catch (IOException e) {
                            showAlert("Yükleme Hatası", "Ev Sahibi paneli yüklenemedi: " + e.getMessage());
                        }
                        break;
                    case 3:
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("kiraci.fxml"));
                            Parent root = loader.load();
                            Scene scene = new Scene(root);
                            Stage stage = (Stage) emailField.getScene().getWindow();
                            stage.setScene(scene);
                            stage.setTitle("Kiracı Paneli");
                        } catch (IOException e) {
                            showAlert("Yükleme Hatası", "Kiracı paneli yüklenemedi: " + e.getMessage());
                        }
                        break;
                    default:
                        showAlert("Hata", "Bilinmeyen rol!");
                }
            } else {
                showAlert("Hatalı Giriş", "E-posta veya şifre yanlış.");
            }

        } catch (SQLException e) {
            showAlert("Bağlantı Hatası", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);  // başlık gizli
        alert.setContentText(message);
        alert.showAndWait();
    }
}
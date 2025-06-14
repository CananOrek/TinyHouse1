package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PipedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class KiraciOdemeController {

    @FXML private TextField txtReservationId;
    @FXML private TextField txtAmount;
    @FXML private ComboBox<String> cmbPaymentMethod;
    @FXML private Label labelToplam;


    @FXML
    public void initialize() {
        // Ödeme yöntemlerini combobox'a yükle
        cmbPaymentMethod.getItems().addAll("Kredi Kartı", "Havale/EFT", "Kapıda Ödeme");
    }

    @FXML
    private void handlePayment() {
        try {
            int reservationId = Integer.parseInt(txtReservationId.getText());
            double amount = Double.parseDouble(txtAmount.getText());
            labelToplam.setText("Toplam Ödeme: ₺" + txtAmount.getText());

            String paymentMethod = cmbPaymentMethod.getValue();

            if (paymentMethod == null || paymentMethod.isEmpty()) {
                showAlert("Hata", "Lütfen bir ödeme yöntemi seçin!");
                return;
            }

            // Veritabanına ödeme kaydet
            try (Connection conn = Database.getConnection()) {
                String sql = "INSERT INTO Payments (ReservationID, PaymentDate, Amount, PaymentMethod) " +
                        "VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, reservationId);
                stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now())); // Bugünün tarihi
                stmt.setDouble(3, amount);
                stmt.setString(4, paymentMethod);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    showAlert("Başarılı", "Ödeme başarıyla gerçekleştirildi.");
                } else {
                    showAlert("Hata", "Ödeme kaydedilemedi!");
                }
            } catch (SQLException e) {
                showAlert("Veritabanı Hatası", e.getMessage());
            }

        } catch (NumberFormatException e) {
            showAlert("Hata", "Lütfen geçerli bir rezervasyon ID ve tutar girin.");
        }
    }
    @FXML
    private Button btnBack;
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
}
}

package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.example.demo.LoginController.currentUserId;

public class KiraciRezervasyonController {

    @FXML private DatePicker checkInDate;
    @FXML private DatePicker checkOutDate;

    private House selectedHouse;

    public void setHouse(House house) {
        this.selectedHouse = house;
    }

    @FXML
    private void handleMakeReservation() {
        LocalDate checkIn = checkInDate.getValue();
        LocalDate checkOut = checkOutDate.getValue();

        if (checkIn == null || checkOut == null || checkIn.isAfter(checkOut)) {
            showAlert("Hata", "Geçerli tarih aralığı seçin.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("EXEC sp_RezervasyonEkle ?, ?, ?, ?");
            stmt.setInt(1, selectedHouse.getId()); // HouseID
            stmt.setInt(2, currentUserId);         // CustomerID
            stmt.setDate(3, java.sql.Date.valueOf(checkIn));
            stmt.setDate(4, java.sql.Date.valueOf(checkOut));
            stmt.executeUpdate();

            showAlert("Başarılı", "Rezervasyon oluşturuldu.");
        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
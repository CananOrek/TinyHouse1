package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.example.demo.LoginController.currentUserId;

public class KiraciRezervasyonController {

    @FXML private DatePicker checkInDate;
    @FXML private DatePicker checkOutDate;

    private House selectedHouse;
    @FXML private TableView<House> houseTable;
    @FXML private TableColumn<House, String> colName;
    @FXML private TableColumn<House, String> colLocation;
    @FXML private TableColumn<House, Double> colPrice;
    @FXML private TableColumn<House, Integer> colCapacity;
    @FXML private TableColumn<House, LocalDate> colStartDate;
    @FXML private TableColumn<House, LocalDate> colEndDate;

    private ObservableList<House> houseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        loadAvailableHouses();
    }

    private void loadAvailableHouses() {
        houseList.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM Houses WHERE IsActive = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("HouseName");
                String location = rs.getString("Location");
                double price = rs.getDouble("PricePerNight");
                int capacity = rs.getInt("Capacity");

                LocalDate startDate = rs.getDate("StartDate") != null ? rs.getDate("StartDate").toLocalDate() : null;
                LocalDate endDate = rs.getDate("EndDate") != null ? rs.getDate("EndDate").toLocalDate() : null;
                boolean isActive = rs.getBoolean("IsActive");

                int houseID = rs.getInt("HouseID"); // Eksik olan

                houseList.add(new House(houseID, name, location, price, capacity, startDate, endDate, isActive));            }

            houseTable.setItems(houseList);
        } catch (SQLException e) {
            showAlert("Veri Hatası", e.getMessage());
        }
    }



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
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kiraci.fxml"));
            Stage stage = (Stage) houseTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            System.out.println("Ekran açılmadı");
        }
    }
}

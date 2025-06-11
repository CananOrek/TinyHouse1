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
import java.sql.*;
import java.time.LocalDate;

public class KiraciController {

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

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
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

    @FXML
    private void handleReservation() {
        House selected = houseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Lütfen bir ev seçin.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kiraci_rezervasyon.fxml"));
            Parent root = loader.load();

            KiraciRezervasyonController controller = loader.getController();
            controller.setHouse(selected);

            Stage stage = new Stage();
            stage.setTitle("Rezervasyon Yap");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert("Hata", "Rezervasyon ekranı açılamadı: " + e.getMessage());
        }
    }

    @FXML
    private void goToMyReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kiraci_rezervasyonlar.fxml"));
            Stage stage = (Stage) houseTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            showAlert("Ekran Hatası", e.getMessage());
        }
    }

    @FXML
    private Button btnMyReservations;

    @FXML
    private void handleMyReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kiraci_rezervasyonlarim.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Rezervasyonlarım");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Hata", "Rezervasyonlarım ekranı açılamadı: " + e.getMessage());
        }
    }
}

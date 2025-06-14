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
    @FXML private TableColumn<House, Integer> colId;
    @FXML private TableColumn<House, String> colName;
    @FXML private TableColumn<House, String> colLocation;
    @FXML private TableColumn<House, Double> colPrice;
    @FXML private TableColumn<House, Double> colAverageRating;

    private ObservableList<House> houseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colAverageRating.setCellValueFactory(new PropertyValueFactory<>("averageRating"));

        loadPopularHouses();
    }

    private void loadPopularHouses() {
        houseList.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = """
                SELECT H.HouseID, H.HouseName, H.Location, H.PricePerNight,
                       dbo.fn_OrtalamaPuan(H.HouseID) AS AverageRating
                FROM Houses H
                WHERE H.IsActive = 1
                ORDER BY AverageRating DESC
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("HouseID");
                String name = rs.getString("HouseName");
                String location = rs.getString("Location");
                double price = rs.getDouble("PricePerNight");
                double avgRating = rs.getDouble("AverageRating");

                // House sınıfında böyle bir constructor varsa (capacity, startDate, endDate olmadan):
                House house = new House(id, name, location, price);
                house.setAverageRating(avgRating); // averageRating alanını set et

                houseList.add(house);

                // Eğer ortalama puan 1 ise uyarı ver
                if (avgRating == 1.0) {
                    showWarning("Uyarı", "Ev '" + name + "' sadece 1 puan aldı! Lütfen kontrol edin.");
                }
            }

            houseTable.setItems(houseList);

        } catch (SQLException e) {
            showWarning("Veritabanı Hatası", e.getMessage());
        }
    }

    private void showWarning(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML
    private Button btnRezervasyonlar;
    @FXML
    private void goToMyReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kiraci_rezervasyonlarim.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnRezervasyonlar.getScene().getWindow(); // herhangi bir sahne elemanı yerine kullanılabilir
            stage.setScene(scene);
            stage.setTitle("Rezervasyonlar");
        } catch (IOException e) {
            // e.printStackTrace();
            showWarning("Hata", "Ekran yüklenemedi: " + e.getMessage());
        }

    }
    @FXML
    private Button btnEvler;
    @FXML
    private void goToPopularHouses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kiraci_rezervasyon.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnEvler.getScene().getWindow(); // herhangi bir sahne elemanı yerine kullanılabilir
            stage.setScene(scene);
            stage.setTitle("Rezervasyonlar");
        } catch (IOException e) {
            // e.printStackTrace();
            showWarning("Hata", "Ekran yüklenemedi: " + e.getMessage());
        }

    }
    @FXML
    private Button btnYorumlarim;
    @FXML
    private void goToMyComments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KiraciYorumlarim.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnYorumlarim.getScene().getWindow(); // herhangi bir sahne elemanı yerine kullanılabilir
            stage.setScene(scene);
            stage.setTitle("Rezervasyonlar");
        } catch (IOException e) {
            // e.printStackTrace();
            showWarning("Hata", "Ekran yüklenemedi: " + e.getMessage());
        }

    }
    @FXML
    private Button btnOdeme;
    @FXML
    private void goToPaymentHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KiraciOdemeGecmisi.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnOdeme.getScene().getWindow(); // herhangi bir sahne elemanı yerine kullanılabilir
            stage.setScene(scene);
            stage.setTitle("Rezervasyonlar");
        } catch (IOException e) {
            // e.printStackTrace();
            showWarning("Hata", "Ekran yüklenemedi: " + e.getMessage());
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

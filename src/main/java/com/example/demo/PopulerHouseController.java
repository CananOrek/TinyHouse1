package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PopulerHouseController {

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
}

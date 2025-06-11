package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class GelistirilmisFiltrelemeController {

    @FXML private TableView<House> houseTable;
    @FXML private TableColumn<House, Integer> colId;
    @FXML private TableColumn<House, String> colName;
    @FXML private TableColumn<House, String> colLocation;
    @FXML private TableColumn<House, Double> colPrice;

    @FXML private TextField txtMinPrice;
    @FXML private TextField txtMaxPrice;
    @FXML private TextField txtLocation;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private TextField txtHouseType;

    private ObservableList<House> houseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    @FXML
    private void handleFilter() {
        houseList.clear();
        try (Connection conn = Database.getConnection()) {
            StringBuilder sql = new StringBuilder("""
                SELECT H.HouseID, H.HouseName, H.Location, H.PricePerNight
                FROM Houses H
                WHERE H.IsActive = 1
                """);

            // Dinamik filtreler
            if (!txtLocation.getText().isEmpty()) {
                sql.append(" AND H.Location LIKE ?");
            }
            if (!txtMinPrice.getText().isEmpty()) {
                sql.append(" AND H.PricePerNight >= ?");
            }
            if (!txtMaxPrice.getText().isEmpty()) {
                sql.append(" AND H.PricePerNight <= ?");
            }
            if (!txtHouseType.getText().isEmpty()) {
                sql.append(" AND H.Description LIKE ?"); // Ev tipi gibi kullanılabilir
            }

            // Tarih filtresi (müsaitlik)
            if (dpStartDate.getValue() != null && dpEndDate.getValue() != null) {
                sql.append("""
                    AND H.HouseID NOT IN (
                        SELECT R.HouseID FROM Reservations R
                        WHERE R.Status <> 'Iptal'
                        AND (R.CheckInDate <= ? AND R.CheckOutDate >= ?)
                    )
                    """);
            }

            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            if (!txtLocation.getText().isEmpty()) {
                stmt.setString(paramIndex++, "%" + txtLocation.getText() + "%");
            }
            if (!txtMinPrice.getText().isEmpty()) {
                stmt.setDouble(paramIndex++, Double.parseDouble(txtMinPrice.getText()));
            }
            if (!txtMaxPrice.getText().isEmpty()) {
                stmt.setDouble(paramIndex++, Double.parseDouble(txtMaxPrice.getText()));
            }
            if (!txtHouseType.getText().isEmpty()) {
                stmt.setString(paramIndex++, "%" + txtHouseType.getText() + "%");
            }
            if (dpStartDate.getValue() != null && dpEndDate.getValue() != null) {
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(dpEndDate.getValue()));
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(dpStartDate.getValue()));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("HouseID");
                String name = rs.getString("HouseName");
                String location = rs.getString("Location");
                double price = rs.getDouble("PricePerNight");

                houseList.add(new House(id, name, location, price));
            }

            houseTable.setItems(houseList);

        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Hata", "Lütfen geçerli bir fiyat değeri girin!");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
}
}


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

public class RezervasyonTakvimiController {

    @FXML private TableView<HouseAvailability> calendarTable;
    @FXML private TableColumn<HouseAvailability, LocalDate> colStartDate;
    @FXML private TableColumn<HouseAvailability, LocalDate> colEndDate;

    private ObservableList<HouseAvailability> reservedDates = FXCollections.observableArrayList();

    private int selectedHouseID;

    public void setSelectedHouseID(int houseID) {
        this.selectedHouseID = houseID;
        loadReservedDates();
    }

    @FXML
    public void initialize() {
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
    }

    private void loadReservedDates() {
        reservedDates.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = """
                SELECT CheckInDate, CheckOutDate
                FROM Reservations
                WHERE HouseID = ? AND Status <> 'Iptal'
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selectedHouseID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate startDate = rs.getDate("CheckInDate").toLocalDate();
                LocalDate endDate = rs.getDate("CheckOutDate").toLocalDate();

                reservedDates.add(new HouseAvailability(startDate, endDate));
            }

            calendarTable.setItems(reservedDates);

        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", e.getMessage());
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


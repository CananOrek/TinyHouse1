package com.example.demo;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class KiraciRezervasyonlarController {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> colId;
    @FXML private TableColumn<Reservation, String> colHouseName;
    @FXML private TableColumn<Reservation, String> colCheckIn;
    @FXML private TableColumn<Reservation, String> colCheckOut;
    @FXML private TableColumn<Reservation, String> colStatus;
    @FXML private TableColumn<Reservation, Double> colAmount; // Ödeme miktarını göstermek için

    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        colHouseName.setCellValueFactory(new PropertyValueFactory<>("houseName"));

        colCheckIn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCheckInDate().toString()));
        colCheckOut.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCheckOutDate().toString()));

        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        loadReservations();
    }


    private void loadReservations() {
        reservations.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT R.ReservationID, H.HouseName, R.CheckInDate, R.CheckOutDate, R.Status, " +
                    "(SELECT ISNULL(SUM(P.Amount), 0) FROM Payments P WHERE P.ReservationID = R.ReservationID) AS Amount " +
                    "FROM Reservations R " +
                    "INNER JOIN Houses H ON R.HouseID = H.HouseID " +
                    "WHERE R.CustomerID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, LoginController.currentUserId); // Güncel kullanıcı ID'si
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("ReservationID");
                String houseName = rs.getString("HouseName");
                LocalDate in = rs.getDate("CheckInDate").toLocalDate();
                LocalDate out = rs.getDate("CheckOutDate").toLocalDate();
                String status = rs.getString("Status");
                double amount = rs.getDouble("Amount");

                reservations.add(new Reservation(id, houseName, in, out, status, amount));
            }

            reservationTable.setItems(reservations);
        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", e.getMessage());
        }
    }

    @FXML
    private void handleCancelReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Lütfen iptal edilecek bir rezervasyon seçin.");
            return;
        }

        if (selected.getStatus().equalsIgnoreCase("Iptal")) {
            showAlert("Uyarı", "Bu rezervasyon zaten iptal edilmiş.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE Reservations SET Status = 'Iptal' WHERE ReservationID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selected.getReservationID());
            stmt.executeUpdate();

            showAlert("Başarılı", "Rezervasyon iptal edildi.");
            loadReservations();
        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kiraci.fxml"));
            Stage stage = (Stage) reservationTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            showAlert("Geri Dönüş Hatası", e.getMessage());
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
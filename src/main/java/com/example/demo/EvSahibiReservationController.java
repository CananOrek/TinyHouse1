package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class EvSahibiReservationController {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> colId;
    @FXML private TableColumn<Reservation, String> colTenant;
    @FXML private TableColumn<Reservation, String> colHouse;
    @FXML private TableColumn<Reservation, LocalDate> colCheckIn;
    @FXML private TableColumn<Reservation, LocalDate> colCheckOut;
    @FXML private TableColumn<Reservation, String> colStatus;
    @FXML private TableColumn<Reservation, String> colPayment;
    @FXML private TableColumn<Reservation, Boolean> colConfirmed;

    private ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private final int OWNER_ID = 2; // Giriş yapan ev sahibinin ID'si burada sabit

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        colTenant.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
        colHouse.setCellValueFactory(new PropertyValueFactory<>("houseName"));
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        colConfirmed.setCellValueFactory(new PropertyValueFactory<>("confirmed"));

        loadReservations();
    }

    private void loadReservations() {
        reservationList.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT R.ReservationID, U.FullName AS TenantName, H.HouseName, " +
                    "R.CheckInDate, R.CheckOutDate, R.Status, R.PaymentStatus, R.IsConfirmed " +
                    "FROM Reservations R " +
                    "JOIN Users U ON R.CustomerID = U.UserID " +
                    "JOIN Houses H ON R.HouseID = H.HouseID " +
                    "WHERE H.OwnerID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, OWNER_ID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservationList.add(new Reservation(
                        rs.getInt("ReservationID"),
                        rs.getString("TenantName"),
                        rs.getString("HouseName"),
                        rs.getDate("CheckInDate").toLocalDate(),
                        rs.getDate("CheckOutDate").toLocalDate(),
                        rs.getString("Status"),
                        rs.getString("PaymentStatus"),
                        rs.getBoolean("IsConfirmed")
                ));
            }
            reservationTable.setItems(reservationList);
        } catch (SQLException e) {
            showAlert("Veri Hatası", e.getMessage());
        }
    }

    @FXML
    private void handleApprove() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Lütfen bir rezervasyon seçin.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE Reservations SET IsConfirmed = 1 WHERE ReservationID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selected.getReservationID());
            stmt.executeUpdate();

            showAlert("Onaylandı", "Rezervasyon onaylandı.");
            loadReservations();
        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", e.getMessage());
        }
    }

    @FXML
    private void handleReject() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Lütfen bir rezervasyon seçin.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE Reservations SET IsConfirmed = 0, Status = 'Reddedildi' WHERE ReservationID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selected.getReservationID());
            stmt.executeUpdate();

            showAlert("Reddedildi", "Rezervasyon reddedildi.");
            loadReservations();
        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi.fxml"));
            Stage stage = (Stage) reservationTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            showAlert("Hata", "Geri dönüş ekranı açılamadı: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateStatus() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Lütfen güncellenecek bir rezervasyon seçin.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getStatus());
        dialog.setTitle("Durum Güncelle");
        dialog.setHeaderText("Yeni rezervasyon durumu girin (örn: Onaylandı, Reddedildi):");

        dialog.showAndWait().ifPresent(newStatus -> {
            try (Connection conn = Database.getConnection()) {
                String sql = "UPDATE Reservations SET Status = ? WHERE ReservationID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, newStatus);
                stmt.setInt(2, selected.getReservationID());
                stmt.executeUpdate();

                showAlert("Başarılı", "Durum güncellendi.");
                loadReservations();
            } catch (SQLException e) {
                showAlert("Hata", e.getMessage());
            }
        });
    }
}

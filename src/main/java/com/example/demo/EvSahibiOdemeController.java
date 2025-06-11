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

public class EvSahibiOdemeController {

    @FXML private TableView<Reservation> paymentTable;
    @FXML private TableColumn<Reservation, String> colHouse;
    @FXML private TableColumn<Reservation, String> colTenant;
    @FXML private TableColumn<Reservation, Double> colAmount;
    @FXML private TableColumn<Reservation, String> colStatus;
    @FXML private TableColumn<Reservation, LocalDate> colPaymentDate;


    private ObservableList<Reservation> paymentList = FXCollections.observableArrayList();
    private final int OWNER_ID = LoginController.currentUserId;

    @FXML
    public void initialize() {
        colHouse.setCellValueFactory(new PropertyValueFactory<>("houseName"));
        colTenant.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        colPaymentDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        loadPayments();
    }

    private void loadPayments() {
        paymentList.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT R.ReservationID, H.HouseName, U.FullName AS TenantName, " +
                    "R.TotalAmount, R.PaymentStatus, R.PaymentDate " +
                    "FROM Reservations R " +
                    "JOIN Houses H ON R.HouseID = H.HouseID " +
                    "JOIN Users U ON R.CustomerID = U.UserID " +
                    "WHERE H.OwnerID = ? AND R.PaymentStatus = 'Ödendi'";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, OWNER_ID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation(
                        rs.getInt("ReservationID"),
                        rs.getString("HouseName"),
                        rs.getString("TenantName"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("PaymentStatus"),
                        rs.getDate("PaymentDate").toLocalDate()
                );
                paymentList.add(r);
            }
            paymentTable.setItems(paymentList);

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
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi.fxml"));
            Stage stage = (Stage) paymentTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            showAlert("Ekran Hatası", "Geri dönüş ekranı yüklenemedi: " + e.getMessage());
        }
    }
}

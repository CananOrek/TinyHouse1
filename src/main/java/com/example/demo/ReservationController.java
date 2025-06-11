package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReservationController {
    @FXML
    private TableView<Reservation> rezervasyonTablosu;
    private ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Reservation, Integer> colId;
    @FXML private TableColumn<Reservation, String> colTenantName;
    @FXML private TableColumn<Reservation, String> colHouseName;
    @FXML private TableColumn<Reservation, LocalDate> colcheckInDate;
    @FXML private TableColumn<Reservation, LocalDate> colcheckOutDate;
    @FXML private TableColumn<Reservation, String> colstatus;
    @FXML private TableColumn<Reservation, String> colPayment;
    @FXML private TableColumn<Reservation, Boolean> colConfirmed;

    @FXML private Button btnIptal;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        colTenantName.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
        colHouseName.setCellValueFactory(new PropertyValueFactory<>("houseName"));
        colcheckInDate.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colcheckOutDate.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        colstatus.setCellValueFactory(new PropertyValueFactory<>("status"));
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
                    "JOIN Houses H ON R.HouseID = H.HouseID " ;
            PreparedStatement stmt = conn.prepareStatement(sql);
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
            rezervasyonTablosu.setItems(reservationList);
        } catch (SQLException e) {
            showAlert("Veri Hatası", e.getMessage());
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
    public void onIptalEt() {
        Reservation secili = rezervasyonTablosu.getSelectionModel().getSelectedItem();
        if (secili != null) {
            secili.setPaymentStatus("İptal");
            rezervasyonTablosu.refresh();
        }
    }
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rezervasyonTablosu.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



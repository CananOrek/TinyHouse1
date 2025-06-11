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

public class EvSahibiGelirRaporuController {

    @FXML private Label toplamGelirLabel;
    @FXML private TableView<GelirRaporu> gelirTablosu;
    @FXML private TableColumn<GelirRaporu, String> colEvAdi;
    @FXML private TableColumn<GelirRaporu, Double> colTutar;
    @FXML private TableColumn<GelirRaporu, LocalDate> colTarih;

    /*
    @FXML private TableView<GelirRaporu> tabloGelir;
    @FXML private TableColumn<GelirRaporu, String> sutunEvAdi;
    @FXML private TableColumn<GelirRaporu, Double> sutunTutar;
    @FXML private TableColumn<GelirRaporu, LocalDate> sutunTarih;
    @FXML private Label toplamGelirLabel;*/

    private ObservableList<GelirRaporu> gelirListesi = FXCollections.observableArrayList();
    private final int EV_SAHIBI_ID = LoginController.currentUserId;

    @FXML
    public void initialize() {
        colEvAdi.setCellValueFactory(new PropertyValueFactory<>("evAdi"));
        colTutar.setCellValueFactory(new PropertyValueFactory<>("tutar"));
        colTarih.setCellValueFactory(new PropertyValueFactory<>("tarih"));

        gelirleriYukle();
    }

    private void gelirleriYukle() {
        gelirListesi.clear();
        double toplam = 0;

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT H.HouseName, R.TotalAmount, R.PaymentDate " +
                    "FROM Reservations R " +
                    "JOIN Houses H ON R.HouseID = H.HouseID " +
                    "WHERE H.OwnerID = ? AND R.PaymentStatus = 'Ödendi'";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, EV_SAHIBI_ID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                GelirRaporu g = new GelirRaporu(
                        rs.getString("HouseName"),
                        rs.getDouble("TotalAmount"),
                        rs.getDate("PaymentDate").toLocalDate()
                );
                gelirListesi.add(g);
                toplam += g.getTutar();
            }

            gelirTablosu.setItems(gelirListesi);
            toplamGelirLabel.setText("Toplam Gelir: " + toplam + " TL");

        } catch (SQLException e) {
            hataGoster("Veri Hatası", e.getMessage());
        }
    }

    private void hataGoster(String baslik, String mesaj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    @FXML
    private void geriDon() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi.fxml"));
            Stage stage = (Stage) gelirTablosu.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            hataGoster("Ekran Hatası", "Ana ekrana dönülemedi: " + e.getMessage());
        }
    }
}

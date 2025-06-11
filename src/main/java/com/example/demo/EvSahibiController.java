package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class EvSahibiController {

    @FXML
    private Button yorumlarButonu;
    @FXML
    private Button rezervasyonButonu;
    @FXML
    private TableColumn<House, LocalDate> colStartDate;
    @FXML
    private TableColumn<House, LocalDate> colEndDate;
    @FXML
    private TableColumn<House, Boolean> colIsActive;
    @FXML
    private TableColumn<House, Integer> colCapacity;
    @FXML
    private TableView<House> houseTable;
    @FXML
    private TableColumn<House, String> colName;
    @FXML
    private TableColumn<House, String> colLocation;
    @FXML
    private TableColumn<House, Double> colPrice;

    private final ObservableList<House> houseList = FXCollections.observableArrayList();
    private final int CURRENT_OWNER_ID = 2; // Geçici sabit ID, gerçek login'den alınmalı

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colIsActive.setCellValueFactory(new PropertyValueFactory<>("isActive"));

        loadHouses();
    }

    private void loadHouses() {
        houseList.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM Houses WHERE OwnerID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, CURRENT_OWNER_ID); // Giriş yapan ev sahibinin ID'si
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("HouseName");
                String location = rs.getString("Location");
                double price = rs.getDouble("PricePerNight");
                int capacity = rs.getInt("Capacity");

                // Tarihleri ve aktiflik durumunu al
                LocalDate start = rs.getDate("StartDate") != null ? rs.getDate("StartDate").toLocalDate() : null;
                LocalDate end = rs.getDate("EndDate") != null ? rs.getDate("EndDate").toLocalDate() : null;
                boolean isActive = rs.getBoolean("IsActive");

                int houseID = rs.getInt("HouseID");

                houseList.add(new House(houseID, name, location, price, capacity, start, end, isActive));
            }

            houseTable.setItems(houseList);

        } catch (SQLException e) {
            showAlert("Veri Yükleme Hatası", e.getMessage());
        }
    }

    @FXML
    private void handleAddHouse() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ev Ekle");
        dialog.setHeaderText("Yeni ev bilgilerini gir:");
        dialog.setContentText("Ad,Konum,Fiyat,Kapasite (örn: Evim,İstanbul,500,4):");

        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 4) {
                try {
                    String name = parts[0].trim();
                    String location = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    int capacity = Integer.parseInt(parts[3].trim());

                    try (Connection conn = Database.getConnection()) {
                        String insert = "INSERT INTO Houses (OwnerID, HouseName, Location, PricePerNight, Capacity) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(insert);
                        stmt.setInt(1, CURRENT_OWNER_ID);
                        stmt.setString(2, name);
                        stmt.setString(3, location);
                        stmt.setDouble(4, price);
                        stmt.setInt(5, capacity);
                        stmt.executeUpdate();

                        loadHouses(); // tabloyu güncelle

                    } catch (SQLException e) {
                        showAlert("Kaydetme Hatası", e.getMessage());
                    }

                } catch (NumberFormatException e) {
                    showAlert("Hatalı Giriş", "Fiyat geçerli bir sayı olmalı.");
                }
            } else {
                showAlert("Eksik Bilgi", "Lütfen 3 alanı da virgülle ayırarak girin.");
            }
        });
    }

    @FXML
    private void handleEditHouse() {
        House selected = houseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Güncellemek için bir ev seçin.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(
                selected.getName() + "," + selected.getLocation() + "," + selected.getPrice() + "," + selected.getCapacity()
        );
        dialog.setTitle("Ev Güncelle");
        dialog.setHeaderText("Yeni bilgileri gir (Ad,Konum,Fiyat,Kapasite):");

        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 4) {
                try {
                    String newName = parts[0].trim();
                    String newLocation = parts[1].trim();
                    double newPrice = Double.parseDouble(parts[2].trim());
                    int newCapacity = Integer.parseInt(parts[3].trim());

                    try (Connection conn = Database.getConnection()) {
                        String sql = "UPDATE Houses SET HouseName=?, Location=?, PricePerNight=?, Capacity=? WHERE HouseName=? AND OwnerID=?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, newName);
                        stmt.setString(2, newLocation);
                        stmt.setDouble(3, newPrice);
                        stmt.setInt(4, newCapacity);
                        stmt.setString(5, selected.getName());
                        stmt.setInt(6, CURRENT_OWNER_ID);
                        int rows = stmt.executeUpdate();

                        if (rows > 0) {
                            showAlert("Başarılı", "Ev güncellendi.");
                            loadHouses();
                        } else {
                            showAlert("Hata", "Ev güncellenemedi.");
                        }

                    } catch (SQLException e) {
                        showAlert("Veri Hatası", e.getMessage());
                    }

                } catch (Exception e) {
                    showAlert("Hatalı Giriş", "Fiyat ve kapasite sayı olmalı.");
                }
            } else {
                showAlert("Hatalı Format", "4 alan girilmeli (Ad,Konum,Fiyat,Kapasite).");
            }
        });
    }

    @FXML
    private void handleDeleteHouse() {
        House selected = houseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Silmek için bir ev seçin.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM Houses WHERE HouseName = ? AND OwnerID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, selected.getName());
            stmt.setInt(2, CURRENT_OWNER_ID);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                showAlert("Başarılı", "Ev silindi.");
                loadHouses();
            } else {
                showAlert("Hata", "Ev silinemedi.");
            }
        } catch (SQLException e) {
            showAlert("Veri Hatası", e.getMessage());
        }
    }

    @FXML
    private void goToReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reservations.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) rezervasyonButonu.getScene().getWindow(); // herhangi bir sahne elemanı yerine kullanılabilir
            stage.setScene(scene);
            stage.setTitle("Rezervasyonlar");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Ekran yüklenemedi: " + e.getMessage());
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
    private void handleUpdateAvailability() {
        House selected = houseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Güncellemek için bir ev seçin.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Durum ve Tarih Güncelle");

        DatePicker startPicker = new DatePicker();
        DatePicker endPicker = new DatePicker();
        CheckBox activeCheck = new CheckBox("Aktif");

        startPicker.setValue(selected.getStartDate());
        endPicker.setValue(selected.getEndDate());
        activeCheck.setSelected(selected.getIsActive());

        VBox box = new VBox(10, new Label("Başlangıç Tarihi:"), startPicker,
                new Label("Bitiş Tarihi:"), endPicker,
                activeCheck);
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = Database.getConnection()) {
                    String sql = "UPDATE Houses SET StartDate=?, EndDate=?, IsActive=? WHERE HouseName=? AND OwnerID=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setDate(1, Date.valueOf(startPicker.getValue()));
                    stmt.setDate(2, Date.valueOf(endPicker.getValue()));
                    stmt.setBoolean(3, activeCheck.isSelected());
                    stmt.setString(4, selected.getName());
                    stmt.setInt(5, CURRENT_OWNER_ID);
                    stmt.executeUpdate();

                    showAlert("Başarılı", "Durum ve tarih güncellendi.");
                    loadHouses();
                } catch (SQLException e) {
                    showAlert("Veri Hatası", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void goToComments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi_yorumlar.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) yorumlarButonu.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Yorumlar");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Yorumlar ekranı yüklenemedi: " + e.getMessage());
        }
    }

    @FXML
    private Button btnRezervasyon;

    @FXML
    private void handleReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi_rezervasyonlar.fxml"));
            Stage stage = (Stage) btnRezervasyon.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            showAlert("Hata", "Rezervasyonlar ekranı açılamadı: " + e.getMessage());
        }
    }

    @FXML
    private void handlePayments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi_odeme.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Ödeme Bilgileri");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            showAlert("Hata", "Ödeme bilgileri ekranı açılamadı: " + e.getMessage());
        }
    }

    @FXML
    private Button gelirRaporuButton;

    @FXML
    private void gelirRaporunaGit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi_gelir_raporu.fxml")); // <== burada dosya adı doğru olmalı
            Stage stage = (Stage) gelirRaporuButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            showAlert("Ekran Hatası", "Gelir Raporu ekranı açılamadı: " + e.getMessage());
        }
    }

    /*private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }*/

}

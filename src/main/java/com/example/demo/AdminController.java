package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AdminController {
    @FXML
    private Label labelToplamKullanici;

    @FXML
    private Label labelToplamOdeme;

    @FXML
    private PieChart pieChartRapor;
    @FXML
    private Button logoutButton;
    @FXML
    public void initialize() {
        labelToplamKullanici.setText("Toplam Kullanıcı: 120");
        labelToplamOdeme.setText("Toplam Ödeme: ₺45.200");

        pieChartRapor.getData().addAll(
                new PieChart.Data("Onaylı", 75),
                new PieChart.Data("Bekliyor", 30),
                new PieChart.Data("İptal", 15)
        );
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Çıkış");
        alert.setHeaderText(null);
        alert.setContentText("Çıkış yapılıyor...");
        alert.showAndWait();


        System.exit(0);
    }
    private void openNewWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void goToUserManagement(ActionEvent event) {
        openNewWindow("admin_user_management.fxml","Kullanıcı Yönetimi");
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
    @FXML
    private void rezervasyonlarButton(ActionEvent event) {
        openNewWindow("reservations.fxml","Rezervasyon Yönetimi");
        System.out.println("FXML yüklendi");
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void onIlanDenetimi(ActionEvent event) {
        openNewWindow("ilanYonetimi.fxml","İlan Yönetimi");
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void onOdemeGecmisi(ActionEvent event) {
        openNewWindow("OdemeGecmisi.fxml","Ödeme Geçmişi");
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
}




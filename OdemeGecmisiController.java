package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class OdemeGecmisiController {

    @FXML private TableView<Odeme> odemeTablosu;
    @FXML private TableColumn<Odeme, String> colKullanici;
    @FXML private TableColumn<Odeme, String> colEv;
    @FXML private TableColumn<Odeme, String> colTutar;
    @FXML private TableColumn<Odeme, String> colTarih;
    @FXML private Label labelToplam;

    private ObservableList<Odeme> odemeler;

    @FXML
    public void initialize() {
        colKullanici.setCellValueFactory(data -> data.getValue().kullaniciProperty());
        colEv.setCellValueFactory(data -> data.getValue().evProperty());
        colTutar.setCellValueFactory(data -> data.getValue().tutarProperty());
        colTarih.setCellValueFactory(data -> data.getValue().tarihProperty());

        odemeler = FXCollections.observableArrayList(
                new Odeme("ayse", "Doğa Evi", "1500", "2025-06-01"),
                new Odeme("mehmet", "Sahil Tiny", "1800", "2025-06-05"),
                new Odeme("zeynep", "Orman Kabin", "1200", "2025-06-08")
        );

        odemeTablosu.setItems(odemeler);

        double toplam = 0;
        for (Odeme odeme : odemeler) {
            toplam += Double.parseDouble(odeme.getTutar());
        }
        labelToplam.setText("Toplam Ödeme: ₺" + toplam);
    }
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) odemeTablosu.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.example.demo;

import com.example.demo.Ilan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class IlanYonetimiController {

    @FXML
    private TableView<Ilan> ilanTablosu;
    @FXML
    private TableColumn<Ilan, String> colEvSahibi;
    @FXML
    private TableColumn<Ilan, String> colEvAdi;
    @FXML
    private TableColumn<Ilan, String> colKonum;
    @FXML
    private TableColumn<Ilan, String> colFiyat;
    @FXML
    private TableColumn<Ilan, String> colTarih;
    @FXML
    private Button btnSil;
    @FXML
    private Button btnDuzenle;

    private ObservableList<Ilan> ilanlar;

    @FXML
    public void initialize() {
        colEvSahibi.setCellValueFactory(data -> data.getValue().evSahibiProperty());
        colEvAdi.setCellValueFactory(data -> data.getValue().evAdiProperty());
        colKonum.setCellValueFactory(data -> data.getValue().konumProperty());
        colFiyat.setCellValueFactory(data -> data.getValue().fiyatProperty());
        colTarih.setCellValueFactory(data -> data.getValue().tarihProperty());

        ilanlar = FXCollections.observableArrayList(
                new Ilan("mehmet", "Sahil Tiny", "İzmir", "1500", "2025-06-10 - 2025-06-15"),
                new Ilan("fatma", "Doğa Evi", "Bolu", "1200", "2025-07-01 - 2025-07-05"),
                new Ilan("zeynep", "Orman Kabin", "Rize", "1100", "2025-06-20 - 2025-06-25")
        );

        ilanTablosu.setItems(ilanlar);
    }

    @FXML
    public void onIlanSil() {
        Ilan secili = ilanTablosu.getSelectionModel().getSelectedItem();
        if (secili != null) {
            ilanlar.remove(secili);
        }
    }

    @FXML
    public void onIlanGuncelle() {
        Ilan secili = ilanTablosu.getSelectionModel().getSelectedItem();
        if (secili != null) {
            secili.setFiyat("Güncellendi");
            ilanTablosu.refresh();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ilanTablosu.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

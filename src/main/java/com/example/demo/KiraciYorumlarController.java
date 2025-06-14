package com.example.demo;

import com.example.demo.Comment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class KiraciYorumlarController {

    @FXML private TableView<Comment> yorumTablosu;
    @FXML private TableColumn<Comment, String> colEv;
    @FXML private TableColumn<Comment, String> colKiraci;
    @FXML private TableColumn<Comment, String> colIcerik;
    @FXML private TableColumn<Comment, LocalDate> colTarih;
    @FXML private Button btnSil, btnGuncelle;

    private ObservableList<Comment> yorumlar;

    @FXML
    public void initialize() {
        // Property'leri doğru şekilde bağla
        colKiraci.setCellValueFactory(data -> data.getValue().tenantNameProperty());
        colEv.setCellValueFactory(data -> data.getValue().houseNameProperty());
        colIcerik.setCellValueFactory(data -> data.getValue().commentTextProperty());
        colTarih.setCellValueFactory(data -> data.getValue().commentDateProperty());

        // Örnek veriler (Comment sınıfı yapısına uygun)
        yorumlar = FXCollections.observableArrayList(
                new Comment("Ahmet", "Sahil Evi", "Çok temiz ve güzel bir yerdi.", 5, LocalDate.of(2025, 7, 6), ""),
                new Comment("Mehmet", "Dağ Evi", "Manzara harikaydı ama biraz serindi.", 4, LocalDate.of(2025, 8, 10), "")
        );

        yorumTablosu.setItems(yorumlar);
    }

    @FXML
    public void onSil() {
        Comment secili = yorumTablosu.getSelectionModel().getSelectedItem();
        if (secili != null) {
            yorumlar.remove(secili);
        }
    }

    @FXML
    public void onGuncelle() {
        Comment secili = yorumTablosu.getSelectionModel().getSelectedItem();
        if (secili != null) {
            TextInputDialog dialog = new TextInputDialog(secili.getCommentText());
            dialog.setTitle("Yorum Güncelle");
            dialog.setHeaderText("Yorumu güncelleyin");
            dialog.setContentText("Yeni yorum:");

            dialog.showAndWait().ifPresent(yeniIcerik -> {
                secili.commentTextProperty().set(yeniIcerik);
                yorumTablosu.refresh();
            });
        }
    }
}
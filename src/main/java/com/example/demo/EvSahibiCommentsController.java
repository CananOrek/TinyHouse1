package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class EvSahibiCommentsController {

    @FXML private TableView<Comment> commentTable;
    @FXML private TableColumn<Comment, String> colHouse;
    @FXML private TableColumn<Comment, String> colTenant;
    @FXML private TableColumn<Comment, String> colComment;
    @FXML private TableColumn<Comment, Integer> colRating;
    @FXML private TableColumn<Comment, String> colReply;

    private ObservableList<Comment> commentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colHouse.setCellValueFactory(cell -> cell.getValue().houseNameProperty());
        colTenant.setCellValueFactory(cell -> cell.getValue().tenantNameProperty());
        colComment.setCellValueFactory(cell -> cell.getValue().commentTextProperty());
        colRating.setCellValueFactory(cell -> cell.getValue().ratingProperty().asObject());
        colReply.setCellValueFactory(data -> data.getValue().replyTextProperty());

        loadComments();
    }

    private void loadComments() {
        commentList.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT C.CommentID, C.CommentText, C.Rating, C.CommentDate, " +
                    "C.ReplyText, U.FullName AS TenantName, H.HouseName " +
                    "FROM Comments C " +
                    "JOIN Users U ON C.UserID = U.UserID " +
                    "JOIN Houses H ON C.HouseID = H.HouseID " +
                    "WHERE H.OwnerID = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, LoginController.currentUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment(
                        rs.getString("TenantName"),
                        rs.getString("HouseName"),
                        rs.getString("CommentText"),
                        rs.getInt("Rating"),
                        rs.getDate("CommentDate").toLocalDate(),
                        rs.getString("ReplyText") // ← yeni eklenen alan
                );
                commentList.add(comment);
            }

            commentTable.setItems(commentList);
        } catch (SQLException e) {
            showAlert("Yorumlar Yüklenemedi", e.getMessage());
        }
    }

    @FXML
    private void handleReply() {
        Comment selected = commentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Lütfen cevap vermek istediğiniz yorumu seçin.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Yorum Cevapla");
        dialog.setHeaderText("Kiracıya verilecek cevabı yazın:");
        dialog.setContentText("Cevap:");

        dialog.showAndWait().ifPresent(reply -> {
            try (Connection conn = Database.getConnection()) {
                String sql = "UPDATE Comments SET ReplyText = ? WHERE CommentText = ? AND UserID = " +
                        "(SELECT UserID FROM Users WHERE FullName = ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, reply);
                stmt.setString(2, selected.getCommentText());
                stmt.setString(3, selected.getTenantName());
                stmt.executeUpdate();

                showAlert("Başarılı", "Yorum cevabı eklendi.");
                loadComments();
            } catch (SQLException e) {
                showAlert("Veritabanı Hatası", e.getMessage());
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("evsahibi.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) commentTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

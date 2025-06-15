package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class AdminUserController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colID;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Boolean> colActive;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("isActive"));

        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        String query = "SELECT U.UserID, U.FullName, U.Email, U.IsActive, R.RoleName " +
                "FROM Users U JOIN Roles R ON U.RoleID = R.RoleID";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                userList.add(new User(
                        rs.getInt("UserID"),
                        rs.getString("FullName"),
                        rs.getString("Email"),
                        rs.getString("RoleName"),
                        rs.getBoolean("IsActive")
                ));
            }
            userTable.setItems(userList);
        } catch (SQLException e) {
            showAlert("Yükleme Hatası", e.getMessage());
        }
    }

    @FXML
    private void handleDeactivateUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Pasife çekmek için kullanıcı seçin.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call sp_PasifKullanici(?)}");
            stmt.setInt(1, selected.getUserID());
            stmt.execute();

            showAlert("Başarılı", "Kullanıcı pasif hale getirildi.");
            loadUsers();
        } catch (SQLException e) {
            showAlert("Hata", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Uyarı", "Silmek için kullanıcı seçin.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE UserID = ?");
            stmt.setInt(1, selected.getUserID());
            stmt.executeUpdate();

            showAlert("Başarılı", "Kullanıcı silindi.");
            loadUsers();
        } catch (SQLException e) {
            showAlert("Hata", e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleActivateUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Uyarı", "Lütfen aktif hale getirmek için bir kullanıcı seçin.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE Users SET IsActive = 1 WHERE UserID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selectedUser.getUserID());
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                showAlert("Başarılı", "Kullanıcı aktif hale getirildi.");
                loadUsers(); // tabloyu yenile
            } else {
                showAlert("Hata", "İşlem gerçekleştirilemedi.");
            }

        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", e.getMessage());
        }
    }


}

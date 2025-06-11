package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=tinyHouse;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa"; // kendi kullanıcı adını yaz
    private static final String PASSWORD = "123"; // kendi şifreni yaz

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Bağlantıyı test etmek için:
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Veritabanına başarıyla bağlanıldı!");
        } catch (SQLException e) {
            System.out.println("❌ Bağlantı hatası: " + e.getMessage());
        }
    }
}

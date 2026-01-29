package org.example.library.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Zmiana na MySQL (XAMPP)
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteka";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Puste hasło w XAMPP

    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        // Sprawdzamy czy połączenie istnieje i czy jest otwarte
        if (connection == null || connection.isClosed()) {
            // Tutaj dodajemy USER i PASSWORD, których SQLite nie wymagał
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}
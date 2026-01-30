package org.library.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PolaczenieBazyDanych {
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteka";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Puste hasło (zgodnie z Twoim plikiem)

    private static Connection connection = null;

    private PolaczenieBazyDanych() {}

    public static Connection getConnection() throws SQLException {
        // Sprawdzamy czy połączenie istnieje i czy jest otwarte
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}
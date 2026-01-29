package org.example.library.dao;

import org.example.library.model.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RezerwacjaDAO {

    public void addRezerwacja(int czytelnikId, String isbn) {
        // Updated column name to KsiążkaISBN
        String sql = "INSERT INTO Rezerwacja(Data_rezerwacji, CzytelnikID_Czytelnika, KsiążkaISBN) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Changed setLong to setDate for MySQL compatibility
            pstmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setInt(2, czytelnikId);
            pstmt.setString(3, isbn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasActiveReservation(int czytelnikId, String isbn) {
        // Updated column name to KsiążkaISBN
        String sql = "SELECT 1 FROM Rezerwacja WHERE CzytelnikID_Czytelnika = ? AND KsiążkaISBN = ? AND Status = 'Aktywna'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, czytelnikId);
            pstmt.setString(2, isbn);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<org.example.library.model.Rezerwacja> getAllReservations() {
        List<org.example.library.model.Rezerwacja> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Rezerwacja"; 

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new org.example.library.model.Rezerwacja(
                        rs.getInt("ID_Rezerwacji"),
                        rs.getDate("Data_rezerwacji"),
                        rs.getInt("CzytelnikID_Czytelnika"),
                        rs.getString("KsiążkaISBN"),
                        rs.getString("Status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateReservationStatus(int id, String status) {
        String sql = "UPDATE Rezerwacja SET Status = ? WHERE ID_Rezerwacji = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
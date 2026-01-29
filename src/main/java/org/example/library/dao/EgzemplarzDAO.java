package org.example.library.dao;

import org.example.library.model.DatabaseConnection;
import org.example.library.model.Egzemplarz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EgzemplarzDAO {

    public List<Egzemplarz> getAllEgzemplarze() {
        List<Egzemplarz> egzemplarze = new ArrayList<>();
        String sql = "SELECT * FROM Egzemplarz";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                egzemplarze.add(mapRowToEgzemplarz(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return egzemplarze;
    }

    // NOWA METODA: Wyszukiwanie po kodzie kreskowym (skaner)
    public Optional<Egzemplarz> getEgzemplarzByKod(String kod) {
        String sql = "SELECT * FROM Egzemplarz WHERE Kod_kreskowy = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kod);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToEgzemplarz(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Egzemplarz mapRowToEgzemplarz(ResultSet rs) throws SQLException {
        return new Egzemplarz(
                rs.getInt("ID_Egzemplarza"),
                rs.getString("Kod_kreskowy"),
                rs.getInt("Lokalizacja_Regal"),
                rs.getInt("Lokalizacja_Polka"),
                rs.getString("Status_wypozyczenia"),
                rs.getString("KsiazkaISBN") // Poprawiono nazwę kolumny (bez polskich znaków)
        );
    }

    public void updateStatus(int egzemplarzId, String nowyStatus) {
        String sql = "UPDATE Egzemplarz SET Status_wypozyczenia = ? WHERE ID_Egzemplarza = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nowyStatus);
            pstmt.setInt(2, egzemplarzId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
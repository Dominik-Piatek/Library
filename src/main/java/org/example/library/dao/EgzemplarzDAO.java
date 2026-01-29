package org.example.library.dao;

import org.example.library.model.DatabaseConnection;
import org.example.library.model.Egzemplarz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EgzemplarzDAO {

    // Metoda do pobierania wszystkich egzemplarzy
    public List<Egzemplarz> getAllEgzemplarze() {
        List<Egzemplarz> egzemplarze = new ArrayList<>();
        String sql = "SELECT * FROM Egzemplarz";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Egzemplarz e = mapRowToEgzemplarz(rs);
                egzemplarze.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return egzemplarze;
    }

    // Metoda do znajdowania egzemplarza po ID
    public Egzemplarz getEgzemplarzById(int id) {
        String sql = "SELECT * FROM Egzemplarz WHERE ID_Egzemplarza = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToEgzemplarz(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Metoda pomocnicza do mapowania wyników z bazy na obiekt Javy
    private Egzemplarz mapRowToEgzemplarz(ResultSet rs) throws SQLException {
        // Upewnij się, że konstruktor w klasie Egzemplarz (model) pasuje do tych danych!
        // Jeśli w modelu masz inne typy danych, musisz to dostosować.
        // Zakładam standardowy konstruktor na podstawie Twojej bazy SQL.
        return new Egzemplarz(
                rs.getInt("ID_Egzemplarza"),
                rs.getString("Kod_kreskowy"),
                rs.getInt("Lokalizacja_Regal"),
                rs.getInt("Lokalizacja_Polka"),
                rs.getString("Status_wypozyczenia"),
                rs.getString("KsiążkaISBN")
        );
    }

    // Metoda do zmiany statusu (np. na 'Wypożyczona')
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
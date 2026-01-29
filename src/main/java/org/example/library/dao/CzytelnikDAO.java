package org.example.library.dao;

import org.example.library.model.Czytelnik;
import org.example.library.model.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CzytelnikDAO {

    public void addCzytelnik(Czytelnik czytelnik) {
        String sql = "INSERT INTO Czytelnik(Imię, Nazwisko, Nr_telefonu, `E-mail`, Haslo_skrot) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, czytelnik.getImie());
            pstmt.setString(2, czytelnik.getNazwisko());
            pstmt.setString(3, czytelnik.getNrTelefonu());
            pstmt.setString(4, czytelnik.getEmail());
            pstmt.setString(5, czytelnik.getHasloSkrot());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Czytelnik> getAllCzytelnicy() {
        List<Czytelnik> list = new ArrayList<>();
        String sql = "SELECT * FROM Czytelnik";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Czytelnik(
                        rs.getInt("ID_Czytelnika"),
                        rs.getString("Imię"),
                        rs.getString("Nazwisko"),
                        rs.getString("Nr_telefonu"),
                        rs.getString("E-mail"),
                        rs.getString("Haslo_skrot")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public java.util.Optional<Czytelnik> authenticate(String email, String password) {
        String sql = "SELECT * FROM Czytelnik WHERE `E-mail` = ? AND Haslo_skrot = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return java.util.Optional.of(new Czytelnik(
                        rs.getInt("ID_Czytelnika"),
                        rs.getString("Imię"),
                        rs.getString("Nazwisko"),
                        rs.getString("Nr_telefonu"),
                        rs.getString("E-mail"),
                        rs.getString("Haslo_skrot")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return java.util.Optional.empty();
    }

    public void updateCzytelnik(Czytelnik czytelnik) {
        String sql = "UPDATE Czytelnik SET Imię=?, Nazwisko=?, Nr_telefonu=?, `E-mail`=?, Haslo_skrot=? WHERE ID_Czytelnika=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, czytelnik.getImie());
            pstmt.setString(2, czytelnik.getNazwisko());
            pstmt.setString(3, czytelnik.getNrTelefonu());
            pstmt.setString(4, czytelnik.getEmail());
            pstmt.setString(5, czytelnik.getHasloSkrot());
            pstmt.setInt(6, czytelnik.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
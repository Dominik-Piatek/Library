package org.library.dao;

import org.library.model.Czytelnik;
import org.library.model.PolaczenieBazyDanych;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CzytelnikDAO {

    public void addCzytelnik(Czytelnik czytelnik) {

        String sql = "INSERT INTO Czytelnik(Imie, Nazwisko, Nr_telefonu, Email, Haslo_skrot) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
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
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Czytelnik(
                        rs.getInt("ID_Czytelnika"),
                        rs.getString("Imie"),      // ZMIANA: Imie
                        rs.getString("Nazwisko"),
                        rs.getString("Nr_telefonu"),
                        rs.getString("Email"),     // ZMIANA: Email
                        rs.getString("Haslo_skrot")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public java.util.Optional<Czytelnik> authenticate(String email, String password) {

        String sql = "SELECT * FROM Czytelnik WHERE Email = ? AND Haslo_skrot = ?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return java.util.Optional.of(new Czytelnik(
                        rs.getInt("ID_Czytelnika"),
                        rs.getString("Imie"),      // ZMIANA: Imie
                        rs.getString("Nazwisko"),
                        rs.getString("Nr_telefonu"),
                        rs.getString("Email"),     // ZMIANA: Email
                        rs.getString("Haslo_skrot")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return java.util.Optional.empty();
    }

    public void updateCzytelnik(Czytelnik czytelnik) {

        String sql = "UPDATE Czytelnik SET Imie=?, Nazwisko=?, Nr_telefonu=?, Email=?, Haslo_skrot=? WHERE ID_Czytelnika=?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
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
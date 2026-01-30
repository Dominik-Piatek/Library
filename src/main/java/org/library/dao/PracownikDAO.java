package org.library.dao;

import org.library.model.PolaczenieBazyDanych;
import org.library.model.Pracownik;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PracownikDAO {

    public Optional<Pracownik> authenticate(String login, String passwordHash) {
        String sql = "SELECT * FROM Pracownik WHERE Login = ? AND Haslo_skrot = ?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, passwordHash);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToPracownik(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void addPracownik(Pracownik pracownik) {
        String sql = "INSERT INTO Pracownik(Imie, Nazwisko, Login, Haslo_skrot, Rola) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pracownik.getImie());
            pstmt.setString(2, pracownik.getNazwisko());
            pstmt.setString(3, pracownik.getLogin());
            pstmt.setString(4, pracownik.getHasloSkrot());
            pstmt.setString(5, pracownik.getRola());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Pracownik> getAllPracownicy() {
        List<Pracownik> list = new ArrayList<>();
        String sql = "SELECT * FROM Pracownik";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToPracownik(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Pracownik mapResultSetToPracownik(ResultSet rs) throws SQLException {
        return new Pracownik(
                rs.getInt("ID_Pracownika"),
                rs.getString("Imie"),
                rs.getString("Nazwisko"),
                rs.getString("Login"),
                rs.getString("Haslo_skrot"),
                rs.getString("Rola")
        );
    }

    public void updatePracownik(Pracownik pracownik) {
        String sql = "UPDATE Pracownik SET Imie=?, Nazwisko=?, Login=?, Haslo_skrot=?, Rola=? WHERE ID_Pracownika=?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pracownik.getImie());
            pstmt.setString(2, pracownik.getNazwisko());
            pstmt.setString(3, pracownik.getLogin());
            pstmt.setString(4, pracownik.getHasloSkrot());
            pstmt.setString(5, pracownik.getRola());
            pstmt.setInt(6, pracownik.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
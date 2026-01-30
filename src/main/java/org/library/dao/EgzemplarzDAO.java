package org.library.dao;

import org.library.model.PolaczenieBazyDanych;
import org.library.model.Egzemplarz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EgzemplarzDAO {

    public void addEgzemplarz(Egzemplarz egzemplarz) {
        String sql = "INSERT INTO Egzemplarz(Kod_kreskowy, Lokalizacja_Regal, Lokalizacja_Polka, Status_wypozyczenia, KsiazkaISBN) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, egzemplarz.getKodKreskowy());
            pstmt.setInt(2, egzemplarz.getLokalizacjaRegal());
            pstmt.setInt(3, egzemplarz.getLokalizacjaPolka());
            pstmt.setString(4, egzemplarz.getStatusWypozyczenia());
            pstmt.setString(5, egzemplarz.getKsiazkaIsbn());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Egzemplarz> getAllEgzemplarze() {
        List<Egzemplarz> list = new ArrayList<>();
        String sql = "SELECT * FROM Egzemplarz";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToEgzemplarz(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Egzemplarz> getEgzemplarzeByIsbn(String isbn) {
        List<Egzemplarz> list = new ArrayList<>();
        String sql = "SELECT * FROM Egzemplarz WHERE KsiazkaISBN = ?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToEgzemplarz(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Optional<Egzemplarz> getEgzemplarzByKod(String kod) {
        String sql = "SELECT * FROM Egzemplarz WHERE Kod_kreskowy = ?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
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

    // Ujednolicona nazwa metody (updateStatus, nie updateStatusEgzemplarza)
    public void updateStatus(int id, String status) {
        String sql = "UPDATE Egzemplarz SET Status_wypozyczenia = ? WHERE ID_Egzemplarza = ?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEgzemplarz(Egzemplarz egzemplarz) {
        String sql = "UPDATE Egzemplarz SET Lokalizacja_Regal = ?, Lokalizacja_Polka = ?, Status_wypozyczenia = ? WHERE ID_Egzemplarza = ?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, egzemplarz.getLokalizacjaRegal());
            pstmt.setInt(2, egzemplarz.getLokalizacjaPolka());
            pstmt.setString(3, egzemplarz.getStatusWypozyczenia());
            pstmt.setInt(4, egzemplarz.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEgzemplarz(int id) {
        String sql = "DELETE FROM Egzemplarz WHERE ID_Egzemplarza = ?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Egzemplarz mapRowToEgzemplarz(ResultSet rs) throws SQLException {
        return new Egzemplarz(
                rs.getInt("ID_Egzemplarza"),
                rs.getString("Kod_kreskowy"),
                rs.getInt("Lokalizacja_Regal"),
                rs.getInt("Lokalizacja_Polka"),
                rs.getString("Status_wypozyczenia"),
                rs.getString("KsiazkaISBN"));
    }
}
package org.library.dao;

import org.library.model.PolaczenieBazyDanych;
import org.library.model.Wypozyczenie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WypozyczenieDAO {

    public void addWypozyczenie(Wypozyczenie wyp) {
        String sql = "INSERT INTO Wypozyczenie(Data_wypozyczenia, Planowany_termin_zwrotu, CzytelnikID_Czytelnika, EgzemplarzID_Egzemplarza, PracownikID_Pracownika) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, new java.sql.Date(wyp.getDataWypozyczenia().getTime()));
            pstmt.setDate(2, new java.sql.Date(wyp.getPlanowanyTerminZwrotu().getTime()));

            pstmt.setInt(3, wyp.getCzytelnikId());
            pstmt.setInt(4, wyp.getEgzemplarzId());
            pstmt.setInt(5, wyp.getPracownikId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void zwrocKsiazke(int idWypozyczenia, java.sql.Date dataZwrotu, double kara) {
        String sql = "UPDATE Wypozyczenie SET Faktyczna_data_zwrotu = ?, Kara = ? WHERE ID_Wypozyczenia = ?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, dataZwrotu);
            pstmt.setDouble(2, kara);
            pstmt.setInt(3, idWypozyczenia);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Wypozyczenie> getAllWypozyczenia() {
        List<Wypozyczenie> list = new ArrayList<>();
        String sql = "SELECT * FROM Wypozyczenie";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToWypozyczenie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Wypozyczenie> getAktywneWypozyczeniaCzytelnika(int czytelnikId) {
        List<Wypozyczenie> list = new ArrayList<>();
        String sql = "SELECT * FROM Wypozyczenie WHERE CzytelnikID_Czytelnika = ? AND Faktyczna_data_zwrotu IS NULL";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, czytelnikId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToWypozyczenie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Wypozyczenie> getWypozyczeniaHistory(int czytelnikId) {
        List<Wypozyczenie> list = new ArrayList<>();
        String sql = "SELECT * FROM Wypozyczenie WHERE CzytelnikID_Czytelnika = ? AND Faktyczna_data_zwrotu IS NOT NULL";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, czytelnikId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToWypozyczenie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Optional<Wypozyczenie> findActiveLoanByCopyId(int copyId) {
        String sql = "SELECT * FROM Wypozyczenie WHERE EgzemplarzID_Egzemplarza = ? AND Faktyczna_data_zwrotu IS NULL";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, copyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToWypozyczenie(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Wypozyczenie mapResultSetToWypozyczenie(ResultSet rs) throws SQLException {
        return new Wypozyczenie(
                rs.getInt("ID_Wypozyczenia"),
                rs.getDate("Data_wypozyczenia"),
                rs.getDate("Planowany_termin_zwrotu"),
                rs.getDate("Faktyczna_data_zwrotu"),
                rs.getDouble("Kara"),
                rs.getInt("CzytelnikID_Czytelnika"),
                rs.getInt("EgzemplarzID_Egzemplarza"),
                rs.getInt("PracownikID_Pracownika")
        );
    }
}
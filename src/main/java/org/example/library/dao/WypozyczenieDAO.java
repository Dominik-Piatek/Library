package org.example.library.dao;

import org.example.library.model.DatabaseConnection;
import org.example.library.model.Wypozyczenie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WypozyczenieDAO {

    public void addWypozyczenie(Wypozyczenie wyp) {
        String sql = "INSERT INTO Wypozyczenie(Data_wypozyczenia, Planowany_termin_zwrotu, CzytelnikID_Czytelnika, EgzemplarzID_Egzemplarza, PracownikID_Pracownika) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
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

    public void zwrocKsiazke(int idPypozyczenia, Date dataZwrotu, double kara) {
        String sql = "UPDATE Wypozyczenie SET Faktyczna_data_zwrotu = ?, Kara = ? WHERE ID_Wypozyczenia = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(dataZwrotu.getTime()));
            pstmt.setDouble(2, kara);
            pstmt.setInt(3, idPypozyczenia);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Wypozyczenie> getAktywneWypozyczeniaCzytelnika(int czytelnikId) {
        List<Wypozyczenie> list = new ArrayList<>();
        String sql = "SELECT * FROM Wypozyczenie WHERE CzytelnikID_Czytelnika = ? AND Faktyczna_data_zwrotu IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
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

    private Wypozyczenie mapResultSetToWypozyczenie(ResultSet rs) throws SQLException {
        return new Wypozyczenie(
                rs.getInt("ID_Wypozyczenia"),
                rs.getDate("Data_wypozyczenia"),
                rs.getDate("Planowany_termin_zwrotu"),
                rs.getDate("Faktyczna_data_zwrotu"),
                rs.getDouble("Kara"),
                rs.getInt("CzytelnikID_Czytelnika"),
                rs.getInt("EgzemplarzID_Egzemplarza"),
                rs.getInt("PracownikID_Pracownika"));
    }

    public List<Wypozyczenie> getWypozyczeniaHistory(int czytelnikId) {
        List<Wypozyczenie> list = new ArrayList<>();
        String sql = "SELECT * FROM Wypozyczenie WHERE CzytelnikID_Czytelnika = ? AND Faktyczna_data_zwrotu IS NOT NULL";
        try (Connection conn = DatabaseConnection.getConnection();
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
}

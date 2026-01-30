package org.library.dao;

import org.library.model.PolaczenieBazyDanych;
import org.library.model.Egzemplarz;
import org.library.model.Ksiazka;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KsiazkaDAO {

    public void addKsiazka(Ksiazka ksiazka) {
        String sql = "INSERT INTO Ksiazka(ISBN, Tytul, Autor, Gatunek, Rok_wydania, Dziedzina) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ksiazka.getIsbn());
            pstmt.setString(2, ksiazka.getTytul());
            pstmt.setString(3, ksiazka.getAutor());
            pstmt.setString(4, ksiazka.getGatunek());
            pstmt.setInt(5, ksiazka.getRokWydania());
            pstmt.setString(6, ksiazka.getDziedzina());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addEgzemplarz(Egzemplarz egzemplarz) {
        String sql = "INSERT INTO Egzemplarz(Kod_kreskowy, Lokalizacja_Regal, Lokalizacja_Polka, Status_wypozyczenia, KsiazkaISBN) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, egzemplarz.getKodKreskowy());
            pstmt.setInt(2, egzemplarz.getLokalizacjaRegal());
            pstmt.setInt(3, egzemplarz.getLokalizacjaPolka());
            pstmt.setString(4, egzemplarz.getStatusWypozyczenia());
            pstmt.setString(5, egzemplarz.getKsiazkaIsbn());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Ksiazka> getAllKsiazki() {
        List<Ksiazka> list = new ArrayList<>();
        String sql = "SELECT * FROM Ksiazka";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Ksiazka(
                        rs.getString("ISBN"),
                        rs.getString("Tytul"),
                        rs.getString("Autor"),
                        rs.getString("Gatunek"),
                        rs.getInt("Rok_wydania"),
                        rs.getString("Dziedzina")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Ksiazka> getOnlyAvailableBooks() {
        List<Ksiazka> list = new ArrayList<>();
        String sql = "SELECT DISTINCT k.* FROM Ksiazka k JOIN Egzemplarz e ON k.ISBN = e.KsiazkaISBN WHERE e.Status_wypozyczenia = 'Dostępna'";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Ksiazka(
                        rs.getString("ISBN"),
                        rs.getString("Tytul"),
                        rs.getString("Autor"),
                        rs.getString("Gatunek"),
                        rs.getInt("Rok_wydania"),
                        rs.getString("Dziedzina")
                        // USUNIĘTO odczyt PracownikID
                ));
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
                list.add(new Egzemplarz(
                        rs.getInt("ID_Egzemplarza"),
                        rs.getString("Kod_kreskowy"),
                        rs.getInt("Lokalizacja_Regal"),
                        rs.getInt("Lokalizacja_Polka"),
                        rs.getString("Status_wypozyczenia"),
                        rs.getString("KsiazkaISBN")));
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
                return Optional.of(new Egzemplarz(
                        rs.getInt("ID_Egzemplarza"),
                        rs.getString("Kod_kreskowy"),
                        rs.getInt("Lokalizacja_Regal"),
                        rs.getInt("Lokalizacja_Polka"),
                        rs.getString("Status_wypozyczenia"),
                        rs.getString("KsiazkaISBN")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void updateStatusEgzemplarza(int id, String status) {
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

    public void updateKsiazka(Ksiazka ksiazka) {
        String sql = "UPDATE Ksiazka SET Tytul=?, Autor=?, Gatunek=?, Rok_wydania=?, Dziedzina=? WHERE ISBN=?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ksiazka.getTytul());
            pstmt.setString(2, ksiazka.getAutor());
            pstmt.setString(3, ksiazka.getGatunek());
            pstmt.setInt(4, ksiazka.getRokWydania());
            pstmt.setString(5, ksiazka.getDziedzina());
            pstmt.setString(6, ksiazka.getIsbn());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteKsiazka(String isbn) {
        String sql = "DELETE FROM Ksiazka WHERE ISBN=?";
        try (Connection conn = PolaczenieBazyDanych.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
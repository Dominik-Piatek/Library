package org.library.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InicjalizatorBazyDanych {
        public static void initialize() {
                // Łączymy się z samym serwerem
                String urlServer = "jdbc:mysql://localhost:3306/";
                String user = "root";
                String password = "";

                try (Connection conn = DriverManager.getConnection(urlServer, user, password);
                     Statement stmt = conn.createStatement()) {

                        // Tworzymy bazę, jeśli nie istnieje
                        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS biblioteka");

                } catch (SQLException e) {
                        e.printStackTrace();
                        System.err.println("Bład podczas tworzenia bazy danych!");
                        return;
                }

                // inicjalizacja tabel
                try (Connection conn = PolaczenieBazyDanych.getConnection();
                     Statement stmt = conn.createStatement()) {

                        // Upewniamy się, że używamy dobrej bazy
                        stmt.executeUpdate("USE biblioteka");

                        // Tabela Czytelnik
                        String sqlCzytelnik = "CREATE TABLE IF NOT EXISTS Czytelnik (" +
                                "ID_Czytelnika int(10) NOT NULL AUTO_INCREMENT, " +
                                "Imie varchar(50) NOT NULL, " +
                                "Nazwisko varchar(50) NOT NULL, " +
                                "Nr_telefonu varchar(15) NOT NULL UNIQUE, " +
                                "Email varchar(100) NOT NULL UNIQUE, " +
                                "Haslo_skrot varchar(255) NOT NULL, " +
                                "PRIMARY KEY (ID_Czytelnika))";
                        stmt.executeUpdate(sqlCzytelnik);

                        // Tabela Ksiazka
                        String sqlKsiazka = "CREATE TABLE IF NOT EXISTS Ksiazka (" +
                                "ISBN varchar(13) NOT NULL, " +
                                "Tytul varchar(200) NOT NULL, " +
                                "Autor varchar(100) NOT NULL, " +
                                "Gatunek varchar(50) NOT NULL, " +
                                "Rok_wydania int(4) NOT NULL, " +
                                "Dziedzina varchar(50) NOT NULL, " +
                                "PRIMARY KEY (ISBN))";
                        stmt.executeUpdate(sqlKsiazka);

                        // Tabela Pracownik
                        String sqlPracownik = "CREATE TABLE IF NOT EXISTS Pracownik (" +
                                "ID_Pracownika int(10) NOT NULL AUTO_INCREMENT, " +
                                "Imie varchar(50) NOT NULL, " +
                                "Nazwisko varchar(50) NOT NULL, " +
                                "Login varchar(30) NOT NULL UNIQUE, " +
                                "Haslo_skrot varchar(255) NOT NULL, " +
                                "Rola varchar(20) NOT NULL, " +
                                "PRIMARY KEY (ID_Pracownika))";
                        stmt.executeUpdate(sqlPracownik);

                        // Tabela Egzemplarz
                        String sqlEgzemplarz = "CREATE TABLE IF NOT EXISTS Egzemplarz (" +
                                "ID_Egzemplarza int(10) NOT NULL AUTO_INCREMENT, " +
                                "Kod_kreskowy varchar(50) NOT NULL UNIQUE, " +
                                "Lokalizacja_Regal int(5) NOT NULL, " +
                                "Lokalizacja_Polka int(5) NOT NULL, " +
                                "Status_wypozyczenia varchar(30) NOT NULL, " +
                                "KsiazkaISBN varchar(13) NOT NULL, " +
                                "PRIMARY KEY (ID_Egzemplarza))";
                        stmt.executeUpdate(sqlEgzemplarz);

                        // Tabela Wypozyczenie
                        String sqlWypozyczenie = "CREATE TABLE IF NOT EXISTS Wypozyczenie (" +
                                "ID_Wypozyczenia int(10) NOT NULL AUTO_INCREMENT, " +
                                "Data_wypozyczenia date NOT NULL, " +
                                "Planowany_termin_zwrotu date NOT NULL, " +
                                "Faktyczna_data_zwrotu date, " +
                                "Kara decimal(20, 2) DEFAULT 0.00 NOT NULL, " +
                                "CzytelnikID_Czytelnika int(10) NOT NULL, " +
                                "EgzemplarzID_Egzemplarza int(10) NOT NULL, " +
                                "PracownikID_Pracownika int(10) NOT NULL, " +
                                "PRIMARY KEY (ID_Wypozyczenia))";
                        stmt.executeUpdate(sqlWypozyczenie);

                        // Relacje (Klucze obce)
                        try {
                                stmt.executeUpdate("ALTER TABLE Egzemplarz ADD CONSTRAINT FKEgzemplarz_Ksiazka FOREIGN KEY (KsiazkaISBN) REFERENCES Ksiazka (ISBN) ON DELETE CASCADE ON UPDATE CASCADE");
                        } catch (SQLException e) { }

                        try {
                                stmt.executeUpdate("ALTER TABLE Wypozyczenie ADD CONSTRAINT FKWypozyczenie_Czytelnik FOREIGN KEY (CzytelnikID_Czytelnika) REFERENCES Czytelnik (ID_Czytelnika)");
                        } catch (SQLException e) { }

                        try {
                                stmt.executeUpdate("ALTER TABLE Wypozyczenie ADD CONSTRAINT FKWypozyczenie_Egzemplarz FOREIGN KEY (EgzemplarzID_Egzemplarza) REFERENCES Egzemplarz (ID_Egzemplarza)");
                        } catch (SQLException e) { }

                        try {
                                stmt.executeUpdate("ALTER TABLE Wypozyczenie ADD CONSTRAINT FKWypozyczenie_Pracownik FOREIGN KEY (PracownikID_Pracownika) REFERENCES Pracownik (ID_Pracownika)");
                        } catch (SQLException e) { }

                        // Dodanie domyślnego Administratora
                        try {
                                stmt.executeUpdate("INSERT INTO Pracownik (Imie, Nazwisko, Login, Haslo_skrot, Rola) VALUES ('Jan', 'Kowalski', 'admin', 'admin123', 'Administrator')");
                        } catch (SQLException e) { }

                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }
}
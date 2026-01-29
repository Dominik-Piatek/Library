package org.example.library.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
        public static void initialize() {
                try (Connection conn = DatabaseConnection.getConnection();
                                Statement stmt = conn.createStatement()) {

                        // Pracownik (Employees)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Pracownik (" +
                                        "ID_Pracownika INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "Imie VARCHAR(255) NOT NULL, " +
                                        "Nazwisko VARCHAR(255) NOT NULL, " +
                                        "Login VARCHAR(255) NOT NULL UNIQUE, " +
                                        "Haslo_skrot VARCHAR(255) NOT NULL, " +
                                        "Rola VARCHAR(50) NOT NULL, " +
                                        "ID_Administratora INTEGER, " +
                                        "FOREIGN KEY(ID_Administratora) REFERENCES Pracownik(ID_Pracownika))");

                        // Czytelnik (Readers)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Czytelnik (" +
                                        "ID_Czytelnika INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "`Imię` VARCHAR(255) NOT NULL, " +
                                        "Nazwisko VARCHAR(255) NOT NULL, " +
                                        "Nr_telefonu VARCHAR(20) NOT NULL UNIQUE, " +
                                        "`E-mail` VARCHAR(255) NOT NULL UNIQUE, " +
                                        "Haslo_skrot VARCHAR(255) NOT NULL)");

                        // Ksiazka (Books)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Ksiazka (" +
                                        "ISBN VARCHAR(20) PRIMARY KEY, " +
                                        "Tytul VARCHAR(255) NOT NULL, " +
                                        "Autor VARCHAR(255) NOT NULL, " +
                                        "Gatunek VARCHAR(100) NOT NULL, " +
                                        "Rok_wydania INTEGER NOT NULL, " +
                                        "Dziedzina VARCHAR(100) NOT NULL, " +
                                        "PracownikID_Pracownika INTEGER NOT NULL, " +
                                        "FOREIGN KEY(PracownikID_Pracownika) REFERENCES Pracownik(ID_Pracownika))");

                        // Egzemplarz (Copies)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Egzemplarz (" +
                                        "ID_Egzemplarza INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "Kod_kreskowy VARCHAR(50) NOT NULL UNIQUE, " +
                                        "Lokalizacja_Regal INTEGER NOT NULL, " +
                                        "Lokalizacja_Polka INTEGER NOT NULL, " +
                                        "Status_wypozyczenia VARCHAR(50) NOT NULL, " +
                                        "KsiazkaISBN VARCHAR(20) NOT NULL, " +
                                        "FOREIGN KEY(KsiazkaISBN) REFERENCES Ksiazka(ISBN))");

                        // Attempt to migrate column name if it exists in Polish
                        try {
                            stmt.execute("ALTER TABLE Egzemplarz CHANGE `KsiążkaISBN` KsiazkaISBN VARCHAR(20) NOT NULL");
                            System.out.println("Migrated Egzemplarz column KsiążkaISBN to KsiazkaISBN");
                        } catch (SQLException e) {
                            // Ignore if column doesn't exist or already renamed
                        }

                        // Wypozyczenie (Loans)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Wypozyczenie (" +
                                        "ID_Wypozyczenia INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "Data_wypozyczenia DATE NOT NULL, " +
                                        "Planowany_termin_zwrotu DATE NOT NULL, " +
                                        "Faktyczna_data_zwrotu DATE, " +
                                        "Kara DECIMAL(10,2) DEFAULT 0.00, " +
                                        "CzytelnikID_Czytelnika INTEGER NOT NULL, " +
                                        "EgzemplarzID_Egzemplarza INTEGER NOT NULL, " +
                                        "PracownikID_Pracownika INTEGER NOT NULL, " +
                                        "FOREIGN KEY(CzytelnikID_Czytelnika) REFERENCES Czytelnik(ID_Czytelnika), " +
                                        "FOREIGN KEY(EgzemplarzID_Egzemplarza) REFERENCES Egzemplarz(ID_Egzemplarza), "
                                        +
                                        "FOREIGN KEY(PracownikID_Pracownika) REFERENCES Pracownik(ID_Pracownika))");

                        // Rezerwacja (Reservations)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Rezerwacja (" +
                                        "ID_Rezerwacji INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "Data_rezerwacji DATE NOT NULL, " +
                                        "CzytelnikID_Czytelnika INTEGER NOT NULL, " +
                                        "KsiazkaISBN VARCHAR(20) NOT NULL, " +
                                        "Status VARCHAR(50) DEFAULT 'Aktywna', " +
                                        "FOREIGN KEY(CzytelnikID_Czytelnika) REFERENCES Czytelnik(ID_Czytelnika), " +
                                        "FOREIGN KEY(KsiazkaISBN) REFERENCES Ksiazka(ISBN))");

                        // Attempt to migrate column name in Rezerwacja too
                        try {
                            stmt.execute("ALTER TABLE Rezerwacja CHANGE `KsiążkaISBN` KsiazkaISBN VARCHAR(20) NOT NULL");
                            System.out.println("Migrated Rezerwacja column KsiążkaISBN to KsiazkaISBN");
                        } catch (SQLException e) {
                            // Ignore
                        }

                        // Default Admin
                        try (java.sql.ResultSet rs = stmt.executeQuery("SELECT count(*) FROM Pracownik")) {
                                if (rs.next() && rs.getInt(1) == 0) {
                                        stmt.execute("INSERT INTO Pracownik (Imie, Nazwisko, Login, Haslo_skrot, Rola) VALUES ('Admin', 'System', 'admin', 'admin', 'Administrator')");
                                        System.out.println("Default Admin created: admin/admin");
                                }
                        }

                        System.out.println("Database initialized successfully.");

                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }
}
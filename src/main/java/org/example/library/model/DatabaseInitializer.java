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
                                        "ID_Pracownika INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "Imie TEXT NOT NULL, " +
                                        "Nazwisko TEXT NOT NULL, " +
                                        "Login TEXT NOT NULL UNIQUE, " +
                                        "Haslo_skrot TEXT NOT NULL, " +
                                        "Rola TEXT NOT NULL, " +
                                        "ID_Administratora INTEGER, " +
                                        "FOREIGN KEY(ID_Administratora) REFERENCES Pracownik(ID_Pracownika))");

                        // Czytelnik (Readers)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Czytelnik (" +
                                        "ID_Czytelnika INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "Imie TEXT NOT NULL, " +
                                        "Nazwisko TEXT NOT NULL, " +
                                        "Nr_telefonu TEXT NOT NULL UNIQUE, " +
                                        "Email TEXT NOT NULL UNIQUE, " +
                                        "Haslo_skrot TEXT NOT NULL, " +
                                        "PracownikID_Pracownika INTEGER NOT NULL, " +
                                        "FOREIGN KEY(PracownikID_Pracownika) REFERENCES Pracownik(ID_Pracownika))");

                        // Ksiazka (Books)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Ksiazka (" +
                                        "ISBN TEXT PRIMARY KEY, " +
                                        "Tytul TEXT NOT NULL, " +
                                        "Autor TEXT NOT NULL, " +
                                        "Gatunek TEXT NOT NULL, " +
                                        "Rok_wydania INTEGER NOT NULL, " +
                                        "Dziedzina TEXT NOT NULL, " +
                                        "PracownikID_Pracownika INTEGER NOT NULL, " +
                                        "FOREIGN KEY(PracownikID_Pracownika) REFERENCES Pracownik(ID_Pracownika))");

                        // Egzemplarz (Copies)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Egzemplarz (" +
                                        "ID_Egzemplarza INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "Kod_kreskowy TEXT NOT NULL UNIQUE, " +
                                        "Lokalizacja_Regal INTEGER NOT NULL, " +
                                        "Lokalizacja_Polka INTEGER NOT NULL, " +
                                        "Status_wypozyczenia TEXT NOT NULL, " +
                                        "KsiazkaISBN TEXT NOT NULL, " +
                                        "FOREIGN KEY(KsiazkaISBN) REFERENCES Ksiazka(ISBN))");

                        // Wypozyczenie (Loans)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Wypozyczenie (" +
                                        "ID_Wypozyczenia INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "Data_wypozyczenia INTEGER NOT NULL, " +
                                        "Planowany_termin_zwrotu INTEGER NOT NULL, " +
                                        "Faktyczna_data_zwrotu INTEGER, " +
                                        "Kara REAL DEFAULT 0.00, " +
                                        "CzytelnikID_Czytelnika INTEGER NOT NULL, " +
                                        "EgzemplarzID_Egzemplarza INTEGER NOT NULL, " +
                                        "PracownikID_Pracownika INTEGER NOT NULL, " +
                                        "FOREIGN KEY(CzytelnikID_Czytelnika) REFERENCES Czytelnik(ID_Czytelnika), " +
                                        "FOREIGN KEY(EgzemplarzID_Egzemplarza) REFERENCES Egzemplarz(ID_Egzemplarza), "
                                        +
                                        "FOREIGN KEY(PracownikID_Pracownika) REFERENCES Pracownik(ID_Pracownika))");

                        // Rezerwacja (Reservations)
                        stmt.execute("CREATE TABLE IF NOT EXISTS Rezerwacja (" +
                                        "ID_Rezerwacji INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "Data_rezerwacji INTEGER NOT NULL, " +
                                        "CzytelnikID_Czytelnika INTEGER NOT NULL, " +
                                        "KsiazkaISBN TEXT NOT NULL, " +
                                        "Status TEXT DEFAULT 'Aktywna', " +
                                        "FOREIGN KEY(CzytelnikID_Czytelnika) REFERENCES Czytelnik(ID_Czytelnika), " +
                                        "FOREIGN KEY(KsiazkaISBN) REFERENCES Ksiazka(ISBN))");

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

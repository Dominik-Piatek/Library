package org.example.library;

import org.example.library.dao.*;
import org.example.library.model.*;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class Verification_All {
    public static void main(String[] args) {
        System.out.println("=== Starting Full System Verification ===");

        DatabaseInitializer.initialize();

        // DAOs
        PracownikDAO employeeDAO = new PracownikDAO();
        CzytelnikDAO readerDAO = new CzytelnikDAO();
        KsiazkaDAO bookDAO = new KsiazkaDAO();
        WypozyczenieDAO loanDAO = new WypozyczenieDAO();
        // EgzemplarzDAO copyDAO = new EgzemplarzDAO(); // Removed, handled via SQL

        // 1. Admin creates a Librarian
        System.out.println("\n[TEST 1] Admin creating Librarian...");
        String libLogin = "lib_test_" + System.currentTimeMillis();
        Pracownik lib = new Pracownik("Librarian", "Test", libLogin, "pass", "Bibliotekarz", 1);
        employeeDAO.addPracownik(lib);

        Optional<Pracownik> authLib = employeeDAO.authenticate(libLogin, "pass");
        if (authLib.isPresent() && "Bibliotekarz".equals(authLib.get().getRola())) {
            System.out.println("[PASS] Librarian created and authenticated.");
        } else {
            System.err.println("[FAIL] Librarian creation/auth failed.");
            return;
        }

        // 2. Librarian registers a Reader (simulated by DAO)
        System.out.println("\n[TEST 2] Librarian registering Reader...");
        String readerEmail = "reader_" + System.currentTimeMillis() + "@test.com";
        Czytelnik reader = new Czytelnik("John", "Doe", "555-" + System.currentTimeMillis(), readerEmail, "pass",
                authLib.get().getId());
        readerDAO.addCzytelnik(reader);

        // Fetch reader ID
        Optional<Czytelnik> authReader = readerDAO.authenticate(readerEmail, "pass");
        if (authReader.isPresent()) {
            System.out.println("[PASS] Reader registered and found: ID=" + authReader.get().getId());
        } else {
            System.err.println("[FAIL] Reader registration failed.");
            return;
        }
        int readerId = authReader.get().getId();

        // 3. Setup Book and Copy
        System.out.println("\n[TEST 3] Setting up Book and Copy...");
        String isbn = "978-TEST-" + System.currentTimeMillis();
        bookDAO.addKsiazka(new Ksiazka(isbn, "Test Book", "Author", "Genre", 2024, "IT", 1));

        // Create Copy (Egzemplarz) - Manual SQL or DAO if exists.
        // I didn't verify EgzemplarzDAO fully, let's Insert manually using connection
        // if needed or assume EgzemplarzDAO works.
        // Actually, DatabaseInitializer creates Egzemplarz table but I didn't write
        // EgzemplarzDAO in this session.
        // Let's Insert via SQL shortcut for verification to avoid implementing DAO just
        // for this test if not requested.
        // Wait, LoanPanel uses copyId. I need a valid copyId.
        int copyId = -1;
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO Egzemplarz (Kod_kreskowy, Lokalizacja_Regal, Lokalizacja_Polka, Status_wypozyczenia, KsiazkaISBN) VALUES (?, 1, 1, 'Dostepny', ?)")) {
            stmt.setString(1, "CODE-" + System.currentTimeMillis());
            stmt.setString(2, isbn);
            stmt.executeUpdate();

            // Get ID
            try (java.sql.ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next())
                    copyId = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (copyId > 0) {
            System.out.println("[PASS] Copy created with ID: " + copyId);
        } else {
            System.err.println("[FAIL] Copy creation failed.");
            return;
        }

        // 4. Librarian creates Loan
        System.out.println("\n[TEST 4] Creating Loan...");
        Calendar cal = Calendar.getInstance();
        Date now = new Date(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_YEAR, 14);
        Date due = new Date(cal.getTimeInMillis());

        Wypozyczenie loan = new Wypozyczenie(0, now, due, null, 0.0, readerId, copyId, authLib.get().getId());
        loanDAO.addWypozyczenie(loan);

        List<Wypozyczenie> activeLoans = loanDAO.getAktywneWypozyczeniaCzytelnika(readerId);
        if (!activeLoans.isEmpty() && activeLoans.get(0).getEgzemplarzId() == copyId) {
            System.out.println("[PASS] Loan created and found in active list.");
        } else {
            System.err.println("[FAIL] Loan creation/retrieval failed.");
            return;
        }
        int loanId = activeLoans.get(0).getId();

        // 5. Return Book
        System.out.println("\n[TEST 5] Returning Book...");
        loanDAO.zwrocKsiazke(loanId, now, 0.0);

        activeLoans = loanDAO.getAktywneWypozyczeniaCzytelnika(readerId);
        if (activeLoans.isEmpty()) {
            System.out.println("[PASS] Book returned, no active loans remaining.");
        } else {
            System.err.println("[FAIL] Loan still active after return.");
        }

        System.out.println("\n=== verification_All Complete ===");
    }
}

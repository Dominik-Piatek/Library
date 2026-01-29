package org.example.library;

import org.example.library.dao.CzytelnikDAO;
import org.example.library.dao.KsiazkaDAO;
import org.example.library.dao.RezerwacjaDAO;
import org.example.library.model.Czytelnik;
import org.example.library.model.DatabaseInitializer;
import org.example.library.model.Ksiazka;

import java.util.Optional;

public class Verification {
    public static void main(String[] args) {
        System.out.println("=== Starting Verification ===");

        // 1. Init DB
        DatabaseInitializer.initialize();
        System.out.println("[PASS] Database Initialized");

        // 2. Setup Data
        CzytelnikDAO readerDAO = new CzytelnikDAO();
        // Create a test reader if not exists
        try {
            readerDAO.addCzytelnik(new Czytelnik("Test", "User", "123456789", "test@example.com", "password", 1));
            System.out.println("[INFO] Test Reader Created/Ensured");
        } catch (Exception e) {
            System.out.println("[INFO] Test Reader creation skipped (might exist): " + e.getMessage());
        }

        KsiazkaDAO bookDAO = new KsiazkaDAO();
        // Create a test book if not exists
        try {
            bookDAO.addKsiazka(new Ksiazka("978-3-16-148410-0", "Test Book", "Author", "Genre", 2023, "IT", 1));
            System.out.println("[INFO] Test Book Created/Ensured");
        } catch (Exception e) {
            System.out.println("[INFO] Test Book creation skipped (might exist): " + e.getMessage());
        }

        // 3. Test Login
        Optional<Czytelnik> loggedIn = readerDAO.authenticate("test@example.com", "password");
        if (loggedIn.isPresent()) {
            System.out.println("[PASS] Reader Login Successful for: " + loggedIn.get().getEmail());
        } else {
            System.err.println("[FAIL] Reader Login Failed");
            return;
        }

        // 4. Test Reservation
        RezerwacjaDAO reservationDAO = new RezerwacjaDAO();
        String isbn = "978-3-16-148410-0";
        int readerId = loggedIn.get().getId();

        System.out.println("Checking active reservation...");
        boolean hasRes = reservationDAO.hasActiveReservation(readerId, isbn);
        if (!hasRes) {
            System.out.println("No active reservation found. Creating one...");
            reservationDAO.addRezerwacja(readerId, isbn);
            System.out.println("[PASS] Reservation Created");
        } else {
            System.out.println("[PASS] Active reservation already exists");
        }

        if (reservationDAO.hasActiveReservation(readerId, isbn)) {
            System.out.println("[PASS] Reservation Verified in DB");
        } else {
            System.err.println("[FAIL] Reservation not found after creation");
        }

        System.out.println("=== Verification Complete ===");
    }
}

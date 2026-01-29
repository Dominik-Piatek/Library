package org.example.library.view;

import org.example.library.model.Czytelnik;
import org.example.library.model.Uzytkownik;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(Uzytkownik currentUser) {
        // Podstawowe ustawienia okna
        setTitle("System Biblioteczny - Zalogowany: " + currentUser.getLogin() + " (" + currentUser.getRola() + ")");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel główny z CardLayout
        JPanel contentPanel = new JPanel(new CardLayout());

        String rola = currentUser.getRola();

        // --- LOGIKA WYBORU EKRANU ---

        // 1. ADMIN / MANAGER / ADMINISTRATOR
        if ("Manager".equalsIgnoreCase(rola) ||
                "Admin".equalsIgnoreCase(rola) ||
                "Administrator".equalsIgnoreCase(rola)) {

            // ZMIANA: Zamiast JTabbedPane, wywołujemy Twój AdminPanel
            contentPanel.add(new AdminPanel(), "Admin");
        }

        // 2. BIBLIOTEKARZ
        else if ("Bibliotekarz".equalsIgnoreCase(rola)) {
            contentPanel.add(new LibrarianPanel(currentUser.getId()), "Librarian");
        }

        // 3. CZYTELNIK
        else if ("Czytelnik".equalsIgnoreCase(rola)) {
            if (currentUser instanceof Czytelnik) {
                contentPanel.add(new ReaderPanel((Czytelnik) currentUser), "Reader");
            } else {
                JPanel errorPanel = new JPanel();
                errorPanel.add(new JLabel("Błąd danych czytelnika."));
                contentPanel.add(errorPanel, "Error");
            }
        }

        // 4. ROLA NIEZNANA
        else {
            JPanel errorPanel = new JPanel();
            errorPanel.add(new JLabel("Nie rozpoznano roli: " + rola));
            contentPanel.add(errorPanel, "Unknown");
        }

        add(contentPanel);
    }
}
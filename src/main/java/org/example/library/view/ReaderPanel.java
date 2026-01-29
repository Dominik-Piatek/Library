package org.example.library.view;

import org.example.library.controller.LoginController;
import org.example.library.model.Czytelnik;

import javax.swing.*;
import java.awt.*;

public class ReaderPanel extends JPanel {
    private Czytelnik currentUser;

    public ReaderPanel(Czytelnik user) {
        this.currentUser = user;

        // Ustawienia głównego widoku (GridBagLayout do wyśrodkowania szarego panelu)
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        // Konfiguracja rozciągania (żeby szary panel był responsywny, ale nie za duży)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(30, 30, 30, 30); // Marginesy zewnętrzne

        // --- SZARY KONTENER MENU ---
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(230, 230, 230)); // Jasnoszary
        menuPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40) // Marginesy wewnętrzne
        ));

        // Tytuł "Menu główne"
        JLabel titleLabel = new JLabel("Menu główne");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32)); // Styl z makiety
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(Box.createVerticalGlue()); // Sprężynka góra (do centrowania w pionie)
        menuPanel.add(titleLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 40))); // Odstęp

        // --- PRZYCISKI ---

        // 1. Wyświetl dostępne książki -> Otwiera CatalogDialog (stworzony wcześniej)
        menuPanel.add(createMenuButton("Wyświetl dostępne książki", e -> showCatalogDialog()));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Historia wypożyczeń -> Otwiera HistoryDialog (stworzony wcześniej)
        menuPanel.add(createMenuButton("Historia wypożyczeń", e -> showHistoryDialog()));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Wyloguj -> Wraca do ekranu logowania
        menuPanel.add(createMenuButton("Wyloguj", e -> performLogout()));

        menuPanel.add(Box.createVerticalGlue()); // Sprężynka dół

        // Dodanie szarego panelu do głównego okna
        add(menuPanel, gbc);
    }

    // Metoda pomocnicza do tworzenia jednolitych przycisków
    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(400, 50));
        button.setPreferredSize(new Dimension(400, 50));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.addActionListener(action);
        return button;
    }

    // --- AKCJE PRZYCISKÓW ---

    private void showCatalogDialog() {
        // Otwieramy okno katalogu
        // (Wymaga istnienia klasy CatalogDialog z poprzednich kroków)
        CatalogDialog dialog = new CatalogDialog((Frame) SwingUtilities.getWindowAncestor(this), currentUser);
        dialog.setVisible(true);
    }

    private void showHistoryDialog() {
        // Otwieramy okno historii
        // (Wymaga istnienia klasy HistoryDialog z poprzednich kroków)
        HistoryDialog dialog = new HistoryDialog((Frame) SwingUtilities.getWindowAncestor(this), currentUser);
        dialog.setVisible(true);
    }

    private void performLogout() {
        // Zamykamy obecne okno
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            window.dispose();
        }
        // Otwieramy ekran logowania
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            new LoginController(loginFrame);
            loginFrame.setVisible(true);
        });
    }
}
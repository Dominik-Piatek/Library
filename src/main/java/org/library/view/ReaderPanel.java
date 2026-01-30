package org.library.view;

import org.library.controller.LoginController;
import org.library.model.Czytelnik;

import javax.swing.*;
import java.awt.*;

public class ReaderPanel extends JPanel {
    private Czytelnik currentUser;

    public ReaderPanel(Czytelnik user) {
        this.currentUser = user;

        // Ustawiamy Layout główny
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);


        // --- PANEL CENTRALNY (do wyśrodkowania szarego pudełka) ---
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);

        // --- SZARY KONTENER MENU ---
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(230, 230, 230)); // Jasnoszary
        menuPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(40, 60, 40, 60) // Marginesy wewnątrz szarego
        ));

        // Tytuł wewnątrz szarego pola ("Menu główne")
        JLabel menuTitle = new JLabel("Menu główne");
        menuTitle.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(menuTitle);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 40))); // Odstęp

        // --- PRZYCISKI ---
        menuPanel.add(createMenuButton("Wyświetl dostępne książki", e -> showCatalogDialog()));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        menuPanel.add(createMenuButton("Historia wypożyczeń", e -> showHistoryDialog()));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        menuPanel.add(createMenuButton("Wyloguj", e -> performLogout()));

        // Dodajemy szary panel do środkowego wrappera
        centerWrapper.add(menuPanel, gbc);

        // Dodajemy wrapper do głównego widoku
        add(centerWrapper, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(400, 50));
        button.setPreferredSize(new Dimension(400, 50));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setBackground(new Color(245, 245, 245));
        button.addActionListener(action);
        return button;
    }

    // --- AKCJE ---

    private void showCatalogDialog() {
        CatalogDialog dialog = new CatalogDialog((Frame) SwingUtilities.getWindowAncestor(this), currentUser);
        dialog.setVisible(true);
    }

    private void showHistoryDialog() {
        HistoryDialog dialog = new HistoryDialog((Frame) SwingUtilities.getWindowAncestor(this), currentUser);
        dialog.setVisible(true);
    }

    private void performLogout() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            window.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            new LoginController(loginFrame);
            loginFrame.setVisible(true);
        });
    }
}
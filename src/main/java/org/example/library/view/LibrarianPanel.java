package org.example.library.view;

import org.example.library.controller.LoginController;

import javax.swing.*;
import java.awt.*;

public class LibrarianPanel extends JPanel {

    private int librarianId; // ID zalogowanego bibliotekarza

    // Konstruktor przyjmuje ID bibliotekarza
    public LibrarianPanel(int librarianId) {
        this.librarianId = librarianId;

        // Główny layout (centrowanie na ekranie)
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        // Konfiguracja rozciągania
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(30, 30, 30, 30);

        // --- SZARY KONTENER MENU ---
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(230, 230, 230)); // Jasnoszary
        menuPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));

        // Tytuł
        JLabel titleLabel = new JLabel("Menu bibliotekarza");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(titleLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // --- PRZYCISKI ---

        // 1. Utwórz konto czytelnika
        menuPanel.add(createMenuButton("Utwórz konto czytelnika", e -> showCreateReaderDialog()));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Wypożycz książkę (Przekazujemy ID do LoanPanel)
        menuPanel.add(createMenuButton("Wypożycz książkę", e -> showLoanDialog()));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Zarejestruj zwrot (Otwiera ReturnBookDialog)
        menuPanel.add(createMenuButton("Zarejestruj zwrot", e -> showReturnDialog()));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 4. Wyloguj
        menuPanel.add(createMenuButton("Wyloguj", e -> performLogout()));

        menuPanel.add(Box.createVerticalGlue());

        add(menuPanel, gbc);
    }

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

    private void showCreateReaderDialog() {
        // Otwieramy okno tworzenia konta czytelnika
        CreateReaderDialog dialog = new CreateReaderDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private void showLoanDialog() {
        // Otwieramy panel wypożyczania w oknie dialogowym
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Wypożyczanie książki", true);

        // Przekazujemy ID bibliotekarza do panelu, żeby wiedział kto wypożycza
        LoanPanel loanPanel = new LoanPanel(this.librarianId);

        dialog.setContentPane(loanPanel);
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showReturnDialog() {
        // Otwieramy panel zwrotów
        ReturnBookDialog dialog = new ReturnBookDialog((Frame) SwingUtilities.getWindowAncestor(this));
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
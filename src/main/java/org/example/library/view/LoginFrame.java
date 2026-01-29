package org.example.library.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("System Biblioteczny - Autoryzacja");
        setSize(800, 600); // Duże okno, żeby panel był na środku
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Wyśrodkowanie okna na ekranie

        // Główny layout okna - GridBagLayout pozwala wyśrodkować mniejszy panel w dużym oknie
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE); // Białe tło dookoła

        initComponents();
    }

    private void initComponents() {
        // --- SZARY KONTENER (RAMKA LOGOWANIA) ---
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(new Color(230, 230, 230)); // Jasnoszary
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1), // Czarna ramka
                new EmptyBorder(40, 60, 40, 60) // Marginesy wewnątrz
        ));

        // Tytuł "Logowanie"
        JLabel titleLabel = new JLabel("Logowanie");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28)); // Styl jak na makiecie
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 40))); // Odstęp

        // Pole: login/email
        loginField = new JTextField();
        loginPanel.add(createLabeledRow("login/email:", loginField));
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Pole: hasło
        passwordField = new JPasswordField();
        loginPanel.add(createLabeledRow("hasło:", passwordField));
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Przycisk Zaloguj
        loginButton = new JButton("zaloguj");
        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setMaximumSize(new Dimension(150, 40));
        loginPanel.add(loginButton);

        // Dodanie panelu do okna
        add(loginPanel);
    }

    // Metoda pomocnicza tworząca wiersz: Etykieta (po lewej) + Pole (po prawej)
    private JPanel createLabeledRow(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(new Color(230, 230, 230)); // Tło zgodne z kontenerem
        panel.setMaximumSize(new Dimension(400, 35));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(label);
        panel.add(field);
        return panel;
    }

    // --- Gettery dla Kontrolera ---
    public JTextField getLoginField() { return loginField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
}
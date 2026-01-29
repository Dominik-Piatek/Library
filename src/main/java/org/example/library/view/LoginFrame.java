package org.example.library.view;

import org.example.library.dao.CzytelnikDAO;
import org.example.library.model.Czytelnik;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

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

        // Opcjonalnie: Przycisk rejestracji (dla czytelników)
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        registerButton = new JButton("Załóż konto");
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(Color.BLUE);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> showRegistrationDialog());
        loginPanel.add(registerButton);

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

    // --- Okno rejestracji (dla czytelnika) ---
    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Rejestracja", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);

        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();

        dialog.add(new JLabel("  Imię:")); dialog.add(nameField);
        dialog.add(new JLabel("  Nazwisko:")); dialog.add(surnameField);
        dialog.add(new JLabel("  Telefon:")); dialog.add(phoneField);
        dialog.add(new JLabel("  Email:")); dialog.add(emailField);
        dialog.add(new JLabel("  Hasło:")); dialog.add(passField);

        JButton submitButton = new JButton("Zarejestruj");
        submitButton.addActionListener(ev -> {
            try {
                if (emailField.getText().isEmpty() || new String(passField.getPassword()).isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Email i hasło są wymagane!");
                    return;
                }
                CzytelnikDAO dao = new CzytelnikDAO();
                Czytelnik c = new Czytelnik(
                        nameField.getText(),
                        surnameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        new String(passField.getPassword()),
                        1 // Domyślne ID pracownika tworzącego konto (np. system/admin)
                );
                dao.addCzytelnik(c);
                JOptionPane.showMessageDialog(dialog, "Konto utworzone! Możesz się zalogować.");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Błąd: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(submitButton);
        dialog.setVisible(true);
    }

    // --- Gettery dla Kontrolera ---
    public JTextField getLoginField() { return loginField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getRegisterButton() { return registerButton; }
}
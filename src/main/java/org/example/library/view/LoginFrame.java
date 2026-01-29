package org.example.library.view;

import javax.swing.*;
import java.awt.*;
import org.example.library.dao.CzytelnikDAO;
import org.example.library.model.Czytelnik;

public class LoginFrame extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame() {
        setTitle("System Biblioteczny - Autoryzacja");
        setSize(400, 380); // Increased height for register button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Dostęp do Biblioteki");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel loginLabel = new JLabel("Login / Email");
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(loginLabel);

        loginField = new JTextField(15);
        loginField.setMaximumSize(new Dimension(300, 30));
        mainPanel.add(loginField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel passLabel = new JLabel("Hasło");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passLabel);

        passwordField = new JPasswordField(15);
        passwordField.setMaximumSize(new Dimension(300, 30));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        loginButton = new JButton("Zaloguj");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setMaximumSize(new Dimension(150, 35));

        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        registerButton = new JButton("Załóż konto");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerButton.setMaximumSize(new Dimension(150, 30));
        registerButton.addActionListener(e -> showRegistrationDialog());

        mainPanel.add(registerButton);

        add(mainPanel);
    }

    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Rejestracja Nowego Czytelnika", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);

        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();

        dialog.add(new JLabel("  Imię:"));
        dialog.add(nameField);
        dialog.add(new JLabel("  Nazwisko:"));
        dialog.add(surnameField);
        dialog.add(new JLabel("  Telefon:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("  Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("  Hasło:"));
        dialog.add(passField);

        JButton submitButton = new JButton("Zarejestruj");
        submitButton.addActionListener(ev -> {
            try {
                // Validate
                if (nameField.getText().isEmpty() || surnameField.getText().isEmpty()
                        || emailField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Wypełnij wymagane pola!");
                    return;
                }

                CzytelnikDAO dao = new CzytelnikDAO();
                // Check if exists? (Optional)

                Czytelnik c = new Czytelnik(
                        nameField.getText(),
                        surnameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        new String(passField.getPassword()),
                        1 // Default assigned to Admin ID 1 for now
                );
                dao.addCzytelnik(c);
                JOptionPane.showMessageDialog(dialog, "Konto utworzone! Możesz się zalogować.");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Błąd: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(submitButton); // Center button?

        dialog.setVisible(true);
    }

    public JTextField getLoginField() {
        return loginField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getLoginButton() {
        return loginButton;
    }
}

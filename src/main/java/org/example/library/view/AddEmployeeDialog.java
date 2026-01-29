package org.example.library.view;

import org.example.library.dao.PracownikDAO;
import org.example.library.model.Pracownik;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddEmployeeDialog extends JDialog {

    private JTextField nameField;
    private JTextField surnameField;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JPasswordField repeatPasswordField;
    private JComboBox<String> roleCombo;

    public AddEmployeeDialog(Frame owner) {
        super(owner, "Tworzenie konta pracownika", true);
        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel titleLabel = new JLabel("Tworzenie konta pracownika");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        mainPanel.add(createLabeledField("Imie", nameField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Nazwisko", surnameField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("login", loginField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- POPRAWKA TUTAJ: Usunięto "Kierownik" ---
        String[] roles = { "Bibliotekarz", "Administrator" };
        roleCombo = new JComboBox<>(roles);
        roleCombo.setBackground(Color.WHITE);
        mainPanel.add(createLabeledField("rola", roleCombo));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("hasło", passwordField = new JPasswordField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("powtórz hasło", repeatPasswordField = new JPasswordField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton cancelButton = createButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton confirmButton = createButton("Zatwierdź");
        confirmButton.addActionListener(e -> saveEmployee());

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        mainPanel.add(buttonPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(230, 230, 230));
        panel.setMaximumSize(new Dimension(400, 50));

        JLabel label = new JLabel("  " + labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(100, 30));

        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }

    private void saveEmployee() {
        String pass1 = new String(passwordField.getPassword());
        String pass2 = new String(repeatPasswordField.getPassword());

        if (!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(this, "Hasła nie są identyczne!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nameField.getText().isEmpty() || loginField.getText().isEmpty() || pass1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wypełnij wszystkie pola!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            PracownikDAO dao = new PracownikDAO();
            Pracownik p = new Pracownik(
                    nameField.getText(),
                    surnameField.getText(),
                    loginField.getText(),
                    pass1,
                    (String) roleCombo.getSelectedItem(),
                    1
            );
            dao.addPracownik(p);
            JOptionPane.showMessageDialog(this, "Konto pracownika utworzone pomyślnie!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Błąd bazy danych: " + ex.getMessage());
        }
    }
}
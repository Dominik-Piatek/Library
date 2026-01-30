package org.library.view;

import org.library.dao.CzytelnikDAO;
import org.library.model.Czytelnik;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateReaderDialog extends JDialog {

    private JTextField nameField;
    private JTextField surnameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField repeatPasswordField;

    public CreateReaderDialog(Frame owner) {
        super(owner, "Tworzenie konta", true);
        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Główny panel (szare tło)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230)); // Jasnoszary
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50)); // Marginesy

        // Tytuł
        JLabel titleLabel = new JLabel("Tworzenie konta");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28)); // Styl "Comic Sans" z makiety
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Pola formularza
        mainPanel.add(createLabeledField("Imie", nameField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Nazwisko", surnameField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("adres e-mail", emailField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("numer telefonu", phoneField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("hasło", passwordField = new JPasswordField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("powtórz hasło", repeatPasswordField = new JPasswordField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Przyciski
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton cancelButton = createButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton confirmButton = createButton("Zatwierdź");
        confirmButton.addActionListener(e -> saveReader());

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
        label.setPreferredSize(new Dimension(120, 30)); // Nieco szersza etykieta dla "adres e-mail"

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

    private void saveReader() {
        String pass1 = new String(passwordField.getPassword());
        String pass2 = new String(repeatPasswordField.getPassword());

        // Walidacja
        if (nameField.getText().isEmpty() || surnameField.getText().isEmpty() || emailField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wypełnij podstawowe dane (Imię, Nazwisko, Email)!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(this, "Hasła nie są identyczne!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            CzytelnikDAO dao = new CzytelnikDAO();
            Czytelnik c = new Czytelnik(
                    nameField.getText(),
                    surnameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    pass1,
                    1
            );
            dao.addCzytelnik(c);
            JOptionPane.showMessageDialog(this, "Konto czytelnika utworzone pomyślnie!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Błąd bazy danych: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}
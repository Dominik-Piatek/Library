package org.example.library.view;

import org.example.library.dao.CzytelnikDAO;
import org.example.library.model.Czytelnik;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditReaderProfileDialog extends JDialog {

    private final Czytelnik reader;
    private final JTextField nameField;
    private final JTextField surnameField;
    private final JTextField phoneField;
    private final JTextField emailField;
    private String newPassword = null; // Zmienna tymczasowa na nowe hasło

    public EditReaderProfileDialog(Frame owner, Czytelnik reader) {
        super(owner, "Modyfikacja danych czytelnika", true);
        this.reader = reader;
        setSize(500, 550);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Główny panel (szare tło)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Tytuł
        JLabel titleLabel = new JLabel("Modyfikuj dane");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Pola tekstowe
        mainPanel.add(createLabeledField("Imie", nameField = new JTextField(reader.getImie())));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Nazwisko", surnameField = new JTextField(reader.getNazwisko())));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Telefon", phoneField = new JTextField(reader.getNrTelefonu())));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Email", emailField = new JTextField(reader.getEmail())));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Przycisk "Resetuj hasło"
        JPanel passPanel = new JPanel(new BorderLayout(5, 5));
        passPanel.setBackground(new Color(230, 230, 230));
        passPanel.setMaximumSize(new Dimension(400, 50));

        JLabel passLabel = new JLabel("  hasło");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setPreferredSize(new Dimension(100, 30));

        JButton resetPassBtn = new JButton("Resetuj hasło");
        resetPassBtn.setPreferredSize(new Dimension(200, 30));
        resetPassBtn.setFocusPainted(false);
        resetPassBtn.addActionListener(e -> handlePasswordReset());

        passPanel.add(passLabel, BorderLayout.WEST);
        passPanel.add(resetPassBtn, BorderLayout.CENTER);

        mainPanel.add(passPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Przyciski Anuluj / Zatwierdź
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton cancelButton = createButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton confirmButton = createButton("Zatwierdź");
        confirmButton.addActionListener(e -> saveChanges());

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        mainPanel.add(buttonPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    // Logika przycisku Resetuj hasło
    private void handlePasswordReset() {
        String input = JOptionPane.showInputDialog(this, "Wpisz nowe hasło dla czytelnika:", "Zmiana hasła", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            this.newPassword = input.trim();
            JOptionPane.showMessageDialog(this, "Hasło zostało zmienione tymczasowo.\nKliknij 'Zatwierdź', aby zapisać zmiany w bazie.");
        }
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

    private void saveChanges() {
        try {
            // Aktualizujemy obiekt czytelnika danymi z formularza
            reader.setImie(nameField.getText());
            reader.setNazwisko(surnameField.getText());
            reader.setNrTelefonu(phoneField.getText());
            reader.setEmail(emailField.getText());

            // Jeśli użytkownik zresetował hasło, aktualizujemy je
            if (newPassword != null) {
                reader.setHasloSkrot(newPassword);
            }

            // Zapis do bazy
            CzytelnikDAO dao = new CzytelnikDAO();
            dao.updateCzytelnik(reader);

            JOptionPane.showMessageDialog(this, "Dane zaktualizowane!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd zapisu: " + e.getMessage());
        }
    }
}
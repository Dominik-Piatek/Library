package org.example.library.view;

import org.example.library.dao.CzytelnikDAO;
import org.example.library.model.Czytelnik;

import javax.swing.*;
import java.awt.*;

public class EditReaderProfileDialog extends JDialog {
    private Czytelnik reader;
    private CzytelnikDAO dao;
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JPasswordField passField;

    public EditReaderProfileDialog(Frame owner, Czytelnik reader) {
        super(owner, "Edycja Profilu", true);
        this.reader = reader;
        this.dao = new CzytelnikDAO();

        setSize(350, 300);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(6, 2, 10, 10));

        nameField = new JTextField(reader.getImie());
        surnameField = new JTextField(reader.getNazwisko());
        phoneField = new JTextField(reader.getNrTelefonu());
        emailField = new JTextField(reader.getEmail());
        passField = new JPasswordField(reader.getHasloSkrot()); // In real app, empty by default to keep unchanged

        add(new JLabel("  Imię:"));
        add(nameField);
        add(new JLabel("  Nazwisko:"));
        add(surnameField);
        add(new JLabel("  Telefon:"));
        add(phoneField);
        add(new JLabel("  Email:"));
        add(emailField);
        add(new JLabel("  Nowe hasło:"));
        add(passField);

        JButton saveButton = new JButton("Zapisz zmiany");
        saveButton.addActionListener(e -> saveProfile());

        add(new JLabel(""));
        add(saveButton);
    }

    private void saveProfile() {
        try {
            reader.setImie(nameField.getText());
            reader.setNazwisko(surnameField.getText());
            reader.setNrTelefonu(phoneField.getText());
            reader.setEmail(emailField.getText());
            // Only update password if changed (simple logic for MVP)
            String newPass = new String(passField.getPassword());
            if (!newPass.isEmpty()) {
                reader.setHasloSkrot(newPass);
            }

            dao.updateCzytelnik(reader);
            JOptionPane.showMessageDialog(this, "Dane zaktualizowane!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage());
        }
    }
}

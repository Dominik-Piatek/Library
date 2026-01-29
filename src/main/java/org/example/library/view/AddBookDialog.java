package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.model.Egzemplarz;
import org.example.library.model.Ksiazka;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.UUID;

public class AddBookDialog extends JDialog {

    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField yearField;
    private JTextField rackField; // Regał
    private JTextField shelfField; // Półka

    public AddBookDialog(Frame owner) {
        super(owner, "Dodaj książkę", true);
        setSize(500, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Główny panel (szare tło)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Tytuł formularza
        JLabel titleLabel = new JLabel("Dodaj książkę:");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24)); // Styl "Comic Sans" z makiety
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Pola formularza
        mainPanel.add(createLabeledField("Tytuł", titleField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Autor", authorField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("ISBN", isbnField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Rok wydania", yearField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Regał", rackField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Półka", shelfField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Przyciski
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton cancelButton = createButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton addButton = createButton("Dodaj");
        addButton.addActionListener(e -> saveBook());

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        mainPanel.add(buttonPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(230, 230, 230));
        panel.setMaximumSize(new Dimension(400, 35));

        JLabel label = new JLabel(labelText);
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 30));
        return btn;
    }

    private void saveBook() {
        // Walidacja
        if (titleField.getText().isEmpty() || isbnField.getText().isEmpty() || yearField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wypełnij wymagane pola (Tytuł, ISBN, Rok)!");
            return;
        }

        try {
            int year = Integer.parseInt(yearField.getText());
            int rack = Integer.parseInt(rackField.getText().isEmpty() ? "0" : rackField.getText());
            int shelf = Integer.parseInt(shelfField.getText().isEmpty() ? "0" : shelfField.getText());

            KsiazkaDAO dao = new KsiazkaDAO();

            // 1. Tworzymy książkę
            // Uwaga: Makieta nie ma pola "Gatunek" i "Dziedzina", więc wpisujemy domyślne "Inne"
            Ksiazka k = new Ksiazka(
                    isbnField.getText(),
                    titleField.getText(),
                    authorField.getText(),
                    "Inne", // Domyślny gatunek
                    year,
                    "Inne", // Domyślna dziedzina
                    1 // ID Administratora (hardcoded dla MVP)
            );
            dao.addKsiazka(k);

            // 2. Tworzymy pierwszy egzemplarz tej książki (skoro podajemy regał i półkę)
            // Generujemy unikalny kod kreskowy
            String barcode = "CODE-" + System.currentTimeMillis();
            Egzemplarz e = new Egzemplarz(
                    barcode,
                    rack,
                    shelf,
                    "Dostępna",
                    isbnField.getText()
            );
            dao.addEgzemplarz(e);

            JOptionPane.showMessageDialog(this, "Książka i egzemplarz dodane pomyślnie!");
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Rok, Regał i Półka muszą być liczbami!", "Błąd", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd bazy danych: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}
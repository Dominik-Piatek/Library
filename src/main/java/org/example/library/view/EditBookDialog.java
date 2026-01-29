package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.model.DatabaseConnection;
import org.example.library.model.Egzemplarz;
import org.example.library.model.Ksiazka;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

public class EditBookDialog extends JDialog {

    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField yearField;
    private JTextField rackField; // Regał
    private JTextField shelfField; // Półka

    private String originalIsbn;
    private int egzemplarzIdToUpdate = -1; // ID egzemplarza do aktualizacji lokalizacji

    public EditBookDialog(Frame owner, String isbn) {
        super(owner, "Edycja książki", true);
        this.originalIsbn = isbn;
        setSize(500, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Główny panel (szare tło)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Tytuł
        JLabel titleLabel = new JLabel("Edytuj dane książki");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Inicjalizacja pól
        titleField = new JTextField();
        authorField = new JTextField();
        isbnField = new JTextField();
        isbnField.setEditable(false); // ISBN jest kluczem, zazwyczaj się go nie edytuje
        isbnField.setBackground(new Color(210, 210, 210)); // Szare tło dla zablokowanego
        yearField = new JTextField();
        rackField = new JTextField();
        shelfField = new JTextField();

        // Dodawanie pól do widoku
        mainPanel.add(createLabeledField("Tytuł", titleField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Autor", authorField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("ISBN", isbnField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Rok wydania", yearField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Regał", rackField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Półka", shelfField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Przyciski
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton cancelButton = createButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton confirmButton = createButton("Zatwierdź");
        confirmButton.addActionListener(e -> saveChanges());

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        mainPanel.add(buttonPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Załadowanie danych do formularza
        loadData();
    }

    private void loadData() {
        KsiazkaDAO dao = new KsiazkaDAO();

        // 1. Znajdź książkę (proste filtrowanie po liście, bo DAO nie ma metody getOne)
        Optional<Ksiazka> bookOpt = dao.getAllKsiazki().stream()
                .filter(k -> k.getIsbn().equals(originalIsbn))
                .findFirst();

        if (bookOpt.isPresent()) {
            Ksiazka k = bookOpt.get();
            titleField.setText(k.getTytul());
            authorField.setText(k.getAutor());
            isbnField.setText(k.getIsbn());
            yearField.setText(String.valueOf(k.getRokWydania()));
        }

        // 2. Znajdź pierwszy egzemplarz, żeby pobrać lokalizację (Regał/Półka)
        List<Egzemplarz> copies = dao.getEgzemplarzeByIsbn(originalIsbn);
        if (!copies.isEmpty()) {
            Egzemplarz e = copies.get(0);
            rackField.setText(String.valueOf(e.getLokalizacjaRegal()));
            shelfField.setText(String.valueOf(e.getLokalizacjaPolka()));
            this.egzemplarzIdToUpdate = e.getId();
        }
    }

    private void saveChanges() {
        try {
            int year = Integer.parseInt(yearField.getText());
            int rack = Integer.parseInt(rackField.getText());
            int shelf = Integer.parseInt(shelfField.getText());

            KsiazkaDAO dao = new KsiazkaDAO();

            // 1. Aktualizacja danych książki
            // Tworzymy obiekt z nowymi danymi, ale STARYM ISBN (klucz)
            Ksiazka k = new Ksiazka(
                    originalIsbn,
                    titleField.getText(),
                    authorField.getText(),
                    "Inne", // Gatunek (nie ma na widoku)
                    year,
                    "Inne", // Dziedzina (nie ma na widoku)
                    1 // ID Admina (bez zmian)
            );
            dao.updateKsiazka(k);

            // 2. Aktualizacja lokalizacji egzemplarza (SQL bezpośredni, bo DAO nie ma takiej metody)
            if (egzemplarzIdToUpdate != -1) {
                updateEgzemplarzLocation(egzemplarzIdToUpdate, rack, shelf);
            }

            JOptionPane.showMessageDialog(this, "Dane zaktualizowane!");
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Rok, Regał i Półka muszą być liczbami!", "Błąd", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd zapisu: " + e.getMessage());
        }
    }

    // Pomocnicza metoda do aktualizacji lokalizacji (Bezpośredni SQL dla szybkości)
    private void updateEgzemplarzLocation(int id, int regal, int polka) {
        String sql = "UPDATE Egzemplarz SET Lokalizacja_Regal = ?, Lokalizacja_Polka = ? WHERE ID_Egzemplarza = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, regal);
            pstmt.setInt(2, polka);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
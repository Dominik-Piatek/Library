package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.model.Ksiazka;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddBookDialog extends JDialog {

    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField yearField;
    private JTextField genreField; // Gatunek
    private JTextField categoryField; // Dziedzina

    public AddBookDialog(Window owner) {
        super(owner, "Dodaj książkę", ModalityType.APPLICATION_MODAL);
        setSize(500, 550); // Increased height slightly
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Główny panel (szare tło)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Tytuł formularza
        JLabel titleLabel = new JLabel("Dodaj książkę:");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
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

        mainPanel.add(createLabeledField("Gatunek", genreField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Dziedzina", categoryField = new JTextField()));
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
        if (titleField.getText().isEmpty() || isbnField.getText().isEmpty() || yearField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wypełnij wymagane pola (Tytuł, ISBN, Rok)!");
            return;
        }

        try {
            int year = Integer.parseInt(yearField.getText());
            
            KsiazkaDAO dao = new KsiazkaDAO();

            Ksiazka k = new Ksiazka(
                    isbnField.getText(),
                    titleField.getText(),
                    authorField.getText(),
                    genreField.getText(),
                    year,
                    categoryField.getText(),
                    1 // Default PracownikID (should be logged in user ideally, but 1 is safe for now)
            );
            dao.addKsiazka(k);

            JOptionPane.showMessageDialog(this, "Książka dodana pomyślnie!");
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Rok musi być liczbą!", "Błąd", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd bazy danych: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}
package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.model.Ksiazka;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

public class EditBookDialog extends JDialog {

    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField yearField;
    private JTextField genreField; // Gatunek
    private JTextField categoryField; // Dziedzina

    private String originalIsbn;

    public EditBookDialog(Window owner, String isbn) {
        super(owner, "Edycja książki", ModalityType.APPLICATION_MODAL);
        this.originalIsbn = isbn;
        setSize(500, 550);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titleLabel = new JLabel("Edytuj dane książki");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        titleField = new JTextField();
        authorField = new JTextField();
        isbnField = new JTextField();
        isbnField.setEditable(false);
        isbnField.setBackground(new Color(210, 210, 210));
        yearField = new JTextField();
        genreField = new JTextField();
        categoryField = new JTextField();

        mainPanel.add(createLabeledField("Tytuł", titleField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createLabeledField("Autor", authorField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createLabeledField("ISBN", isbnField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createLabeledField("Rok wydania", yearField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createLabeledField("Gatunek", genreField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createLabeledField("Dziedzina", categoryField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Przycisk "Edytuj egzemplarze"
        JButton editCopiesButton = new JButton("Edytuj egzemplarze");
        editCopiesButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        editCopiesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editCopiesButton.addActionListener(e -> openEditCopiesDialog());
        mainPanel.add(editCopiesButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

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

        loadData();
    }

    private void loadData() {
        KsiazkaDAO dao = new KsiazkaDAO();
        Optional<Ksiazka> bookOpt = dao.getAllKsiazki().stream()
                .filter(k -> k.getIsbn().equals(originalIsbn))
                .findFirst();

        if (bookOpt.isPresent()) {
            Ksiazka k = bookOpt.get();
            titleField.setText(k.getTytul());
            authorField.setText(k.getAutor());
            isbnField.setText(k.getIsbn());
            yearField.setText(String.valueOf(k.getRokWydania()));
            genreField.setText(k.getGatunek());
            categoryField.setText(k.getDziedzina());
        }
    }

    private void saveChanges() {
        try {
            int year = Integer.parseInt(yearField.getText());

            KsiazkaDAO dao = new KsiazkaDAO();
            Ksiazka k = new Ksiazka(
                    originalIsbn, 
                    titleField.getText(), 
                    authorField.getText(), 
                    genreField.getText(), 
                    year, 
                    categoryField.getText(), 
                    1 // Default PracownikID
            );
            dao.updateKsiazka(k);

            JOptionPane.showMessageDialog(this, "Dane zaktualizowane!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd zapisu: " + e.getMessage());
        }
    }

    private void openEditCopiesDialog() {
        JOptionPane.showMessageDialog(this, "Funkcjonalność edycji egzemplarzy zostanie dodana później.");
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(230, 230, 230));
        panel.setMaximumSize(new Dimension(400, 35));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 30));
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(100, 30));
        return btn;
    }
}
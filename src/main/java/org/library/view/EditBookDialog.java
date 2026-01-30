package org.library.view;

import org.library.dao.EgzemplarzDAO;
import org.library.dao.KsiazkaDAO;
import org.library.model.Egzemplarz;
import org.library.model.Ksiazka;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class EditBookDialog extends JDialog {

    // Pola formularza
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField yearField;
    private JTextField genreField;
    private JTextField categoryField;

    // Label do wyświetlania licznika
    private JLabel copiesCountLabel;

    private String originalIsbn;
    private Ksiazka bookToEdit;

    // DAO
    private KsiazkaDAO ksiazkaDAO;
    private EgzemplarzDAO egzemplarzDAO;

    public EditBookDialog(Window owner, String isbn) {
        super(owner, "Edycja książki", ModalityType.APPLICATION_MODAL);
        this.originalIsbn = isbn;
        this.ksiazkaDAO = new KsiazkaDAO();
        this.egzemplarzDAO = new EgzemplarzDAO();

        // 1. Najpierw pobieramy dane książki z bazy
        loadBookData();

        setSize(550, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- NAGŁÓWEK ---
        JLabel headerLabel = new JLabel("Edytuj dane książki");
        headerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // --- FORMULARZ (GridBagLayout) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(230, 230, 230)); // Szare tło
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Jeśli nie znaleziono książki, tworzymy puste pola
        String tytul = (bookToEdit != null) ? bookToEdit.getTytul() : "";
        String autor = (bookToEdit != null) ? bookToEdit.getAutor() : "";
        String rokWydania = (bookToEdit != null) ? String.valueOf(bookToEdit.getRokWydania()) : "";
        String gatunek = (bookToEdit != null) ? bookToEdit.getGatunek() : "";
        String dziedzina = (bookToEdit != null) ? bookToEdit.getDziedzina() : "";

        // Wiersz 0: Tytuł
        addFormRow(formPanel, gbc, 0, "Tytuł", titleField = new JTextField(tytul));

        // Wiersz 1: Autor
        addFormRow(formPanel, gbc, 1, "Autor", authorField = new JTextField(autor));

        // Wiersz 2: ISBN (Read-only)
        isbnField = new JTextField(originalIsbn);
        isbnField.setEditable(false);
        isbnField.setBackground(new Color(210, 210, 210));
        addFormRow(formPanel, gbc, 2, "ISBN", isbnField);

        // Wiersz 3: Rok wydania
        addFormRow(formPanel, gbc, 3, "Rok wydania", yearField = new JTextField(rokWydania));

        // Wiersz 4: Gatunek
        addFormRow(formPanel, gbc, 4, "Gatunek", genreField = new JTextField(gatunek));

        // Wiersz 5: Dziedzina
        addFormRow(formPanel, gbc, 5, "Dziedzina", categoryField = new JTextField(dziedzina));

        // Wiersz 6: EGZEMPLARZE
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.weightx = 0.0;
        JLabel copyLabel = new JLabel("Egzemplarze");
        copyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(copyLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        // Panel wewnętrzny dla licznika i przycisku
        JPanel copyActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        copyActionPanel.setBackground(new Color(230, 230, 230));

        copiesCountLabel = new JLabel("dostępne .../...");
        copiesCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton editCopiesBtn = new JButton("edytuj");
        editCopiesBtn.setFocusPainted(false);
        editCopiesBtn.setPreferredSize(new Dimension(80, 25));
        editCopiesBtn.addActionListener(e -> openEditCopiesDialog());

        copyActionPanel.add(copiesCountLabel);
        copyActionPanel.add(editCopiesBtn);

        formPanel.add(copyActionPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- PRZYCISKI DOLNE ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton cancelBtn = new JButton("Anuluj");
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Zatwierdź");
        saveBtn.setPreferredSize(new Dimension(100, 35));
        saveBtn.addActionListener(e -> saveChanges());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // Na koniec odświeżamy licznik egzemplarzy
        refreshCopiesCount();
    }

    private void loadBookData() {
        Optional<Ksiazka> bookOpt = ksiazkaDAO.getAllKsiazki().stream()
                .filter(k -> k.getIsbn().equals(originalIsbn))
                .findFirst();

        if (bookOpt.isPresent()) {
            this.bookToEdit = bookOpt.get();
        } else {
            JOptionPane.showMessageDialog(this, "Błąd: Nie znaleziono książki o podanym ISBN.");
            dispose();
        }
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        field.setPreferredSize(new Dimension(200, 30));
        panel.add(field, gbc);
    }

    private void refreshCopiesCount() {
        List<Egzemplarz> copies = egzemplarzDAO.getEgzemplarzeByIsbn(originalIsbn);
        long total = copies.size();
        long available = copies.stream()
                .filter(e -> "Dostępna".equalsIgnoreCase(e.getStatusWypozyczenia()))
                .count();

        copiesCountLabel.setText("dostępne " + available + "/" + total);
    }

    private void openEditCopiesDialog() {
        CopyManagementDialog dialog = new CopyManagementDialog(this, originalIsbn);
        dialog.setVisible(true);
        refreshCopiesCount();
    }

    private void saveChanges() {
        try {
            int year = Integer.parseInt(yearField.getText());

            Ksiazka k = new Ksiazka(
                    originalIsbn,
                    titleField.getText(),
                    authorField.getText(),
                    genreField.getText(),
                    year,
                    categoryField.getText()
            );

            ksiazkaDAO.updateKsiazka(k);

            JOptionPane.showMessageDialog(this, "Dane zaktualizowane!");
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Rok wydania musi być liczbą!", "Błąd", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd zapisu: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}
package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.model.Ksiazka;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookManagementPanel extends JPanel {
    private JTable bookTable;
    private KsiazkaDAO ksiazkaDAO;

    public BookManagementPanel() {
        this.ksiazkaDAO = new KsiazkaDAO();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);

        JButton refreshButton = createStyledButton("Odśwież", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> loadBooks());
        toolBar.add(refreshButton);
        toolBar.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton addButton = createStyledButton("Dodaj książkę", new Color(46, 204, 113));
        addButton.addActionListener(e -> showAddBookDialog());
        toolBar.add(addButton);

        add(toolBar, BorderLayout.NORTH);

        // Table
        bookTable = new JTable();
        bookTable.setRowHeight(25);
        bookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        loadBooks();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }

    private void loadBooks() {
        List<Ksiazka> books = ksiazkaDAO.getAllKsiazki();
        String[] columns = { "ISBN", "Tytuł", "Autor", "Gatunek", "Rok", "Dziedzina" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Ksiazka k : books) {
            model.addRow(new Object[] { k.getIsbn(), k.getTytul(), k.getAutor(), k.getGatunek(), k.getRokWydania(),
                    k.getDziedzina() });
        }
        bookTable.setModel(model);
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Book", true);
        dialog.setLayout(new GridLayout(7, 2));
        dialog.setSize(300, 300);

        JTextField isbnField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField fieldField = new JTextField();

        dialog.add(new JLabel("ISBN:"));
        dialog.add(isbnField);
        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Author:"));
        dialog.add(authorField);
        dialog.add(new JLabel("Genre:"));
        dialog.add(genreField);
        dialog.add(new JLabel("Year:"));
        dialog.add(yearField);
        dialog.add(new JLabel("Field:"));
        dialog.add(fieldField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Ksiazka k = new Ksiazka(
                        isbnField.getText(),
                        titleField.getText(),
                        authorField.getText(),
                        genreField.getText(),
                        Integer.parseInt(yearField.getText()),
                        fieldField.getText(),
                        1 // Default ID, typically logged in user ID
                );
                ksiazkaDAO.addKsiazka(k);
                dialog.dispose();
                loadBooks();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving book: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(saveButton);

        dialog.setVisible(true);
    }
}

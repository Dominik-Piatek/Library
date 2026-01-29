package org.example.library.view;

import org.example.library.dao.CzytelnikDAO;
import org.example.library.model.Czytelnik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReaderManagementPanel extends JPanel {
    private JTable readerTable;
    private CzytelnikDAO czytelnikDAO;
    private int loggedInLibrarianId;

    public ReaderManagementPanel(int librarianId) {
        this.loggedInLibrarianId = librarianId;
        this.czytelnikDAO = new CzytelnikDAO();
        setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(e -> loadReaders());
        toolBar.add(refreshButton);

        JButton addButton = new JButton("Dodaj czytelnika");
        addButton.addActionListener(e -> showAddReaderDialog());
        toolBar.add(addButton);

        add(toolBar, BorderLayout.NORTH);

        readerTable = new JTable();
        add(new JScrollPane(readerTable), BorderLayout.CENTER);

        loadReaders();
    }

    private void loadReaders() {
        List<Czytelnik> readers = czytelnikDAO.getAllCzytelnicy();
        String[] columns = { "ID", "Imię", "Nazwisko", "Telefon", "Email" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Czytelnik c : readers) {
            model.addRow(new Object[] { c.getId(), c.getImie(), c.getNazwisko(), c.getNrTelefonu(), c.getEmail() });
        }
        readerTable.setModel(model);
    }

    private void showAddReaderDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Dodaj czytelnika", true);
        dialog.setLayout(new GridLayout(6, 2));
        dialog.setSize(300, 250);

        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        dialog.add(new JLabel("Imię:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Nazwisko:"));
        dialog.add(surnameField);
        dialog.add(new JLabel("Telefon:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Hasło:"));
        dialog.add(passwordField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Czytelnik c = new Czytelnik(
                        nameField.getText(),
                        surnameField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        new String(passwordField.getPassword()),
                        loggedInLibrarianId);
                czytelnikDAO.addCzytelnik(c);
                dialog.dispose();
                loadReaders();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding reader: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(saveButton);

        dialog.setVisible(true);
    }
}

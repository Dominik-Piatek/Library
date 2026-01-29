package org.example.library.view;

import org.example.library.dao.CzytelnikDAO;
import org.example.library.dao.EgzemplarzDAO;
import org.example.library.dao.KsiazkaDAO;
import org.example.library.dao.WypozyczenieDAO;
import org.example.library.model.Czytelnik;
import org.example.library.model.Ksiazka;
import org.example.library.model.Wypozyczenie;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date; // Using java.sql.Date for simplicity with JDBC
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class LoanPanel extends JPanel {
    private int librarianId;
    private WypozyczenieDAO wypozyczenieDAO;
    private CzytelnikDAO czytelnikDAO;
    // For MVP, assuming loaning by entering IDs directly or selecting from lists.
    // Let's implement a simple flow: Enter Reader ID, Enter Book ISBN (or Copy ID),
    // Click Loan.

    private JTextField readerIdField;
    private JTextField copyIdField; // Using Copy ID (EgzemplarzID) or ISBN if simplified. DB Wypozyczenie needs
                                    // EgzemplarzID.
    // We need EgzemplarzDAO to resolve ISBN to available copy? Or just enter Copy
    // ID.
    // Let's assume EgzemplarzID for precision as per schema.

    private JTable activeLoansTable;

    public LoanPanel(int librarianId) {
        this.librarianId = librarianId;
        this.wypozyczenieDAO = new WypozyczenieDAO();
        this.czytelnikDAO = new CzytelnikDAO();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // West: New Loan Form
        JPanel loanForm = new JPanel(new GridLayout(6, 1, 5, 5));
        loanForm.setBorder(BorderFactory.createTitledBorder("Nowe wypożyczenie"));

        readerIdField = new JTextField();
        copyIdField = new JTextField();

        loanForm.add(new JLabel("ID Czytelnika:"));
        loanForm.add(readerIdField);
        loanForm.add(new JLabel("ID Egzemplarza:"));
        loanForm.add(copyIdField);

        JButton loanButton = new JButton("Zarejestruj wypożyczenie");
        loanButton.setBackground(new Color(46, 204, 113));
        loanButton.setForeground(Color.WHITE);
        loanButton.addActionListener(e -> performLoan());
        loanForm.add(loanButton);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(loanForm, BorderLayout.NORTH);

        add(leftPanel, BorderLayout.WEST);

        // Center: Active Loans / Returns
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Aktywne wypożyczenia"));

        activeLoansTable = new JTable();
        centerPanel.add(new JScrollPane(activeLoansTable), BorderLayout.CENTER);

        JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(e -> refreshLoans());

        JButton returnButton = new JButton("Zwróć zaznaczone");
        returnButton.setBackground(new Color(231, 76, 60));
        returnButton.setForeground(Color.WHITE);
        returnButton.addActionListener(e -> performReturn());

        returnPanel.add(refreshButton);
        returnPanel.add(returnButton);
        centerPanel.add(returnPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void performLoan() {
        try {
            int readerId = Integer.parseInt(readerIdField.getText());
            int copyId = Integer.parseInt(copyIdField.getText());

            // Basic validation skipped for speed, assume IDs exist.
            // Calculate return date (e.g., 14 days)
            Calendar cal = Calendar.getInstance();
            java.util.Date now = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, 14);
            java.util.Date due = cal.getTime();

            Wypozyczenie wyp = new Wypozyczenie(
                    0, // ID auto
                    now,
                    due,
                    null,
                    0.0,
                    readerId,
                    copyId,
                    librarianId);

            wypozyczenieDAO.addWypozyczenie(wyp);
            JOptionPane.showMessageDialog(this, "Wypożyczenie dodane pomyślnie!");
            readerIdField.setText("");
            copyIdField.setText("");
            refreshLoans(); // Ideally filter by reader, but let's refresh view?
            // Note: refreshLoans depends on logic. Below I'll impl generic refresh or by
            // reader.

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nieprawidłowe ID.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage());
        }
    }

    private void refreshLoans() {
        // Since we don't have a specific reader selected, maybe show ALL active loans
        // or ask for Reader ID?
        // Let's use the readerIdField if populated to filter, else valid warning.
        try {
            String txt = readerIdField.getText();
            if (txt.isEmpty()) {
                // JOptionPane.showMessageDialog(this, "Enter Reader ID to see loans.");
                // Alternatively, show nothing or all (if method exists).
                // DAO has getAktywneWypozyczeniaCzytelnika(id).
                return;
            }
            int readerId = Integer.parseInt(txt);
            List<Wypozyczenie> loans = wypozyczenieDAO.getAktywneWypozyczeniaCzytelnika(readerId);

            String[] columns = { "ID", "Data", "Termin zwrotu", "ID Egzemplarza" };
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (Wypozyczenie w : loans) {
                model.addRow(new Object[] { w.getId(), w.getDataWypozyczenia(), w.getPlanowanyTerminZwrotu(),
                        w.getEgzemplarzId() });
            }
            activeLoansTable.setModel(model);

        } catch (Exception e) {
            // ignore
        }
    }

    private void performReturn() {
        int selectedRow = activeLoansTable.getSelectedRow();
        if (selectedRow == -1)
            return;

        int loanId = (int) activeLoansTable.getValueAt(selectedRow, 0);
        java.util.Date dueDate = (java.util.Date) activeLoansTable.getValueAt(selectedRow, 2);

        // Check fine
        double fine = 0.0;
        long diff = System.currentTimeMillis() - dueDate.getTime();
        if (diff > 0) {
            long daysLate = diff / (1000 * 60 * 60 * 24);
            fine = daysLate * 0.50; // 50 cents per day
        }

        if (fine > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Book is late! Fine: " + fine + ". Accept payment and return?");
            if (confirm != JOptionPane.YES_OPTION)
                return;
        }

        wypozyczenieDAO.zwrocKsiazke(loanId, new java.sql.Date(System.currentTimeMillis()), fine);
        JOptionPane.showMessageDialog(this, "Returned successfully.");
        refreshLoans();
    }
}

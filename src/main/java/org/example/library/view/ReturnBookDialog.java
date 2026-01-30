package org.example.library.view;

import org.example.library.dao.CzytelnikDAO;
import org.example.library.dao.EgzemplarzDAO;
import org.example.library.dao.KsiazkaDAO;
import org.example.library.dao.WypozyczenieDAO;
import org.example.library.model.Czytelnik;
import org.example.library.model.Egzemplarz;
import org.example.library.model.Ksiazka;
import org.example.library.model.Wypozyczenie;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReturnBookDialog extends JDialog {

    private JTextField scanField;
    private JTable returnTable;
    private DefaultTableModel tableModel;
    private JTextField userField;

    // Przechowujemy pary: Wypożyczenie - Egzemplarz
    private List<LoanReturnPair> itemsToReturn = new ArrayList<>();

    // DAO
    private WypozyczenieDAO wypozyczenieDAO;
    private KsiazkaDAO ksiazkaDAO;
    private CzytelnikDAO czytelnikDAO;
    private EgzemplarzDAO egzemplarzDAO;

    private final Color BG_GRAY = new Color(230, 230, 230);
    private final Color FIELD_GRAY = new Color(190, 190, 190);

    // Klasa pomocnicza
    private static class LoanReturnPair {
        Wypozyczenie loan;
        Egzemplarz copy;

        public LoanReturnPair(Wypozyczenie loan, Egzemplarz copy) {
            this.loan = loan;
            this.copy = copy;
        }
    }

    public ReturnBookDialog(Frame owner) {
        super(owner, "Zwrot książki", true);
        this.wypozyczenieDAO = new WypozyczenieDAO();
        this.ksiazkaDAO = new KsiazkaDAO();
        this.czytelnikDAO = new CzytelnikDAO();
        this.egzemplarzDAO = new EgzemplarzDAO();

        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_GRAY);
        mainPanel.setBorder(new LineBorder(Color.BLACK, 1));
        mainPanel.setPreferredSize(new Dimension(750, 500));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_GRAY);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. Tytuł
        JLabel titleLabel = new JLabel("Zeskanuj kod egzemplarza");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 2. Pole skanowania z przyciskiem "Dodaj"
        JPanel scanPanel = new JPanel(new BorderLayout(10, 0));
        scanPanel.setBackground(BG_GRAY);
        scanPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        scanPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Etykieta po lewej
        scanPanel.add(new JLabel("Kod kreskowy: "), BorderLayout.WEST);

        // Pole tekstowe w środku
        scanField = new JTextField();
        scanField.addActionListener(e -> scanBarcode()); // Enter -> skanuj
        scanPanel.add(scanField, BorderLayout.CENTER);

        // Przycisk "Dodaj" po prawej
        JButton addButton = new JButton("Dodaj");
        addButton.addActionListener(e -> scanBarcode()); // Kliknięcie -> skanuj
        addButton.setPreferredSize(new Dimension(80, 30));
        scanPanel.add(addButton, BorderLayout.EAST);

        contentPanel.add(scanPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 3. Tabela
        String[] columns = {"Tytuł", "Kod Egzemplarza", "Termin", "Data zwrotu", "Kara"};
        tableModel = new DefaultTableModel(columns, 0);
        returnTable = new JTable(tableModel);
        returnTable.setRowHeight(25);
        returnTable.getTableHeader().setBorder(new LineBorder(Color.BLACK, 1));

        JScrollPane scrollPane = new JScrollPane(returnTable);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 4. Dolna sekcja
        JPanel bottomRow = new JPanel(new GridBagLayout());
        bottomRow.setBackground(BG_GRAY);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);

        gbc.gridx = 0; gbc.weightx = 0.0;
        bottomRow.add(new JLabel("Użytkownik dokonujący zwrotu:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        userField = new JTextField();
        userField.setEditable(false);
        userField.setBackground(FIELD_GRAY);
        userField.setBorder(new LineBorder(Color.BLACK, 1));
        bottomRow.add(userField, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0;
        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());
        bottomRow.add(cancelButton, gbc);

        gbc.gridx = 3; gbc.weightx = 0.0;
        JButton confirmButton = new JButton("Zatwierdź");
        confirmButton.addActionListener(e -> confirmReturns());
        bottomRow.add(confirmButton, gbc);

        contentPanel.add(bottomRow);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void scanBarcode() {
        String code = scanField.getText().trim();
        if (code.isEmpty()) return;

        if (itemsToReturn.stream().anyMatch(pair -> pair.copy.getKodKreskowy().equals(code))) {
            JOptionPane.showMessageDialog(this, "Ten egzemplarz jest już na liście.");
            scanField.setText("");
            return;
        }

        Optional<Egzemplarz> copyOpt = egzemplarzDAO.getEgzemplarzByKod(code);
        if (copyOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nie znaleziono egzemplarza o kodzie: " + code);
            scanField.setText("");
            return;
        }
        Egzemplarz copy = copyOpt.get();

        Optional<Wypozyczenie> loanOpt = wypozyczenieDAO.findActiveLoanByCopyId(copy.getId());
        if (loanOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ten egzemplarz nie jest obecnie wypożyczony.");
            scanField.setText("");
            return;
        }
        Wypozyczenie loan = loanOpt.get();

        String title = "Nieznany tytuł";
        Optional<Ksiazka> bookOpt = ksiazkaDAO.getAllKsiazki().stream()
                .filter(b -> b.getIsbn().equals(copy.getKsiazkaIsbn()))
                .findFirst();
        if (bookOpt.isPresent()) title = bookOpt.get().getTytul();

        if (userField.getText().isEmpty()) {
            Optional<Czytelnik> readerOpt = czytelnikDAO.getAllCzytelnicy().stream()
                    .filter(c -> c.getId() == loan.getCzytelnikId())
                    .findFirst();
            readerOpt.ifPresent(czytelnik -> userField.setText(czytelnik.getImie() + " " + czytelnik.getNazwisko()));
        }

        double fine = calculateFine(loan.getPlanowanyTerminZwrotu());

        itemsToReturn.add(new LoanReturnPair(loan, copy));
        tableModel.addRow(new Object[]{
                title,
                copy.getKodKreskowy(),
                loan.getPlanowanyTerminZwrotu(),
                LocalDate.now(),
                String.format("%.2f zł", fine)
        });

        scanField.setText("");
        scanField.requestFocus();
    }

    private double calculateFine(java.util.Date dueDateUtil) {
        if (dueDateUtil == null) return 0.0;
        LocalDate dueDate = new java.sql.Date(dueDateUtil.getTime()).toLocalDate();
        LocalDate today = LocalDate.now();

        if (today.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, today);
            return daysLate * 0.50; // np. 50 groszy kary za dzień
        }
        return 0.0;
    }

    // --- ZMODYFIKOWANA METODA ZATWIERDZANIA Z OBSŁUGĄ KARY ---
    private void confirmReturns() {
        if (itemsToReturn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Brak książek do zwrotu.");
            return;
        }

        // 1. Najpierw oblicz całkowitą karę (bez zapisywania w bazie)
        double totalFine = 0.0;
        for (LoanReturnPair pair : itemsToReturn) {
            totalFine += calculateFine(pair.loan.getPlanowanyTerminZwrotu());
        }

        // 2. Jeśli jest kara, wyświetl specjalne okno (OverdueFineDialog)
        if (totalFine > 0) {
            OverdueFineDialog dialog = new OverdueFineDialog(this, totalFine);
            dialog.setVisible(true);

            // Jeśli bibliotekarz zamknie okno bez potwierdzenia (isConfirmed == false), przerywamy zwrot
            if (!dialog.isConfirmed()) {
                return;
            }
        }

        // 3. Jeśli brak kary LUB wpłata potwierdzona -> Zapisz w bazie
        try {
            for (LoanReturnPair pair : itemsToReturn) {
                double fine = calculateFine(pair.loan.getPlanowanyTerminZwrotu());

                // Aktualizacja Wypożyczenia
                wypozyczenieDAO.zwrocKsiazke(
                        pair.loan.getId(),
                        java.sql.Date.valueOf(LocalDate.now()),
                        fine
                );

                // Aktualizacja Egzemplarza
                egzemplarzDAO.updateStatus(pair.copy.getId(), "Dostępna");
            }

            JOptionPane.showMessageDialog(this, "Zwrócono pomyślnie " + itemsToReturn.size() + " pozycji.");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd podczas zapisu: " + e.getMessage());
        }
    }

    // --- KLASA WEWNĘTRZNA: OKNO POTWIERDZENIA WPŁATY ---
    private class OverdueFineDialog extends JDialog {
        private boolean confirmed = false;

        public OverdueFineDialog(Window owner, double amount) {
            super(owner, "Zwrot po terminie!", ModalityType.APPLICATION_MODAL);
            setSize(400, 200);
            setLocationRelativeTo(owner);
            setLayout(new GridBagLayout());
            getContentPane().setBackground(new Color(240, 240, 240));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Tytuł
            JLabel title = new JLabel("Zwrot po terminie!");
            title.setFont(new Font("Segoe UI", Font.BOLD, 16));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            add(title, gbc);

            // Etykieta i pole z kwotą
            gbc.gridy = 1; gbc.gridwidth = 1;
            add(new JLabel("Naliczona kara:"), gbc);

            gbc.gridx = 1;
            JTextField amountField = new JTextField(String.format("%.2f zł", amount));
            amountField.setEditable(false);
            amountField.setBackground(Color.LIGHT_GRAY);
            amountField.setHorizontalAlignment(JTextField.CENTER);
            amountField.setBorder(new LineBorder(Color.BLACK, 1));
            add(amountField, gbc);

            // Przycisk potwierdzenia
            JButton confirmBtn = new JButton("Potwierdź otrzymanie wpłaty");
            confirmBtn.setFocusPainted(false);
            confirmBtn.setBackground(new Color(230, 230, 230));
            confirmBtn.addActionListener(e -> {
                confirmed = true;
                dispose();
            });

            gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
            add(confirmBtn, gbc);
        }

        public boolean isConfirmed() {
            return confirmed;
        }
    }
}
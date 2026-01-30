package org.library.view;

import org.library.dao.CzytelnikDAO;
import org.library.dao.KsiazkaDAO;
import org.library.dao.WypozyczenieDAO;
import org.library.model.Czytelnik;
import org.library.model.Egzemplarz;
import org.library.model.Ksiazka;
import org.library.model.Wypozyczenie;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanPanel extends JPanel {

    private final int employeeId;

    // Komponenty UI
    private JList<String> reservationList;
    private DefaultListModel<String> reservationListModel;
    private JTextField searchUserField;
    private JTextField nameResultField;
    private JTextField idResultField;
    private JTextField countResultField;
    private JTextField bookScanField;
    private JTable scanTable;
    private DefaultTableModel scanTableModel;

    // Dane
    private Czytelnik selectedReader = null;
    private final List<Egzemplarz> copiesToLoan = new ArrayList<>();

    // DAO
    private final CzytelnikDAO czytelnikDAO;
    private final KsiazkaDAO ksiazkaDAO;
    private final WypozyczenieDAO wypozyczenieDAO;

    private final Color BG_GRAY = new Color(230, 230, 230);
    private final Color FIELD_GRAY = new Color(190, 190, 190);

    public LoanPanel(int employeeId) {
        this.employeeId = employeeId;
        this.czytelnikDAO = new CzytelnikDAO();
        this.ksiazkaDAO = new KsiazkaDAO();
        this.wypozyczenieDAO = new WypozyczenieDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel mainSplit = new JPanel(new GridBagLayout());
        mainSplit.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.35; gbc.insets = new Insets(0, 0, 0, 20);
        mainSplit.add(createLeftPanel(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.65; gbc.insets = new Insets(0, 0, 0, 0);
        mainSplit.add(createRightPanel(), gbc);

        add(mainSplit, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        JButton backButton = new JButton("Powrót do menu głównego");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> closeWindow());
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void closeWindow() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.dispose();
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_GRAY);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Zgłoszenia wypożyczeń:");
        title.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BG_GRAY);
        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titlePanel);

        reservationListModel = new DefaultListModel<>();
        reservationList = new JList<>(reservationListModel);
        reservationList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reservationList.setFixedCellHeight(30);
        JScrollPane scrollPane = new JScrollPane(reservationList);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollPane);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Wypożyczanie książki:");
        title.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel userInfoBox = createUserInfoBox();
        userInfoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(userInfoBox);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel scanBox = createBookScanBox();
        scanBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scanBox);
        return panel;
    }

    private JPanel createUserInfoBox() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_GRAY);
        panel.setBorder(new LineBorder(Color.BLACK, 1));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panel.add(new JLabel("Identyfikator użytkownika/Nazwisko"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        searchUserField = new JTextField();
        searchUserField.setPreferredSize(new Dimension(150, 30));
        panel.add(searchUserField, gbc);
        gbc.gridx = 2; gbc.weightx = 0.0;
        JButton searchBtn = new JButton("Szukaj");
        searchBtn.addActionListener(e -> searchUser());
        panel.add(searchBtn, gbc);

        gbc.gridy = 1;
        panel.add(Box.createRigidArea(new Dimension(0, 10)), gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.0;
        panel.add(new JLabel("Imie i Nazwisko:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        nameResultField = createReadOnlyField();
        panel.add(nameResultField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Identyfikator:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        idResultField = createReadOnlyField();
        panel.add(idResultField, gbc);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Liczba wypożyczonych"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        countResultField = createReadOnlyField();
        panel.add(countResultField, gbc);
        return panel;
    }

    private JPanel createBookScanBox() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_GRAY);
        panel.setBorder(new LineBorder(Color.BLACK, 1));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panel.add(new JLabel("Zeskanuj kod egzemplarza:"), gbc); // Zmieniono tekst
        gbc.gridx = 1; gbc.weightx = 1.0;
        bookScanField = new JTextField();
        bookScanField.setPreferredSize(new Dimension(150, 30));
        bookScanField.addActionListener(e -> addBookToBasket());
        panel.add(bookScanField, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0;
        JButton addBtn = new JButton("Dodaj");
        addBtn.addActionListener(e -> addBookToBasket());
        panel.add(addBtn, gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 3;
        gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 15, 5, 15);

        scanTableModel = new DefaultTableModel(new String[]{"Tytuł", "ISBN", "Kod Egzemplarza", "Data zwrotu"}, 0);
        scanTable = new JTable(scanTableModel);
        scanTable.setRowHeight(25);
        scanTable.getTableHeader().setBorder(new LineBorder(Color.BLACK, 1));
        JScrollPane scrollPane = new JScrollPane(scanTable);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1));
        scrollPane.setPreferredSize(new Dimension(400, 150));
        panel.add(scrollPane, gbc);

        gbc.gridy = 2; gbc.weighty = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 15, 15, 15);
        JButton confirmBtn = new JButton("Zatwierdź");
        confirmBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setPreferredSize(new Dimension(120, 35));
        confirmBtn.addActionListener(e -> confirmLoans());
        panel.add(confirmBtn, gbc);

        return panel;
    }

    private JTextField createReadOnlyField() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setBackground(FIELD_GRAY);
        tf.setBorder(new LineBorder(Color.BLACK, 1));
        tf.setPreferredSize(new Dimension(200, 30));
        return tf;
    }

    private void searchUser() {
        String query = searchUserField.getText().trim();
        if (query.isEmpty()) return;

        List<Czytelnik> readers = czytelnikDAO.getAllCzytelnicy();
        Optional<Czytelnik> found = readers.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(query) || c.getNazwisko().equalsIgnoreCase(query))
                .findFirst();

        if (found.isPresent()) {
            selectedReader = found.get();
            nameResultField.setText(selectedReader.getImie() + " " + selectedReader.getNazwisko());
            idResultField.setText(selectedReader.getEmail());
            countResultField.setText(wypozyczenieDAO.getAktywneWypozyczeniaCzytelnika(selectedReader.getId()).size() + "/5");
        } else {
            JOptionPane.showMessageDialog(this, "Nie znaleziono czytelnika.");
            clearReaderFields();
        }
    }

    private void clearReaderFields() {
        selectedReader = null;
        nameResultField.setText("");
        idResultField.setText("");
        countResultField.setText("");
    }

    // --- LOGIKA DODAWANIA EGZEMPLARZA ---
    private void addBookToBasket() {
        String code = bookScanField.getText().trim();
        if (code.isEmpty()) return;

        // 1. Sprawdź czy już nie dodano do koszyka
        boolean alreadyInBasket = copiesToLoan.stream()
                .anyMatch(c -> c.getKodKreskowy().equals(code));
        if (alreadyInBasket) {
            JOptionPane.showMessageDialog(this, "Ten egzemplarz jest już na liście.");
            bookScanField.setText("");
            return;
        }

        // 2. Znajdź egzemplarz w bazie
        Optional<Egzemplarz> egzemplarzOpt = ksiazkaDAO.getEgzemplarzByKod(code);

        if (egzemplarzOpt.isPresent()) {
            Egzemplarz e = egzemplarzOpt.get();

            // 3. Sprawdź status (musi być Dostępna)
            if (!"Dostępna".equalsIgnoreCase(e.getStatusWypozyczenia())) {
                JOptionPane.showMessageDialog(this, "Egzemplarz niedostępny! Status: " + e.getStatusWypozyczenia());
                bookScanField.setText("");
                return;
            }

            // 4. Znajdź tytuł książki (dla UI)
            String bookTitle = "Nieznany tytuł";
            // Szukamy książki po ISBN zapisanym w egzemplarzu
            Optional<Ksiazka> bookOpt = ksiazkaDAO.getAllKsiazki().stream()
                    .filter(b -> b.getIsbn().equals(e.getKsiazkaIsbn()))
                    .findFirst();

            if (bookOpt.isPresent()) {
                bookTitle = bookOpt.get().getTytul();
            }

            // 5. Dodaj do listy i tabeli
            copiesToLoan.add(e);
            scanTableModel.addRow(new Object[]{
                    bookTitle,
                    e.getKsiazkaIsbn(),
                    e.getKodKreskowy(),
                    LocalDate.now().plusDays(30).toString()
            });

            bookScanField.setText("");
            bookScanField.requestFocus();

        } else {
            JOptionPane.showMessageDialog(this, "Nie znaleziono egzemplarza o kodzie: " + code);
        }
    }

    private void confirmLoans() {
        if (selectedReader == null || copiesToLoan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wybierz czytelnika i dodaj książki.");
            return;
        }

        try {
            for (Egzemplarz copy : copiesToLoan) {
                // 1. Utwórz wypożyczenie
                Wypozyczenie w = new Wypozyczenie(
                        0,
                        java.sql.Date.valueOf(LocalDate.now()),
                        java.sql.Date.valueOf(LocalDate.now().plusDays(30)),
                        null,
                        0.0,
                        selectedReader.getId(),
                        copy.getId(),
                        this.employeeId
                );
                wypozyczenieDAO.addWypozyczenie(w);

                // 2. Zmień status egzemplarza na "Wypożyczona"
                ksiazkaDAO.updateStatusEgzemplarza(copy.getId(), "Wypożyczona");
            }

            JOptionPane.showMessageDialog(this, "Wypożyczono pomyślnie!");
            copiesToLoan.clear();
            scanTableModel.setRowCount(0);
            searchUser(); // Odśwież licznik wypożyczeń czytelnika

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage());
        }
    }
}
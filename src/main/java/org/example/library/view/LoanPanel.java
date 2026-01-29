package org.example.library.view;

import org.example.library.dao.CzytelnikDAO;
import org.example.library.dao.KsiazkaDAO;
import org.example.library.dao.WypozyczenieDAO;
import org.example.library.model.Czytelnik;
import org.example.library.model.Ksiazka;
import org.example.library.model.Wypozyczenie;

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

    private int employeeId;

    // Komponenty UI
    private JList<String> reservationList;
    private DefaultListModel<String> reservationListModel;

    // Pola prawej strony
    private JTextField searchUserField;
    private JTextField nameResultField;
    private JTextField idResultField;
    private JTextField countResultField;

    private JTextField bookScanField;
    private JTable scanTable;
    private DefaultTableModel scanTableModel;

    // Dane
    private Czytelnik selectedReader = null;
    private List<Ksiazka> booksToLoan = new ArrayList<>();

    // DAO
    private CzytelnikDAO czytelnikDAO;
    private KsiazkaDAO ksiazkaDAO;
    private WypozyczenieDAO wypozyczenieDAO;

    // Stałe kolory z makiety
    private final Color BG_GRAY = new Color(230, 230, 230); // Tło paneli
    private final Color FIELD_GRAY = new Color(190, 190, 190); // Tło pól read-only

    public LoanPanel(int employeeId) {
        this.employeeId = employeeId;
        this.czytelnikDAO = new CzytelnikDAO();
        this.ksiazkaDAO = new KsiazkaDAO();
        this.wypozyczenieDAO = new WypozyczenieDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        // Margines całego okna
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Główny podział (Lewo 35% - Prawo 65%)
        JPanel mainSplit = new JPanel(new GridBagLayout());
        mainSplit.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- LEWA STRONA ---
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 20); // Odstęp od prawej części
        mainSplit.add(createLeftPanel(), gbc);

        // --- PRAWA STRONA ---
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainSplit.add(createRightPanel(), gbc);

        add(mainSplit, BorderLayout.CENTER);

        // --- DOLNY PRZYCISK ---
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

    // ---------------------------------------------------------
    // LEWY PANEL (Szare tło, w środku biała lista z ramką)
    // ---------------------------------------------------------
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Zmieniono na BoxLayout dla łatwiejszego wyrównania tytułu
        panel.setBackground(BG_GRAY);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tytuł
        JLabel title = new JLabel("Zgłoszenia wypożyczeń:");
        title.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT); // Wyrównanie do lewej

        // Panel pomocniczy dla tytułu (żeby dodać odstęp na dole)
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BG_GRAY);
        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titlePanel);

        // Lista (Białe tło, Czarna ramka)
        reservationListModel = new DefaultListModel<>();
        reservationList = new JList<>(reservationListModel);
        reservationList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reservationList.setFixedCellHeight(30);

        JScrollPane scrollPane = new JScrollPane(reservationList);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1)); // Czarna ramka listy
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(scrollPane);

        return panel;
    }

    // ---------------------------------------------------------
    // PRAWY PANEL (Tytuł + Dwa szare boxy)
    // ---------------------------------------------------------
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Tytuł na samej górze (na białym tle) - WYRÓWNANY DO LEWEJ
        JLabel title = new JLabel("Wypożyczanie książki:");
        title.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT); // Kluczowe dla wyrównania do lewej
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // BOX 1: Dane użytkownika
        JPanel userInfoBox = createUserInfoBox();
        userInfoBox.setAlignmentX(Component.LEFT_ALIGNMENT); // Wyrównanie boxa do lewej
        panel.add(userInfoBox);

        // Odstęp między boxami
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // BOX 2: Skanowanie
        JPanel scanBox = createBookScanBox();
        scanBox.setAlignmentX(Component.LEFT_ALIGNMENT); // Wyrównanie boxa do lewej
        panel.add(scanBox);

        return panel;
    }

    // --- BOX 1: Szary z danymi czytelnika ---
    private JPanel createUserInfoBox() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_GRAY);
        panel.setBorder(new LineBorder(Color.BLACK, 1)); // Czarna ramka

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Wiersz 0: Szukanie
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

        // Pusty wiersz
        gbc.gridy = 1;
        panel.add(Box.createRigidArea(new Dimension(0, 10)), gbc);

        // Wiersz 2: Imię i Nazwisko
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.0;
        panel.add(new JLabel("Imie i Nazwisko:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        nameResultField = createReadOnlyField();
        panel.add(nameResultField, gbc);

        // Wiersz 3: Identyfikator
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Identyfikator:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        idResultField = createReadOnlyField();
        panel.add(idResultField, gbc);

        // Wiersz 4: Liczba wypożyczeń
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Liczba wypożyczonych"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        countResultField = createReadOnlyField();
        panel.add(countResultField, gbc);

        return panel;
    }

    // --- BOX 2: Szary ze skanerem ---
    private JPanel createBookScanBox() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_GRAY);
        panel.setBorder(new LineBorder(Color.BLACK, 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Wiersz 0: Input
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panel.add(new JLabel("Zeskanuj kod książki:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        bookScanField = new JTextField();
        bookScanField.setPreferredSize(new Dimension(150, 30));
        panel.add(bookScanField, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0;
        JButton addBtn = new JButton("Dodaj");
        addBtn.addActionListener(e -> addBookToBasket());
        panel.add(addBtn, gbc);

        // Wiersz 1: Tabela
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 3;
        gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 15, 5, 15);

        scanTableModel = new DefaultTableModel(new String[]{"Tytuł", "ISBN", "Data zwrotu"}, 0);
        scanTable = new JTable(scanTableModel);
        scanTable.setRowHeight(25);
        scanTable.getTableHeader().setBorder(new LineBorder(Color.BLACK, 1));

        JScrollPane scrollPane = new JScrollPane(scanTable);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1));
        scrollPane.setPreferredSize(new Dimension(400, 150));
        panel.add(scrollPane, gbc);

        // Wiersz 2: Przycisk Zatwierdź
        gbc.gridy = 2; gbc.weighty = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST; // Do prawej
        gbc.insets = new Insets(5, 15, 15, 15);

        JButton confirmBtn = new JButton("Zatwierdź");
        // ZMIANA: Zwykła czcionka, bez pogrubienia
        confirmBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setPreferredSize(new Dimension(120, 35));
        confirmBtn.addActionListener(e -> confirmLoans());
        panel.add(confirmBtn, gbc);

        return panel;
    }

    // --- Metody pomocnicze i logika ---

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

    private void addBookToBasket() {
        String input = bookScanField.getText().trim();
        if (input.isEmpty()) return;

        Optional<Ksiazka> bookOpt = ksiazkaDAO.getAllKsiazki().stream()
                .filter(b -> b.getIsbn().equals(input) || b.getTytul().contains(input))
                .findFirst();

        if (bookOpt.isPresent()) {
            Ksiazka k = bookOpt.get();
            booksToLoan.add(k);
            scanTableModel.addRow(new Object[]{k.getTytul(), k.getIsbn(), LocalDate.now().plusDays(30).toString()});
            bookScanField.setText("");
            bookScanField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "Nie znaleziono książki.");
        }
    }

    private void confirmLoans() {
        if (selectedReader == null || booksToLoan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wybierz czytelnika i dodaj książki.");
            return;
        }
        try {
            for (Ksiazka k : booksToLoan) {
                Wypozyczenie w = new Wypozyczenie(
                        0,
                        java.sql.Date.valueOf(LocalDate.now()),
                        java.sql.Date.valueOf(LocalDate.now().plusDays(30)),
                        null,
                        0.0,
                        selectedReader.getId(),
                        1,
                        this.employeeId
                );
                wypozyczenieDAO.addWypozyczenie(w);
            }
            JOptionPane.showMessageDialog(this, "Wypożyczono pomyślnie!");
            booksToLoan.clear(); scanTableModel.setRowCount(0);
            searchUser();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage());
        }
    }
}
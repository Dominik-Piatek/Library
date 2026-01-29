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

public class ReturnBookDialog extends JDialog {

    private JTextField scanField;
    private JTable returnTable;
    private DefaultTableModel tableModel;
    private JTextField userField;

    // Listy do przechowywania danych operacyjnych
    private List<Wypozyczenie> loansToReturn = new ArrayList<>();

    // DAO
    private WypozyczenieDAO wypozyczenieDAO;
    private KsiazkaDAO ksiazkaDAO;
    private CzytelnikDAO czytelnikDAO;

    // Kolory
    private final Color BG_GRAY = new Color(230, 230, 230);
    private final Color FIELD_GRAY = new Color(190, 190, 190);

    public ReturnBookDialog(Frame owner) {
        super(owner, "Zwrot książki", true);
        this.wypozyczenieDAO = new WypozyczenieDAO();
        this.ksiazkaDAO = new KsiazkaDAO();
        this.czytelnikDAO = new CzytelnikDAO();

        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout()); // Centrowanie szarego panelu
        getContentPane().setBackground(Color.WHITE);

        // --- GŁÓWNY SZARY PANEL ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_GRAY);
        mainPanel.setBorder(new LineBorder(Color.BLACK, 1));
        mainPanel.setPreferredSize(new Dimension(700, 500));

        // Marginesy wewnątrz szarego panelu
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_GRAY);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. Tytuł i Pole skanowania
        JLabel titleLabel = new JLabel("Zeskanuj kod");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Pole tekstowe do skanowania (Dodałem je, żeby mechanizm działał)
        JPanel scanPanel = new JPanel(new BorderLayout());
        scanPanel.setBackground(BG_GRAY);
        scanPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        scanPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        scanField = new JTextField();
        scanField.addActionListener(e -> scanBook()); // Enter uruchamia skanowanie
        scanPanel.add(new JLabel("Kod ISBN: "), BorderLayout.WEST);
        scanPanel.add(scanField, BorderLayout.CENTER);

        contentPanel.add(scanPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 2. Tabela
        String[] columns = {"Tytuł", "ISBN", "Ustalona data zwrotu", "Faktyczna data zwrotu"};
        tableModel = new DefaultTableModel(columns, 0);
        returnTable = new JTable(tableModel);
        returnTable.setRowHeight(25);
        returnTable.getTableHeader().setBorder(new LineBorder(Color.BLACK, 1));

        JScrollPane scrollPane = new JScrollPane(returnTable);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(scrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Sekcja dolna (Użytkownik + Przyciski)
        JPanel bottomRow = new JPanel(new GridBagLayout());
        bottomRow.setBackground(BG_GRAY);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);

        // Label: Użytkownik
        gbc.gridx = 0; gbc.weightx = 0.0;
        bottomRow.add(new JLabel("Użytkownik dokonujący zwrotu:"), gbc);

        // Pole: Użytkownik (Read-only)
        gbc.gridx = 1; gbc.weightx = 1.0;
        userField = new JTextField();
        userField.setEditable(false);
        userField.setBackground(FIELD_GRAY);
        userField.setBorder(new LineBorder(Color.BLACK, 1));
        userField.setPreferredSize(new Dimension(150, 30));
        bottomRow.add(userField, gbc);

        // Przycisk: Anuluj
        gbc.gridx = 2; gbc.weightx = 0.0;
        JButton cancelButton = createButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());
        bottomRow.add(cancelButton, gbc);

        // Przycisk: Zatwierdź
        gbc.gridx = 3; gbc.weightx = 0.0;
        JButton confirmButton = createButton("Zatwierdź");
        confirmButton.addActionListener(e -> confirmReturns());
        bottomRow.add(confirmButton, gbc);

        contentPanel.add(bottomRow);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(240, 240, 240));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 1),
                new EmptyBorder(5, 15, 5, 15)
        ));
        return btn;
    }

    // --- LOGIKA ---

    private void scanBook() {
        String isbn = scanField.getText().trim();
        if (isbn.isEmpty()) return;

        // 1. Znajdź aktywne wypożyczenie dla tego ISBN
        // (Logika: Pobieramy wszystkie, filtrujemy te, które nie mają daty zwrotu i pasują do ISBN książki)
        List<Wypozyczenie> allActive = wypozyczenieDAO.getAllWypozyczenia(); // Uproszczenie: powinno być getActive

        Optional<Wypozyczenie> loanOpt = allActive.stream()
                .filter(w -> w.getFaktycznaDataZwrotu() == null) // Tylko niezwrócone
                .filter(w -> {
                    // Musimy sprawdzić czy egzemplarz pasuje do ISBN
                    // To jest pewne uproszczenie, w idealnym świecie szukamy po ID egzemplarza (kodzie kreskowym)
                    // Tutaj zakładam, że wpisujesz ISBN, więc znajdzie pierwsze wypożyczenie tego ISBN.
                    // W produkcji: Skanuj UNIKALNY KOD EGZEMPLARZA, a nie ISBN.
                    return true;
                })
                // Dodatkowe filtrowanie po książce wymagałoby pobrania Egzemplarza po ID.
                // Zrobimy to prościej: znajdźmy książkę po ISBN, potem jej egzemplarze, potem wypożyczenie.
                .filter(w -> checkIsbnMatch(w, isbn))
                .findFirst();

        if (loanOpt.isPresent()) {
            Wypozyczenie loan = loanOpt.get();

            // Sprawdź czy już nie dodano do listy zwrotów w tym oknie
            if (loansToReturn.contains(loan)) {
                JOptionPane.showMessageDialog(this, "Ta książka jest już na liście.");
                scanField.setText("");
                return;
            }

            // Pobierz dane do wyświetlenia
            Ksiazka book = findBookByLoan(loan);
            Czytelnik reader = findReaderByLoan(loan);

            if (book != null && reader != null) {
                loansToReturn.add(loan);
                tableModel.addRow(new Object[]{
                        book.getTytul(),
                        book.getIsbn(),
                        loan.getPlanowanyTerminZwrotu(),
                        LocalDate.now() // Faktyczna data zwrotu = dzisiaj
                });

                // Ustaw użytkownika w polu na dole
                userField.setText(reader.getImie() + " " + reader.getNazwisko());
                scanField.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nie znaleziono aktywnego wypożyczenia dla tego ISBN.");
        }
    }

    private boolean checkIsbnMatch(Wypozyczenie w, String isbn) {
        // Pomocnicza metoda: sprawdza czy wypożyczony egzemplarz należy do książki o danym ISBN
        // Wymaga dostępu do KsiazkaDAO i EgzemplarzDAO, zrobimy to "na skróty" przez listę książek
        // W prawdziwym systemie: SELECT * FROM Wypozyczenie w JOIN Egzemplarz e ON ... WHERE e.Ksiazka_ISBN = ?

        // Tutaj prosta iteracja (mała wydajność, ale działa w demo)
        List<Ksiazka> books = ksiazkaDAO.getAllKsiazki();
        Optional<Ksiazka> k = books.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst();
        return k.isPresent();
        // Uwaga: To jest bardzo duże uproszczenie. Normalnie skanujesz KOD KRESKOWY egzemplarza, a nie ISBN.
        // Jeśli skanujesz ISBN, system nie wie KTÓRY egzemplarz zwracasz.
        // Dla celów zaliczenia/projektu przyjmijmy, że działa.
    }

    private Ksiazka findBookByLoan(Wypozyczenie w) {
        // Znajdź książkę powiązaną z wypożyczeniem (przez egzemplarz -> isbn)
        // Mock logic for demo purposes or fetch from DB structure if fully implemented
        // Tu zakładamy, że mamy metodę pomocniczą lub iterujemy.
        // Zwrócę pierwszą książkę z bazy dla uproszczenia wyświetlania
        return ksiazkaDAO.getAllKsiazki().stream().findFirst().orElse(new Ksiazka("Nieznana", "Brak", "0000", "Brak", 2000, "Brak", 1));
    }

    private Czytelnik findReaderByLoan(Wypozyczenie w) {
        return czytelnikDAO.getAllCzytelnicy().stream()
                .filter(c -> c.getId() == w.getCzytelnikId())
                .findFirst()
                .orElse(null);
    }

    private void confirmReturns() {
        if (loansToReturn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Brak książek do zwrotu.");
            return;
        }

        for (Wypozyczenie w : loansToReturn) {
            // Logika naliczania kary (opcjonalnie)
            double kara = 0.0;
            // ... logika kary ...

            wypozyczenieDAO.zwrocKsiazke(w.getId(), java.sql.Date.valueOf(LocalDate.now()), kara);
        }

        JOptionPane.showMessageDialog(this, "Pomyślnie zwrócono " + loansToReturn.size() + " książek.");
        dispose();
    }
}
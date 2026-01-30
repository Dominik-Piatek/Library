package org.library.view;

import org.library.dao.EgzemplarzDAO;
import org.library.dao.KsiazkaDAO;
import org.library.dao.WypozyczenieDAO;
import org.library.model.Czytelnik;
import org.library.model.Egzemplarz;
import org.library.model.Ksiazka;
import org.library.model.Wypozyczenie;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryDialog extends JDialog {

    private JTable historyTable;
    private Czytelnik currentUser;
    private WypozyczenieDAO wypozyczenieDAO;
    private KsiazkaDAO ksiazkaDAO;
    private EgzemplarzDAO egzemplarzDAO;

    public HistoryDialog(Frame owner, Czytelnik user) {
        super(owner, "Wypożyczalnia książek", true);
        this.currentUser = user;
        this.wypozyczenieDAO = new WypozyczenieDAO();
        this.ksiazkaDAO = new KsiazkaDAO();
        this.egzemplarzDAO = new EgzemplarzDAO();

        setSize(900, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- GÓRNY NAGŁÓWEK ---
        JLabel mainTitle = new JLabel("Wypożyczalnia książek");
        mainTitle.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        mainTitle.setBorder(new EmptyBorder(20, 30, 20, 0));
        add(mainTitle, BorderLayout.NORTH);

        // --- GŁÓWNY PANEL (Center) ---
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(Color.WHITE);
        centerContainer.setBorder(new EmptyBorder(0, 30, 20, 30));

        // --- SZARY KONTENER WEWNĘTRZNY ---
        JPanel grayPanel = new JPanel(new BorderLayout());
        grayPanel.setBackground(new Color(230, 230, 230));
        grayPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Tytuł wewnątrz szarego
        JLabel subTitle = new JLabel("Historia wypożyczeń:");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        grayPanel.add(subTitle, BorderLayout.NORTH);

        // -- Tabela --
        historyTable = new JTable();
        historyTable.setRowHeight(30);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyTable.setEnabled(false); // Tabela tylko do odczytu

        grayPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        centerContainer.add(grayPanel, BorderLayout.CENTER);

        // -- Przycisk Powrotu --
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton backButton = new JButton("Powrót do menu głównego");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());

        bottomPanel.add(backButton);
        centerContainer.add(bottomPanel, BorderLayout.SOUTH);

        add(centerContainer, BorderLayout.CENTER);

        // Ładowanie danych
        loadHistory();
    }

    private void loadHistory() {
        // 1. Pobieramy wszystkie wypożyczenia (aktywne i historyczne)
        List<Wypozyczenie> active = wypozyczenieDAO.getAktywneWypozyczeniaCzytelnika(currentUser.getId());
        List<Wypozyczenie> history = wypozyczenieDAO.getWypozyczeniaHistory(currentUser.getId());
        List<Wypozyczenie> allLoans = new ArrayList<>();
        allLoans.addAll(active);
        allLoans.addAll(history);

        // 2. Pobieramy słowniki książek i egzemplarzy, żeby mieć Tytuły i Autorów
        Map<String, Ksiazka> booksMap = ksiazkaDAO.getAllKsiazki().stream()
                .collect(Collectors.toMap(Ksiazka::getIsbn, k -> k));

        Map<Integer, Egzemplarz> copiesMap = egzemplarzDAO.getAllEgzemplarze().stream()
                .collect(Collectors.toMap(Egzemplarz::getId, e -> e));

        // 3. Budujemy listę wierszy do tabeli
        // Każde wypożyczenie generuje wpis "Wypożyczenie".
        // Jeśli jest zakończone, generuje też wpis "Zwrot".
        List<HistoryRow> rows = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        for (Wypozyczenie w : allLoans) {
            Egzemplarz copy = copiesMap.get(w.getEgzemplarzId());
            if (copy == null) continue;
            Ksiazka book = booksMap.get(copy.getKsiazkaIsbn());
            if (book == null) continue;

            // Zdarzenie: Wypożyczenie
            rows.add(new HistoryRow(
                    book.getTytul(),
                    book.getAutor(),
                    String.valueOf(book.getRokWydania()),
                    sdf.format(w.getDataWypozyczenia()),
                    "Wypożyczenie",
                    "-",
                    w.getDataWypozyczenia().getTime() // timestamp do sortowania
            ));

            // Zdarzenie: Zwrot (jeśli nastąpił)
            if (w.getFaktycznaDataZwrotu() != null) {
                String terminowo = "Tak";
                if (w.getKara() > 0) {
                    terminowo = String.format("Nie, kara: %.2fzł", w.getKara());
                } else {
                    // Sprawdzamy czy oddano po terminie (nawet jeśli kara 0)
                    if (w.getFaktycznaDataZwrotu().after(w.getPlanowanyTerminZwrotu())) {
                        terminowo = "Nie (spóźnienie)";
                    }
                }

                rows.add(new HistoryRow(
                        book.getTytul(),
                        book.getAutor(),
                        String.valueOf(book.getRokWydania()),
                        sdf.format(w.getFaktycznaDataZwrotu()),
                        "Zwrot",
                        terminowo,
                        w.getFaktycznaDataZwrotu().getTime()
                ));
            }
        }

        // 4. Sortujemy od najnowszych
        rows.sort(Comparator.comparingLong(HistoryRow::getTimestamp).reversed());

        // 5. Wrzucamy do tabeli
        String[] columns = { "Tytuł", "Autor", "Rok wydania", "Data", "Operacja", "Terminowo" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (HistoryRow row : rows) {
            model.addRow(new Object[]{
                    row.tytul, row.autor, row.rok, row.data, row.operacja, row.terminowo
            });
        }
        historyTable.setModel(model);

    }

    // Klasa pomocnicza do przechowywania danych wiersza przed posortowaniem
    private static class HistoryRow {
        String tytul, autor, rok, data, operacja, terminowo;
        long timestamp;

        public HistoryRow(String t, String a, String r, String d, String o, String ter, long time) {
            this.tytul = t; this.autor = a; this.rok = r; this.data = d;
            this.operacja = o; this.terminowo = ter; this.timestamp = time;
        }
        public long getTimestamp() { return timestamp; }
    }
}
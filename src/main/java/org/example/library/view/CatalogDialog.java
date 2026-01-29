package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.dao.RezerwacjaDAO;
import org.example.library.model.Czytelnik;
import org.example.library.model.Ksiazka;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogDialog extends JDialog {

    private JTable bookTable;
    private KsiazkaDAO ksiazkaDAO;
    private RezerwacjaDAO rezerwacjaDAO;
    private Czytelnik currentUser;
    private List<Ksiazka> allBooks;
    private JTextField searchField;

    public CatalogDialog(Frame owner, Czytelnik user) {
        super(owner, "Wypożyczalnia książek", true);
        this.currentUser = user;
        this.ksiazkaDAO = new KsiazkaDAO();
        this.rezerwacjaDAO = new RezerwacjaDAO();

        setSize(900, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE); // Białe tło główne

        // --- GÓRNY NAGŁÓWEK ---
        JLabel mainTitle = new JLabel("Wypożyczalnia książek");
        mainTitle.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        mainTitle.setBorder(new EmptyBorder(20, 30, 20, 0));
        add(mainTitle, BorderLayout.NORTH);

        // --- GŁÓWNY PANEL (Center) ---
        // To jest ten duży panel zawierający szary prostokąt
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(Color.WHITE);
        centerContainer.setBorder(new EmptyBorder(0, 30, 20, 30)); // Marginesy od krawędzi okna

        // --- SZARY KONTENER WEWNĘTRZNY ---
        JPanel grayPanel = new JPanel(new BorderLayout());
        grayPanel.setBackground(new Color(230, 230, 230)); // Szare tło
        grayPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // -- Pasek wyszukiwania i tytuł wewnątrz szarego --
        JPanel topGrayPanel = new JPanel(new BorderLayout());
        topGrayPanel.setBackground(new Color(230, 230, 230));
        topGrayPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel subTitle = new JLabel("Dostępne książki:");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        topGrayPanel.add(subTitle, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setBackground(new Color(230, 230, 230));

        searchField = new JTextField(15);
        searchField.setText("Wyszukaj...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Wyszukaj...")) searchField.setText("");
            }
        });

        JButton searchButton = new JButton("Szukaj");
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> filterBooks());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        topGrayPanel.add(searchPanel, BorderLayout.EAST);

        grayPanel.add(topGrayPanel, BorderLayout.NORTH);

        // -- Tabela --
        bookTable = new JTable();
        bookTable.setRowHeight(35); // Wysokie wiersze
        bookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        grayPanel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        centerContainer.add(grayPanel, BorderLayout.CENTER);

        // -- Przycisk Powrotu (Na dole, po prawej, pod szarym panelem) --
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

        // Załadowanie danych
        loadBooks();
    }

    private void loadBooks() {
        allBooks = ksiazkaDAO.getAllKsiazki();
        updateTable(allBooks);
    }

    private void filterBooks() {
        String query = searchField.getText().toLowerCase();
        if (query.equals("wyszukaj...") || query.isEmpty()) {
            updateTable(allBooks);
            return;
        }

        List<Ksiazka> filtered = allBooks.stream()
                .filter(b -> b.getTytul().toLowerCase().contains(query) ||
                        b.getAutor().toLowerCase().contains(query) ||
                        b.getGatunek().toLowerCase().contains(query))
                .collect(Collectors.toList());
        updateTable(filtered);
    }

    private void updateTable(List<Ksiazka> books) {
        // Kolumny zgodne z Twoim rysunkiem
        String[] columns = { "Tytuł", "Autor", "Rok wydania", "Gatunek", "Wypożycz" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Tylko przycisk
            }
        };

        for (Ksiazka k : books) {
            model.addRow(new Object[] {
                    k.getTytul(),
                    k.getAutor(),
                    k.getRokWydania(),
                    k.getGatunek(),
                    "Wypożycz"
            });
        }
        bookTable.setModel(model);

        // Konfiguracja przycisku "Wypożycz"
        bookTable.getColumn("Wypożycz").setCellRenderer(new ButtonRenderer());
        bookTable.getColumn("Wypożycz").setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void performReservation(int row) {
        // Pobieramy książkę z aktualnie wyświetlanej listy w tabeli
        String tytul = (String) bookTable.getValueAt(row, 0);

        // Znajdujemy ISBN na podstawie tytułu (uproszczenie, w idealnym świecie mamy ukrytą kolumnę ID/ISBN)
        Ksiazka selectedBook = allBooks.stream()
                .filter(b -> b.getTytul().equals(tytul))
                .findFirst()
                .orElse(null);

        if (selectedBook != null) {
            if (rezerwacjaDAO.hasActiveReservation(currentUser.getId(), selectedBook.getIsbn())) {
                JOptionPane.showMessageDialog(this, "Masz już aktywną rezerwację na tę książkę.");
                return;
            }
            // Dodajemy rezerwację
            rezerwacjaDAO.addRezerwacja(currentUser.getId(), selectedBook.getIsbn());
            JOptionPane.showMessageDialog(this, "Książka została zarezerwowana! Odbierz ją w bibliotece.");
        }
    }

    // --- RENDERER PRZYCISKU ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Wypożycz" : value.toString());
            return this;
        }
    }

    // --- EDYTOR PRZYCISKU (OBSŁUGA KLIKNIĘCIA) ---
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Wypożycz" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                performReservation(currentRow);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
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

    // Zmieniono nazwę listy na bardziej adekwatną
    private List<Ksiazka> availableBooks;
    private JTextField searchField;

    public CatalogDialog(Frame owner, Czytelnik user) {
        super(owner, "Wypożyczalnia książek", true);
        this.currentUser = user;
        this.ksiazkaDAO = new KsiazkaDAO();
        this.rezerwacjaDAO = new RezerwacjaDAO();

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
        bookTable.setRowHeight(35);
        bookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        grayPanel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

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

        loadBooks();
    }

    private void loadBooks() {
        // ZMIANA: Używamy metody pobierającej TYLKO dostępne książki
        availableBooks = ksiazkaDAO.getOnlyAvailableBooks();

        if (availableBooks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Brak dostępnych książek w bibliotece.");
        }
        updateTable(availableBooks);
    }

    private void filterBooks() {
        String query = searchField.getText().toLowerCase();
        if (query.equals("wyszukaj...") || query.isEmpty()) {
            updateTable(availableBooks);
            return;
        }

        List<Ksiazka> filtered = availableBooks.stream()
                .filter(b -> b.getTytul().toLowerCase().contains(query) ||
                        b.getAutor().toLowerCase().contains(query) ||
                        b.getGatunek().toLowerCase().contains(query))
                .collect(Collectors.toList());
        updateTable(filtered);
    }

    private void updateTable(List<Ksiazka> books) {
        String[] columns = { "Tytuł", "Autor", "Rok wydania", "Gatunek", "Wypożycz" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
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

        bookTable.getColumn("Wypożycz").setCellRenderer(new ButtonRenderer());
        bookTable.getColumn("Wypożycz").setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void performReservation(int row) {
        // Pobieramy tytuł z tabeli (uwaga: jeśli są duplikaty tytułów, lepiej użyć ukrytej kolumny ISBN, ale tu upraszczamy)
        String tytul = (String) bookTable.getValueAt(row, 0);

        // Szukamy książki w liście `availableBooks` (nie `allBooks`)
        Ksiazka selectedBook = availableBooks.stream()
                .filter(b -> b.getTytul().equals(tytul))
                .findFirst()
                .orElse(null);

        if (selectedBook != null) {
            if (rezerwacjaDAO.hasActiveReservation(currentUser.getId(), selectedBook.getIsbn())) {
                JOptionPane.showMessageDialog(this, "Masz już aktywną rezerwację na tę książkę.");
                return;
            }
            rezerwacjaDAO.addRezerwacja(currentUser.getId(), selectedBook.getIsbn());
            JOptionPane.showMessageDialog(this, "Książka została zarezerwowana! Odbierz ją w bibliotece.");
        }
    }

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
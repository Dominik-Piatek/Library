package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.dao.RezerwacjaDAO;
import org.example.library.dao.WypozyczenieDAO;
import org.example.library.model.Czytelnik;
import org.example.library.model.Ksiazka;
import org.example.library.model.Wypozyczenie;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class ReaderPanel extends JPanel {
    private Czytelnik currentUser;
    private KsiazkaDAO ksiazkaDAO;
    private RezerwacjaDAO rezerwacjaDAO;
    private WypozyczenieDAO wypozyczenieDAO;
    private JTable bookTable;
    private JTextField searchField;

    public ReaderPanel(Czytelnik user) {
        this.currentUser = user;
        this.ksiazkaDAO = new KsiazkaDAO();
        this.rezerwacjaDAO = new RezerwacjaDAO();
        this.wypozyczenieDAO = new WypozyczenieDAO();

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Katalog i Rezerwacje", createCatalogPanel());
        tabbedPane.addTab("Moje Konto", createAccountPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Bar
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchField = new JTextField();
        JButton searchButton = new JButton("Szukaj");
        searchButton.addActionListener(e -> filterBooks(searchField.getText()));

        searchPanel.add(new JLabel("Szukaj (Tytuł/Autor): "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Books Table
        bookTable = new JTable();
        bookTable.setRowHeight(30);
        loadBooks();

        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // History Table
        JTable historyTable = new JTable();
        List<Wypozyczenie> history = wypozyczenieDAO.getWypozyczeniaHistory(currentUser.getId());

        String[] columns = { "ID", "Data Wypożyczenia", "Data Zwrotu", "ID Egzemplarza", "Kara" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Wypozyczenie w : history) {
            model.addRow(new Object[] {
                    w.getId(),
                    w.getDataWypozyczenia(),
                    w.getFaktycznaDataZwrotu(),
                    w.getEgzemplarzId(),
                    w.getKara()
            });
        }
        historyTable.setModel(model);

        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        container.add(new JLabel("Historia Wypożyczeń", SwingConstants.CENTER), BorderLayout.NORTH);
        container.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        panel.add(container, BorderLayout.CENTER);
        return panel;
    }

    private void loadBooks() {
        List<Ksiazka> books = ksiazkaDAO.getAllKsiazki();
        populateTable(books);
    }

    private void filterBooks(String query) {
        List<Ksiazka> allBooks = ksiazkaDAO.getAllKsiazki();
        List<Ksiazka> filtered = allBooks.stream()
                .filter(b -> b.getTytul().toLowerCase().contains(query.toLowerCase()) ||
                        b.getAutor().toLowerCase().contains(query.toLowerCase()))
                .toList();
        populateTable(filtered);
    }

    private void populateTable(List<Ksiazka> books) {
        String[] columns = { "ISBN", "Tytuł", "Autor", "Gatunek", "Rok", "Akcja" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Allow button click
            }
        };

        for (Ksiazka b : books) {
            model.addRow(new Object[] { b.getIsbn(), b.getTytul(), b.getAutor(), b.getGatunek(), b.getRokWydania(),
                    "Zarezerwuj" });
        }
        bookTable.setModel(model);

        bookTable.getColumn("Akcja").setCellRenderer(new ButtonRenderer());
        bookTable.getColumn("Akcja").setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    public void reserveBook(String isbn) {
        if (rezerwacjaDAO.hasActiveReservation(currentUser.getId(), isbn)) {
            JOptionPane.showMessageDialog(this, "Masz już aktywną rezerwację na tę książkę.");
            return;
        }
        rezerwacjaDAO.addRezerwacja(currentUser.getId(), isbn);
        JOptionPane.showMessageDialog(this, "Rezerwacja złożona pomyślnie!");
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText((value == null) ? "Zarezerwuj" : value.toString());
            setBackground(new Color(60, 179, 113));
            setForeground(Color.WHITE);
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

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            label = (value == null) ? "Zarezerwuj" : value.toString();
            button.setText(label);
            button.setBackground(new Color(60, 179, 113));
            button.setForeground(Color.WHITE);
            isPushed = true;
            currentRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                String isbn = (String) bookTable.getValueAt(currentRow, 0);
                reserveBook(isbn);
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}

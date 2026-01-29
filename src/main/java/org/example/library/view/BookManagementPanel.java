package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.model.Ksiazka;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BookManagementPanel extends JPanel {
    private JTable bookTable;
    private KsiazkaDAO ksiazkaDAO;
    private List<Ksiazka> allBooks; // Przechowujemy wszystkie książki do filtrowania
    private JTextField searchField;

    public BookManagementPanel() {
        this.ksiazkaDAO = new KsiazkaDAO();

        // Ustawienia głównego panelu (białe tło, marginesy)
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- SZARY KONTENER ---
        JPanel grayPanel = new JPanel(new BorderLayout());
        grayPanel.setBackground(new Color(230, 230, 230));
        grayPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // --- GÓRA: TYTUŁ I WYSZUKIWARKA ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(230, 230, 230));
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Książki w systemie:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Panel wyszukiwania (po prawej)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(230, 230, 230));

        searchField = new JTextField(15);
        searchField.setText("Wyszukaj...");
        // Czyścimy pole po kliknięciu
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Wyszukaj...")) {
                    searchField.setText("");
                }
            }
        });

        JButton searchButton = new JButton("Szukaj");
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> filterBooks());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        topPanel.add(searchPanel, BorderLayout.EAST);

        grayPanel.add(topPanel, BorderLayout.NORTH);

        // --- ŚRODEK: TABELA ---
        bookTable = new JTable();
        bookTable.setRowHeight(35);
        bookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(bookTable);
        grayPanel.add(scrollPane, BorderLayout.CENTER);

        // Dodajemy szary panel do głównego widoku
        add(grayPanel, BorderLayout.CENTER);

        // --- DÓŁ: PRZYCISKI FUNKCYJNE ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("Powrót do menu głównego");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> closeWindow());

        JButton addButton = new JButton("Dodaj książkę");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddBookDialog());

        bottomPanel.add(backButton);
        bottomPanel.add(addButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Załaduj dane przy starcie
        loadBooks();
    }

    private void closeWindow() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
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
                        b.getIsbn().toLowerCase().contains(query))
                .collect(Collectors.toList());
        updateTable(filtered);
    }

    private void updateTable(List<Ksiazka> books) {
        String[] columns = { "Tytuł", "Autor", "Rok wydania", "ISBN", "Usuń", "Edytuj" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; // Tylko przyciski są aktywne
            }
        };

        for (Ksiazka k : books) {
            model.addRow(new Object[] {
                    k.getTytul(),
                    k.getAutor(),
                    k.getRokWydania(),
                    k.getIsbn(),
                    "Usuń",
                    "Edytuj"
            });
        }
        bookTable.setModel(model);

        // Konfiguracja przycisków w tabeli
        bookTable.getColumn("Usuń").setCellRenderer(new ButtonRenderer());
        bookTable.getColumn("Usuń").setCellEditor(new DeleteButtonEditor(new JCheckBox()));

        bookTable.getColumn("Edytuj").setCellRenderer(new ButtonRenderer());
        bookTable.getColumn("Edytuj").setCellEditor(new EditButtonEditor(new JCheckBox()));
    }

    // --- Logika dodawania książki ---
    private void showAddBookDialog() {
        // Otwieramy okno dodawania
        AddBookDialog dialog = new AddBookDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        // Po zamknięciu odświeżamy tabelę
        loadBooks();
    }

    // --- Klasa Renderera (Wygląd przycisku) ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // --- Obsługa przycisku USUŃ ---
    class DeleteButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public DeleteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Usuń" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Pobieramy ISBN z wiersza
                String isbn = (String) bookTable.getValueAt(currentRow, 3);
                int confirm = JOptionPane.showConfirmDialog(button,
                        "Czy na pewno usunąć książkę o ISBN: " + isbn + "?",
                        "Potwierdzenie", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    ksiazkaDAO.deleteKsiazka(isbn);
                    loadBooks(); // Odśwież tabelę
                }
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

    // --- Obsługa przycisku EDYTUJ ---
    class EditButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public EditButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Edytuj" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                String isbn = (String) bookTable.getValueAt(currentRow, 3);

                // Otwieramy okno edycji
                Window parentWindow = SwingUtilities.getWindowAncestor(button);
                if (parentWindow instanceof Frame) {
                    new EditBookDialog((Frame) parentWindow, isbn).setVisible(true);
                } else {
                    new EditBookDialog(null, isbn).setVisible(true);
                }

                // Po zamknięciu odświeżamy tabelę
                loadBooks();
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
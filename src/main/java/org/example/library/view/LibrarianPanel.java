package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.dao.RezerwacjaDAO;
import org.example.library.model.Ksiazka;
import org.example.library.model.Rezerwacja;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class LibrarianPanel extends JPanel {
    private KsiazkaDAO ksiazkaDAO;
    private RezerwacjaDAO rezerwacjaDAO;

    public LibrarianPanel() {
        this.ksiazkaDAO = new KsiazkaDAO();
        this.rezerwacjaDAO = new RezerwacjaDAO();

        setLayout(new BorderLayout());
        add(new JLabel("Panel Bibliotekarza", SwingConstants.CENTER), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Czytelnicy", new ReaderManagementPanel(1)); // Default admin ID
        tabbedPane.addTab("Wypożyczenia", new LoanPanel(1));
        tabbedPane.addTab("Książki", createBookPanel());
        tabbedPane.addTab("Zgłoszenia (Rezerwacje)", createReservationPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable bookTable = new JTable();
        bookTable.setRowHeight(30);

        List<Ksiazka> books = ksiazkaDAO.getAllKsiazki();
        String[] columns = { "ISBN", "Tytuł", "Autor", "Edycja", "Usuwanie" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };

        for (Ksiazka b : books) {
            model.addRow(new Object[] { b.getIsbn(), b.getTytul(), b.getAutor(), "Edytuj", "Usuń" });
        }
        bookTable.setModel(model);

        bookTable.getColumn("Edycja").setCellRenderer(new ButtonRenderer(Color.ORANGE));
        bookTable.getColumn("Edycja").setCellEditor(new ButtonEditor(new JCheckBox(), true, bookTable));

        bookTable.getColumn("Usuwanie").setCellRenderer(new ButtonRenderer(Color.RED));
        bookTable.getColumn("Usuwanie").setCellEditor(new ButtonEditor(new JCheckBox(), false, bookTable));

        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton refreshBtn = new JButton("Odśwież");
        refreshBtn.addActionListener(e -> {
            // quick refresh logic: recreate panel or reload model
            // For MVP, just message
            JOptionPane.showMessageDialog(panel, "Odświeżanie wymaga przeładowania widoku (TODO)");
        });
        controlPanel.add(refreshBtn);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTable resTable = new JTable();
        resTable.setRowHeight(30);

        List<Rezerwacja> resList = rezerwacjaDAO.getAllReservations();
        String[] columns = { "ID", "Data", "Czytelnik ID", "ISBN", "Status", "Akcja" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        for (Rezerwacja r : resList) {
            // Only show active? Or all. Let's show all for now.
            model.addRow(new Object[] { r.getId(), r.getDataRezerwacji(), r.getCzytelnikId(), r.getKsiazkaIsbn(),
                    r.getStatus(), "Zrealizuj" });
        }
        resTable.setModel(model);

        resTable.getColumn("Akcja").setCellRenderer(new ButtonRenderer(new Color(46, 204, 113)));
        resTable.getColumn("Akcja").setCellEditor(new ReservationActionEditor(new JCheckBox(), resTable));

        panel.add(new JScrollPane(resTable), BorderLayout.CENTER);
        return panel;
    }

    // --- Helpers ---

    class ButtonRenderer extends JButton implements TableCellRenderer {
        private Color bgColor;

        public ButtonRenderer(Color color) {
            this.bgColor = color;
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(bgColor);
            setForeground(Color.WHITE);
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private boolean isEdit;
        private JTable table;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox, boolean isEdit, JTable table) {
            super(checkBox);
            this.isEdit = isEdit;
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                String isbn = (String) table.getValueAt(currentRow, 0);
                if (isEdit) {
                    JOptionPane.showMessageDialog(button,
                            "Edycja książki ISBN: " + isbn + " (Funkcja w przygotowaniu)");
                    // Open Edit Dialog Logic (TODO)
                } else {
                    int confirm = JOptionPane.showConfirmDialog(button,
                            "Czy na pewno usunąć książkę ISBN: " + isbn + "?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        ksiazkaDAO.deleteKsiazka(isbn);
                        ((DefaultTableModel) table.getModel()).removeRow(currentRow);
                    }
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    class ReservationActionEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private int currentRow;

        public ReservationActionEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            label = (value == null) ? "Zrealizuj" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int resId = (Integer) table.getValueAt(currentRow, 0);
                // "Realize" means basically update status to "Zrealizowana" and maybe Create
                // Loan?
                // For MVP, just update status.
                rezerwacjaDAO.updateReservationStatus(resId, "Zrealizowana");
                JOptionPane.showMessageDialog(button, "Rezerwacja oznaczona jako zrealizowana!");
                table.setValueAt("Zrealizowana", currentRow, 4);
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

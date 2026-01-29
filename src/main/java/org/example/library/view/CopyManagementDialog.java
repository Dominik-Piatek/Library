package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.model.Egzemplarz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class CopyManagementDialog extends JDialog {
    private JTable copyTable;
    private KsiazkaDAO ksiazkaDAO;
    private String isbn;
    private List<Egzemplarz> copies;

    public CopyManagementDialog(Window owner, String isbn) {
        super(owner, "Zarządzanie egzemplarzami", ModalityType.APPLICATION_MODAL);
        this.isbn = isbn;
        this.ksiazkaDAO = new KsiazkaDAO();
        setSize(800, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Egzemplarze książki (ISBN: " + isbn + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        copyTable = new JTable();
        copyTable.setRowHeight(35);
        copyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(copyTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton closeButton = new JButton("Zamknij");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());

        JButton addButton = new JButton("Dodaj egzemplarz");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddCopyDialog());

        bottomPanel.add(closeButton);
        bottomPanel.add(addButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        loadCopies();
    }

    private void loadCopies() {
        copies = ksiazkaDAO.getEgzemplarzeByIsbn(isbn);
        updateTable(copies);
    }

    private void updateTable(List<Egzemplarz> copies) {
        String[] columns = { "ID", "Kod kreskowy", "Półka", "Regał", "Status", "Usuń", "Edytuj" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6;
            }
        };

        for (Egzemplarz e : copies) {
            model.addRow(new Object[] {
                    e.getId(),
                    e.getKodKreskowy(),
                    e.getLokalizacjaPolka(),
                    e.getLokalizacjaRegal(),
                    e.getStatusWypozyczenia(),
                    "Usuń",
                    "Edytuj"
            });
        }
        copyTable.setModel(model);

        copyTable.getColumn("Usuń").setCellRenderer(new ButtonRenderer());
        copyTable.getColumn("Usuń").setCellEditor(new DeleteButtonEditor(new JCheckBox()));

        copyTable.getColumn("Edytuj").setCellRenderer(new ButtonRenderer());
        copyTable.getColumn("Edytuj").setCellEditor(new EditButtonEditor(new JCheckBox()));
    }

    private void showAddCopyDialog() {
        AddCopyDialog dialog = new AddCopyDialog(this, isbn);
        dialog.setVisible(true);
        loadCopies();
    }

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
                int id = (Integer) copyTable.getValueAt(currentRow, 0);
                int confirm = JOptionPane.showConfirmDialog(button,
                        "Czy na pewno usunąć egzemplarz ID: " + id + "?",
                        "Potwierdzenie", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    ksiazkaDAO.deleteEgzemplarz(id);
                    loadCopies();
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
                int id = (Integer) copyTable.getValueAt(currentRow, 0);
                // Find the Egzemplarz object
                Egzemplarz copy = copies.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
                if (copy != null) {
                    new EditCopyDialog(SwingUtilities.getWindowAncestor(button), copy).setVisible(true);
                    loadCopies();
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
}
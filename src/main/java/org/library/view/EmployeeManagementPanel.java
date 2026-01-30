package org.library.view;

import org.library.dao.PracownikDAO;
import org.library.model.Pracownik;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {
    private JTable employeeTable;
    private PracownikDAO pracownikDAO;
    private List<Pracownik> currentEmployeeList;

    public EmployeeManagementPanel() {
        this.pracownikDAO = new PracownikDAO();

        // Główny layout
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. Szary kontener ---
        JPanel grayContainer = new JPanel(new BorderLayout());
        grayContainer.setBackground(new Color(230, 230, 230));
        grayContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Tytuł
        JLabel titleLabel = new JLabel("Lista pracowników");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        grayContainer.add(titleLabel, BorderLayout.NORTH);

        // Tabela
        employeeTable = new JTable();
        employeeTable.setRowHeight(35);
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        grayContainer.add(scrollPane, BorderLayout.CENTER);

        add(grayContainer, BorderLayout.CENTER);

        // --- 2. Przycisk powrotu ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton backButton = new JButton("Powrót do menu głównego");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> closeWindow());

        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadEmployees();
    }

    private void closeWindow() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    private void loadEmployees() {
        currentEmployeeList = pracownikDAO.getAllPracownicy();

        String[] columns = { "Imie", "Nazwisko", "Rola", "Modyfikuj konto" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        for (Pracownik p : currentEmployeeList) {
            model.addRow(new Object[] {
                    p.getImie(),
                    p.getNazwisko(),
                    p.getRola(),
                    "Modyfikuj"
            });
        }
        employeeTable.setModel(model);

        employeeTable.getColumn("Modyfikuj konto").setCellRenderer(new ButtonRenderer());
        employeeTable.getColumn("Modyfikuj konto").setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    // --- Renderer ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Modyfikuj" : value.toString());
            return this;
        }
    }

    // --- Edytor  ---
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
            label = (value == null) ? "Modyfikuj" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                Pracownik selectedEmp = currentEmployeeList.get(currentRow);

                SwingUtilities.invokeLater(() -> {
                    Window parentWindow = SwingUtilities.getWindowAncestor(EmployeeManagementPanel.this);
                    if (parentWindow instanceof Frame) {
                        new EditEmployeeProfileDialog((Frame) parentWindow, selectedEmp).setVisible(true);
                    } else {
                        new EditEmployeeProfileDialog(null, selectedEmp).setVisible(true);
                    }
                    loadEmployees();
                });
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
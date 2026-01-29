package org.example.library.view;

import org.example.library.dao.PracownikDAO;
import org.example.library.model.Pracownik;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {
    private JTable employeeTable;
    private PracownikDAO pracownikDAO;
    private List<Pracownik> currentEmployeeList; // Przechowujemy listę, żeby wiedzieć kogo edytować

    public EmployeeManagementPanel() {
        this.pracownikDAO = new PracownikDAO();

        // Główny layout całego okna
        setLayout(new BorderLayout());
        setBackground(Color.WHITE); // Tło pod spodem
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Margines zewnętrzny

        // --- 1. Szary kontener na środku ---
        JPanel grayContainer = new JPanel(new BorderLayout());
        grayContainer.setBackground(new Color(230, 230, 230)); // Jasnoszary
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
        employeeTable.setRowHeight(35); // Wyższe wiersze dla przycisków
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Dodajemy tabelę do przewijanego panelu
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        grayContainer.add(scrollPane, BorderLayout.CENTER);

        // Dodajemy szary kontener do głównego widoku
        add(grayContainer, BorderLayout.CENTER);

        // --- 2. Przycisk powrotu na dole ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton backButton = new JButton("Powrót do menu główne...");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> closeWindow());

        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Załadowanie danych
        loadEmployees();
    }

    private void closeWindow() {
        // Zamyka okno dialogowe, w którym znajduje się ten panel
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    private void loadEmployees() {
        currentEmployeeList = pracownikDAO.getAllPracownicy();

        // Kolumny zgodne z makietą
        String[] columns = { "Imie", "Nazwisko", "Rola", "Modyfikuj konto" };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Tylko ostatnia kolumna (przycisk) jest edytowalna/klikalna
                return column == 3;
            }
        };

        for (Pracownik p : currentEmployeeList) {
            model.addRow(new Object[] {
                    p.getImie(),
                    p.getNazwisko(),
                    p.getRola(),
                    "Modyfikuj" // Tekst na przycisku
            });
        }
        employeeTable.setModel(model);

        // Ustawienie renderera przycisków dla ostatniej kolumny
        employeeTable.getColumn("Modyfikuj konto").setCellRenderer(new ButtonRenderer());
        employeeTable.getColumn("Modyfikuj konto").setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    // --- Klasa wewnętrzna: Wygląd przycisku w tabeli ---
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

    // --- Klasa wewnętrzna: Działanie przycisku w tabeli ---
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
                // Logika kliknięcia przycisku
                Pracownik selectedEmp = currentEmployeeList.get(currentRow);

                // Otwórz okno edycji (musisz mieć EditEmployeeProfileDialog)
                // Zakładam, że masz już ten plik z poprzednich kroków
                Window parentWindow = SwingUtilities.getWindowAncestor(button);
                if (parentWindow instanceof Frame) {
                    new EditEmployeeProfileDialog((Frame) parentWindow, selectedEmp).setVisible(true);
                } else {
                    // Fallback jeśli parent nie jest Frame
                    new EditEmployeeProfileDialog(null, selectedEmp).setVisible(true);
                }

                // Po zamknięciu edycji odśwież tabelę
                loadEmployees();
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
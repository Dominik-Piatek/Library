package org.example.library.view;

import org.example.library.dao.PracownikDAO;
import org.example.library.model.Pracownik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {
    private JTable employeeTable;
    private PracownikDAO pracownikDAO;

    public EmployeeManagementPanel() {
        this.pracownikDAO = new PracownikDAO();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);

        JButton refreshButton = createStyledButton("Odśwież", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> loadEmployees());
        toolBar.add(refreshButton);
        toolBar.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton addButton = createStyledButton("Dodaj pracownika", new Color(46, 204, 113));
        addButton.addActionListener(e -> showAddEmployeeDialog());
        toolBar.add(addButton);

        add(toolBar, BorderLayout.NORTH);

        employeeTable = new JTable();
        employeeTable.setRowHeight(25);
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        loadEmployees();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }

    private void loadEmployees() {
        List<Pracownik> employees = pracownikDAO.getAllPracownicy();
        String[] columns = { "ID", "Imię", "Nazwisko", "Login", "Rola" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Pracownik p : employees) {
            model.addRow(new Object[] { p.getId(), p.getImie(), p.getNazwisko(), p.getLogin(), p.getRola() });
        }
        employeeTable.setModel(model);
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Employee", true);
        dialog.setLayout(new GridLayout(6, 2));
        dialog.setSize(300, 250);

        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();
        JTextField loginField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[] { "Bibliotekarz", "Administrator", "Kierownik" });

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Surname:"));
        dialog.add(surnameField);
        dialog.add(new JLabel("Login:"));
        dialog.add(loginField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Role:"));
        dialog.add(roleCombo);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                // Determine logged in admin ID logic here, hardcoded 1 for MVP
                int adminId = 1;
                Pracownik p = new Pracownik(
                        nameField.getText(),
                        surnameField.getText(),
                        loginField.getText(),
                        new String(passwordField.getPassword()),
                        (String) roleCombo.getSelectedItem(),
                        adminId);
                pracownikDAO.addPracownik(p);
                dialog.dispose();
                loadEmployees();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding employee: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(saveButton);

        dialog.setVisible(true);
    }
}

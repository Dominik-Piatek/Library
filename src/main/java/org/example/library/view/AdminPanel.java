package org.example.library.view;

import org.example.library.controller.LoginController;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {

    public AdminPanel() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(30, 30, 30, 30);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(230, 230, 230));
        menuPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));

        JLabel titleLabel = new JLabel("Menu kierownika");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(titleLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // --- PRZYCISKI ---

        // 1. Dodaj konto pracownika (TERAZ OTWIERA NOWY DIALOG)
        menuPanel.add(createMenuButton("Dodaj konto pracownika", e -> {
            new AddEmployeeDialog((Frame) SwingUtilities.getWindowAncestor(this)).setVisible(true);
        }));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 2. Otwórz panel zarządzania pracownikami
        menuPanel.add(createMenuButton("Otwórz panel zarządzania pracownikami", e -> openEmployeeManagement()));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 3. Zarządzanie książkami
        menuPanel.add(createMenuButton("Zarządzanie książkami", e -> openBookManagement()));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 4. Statystyki
        menuPanel.add(createMenuButton("Statystyki", e -> showStatisticsMsg()));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // 5. Wyloguj
        menuPanel.add(createMenuButton("Wyloguj", e -> performLogout()));

        menuPanel.add(Box.createVerticalGlue());

        add(menuPanel, gbc);
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(600, 65));
        button.setPreferredSize(new Dimension(600, 65));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        button.addActionListener(action);
        return button;
    }

    private void openEmployeeManagement() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Zarządzanie Pracownikami", true);
        dialog.setContentPane(new EmployeeManagementPanel());
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void openBookManagement() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Zarządzanie Książkami", true);
        dialog.setContentPane(new BookManagementPanel());
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showStatisticsMsg() {
        JOptionPane.showMessageDialog(this, "Moduł statystyk jest w trakcie budowy.");
    }

    private void performLogout() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            window.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            new LoginController(loginFrame);
            loginFrame.setVisible(true);
        });
    }
}
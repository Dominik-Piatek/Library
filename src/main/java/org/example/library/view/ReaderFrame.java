package org.example.library.view;

import org.example.library.model.Czytelnik;

import javax.swing.*;
import java.awt.*;

public class ReaderFrame extends JFrame {
    private Czytelnik currentUser;

    public ReaderFrame(Czytelnik user) {
        this.currentUser = user;
        setTitle("System Biblioteczny - " + user.getImie() + " " + user.getNazwisko());
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80)); // Dark Blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Witaj, " + currentUser.getImie());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Wyloguj");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu accountMenu = new JMenu("Konto");
        JMenuItem editItem = new JMenuItem("Edytuj dane");
        editItem.addActionListener(e -> new EditReaderProfileDialog(this, currentUser).setVisible(true));
        accountMenu.add(editItem);
        menuBar.add(accountMenu);
        setJMenuBar(menuBar);

        // Content
        add(new ReaderPanel(currentUser), BorderLayout.CENTER);
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            new org.example.library.controller.LoginController(loginFrame);
            loginFrame.setVisible(true);
        });
    }
}

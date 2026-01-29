package org.example.library.view;

import org.example.library.model.Pracownik;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Pracownik currentUser;

    public MainFrame(Pracownik user) {
        this.currentUser = user;
        setTitle("System Biblioteczny - " + user.getImie() + " " + user.getNazwisko() + " [" + user.getRola() + "]");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(new JLabel("Witaj, " + currentUser.getImie()), BorderLayout.WEST);

        JButton logoutButton = new JButton("Wyloguj");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu accountMenu = new JMenu("Konto");
        JMenuItem editItem = new JMenuItem("Edytuj dane");
        editItem.addActionListener(e -> new EditEmployeeProfileDialog(this, currentUser).setVisible(true));
        accountMenu.add(editItem);
        menuBar.add(accountMenu);
        setJMenuBar(menuBar);

        // Content Area
        JPanel contentPanel = new JPanel(new CardLayout());

        if ("Administrator".equalsIgnoreCase(currentUser.getRola())) {
            contentPanel.add(new AdminPanel(), "Admin");
        } else if ("Kierownik".equalsIgnoreCase(currentUser.getRola())) {
            // Manager gets a special panel with ALL features
            JTabbedPane managerPane = new JTabbedPane();
            managerPane.addTab("Książki", new BookManagementPanel());
            managerPane.addTab("Pracownicy", new EmployeeManagementPanel());
            managerPane.addTab("Czytelnicy", new ReaderManagementPanel(currentUser.getId()));
            managerPane.addTab("Wypożyczenia", new LoanPanel(currentUser.getId()));
            contentPanel.add(managerPane, "Manager");
        } else if ("Bibliotekarz".equalsIgnoreCase(currentUser.getRola())) {
            contentPanel.add(new LibrarianPanel(), "Librarian");
        } else {
            contentPanel.add(new JLabel("Rola nierozpoznana"), "Error");
        }

        add(contentPanel, BorderLayout.CENTER);
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

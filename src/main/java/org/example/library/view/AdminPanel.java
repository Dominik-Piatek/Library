package org.example.library.view;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    public AdminPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Panel Administratora", SwingConstants.CENTER), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Książki", new BookManagementPanel());
        tabbedPane.addTab("Pracownicy", new EmployeeManagementPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}

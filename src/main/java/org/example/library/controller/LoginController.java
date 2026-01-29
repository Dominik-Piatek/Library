package org.example.library.controller;

import org.example.library.dao.PracownikDAO;
import org.example.library.model.Pracownik;
import org.example.library.view.LoginFrame;
import org.example.library.view.MainFrame;

import javax.swing.*;
import java.util.Optional;

public class LoginController {
    private LoginFrame view;
    private PracownikDAO pracownikDAO;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.pracownikDAO = new PracownikDAO();
        initController();
    }

    private void initController() {
        view.getLoginButton().addActionListener(e -> login());
    }

    private void login() {
        String login = view.getLoginField().getText();
        String password = new String(view.getPasswordField().getPassword());

        // 1. Try Employee Login
        Optional<Pracownik> employee = pracownikDAO.authenticate(login, password);
        if (employee.isPresent()) {
            view.dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(employee.get()).setVisible(true));
            return;
        }

        // 2. Try Reader Login (Using CzytelnikDAO)
        // Note: Readers log in with Email (or Phone) usually, but form says "Login".
        // We will assume 'login' field can be email for readers.
        org.example.library.dao.CzytelnikDAO czytelnikDAO = new org.example.library.dao.CzytelnikDAO();
        Optional<org.example.library.model.Czytelnik> reader = czytelnikDAO.authenticate(login, password);

        if (reader.isPresent()) {
            view.dispose();
            SwingUtilities.invokeLater(() -> new org.example.library.view.ReaderFrame(reader.get()).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(view, "Invalid credentials for Employee or Reader", "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

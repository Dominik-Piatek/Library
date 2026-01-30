package org.library.controller;

import org.library.dao.PracownikDAO;
import org.library.model.Pracownik;
import org.library.view.LoginFrame;
import org.library.view.MainFrame;

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

        // 1. Sprawdzamy czy to Pracownik
        Optional<Pracownik> employee = pracownikDAO.authenticate(login, password);
        if (employee.isPresent()) {
            view.dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(employee.get()).setVisible(true));
            return;
        }

        // 2. Sprawdzamy czy to Czytelnik
        org.library.dao.CzytelnikDAO czytelnikDAO = new org.library.dao.CzytelnikDAO();
        Optional<org.library.model.Czytelnik> reader = czytelnikDAO.authenticate(login, password);

        if (reader.isPresent()) {
            view.dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(reader.get()).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(view, "Błędny login lub hasło (Pracownik lub Czytelnik)", "Błąd logowania",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
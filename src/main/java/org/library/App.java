package org.library;

import org.library.controller.LoginController;
import org.library.model.InicjalizatorBazyDanych;
import org.library.view.LoginFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {

        // 1. Inicjalizacja Bazy Danych, jesli nie jest juz utworzona
        InicjalizatorBazyDanych.initialize();

        // 2. Ustawienie wyglądu aplikacji
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Uruchomienie interfejsu graficznego
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            new LoginController(loginFrame);
            loginFrame.setVisible(true);
        });
    }
}
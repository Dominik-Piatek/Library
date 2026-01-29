package org.example.library;

import org.example.library.controller.LoginController;
import org.example.library.model.DatabaseInitializer;
import org.example.library.view.LoginFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {

        // Setup UI Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 999);
            UIManager.put("Component.arc", 999);
            UIManager.put("ProgressBar.arc", 999);
            UIManager.put("TextComponent.arc", 999);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch UI
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            new LoginController(loginFrame);
            loginFrame.setVisible(true);
        });
    }
}

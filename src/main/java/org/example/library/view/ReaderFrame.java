package org.example.library.view;

import org.example.library.model.Czytelnik;

import javax.swing.*;
import java.awt.*;

public class ReaderFrame extends JFrame {
    private Czytelnik currentUser;

    public ReaderFrame(Czytelnik user) {
        this.currentUser = user;
        setTitle("System Biblioteczny"); // Prosty tytuł okna
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Ustawiamy prosty layout
        setLayout(new BorderLayout());

        // USUNIĘTO: headerPanel (niebieski pasek)
        // USUNIĘTO: JMenuBar (zakładam, że chcesz czysty wygląd jak na makiecie)

        // Dodajemy tylko Twój panel, który teraz zawiera cały wygląd (tytuł i menu)
        add(new ReaderPanel(currentUser), BorderLayout.CENTER);
    }
}
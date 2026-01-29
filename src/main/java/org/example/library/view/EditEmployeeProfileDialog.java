package org.example.library.view;

import org.example.library.dao.PracownikDAO;
import org.example.library.model.Pracownik;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditEmployeeProfileDialog extends JDialog {

    private final Pracownik employee;
    private final JTextField nameField;
    private final JTextField surnameField;
    private final JTextField loginField;
    private final JComboBox<String> roleCombo;
    private String newPassword = null; // Zmienna tymczasowa na nowe hasło

    public EditEmployeeProfileDialog(Frame owner, Pracownik employee) {
        super(owner, "Modyfikacja danych", true);
        this.employee = employee;
        setSize(500, 550);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Główny panel (szare tło)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Tytuł
        JLabel titleLabel = new JLabel("Modyfikuj dane");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Pola tekstowe
        mainPanel.add(createLabeledField("Imie", nameField = new JTextField(employee.getImie())));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Nazwisko", surnameField = new JTextField(employee.getNazwisko())));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("login", loginField = new JTextField(employee.getLogin())));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Lista rozwijana (ComboBox)
        String[] roles = { "Bibliotekarz", "Administrator" };
        roleCombo = new JComboBox<>(roles);
        roleCombo.setSelectedItem(employee.getRola());
        roleCombo.setBackground(Color.WHITE);
        mainPanel.add(createLabeledField("uprawnienia", roleCombo));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Przycisk "Resetuj hasło"
        JPanel passPanel = new JPanel(new BorderLayout(5, 5));
        passPanel.setBackground(new Color(230, 230, 230));
        passPanel.setMaximumSize(new Dimension(400, 50));

        JLabel passLabel = new JLabel("  hasło");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setPreferredSize(new Dimension(100, 30));

        JButton resetPassBtn = new JButton("Resetuj hasło");
        resetPassBtn.setPreferredSize(new Dimension(200, 30));
        resetPassBtn.setFocusPainted(false);
        resetPassBtn.addActionListener(e -> handlePasswordReset());

        passPanel.add(passLabel, BorderLayout.WEST);
        passPanel.add(resetPassBtn, BorderLayout.CENTER);

        mainPanel.add(passPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Przyciski Anuluj / Zatwierdź
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton cancelButton = createButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton confirmButton = createButton("Zatwierdź");
        confirmButton.addActionListener(e -> saveChanges());

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        mainPanel.add(buttonPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    // --- ZMIANA TUTAJ: Otwieramy nowe okienko ---
    private void handlePasswordReset() {
        // Tworzymy i pokazujemy nowy dialog
        ResetPasswordDialog dialog = new ResetPasswordDialog(this);
        dialog.setVisible(true);

        // Po zamknięciu sprawdzamy czy coś wpisano
        String pass = dialog.getConfirmedPassword();
        if (pass != null) {
            this.newPassword = pass;
            JOptionPane.showMessageDialog(this, "Hasło zostało zmienione w pamięci.\nKliknij 'Zatwierdź' na dole, aby zapisać w bazie.");
        }
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(230, 230, 230));
        panel.setMaximumSize(new Dimension(400, 50));

        JLabel label = new JLabel("  " + labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(100, 30));

        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }

    private void saveChanges() {
        try {
            // Aktualizujemy obiekt pracownika
            employee.setImie(nameField.getText());
            employee.setNazwisko(surnameField.getText());
            employee.setLogin(loginField.getText());
            employee.setRola((String) roleCombo.getSelectedItem());

            // Jeśli hasło zostało zmienione w nowym okienku
            if (newPassword != null) {
                employee.setHasloSkrot(newPassword);
            }

            // Zapis do bazy
            PracownikDAO dao = new PracownikDAO();
            dao.updatePracownik(employee);

            JOptionPane.showMessageDialog(this, "Dane zaktualizowane!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd zapisu: " + e.getMessage());
        }
    }
}
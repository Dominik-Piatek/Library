package org.example.library.view;

import org.example.library.dao.PracownikDAO;
import org.example.library.model.Pracownik;

import javax.swing.*;
import java.awt.*;

public class EditEmployeeProfileDialog extends JDialog {
    private Pracownik employee;
    private PracownikDAO dao;
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField loginField;
    private JPasswordField passField;

    public EditEmployeeProfileDialog(Frame owner, Pracownik employee) {
        super(owner, "Edycja Profilu", true);
        this.employee = employee;
        this.dao = new PracownikDAO();

        setSize(350, 250);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(5, 2, 10, 10));

        nameField = new JTextField(employee.getImie());
        surnameField = new JTextField(employee.getNazwisko());
        loginField = new JTextField(employee.getLogin());
        passField = new JPasswordField(employee.getHasloSkrot());

        add(new JLabel("  Imię:"));
        add(nameField);
        add(new JLabel("  Nazwisko:"));
        add(surnameField);
        add(new JLabel("  Login:"));
        add(loginField);
        add(new JLabel("  Nowe hasło:"));
        add(passField);

        JButton saveButton = new JButton("Zapisz zmiany");
        saveButton.addActionListener(e -> saveProfile());

        add(new JLabel(""));
        add(saveButton);
    }

    private void saveProfile() {
        try {
            employee.setImie(nameField.getText());
            employee.setNazwisko(surnameField.getText());
            employee.setLogin(loginField.getText());
            String newPass = new String(passField.getPassword());
            if (!newPass.isEmpty()) {
                employee.setHasloSkrot(newPass);
            }

            dao.updatePracownik(employee);
            JOptionPane.showMessageDialog(this, "Dane zaktualizowane!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage());
        }
    }
}

package org.library.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ResetPasswordDialog extends JDialog {

    private JPasswordField passField;
    private JPasswordField repeatPassField;
    private String confirmedPassword = null; // Tu zapiszemy wynik

    public ResetPasswordDialog(JDialog owner) {
        super(owner, "Resetowanie hasła", true); // true = modalne (blokuje okno pod spodem)
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Szary panel główny
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Tytuł
        JLabel titleLabel = new JLabel("Resetowanie hasła:");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Kontener na tytuł żeby wyrównać go do lewej
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(230, 230, 230));
        titlePanel.add(titleLabel);

        mainPanel.add(titlePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Pola haseł
        mainPanel.add(createLabeledField("Nowe hasło", passField = new JPasswordField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createLabeledField("Powtórz nowe", repeatPassField = new JPasswordField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Przyciski
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Przyciski do prawej
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton cancelButton = createButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton confirmButton = createButton("Zatwierdź");
        confirmButton.addActionListener(e -> confirm());

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        mainPanel.add(buttonPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(230, 230, 230));
        panel.setMaximumSize(new Dimension(400, 35));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setPreferredSize(new Dimension(100, 30));
        label.setHorizontalAlignment(SwingConstants.RIGHT); // Wyrównanie etykiety do prawej (jak na szkicu)

        field.setPreferredSize(new Dimension(200, 30));

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 30));
        return btn;
    }

    private void confirm() {
        String p1 = new String(passField.getPassword());
        String p2 = new String(repeatPassField.getPassword());

        if (p1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hasło nie może być puste.");
            return;
        }
        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(this, "Hasła nie są takie same.");
            return;
        }

        this.confirmedPassword = p1;
        dispose();
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }
}
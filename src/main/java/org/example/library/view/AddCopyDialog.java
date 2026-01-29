package org.example.library.view;

import org.example.library.dao.KsiazkaDAO;
import org.example.library.model.Egzemplarz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddCopyDialog extends JDialog {

    private JTextField barcodeField;
    private JTextField rackField;
    private JTextField shelfField;
    private JComboBox<String> statusCombo;
    private String isbn;

    public AddCopyDialog(Window owner, String isbn) {
        super(owner, "Dodaj egzemplarz", ModalityType.APPLICATION_MODAL);
        this.isbn = isbn;
        setSize(400, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel("Nowy egzemplarz");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Auto-generate barcode suggestion
        String defaultBarcode = "CODE-" + System.currentTimeMillis();
        mainPanel.add(createLabeledField("Kod kreskowy", barcodeField = new JTextField(defaultBarcode)));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Regał", rackField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(createLabeledField("Półka", shelfField = new JTextField()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] statuses = { "Dostępna", "Wypożyczona", "Zarezerwowana", "Niedostępna" };
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setBackground(Color.WHITE);
        mainPanel.add(createLabeledField("Status", statusCombo));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton addButton = new JButton("Dodaj");
        addButton.addActionListener(e -> saveCopy());

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        mainPanel.add(buttonPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(230, 230, 230));
        panel.setMaximumSize(new Dimension(350, 35));

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 30));
        
        field.setPreferredSize(new Dimension(200, 30));

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void saveCopy() {
        try {
            String barcode = barcodeField.getText();
            int rack = Integer.parseInt(rackField.getText());
            int shelf = Integer.parseInt(shelfField.getText());
            String status = (String) statusCombo.getSelectedItem();

            if (barcode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kod kreskowy jest wymagany!");
                return;
            }

            Egzemplarz e = new Egzemplarz(barcode, rack, shelf, status, isbn);
            boolean success = new KsiazkaDAO().addEgzemplarz(e);

            if (success) {
                JOptionPane.showMessageDialog(this, "Egzemplarz dodany!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Nie udało się dodać egzemplarza.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Regał i Półka muszą być liczbami!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage());
        }
    }
}
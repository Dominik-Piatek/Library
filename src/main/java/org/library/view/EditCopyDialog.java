package org.library.view;

import org.library.dao.KsiazkaDAO;
import org.library.model.Egzemplarz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditCopyDialog extends JDialog {

    private final JTextField barcodeField;
    private final JTextField rackField;
    private final JTextField shelfField;
    private final JComboBox<String> statusCombo;
    private final Egzemplarz copy;

    public EditCopyDialog(Window owner, Egzemplarz copy) {
        super(owner, "Edycja egzemplarza", ModalityType.APPLICATION_MODAL);
        this.copy = copy;
        setSize(400, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel("Edytuj egzemplarz");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        barcodeField = new JTextField(copy.getKodKreskowy());
        barcodeField.setEditable(false); // Barcode usually shouldn't change
        barcodeField.setBackground(new Color(220, 220, 220));
        
        mainPanel.add(createLabeledField("Kod kreskowy", barcodeField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        rackField = new JTextField(String.valueOf(copy.getLokalizacjaRegal()));
        mainPanel.add(createLabeledField("Regał", rackField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        shelfField = new JTextField(String.valueOf(copy.getLokalizacjaPolka()));
        mainPanel.add(createLabeledField("Półka", shelfField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] statuses = { "Dostępna", "Wypożyczona", "Zarezerwowana", "Niedostępna" };
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(copy.getStatusWypozyczenia());
        statusCombo.setBackground(Color.WHITE);
        mainPanel.add(createLabeledField("Status", statusCombo));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> saveChanges());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

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

    private void saveChanges() {
        try {
            int rack = Integer.parseInt(rackField.getText());
            int shelf = Integer.parseInt(shelfField.getText());
            String status = (String) statusCombo.getSelectedItem();

            copy.setLokalizacjaRegal(rack);
            copy.setLokalizacjaPolka(shelf);
            copy.setStatusWypozyczenia(status);

            new KsiazkaDAO().updateEgzemplarz(copy);

            JOptionPane.showMessageDialog(this, "Zmiany zapisane!");
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Regał i Półka muszą być liczbami!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage());
        }
    }
}
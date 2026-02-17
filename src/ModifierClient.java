import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ModifierClient extends JFrame {
    private JTextField idField, nameField, emailField, phoneField;

    public ModifierClient() {
        setTitle("Modifier Client");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel Formulaire
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("ID Client:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Nom Complet:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Téléphone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        // Boutons
        JButton updateButton = new JButton("Modifier");
        JButton backButton = new JButton("Retour");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> updateClient());
        backButton.addActionListener(e -> {
            new SysGestionClient().setVisible(true);
            setVisible(false);
        });
    }

    private void updateClient() {
        String idText = idField.getText();
        String nom = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (idText.isEmpty() || nom.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.");
            return;
        }

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement("UPDATE clients SET nom = ?, email = ?, telephone = ? WHERE idClient = ?")) {
            stmt.setString(1, nom);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setInt(4, Integer.parseInt(idText));
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Client modifié avec succès !");
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification du client : " + e.getMessage());
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }
}

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AjouterClient extends JFrame {
    private JTextField nameField, emailField, phoneField;

    public AjouterClient() {
        setTitle("Ajouter Client");
        setSize(6000, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nom Complet:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Téléphone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);


        JButton addButton = new JButton("Ajouter");
        JButton backButton = new JButton("Retour");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addClient());
        backButton.addActionListener(e -> {
            new SysGestionClient().setVisible(true);
            setVisible(false);
        });
    }

    private void addClient() {
        String nom = nameField.getText();
        String email = emailField.getText();
        String telephone = phoneField.getText();

        if (nom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.");
            return;
        }

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO clients (nom, email, telephone) VALUES (?, ?, ?)")) {
            stmt.setString(1, nom);
            stmt.setString(2, email);
            stmt.setString(3, telephone);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Client ajouté avec succès !");
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du client : " + e.getMessage());
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }
}

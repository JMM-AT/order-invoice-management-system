import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SupprimerClient extends JFrame {
    private JTextField idField;

    public SupprimerClient() {
        setTitle("Supprimer Client");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("ID Client:"));
        idField = new JTextField();
        formPanel.add(idField);


        JButton deleteButton = new JButton("Supprimer");
        JButton backButton = new JButton("Retour");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        deleteButton.addActionListener(e -> deleteClient());
        backButton.addActionListener(e -> {
            new SysGestionClient().setVisible(true);
            setVisible(false);
        });
    }

    private void deleteClient() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'ID client est obligatoire.");
            return;
        }

        int clientId = Integer.parseInt(idText);


        if (!clientExists(clientId)) {
            JOptionPane.showMessageDialog(this, "Le client avec l'ID " + clientId + " n'existe pas.");
            return;
        }

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM clients WHERE idClient = ?")) {
            stmt.setInt(1, clientId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Client supprimé avec succès !");
            idField.setText("");  
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du client : " + e.getMessage());
        }
    }


    private boolean clientExists(int clientId) {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM clients WHERE idClient = ?")) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();


            return rs.next();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la vérification du client : " + e.getMessage());
            return false;
        }
    }
}

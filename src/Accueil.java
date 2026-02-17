import javax.swing.*;
import styles.StyleManager;
import styles.StyledButton;
import styles.StyledPanel;

import java.awt.*;
import java.sql.*;

public class Accueil extends JFrame {


	public Accueil() {

		setTitle("Système de Gestion des Commandes et des Factures");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 


        createMainPanel();


        setVisible(true);
    }


	private void createMainPanel() {

		JPanel mainPanel = new StyledPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));  // Alignement horizontal avec espacement

        JButton btnGestionClient = new StyledButton("Gestion des Clients");
        JButton btnGestionFacture = new StyledButton("Gestion des Factures");
        JButton btnGestionCommandes = new StyledButton("Gestion des Commandes");
        JButton btnGestionProduits = new StyledButton("Gestion des Produits");

        btnGestionClient.addActionListener(e -> openGestionClient());
        btnGestionFacture.addActionListener(e -> openGestionFacture());
        btnGestionProduits.addActionListener(e -> openGestionProduits());
        btnGestionCommandes.addActionListener(e -> openGestionCommandes());

        buttonPanel.add(btnGestionClient);
        buttonPanel.add(btnGestionFacture);
        buttonPanel.add(btnGestionCommandes);
        buttonPanel.add(btnGestionProduits);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);


        JLabel welcomeLabel = new JLabel("Bienvenue dans votre Système de gestion", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));  
        welcomeLabel.setForeground(new Color(52, 152, 219));  
        mainPanel.add(welcomeLabel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void openGestionClient() {
        new SysGestionClient().setVisible(true);  
        setVisible(false); 
    }

    private void openGestionFacture() {
        new GestionFacture().setVisible(true); 
        setVisible(false); 
    }

    private void openGestionProduits() {
        new GestionProduitsUI().setVisible(true);  
        setVisible(false); 
    }


    private void openGestionCommandes() {
        new GestionCommandesUI().setVisible(true);  
        setVisible(false);  
    }


    private boolean clientExiste(int clientId) {
        try (Connection conn = DatabaseManager.connect()) {
            String query = "SELECT COUNT(*) FROM clients WHERE idClient = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, clientId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {

    	StyleManager.applyGlobalStyle();

        SwingUtilities.invokeLater(() -> {
            new Accueil().setVisible(true);  
        });
    }
}
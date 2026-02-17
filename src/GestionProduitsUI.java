import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class GestionProduitsUI extends JFrame {
    private JTextField idField, nomField, prixField, quantiteField;
    private JTextField idModifField, nomModifField, prixModifField, quantiteModifField;
    private JTextArea outputArea;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public GestionProduitsUI() {
        setTitle("Gestion des Produits");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);


        JPanel menuPrincipalPanel = new JPanel(new GridLayout(1, 1));
        JButton gestionProduitsButton = new JButton("Gestion des Produits");
        gestionProduitsButton.setPreferredSize(new Dimension(150, 30)); // Taille du bouton
        menuPrincipalPanel.add(gestionProduitsButton);


        JPanel sousMenuPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // 4 lignes, 1 colonne, espacement
        sousMenuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marge autour des boutons

        JButton ajouterButton = new JButton("Ajouter un produit");
        JButton afficherButton = new JButton("Afficher les produits");
        JButton modifierButton = new JButton("Modifier un produit");
        JButton supprimerButton = new JButton("Supprimer un produit");


        Dimension buttonSize = new Dimension(150, 30); // Taille des boutons
        ajouterButton.setPreferredSize(buttonSize);
        afficherButton.setPreferredSize(buttonSize);
        modifierButton.setPreferredSize(buttonSize);
        supprimerButton.setPreferredSize(buttonSize);

        sousMenuPanel.add(ajouterButton);
        sousMenuPanel.add(afficherButton);
        sousMenuPanel.add(modifierButton);
        sousMenuPanel.add(supprimerButton);

        gestionProduitsButton.addActionListener(e -> cardLayout.show(mainPanel, "SousMenu"));
        ajouterButton.addActionListener(e -> cardLayout.show(mainPanel, "Ajouter"));
        afficherButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Afficher");
            afficherProduits(); 
        });
        modifierButton.addActionListener(e -> cardLayout.show(mainPanel, "Modifier"));
        supprimerButton.addActionListener(e -> cardLayout.show(mainPanel, "Supprimer"));

        // Panel pour ajouter un produit
        JPanel ajouterPanel = createAjouterPanel();
        JPanel afficherPanel = createAfficherPanel();
        JPanel modifierPanel = createModifierPanel();
        JPanel supprimerPanel = createSupprimerPanel();

        // Ajouter les panels au mainPanel
        mainPanel.add(menuPrincipalPanel, "MenuPrincipal");
        mainPanel.add(sousMenuPanel, "SousMenu");
        mainPanel.add(ajouterPanel, "Ajouter");
        mainPanel.add(afficherPanel, "Afficher");
        mainPanel.add(modifierPanel, "Modifier");
        mainPanel.add(supprimerPanel, "Supprimer");

        // Ajouter le mainPanel à la fenêtre
        add(mainPanel);

        // Afficher le menu principal au démarrage
        cardLayout.show(mainPanel, "MenuPrincipal");
    }

    private JPanel createAjouterPanel() {
        JPanel ajouterPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        ajouterPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ajouterPanel.add(new JLabel("ID du produit:"));
        idField = new JTextField();
        ajouterPanel.add(idField);

        ajouterPanel.add(new JLabel("Nom du produit:"));
        nomField = new JTextField();
        ajouterPanel.add(nomField);

        ajouterPanel.add(new JLabel("Prix unitaire:"));
        prixField = new JTextField();
        ajouterPanel.add(prixField);

        ajouterPanel.add(new JLabel("Quantité en stock:"));
        quantiteField = new JTextField();
        ajouterPanel.add(quantiteField);

        JButton validerAjouterButton = new JButton("Valider");
        validerAjouterButton.setPreferredSize(new Dimension(150, 30)); // Taille du bouton
        validerAjouterButton.addActionListener(e -> ajouterProduit());
        ajouterPanel.add(validerAjouterButton);

        JButton retourAjouterButton = new JButton("Retour");
        retourAjouterButton.setPreferredSize(new Dimension(150, 30)); // Taille du bouton
        retourAjouterButton.addActionListener(e -> cardLayout.show(mainPanel, "SousMenu"));
        ajouterPanel.add(retourAjouterButton);

        return ajouterPanel;
    }

    private JPanel createAfficherPanel() {
        JPanel afficherPanel = new JPanel(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        afficherPanel.add(scrollPane, BorderLayout.CENTER);

        JButton retourAfficherButton = new JButton("Retour");
        retourAfficherButton.setPreferredSize(new Dimension(150, 30)); // Taille du bouton
        retourAfficherButton.addActionListener(e -> cardLayout.show(mainPanel, "SousMenu"));
        afficherPanel.add(retourAfficherButton, BorderLayout.SOUTH);

        return afficherPanel;
    }

    private JPanel createModifierPanel() {
        JPanel modifierPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        modifierPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        modifierPanel.add(new JLabel("ID du produit à modifier:"));
        idModifField = new JTextField();
        modifierPanel.add(idModifField);

        modifierPanel.add(new JLabel("Nom du produit:"));
        nomModifField = new JTextField();
        modifierPanel.add(nomModifField);

        modifierPanel.add(new JLabel("Prix unitaire:"));
        prixModifField = new JTextField();
        modifierPanel.add(prixModifField);

        modifierPanel.add(new JLabel("Quantité en stock:"));
        quantiteModifField = new JTextField();
        modifierPanel.add(quantiteModifField);

        JButton validerModifierButton = new JButton("Valider");
        validerModifierButton.setPreferredSize(new Dimension(150, 30)); 
        validerModifierButton.addActionListener(e -> modifierProduit());
        modifierPanel.add(validerModifierButton);

        JButton retourModifierButton = new JButton("Retour");
        retourModifierButton.setPreferredSize(new Dimension(150, 30));
        retourModifierButton.addActionListener(e -> cardLayout.show(mainPanel, "SousMenu"));
        modifierPanel.add(retourModifierButton);

        return modifierPanel;
    }

    private JPanel createSupprimerPanel() {
        JPanel supprimerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        supprimerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        supprimerPanel.add(new JLabel("ID du produit à supprimer:"));
        JTextField idSupprimerField = new JTextField();
        supprimerPanel.add(idSupprimerField);

        JButton validerSupprimerButton = new JButton("Supprimer");
        validerSupprimerButton.setPreferredSize(new Dimension(150, 30)); // Taille du bouton
        validerSupprimerButton.addActionListener(e -> supprimerProduit(idSupprimerField.getText()));
        supprimerPanel.add(validerSupprimerButton);

        JButton retourSupprimerButton = new JButton("Retour");
        retourSupprimerButton.setPreferredSize(new Dimension(150, 30)); // Taille du bouton
        retourSupprimerButton.addActionListener(e -> cardLayout.show(mainPanel, "SousMenu"));
        supprimerPanel.add(retourSupprimerButton);

        return supprimerPanel;
    }
    private void ajouterProduit() {
        int idProduit;
        String nom = nomField.getText();
        double prix;
        int quantite;

        try {
            idProduit = Integer.parseInt(idField.getText());
            prix = Double.parseDouble(prixField.getText());
            quantite = Integer.parseInt(quantiteField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un ID, un prix et une quantité valides.");
            return;
        }

        // Ajouter le produit
        String insertSql = "INSERT INTO produits (idProduit, nom, prix, quantiteEnStock) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, idProduit);
            insertStmt.setString(2, nom);
            insertStmt.setDouble(3, prix);
            insertStmt.setInt(4, quantite);
            insertStmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Produit ajouté avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du produit.");
        }
    }

    private void modifierProduit() {
        try {
            int idProduit = Integer.parseInt(idModifField.getText());
            String nom = nomModifField.getText();
            double prix = Double.parseDouble(prixModifField.getText());
            int quantite = Integer.parseInt(quantiteModifField.getText());

            String sql = "UPDATE produits SET nom = ?, prix = ?, quantiteEnStock = ? WHERE idProduit = ?";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nom);
                stmt.setDouble(2, prix);
                stmt.setInt(3, quantite);
                stmt.setInt(4, idProduit);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Produit modifié avec succès !");
                    afficherProduits(); // Refresh the list after modification
                } else {
                    JOptionPane.showMessageDialog(this, "Aucun produit trouvé avec cet ID.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs valides pour l'ID, le prix et la quantité.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification du produit.");
        }
    }

    private void supprimerProduit(String idProduitStr) {
        try {
            int idProduit = Integer.parseInt(idProduitStr);

            // Supprimer les lignes correspondantes dans la table 'lignes_facture'
            String deleteFactureSql = "DELETE FROM lignes_facture WHERE idProduit = ?";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement deleteFactureStmt = conn.prepareStatement(deleteFactureSql)) {
                deleteFactureStmt.setInt(1, idProduit);
                deleteFactureStmt.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression des lignes de facture.");
                e.printStackTrace();
                return; // Si cette étape échoue, on arrête le processus
            }

            // Supprimer les lignes correspondantes dans la table 'lignes_commande'
            String deleteCommandeSql = "DELETE FROM lignes_commande WHERE idProduit = ?";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement deleteCommandeStmt = conn.prepareStatement(deleteCommandeSql)) {
                deleteCommandeStmt.setInt(1, idProduit);
                deleteCommandeStmt.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression des lignes de commande.");
                e.printStackTrace();
                return; // Si cette étape échoue, on arrête le processus
            }

            // Enfin, supprimer le produit de la table 'produits'
            String deleteProduitSql = "DELETE FROM produits WHERE idProduit = ?";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement deleteProduitStmt = conn.prepareStatement(deleteProduitSql)) {
                deleteProduitStmt.setInt(1, idProduit);
                int rowsDeleted = deleteProduitStmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Produit supprimé avec succès !");
                } else {
                    JOptionPane.showMessageDialog(this, "Aucun produit trouvé avec cet ID.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un ID valide.");
            ex.printStackTrace();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du produit: " + ex.getMessage());
            ex.printStackTrace(); // Affiche l'exception détaillée pour débogage
        }
    }




    private void afficherProduits() {
        String sql = "SELECT * FROM produits";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            outputArea.setText("");
            while (rs.next()) {
                outputArea.append("ID: " + rs.getInt("idProduit") +
                        ", Nom: " + rs.getString("nom") +
                        ", Prix: " + rs.getDouble("prix") +
                        ", Quantité: " + rs.getInt("quantiteEnStock") + "\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des produits.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionProduitsUI().setVisible(true));
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestionCommandesUI extends JFrame {

    private JComboBox<String> clientComboBox, produitComboBox;
    private JTextField quantiteField, remiseField, searchField;
    private JTextArea outputArea;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private List<LigneCommande> lignesCommande = new ArrayList<>();
    private JLabel totalLabel;

    public GestionCommandesUI() {
        setTitle("Gestion des Commandes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createMainMenu();
        createAddOrderMenu();
        createViewOrdersMenu();

        add(mainPanel);
        cardLayout.show(mainPanel, "MainMenu");
    }

    private void createMainMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(3, 1, 10, 10)); // Espacement entre les boutons
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marge autour du panneau

        JButton addOrderButton = new JButton("Créer une commande");
        JButton viewOrdersButton = new JButton("Afficher les commandes");
        JButton exitButton = new JButton("Quitter");

        // Définir la taille des boutons
        Dimension buttonSize = new Dimension(150, 30); // Taille réduite des boutons
        addOrderButton.setPreferredSize(buttonSize);
        viewOrdersButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);

        addOrderButton.addActionListener(e -> cardLayout.show(mainPanel, "AddOrderMenu"));
        viewOrdersButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "ViewOrdersMenu");
            displayOrders();
        });
        exitButton.addActionListener(e -> System.exit(0));

        menuPanel.add(addOrderButton);
        menuPanel.add(viewOrdersButton);
        menuPanel.add(exitButton);

        mainPanel.add(menuPanel, "MainMenu");
    }

    private void createAddOrderMenu() {
        JPanel addOrderPanel = new JPanel(new GridLayout(7, 2, 10, 10)); // 7 lignes, 2 colonnes, espacement
        addOrderPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Marge autour du panneau

        clientComboBox = new JComboBox<>();
        produitComboBox = new JComboBox<>();
        quantiteField = new JTextField();
        remiseField = new JTextField("0");
        totalLabel = new JLabel("Total : 0.0 €");

        JButton addLineButton = new JButton("Ajouter Ligne");
        JButton finalizeOrderButton = new JButton("Finaliser Commande");
        JButton backButton = new JButton("Retour");

        // Définir la taille des boutons
        Dimension buttonSize = new Dimension(150, 30); // Taille des boutons
        addLineButton.setPreferredSize(buttonSize);
        finalizeOrderButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);

        addOrderPanel.add(new JLabel("Client :"));
        addOrderPanel.add(clientComboBox);
        addOrderPanel.add(new JLabel("Produit :"));
        addOrderPanel.add(produitComboBox);
        addOrderPanel.add(new JLabel("Quantité :"));
        addOrderPanel.add(quantiteField);
        addOrderPanel.add(new JLabel("Remise (%) :"));
        addOrderPanel.add(remiseField);
        addOrderPanel.add(totalLabel);
        addOrderPanel.add(new JLabel()); // Espace vide
        addOrderPanel.add(addLineButton);
        addOrderPanel.add(finalizeOrderButton);
        addOrderPanel.add(backButton);

        addLineButton.addActionListener(e -> addLine());
        finalizeOrderButton.addActionListener(e -> finalizeOrder());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        mainPanel.add(addOrderPanel, "AddOrderMenu");

        loadClients();
        loadProducts();
    }

    private void createViewOrdersMenu() {
        JPanel viewOrdersPanel = new JPanel(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Espacement
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");
        JButton exportButton = new JButton("Exporter CSV");
        JButton backButton = new JButton("Retour");

        // Définir la taille des boutons
        Dimension buttonSize = new Dimension(150, 30); // Taille des boutons
        searchButton.setPreferredSize(buttonSize);
        exportButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);

        searchButton.addActionListener(e -> searchOrders());
        exportButton.addActionListener(e -> exportToCSV());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        searchPanel.add(new JLabel("Recherche :"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(exportButton);

        viewOrdersPanel.add(scrollPane, BorderLayout.CENTER);
        viewOrdersPanel.add(searchPanel, BorderLayout.NORTH);
        viewOrdersPanel.add(backButton, BorderLayout.SOUTH);

        mainPanel.add(viewOrdersPanel, "ViewOrdersMenu");
    }
    private void loadClients() {
        String sql = "SELECT idClient, nom FROM clients";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            clientComboBox.removeAllItems();
            while (rs.next()) {
                clientComboBox.addItem(rs.getInt("idClient") + " - " + rs.getString("nom"));
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des clients : " + e.getMessage());
        }
    }

    private void loadProducts() {
        String sql = "SELECT idProduit, nom FROM produits";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            produitComboBox.removeAllItems();
            while (rs.next()) {
                produitComboBox.addItem(rs.getInt("idProduit") + " - " + rs.getString("nom"));
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des produits : " + e.getMessage());
        }
    }

    private void addLine() {
        try {
            int idProduit = Integer.parseInt(produitComboBox.getSelectedItem().toString().split(" - ")[0]);
            int quantite = Integer.parseInt(quantiteField.getText());
            double prix = getProductPrice(idProduit);
            double remise = Double.parseDouble(remiseField.getText());
            double sousTotal = prix * quantite * (1 - remise / 100);

            lignesCommande.add(new LigneCommande(idProduit, quantite, sousTotal));
            updateTotal();

            JOptionPane.showMessageDialog(this, "Ligne ajoutée !");
        } catch (Exception e) {
            showError("Erreur lors de l'ajout de la ligne : " + e.getMessage());
        }
    }

    private double getProductPrice(int idProduit) throws SQLException {
        String sql = "SELECT prix FROM produits WHERE idProduit = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProduit);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("prix");
            }
        }
        throw new SQLException("Produit non trouvé.");
    }

    private void finalizeOrder() {
        try {
            int idClient = Integer.parseInt(clientComboBox.getSelectedItem().toString().split(" - ")[0]);
            int idCommande = createOrder(idClient);
            for (LigneCommande ligne : lignesCommande) {
                addOrderLine(idCommande, ligne);
            }
            lignesCommande.clear();
            updateTotal();
            JOptionPane.showMessageDialog(this, "Commande finalisée avec succès !");
        } catch (Exception e) {
            showError("Erreur lors de la finalisation : " + e.getMessage());
        }
    }

    private int createOrder(int idClient) throws SQLException {
        String sql = "INSERT INTO commandes (idClient, date) VALUES (?, CURRENT_DATE)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, idClient);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Impossible de créer la commande.");
    }

    private void addOrderLine(int idCommande, LigneCommande ligne) throws SQLException {
        String sql = "INSERT INTO lignes_commande (idCommande, idProduit, quantite, sousTotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            stmt.setInt(2, ligne.getIdProduit());
            stmt.setInt(3, ligne.getQuantite());
            stmt.setDouble(4, ligne.getSousTotal());
            stmt.executeUpdate();
        }
    }

    private void updateTotal() {
        double total = lignesCommande.stream().mapToDouble(LigneCommande::getSousTotal).sum();
        totalLabel.setText("Total : " + total + " €");
    }

    private void displayOrders() {
        String sql = "SELECT c.idCommande, cl.nom AS client, c.date, SUM(lc.sousTotal) AS total " +
                     "FROM commandes c " +
                     "JOIN clients cl ON c.idClient = cl.idClient " +
                     "JOIN lignes_commande lc ON c.idCommande = lc.idCommande " +
                     "GROUP BY c.idCommande, cl.nom, c.date";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            outputArea.setText("Historique des commandes :\n");
            while (rs.next()) {
                outputArea.append("Commande #" + rs.getInt("idCommande") +
                        " | Client : " + rs.getString("client") +
                        " | Date : " + rs.getDate("date") +
                        " | Total : " + rs.getDouble("total") + " €\n");
            }
        } catch (SQLException e) {
            showError("Erreur d'affichage des commandes : " + e.getMessage());
        }
    }

    private void searchOrders() {
        String searchTerm = searchField.getText();
        String sql = "SELECT c.idCommande, cl.nom AS client, c.date, SUM(lc.sousTotal) AS total " +
                     "FROM commandes c " +
                     "JOIN clients cl ON c.idClient = cl.idClient " +
                     "JOIN lignes_commande lc ON c.idCommande = lc.idCommande " +
                     "WHERE cl.nom LIKE ? OR c.date LIKE ? " +
                     "GROUP BY c.idCommande, cl.nom, c.date";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            outputArea.setText("Résultats de recherche :\n");
            while (rs.next()) {
                outputArea.append("Commande #" + rs.getInt("idCommande") +
                        " | Client : " + rs.getString("client") +
                        " | Date : " + rs.getDate("date") +
                        " | Total : " + rs.getDouble("total") + " €\n");
            }
        } catch (SQLException e) {
            showError("Erreur lors de la recherche : " + e.getMessage());
        }
    }

    private void exportToCSV() {
        String filePath = "commandes.csv";
        String sql = "SELECT c.idCommande, cl.nom AS client, c.date, SUM(lc.sousTotal) AS total " +
                     "FROM commandes c " +
                     "JOIN clients cl ON c.idClient = cl.idClient " +
                     "JOIN lignes_commande lc ON c.idCommande = lc.idCommande " +
                     "GROUP BY c.idCommande, cl.nom, c.date";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();
             FileWriter writer = new FileWriter(filePath)) {

            writer.write("ID,Client,Date,Total\n");
            while (rs.next()) {
                writer.write(rs.getInt("idCommande") + "," +
                        rs.getString("client") + "," +
                        rs.getDate("date") + "," +
                        rs.getDouble("total") + "\n");
            }
            JOptionPane.showMessageDialog(this, "Données exportées avec succès dans " + filePath);
        } catch (SQLException | IOException e) {
            showError("Erreur d'exportation : " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionCommandesUI().setVisible(true));
    }

    private static class LigneCommande {
        private final int idProduit;
        private final int quantite;
        private final double sousTotal;

        public LigneCommande(int idProduit, int quantite, double sousTotal) {
            this.idProduit = idProduit;
            this.quantite = quantite;
            this.sousTotal = sousTotal;
        }

        public int getIdProduit() {
            return idProduit;
        }

        public int getQuantite() {
            return quantite;
        }

        public double getSousTotal() {
            return sousTotal;
        }
    }
}

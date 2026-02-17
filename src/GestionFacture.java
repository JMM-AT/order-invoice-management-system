import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestionFacture extends JFrame {

    private FactureService factureService;
    private LigneFactureService ligneFactureService;
    private ExportService exportService;

    public GestionFacture() {
        setTitle("Gestion des Factures");
        setSize(1000, 600); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        factureService = new FactureService();
        ligneFactureService = new LigneFactureService();
        exportService = new ExportService();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));

        JButton btnAfficherFactures = new JButton("Afficher les Factures");
        JButton btnAjouterFacture = new JButton("Ajouter une Facture");
        JButton btnAjouterLigneFacture = new JButton("Ajouter Ligne Facture");
        JButton btnSupprimerFacture = new JButton("Supprimer une Facture");

        btnAfficherFactures.addActionListener(e -> openFacturesWindow());
        btnAjouterFacture.addActionListener(e -> openAddFactureForm());
        btnAjouterLigneFacture.addActionListener(e -> openAddLigneFactureForm());
        btnSupprimerFacture.addActionListener(e -> deleteFactureAction());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3, 10, 10));
        buttonPanel.add(btnAfficherFactures);
        buttonPanel.add(btnAjouterFacture);
        buttonPanel.add(btnAjouterLigneFacture);
        buttonPanel.add(btnSupprimerFacture);

        panel.add(buttonPanel, BorderLayout.NORTH);

        JButton btnRetour = new JButton("Retour");
        btnRetour.addActionListener(e -> {
            this.dispose();  
            new Accueil().setVisible(true);  
        });

        JPanel retourPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        retourPanel.add(btnRetour);
        panel.add(retourPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void openFacturesWindow() {
        JFrame facturesWindow = new JFrame("Liste des Factures avec Détails");
        facturesWindow.setSize(1000, 600);
        facturesWindow.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));


        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JTextField searchClientField = new JTextField(20);
        JTextField searchDateField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");

        searchPanel.add(new JLabel("Rechercher par client :"));
        searchPanel.add(searchClientField);
        searchPanel.add(new JLabel("Rechercher par date :"));
        searchPanel.add(searchDateField);
        searchPanel.add(searchButton);


        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID Facture");
        tableModel.addColumn("Client");
        tableModel.addColumn("Date");
        tableModel.addColumn("Montant Total");

        JTable facturesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(facturesTable);


        JButton btnExportCSV = new JButton("Exporter en CSV");
        btnExportCSV.addActionListener(e -> {
            int idFacture = obtenirIdFacture();
            if (idFacture != -1) {
                exportService.exporterFactureCSV(idFacture);
            }
        });


        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(btnExportCSV, BorderLayout.SOUTH);


        searchButton.addActionListener(e -> {
            String client = searchClientField.getText().trim();
            String date = searchDateField.getText().trim();
            loadFactures(tableModel, client, date);
        });


        loadFactures(tableModel, "", "");

        facturesWindow.add(mainPanel);
        facturesWindow.setVisible(true);
    }

    private void loadFactures(DefaultTableModel tableModel, String clientFilter, String dateFilter) {
        tableModel.setRowCount(0); 

        String query = """
            SELECT f.idFacture, c.nom AS client, f.date, f.montantTotal
            FROM factures f
            JOIN clients c ON f.idClient = c.idClient
            WHERE c.nom LIKE ? AND f.date LIKE ?
            ORDER BY f.idFacture
            """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + clientFilter + "%");
            stmt.setString(2, "%" + dateFilter + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idFacture = rs.getInt("idFacture");
                String client = rs.getString("client");
                String date = rs.getString("date");
                double montantTotal = rs.getDouble("montantTotal");

                tableModel.addRow(new Object[]{idFacture, client, date, montantTotal});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des factures : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int obtenirIdFacture() {
        String idFactureText = JOptionPane.showInputDialog(
                this,
                "Entrez l'ID de la facture à exporter :",
                "Exporter Facture",
                JOptionPane.QUESTION_MESSAGE
        );

        if (idFactureText == null) {
            return -1;
        }

        idFactureText = idFactureText.trim();

        if (idFactureText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'ID de la facture ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return -1;
        }

        try {
            return Integer.parseInt(idFactureText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "L'ID de la facture doit être un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }
    private void openAddFactureForm() {
        JFrame addFactureFrame = new JFrame("Ajouter une Facture");
        addFactureFrame.setSize(400, 300);
        addFactureFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addFactureFrame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField idClientField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField remiseField = new JTextField();

        panel.add(new JLabel("ID Client:"));
        panel.add(idClientField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Remise (en %):"));
        panel.add(remiseField);

        addFactureFrame.add(panel, BorderLayout.CENTER);

        JButton btnSave = new JButton("Sauvegarder");
        btnSave.addActionListener(e -> {
            String idClientText = idClientField.getText().trim();
            String dateText = dateField.getText().trim();
            String remiseText = remiseField.getText().trim();

            if (idClientText.isEmpty() || dateText.isEmpty() || remiseText.isEmpty()) {
                JOptionPane.showMessageDialog(addFactureFrame, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int idClient = Integer.parseInt(idClientText);
                double remise = Double.parseDouble(remiseText);

                if (remise < 0 || remise > 100) {
                    JOptionPane.showMessageDialog(addFactureFrame, "La remise doit être un pourcentage entre 0 et 100.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!factureService.existsInDatabase("clients", "idClient", idClient)) {
                    JOptionPane.showMessageDialog(addFactureFrame, "Le client avec l'ID " + idClient + " n'existe pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (factureService.ajouterFacture(idClient, dateText)) {
                    JOptionPane.showMessageDialog(addFactureFrame, "Facture ajoutée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    addFactureFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(addFactureFrame, "Erreur lors de l'ajout de la facture.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addFactureFrame, "Les champs doivent être des nombres valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> addFactureFrame.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        addFactureFrame.add(buttonPanel, BorderLayout.SOUTH);

        addFactureFrame.setVisible(true);
    }

    private void openAddLigneFactureForm() {
        JFrame addFrame = new JFrame("Ajouter une Ligne de Facture");
        addFrame.setSize(400, 300);
        addFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addFrame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField idFactureField = new JTextField();
        JTextField idProduitField = new JTextField();
        JTextField quantiteField = new JTextField();
        JTextField remiseField = new JTextField();

        panel.add(new JLabel("ID Facture:"));
        panel.add(idFactureField);
        panel.add(new JLabel("ID Produit:"));
        panel.add(idProduitField);
        panel.add(new JLabel("Quantité:"));
        panel.add(quantiteField);
        panel.add(new JLabel("Remise (en %):"));
        panel.add(remiseField);

        addFrame.add(panel, BorderLayout.CENTER);

        JButton btnSave = new JButton("Sauvegarder");
        btnSave.addActionListener(e -> {
            String idFacture = idFactureField.getText().trim();
            String idProduit = idProduitField.getText().trim();
            String quantite = quantiteField.getText().trim();
            String remise = remiseField.getText().trim();

            if (idFacture.isEmpty() || idProduit.isEmpty() || quantite.isEmpty() || remise.isEmpty()) {
                JOptionPane.showMessageDialog(addFrame, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int idFactureInt = Integer.parseInt(idFacture);
                int idProduitInt = Integer.parseInt(idProduit);
                int quantiteInt = Integer.parseInt(quantite);
                double remiseValue = Double.parseDouble(remise);

                if (remiseValue < 0 || remiseValue > 100) {
                    JOptionPane.showMessageDialog(addFrame, "La remise doit être entre 0 et 100.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!factureService.existsInDatabase("factures", "idFacture", idFactureInt)) {
                    JOptionPane.showMessageDialog(addFrame, "La facture avec l'ID " + idFactureInt + " n'existe pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!factureService.existsInDatabase("produits", "idProduit", idProduitInt)) {
                    JOptionPane.showMessageDialog(addFrame, "Le produit avec l'ID " + idProduitInt + " n'existe pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (ligneFactureService.ajouterLigneFacture(idFactureInt, idProduitInt, quantiteInt, remiseValue)) {
                    JOptionPane.showMessageDialog(addFrame, "Ligne de facture ajoutée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    addFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(addFrame, "Erreur lors de l'ajout de la ligne de facture.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addFrame, "Veuillez entrer des valeurs valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> addFrame.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        addFrame.add(buttonPanel, BorderLayout.SOUTH);

        addFrame.setVisible(true);
    }

    private void deleteFactureAction() {
        String idFacture = JOptionPane.showInputDialog(this, "Entrez l'ID de la facture à supprimer :");

        if (idFacture != null && !idFacture.trim().isEmpty()) {
            try {
                int idFactureInt = Integer.parseInt(idFacture);

                if (!factureService.existsInDatabase("factures", "idFacture", idFactureInt)) {
                    JOptionPane.showMessageDialog(this, "La facture avec l'ID " + idFactureInt + " n'existe pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer la facture avec l'ID " + idFactureInt + " ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (factureService.deleteFacture(idFactureInt)) {
                        JOptionPane.showMessageDialog(this, "Facture supprimée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la facture.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "L'ID de la facture doit être un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	GestionFacture app = new GestionFacture();
            app.setVisible(true);
        });
    }
}
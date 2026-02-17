import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class ExportService {

    public void exporterFactureCSV(int idFacture) {
        try (Connection conn = DatabaseManager.connect()) {
            String factureQuery = "SELECT f.idFacture, f.date, f.montantTotal, c.nom AS clientNom " +
                                  "FROM factures f INNER JOIN clients c ON f.idClient = c.idClient WHERE f.idFacture = ?";
            String ligneFactureQuery = "SELECT lf.idLigne, p.nom AS produitNom, lf.quantite, lf.sousTotal " +
                                       "FROM lignes_facture lf INNER JOIN produits p ON lf.idProduit = p.idProduit WHERE lf.idFacture = ?";

            try (PreparedStatement factureStmt = conn.prepareStatement(factureQuery);
                 PreparedStatement ligneFactureStmt = conn.prepareStatement(ligneFactureQuery)) {

                factureStmt.setInt(1, idFacture);
                ligneFactureStmt.setInt(1, idFacture);

                File csvFile = new File("Facture_" + idFacture + ".csv");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {

                    try (ResultSet factureRs = factureStmt.executeQuery()) {
                        if (factureRs.next()) {
                            writer.write("Facture ID,Date,Client,Montant Total");
                            writer.newLine();
                            writer.write(factureRs.getInt("idFacture") + "," +
                                         factureRs.getString("date") + "," +
                                         factureRs.getString("clientNom") + "," +
                                         factureRs.getDouble("montantTotal"));
                            writer.newLine();
                            writer.newLine();
                        } else {
                            JOptionPane.showMessageDialog(null, "Facture introuvable !", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    writer.write("Ligne ID,Produit,Quantité,Sous Total");
                    writer.newLine();

                    try (ResultSet ligneFactureRs = ligneFactureStmt.executeQuery()) {
                        while (ligneFactureRs.next()) {
                            writer.write(ligneFactureRs.getInt("idLigne") + "," +
                                         ligneFactureRs.getString("produitNom") + "," +
                                         ligneFactureRs.getInt("quantite") + "," +
                                         ligneFactureRs.getDouble("sousTotal"));
                            writer.newLine();
                        }
                    }

                    JOptionPane.showMessageDialog(null, "Facture exportée avec succès : " + csvFile.getAbsolutePath(), "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de l'exportation de la facture : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
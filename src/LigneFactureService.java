import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LigneFactureService {

    public boolean ajouterLigneFacture(int idFacture, int idProduit, int quantite, double remiseLigne) {
        try (Connection conn = DatabaseManager.connect()) {
            double prixProduit = getPrixProduit(idProduit);
            double sousTotal = prixProduit * quantite * (1 - remiseLigne / 100);

            String query = "INSERT INTO lignes_facture (idFacture, idProduit, quantite, sousTotal) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idFacture);
                stmt.setInt(2, idProduit);
                stmt.setInt(3, quantite);
                stmt.setDouble(4, sousTotal);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    double montantTotal = recalculerMontantTotal(idFacture);
                    return mettreAJourMontantTotal(idFacture, montantTotal);
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la ligne de facture : " + e.getMessage());
            return false;
        }
    }

    private double recalculerMontantTotal(int idFacture) {
        double montantTotal = 0.0;
        try (Connection conn = DatabaseManager.connect()) {
            String query = "SELECT sousTotal FROM lignes_facture WHERE idFacture = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idFacture);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        montantTotal += rs.getDouble("sousTotal");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du calcul du montant total : " + e.getMessage());
        }
        return montantTotal;
    }

    private boolean mettreAJourMontantTotal(int idFacture, double montantTotal) {
        try (Connection conn = DatabaseManager.connect()) {
            String query = "UPDATE factures SET montantTotal = ? WHERE idFacture = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setDouble(1, montantTotal);
                stmt.setInt(2, idFacture);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du montant total de la facture : " + e.getMessage());
            return false;
        }
    }

    private double getPrixProduit(int idProduit) {
        try (Connection conn = DatabaseManager.connect()) {
            String query = "SELECT prix FROM produits WHERE idProduit = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idProduit);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("prix");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du prix du produit : " + e.getMessage());
        }
        return 0.0;
    }
}
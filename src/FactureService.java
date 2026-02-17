import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FactureService {

    public boolean ajouterFacture(int idClient, String date) {
        try (Connection conn = DatabaseManager.connect()) {
            String query = "INSERT INTO factures (idClient, date, montantTotal) VALUES (?, ?, 0)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idClient);
                stmt.setString(2, date);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la facture : " + e.getMessage());
            return false;
        }
    }

    public boolean deleteFacture(int idFacture) {
        try (Connection conn = DatabaseManager.connect()) {
            String query = "DELETE FROM factures WHERE idFacture = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idFacture);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la facture : " + e.getMessage());
            return false;
        }
    }

    public boolean updateFacture(int idFacture, String newDate) {
        try (Connection conn = DatabaseManager.connect()) {
            String query = "UPDATE factures SET date = ? WHERE idFacture = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, newDate);
                stmt.setInt(2, idFacture);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la facture : " + e.getMessage());
            return false;
        }
    }

    public boolean existsInDatabase(String table, String column, int id) {
        try (Connection conn = DatabaseManager.connect()) {
            String query = "SELECT 1 FROM " + table + " WHERE " + column + " = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification dans la base de données : " + e.getMessage());
            return false;
        }
    }
}
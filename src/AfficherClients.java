import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AfficherClients extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public AfficherClients() {
        setTitle("Afficher Clients");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Email", "Téléphone"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        

       

        loadClients();
        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> {
            new SysGestionClient().setVisible(true);
            setVisible(false);
        });
        add(backButton, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);
        
    }

    private void loadClients() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM clients")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("idClient"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("telephone")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des clients : " + e.getMessage());
        }
    }
}
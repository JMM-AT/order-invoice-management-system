import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SysGestionClient extends JFrame {
    public SysGestionClient() {
        setTitle("Système de Gestion des Clients");
        setSize(300, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        JButton addButton = new JButton("Ajouter Client");
        JButton showButton = new JButton("Afficher Clients");
        JButton deleteButton = new JButton("Supprimer Client");
        JButton updateButton = new JButton("Modifier Client");
        JButton exitButton = new JButton("Retour");
       ArrayList<JButton> list=new ArrayList<JButton>();
       list.add(addButton);
       list.add(exitButton);
       list.add(updateButton);
       list.add(deleteButton);
       list.add(showButton);
      for(int i =0;i<list.size();i++) {
    	  ((Component) list.get(i)).setPreferredSize(new Dimension(200, 50));  
    	  ((Component) list.get(i)).setMinimumSize(new Dimension(100, 40));    
    	  ((Component) list.get(i)).setMaximumSize(new Dimension(300, 80));
      }
       
        
       
        panel.add(addButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(showButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(deleteButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(updateButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(exitButton);


        add(panel);


        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AjouterClient().setVisible(true);
                setVisible(false); 
            }
        });

        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AfficherClients().setVisible(true);
                setVisible(false);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SupprimerClient().setVisible(true);
                setVisible(false);
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ModifierClient().setVisible(true);
                setVisible(false);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	new Accueil().setVisible(true); 
                setVisible(false); 
            }
            
        });
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SysGestionClient().setVisible(true));
    }
}

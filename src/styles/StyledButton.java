package styles;



import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StyledButton extends JButton {
    public StyledButton(String text) {
        super(text);
        setContentAreaFilled(false); 
        setFocusPainted(false); 
        setBorderPainted(false); 
        setForeground(Color.WHITE); 
        setFont(new Font("Arial", Font.BOLD, 14)); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

       
        if (getModel().isPressed()) {
            g2.setColor(new Color(41, 128, 185)); 
        } else if (getModel().isRollover()) {
            g2.setColor(new Color(52, 152, 219)); 
        } else {
            g2.setColor(new Color(52, 152, 219)); 
        }

        
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        g2.dispose();

        super.paintComponent(g);
    }
}
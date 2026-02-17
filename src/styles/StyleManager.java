package styles;



import javax.swing.*;
import java.awt.*;

public class StyleManager {
    public static void applyGlobalStyle() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        UIManager.put("Panel.background", new Color(240, 240, 240));
        UIManager.put("Button.background", new Color(52, 152, 219)); 
        UIManager.put("Button.foreground", Color.WHITE); 
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14)); 
        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 14)); 
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("Arial", Font.PLAIN, 14)); 
        UIManager.put("Table.font", new Font("Arial", Font.PLAIN, 14)); 
        UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 14)); 
    }
}

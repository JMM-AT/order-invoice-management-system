package styles;



import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StyledTable extends JTable {
    public StyledTable() {

    	setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }
                return c;
            }
        });


    	getTableHeader().setBackground(new Color(52, 152, 219));
        getTableHeader().setForeground(Color.WHITE);
        getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    }
}

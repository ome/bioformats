package loci.ome.notebook;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Dimension;

public class GotoRenderer extends JButton
  implements TableCellRenderer
{
  public GotoRenderer() {
    super("Goto");
    setActionCommand("goto");
    setPreferredSize(new Dimension(70, 15));
    setOpaque(true); //MUST do this for background to show up.
  }

  public Component getTableCellRendererComponent(
                          JTable table, Object value,
                          boolean isSelected, boolean hasFocus,
                          int row, int column) {
    if (value == null) {
      setToolTipText("Not a reference.");
      return new JLabel();
    }
    else {
      setToolTipText("Go to the referenced table.");
      return this;
    }
  }
}

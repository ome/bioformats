package loci.ome.notebook;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.EmptyBorder;

import java.awt.Component;
import java.awt.BorderLayout;

public class VariableTextAreaRenderer extends JPanel
  implements TableCellRenderer
{
  public static final ImageIcon TEXT_BULLET = 
    MetadataPane.createImageIcon("Icons/TextBullet.GIF",
    "An icon signifying that a textarea will edit this cell.");

  private JLabel jl;  

  public VariableTextAreaRenderer() {
    super();
	  jl = new JLabel();
    JLabel iconL = new JLabel(TEXT_BULLET);
    iconL.setBorder(new EmptyBorder(0,2,0,2));
    setLayout(new BorderLayout());
    add(jl, BorderLayout.WEST);
    add(iconL, BorderLayout.EAST);
    setOpaque(false);
  }
  
  public Component getTableCellRendererComponent(
                          JTable table, Object value,
                          boolean isSelected, boolean hasFocus,
                          int row, int column) {
    jl.setText( (String) value );
    return this;
  }
  
  //overidden for performance reasons... see jdk 1.4 doc
  //for details as to why I did this
//  public void validate() {}
//  public void revalidate() {}
  public void repaint(long tm, int x, int y,
    int width, int height) {}
  public void firePropertyChange(String propertyName,
    boolean oldValue, boolean newValue) {}
  protected void firePropertyChange(String propertyName,
    Object oldValue, Object newValue) {}
}
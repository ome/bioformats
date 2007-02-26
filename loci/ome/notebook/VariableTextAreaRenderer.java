//
// VariableTextAreaRenderer.java
//

/*
OME Metadata Notebook application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006-@year@ Christopher Peterson.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.ome.notebook;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * A class to handle how to display a JTextArea edited
 * cell when it's not being edited. Basically displays
 * the first bit of text in a label and a nifty icon on
 * the right side to signify that this cell will expand.
 * If the text is too long to fit into the cell then the
 * label will be clipped and ... will be added.
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class VariableTextAreaRenderer extends JPanel
  implements TableCellRenderer
{
  /** A nifty icon to signify that a textare will edit this cell.*/
  public static final ImageIcon TEXT_BULLET =
    MetadataPane.createImageIcon("Icons/text-bullet.gif",
    "An icon signifying that a textarea will edit this cell.");

  /** The label that will hold the text value of this cell.*/
  private JLabel jl;

  /** Construct a new VariableTextAreaRenderer.*/
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

  /**
  * The method that a table calls to get the renderer component
  * of a particular cell. Returns a simple JPanel here with a label
  * and an icon added to it.
  */
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column)
  {
    String result = (String) value;
    Font aFont = jl.getFont();
    FontMetrics fm = getFontMetrics(aFont);
    int stringWidth = -1;
    if(result != null) stringWidth = fm.stringWidth(result);
    else stringWidth = 0;
    int colWidth = table.getColumnModel().getColumn(1).getWidth();
    if (stringWidth > colWidth - 15) {
      for(int i=result.length();i>0;i--) {
        String subValue = result.substring(0,i);
        int subWidth = fm.stringWidth(subValue);
        if (subWidth < colWidth - 30) {
          result = subValue + "...";
          break;
        }
      }
    }
    jl.setText(result);
    return this;
  }

  //overidden for performance reasons... see jdk 1.4 doc
  //for details as to why I did this
//  public void validate() {}
//  public void revalidate() {}
  /**
  * Made no-op for performance reasons... see jdk 1.4 API
  * Specifications on DefaultCellRenderer for details.
  */
  public void repaint(long tm, int x, int y,
    int width, int height) {}
  /**
  * Made no-op for performance reasons... see jdk 1.4 API
  * Specifications on DefaultCellRenderer for details.
  */
  public void firePropertyChange(String propertyName,
    boolean oldValue, boolean newValue) {}
  /**
  * Made no-op for performance reasons... see jdk 1.4 API
  * Specifications on DefaultCellRenderer for details.
  */
  protected void firePropertyChange(String propertyName,
    Object oldValue, Object newValue) {}

}

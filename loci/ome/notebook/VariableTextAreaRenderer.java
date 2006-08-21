//
// VariableTextAreaRenderer.java
//

/*
OME Metadata Notebook application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006 Christopher Peterson.

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
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class VariableTextAreaRenderer extends JPanel
  implements TableCellRenderer
{

  public static final ImageIcon TEXT_BULLET =
    MetadataPane.createImageIcon("Icons/text-bullet.gif",
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

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column)
  {
    jl.setText((String) value);
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

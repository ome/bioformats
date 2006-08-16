//
// GotoRenderer.java
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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;

public class GotoRenderer extends JButton
  implements TableCellRenderer
{
  public GotoRenderer() {
    super("Goto");
    setActionCommand("goto");
    setPreferredSize(new Dimension(70, 15));
    setForeground(new Color(0,0,50));
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

//
// GotoRenderer.java
//

/*
OME Metadata Editor application for exploration and editing of OME-XML and
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

package loci.ome.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * A class to handle the appearance of "Goto" buttons
 * that are within a cell of a given ClickableTable.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/ome/editor/GotoRenderer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/ome/editor/GotoRenderer.java">SVN</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class GotoRenderer extends JButton implements TableCellRenderer {

  /**Construct a GotoRenderer.*/
  public GotoRenderer() {
    super("Goto");
    setActionCommand("goto");
    setPreferredSize(new Dimension(70, 15));
    setForeground(new Color(0,0,50));
    setOpaque(true); //MUST do this for background to show up.
  }

  /**
  * Overide the TableCellRenderer method to give back the appropriate
  * component to make the buttons appear even when not clicked on.
  */
  public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
  {
    if (value == null) {
      setToolTipText("Not a reference.");
      return new JLabel();
    }
    else {
      setToolTipText("Go to the referenced table.");
      return this;
    }
  }

  //overidden for performance reasons... see jdk 1.4 doc
  //for details as to why I did this
  public void validate() {}
  public void revalidate() {}
  public void repaint(long tm, int x, int y, int width, int height) {}
  public void firePropertyChange(String propertyName,
    boolean oldValue, boolean newValue) {}
  protected void firePropertyChange(String propertyName,
    Object oldValue, Object newValue) {}
}

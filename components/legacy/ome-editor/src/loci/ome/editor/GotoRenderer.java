/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/GotoRenderer.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/GotoRenderer.java;hb=HEAD">Gitweb</a></dd></dl>
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

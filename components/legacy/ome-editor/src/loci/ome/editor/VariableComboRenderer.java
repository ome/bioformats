/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
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

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * A class to handle how to display a combobox cell even
 * when the cell is not being edited.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/VariableComboRenderer.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/VariableComboRenderer.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class VariableComboRenderer extends JComboBox
  implements TableCellRenderer
{
  /** Construct a new VariableComboRenderer.*/
  public VariableComboRenderer() { super(); }

  /**
  * Returns the component that should be displayed by the table in
  * a reference cell when it is not being edited.
  */
  public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column)
  {
    removeAllItems();
    addItem(value);
    setBorder((EmptyBorder) null);
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

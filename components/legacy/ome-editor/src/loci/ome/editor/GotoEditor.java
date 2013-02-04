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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * A class to handle the behavior of the Goto buttons
 * that are within a cell of a given ClickableTable.
 * These buttons should go to the referenced TabPanel.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/GotoEditor.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/GotoEditor.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class GotoEditor extends AbstractCellEditor
  implements TableCellEditor, ActionListener
{
  /**The "Goto" button we're editing.*/
  TableButton button;

  /**The TablePanel this TableEditor is for.*/
  MetadataPane.TablePanel tableP;

  /**Construct a GotoEditor for a given TablePanel*/
  public GotoEditor(MetadataPane.TablePanel tp) {
    button = null;
    tableP = tp;
  }

  /**Sets up which button was being clicked, stops editing*/
  public void actionPerformed(ActionEvent e) {
    button = (TableButton) e.getSource();
    fireEditingStopped();
  }

  /** Implement the one CellEditor method that AbstractCellEditor doesn't. */
  public Object getCellEditorValue() {
      return button;
  }

  /** Override the method defined by AbstractCellEditor. */
  public Component getTableCellEditorComponent(JTable table,
    Object value, boolean isSelected, int row, int column)
  {
    if (row < tableP.attrList.size()) return null;
    button = new TableButton(tableP.table, row);
    button.setActionCommand("Goto");
    button.addActionListener(this);
    button.addActionListener(tableP);
    button.setForeground(new Color(0,0,50));
    return button;
  }

  /**
   * Helper class to handle the "GOTO" buttons that take you to a particular
   * Element ID's representation in the program.
   */
  public class TableButton extends JButton {
    public JTable table;
    public int whichRow;

    public TableButton( JTable jt, int i) {
      super("Goto");
      table = jt;
      whichRow = i;
      Integer aInt = new Integer(i);
      setActionCommand("goto");
      setPreferredSize(new Dimension(70, 15));
    }
  }

}

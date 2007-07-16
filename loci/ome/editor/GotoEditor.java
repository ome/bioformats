//
// GotoEditor.java
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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.*;

/*
* A class to handle the behavior of the Goto buttons
* that are within a cell of a given ClickableTable.
* These buttons should go to the referenced TabPanel.
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

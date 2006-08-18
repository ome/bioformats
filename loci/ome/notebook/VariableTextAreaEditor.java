//
// VariableTextAreaEditor.java
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

import java.awt.Component;
import java.awt.event.FocusEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;

public class VariableTextAreaEditor extends VariableTextEditor
  implements TableCellEditor
{

  public VariableTextAreaEditor(MetadataPane.TablePanel tp) { super(tp); }

  // -- TableCellEditor API methods --

  public Component getTableCellEditorComponent(JTable table, Object value,
    boolean isSelected, int row, int column)
  {
    table.setRowHeight(row, table.getRowHeight() * 4);
    JTextArea thisText = new JTextArea(new RowDoc(row), (String) value, 1, 4);
    thisText.setLineWrap(true);
    thisText.setWrapStyleWord(true);
    thisText.setEditable(true);
    thisText.getDocument().addDocumentListener(this);
    thisText.addFocusListener(this);
    thisText.addMouseListener(this);
    JScrollPane jScr = new JScrollPane(thisText);
    return jScr;
  }

  public void focusLost(FocusEvent e) {
    if (e.getSource() instanceof JTextArea) {
      JTextArea jta = (JTextArea) e.getSource();
      RowDoc rd = (RowDoc) jta.getDocument();
      tableP.table.setRowHeight(rd.row, tableP.table.getRowHeight());
      fireEditingStopped();
    }
  }

}

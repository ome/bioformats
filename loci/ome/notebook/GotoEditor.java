package loci.ome.notebook;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.AbstractCellEditor;
import java.awt.Component;
import java.awt.Dimension;

public class GotoEditor extends AbstractCellEditor
  implements TableCellEditor, ActionListener {
  
  TableButton button;
  MetadataPane.TablePanel tableP;

  public GotoEditor(MetadataPane.TablePanel tp) {
      button = null;
      tableP = tp;
  }

  public void actionPerformed(ActionEvent e) {
    button = (TableButton) e.getSource();
    fireEditingStopped();
  }

  //Implement the one CellEditor method that AbstractCellEditor doesn't.
  public Object getCellEditorValue() {
      return button;
  }

  //Implement the one method defined by TableCellEditor.
  public Component getTableCellEditorComponent(JTable table,
    Object value, boolean isSelected, int row, int column)
  {
    if (row < tableP.attrList.size()) return null;
    else {
      button = new TableButton(tableP.table, row);                                         
      button.setActionCommand("Goto");
      button.addActionListener(this);
      button.addActionListener(tableP);
      return button;
    }
  }
  
  /** Helper class to handle the "GOTO" buttons that take you to a particular
  *   Element ID's representation in the program.
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
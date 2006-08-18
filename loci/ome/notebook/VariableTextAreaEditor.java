package loci.ome.notebook;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.awt.event.FocusEvent;

public class VariableTextAreaEditor extends VariableTextEditor
  implements TableCellEditor
{  
  public VariableTextAreaEditor(MetadataPane.TablePanel tp) {
    super(tp);
	}

  //Implement the one method defined by TableCellEditor.
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
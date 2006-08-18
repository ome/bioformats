package loci.ome.notebook;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import java.awt.Component;

public class VariableTextFieldEditor extends VariableTextEditor
  implements TableCellEditor
{  
  public VariableTextFieldEditor(MetadataPane.TablePanel tp) {
    super(tp);
	}

  //Implement the one method defined by TableCellEditor.
  public Component getTableCellEditorComponent(JTable table, Object value,
    boolean isSelected, int row, int column)
  {
    JTextField text = new JTextField(new RowDoc(row), (String) value, 1); 
    text.getDocument().addDocumentListener(this);
    text.addFocusListener(this);
    text.addMouseListener(this);
    text.addActionListener(this);
    return text;
  }
}
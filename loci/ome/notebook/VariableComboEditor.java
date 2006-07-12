import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import java.awt.Component;

public class VariableComboEditor extends AbstractCellEditor
  implements TableCellEditor {

  JComboBox jBox;

  public VariableComboEditor(JComboBox jcb) {
    jBox = jcb;
  }

//Implement the one CellEditor method that AbstractCellEditor doesn't.
  public Object getCellEditorValue() {
    return jBox.getSelectedItem();
  }

//Implement the one method defined by TableCellEditor.
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	return jBox;
  }
}

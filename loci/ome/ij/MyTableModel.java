import javax.swing.table.DefaultTableModel;

/**
 * MyTableModel is the class that handles
 * the table used in displaying the images
 * selectable for download.
 * @author Philip Huettl pmhuettl@wisc.edu
 */

public class MyTableModel extends DefaultTableModel {
 
  public MyTableModel(Object[][] data, Object[] columnNames) {
    super(data, columnNames);
  }
  
  public Class getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }
  
  public boolean isCellEditable(int row, int col) {
    //Note that the data/cell address is constant,
    //no matter where the cell appears onscreen.
    if (col == 0)  return true;
    return false;
  }
}

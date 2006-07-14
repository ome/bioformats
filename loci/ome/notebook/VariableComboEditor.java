//
//  VariableComboEditor.java
//

package loci.ome.notebook;

import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Hashtable;
import java.util.Vector;

import org.openmicroscopy.xml.OMEXMLNode;

public class VariableComboEditor extends AbstractCellEditor
  implements TableCellEditor, ActionListener {

  private Vector idPanels, addPanels;

  private OMEXMLNode oNode;

  private JRowBox box;

  private JTable refTable;

  private static final Hashtable REF_HASH = TemplateParser.getRefHash();

  public VariableComboEditor(Vector IDP, Vector AddP, OMEXMLNode oN) {
    idPanels = IDP;
    addPanels = AddP;
    oNode = oN;
    box = new JRowBox(-1);
//    box.addItem("This is the default combo editor");
  }

// -- VariableComboEditor API --
  public void addAll(JRowBox jrb) {
    for (int i = 0;i<idPanels.size();i++) {
      MetadataPane.TablePanel tp = (MetadataPane.TablePanel) idPanels.get(i);
      jrb.addItem(tp.name);
    }
    for (int i = 0;i<addPanels.size();i++) {
      jrb.addItem( (String) addPanels.get(i) );
    }
  }

// -- AbstractCellEditor Implementation --

//Implement the one CellEditor method that AbstractCellEditor doesn't.
  public Object getCellEditorValue() {
    return box.getSelectedItem();
  }

//Implement the one method defined by TableCellEditor.
  public Component getTableCellEditorComponent(JTable table, Object value,
    boolean isSelected, int row, int column)
  {
    refTable = table;
    JRowBox thisBox = new JRowBox(row);
    TableModel tModel = table.getModel();
    String eleName = oNode.getDOMElement().getTagName();
    String attrName = (String) tModel.getValueAt(row, 0);
    Hashtable subHash = (Hashtable) REF_HASH.get(eleName);
    if (subHash != null) {
      String type = (String) subHash.get(attrName);
      if (type != null) {
        for(int i = 0;i<idPanels.size();i++) {
          MetadataPane.TablePanel tp =
            (MetadataPane.TablePanel) idPanels.get(i);
          String tpClass = tp.oNode.getClass().getName();
          boolean isCorrectType =
            tpClass.equals("org.openmicroscopy.xml.st." + type + "Node");
          if (isCorrectType) thisBox.addItem(tp.name);
        }

        for(int i = 0;i<addPanels.size();i++) {
          String thisExID = (String) addPanels.get(i);
          if (thisExID.indexOf(":" + type + ":") >= 0) {
            thisBox.addItem(thisExID);
          }
        }
      }
      else addAll(thisBox);
    }
    else addAll(thisBox);
    thisBox.addActionListener(this);

    thisBox.setSelectedItem(value);

    return thisBox;
  }

// -- EventListener API --

  public void actionPerformed(ActionEvent e) {
    if( e.getSource() instanceof JRowBox) {
      box = (JRowBox) e.getSource();
      int row = box.row;

      TableModel model = (TableModel) refTable.getModel();
      String data = (String) box.getSelectedItem();
      String attr = (String) model.getValueAt(row,0);
      String thisID = null;
      if(data == null) oNode.setAttribute(attr,"");
      else {
        for(int i = 0;i<idPanels.size();i++) {
          MetadataPane.TablePanel tp =
            (MetadataPane.TablePanel) idPanels.get(i);
          if(data.equals(tp.name)) thisID = tp.id;
        }
        if (thisID != null) {
          oNode.setAttribute(attr, thisID);
        }
        else {
          for(int i = 0;i<addPanels.size();i++) {
            String s = (String) addPanels.get(i);
            if(data.equals(s)) {
              oNode.setAttribute(attr, s.substring(11));
            }
          }
        }
      }
      fireEditingStopped();
    }
  }

// -- Helper Classes --

  public class JRowBox extends JComboBox {
    public int row;

    public JRowBox (int r) {
      super();
      row = r;
    }
  }
}

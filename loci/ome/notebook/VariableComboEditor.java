/*
* VariableComboEditor.java
*
*   a class that allows a table to have comboboxes
* that have different selections depending on the row
* specified, e.g. a row with "Group" that's a reference
* should only have LSID selections that are also Groups
*/

/*
* Written by:  Christopher Peterson  <crpeterson2@wisc.edu>
*/

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

  // -- Fields --

  //Vectors that hold Panels that have ID (idPanels)
  //and also all external references in the file (addPanels)
  private Vector idPanels, addPanels;

  //refers to the node associated with the TablePanel that
  //holds the table which uses this cell editor
  private OMEXMLNode oNode;

  //holds the current combobox needed when the cell is clicked
  private JRowBox box;

  //holds the table of references this cell editor is for
  private JTable refTable;
  
  //holds internal reference data found in the ome-xml file
  //that is currently opened by MetadataPane
  private Hashtable iDefs;

  // -- Constants --

  //get the hashtable describing which attributes refer to what
  //type of nodes
  private static final Hashtable REF_HASH = TemplateParser.getRefHash();

  // -- Constructor --

  //IDP : vector of TablePanels that have been found to have ID attributes
  //AddP : vector of Strings that hold all external LSIDs found in this file
  //oN : the OMEXMLNode associated with the TablePanel this editor edits
  //internalDefs : a hashtable representing semantic type defs found in
  //    the current open file itself
  public VariableComboEditor(Vector IDP, Vector AddP, 
    OMEXMLNode oN, Hashtable internalDefs)
  {
		//initialize all fields
    idPanels = IDP;
    addPanels = AddP;
    oNode = oN;
    box = new JRowBox(-1);
    iDefs = internalDefs;
  }

  // -- VariableComboEditor API --
  
  //if we give up on tailoring the combobox to the appropriate types,
  //this method simply adds all possible references of all types to
  //the combobox
  public void addAll(JRowBox jrb) {
    //add internal references
    for (int i = 0;i<idPanels.size();i++) {
      MetadataPane.TablePanel tp = (MetadataPane.TablePanel) idPanels.get(i);
      jrb.addItem(tp.name);
    }
    //add external references
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
    //use parameters to get information about the cell being edited
    refTable = table;
    JRowBox thisBox = new JRowBox(row);
    TableModel tModel = table.getModel();
    //tagname of the node associated with this table
    String eleName = oNode.getDOMElement().getTagName();
    //name of the attribute being edited
    String attrName = (String) tModel.getValueAt(row, 0);
    //get the sub-hashtable for the key associated with this tablepanel's
    //OMEXMLNode... check internal defs first, then external defs
    Hashtable subHash = (Hashtable) iDefs.get(eleName);
    if (subHash == null) subHash = (Hashtable) REF_HASH.get(eleName);
    if (subHash != null) {
      //get the string representation of the type this attribute should
      //refer to
      String type = (String) subHash.get(attrName);
      if (type != null) {
        //check the list of internal references for nodes of the
        //type that we just found
        for(int i = 0;i<idPanels.size();i++) {
          MetadataPane.TablePanel tp =
            (MetadataPane.TablePanel) idPanels.get(i);
          String tpClass = tp.oNode.getClass().getName();
          boolean isCorrectType =
            tpClass.equals("org.openmicroscopy.xml.st." + type + "Node");
          //if the node is of the right type, add it to the combobox
          if (isCorrectType) thisBox.addItem(tp.name);
        }

         //check the list of external references for references of
         //the appropriate type
        for(int i = 0;i<addPanels.size();i++) {
          String thisExID = (String) addPanels.get(i);
          if (thisExID.indexOf(":" + type + ":") >= 0) {
            //add this external reference to the combobox
            thisBox.addItem(thisExID);
          }
        }
      }
      //if no type is found for this attribute, add all internal/external
      //reference options to the combobox
      else addAll(thisBox);
    }
    //if no attributes are found for this element, add all references 
    else addAll(thisBox);

    //listen to changes in this combobox
    thisBox.addActionListener(this);
		//set the initial item in the combobox based on the parameter "value"
    thisBox.setSelectedItem(value);

    return thisBox;
  }

// -- EventListener API --

  public void actionPerformed(ActionEvent e) {
    //check if the action was done by a combobox
    if( e.getSource() instanceof JRowBox) {
      box = (JRowBox) e.getSource();
      //get the row this JRowBox is made to edit
      int row = box.row;

      TableModel model = (TableModel) refTable.getModel();
      //get the new value of the attribute selected in the combobox
      String data = (String) box.getSelectedItem();
      //get the attribute of this row from the table
      String attr = (String) model.getValueAt(row,0);
      //set default of thisID as null
      String thisID = null;
      if(data == null) oNode.setAttribute(attr,"");
      else {
        //check if the selected item corresponds to an internal id
        //if it does, set thisID to the internal id in question
        for(int i = 0;i<idPanels.size();i++) {
          MetadataPane.TablePanel tp =
            (MetadataPane.TablePanel) idPanels.get(i);
          if(data.equals(tp.name)) thisID = tp.id;
        }
        //if an internal reference was found, set the node tree
        //to reflect the changes made by the new selection of
        //the attribute's value
        if (thisID != null) {
          oNode.setAttribute(attr, thisID);
        }
        //if no internal reference was found, look for an external
        //reference and clip the "(External) " marker from it
        //before setting the attribute to the new value
        else {
          for(int i = 0;i<addPanels.size();i++) {
            String s = (String) addPanels.get(i);
            if(data.equals(s)) {
              oNode.setAttribute(attr, s.substring(11));
            }
          }
        }
      }
      //let the table know that our editor is done with its
      //eeeeeevil deeds
      fireEditingStopped();
    }
  }

// -- Helper Classes --

  //very simple extension of JComboBox that simply adds an int
  //field to designate which row this combobox edits
  public class JRowBox extends JComboBox {
    public int row;

    public JRowBox (int r) {
      super();
      row = r;
    }
  }
}

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
import javax.swing.JTextField;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.table.TableModel;

import java.awt.Component;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import java.util.Hashtable;
import java.util.Vector;

import org.openmicroscopy.xml.OMEXMLNode;
import org.openmicroscopy.xml.DOMUtil;
import org.w3c.dom.Element;

public class VariableComboEditor extends AbstractCellEditor
  implements TableCellEditor, ActionListener, 
  DocumentListener, FocusListener, MouseListener
{

  // -- Fields --

  //Vectors that hold Panels that have ID (idPanels)
  //and also all external references in the file (addPanels)
  private Vector idPanels, addPanels;

  //refers to the node associated with the TablePanel that
  //holds the table which uses this cell editor
  private OMEXMLNode oNode;
  
  //refers to the TablePanel using this editor
  private MetadataPane.TablePanel tableP;

  //holds the current combobox needed when the cell is clicked
  private JRowBox box;
  
  //holds the current label needed when a non-ref cell is clicked
  private JTextField text;

  //holds the table of references this cell editor is for
  private JTable refTable;
  
  //holds internal reference data found in the ome-xml file
  //that is currently opened by MetadataPane
  private Hashtable iDefs;
  
  private String result;

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
    MetadataPane.TablePanel tp, Hashtable internalDefs)
  {
		//initialize all fields
		result = null;
    idPanels = IDP;
    addPanels = AddP;
    tableP = tp;
    oNode = tp.oNode;
    box = new JRowBox(-1);
    text = new JTextField();
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
    return result;
  }

//Implement the one method defined by TableCellEditor.
  public Component getTableCellEditorComponent(JTable table, Object value,
    boolean isSelected, int row, int column)
  {
    refTable = table;
    TableModel tModel = table.getModel();
  
    Vector fullList = DOMUtil.getChildElements("OMEAttribute",tableP.el);
    Element templateE = null;
    for (int i = 0;i<fullList.size();i++) {
      Element thisE = (Element) fullList.get(i);
      String nameAttr = thisE.getAttribute("XMLName");
      if(thisE.hasAttribute("Name")) nameAttr = thisE.getAttribute("Name");
      if(nameAttr.equals((String) tModel.getValueAt(row, 0))) templateE = thisE;      
    }
    
    String cellType = null;
    if(templateE.hasAttribute("Type")) cellType = templateE.getAttribute("Type");
    
    if (cellType == null) {
      JTextField thisText = new JTextField(new RowDoc(row), (String) value, 1);
      thisText.setEditable(true);
      thisText.getDocument().addDocumentListener(this);
      thisText.addFocusListener(this);
      thisText.addMouseListener(this);
      return thisText;
    }
    else {
      if (cellType.equals("Ref")) {
        //use parameters to get information about the cell being edited
		    JRowBox thisBox = new JRowBox(row);
		    thisBox.addFocusListener(this);
		    
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
      else return null;
    }
   }
   
   public void setNode(int row, String value) {
     TableModel tModel = refTable.getModel();
     String attrName = (String) tModel.getValueAt(row, 0);
     if (value == null || value.equals("") ) {
       if(oNode.getDOMElement().hasAttribute(attrName)) 
         oNode.getDOMElement().removeAttribute(attrName);
     }
     else {
	     if (attrName.endsWith("CharData") ) {
	       DOMUtil.setCharacterData(value, oNode.getDOMElement());
	     }
	     else oNode.setAttribute(attrName, value);
     }
   }

// -- EventListener API --

  //okay I know this gets ugly, but I didn't code tables to be this way!
  public void actionPerformed(ActionEvent e) {
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
      if(data == null || data.equals("")) {
        if(oNode.getDOMElement().hasAttribute(attr)) 
          oNode.getDOMElement().removeAttribute(attr);
      }
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
      result = (String) box.getSelectedItem();
      
      //let the table know that our editor is done with its
      //eeeeeevil deeds
      fireEditingStopped();
    }
    if (e.getSource() instanceof JTextField) {
      text = (JTextField) e.getSource();
      try {
        result = text.getDocument().getText(0, 
          text.getDocument().getLength());
      }
      catch (Exception exc) {System.out.println(exc);}
      System.out.println("Text Action: " + result);
      fireEditingStopped();
    }
  }
  
  public void insertUpdate(DocumentEvent e) {
    try {
      result = e.getDocument().getText(0, 
        e.getDocument().getLength());
    }
    catch (Exception exc) {System.out.println(exc);}
    RowDoc rd = (RowDoc) e.getDocument();
    setNode(rd.row, result);
  }
  
  public void removeUpdate(DocumentEvent e) {
    try {
      result = e.getDocument().getText(0, 
        e.getDocument().getLength());
    }
    catch (Exception exc) {System.out.println(exc);}
    RowDoc rd = (RowDoc) e.getDocument();
    setNode(rd.row, result);
  }
  
  public void changedUpdate(DocumentEvent e) {
    try {
      result = e.getDocument().getText(0, 
        e.getDocument().getLength());
    }
    catch (Exception exc) {System.out.println(exc);}
    RowDoc rd = (RowDoc) e.getDocument();
    setNode(rd.row, result);
  }
  
  public void focusGained(FocusEvent e) {
    if (e.getSource() instanceof JTextField) {
      JTextField text = (JTextField) e.getSource();
      try {
        result = text.getDocument().getText(0, 
          text.getDocument().getLength());
      }
      catch (Exception exc) {System.out.println(exc);}
    }
    if (e.getSource() instanceof JRowBox) {
      JRowBox thisBox = (JRowBox) e.getSource();
      result = (String) thisBox.getSelectedItem();
    }
  }
  
  public void focusLost(FocusEvent e) {}

  public void mousePressed(MouseEvent e) {
	  if (e.getSource() instanceof JTextField &&
    e.getButton() == MouseEvent.BUTTON1) {
		  JTextField text = (JTextField) e.getSource();
		  try {
		    result = text.getDocument().getText(0, 
		      text.getDocument().getLength());
		  }
		  catch (Exception exc) {System.out.println(exc);}
	  }
	  if (e.getSource() instanceof JRowBox &&
    e.getButton() == MouseEvent.BUTTON1) {
		  JRowBox thisBox = (JRowBox) e.getSource();
      result = (String) thisBox.getSelectedItem();
	  }
	}
	
  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  
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
  
  //very simple extension of Document (text) that simply adds an int
  //field to designate which row this Document edits
  public class RowDoc extends DefaultStyledDocument {
    public int row;

    public RowDoc (int r) {
      super();
      row = r;
    }
  }
}

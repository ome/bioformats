/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.ome.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import ome.xml.OMEXMLNode;

/**
 * A class to handle a combobox editor that chooses its items
 * intelligently based on the template. This class looks at the
 * files in the TypeDef folder to see what kind of tag this
 * reference should be pointing to and then lists all reference
 * IDs found in the file that meet this criterion.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/VariableComboEditor.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/VariableComboEditor.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class VariableComboEditor extends AbstractCellEditor
  implements TableCellEditor, ActionListener, FocusListener, MouseListener
{

  // -- Fields --

  /**
   * Vectors that hold Panels that have ID (idPanels)
   * and also all external references in the file (addPanels)
   */
  private Vector idPanels, addPanels;

  /**
   * refers to the node associated with the TablePanel that
   * holds the table which uses this cell editor
   */
  private OMEXMLNode oNode;

  /** refers to the TablePanel using this editor */
  private MetadataPane.TablePanel tableP;

  /** holds the current combobox needed when the cell is clicked */
  private JRowBox box;

  /** holds the table of references this cell editor is for */
  protected JTable refTable;

  /**
   * holds internal reference data found in the ome-xml file
   * that is currently opened by MetadataPane
   */
  private Hashtable iDefs;

  private String result;

  // -- Constants --

  /**
   * get the hashtable describing which attributes refer to what
   * type of nodes
   */
  private static final Hashtable REF_HASH = TemplateParser.getRefHash();

  // -- Constructor --

  /**
   * Construct a new VariableComboEditor.
   * @param IDP vector of TablePanels that have been found to have ID
   *        attributes.
   * @param AddP vector of Strings that hold all external LSIDs found in this
   *        file.
   * @param tp the TablePanel this editor edits.
   * @param internalDefs a hashtable representing semantic type defs found in
   *        the current open file itself.
   */
  public VariableComboEditor(Vector IDP, Vector AddP,
    MetadataPane.TablePanel tp, Hashtable internalDefs)
  {
    //initialize all fields
    result = null;
    idPanels = IDP;
    addPanels = AddP;
    tableP = tp;
    refTable = tp.table;
    oNode = tp.oNode;
    box = new JRowBox(-1);
    iDefs = internalDefs;
  }

  // -- VariableComboEditor API --

  /**Reset the necessary lists for this editor.*/
  public void setDefs(Vector IDP, Vector AddP) {
    idPanels = IDP;
    addPanels = AddP;
  }

  /**
   * If we give up on tailoring the combobox to the appropriate types,
   * this method simply adds all possible references of all types to
   * the combobox.
   */
  public void addAll(JRowBox jrb) {
    //add internal references
    for (int i = 0;i<idPanels.size();i++) {
      MetadataPane.TablePanel tp = (MetadataPane.TablePanel) idPanels.get(i);
      if (tp.refDetails != null && !tp.refDetails.equals(""))
        jrb.addItem(tp.name + " - " + tp.refDetails);
      else jrb.addItem(tp.name);
    }
    //add external references
    for (int i = 0;i<addPanels.size();i++) {
      jrb.addItem( (String) addPanels.get(i) );
    }
  }

  // -- AbstractCellEditor Implementation --

  /** Implement the one CellEditor method that AbstractCellEditor doesn't. */
  public Object getCellEditorValue() {
    return result;
  }

  /** Implement the one method defined by TableCellEditor. */
  public Component getTableCellEditorComponent(JTable table, Object value,
    boolean isSelected, int row, int column)
  {
    TableModel tModel = table.getModel();

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
        for (int i = 0;i<idPanels.size();i++) {
          MetadataPane.TablePanel tp =
            (MetadataPane.TablePanel) idPanels.get(i);
          String tpClass = tp.oNode.getClass().getName();
          boolean isCorrectType =
            tpClass.equals("org.openmicroscopy.xml.st." + type + "Node");
          //if the node is of the right type, add it to the combobox
          if (isCorrectType && !tp.refDetails.equals("")) {
            thisBox.addItem(tp.name + " - " + tp.refDetails);
          }
          else if (isCorrectType && tp.refDetails.equals("")) {
            thisBox.addItem(tp.name);
          }
        }

         //check the list of external references for references of
         //the appropriate type
        for (int i = 0;i<addPanels.size();i++) {
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

  /**Handle whenever the user has selected an item from this combobox.*/
  public void actionPerformed(ActionEvent e) {
    if ( e.getSource() instanceof JRowBox) {
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
      if (data == null || data.equals("")) {
        if (oNode.getDOMElement().hasAttribute(attr))
          oNode.getDOMElement().removeAttribute(attr);
      }
      else {
        //check if the selected item corresponds to an internal id
        //if it does, set thisID to the internal id in question
        for (int i = 0;i<idPanels.size();i++) {
          MetadataPane.TablePanel tp =
            (MetadataPane.TablePanel) idPanels.get(i);
          if (data.startsWith(tp.name)) thisID = tp.id;
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
          for (int i = 0;i<addPanels.size();i++) {
            String s = (String) addPanels.get(i);
            if (data.equals(s)) {
              oNode.setAttribute(attr, s.substring(11));
            }
          }
        }
      }
      result = (String) box.getSelectedItem();

      tableP.callStateChanged(true);

      //let the table know that our editor is done with its
      //eeeeeevil deeds
      fireEditingStopped();
    }
  }

  /**
  * Needs this in order to handle when the user presses tab
  * while the editor combobox is still open.
  */
  public void focusGained(FocusEvent e) {
    if (e.getSource() instanceof JRowBox) {
      JRowBox thisBox = (JRowBox) e.getSource();
      result = (String) thisBox.getSelectedItem();
    }
  }

  public void focusLost(FocusEvent e) {}

  /** Handle whenever the user clicks on this editor.*/
  public void mousePressed(MouseEvent e) {
    if (e.getSource() instanceof JRowBox &&
      e.getButton() == MouseEvent.BUTTON1)
    {
      JRowBox thisBox = (JRowBox) e.getSource();
      result = (String) thisBox.getSelectedItem();
    }
  }

  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  // -- Helper Classes --

  /**
   * Very simple extension of JComboBox that simply adds an int
   * field to designate which row this combobox edits.
   */
  public class JRowBox extends JComboBox {
    public int row;

    public JRowBox (int r) {
      super();
      row = r;
    }
  }

}

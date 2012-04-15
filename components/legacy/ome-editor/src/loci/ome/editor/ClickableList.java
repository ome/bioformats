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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class that makes lists you can right click to
 * add or subtract notes from. Also handles the changes
 * to the OMEXMLNodes that arrises from user interaction
 * with the GUI of a NotePanel.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/ClickableList.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/ClickableList.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class ClickableList extends JList
  implements MouseListener, ActionListener, DocumentListener
{
  /**The popup menu created when right-clicking on this list.*/
  protected JPopupMenu jPop;

  /**The TablePanel this list of notes is for*/
  protected MetadataPane.TablePanel tableP;

  /**The textarea that displays these notes.*/
  protected JTextArea textArea;

  /**The listmodel for this list.*/
  protected DefaultListModel myModel;

  /**The NotePanel that contains this list of notes.*/
  protected NotePanel noteP;

  /**Make a clickable list to display the names of notes for a
  * TablePanel.
  * @param thisModel The listmodel for this list.
  * @param nP The NotePanel that contains this list of notes.
  */
  public ClickableList (DefaultListModel thisModel, NotePanel nP) {
    super(thisModel);

    DefaultListSelectionModel selectModel = new DefaultListSelectionModel();
    selectModel.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
    setSelectionModel(selectModel);

    myModel = thisModel;
    addMouseListener(this);
    jPop = new JPopupMenu();
    tableP = nP.tableP;
    textArea = nP.textArea;
    noteP = nP;
  }

  /** Handles right-clicking on this list to call up a popup menu.*/
  public void mousePressed(MouseEvent e) {
    if ((e.getButton() == MouseEvent.BUTTON3
      || e.getButton() == MouseEvent.BUTTON2)
      && tableP.isEditable())
    {
      jPop = new JPopupMenu("Add/Remove Notes");

      JMenuItem addItem = new JMenuItem("Add a new note");
      addItem.addActionListener(this);
      addItem.setActionCommand("add");
//      addItem.setForeground(new Color(0,100,0));
      JMenuItem delItem = new JMenuItem("Delete selected note");
      delItem.addActionListener(this);
      delItem.setActionCommand("remove");
//      delItem.setForeground(new Color(100,0,0));

      jPop.add(addItem);
      jPop.add(delItem);
      jPop.show(this, e.getX(), e.getY());
    }
  }

  /**Sets the selected note to the stated value.
  *  @param text The value that the note should have.
  */
  public void setValue(String text) {
    if (((String) getSelectedValue()) != null && text != null) {
      Element currentCA = DOMUtil.getChildElement("CustomAttributes",
        tableP.tPanel.oNode.getDOMElement());
      Vector childList = DOMUtil.getChildElements(
        tableP.el.getAttribute("XMLName") + "Annotation", currentCA);
      Element childEle = null;
      for(int i = 0;i<childList.size();i++) {
        Element thisEle = (Element) childList.get(i);
        if (thisEle.getAttribute("Name").equals((String) getSelectedValue())) {
          childEle = thisEle;
        }
      }
      if (childEle!=null) childEle.setAttribute("Value", text);
    }
  }

  // -- Event API methods --

  //abstract methods we must override but have no use for
  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  /**Handles all popup menu commands.*/
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("add".equals(cmd)) {
      String newName = (String)JOptionPane.showInputDialog(
        getTopLevelAncestor(), "Please create a name for the new\n" +
        tableP.name + " Note...\n", "Create a New Note",
        JOptionPane.PLAIN_MESSAGE, null, null, "NewName");

      //If a string was returned, say so.
      if ((newName != null) && (newName.length() > 0)) {
        String newXMLName = tableP.el.getAttribute("XMLName") + "Annotation";

        Element omeE = tableP.tPanel.ome.getDOMElement();
        NodeList omeChildren = omeE.getChildNodes();
        Element childE = null;
        for(int i = 0;i < omeChildren.getLength();i++) {
          Element tempE = (Element) omeChildren.item(i);
          if (tempE.getTagName().equals("SemanticTypeDefinitions")) {
            childE = tempE;
          }
        }
        if (childE == null) {
          childE = DOMUtil.createChild(omeE, "SemanticTypeDefinitions");
        }

        Element stdE = childE;
        boolean alreadyPresent = false;
        NodeList stdChildren = childE.getChildNodes();
        for(int i = 0;i < stdChildren.getLength();i++) {
          Element tempE = (Element) stdChildren.item(i);
          if (tempE.getTagName().equals("SemanticType") &&
            tempE.getAttribute("Name").equals(newXMLName))
          {
            alreadyPresent = true;
          }
        }

        if(!alreadyPresent) {
          childE = DOMUtil.createChild(stdE, "SemanticType");
          childE.setAttribute("Name", newXMLName);
          childE.setAttribute("AppliesTo", tableP.tPanel.name.substring(0,1));
          Element stE = childE;

          //Get an upper-case representation of this new semantic type
          //to use as a DBLocation
          String dbPrefix = "LOCI." +
            tableP.el.getAttribute("XMLName").toUpperCase() + "_ANNOTATION.";

          childE = DOMUtil.createChild(stE, "Element");
          childE.setAttribute("Name", "Name");
          childE.setAttribute("DBLocation", dbPrefix + "NAME");
          childE.setAttribute("DataType", "string");

          childE = DOMUtil.createChild(stE, "Element");
          childE.setAttribute("Name", "Value");
          childE.setAttribute("DBLocation", dbPrefix + "VALUE");
          childE.setAttribute("DataType", "string");

          childE = DOMUtil.createChild(stE, "Element");
          childE.setAttribute("Name", "NoteFor");
          childE.setAttribute("DBLocation", dbPrefix + "NOTE_FOR");
          childE.setAttribute("DataType", "reference");
          childE.setAttribute("RefersTo", tableP.el.getAttribute("XMLName"));
        }

        OMEXMLNode newNode = MetadataPane.makeNode(newXMLName,
          tableP.tPanel.oNode);
        newNode.setAttribute("NoteFor", tableP.id);
        newNode.setAttribute("Name", newName);
        myModel.addElement(newName);
        noteP.setNameLabel(true);
        tableP.setNumNotes(noteP.getNumNotes());
        setSelectedIndex(myModel.getSize() -1);
        ensureIndexIsVisible(getSelectedIndex());
      }
      tableP.callStateChanged(true);
    }
    if ("remove".equals(cmd)) {
      if ( ((String) getSelectedValue()) != null) {
        int prevIndex = getSelectedIndex();
        Element currentCA = DOMUtil.getChildElement("CustomAttributes",
          tableP.tPanel.oNode.getDOMElement());
        Vector childList = DOMUtil.getChildElements(
          tableP.el.getAttribute("XMLName") + "Annotation", currentCA);
        Element childEle = null;
        for(int i = 0;i<childList.size();i++) {
          Element thisEle = (Element) childList.get(i);
          if (thisEle.getAttribute("Name").equals(
            (String) getSelectedValue()))
          {
            childEle = thisEle;
          }
        }
        currentCA.removeChild((Node) childEle);
        NodeList caChildren = currentCA.getChildNodes();
        if ( caChildren != null) {
          if ( caChildren.getLength() == 0) {
            tableP.tPanel.oNode.getDOMElement().removeChild( (Node) currentCA);
          }
        }
        else tableP.tPanel.oNode.getDOMElement().removeChild( (Node) currentCA);

        Document thisDoc = null;
        try {
          thisDoc = tableP.tPanel.ome.getOMEDocument(true);
        }
        catch (Exception exc) {
          exc.printStackTrace();
        }
        Element foundE = DOMUtil.findElement(
          tableP.el.getAttribute("XMLName") + "Annotation", thisDoc);

        if (foundE == null) {
          String newXMLName = tableP.el.getAttribute("XMLName") + "Annotation";

          Element omeE = tableP.tPanel.ome.getDOMElement();
          NodeList omeChildren = omeE.getChildNodes();
          Element childE = null;
          for(int i = 0;i < omeChildren.getLength();i++) {
            Element tempE = (Element) omeChildren.item(i);
            if (tempE.getTagName().equals("SemanticTypeDefinitions")) {
              childE = tempE;
            }
          }
          if (childE != null) {
            Element stdE = childE;
            NodeList stdChildren = childE.getChildNodes();
            Element stE = null;
            for(int i = 0;i < stdChildren.getLength();i++) {
              Element tempE = (Element) stdChildren.item(i);
              if (tempE.getTagName().equals("SemanticType") &&
                tempE.getAttribute("Name").equals(newXMLName))
              {
                stE = tempE;
              }
            }
            if (stE != null) stdE.removeChild((Node) stE);
            if (stdChildren != null) {
              if (stdChildren.getLength() == 0) {
                omeE.removeChild((Node) stdE);
              }
            }
            else omeE.removeChild((Node) stdE);
          }
        }

        myModel.removeElementAt(getSelectedIndex());
        if (myModel.size() == 0) {
          noteP.setNameLabel(false);
          noteP.setNotesLabel(false);
        }
        tableP.setNumNotes(noteP.getNumNotes());
        if (myModel.size() == 1) setSelectedIndex(0);
        else {
          if (prevIndex >= myModel.size() - 1)
            setSelectedIndex(prevIndex - 1);
          else setSelectedIndex(prevIndex);
        }
        ensureIndexIsVisible(getSelectedIndex());
      }
      else {
        JOptionPane.showMessageDialog(getTopLevelAncestor(),
          "Because there is no name selected, you\n" +
          "cannot delete the selected note.",
          "No Selection Found", JOptionPane.WARNING_MESSAGE);
      }
      tableP.callStateChanged(true);
    }
  }

  /**Handles insert changes to the textarea that the notes' values
  *  are stored in. Sets the value of the appropriate node as well.
  */
  public void insertUpdate(DocumentEvent e) {
    String result = null;
    try {
      result = e.getDocument().getText(0, e.getDocument().getLength());
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
    if (myModel.size() != 0 && getSelectedIndex() != -1) {
      if (result.equals("")) noteP.setNotesLabel(false);
      else noteP.setNotesLabel(true);
    }
    setValue(result);
    if(tableP.isEditable()) tableP.callStateChanged(true);
  }

  /**Handles remove changes to the textarea that the notes' values
  *  are stored in. Sets the value of the appropriate node as well.
  */
  public void removeUpdate(DocumentEvent e) {
    String result = null;
    try {
      result = e.getDocument().getText(0, e.getDocument().getLength());
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
    if (myModel.size() != 0 && getSelectedIndex() != -1) {
      if (result.equals("")) noteP.setNotesLabel(false);
      else noteP.setNotesLabel(true);
    }
    setValue(result);
    if(tableP.isEditable()) tableP.callStateChanged(true);
  }

  /**Handles wierd changes to the textarea that the notes' values
  *  are stored in. Sets the value of the appropriate node as well.
  */
  public void changedUpdate(DocumentEvent e) {
    String result = null;
    try {
      result = e.getDocument().getText(0, e.getDocument().getLength());
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
    if (myModel.size() != 0 && getSelectedIndex() != -1) {
      if (result.equals("")) noteP.setNotesLabel(false);
      else noteP.setNotesLabel(true);
    }
    setValue(result);
    if(tableP.isEditable()) tableP.callStateChanged(true);
  }

}

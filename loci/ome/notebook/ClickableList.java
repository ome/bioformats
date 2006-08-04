package loci.ome.notebook;

import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.DefaultListSelectionModel;

import java.awt.Color;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import org.openmicroscopy.xml.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

public class ClickableList extends JList
  implements MouseListener, ActionListener, DocumentListener {
  
  protected JPopupMenu jPop;
  protected MetadataPane.TablePanel tableP;
  protected JTextArea textArea;
  protected DefaultListModel myModel;
  protected NotePanel noteP;

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
  
  public void mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON3 
      || e.getButton() == MouseEvent.BUTTON2) 
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
	
	public void setValue(String text) {
	  if ( ((String) getSelectedValue()) != null && text != null) {
	    Element currentCA = DOMUtil.getChildElement("CustomAttributes", tableP.tPanel.oNode.getDOMElement());
	    Vector childList = DOMUtil.getChildElements(tableP.el.getAttribute("XMLName") +
	      "Annotation", currentCA);
	    Element childEle = null;
	    for(int i = 0;i<childList.size();i++) {
	      Element thisEle = (Element) childList.get(i);
	      if(thisEle.getAttribute("Name").equals((String) getSelectedValue())) childEle = thisEle;
	    }
	    childEle.setAttribute("Value", text);
	  }
	}
	
	// -- Event API Overridden Methods --
	
  //abstract methods we must override but have no use for
  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("add".equals(cmd)) {
      String newName = (String)JOptionPane.showInputDialog(
                    getTopLevelAncestor(),
                    "Please create a name for the new\n"
                    + tableP.name + " Note...\n",
                    "Create a New Note",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "NewName");

			//If a string was returned, say so.
			if ((newName != null) && (newName.length() > 0)) {
	      CustomAttributesNode caNode = null;
	      Element currentCA = DOMUtil.getChildElement("CustomAttributes", tableP.tPanel.oNode.getDOMElement());
	      if (currentCA != null) caNode = new CustomAttributesNode(currentCA);
	      else {
	        Element cloneEle = DOMUtil.createChild(tableP.tPanel.oNode.getDOMElement(),"CustomAttributes");
	        caNode = new CustomAttributesNode(cloneEle);
	      }
	      
	      String newXMLName = tableP.el.getAttribute("XMLName") + "Annotation";

				Element omeE = tableP.tPanel.ome.getDOMElement();
				NodeList omeChildren = omeE.getChildNodes();
				Element childE = null;
				for(int i = 0;i < omeChildren.getLength();i++) {
				  Element tempE = (Element) omeChildren.item(i);
				  if (tempE.getTagName().equals("SemanticTypeDefinitions"))
				    childE = tempE; 
				}
				if (childE == null) childE =
				  DOMUtil.createChild(omeE, "SemanticTypeDefinitions");
				
				Element stdE = childE;
				boolean alreadyPresent = false;
				NodeList stdChildren = childE.getChildNodes(); 
				for(int i = 0;i < stdChildren.getLength();i++) {
				  Element tempE = (Element) stdChildren.item(i);
				  if (tempE.getTagName().equals("SemanticType") &&
				    tempE.getAttribute("Name").equals(newXMLName) )
				      alreadyPresent = true;
				}
				
				if(!alreadyPresent) {
				  childE = DOMUtil.createChild(stdE, "SemanticType");
				  childE.setAttribute("Name", newXMLName);
				  childE.setAttribute("AppliesTo", tableP.tPanel.name.substring(0,1));
				  Element stE = childE;
				  
				  childE = DOMUtil.createChild(stE, "Element");
				  childE.setAttribute("Name", "Name");
				  childE.setAttribute("DBLocation", "");
				  childE.setAttribute("DataType", "string");
				  
				  childE = DOMUtil.createChild(stE, "Element");
				  childE.setAttribute("Name", "Value");
				  childE.setAttribute("DBLocation", "");
				  childE.setAttribute("DataType", "string");
				  
				  childE = DOMUtil.createChild(stE, "Element");
				  childE.setAttribute("Name", "NoteFor");
				  childE.setAttribute("DBLocation", "");
				  childE.setAttribute("DataType", "reference");
				  childE.setAttribute("RefersTo", tableP.el.getAttribute("XMLName"));
				}
					
	      AttributeNode newNode = new AttributeNode(caNode, newXMLName);
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
	      Element currentCA = DOMUtil.getChildElement("CustomAttributes", tableP.tPanel.oNode.getDOMElement());
	      Vector childList = DOMUtil.getChildElements(tableP.el.getAttribute("XMLName") +
	        "Annotation", currentCA);
	      Element childEle = null;
	      for(int i = 0;i<childList.size();i++) {
	        Element thisEle = (Element) childList.get(i);
	        if(thisEle.getAttribute("Name").equals((String) getSelectedValue())) childEle = thisEle;
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
	      Element foundE = DOMUtil.findElement(tableP.el.getAttribute("XMLName")
	         + "Annotation", thisDoc);
	        
	      if (foundE == null) {
		      String newXMLName = tableP.el.getAttribute("XMLName") + "Annotation";
	
					Element omeE = tableP.tPanel.ome.getDOMElement();
					NodeList omeChildren = omeE.getChildNodes();
					Element childE = null;
					for(int i = 0;i < omeChildren.getLength();i++) {
					  Element tempE = (Element) omeChildren.item(i);
					  if (tempE.getTagName().equals("SemanticTypeDefinitions"))
					    childE = tempE; 
					}
					if (childE != null) {
						Element stdE = childE;
						NodeList stdChildren = childE.getChildNodes();
						Element stE = null; 
						for(int i = 0;i < stdChildren.getLength();i++) {
						  Element tempE = (Element) stdChildren.item(i);
						  if (tempE.getTagName().equals("SemanticType") &&
						    tempE.getAttribute("Name").equals(newXMLName) )
						      stE = tempE;
					  }
					  if (stE != null) stdE.removeChild( (Node) stE );
			      if ( stdChildren != null) {
			        if ( stdChildren.getLength() == 0) {
			          omeE.removeChild( (Node) stdE);
			        }
			      }
			      else omeE.removeChild( (Node) stdE);
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
  
  public void insertUpdate(DocumentEvent e) {
    String result = null;
    try {
      result = e.getDocument().getText(0, e.getDocument().getLength());
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
    if (myModel.size() != 0 && getSelectedIndex() != -1) {
	    if (result.equals("")) {
	      noteP.setNotesLabel(false);
	    }
	    else noteP.setNotesLabel(true);
    }
    setValue(result);
    tableP.callStateChanged(true);
  }
  
  public void removeUpdate(DocumentEvent e) {
    String result = null;
    try {
      result = e.getDocument().getText(0, e.getDocument().getLength());
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
    if (myModel.size() != 0 && getSelectedIndex() != -1) {
	    if (result.equals("")) {
	      noteP.setNotesLabel(false);
	    }
	    else noteP.setNotesLabel(true);
    }
    setValue(result);
    tableP.callStateChanged(true);
  }
  
  public void changedUpdate(DocumentEvent e) {
    String result = null;
    try {
      result = e.getDocument().getText(0, e.getDocument().getLength());
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
    if (myModel.size() != 0 && getSelectedIndex() != -1) {
	    if (result.equals("")) {
	      noteP.setNotesLabel(false);
	    }
	    else noteP.setNotesLabel(true);
    }
    setValue(result);
    tableP.callStateChanged(true);
  }
}

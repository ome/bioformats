package loci.ome.notebook;

import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JScrollPane;

import java.awt.Point;
import java.awt.Container;

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
      JMenuItem delItem = new JMenuItem("Delete selected note");
      delItem.addActionListener(this);
      delItem.setActionCommand("remove");
      
      jPop.add(addItem);
      jPop.add(delItem);
      jPop.show(this, e.getX(), e.getY());
    }
	}
	
	public void setValue(String text) {
	  if ( ((String) getSelectedValue()) != null && text != null) {
	    Element currentCA = DOMUtil.getChildElement("CustomAttributes", tableP.tPanel.oNode.getDOMElement());
	    Vector childList = DOMUtil.getChildElements(tableP.tPanel.el.getAttribute("XMLName") +
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
	
	      AttributeNode newNode = new AttributeNode(caNode,
	        tableP.tPanel.el.getAttribute("XMLName") + "Annotation");
	      newNode.setAttribute("NoteFor", tableP.id);  
	      newNode.setAttribute("Name", newName);
	      myModel.addElement(newName);
	      tableP.setNumNotes(noteP.getNumNotes());
	      setSelectedIndex(myModel.getSize() -1);
	      ensureIndexIsVisible(getSelectedIndex());
/*
	      Container anObj = (Container) this;
          while(!(anObj instanceof JScrollPane)) {
            anObj = anObj.getParent();
          }
        JScrollPane jScr = (JScrollPane) anObj;
        Point loc = new Point(0, jScr.getViewport().getViewSize().height - 
          jScr.getViewport().getExtentSize().height);
        jScr.getViewport().setViewPosition(loc);
*/
	    }
	  }
    if ("remove".equals(cmd)) {
      if ( ((String) getSelectedValue()) != null) {
        int prevIndex = getSelectedIndex();
	      Element currentCA = DOMUtil.getChildElement("CustomAttributes", tableP.tPanel.oNode.getDOMElement());
	      Vector childList = DOMUtil.getChildElements(tableP.tPanel.el.getAttribute("XMLName") +
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
	      myModel.removeElementAt(getSelectedIndex());
	      tableP.setNumNotes(noteP.getNumNotes());
	      if (prevIndex > 0) setSelectedIndex(prevIndex - 1);
	      ensureIndexIsVisible(getSelectedIndex());
	    }
	    else {
	      JOptionPane.showMessageDialog(getTopLevelAncestor(),
          "Because there is no name selected, you\n" +
          "cannot delete the selected note.",
          "No Selection Found", JOptionPane.WARNING_MESSAGE);
	    }
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
    setValue(result);
  }
  
  public void removeUpdate(DocumentEvent e) {
    String result = null;
    try {
      result = e.getDocument().getText(0, e.getDocument().getLength());
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
    setValue(result);
  }
  
  public void changedUpdate(DocumentEvent e) {
    String result = null;
    try {
      result = e.getDocument().getText(0, e.getDocument().getLength());
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
    setValue(result);
  }
}
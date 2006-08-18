package loci.ome.notebook;

import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.table.TableModel;

import java.awt.Component;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.AWTEvent;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import org.openmicroscopy.xml.OMEXMLNode;
import org.openmicroscopy.xml.DOMUtil;
import org.w3c.dom.Element;

public abstract class VariableTextEditor extends AbstractCellEditor
  implements TableCellEditor, ActionListener, 
  DocumentListener, FocusListener, MouseListener
{
  // -- Fields --
  
  //refers to the TablePanel using this editor
  protected MetadataPane.TablePanel tableP;
  
  protected String result;

  // -- Constructor --

  public VariableTextEditor(MetadataPane.TablePanel tp) {
		tableP = tp;
		result = null;
	}
	
	public Object getCellEditorValue() {
    return result;
  }
  
  public void setNode(int row, String value) {
    TableModel tModel = tableP.table.getModel();
    String attrName = (String) tModel.getValueAt(row, 0);
    if (value == null || value.equals("") ) {
      if(tableP.oNode.getDOMElement().hasAttribute(attrName)) 
        tableP.oNode.getDOMElement().removeAttribute(attrName);
    }
    else {
	    if (attrName.endsWith("CharData") ) {
	      DOMUtil.setCharacterData(value, tableP.oNode.getDOMElement());
	    }
	    else tableP.oNode.setAttribute(attrName, value);
    }
  }
   
  public void changeResult(AWTEvent e) {
    if (e.getSource() instanceof JTextComponent) {
      JTextComponent text = (JTextComponent) e.getSource();
      try {
        result = text.getDocument().getText(0, 
          text.getDocument().getLength());
      }
      catch (Exception exc) {System.out.println(exc);}
    }
  }
  
  public void changeNode(DocumentEvent e) {
    try {
      result = e.getDocument().getText(0, 
        e.getDocument().getLength());
    }
    catch (Exception exc) {System.out.println(exc);}
    RowDoc rd = (RowDoc) e.getDocument();
    setNode(rd.row, result);
    tableP.callStateChanged(true);
  }
   
  public void actionPerformed(ActionEvent e) {
    changeResult(e);
    fireEditingStopped();
  }
  
  public void focusGained(FocusEvent e) {
    changeResult(e);
  }
  
  public void focusLost(FocusEvent e) {}
	
  public void mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) changeResult(e);
	}
	
	public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  
  public void insertUpdate(DocumentEvent e) {
    changeNode(e);
  }
  
  public void removeUpdate(DocumentEvent e) {
    changeNode(e);
  }
  
  public void changedUpdate(DocumentEvent e) {
    changeNode(e);
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
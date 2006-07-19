package loci.ome.notebook;

import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Color;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.openmicroscopy.xml.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import loci.formats.ReflectedUniverse;

import java.util.Vector;

public class ClickableTable extends JTable 
  implements MouseListener, ActionListener {

  protected MetadataPane.TablePanel tp;
  protected JPopupMenu jPop;
  private int thisRow;
  private String attrName;
  private Boolean isDuplicate;

  // -- ClickableTable Constructors --

  public ClickableTable(TableModel model, MetadataPane.TablePanel tablePanel) {
    super(model);
    addMouseListener(this);
    tp = tablePanel;
    jPop = new JPopupMenu();
    thisRow = -1;
    attrName = null;
  }
  
  // -- Static ClickableTable API Methods --
    
  public static boolean isInCustom(String tagName) {
    if (tagName.equals("Project") ||
      tagName.equals("Feature") ||
      tagName.equals("CustomAttributes") ||
      tagName.equals("Dataset") ||
      tagName.equals("Image"))
    {return false;}
    else return true;
  }
  
  public static boolean usesAn(char c) {
    boolean result = false;
    switch(c) {
      case 'a':
      case 'A':
      case 'e':
      case 'E':
      case 'i':
      case 'I':
      case 'o':
      case 'O':
      case 'h':
      case 'H':
        result = true;
        break;
      default:
        result = false;
        break;
    }
    return result;
  }
  
  // -- MouseListener API Methods --
  
  public void mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON2) {
      thisRow = rowAtPoint(e.getPoint());
      attrName = (String) getModel().getValueAt(thisRow,0);
      jPop = new JPopupMenu("Add/Remove " + attrName + " Attribute:");
      JMenuItem infoItem = null;
      if( usesAn(attrName.charAt(0)) ) infoItem = new JMenuItem("What is an " + attrName + "?");
      else infoItem = new JMenuItem("What is a " + attrName + "?");
      
      String realBigName = tp.name;
      isDuplicate = false;
      if(realBigName.endsWith(")") ) {
        isDuplicate = true;
        realBigName = realBigName.substring(0,realBigName.length()-4);
      }
        
      
      JMenuItem addItem = new JMenuItem("Add another " + realBigName);
      JMenuItem bigRemItem = new JMenuItem("Delete this " + realBigName);
      JMenuItem remItem = new JMenuItem("Delete this " + attrName);
      infoItem.addActionListener(this);
      infoItem.setActionCommand("help");
      addItem.addActionListener(this);
      addItem.setActionCommand("bigAdd");
      bigRemItem.addActionListener(this);
      bigRemItem.setActionCommand("bigRem");
      remItem.addActionListener(this);
      remItem.setActionCommand("delete");
      
      jPop.add(infoItem);
      JSeparator sep = new JSeparator();
      jPop.add(sep);
      jPop.add(addItem);
      jPop.add(bigRemItem);
      JSeparator sep2 = new JSeparator();
      jPop.add(sep2);
      jPop.add(remItem);
      jPop.show(this, e.getX(), e.getY());
    }
  }
  
  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  
  // -- ActionLister API Methods --
  
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("help".equals(cmd)) {
      HelpFrame helpWin = new HelpFrame();
    }
    if ("delete".equals(cmd)) {
      Vector attrVector = DOMUtil.getChildElements("OMEAttribute", tp.el);
      Element thisAttr = null;
      for (int i = 0;i<attrVector.size();i++) {
        Element temp = (Element) attrVector.get(i);
        if (temp.hasAttribute("Name")) {
          if (attrName.equals(temp.getAttribute("Name")) ) thisAttr = temp;
        }
        else if (temp.hasAttribute("XMLName") && !temp.hasAttribute("Name") ) {
          if (attrName.equals(temp.getAttribute("XMLName")) ) thisAttr = temp;
        }
      }
      tp.oNode.setAttribute(thisAttr.getAttribute("XMLName"), "");
      getModel().setValueAt("", thisRow, 1);
    }
    if ("bigAdd".equals(cmd)) {
      if (tp.isTopLevel) {
        String thisTagName =tp.oNode.getDOMElement().getTagName();
      
        Element parentEle = null;
        if (!isInCustom(thisTagName)) {
          parentEle = tp.tPanel.ome.getDOMElement();
        }
        else parentEle = tp.tPanel.ome.getChild("CustomAttributes").getDOMElement();
        Element cloneEle = DOMUtil.createChild(parentEle,thisTagName);      
           
        tp.callReRender();
      }
      else {
        String thisTagName =tp.oNode.getDOMElement().getTagName();
        if (!isInCustom(thisTagName)) {
          Element anEle = DOMUtil.createChild(tp.tPanel.oNode.getDOMElement(), 
            thisTagName);
        }
        else {
          OMEXMLNode realParent = tp.tPanel.oNode.getChild("CustomAttributes");
          Element anEle = DOMUtil.createChild(realParent.getDOMElement(), 
            thisTagName);
        }
        tp.callReRender();
      }
    }
    if ("bigRem".equals(cmd)) {
      if (tp.isTopLevel) {
        String thisTagName =tp.oNode.getDOMElement().getTagName();
        Element parentEle = null;
        if (!isInCustom(thisTagName)) {
          parentEle = tp.tPanel.ome.getDOMElement();
          parentEle.removeChild((Node) tp.oNode.getDOMElement());
        }
        else {
          OMEXMLNode realParent = tp.tPanel.ome.getChild("CustomAttributes");
          parentEle = realParent.getDOMElement();
          parentEle.removeChild((Node) tp.oNode.getDOMElement());            
        }
        tp.callReRender();
      }
      else {
        String thisTagName =tp.oNode.getDOMElement().getTagName();
        if (!isInCustom(thisTagName)) {
          Element parentEle = tp.tPanel.oNode.getDOMElement();
          parentEle.removeChild((Node) tp.oNode.getDOMElement());
        }
        else {
          OMEXMLNode realParent = tp.tPanel.oNode.getChild("CustomAttributes");
          Element parentEle = realParent.getDOMElement(); 
          parentEle.removeChild((Node) tp.oNode.getDOMElement());
        }
        tp.callReRender();
      }
    }
  }

  // -- Helper Classes --
  
  public class HelpFrame extends JFrame {
    public HelpFrame() {
      super("Help! - " + tp.name);
      setLocation(200,200);
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      
      JPanel contentPanel = new JPanel();
      Dimension dim = new Dimension(300,125);
      contentPanel.setPreferredSize(dim);
      contentPanel.setLayout(new BorderLayout());
      setContentPane(contentPanel);
      contentPanel.setBackground(new Color(0,0,50));

      JLabel titleLabel = new JLabel(" " + attrName + ":");
      Font thisFont = titleLabel.getFont();
      Font newFont = new Font(thisFont.getFontName(),Font.BOLD,18);
      titleLabel.setFont(newFont);
      contentPanel.add(titleLabel, BorderLayout.NORTH);
      titleLabel.setForeground(new Color(255,255,255));

      String desc = "      No description available for " + attrName + ".";
      Vector attrVector = DOMUtil.getChildElements("OMEAttribute", tp.el);
      Element thisAttr = null;
      for (int i = 0;i<attrVector.size();i++) {
        Element temp = (Element) attrVector.get(i);
        if (temp.hasAttribute("Name")) {
          if (attrName.equals(temp.getAttribute("Name")) ) thisAttr = temp;
        }
        else if (temp.hasAttribute("XMLName") && ! temp.hasAttribute("Name") ) {
          if (attrName.equals(temp.getAttribute("XMLName")) ) thisAttr = temp;
        }
      }
      
      if (thisAttr != null && thisAttr.hasAttribute("Description")) desc = "      " + thisAttr.getAttribute("Description");
      JTextArea descArea = new JTextArea(desc);
      descArea.setEditable(false);
      descArea.setLineWrap(true);
      descArea.setWrapStyleWord(true);
      contentPanel.add(descArea, BorderLayout.CENTER);      
      
      pack();
      setVisible(true);
    }
  }
}


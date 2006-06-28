//
// MetadataPane.java
//

package loci.ome.notebook;

import java.awt.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import loci.formats.RandomAccessStream;
import loci.formats.TiffTools;
import org.openmicroscopy.xml.*;
import org.w3c.dom.*;
import java.awt.event.*;
import javax.swing.event.*;

/** MetadataPane is a panel that displays OME-XML metadata. */
public class MetadataPane extends JPanel
  implements Runnable, ChangeListener
{
  // -- Constants --
  protected static final String[] TREE_COLUMNS = {"Attribute", "Value"};

  // -- Fields --

  /** Table model for attributes table*/
  DefaultTableModel myTableModel;

  /** Pane containing XML tree. */
  protected JTabbedPane tabPane;

  /** TemplateParser object*/
  protected TemplateParser tParse;

  OMENode omeNode;

  // -- Fields - raw panel --

  /** Panel containing raw XML dump. */
  protected JPanel rawPanel;

  /** Text area displaying raw XML. */
  protected JTextArea rawText;

  /** Whether XML is being displayed in raw form. */
  protected boolean raw;


  // -- Constructor --

  /** Constructs widget for displaying OME-XML metadata. */
  public MetadataPane(TemplateParser tp) {

    // -- Tabbed Pane Initialization --

    tParse = tp;
    tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
    tabPane.addChangeListener(this);
    setupTabs();
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    add(tabPane);
    tabPane.setVisible(true);
    omeNode = null;
    
    // -- Raw panel --

    raw = false;
    rawPanel = new JPanel();
    rawPanel.setLayout(new BorderLayout());

    // label explaining what happened
    JLabel rawLabel = new JLabel("Metadata parsing failed. " +
      "Here is the raw info. Good luck!");
    rawLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
    rawPanel.add(rawLabel, BorderLayout.NORTH);

    // text area for displaying raw XML
    rawText = new JTextArea();
    rawText.setLineWrap(true);
    rawText.setColumns(50);
    rawText.setRows(30);
    rawText.setEditable(false);
    rawPanel.add(new JScrollPane(rawText), BorderLayout.CENTER);
    rawPanel.setVisible(false);
    add(rawPanel);
  }


  // -- MetadataPane API methods --

  /**
   * Sets the displayed OME-XML metadata to correspond
   * to the given character string of XML.
   */
  public void setOMEXML(String xml) {
    OMENode ome = null;
    try { ome = new OMENode(xml); }
    catch (Exception exc) { }
    raw = ome == null;
    if (raw) rawText.setText(xml);
    else setOMEXML(ome);
    SwingUtilities.invokeLater(this);
  }

  /**
   * Sets the displayed OME-XML metadata to correspond
   * to the given OME-XML or OME-TIFF file.
   * @return true if the operation was successful
   */
  public boolean setOMEXML(File file) {
    try {
      DataInputStream in = new DataInputStream(new FileInputStream(file));
      byte[] header = new byte[8];
      in.readFully(header);
      if (TiffTools.isValidHeader(header)) {
        // TIFF file
        in.close();
        RandomAccessStream ras = new RandomAccessStream(file.getPath());
        Hashtable ifd = TiffTools.getFirstIFD(ras);
        ras.close();
        if (ifd == null) return false;
        Object value = TiffTools.getIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION);
        String xml = null;
        if (value instanceof String) xml = (String) value;
        else if (value instanceof String[]) {
          String[] s = (String[]) value;
          StringBuffer sb = new StringBuffer();
          for (int i=0; i<s.length; i++) sb.append(s[i]);
          xml = sb.toString();
        }
        if (xml == null) return false;
        setOMEXML(xml);
      }
      else {
        String s = new String(header).trim();
        if (s.startsWith("<?xml") || s.startsWith("<OME")) {
          // raw OME-XML
          byte[] data = new byte[(int) file.length()];
          System.arraycopy(header, 0, data, 0, 8);
          in.readFully(data, 8, data.length - 8);
          in.close();
          setOMEXML(new String(data));
        }
        else return false;
      }
      return true;
    }
    catch (IOException exc) { return false; }
  }

  /** Sets the displayed OME-XML metadata. */
  public void setOMEXML(OMENode ome) {
    // populate OME-XML tree
    Document doc = null;
    try { doc = ome == null ? null : ome.getOMEDocument(false); }
    catch (Exception exc) { }
    if (doc != null) {
      setupTabs(ome);
    }
  }
  
  /** Sets up the JTabbedPane based on a template, assumes that no OMEXML
  *   file is being compared to the template, so no data will be displayed.
  *   should be used to initialize the application and to create new OMEXML
  *   documents based on the template
  */
  public void setupTabs() {
    tabPane.removeAll();
    Element[] tabList = tParse.getTabs();
    for(int i = 0;i< tabList.length;i++) {
      String thisName = tabList[i].getAttribute("Name");
      if(thisName.length() == 0) thisName = tabList[i].getAttribute("XMLName");
      TabPanel tPanel = new TabPanel(tabList[i]);
      JScrollPane scrollPane = new JScrollPane(tPanel);
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      String desc = tabList[i].getAttribute("Description");
      if (desc.length() == 0) tabPane.addTab(thisName, null, scrollPane, null);
      else tabPane.addTab(thisName, null, scrollPane, desc);
      int keyNumber = getKey(i+1);
      if(keyNumber !=0 ) tabPane.setMnemonicAt(i, keyNumber);
    }
  }
  
  /**sets up the JTabbedPane given an OMENode from an OMEXML file.
  *the template will set which parts of the file are displayed.
  */
  public void setupTabs(OMENode ome) {
    tabPane.removeAll();
    Element[] tabList = tParse.getTabs();
    Vector actualTabs = new Vector(2 * tabList.length);
    for(int i = 0;i< tabList.length;i++) {
      Vector inOmeList = ome.getChildren(tabList[i].getAttribute("XMLName"));
      int vSize = inOmeList.size();
      if(vSize >0) {
        for(int j = 0;j<vSize;j++) {
          String thisName = tabList[i].getAttribute("Name");
          if(thisName.length() == 0) thisName = tabList[i].getAttribute("XMLName");
          TabPanel tPanel = new TabPanel(tabList[i]);
          tPanel.oNode = (OMEXMLNode) inOmeList.get(j);
          JScrollPane scrollPane = new JScrollPane(tPanel);
          scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
          scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
          String desc = tabList[i].getAttribute("Description");
          if (vSize > 1) {
            Integer jInt = new Integer(j+1);
            thisName = thisName + " - " + jInt.toString();
          }
          if (desc.length() == 0) tabPane.addTab(thisName, null, scrollPane, null);
          else tabPane.addTab(thisName, null, tPanel, desc);
          actualTabs.add(tabList[i]);
        }
      }
      else {
        String thisName = tabList[i].getAttribute("Name");
        if(thisName.length() == 0) thisName = tabList[i].getAttribute("XMLName");
        TabPanel tPanel = new TabPanel(tabList[i]);
        JScrollPane scrollPane = new JScrollPane(tPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        String desc = tabList[i].getAttribute("Description");
        if (desc.length() == 0) tabPane.addTab(thisName, null, scrollPane, null);
        else tabPane.addTab(thisName, null, tPanel, desc);
        actualTabs.add(tabList[i]);
      }
    }
    for(int i = 0;i<actualTabs.size();i++) {
      int keyNumber = getKey(i+1);
      if(keyNumber !=0 ) tabPane.setMnemonicAt(i, keyNumber);
    }
    MetadataNotebook mn = (MetadataNotebook) getParent().getParent().getParent();
    mn.changeTabMenu(actualTabs.toArray());
    Element [] ele = new Element[0];
  }
  
  public void renderTab(TabPanel tp) {
    if(tp.oNode == null) {
      tp.removeAll();

  //Set up the GridBagLayout  
      tp.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.NONE;
      gbc.anchor = GridBagConstraints.NORTHWEST;
      Insets ins = new Insets(10,10,10,10);
      gbc.insets = ins;
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.gridheight = 1;
  //placeY will hold info on which position to add a new component to the layout
      int placeY = 0;
  
      
  //add a title label to show which element  
  
      JPanel titlePanel = new JPanel();
      titlePanel.setLayout(new GridLayout(2,1));
      JLabel title = new JLabel();
      Font thisFont = title.getFont();
      Font newFont = new Font(thisFont.getFontName(),Font.BOLD,16);
      title.setFont(newFont);
      title.setText(tp.name + ":");
      titlePanel.add(title);

  
  //if title has a description, add it in italics
      if (tp.el.hasAttribute("Description")) {
        if(tp.el.getAttribute("Description").length() != 0) {
          JLabel descrip = new JLabel();
          newFont = new Font(thisFont.getFontName(),Font.ITALIC,thisFont.getSize());
          descrip.setFont(newFont);
          descrip.setText( "     " + tp.el.getAttribute("Description"));
          ins = new Insets(10,30,10,10);
          titlePanel.add(descrip);
        }
      }
      
      gbc.gridy = placeY;
      tp.add(titlePanel,gbc);
      placeY++;
      
//NEED TO HANDLE CDATA HERE      
      Element cDataEl = DOMUtil.getChildElement("CData",tp.el);
      if (cDataEl != null) DOMUtil.getCharacterData(cDataEl);
      
      Vector attrList = DOMUtil.getChildElements("OMEAttribute",tp.el);

      myTableModel = new DefaultTableModel(TREE_COLUMNS, 0) {
        public boolean isCellEditable(int row, int col) { 
          if(col < 1) return false;
          else return true;
        }  
      };
      
      JPanel tablePanel = new JPanel();
      tablePanel.setLayout(new GridLayout(3,1));
      JLabel tableName = new JLabel(tp.name + " Attributes:");
      JTable newTable = new JTable(myTableModel);
      newTable.getColumnModel().getColumn(0).setPreferredWidth(150);
      newTable.getColumnModel().getColumn(1).setPreferredWidth(400);
      tablePanel.add(tableName);
      tablePanel.add(newTable.getTableHeader());
      tablePanel.add(newTable);
      gbc.gridy = placeY;
      tp.add(tablePanel,gbc);
      placeY++;

      
    // update OME-XML attributes table
      myTableModel.setRowCount(attrList.size());
      for (int i=0; i<attrList.size(); i++) {
        Element thisEle = null;
        if (attrList.get(i) instanceof Element) thisEle = (Element) attrList.get(i); 
        myTableModel.setValueAt(thisEle.getAttribute("XMLName"), i, 0);
      }

      
    }
    else {
    }
  }
  
  /*changes the selected tab to tab of index i
  */
  public void tabChange(int i) {
    tabPane.setSelectedIndex(i);
  }

  // -- Component API methods --

  /** Sets the initial size of the metadata pane to be reasonable. */
  public Dimension getPreferredSize() { return new Dimension(700, 500); }

  // -- Runnable API methods --

  /** Shows or hides the proper subpanes. */
  public void run() {
    tabPane.setVisible(!raw);
    rawPanel.setVisible(raw);
    validate();
    repaint();
  }
  
  // -- ChangeListener API methods --
   
  public void stateChanged(ChangeEvent e) {
    if(e.getSource() instanceof JTabbedPane) {
      if(tabPane.getSelectedComponent() instanceof JScrollPane) {
        JScrollPane sp = (JScrollPane) tabPane.getSelectedComponent();
        if(sp.getViewport().getView() instanceof TabPanel) {
          TabPanel tp = (TabPanel) sp.getViewport().getView();
          renderTab(tp);
        }
      }
    }
  }
  
  // -- Static methods --
  
  public static int getKey(int i) {
      int keyNumber = 0;
      switch (i) {
        case 1 : 
	  keyNumber = KeyEvent.VK_1;
          break;
        case 2 : 
          keyNumber = KeyEvent.VK_2;
          break;
        case 3 : 
	  keyNumber = KeyEvent.VK_3;
          break;
        case 4 : 
	  keyNumber = KeyEvent.VK_4;
          break;
        case 5 : 
	  keyNumber = KeyEvent.VK_5;
          break;
        case 6 : 
	  keyNumber = KeyEvent.VK_6;
          break;
        case 7 : 
	  keyNumber = KeyEvent.VK_7;
          break;
        case 8 : 
          keyNumber = KeyEvent.VK_8;
          break;
        case 9 : 
          keyNumber = KeyEvent.VK_9;
          break;
        case 10 : 
          keyNumber = KeyEvent.VK_0;
          break;
        default:
          keyNumber = 0;
      }
      return keyNumber;
  }
  
  // -- Helper classes --

  /** Helper class to make my life easier in the creation and use of tabs
  *   associates a given xml template element and also an optional OMEXMLNode
  *   with a JPanel that represents the content of a tab.
  */
  public class TabPanel extends JPanel {
    public Element el;
    public String name;
    public OMEXMLNode oNode;
    public TabPanel(Element el) {
      this.el = el;
      oNode = null;
      name = el.getAttribute("Name");
      if(name.length() == 0) name= el.getAttribute("XMLName");
    }
    public String toString() { return el == null ? "null" : el.getTagName(); }
  }
}

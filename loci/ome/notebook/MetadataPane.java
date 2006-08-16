package loci.ome.notebook;

import java.awt.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import loci.formats.*;
import loci.formats.in.*;
import org.openmicroscopy.xml.*;
import org.w3c.dom.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import javax.swing.table.*;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
*   MetadataPane.java:
*      MetadataPane is a panel that displays OME-XML metadata.
*      Most of the gui code is in here.
*			 If you want a panel instead of a window, instantiate this
*    instead of MetadataNotebook.
*
*   Written by: Christopher Peterson <crpeterson2@wisc.edu>
*/

public class MetadataPane extends JPanel
  implements ActionListener, Runnable
{
  // -- Constants --
  protected static final String[] TREE_COLUMNS = {"Attribute", "Value", "Goto"};
  
  public static final ImageIcon DATA_BULLET = 
    createImageIcon("Icons/Bullet3.gif",
      "An icon signifying that metadata is present.");
            
  public static final ImageIcon NO_DATA_BULLET = 
    createImageIcon("Icons/Bullet2.gif",
      "An icon signifying that no metadata is present.");
      
  public static final Color ADD_COLOR =
    new Color(0,100,0);
    
  public static final Color DELETE_COLOR =
    new Color(100,0,0);
    
  public static final Color TEXT_COLOR =
    new Color(0,0,50);

  // -- Fields --

  /** Pane containing XML tree. */
  protected JTabbedPane tabPane;

  /** TemplateParser object*/
  protected TemplateParser tParse;

  /** Keeps track of the OMENode being operated on currently*/
  protected OMENode thisOmeNode;

  /** A list of all TablePanel objects */
  protected Vector panelList;

  /** A list of TablePanel objects that have ID */
  protected Vector panelsWithID;

  /** A list of external references to be added to the combobox cell editor*/
  protected Vector addItems;
  
  /** A list of the TabPanels*/
  protected Vector tabPanelList;
 
  /** Hashtable containing internal semantic type defs in current file*/
  public Hashtable internalDefs;

  /** Signifies that the current file has
  *   changed from the last saved version*/
  public boolean hasChanged;
  
  /** If true, the save button should be display in each TabPanel*/
  protected boolean addSave;
  
  /** Holds the original file if it is of TIFF format*/
  protected File originalTIFF;
  
  /** Holds the currently edited file, or null if none*/
  protected File currentFile;
  
  /** Holds the first image of a tiff file*/
  public BufferedImage img, thumb;

  // -- Fields - raw panel --

  /** Panel containing raw XML dump. */
  protected JPanel rawPanel;

  /** Text area displaying raw XML. */
  protected JTextArea rawText;

  /** Whether XML is being displayed in raw form. */
  protected boolean raw;


  // -- Constructor --

  /** Constructs widget for displaying OME-XML metadata. */
  public MetadataPane() {
    this((File) null, true);
  }
  
  public MetadataPane(File f)
  {
    this(f, true);
  }
  
  //file: the file to open initially
  //addSave: whether or not to display the save button
  public MetadataPane(File file, boolean save) {
    // -- General Field Initialization --

    panelList = new Vector();
    panelsWithID = new Vector();
    addItems = new Vector();
    File f = new File("Template.xml");
    tParse = new TemplateParser(f);
    thisOmeNode = null;
    internalDefs = null;
    hasChanged = false;
    currentFile = null;
    addSave = save;
    originalTIFF = null;
    img = null;
    thumb = null;

    // -- Tabbed Pane Initialization --

    tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
    setupTabs();
    setLayout(new CardLayout());
    add(tabPane,"tabs");
    setPreferredSize(new Dimension(700, 500));
    tabPane.setVisible(true);
    setVisible(true);
 
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
    add(rawPanel,"raw");
    
    if (file != null) {
	    setOMEXML(file);
	    if (getTopLevelAncestor() instanceof MetadataNotebook) {
	      MetadataNotebook mn = (MetadataNotebook) getTopLevelAncestor();
	      mn.setCurrentFile(file);
	    }
	  }
  }

  // -- MetadataPane API methods --

  /**
  * retrieves the current document object describing the whole OMEXMLNode tree
  */
  public Document getDoc() {
    Document doc = null;
    try {
      doc = thisOmeNode.getOMEDocument(false);
    }
    catch (Exception e) {    }
    return doc;
  }
  
  public boolean getState() { return hasChanged; }
  
  public void stateChanged(boolean change) {
    hasChanged = change;
    for(int i = 0; i < tabPanelList.size();i++) {
      TabPanel thisTab = (TabPanel) tabPanelList.get(i);
      if(change) thisTab.saveButton.setForeground(ADD_COLOR);
      else thisTab.saveButton.setForeground(TEXT_COLOR);
    }
  } 

  public OMENode getRoot() { return thisOmeNode; }
  
  public void saveFile(File file) {
    try {
      //use the node tree in the MetadataPane to write flattened OMECA
      //to a given file
      if(originalTIFF != null) {
        String xml = thisOmeNode.writeOME(true);
        
        if(originalTIFF == file) {
          RandomAccessFile raf = new RandomAccessFile(file, "rw");
          TiffTools.overwriteIFDValue(raf, 0, TiffTools.IMAGE_DESCRIPTION, xml);
          raf.close();
        }
        else {
          FileInputStream fis = new FileInputStream(originalTIFF);
          FileOutputStream fos = new FileOutputStream(file);
          int myByte = fis.read();
          while (myByte != -1) {
            fos.write(myByte);
            myByte = fis.read();
          }
          fis.close();
          fos.close();
          
          RandomAccessFile raf = new RandomAccessFile(file, "rw");
          TiffTools.overwriteIFDValue(raf, 0, TiffTools.IMAGE_DESCRIPTION, xml);
          raf.close();
        }
      }
      else {
	      thisOmeNode.writeOME(file, true);
	      if (getTopLevelAncestor() instanceof MetadataNotebook) {
	        MetadataNotebook mdn = (MetadataNotebook) getTopLevelAncestor();
	        mdn.setTitle("OME Metadata Notebook - " + file);
	      }
      }
    }
    catch (Exception e) {
      //if all hell breaks loose, display an error dialog
      JOptionPane.showMessageDialog(getTopLevelAncestor(),
            "Sadly, the file you specified is either write-protected\n" +
            "or in use by another program. Game over, man.",
            "Unable to Write to Specified File", JOptionPane.ERROR_MESSAGE);
      System.out.println("ERROR! Attempt failed to open file: " + file.getName() );
    }
  }

  /**
   * Sets the displayed OME-XML metadata to correspond
   * to the given character string of XML.
   */
  private void setOMEXML(String xml) {
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
        originalTIFF = file;
        
	      OMENode ome = null;
	      
	      try {
		      ImageReader reader = new ImageReader();
					OMEXMLMetadataStore ms = new OMEXMLMetadataStore();
					reader.setMetadataStore(ms);  // tells reader to write metadata as it's being parsed to an OMENode (DOM in memory)
					String id = file.getPath();
					
				  img = reader.openImage(id, 0); // gets first image from the file
					int width = 50, height = 50;
					thumb = ImageTools.scale(img, width, height);
					ome = (OMENode) ms.getRoot();
				}
				catch (Exception exc) {
				  exc.printStackTrace();
				}
			
				setOMEXML(ome);
      }
      else {
        originalTIFF = null;
        img = null;
        thumb = null;
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
      
      currentFile = file;
      return true;
    }
    catch (IOException exc) { return false; }
  }

  /** Sets the displayed OME-XML metadata. */
  private void setOMEXML(OMENode ome) {
    // test for document, then call the setup(OMENode ome) method
    Document doc = null;
    try { doc = ome == null ? null : ome.getOMEDocument(false); }
    catch (Exception exc) { }
    if (doc != null) {
      internalDefs = new Hashtable();

			//time to parse internal semantic type defs in file
			//to handle appropriate reference types
	    Element thisRoot = ome.getDOMElement();
	    NodeList nl = thisRoot.getChildNodes();
	    for (int j = 0;j < nl.getLength();j++) {
	      Node node = nl.item(j);
	      if(node instanceof Element) {
	        Element someE = (Element) node;
	        if (someE.getTagName().equals("STD:SemanticTypeDefinitions")) {
	          NodeList omeEleList = node.getChildNodes();
	          for(int k = 0;k < omeEleList.getLength();k++) {
	            node = omeEleList.item(k);
	            if(node instanceof Element) {
	              Element omeEle = (Element) node;
	              if (omeEle.getTagName().equals("SemanticType")) {
	                NodeList omeAttrList = node.getChildNodes();
	                Hashtable thisHash = new Hashtable(10);
	                for(int l = 0;l < omeAttrList.getLength();l++) {
	                  node = omeAttrList.item(l);
	                  if(node instanceof Element) {
	                    Element omeAttr = (Element) node;
	                    if (omeAttr.getTagName().equals("Element")) {
	                      if(omeAttr.hasAttribute("DataType")) {
	                        String dType = omeAttr.getAttribute("DataType");
	                        if(dType.equals("reference")) {
	                          String attrName = omeAttr.getAttribute("Name");
	                          String refType = omeAttr.getAttribute("RefersTo");
	                          thisHash.put(attrName,refType);
	                        }
	                      }
	                    }
	                  }
	                }
	                internalDefs.put(omeEle.getAttribute("Name"), thisHash);
	              }
	            }
	          }
	        }
	      }
	    }
    
      thisOmeNode = ome;
      stateChanged(false);
      setupTabs(ome);
    }
  }

  /** Sets up the JTabbedPane based on a template, assumes that no OMEXML
  *   file is being compared to the template, so no data will be displayed.
  *   should be used to initialize the application and to create new OMEXML
  *   documents based on the template
  */
  public void setupTabs() {
    //make sure all old gui stuff is tossed when this called twice.
    //also clear our TablePanel lists
    tabPanelList = new Vector();
    panelList = new Vector();
    panelsWithID = new Vector();
    addItems = new Vector();
    tabPane.removeAll();
    currentFile = null;
    originalTIFF = null;
    img = null;
    thumb = null;
    internalDefs = new Hashtable();
    try {thisOmeNode = new OMENode();}
    catch(Exception e) {e.printStackTrace();}

    //use the list acquired from Template.xml to form the initial tabs
    Element[] tabList = tParse.getTabs();
    for(int i = 0;i< tabList.length;i++) {
      String thisName = tabList[i].getAttribute("Name");
      if(thisName.length() == 0) thisName = tabList[i].getAttribute("XMLName");
      //make a TabPanel Object that represents the panel
      //that displays data for a node
      TabPanel tPanel = new TabPanel(tabList[i]);

      //set the field oNode in TabPanel to reflect the structure of the xml
      //document being formed
      
      //do all the good stuff to flesh out the TabPanel's gui with the tables
      //and assorted stuff
      renderTab(tPanel);

      //set up a scrollpane to hold the TabPanel in case it's too large to fit
      JScrollPane scrollPane = new JScrollPane(tPanel);
      scrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      scrollPane.setHorizontalScrollBarPolicy(
      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      String desc = tabList[i].getAttribute("Description");
      if (desc.length() == 0) tabPane.addTab(thisName, NO_DATA_BULLET,
        scrollPane, null);
      else tabPane.addTab(thisName, NO_DATA_BULLET, scrollPane, desc);
      int keyNumber = getKey(i+1);
      if(keyNumber !=0 ) tabPane.setMnemonicAt(i, keyNumber);
    }

    //Makes sure that the external references do not mirror the internal ones
    //since there should be no intersection between the two sets
    for (int j = 0;j<panelsWithID.size();j++) {
      TablePanel tempTP = (TablePanel) panelsWithID.get(j);
      String tryID = "(External) " + tempTP.id;
      if(addItems.indexOf(tryID) >= 0) addItems.remove(tryID);
    }

    //this part sets up the refTable's comboBox editor to have choices
    //corresponding to every TablePanel that has a valid ID attribute
    for (int i = 0;i<panelList.size();i++) {
      TablePanel p = (TablePanel) panelList.get(i);
      p.setEditor();
    }
    
    stateChanged(false);
  }

  /** sets up the JTabbedPane given an OMENode from an OMEXML file.
  *   the template will set which parts of the file are displayed.
  */
  public void setupTabs(OMENode ome) {
    //Get rid of old gui components and old TablePanel lists
    //when new file is opened
    tabPane.removeAll();
    tabPanelList = new Vector();
    panelList = new Vector();
    panelsWithID = new Vector();
    addItems = new Vector();

    //use the list acquired from Template.xml to form the initial tabs
    Element[] tabList = tParse.getTabs();
    Vector actualTabs = new Vector(2 * tabList.length);
    Vector oNodeList = new Vector(2 * tabList.length);
    for(int i = 0;i< tabList.length;i++) {
      //since we have an OMEXML file to compare to now, we have to worry about
      //repeat elements
      //inOmeList will hold all instances of a particular tagname on one level
      //of the node-tree
      Vector inOmeList = null;
      String aName = tabList[i].getAttribute("XMLName");
      //work-around checks if we need to look in CustomAttributes,
      //and subsequently ignore it
      if( aName.equals("Image") || aName.equals("Feature") ||
        aName.equals("Dataset") || aName.equals("Project") )
      {
        inOmeList = ome.getChildren(aName);
      }
      else {
        if( ome.getChild("CustomAttributes") != null) 
          inOmeList = ome.getChild("CustomAttributes").getChildren(aName);
      }
      int vSize = 0;
      if (inOmeList != null) vSize = inOmeList.size();
      //check to see if one or more elements with the given tagname
      //(a.k.a. "XMLName") exist in the file
      if(vSize >0) {
        for(int j = 0;j<vSize;j++) {
          String thisName = tabList[i].getAttribute("Name");
          if(thisName.length() == 0) {
            thisName = tabList[i].getAttribute("XMLName");
          }
          //create our friend, the TabPanel, which holds the template Element
          //and the actual OMEXMLNode that either existed previously or has
          //been created by the ReflectedUniverse mumbojumbo
          TabPanel tPanel = new TabPanel(tabList[i]);
          tPanel.oNode = (OMEXMLNode) inOmeList.get(j);
          //call renderTab(TabPanel tp) to set up the TabPanel gui, create
          //TablePanels to display Elements, etc.
          renderTab(tPanel);
          //create a scrollpane to hold the tabpanel in case it is too large
          JScrollPane scrollPane = new JScrollPane(tPanel);
          scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
          //test if a description is associated with this tab in the template
          String desc = tabList[i].getAttribute("Description");
          thisName = getTreePathName(tPanel.el,tPanel.oNode);
          if (desc.length() == 0) {
            tabPane.addTab(thisName, DATA_BULLET, scrollPane, null);
          }
          else tabPane.addTab(thisName, DATA_BULLET, scrollPane, desc);
          actualTabs.add(tabList[i]);
          oNodeList.add(tPanel.oNode);
        }
      }
      //if no instances of tagname in file,
      //still set up a blank table based on the template
      else {
        //this section the same as above case, see comments there
        String thisName = tabList[i].getAttribute("Name");
        if(thisName.length() == 0) {
          thisName = tabList[i].getAttribute("XMLName");
        }
        TabPanel tPanel = new TabPanel(tabList[i]);
        
        renderTab(tPanel);
        JScrollPane scrollPane = new JScrollPane(tPanel);
        scrollPane.setVerticalScrollBarPolicy(
          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        String desc = tabList[i].getAttribute("Description");
        if (desc.length() == 0) {
          tabPane.addTab(thisName, NO_DATA_BULLET, scrollPane, null);
        }
        else tabPane.addTab(thisName, NO_DATA_BULLET, scrollPane, desc);
        actualTabs.add(tabList[i]);
        oNodeList.add(tPanel.oNode);
      }
    }
    //set up mnemonics, and form an array holding the names of the tabs
    String[] tabNames = new String[actualTabs.size()];
    for(int i = 0;i<actualTabs.size();i++) {
      int keyNumber = getKey(i+1);
      if(keyNumber !=0 ) tabPane.setMnemonicAt(i, keyNumber);
      Element e = (Element) actualTabs.get(i);
      tabNames[i] = getTreePathName(e, (OMEXMLNode) oNodeList.get(i));
      if (tabNames[i] == null) tabNames[i] = getTreePathName(e);
    }
    //change the "Tabs" menu in the original window to reflect the actual tabs
    //created (duplicate tabs are the reason for this)
    if (getTopLevelAncestor() instanceof MetadataNotebook) {
      MetadataNotebook mn = (MetadataNotebook) getTopLevelAncestor();
      mn.changeTabMenu(tabNames);
    }

    //Makes sure that the external references do not mirror the internal ones
    //since there should be no intersection between the two sets
    for (int j = 0;j<panelsWithID.size();j++) {
      TablePanel tempTP = (TablePanel) panelsWithID.get(j);
      String tryID = "(External) " + tempTP.id;
      if(addItems.indexOf(tryID) >= 0) addItems.remove(tryID);
    }

    //this part sets up the refTable's comboBox editor to have choices
    //corresponding to every TablePanel that has a valid ID attribute
    for (int i = 0;i<panelList.size();i++) {
      TablePanel p = (TablePanel) panelList.get(i);
      p.setEditor();
    }
  }

  /**
   * fleshes out the GUI of a given TabPanel, adding TablePanels appropriately
   */
  public void renderTab(TabPanel tp) {
    if(!tp.isRendered) {
      tp.isRendered = true;
      tp.removeAll();
      
      Vector iHoldTables = new Vector();

      //add a title label to show which element
      JPanel titlePanel = new JPanel();
      titlePanel.setLayout(new GridLayout(2,1));
      JLabel title = new JLabel();
      Font thisFont = title.getFont();
      Font newFont = new Font(thisFont.getFontName(),Font.BOLD,18);
      title.setFont(newFont);
      if (tp.oNode != null) title.setText(" " + getTreePathName(tp.el,tp.oNode) + ":");
      else title.setText(" " + getTreePathName(tp.el) + ":");
      title.setForeground(new Color(255,255,255));
      
      tp.saveButton = new JButton("QuickSave");
      tp.saveButton.setPreferredSize(new Dimension(100,17));
      tp.saveButton.setActionCommand("save");
      tp.saveButton.addActionListener(this);
      tp.saveButton.setOpaque(false);
      tp.saveButton.setForeground(TEXT_COLOR);
      if(getState()) tp.saveButton.setForeground(ADD_COLOR);
      if(!addSave) tp.saveButton.setVisible(false);
     
      Color aColor = getBackground();
      
      JTextArea descrip = new JTextArea();

      //if title has a description, add it in italics
      if (tp.el.hasAttribute("Description")) {
        if(tp.el.getAttribute("Description").length() != 0) {
          descrip.setEditable(false);
          descrip.setLineWrap(true);
          descrip.setWrapStyleWord(true);
          descrip.setBackground(aColor);
          newFont = new Font(thisFont.getFontName(),
            Font.ITALIC,thisFont.getSize());
          descrip.setFont(newFont);
          descrip.setText( "     " + tp.el.getAttribute("Description"));
        }
      }
      
      FormLayout myLayout = new FormLayout(
        "pref, 5dlu, pref:grow:right, 5dlu",
        "5dlu, pref, 5dlu, pref");
      PanelBuilder build = new PanelBuilder(myLayout);
      CellConstraints cellC = new CellConstraints();
      
      build.add( title, cellC.xy(1, 2, "left,center"));
      build.add( tp.saveButton, cellC.xy(3, 2, "right,center"));
      build.add( descrip, cellC.xyw(1, 4, 4, "fill,center"));
      titlePanel = build.getPanel();
      titlePanel.setBackground(TEXT_COLOR);
      
	  //this sets up titlePanel so we can access its height later to
	  //use for "Goto" button scrollpane view setting purposes
      tp.titlePanel = titlePanel;

	  //First instantiation of TablePanel. This one corresponds to the
	  //actual "top-level" element, e.g. what the main Tab element is
      TablePanel pan = new TablePanel(tp.el, tp, tp.oNode);
      iHoldTables.add(pan);

	  //make the nested elements have a further indent to distinguish them

	  //look at the template to get the nested Elements we need to display
	  //with their own TablePanels
      Vector theseElements = DOMUtil.getChildElements("OMEElement",tp.el);
      //will be a list of those nested elements that have their own nested
      //elements
      Vector branchElements = new Vector(theseElements.size());

	  //check out each nested Element
      for(int i = 0;i<theseElements.size();i++) {
        Element e = null;
        if (theseElements.get(i) instanceof Element) {
          e = (Element) theseElements.get(i);
        }
        if (DOMUtil.getChildElements("OMEElement",e).size() != 0) {
          branchElements.add(e);
        }
        else {
          if(tp.oNode != null) {
            Vector v = new Vector();
            String aName = e.getAttribute("XMLName");
            if( aName.equals("Image") || aName.equals("Feature") ||
              aName.equals("Dataset") || aName.equals("Project") )
            {
              v = DOMUtil.getChildElements(aName, tp.oNode.getDOMElement());
            }
            else if (tp.oNode.getChild("CustomAttributes") != null) {
              v = DOMUtil.getChildElements(aName, 
                tp.oNode.getChild("CustomAttributes").getDOMElement());
            }

            if (v.size() == 0) {         
              OMEXMLNode n = null;
              TablePanel p = new TablePanel(e, tp, n);
              iHoldTables.add(p);
            }
            else {
              for(int j = 0;j<v.size();j++) {
                Element anEle = (Element) v.get(j);
                
	              OMEXMLNode n = null;
	              String unknownName = aName;
	
	              try {
	                ReflectedUniverse r = new ReflectedUniverse();
	                if (unknownName.equals("Project") ||
	                  unknownName.equals("Feature") ||
	                  unknownName.equals("CustomAttributes") ||
	                  unknownName.equals("Dataset") ||
	                  unknownName.equals("Image"))
	                {
	                  r.exec("import org.openmicroscopy.xml." +
	                    unknownName + "Node");
	                  r.setVar("DOMElement", anEle);
	                  r.exec("result = new " + unknownName + "Node(DOMElement)");
	                  n = (OMEXMLNode) r.getVar("result");
	                }
	                else {
	                    r.exec("import org.openmicroscopy.xml.st." +
	                      unknownName + "Node");
	                    r.setVar("DOMElement", anEle);
	                    r.exec("result = new " + unknownName + "Node(DOMElement)");
	                    n = (OMEXMLNode) r.getVar("result");
	                }
	              }
	              catch (Exception exc) {
//	                System.out.println(exc.toString());
	              }
	              if (n == null) {
	                n = new AttributeNode(anEle);
	              }

                TablePanel p = new TablePanel(e, tp, n);
                iHoldTables.add(p);
              }
            }
          }
          else {
            OMEXMLNode n = null;
            
            TablePanel p = new TablePanel(e, tp, n);
            iHoldTables.add(p);
          }
        }
      }
      
      String rowString = "pref, 10dlu, ";
      for (int i = 0;i<iHoldTables.size();i++) {
      	rowString = rowString + "pref, 5dlu, ";
      }
      rowString = rowString.substring(0, rowString.length() - 2);
        
      FormLayout layout = new FormLayout(
        "5dlu, 5dlu, pref:grow, 5dlu, 5dlu",
        rowString);
      tp.setLayout(layout);
      CellConstraints cc = new CellConstraints();
      
      tp.add(titlePanel, cc.xyw(1,1,5));
      
      int row = 1;
      for (int i = 0;i<iHoldTables.size();i++) {
        row = row + 2;
				if (i == 0) tp.add( (Component)iHoldTables.get(i), cc.xyw(2, row, 2, "fill,center"));
				else tp.add( (Component)iHoldTables.get(i), cc.xyw(3, row, 2, "fill,center"));
      }
      
      //Layout stuff distinguishes between the title and the data panels
    }
  }

  /** changes the selected tab to tab of index i */
  public void tabChange(int i) {
    tabPane.setSelectedIndex(i);
  }
  
  /** resets all gui to reflect changes to the node tree*/
  public void reRender() {
    int tabIndex = tabPane.getSelectedIndex();
    setupTabs(thisOmeNode);
    tabPane.setSelectedIndex(tabIndex);
  }

  // -- Runnable API methods --

  /** Shows or hides the proper subpanes. */
  public void run() {
    tabPane.setVisible(!raw);
    rawPanel.setVisible(raw);
    validate();
    repaint();
  }

  // -- Event API methods --
  
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("save")) {
      if(currentFile != null) {
        saveFile(currentFile);
        stateChanged(false);
      }
      else JOptionPane.showMessageDialog(getTopLevelAncestor(),
            "There is no current file specified,\n" +
            "so you cannot QuickSave.",
            "No Current File Found", JOptionPane.ERROR_MESSAGE);
		}
  }
  
  // -- Static methods --
  
  /** Returns an ImageIcon, or null if the path was invalid. */
  protected static ImageIcon createImageIcon(String path,
                                           String description) {
    java.net.URL imgURL = MetadataPane.class.getResource(path);
    if (imgURL != null) {
        return new ImageIcon(imgURL, description);
    } else {
        System.err.println("Couldn't find file: " + path);
        return null;
    }
  }

  public static String getTreePathName(Element e) {
    String thisName = null;
    if(e.hasAttribute("Name"))thisName = e.getAttribute("Name");
    else thisName = e.getAttribute("XMLName");

    Element aParent = DOMUtil.getAncestorElement("OMEElement",e);
    while(aParent != null) {
      if(aParent.hasAttribute("Name")) {
        thisName = aParent.getAttribute("Name") + thisName;
      }
      else thisName = aParent.getAttribute("XMLName") + ": " + thisName;
      aParent = DOMUtil.getAncestorElement("OMEElement",aParent);
    }
    return thisName;
  }

  public static String getTreePathName(Element el, OMEXMLNode on) {
    if(el != null && on != null) {
      Vector pathList = new Vector();
      Element aParent = on.getDOMElement();
      Vector pathNames = getTreePathList(el);
      pathNames.add("OME");
      pathList.add(aParent);

      for (int i = 1;i<pathNames.size();i++) {
        String s = (String) pathNames.get(i);
        aParent = DOMUtil.getAncestorElement(s, aParent);
        pathList.add(0,aParent);
      }

      String result = "";

      for (int i = 0;i<pathList.size() - 1;i++) {
        aParent = (Element) pathList.get(i);
        Element aChild = (Element) pathList.get(i+1);
        String thisName = aChild.getTagName();

        NodeList nl = aParent.getElementsByTagName(thisName);

        if (nl.getLength() == 1) {
          Element e = (Element) nl.item(0);
          if (i == 0) result = result + e.getTagName();
          else result = result + ": " + e.getTagName();
        }
        else {
          for (int j = 0;j<nl.getLength();j++) {
            Element e = (Element) nl.item(j);
            if (e == aChild) {
              Integer aInt = new Integer(j+1);
              if (i == 0) result += e.getTagName() + " (" + aInt.toString() + ")";
              else result += ": " + e.getTagName() + " (" + aInt.toString() + ")";
            }
          }
        }
      }
      return result;
    }
    else return null;
  }
  
  //tests if the given tagname should be 
  //placed under a CustomAttributesNode  
  public static boolean isInCustom(String tagName) {
    if (tagName.equals("Project") ||
      tagName.equals("Feature") ||
      tagName.equals("CustomAttributes") ||
      tagName.equals("Dataset") ||
      tagName.equals("Image"))
    {return false;}
    else return true;
  }
  
  //return a new node of type specified by unknownName
  //with the specified parent
  //N.B. The Parent can either be the direct parent or the
  //ancestor that has the CustomAttributesNode that 
  //is the real parent
  public static OMEXMLNode makeNode(String unknownName, OMEXMLNode parent) {
    OMEXMLNode n = null;        
    CustomAttributesNode caNode = null;

    try {
      ReflectedUniverse r = new ReflectedUniverse();
      if (!isInCustom(unknownName)) {
        r.exec("import org.openmicroscopy.xml." +
          unknownName + "Node");
        r.setVar("parent", parent);
        r.exec("result = new " + unknownName + "Node(parent)");
        n = (OMEXMLNode) r.getVar("result");
      }
      else {
        Element currentCA = DOMUtil.getChildElement("CustomAttributes", 
          parent.getDOMElement());
        if (currentCA != null) {
          caNode = new CustomAttributesNode(currentCA);
          r.exec("import org.openmicroscopy.xml.CustomAttributesNode");
          r.exec("import org.openmicroscopy.xml.st." +
            unknownName + "Node");
          r.setVar("parent", caNode);
          r.exec("result = new " + unknownName + "Node(parent)");
          n = (OMEXMLNode) r.getVar("result");
        }
        else {
          Element cloneEle = DOMUtil.createChild(
            parent.getDOMElement(),"CustomAttributes");
          caNode = new CustomAttributesNode(cloneEle);
          r.exec("import org.openmicroscopy.xml.CustomAttributesNode");
          r.exec("import org.openmicroscopy.xml.st." +
            unknownName + "Node");
          r.setVar("parent", caNode);
          r.exec("result = new " + unknownName + "Node(parent)");
          n = (OMEXMLNode) r.getVar("result");
        }
      }
    }		        
    catch (Exception exc) {
//      System.out.println(exc.toString());
    }
    if (caNode != null && n == null) n = new AttributeNode(caNode, unknownName);
    return n;
  }

  /** returns a vector of Strings representing the XMLNames of the
  *   template's ancestors in ascending order in the list.
  */
  public static Vector getTreePathList(Element e) {
    Vector thisPath = new Vector(10);
    thisPath.add(e.getAttribute("XMLName"));

    Element aParent = DOMUtil.getAncestorElement("OMEElement",e);
    while(aParent != null) {
      thisPath.add(aParent.getAttribute("XMLName"));
      aParent = DOMUtil.getAncestorElement("OMEElement",aParent);
    }
    return thisPath;
  }

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
  public class TabPanel extends JPanel
    implements Scrollable
  {
    protected Element el;
    public String name;
    private boolean isRendered;
    protected OMEXMLNode oNode;
    protected OMENode ome;
    protected JPanel titlePanel;
    protected JButton saveButton;

    public TabPanel(Element el) {
      ome = thisOmeNode;
      isRendered = false;
      this.el = el;
      oNode = null;
      name = getTreePathName(el);
      titlePanel = null;
      tabPanelList.add(this);
      saveButton = null;
    }
    
    public String toString() { return el == null ? "null" : el.getTagName(); }
            
    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }
    
    public int getScrollableUnitIncrement(Rectangle visibleRect,
      int orientation, int direction) {return 5;}
    public int getScrollableBlockIncrement(Rectangle visibleRect,
      int orientation, int direction) {return visibleRect.height;}
    public boolean getScrollableTracksViewportWidth() {return true;}
    public boolean getScrollableTracksViewportHeight() {return false;}    
  }

/** Helper class to handle the various TablePanels that will be created to
*   display the attributes of Elements that have no nested Elements
*/
  public class TablePanel extends JPanel
    implements ActionListener, MouseListener
  {
    public OMEXMLNode oNode;
    public TabPanel tPanel;
    public NotePanel noteP;
    public String id;
    public String name;
    public ClickableTable table;
    public Element el;
    public boolean isTopLevel;
    private JButton noteButton;
    protected Vector attrList, refList;

    public TablePanel(Element e, TabPanel tp, OMEXMLNode on) {
      isTopLevel = false;
      
      //check if this TablePanel is "top level"
      if ( tp.oNode == null ) {
				Vector foundEles = DOMUtil.getChildElements("OMEElement", tParse.getRoot());
				
				for (int i = 0;i < foundEles.size();i++) {
				  Element thisNode = (Element) foundEles.get(i);
				  if (thisNode == e) isTopLevel = true;
				}
      }      
      else if(tp.oNode != null && tp.oNode == on) isTopLevel = true;

      el = e;
      oNode = on;
      tPanel = tp;
      id = null;
      JComboBox comboBox = null;
      if (on != null) name = getTreePathName(e,on);
      else name = getTreePathName(e);
      String thisName = name;
      panelList.add(this);

      Vector fullList = DOMUtil.getChildElements("OMEAttribute",e);
      attrList = new Vector();
      refList = new Vector();
      for(int i = 0;i<fullList.size();i++) {
        Element thisE = (Element) fullList.get(i);
        if(thisE.hasAttribute("Type") ) {
          if(thisE.getAttribute("Type").equals("Ref")) {
            if (oNode != null) {
	            String value = oNode.getAttribute(thisE.getAttribute("XMLName"));
	            if (value != null && !value.equals("")) {
	              if ( addItems.indexOf("(External) " + value) < 0) {
	                addItems.add("(External) " + value);
	              }
	            }
            }
            refList.add(thisE);
          }
          else if(thisE.getAttribute("Type").equals("ID") && oNode != null) {
            if (oNode.getDOMElement().hasAttribute("ID")) {
              id = oNode.getAttribute("ID");
              panelsWithID.add(this);
            }
          }
          else attrList.add(thisE);
        }
        else attrList.add(thisE);
      }

      Element cDataEl = DOMUtil.getChildElement("CData",e);
      if (cDataEl != null) attrList.add(0,cDataEl);
      
      JLabel tableName = null;
      if(oNode == null) tableName = 
        new JLabel(thisName, NO_DATA_BULLET, JLabel.LEFT);
      else tableName = new JLabel(thisName, DATA_BULLET, JLabel.LEFT);
      Font thisFont = tableName.getFont();
      thisFont = new Font(thisFont.getFontName(),
        Font.BOLD,12);
      tableName.setFont(thisFont);
      if (el.hasAttribute("ShortDesc"))
        tableName.setToolTipText(el.getAttribute("ShortDesc"));
      else if (el.hasAttribute("Description"))
        tableName.setToolTipText(el.getAttribute("Description"));
      tableName.setForeground(TEXT_COLOR);
      
      noteButton = new JButton("Notes");
      noteButton.setPreferredSize(new Dimension(85,17));
      noteButton.addActionListener(this);
      noteButton.setActionCommand("getNotes");
      noteButton.setToolTipText(
        "Display or hide the notes associated with this " + name + ".");
      noteButton.setForeground(TEXT_COLOR);
      
      JLabel imageLabel = null;
  		if (name.endsWith("Pixels") || name.endsWith("Pixels (1)")) {
				if(thumb != null) {
        	imageLabel = new JLabel(new ImageIcon(thumb));
        	imageLabel.setToolTipText("The first image of these pixels." +
        	  " Click for full sized image.");
        	imageLabel.addMouseListener(this);
        }
      }
      
      DefaultTableModel myTableModel = 
        new DefaultTableModel(TREE_COLUMNS, 0)
      {
        public boolean isCellEditable(int row, int col) {
          if(col < 1) return false;
          else return true;
        }
      };
      
      table = new ClickableTable(myTableModel, this);
//      table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
      table.getColumnModel().getColumn(0).setPreferredWidth(125);
      table.getColumnModel().getColumn(1).setPreferredWidth(430);
      table.getColumnModel().getColumn(2).setPreferredWidth(70);
      table.getColumnModel().getColumn(2).setMaxWidth(70);
      table.getColumnModel().getColumn(2).setMinWidth(70);
      JTableHeader tHead = table.getTableHeader();
      tHead.setResizingAllowed(true);
      tHead.setReorderingAllowed(true);
//      tHead.setBackground(NotePanel.BACK_COLOR);
//      tHead.setOpaque(false);
      myTableModel.setRowCount(attrList.size() + refList.size());
      
      String clippedName = name;
      if (name.endsWith(")")) clippedName = name.substring(0,name.length() - 4);
      
      JButton addButton = new JButton("New Table");
      addButton.setPreferredSize(new Dimension(110,17));
      addButton.addActionListener(table);
      addButton.setActionCommand("bigAdd");
      addButton.setToolTipText("Create a new " + clippedName + " table.");
      if ( !isTopLevel && tPanel.oNode == null) addButton.setEnabled(false);
      addButton.setForeground(ADD_COLOR);
      
      JButton delButton = new JButton("Delete Table");
      delButton.setPreferredSize(new Dimension(110,17));
      delButton.addActionListener(table);
      delButton.setActionCommand("bigRem");
      delButton.setToolTipText("Delete this " + clippedName + " table.");
      if ( oNode == null) delButton.setVisible(false);
      delButton.setForeground(DELETE_COLOR);
      
      
      noteP = new NotePanel(this);
      setNumNotes(noteP.getNumNotes());
      
      FormLayout layout = new FormLayout(
        "pref, 10dlu, pref, 10dlu, pref, pref:grow:right, 5dlu, pref",
        "pref,2dlu,pref,pref,3dlu,pref,3dlu");
      setLayout(layout);
      CellConstraints cc = new CellConstraints();
      
    	add(tableName, cc.xy(1,1));
			add(noteButton, cc.xy(3,1, "left,center"));
			if (imageLabel != null)
  			add(imageLabel, cc.xy(5,1, "center,top"));
			add(addButton, cc.xy(6,1, "right,center"));
			add(delButton, cc.xy(8,1, "right,center"));
			add(tHead, cc.xyw(1,3,8, "fill, center"));
			add(table, cc.xyw(1,4,8, "fill, center"));
      add(noteP, cc.xyw(1,6,8, "fill,center"));
			
			if (oNode == null) {
        tHead.setVisible(false);
        noteButton.setVisible(false);
        table.setVisible(false);
      }

      if (attrList.size() != 0) {
        // update OME-XML attributes table
        for (int i=0; i<attrList.size(); i++) {
          Element thisEle = null;
          if (attrList.get(i) instanceof Element) {
            thisEle = (Element) attrList.get(i);
          }
          if (thisEle != null) {
            String attrName = thisEle.getAttribute("XMLName");
            if (thisEle.hasAttribute("Name")) {
              myTableModel.setValueAt(thisEle.getAttribute("Name"), i, 0);
              if(oNode != null) {
                if(oNode.getDOMElement().hasAttribute(attrName)) {
                  myTableModel.setValueAt(oNode.getAttribute(attrName), i, 1);
                }

              }
            }
            else if (!thisEle.hasAttribute("Name") &&
              thisEle.hasAttribute("XMLName")) {
              myTableModel.setValueAt(thisEle.getAttribute("XMLName"), i, 0);
              if(oNode != null) {
                if(oNode.getDOMElement().hasAttribute(attrName)) {
                  myTableModel.setValueAt(oNode.getAttribute(attrName), i, 1);
                }

              }
            }
            else {
              if(e.hasAttribute("Name")) {
                myTableModel.setValueAt(e.getAttribute("Name") +
                  " CharData", i, 0);
              }
              else {
                myTableModel.setValueAt(e.getAttribute("XMLName") +
                  " CharData", i, 0);
              }
              if(oNode != null) {
                if(DOMUtil.getCharacterData(oNode.getDOMElement()) != null) {
                  myTableModel.setValueAt(
                    DOMUtil.getCharacterData(oNode.getDOMElement() ), i, 1);
                }
              }
            }
            
          }
        }
      }
      
      if (refList.size() > 0) {      
        for (int i=0; i<refList.size(); i++) {
          Element thisEle = null;
          if (refList.get(i) instanceof Element) {
            thisEle = (Element) refList.get(i);
          }
          if (thisEle != null) {
            if (thisEle.hasAttribute("Name")) {
              myTableModel.setValueAt(thisEle.getAttribute("Name"), 
                i + attrList.size(), 0);
            }
            else if (thisEle.hasAttribute("XMLName")) {
              myTableModel.setValueAt(thisEle.getAttribute("XMLName"), 
                i + attrList.size(), 0);
            }
          }
        } 
      }
      
      TableColumn refColumn = table.getColumnModel().getColumn(1);
      refColumn.setCellRenderer(new VariableComboRenderer(el));
    }

    public void setEditor() {
      if(table != null) {
        TableModel model = table.getModel();
        TableColumn refColumn = table.getColumnModel().getColumn(1);
        for(int i = 0;i < table.getRowCount();i++) {
          if ( i < attrList.size()) {
          }
          else {
	          boolean isLocal = false;
	          String attrName = (String) model.getValueAt(i,0);
	          String value = null;
	          if(oNode != null) value = oNode.getAttribute(attrName);
	          for(int j = 0;j < panelsWithID.size();j++) {
	            TablePanel tp = (TablePanel) panelsWithID.get(j);
	            if (tp.id != null && value != null) {
	              if (value.equals(tp.id)) {
	                isLocal = true;
	                model.setValueAt(tp.name,i,1);
	              }
	            }
	          }
	          if(!isLocal && value != null && !value.equals("")) {
	            model.setValueAt("(External) " + value,i,1);
	          }
	          //makes the initial value non-null to display the buttons
	          model.setValueAt("foobar", i, 2);
	        }
        }

        refColumn.setCellEditor(new VariableComboEditor(panelsWithID,
          addItems, this, internalDefs));
        TableColumn gotoColumn = table.getColumnModel().getColumn(2);
        gotoColumn.setCellEditor(new GotoEditor(this));
        gotoColumn.setCellRenderer(new GotoRenderer());
      }
    }
    
    public void setNumNotes(int n) {
      noteButton.setText("Notes (" + n + ")");
    }

    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof GotoEditor.TableButton) {
        GotoEditor.TableButton tb = (GotoEditor.TableButton) e.getSource();
        JTable jt = tb.table;

        TableModel model = jt.getModel();
        Object obj = model.getValueAt(tb.whichRow, 1);
        if(obj != null && !obj.toString().equals("")) {
	        String aName = obj.toString();
	        TablePanel aPanel = null;
	
	        int whichNum = -23;
	
	        for(int i = 0;i<panelsWithID.size();i++) {
	          aPanel = (TablePanel) panelsWithID.get(i);
	          if (aPanel.name.equals(aName)) whichNum = i;
	        }
	
	        if(whichNum != -23) {
	          TablePanel tablePan = (TablePanel) panelsWithID.get(whichNum);
	          TabPanel tp = tablePan.tPanel;
	          Container anObj = (Container) tp;
	          while(!(anObj instanceof JScrollPane)) {
	            anObj = anObj.getParent();
	          }
	          JScrollPane jScr = (JScrollPane) anObj;
	          while(!(anObj instanceof JTabbedPane)) {
	            anObj = anObj.getParent();
	          }
	          JTabbedPane jTabP = (JTabbedPane) anObj;
	          jTabP.setSelectedComponent(jScr);
	          Point loc = tablePan.getLocation();
	          loc.x = 0;
	          loc.y = tp.titlePanel.getHeight() + loc.y;
	          jScr.getViewport().setViewPosition(loc);
	        }
	        else {
	          JOptionPane.showMessageDialog((Frame) getTopLevelAncestor(),
	            "Since the ID in question refers to something\n" +
	            "outside of this file, you cannot \"Goto\" it.",
	            "External Reference Detected", JOptionPane.WARNING_MESSAGE);
	        }
	      }
	      else {
	        JOptionPane.showMessageDialog((Frame) getTopLevelAncestor(),
	            "Since the ID in question is currently blank,\n" +
	            "you cannot \"Goto\" it.",
	            "Null Reference Detected", JOptionPane.WARNING_MESSAGE);
	      }
      }
      else if(e.getActionCommand().equals("getNotes")) {
        if (noteP.isVisible()) noteP.setVisible(false);
        else noteP.setVisible(true);
        noteP.revalidate();
      }
    }
    
    public void mouseClicked(MouseEvent e) {
      if (e.getSource() instanceof JLabel) {
	      JOptionPane.showMessageDialog(getTopLevelAncestor(), null,
	        "(Full Sized) " + name, JOptionPane.PLAIN_MESSAGE,
	        new ImageIcon(img));
	    }
    }
    
    public void mousePressed(MouseEvent e) {}
	  public void mouseReleased(MouseEvent e) {}
	  public void mouseEntered(MouseEvent e) {}
	  public void mouseExited(MouseEvent e) {}
    
    public void callReRender() {
      reRender();
    }
    
    public void callStateChanged(boolean hasChanged) {
      stateChanged(true);
    }
  }
}

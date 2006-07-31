//
// MetadataPane.java
//

package loci.ome.notebook;

import java.awt.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import loci.formats.RandomAccessStream;
import loci.formats.TiffTools;
import loci.formats.ReflectedUniverse;
import org.openmicroscopy.xml.*;
import org.w3c.dom.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.table.*;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/** MetadataPane is a panel that displays OME-XML metadata. */
public class MetadataPane extends JPanel
  implements Runnable
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

    // -- General Field Initialization --

    panelList = new Vector();
    panelsWithID = new Vector();
    addItems = new Vector();
    tParse = tp;
    thisOmeNode = null;
    internalDefs = null;

    // -- Tabbed Pane Initialization --


    tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
    setupTabs();
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    add(tabPane);
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
    add(rawPanel);
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

  public OMENode getRoot() { return thisOmeNode; }

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
    // test for document, then call the setup(OMENode ome) method
    Document doc = null;
    try { doc = ome == null ? null : ome.getOMEDocument(false); }
    catch (Exception exc) { }
    if (doc != null) {
      thisOmeNode = ome;
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
      OMEXMLNode n = null;
      String unknownName = tabList[i].getAttribute("XMLName");
      CustomAttributesNode caNode = null;

      try {
        //reflect api gets around large switch statements
        ReflectedUniverse r = new ReflectedUniverse();
        if (unknownName.equals("Project") || unknownName.equals("Feature") ||
          unknownName.equals("CustomAttributes") ||
          unknownName.equals("Dataset") || unknownName.equals("Image"))
        {
          r.exec("import org.openmicroscopy.xml." + unknownName + "Node");
          r.setVar("parent", thisOmeNode);
          r.exec("result = new " + unknownName + "Node(parent)");
          n = (OMEXMLNode) r.getVar("result");
        }
        else {
	        Element currentCA = DOMUtil.getChildElement("CustomAttributes", thisOmeNode.getDOMElement());
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
	          Element cloneEle = DOMUtil.createChild(thisOmeNode.getDOMElement(),"CustomAttributes");
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
        System.out.println(exc.toString());
      }
      if (caNode != null && n == null) n = new AttributeNode(caNode, unknownName);
			tPanel.oNode = n;

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
      if (desc.length() == 0) tabPane.addTab(thisName, null, scrollPane, null);
      else tabPane.addTab(thisName, null, scrollPane, desc);
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
    internalDefs = new Hashtable();
    
    try {
      Element damnElement = DOMUtil.findElement("LaserCoordinates", ome.getOMEDocument(true));
      System.out.println(damnElement);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

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
      else inOmeList = ome.getChild("CustomAttributes").getChildren(aName);
      int vSize = inOmeList.size();
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
            tabPane.addTab(thisName, null, scrollPane, null);
          }
          else tabPane.addTab(thisName, null, scrollPane, desc);
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
        
        OMEXMLNode n = null;
        CustomAttributesNode caNode = null;

        try {
          ReflectedUniverse r = new ReflectedUniverse();
          String unknownName = thisName;
          if (unknownName.equals("Project") ||
            unknownName.equals("Feature") ||
            unknownName.equals("CustomAttributes") ||
            unknownName.equals("Dataset") ||
            unknownName.equals("Image"))
          {
            r.exec("import org.openmicroscopy.xml." +
              unknownName + "Node");
            r.setVar("parent", thisOmeNode);
            r.exec("result = new " + unknownName + "Node(parent)");
            n = (OMEXMLNode) r.getVar("result");
          }
          else {
            Element currentCA = DOMUtil.getChildElement("CustomAttributes", thisOmeNode.getDOMElement());
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
              Element cloneEle = DOMUtil.createChild(thisOmeNode.getDOMElement(),"CustomAttributes");
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
          System.out.println(exc.toString());
        }
        if (caNode != null && n == null) n = new AttributeNode(caNode, thisName);
        tPanel.oNode = n;
        
        renderTab(tPanel);
        JScrollPane scrollPane = new JScrollPane(tPanel);
        scrollPane.setVerticalScrollBarPolicy(
          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        String desc = tabList[i].getAttribute("Description");
        if (desc.length() == 0) {
          tabPane.addTab(thisName, null, scrollPane, null);
        }
        else tabPane.addTab(thisName, null, scrollPane, desc);
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
    }
    //change the "Tabs" menu in the original window to reflect the actual tabs
    //created (duplicate tabs are the reason for this)
    MetadataNotebook mn = (MetadataNotebook) getTopLevelAncestor();
    mn.changeTabMenu(tabNames);

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
      titlePanel.add(title);
      Color aColor = title.getBackground();

      //if title has a description, add it in italics
      if (tp.el.hasAttribute("Description")) {
        if(tp.el.getAttribute("Description").length() != 0) {
          JTextArea descrip = new JTextArea();
          descrip.setEditable(false);
          descrip.setLineWrap(true);
          descrip.setWrapStyleWord(true);
          descrip.setBackground(aColor);
          newFont = new Font(thisFont.getFontName(),
            Font.ITALIC,thisFont.getSize());
          descrip.setFont(newFont);
          descrip.setText( "     " + tp.el.getAttribute("Description"));
          titlePanel.add(descrip);
        }
      }
      title.setForeground(new Color(255,255,255));
      titlePanel.setBackground(new Color(0,0,50));
      
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
            
//            System.out.println("These elements were found for " + aName + ":"
//              + v);

            if (v.size() == 0) {
              //Use reflect api to avoid large switch statement to handle
              //construction of different nodes
              //OMEXMLNode child classes
              OMEXMLNode n = null;
              CustomAttributesNode caNode = null;
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
                  r.setVar("parent", tp.oNode);
                  r.exec("result = new " + unknownName + "Node(parent)");
                  n = (OMEXMLNode) r.getVar("result");
                }
                else {

                  Element currentCA = DOMUtil.getChildElement("CustomAttributes", tp.oNode.getDOMElement());
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
                    Element cloneEle = DOMUtil.createChild(tp.oNode.getDOMElement(),"CustomAttributes");
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
                System.out.println(exc.toString());
              }
              if (caNode != null && n == null) {
                n = new AttributeNode(caNode, unknownName);
//                System.out.println("Was not found in file: " + n);
              }
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
	                System.out.println(exc.toString());
	              }
	              if (n == null) {
	                n = new AttributeNode(anEle);
//	                System.out.println("Was found in file: " + n);
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
      
      String rowString = "";
      for (int i = 0;i<iHoldTables.size();i++) {
      	rowString = rowString + "5dlu, pref, ";
      }
      rowString = rowString.substring(0, rowString.length() - 2);
        
      FormLayout layout = new FormLayout(
        "5dlu, 5dlu, pref:grow, 5dlu, 5dlu",
        rowString);
      PanelBuilder builder = new PanelBuilder(layout);
      CellConstraints cc = new CellConstraints();
      
      int row = 0;
      for (int i = 0;i<iHoldTables.size();i++) {
        row = row + 2;
				if (i == 0) builder.add( (Component)iHoldTables.get(i), cc.xyw(2, row, 2));
				else builder.add( (Component)iHoldTables.get(i), cc.xyw(3, row, 2));
      }
      
      //Layout stuff distinguishes between the title and the data panels
      tp.setLayout(new BorderLayout());
      tp.add(titlePanel,BorderLayout.NORTH);
      tp.add(builder.getPanel(),BorderLayout.CENTER);
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
  
  // -- Static methods --

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
    else return "NULL POINTER";
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

    public TabPanel(Element el) {
      ome = thisOmeNode;
      isRendered = false;
      this.el = el;
      oNode = null;
      name = getTreePathName(el);
      titlePanel = null;
      tabPanelList.add(this);
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

/** Helper class to handle the "GOTO" buttons that take you to a particular
*   Element ID's representation in the program.
*/
  public class TableButton extends JButton {
    public JTable table;
    public int whichRow;

    public TableButton( JTable jt, int i) {
      super("Goto");
      table = jt;
      whichRow = i;
      Integer aInt = new Integer(i);
      setActionCommand("goto");
      setPreferredSize(new Dimension(70, 15));
    }
  }

/** Helper class to handle the various TablePanels that will be created to
*   display the attributes of Elements that have no nested Elements
*/
  public class TablePanel extends JPanel
    implements TableModelListener, ActionListener
  {
    public OMEXMLNode oNode;
    public TabPanel tPanel;
    public NotePanel noteP;
    public String id;
    public String name;
    public JTable newTable, refTable;
    public Element el;
    public boolean isTopLevel;
    private JButton noteButton;

    public TablePanel(Element e, TabPanel tp, OMEXMLNode on) {
      isTopLevel = false;
      if(tp.oNode == on) isTopLevel = true;
      el = e;
      oNode = on;
      tPanel = tp;
      id = null;
      newTable = null;
      refTable = null;
      JComboBox comboBox = null;
      name = getTreePathName(e,on);
      String thisName = getTreePathName(e, on);
      panelList.add(this);

      Vector fullList = DOMUtil.getChildElements("OMEAttribute",e);
      Vector attrList = new Vector();
      Vector refList = new Vector();
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

      JPanel lowPanel = new JPanel();
      
      FormLayout layout = new FormLayout(
        "pref, 10dlu, pref:grow, pref",
        "pref,2dlu,pref,pref,pref,3dlu,pref,3dlu");
      PanelBuilder builder = new PanelBuilder(layout);
      CellConstraints cc = new CellConstraints();

      if (attrList.size() != 0) {
        myTableModel = new DefaultTableModel(TREE_COLUMNS, 0) {
          public boolean isCellEditable(int row, int col) {
            if(col < 1) return false;
            else return true;
          }
        };

        myTableModel.addTableModelListener(this);

        setLayout(new GridLayout(0,1));
        JLabel tableName = new JLabel(thisName);

        newTable = new ClickableTable(myTableModel, this);
        newTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        newTable.getColumnModel().getColumn(0).setPreferredWidth(125);
        newTable.getColumnModel().getColumn(1).setPreferredWidth(500);
        newTable.getColumnModel().getColumn(0).setMinWidth(125);
        JTableHeader tHead = newTable.getTableHeader();
        tHead.setResizingAllowed(false);
        tHead.setReorderingAllowed(false);
        noteButton = new JButton("Notes");
        noteButton.setPreferredSize(new Dimension(85,15));
        noteButton.addActionListener(this);
        noteButton.setActionCommand("getNotes");
        
				builder.add(tableName, cc.xy(1,1));
				builder.add(noteButton, cc.xy(3,1, "left,center"));
				builder.add(tHead, cc.xyw(1,3,4));
				builder.add(newTable, cc.xyw(1,4,4));

        // update OME-XML attributes table
        myTableModel.setRowCount(attrList.size());
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
            else if (thisEle.hasAttribute("XMLName")) {
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
        myTableModel = new DefaultTableModel(TREE_COLUMNS, 0) {
          public boolean isCellEditable(int row, int col) {
            if(col < 1) return false;
            else return true;
          }
        };

        myTableModel.addTableModelListener(this);

        refTable = new ClickableTable(myTableModel, this);
        refTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        refTable.getColumnModel().getColumn(0).setPreferredWidth(125);
        refTable.getColumnModel().getColumn(1).setPreferredWidth(430);
        refTable.getColumnModel().getColumn(0).setMinWidth(125);
        TableColumn refColumn = refTable.getColumnModel().getColumn(1);

        comboBox = new JComboBox();
        refColumn.setCellEditor(new DefaultCellEditor(comboBox));

        JPanel buttonPanel;
        String rowString = "";
        for (int i=0; i<refList.size(); i++) {
          rowString = rowString + "pref:grow, ";
        }
        rowString = rowString.substring(0,rowString.length()-2);
        
        FormLayout layout2 = new FormLayout(
          "pref",
          rowString);
        PanelBuilder builder2 = new PanelBuilder(layout2);
        CellConstraints cc2 = new CellConstraints();

        myTableModel.setRowCount(refList.size());
        for (int i=0; i<refList.size(); i++) {
          Element thisEle = null;
          if (refList.get(i) instanceof Element) {
            thisEle = (Element) refList.get(i);
          }
          if (thisEle != null) {
            if (thisEle.hasAttribute("Name")) {
              myTableModel.setValueAt(thisEle.getAttribute("Name"), i, 0);
            }
            else if (thisEle.hasAttribute("XMLName")) {
              myTableModel.setValueAt(thisEle.getAttribute("XMLName"), i, 0);
            }
          }
          TableButton tb = new TableButton(refTable,i);
          tb.addActionListener(this);
          builder2.add(tb, cc2.xy(1,i+1, "center,fill"));
        }
        
        buttonPanel = builder2.getPanel();

        if(attrList.size() == 0) {
          JLabel tableName = new JLabel(thisName);
          JTableHeader tHead = refTable.getTableHeader();
          tHead.setResizingAllowed(false);
          tHead.setReorderingAllowed(false);
          noteButton = new JButton("Notes");
          noteButton.setPreferredSize(new Dimension(85,15));
          noteButton.addActionListener(this);
          noteButton.setActionCommand("getNotes");
        
			  	builder.add(tableName, cc.xy(1,1));
			  	builder.add(noteButton, cc.xy(3,1, "left,center"));
				  builder.add(tHead, cc.xyw(1,3,3));
          builder.add(refTable, cc.xyw(1,4,3));
          builder.add(buttonPanel, cc.xy(4,4));
        }
        else {
					builder.add(refTable, cc.xyw(1,5,3));
          builder.add(buttonPanel, cc.xy(4,5, "center,fill"));
        }      
      }
      
      noteP = new NotePanel(this);
      builder.add(noteP, cc.xyw(1,7,4, "fill,center"));
      setNumNotes(noteP.getNumNotes());
      
      add(builder.getPanel());
    }

    public void setEditor() {
      if(refTable != null) {
        TableModel model = refTable.getModel();
        TableColumn refColumn = refTable.getColumnModel().getColumn(1);
        for(int i = 0;i < refTable.getRowCount();i++) {
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
        }

        refColumn.setCellEditor(new VariableComboEditor(panelsWithID,
          addItems, oNode, internalDefs));
      }
    }
    
    public void setNumNotes(int n) {
      noteButton.setText("Notes (" + n + ")");
    }

    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof TableButton) {
        TableButton tb = (TableButton) e.getSource();
        JTable jt = tb.table;

        TableModel model = jt.getModel();
        Object obj = model.getValueAt(tb.whichRow, 1);
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
      else if(e.getActionCommand().equals("getNotes")) {
        if (noteP.isVisible()) noteP.setVisible(false);
        else noteP.setVisible(true);
        noteP.revalidate();
      }
    }

    public void tableChanged(TableModelEvent e) {
      int column = e.getColumn();
      if (e.getType() == TableModelEvent.UPDATE && column == 1 &&
        ((TableModel) e.getSource()) == newTable.getModel())
      {
        int row = e.getFirstRow();
        TableModel model = (TableModel) e.getSource();
        String data = (String) model.getValueAt(row, column);
        String attr = (String) model.getValueAt(row,0);
        if ( data == null ) data = "";
        if ( oNode != null ) {
          if (attr.endsWith("CharData") ) {
            DOMUtil.setCharacterData(data, oNode.getDOMElement());
          }
          else oNode.setAttribute(attr, data);
        }
      }
    }
    
    public void callReRender() {
      reRender();
    }
  }
}

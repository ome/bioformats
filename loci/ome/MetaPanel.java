//
// MetaPanel.java
//

package loci.ome;

import ij.*;
import ij.process.ImageProcessor;

import org.w3c.dom.*;
import java.util.*;
import java.lang.reflect.*;

import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.xml.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MetaPanel is the class that handles the window used to view attributes
 * associated with an image.
 *
 * @author Philip Huettl pmhuettl at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class MetaPanel implements ActionListener, TreeSelectionListener {

  // -- Constants --

  public static final String[] FEATURE_TYPES = {
    "Bounds", "Extent", "Location", "Ratio", "Signal", "Threshold", "Timepoint"
  };

  // -- Fields --

  private JButton cancel;
  private JDialog dia;
  private JTextField text1, text2;

  private Object[] meta;
  private int imagePID;
  private JTree tree;
  private JSplitPane splitPane;
  private JPanel customPane, imagePane;
  private JTextField iName, iDate, idescription, iID;
  private JTextArea customField;
  private DefaultMutableTreeNode root;

  private static ImageProcessor imageP;
  private static boolean isXML;
  private static OMENode omeNode;
  private static DefaultMutableTreeNode defaultNode;
  private static int omeID;

  private static Hashtable table;

  // -- Constructor --

  public MetaPanel(Frame frame, int imageplusID, Object[] obmeta) {
    table = new Hashtable();
    imagePID = imageplusID;
    meta = obmeta;

    //Create the tree
    root = (DefaultMutableTreeNode) obmeta[1];

    tree = new JTree(root);
    tree.getSelectionModel().setSelectionMode
      (TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(this);
    JScrollPane treeView = new JScrollPane(tree);
    treeView.setPreferredSize(new Dimension(400, 400));

    //creates the dialog box
    dia = new JDialog(frame, "OME Image Attributes", false);
    JPanel pane = new JPanel();
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    customPane = new JPanel();
    customPane.setLayout(new GridBagLayout());
    GridBagConstraints gbl = new GridBagConstraints();
    gbl.fill = GridBagConstraints.BOTH;
    gbl.gridwidth = GridBagConstraints.REMAINDER;
    gbl.gridheight = GridBagConstraints.REMAINDER;
    gbl.weighty = 1;
    gbl.weightx = 1;
    customField = new JTextArea(2,20);
    customField.setLineWrap(true);
    customField.setWrapStyleWord(true);
    JScrollPane customScroll = new JScrollPane(customField);
    treeView.setMinimumSize(new Dimension(200,200));
    customPane.add(customScroll, gbl);
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
      treeView, customPane);
    customPane.setMinimumSize(customPane.getPreferredSize());
    pane.add(splitPane);
    JPanel paneButtons = new JPanel();
    paneButtons.setLayout(new BoxLayout(paneButtons, BoxLayout.X_AXIS));
    paneButtons.setBorder(new EmptyBorder(5,5,5,5));
    paneButtons.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    pane.add(paneButtons);
    dia.setContentPane(pane);
    cancel = new JButton("Close");
    cancel.setActionCommand("cancel");
    paneButtons.add(cancel);
    cancel.addActionListener(this);
    dia.pack();
    centerWindow(frame, dia);
  }

  /** shows and retrieves info from the DownPanel */
  public void show() { dia.setVisible(true); }

  /** implements the ActionListener actionPerformed method */
  public void actionPerformed(ActionEvent e) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
      tree.getLastSelectedPathComponent();
    dia.setVisible(false);
  }

  /** detects selection in the tree */
  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node =
      (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    customField.setText("");
    if (node == null) return;
    XMLObject nodeInfo = (XMLObject)node.getUserObject();
    int type = nodeInfo.getType();
    customField.setText(nodeInfo.getValue());
  }

  // -- OMESidePanel methods --

  /** puts the given window at the edge of the specified parent window. */
  public static void centerWindow(Window parent, Window window) {
    Point loc = parent.getLocation();
    Dimension p = parent.getSize();
    Dimension w = window.getSize();
    int x = loc.x + (p.width - w.width) / 2;
    int y = loc.y + (p.height - w.height) / 2;
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    window.setLocation(x, y);
  }


  // -- OMEMetaDataHandler methods --

  /**Method that begins the process of getting metadata from an OME_TIFF file*/
  public static DefaultMutableTreeNode exportMeta(String descr,
    int ijimageID)
  {
    isXML = true;
    IJ.showStatus("Retrieving OME-TIFF header.");
    Object[] meta = new Object[2];
    if (descr == null) {
      IJ.showStatus("Not an OME-TIFF file.");
      if (meta == null) {
        meta = new Object[2];
        meta[0] = new Integer(0);
      }
      meta[1] = defaultNode;
      defaultNode = null;
      return null;
    }
    IJ.showStatus("Parsing OME-TIFF header.");
    try {
      omeNode = new OMENode(descr);
    }
    catch (Exception e) {
      IJ.showStatus("Error parsing OME-XML metadata, possibly not present.");
      if (meta == null) {
        meta = new Object[2];
        meta[0] = new Integer(0);
      }
      meta[1] = null;
      return null;
    }
    if (meta == null) {
      meta = new Object[2];
      meta[0] = new Integer(0);
    }
    meta[1] = new DefaultMutableTreeNode(
      new XMLObject("OME-XML"));
    IJ.showStatus("Metadata is being put into tree structure.");
    addDisk(omeNode, (DefaultMutableTreeNode) meta[1], null, "OME");
    return (DefaultMutableTreeNode) meta[1];
  }

  private static void exportMeta(Feature feature, DefaultMutableTreeNode root,
    DataFactory df) {
    IJ.showStatus("Retrieving Feature attributes.");
    DefaultMutableTreeNode featureNode = new DefaultMutableTreeNode(
      new XMLObject(XMLObject.FEATUREHEADING));
    root.add(featureNode);
    featureNode.add(new DefaultMutableTreeNode(
      new XMLObject("Name", feature.getName(), XMLObject.FEATURE)));
    featureNode.add(new DefaultMutableTreeNode(
      new XMLObject("Tag", feature.getTag(), XMLObject.FEATURE)));
    //add trajectory entries
    java.util.List trajects = null;
    if (df != null) {
      Criteria trajCriteria = new Criteria();
      trajCriteria.addWantedField("feature_id");
      trajCriteria.addFilter("feature_id", (new Integer(
        feature.getID())).toString());
      //retrieve element to add with all columns loaded
      trajects = df.retrieveList("Trajectory", trajCriteria);
    }
    else trajects = ((Trajectory)feature).getTrajectoryEntryList();

    Iterator iterTra = trajects.iterator();
    while (iterTra.hasNext()) {
      addDisk((Trajectory)iterTra.next(), featureNode, df, "Trajectory");
    }
    //add attributes to this element's node
    if (df != null) {
      for (int i=0; i<FEATURE_TYPES.length; i++) {
        Criteria criteria = new Criteria();
        criteria.addWantedField("feature_id");
        criteria.addFilter("feature_id",
          new Integer(feature.getID()).toString());
        IJ.showStatus("Retrieving " + FEATURE_TYPES[i] + "s.");
        java.util.List customs = null;
        try {
          customs = df.retrieveList(FEATURE_TYPES[i], criteria);
        }
        catch (Exception e) {
          e.printStackTrace();
          IJ.showStatus("Error while retrieving " + FEATURE_TYPES[i] + "s.");
        }
        if (customs != null) {
          Iterator itCustoms = customs.iterator();
          while (itCustoms.hasNext()) {
            addDisk(itCustoms.next(), featureNode, df, FEATURE_TYPES[i]);
          }
        }
      }
    }
    else addDisk(feature, featureNode, df, "Feature");
    //add child features to tree
    java.util.List features = feature.getChildren();
    Iterator iter = features.iterator();
    while (iter.hasNext()) {
      exportMeta((Feature)iter.next(), featureNode, df);
    }
  }

  /**
   * Add an element's attributes and children to the metadata tree.
   * This method is only called if we are working with an image stored on
   * disk (requires LOCI's OME-XML package).
   */
  private static void addDisk(Object element, DefaultMutableTreeNode root,
    DataFactory df, String identifier) {

    IJ.showStatus("Retrieving " + identifier + " attributes.");
    if (element == null) return;

    OMEXMLNode xml = null;
    if (!element.toString().startsWith("[")) {
      xml = (OMEXMLNode) element;
    }

    String[] tempAttrs = null;
    String[] tempValues = null;

    try {
      tempAttrs = xml.getAttributeNames();
      tempValues = xml.getAttributeValues();
    }
    catch (NullPointerException n) { }

    // NodeList is a list of child DOM elements
    NodeList tempChilds = null;
    Object[] childs = null;
    String[] names = null;
    try {
      tempChilds = xml.getDOMElement().getChildNodes();
      childs = new Object[tempChilds.getLength()];
    }
    catch (NullPointerException n) { }

    try {
      for (int i=0; i<tempChilds.getLength(); i++) {
        String tmp = tempChilds.item(i).toString();
        String id = tmp.substring(1, tmp.indexOf(" "));
        String tmpId = tmp;
        if (tmp.indexOf(">") >= 0) tmpId = tmp.substring(1, tmp.indexOf(">"));
        if (tmpId.length() < id.length()) id = tmpId;
        if (!id.endsWith("Node")) {
          id = id + "Node";
        }
        if (!id.startsWith("Attribute") &&
            !id.startsWith("CustomAttributes") &&
            !id.startsWith("Dataset") && !id.startsWith("Feature") &&
            !id.startsWith("Image") && !id.equals("OMENode") &&
            !id.startsWith("Project"))
        {
          if (id.indexOf(":") != -1) {
            id = id.substring(0, id.indexOf(":"));
          }
          id = "org.openmicroscopy.xml.st." + id + "Node";
        }
        else {
          if (id.indexOf(":") != -1) {
            id = id.substring(0, id.indexOf(":")) + "Node";
          }
          id = "org.openmicroscopy.xml." + id;
        }

        // construct a new node using this class
        Class toConstruct = Class.forName(id.trim());
        Constructor construct =
          toConstruct.getDeclaredConstructor(new Class[] {Element.class});
        childs[i] =
          construct.newInstance(new Object[] {(Element) tempChilds.item(i)});
      }

      names = new String[childs.length];
      for (int i=0; i<childs.length; i++) {
        String tmp = childs[i].toString();
        names[i] = tmp.substring(tmp.lastIndexOf(".")+1, tmp.lastIndexOf("@"));
      }
    }
    catch (Throwable t) { }

    if (df != null && tempAttrs != null) {
      Criteria criteria = makeAttributeFields(tempAttrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(
        ((ImageGroup) element).getID())).toString());
      element = df.retrieve(identifier, criteria);
    }

    DefaultMutableTreeNode node = new DefaultMutableTreeNode(new XMLObject(
      identifier, XMLObject.ELEMENT));
    root.add(node);

    if (tempAttrs != null && tempValues != null) {
      for (int i=0; i<tempValues.length; i++) {
        node.add(new DefaultMutableTreeNode(new XMLObject(tempAttrs[i],
          tempValues[i], XMLObject.ATTRIBUTE)));
      }
    }

    if (tempChilds != null) {
      for (int i=0; i<childs.length; i++) {
        String newName = (names == null) ? null : names[i];
        addDisk(childs[i], node, df, newName);
      }
    }
  }

  // -- OMEDownPanel methods --

  /** produces an error notification popup with the inputted text */
  public static void error(Frame frame, String s, String x){
    JOptionPane.showMessageDialog(frame,s,x,JOptionPane.ERROR_MESSAGE);
  }

  // -- OMERetrieve methods --

  /** populates the criteria for a certain attribute */
  public static Criteria makeAttributeFields(String[] attr) {
    Criteria c = new Criteria();
    for (int i=0; i<attr.length; i++) {
      c.addWantedField(attr[i]);
    }
    return c;
  }
}

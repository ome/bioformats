import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import org.openmicroscopy.ds.dto.*;
import loci.ome.xml.*;
/**
 * OMEMetaPanel is the class that handles
 * the window used to view attributes
 * associated with an image.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMEMetaPanel implements ActionListener, TreeSelectionListener{
  //Fields
  public static final String[] IMAGE_TYPES={"Dimensions", "DisplayROI",
    "ImageAnnotation",  "ImageExperiment", "ImageGroup","ImageInstrument",
    "ImagePlate", "ImageTestSignature", "ImagingEnvironment",  
    "PixelChannelComponent", "PlaneCentroid", "PlaneGeometricMean", 
    "PlaneGeometricSigma", "PlaneMaximum", "PlaneMean",
    "PlaneMinimum", "PlaneSigma", "PlaneSum_i", "PlaneSum_i2", "PlaneSum_log_i",
    "PlaneSum_Xi", "PlaneSum_Yi", "PlaneSum_Zi", "StackCentroid", "StackGeometricMean",
    "StackGeometricSigma", "StackMaximum", "StackMean", "StackMinimum", "StackSigma",
    "StageLabel",  "Thumbnail", "Classification"};
  public static final String[][] IMAGE_ATTRS={
    //Dimensions
    {"PixelSizeC", "PixelSizeT", "PixelSizeX", "PixelSizeY", "PixelSizeZ"},
    //DisplayROI
    {"DisplayOptions", "T0", "T1", "X0", "X1", "Y0", "Y1", "Z0", "Z1"},
    //ImageAnnotation
    {"Content", "Experimenter", "TheC", "TheT", "TheZ","Timestamp", "Valid"},
    //ImageExperiment
    {"Experiment"},
    //ImageGroup 
    {"Group"},
    //ImageInstrument
    {"Instrument", "Objective"},
    //ImagePlate
    {"Plate", "Sample", "Well"},
    //ImageTestSignature
    {"Value"},
    //ImagingEnvironment
    {"AirPressure", "CO2Percent", "Humidity", "Temperature"},
    //PixelChannelComponent
    {"ColorDomain", "Index", "LogicalChannel", "Pixels"},
    //PlaneCentroid
    {"TheC", "TheT", "TheZ", "X", "Y"},
    //PlaneGeometricMean
    {"GeometricMean", "TheC", "TheT", "TheZ"},
    //PlaneGeometricSigma
    {"GeometricSigma", "TheC", "TheT", "TheZ"},
    //PlaneMaximum
    {"Maximum", "TheC", "TheT", "TheZ"},
    //PlaneMean
    {"Mean", "TheC", "TheT", "TheZ"},
    //PlaneMinimum
    {"Minimum", "TheC", "TheT", "TheZ"},
    //PlaneSigma
    {"Sigma", "TheC", "TheT", "TheZ"},
    //PlaneSum_i
    {"Sum_i", "TheC", "TheT", "TheZ"},
    //PlaneSum_i2
    {"Sum_i2", "TheC", "TheT", "TheZ"},
    //PlaneSum_log_i
    {"Sum_log_i", "TheC", "TheT", "TheZ"},
    //PlaneSum_Xi
    {"Sum_Xi", "TheC", "TheT", "TheZ"},
    //PlaneSum_Yi
    {"Sum_Yi", "TheC", "TheT", "TheZ"},
    //PlaneSum_Zi
    {"Sum_Zi", "TheC", "TheT", "TheZ"},
    //StackCentroid
    {"TheC", "TheT", "X", "Y", "Z"},
    //StackGeometricMean
    {"GeometricMean","TheC", "TheT"},
    //StackGeometricSigma
    {"GeometricSigma","TheC", "TheT"},
    //StackMaximum
    {"Maximum","TheC", "TheT"},
    //StackMean
    {"Mean","TheC", "TheT"},
    //StackMinimum
    {"Minimum","TheC", "TheT"},
    //StackSigma
    {"Sigma","TheC", "TheT"},
    //StageLabel
    {"Name","X", "Y", "Z"},
    //Thumbnail
    {"MimeType","Path", "Repository"},
    //Classification
    {"Category", "Confidence", "Valid"}
  };
  public static final String[] FEATURE_TYPES={"Bounds",
     "Extent", "Location", "Ratio","Signal","Threshold", "Timepoint"
  };
  public static final String[][] FEATURE_ATTRS={
    //Bounds
    {"Height", "Width", "X", "Y"},
    //Extent
    {"FormFacter", "MaxX", "MaxY", "MaxZ", "MinX", "MinY", "MinZ", "Perimeter", "SigmaX",
      "SigmaY", "SigmaZ", "SurfaceArea", "Volume"},
    //Location
    {"TheX", "TheY", "TheZ"},
    //Ratio
    {"Ratio"},
    //Signal
    {"Background", "CentroidX", "CentroidY", "CentroidZ", "GeometricMean",
      "GeometricSigma", "Integral", "Mean", "Sigma", "TheC"},
    //Threshold
    {"Threshold"},
    //Timepoint
    {"TheT"}
  };

  private JButton cancel, save, addElement;
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
    
  //Constructor, sets up the dialog box
  public OMEMetaPanel(Frame frame, int imageplusID, Object[] obmeta){
    imagePID=imageplusID;
    meta=obmeta;
    //Create the tree
    root= (DefaultMutableTreeNode)obmeta[1];
    tree=new JTree(root);
    tree.getSelectionModel().setSelectionMode
            (TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(this);
    JScrollPane treeView = new JScrollPane(tree);
    treeView.setPreferredSize(new Dimension(200, 200));
    
    
    //creates the dialog box
    dia=new JDialog(frame, "OME Image Attributes", true);
    JPanel pane=new JPanel();
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    customPane=new JPanel();
    save=new JButton("Save");
    customField=new JTextArea(2,10);
    customField.setLineWrap(true);
    customField.setWrapStyleWord(true);
    JScrollPane customScroll=new JScrollPane(customField);
    customPane.add(customScroll);
//    customPane.add(save);
    splitPane=new JSplitPane(JSplitPane.VERTICAL_SPLIT, treeView, customPane);
    pane.add(splitPane);
    JPanel paneButtons=new JPanel();
    paneButtons.setLayout(new BoxLayout(paneButtons, BoxLayout.X_AXIS));
    paneButtons.setBorder(new EmptyBorder(5,5,5,5));
    paneButtons.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    pane.add(paneButtons);
    dia.setContentPane(pane);
    cancel=new JButton("Close");
    cancel.setActionCommand("cancel");
    
    addElement=new JButton("Add Element");
    addElement.setActionCommand("addelement");
//    paneButtons.add(addElement);
    paneButtons.add(cancel);
    cancel.addActionListener(this);
    save.setActionCommand("save");
    save.addActionListener(this);
    save.setEnabled(false);
    addElement.addActionListener(this);
    addElement.setEnabled(false);
    
    dia.pack();
    centerWindow(frame, dia);
  }//end of public constructor
  
  //Methods
  /**shows and retrieves info from the DownPanel*/
  public void show(){
    dia.show();
    //checks and puts results into an array
  }//end of search method
  
  /** Centers the given window within the specified parent window. */
  private void centerWindow(Window parent, Window window) {
    Point loc = parent.getLocation();
    Dimension p = parent.getSize();
    Dimension w = window.getSize();
    int x = loc.x + (p.width - w.width) / 2;
    int y = loc.y + (p.height - w.height) / 2;
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    window.setLocation(x, y);
  }//end of centerWindow method
  
  //not possible yet
  /**gets input from user about what type of element to add*/
/*  private void addNewElement(){
    JDialog di=new JDialog(dia, "Element Detail", true);
    JPanel main=new JPanel(), buttons=new JPanel(), elements=new JPanel();
    main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
    buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
    GridLayout grid=new GridLayout(3,2);
    elements.setLayout(grid);
    JLabel ele=new JLabel("Element Type:"), attr=new JLabel("Attribute Name:"),
      val=new JLabel("Value:");
//    JComboBox
    elements.add(ele);
    elements.add
    di.setContentPane(main);
    JButton ok=new JButton("OK"), no= new JButton("Cancel");
    ok.setActionCommand("OK");
    no.setActionCommand("no");
    ok.setActionListener(this);
    no.setActionListener(this);
    buttons.add(ok);
    buttons.add(no);
  }//end of addNewElement method
  
  /**implements the ActionListener actionPerformed method*/
  public void actionPerformed(ActionEvent e){
    DefaultMutableTreeNode node=
        (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
    if("save".equals(e.getActionCommand())){
      ((XMLObject)node.getUserObject()).setValue(customField.getText());
    }else if("addelement".equals(e.getActionCommand())){
        if("Add Element".equals(addElement.getText())){
          String s= JOptionPane.showInputDialog(dia, "Please enter the new Element name.");
          node.add(new DefaultMutableTreeNode(new XMLObject(s, XMLObject.ELEMENT)));
        }else if("Add Attribute".equals(addElement.getText())){
          node.add(new DefaultMutableTreeNode(new XMLObject(
            JOptionPane.showInputDialog(dia, "Please enter the new Attribute name."),
            JOptionPane.showInputDialog(dia, "Please enter the Attribute's value."), 
            XMLObject.ATTRIBUTE)));
        }else if("Add Feature".equals(addElement.getText())){
          DefaultMutableTreeNode feature=new DefaultMutableTreeNode(new XMLObject(
            XMLObject.FEATUREHEADING));
          node.add(feature);
          feature.add(new DefaultMutableTreeNode(new XMLObject("Name",
            JOptionPane.showInputDialog(dia, "Please enter the Feature's name."),
            XMLObject.FEATURE)));
          feature.add(new DefaultMutableTreeNode(new XMLObject("Tag",
            JOptionPane.showInputDialog(dia, "Please enter the Feature's tag."),
            XMLObject.FEATURE)));
        }
    }else dia.hide();
  }//end of actionPerformed method
  
  /**detects selection in the tree*/
  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       tree.getLastSelectedPathComponent();
    if (node == null) {
      customField.setText("");
      save.setEnabled(false);
      addElement.setEnabled(false);
      return;
    }
    XMLObject nodeInfo = (XMLObject)node.getUserObject();
    int type=nodeInfo.getType();
    customField.setText("");
    save.setEnabled(false);
    addElement.setEnabled(false);
    if (type==XMLObject.ATTRIBUTE||type==XMLObject.FEATURE) {
      customField.setText(nodeInfo.getValue());
      //save.setEnabled(true);
    }else if (type==XMLObject.READONLY||type==XMLObject.IMAGE||type==XMLObject.PIXELS){
      customField.setText(nodeInfo.getValue());
    }else if (type==XMLObject.ELEMENT){
      addElement.setText("Add Attribute");
      addElement.setEnabled(true);
    }else if (type==XMLObject.CUSTOMHEADING){
      addElement.setText("Add Element");
      addElement.setEnabled(true);
    }else if (type==XMLObject.IMAGEHEADING || type==XMLObject.FEATUREHEADING){
      addElement.setText("Add Feature");
      addElement.setEnabled(true);
    }
  }//end of valueChanged method

}//end of OMEMetaPanel class
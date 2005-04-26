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
  public static final String[] IMAGE_TYPES={"Dimensions", "DisplayOptions",
    "ImageAnnotation",  "ImageExperiment", "ImageGroup","ImageInstrument",
    "ImagePlate", "ImageTestSignature", "ImagingEnvironment",  
    "PlaneCentroid", "PlaneGeometricMean", 
    "PlaneGeometricSigma", "PlaneMaximum", "PlaneMean",
    "PlaneMinimum", "PlaneSigma", "PlaneSum_i", "PlaneSum_i2", "PlaneSum_log_i",
    "PlaneSum_Xi", "PlaneSum_Yi", "PlaneSum_Zi", "StackCentroid", "StackGeometricMean",
    "StackGeometricSigma", "StackMaximum", "StackMean", "StackMinimum", "StackSigma",
    "StageLabel",  "Thumbnail", "Classification"};
  public static final String[] FEATURE_TYPES={"Bounds",
     "Extent", "Location", "Ratio","Signal","Threshold", "Timepoint"
  };
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
    customPane.setLayout(new GridBagLayout());
    GridBagConstraints gbl=new GridBagConstraints();
    gbl.fill=GridBagConstraints.BOTH;
    gbl.gridwidth=GridBagConstraints.REMAINDER;
    gbl.gridheight=GridBagConstraints.REMAINDER;
    gbl.weighty=1;
    gbl.weightx=1;
    customField=new JTextArea(2,20);
    customField.setLineWrap(true);
    customField.setWrapStyleWord(true);
    JScrollPane customScroll=new JScrollPane(customField);
    treeView.setMinimumSize(new Dimension(50,50));
    customPane.add(customScroll, gbl);
    splitPane=new JSplitPane(JSplitPane.VERTICAL_SPLIT, treeView, customPane);
    customPane.setMinimumSize(customPane.getPreferredSize());
    pane.add(splitPane);
    JPanel paneButtons=new JPanel();
    paneButtons.setLayout(new BoxLayout(paneButtons, BoxLayout.X_AXIS));
    paneButtons.setBorder(new EmptyBorder(5,5,5,5));
    paneButtons.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    pane.add(paneButtons);
    dia.setContentPane(pane);
    cancel=new JButton("Close");
    cancel.setActionCommand("cancel");
    paneButtons.add(cancel);
    cancel.addActionListener(this);
    dia.pack();
    centerWindow(frame, dia);
  }//end of public constructor
  
  //Methods
  /**shows and retrieves info from the DownPanel*/
  public void show(){
    dia.show();
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
  
  /**implements the ActionListener actionPerformed method*/
  public void actionPerformed(ActionEvent e){
    DefaultMutableTreeNode node=
        (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
    if("save".equals(e.getActionCommand())){
      ((XMLObject)node.getUserObject()).setValue(customField.getText());
    }else dia.hide();
  }//end of actionPerformed method
  
  /**detects selection in the tree*/
  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       tree.getLastSelectedPathComponent();
    if (node == null) {
      customField.setText("");
      return;
    }
    XMLObject nodeInfo = (XMLObject)node.getUserObject();
    int type=nodeInfo.getType();
    customField.setText("");
    if (type==XMLObject.ATTRIBUTE||type==XMLObject.FEATURE) {
      customField.setText(nodeInfo.getValue());
    }else if (type==XMLObject.READONLY||type==XMLObject.IMAGE||type==XMLObject.PIXELS){
      customField.setText(nodeInfo.getValue());
    }
  }//end of valueChanged method

}//end of OMEMetaPanel class
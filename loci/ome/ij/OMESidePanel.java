import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import ij.WindowManager;
import ij.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * OMESidePanel is the class that handles
 * the window alongside ImageJ.
 *
 * @author Philip Huettl pmhuettl@wisc.edu
 * @author Melissa Linkert, linkert at cs.wisc.edu
 */

public class OMESidePanel implements ActionListener {
  
  // -- Fields --
	
  private static JButton close, upload, download, edit, open;
  public static JDialog dia;
  public static boolean cancelPlugin;
  private static JList list;
  private static Frame parentWindow;
  private static ImagePlus[] imp;
  private static Hashtable table;

  private static String serverName;
  private static String username;
 
  private static int[] imageIds;
  
  //Constructor, sets up the dialog box
  public OMESidePanel(Frame frame) {
    table = new Hashtable();
    // parent is ImageJ
    parentWindow = frame;
    cancelPlugin = false;
    
    //creates the dialog box for searching for images
    dia = new JDialog(frame, "OME Plugin", false);
    JPanel pane = new JPanel();
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    pane.setBorder(new EmptyBorder(5,5,5,5));
    
    //panels
    JPanel paneR = new JPanel();
    JPanel paneInfo = new JPanel();
    JPanel paneButtons = new JPanel();
    JPanel paneUp = new JPanel();
    JPanel paneTwo = new JPanel();
    paneTwo.setLayout(new BoxLayout(paneTwo, BoxLayout.X_AXIS));
    paneR.setLayout(new BoxLayout(paneR, BoxLayout.Y_AXIS));
    paneR.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    paneInfo.setLayout(new BoxLayout(paneInfo, BoxLayout.X_AXIS));
    paneInfo.setBorder(new CompoundBorder(new EtchedBorder(
      EtchedBorder.RAISED),new EmptyBorder(5,5,5,5)));
    paneButtons.setLayout(new BoxLayout(paneButtons, BoxLayout.X_AXIS));
    paneButtons.setBorder(new EmptyBorder(5,5,5,5));
    paneUp.setBorder(new EmptyBorder(2,2,2,2));
    pane.add(paneUp);
    pane.add(paneInfo);
    pane.add(paneButtons);
    paneInfo.add(paneR);
    dia.setContentPane(pane);
    
    //borders
    EmptyBorder bordCombo = new EmptyBorder(1,0,4,0);
    EmptyBorder bordText = new EmptyBorder(3,0,2,0);

    open = new JButton("Open");
    close = new JButton("Close");
    upload = new JButton("Download");
    download = new JButton("Upload");
    edit = new JButton("Edit");
    upload.setMinimumSize(download.getPreferredSize());
    open.setActionCommand("open");
    close.setActionCommand("close");
    upload.setActionCommand("upload");
    download.setActionCommand("download");
    edit.setActionCommand("edit");
    paneUp.add(upload);
    paneUp.setMaximumSize(paneUp.getPreferredSize());
    paneButtons.add(open);
    paneButtons.add(close);
    open.addActionListener(this);
    close.addActionListener(this);
    upload.addActionListener(this);
    download.addActionListener(this);
    edit.addActionListener(this);
    
    //List
    list = new JList();
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane sp = new JScrollPane(list);
    sp.setMinimumSize(new Dimension(500, 500)); 
    sp.setPreferredSize(sp.getMinimumSize());
    paneR.add(sp);
    paneR.add(paneTwo);
    paneTwo.add(download);
    paneTwo.add(edit);
    dia.pack();
    centerWindow(frame, dia);
  }
 
  /** shows and retrieves info from the SidePanel */
  public static void showIt() {
    Vector imgs = WiscScan.getIDs();
    int[] openpics;
    if(imgs != null) {
      openpics = new int[imgs.size()];
      for(int i=0; i<imgs.size(); i++) {
        openpics[i] = ((Integer) imgs.get(i)).intValue();
      }
    }
    else {
      openpics = new int[0];
    }  
  
    imageIds = openpics;
    Vector names = WiscScan.getNames();
      
    imp = new ImagePlus[openpics.length];
    String[] titles = new String[openpics.length];
    Vector descr = WiscScan.getDescription();
    for (int i=0; i<openpics.length; i++) {
      imp[i] = WindowManager.getImage(openpics[i]);
      titles[i] = (String) names.get(i); 
      int ijimage = openpics[i]; 

      if (!table.containsKey(new Integer(ijimage))) {
        boolean xalan = false;
        IJ.showStatus("Attempting to find xml class...");
        try {
         Class c = Class.forName("javax.xml.transform.TransformerFactory");
         if (c != null) xalan = true;
       }
       catch (NoClassDefFoundError exc) { }
       catch (ClassNotFoundException ex) { }
       if (xalan) {
          try {
	    OMEMetaDataHandler.exportMeta((String) descr.get(i), ijimage); 
          }
          catch (Exception exc) { 
            exc.printStackTrace();
            IJ.showStatus("Error reading xml code.");
          }
        }
	else{
          IJ.showStatus("Java 1.4 required to retrieve OME metadata.");
        }
      }
    }
    list.setListData(titles);
    dia.show();
    if (cancelPlugin) return;
  }
   
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
  
  /** implements the ActionListener actionPerformed method */
  public void actionPerformed(ActionEvent e) {
    if ("upload".equals(e.getActionCommand())) {
      OMETools omed = new OMETools();
      omed.run(this);
      showIt();
    }
    else if ("download".equals(e.getActionCommand())) {
      if (list.getSelectedIndex() != -1) {
        OMETools omeu = new OMETools();
        int x = list.getSelectedIndex();
        if (table.containsKey(new Integer(imp[x].getID()))) {
          omeu.run(imp[x], getImageMeta(imp[x].getID()));
        }
	else omeu.run(imp[x], null);
      }
      else {
        JOptionPane.showMessageDialog(parentWindow,
          "Please select an image to export to OME.",
          "OME Plugin",JOptionPane.INFORMATION_MESSAGE);
        showIt();
      }
    }
    else if ("edit".equals(e.getActionCommand())) {
      int z = list.getSelectedIndex();
      if (z != -1) {
        int y = imageIds[z]; 
        OMEMetaPanel meta = new OMEMetaPanel(parentWindow, y, 
	  (Object[]) getImageMeta(y));
        meta.show();
      }
      else {
        JOptionPane.showMessageDialog(parentWindow,
          "Please select an image to edit.",
          "OME Plugin",JOptionPane.INFORMATION_MESSAGE);
        showIt();
      }
    }
    else if("open".equals(e.getActionCommand())) {
      IJ.runPlugIn("WiscScan", "");
    }	    
    else {
      boolean bol = yesNo(parentWindow, "Are you sure you want to exit?");
      if (bol) {
        cancelPlugin = true;
        dia.dispose();
      }
      else showIt();
    }
  }
  
  /** pops up Yes no dialog window */
  public static boolean yesNo(Frame owner, String question) {
    int n = JOptionPane.showConfirmDialog(owner, question, "OME Plugin",
      JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    return (n == JOptionPane.YES_OPTION);
  }
  
  /** adds the information to the table to upload the image back to OME */
  public static void hashInImage(int ijid, Object[] ob) {
    table.put(new Integer(ijid), ob);
  }
  
  /** gets the OME image ID from the corresponding imagePlus ID from imageJ */
  private int getOmeID(int ijID) {
    Object[] ob = (Object[]) table.get(new Integer(ijID));
    if (ob[0] != null) {
      return ((Integer)ob[0]).intValue();
    }
    return 0; 
  }
  
  /** returns the metadata array for an imagePlus ID */
  public static Object[] getImageMeta(int ijID) {
    return (Object[]) table.get(new Integer(ijID));
  }

  // -- Utility methods --
  
  public static String getServer() {
    return serverName;
  }

  public static String getUser() {
    return username;
  }

  public static void setServer(String newServer) {
    serverName = newServer;
  }

  public static void setUser(String newUser) {
    username = newUser;
  }  
  
}

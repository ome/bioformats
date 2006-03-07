package loci.ome.ij;

import java.awt.*;
import java.awt.event.*;
import ij.*;
import ij.gui.ImageWindow;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import loci.ome.MetaPanel;

/**
 * OMESidePanel is the class that handles the window alongside ImageJ.
 *
 * @author Philip Huettl pmhuettl at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
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
  private static int numOpenWindows = 0;
  private static int point = 0;

  // temporary constructor
  public OMESidePanel() {
  }

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

    open = new JButton("4D Open");
    close = new JButton("Close");
    upload = new JButton("Download");
    download = new JButton("Upload");
    edit = new JButton("View Metadata");
    upload.setMinimumSize(download.getPreferredSize());
    open.setActionCommand("open");
    close.setActionCommand("close");
    upload.setActionCommand("upload");
    download.setActionCommand("download");
    edit.setActionCommand("edit");
    paneUp.add(open);
    paneUp.add(upload);
    paneUp.setMaximumSize(paneUp.getPreferredSize());
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

  private static boolean inVector(Object obj, Vector v) {
    if (v.size() == 0) return false;
    for (int i=0; i<v.size(); i++) {
      if (v.contains(obj)) return true;
    }
    return false;
  }

  private static boolean hasPartner(int[] toSearch, int query, int index) {
    if ((toSearch == null) || (toSearch.length == 0)) return false;
    for (int i=index; i<toSearch.length; i++) {
      if (toSearch[i] == query) return true;
    }
    return false;
  }

  /** shows and retrieves info from the SidePanel */
  public static void showIt() {
    // be warned that this method is rather convoluted

    OMEMetaDataHandler.index = 0;
    int[] openpics = WindowManager.getIDList();
    if (openpics == null) openpics = new int[0];

    Vector names = new Vector();
    Vector descr = new Vector();

    // names and descriptions from WiscScan
    Vector wiscScanNames = WiscScan.getNames();
    Vector wiscScanDescr = WiscScan.getDescription();

    Vector idCheck = new Vector();
    int pt = 0;
    int numPics = 0;

    // First determine the name and metadata string for each open image.
    // Note that the *true* number of open images will most likely be less
    // than openpics.length, since ImageJ counts each instance of the WiscScan
    // viewer as two images (this isn't a bug in ImageJ, but rather a "feature"
    // of the WiscScan viewer).

    ImageWindow win;
    ImagePlus ip;
    for (int i=0; i<openpics.length; i++) {
      ip = WindowManager.getImage(openpics[i]);
      if (ip != null) win = ip.getWindow();
      else {
        i++;
        ip = WindowManager.getImage(openpics[i]);
        win = ip.getWindow();
      }

      String className = win == null ? null : win.getClass().getName();
      if (win == null || className.equals("ij.gui.StackWindow")) {
        // 3D image -- was opened with the default ImageJ viewer
        numPics++;
        try {
          names.add(ip.getTitle());
          descr.add(ip.getOriginalFileInfo().description);
        }
        catch (NullPointerException e) { }
      }
      else if (className.equals("loci.ome.ij.WiscScan$CustomWindow")) {
        // 4D OME image -- was opened through the OME plugin
        try {
          numPics++;
          if (wiscScanNames != null) {
            names.add(wiscScanNames.get(pt));
            descr.add(wiscScanDescr.get(pt));
          }
          else {
            // database image
                  names.add(ip.getTitle());
            descr.add(null);
                }
          pt++;
        }
        catch (ArrayIndexOutOfBoundsException e) { }
      }
      else if (className.equals("loci.ome.ij.WiscScan$CustomWindow")) {
        // 4D WiscScan image -- was opened using the WiscScan 4D plugin

        // in this case, we can only add an image to the list if its ID
        // isn't already in the list, and if there is another entry in
        // openpics with the same ID; see above note on how ImageJ handles
        // the WiscScan viewer
        if (!inVector(new Integer(openpics[i]), idCheck) &&
          hasPartner(openpics, openpics[i], i+1))
        {
          numPics++;
          names.add(ip.getTitle());
          descr.add("");
          idCheck.add(new Integer(openpics[i]));
        }
      }
    }

    imageIds = openpics;
    imp = new ImagePlus[openpics.length];
    String[] titles = new String[openpics.length];

    // now that we have a (hopefully) accurate list of images, we can add them
    // to the panel
    for (int i=0; i<numPics; i++) {
      try {
        imp[i] = WindowManager.getImage(openpics[i]);
        titles[i] = (String) names.get(i);
        int ijimage = openpics[i];

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
          catch (ArrayIndexOutOfBoundsException x) { }
          catch (Exception exc) {
            exc.printStackTrace();
            IJ.showStatus("Error reading xml code.");
          }
        }
        else {
          IJ.showStatus("Java 1.4 required to retrieve OME metadata.");
        }
      }
      catch (ArrayIndexOutOfBoundsException n) { }
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
    // note that "upload" => download and
    // "download" => upload

    if ("upload".equals(e.getActionCommand())) {
      OMETools omed = new OMETools();
      omed.run(this);
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
        Object[] metadata = (Object[]) getImageMeta(y);
        if (metadata == null) metadata = new Object[3];
        MetaPanel meta = new MetaPanel(parentWindow, y, metadata);
        meta.show();
      }
      else {
        JOptionPane.showMessageDialog(parentWindow,
          "Please select an image to edit.",
          "OME Plugin",JOptionPane.INFORMATION_MESSAGE);
        showIt();
      }
    }
    else if ("open".equals(e.getActionCommand())) {
      IJ.runPlugIn("WiscScan", "");
    }
    else {
      cancelPlugin = true;
      dia.dispose();
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

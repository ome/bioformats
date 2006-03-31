package loci.ome.ij;

import java.awt.*;
import java.awt.event.*;
import ij.*;
//import ij.gui.ImageWindow;
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
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class OMESidePanel implements ActionListener {

  // -- Fields --

  private static JButton close, download, upload, edit, open;
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

  /** constructor, sets up the dialog box */
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
    paneTwo.setBorder(new EmptyBorder(5,0,0,0));
    paneR.setLayout(new BoxLayout(paneR, BoxLayout.Y_AXIS));
    paneR.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    paneInfo.setLayout(new BoxLayout(paneInfo, BoxLayout.X_AXIS));
    paneInfo.setBorder(new CompoundBorder(new EtchedBorder(
      EtchedBorder.RAISED),new EmptyBorder(5,5,5,5)));
    paneButtons.setLayout(new BoxLayout(paneButtons, BoxLayout.X_AXIS));
    paneButtons.setBorder(new EmptyBorder(5,5,5,5));
    paneUp.setLayout(new BoxLayout(paneUp, BoxLayout.X_AXIS));
    paneUp.setBorder(new EmptyBorder(2,2,6,2));
    pane.add(paneUp);
    pane.add(paneInfo);
    pane.add(paneButtons);
    paneInfo.add(paneR);
    dia.setContentPane(pane);

    open = new JButton("4D Open");
    close = new JButton("Close");
    download = new JButton("Download");
    upload = new JButton("Upload");
    edit = new JButton("View Metadata");
    open.setActionCommand("open");
    close.setActionCommand("close");
    download.setActionCommand("download");
    upload.setActionCommand("upload");
    edit.setActionCommand("edit");
    paneUp.add(Box.createHorizontalGlue());
    paneUp.add(open);
    paneUp.add(Box.createHorizontalStrut(4));
    paneUp.add(download);
    paneUp.add(Box.createHorizontalGlue());
    paneUp.setMaximumSize(paneUp.getPreferredSize());
    paneButtons.add(close);
    open.addActionListener(this);
    close.addActionListener(this);
    download.addActionListener(this);
    upload.addActionListener(this);
    edit.addActionListener(this);

    //list
    list = new JList();
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setVisibleRowCount(10);
    JScrollPane sp = new JScrollPane(list);
    paneR.add(sp);
    paneR.add(paneTwo);
    paneTwo.add(upload);
    paneTwo.add(Box.createHorizontalStrut(4));
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
    OMEController.findOpenImages();
    OMEController.checkMetadata();

    int[] ids = OMEController.getCurrent();
    String[] names = OMEController.getNames();
    String[] descriptions = OMEController.getDescrs();

    imageIds = ids;

    int numPics = names.length;
    imp = new ImagePlus[numPics];

    // now that we have a (hopefully) accurate list of images, we can add them
    // to the panel
    for (int i=0; i<numPics; i++) {
      try {
        imp[i] = WindowManager.getImage(ids[i]);
        int ijimage = ids[i];

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
            Object o = MetaPanel.exportMeta(descriptions[i], ijimage);
            hashInImage(ijimage, new Object[] {null, o});
            OMEController.checkMetadata();
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
    list.setListData(names);

    dia.show();
    if (cancelPlugin) return;
  }

  /** puts the given window at the bottom edge of the parent window. */
  public static void centerWindow(Window parent, Window window) {
    Point loc = parent.getLocation();
    Dimension p = parent.getSize();
    Dimension w = window.getSize();
    int x = loc.x + (p.width - w.width) / 2;
    int y = loc.y + p.height;
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    window.setLocation(x, y);
  }

  /** pops up Yes no dialog window */
  public static boolean yesNo(Frame owner, String question) {
    int n = JOptionPane.showConfirmDialog(owner, question, "OME Plugin",
      JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    return (n == JOptionPane.YES_OPTION);
  }

  /** adds the information to the table to upload the image back to OME */
  public static void hashInImage(int ijid, Object[] ob) {
    if (!table.containsKey(new Integer(ijid))) {
      table.put(new Integer(ijid), ob);
    }
    else {
      // pick a new ID and add it

            /*
      int id = -1;
      while (table.containsKey(new Integer(id))) {
        id--;
      }
      table.put(new Integer(id), ob);
      ids.add(new Integer(id));
    }

    while(ids.contains(new Integer(-3))) {
      ids.remove(new Integer(-3));
    */
    }
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


  // -- ActionListener methods --

  /** implements the ActionListener actionPerformed method */
  public void actionPerformed(ActionEvent e) {
    if ("download".equals(e.getActionCommand())) {
      OMETools omed = new OMETools();
      omed.run(this);
    }
    else if ("upload".equals(e.getActionCommand())) {
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
        int id = imageIds[z];
        Object[] metadata = (Object[]) getImageMeta(id);
        MetaPanel meta = new MetaPanel(parentWindow, id, metadata);
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
      IJ.runPlugIn("loci.browser.LociDataBrowser", "");
    }
    else {
      cancelPlugin = true;
      dia.dispose();
    }
  }


  // -- Utility methods --

  public static Hashtable getTable() { return table; }

  public static void setTable(Hashtable t) { table = t; }

  public static String getServer() { return serverName; }

  public static String getUser() { return username; }

  public static void setServer(String newServer) { serverName = newServer; }

  public static void setUser(String newUser) { username = newUser; }

}

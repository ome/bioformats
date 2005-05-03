import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import ij.WindowManager;
import ij.*;
import java.util.Hashtable;

/**
 * OMESidePanel is the class that handles
 * the window alongside ImageJ.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMESidePanel implements ActionListener{
  //Fields
  private static JButton close, upload, download, refresh, edit;
  public static JDialog dia;
  public static boolean cancelPlugin;
  private static JList list;
  private static Frame parentWindow;
  private static ImagePlus[] imp;
  private static Hashtable table;
  
  //Constructor, sets up the dialog box
  public OMESidePanel(Frame frame){
    table=new Hashtable();
    
    parentWindow=frame;
    cancelPlugin=false;
    //creates the dialog box for searching for images
    dia=new JDialog(frame, "OME Plugin", false);
    JPanel pane=new JPanel();
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

    pane.setBorder(new EmptyBorder(5,5,5,5));
    //panels
    JPanel paneR=new JPanel(), paneInfo=new JPanel(), paneButtons=
    new JPanel(), paneUp=new JPanel(), paneTwo=new JPanel();
    paneTwo.setLayout(new BoxLayout(paneTwo, BoxLayout.X_AXIS));
    paneR.setLayout(new BoxLayout(paneR, BoxLayout.Y_AXIS));
    paneR.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    paneInfo.setLayout(new BoxLayout(paneInfo, BoxLayout.X_AXIS));
    paneInfo.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED),new EmptyBorder(5,5,5,5)));
    paneButtons.setLayout(new BoxLayout(paneButtons, BoxLayout.X_AXIS));
    paneButtons.setBorder(new EmptyBorder(5,5,5,5));
    paneInfo.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    paneButtons.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    paneUp.setBorder(new EmptyBorder(2,2,2,2));
    pane.add(paneUp);
    pane.add(paneInfo);
    pane.add(paneButtons);
    paneInfo.add(paneR);
    dia.setContentPane(pane);
    //borders
    EmptyBorder bordCombo=new EmptyBorder(1,0,4,0);
    EmptyBorder bordText=new EmptyBorder(3,0,2,0);

    close= new JButton("Close");
    refresh=new JButton("Refresh");
    refresh.setAlignmentX(JButton.RIGHT_ALIGNMENT);
    upload=new JButton("Download");
    upload.setAlignmentX(JButton.CENTER_ALIGNMENT);
    download=new JButton("Upload");
    download.setAlignmentX(JButton.CENTER_ALIGNMENT);
    edit=new JButton("Edit");
    edit.setAlignmentX(JButton.CENTER_ALIGNMENT);
//    edit.setEnabled(false);
    upload.setMinimumSize(download.getPreferredSize());
    close.setActionCommand("close");
    close.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    upload.setActionCommand("upload");
    download.setActionCommand("download");
    refresh.setActionCommand("refresh");
    edit.setActionCommand("edit");
    paneUp.add(upload);
    paneUp.setMaximumSize(paneUp.getPreferredSize());
    paneButtons.add(refresh);
    paneButtons.add(close);
    paneButtons.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    close.addActionListener(this);
    upload.addActionListener(this);
    download.addActionListener(this);
    refresh.addActionListener(this);
    edit.addActionListener(this);
    //List
    list=new JList();
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane sp = new JScrollPane(list);
    sp.setAlignmentX(JScrollPane.CENTER_ALIGNMENT);
    sp.setMinimumSize(new Dimension(150,125)); 
    sp.setPreferredSize(sp.getMinimumSize());
    paneR.add(sp);
    paneR.add(paneTwo);
    paneTwo.add(download);
    paneTwo.add(edit);
    dia.pack();
    edgeWindow(frame, dia);
  }//end of public constructor
  
  //Methods
  /**shows and retrieves info from the SidePanel*/
  public void showIt(){
    int[] openpics=WindowManager.getIDList();
    if ( openpics==null) {
      openpics=new int[0];
    }
    imp=new ImagePlus[openpics.length];
    String[] titles=new String[openpics.length];
    for ( int i=0;i<openpics.length ;i++ ) {
      imp[i]=WindowManager.getImage(openpics[i]);
      titles[i]=imp[i].getTitle();
      int ijimage=imp[i].getID();
      if (!table.containsKey(new Integer(ijimage))) {
        boolean xalan = false;
        IJ.showStatus("Attempting to find xml class...");
        try {
          Class c = Class.forName("javax.xml.transform.TransformerFactory");
          if (c != null) xalan = true;
        }
        catch (NoClassDefFoundError exc) { }
        catch (ClassNotFoundException ex){}
        if ( xalan) {
          // this works when readTiff is a static method of XMLUtils
          try {
            //Class myXMLclass = Class.forName("XMLUtils");
            //Method m = myXMLclass.getMethod("readTiff", new Class[] {Integer.class});
            //IJ.showStatus("Ready to read xml in tiff header.");
            OMEMetaDataHandler.exportMeta(ijimage);
            
            //m.invoke(null, new Object[] {new Integer(ijimage)});
            
            
            //old method
            //readTiff(ijimage);
          }
          catch (Exception exc) { 
            exc.printStackTrace();
            IJ.showStatus("Error reading xml code.");
          }
        }else{
          IJ.showStatus("Java 1.4 required to retrieve OME metadata.");
          //OMELoginPanel.infoShow(IJ.getInstance(),
          //"Java 1.4 required to retrieve OME metadata.",
          //"OME Download");
        }
      }
    }
    list.setListData(titles);
    dia.show();
    if ( cancelPlugin) return;
  }//end of showIt method
  
   
  /** puts the given window at the edge of the specified parent window. */
  private void edgeWindow(Window parent, Window window) {
    Point loc = parent.getLocation();
    Dimension p = parent.getSize();
    Dimension w = window.getSize();
    int x = loc.x + p.width;
    int y = loc.y + p.height;
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    window.setLocation(x, y);
  }//end of centerWindow method
  
  /**implements the ActionListener actionPerformed method*/
  public void actionPerformed(ActionEvent e){
    if ( "upload".equals(e.getActionCommand())) {
      OMEDownload omed=new OMEDownload();
      omed.run(this);
      showIt();
    }else if ("download".equals(e.getActionCommand()) ) {
      if ( list.getSelectedIndex()!=-1) {
        OMEUpload omeu=new OMEUpload();
        int x=list.getSelectedIndex();
        if ( table.containsKey(new Integer(imp[x].getID()))) {
          omeu.run(imp[x], getImageMeta(imp[x].getID()));
        }else omeu.run(imp[x], null);
      }else{
        JOptionPane.showMessageDialog(parentWindow,
        "Please select an image to export to OME.",
        "OME Plugin",JOptionPane.INFORMATION_MESSAGE);
        showIt();
      }
    }else if("refresh".equals(e.getActionCommand())) {
      showIt();
    }else if ("edit".equals(e.getActionCommand())) {
      int z=list.getSelectedIndex();
      if ( z!=-1) {
        int y= imp[z].getID();
        OMEMetaPanel meta=new OMEMetaPanel(parentWindow, y, (Object[])getImageMeta(y));
        meta.show();
      }else {
        JOptionPane.showMessageDialog(parentWindow,
        "Please select an image to edit.",
        "OME Plugin",JOptionPane.INFORMATION_MESSAGE);
        showIt();
      }
    }else{
      boolean bol=yesNo(parentWindow, "Are you sure you want to exit?");
      if ( bol) {
        cancelPlugin=true;
        dia.dispose();
      }else showIt();
    }
  }//end of actionPerformed method
  
  /**pops up Yes no dialog window*/
  public static boolean yesNo(Frame owner, String question){
    int n= JOptionPane.showConfirmDialog(owner, question,"OME Plugin",
    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    
    if ( n==JOptionPane.YES_OPTION) {
      return true;
    }else return false;
  }//end of yesNo method
  
  /**adds the information to the table to upload the image back to OME*/
  public static void hashInImage(int ijid, Object[] ob){
    table.put(new Integer(ijid), ob);
  }//end of hashInImage method
  
  /**gets the OME image ID from the corresponding imagePlus ID from imageJ*/
  private int getOmeID(int ijID){
    Object[] ob=(Object[])table.get(new Integer(ijID));
    if ( ob[0]!=null) {
      return ((Integer)ob[0]).intValue();
    }else{
      return 0;
    }
  }//end of getOmeID method
  
  /**returns the metadata array for an imagePlus ID*/
  public static Object[] getImageMeta(int ijID){
    return (Object[])table.get(new Integer(ijID));
  }//end of getImageMeta method

}//end of OMESidePanel class

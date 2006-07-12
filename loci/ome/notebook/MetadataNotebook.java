package loci.ome.notebook;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.*;
import loci.ome.viewer.*;
import loci.util.About;
import org.w3c.dom.*;
import org.openmicroscopy.xml.DOMUtil;

/**
*   MetadataNotebook.java: 
*      an user-friendly application for displaying and editing OME-XML metadata.
*
*   Written by: Christopher Peterson <crpeterson2@wisc.edu>
*/

public class MetadataNotebook extends JFrame
  implements ActionListener, Runnable
{

  // -- Constants --

  /** Key mask for use with keyboard shortcuts on this operating system. */
  public static final int MENU_MASK =
    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();


  // -- Fields --

  protected JFileChooser chooser;
  protected MetadataPane metadata;
  protected File currentFile;
  protected JMenu tabsMenu;
  protected boolean opening;

  // -- Constructor --

  public MetadataNotebook(String[] args) {
    super("OME Metadata Notebook");
    
    currentFile = null;
    opening = true;
    
    File f = new File("Template.xml");
    TemplateParser tp = new TemplateParser(f);
    metadata = new MetadataPane(tp);
    setContentPane(metadata);
    
    JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);
    JMenu file = new JMenu("File");
    menubar.add(file);
    JMenuItem fileOpen = new JMenuItem("Open");
    file.add(fileOpen);
    fileOpen.setActionCommand("open");
    fileOpen.addActionListener(this);
    fileOpen.setMnemonic('o');
    fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU_MASK));
    JMenuItem fileSave = new JMenuItem("Save");
    file.add(fileSave);
    fileSave.setActionCommand("save");
    fileSave.addActionListener(this);
    fileSave.setMnemonic('s');
    fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_MASK));
    JMenuItem fileSaveAs = new JMenuItem("Save As...");
    file.add(fileSaveAs);
    fileSaveAs.setActionCommand("saveAs");
    fileSaveAs.addActionListener(this);
    fileSaveAs.setMnemonic('s');
    fileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
    JSeparator jSep = new JSeparator();
    file.add(jSep);
    JMenuItem fileExit = new JMenuItem("Exit");
    file.add(fileExit);
    fileExit.setActionCommand("exit");
    fileExit.addActionListener(this);
    fileExit.setMnemonic('x');
    fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, MENU_MASK));
    
    tabsMenu = new JMenu("Tabs");
    menubar.add(tabsMenu);
    Element[] tabs = tp.getTabs();
    String[] tabNames = new String[tabs.length];
    for(int i = 0;i<tabs.length;i++) {
      Element e = tabs[i];
      tabNames[i] = MetadataPane.getTreePathName(e);
    }
    changeTabMenu(tabNames);
    
    JMenu toolsMenu = new JMenu("Tools");
    menubar.add(toolsMenu);
    JMenuItem advView = new JMenuItem("Advanced Viewer");
    toolsMenu.add(advView);
    advView.setActionCommand("advanced");
    advView.addActionListener(this);
    advView.setMnemonic('v');
    advView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, MENU_MASK));
    
    JMenu help = new JMenu("Help");
    menubar.add(help);
    JMenuItem helpAbout = new JMenuItem("About");
    help.add(helpAbout);
    helpAbout.setActionCommand("about");
    helpAbout.addActionListener(this);
    helpAbout.setMnemonic('a');
    helpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, MENU_MASK));

    chooser = new JFileChooser(System.getProperty("user.dir"));

    if (args.length > 0) openFile(new File(args[0]));
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocation(100, 100);
    pack();
    setVisible(true);
  }


  // -- MetadataViewer API methods --

  public void openFile(File file) { 
    metadata.setOMEXML(file);
    setTitle("OME Metadata Notebook - " + file); 
  }
  
  public void saveFile(File file) {
    try {
      metadata.getRoot().writeOME(file, true);
//      FileOutputStream fos = new FileOutputStream(file);
//      DOMUtil.writeXML(fos, metadata.getDoc() ); 
    }
    catch (Exception e) {
//EVENTUALLY HAVE A DIALOG HERE
      System.out.println("Attempt failed to open file: " + file.getName() );    
    }
  }
  
  public void changeTabMenu(String[] tabs) {
    tabsMenu.removeAll();
    for (int i=0; i<tabs.length; i++) {
      String thisName = tabs[i];
      JMenuItem thisTab = new JMenuItem(thisName); 
      tabsMenu.add(thisTab);      
      thisTab.setAccelerator(KeyStroke.getKeyStroke(MetadataPane.getKey(i+1), InputEvent.ALT_MASK));
      Integer aInt = new Integer(i);
      thisTab.setActionCommand("tabChange" + aInt.toString());
      thisTab.addActionListener(this);
    }
  }


  // -- ActionListener API methods --

  /** Handles menu commands. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("open".equals(cmd)) {
      chooser.setDialogTitle("Open");
      chooser.setApproveButtonText("Open");
      chooser.setApproveButtonToolTipText("Open selected file."); 
      opening = true;
      int rval = chooser.showOpenDialog(this);
      if (rval == JFileChooser.APPROVE_OPTION) {
        new Thread(this, "MetadataNotebook-Opener").start();
      }
    }
    else if ("saveAs".equals(cmd) || ( "save".equals(cmd) && currentFile == null) ) {
      opening = false;
      chooser.setDialogTitle("Save");
      chooser.setApproveButtonText("Save");
      chooser.setApproveButtonToolTipText("Save to selected file."); 
      int rval = chooser.showOpenDialog(this);
      if (rval == JFileChooser.APPROVE_OPTION) {
        new Thread(this, "MetadataNotebook-Saver").start();
      }
    }
    else if ("save".equals(cmd) && currentFile != null) saveFile(currentFile);
    else if ("exit".equals(cmd)) System.exit(0);
    else if ("about".equals(cmd)) About.show();
    else if ("advanced".equals(cmd)) {
      String[] stuff = {};
      MetadataViewer mdv = new MetadataViewer(stuff);
      mdv.openFile(currentFile);
    }
    else if(cmd.startsWith("tabChange")) {
      metadata.tabChange( Integer.parseInt(cmd.substring(9)) );
    }
  }


  // -- Runnable API methods --

  /** Opens a file in a separate thread. */
  public void run() {
    wait(true);
    currentFile = chooser.getSelectedFile();
    if (opening) openFile(currentFile);
    else saveFile(currentFile);
    wait(false);
  }


  // -- Helper methods --

  /** Toggles wait cursor. */
  protected void wait(boolean wait) {
    setCursor(wait ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null);
  }


  // -- Main method --

  public static void main(String[] args) { new MetadataNotebook(args); }

}

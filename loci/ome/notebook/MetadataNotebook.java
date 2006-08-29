//
// MetadataNotebook.java
//

/*
OME Metadata Notebook application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006 Christopher Peterson.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.ome.notebook;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
import org.w3c.dom.*;

/**
 * An user-friendly application for displaying and editing OME-XML metadata.
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class MetadataNotebook extends JFrame
  implements ActionListener, ItemListener, Runnable
{

  // -- Constants --

  /** Key mask for use with keyboard shortcuts on this operating system. */
  public static final int MENU_MASK =
    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

  // -- Fields --

  /**The file chooser used to save and open files.*/
  protected JFileChooser chooser;
  
  /**The MetadataPane used to display/edit OMEXML content.*/
  protected MetadataPane metadata;
  
  /**Holds the current file being displayed.*/
  protected File currentFile;
  
  /**The "Tabs" menu.*/
  protected JMenu tabsMenu;
  
  /**Signifies whether we're opening(true) or saving(false) a file.*/
  protected boolean opening;
  
  /**Holds the xml viewer that displays xml data in a JTree*/
  protected loci.ome.viewer.MetadataPane mdp;
  
  /**The File&gt;New menu item.*/
  protected JMenuItem fileNew;
  
  /**The NotePane that displays a comprehensive list of all notes.*/
  protected NotePane noteP;
  
  /**
   * The WiscScan emulator that mimics the GUI of the WiscScan
   * program for ease of use by our in-house biologists.
   */
  protected WiscScanPane scanP;
  
  /**The checkboxes that switch between the four views.*/
  protected JCheckBoxMenuItem advView, noteView, normView, scanView;

  // -- Constructors --

  /** Create a default notebook window with save function and editing enabled.*/
  public MetadataNotebook(String[] args) {
    this(args,true,true);
  }

  /**
  * Create a notebook window with specified save and editing policies.
  * @param addSave whether or not saving should be enabled
  * @param editable whether or not users should be able to edit the xml
  */
  public MetadataNotebook(String[] args, boolean addSave, boolean editable) {
    super("OME Metadata Notebook");

		try {
      String os = System.getProperty("os.name");
      String laf = os != null && os.indexOf("Windows") >= 0 ?
        "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" :
        "javax.swing.plaf.metal.MetalLookAndFeel";
	    UIManager.setLookAndFeel(laf);
		}
		catch (Exception exc) {
		  System.err.println("Sorry, but we could not find the look and feel JAR.");
		}
		
    //initialize fields
    currentFile = null;
    opening = true;

    //give the Template.xml file to the parser to feed on
    TemplateParser tp = new TemplateParser("Template.xml");

    mdp = new loci.ome.viewer.MetadataPane();

    noteP = new NotePane();
    scanP = new WiscScanPane();
    scanP.setEditable(editable);

    //create a MetadataPane, where most everything happens
    if (args.length > 0) {
      File file = null;
      try {
        file = new File(args[0]);
      }
      catch (Exception exc) {
        System.out.println("Error occured: You suck.");
      }
      currentFile = file;
      metadata = new MetadataPane(file, addSave, editable);
      setTitle("OME Metadata Notebook - " + file);
    }
    else metadata = new MetadataPane((File)null, addSave, editable);

    metadata.setVisible(true);
    mdp.setVisible(false);
    noteP.setVisible(false);
    scanP.setVisible(false);

    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new CardLayout());
    contentPanel.setBorder((EmptyBorder) null);
//    metadata.setBorder(new EmptyBorder(0,0,0,0));
//    mdp.setBorder(new EmptyBorder(0,0,0,0));
    contentPanel.add("notebook", metadata);
    contentPanel.add("viewer", mdp);
    contentPanel.add("notes", noteP);
    contentPanel.add("scan", scanP);
    setContentPane(contentPanel);

    //setup the menus on this frame
    JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);
    JMenu file = new JMenu("File");
    menubar.add(file);
    file.setMnemonic('f');
    fileNew = new JMenuItem("New...");
    file.add(fileNew);
    fileNew.setActionCommand("new");
    fileNew.addActionListener(this);
    fileNew.setMnemonic('n');
    fileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, MENU_MASK));
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
    fileSave.setEnabled(addSave);
    JMenuItem fileSaveAs = new JMenuItem("Save As...");
    file.add(fileSaveAs);
    fileSaveAs.setActionCommand("saveAs");
    fileSaveAs.addActionListener(this);
    fileSaveAs.setMnemonic('s');
    fileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
      KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
    JSeparator jSep = new JSeparator();
    file.add(jSep);
    JMenuItem fileExit = new JMenuItem("Exit");
    file.add(fileExit);
    fileExit.setActionCommand("exit");
    fileExit.addActionListener(this);
    fileExit.setMnemonic('x');
    fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, MENU_MASK));

    //setup the tab menu to reflect the top-level tab names gathered by
    //TemplateParser from the template in Template.xml
    tabsMenu = new JMenu("Tabs");
    tabsMenu.setMnemonic('b');
    menubar.add(tabsMenu);
    Element[] tabs = tp.getTabs();
    String[] tabNames = new String[tabs.length];
    for (int i = 0;i<tabs.length;i++) {
      Element e = tabs[i];
      tabNames[i] = MetadataPane.getTreePathName(e);
    }
    //call the method that changes the names in the Tabs menu
    changeTabMenu(tabNames);

    JMenu toolsMenu = new JMenu("Tools");
    toolsMenu.setMnemonic('t');
    menubar.add(toolsMenu);    
    normView = new JCheckBoxMenuItem("Normal View");
    normView.setSelected(true);
    toolsMenu.add(normView);
    normView.addItemListener(this);
    normView.setMnemonic('r');
    normView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_MASK));
    scanView = new JCheckBoxMenuItem("WiscScan View");
    scanView.setSelected(false);
    toolsMenu.add(scanView);
    scanView.addItemListener(this);
    scanView.setMnemonic('w');
    scanView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, MENU_MASK));
    noteView = new JCheckBoxMenuItem("Note List View");
    noteView.setSelected(false);
    toolsMenu.add(noteView);
    noteView.addItemListener(this);
    noteView.setMnemonic('l');
    noteView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, MENU_MASK));
    advView = new JCheckBoxMenuItem("XML View");
    advView.setSelected(false);
    toolsMenu.add(advView);
    advView.addItemListener(this);
    advView.setMnemonic('v');
    advView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, MENU_MASK));
    JSeparator sep = new JSeparator();
    toolsMenu.add(sep);
    JMenuItem exportItem = new JMenuItem("Export Notes");
    exportItem.setSelected(false);
    toolsMenu.add(exportItem);
    exportItem.addActionListener(this);
    exportItem.setActionCommand("export");
    exportItem.setMnemonic('x');
    exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, MENU_MASK));

    JMenu help = new JMenu("Help");
    help.setMnemonic('h');
    menubar.add(help);
    JMenuItem helpAbout = new JMenuItem("About");
    help.add(helpAbout);
    helpAbout.setActionCommand("about");
    helpAbout.addActionListener(this);
    helpAbout.setMnemonic('a');
    helpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, MENU_MASK));

    //make a filechooser to open and save our precious files
    chooser = new JFileChooser(System.getProperty("user.dir"));
    
    //make WiscScan view the default
    scanView.setSelected(true);

    //useful frame method that handles closing of window
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    //put frame in the right place, with the right size, and make visible
    setLocation(100, 100);
    pack();
    setVisible(true);
  }

  // -- MetadataNotebook API methods --

  /**Set the current file being displayed to this file.*/
  protected void setCurrentFile(File aFile) { currentFile = aFile; }

  /** opens a file, sets the title of the frame to reflect the current file.*/
  public void openFile(File file) {
    metadata.setOMEXML(file);
    mdp.setOMEXML(file);
    scanP.setOMEXML(metadata.getRoot());
    if(noteView.getState()) noteP.setPanels(metadata.panelList);
    setTitle("OME Metadata Notebook - " + file);
  }

  /** saves to a file, sets title of frame to reflect the current file */
  public void saveFile(File file) {
    metadata.saveFile(file);
  }

  /**Given an array of Strings of appropriate tab names, this method
   * sets up the tab menu accordingly.
   */
  public void changeTabMenu(String[] tabs) {
    tabsMenu.removeAll();
    for (int i=0; i<tabs.length; i++) {
      String thisName = tabs[i];
      JMenuItem thisTab = new JMenuItem(thisName);
      tabsMenu.add(thisTab);
      //set up shortcut keys if tabs menu has less than 11 items
      if ((i+1) < 11) {
        thisTab.setAccelerator(KeyStroke.getKeyStroke(
          MetadataPane.getKey(i+1), InputEvent.ALT_MASK));
      }
      Integer aInt = new Integer(i);
      thisTab.setActionCommand("tabChange" + aInt.toString());
      thisTab.addActionListener(this);
    }
  }


  // -- ActionListener API methods --

  /** Handles menu commands. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("new".equals(cmd)) {
      if (metadata.getState()) {
        Object[] options = {"Yes, do it!", "No thanks."};
        int n = JOptionPane.showOptionDialog(this,
          "Are you sure you want to create\n" +
          "a new file without saving your\n" +
          "changes to the current file?",
          "Current File Not Saved",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null,     //don't use a custom Icon
          options,  //the titles of buttons
          options[0]); //default button title
        if (n == JOptionPane.YES_OPTION) {
          setTitle("OME Metadata Notebook");
          currentFile = null;
          metadata.setupTabs();
        }
      }
      else {
        setTitle("OME Metadata Notebook");
        currentFile = null;
        metadata.setupTabs();
      }
    }
    else if ("open".equals(cmd)) {
      if (metadata.getState()) {
        Object[] options = {"Yes, do it!", "No thanks."};
        int n = JOptionPane.showOptionDialog(this,
          "Are you sure you want to open\n" +
          "a new file without saving your\n" +
          "changes to the current file?",
          "Current File Not Saved",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null,     //don't use a custom Icon
          options,  //the titles of buttons
          options[0]); //default button title
        if (n == JOptionPane.YES_OPTION) {
          chooser.setDialogTitle("Open");
          chooser.setApproveButtonText("Open");
          chooser.setApproveButtonToolTipText("Open selected file.");
          opening = true;
          int rval = chooser.showOpenDialog(this);
          if (rval == JFileChooser.APPROVE_OPTION) {
            new Thread(this, "MetadataNotebook-Opener").start();
          }
        }
      }
      else {
        chooser.setDialogTitle("Open");
        chooser.setApproveButtonText("Open");
        chooser.setApproveButtonToolTipText("Open selected file.");
        opening = true;
        int rval = chooser.showOpenDialog(this);
        if (rval == JFileChooser.APPROVE_OPTION) {
          new Thread(this, "MetadataNotebook-Opener").start();
        }
      }
    }
    else if ("saveAs".equals(cmd) ||
      ("save".equals(cmd) && currentFile == null))
    {
      opening = false;
      chooser.setDialogTitle("Save");
      chooser.setApproveButtonText("Save");
      chooser.setApproveButtonToolTipText("Save to selected file.");
      int rval = chooser.showOpenDialog(this);
      if (rval == JFileChooser.APPROVE_OPTION) {
        new Thread(this, "MetadataNotebook-Saver").start();
        metadata.stateChanged(false);
      }
    }
    else if ("save".equals(cmd) && currentFile != null) {
      saveFile(currentFile);
      metadata.stateChanged(false);
    }
    else if ("exit".equals(cmd)) {
          if (metadata.getState()) {
        Object[] options = {"Yes, exit!", "No thanks."};
        int n = JOptionPane.showOptionDialog(this,
          "Are you sure you want to exit without\n" +
          "saving your changes to the current file?",
          "Current File Not Saved",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null,     //don't use a custom Icon
          options,  //the titles of buttons
          options[0]); //default button title
        if (n == JOptionPane.YES_OPTION) {
          System.exit(0);
        }
      }
      else {
        System.exit(0);
      }
    }
    else if ("about".equals(cmd)) {
      JOptionPane.showMessageDialog(this,
        "OME Metadata Notebook\n" +
        "Built @date@\n\n" +
        "The OME Metadata Notebook is LOCI software written by\n" +
        "Christopher Peterson.\n" +
        "http://www.loci.wisc.edu/software/#notebook",
        "OME Metadata Notebook", JOptionPane.INFORMATION_MESSAGE);
    }
    else if (cmd.startsWith("tabChange")) {
      metadata.tabChange( Integer.parseInt(cmd.substring(9)) );
    }
    else if ("export".equals(cmd)) {
      noteP.tPanels = metadata.panelList;
      noteP.exportNotes();
    }
  }

  /**Handles the checkbox menuitems that change the view.*/
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED && 
      (JCheckBoxMenuItem) e.getItem() == advView) {
      noteView.setState(false);
      normView.setState(false);
      scanView.setState(false);
      metadata.setVisible(false);
      noteP.setVisible(false);
      scanP.setVisible(false);
      tabsMenu.setEnabled(false);
      fileNew.setEnabled(false);
      mdp.setOMEXML(metadata.getRoot());
      mdp.setVisible(true);
    }
    else if (e.getStateChange() == ItemEvent.SELECTED && 
      (JCheckBoxMenuItem) e.getItem() == noteView) {
      advView.setState(false);
      normView.setState(false);
      scanView.setState(false);
      metadata.setVisible(false);
      mdp.setVisible(false);
      scanP.setVisible(false);
      tabsMenu.setEnabled(false);
      fileNew.setEnabled(false);
      noteP.setPanels(metadata.panelList);
      noteP.setVisible(true);
    }
    else if (e.getStateChange() == ItemEvent.SELECTED && 
      (JCheckBoxMenuItem) e.getItem() == scanView) {
      noteView.setState(false);
      advView.setState(false);
      normView.setState(false);
      metadata.setVisible(false);
      mdp.setVisible(false);
      tabsMenu.setEnabled(false);
      fileNew.setEnabled(false);
      scanP.setOMEXML(metadata.getRoot());
      scanP.setVisible(true);
    }
    else if (e.getStateChange() == ItemEvent.SELECTED && 
      (JCheckBoxMenuItem) e.getItem() == normView) {
      advView.setState(false);
      noteView.setState(false);
      scanView.setState(false);
      noteP.setVisible(false);
      scanP.setVisible(false);
      mdp.setVisible(false);
      tabsMenu.setEnabled(true);
      fileNew.setEnabled(true);
      metadata.setupTabs(metadata.getRoot());
      metadata.setVisible(true);
    }
    else {
      if(!advView.getState() && !noteView.getState()
        && !scanView.getState())
      	normView.setState(true);
      else {
	      noteP.setVisible(false);
	      mdp.setVisible(false);
	      scanP.setVisible(false);
	      tabsMenu.setEnabled(true);
	      fileNew.setEnabled(true);
	      metadata.setupTabs(metadata.getRoot());
	      metadata.setVisible(true);
      }
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

  /**Test method for debug uses, or simply to bring up a notebook window
  *  from the console or whatever.
  */
  public static void main(String[] args) { new MetadataNotebook(args); }

}

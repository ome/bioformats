/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.ome.editor;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import loci.formats.gui.BufferedImageReader;
import loci.formats.gui.ExtensionFileFilter;
import loci.formats.gui.GUITools;

import org.openmicroscopy.xml.OMENode;
import org.w3c.dom.Element;

/**
 * An user-friendly application for displaying and editing OME-XML metadata.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/MetadataEditor.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/MetadataEditor.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class MetadataEditor extends JFrame
  implements ActionListener, ItemListener, Runnable
{

  // -- Constants --

  /** URL of OME Metadata Editor web page. */
  public static final String URL_OME_METADATA_EDITOR =
    "http://www.loci.wisc.edu/software/ome-metadata-editor";

  /** Key mask for use with keyboard shortcuts on this operating system. */
  public static final int MENU_MASK =
    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

  // -- Fields --

  /**The file chooser used to save and open files.*/
  protected JFileChooser opener, saver;

  /**Format filters for saver JFileChooser.*/
  protected ExtensionFileFilter tiffFilter, omeFilter;

  /**The MetadataPane used to display/edit OMEXML content.*/
  protected MetadataPane metadata;

  /**Holds the current file being displayed.*/
  protected File currentFile;

  /**The "Tabs" menu.*/
  protected JMenu tabsMenu;

  /**Signifies whether we're opening(true) or saving(false) a file.*/
  protected boolean opening;

  /**Holds the xml viewer that displays xml data in a JTree.*/
  protected loci.ome.viewer.MetadataPane mdp;

  /**The File&gt;New menu item.*/
  protected JMenuItem fileNew;

  /**The File&gt;Save menu item.*/
  protected JMenuItem fileSave;

  /**The NotePane that displays a comprehensive list of all notes.*/
  protected NotePane noteP;

  /**
   * The WiscScan emulator that mimics the GUI of the WiscScan
   * program for ease of use by our in-house biologists.
   */
  protected WiscScanPane scanP;

  /**The checkboxes that switch between the four views.*/
  protected JCheckBoxMenuItem advView, noteView, normView, scanView, showID;

  // -- Constructors --

  public MetadataEditor() {
    this((String[]) null);
  }

  /** Create a default editor window with save function and editing enabled.*/
  public MetadataEditor(String[] args) {
    this(args, (OMENode) null, (String) null, true, true);
  }

  /**
  * Create an editor window with specified save and editing policies.
  * @param args an array of strings the first entry of which should be a
  * filename, otherwise, send a (String[]) null as this parameter.
  * @param ome An OMENode xml root to be launched if a filename is not
  * appropriate, for instance when in the LociDataBrowser we have a
  * FilePattern if using a FileStitcher. Thus, send ome instead. Note
  * that this is a temporary fix. If your file is OME-Tiff there's going
  * to be a problem if saving is enabled and you try to save to it, since
  * we're circumventing the code that flags TIFF files.
  * @param title Sets the title of the editor window, which is done by
  * default if a file URL is given in args, but otherwise should be set
  * using this String parameter.
  * @param addSave whether or not saving should be enabled
  * @param editable whether or not users should be able to edit the xml
  */
  public MetadataEditor(String[] args, OMENode ome, String title,
    boolean addSave, boolean editable)
  {
    super("OME Metadata Editor");
    if (title != null) setTitle(title);

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
    if (args != null && args.length > 0) {
      File file = null;
      try {
        file = new File(args[0]);
      }
      catch (Exception exc) {
        System.out.println("Error occured: You suck.");
      }
      currentFile = file;
      metadata = new MetadataPane(file, addSave, editable);
      setTitle("OME Metadata Editor - " + file);
    }
    else metadata = new MetadataPane((File) null, addSave, editable);
    if (ome != null) metadata.setOMEXML(ome);

    metadata.setVisible(true);
    mdp.setVisible(false);
    noteP.setVisible(false);
    scanP.setVisible(false);

    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new CardLayout());
    contentPanel.setBorder((EmptyBorder) null);
    contentPanel.add("editor", metadata);
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
    JSeparator jSep = new JSeparator();
    file.add(jSep);
    fileSave = new JMenuItem("Save");
    file.add(fileSave);
    fileSave.setActionCommand("save");
    fileSave.addActionListener(this);
    fileSave.setMnemonic('s');
    fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_MASK));
    fileSave.setEnabled(addSave);
    JMenuItem fileSaveComp = new JMenuItem("Save to Companion");
    file.add(fileSaveComp);
    fileSaveComp.setActionCommand("saveComp");
    fileSaveComp.addActionListener(this);
    fileSaveComp.setMnemonic('c');
    fileSaveComp.setAccelerator(
      KeyStroke.getKeyStroke(KeyEvent.VK_C, MENU_MASK));
    fileSaveComp.setEnabled(addSave);
    JMenuItem fileSaveAs = new JMenuItem("Save As...");
    file.add(fileSaveAs);
    fileSaveAs.setActionCommand("saveAs");
    fileSaveAs.addActionListener(this);
    fileSaveAs.setMnemonic('s');
    fileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
      KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
    fileSaveAs.setEnabled(addSave);
    jSep = new JSeparator();
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
    for (int i = 0; i<tabs.length; i++) {
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
    JMenuItem mergeItem = new JMenuItem("Merge Companion File");
    mergeItem.setSelected(false);
    toolsMenu.add(mergeItem);
    mergeItem.addActionListener(this);
    mergeItem.setActionCommand("merge");
    mergeItem.setMnemonic('m');
    mergeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, MENU_MASK));

    JMenu options = new JMenu("Options");
    options.setMnemonic('o');
    menubar.add(options);
    showID = new JCheckBoxMenuItem("Show IDs");
    showID.setSelected(false);
    options.add(showID);
    showID.addItemListener(this);
    showID.setMnemonic('i');
    showID.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, MENU_MASK));

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
    tiffFilter = new ExtensionFileFilter(
      new String[] {"tif", "tiff"}, "Tagged Image File Format");
    omeFilter = new ExtensionFileFilter("ome", "OME-XML");
    ExtensionFileFilter allFilter = new ExtensionFileFilter(
      new String[] {"tif", "tiff", "ome"}, "All supported file formats");
    ExtensionFileFilter[] filters =
      new ExtensionFileFilter[] {tiffFilter, omeFilter};
    saver = GUITools.buildFileChooser(filters);
    saver.setCurrentDirectory(new File(System.getProperty("user.dir")));
    if (metadata.reader == null) metadata.reader = new BufferedImageReader();
    opener = GUITools.buildFileChooser(metadata.reader);
    opener.setCurrentDirectory(new File(System.getProperty("user.dir")));

    //make WiscScan view the default
    //scanView.setSelected(true);

    //useful frame method that handles closing of window
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    //put frame in the right place, with the right size, and make visible
    setLocation(100, 100);
    pack();
    setVisible(true);
  }

  // -- MetadataEditor API methods --

  /** Sets the current file being displayed to this file. */
  protected void setCurrentFile(File aFile) {
    currentFile = aFile;
    boolean allowSave = !metadata.testThirdParty(currentFile);
    fileSave.setEnabled(allowSave);
    for (int i=0; i<metadata.tabPanelList.size(); i++) {
      MetadataPane.TabPanel tp = (MetadataPane.TabPanel)
        metadata.tabPanelList.get(i);
      tp.saveButton.setEnabled(allowSave);
    }
  }

  /** Opens a file, sets the title of the frame to reflect the current file. */
  public void openFile(File file) {
    metadata.setOMEXML(file);
    mdp.setOMEXML(file);
    scanP.setOMEXML(metadata.getRoot());
    if (noteView.getState()) noteP.setPanels(metadata.panelList);
    setTitle("OME Metadata Editor - " + file);
  }

  /** Saves to a file, sets title of frame to reflect the current file. */
  public void saveFile(File file) {
    metadata.saveFile(file);
  }

  /** Saves to a companion file, same path with .meta extenstion, pure ome. */
  public void saveCompanionFile(File file) {
    metadata.saveCompanionFile(file);
  }

  public void saveTiffFile(File file) {
    metadata.saveTiffFile(file);
  }

  public void saveTiffFile(File file, String outPath) {
    metadata.saveTiffFile(file, outPath);
  }

  /**
   * Given an array of Strings of appropriate tab names,
   * this method sets up the tab menu accordingly.
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
          setTitle("OME Metadata Editor");
          currentFile = null;
          metadata.setupTabs();
        }
      }
      else {
        setTitle("OME Metadata Editor");
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
          opening = true;
          int rval = opener.showOpenDialog(this);
          if (rval == JFileChooser.APPROVE_OPTION) {
            new Thread(this, "MetadataEditor-Opener").start();
          }
        }
      }
      else {
        opening = true;
        int rval = opener.showOpenDialog(this);
        if (rval == JFileChooser.APPROVE_OPTION) {
          new Thread(this, "MetadataEditor-Opener").start();
        }
      }
    }
    else if ("saveAs".equals(cmd) ||
      ("save".equals(cmd) && currentFile == null))
    {
      opening = false;
      int rval = saver.showSaveDialog(this);
      if (rval == JFileChooser.APPROVE_OPTION) {
        new Thread(this, "MetadataEditor-Saver").start();
        metadata.stateChanged(false);
      }
    }
    else if ("save".equals(cmd) && currentFile != null) {
      saveFile(currentFile);
      metadata.stateChanged(false);
    }
    else if ("saveComp".equals(cmd) && currentFile != null) {
      saveCompanionFile(currentFile);
      metadata.stateChanged(false);
    }
    else if ("merge".equals(cmd)) {
      metadata.merge();
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
        "OME Metadata Editor\n" +
        "Revision @vcs.revision@, built @date@\n\n" +
        "The OME Metadata Editor is LOCI software written by\n" +
        "Christopher Peterson.\n" +
        URL_OME_METADATA_EDITOR,
        "OME Metadata Editor", JOptionPane.INFORMATION_MESSAGE);
    }
    else if (cmd.startsWith("tabChange")) {
      metadata.tabChange(Integer.parseInt(cmd.substring(9)));
    }
    else if ("export".equals(cmd)) {
      noteP.tPanels = metadata.panelList;
      noteP.exportNotes();
    }
  }

  /**Handles the checkbox menuitems that change the view.*/
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED &&
      (JCheckBoxMenuItem) e.getItem() == advView)
    {
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
      (JCheckBoxMenuItem) e.getItem() == noteView)
    {
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
      (JCheckBoxMenuItem) e.getItem() == scanView)
    {
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
      (JCheckBoxMenuItem) e.getItem() == normView)
    {
      advView.setState(false);
      noteView.setState(false);
      scanView.setState(false);
      noteP.setVisible(false);
      scanP.setVisible(false);
      mdp.setVisible(false);
      tabsMenu.setEnabled(true);
      fileNew.setEnabled(true);
      metadata.reRender();
      metadata.setVisible(true);
    }
    else if (e.getStateChange() == ItemEvent.SELECTED &&
      (JCheckBoxMenuItem) e.getItem() == showID)
    {
      metadata.showIDs = true;
      metadata.reRender();
    }
    else if (e.getStateChange() == ItemEvent.DESELECTED &&
      (JCheckBoxMenuItem) e.getItem() == showID)
    {
      metadata.showIDs = false;
      metadata.reRender();
    }
    else {
      if (!advView.getState() && !noteView.getState() && !scanView.getState()) {
        normView.setState(true);
      }
      else {
        noteP.setVisible(false);
        mdp.setVisible(false);
        scanP.setVisible(false);
        tabsMenu.setEnabled(true);
        fileNew.setEnabled(true);
        metadata.reRender();
        metadata.setVisible(true);
      }
    }
  }

  // -- Runnable API methods --

  /** Opens a file in a separate thread. */
  public void run() {
    wait(true);
    if (opening) {
      currentFile = opener.getSelectedFile();
      openFile(currentFile);
    }
    else {
      File outFile = saver.getSelectedFile();
      FileFilter filter = saver.getFileFilter();
      if (filter.equals((FileFilter) omeFilter)) {
        if (outFile.getPath().endsWith(".ome")) {
          currentFile = outFile;
          saveFile(outFile);
        }
        else {
          outFile = new File(outFile.getPath() + ".ome");
          currentFile = outFile;
          saveFile(outFile);
        }
      }
      else if (filter.equals((FileFilter) tiffFilter)) {
        if (outFile.getPath().endsWith(".tif") ||
          outFile.getPath().endsWith(".tiff"))
        {
          saveTiffFile(currentFile, outFile.getPath());
          currentFile = outFile;
        }
        else {
          outFile = new File(outFile.getPath() + ".tif");
          saveTiffFile(currentFile, outFile.getPath());
          currentFile = outFile;
        }
      }
      else {
        String path = outFile.getPath();
        if (path.endsWith("ome")) saveFile(currentFile);
        else if (path.endsWith("tif") || path.endsWith("tiff")) {
          saveTiffFile(currentFile, path);
        }
        else {
          System.out.println("We could not identify which format you wanted, " +
            "so the file was not saved.");
        }
      }
    }
    wait(false);
  }

  // -- Helper methods --

  /** Toggles wait cursor. */
  protected void wait(boolean wait) {
    setCursor(wait ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null);
  }

  // -- Main method --

  /**Test method for debug uses, or simply to bring up an editor window
  *  from the console or whatever.
  */
  public static void main(String[] args) { new MetadataEditor(args); }

}

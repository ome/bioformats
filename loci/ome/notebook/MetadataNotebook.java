package loci.ome.notebook;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import loci.ome.viewer.*;
import loci.util.About;
import org.w3c.dom.*;

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


  // -- Constructor --

  public MetadataNotebook(String[] args) {
    super("OME Metadata Notebook");
    
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
    JMenuItem fileExit = new JMenuItem("Exit");
    file.add(fileExit);
    fileExit.setActionCommand("exit");
    fileExit.addActionListener(this);
    fileExit.setMnemonic('x');
    fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, MENU_MASK));
    
    tabsMenu = new JMenu("Tabs");
    menubar.add(tabsMenu);
    Element[] tabs = tp.getTabs();
    changeTabMenu(tabs);
    
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
    setTitle("OME Metadata Viewer - " + file); 
  }
  
  public void changeTabMenu(Object[] tabs) {
    tabsMenu.removeAll();
    for (int i=0; i<tabs.length; i++) {
      Element elTab = (Element) tabs[i];
      String thisName = elTab.getAttribute("Name");
      if(thisName.length() == 0) thisName = elTab.getAttribute("XMLName");
      JMenuItem thisTab = new JMenuItem(thisName); 
      tabsMenu.add(thisTab);
      switch (i+1) {
        case 10 : 
          thisTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.ALT_MASK));
          thisTab.setMnemonic(KeyEvent.VK_0);
          break;
        case 1 : 
          thisTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_MASK));
          thisTab.setMnemonic(KeyEvent.VK_1);
          break;
        case 2 : 
          thisTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_MASK));
          thisTab.setMnemonic(KeyEvent.VK_2);
          break;
        case 3 : 
          thisTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_MASK));
          thisTab.setMnemonic(KeyEvent.VK_3);
          break;
        case 4 : 
          thisTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_MASK));
          thisTab.setMnemonic(KeyEvent.VK_4);
          break;
        case 5 : 
          thisTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.ALT_MASK));
          thisTab.setMnemonic(KeyEvent.VK_5);
          break;
        case 6 : 
          thisTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.ALT_MASK));
          thisTab.setMnemonic(KeyEvent.VK_6);
          break;
        case 7 : 
          thisTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, InputEvent.ALT_MASK));
          thisTab.setMnemonic(KeyEvent.VK_7);
          break;
        case 8 : 
          thisTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.ALT_MASK));
          thisTab.setMnemonic(KeyEvent.VK_8);
          break;
        case 9 : 
          thisTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_9, InputEvent.ALT_MASK));
          thisTab.setMnemonic(KeyEvent.VK_9);
          break;
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
    if ("open".equals(cmd)) {
      int rval = chooser.showOpenDialog(this);
      if (rval == JFileChooser.APPROVE_OPTION) {
        new Thread(this, "MetadataViewer-Opener").start();
      }
    }
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
    openFile(currentFile);
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

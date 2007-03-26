// 
// Notebook.java
//

/*
OME Metadata Notebook application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006-@year@ Christopher Peterson.
  
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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import loci.formats.*;
import loci.formats.ome.*;
import org.openmicroscopy.xml.*;
import org.w3c.dom.*;

/** Main notebook window. */
public class Notebook extends JFrame implements ActionListener {

  // -- Constants --

  /** Template that is loaded automatically. */
  private static final String DEFAULT_TEMPLATE = "templates/viewer.template"; 

  // -- Fields --

  /** Progress/status bar. */
  private JProgressBar progress;

  /** Menu bar. */
  private JMenuBar menubar;

  /** Main viewing area. */
  private JTabbedPane tabPane;

  /** Current GUI template. */
  private Template currentTemplate;

  /** Current template file name. */
  private String templateName; 

  /** Foreground (font) color. */
  private Color foreground;

  /** Background color. */
  private Color background;

  /** Current font. */
  private Font font;

  /** Name of the current image file. */
  private String currentFile;

  /** OME-CA root node for currently open image file. */
  private OMENode currentRoot;

  // -- Constructor -- 

  /** Constructs a new main window with the default template. */
  public Notebook(String template, String newfile) {
    super("OME Notebook"); 

    // set up the main panel

    JPanel contentPane = new JPanel();

    // set up the menu bar
    
    menubar = new JMenuBar();
    
    JMenu file = new JMenu("File");
 
    JMenuItem newFile = new JMenuItem("New...");
    newFile.setActionCommand("new");
    newFile.addActionListener(this);
    file.add(newFile);

    JMenuItem openFile = new JMenuItem("Open");
    openFile.setActionCommand("open");
    openFile.addActionListener(this);
    file.add(openFile);

    JMenuItem saveFile = new JMenuItem("Save");
    saveFile.setActionCommand("save");
    saveFile.addActionListener(this);
    file.add(saveFile);

    JMenuItem close = new JMenuItem("Close");
    close.setActionCommand("close");
    close.addActionListener(this);
    file.add(close);

    JMenu view = new JMenu("View");

    JMenuItem loadTemplate = new JMenuItem("Switch Templates");
    loadTemplate.setActionCommand("load");
    loadTemplate.addActionListener(this);
    view.add(loadTemplate);
    view.add(new JSeparator());

    menubar.add(file);
    menubar.add(view);

    // add the status bar
    progress = new JProgressBar(0, 1);
    progress.setStringPainted(true); 
    menubar.add(progress);

    setJMenuBar(menubar); 

    // provide a place to show metadata

    tabPane = new JTabbedPane();
    contentPane.add(tabPane);

    // load a default template

    if (template != null) {
      loadTemplate(template);
    }
    else {
      try {
        loadTemplate(new File(new URI(Notebook.class.getResource(
          DEFAULT_TEMPLATE).toString())).getAbsolutePath());
      }
      catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }
  
    try {
      if (newfile != null) openFile(newfile);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
    setVisible(true);
  }

  // -- Notebook API methods --

  /** Load and apply a template from the specified file. */
  public void loadTemplate(String filename) {
    // clear out previous template 

    progress.setString("Loading template " + filename);

    templateName = filename;

    // parse the template file 
    loadTemplate(new Template(filename));
  }

  /** Load and apply the given template. */
  public void loadTemplate(Template t) {
    currentTemplate = t;

    if (tabPane != null) getContentPane().remove(tabPane);
    tabPane = new JTabbedPane();
    getContentPane().add(tabPane);
    
    // retrieve defined GUI parameters
    setPreferredSize(new Dimension(currentTemplate.getDefaultWidth(), 
      currentTemplate.getDefaultHeight()));

    font = new Font(currentTemplate.getFontStyle(), Font.PLAIN, 
      currentTemplate.getFontSize());

    int[] fore = currentTemplate.getFontColor();
    int[] back = currentTemplate.getBackgroundColor();

    foreground = new Color(fore[0], fore[1], fore[2]);
    background = new Color(back[0], back[1], back[2]);

    // set up all of the defined tabs and fields 
    
    TemplateTab[] tabs = currentTemplate.getTabs();

    for (int i=0; i<tabs.length; i++) {
      Vector groups = tabs[i].getAllGroups();
      Vector fields = tabs[i].getAllFields();
  
      JScrollPane scroll = new JScrollPane();
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(1, 2));
      scroll.getViewport().add(panel);
    
      for (int j=0; j<groups.size(); j++) {
        TemplateGroup group = (TemplateGroup) groups.get(j); 
        for (int r=0; r<group.getRepetitions(); r++) { 
          GridLayout grid = (GridLayout) panel.getLayout();
          grid.setRows(grid.getRows() + group.getNumFields() + 1);
          panel.setLayout(grid);
     
          panel.add(new JLabel(group.getName() + " #" + (r + 1)));
          if (currentTemplate.editTemplateFields() && r == 0) {
            JButton add = new JButton("Add new instance of this group");
            add.setActionCommand("cloneGroup" + i + "-" + j);
            add.addActionListener(this);
            panel.add(add);
          }
          else panel.add(new JLabel(""));

          for (int k=0; k<group.getNumFields(); k++) {
            TemplateField field = group.getField(r, k); 
            panel.add(new JLabel(field.getName()));
            panel.add(field.getComponent());
          } 
        } 
      }

      GridLayout grid = (GridLayout) panel.getLayout();
      grid.setRows(grid.getRows() + fields.size() - 1);
      panel.setLayout(grid);

      for (int j=0; j<fields.size(); j++) {
        panel.add(new JLabel(tabs[i].getField(j).getName()));
        panel.add(tabs[i].getField(j).getComponent());
      }
    
      tabPane.add(scroll, tabs[i].getName()); 
    }
    
    setFontAndColors(this);
    changeEditable(currentTemplate.isEditable(), this);
    progress.setString("");

    pack(); 
  }

  /** Recursively set the font and colors for the root and all children. */
  public void setFontAndColors(Container root) {
    Component[] components = root instanceof JMenu ? 
      ((JMenu) root).getMenuComponents() : root.getComponents();
    for (int i=0; i<components.length; i++) {
      components[i].setFont(font);
      components[i].setForeground(foreground);
      components[i].setBackground(background);

      if (components[i] instanceof JTextArea || 
        components[i] instanceof JComboBox || 
        components[i] instanceof JCheckBox) 
      {
        LineBorder b = new LineBorder(foreground); 
        ((JComponent) components[i]).setBorder(b); 
      }

      if (components[i] instanceof Container) {
        setFontAndColors((Container) components[i]);
      }
    }
  }

  /** Recursively enable or disable all chilren (non-Container objects). */
  public void changeEditable(boolean enable, Container root) {
    Component[] c = root.getComponents();
    for (int i=0; i<c.length; i++) {
      if (!(c[i] instanceof JMenuBar) && !(c[i] instanceof JMenu) && 
        !(c[i] instanceof JMenuItem) && !(c[i] instanceof JLabel) &&
        !(c[i] instanceof JScrollBar) && !(c[i] instanceof JTabbedPane))
      {  
        c[i].setEnabled(enable); 
      }
      if (c[i] instanceof Container) {
        changeEditable(enable, (Container) c[i]);
      }
    }
  }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    if (cmd.equals("new")) {
      // check if the user wants to save the current metadata first 

      CustomDialog dialog = 
        new CustomDialog(this, "", "Save current metadata?"); 
    }
    else if (cmd.equals("open")) {
      progress.setString("Opening file..."); 
      loadTemplate(templateName); 

      try {
        ImageReader reader = new ImageReader();
        JFileChooser chooser = reader.getFileChooser();

        int status = chooser.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
          currentFile = chooser.getSelectedFile().getAbsolutePath();
        }
        if (currentFile == null) return; 

        openFile(currentFile);
      }
      catch (Exception exc) {
        exc.printStackTrace();
      }
    }
    else if (cmd.equals("save")) {
      progress.setString("Saving metadata to companion file...");
     
      if (currentRoot == null) {
        OMEXMLMetadataStore tmp = new OMEXMLMetadataStore();
        tmp.createRoot();
        currentRoot = (OMENode) tmp.getRoot();
      }

      currentTemplate.saveFields(currentRoot);
   
      // always save to the current filename + ".ome" 
      
      OMEXMLMetadataStore store = new OMEXMLMetadataStore();
      store.setRoot(currentRoot);

      try {
        String name = currentFile;
        
        if (name == null) {
          JFileChooser chooser = new JFileChooser();
      
          FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
              return true; 
            }
            public String getDescription() {
              return "OME-XML files";
            }
          };

          chooser.setFileFilter(filter);

          int status = chooser.showSaveDialog(this);
          if (status == JFileChooser.APPROVE_OPTION) {
            name = chooser.getSelectedFile().getAbsolutePath();
            if (name == null) return; 
          }
        }
        
        if (!name.endsWith(".ome")) name += ".ome";

        File f = new File(name);
        currentRoot.writeOME(f, false);
        progress.setString("Finished writing companion file (" + name + ")"); 
      }
      catch (Exception io) {
        io.printStackTrace();
      }
    }
    else if (cmd.equals("close")) {
      dispose();
    }
    else if (cmd.equals("load")) {
      // prompt for the new template file 
      JFileChooser chooser = new JFileChooser();
      
      FileFilter filter = new FileFilter() {
        public boolean accept(File f) {
          return f.getAbsolutePath().endsWith(".template") || f.isDirectory();
        }
        public String getDescription() {
          return "OME Notebook Templates";
        }
      };

      chooser.setFileFilter(filter);

      int status = chooser.showOpenDialog(this);
      if (status == JFileChooser.APPROVE_OPTION) {
        loadTemplate(chooser.getSelectedFile().getAbsolutePath());
      }
      try { 
        if (currentFile != null) openFile(currentFile); 
      }
      catch (Exception exc) {
        exc.printStackTrace();
      }
    }
    else if (cmd.startsWith("cloneGroup")) {
      cmd = cmd.substring(10);
      int tabIndex = Integer.parseInt(cmd.substring(0, cmd.indexOf("-")));
      int groupIndex = Integer.parseInt(cmd.substring(cmd.indexOf("-") + 1));

      TemplateTab tab = currentTemplate.getTabs()[tabIndex];
      TemplateGroup group = tab.getGroup(groupIndex);
      group.setRepetitions(group.getRepetitions() + 1);
      loadTemplate(currentTemplate); 
      try { 
        if (currentFile != null) openFile(currentFile); 
      }
      catch (Exception exc) {
        exc.printStackTrace();
      }
    } 
  
  }

  // -- Helper methods --

  private void openFile(String file) throws Exception {
    currentFile = file; 
    FileStitcher stitcher = new FileStitcher();
    progress.setString("Reading " + currentFile);
    
    if (currentFile.endsWith(".ome")) {
      File f = new File(currentFile); 
      currentRoot = new OMENode(f);
    }
    else {
      // first look for a companion file
      File companion = new File(currentFile + ".ome");
      if (companion.exists()) {
        progress.setString("Reading companion file (" + companion + ")"); 
        currentRoot = new OMENode(companion);
      }

      stitcher.setMetadataStore(new OMEXMLMetadataStore());
      OMEXMLMetadataStore store = 
        (OMEXMLMetadataStore) stitcher.getMetadataStore(currentFile);

      if (companion.exists()) {
        // merge the two OMENode objects
        // this depends upon the precedence setting in the template

        progress.setString("Merging companion and original file...");

        if (currentTemplate.preferCompanion()) {
          currentRoot = merge(currentRoot, (OMENode) store.getRoot());
        }
        else {
          currentRoot = merge((OMENode) store.getRoot(), currentRoot); 
        }
      }
      else currentRoot = (OMENode) store.getRoot();
      stitcher.close();
    } 
    progress.setString("Populating fields..."); 
    currentTemplate.populateFields(currentRoot); 
    loadTemplate(currentTemplate); 
    progress.setString(""); 
  }

  /**
   * Merge two OME-CA trees. When a conflict arises, use the value in 'over'.
   * This method was adapted from an earlier version of the OME Notebook,
   * written by Christopher Peterson.
   *
   * @param high OMENode with higher priority
   * @param low OMENode with lower priority
   */
  private OMENode merge(OMENode high, OMENode low) {
    OMEXMLNode temp = merge((OMEXMLNode) high, (OMEXMLNode) low);
    if (temp instanceof OMENode) return (OMENode) temp;
    return null;
  }
  
  /**
   * Merge two OME-XML trees.
   * This method was adapted from an earlier version of the OME Notebook,
   * written by Christopher Peterson.
   */
  private OMEXMLNode merge(OMEXMLNode high, OMEXMLNode low) {
    OMEXMLNode result = high;
    Vector highList = result.getChildNodes();
    Vector lowList = low.getChildNodes();
    Vector ids = new Vector();

    boolean isHighCustom = false;
    boolean isLowCustom = false;
    boolean addedCustom = false;

    for (int i=0; i<highList.size(); i++) {
      OMEXMLNode highNode = (OMEXMLNode) highList.get(i);
      String highID = highNode.getLSID();
      if (highID == null) isHighCustom = true;

      for (int j=0; j<lowList.size(); j++) {
        OMEXMLNode lowNode = (OMEXMLNode) lowList.get(j);
        String lowID = lowNode.getLSID();
        if (lowID == null) isLowCustom = true;

        if (isHighCustom && !isLowCustom) isHighCustom = false;
        else if (!isHighCustom && isLowCustom && !addedCustom) {
          result.getDOMElement().appendChild(createClone(
            lowNode.getDOMElement(), 
            highNode.getDOMElement().getOwnerDocument()));
          addedCustom = true;
          isLowCustom = false;
        }
        else if (!isHighCustom && !isLowCustom) {
          if (lowID.equals(highID)) merge(highNode, lowNode);
          else {
            if (ids.indexOf(lowID) > -1) {
              result.getDOMElement().appendChild(createClone(
                lowNode.getDOMElement(), 
                highNode.getDOMElement().getOwnerDocument()));
              ids.add(lowID); 
            }
          }
        }
      }
    }
    return result; 
  }

  /** 
   * Clones the specified DOM element, preserving the parent structure. 
   * This method was adapted from an earlier version of the OME Notebook,
   * written by Christopher Peterson.
   */
  private Element createClone(Element el, Document doc) {
    String tag = el.getTagName();
    Element clone = doc.createElement(tag);

    if (el.hasAttributes()) {
      NamedNodeMap map = el.getAttributes();
      for (int i=0; i<map.getLength(); i++) {
        Node attr = map.item(i);
        clone.setAttribute(attr.getNodeName(), attr.getNodeValue());
      }
    }
  
    if (el.hasChildNodes()) {
      NodeList nodes = el.getChildNodes();
      for (int i=0; i<nodes.getLength(); i++) {
        Node node = nodes.item(i);
        if (node instanceof Element) {
          clone.appendChild(createClone((Element) node, doc));
        }
      }
    }
    return clone; 
  }

  // -- Helper class --

  /** Custom yes/no dialog. */
  public class CustomDialog extends JFrame implements ActionListener {
    Notebook parent; 

    // TODO : this window isn't packed very nicely
    public CustomDialog(Notebook parent, String title, String question) {
      super(title);

      this.parent = parent;

      JLabel label = new JLabel(question);

      JButton yes = new JButton("Yes");
      yes.setActionCommand("yes");
      yes.addActionListener(this);
      JButton no = new JButton("No");
      no.setActionCommand("no");
      no.addActionListener(this);
 
      JPanel content = new JPanel();
      content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS)); 
      content.add(label);
      content.add(yes);
      content.add(no);

      add(content);
      setPreferredSize(new Dimension(200, 150));

      pack();
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setVisible(true);
    }
  
    public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
 
      if (cmd.equals("yes")) {
        parent.actionPerformed(new ActionEvent(this, -1, "save"));
        parent.loadTemplate(parent.templateName); 
      }
      dispose(); 
    }
  }

  // -- Main method --

  public static void main(String[] args) {
    Notebook n = new Notebook(args.length > 0 ? args[0] : null,
      args.length > 1 ? args[1] : null);
  }

}

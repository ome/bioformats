//
// Notes.java
//

/*
OME Metadata Notes application for exploration and editing of OME-XML and
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

package loci.ome.notes;

import com.jgoodies.forms.layout.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import loci.formats.*;
import loci.formats.gui.GUITools;
import loci.formats.ome.*;
import org.openmicroscopy.xml.*;
import org.w3c.dom.*;

/**
 * Main notes window.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/ome/notes/Notes.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/ome/notes/Notes.java">SVN</a></dd></dl>
 */
public class Notes extends JFrame implements ActionListener {

  // -- Constants --

  /** Template that is loaded automatically. */
  private static final String DEFAULT_TEMPLATE = "templates/viewer.template";

  private static final CellConstraints CC = new CellConstraints();

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

  /** Thumbnails for the current file. */
  private Vector thumb;

  // -- Constructor --

  /** Constructs a new main window with the default template. */
  public Notes() {
    this(null, (String) null);
  }

  /**
   * Constructs a new main window with the given metadata,
   * and default template.
   */
  public Notes(OMENode root) {
    this(null, root);
  }

  /** Constructs a new main window with the given metadata and template. */
  public Notes(String template, OMENode root) {
    super("OME Notes");
    setupWindow();

    // load the appropriate template

    if (template != null) {
      loadTemplate(template);
    }
    else {
      templateName = DEFAULT_TEMPLATE;
      loadTemplate(Notes.class.getResourceAsStream(DEFAULT_TEMPLATE));
    }

    currentRoot = root;
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);
  }

  /** Constructs a new main window with the given template. */
  public Notes(String template, String newfile) {
    super("OME Notes");
    setupWindow();

    // load the appropriate template

    if (template != null) {
      loadTemplate(template);
    }
    else {
      templateName = DEFAULT_TEMPLATE;
      loadTemplate(Notes.class.getResourceAsStream(DEFAULT_TEMPLATE));
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

  // -- Notes API methods --

  /** Load and apply a template from the specified file. */
  public void loadTemplate(String filename) {
    // clear out previous template

    progress.setString("Loading template " + filename);

    templateName = filename;

    // parse the template file
    try {
      loadTemplate(new Template(filename));
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Load and apply a template from the specified InputStream. */
  public void loadTemplate(InputStream stream) {
    progress.setString("Loading template...");
    try {
      loadTemplate(new Template(stream));
    }
    catch (IOException e) {
      e.printStackTrace();
    }
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

      String rowString = "pref,";
      String colString = "pref,pref:grow,pref:grow,pref:grow,";

      int numRows = tabs[i].getRows();
      int numColumns = tabs[i].getColumns();

      for (int j=0; j<numRows; j++) {
        rowString += "pref:grow,pref,";
      }

      for (int j=0; j<numColumns; j++) {
        colString += "pref:grow,pref,pref:grow,pref,";
      }

      FormLayout l = new FormLayout(colString, rowString);
      panel.setLayout(l);

      scroll.getViewport().add(panel);

      int[] rowNumber = new int[l.getColumnCount()];
      Arrays.fill(rowNumber, 2);

      for (int j=0; j<groups.size(); j++) {
        TemplateGroup group = (TemplateGroup) groups.get(j);
        for (int r=0; r<group.getRepetitions(); r++) {
          FormLayout layout = (FormLayout) panel.getLayout();

          int col = 2;

          if (currentTemplate.editTemplateFields()) {
            JButton add = new JButton("+");
            add.setActionCommand("cloneGroup " + i + "-" + j);
            add.addActionListener(this);
            panel.add(add, CC.xy(col, rowNumber[col - 1]));
            rowNumber[col - 1]++;
            col++;

            JButton remove = new JButton("-");
            remove.setActionCommand("removeGroup" + i + "-" + j);
            remove.addActionListener(this);
            panel.add(remove, CC.xy(col, rowNumber[col - 1]));
            rowNumber[col - 1]++;
            col++;
          }

          panel.add(new JLabel(group.getName() + " #" + (r + 1)),
            CC.xy(col, rowNumber[col - 1]));
          rowNumber[col - 1] += 3;
          col++;

          for (int k=0; k<group.getNumFields(); k++) {
            TemplateField field = group.getField(r, k);
            setupField(field, col, r, rowNumber, panel);
          }
          rowNumber[col - 1]++;
        }
      }

      for (int j=0; j<fields.size(); j++) {
        TemplateField f = tabs[i].getField(j);
        setupField(f, 2, 0, rowNumber, panel);
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

      int s = JOptionPane.showConfirmDialog(this, "Save current metadata?", "",
        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

      if (s == JOptionPane.YES_OPTION) {
        actionPerformed(new ActionEvent(this, -1, "save"));
      }
      thumb.clear();
      if (templateName.equals(DEFAULT_TEMPLATE)) {
        loadTemplate(Notes.class.getResourceAsStream(DEFAULT_TEMPLATE));
      }
      else loadTemplate(templateName);
    }
    else if (cmd.equals("open")) {
      progress.setString("Opening file...");

      try {
        ImageReader reader = new ImageReader();
        JFileChooser chooser = GUITools.buildFileChooser(reader);

        int status = chooser.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
          currentFile = chooser.getSelectedFile().getAbsolutePath();
        }
        if (currentFile == null) return;

        thumb.clear();
        if (templateName.equals(DEFAULT_TEMPLATE)) {
          loadTemplate(Notes.class.getResourceAsStream(DEFAULT_TEMPLATE));
        }
        else loadTemplate(templateName);
        openFile(currentFile);
      }
      catch (Exception exc) {
        exc.printStackTrace();
      }
    }
    else if (cmd.equals("save")) {
      progress.setString("Saving metadata to companion file...");

      if (currentRoot == null) {
        OMEXMLMetadata tmp = new OMEXMLMetadata();
        currentRoot = (OMENode) tmp.getRoot();
      }

      currentTemplate.saveFields(currentRoot);

      // always save to the current filename + ".ome"

      OMEXMLMetadata store = new OMEXMLMetadata();
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

        if (name == null) return;
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
          return "OME Notes Templates";
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
      int tabIndex =
        Integer.parseInt(cmd.substring(0, cmd.indexOf("-")).trim());
      int groupIndex =
        Integer.parseInt(cmd.substring(cmd.indexOf("-") + 1).trim());

      TemplateTab tab = currentTemplate.getTabs()[tabIndex];
      TemplateGroup group = tab.getGroup(groupIndex);
      group.setRepetitions(group.getRepetitions() + 1);

      int tabIdx = tabPane.getSelectedIndex();

      loadTemplate(currentTemplate);
      try {
        if (currentFile != null) openFile(currentFile);
      }
      catch (Exception exc) {
        exc.printStackTrace();
      }

      tabPane.setSelectedIndex(tabIdx);
    }
    else if (cmd.startsWith("removeGroup")) {
      cmd = cmd.substring(11);
      int tabIndex =
        Integer.parseInt(cmd.substring(0, cmd.indexOf("-")).trim());
      int groupIndex =
        Integer.parseInt(cmd.substring(cmd.indexOf("-") + 1).trim());

      TemplateTab tab = currentTemplate.getTabs()[tabIndex];
      TemplateGroup group = tab.getGroup(groupIndex);
      group.setRepetitions(group.getRepetitions() - 1);

      int tabIdx = tabPane.getSelectedIndex();

      loadTemplate(currentTemplate);
      try {
        if (currentFile != null) openFile(currentFile);
      }
      catch (Exception exc) {
        exc.printStackTrace();
      }

      tabPane.setSelectedIndex(tabIdx);
    }

  }

  // -- Helper methods --

  private void setupWindow() {
    thumb = new Vector();

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
  }

  private void openFile(String file) throws Exception {
    currentFile = file;
    ImageReader reader = new ImageReader();
    reader.setNormalized(true);
    reader.setOriginalMetadataPopulated(true);
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

      reader.setMetadataStore(new OMEXMLMetadata());
      reader.setId(currentFile);
      OMEXMLMetadata store =
        (OMEXMLMetadata) reader.getMetadataStore();

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

      // grab thumbnails

      for (int i=0; i<reader.getSeriesCount(); i++) {
        reader.setSeries(i);
        thumb.add(reader.openThumbImage(0));
      }

      reader.close();
    }

    progress.setString("Populating fields...");
    currentTemplate.initializeFields(currentRoot);
    currentTemplate.populateFields(currentRoot);
    loadTemplate(currentTemplate);
    progress.setString("");
  }

  /** Set up a new field in the current tab. */
  private void setupField(TemplateField field, int col, int r, int[] rowNumber,
    JPanel panel)
  {
    JComponent c = field.getComponent();

    if (field.getType().equals("thumbnail")) {
      BufferedImage thumbnail = null;
      if (field.getValueMap() == null && thumb.size() > 0) {
        thumbnail = (BufferedImage) thumb.get(r);
      }
      else if (field.getValueMap() != null) {
        try {
          ImageReader tempReader = new ImageReader();
          tempReader.setId(field.getValueMap());
          if (tempReader.getImageCount() > 0) {
            thumbnail = tempReader.openThumbImage(0);
          }
          tempReader.close();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }

      if (thumbnail != null) {
        ((JLabel) c).setIcon(new ImageIcon(thumbnail));
      }
    }

    int cndx = (field.getColumn() - 1) * 4 + col;
    int rowNdx = field.getRow() == -1 ? rowNumber[cndx - 1] : 2*field.getRow();
    FormLayout layout = (FormLayout) panel.getLayout();
    if (rowNdx > layout.getRowCount() - 1) {
      layout.appendRow(new RowSpec("pref:grow"));
      layout.appendRow(new RowSpec("pref"));
    }

    panel.add(new JLabel(field.getName()), CC.xy(cndx, rowNdx));
    panel.add(c, CC.xy(cndx + 1, rowNdx));

    if (field.getRow() == -1) {
      rowNumber[cndx - 1]++;
      rowNumber[cndx]++;
    }

    if (rowNdx > rowNumber[1]) {
      for (int i=0; i<col - 1; i++) {
        rowNumber[i] = rowNdx + 2;
      }
    }
  }

  /**
   * Merge two OME-CA trees. When a conflict arises, use the value in 'over'.
   * This method was adapted from an earlier version of OME Notes,
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
   * This method was adapted from an earlier version of OME Notes,
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
   * This method was adapted from an earlier version of OME Notes,
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

  // -- Main method --

  public static void main(String[] args) {
    String template = null, data = null;
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-template")) {
        if (args.length > i + 1) {
          template = args[++i];
        }
        else System.err.println("Please specify a template file");
      }
      else data = args[i];
    }

    new Notes(template, data);
  }

}

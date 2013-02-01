/*
 * #%L
 * OME Notes library for flexible organization and presentation of OME-XML
 * metadata.
 * %%
 * Copyright (C) 2007 - 2013 Open Microscopy Environment:
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

package loci.ome.notes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ImageReader;
import loci.formats.gui.BufferedImageReader;
import loci.formats.gui.GUITools;
import loci.formats.meta.AggregateMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Main notes window.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-notes/src/loci/ome/notes/Notes.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-notes/src/loci/ome/notes/Notes.java;hb=HEAD">Gitweb</a></dd></dl>
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

  /** Menu item for saving data. */
  private JMenuItem saveFile;

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

  /** Thumbnails for the current file. */
  private Vector thumb;

  private AggregateMetadata metadata;
  private OMEXMLService service;

  // -- Constructor --

  /** Constructs a new main window with the default template. */
  public Notes() {
    this(null, (String) null);
  }

  /**
   * Constructs a new main window with the given metadata,
   * and default template.
   */
  public Notes(AggregateMetadata store) {
    this(null, store);
  }

  /** Constructs a new main window with the given metadata and template. */
  public Notes(String template, AggregateMetadata store) {
    super("OME Notes");
    try {
      ServiceFactory factory = new ServiceFactory();
      service = (OMEXMLService) factory.getInstance(OMEXMLService.class);
    }
    catch (DependencyException e) { e.printStackTrace(); }

    setupWindow();

    // load the appropriate template

    if (template != null) {
      loadTemplate(template);
    }
    else {
      templateName = DEFAULT_TEMPLATE;
      loadTemplate(Notes.class.getResourceAsStream(DEFAULT_TEMPLATE));
    }

    metadata = store;
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

    tabPane.removeAll();

    // retrieve defined GUI parameters
    setSize(new Dimension(currentTemplate.getDefaultWidth(),
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
    saveFile.setEnabled(enable);
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

      currentTemplate.saveFields(metadata);

      // always save to the current filename + ".ome"

      metadata = new AggregateMetadata(new ArrayList());

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

        String xml = null;
        try {
          xml = service.getOMEXML((MetadataRetrieve) metadata);
        }
        catch (ServiceException exc) { }

        File f = new File(name);
        FileWriter writer = new FileWriter(f);
        writer.write(xml);
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
    contentPane.setLayout(new BorderLayout());
    setContentPane(contentPane);

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

    saveFile = new JMenuItem("Save");
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

    setJMenuBar(menubar);

    // add the status bar
    progress = new JProgressBar(0, 1);
    progress.setStringPainted(true);
    contentPane.add(progress, BorderLayout.SOUTH);

    // provide a place to show metadata
    tabPane = new JTabbedPane();
    contentPane.add(tabPane, BorderLayout.CENTER);
  }

  private void openFile(String file) throws Exception {
    currentFile = file;
    BufferedImageReader reader = new BufferedImageReader();
    reader.setNormalized(true);
    reader.setOriginalMetadataPopulated(true);
    progress.setString("Reading " + currentFile);

    ArrayList delegates = new ArrayList();

    if (currentFile.endsWith(".ome")) {
      RandomAccessInputStream s = new RandomAccessInputStream(currentFile);
      String xml = s.readString((int) s.length());
      s.close();
      try {
        delegates.add(service.createOMEXMLMetadata(xml));
      }
      catch (ServiceException e) { }
    }
    else {
      // first look for a companion file
      File companion = new File(currentFile + ".ome");
      MetadataStore companionStore = null, readerStore = null;
      if (companion.exists()) {
        progress.setString("Reading companion file (" + companion + ")");
        RandomAccessInputStream s = new RandomAccessInputStream(currentFile);
        String xml = s.readString((int) s.length());
        s.close();
        try {
          companionStore = service.createOMEXMLMetadata(xml);
        }
        catch (ServiceException e) { }
      }

      try {
        reader.setMetadataStore(service.createOMEXMLMetadata());
      }
      catch (ServiceException e) { }
      reader.setId(currentFile);
      readerStore = reader.getMetadataStore();

      if (companion.exists()) {
        // merge the two OMENode objects
        // this depends upon the precedence setting in the template

        progress.setString("Merging companion and original file...");

        if (currentTemplate.preferCompanion()) {
          delegates.add(companionStore);
          delegates.add(readerStore);
        }
        else {
          delegates.add(readerStore);
          delegates.add(companionStore);
        }
      }
      else delegates.add(readerStore);

      // grab thumbnails

      for (int i=0; i<reader.getSeriesCount(); i++) {
        reader.setSeries(i);
        thumb.add(reader.openThumbImage(0));
      }

      reader.close();
    }
    metadata = new AggregateMetadata(delegates);

    progress.setString("Populating fields...");
    currentTemplate.initializeFields(metadata);
    currentTemplate.populateFields(metadata);
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
          BufferedImageReader tempReader = new BufferedImageReader();
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

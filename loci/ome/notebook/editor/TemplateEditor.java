//
// TemplateEditor.java
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

package loci.ome.notebook.editor;

import com.jgoodies.forms.layout.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import loci.ome.notebook.*;
import loci.ome.viewer.MetadataPane;
import org.openmicroscopy.xml.OMENode;

/** Main class for notebook template editor. */
public class TemplateEditor extends JFrame 
  implements ActionListener, MouseListener 
{

  // -- Constants --

  /** List of component names. */
  private String[] COMPONENT_NAMES = new String[] {"Text field", 
    "Number field", "Choice field", "Checkbox field"
  };

  /** List of component classes. */
  private Class[] COMPONENTS = new Class[] {
    JTextField.class, JSpinner.class, JComboBox.class, JCheckBox.class
  };

  /** List of component types. */
  private String[] COMPONENT_TYPES = new String[] {
    "var", "int", "enum", "bool"
  };

  public static int TAB = 0, GROUP = 1, FIELD = 2;

  // -- Fields --

  /** Menu for choosing which tab to edit. */
  private JMenu tabMenu;

  /** Menus for choosing which tab, group, or field to remove. */
  private JMenu removeTab, removeGroup, removeField;

  /** Menu for choosing a field to edit. */
  private JMenu fieldOptions;

  /** Layout for the template panel. */
  private FormLayout templateLayout;

  /** Panel that shows the template. */
  private JPanel template;

  /** Panel that contains organization options. */
  private JPanel organization;

  /** List of TemplateTabs. */
  private Vector tabs;

  /** List of tab panels corresponding to TemplateTabs. */
  private Vector tabPanels;
 
  /** Current tab index. */
  private int currentTab;

  /** General template options. */
  private Hashtable options;

  /** Popup menu for dynamically adding/removing rows. */
  private JPopupMenu popup;

  // -- Constructor --

  /** Constructs a new window with a blank template. */
  public TemplateEditor() {
    super("OME Notebook Template Editor");

    tabs = new Vector();
    tabPanels = new Vector();

    // set up menu bar 

    JMenuBar menubar = new JMenuBar();

    JMenu file = new JMenu("File");

    JMenuItem newFile = new JMenuItem("New...");
    newFile.setActionCommand("new");
    newFile.addActionListener(this);
    file.add(newFile);

    JMenuItem openFile = new JMenuItem("Open...");
    openFile.setActionCommand("open");
    openFile.addActionListener(this);
    file.add(openFile);

    JMenuItem saveFile = new JMenuItem("Save...");
    saveFile.setActionCommand("save");
    saveFile.addActionListener(this);
    file.add(saveFile);

    JMenuItem quit = new JMenuItem("Quit");
    quit.setActionCommand("quit");
    quit.addActionListener(this);
    file.add(quit);
 
    menubar.add(file);
  
    JMenu view = new JMenu("View");

    tabMenu = new JMenu("Tab");
    view.add(tabMenu);
    menubar.add(view);

    JMenu add = new JMenu("Add");

    JMenuItem newTab = new JMenuItem("Tab");
    newTab.setActionCommand("new tab");
    newTab.addActionListener(this);
    add.add(newTab);

    JMenuItem newGroup = new JMenuItem("Group");
    newGroup.setActionCommand("new group");
    newGroup.addActionListener(this);
    add.add(newGroup);

    add.add(new JSeparator());

    for (int i=0; i<COMPONENT_NAMES.length; i++) {
      JMenuItem item = new JMenuItem(COMPONENT_NAMES[i]);
      item.setActionCommand(COMPONENT_NAMES[i]);
      item.addActionListener(this);
      add.add(item);
    }

    menubar.add(add); 

    JMenu remove = new JMenu("Remove");

    removeTab = new JMenu("Tab");
    remove.add(removeTab);

    removeGroup = new JMenu("Group");
    remove.add(removeGroup);

    removeField = new JMenu("Field");
    remove.add(removeField);

    menubar.add(remove);

    JMenu optionsMenu = new JMenu("Options");

    JMenuItem general = new JMenuItem("General");
    general.setActionCommand("general options");
    general.addActionListener(this);
    optionsMenu.add(general);

    fieldOptions = new JMenu("Field");
    optionsMenu.add(fieldOptions);

    menubar.add(optionsMenu);

    setJMenuBar(menubar);

    // set up popup menu
      
    popup = new JPopupMenu();
    JMenuItem item = new JMenuItem("Add row");
    item.setActionCommand("add row");
    item.addActionListener(this);
    popup.add(item);

    item = new JMenuItem("Remove row");
    item.setActionCommand("remove row");
    item.addActionListener(this);
    popup.add(item);

    popup.add(new JSeparator());

    item = new JMenuItem("Add column");
    item.setActionCommand("add column");
    item.addActionListener(this);
    popup.add(item);

    item = new JMenuItem("Remove column");
    item.setActionCommand("remove column");
    item.addActionListener(this);
    popup.add(item);

    // set up template panel

    CellConstraints cc = new CellConstraints();

    templateLayout = new FormLayout("pref:grow, pref:grow", "pref:grow");
    template = new JPanel(templateLayout);
    template.addMouseListener(this);

    String rowString = "2dlu, pref:grow";
    for (int i=1; i<COMPONENT_NAMES.length; i++) {
      rowString += ", 2dlu, pref:grow";
    }

    setContentPane(template); 
    setPreferredSize(new Dimension(768, 768));
    pack();
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);
  }

  // -- TemplateEditor API methods --

  /** Add a new tab to this template. */
  private void addTab(TemplateTab tab) {
    addTab(tab, tab.getNumFields(), 1);
  }

  /** Add a new tab with the specified number of rows and columns. */
  private void addTab(TemplateTab tab, int rows, int columns) {
    tabs.add(tab);

    StringBuffer row = new StringBuffer();
    StringBuffer col = new StringBuffer();

    for (int i=0; i<rows; i++) {
      row.append("pref:grow");
      if (i < rows - 1) row.append(", ");
    }
  
    for (int i=0; i<columns; i++) {
      col.append("pref:grow, pref:grow");
      if (i < columns - 1) col.append(", ");
    } 

    FormLayout layout = new FormLayout(col.toString(), row.toString());
    JPanel panel = new JPanel(layout); 
   
    CellConstraints cc = new CellConstraints();
      
    for (int i=0; i<tab.getNumFields(); i++) {
      TemplateField f = tab.getField(i); 
      panel.add(new JLabel(f.getName()), cc.xy(1, i + 1));
      panel.add(f.getComponent(), cc.xy(2, i + 1));    
    }
    tabPanels.add(panel); 
  
    // add this tab to the menu bar
    JMenuItem item = new JMenuItem(tab.getName());
    item.setActionCommand("tab" + (tabs.size() - 1));
    item.addActionListener(this);
    tabMenu.add(item);
    pack(); 
  }

  private void updateRemoveLists() {
    removeTab.removeAll();
    removeGroup.removeAll();
    removeField.removeAll();
 
    for (int i=0; i<tabs.size(); i++) {
      TemplateTab tab = (TemplateTab) tabs.get(i); 
      
      JMenuItem item = new JMenuItem(tab.getName());
      item.setActionCommand("removeTab " + i);
      item.addActionListener(this);
      removeTab.add(item);

      int numFields = 0;

      for (int j=0; j<tab.getNumGroups(); j++) {
        TemplateGroup g = tab.getGroup(j);

        JMenuItem group = new JMenuItem(g.getName());
        group.setActionCommand("removeGroup " + j);
        group.addActionListener(this);
        removeGroup.add(group);

        for (int k=0; k<g.getNumFields(); k++) {
          JMenuItem f = new JMenuItem(g.getField(0, k).getName());
          f.setActionCommand("removeField " + j + "-" + (numFields + k));
          f.addActionListener(this);
          removeField.add(f);
        }
        numFields += g.getNumFields(); 
      }
      
      for (int j=0; j<tab.getNumFields(); j++) {
        JMenuItem f = new JMenuItem(tab.getField(j).getName());
        f.setActionCommand("removeField " + (numFields + j)); 
        f.addActionListener(this);
        removeField.add(f);
      }
    }
 
  }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd == null) return;

    if (cmd.equals("new")) {
    }
    else if (cmd.equals("open")) {
      // prompt for template file to open
      JFileChooser chooser = new JFileChooser();
      FileFilter filter = new FileFilter() {
        public boolean accept(File f) {
          return f.getAbsolutePath().endsWith(".template") || f.isDirectory();
        }
        public String getDescription() { return "OME Notebook templates"; } 
      };

      chooser.setFileFilter(filter);

      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        try { 
          Template t = 
            new Template(chooser.getSelectedFile().getAbsolutePath());
          TemplateTab[] newTabs = t.getTabs();
          for (int i=0; i<newTabs.length; i++) addTab(newTabs[i]);
        }
        catch (IOException exc) {
          exc.printStackTrace();
        }
      }
    
    }
    else if (cmd.equals("save")) {
      // prompt for path to save to
      try { 
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
          Hashtable h = new Hashtable();
          if (options != null && options.size() > 0) {  
            Object[] keys = options.keySet().toArray();
            for (int i=0; i<keys.length; i++) {
              String value = ((Option) options.get(keys[i])).value; 
              if (value != null) h.put(keys[i], value); 
            } 
          } 

          Template t = new Template(
            (TemplateTab[]) tabs.toArray(new TemplateTab[0]), h);
          t.save(chooser.getSelectedFile().getAbsolutePath()); 
        }
      }
      catch (IOException exc) {
        exc.printStackTrace();
      }
    }
    else if (cmd.equals("quit")) dispose(); 
    else if (cmd.equals("new tab")) {
      TemplateTab t = new TemplateTab();
      Prompter fp = 
        new Prompter(this, null, Integer.MAX_VALUE, Integer.MAX_VALUE, TAB); 
      if (!fp.wasCanceled()) { 
        t.setName(fp.getName()); 
        addTab(t, fp.getRow(), fp.getColumn()); 
      }  
      updateRemoveLists(); 
    }
    else if (cmd.equals("new group")) {
      TemplateGroup g = new TemplateGroup();

      Prompter fp = new Prompter(this, null, 0, 0, GROUP);
      if (fp.wasCanceled()) return;

      g.setName(fp.getName());
      g.setRepetitions(fp.getRepetitions());

      ((TemplateTab) tabs.get(currentTab)).addGroup(g);
      updateRemoveLists(); 
    }
    else if (cmd.startsWith("removeTab")) {
      int ndx = Integer.parseInt(cmd.substring(cmd.indexOf(" ") + 1));
      tabs.removeElementAt(ndx); 
     
      tabMenu.removeAll();

      for (int i=0; i<tabs.size(); i++) {
        JMenuItem item = new JMenuItem(((TemplateTab) tabs.get(i)).getName());
        item.setActionCommand("tab" + i);
        item.addActionListener(this);
        tabMenu.add(item);
      }
      
      updateRemoveLists();  
    }
    else if (cmd.startsWith("removeGroup")) {
      int ndx = Integer.parseInt(cmd.substring(cmd.indexOf(" ") + 1));
      ((TemplateTab) tabs.get(currentTab)).removeGroup(ndx);
      updateRemoveLists(); 
    }
    else if (cmd.startsWith("removeField")) {
      String s = cmd.substring(cmd.indexOf(" ") + 1);
      if (s.indexOf("-") != -1) {
        int groupIdx = Integer.parseInt(s.substring(0, s.indexOf("-")));
        int fieldIdx = Integer.parseInt(s.substring(s.indexOf("-") + 1));

        ((TemplateTab) tabs.get(currentTab)).getGroup(
          groupIdx).removeField(fieldIdx);
      }
      else {
        int fieldIdx = Integer.parseInt(s);
        ((TemplateTab) tabs.get(currentTab)).removeField(fieldIdx);
      }
      updateRemoveLists(); 
    }
    else if (cmd.equals("general options")) {
      // pop up a dialog with general template options 
  
      if (options == null) {
        options = new Hashtable();
        //options.put("font-style", new Option("Font", new JTextField("")));
        //options.put("font-size", new Option("Font size", 
        //  new JSpinner(new SpinnerNumberModel(12, 1, Integer.MAX_VALUE, 1))));
        //options.put("font-color", new Option());
        //options.put("background-color", new Option());
        options.put("default-width", new Option("Window width",
          new JSpinner(new SpinnerNumberModel(800, 1, Integer.MAX_VALUE, 1))));
        options.put("default-height", new Option("Window height",
          new JSpinner(new SpinnerNumberModel(600, 1, Integer.MAX_VALUE, 1))));
        options.put("editable", new Option("Allow editing", 
          new JCheckBox("", true)));
        options.put("prefer-companion", 
          new Option("Prefer metadata companion file", 
          new JCheckBox("", true)));
        //options.put("edit-template-fields", new Options());
        //options.put("edit-mapping", new Options());
        options.put("rows", 
          new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1))); 
        options.put("columns", 
          new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1))); 
      } 

      Options window = new Options(this, options);
      options = window.getOptions();
    }
    else if (cmd.startsWith("tab")) {
      // switch which tab we're editing
   
      currentTab = Integer.parseInt(cmd.substring(3));
      template = (JPanel) tabPanels.get(currentTab);
      template.addMouseListener(this); 
      setContentPane(template); 
      
      fieldOptions.removeAll();
      TemplateTab tab = (TemplateTab) tabs.get(currentTab);
      for (int i=0; i<tab.getNumFields(); i++) {
        JMenuItem item = new JMenuItem(tab.getField(i).getName());
        item.setActionCommand("field" + i);
        item.addActionListener(this);
        fieldOptions.add(item);
      } 
      
      pack();
      updateRemoveLists(); 
    }
    else if (cmd.equals("add row")) {
      FormLayout current = 
        (FormLayout) ((JPanel) tabPanels.get(currentTab)).getLayout();
      current.appendRow(new RowSpec("pref:grow")); 
      ((JPanel) tabPanels.get(currentTab)).setLayout(current); 
    }
    else if (cmd.equals("remove row")) {
      FormLayout current = 
        (FormLayout) ((JPanel) tabPanels.get(currentTab)).getLayout();
      current.removeRow(current.getRowCount()); 
      ((JPanel) tabPanels.get(currentTab)).setLayout(current); 
    }
    else if (cmd.equals("add column")) {
      FormLayout current = 
        (FormLayout) ((JPanel) tabPanels.get(currentTab)).getLayout();
      current.appendColumn(new ColumnSpec("pref:grow")); 
      current.appendColumn(new ColumnSpec("pref:grow")); 
      ((JPanel) tabPanels.get(currentTab)).setLayout(current); 
    }
    else if (cmd.equals("remove column")) {
      FormLayout current = 
        (FormLayout) ((JPanel) tabPanels.get(currentTab)).getLayout();
      current.removeColumn(current.getColumnCount()); 
      current.removeColumn(current.getColumnCount()); 
      ((JPanel) tabPanels.get(currentTab)).setLayout(current); 
    }
    else {
      // prompt for name and location 
      FormLayout current = 
        (FormLayout) ((JPanel) tabPanels.get(currentTab)).getLayout(); 
      int maxRow = current.getRowCount();
      int maxCol = current.getColumnCount() / 2;
      
      String[] groupNames = 
        new String[((TemplateTab) tabs.get(currentTab)).getNumGroups() + 1]; 
      for (int i=1; i<=groupNames.length-1; i++) {
        groupNames[i] = 
          ((TemplateTab) tabs.get(currentTab)).getGroup(i - 1).getName();
      }

      Prompter fp = new Prompter(this, groupNames, maxRow, maxCol, FIELD); 
     
      if (fp.wasCanceled()) return; 

      int row = fp.getRow();
      int col = fp.getColumn();
      String name = fp.getName();
   
      int ndx = -1;
      for (int i=0; i<COMPONENT_NAMES.length; i++) {
        if (cmd.equals(COMPONENT_NAMES[i])) {
          ndx = i;
          break;
        }
      }

      // now add this component to the panel 
 
      // TODO : need to handle group layouts 
      try {
        CellConstraints cc = new CellConstraints();
        JPanel pane = (JPanel) tabPanels.get(currentTab);
        pane.add(new JLabel(name), cc.xy(col*2 - 1, row));
        pane.add((JComponent) COMPONENTS[ndx].newInstance(), cc.xy(col*2, row));
        tabPanels.setElementAt(pane, currentTab);
        pack(); 
      }
      catch (Exception exc) {
        exc.printStackTrace();
      }

      // add this field to the current tab 
      TemplateField field = new TemplateField();
      field.setName(name);
      field.setRow(row);
      field.setColumn(col);
      field.setType(COMPONENT_TYPES[ndx]);
      field.setMap(fp.getMap()); 

      String group = fp.getGroup();
      if (group.length() == 0) {
        ((TemplateTab) tabs.get(currentTab)).addField(field);
      }
      else {
        TemplateTab thisTab = (TemplateTab) tabs.get(currentTab);
        for (int i=0; i<thisTab.getNumGroups(); i++) {
          if (thisTab.getGroup(i).getName().equals(group)) {
            thisTab.getGroup(i).addField(field);  
            break; 
          }
        } 
      }

      // add this field to the menu
      JMenuItem menuItem = new JMenuItem(name);
      menuItem.setActionCommand("field" + fieldOptions.getItemCount());
      menuItem.addActionListener(this);
      fieldOptions.add(menuItem);
      pack();
      updateRemoveLists(); 
    }
  
  }
 
  // -- MouseListener API methods --

  public void mouseClicked(MouseEvent e) { }

  public void mouseEntered(MouseEvent e) { }

  public void mouseExited(MouseEvent e) { }

  public void mousePressed(MouseEvent e) {
    int button = e.getButton();
    if (button == MouseEvent.BUTTON3) {
      if (e.isPopupTrigger()) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  public void mouseReleased(MouseEvent e) {
    int button = e.getButton();
    if (button == MouseEvent.BUTTON3) {
      if (e.isPopupTrigger()) {
        popup.setVisible(false);
      }
    }
  }

  // -- Helper classes --

  /** Prompts for necessary information about a tab, group or field. */
  public class Prompter extends JDialog implements ActionListener {
  
    // -- Static fields --

    private Hashtable omexmlMaps = buildMappings();

    Hashtable buildMappings() {
      InputStream stream = 
        TemplateEditor.class.getResourceAsStream("mapping.txt");  
      BufferedReader b = new BufferedReader(new InputStreamReader(stream)); 
    
      Hashtable h = new Hashtable();
      
      try {
        String line = b.readLine().trim();
        while (line != null) {
          String key = line.substring(1, line.indexOf("\"", 1));
          line = line.substring(line.indexOf("\"", 1) + 1).trim();
          String value = line.substring(1, line.indexOf("\"", 1));
          h.put(key, value);

          line = b.readLine();
        }
        b.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      return h;
    };

    // -- Fields --

    private JTextField name;
    private JSpinner row, col;
    private JComboBox omexml;
    private JComboBox group; 
    private JSpinner groupSpinner; 
    private boolean canceled = false;

    // -- Constructor --
  
    public Prompter(JFrame owner, String[] groups, int rowMax, int colMax, 
      int type) 
    {
      super(owner, "New " + (type == TemplateEditor.FIELD ? "field" : 
        type == TemplateEditor.TAB ? "tab" : "group") + "...", true); 

      FormLayout layout =
        new FormLayout("2dlu, pref:grow, 2dlu, pref:grow, 2dlu",
        "pref:grow, pref:grow, pref:grow, pref:grow, pref:grow, pref:grow");
      JPanel pane = new JPanel(layout);
      CellConstraints cc = new CellConstraints();

      name = new JTextField();

      String n = type == TemplateEditor.TAB ? "Tab" : 
        type == TemplateEditor.GROUP ? "Group" : "Field";

      pane.add(new JLabel(n + " name:"), cc.xy(2, 1));
      pane.add(name, cc.xy(4, 1)); 
      
      if (type != TemplateEditor.GROUP) { 
        boolean field = type == TemplateEditor.FIELD; 
        
        row = new JSpinner(new SpinnerNumberModel(1, 1, rowMax, 1));
        col = new JSpinner(new SpinnerNumberModel(1, 1, colMax, 1));
        pane.add(new JLabel(field ? "Row:" : "Rows:"), cc.xy(2, 2));
        pane.add(new JLabel(field ? "Column:" : "Columns:"), cc.xy(2, 3));
        pane.add(row, cc.xy(4, 2));
        pane.add(col, cc.xy(4, 3));
      } 
      
      if (type == TemplateEditor.FIELD) {
        pane.add(new JLabel("OME-XML mapping:"), cc.xy(2, 5)); 
        pane.add(new JLabel("Group:"), cc.xy(2, 4));
        group = new JComboBox(groups);
        pane.add(group, cc.xy(4, 4));

        omexml = new JComboBox(omexmlMaps.keySet().toArray());
        pane.add(omexml, cc.xy(4, 5)); 
      } 
     
      if (type == TemplateEditor.GROUP) {
        groupSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 
          Integer.MAX_VALUE, 1));
        pane.add(new JLabel("Repetitions:"), cc.xy(2, 2));
        pane.add(groupSpinner, cc.xy(4, 2));
      }

      JButton ok = new JButton("OK");
      ok.setActionCommand("ok");
      ok.addActionListener(this);
      pane.add(ok, cc.xy(2, 6)); 

      JButton cancel = new JButton("Cancel");
      cancel.setActionCommand("cancel");
      cancel.addActionListener(this);
      pane.add(cancel, cc.xy(4, 6));

      setContentPane(pane);  
      setPreferredSize(new Dimension(325, 130));
      pack();
      setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
      setVisible(true); 
    }

    // -- Prompter API methods --

    public int getRow() { return ((Integer) row.getValue()).intValue(); }

    public int getColumn() { return ((Integer) col.getValue()).intValue(); }

    public String getName() { return name.getText(); }

    public String getMap() { 
      return (String) omexmlMaps.get(omexml.getSelectedItem()); 
    }

    public String getGroup() { return (String) group.getSelectedItem(); }

    public int getRepetitions() { 
      return ((Integer) groupSpinner.getValue()).intValue(); 
    }

    public boolean wasCanceled() { return canceled; }

    // -- ActionListener API methods --

    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("ok")) dispose();
      else if (e.getActionCommand().equals("mapping")) {
      }
      else if (e.getActionCommand().equals("cancel")) {
        canceled = true;
        dispose(); 
      }
    }
  }

  /** Displays a list of general template options. */
  public class Options extends JDialog implements ActionListener {

    // -- Fields --

    private Hashtable options;

    // -- Constructor --

    public Options(JFrame parent, Hashtable options) {
      super(parent, "General Template Options", true);
      this.options = options;
  
      String rowString = "2dlu";
      for (int i=0; i<=options.size(); i++) {
        rowString += ", pref:grow, 2dlu";
      }

      FormLayout layout = 
        new FormLayout("2dlu, pref:grow, 2dlu, pref:grow, 2dlu", rowString);
      JPanel panel = new JPanel(layout);

      CellConstraints cc = new CellConstraints();
      Object[] keys = options.keySet().toArray();
      Arrays.sort(keys);

      for (int i=0; i<keys.length; i++) {
        Option o = (Option) options.get(keys[i]);
        panel.add(new JLabel(o.label), cc.xy(2, 2*(i + 1)));
        panel.add(o.component, cc.xy(4, 2*(i + 1))); 
      }

      JButton ok = new JButton("OK");
      ok.setActionCommand("ok");
      ok.addActionListener(this);
      panel.add(ok, cc.xyw(2, 2*(keys.length + 1), 3));

      setContentPane(panel);
      setPreferredSize(new Dimension(400, 400));
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      pack(); 
      setVisible(true);
    }

    // -- Options API methods --

    public Hashtable getOptions() { return options; }

    // -- ActionListener API methods --

    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("ok")) {
        Component[] components = getContentPane().getComponents();
        for (int i=0; i<components.length; i++) { 
          Component src = components[i]; 

          Option o = null;
          String cmd = null;

          Object[] keys = options.keySet().toArray();
          for (int j=0; j<keys.length; j++) {
            if (((Option) options.get(keys[j])).component.equals(src)) {
              o = (Option) options.get(keys[j]);
              cmd = (String) keys[j]; 
              break;
            }
          }
          
          if (src instanceof JTextField) {
            o.value = ((JTextField) src).getText();
            options.put(cmd, o); 
          }
          else if (src instanceof JSpinner) {
            o.value = ((JSpinner) src).getValue().toString();
            options.put(cmd, o);
          }
          else if (src instanceof JComboBox) {
            o.value = ((JComboBox) src).getSelectedItem().toString();
            options.put(cmd, o);
          }
          else if (src instanceof JCheckBox) {
            o.value = "" + ((JCheckBox) src).isSelected();
            options.put(cmd, o);
          }
        } 
        dispose(); 
      }
    }

  }

  public class Option {
    public String label;
    public String value;
    public JComponent component;
  
    public Option(String label, JComponent component) {
      this.label = label;
      this.component = component;
    }
  }

  // -- Main method --

  public static void main(String[] args) {
    new TemplateEditor();
  }
}

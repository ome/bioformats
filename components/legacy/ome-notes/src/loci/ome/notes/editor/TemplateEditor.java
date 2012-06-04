/*
 * #%L
 * OME Notes library for flexible organization and presentation of OME-XML
 * metadata.
 * %%
 * Copyright (C) 2007 - 2012 Open Microscopy Environment:
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

package loci.ome.notes.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
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
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import loci.formats.FormatException;
import loci.formats.gui.BufferedImageReader;
import loci.ome.notes.Template;
import loci.ome.notes.TemplateField;
import loci.ome.notes.TemplateTab;
import loci.ome.notes.TemplateTools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Main class for template editor.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-notes/src/loci/ome/notes/editor/TemplateEditor.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-notes/src/loci/ome/notes/editor/TemplateEditor.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class TemplateEditor extends JFrame implements ActionListener {

  // -- Constants --

  /** List of component names. */
  protected static final String[] COMPONENT_NAMES = new String[] {
    "Text field", "Number field", "Choice box", "Check box", "Image thumbnail"
  };

  /** List of component classes. */
  protected static final Class[] COMPONENTS = new Class[] {
    JTextField.class, JSpinner.class, JComboBox.class, JCheckBox.class,
    JLabel.class
  };

  /** List of component types. */
  protected static final String[] COMPONENT_TYPES = new String[] {
    "var", "int", "enum", "bool", "thumbnail"
  };

  public static final int TAB = 0, GROUP = 1, FIELD = 2;

  // -- Fields --

  /** Main editing panel. */
  private JTabbedPane tabPane;

  /** Panel containing drag-and-drop components. */
  private JPanel componentPane;

  /** Current file name. */
  private String currentFile;

  private JTextField newTabName;

  private PictureTransferHandler pictureHandler;

  private int lastMenuX = 0;
  private int lastMenuY = 0;
  private JComponent lastMenuComponent = null;

  private Hashtable[] icons;
  private Hashtable[] fields;

  // -- Constructor --

  public TemplateEditor() {
    super("OME Notes Template Editor");

    pictureHandler = new PictureTransferHandler();
    icons = new Hashtable[0];
    fields = new Hashtable[0];

    // set up the menu bar

    JMenuBar menubar = new JMenuBar();

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

    JMenuItem quit = new JMenuItem("Quit");
    quit.setActionCommand("quit");
    quit.addActionListener(this);
    file.add(quit);

    menubar.add(file);

    setJMenuBar(menubar);

    // set up the toolbar

    JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);

    JButton tab = new JButton("New Tab");
    tab.addActionListener(this);
    tab.setActionCommand("prompt tab");

    JButton row = new JButton("Add Row");
    row.addActionListener(this);
    row.setActionCommand("add row");

    JButton col = new JButton("Add Column");
    col.addActionListener(this);
    col.setActionCommand("add col");

    JButton remove = new JButton("Remove this tab");
    remove.addActionListener(this);
    remove.setActionCommand("removeTab");

    toolbar.add(tab);
    toolbar.add(row);
    toolbar.add(col);
    toolbar.add(remove);

    // set up the rest of the window

    CellConstraints cc = new CellConstraints();

    FormLayout layout =
      new FormLayout("pref:grow,pref,pref:grow,pref:grow,pref:grow",
      "pref,5dlu:grow,pref:grow,pref:grow,pref:grow");

    JPanel contentPane = new JPanel(layout);

    contentPane.add(toolbar, cc.xywh(1, 1, 5, 1));

    tabPane = new JTabbedPane();
    contentPane.add(tabPane, cc.xywh(3, 2, 3, 4));

    FormLayout componentLayout = new FormLayout("pref:grow,pref:grow",
      "pref:grow,pref:grow,pref:grow,pref:grow,pref:grow,pref:grow");
    componentPane = new JPanel(componentLayout);

    componentPane.add(new JLabel("Field Choices"), cc.xywh(1, 1, 2, 1));

    try {
      for (int i=0; i<COMPONENTS.length; i++) {
        JLabel label = new JLabel(COMPONENT_NAMES[i]);
        componentPane.add(label, cc.xy(1, i + 2));

        JPanel panel = new JPanel();
        JComponent component = (JComponent) COMPONENTS[i].newInstance();
        component.setPreferredSize(new Dimension(64, 25));
        component.setEnabled(false);
        panel.add(component);

        DraggableIcon img = new DraggableIcon(panel, -1, -1);
        img.setTransferHandler(pictureHandler);
        img.setEditable(false);

        componentPane.add(img, cc.xy(2, i + 2));
      }
    }
    catch (Exception e) {
      error("Failed to create field list", e);
    }

    contentPane.add(componentPane, cc.xywh(1, 2, 1, 4));

    setContentPane(contentPane);
    setSize(new Dimension(768, 768));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);
  }

  // -- TemplateEditor API methods --

  // -- ActionListener API method --

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    if (cmd.equals("new")) {
      icons = new Hashtable[0];
      fields = new Hashtable[0];

      tabPane.removeAll();
      repaint();
    }
    else if (cmd.equals("open")) {
      JFileChooser chooser = new JFileChooser();
      FileFilter filter = new FileFilter() {
        public boolean accept(File f) {
          return f.getAbsolutePath().endsWith(".template") || f.isDirectory();
        }
        public String getDescription() { return "OME Notes Templates"; }
      };

      chooser.setFileFilter(filter);

      int status = chooser.showOpenDialog(this);
      if (status == JFileChooser.APPROVE_OPTION) {
        String file = chooser.getSelectedFile().getAbsolutePath();
        try {
          Template t = new Template(file);

          TemplateTab[] tabs = t.getTabs();
          for (int i=0; i<tabs.length; i++) {
            int rows = tabs[i].getRows();
            int cols = tabs[i].getColumns();
            if (cols == 0) cols = 1;
            if (rows == 0) rows = 1;

            addTab(tabs[i].getName(), rows, cols);
            tabPane.setSelectedIndex(i);

            for (int j=0; j<tabs[i].getNumFields(); j++) {
              TemplateField f = tabs[i].getField(j);

              int x = f.getRow();
              int y = f.getColumn();
              if (x == -1) x = 1;
              if (y == -1) y = j + 1;

              Point p = new Point(x, y);
              DraggableIcon icon = (DraggableIcon) icons[i].get(p);

              icon.label = new JLabel(f.getName());

              JPanel panel = new JPanel();
              panel.add(f.getComponent());

              icon.setPanel(panel);
            }
          }
        }
        catch (Exception exc) {
          error("Failed to parse template", exc);
        }

        tabPane.setSelectedIndex(0);
      }
    }
    else if (cmd.equals("save")) {
      // build up the template from the components

      TemplateTab[] tabs = new TemplateTab[tabPane.getTabCount()];

      for (int i=0; i<tabs.length; i++) {
        tabs[i] = new TemplateTab();
        tabs[i].setName(tabPane.getTitleAt(i));
        JComponent c = (JComponent) tabPane.getComponentAt(i);
        FormLayout layout = (FormLayout) c.getLayout();

        tabs[i].setRows(layout.getRowCount());
        tabs[i].setColumns(layout.getColumnCount());

        Object[] keys = icons[i].keySet().toArray();

        for (int j=0; j<keys.length; j++) {
          Point p = (Point) keys[j];
          DraggableIcon icon = (DraggableIcon) icons[i].get(p);
          TemplateField f = (TemplateField) fields[i].get(p);

          if (icon.image != null) {
            Component[] components = icon.image.getComponents();
            JLabel label = icon.label;
            JComponent component = (JComponent) components[0];

            f.setComponent(component);

            for (int k=0; k<COMPONENTS.length; k++) {
              if (component.getClass().equals(COMPONENTS[k])) {
                f.setType(COMPONENT_TYPES[k]);
                break;
              }
            }

            f.setRow(p.y);
            f.setColumn(p.x);
            f.setDefaultValue(TemplateTools.getComponentValue(component));

            tabs[i].addField(f);
          }
        }
      }

      Template t = new Template(tabs, null);

      // prompt for filename to save to
      if (currentFile == null) {
        JFileChooser chooser = new JFileChooser();

        FileFilter filter = new FileFilter() {
          public boolean accept(File f) { return true; }
          public String getDescription() { return "All files"; }
        };

        chooser.setFileFilter(filter);

        int status = chooser.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
          currentFile = chooser.getSelectedFile().getAbsolutePath();
          if (currentFile == null) return;
        }
      }

      try {
        t.save(currentFile);
      }
      catch (IOException io) {
        error("Failed to save template", io);
      }
    }
    else if (cmd.equals("quit")) dispose();
    else if (cmd.equals("add row")) addRow();
    else if (cmd.equals("add col")) addColumn();
    else if (cmd.equals("prompt tab")) {
      // prompt for tab name
      JPopupMenu menu = new JPopupMenu();
      newTabName = new JTextField();
      newTabName.setPreferredSize(new Dimension(200, 25));
      menu.add(newTabName);
      JButton b = new JButton("OK");
      b.addActionListener(this);
      b.setActionCommand("new tab");
      menu.add(b);

      JComponent s = (JComponent) e.getSource();
      menu.show(s, s.getX(), s.getY());
      newTabName.grabFocus();
    }
    else if (cmd.equals("new tab")) {
      newTabName.getParent().setVisible(false);
      addTab(newTabName.getText(), 2, 2);
    }
    else if (cmd.equals("setName")) {
      JPopupMenu menu = (JPopupMenu) ((JComponent) e.getSource()).getParent();
      DraggableIcon icon = (DraggableIcon) menu.getInvoker();
      menu.setVisible(false);

      String text = ((JTextField) menu.getComponents()[0]).getText();

      Point p = new Point(icon.gridx, icon.gridy);
      int ndx = tabPane.getSelectedIndex();
      TemplateField f = (TemplateField) fields[ndx].get(p);
      f.setName(text);
      f.setNameMap(null);

      // set the name
      if (icon.label != null) icon.remove(icon.label);
      icon.remove(icon.image);
      icon.label = new JLabel(text);
      icon.add(icon.label);
      icon.add(icon.image);
      icon.getParent().repaint();
    }
    else if (cmd.equals("changeName")) {
      // prompt for new field name
      JPopupMenu menu = new JPopupMenu();
      JTextField field = new JTextField();
      field.setPreferredSize(new Dimension(200, 25));
      menu.add(field);
      JButton b = new JButton("OK");
      b.addActionListener(this);
      b.setActionCommand("setName");
      menu.add(b);
      menu.show(lastMenuComponent, lastMenuX, lastMenuY);
      field.grabFocus();
    }
    else if (cmd.equals("nameMap")) {
      JPopupMenu menu = (JPopupMenu) ((JComponent) e.getSource()).getParent();
      DraggableIcon icon = (DraggableIcon) menu.getInvoker();
      menu.setVisible(false);

      MappingWindow w = new MappingWindow(this, true);
      w.show(lastMenuComponent, lastMenuX, lastMenuY);
    }
    else if (cmd.equals("map")) {
      JPopupMenu menu = (JPopupMenu) ((JComponent) e.getSource()).getParent();
      DraggableIcon icon = (DraggableIcon) menu.getInvoker();
      menu.setVisible(false);

      MappingWindow w = new MappingWindow(this, false);
      w.show(lastMenuComponent, lastMenuX, lastMenuY);
    }
    else if (cmd.equals("repeat")) {
      JMenuItem item = (JMenuItem) e.getSource();
      DraggableIcon icon =
        (DraggableIcon) ((JPopupMenu) item.getParent()).getInvoker();
      TemplateField f = getField(icon);

      if (item.getText().equals("Repeat this field")) {
        item.setText("Don't repeat this field");
        f.setRepeated(true);
      }
      else {
        item.setText("Repeat this field");
        f.setRepeated(false);
      }
    }
    else if (cmd.equals("removeField")) {
      JPopupMenu menu = (JPopupMenu) ((JComponent) e.getSource()).getParent();
      DraggableIcon icon = (DraggableIcon) menu.getInvoker();
      menu.setVisible(false);

      int idx = tabPane.getSelectedIndex();
      Object[] keys = icons[idx].keySet().toArray();
      for (int i=0; i<keys.length; i++) {
        if (icons[idx].get(keys[i]).equals(icon)) {
          icons[idx].remove(keys[i]);
          fields[idx].remove(keys[i]);
          break;
        }
      }

      icon.remove(icon.label);
      icon.remove(icon.image);
      tabPane.repaint();
    }
    else if (cmd.startsWith("removeRow")) {
      int row = Integer.parseInt(cmd.substring(cmd.indexOf(":") + 1));
      JPopupMenu menu = (JPopupMenu) ((JComponent) e.getSource()).getParent();
      menu.setVisible(false);

      JPanel pane = (JPanel) tabPane.getSelectedComponent();
      FormLayout layout = (FormLayout) pane.getLayout();

      int rows = layout.getRowCount();
      int cols = layout.getColumnCount();

      int idx = tabPane.getSelectedIndex();

      for (int i=0; i<cols; i++) {
        pane.remove((JComponent) icons[idx].get(new Point(i + 1, row + 1)));
      }

      rekey(row, -1);
      layout.removeRow(row + 1);
      tabPane.repaint();
    }
    else if (cmd.startsWith("removeColumn")) {
      int col = Integer.parseInt(cmd.substring(cmd.indexOf(":") + 1));
      JPopupMenu menu = (JPopupMenu) ((JComponent) e.getSource()).getParent();
      menu.setVisible(false);

      JPanel pane = (JPanel) tabPane.getSelectedComponent();
      FormLayout layout = (FormLayout) pane.getLayout();

      int rows = layout.getRowCount();
      int cols = layout.getColumnCount();
      int idx = tabPane.getSelectedIndex();

      for (int i=0; i<rows; i++) {
        pane.remove((JComponent) icons[idx].get(new Point(col + 1, i + 1)));
      }

      rekey(-1, col);
      layout.removeColumn(col + 1);
      tabPane.repaint();
    }
    else if (cmd.equals("removeTab")) {
      int ndx = tabPane.getSelectedIndex();
      tabPane.remove(ndx);

      Hashtable[] h = new Hashtable[icons.length - 1];
      Hashtable[] f = new Hashtable[fields.length - 1];

      System.arraycopy(icons, 0, h, 0, ndx);
      System.arraycopy(icons, ndx + 1, h, ndx, h.length - ndx);
      System.arraycopy(fields, 0, f, 0, ndx);
      System.arraycopy(fields, ndx + 1, f, ndx, f.length - ndx);

      icons = h;
      fields = f;
    }
    else if (cmd.equals("specifyChoices")) {
      JMenuItem item = (JMenuItem) e.getSource();
      DraggableIcon icon =
        (DraggableIcon) ((JPopupMenu) item.getParent()).getInvoker();
      TemplateField f = getField(icon);

      EnumWindow w = new EnumWindow(this, f.getEnums());
      w.show(lastMenuComponent, lastMenuX, lastMenuY);
    }
    else if (cmd.equals("setThumbSource")) {
      JPopupMenu menu = new JPopupMenu();
      ButtonGroup g = new ButtonGroup();

      JRadioButton dataset = new JRadioButton("Use thumbnail from dataset");
      dataset.setSelected(true);
      g.add(dataset);

      JRadioButton file = new JRadioButton("Use thumbnail from file:");
      g.add(file);

      menu.add(dataset);
      menu.add(file);

      JTextField field = new JTextField();
      field.setPreferredSize(new Dimension(200, 25));
      menu.add(field);

      JButton b = new JButton("OK");
      b.addActionListener(this);
      b.setActionCommand("applyThumbSource");
      menu.add(b);
      menu.show(lastMenuComponent, lastMenuX, lastMenuY);
    }
    else if (cmd.equals("applyThumbSource")) {
      JPopupMenu menu = (JPopupMenu) ((JComponent) e.getSource()).getParent();
      DraggableIcon icon = (DraggableIcon) menu.getInvoker();

      Component[] c = menu.getComponents();
      JRadioButton dataset = (JRadioButton) c[0];

      String text = null;

      if (!dataset.isSelected()) {
        JTextField t = (JTextField) c[2];
        text = t.getText();
        getField(icon).setValueMap(text);
      }

      menu.setVisible(false);

      if (text != null) {
        try {
          BufferedImageReader reader = new BufferedImageReader();
          reader.setId(text);
          BufferedImage thumb = reader.openThumbImage(0);
          JLabel label = (JLabel) icon.image.getComponents()[0];
          label.setIcon(new ImageIcon(thumb));
          reader.close();
        }
        catch (FormatException exc) {
          error("Failed to open thumbnail (" + text + ")", exc);
        }
        catch (IOException exc) {
          error("Failed to open thumbnail (" + text + ")", exc);
        }
      }
    }
    else if (cmd.equals("ok")) {
      // this event came from an instance of EnumWindow
      JPanel parent = (JPanel) ((JButton) e.getSource()).getParent();
      EnumWindow menu = (EnumWindow) parent.getParent();
      DraggableIcon icon = (DraggableIcon) menu.getInvoker();
      TemplateField f = getField(icon);
      menu.setVisible(false);

      String[] options = menu.getOptions();
      f.setEnums(options);

      JComboBox box = (JComboBox) icon.image.getComponents()[0];
      for (int i=0; i<options.length; i++) box.addItem(options[i]);
      repaint();
    }
    else if (cmd.equals("chooseMapping")) {
      // this event came from an instance of MappingWindow
      JTabbedPane parent = (JTabbedPane) ((JButton) e.getSource()).getParent();
      MappingWindow menu = (MappingWindow) parent.getParent();
      DraggableIcon icon = (DraggableIcon) menu.getInvoker();
      TemplateField f = getField(icon);

      String omexmlMap = null;

      if (menu.nameMap) f.setNameMap(omexmlMap);
      else f.setValueMap(omexmlMap);
      menu.setVisible(false);
    }
  }

  // -- Helper methods --

  /** Get the TemplateField corresponding to the given DraggableIcon. */
  private TemplateField getField(DraggableIcon icon) {
    int idx = tabPane.getSelectedIndex();
    Object[] keys = icons[idx].keySet().toArray();
    for (int i=0; i<keys.length; i++) {
      if (icons[idx].get(keys[i]).equals(icon)) {
        return (TemplateField) fields[idx].get(keys[i]);
      }
    }
    return null;
  }

  /** Update the icon and template hashtables. */
  private void rekey(int row, int col) {
    int idx = tabPane.getSelectedIndex();
    Object[] keys = icons[idx].keySet().toArray();
    Hashtable h = new Hashtable();
    Hashtable f = new Hashtable();

    for (int i=0; i<keys.length; i++) {
      Point p = (Point) keys[i];

      if ((p.x == col + 1 && col != -1) || (p.y == row + 1 && row != -1)) {
        icons[idx].remove(keys[i]);
        fields[idx].remove(keys[i]);
      }
      else if ((p.x > col + 1 && col != -1) || (p.y > row + 1 && row != -1)) {
        int x = col != -1 ? p.x - 1 : p.x;
        int y = col != -1 ? p.y - 1 : p.y;
        Point k = new Point(x, y);
        h.put(k, icons[idx].get(keys[i]));
        f.put(k, fields[idx].get(keys[i]));
      }
    }

    icons[idx] = h;
    fields[idx] = f;
  }

  /** Add a new tab with the given name. */
  private void addTab(String name, int rows, int cols) {
    String rowString = "";
    for (int i=0; i<rows; i++) {
      rowString += "pref:grow";
      if (i < rows - 1) rowString += ",";
    }

    String colString = "";
    for (int i=0; i<cols; i++) {
      colString += "pref:grow";
      if (i < cols - 1) colString += ",";
    }

    FormLayout layout = new FormLayout(colString, rowString);

    Hashtable[] tmp = icons;
    icons = new Hashtable[tmp.length + 1];
    System.arraycopy(tmp, 0, icons, 0, tmp.length);
    icons[icons.length - 1] = new Hashtable();

    tmp = fields;
    fields = new Hashtable[tmp.length + 1];
    System.arraycopy(tmp, 0, fields, 0, tmp.length);
    fields[fields.length - 1] = new Hashtable();

    JPanel panel = new JPanel(layout);

    for (int x=0; x<rows; x++) {
      for (int y=0; y<cols; y++) {
        addIcon(panel, x, y, tabPane.getTabCount());
      }
    }

    tabPane.addTab(name, panel);
  }

  /** Add a new column to the current tab. */
  private void addRow() {
    JPanel selected = (JPanel) tabPane.getSelectedComponent();
    FormLayout layout = (FormLayout) selected.getLayout();
    layout.appendRow(new RowSpec("pref:grow"));

    int ndx = tabPane.getSelectedIndex();

    for (int i=0; i<layout.getColumnCount(); i++) {
      addIcon(selected, layout.getRowCount() - 1, i, ndx);
    }
    tabPane.repaint();
  }

  /** Add a new row to the current tab. */
  private void addColumn() {
    JPanel selected = (JPanel) tabPane.getSelectedComponent();
    FormLayout layout = (FormLayout) selected.getLayout();
    layout.appendColumn(new ColumnSpec("pref:grow"));

    int ndx = tabPane.getSelectedIndex();

    for (int i=0; i<layout.getRowCount(); i++) {
      addIcon(selected, i, layout.getColumnCount() - 1, ndx);
    }
    tabPane.repaint();
  }

  /** Add a field icon at the specified grid coordinates. */
  private void addIcon(JPanel p, int x, int y, int tab) {
    CellConstraints cc = new CellConstraints();
    DraggableIcon dummy = new DraggableIcon(new JPanel(), y + 1, x + 1);
    dummy.setTransferHandler(pictureHandler);
    dummy.setPreferredSize(new Dimension(128, 128));

    JPopupMenu menu = new JPopupMenu();
    JMenuItem item = new JMenuItem("Change field name");
    item.addActionListener(this);
    item.setActionCommand("changeName");
    menu.add(item);

    item = new JMenuItem("Change name mapping");
    item.addActionListener(this);
    item.setActionCommand("nameMap");
    menu.add(item);

    item = new JMenuItem("Change OME-XML mapping");
    item.addActionListener(this);
    item.setActionCommand("map");
    menu.add(item);

    item = new JMenuItem("Repeat this field");
    item.addActionListener(this);
    item.setActionCommand("repeat");
    menu.add(item);

    menu.add(new JSeparator());

    item = new JMenuItem("Remove this field");
    item.addActionListener(this);
    item.setActionCommand("removeField");
    menu.add(item);

    item = new JMenuItem("Remove this row");
    item.addActionListener(this);
    item.setActionCommand("removeRow:" + x);
    menu.add(item);

    item = new JMenuItem("Remove this column");
    item.addActionListener(this);
    item.setActionCommand("removeColumn:" + y);
    menu.add(item);

    dummy.menu = menu;
    dummy.listener = this;
    MouseListener popupListener = new PopupListener(menu);
    dummy.addMouseListener(popupListener);

    p.add(dummy, cc.xy(y + 1, x + 1));
    icons[tab].put(new Point(y + 1, x + 1), dummy);
    fields[tab].put(new Point(y + 1, x + 1), new TemplateField());
  }

  /** Display an error message. */
  protected void error(String msg, Exception exc) {
    if (exc != null) {
      StackTraceElement[] n = exc.getStackTrace();
      for (int i=0; i<n.length; i++) {
        msg += "\n" + n[i];
      }
    }
    JOptionPane.showMessageDialog(this, msg, "Error!",
      JOptionPane.ERROR_MESSAGE);
  }

  // -- Helper classes --

  class PopupListener extends MouseAdapter {
    JPopupMenu popup;

    PopupListener(JPopupMenu popupMenu) {
      popup = popupMenu;
    }

    public void mousePressed(MouseEvent e) { maybeShowPopup(e); }

    public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }

    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        lastMenuComponent = (JComponent) e.getComponent();
        lastMenuX = e.getX();
        lastMenuY = e.getY();
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }

  }

  // -- Main method --

  public static void main(String[] args) {
    new TemplateEditor();
  }

}

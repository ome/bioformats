//
// ConfigWindow.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson and Philip Huettl.

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

package loci.plugins.config;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A window for managing configuration of the LOCI plugins.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/config/ConfigWindow.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/config/ConfigWindow.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ConfigWindow extends JFrame
  implements ItemListener, ListSelectionListener
{

  // -- Fields --

  private JList formatsList;
  private JPanel formatInfo;
  private JTextField extensions;
  private JCheckBox enabled;

  private JList libsList;
  private JTextField type, status, version, path, url, license;
  private JTextArea notes;

  // -- Constructor --

  public ConfigWindow() {
    setTitle("LOCI Plugins Configuration");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // generate list of formats
    FormatEntry[] formats = null;
    try {
      Class irClass = Class.forName("loci.formats.ImageReader");
      Object ir = irClass.newInstance();
      Method getClasses = irClass.getMethod("getReaders", null);
      Object[] readers = (Object[]) getClasses.invoke(ir, null);
      formats = new FormatEntry[readers.length];
      for (int i=0; i<readers.length; i++) {
        formats[i] = new FormatEntry(readers[i]);
      }
      Arrays.sort(formats);
    }
    catch (Throwable t) {
      t.printStackTrace();
      formats = new FormatEntry[0];
    }

    // enumerate list of libraries
    final String libCore = "Core library";
    final String libNative = "Native library";
    final String libPlugin = "ImageJ plugin";
    final String libJava = "Java library";

    String javaVersion = System.getProperty("java.version") +
      " (" + System.getProperty("java.vendor") + ")";

    String bfVersion = "@date@";
    // Ant replaces date token with datestamp of the build
    if (bfVersion.equals("@" + "date" + "@")) bfVersion = "Internal build";

    String qtVersion = null;
    try {
      Class qtToolsClass = Class.forName("loci.formats.LegacyQTTools");
      Object qtTools = qtToolsClass.newInstance();
      Method getQTVersion = qtToolsClass.getMethod("getQTVersion", null);
      qtVersion = (String) getQTVersion.invoke(qtTools, null);
    }
    catch (Throwable t) { }

    String matlabVersion = null;
    try {
      Class matlabClass = Class.forName("com.mathworks.jmi.Matlab");
      Object matlab = matlabClass.newInstance();
      Method eval = matlabClass.getMethod("eval", new Class[] {String.class});
      String ans = (String) eval.invoke(matlab, new Object[] {"version"});
      if (ans.startsWith("ans =")) ans = ans.substring(5);
      matlabVersion = ans.trim();
    }
    catch (Throwable t) { }

    Hashtable versions = new Hashtable();
    if (javaVersion != null) versions.put("javaVersion", javaVersion);
    if (bfVersion != null) versions.put("bfVersion", bfVersion);
    if (qtVersion != null) versions.put("qtVersion", qtVersion);
    if (matlabVersion != null) versions.put("matlabVersion", matlabVersion);

    LibraryEntry[] libraries = parseLibraries("libraries.txt", versions);
    Arrays.sort(libraries);

    // build UI

    JTabbedPane tabs = new JTabbedPane();
    tabs.setBorder(new EmptyBorder(3, 3, 3, 3));
    setContentPane(tabs);

    JPanel optionsPanel = new JPanel();
    tabs.addTab("Options", optionsPanel);

    formatsList = makeList(formats);

    formatInfo = new JPanel();
    tabs.addTab("Formats", makeSplitPane(formatsList, formatInfo));

    extensions = makeTextField();
    enabled = new JCheckBox("", false);
    enabled.addItemListener(this);

    doFormatLayout(null);

    // TODO - reader-specific options
    // - QT: "Use QuickTime for Java" checkbox (default off)
    // - ND2: "Use Nikon's ND2 plugin" checkbox (default off)
    // - Flex: "LuraWave license code" label and text field (default nothing)
    //   + if sys prop set, but ij prop not set, GRAY OUT AND DO NOT SHOW
    // - SDT: "Merge lifetime bins to intensity" checkbox

    libsList = makeList(libraries);
    JPanel libInfo = new JPanel();
    tabs.addTab("Libraries", makeSplitPane(libsList, libInfo));

    libInfo.setLayout(new SpringLayout());

    libInfo.add(makeLabel("Type", false));
    type = makeTextField();
    libInfo.add(type);

    libInfo.add(makeLabel("Status", false));
    status = makeTextField();
    libInfo.add(status);

    libInfo.add(makeLabel("Version", false));
    version = makeTextField();
    libInfo.add(version);

    // TODO - install/upgrade button, if applicable
    // - can upgrade any JAR from LOCI repository
    //   + upgrade button for "ImageJ" just launches ImageJ upgrade plugin
    // - can install native libs by downloading installer from its web site
    //   + QuickTime for Java
    //   + Nikon ND2 plugin
    //   + ImageIO Tools

    libInfo.add(makeLabel("Path", false));
    path = makeTextField();
    libInfo.add(path);

    libInfo.add(makeLabel("URL", false));
    url = makeTextField();
    libInfo.add(url);

    libInfo.add(makeLabel("License", false));
    license = makeTextField();
    libInfo.add(license);

    libInfo.add(makeLabel("Notes", true));
    notes = new JTextArea();
    notes.setEditable(false);
    notes.setWrapStyleWord(true);
    notes.setLineWrap(true);
    libInfo.add(new JScrollPane(notes));

    // TODO - "How to install" for each library?

    SpringUtilities.makeCompactGrid(libInfo, 7, 2, 3, 3, 3, 3);

    tabs.setSelectedIndex(1);
    pack();
  }

  // -- ItemListener API methods --

  public void itemStateChanged(ItemEvent e) {
    FormatEntry entry = (FormatEntry) formatsList.getSelectedValue();
    setReaderEnabled(entry, enabled.isSelected());
  }

  // -- ListSelectionListener API methods --

  public void valueChanged(ListSelectionEvent e) {
    Object src = e.getSource();
    if (src == formatsList) {
      FormatEntry entry = (FormatEntry) formatsList.getSelectedValue();
      doFormatLayout(entry);
    }
    else if (src == libsList) {
      LibraryEntry entry = (LibraryEntry) libsList.getSelectedValue();
      type.setText(entry.type);
      status.setText(entry.status);
      version.setText(entry.version);
      path.setText(entry.path);
      url.setText(entry.url);
      license.setText(entry.license);
      notes.setText(entry.notes);
    }
  }

  // -- Utility methods --

  public static JTextField makeTextField() {
    JTextField textField = new JTextField(38);
    int prefHeight = textField.getPreferredSize().height;
    textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, prefHeight));
    textField.setEditable(false);
    return textField;
  }

  // -- Helper methods --

  private static LibraryEntry[] parseLibraries(String resource,
    Hashtable versions)
  {
    Vector list = new Vector();
    Hashtable props = null;
    String propKey = null;
    StringBuffer propValue = new StringBuffer();
    BufferedReader in = new BufferedReader(new InputStreamReader(
      ConfigWindow.class.getResourceAsStream(resource)));
    while (true) {
      String line = null;
      try {
        line = in.readLine();
      }
      catch (IOException exc) {
        break;
      }
      if (line == null) break;

      // ignore characters following # sign (comments)
      int ndx = line.indexOf("#");
      if (ndx >= 0) line = line.substring(0, ndx);
      boolean space = line.startsWith(" ");
      line = line.trim();
      if (line.equals("")) continue;

      // parse key/value pairs
      int equals = line.indexOf("=");
      if (line.startsWith("[")) {
        // new entry
        if (props == null) props = new Hashtable();
        else {
          addProp(props, propKey, propValue.toString(), versions);
          list.add(new LibraryEntry(props));
        }
        props.clear();
        props.put("name", line.substring(1, line.length() - 1));
        propKey = null;
      }
      else if (space) {
        // append to previous property value
        propValue.append(" ");
        propValue.append(line);
      }
      else if (equals >= 0) {
        addProp(props, propKey, propValue.toString(), versions);
        propKey = line.substring(0, equals - 1).trim();
        propValue.setLength(0);
        propValue.append(line.substring(equals + 1).trim());
      }
    }
    try {
      in.close();
    }
    catch (IOException exc) { }
    LibraryEntry[] entries = new LibraryEntry[list.size()];
    list.copyInto(entries);
    return entries;
  }

  private static void addProp(Hashtable props,
    String key, String value, Hashtable versions)
  {
    if (key == null) return;

    // replace \n sequence with newlines
    value = value.replaceAll("\\\\n *", "\n");

    if (key.equals("version")) {
      // get actual value from versions hashtable
      value = (String) versions.get(value);
    }
    if (value != null) props.put(key, value);
  }

  private JLabel makeLabel(String text, boolean top) {
    JLabel label = new JLabel(text);
    label.setHorizontalAlignment(SwingConstants.RIGHT);
    if (top) label.setVerticalAlignment(SwingConstants.TOP);
    return label;
  }

  private JList makeList(Object[] data) {
    JList list = new JList(data);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setVisibleRowCount(25);
    list.addListSelectionListener(this);
    return list;
  }

  private JSplitPane makeSplitPane(JList list, JPanel info) {
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
      new JScrollPane(list), info);
    splitPane.setDividerLocation(260);
    return splitPane;
  }

  private void doFormatLayout(FormatEntry entry) {
    if (entry != null) {
      // build list of extensions
      StringBuffer sb = new StringBuffer();
      for (int i=0; i<entry.suffixes.length; i++) {
        if (i > 0) sb.append(", ");
        sb.append(entry.suffixes[i]);
      }
      extensions.setText(sb.toString());
    }

    enabled.setSelected(isReaderEnabled(entry));

    formatInfo.removeAll();
    formatInfo.setLayout(new SpringLayout());

    formatInfo.add(makeLabel("Extensions", false));
    formatInfo.add(extensions);

    formatInfo.add(makeLabel("Enabled", false));
    formatInfo.add(enabled);

    // format-specific widgets
    int rows = entry == null ? 0 : entry.widgets.length;
    for (int i=0; i<rows; i++) {
      formatInfo.add(makeLabel(entry.labels[i], false));
      formatInfo.add(entry.widgets[i]);
    }

    SpringUtilities.makeCompactGrid(formatInfo, 2 + rows, 2, 3, 3, 3, 3);

    formatInfo.validate();
    formatInfo.repaint();
  }

  private boolean isReaderEnabled(FormatEntry entry) {
    if (entry == null) return false;
    try {
      Class importerClass = Class.forName("loci.plugins.Importer");
      Field field = importerClass.getField("READER_ENABLED_PROPERTY");
      String key = field.get(null) + "." + entry.readerName;
      Class prefsClass = Class.forName("ij.Prefs");
      Method get = prefsClass.getMethod("get",
        new Class[] {String.class, boolean.class});
      Boolean on = (Boolean) get.invoke(null,
        new Object[] {key, Boolean.TRUE});
      return on.booleanValue();
    }
    catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }

  private void setReaderEnabled(FormatEntry entry, boolean on) {
    if (entry == null) return;
    try {
      Class importerClass = Class.forName("loci.plugins.Importer");
      Field field = importerClass.getField("READER_ENABLED_PROPERTY");
      String key = field.get(null) + "." + entry.readerName;
      Class prefsClass = Class.forName("ij.Prefs");
      Method set = prefsClass.getMethod("set",
        new Class[] {String.class, boolean.class});
      set.invoke(null, new Object[] {key, new Boolean(on)});
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }

}

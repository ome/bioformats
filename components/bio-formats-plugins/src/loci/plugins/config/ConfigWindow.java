/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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

package loci.plugins.config;

import ij.Prefs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

import javax.imageio.spi.IIORegistry;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import loci.common.Constants;
import loci.common.DateTools;
import loci.formats.FormatTools;
import loci.plugins.util.WindowTools;

/**
 * A window for managing configuration of the Bio-Formats plugins.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/src/loci/plugins/config/ConfigWindow.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/src/loci/plugins/config/ConfigWindow.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ConfigWindow extends JFrame
  implements ActionListener, ItemListener, ListSelectionListener, Runnable
{

  // -- Constants --

  private static final int PAD = 5;
  private static final String UPGRADE_CHECK_KEY = "bioformats.upgradeCheck";

  // -- Fields --

  private DefaultListModel formatsListModel;
  private JList formatsList;
  private JPanel formatInfo;
  private JTextField extensions;
  private JCheckBox enabledBox, windowlessBox, upgradeBox;

  private DefaultListModel libsListModel;
  private JList libsList;
  private JTextField type, status, version, path, url, license;
  private JTextArea notes;

  private PrintWriter log;

  // -- Constructor --

  public ConfigWindow() {
    setTitle("Bio-Formats Plugins Configuration");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // build UI

    final String loading = "Loading...";

    JTabbedPane tabs = new JTabbedPane();
    tabs.setBorder(new EmptyBorder(PAD, PAD, PAD, PAD));
    setContentPane(tabs);

    JPanel installPanel = new JPanel();
    //tabs.addTab("Install", installPanel);

    JButton installButton = new JButton(
      "<html><center><br><font size=\"+1\">Check my system,<br>" +
      "install missing libraries,<br>" +
      "and upgrade old files</font><br>" +
      "<font size=\"-1\">&nbsp;<br>" +
      "(We'll ask before we install or upgrade anything.)" +
      "</font><br>&nbsp;");
    installButton.addActionListener(this);
    installPanel.add(installButton);

    formatsListModel = new DefaultListModel();
    formatsListModel.addElement(loading);
    formatsList = makeList(formatsListModel);

    formatInfo = new JPanel();
    tabs.addTab("Formats", makeSplitPane(formatsList, formatInfo));

    extensions = makeTextField();
    enabledBox = new JCheckBox("", false);
    enabledBox.addItemListener(this);
    windowlessBox = new JCheckBox("", false);
    windowlessBox.addItemListener(this);

    doFormatLayout(null);

    libsListModel = new DefaultListModel();
    libsListModel.addElement(loading);
    libsList = makeList(libsListModel);
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

    // TODO: install/upgrade button, if applicable
    // - can upgrade any JAR from the repository
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

    // TODO: "How to install" for each library?

    JPanel upgradePanel = new JPanel();
    tabs.addTab("Upgrade", upgradePanel);

    upgradePanel.setLayout(new SpringLayout());

    JLabel upgradeLabel =
      new JLabel("Automatically check for new versions of the Bio-Formats plugins");
    upgradePanel.add(upgradeLabel);

    final boolean checkForUpgrades = Prefs.get(UPGRADE_CHECK_KEY, true);
    upgradeBox = new JCheckBox("", checkForUpgrades);
    upgradeBox.addItemListener(this);
    upgradePanel.add(upgradeBox);

    SpringUtilities.makeCompactGrid(upgradePanel, 1, 2, PAD, PAD, PAD, PAD);

    JPanel logPanel = new JPanel();
    tabs.addTab("Log", logPanel);

    logPanel.setLayout(new java.awt.BorderLayout());

    JTextArea logArea = new JTextArea();
    logArea.setEditable(false);
    logArea.setRows(10);
    logPanel.add(new JScrollPane(logArea));

    SpringUtilities.makeCompactGrid(libInfo, 7, 2, PAD, PAD, PAD, PAD);

    pack();

    TextAreaWriter taw = new TextAreaWriter(logArea);
    log = new PrintWriter(taw);

    new Thread(this, "ConfigWindow-Loader").start();
  }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    new LociInstaller().run(null);
    dispose();
  }

  // -- ItemListener API methods --

  public void itemStateChanged(ItemEvent e) {
    Object src = e.getSource();

    if (src == upgradeBox) {
      Prefs.set(UPGRADE_CHECK_KEY, upgradeBox.isSelected());
      return;
    }

    Object value = formatsList.getSelectedValue();
    if (!(value instanceof FormatEntry)) return;
    FormatEntry entry = (FormatEntry) value;

    if (src == enabledBox) {
      setReaderEnabled(entry, enabledBox.isSelected());
    }
    else if (src == windowlessBox) {
      setReaderWindowless(entry, windowlessBox.isSelected());
    }
  }

  // -- ListSelectionListener API methods --

  public void valueChanged(ListSelectionEvent e) {
    Object src = e.getSource();
    if (src == formatsList) {
      Object value = formatsList.getSelectedValue();
      if (!(value instanceof FormatEntry)) return;
      FormatEntry entry = (FormatEntry) formatsList.getSelectedValue();
      doFormatLayout(entry);
    }
    else if (src == libsList) {
      Object value = libsList.getSelectedValue();
      if (!(value instanceof LibraryEntry)) return;
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

  // -- Runnable API methods --

  /** Populate configuration information in a separate thread. */
  public void run() {
    log.println("Bio-Formats Plugins configuration - " + DateTools.getTimestamp());

    // list system properties
    log.println();
    log.println("-- System properties --");
    Properties sysProps = System.getProperties();
    for (Object name : sysProps.keySet()) {
      log.println(name + " = " + sysProps.getProperty(name.toString()));
    }

    // generate list of formats
    log.println();
    log.println("-- Formats --");
    try {
      Class<?> irClass = Class.forName("loci.formats.ImageReader");
      Object ir = irClass.newInstance();
      Method getClasses = irClass.getMethod("getReaders");
      Object[] readers = (Object[]) getClasses.invoke(ir);
      for (int i=0; i<readers.length; i++) {
        FormatEntry entry = new FormatEntry(log, readers[i]);
        addEntry(entry, formatsListModel);
      }
    }
    catch (Throwable t) {
      log.println("Could not generate list of supported formats:");
      t.printStackTrace(log);
    }

    log.println();
    log.println("-- Libraries --");

    // enumerate list of libraries

    String javaVersion = System.getProperty("java.version") +
      " (" + System.getProperty("java.vendor") + ")";

    String bfVersion = FormatTools.DATE;
    // Ant replaces date token with datestamp of the build
    if (bfVersion.equals("@" + "date" + "@")) bfVersion = "Internal build";

    String qtVersion = null;
    try {
      Class<?> qtToolsClass = Class.forName("loci.formats.gui.LegacyQTTools");
      Object qtTools = qtToolsClass.newInstance();
      Method getQTVersion = qtToolsClass.getMethod("getQTVersion");
      qtVersion = (String) getQTVersion.invoke(qtTools);
    }
    catch (Throwable t) {
      log.println("Could not determine QuickTime version:");
      t.printStackTrace(log);
    }

    String clibIIOVersion = null;
    try {
      Class<?> jpegSpi = Class.forName(
        "com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageReaderSpi");
      IIORegistry registry = IIORegistry.getDefaultInstance();
      Object jpeg = registry.getServiceProviderByClass(jpegSpi);
      if (jpeg == null) clibIIOVersion = LibraryEntry.MISSING_VERSION_CODE;
    }
    catch (Throwable t) {
      if (t instanceof ClassNotFoundException) {
        // JAI Image I/O Tools library not available
      }
      else {
        log.println("Error determining native Image I/O Tools version:");
        t.printStackTrace(log);
      }
    }

    String matlabVersion = null;
    try {
      Class<?> matlabClass = Class.forName("com.mathworks.jmi.Matlab");
      Object matlab = matlabClass.newInstance();
      Method eval = matlabClass.getMethod("eval", new Class[] {String.class});

      String ans = (String) eval.invoke(matlab, new Object[] {"version"});
      if (ans.startsWith("ans =")) ans = ans.substring(5);
      matlabVersion = ans.trim();
    }
    catch (Throwable t) {
      if (t instanceof ClassNotFoundException) {
        // MATLAB library not available
      }
      else {
        log.println("Error determining MATLAB version:");
        t.printStackTrace(log);
      }
    }

    HashMap<String, String> versions = new HashMap<String, String>();
    versions.put("javaVersion", javaVersion);
    versions.put("bfVersion", bfVersion);
    if (qtVersion != null) versions.put("qtVersion", qtVersion);
    if (clibIIOVersion != null) versions.put("clibIIOVersion", clibIIOVersion);
    if (matlabVersion != null) versions.put("matlabVersion", matlabVersion);

    // parse libraries
    HashMap<String, String> props = null;
    String propKey = null;
    StringBuffer propValue = new StringBuffer();
    String resource = "libraries.txt";
    BufferedReader in = null;
    try {
      in = new BufferedReader(new InputStreamReader(
        ConfigWindow.class.getResourceAsStream(resource), Constants.ENCODING));
    }
    catch (UnsupportedEncodingException e) {
      log.println("UTF-8 encoding is not supported.  Something is very wrong.");
      e.printStackTrace(log);
    }
    while (true) {
      String line = null;
      try {
        line = in.readLine();
      }
      catch (IOException exc) {
        log.println("Error parsing " + resource + ":");
        exc.printStackTrace(log);
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
        if (props == null) props = new HashMap<String, String>();
        else {
          addProp(props, propKey, propValue.toString(), versions);
          LibraryEntry entry = new LibraryEntry(log, props);
          addEntry(entry, libsListModel);
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
    catch (IOException exc) {
      log.println("Error closing " + resource + ":");
      exc.printStackTrace(log);
    }
  }

  // -- Utility methods --

  public static JTextField makeTextField() {
    JTextField textField = new JTextField(48);
    int prefHeight = textField.getPreferredSize().height;
    textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, prefHeight));
    textField.setEditable(false);
    return textField;
  }

  public static void addEntry(final Comparable<Object> c,
    final DefaultListModel listModel)
  {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        // remove "Loading..." message
        if (listModel.size() == 1 && listModel.get(0) instanceof String) {
          listModel.remove(0);
        }
        // binary search for proper location
        int min = 0, max = listModel.size();
        while (min < max) {
          int mid = (min + max) / 2;
          Object o = listModel.get(mid);
          int result = c.compareTo(o);
          if (result > 0) min = mid + 1;
          else max = mid;
        }
        listModel.add(max, c);
      }
    });
  }

  // -- Helper methods --

  private void addProp(HashMap<String, String> props,
    String key, String value, HashMap<String, String> versions)
  {
    if (key == null) return;

    // replace \n sequence with newlines
    value = value.replaceAll("\\\\n *", "\n");

    if (key.equals("version")) {
      // get actual value from versions hashtable
      value = versions.get(value);
    }
    if (value != null) props.put(key, value);
  }

  private JLabel makeLabel(String text, boolean top) {
    JLabel label = new JLabel(text);
    label.setHorizontalAlignment(SwingConstants.RIGHT);
    if (top) label.setVerticalAlignment(SwingConstants.TOP);
    return label;
  }

  private JList makeList(DefaultListModel listModel) {
    JList list = new JList(listModel);
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

    enabledBox.setSelected(isReaderEnabled(entry));
    windowlessBox.setSelected(isReaderWindowless(entry));

    formatInfo.removeAll();
    formatInfo.setLayout(new SpringLayout());

    formatInfo.add(makeLabel("Extensions", false));
    formatInfo.add(extensions);

    formatInfo.add(makeLabel("Enabled", false));
    formatInfo.add(enabledBox);

    formatInfo.add(makeLabel("Windowless", false));
    formatInfo.add(windowlessBox);

    // format-specific widgets
    int rows = entry == null ? 0 : entry.widgets.length;
    for (int i=0; i<rows; i++) {
      formatInfo.add(makeLabel(entry.labels[i], false));
      formatInfo.add(entry.widgets[i]);
    }

    SpringUtilities.makeCompactGrid(formatInfo,
      3 + rows, 2, PAD, PAD, PAD, PAD);

    formatInfo.validate();
    formatInfo.repaint();
  }

  private boolean isReaderEnabled(FormatEntry entry) {
    return isReaderPref("ENABLED", entry, true);
  }

  private void setReaderEnabled(FormatEntry entry, boolean value) {
    setReaderPref("ENABLED", entry, value);
  }

  private boolean isReaderWindowless(FormatEntry entry) {
    return isReaderPref("WINDOWLESS", entry, false);
  }

  private void setReaderWindowless(FormatEntry entry, boolean value) {
    setReaderPref("WINDOWLESS", entry, value);
  }

  private boolean isReaderPref(String pref, FormatEntry entry,
    boolean defaultValue)
  {
    if (entry == null) return false;
    try {
      Boolean result = (Boolean)
        invokeMethod("PREF_READER_" + pref, "get", entry, defaultValue);
      return result.booleanValue();
    }
    catch (Throwable t) {
      t.printStackTrace();
      log.println("Error querying property '" + pref.toLowerCase() +
        "' for reader '" + entry.readerName + "':");
      t.printStackTrace(log);
      return false;
    }
  }

  private void setReaderPref(String pref, FormatEntry entry, boolean value) {
    if (entry == null) return;
    try {
      invokeMethod("PREF_READER_" + pref, "set", entry, value);
    }
    catch (Throwable t) {
      t.printStackTrace();
      log.println("Error setting property '" + pref.toLowerCase() +
        "' for reader '" + entry.readerName + "':");
      t.printStackTrace(log);
    }
  }

  private static final Class<?>[] PARAMS = {String.class, boolean.class};

  private Object invokeMethod(String fieldName, String methodName,
    FormatEntry entry, boolean value) throws ClassNotFoundException,
    NoSuchFieldException, IllegalAccessException, NoSuchMethodException,
    InvocationTargetException
  {
    Class<?> utilClass = Class.forName("loci.plugins.util.LociPrefs");
    Field field = utilClass.getField(fieldName);
    String key = field.get(null) + "." + entry.readerName;
    Class<?> prefsClass = Class.forName("ij.Prefs");
    Method method = prefsClass.getMethod(methodName, PARAMS);
    Object[] args = {key, value ? Boolean.TRUE : Boolean.FALSE};
    return method.invoke(null, args);
  }

}

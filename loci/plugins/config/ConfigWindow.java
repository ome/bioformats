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
import java.lang.reflect.Method;
import java.util.Arrays;
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
public class ConfigWindow extends JFrame implements ListSelectionListener {

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

    LibraryEntry[] libraries = {
      // core libraries
      new LibraryEntry("Java", libCore, // classes.jar, rt.jar
        "java.lang.System", javaVersion,
        "http://java.sun.com/", "Varies", "Core Java library"),
      new LibraryEntry("ImageJ", libCore, // ij.jar
        "ij.ImageJ", null,
        "http://rsb.info.nih.gov/ij/", "Public domain",
        "Core ImageJ library"),
      new LibraryEntry("Java3D", libCore,
        "javax.vecmath.Point3d", null,
        "https://java3d.dev.java.net/", "GPL",
        "Not used; listed for informational purposes only."),
      new LibraryEntry("Jython", libCore,
        "org.python.util.PythonInterpreter", null,
        "http://www.jython.org/", "BSD",
        "Not used; listed for informational purposes only."),
      new LibraryEntry("MATLAB", libCore,
        "com.mathworks.jmi.Matlab", matlabVersion,
        "http://www.mathworks.com/products/matlab/", "Commercial",
        "Not used; listed for informational purposes only. " +
        "Note that for MATLAB to be successfully detected here, " +
        "ImageJ must be launched from within the MATLAB environment."),

      // native libraries
      new LibraryEntry("QuickTime for Java", libNative,
        "quicktime.QTSession", qtVersion, // QTJava.zip
        "http://www.apple.com/quicktime/", "Commercial",
        "Bio-Formats has two modes of operation for QuickTime movies:\n" +
        "1) QTJava mode requires the QuickTime for Java library to be " +
        "installed.\n" +
        "2) Native mode works on systems with no QuickTime (e.g., Linux).\n" +
        "\n" +
        "Using QTJava mode adds or improves support for the following " +
        "codecs:\n" +
        "1) [iraw] Intel YUV Uncompressed: enables write\n" +
        "2) [rle] Animation (run length encoded RGB): " +
        "improves read, enables write\n" +
        "3) [rpza] Apple Video 16 bit \"road pizza\": improves read\n" +
        "4) [cvid] Cinepak: enables read and write\n" +
        "5) [svq1] Sorenson Video: enables read and write\n" +
        "6) [svq3] Sorenson Video 3: enables read and write\n" +
        "7) [mp4v] MPEG-4: enables read and write\n" +
        "8) [h263] H.263: enables read and write\n" +
        "\n" +
        "You can toggle which mode is used " +
        "in the Formats tab's \"QuickTime\" entry."),
      new LibraryEntry("JAI Image I/O Tools - native codecs", libNative,
        "TODO", null,
        "https://jai-imageio.dev.java.net/", "BSD",
        "Used by Bio-Formats for lossless JPEG support in DICOM."),
      new LibraryEntry("Nikon ND2 plugin", libNative,
        "TODO", null,
        "http://rsb.info.nih.gov/ij/plugins/nd2-reader.html", "Commercial",
        "Optional plugin. If you have Nikon's ND2 plugin installed, you can " +
        "configure Bio-Formats to use it instead of its native ND2 support " +
        "in the Formats tab's \"Nikon ND2\" entry."),

      // ImageJ plugins
      new LibraryEntry("LOCI plugins", libPlugin,
        "loci.plugins.About", bfVersion, // loci_plugins.jar
        "http://www.loci.wisc.edu/ome/formats.html", "LGPL",
        "LOCI Plugins for ImageJ: a collection of ImageJ plugins including " +
        "the Bio-Formats Importer, Bio-Formats Exporter, Data Browser, " +
        "Stack Colorizer, Stack Slicer, and OME plugins."),
      new LibraryEntry("Image5D", libPlugin,
        "i5d.Image5D", null, // Image_5D.jar
        "http://rsb.info.nih.gov/ij/plugins/image5d.html", "Public domain",
        "Optional plugin. If you have Image5D installed, the Bio-Formats " +
        "Importer plugin can use Image5D to display your image stacks."),
      new LibraryEntry("View5D", libPlugin,
        "View5D_", null, // View5D_.jar
        "http://www.nanoimaging.de/View5D/", "GPL",
        "Optional plugin. If you have View5D installed, the Bio-Formats " +
        "Importer plugin can use View5D to display your image stacks."),

      // Java libraries
      new LibraryEntry("Bio-Formats", libJava,
        "loci.formats.IFormatReader", bfVersion, // bio-formats.jar
        "http://www.loci.wisc.edu/ome/formats.html", "LGPL",
        "LOCI Bio-Formats package for reading and converting " +
        "biological file formats."),
      new LibraryEntry("BUFR Java Decoder", libJava,
        "ucar.bufr.BufrDump", null, // bufr-1.1.00.jar
        "http://www.unidata.ucar.edu/software/decoders/", "LGPL",
        "Used by the NetCDF Java library."),
      new LibraryEntry("JAI Image I/O Tools - Java wrapper", libJava,
        "com.sun.medialib.codec.jiio.Constants", null, // clibwrapper_jiio.jar
        "https://jai-imageio.dev.java.net/", "BSD",
        "Java wrapper for JAI Image I/O Tools native codecs."),
      new LibraryEntry("GRIB Java Decoder", libJava,
        "ucar.grib.GribChecker", null, // grib-5.1.03.jar
        "http://www.unidata.ucar.edu/software/decoders/", "LGPL",
        "Used by the NetCDF Java library."),
      new LibraryEntry("JAI Image I/O Tools - Java codecs", libJava,
        "com.sun.media.imageio.plugins.jpeg2000.J2KImageReadParam",
        null, // jai_imageio.jar
        "https://jai-imageio.dev.java.net/", "BSD",
        "Used by Bio-Formats for JPEG2000 support (ND2, JP2)."),
      new LibraryEntry("MDB Tools (Java port)", libJava,
        "mdbtools.libmdb.MdbFile", null, // mdbtools-java.jar
        "http://sourceforge.net/forum/message.php?msg_id=2550619", "LGPL",
        "Used by Bio-Formats for Zeiss LSM metadata in MDB database files."),
      new LibraryEntry("NetCDF Java", libJava,
        "ucar.nc2.NetcdfFile", null, // netcdf-4.0.jar
        "http://www.unidata.ucar.edu/software/netcdf-java/", "LGPL",
        "Used by Bio-Formats for HDF support (Imaris 5.5)."),
      new LibraryEntry("Apache Jakarta POI (LOCI version)", libJava,
        "org.apache.poi.poifs.filesystem.POIFSDocument", null, // poi-loci.jar
        "http://jakarta.apache.org/poi/", "Apache",
        "Used by Bio-Formats for OLE support in CXD, IPW, OIB and ZVI " +
        "formats. Based on poi-2.5.1-final-20040804.jar, with bugfixes for " +
        "OLE v2 and memory efficiency improvements."),
      new LibraryEntry("Simple Logging Facade for Java", libJava,
        "org.slf4j.Logger", null, // slf4j-jdk14.jar
        "http://www.slf4j.org/", "MIT",
        "Used by the NetCDF Java library."),
      new LibraryEntry("OME Java", libJava,
        "ome.xml.OMEXMLNode", null, // ome-java.jar
        "http://openmicroscopy.org/api/java/", "LGPL",
        "Used by the \"Download from OME\" and \"Upload to OME\" plugins " +
        "to connect to OME. Used by Bio-Formats to work with OME-XML."),
      new LibraryEntry("Apache Jakarta Commons HttpClient", libJava,
        "org.apache.commons.httpclient.HttpConnection",
        null, // commons-httpclient-2.0-rc2.jar
        "http://jakarta.apache.org/commons/httpclient/", "Apache",
        "Required for OME Java to communicate with OME servers."),
      new LibraryEntry("Apache Jakarta Commons Logging", libJava,
        "org.apache.commons.logging.Log", null, // commons-logging.jar
        "http://jakarta.apache.org/commons/logging/", "Apache",
        "Used by OME Java."),
      new LibraryEntry("Apache XML-RPC", libJava,
        "org.apache.xmlrpc.XmlRpc", null, // xmlrpc-1.2-b1.jar
        "http://ws.apache.org/xmlrpc/", "Apache",
        "Required for OME Java to communicate with OME servers"),
      new LibraryEntry("OMERO Common", libJava,
        "ome.model.core.Image", null, // omero-common.jar
        "http://trac.openmicroscopy.org.uk/omero/wiki/MilestoneDownloads",
        "LGPL", "Used by Bio-Formats to connect to OMERO."),
      new LibraryEntry("OMERO Client", libJava,
        "ome.client.Session", null, // omero-client.jar
        "http://trac.openmicroscopy.org.uk/omero/wiki/MilestoneDownloads",
        "LGPL", "Used by Bio-Formats to connect to OMERO."),
      new LibraryEntry("Spring", libJava,
        "org.springframework.core.SpringVersion", null, // spring.jar
        "http://springframework.org/", "Apache",
        "Used by Bio-Formats to connect to OMERO."),
      new LibraryEntry("JBoss Client", libJava,
        "org.jboss.system.Service", null, // jbossall-client.jar
        "http://jboss.org/", "LGPL",
        "Used by Bio-Formats to connect to OMERO."),
      new LibraryEntry("JGoodies Forms", libJava,
        "com.jgoodies.forms.layout.FormLayout", null, // forms-1.0.4.jar
        "http://www.jgoodies.com/freeware/forms/index.html", "BSD",
        "Used for layout by the Data Browser plugin."),
      new LibraryEntry("OME Notes", libJava,
        "loci.ome.notes.Notes", null, // ome-notes.jar
        "http://www.loci.wisc.edu/ome/notes.html", "LGPL",
        "OME Notes library for flexible organization and presentation " +
        "of OME-XML metadata."),
      new LibraryEntry("LuraWave decoder SDK", libJava,
        "com.luratech.lwf.lwfDecoder", null, // lwf_jsdk2.6.jar
        "http://www.luratech.com/", "Commercial",
        "Used by Bio-Formats to decode Flex files " +
        "compressed with the LuraWave JPEG2000 codec.")
    };
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

      enabled.setSelected(true);
    }

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

}

//
// ShortcutPanel.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import ij.IJ;
import ij.plugin.PlugIn;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * A plugin that displays a small window containing shortcuts to the LOCI
 * plugins, including the Bio-Formats Importer, Bio-Formats Exporter,
 * Stack Colorizer and Stack Slicer.
 *
 * Files dragged and dropped onto the window will be opened using the
 * Bio-Formats Importer plugin.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/ShortcutPanel.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/ShortcutPanel.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ShortcutPanel extends JPanel implements ActionListener, PlugIn {

  // -- Constants --

  /** Name of plugin to use for opening dropped files. */
  protected static final String OPENER_PLUGIN = "Bio-Formats Importer";

  /** Name of this plugin, ignored when parsing the plugins list. */
  protected static final String SHORTCUT_PLUGIN =
    "LOCI Plugins Shortcut Window";

  /** Name of the 'LOCI' submenu. */
  protected static final String NORMAL_MENU = "Plugins>LOCI";

  /** Name of the 'About Plugins' submenu. */
  protected static final String HELP_MENU = "Help>About Plugins";

  /** Number of pixels for window border. */
  protected static final int BORDER = 10;

  // -- Fields --

  /** Index of plugin to use to open dropped files. */
  protected static int openerIndex;

  /** Details for installed LOCI plugins. */
  protected static String[] names, plugins, args;

  static {
    // load list of LOCI plugins
    int index = -1;
    Vector vNames = new Vector();
    Vector vPlugins = new Vector();
    Vector vArgs = new Vector();

    // read from configuration file
    try {
      URL url = ShortcutPanel.class.getResource("ShortcutPanel.class");
      String path = url.toString();
      path = path.substring(0, path.indexOf("!")) + "!/plugins.config";
      url = new URL(path);
      BufferedReader in =
        new BufferedReader(new InputStreamReader(url.openStream()));
      while (true) {
        String line = in.readLine();
        if (line == null) break;

        // determine plugin type
        boolean normal = line.startsWith(NORMAL_MENU);
        boolean help = line.startsWith(HELP_MENU);
        if (!normal && !help) continue;

        // parse plugin information
        int quote1 = line.indexOf("\"");
        if (quote1 < 0) continue;
        int quote2 = line.indexOf("\"", quote1 + 1);
        if (quote2 < 0) continue;
        int quote3 = line.indexOf("\"", quote2 + 1);
        if (quote3 < 0) continue;
        int quote4 = line.indexOf("\"", quote3 + 1);
        if (quote4 < 0) continue;
        String name = line.substring(quote1 + 1, quote2);
        if (help) name = "About " + name.substring(0, name.length() - 3);
        String plugin = line.substring(quote2 + 2, quote3 - 1).trim();
        String arg = line.substring(quote3 + 1, quote4);
        if (name.equals(OPENER_PLUGIN)) index = vNames.size();
        if (!name.equals(SHORTCUT_PLUGIN)) {
          vNames.add(name);
          vPlugins.add(plugin);
          vArgs.add(arg);
        }
      }
      in.close();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }

    openerIndex = index;
    names = new String[vNames.size()];
    vNames.copyInto(names);
    plugins = new String[vPlugins.size()];
    vPlugins.copyInto(plugins);
    args = new String[vArgs.size()];
    vArgs.copyInto(args);
  }

  // -- Constructor --

  /** Constructs a shortcut panel. */
  public ShortcutPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));
    setTransferHandler(new ShortcutTransferHandler(this));
    JButton[] b = new JButton[names.length];
    Dimension prefSize = new Dimension(-1, -1);
    Dimension maxSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    for (int i=0; i<names.length; i++) {
      b[i] = new JButton(names[i]);
      Dimension d = b[i].getPreferredSize();
      if (d.width > prefSize.width) prefSize.width = d.width;
      if (d.height > prefSize.height) prefSize.height = d.height;
    }
    for (int i=0; i<b.length; i++) {
      b[i].setPreferredSize(prefSize);
      b[i].setMaximumSize(maxSize);
      b[i].addActionListener(this);
      add(b[i]);
    }
  }

  // -- ShortcutPanel API methods --

  /**
   * Opens the given location (e.g., file)
   * with the Bio-Formats Importer plugin.
   */
  public void open(String id) {
    String arg = args[openerIndex] + "open=[" + id + "] ";
    runPlugIn(plugins[openerIndex], arg);
  }

  public static void runPlugIn(String className, String arg) {
    try {
      IJ.runPlugIn(className, arg);
    }
    catch (Throwable t) {
      Util.reportException(t);
    }
  }

  // -- ActionListener API methods --

  /** Handles button presses. */
  public void actionPerformed(ActionEvent e) {
    JButton b = (JButton) e.getSource();
    String name = b.getText();
    for (int i=0; i<names.length; i++) {
      if (name.equals(names[i])) {
        runPlugIn(plugins[i], args[i]);
        break;
      }
    }
  }

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public void run(String arg) {
    JFrame frame = new JFrame("LOCI Plugins Shortcut Window");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setContentPane(new ShortcutPanel());
    frame.pack();
    Util.placeWindow(frame);
    frame.setVisible(true);
  }

}

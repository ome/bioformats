/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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

package loci.ome.viewer;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 * MetadataViewer is a simple application for displaying OME-XML metadata.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/viewer/MetadataViewer.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/viewer/MetadataViewer.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MetadataViewer extends JFrame
  implements ActionListener, Runnable
{

  // -- Constants --

  /** Key mask for use with keyboard shortcuts on this operating system. */
  public static final int MENU_MASK =
    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

  // -- Fields --

  protected JFileChooser chooser;
  protected MetadataPane metadata;

  // -- Constructor --

  public MetadataViewer(String[] args) {
    super("OME Metadata Viewer");
    metadata = new MetadataPane();
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

  public void openFile(File file) { metadata.setOMEXML(file); }

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
    else if ("about".equals(cmd)) {
      JOptionPane.showMessageDialog(this,
        "OME Metadata Viewer\n" +
        "Revision @vcs.revision@, built @date@\n\n" +
        "The OME Metadata Viewer is LOCI software written by\n" +
        "Curtis Rueden.",
        "OME Metadata Viewer", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  // -- Runnable API methods --

  /** Opens a file in a separate thread. */
  public void run() {
    wait(true);
    File file = chooser.getSelectedFile();
    openFile(file);
    setTitle("OME Metadata Viewer - " + file);
    wait(false);
  }

  // -- Helper methods --

  /** Toggles wait cursor. */
  protected void wait(boolean wait) {
    setCursor(wait ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null);
  }

  // -- Main method --

  public static void main(String[] args) { new MetadataViewer(args); }

}

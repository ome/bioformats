//
// MetadataViewer.java
//

/*
OME Metadata Viewer tool for simple exploration of OME-XML metadata.
Copyright (C) 2006-@year@ Curtis Rueden.

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

package loci.ome.viewer;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

/** MetadataViewer is a simple application for displaying OME-XML metadata. */
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
        "Built @date@\n\n" +
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

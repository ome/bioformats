//
// SwingUtil.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.util;

import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import visad.util.*;

/** SwingUtil contains useful Swing functions. */
public abstract class SwingUtil {

  /** Constructs a JButton with an icon from the given file id. */
  public static JButton makeButton(Object owner, String id,
    String altText, int wpad, int hpad)
  {
    URL url = owner.getClass().getResource(id);
    ImageIcon icon = null;
    if (url != null) icon = new ImageIcon(url);
    JButton button;
    if (icon == null) button = new JButton(altText);
    else {
      button = new JButton(icon);
      button.setPreferredSize(new Dimension(
        icon.getIconWidth() + wpad, icon.getIconHeight() + hpad));
    }
    return button;
  }

  /** Constructs a JToggleButton with an icon from the given file id. */
  public static JToggleButton makeToggleButton(Object owner,
    String id, String altText, int wpad, int hpad)
  {
    URL url = owner.getClass().getResource(id);
    ImageIcon icon = null;
    if (url != null) icon = new ImageIcon(url);
    JToggleButton button;
    if (icon == null) button = new JToggleButton(altText);
    else {
      button = new JToggleButton(icon);
      button.setPreferredSize(new Dimension(
        icon.getIconWidth() + wpad, icon.getIconHeight() + hpad));
    }
    return button;
  }

  /** Fully expands the given JTree from the specified node. */
  public static void expandTree(JTree tree, DefaultMutableTreeNode node) {
    if (node.isLeaf()) return;
    tree.expandPath(new TreePath(node.getPath()));
    Enumeration e = node.children();
    while (e.hasMoreElements()) {
      expandTree(tree, (DefaultMutableTreeNode) e.nextElement());
    }
  }

  /**
   * Toggles the cursor for the given component and all child components
   * between the wait cursor and the normal one.
   */
  public static void setWaitCursor(Component c, boolean wait) {
    setCursor(c, wait ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null);
  }

  /**
   * Sets the cursor for the given component and
   * all child components to the given cursor.
   */
  public static void setCursor(Component c, Cursor cursor) {
    c.setCursor(cursor);
    if (c instanceof Container) {
      Container contain = (Container) c;
      Component[] sub = contain.getComponents();
      for (int i=0; i<sub.length; i++) setCursor(sub[i], cursor);
    }
  }

  /** Gets the containing window for the given component. */
  public static Window getWindow(Component c) {
    while (c != null) {
      if (c instanceof Window) return (Window) c;
      c = c.getParent();
    }
    return null;
  }

  /**
   * Enlarges a window to its preferred width
   * and/or height if it is too small.
   */
  public static void repack(Window w) {
    Dimension size = getRepackSize(w);
    if (!w.getSize().equals(size)) w.setSize(size);
  }

  /** Gets the dimension of this window were it to be repacked. */
  public static Dimension getRepackSize(Window w) {
    Dimension size = w.getSize();
    Dimension pref = w.getPreferredSize();
    if (size.width >= pref.width && size.height >= pref.height) return size;
    return new Dimension(size.width < pref.width ? pref.width : size.width,
      size.height < pref.height ? pref.height : size.height);
  }

  /** Sets the title of the given window. */
  public static void setWindowTitle(Window w, String title) {
    if (w instanceof Frame) ((Frame) w).setTitle(title);
    else if (w instanceof Dialog) ((Dialog) w).setTitle(title);
    else w.setName(title);
  }

  /** Gets the title of the given window. */
  public static String getWindowTitle(Window w) {
    if (w instanceof Frame) return ((Frame) w).getTitle();
    else if (w instanceof Dialog) return ((Dialog) w).getTitle();
    else return w.getName();
  }

  /** Key mask for use with keyboard shortcuts on this operating system. */
  public static final int MENU_MASK =
    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

  /** Sets the keyboard shortcut for the given menu item. */
  public static void setMenuShortcut(GUIFrame frame,
    String menu, String item, int keycode)
  {
    JMenuItem jmi = frame.getMenuItem(menu, item);
    if (jmi == null) return;
    jmi.setAccelerator(KeyStroke.getKeyStroke(keycode, MENU_MASK));
  }

  /**
   * Creates a copy of this menu bar, whose contents update automatically
   * whenever the original menu bar changes.
   */
  public static JMenuBar cloneMenuBar(JMenuBar menubar) {
    if (menubar == null) return null;
    JMenuBar jmb = new JMenuBar();
    int count = menubar.getMenuCount();
    for (int i=0; i<count; i++) jmb.add(cloneMenuItem(menubar.getMenu(i)));
    return jmb;
  }

  /**
   * Creates a copy of this menu item, whose contents update automatically
   * whenever the original menu item changes.
   */
  public static JMenuItem cloneMenuItem(JMenuItem item) {
    if (item == null) return null;
    JMenuItem jmi;
    if (item instanceof JMenu) {
      JMenu menu = (JMenu) item;
      JMenu jm = new JMenu();
      int count = menu.getItemCount();
      for (int i=0; i<count; i++) {
        JMenuItem ijmi = cloneMenuItem(menu.getItem(i));
        if (ijmi == null) jm.addSeparator();
        else jm.add(ijmi);
      }
      jmi = jm;
    }
    else jmi = new JMenuItem();
    ActionListener[] l = item.getActionListeners();
    for (int i=0; i<l.length; i++) jmi.addActionListener(l[i]);
    jmi.setActionCommand(item.getActionCommand());
    syncMenuItem(item, jmi);
    linkMenuItem(item, jmi);
    return jmi;
  }

  /**
   * Configures a scroll pane's properties to always show horizontal and
   * vertical scroll bars. This method only exists to match the Macintosh
   * Aqua Look and Feel as closely as possible.
   */
  public static void configureScrollPane(JScrollPane scroll) {
    if (!LAFUtil.isMacLookAndFeel()) return;
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scroll.setHorizontalScrollBarPolicy(
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
  }

  /** Constructs a JFileChooser that recognizes accepted VisBio file types. */
  public static JFileChooser getVisBioFileChooser() {
    JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
    Vector filters = new Vector();

    // TIFF - tiff/TiffForm, bio/FluoviewTiffForm, ij/ImageJForm
    FileFilter tiff = new ExtensionFileFilter(
      new String[] {"tif", "tiff"}, "Multi-page TIFF stacks");
    filters.add(tiff);

    // Bio-Rad PIC - bio/BioRadForm
    FileFilter biorad = new ExtensionFileFilter("pic", "Bio-Rad PIC files");
    filters.add(biorad);

    // Deltavision - bio/DeltavisionForm
    FileFilter deltavision = new ExtensionFileFilter("dv",
      "Deltavision files");
    filters.add(deltavision);

    // IPLab - bio/IPLabForm
    FileFilter iplab = new ExtensionFileFilter("ipl", "IPLab files");
    filters.add(iplab);

    // Leica - bio/LeicaForm
    FileFilter leica = new ExtensionFileFilter("lei", "Leica files");
    filters.add(leica);

    // Metamorph STK - bio/MetamorphForm
    FileFilter metamorph = new ExtensionFileFilter("stk",
      "Metamorph STK files");
    filters.add(metamorph);

    // Openlab LIFF - bio/OpenlabForm
    FileFilter openlab = new ExtensionFileFilter(new String[]
      {"lif", "liff", ""}, "Openlab LIFF files");
    filters.add(openlab);

    // PerkinElmer - bio/PerkinElmerForm
    FileFilter perkinElmer = new ExtensionFileFilter(new String[]
      {"tim", "zpo", "csv", "htm"}, "PerkinElmer files");
    filters.add(perkinElmer);

    // QuickTime - qt/QTForm
    FileFilter qt = new ExtensionFileFilter("mov", "QuickTime movies");
    filters.add(qt);

    // Zeiss LSM - bio/ZeissForm
    FileFilter lsm = new ExtensionFileFilter("lsm", "Zeiss LSM files");
    filters.add(lsm);

    // Zeiss ZVI - bio/ZVIForm
    FileFilter zvi = new ExtensionFileFilter("zvi", "Zeiss ZVI files");
    filters.add(zvi);

    // BMP - ij/ImageJForm
    FileFilter bmp = new ExtensionFileFilter("bmp", "BMP images");
    filters.add(bmp);

    // DICOM - ij/ImageJForm
    FileFilter dicom = new ExtensionFileFilter("dicom", "DICOM images");
    filters.add(dicom);

    // FITS - ij/ImageJForm
    FileFilter fits = new ExtensionFileFilter("fits", "FITS images");
    filters.add(fits);

    // GIF - ij/ImageJForm
    FileFilter gif = new ExtensionFileFilter("gif", "GIF images");
    filters.add(gif);

    // JPEG - ij/ImageJForm
    FileFilter jpeg = new ExtensionFileFilter(
      new String[] {"jpg", "jpeg", "jpe"}, "JPEG images");
    filters.add(jpeg);

    // PGM - ij/ImageJForm
    FileFilter pgm = new ExtensionFileFilter("pgm", "PGM images");
    filters.add(pgm);

    // PICT - qt/PictForm
    FileFilter pict = new ExtensionFileFilter("pict", "PICT images");
    filters.add(pict);

    // PNG - ij/ImageJForm
    FileFilter png = new ExtensionFileFilter("png", "PNG images");
    filters.add(png);

    // combination filter
    FileFilter[] ff = new FileFilter[filters.size()];
    filters.copyInto(ff);
    FileFilter combo = new ComboFileFilter(ff, "All VisBio file types");

    // add filters to dialog
    dialog.addChoosableFileFilter(combo);
    for (int i=0; i<ff.length; i++) dialog.addChoosableFileFilter(ff[i]);
    dialog.setFileFilter(combo);

    return dialog;
  }

  /** Pops up a message box, blocking the current thread. */
  public static void pause(String msg) {
    JOptionPane.showMessageDialog(null, msg, "VisBio",
      JOptionPane.PLAIN_MESSAGE);
  }

  /** Pops up a message box, blocking the current thread. */
  public static void pause(Throwable t) {
    CharArrayWriter caw = new CharArrayWriter();
    t.printStackTrace(new PrintWriter(caw));
    pause(caw.toString());
  }


  // -- Helper methods --

  /**
   * Forces slave menu item to reflect master menu item
   * using a property change listener.
   */
  protected static void linkMenuItem(JMenuItem master, JMenuItem slave) {
    final JMenuItem source = master, dest = slave;
    source.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        syncMenuItem(source, dest);
      }
    });
  }

  /** Brings the destination menu item into sync with the source item. */
  protected static void syncMenuItem(JMenuItem source, JMenuItem dest) {
    boolean enabled = source.isEnabled();
    if (dest.isEnabled() != enabled) dest.setEnabled(enabled);
    int mnemonic = source.getMnemonic();
    if (dest.getMnemonic() != mnemonic) dest.setMnemonic(mnemonic);
    String text = source.getText();
    if (dest.getText() != text) dest.setText(text);
    KeyStroke accel = source.getAccelerator();
    if (dest.getAccelerator() != accel) dest.setAccelerator(accel);
  }

}

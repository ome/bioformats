//
// SwingUtil.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.Vector;

import javax.swing.*;

import javax.swing.filechooser.FileFilter;

import visad.util.ComboFileFilter;
import visad.util.ExtensionFileFilter;
import visad.util.GUIFrame;
import visad.util.OpenlabFileFilter;

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

  /**
   * Sets the cursor for the given component and
   * all child components to the given cursor.
   */
  public static void setWaitCursor(Component c, Cursor cursor) {
    c.setCursor(cursor);
    if (c instanceof Container) {
      Container contain = (Container) c;
      Component[] sub = contain.getComponents();
      for (int i=0; i<sub.length; i++) setWaitCursor(sub[i], cursor);
    }
  }

  /** Gets the containing window for the given component. */
  public static Window getWindow(Component c) {
    Window w = null;
    Component p = c;
    while (p != null) {
      p = p.getParent();
      if (p instanceof Window) {
        w = (Window) p;
        break;
      }
    }
    return w;
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

    // Bio-Rad PIC - bio/BioRadForm
    FileFilter biorad = new ExtensionFileFilter("pic", "Bio-Rad PIC files");
    dialog.addChoosableFileFilter(biorad);
    filters.add(biorad);

    // Metamorph STK - bio/MetamorphForm
    FileFilter metamorph = new ExtensionFileFilter("stk",
      "Metamorph STK files");
    dialog.addChoosableFileFilter(metamorph);
    filters.add(metamorph);

    // Openlab LIFF - bio/OpenlabForm
    FileFilter openlab = new OpenlabFileFilter();
    dialog.addChoosableFileFilter(openlab);
    filters.add(openlab);

    // Metamorph STK - bio/MetamorphForm
    FileFilter zvi = new ExtensionFileFilter("zvi", "Zeiss ZVI files");
    dialog.addChoosableFileFilter(zvi);
    filters.add(zvi);

    // TIFF - tiff/TiffForm, ij/ImageJForm
    FileFilter tiff = new ExtensionFileFilter(
      new String[] {"tiff", "tif"}, "Multi-page TIFF stacks");
    dialog.addChoosableFileFilter(tiff);
    filters.add(tiff);

    // QuickTime - qt/QTForm
    FileFilter qt = new ExtensionFileFilter("mov", "QuickTime movies");
    dialog.addChoosableFileFilter(qt);
    filters.add(qt);

    // BMP - ij/ImageJForm
    FileFilter bmp = new ExtensionFileFilter("bmp", "BMP images");
    dialog.addChoosableFileFilter(bmp);
    filters.add(bmp);

    // DICOM - ij/ImageJForm
    FileFilter dicom = new ExtensionFileFilter("dicom", "DICOM images");
    dialog.addChoosableFileFilter(dicom);
    filters.add(dicom);

    // FITS - ij/ImageJForm
    FileFilter fits = new ExtensionFileFilter("fits", "FITS images");
    dialog.addChoosableFileFilter(fits);
    filters.add(fits);

    // GIF - ij/ImageJForm
    FileFilter gif = new ExtensionFileFilter("gif", "GIF images");
    dialog.addChoosableFileFilter(gif);
    filters.add(gif);

    // JPEG - ij/ImageJForm
    FileFilter jpeg = new ExtensionFileFilter(
      new String[] {"jpg", "jpeg", "jpe"}, "JPEG images");
    dialog.addChoosableFileFilter(jpeg);
    filters.add(jpeg);

    // PGM - ij/ImageJForm
    FileFilter pgm = new ExtensionFileFilter("pgm", "PGM images");
    dialog.addChoosableFileFilter(pgm);
    filters.add(pgm);

    // PICT - qt/PictForm
    FileFilter pict = new ExtensionFileFilter("pict", "PICT images");
    dialog.addChoosableFileFilter(pict);
    filters.add(pict);

    // combination filter
    FileFilter[] ff = new FileFilter[filters.size()];
    filters.copyInto(ff);
    FileFilter combo = new ComboFileFilter(ff, "All VisBio file types");
    dialog.addChoosableFileFilter(combo);

    return dialog;
  }

  /** Pops up a message box, for blocking the current thread. */
  public static void pause(String msg) {
    JOptionPane.showMessageDialog(null, msg, "VisBio",
      JOptionPane.PLAIN_MESSAGE);
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

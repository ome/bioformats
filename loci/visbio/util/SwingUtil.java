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

import java.net.URL;

import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

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
  protected static final int MENU_MASK =
    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

  /** Sets the keyboard shortcut for the given menu item. */
  public static void setMenuShortcut(GUIFrame frame,
    String menu, String item, int keycode)
  {
    JMenuItem jmi = frame.getMenuItem(menu, item);
    if (jmi == null) return;
    jmi.setAccelerator(KeyStroke.getKeyStroke(keycode, MENU_MASK));
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

    // combination filter
    FileFilter[] ff = new FileFilter[filters.size()];
    filters.copyInto(ff);
    FileFilter combo = new ComboFileFilter(ff, "All VisBio file types");
    dialog.addChoosableFileFilter(combo);

    return dialog;
  }

  /** Pops up a message box, for blocking the current thread. */
  public static void pause(String msg) {
    JOptionPane.showMessageDialog(null, msg, "VisBio", JOptionPane.PLAIN_MESSAGE);
  }

}

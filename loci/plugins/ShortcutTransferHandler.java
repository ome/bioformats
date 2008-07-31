//
// ShortcutTransferHandler.java
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

import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Drag and drop transfer handler for LOCI Plugins Shortcut Window.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/ShortcutTransferHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/ShortcutTransferHandler.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ShortcutTransferHandler extends TransferHandler {

  // -- Fields --

  /** Associated shortcut panel. */
  protected ShortcutPanel shortcutPanel;

  // -- Constructor --

  /** Constructs a new shortcut panel drag and drop transfer handler. */
  public ShortcutTransferHandler(ShortcutPanel shortcutPanel) {
    this.shortcutPanel = shortcutPanel;
  }

  // -- TransferHandler API methods --

  public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
    return true;
  }

  public boolean importData(JComponent comp, Transferable t) {
    try {
      List l = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
      for (int i=0; i<l.size(); i++) {
        File f = (File) l.get(i);
        shortcutPanel.open(f);
      }
    }
    catch (UnsupportedFlavorException e) {
      ij.IJ.error(e.toString());
      return false;
    }
    catch (IOException e) {
      ij.IJ.error(e.toString());
      return false;
    }
    return true;
  }

}

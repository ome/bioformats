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
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Drag and drop transfer handler for LOCI Plugins Shortcut Window.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/ShortcutTransferHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/ShortcutTransferHandler.java">SVN</a></dd></dl>
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
      // search for compatible data flavors (lists, files and strings)
      DataFlavor[] flavors = t.getTransferDataFlavors();
      int fileIndex = -1, stringIndex = -1, listIndex = -1;
      for (int i=0; i<flavors.length; i++) {
        if (fileIndex >= 0 && stringIndex >= 0 && listIndex >= 0) break;
        Class c = flavors[i].getRepresentationClass();
        if (fileIndex < 0 && c == File.class) fileIndex = i;
        if (stringIndex < 0 && c == String.class) stringIndex = i;
        if (listIndex < 0 && c == List.class) listIndex = i;
      }
      // convert data into list of objects
      List list = null;
      if (listIndex >= 0) {
        list = (List) t.getTransferData(flavors[listIndex]);
      }
      else if (fileIndex >= 0) {
        File f = (File) t.getTransferData(flavors[fileIndex]);
        list = new Vector();
        list.add(f);
      }
      else if (stringIndex >= 0) {
        String s = (String) t.getTransferData(flavors[stringIndex]);
        list = new Vector();
        StringTokenizer st = new StringTokenizer(s);
        while (st.hasMoreTokens()) list.add(st.nextToken());
      }
      if (list == null) {
        // no compatible data flavors found
        return false;
      }
      // process each item on the list
      for (int i=0; i<list.size(); i++) {
        Object item = list.get(i);
        String id = null;
        if (item instanceof File) {
          File f = (File) item;
          id = f.getAbsolutePath();
        }
        else if (item instanceof String) {
          id = (String) item;
        }
        if (id == null) {
          System.err.println("Warning: ignoring item #" + i + ": " + item);
        }
        else {
          // convert "file://" URLs into path names
          id = id.replaceAll("^file:/*", "/");
          shortcutPanel.open(id);
        }
      }
    }
    catch (UnsupportedFlavorException e) {
      e.printStackTrace();
      // dump list of supported flavors, for debugging
      DataFlavor[] df = t.getTransferDataFlavors();
      System.err.println("Supported flavors:");
      for (int i=0; i<df.length; i++) {
        System.err.println("\t#" + i + ": " + df[i]);
      }
      ij.IJ.error(e.toString());
      return false;
    }
    catch (IOException e) {
      e.printStackTrace();
      ij.IJ.error(e.toString());
      return false;
    }
    return true;
  }

}

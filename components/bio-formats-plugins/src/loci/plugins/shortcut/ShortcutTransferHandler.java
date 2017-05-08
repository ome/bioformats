/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
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

package loci.plugins.shortcut;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import loci.common.Constants;

/**
 * Drag and drop transfer handler for Bio-Formats Plugins Shortcut Window.
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

  @Override
  public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
    return true;
  }

  @Override
  public boolean importData(JComponent comp, Transferable t) {
    try {
      // search for compatible data flavors (lists, files and strings)
      DataFlavor[] flavors = t.getTransferDataFlavors();
      int fileIndex = -1, stringIndex = -1, listIndex = -1;
      for (int i=0; i<flavors.length; i++) {
        if (fileIndex >= 0 && stringIndex >= 0 && listIndex >= 0) break;
        Class<?> c = flavors[i].getRepresentationClass();
        if (fileIndex < 0 && c == File.class) fileIndex = i;
        if (stringIndex < 0 && c == String.class) stringIndex = i;
        if (listIndex < 0 && c == List.class) listIndex = i;
      }
      // convert data into list of objects
      List<?> list = null;
      if (listIndex >= 0) {
        list = (List<?>) t.getTransferData(flavors[listIndex]);
      }
      else if (fileIndex >= 0) {
        File f = (File) t.getTransferData(flavors[fileIndex]);
        list = Arrays.asList(f);
      }
      else if (stringIndex >= 0) {
        String s = (String) t.getTransferData(flavors[stringIndex]);
        list = Arrays.asList(s.split("[ \t\n\r\f]"));
        //StringTokenizer st = new StringTokenizer(s);
        //while (st.hasMoreTokens()) list.add(st.nextToken());
      }
      if (list == null) {
        // no compatible data flavors found
        return false;
      }
      // process each item on the list
      final String[] ids = new String[list.size()];
      for (int i=0; i<ids.length; i++) {
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
          ids[i] = id.replaceAll("^file:/*", "/");
          if (!new File(ids[i]).exists()) {
            ids[i] = URLDecoder.decode(ids[i], Constants.ENCODING);
          }
        }
      }

      // open each item
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          for (int i=0; i<ids.length; i++) shortcutPanel.open(ids[i]);
        }
      });
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

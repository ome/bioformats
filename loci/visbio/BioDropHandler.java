//
// BioDropHandler.java
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

package loci.visbio;

import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import loci.visbio.data.*;
import loci.visbio.state.StateManager;

/**
 * BioDropHandler provides drag and drop support
 * for handling file drops into VisBio.
 */
public class BioDropHandler extends TransferHandler {

  // -- Fields --

  /** Associated VisBio frame. */
  private VisBioFrame bio;


  // -- Constructor --

  /** Constructs a new transfer handler for VisBio file drops. */
  public BioDropHandler(VisBioFrame bio) {
    super();
    this.bio = bio;
  }


  // -- TransferHandler API methods --

  /** Determines whether a drop operation is legal with the given flavors. */
  public boolean canImport(JComponent comp, DataFlavor[] flavors) {
    bio.toFront(); // be aggressive!
    for (int i=0; i<flavors.length; i++) {
      if (flavors[i].isFlavorJavaFileListType()) return true;
    }
    return false;
  }

  /** Performs a drop operation with the given transferable component. */
  public boolean importData(JComponent comp, Transferable t) {
    if (!t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return false;

    List files;
    try { files = (List) t.getTransferData(DataFlavor.javaFileListFlavor); }
    catch (UnsupportedFlavorException exc) { return false; }
    catch (IOException exc) { return false; }
    if (files.size() < 1) return false;

    File file = (File) files.get(0);
    if (file.getPath().toLowerCase().endsWith(".txt")) {
      // assume file is a VisBio state file
      StateManager sm = (StateManager) bio.getManager(StateManager.class);
      if (sm == null) return false;
      sm.restoreState(file);
    }
    else {
      // assume file is part of a dataset
      DataManager dm = (DataManager)
        bio.getManager(DataManager.class);
      if (dm == null) return false;
      DataTransform data = Dataset.makeTransform(dm, file, bio);
      if (data != null) dm.addData(data);
    }
    return true;
  }

}

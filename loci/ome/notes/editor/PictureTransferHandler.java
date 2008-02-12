//
// PictureTransferHandler.java
//

/*
OME Metadata Notes application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006-@year@ Christopher Peterson.

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

package loci.ome.notes.editor;

import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;

/**
 * Handles drag and drop events.  Adapted from the Java 1.4 example,
 * 'http://java/sun.com/docs/books/tutorial/uiswing/examples/dnd/
 * index.html#DragPictureDemo'.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/ome/notes/editor/PictureTransferHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/ome/notes/editor/PictureTransferHandler.java">SVN</a></dd></dl>
 */
public class PictureTransferHandler extends TransferHandler {
  DataFlavor pictureFlavor = DataFlavor.imageFlavor;
  DraggableIcon icon;

  public boolean importData(JComponent c, Transferable t) {
    if (canImport(c, t.getTransferDataFlavors())) {
      DraggableIcon pic = (DraggableIcon) c;
      if (!pic.editable || icon == pic) return false;

      try {
        JPanel p = (JPanel) t.getTransferData(pictureFlavor);
        JPanel newPanel = new JPanel();
        Component[] cs = p.getComponents();
        for (int i=0; i<cs.length; i++) {
          Component cc = (Component) cs[i].getClass().newInstance();
          if (!(cc instanceof JLabel)) {
            cc.setPreferredSize(new Dimension(64, 25));
          }
          newPanel.add(cc);
        }

        pic.setPanel(newPanel);
        return true;
      }
      catch (Exception e) { e.printStackTrace(); }
    }
    return false;
  }

  protected Transferable createTransferable(JComponent c) {
    icon = (DraggableIcon) c;
    return new PictureTransferable(icon);
  }

  public int getSourceActions(JComponent c) {
    return COPY_OR_MOVE;
  }

  protected void exportDone(JComponent c, Transferable data, int action) {
    icon = null;
  }

  public boolean canImport(JComponent c, DataFlavor[] flavors) {
    for (int i=0; i<flavors.length; i++) {
      if (pictureFlavor.equals(flavors[i])) return true;
    }
    return false;
  }

  class PictureTransferable implements Transferable {
    private JPanel image;

    PictureTransferable(DraggableIcon pic) {
      image = pic.image;
    }

    public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException
    {
      if (!isDataFlavorSupported(flavor)) {
        throw new UnsupportedFlavorException(flavor);
      }
      return image;
    }

    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] { pictureFlavor };
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return pictureFlavor.equals(flavor);
    }
  }
}

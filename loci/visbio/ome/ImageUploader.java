//
// ImageUploader.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

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

package loci.visbio.ome;

import java.util.*;
import loci.formats.FormatTools;
import loci.formats.StatusEvent;
import loci.formats.StatusListener;
import loci.formats.ome.OMEWriter;
import loci.formats.ome.OMEXMLMetadataStore;

/**
 * ImageUploader is a helper class for uploading VisBio datasets
 * (OME images) to the Open Microscopy Environment.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/ome/ImageUploader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/ome/ImageUploader.java">SVN</a></dd></dl>
 */
public class ImageUploader {

  // -- Fields --

  /** List of objects listening for updates to upload tasks. */
  protected Vector listeners;

  // -- Constructor --

  /** Constructs a new OME image uploader. */
  public ImageUploader() { listeners = new Vector(); }

  // -- ImageUploader API methods --

  /**
   * Uploads the given VisBio dataset (OME image) to the specified
   * OME server, using the given username and password.
   */
  public void upload(loci.visbio.data.Dataset data,
    String server, String username, String password)
  {
    try {
      OMEWriter writer = new OMEWriter();
      OMEXMLMetadataStore store = new OMEXMLMetadataStore();
      store.setRoot(data.getOMENode());
      writer.setMetadataStore(store);

      String id = server + "?user=" + username + "&password=" + password;
      writer.setId(id);

      int numFiles = data.getFilenames().length;
      int numImages = data.getImagesPerSource();

      for (int i=0; i<numFiles; i++) {
        for (int j=0; j<numImages; j++) {
          int[] coords = FormatTools.getZCTCoords(
            store.getDimensionOrder(null),
            store.getSizeZ(null).intValue(),
            store.getSizeC(null).intValue(),
            store.getSizeT(null).intValue(),
            numImages*numFiles, numImages*i + j);
          writer.saveImage(
            data.getImage(new int[] {coords[0], coords[1], coords[2], j}),
            i == numFiles - 1 && j == numImages - 1);
        }
      }

      writer.close();
    }
    catch (Exception exc) {
      notifyListeners(new StatusEvent(1, 1,
        "Error uploading (see error console for details)"));
      exc.printStackTrace();
    }
  }

  /** Adds an upload task listener. */
  public void addStatusListener(StatusListener l) {
    synchronized (listeners) { listeners.addElement(l); }
  }

  /** Removes an upload task listener. */
  public void removeStatusListener(StatusListener l) {
    synchronized (listeners) { listeners.removeElement(l); }
  }

  /** Removes all upload task listeners. */
  public void removeAllStatusListeners() {
    synchronized (listeners) { listeners.removeAllElements(); }
  }

  /** Notifies listeners of an upload task update. */
  protected void notifyListeners(StatusEvent e) {
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        StatusListener l = (StatusListener) listeners.elementAt(i);
        l.statusUpdated(e);
      }
    }
  }

}

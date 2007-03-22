//
// ImageUploader.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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
import loci.formats.StatusEvent;
import loci.formats.StatusListener;
import loci.formats.ome.OMEUploader;
import loci.formats.ome.UploadException;

/**
 * ImageUploader is a helper class for uploading VisBio datasets
 * (OME images) to the Open Microscopy Environment.
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
      OMEUploader ul = new OMEUploader(server, username, password);
      String[] ids = data.getFilenames();
      ul.uploadFile(ids[0], true);
    }
    catch (UploadException exc) {
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

//
// ImageDownloader.java
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

import java.util.List;
import java.util.Vector;

import loci.visbio.TaskEvent;
import loci.visbio.TaskListener;

import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;

/**
 * ImageDownloader is a helper class for downloading
 * OME images from the Open Microscopy Environment.
 */
public class ImageDownloader {
  
  // -- Fields --

  /** List of objects listening for updates to download tasks. */
  protected Vector listeners;

 
  // -- Constructor --

  /** Constructs a new OME image downloader. */
  public ImageDownloader() { listeners = new Vector(); }


  // -- ImageDownloader API methods --

  /**
   * Downloads the OME image with the given image id from the
   * specified OME server, using the given username and password.
   */
  public Image download(int imageId,
    String server, String user, String password)
  {
    // This code has been adapted from Phil Huettl's OME plugin for ImageJ
    notifyListeners(new TaskEvent(0, 3, "Preparing criteria..."));
    Criteria criteria = new Criteria();
    addImageFields(criteria);
    criteria.addFilter("id", new Integer(imageId));

    notifyListeners(new TaskEvent(1, 3, "Logging in..."));
    DataFactory df = getDataFactory(server, user, password);

    notifyListeners(new TaskEvent(2, 3, "Retrieving image..."));
    Image[] images = retrieveImages(df, criteria);

    notifyListeners(new TaskEvent(3, 3, "Download complete."));
    return images[0];
  }

  /** Adds an upload task listener. */
  public void addTaskListener(TaskListener l) {
    synchronized (listeners) { listeners.addElement(l); }
  }

  /** Removes an upload task listener. */
  public void removeTaskListener(TaskListener l) {
    synchronized (listeners) { listeners.removeElement(l); }
  }

  /** Removes all upload task listeners. */
  public void removeAllTaskListeners() {
    synchronized (listeners) { listeners.removeAllElements(); }
  }

  /** Notifies listeners of an upload task update. */
  protected void notifyListeners(TaskEvent e) {
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        TaskListener l = (TaskListener) listeners.elementAt(i);
        l.taskUpdated(e);
      }
    }
  }


  // -- Helper methods --

  /** Gets a data factory from the given login information. */
  protected static DataFactory getDataFactory(String server,
    String user, String password)
  {
    // login to OME
    try {
      DataServices rs = DataServer.getDefaultServices(server);
      RemoteCaller rc = rs.getRemoteCaller();
      rc.login(user, password);
      return (DataFactory) rs.getService(DataFactory.class);
    }
    catch (Exception e) { }
    return null;
  }

  /** Adds all image fields to an image criteria. */
  protected static void addImageFields(Criteria criteria) {
    criteria.addWantedField("id");
    criteria.addWantedField("name");
    criteria.addWantedField("created");
    criteria.addWantedField("description");
    criteria.addWantedField("owner");
    criteria.addWantedField("datasets");
    criteria.addWantedField("all_features");
    criteria.addWantedField("all_features", "name");
    criteria.addWantedField("all_features", "tag");
    criteria.addWantedField("all_features", "children");
    criteria.addWantedField("all_features", "parent_feature");
    criteria.addWantedField("owner", "FirstName");
    criteria.addWantedField("owner", "LastName");
    criteria.addWantedField("owner", "id");
    criteria.addWantedField("default_pixels");
    criteria.addWantedField("default_pixels", "id");
    criteria.addWantedField("default_pixels", "PixelType");
    criteria.addWantedField("default_pixels","SizeC");
    criteria.addWantedField("default_pixels","SizeT");
    criteria.addWantedField("default_pixels","SizeX");
    criteria.addWantedField("default_pixels","SizeY");
    criteria.addWantedField("default_pixels","SizeZ");
    criteria.addWantedField("default_pixels","FileSHA1");
    criteria.addWantedField("default_pixels","ImageServerID");
    //criteria.addWantedField("default_pixels","Repository");
    //criteria.addWantedField("default_pixels","Repository.ImageServerURL");
    FieldsSpecification repoFieldSpec = new FieldsSpecification();
    repoFieldSpec.addWantedField("Repository");
    repoFieldSpec.addWantedField("Repository", "ImageServerURL");
    criteria.addWantedFields("default_pixels", repoFieldSpec);
  }
  
  /** Retrieves the Images from the database that match the criteria given. */
  protected static Image[] retrieveImages(DataFactory df, Criteria criteria) {
    if (criteria == null) return null;
    List l = df.retrieveList(Image.class, criteria);
    if (l == null || l.size() == 0) return null;
    Object[] obj = l.toArray();
    Image[] images = new Image[obj.length];
    for (int i=0; i<obj.length; i++) images[i] = (Image) obj[i];
    return images;
  }

}

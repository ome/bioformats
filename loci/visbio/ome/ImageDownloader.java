//
// ImageDownloader.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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

import java.util.List;
import java.util.Vector;
import loci.visbio.TaskEvent;
import loci.visbio.TaskListener;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;

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

  /** Logs in to the given OME server with the specified credentials. */
  public DataServices login(String server, String user, String password) {
    DataServices rs = null;
    try {
      rs = DataServer.getDefaultServices(server);
      RemoteCaller rc = rs.getRemoteCaller();
      rc.login(user, password);
    }
    catch (Exception e) { e.printStackTrace(); }
    return rs;
  }

  /** Logs out of an OME server using the given data services object. */
  public void logout(DataServices rs) { rs.getRemoteCaller().logout(); }

  /**
   * Downloads the OME image with the given image id from an OME server
   * using the specified data factory.
   */
  public Image downloadImage(DataFactory df, int imageId) {
    // This code has been adapted from Phil Huettl's OME plugin for ImageJ
    notifyListeners(new TaskEvent(0, 2, "Preparing criteria..."));
    Criteria criteria = new Criteria();
    addImageFields(criteria);
    criteria.addFilter("id", new Integer(imageId));

    notifyListeners(new TaskEvent(1, 2, "Retrieving image..."));
    Image[] images = retrieveImages(df, criteria);

    notifyListeners(new TaskEvent(2, 2, "Download complete."));
    return images == null || images.length == 0 ? null : images[0];
  }

  /**
   * Downloads pixels for the given image plane using the specified pixels
   * factory, converting to VisAD's standard array representation.
   */
  public float[][] downloadPixels(PixelsFactory pf, Pixels pix, int z, int t) {
    // This code has been adapted from Phil Huettl's OME plugin for ImageJ
    int sizeX = pix.getSizeX().intValue();
    int sizeY = pix.getSizeY().intValue();
    int sizeC = pix.getSizeC().intValue();
    String pixelType = pix.getPixelType();
    int len = sizeX * sizeY;
    float[][] samples = new float[sizeC][len];
    for (int c=0; c<sizeC; c++) {
      notifyListeners(new TaskEvent(c, sizeC,
        "Retrieving pixel channel #" + (c + 1) + "..."));
      byte[] b = null;
      try { b = pf.getPlane(pix, z, c, t, false); }
      catch (ImageServerException exc) {
        exc.printStackTrace();
        return null;
      }
      if (pixelType.equals("uint8")) {
        for (int i=0; i<len; i++) {
          int b0 = b[i] & 0xff;
          samples[c][i] = b0;
        }
      }
      else if (pixelType.equals("uint16")) {
        for (int i=0; i<len; i++) {
          int b0 = b[2 * i] & 0xff;
          int b1 = (b[2 * i + 1] & 0xff) << 8;
          samples[c][i] = b0 | b1;
        }
      }
      else if (pixelType.equals("int16")) {
        // CTR TODO pixel type int16
        return null;
      }
      else if (pixelType.equals("color256")) {
        // CTR TODO pixel type color256
        return null;
      }
      else if (pixelType.equals("colorRGB")) {
        // CTR TODO pixel type colorRGB
        return null;
      }
      else if (pixelType.equals("uint32")) {
        for (int i=0; i<len; i++) {
          int b0 = b[4 * i] & 0xff;
          int b1 = (b[4 * i + 1] & 0xff) << 8;
          int b2 = (b[4 * i + 2] & 0xff) << 16;
          int b3 = (b[4 * i + 3] & 0xff) << 24;
          samples[c][i] = b0 | b1 | b2 | b3;
        }
      }
      else if (pixelType.equals("float")) {
        for (int i=0; i<len; i++) {
          int b0 = b[4 * i] & 0xff;
          int b1 = (b[4 * i + 1] & 0xff) << 8;
          int b2 = (b[4 * i + 2] & 0xff) << 16;
          int b3 = (b[4 * i + 3] & 0xff) << 24;
          samples[c][i] = Float.intBitsToFloat(b0 | b1 | b2 | b3);
        }
      }
    }
    notifyListeners(new TaskEvent(sizeC, sizeC, "Download complete."));
    return samples;
  }

  /** Adds an download task listener. */
  public void addTaskListener(TaskListener l) {
    synchronized (listeners) { listeners.addElement(l); }
  }

  /** Removes an download task listener. */
  public void removeTaskListener(TaskListener l) {
    synchronized (listeners) { listeners.removeElement(l); }
  }

  /** Removes all download task listeners. */
  public void removeAllTaskListeners() {
    synchronized (listeners) { listeners.removeAllElements(); }
  }

  /** Notifies listeners of a download task update. */
  protected void notifyListeners(TaskEvent e) {
    synchronized (listeners) {
      for (int i=0; i<listeners.size(); i++) {
        TaskListener l = (TaskListener) listeners.elementAt(i);
        l.taskUpdated(e);
      }
    }
  }


  // -- Helper methods --

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
    if (l == null) return null;
    Object[] obj = l.toArray();
    Image[] images = new Image[obj.length];
    for (int i=0; i<obj.length; i++) images[i] = (Image) obj[i];
    return images;
  }

}

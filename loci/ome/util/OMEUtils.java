//
// OMEUtils.java
//

/*
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

package loci.ome.util;

import java.net.MalformedURLException;
import java.util.*;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.Image;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;

/** Utility methods for retrieving data from an OME database. */
public class OMEUtils {

  // -- Fields --

  private static boolean loggedIn = false;

  private static DataFactory df;
  private static PixelsFactory pf;
  private static RemoteCaller rc;
  private static DataServices rs;
  private static String omeis;

  // -- Utils API methods --

  /** Login to the OME database */
  public static void login(String server, String user, String pass)
    throws MalformedURLException
  {
    omeis = server;
    if (omeis.indexOf("http://") != -1) {
      omeis = omeis.substring(7, omeis.length());
    }
    omeis = "http://" + omeis.substring(0, omeis.indexOf("/")) + 
      "/cgi-bin/omeis";

    rs = DataServer.getDefaultServices(server);
    rc = rs.getRemoteCaller();
    rc.login(user, pass);
    df = (DataFactory) rs.getService(DataFactory.class);
    pf = (PixelsFactory) rs.getService(PixelsFactory.class);
    loggedIn = true;
  }

  /** Login to the OME database */
  public static void login(String server, String sessionKey)
    throws MalformedURLException
  {
    omeis = server;
    if (omeis.indexOf("http://") != -1) {
      omeis = omeis.substring(7, omeis.length());
    }
    omeis = "http://" + omeis.substring(0, omeis.indexOf("/")) + 
      "/cgi-bin/omeis";
    
    rs = DataServer.getDefaultServices(server);
    rc = rs.getRemoteCaller();
    rc.setSessionKey(sessionKey);
    df = (DataFactory) rs.getService(DataFactory.class);
    pf = (PixelsFactory) rs.getService(PixelsFactory.class);
    loggedIn = true;
  }

  /** Retrieve a list of all images in the database. */
  public static Image[] getAllImages() {
    if (!loggedIn) return null;
    Criteria c = new Criteria();
    setImageCriteria(c);

    List l = df.retrieveList(Image.class, c);
    return (Image[]) l.toArray(new Image[0]);
  }

  /** Retrieve a list of all image IDs in the database. */
  public static int[] getAllImageIDs() {
    Image[] images = getAllImages();
    if (images == null) return null;
    int[] ids = new int[images.length];
    for (int i=0; i<images.length; i++) {
      ids[i] = images[i].getID();
    }
    return ids;
  }

  /**
   * Display a OMETablePanel using the given IDs.
   * @return a list of Image IDs
   */
  public static int[] showTable(int[] ids) throws ImageServerException {
    Image[] images = new Image[ids.length];
    for (int i=0; i<ids.length; i++) {
      Criteria c = new Criteria();
      setImageCriteria(c);
      c.addFilter("id", "=", "" + ids[i]);
      images[i] = (Image) df.retrieve(Image.class, c);
    }
    return showTable(images);
  }

  /**
   * Display a OMETablePanel using the given Images.
   * @return a list of Image IDs
   */
  public static int[] showTable(Image[] images) throws ImageServerException {
    if (!loggedIn) return null;
    Criteria c = new Criteria();
    c.addWantedField("FirstName");
    c.addWantedField("LastName");
    c.addOrderBy("LastName");

    List l = df.retrieveList("Experimenter", c);
    String[][] experimenters = new String[3][l.size()];
    for (int i=0; i<l.size(); i++) {
      experimenters[0][i] = ((Experimenter) l.get(i)).getFirstName();
      experimenters[1][i] = ((Experimenter) l.get(i)).getLastName();
      experimenters[2][i] = "" + ((Experimenter) l.get(i)).getID();
    }

    Hashtable exps = new Hashtable(experimenters.length);
    for (int i=0; i<experimenters[0].length; i++) {
      exps.put(new Integer(experimenters[2][i]), experimenters[1][i] + ", " +
        experimenters[0][i]);
    }

    int numImages = images.length;
    Object[][] props = new Object[numImages][4];
    Object[][] details = new Object[numImages][10];

    Pixels p;
    for (int i=0; i<props.length; i++) {
      props[i][0] = new Boolean(false);
      props[i][1] = images[i].getName() == null ? "" : images[i].getName();
      props[i][2] = String.valueOf(images[i].getID());
      props[i][3] = images[i].getCreated() == null ? "" :
        images[i].getCreated();
      p = images[i].getDefaultPixels();

      Repository r = p.getRepository();
      r.setImageServerURL(omeis); 
      p.setRepository(r);

      if (p == null) continue;
      try {
        details[i][0] = pf.getThumbnail(p);
      }
      catch (NullPointerException e) { details[i][0] = null; }
      details[i][1] =
        (String) exps.get(new Integer(images[i].getOwner().getID()));
      details[i][2] = p.getPixelType() == null ? "" : p.getPixelType();
      details[i][3] = p.getSizeX() == null ? "0" : p.getSizeX().toString();
      details[i][4] = p.getSizeY() == null ? "0" : p.getSizeY().toString();
      details[i][5] = p.getSizeZ() == null ? "0" : p.getSizeZ().toString();
      details[i][6] = p.getSizeC() == null ? "0" : p.getSizeC().toString();
      details[i][7] = p.getSizeT() == null ? "0" : p.getSizeT().toString();
      details[i][8] = images[i].getDescription();
      details[i][9] = props[i][2] == null ? "-1" : props[i][2];
    }

    // make sure nothing is null
    for (int i=0; i<numImages; i++) {
      for (int j=0; j<props[i].length; j++) {
        if (props[i][j] == null) props[i][j] = new Object();
      }
      for (int j=2; j<details[i].length; j++) {
        if (details[i][j] == null) details[i][j] = new String();
      }
    }

    String[] columns = {"", "Name", "ID", "Date Created"};
    OMETablePanel table = new OMETablePanel(null, props, columns, details);
    int[] results = table.getInput();
    return results;
  }

  /** Set up a Criteria object for retrieving Images. */
  public static void setImageCriteria(Criteria c) {
    c.addWantedField("id");
    c.addWantedField("name");
    c.addWantedField("description");
    c.addWantedField("inserted");
    c.addWantedField("created");
    c.addWantedField("owner");
    c.addWantedField("default_pixels");
    c.addWantedField("owner", "FirstName");
    c.addWantedField("owner", "LastName");
    c.addWantedField("owner", "id");
    c.addWantedField("default_pixels", "id");
    c.addWantedField("default_pixels", "SizeX");
    c.addWantedField("default_pixels", "SizeY");
    c.addWantedField("default_pixels", "SizeZ");
    c.addWantedField("default_pixels", "SizeC");
    c.addWantedField("default_pixels", "SizeT");
    c.addWantedField("default_pixels", "PixelType");
    c.addWantedField("default_pixels", "Repository");
    c.addWantedField("default_pixels", "ImageServerID");
    c.addWantedField("default_pixels.Repository", "ImageServerURL");
  }

}

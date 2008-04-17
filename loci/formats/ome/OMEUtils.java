//
// OMEUtils.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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

package loci.formats.ome;

import ij.IJ;
import ij.gui.GenericDialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import javax.swing.*;
import loci.formats.*;
import loci.formats.ome.OMECredentials;
import loci.plugins.Util;

/**
 * Utility methods for retrieving data from an OME database.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ome/OMEUtils.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ome/OMEUtils.java">SVN</a></dd></dl>
 */
public class OMEUtils {

  // -- Constants --

  private static final String NO_OMERO_MSG = "OMERO client libraries not " +
    "found.  Please obtain omero-common.jar and omero-client.jar from " +
    "http://www.loci.wisc.edu/ome/formats.html";

  private static final String NO_OME_MSG = "OME-Java library not found.  " +
    "Please obtain ome-java.jar from http://www.loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private static boolean noOME = false;
  private static boolean noOMERO = false;
  private static ReflectedUniverse r = createReflectedUniverse();
  private static ReflectedUniverse createReflectedUniverse() {
    r = new ReflectedUniverse();
    try {
      r.exec("import ome.api.IQuery");
      r.exec("import ome.api.RawPixelsStore");
      r.exec("import ome.api.ThumbnailStore");
      r.exec("import ome.parameters.Filter");
      r.exec("import ome.parameters.Parameters");
      r.exec("import ome.system.Login");
      r.exec("import ome.system.Server");
      r.exec("import ome.system.ServiceFactory");
      r.exec("import pojos.ImageData");
      r.exec("import pojos.PixelsData");
      r.exec("import pojos.ProjectData");
    }
    catch (ReflectException e) {
      noOMERO = true;
      LogTools.trace(e);
    }
    try {
      r.exec("import java.util.Hashtable");
      r.exec("import java.util.List");
      r.exec("import org.openmicroscopy.ds.Criteria");
      r.exec("import org.openmicroscopy.ds.DataFactory");
      r.exec("import org.openmicroscopy.ds.DataServer");
      r.exec("import org.openmicroscopy.ds.DataServices");
      r.exec("import org.openmicroscopy.ds.RemoteCaller");
      r.exec("import org.openmicroscopy.ds.dto.Image");
      r.exec("import org.openmicroscopy.ds.st.Pixels");
      r.exec("import org.openmicroscopy.ds.st.Repository");
      r.exec("import org.openmicroscopy.is.PixelsFactory");
    }
    catch (ReflectException e) {
      noOME = true;
      LogTools.trace(e);
    }
    return r;
  }

  private static boolean loggedIn = false;
  private static boolean omePixelsInitialized = false;
  private static boolean omeroPixelsInitialized = false;

  // -- Utils API methods --

  /**
   * Get credentials from a string.
   */
  public static OMECredentials parseCredentials(String s) {
    if (s == null || s.trim().equals("")) return null;

    OMECredentials credentials = new OMECredentials();

    if (s.indexOf("\n") != -1) {
      StringTokenizer st = new StringTokenizer(s, "\n");
      String token = null;
      while (st.hasMoreTokens()) {
        token = st.nextToken();
        String key = token.substring(0, token.indexOf("=")).trim();
        String value = token.substring(token.indexOf("=") + 1).trim();
        if (key.equals("server")) credentials.server = value;
        else if (key.equals("username")) credentials.username = value;
        else if (key.equals("port")) credentials.port = value;
        else if (key.equals("password")) credentials.password = value;
        else if (key.equals("id")) credentials.imageID = Long.parseLong(value);
      }
    }
    else {
      credentials.server = s.substring(0, s.lastIndexOf("?"));

      int first = credentials.server.indexOf(":");
      int last = credentials.server.lastIndexOf(":");
      if (credentials.server.indexOf("http://") == -1) {
        first = 0;
        if (last < 0) last = 0;
      }
      if (first != last) {
        credentials.port = credentials.server.substring(last + 1);
        credentials.server = credentials.server.substring(0, last);
      }

      int ndx = s.indexOf("&");
      credentials.username = s.substring(s.lastIndexOf("?") + 6, ndx);
      int end = s.indexOf("&", ndx + 1);
      if (end == -1) end = s.length();
      credentials.password = s.substring(ndx + 10, end);
      ndx = s.indexOf("&", ndx + 1);
      if (ndx > 0) credentials.imageID = Long.parseLong(s.substring(ndx + 4));
    }
    return credentials;
  }

  /** Login to an OME/OMERO server. */
  public static void login(OMECredentials credentials) throws ReflectException
  {
    if (loggedIn) return;
    r.setVar("user", credentials.username);
    r.setVar("pass", credentials.password);
    r.setVar("sname", credentials.server);

    if (credentials.isOMERO) {
      r.setVar("port", Integer.parseInt(credentials.port));
      r.exec("login = new Login(user, pass)");
      r.exec("server = new Server(sname, port)");
      r.exec("sf = new ServiceFactory(server, login)");
      r.exec("thumbs = sf.createThumbnailService()");
      r.exec("admin = sf.getAdminService()");
      r.exec("eventContext = admin.getEventContext()");
    }
    else {
      String s = (String) r.getVar("sname");
      if (s.indexOf("http://") != -1) {
        s = s.substring(7, s.length());
      }
      s = "http://" + s.substring(0, s.indexOf("/")) + "/shoola";
      credentials.server = s;
      r.setVar("sname", s);

      try {
        r.setVar("dfClass", Class.forName("org.openmicroscopy.ds.DataFactory"));
        r.setVar("pfClass",
          Class.forName("org.openmicroscopy.is.PixelsFactory"));
      }
      catch (ClassNotFoundException e) {
        throw new ReflectException(e);
      }

      r.exec("rs = DataServer.getDefaultServices(sname)");
      r.exec("rc = rs.getRemoteCaller()");
      r.exec("rc.login(user, pass)");
      r.exec("df = rs.getService(dfClass)");
      r.exec("pf = rs.getService(pfClass)");
    }
    loggedIn = true;
  }

  /** Log out of OME/OMERO server. */
  public static void logout(boolean isOMERO) {
    loggedIn = false;
    omePixelsInitialized = false;
    omeroPixelsInitialized = false;

    if (!isOMERO) {
      try {
        r.exec("rc.logout()");
      }
      catch (ReflectException e) { }
    }
  }

  /** Get the names of every experimenter in the database. */
  public static String[] getAllExperimenters(boolean isOMERO)
    throws ReflectException
  {
    List exps = null;
    if (isOMERO) {
      String[] rtn = new String[1];
      r.exec("uid = eventContext.getCurrentUserId()");
      r.exec("exp = admin.getExperimenter(uid)");
      r.exec("fname = exp.getFirstName()");
      r.exec("lname = exp.getLastName()");
      rtn[0] = r.getVar("lname") + ", " + r.getVar("fname");
      return rtn;
    }

    getAllImages();
    r.exec("c = new Criteria()");
    r.setVar("OME_ID", "id");
    r.setVar("OME_FIRST_NAME", "FirstName");
    r.setVar("OME_LAST_NAME", "LastName");
    r.exec("c.addWantedField(OME_ID)");
    r.exec("c.addWantedField(OME_FIRST_NAME)");
    r.exec("c.addWantedField(OME_LAST_NAME)");

    r.setVar("exp", "Experimenter");
    r.exec("exps = df.retrieveList(exp, c)");

    exps = (List) r.getVar("exps");
    String[] rtn = new String[exps.size()];
    for (int i=0; i<exps.size(); i++) {
      r.setVar("exp", exps.get(i));
      if (isOMERO) r.exec("exp = new ExperimenterData(exp)");
      r.exec("fname = exp.getFirstName()");
      r.exec("lname = exp.getLastName()");
      rtn[i] = r.getVar("lname") + ", " + r.getVar("fname");
    }
    return rtn;
  }

  /** Filter available pixels to match given criteria. */
  public static void filterPixels(String firstName, String lastName,
    String imageName, String created, String id, boolean isOMERO)
    throws ReflectException
  {
    if (isOMERO) getAllPixels();
    else getAllImages();

    int len = ((Integer) r.getVar("len")).intValue();
    List results = (List) r.getVar(isOMERO ? "results" : "l");
    List newResults = new Vector();
    for (int i=0; i<len; i++) {
      r.setVar("obj", results.get(i));
      String fname = null, lname = null, iname = null,
        create = null, pid = null;
      if (isOMERO) {
        r.exec("pix = new PixelsData(obj)");
        r.exec("v = obj.getId()");
        pid = r.getVar("v").toString();
        r.exec("img = pix.getImage()");
        r.exec("imageName = img.getName()");
        iname = (String) r.getVar("imageName");
        r.exec("owner = pix.getOwner()");
        r.exec("fname = owner.getFirstName()");
        r.exec("lname = owner.getLastName()");

        try {
          r.exec("created = img.getInserted()");
          create = ((Timestamp) r.getVar("created")).toString();
        }
        catch (Exception e) {
          create = null;
        }

        fname = (String) r.getVar("fname");
        lname = (String) r.getVar("lname");
      }
      else {
        r.exec("created = obj.getCreated()");
        create = (String) r.getVar("created");
        r.exec("id = obj.getID()");
        pid = r.getVar("id").toString();
        r.exec("imageName = obj.getName()");
        iname = (String) r.getVar("imageName");
        r.exec("owner = obj.getOwner()");
        r.exec("fname = owner.getFirstName()");
        r.exec("lname = owner.getLastName()");
        fname = (String) r.getVar("fname");
        lname = (String) r.getVar("lname");
      }

      if ((firstName == null || firstName.equals(fname)) &&
        (lastName == null || lastName.equals(lname)) &&
        (imageName == null ||
        iname.toLowerCase().indexOf(imageName.toLowerCase()) != -1) &&
        (created == null || created.equals(create) ||
        created.startsWith(create)) && (id == null || id.equals(pid)))
      {
        newResults.add(results.get(i));
      }
    }

    if (isOMERO) {
      r.setVar("results", newResults);
      r.exec("len = results.size()");
    }
    else {
      r.setVar("l", newResults);
      r.exec("len = l.size()");
    }
  }

  /** Get the width of every accessible image on the server. */
  public static int[] getAllWidths(boolean isOMERO) throws ReflectException {
    if (isOMERO) return getIntValues("getSizeX");
    return getOMEIntValues("getSizeX");
  }

  /** Get the height of every accessible image on the server. */
  public static int[] getAllHeights(boolean isOMERO) throws ReflectException {
    if (isOMERO) return getIntValues("getSizeY");
    return getOMEIntValues("getSizeY");
  }

  /** Get the channel count of every accessible image on the server. */
  public static int[] getAllChannels(boolean isOMERO) throws ReflectException {
    if (isOMERO) return getIntValues("getSizeC");
    return getOMEIntValues("getSizeC");
  }

  /** Get the number of Z slices in each accessible image on the server. */
  public static int[] getAllZs(boolean isOMERO) throws ReflectException {
    if (isOMERO) return getIntValues("getSizeZ");
    return getOMEIntValues("getSizeZ");
  }

  /** Get the number of timepoints in each accessible image on the server. */
  public static int[] getAllTs(boolean isOMERO) throws ReflectException {
    if (isOMERO) return getIntValues("getSizeT");
    return getOMEIntValues("getSizeT");
  }

  /** Get the pixel type of each accessible image on the server. */
  public static String[] getAllTypes(boolean isOMERO) throws ReflectException {
    if (isOMERO) return getStringValues("getPixelType");
    return getOMEStringValues("getPixelType");
  }

  /** Get the name of each accessible image on the server. */
  public static String[] getAllNames(boolean isOMERO) throws ReflectException {
    if (isOMERO) {
      getAllPixels();
      int len = ((Integer) r.getVar("len")).intValue();
      String[] rtn = new String[len];
      for (int i=0; i<len; i++) {
        r.setVar("i", i);
        r.exec("obj = results.get(i)");
        r.exec("obj = new PixelsData(obj)");
        r.exec("image = obj.getImage()");
        r.exec("name = image.getName()");
        rtn[i] = (String) r.getVar("name");
      }
      return rtn;
    }

    getAllImages();
    int len = ((Integer) r.getVar("len")).intValue();
    String[] rtn = new String[len];
    for (int i=0; i<len; i++) {
      r.setVar("i", i);
      r.exec("omeImage = l.get(i)");
      r.exec("v = omeImage.getName()");
      rtn[i] = (String) r.getVar("v");
    }
    return rtn;
  }

  /** Get the description of each accessible image on the server. */
  public static String[] getAllDescriptions(boolean isOMERO)
    throws ReflectException
  {
    if (isOMERO) {
      getAllPixels();
      int len = ((Integer) r.getVar("len")).intValue();
      String[] rtn = new String[len];
      for (int i=0; i<len; i++) {
        r.setVar("i", i);
        r.exec("obj = results.get(i)");
        r.exec("obj = new PixelsData(obj)");
        r.exec("image = obj.getImage()");
        r.exec("name = image.getDescription()");
        rtn[i] = (String) r.getVar("name");
      }
      return rtn;
    }

    getAllImages();
    int len = ((Integer) r.getVar("len")).intValue();
    String[] rtn = new String[len];
    for (int i=0; i<len; i++) {
      r.setVar("i", i);
      r.exec("omeImage = l.get(i)");
      r.exec("v = omeImage.getDescription()");
      rtn[i] = (String) r.getVar("v");
    }
    return rtn;
  }

  /** Get the creation date of each accessible image on the server. */
  public static String[] getAllDates(boolean isOMERO) throws ReflectException {
    if (isOMERO) {
      getAllPixels();
      int len = ((Integer) r.getVar("len")).intValue();
      String[] rtn = new String[len];
      for (int i=0; i<len; i++) {
        r.setVar("i", i);
        r.exec("obj = results.get(i)");
        r.exec("obj = new PixelsData(obj)");
        r.exec("image = obj.getImage()");
        try {
          r.exec("name = image.getInserted()");
          rtn[i] = ((Timestamp) r.getVar("name")).toString();
        }
        catch (Exception e) {
          rtn[i] = new Timestamp(System.currentTimeMillis()).toString();
        }
      }
      return rtn;
    }

    getAllImages();
    int len = ((Integer) r.getVar("len")).intValue();
    String[] rtn = new String[len];
    for (int i=0; i<len; i++) {
      r.setVar("i", i);
      r.exec("omeImage = l.get(i)");
      r.exec("v = omeImage.getCreated()");
      rtn[i] = (String) r.getVar("v");
    }
    return rtn;
  }

  /** Get the thumbnail for each accessible image on the server. */
  public static BufferedImage[] getAllThumbnails(boolean isOMERO)
    throws ReflectException
  {
    if (isOMERO) {
      return new BufferedImage[((Integer) r.getVar("len")).intValue()];
    }

    getAllImages();
    int len = ((Integer) r.getVar("len")).intValue();
    BufferedImage[] rtn = new BufferedImage[len];
    for (int i=0; i<len; i++) {
      r.setVar("i", i);
      r.exec("omeImage = l.get(i)");
      r.exec("omePix = omeImage.getDefaultPixels()");
      r.exec("v = pf.getThumbnail(omePix)");
      rtn[i] = (BufferedImage) r.getVar("v");
    }
    return rtn;
  }

  /** Get all of the image IDs. */
  public static long[] getAllIDs(boolean isOMERO) throws ReflectException {
    if (isOMERO) return getLongValues("getId");

    getAllImages();
    int len = ((Integer) r.getVar("len")).intValue();
    long[] rtn = new long[len];
    for (int i=0; i<len; i++) {
      r.setVar("i", i);
      r.exec("omeImage = l.get(i)");
      r.exec("v = omeImage.getID()");
      rtn[i] = ((Integer) r.getVar("v")).intValue();
    }
    return rtn;
  }

  public long[] showTable(OMECredentials cred) throws ReflectException {
    long[] ids = getAllIDs(cred.isOMERO);
    int[] x = getAllWidths(cred.isOMERO);
    int[] y = getAllHeights(cred.isOMERO);
    int[] z = getAllZs(cred.isOMERO);
    int[] c = getAllChannels(cred.isOMERO);
    int[] t = getAllTs(cred.isOMERO);
    String[] types = getAllTypes(cred.isOMERO);
    String[] names = getAllNames(cred.isOMERO);
    String[] descr = getAllDescriptions(cred.isOMERO);
    String[] created = getAllDates(cred.isOMERO);
    BufferedImage[] thumbs = getAllThumbnails(cred.isOMERO);

    if (ids.length == 0) {
      IJ.error("No images found!");
      return ids;
    }

    // TODO : consolidate this with ImporterOptions.promptSeries logic.

    GenericDialog gd = new GenericDialog("OME/OMERO Plugin");
    GridBagLayout gdl = (GridBagLayout) gd.getLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.gridx = 2;
    gbc.gridwidth = GridBagConstraints.REMAINDER;

    Panel[] p = new Panel[ids.length];
    String[] tips = new String[ids.length];
    for (int i=0; i<ids.length; i++) {
      if (names[i] == null) names[i] = "";

      StringBuffer tip = new StringBuffer();
      tip.append("<HTML>Name: ");
      tip.append(names[i]);
      tip.append("<BR>ID: ");
      tip.append(ids[i]);
      tip.append("<BR>Date Created: ");
      tip.append(created[i]);
      tip.append("<BR>Pixel type: ");
      tip.append(types[i]);
      tip.append("<BR>SizeX: ");
      tip.append(x[i]);
      tip.append("<BR>SizeY: ");
      tip.append(y[i]);
      tip.append("<BR>SizeZ: ");
      tip.append(z[i]);
      tip.append("<BR>SizeC: ");
      tip.append(c[i]);
      tip.append("<BR>SizeT: ");
      tip.append(t[i]);
      tip.append("<BR>Description: ");
      tip.append(descr[i]);
      tip.append("</HTML>");

      tips[i] = tip.toString();

      if (names[i].indexOf(File.separator) != -1) {
        names[i] = names[i].substring(names[i].lastIndexOf(File.separator) + 1);
      }

      gd.addCheckbox(names[i] + " (" + ids[i] + ")", false);
      p[i] = new Panel();
      if (cred.isOMERO) {
        p[i].add(Box.createRigidArea(new Dimension(128, 128)));
        gbc.gridy = i;
      }
      else {
        gbc.gridy = i;
        JLabel label = new JLabel(new ImageIcon(thumbs[i]));
        label.setToolTipText(tips[i]);
        p[i].add(label);
      }
      gdl.setConstraints(p[i], gbc);
      gd.add(p[i]);
    }
    Util.addScrollBars(gd);
    if (cred.isOMERO) {
      OMEROLoader l = new OMEROLoader(ids, cred, p, gd, tips);
      gd.showDialog();
      l.stop();
    }
    else gd.showDialog();
    if (gd.wasCanceled()) return null;

    boolean[] checked = new boolean[ids.length];
    int numChecked = 0;
    for (int i=0; i<ids.length; i++) {
      checked[i] = gd.getNextBoolean();
      if (checked[i]) numChecked++;
    }

    long[] results = new long[numChecked];
    int n = 0;
    for (int i=0; i<ids.length; i++) {
      if (checked[i]) results[n++] = ids[i];
    }
    return results;
  }

  // -- OME-specific helper methods --

  private static void getAllImages() throws ReflectException {
    if (omePixelsInitialized) return;
    r.exec("c = new Criteria()");
    setImageCriteria();
    try {
      r.setVar("imageClass", Class.forName("org.openmicroscopy.ds.dto.Image"));
    }
    catch (ClassNotFoundException e) {
      throw new ReflectException(e);
    }
    r.exec("l = df.retrieveList(imageClass, c)");
    r.exec("len = l.size()");
    omePixelsInitialized = true;
  }

  private static int[] getOMEIntValues(String func) throws ReflectException {
    getAllImages();
    int len = ((Integer) r.getVar("len")).intValue();
    int[] rtn = new int[len];
    for (int i=0; i<len; i++) {
      r.setVar("i", i);
      r.exec("omeImage = l.get(i)");
      r.exec("omePix = omeImage.getDefaultPixels()");
      r.exec("v = omePix." + func + "()");
      rtn[i] = ((Integer) r.getVar("v")).intValue();
    }
    return rtn;
  }

  private static String[] getOMEStringValues(String func)
    throws ReflectException
  {
    getAllImages();
    int len = ((Integer) r.getVar("len")).intValue();
    String[] rtn = new String[len];
    for (int i=0; i<len; i++) {
      r.setVar("i", i);
      r.exec("omeImage = l.get(i)");
      r.exec("omePix = omeImage.getDefaultPixels()");
      r.exec("v = omePix." + func + "()");
      rtn[i] = (String) r.getVar("v");
    }
    return rtn;
  }

  private static void setImageCriteria() throws ReflectException {
    r.setVar("OME_ID", "id");
    r.setVar("OME_NAME", "name");
    r.setVar("OME_DESCRIPTION", "description");
    r.setVar("OME_INSERTED", "inserted");
    r.setVar("OME_CREATED", "created");
    r.setVar("OME_OWNER", "owner");
    r.setVar("OME_DEFAULT_PIXELS", "default_pixels");
    r.setVar("OME_FIRST_NAME", "FirstName");
    r.setVar("OME_LAST_NAME", "LastName");
    r.setVar("OME_SIZE_X", "SizeX");
    r.setVar("OME_SIZE_Y", "SizeY");
    r.setVar("OME_SIZE_Z", "SizeZ");
    r.setVar("OME_SIZE_C", "SizeC");
    r.setVar("OME_SIZE_T", "SizeT");
    r.setVar("OME_PIXEL_TYPE", "PixelType");
    r.setVar("OME_REPOSITORY", "Repository");
    r.setVar("OME_IS_ID", "ImageServerID");
    r.setVar("OME_DP_REPOSITORY", "default_pixels.Repository");
    r.setVar("OME_IS_URL", "ImageServerURL");
    r.exec("c.addWantedField(OME_ID)");
    r.exec("c.addWantedField(OME_NAME)");
    r.exec("c.addWantedField(OME_DESCRIPTION)");
    r.exec("c.addWantedField(OME_INSERTED)");
    r.exec("c.addWantedField(OME_CREATED)");
    r.exec("c.addWantedField(OME_OWNER)");
    r.exec("c.addWantedField(OME_DEFAULT_PIXELS)");
    r.exec("c.addWantedField(OME_OWNER, OME_FIRST_NAME)");
    r.exec("c.addWantedField(OME_OWNER, OME_LAST_NAME)");
    r.exec("c.addWantedField(OME_OWNER, OME_ID)");
    r.exec("c.addWantedField(OME_DEFAULT_PIXELS, OME_ID)");
    r.exec("c.addWantedField(OME_DEFAULT_PIXELS, OME_SIZE_X)");
    r.exec("c.addWantedField(OME_DEFAULT_PIXELS, OME_SIZE_Y)");
    r.exec("c.addWantedField(OME_DEFAULT_PIXELS, OME_SIZE_Z)");
    r.exec("c.addWantedField(OME_DEFAULT_PIXELS, OME_SIZE_C)");
    r.exec("c.addWantedField(OME_DEFAULT_PIXELS, OME_SIZE_T)");
    r.exec("c.addWantedField(OME_DEFAULT_PIXELS, OME_PIXEL_TYPE)");
    r.exec("c.addWantedField(OME_DEFAULT_PIXELS, OME_REPOSITORY)");
    r.exec("c.addWantedField(OME_DEFAULT_PIXELS, OME_IS_ID)");
    r.exec("c.addWantedField(OME_DP_REPOSITORY, OME_IS_URL)");
  }

  // -- OMERO-specific helper methods --

  private static void getAllPixels() throws ReflectException {
    if (omeroPixelsInitialized) return;
    r.exec("uid = eventContext.getCurrentUserId()");
    r.exec("filter = new Filter()");
    r.exec("filter = filter.owner(uid)");
    r.exec("query = sf.getQueryService()");
    r.setVar("q", "select p from Pixels as p " +
      "left outer join fetch p.pixelsDimensions " +
      "left outer join fetch p.pixelsType " +
      "left outer join fetch p.channels " +
      "left outer join fetch p.image");
    r.exec("params = new Parameters(filter)");
    r.exec("results = query.findAllByQuery(q, params)");

    List results = (List) r.getVar("results");
    long uid = ((Long) r.getVar("uid")).longValue();
    for (int i=0; i<results.size(); i++) {
      r.setVar("i", i);
      r.exec("obj = results.get(i)");
      r.exec("obj = new PixelsData(obj)");
      r.exec("owner = obj.getOwner()");
      r.exec("id = owner.getId()");
      long testId = ((Long) r.getVar("id")).longValue();
      if (testId != uid) {
        results.remove(i);
        i--;
      }
    }
    r.exec("len = results.size()");
    omeroPixelsInitialized = true;
  }

  private static int[] getIntValues(String func) throws ReflectException {
    getAllPixels();
    int len = ((Integer) r.getVar("len")).intValue();
    int[] rtn = new int[len];
    for (int i=0; i<len; i++) {
      r.setVar("i", i);
      r.exec("obj = results.get(i)");
      r.exec("obj = new PixelsData(obj)");
      r.exec("v = obj." + func + "()");
      rtn[i] = ((Integer) r.getVar("v")).intValue();
    }
    return rtn;
  }

  private static long[] getLongValues(String func) throws ReflectException {
    getAllPixels();
    int len = ((Integer) r.getVar("len")).intValue();
    long[] rtn = new long[len];
    for (int i=0; i<len; i++) {
      r.setVar("i", i);
      r.exec("obj = results.get(i)");
      r.exec("obj = new PixelsData(obj)");
      r.exec("v = obj." + func + "()");
      rtn[i] = ((Long) r.getVar("v")).longValue();
    }
    return rtn;
  }

  private static String[] getStringValues(String func) throws ReflectException
  {
    getAllPixels();
    int len = ((Integer) r.getVar("len")).intValue();
    String[] rtn = new String[len];
    for (int i=0; i<len; i++) {
      r.setVar("i", i);
      r.exec("obj = results.get(i)");
      r.exec("obj = new PixelsData(obj)");
      r.exec("v = obj." + func + "()");
      rtn[i] = (String) r.getVar("v");
    }
    return rtn;
  }

  // -- Helper class --

  class OMEROLoader implements Runnable {
    private long[] ids;
    private Panel[] p;
    private GenericDialog gd;
    private boolean stop;
    private Thread loader;
    private OMECredentials cred;
    private String[] tips;

    // TODO : consolidate with ThumbLoader

    public OMEROLoader(long[] ids, OMECredentials cred, Panel[] p,
      GenericDialog gd, String[] tips)
    {
      this.ids = ids;
      this.p = p;
      this.gd = gd;
      this.cred = cred;
      this.tips = tips;

      loader = new Thread(this, "OMERO-ThumbLoader");
      loader.start();
    }

    public void stop() {
      if (loader == null) return;
      stop = true;
      try {
        loader.join();
        loader = null;
      }
      catch (InterruptedException exc) {
        exc.printStackTrace();
      }
    }

    public void run() {
      try {
        OMEROReader r = new OMEROReader();
        for (int i=0; i<ids.length; i++) {
          r.setId("server=" + cred.server + "\nusername=" + cred.username +
            "\npassword=" + cred.password + "\nport=" + cred.port + "\nid=" +
            ids[i]);
          BufferedImage thumb = r.openThumbImage(0);
          ImageIcon icon = new ImageIcon(thumb);
          p[i].removeAll();
          JLabel label = new JLabel(icon);
          label.setToolTipText(tips[i]);
          p[i].add(label);
          if (gd != null) gd.validate();
        }
      }
      catch (FormatException exc) {
        exc.printStackTrace();
      }
      catch (IOException exc) {
        exc.printStackTrace();
      }
    }

  }

}

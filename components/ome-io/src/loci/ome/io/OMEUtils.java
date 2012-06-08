/*
 * #%L
 * OME database I/O package for communicating with OME and OMERO servers.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.ome.io;

import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import loci.common.ReflectException;
import loci.common.ReflectedUniverse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for retrieving data from an OME database.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-io/src/loci/ome/io/OMEUtils.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-io/src/loci/ome/io/OMEUtils.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public final class OMEUtils {

  // -- Constants --

  /** URL of OMERO JAR downloads. */
  public static final String URL_OMERO_JARS =
    "http://dev.loci.wisc.edu/svn/java/trunk/jar/";

  /** URL of OME-Java web page. */
  public static final String URL_OME_JAVA = "http://www.openmicroscopy.org/" +
    "site/support/legacy/ome-server/developer/java-api";

  static final String NO_OMERO_MSG = "OMERO client libraries not " +
    "found.  Please obtain omero-common.jar and omero-client.jar from " +
    URL_OMERO_JARS;

  static final String NO_OME_MSG = "OME-Java library not found.  " +
    "Please obtain ome-java.jar from " + URL_OME_JAVA;

  private static final Logger LOGGER = LoggerFactory.getLogger(OMEUtils.class);

  // -- Static fields --

  private static boolean noOME = false;
  private static boolean noOMERO = false;
  private static ReflectedUniverse r = createReflectedUniverse();
  private static ReflectedUniverse createReflectedUniverse() {
    r = new ReflectedUniverse();
    // NB: avoid dependencies on optional ome.api packages
    // NB: avoid dependencies on optional ome.system packages
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
      LOGGER.info(NO_OMERO_MSG, e);
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
      LOGGER.info(NO_OME_MSG, e);
    }
    return r;
  }

  private static boolean loggedIn = false;
  private static boolean omePixelsInitialized = false;
  private static boolean omeroPixelsInitialized = false;

  // -- Constructor --

  /** Disallow instantiation of utility class. */
  private OMEUtils() { }

  // -- OMEUtils API methods --

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
      try {
        r.exec("v = pf.getThumbnail(omePix)");
        rtn[i] = (BufferedImage) r.getVar("v");
      }
      catch (ReflectException e) {
        rtn[i] = null;
      }
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
      try {
        r.exec("v = omePix." + func + "()");
        rtn[i] = ((Integer) r.getVar("v")).intValue();
      }
      catch (ReflectException e) {
        rtn[i] = -1;
      }
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
      try {
        r.exec("v = omePix." + func + "()");
        rtn[i] = (String) r.getVar("v");
      }
      catch (ReflectException e) {
        rtn[i] = "";
      }
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

  private static String[] getStringValues(String func) throws ReflectException {
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

}

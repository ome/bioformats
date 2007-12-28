//
// OMEReader.java
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

import java.awt.image.BufferedImage;
import java.io.*;
import loci.formats.*;

/**
 * OMEReader retrieves images on demand from an OME database.
 * Authentication with the OME server is handled, provided the 'id' parameter
 * is properly formed.
 * The 'id' parameter should be:
 *
 * [server]?user=[username]&password=[password]&id=[image id]
 *
 * where [server] is the URL of the OME data server (not the image server).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ome/OMEReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ome/OMEReader.java">SVN</a></dd></dl>
 */
public class OMEReader extends FormatReader {

  // -- Constants --

  /** Message to display if OME-Java is not found. */
  private static final String NO_OME_JAVA = "OME-Java not found.  Please " +
    "download ome-java.jar from http://www.loci.wisc.edu/ome/formats.html";

  // -- Fields --

  /** Authentication credentials. */
  private OMECredentials credentials;

  // -- Static fields --

  private static boolean hasOMEJava = true;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import java.awt.image.BufferedImage");
      r.exec("import java.lang.Class");
      r.exec("import java.util.List");
      r.exec("import org.openmicroscopy.ds.Criteria");
      r.exec("import org.openmicroscopy.ds.DataFactory");
      r.exec("import org.openmicroscopy.ds.DataServer");
      r.exec("import org.openmicroscopy.ds.DataServices");
      r.exec("import org.openmicroscopy.ds.FieldsSpecification");
      r.exec("import org.openmicroscopy.ds.RemoteCaller");
      r.exec("import org.openmicroscopy.ds.dto.Image");
      r.exec("import org.openmicroscopy.ds.st.Pixels");
      r.exec("import org.openmicroscopy.ds.st.Repository");
      r.exec("import org.openmicroscopy.is.PixelsFactory");
    }
    catch (ReflectException e) {
      if (debug) LogTools.trace(e);
      hasOMEJava = false;
    }
    return r;
  }

  // -- Constructor --

  /** Constructs a new OME reader. */
  public OMEReader() { super("Open Microscopy Environment (OME)", "*"); }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (id.equals(currentId)) return;

    if (!hasOMEJava) throw new FormatException(NO_OME_JAVA);

    credentials = OMEUtils.parseCredentials(id);
    id = String.valueOf(credentials.imageID);

    super.initFile(id);

    // do sanity check on server name
    if (credentials.server.startsWith("http:")) {
      credentials.server = credentials.server.substring(5);
    }
    while (credentials.server.startsWith("/")) {
      credentials.server = credentials.server.substring(1);
    }
    int slash = credentials.server.indexOf("/");
    if (slash >= 0) credentials.server = credentials.server.substring(0, slash);
    int colon = credentials.server.indexOf(":");
    if (colon >= 0) credentials.server = credentials.server.substring(0, colon);

    currentId = credentials.server + ":" + credentials.imageID;

    String omeis = "http://" + credentials.server + "/cgi-bin/omeis";
    credentials.server = "http://" + credentials.server + "/shoola/";
    credentials.isOMERO = false;

    String user = credentials.username;
    String pass = credentials.password;

    try {
      r.exec("c = new Criteria()");
      r.setVar("ID", "id");
      r.setVar("DEFAULT_PIXELS", "default_pixels");
      r.setVar("PIXEL_TYPE", "PixelType");
      r.setVar("SIZE_X", "SizeX");
      r.setVar("SIZE_Y", "SizeY");
      r.setVar("SIZE_Z", "SizeZ");
      r.setVar("SIZE_C", "SizeC");
      r.setVar("SIZE_T", "SizeT");
      r.setVar("IMAGE_SERVER_ID", "ImageServerID");
      r.setVar("REPOSITORY", "Repository");
      r.setVar("IMAGE_SERVER_URL", "ImageServerURL");
      r.setVar("DEFAULT_PIXELS_REPOSITORY", "default_pixels.Repository");
      r.setVar("EQUALS", "=");
      r.setVar("IMAGE_ID", String.valueOf(credentials.imageID));
      r.exec("c.addWantedField(ID)");
      r.exec("c.addWantedField(DEFAULT_PIXELS)");
      r.exec("c.addWantedField(DEFAULT_PIXELS, PIXEL_TYPE)");
      r.exec("c.addWantedField(DEFAULT_PIXELS, SIZE_X)");
      r.exec("c.addWantedField(DEFAULT_PIXELS, SIZE_Y)");
      r.exec("c.addWantedField(DEFAULT_PIXELS, SIZE_Z)");
      r.exec("c.addWantedField(DEFAULT_PIXELS, SIZE_C)");
      r.exec("c.addWantedField(DEFAULT_PIXELS, SIZE_T)");
      r.exec("c.addWantedField(DEFAULT_PIXELS, IMAGE_SERVER_ID)");
      r.exec("c.addWantedField(DEFAULT_PIXELS, REPOSITORY)");
      r.exec("c.addWantedField(DEFAULT_PIXELS_REPOSITORY, IMAGE_SERVER_URL)");
      r.exec("c.addFilter(ID, EQUALS, IMAGE_ID)");

      r.exec("fs = new FieldsSpecification()");
      r.exec("fs.addWantedField(REPOSITORY)");
      r.exec("fs.addWantedField(REPOSITORY, IMAGE_SERVER_URL)");
      r.exec("c.addWantedFields(DEFAULT_PIXELS, fs)");

      r.setVar("server", credentials.server);
      r.exec("rs = DataServer.getDefaultServices(server)");
      r.exec("rc = rs.getRemoteCaller()");

      r.setVar("user", user);
      r.setVar("pass", pass);
      r.exec("rc.login(user, pass)");

      r.setVar("DATA_FACTORY_CLASS", "org.openmicroscopy.ds.DataFactory");
      r.exec("DATA_FACTORY_CLASS = Class.forName(DATA_FACTORY_CLASS)");
      r.setVar("PIXELS_FACTORY_CLASS", "org.openmicroscopy.is.PixelsFactory");
      r.exec("PIXELS_FACTORY_CLASS = Class.forName(PIXELS_FACTORY_CLASS)");
      r.exec("df = rs.getService(DATA_FACTORY_CLASS)");
      r.exec("pf = rs.getService(PIXELS_FACTORY_CLASS)");

      r.setVar("omeis", omeis);
      r.setVar("IMAGE_CLASS", "org.openmicroscopy.ds.dto.Image");
      r.exec("IMAGE_CLASS = Class.forName(IMAGE_CLASS)");
      r.setVar("zero", 0);

      r.exec("images = df.retrieveList(IMAGE_CLASS, c)");
      r.exec("img = images.get(zero)");
      r.exec("pixels = img.getDefaultPixels()");
      r.exec("repository = pixels.getRepository()");
      r.exec("repository.setImageServerURL(omeis)");

      r.exec("thumb = pf.getThumbnail(pixels)");

      r.exec("sizeX = pixels.getSizeX()");
      r.exec("sizeY = pixels.getSizeY()");
      r.exec("sizeZ = pixels.getSizeZ()");
      r.exec("sizeC = pixels.getSizeC()");
      r.exec("sizeT = pixels.getSizeT()");
      r.exec("pixelType = pixels.getPixelType()");

      core.sizeX[0] = ((Integer) r.getVar("sizeX")).intValue();
      core.sizeY[0] = ((Integer) r.getVar("sizeY")).intValue();
      core.sizeZ[0] = ((Integer) r.getVar("sizeZ")).intValue();
      core.sizeC[0] = ((Integer) r.getVar("sizeC")).intValue();
      core.sizeT[0] = ((Integer) r.getVar("sizeT")).intValue();
      core.pixelType[0] =
        FormatTools.pixelTypeFromString((String) r.getVar("pixelType"));
      core.currentOrder[0] = "XYZCT";

      core.imageCount[0] = core.sizeZ[0] * core.sizeC[0] * core.sizeT[0];
      core.rgb[0] = false;

      r.exec("thumbX = thumb.getWidth()");
      r.exec("thumbY = thumb.getHeight()");

      core.thumbSizeX[0] = ((Integer) r.getVar("thumbX")).intValue();
      core.thumbSizeY[0] = ((Integer) r.getVar("thumbY")).intValue();
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }

    core.littleEndian[0] = true;
    core.interleaved[0] = false;

    MetadataStore store = getMetadataStore();
    FormatTools.populatePixels(store, this);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null);
    }
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return true;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);
    int[] indices = getZCTCoords(no);

    r.setVar("zIndex", indices[0]);
    r.setVar("cIndex", indices[1]);
    r.setVar("tIndex", indices[2]);
    r.setVar("bigEndian", false);
    try {
      r.exec("buf = pf.getPlane(pixels, zIndex, cIndex, tIndex, bigEndian)");
      buf = (byte[]) r.getVar("buf");
    }
    catch (ReflectException e) {
      throw new FormatException(e);
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.checkPlaneNumber(this, no);
    byte[][] b = ImageTools.getPixelBytes(openThumbImage(no), true);
    byte[] rtn = new byte[b.length * b[0].length];
    for (int i=0; i<b.length; i++) {
      System.arraycopy(b[i], 0, rtn, i*b[0].length, b[i].length);
    }
    return rtn;
  }

  /* @see loci.formats.IFormatReader#openThumbImage(int) */
  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    try {
      return (BufferedImage) r.getVar("thumb");
    }
    catch (ReflectException e) {
      if (debug) LogTools.trace(e);
    }
    return super.openThumbImage(no);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    try {
      r.exec("rc.logout()");
    }
    catch (ReflectException e) { }
    if (!fileOnly) currentId = null;
  }

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    close(false);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String) */
  public boolean isThisType(String id) {
    return id.indexOf("id") != -1 && (id.indexOf("password") != -1 ||
      id.indexOf("sessionKey") != -1);
  }

}

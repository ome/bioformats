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
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;

/**
 * OMEReader retrieves images on demand from an OME database.
 * Authentication with the OME server is handled, provided the 'id' parameter
 * is properly formed.
 * The 'id' parameter should take one of the following forms:
 *
 * [server]?user=[username]&password=[password]&id=[image id]
 * [server]?key=[session key]&id=[image id]
 *
 * where [server] is the URL of the OME data server (not the image server).
 */
public class OMEReader extends FormatReader {

  // -- Fields --

  /** String containing authentication information. */
  private String loginString;

  /** Number of images. */
  private int numImages;

  /** Current server. */
  private String server;

  /** Session key for authentication. */
  private String sessionKey;

  /** OME image ID. */
  private String imageId;

  /** Thumbnail associated with the current dataset. */
  private BufferedImage thumb;

  /** Download helpers. */
  private DataServices rs;
  private RemoteCaller rc;
  private DataFactory df;
  private PixelsFactory pf;
  private Pixels pixels;

  // -- Constructor --

  /** Constructs a new OME reader. */
  public OMEReader() { super("Open Microscopy Environment (OME)", "*"); }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (id.equals(loginString)) return;
    super.initFile(id);

    loginString = id;
    server = id.substring(0, id.lastIndexOf("?"));
    int ndx = id.indexOf("&");
    String user = null;
    String pass = null;
    if (id.indexOf("user") != -1) {
      user = id.substring(id.lastIndexOf("?") + 6, ndx);
      pass = id.substring(ndx + 10, id.indexOf("&", ndx + 1));
      ndx = id.indexOf("&", ndx + 1);
      imageId = id.substring(ndx + 4);
    }
    else {
      sessionKey = id.substring(id.lastIndexOf("?") + 5, ndx);
      ndx = id.indexOf("&", ndx + 1);
      imageId = id.substring(ndx + 4);
    }

    // do sanity check on server name
    if (server.startsWith("http:")) {
      server = server.substring(5);
    }
    while (server.startsWith("/")) server = server.substring(1);
    int slash = server.indexOf("/");
    if (slash >= 0) server = server.substring(0, slash);
    int colon = server.indexOf(":");
    if (colon >= 0) server = server.substring(0, colon);

    currentId = server + ":" + imageId;

    server = "http://" + server + "/shoola/";

    Criteria c = new Criteria();
    c.addWantedField("id");
    c.addWantedField("default_pixels");
    c.addWantedField("default_pixels", "PixelType");
    c.addWantedField("default_pixels", "SizeX");
    c.addWantedField("default_pixels", "SizeY");
    c.addWantedField("default_pixels", "SizeZ");
    c.addWantedField("default_pixels", "SizeC");
    c.addWantedField("default_pixels", "SizeT");
    c.addWantedField("default_pixels", "ImageServerID");
    c.addWantedField("default_pixels", "Repository");
    c.addWantedField("default_pixels.Repository", "ImageServerURL");
    c.addFilter("id", "=", imageId);

    FieldsSpecification fs = new FieldsSpecification();
    fs.addWantedField("Repository");
    fs.addWantedField("Repository", "ImageServerURL");
    c.addWantedFields("default_pixels", fs);

    try {
      rs = DataServer.getDefaultServices(server);
    }
    catch (Exception e) { throw new FormatException(e); }

    rc = rs.getRemoteCaller();

    if (user != null && pass != null) rc.login(user, pass);
    else if (sessionKey != null) rc.setSessionKey(sessionKey);

    df = (DataFactory) rs.getService(DataFactory.class);
    pf = (PixelsFactory) rs.getService(PixelsFactory.class);

    Image img = (Image) df.retrieveList(Image.class, c).get(0);
    pixels = img.getDefaultPixels();

    try {
      thumb = pf.getThumbnail(pixels);
    }
    catch (ImageServerException e) {
      if (debug) e.printStackTrace();
    }

    core.sizeX[0] = pixels.getSizeX().intValue();
    core.sizeY[0] = pixels.getSizeY().intValue();
    core.sizeZ[0] = pixels.getSizeZ().intValue();
    core.sizeC[0] = pixels.getSizeC().intValue();
    core.sizeT[0] = pixels.getSizeT().intValue();
    core.pixelType[0] = FormatTools.pixelTypeFromString(pixels.getPixelType());
    core.currentOrder[0] = "XYZCT";

    numImages = core.sizeZ[0] * core.sizeC[0] * core.sizeT[0];

    MetadataStore store = getMetadataStore();
    store.setPixels(
      new Integer(core.sizeX[0]),
      new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]),
      new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]),
      new Integer(core.pixelType[0]),
      new Boolean(!isLittleEndian()),
      core.currentOrder[0],
      null,
      null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return true;
  }

  /* @see loci.formats.IFormatReader#getImageCount() */
  public int getImageCount() throws FormatException, IOException {
    return numImages;
  }

  /* @see loci.formats.IFormatReader#isRGB() */
  public boolean isRGB() throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#getThumbSizeX() */
  public int getThumbSizeX() throws FormatException, IOException {
    return thumb.getWidth();
  }

  /* @see loci.formats.IFormatReader#getThumbSizeY() */
  public int getThumbSizeY() throws FormatException, IOException {
    return thumb.getHeight();
  }

  /* @see loci.formats.IFormatReader#isLittleEndian() */
  public boolean isLittleEndian() throws FormatException, IOException {
    return true;
  }

  /* @see loci.formats.IFormatReader#isInterleaved(int) */
  public boolean isInterleaved(int subC) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    if (no < 0 || no >= numImages) {
      throw new FormatException("Invalid image number: " + no);
    }
    int[] indices = getZCTCoords(no);
    try {
      byte[] b = pf.getPlane(pixels, indices[0], indices[1], indices[2], false);
      return b;
    }
    catch (ImageServerException e) {
      throw new FormatException(e);
    }
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    return ImageTools.makeImage(openBytes(no), core.sizeX[0],
      core.sizeY[0], 1, false, FormatTools.getBytesPerPixel(core.pixelType[0]),
      true);
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    if (no < 0 || no >= numImages) {
      throw new FormatException("Invalid image number: " + no);
    }
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
    if (no < 0 || no >= numImages) {
      throw new FormatException("Invalid image number: " + no);
    }
    return thumb;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && rc != null) rc.logout();
    else close();
  }

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws FormatException, IOException {
    if (rc != null) rc.logout();
    thumb = null;
    rs = null;
    rc = null;
    df = null;
    pf = null;
    pixels = null;
    currentId = null;
    loginString = null;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String) */
  public boolean isThisType(String id) {
    return id.indexOf("id") != -1 && (id.indexOf("password") != -1 ||
      id.indexOf("sessionKey") != -1);
  }

}

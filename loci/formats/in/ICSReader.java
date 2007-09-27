//
// ICSReader.java
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

package loci.formats.in;

import java.io.*;
import java.util.StringTokenizer;
import java.util.zip.*;
import loci.formats.*;
import loci.formats.codec.ByteVector;

/**
 * ICSReader is the file format reader for ICS (Image Cytometry Standard)
 * files. More information on ICS can be found at http://libics.sourceforge.net
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/ICSReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/ICSReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */

public class ICSReader extends FormatReader {

  // -- Constants --

  /** Metadata field categories. */
  private static final String[] CATEGORIES = new String[] {
    "ics_version", "filename", "source", "layout", "representation",
    "parameter", "sensor", "history", "document", "view", "end"
  };

  /** Metadata field subcategories. */
  private static final String[] SUB_CATEGORIES = new String[] {
    "file", "offset", "parameters", "order", "sizes", "coordinates",
    "significant_bits", "format", "sign", "compression", "byte_order",
    "origin", "scale", "units", "labels", "SCIL_TYPE", "type", "model",
    "s_params", "laser", "gain1", "gain2", "gain3", "gain4", "dwell",
    "shutter1", "shutter2", "shutter3", "pinhole", "laser1", "laser2",
    "laser3", "objective", "PassCount", "step1", "step2", "step3", "view",
    "view1", "date", "GMTdate", "label"
  };

  /** Metadata field sub-subcategories. */
  private static final String[] SUB_SUB_CATEGORIES = new String[] {
    "Channels", "PinholeRadius", "LambdaEx", "LambdaEm", "ExPhotonCnt",
    "RefInxMedium", "NumAperture", "RefInxLensMedium", "PinholeSpacing",
    "power", "wavelength", "name", "Type", "Magnification", "NA",
    "WorkingDistance", "Immersion", "Pinhole", "Channel 1", "Channel 2",
    "Channel 3", "Channel 4", "Gain 1", "Gain 2", "Gain 3", "Gain 4",
    "Shutter 1", "Shutter 2", "Shutter 3", "Position", "Size", "Port",
    "Cursor", "Color", "BlackLevel", "Saturation", "Gamma", "IntZoom",
    "Live", "Synchronize", "ShowIndex", "AutoResize", "UseUnits", "Zoom",
    "IgnoreAspect", "ShowCursor", "ShowAll", "Axis", "Order", "Tile", "scale",
    "DimViewOption"
  };

  // -- Fields --

  /** Current filename. */
  protected String currentIcsId;
  protected String currentIdsId;

  /** Current ICS file. */
  protected Location icsIn;

  /** Number of bits per pixel. */
  protected int bitsPerPixel;

  /** Flag indicating whether current file is v2.0. */
  protected boolean versionTwo;

  /** Image data. */
  protected byte[] data;

  // -- Constructor --

  /** Constructs a new ICSReader. */
  public ICSReader() {
    super("Image Cytometry Standard", new String[] {"ics", "ids"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    int bpp = bitsPerPixel / 8;

    int len = core.sizeX[0] * core.sizeY[0] * bpp * getRGBChannelCount();
    int offset = len * no;
    if (!core.rgb[0] && core.sizeC[0] > 4) {
      int pt = 0;
      for (int i=no*bpp; i<data.length; i+=core.sizeC[0]*bpp) {
        System.arraycopy(data, i, buf, pt, bpp);
        pt += bpp;
      }
    }
    else System.arraycopy(data, offset, buf, 0, len);

    return buf;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    if (versionTwo) {
      return new String[] {currentIdsId == null ? "" : currentIdsId};
    }
    return new String[] {currentIdsId, currentIcsId};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    icsIn = null;
    currentIcsId = null;
    currentIdsId = null;
    data = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ICSReader.initFile(" + id + ")");
    super.initFile(id);

    status("Finding companion file");

    //String icsId = l.getPath(), idsId = l.getPath();
    String icsId = id, idsId = id;
    int dot = id.lastIndexOf(".");
    String ext = dot < 0 ? "" : id.substring(dot + 1).toLowerCase();
    if (ext.equals("ics")) {
      // convert C to D regardless of case
      char[] c = idsId.toCharArray();
      c[c.length - 2]++;
      idsId = new String(c);
    }
    else if (ext.equals("ids")) {
      // convert D to C regardless of case
      char[] c = icsId.toCharArray();
      c[c.length - 2]--;
      icsId = new String(c);
    }

    if (icsId == null) throw new FormatException("No ICS file found.");
    Location icsFile = new Location(icsId);
    if (!icsFile.exists()) throw new FormatException("ICS file not found.");

    status("Checking file version");

    // check if we have a v2 ICS file
    RandomAccessStream f = new RandomAccessStream(icsId);
    byte[] b = new byte[17];
    f.read(b);
    f.close();
    if (new String(b).trim().equals("ics_version\t2.0")) {
      in = new RandomAccessStream(icsId);
      versionTwo = true;
    }
    else {
      if (idsId == null) throw new FormatException("No IDS file found.");
      Location idsFile = new Location(idsId);
      if (!idsFile.exists()) throw new FormatException("IDS file not found.");
      currentIdsId = idsId;
      in = new RandomAccessStream(idsId);
    }

    currentIcsId = icsId;

    icsIn = icsFile;

    status("Reading metadata");

    String layoutSizes = null, layoutOrder = null, byteOrder = null;
    String rFormat = null, compression = null, scale = null;

    RandomAccessStream reader = new RandomAccessStream(icsIn.getAbsolutePath());
    StringTokenizer t;
    String token;
    String s = reader.readString((int) reader.length());
    reader.close();
    StringTokenizer st = new StringTokenizer(s, "\n");
    String line = st.nextToken();
    line = st.nextToken();
    while (line != null && !line.trim().equals("end")) {
      t = new StringTokenizer(line);
      StringBuffer key = new StringBuffer();
      while (t.hasMoreTokens()) {
        token = t.nextToken();
        boolean foundValue = true;
        for (int i=0; i<CATEGORIES.length; i++) {
          if (token.equals(CATEGORIES[i])) foundValue = false;
        }
        for (int i=0; i<SUB_CATEGORIES.length; i++) {
          if (token.equals(SUB_CATEGORIES[i])) foundValue = false;
        }
        for (int i=0; i<SUB_SUB_CATEGORIES.length; i++) {
          if (token.equals(SUB_SUB_CATEGORIES[i])) foundValue = false;
        }

        if (foundValue) {
          StringBuffer value = new StringBuffer();
          value.append(token);
          while (t.hasMoreTokens()) {
            value.append(" ");
            value.append(t.nextToken());
          }
          String k = key.toString().trim();
          String v = value.toString().trim();
          addMeta(k, v);

          if (k.equals("layout sizes")) layoutSizes = v;
          else if (k.equals("layout order")) layoutOrder = v;
          else if (k.equals("representation byte_order")) byteOrder = v;
          else if (k.equals("representation format")) rFormat = v;
          else if (k.equals("representation compression")) compression = v;
          else if (k.equals("parameter scale")) scale = v;
        }
        else {
          key.append(token);
          key.append(" ");
        }
      }
      if (st.hasMoreTokens()) line = st.nextToken();
      else line = null;
    }

    status("Populating metadata");

    layoutOrder = layoutOrder.trim();
    // bpp, width, height, z, channels
    StringTokenizer t1 = new StringTokenizer(layoutSizes);
    StringTokenizer t2 = new StringTokenizer(layoutOrder);

    core.rgb[0] = layoutOrder.indexOf("ch") >= 0 &&
      layoutOrder.indexOf("ch") < layoutOrder.indexOf("x");

    String imageToken;
    String orderToken;
    while (t1.hasMoreTokens() && t2.hasMoreTokens()) {
      imageToken = t1.nextToken().trim();
      orderToken = t2.nextToken().trim();
      if (orderToken.equals("bits")) {
        bitsPerPixel = Integer.parseInt(imageToken);
      }
      else if (orderToken.equals("x")) {
        core.sizeX[0] = Integer.parseInt(imageToken);
      }
      else if (orderToken.equals("y")) {
        core.sizeY[0] = Integer.parseInt(imageToken);
      }
      else if (orderToken.equals("z")) {
        core.sizeZ[0] = Integer.parseInt(imageToken);
      }
      else if (orderToken.equals("ch")) {
        core.sizeC[0] = Integer.parseInt(imageToken);
        if (core.sizeC[0] > 4) core.rgb[0] = false;
      }
      else {
        core.sizeT[0] = Integer.parseInt(imageToken);
      }
    }

    if (core.sizeZ[0] == 0) core.sizeZ[0] = 1;
    if (core.sizeC[0] == 0) core.sizeC[0] = 1;
    if (core.sizeT[0] == 0) core.sizeT[0] = 1;

    if (core.imageCount[0] == 0) core.imageCount[0] = 1;
    core.rgb[0] = core.rgb[0] && core.sizeC[0] > 1;
    core.interleaved[0] = core.rgb[0];
    core.imageCount[0] = core.sizeZ[0] * core.sizeT[0];
    if (!core.rgb[0]) core.imageCount[0] *= core.sizeC[0];
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.metadataComplete[0] = true;

    String endian = byteOrder;
    core.littleEndian[0] = true;

    if (endian != null) {
      StringTokenizer endianness = new StringTokenizer(endian);
      String firstByte = endianness.nextToken();
      int first = Integer.parseInt(firstByte);
      core.littleEndian[0] = rFormat.equals("real") ? first == 1 : first != 1;
    }

    String test = compression;
    boolean gzip = (test == null) ? false : test.equals("gzip");

    if (versionTwo) {
      s = in.readLine();
      while(!s.trim().equals("end")) s = in.readLine();
    }
    data = new byte[(int) (in.length() - in.getFilePointer())];

    // extra check is because some of our datasets are labeled as 'gzip', and
    // have a valid GZIP header, but are actually uncompressed
    if (gzip && ((data.length / (core.imageCount[0]) <
      (core.sizeX[0] * core.sizeY[0] * bitsPerPixel / 8))))
    {
      status("Decompressing pixel data");
      in.read(data);
      byte[] buf = new byte[8192];
      ByteVector v = new ByteVector();
      try {
        GZIPInputStream decompressor =
          new GZIPInputStream(new ByteArrayInputStream(data));
        int r = decompressor.read(buf, 0, buf.length);
        while (r > 0) {
          v.add(buf, 0, r);
          r = decompressor.read(buf, 0, buf.length);
        }
        data = v.toByteArray();
      }
      catch (IOException dfe) {
        throw new FormatException("Error uncompressing gzip'ed data", dfe);
      }
    }
    else in.readFully(data);

    status("Populating metadata");

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    String fname = (String) getMeta("filename");
    if (fname == null) fname = currentId;
    store.setImage(fname, null, null, null);

    // populate Pixels element

    String o = layoutOrder;
    o = o.trim();
    o = o.substring(o.indexOf("x")).trim();
    char[] tempOrder = new char[(o.length() / 2) + 1];
    int pt = 0;
    for (int i=0; i<o.length(); i+=2) {
      tempOrder[pt] = o.charAt(i);
      pt++;
    }
    o = new String(tempOrder).toUpperCase().trim();
    if (o.indexOf("Z") == -1) o = o + "Z";
    if (o.indexOf("T") == -1) o = o + "T";
    if (o.indexOf("C") == -1) o = o + "C";

    String fmt = rFormat;

    if (bitsPerPixel < 32) core.littleEndian[0] = !core.littleEndian[0];

    if (fmt.equals("real")) core.pixelType[0] = FormatTools.FLOAT;
    else if (fmt.equals("integer")) {
      while (bitsPerPixel % 8 != 0) bitsPerPixel++;
      if (bitsPerPixel == 24 || bitsPerPixel == 48) bitsPerPixel /= 3;

      switch (bitsPerPixel) {
        case 8:
          core.pixelType[0] = FormatTools.UINT8;
          break;
        case 16:
          core.pixelType[0] = FormatTools.UINT16;
          break;
        case 32:
          core.pixelType[0] = FormatTools.UINT32;
          break;
      }
    }
    else {
      throw new RuntimeException("Unknown pixel format: " + format);
    }

    core.currentOrder[0] = o.trim();

    FormatTools.populatePixels(store, this);

    String pixelSizes = scale;
    o = layoutOrder;
    if (pixelSizes != null) {
      StringTokenizer pixelSizeTokens = new StringTokenizer(pixelSizes);
      StringTokenizer axisTokens = new StringTokenizer(o);

      Float pixX = null, pixY = null, pixZ = null, pixC = null, pixT = null;

      while (pixelSizeTokens.hasMoreTokens()) {
        String axis = axisTokens.nextToken().trim().toLowerCase();
        String size = pixelSizeTokens.nextToken().trim();
        if (axis.equals("x")) pixX = new Float(size);
        else if (axis.equals("y")) pixY = new Float(size);
        else if (axis.equals("ch")) pixC = new Float(size);
        else if (axis.equals("z")) pixZ = new Float(size);
        else if (axis.equals("t")) pixT = new Float(size);
      }
      store.setDimensions(pixX, pixY, pixZ, pixC, pixT, null);
    }

    String em = (String) getMeta("sensor s_params LambdaEm");
    String ex = (String) getMeta("sensor s_params LambdaEx");
    int[] emWave = new int[core.sizeC[0]];
    int[] exWave = new int[core.sizeC[0]];
    if (em != null) {
      StringTokenizer emTokens = new StringTokenizer(em);
      for (int i=0; i<core.sizeC[0]; i++) {
        emWave[i] = (int) Float.parseFloat(emTokens.nextToken().trim());
      }
    }
    if (ex != null) {
      StringTokenizer exTokens = new StringTokenizer(ex);
      for (int i=0; i<core.sizeC[0]; i++) {
        exWave[i] = (int) Float.parseFloat(exTokens.nextToken().trim());
      }
    }

    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
       null, null, null, null, null, null, null, null, null, null, null,
       new Integer(emWave[i]), new Integer(exWave[i]), null, null, null);
    }
  }

}

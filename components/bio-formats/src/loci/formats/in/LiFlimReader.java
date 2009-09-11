//
// LiFlimReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats.in;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.LogTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * LiFlimReader is the file format reader for LI-FLIM files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/LiFlimReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/LiFlimReader.java">SVN</a></dd></dl>
 */
public class LiFlimReader extends FormatReader {

  // -- Constants --

  // INI keys
  public static final String VERSION_KEY = "FLIMIMAGE: INFO - version";
  public static final String COMPRESSION_KEY = "FLIMIMAGE: INFO - compression";
  public static final String DATATYPE_KEY = "FLIMIMAGE: LAYOUT - datatype";
  public static final String C_KEY = "FLIMIMAGE: LAYOUT - channels";
  public static final String X_KEY = "FLIMIMAGE: LAYOUT - x";
  public static final String Y_KEY = "FLIMIMAGE: LAYOUT - y";
  public static final String Z_KEY = "FLIMIMAGE: LAYOUT - z";
  public static final String P_KEY = "FLIMIMAGE: LAYOUT - phases";
  public static final String F_KEY = "FLIMIMAGE: LAYOUT - frequencies";
  public static final String T_KEY = "FLIMIMAGE: LAYOUT - timestamps";

  // supported versions
  public static final String[] KNOWN_VERSIONS = {"1.0"};

  // compression types
  public static final String COMPRESSION_NONE = "0";
  public static final String COMPRESSION_GZIP = "1";

  // data types
  public static final String DATATYPE_UINT8 = "UINT8";
  public static final String DATATYPE_INT8 = "INT8";
  public static final String DATATYPE_UINT16 = "UINT16";
  public static final String DATATYPE_INT16 = "INT16";
  public static final String DATATYPE_UINT32 = "UINT32";
  public static final String DATATYPE_INT32 = "INT32";
  public static final String DATATYPE_REAL32 = "REAL32";
  public static final String DATATYPE_REAL64 = "REAL64";

  // -- Fields --

  /** Offset to start of pixel data. */
  private long dataOffset;

  /** Parsed configuration data. */
  private IniList ini;

  private String version;
  private String compression;
  private String datatype;
  private String channels;
  private String xLen;
  private String yLen;
  private String zLen;
  private String phases;
  private String frequencies;
  private String timestamps;

  /** True if gzip compression was used to deflate the pixels. */
  private boolean gzip;

  /** Stream to use for reading gzip-compressed pixel data. */
  private DataInputStream gz;

  /** Image number indicating position in gzip stream. */
  private int gzPos;

  // -- Constructor --

  /** Constructs a new LI-FLIM reader. */
  public LiFlimReader() {
    super("LI-FLIM", "fli");
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int sizeX = getSizeX();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int bytesPerPlane = getSizeX() * getSizeY() * bpp;

    int size = bpp * w * h;

    if (gzip) {
      prepareGZipStream(no);

      // read compressed data
      byte[] bytes = new byte[bytesPerPlane];
      gz.readFully(bytes);

      // crop data
      if (w == sizeX) {
        // copy data directly, in one block
        System.arraycopy(bytes, sizeX * y, buf, 0, buf.length);
      }
      else {
        // copy data line by line
        for (int r=0; r<h; r++) {
          int bytesIndex = bpp * (sizeX * (r + y) + x);
          int bufIndex = bpp * w * r;
          System.arraycopy(bytes, bytesIndex, buf, bufIndex, bpp * w);
        }
      }
    }
    else if (w == sizeX) {
      // read data directly, in one block
      int scan = bpp * sizeX;
      in.seek(dataOffset + bytesPerPlane * no + scan * y);
      in.readFully(buf, 0, size);
    }
    else {
      // read data line by line
      int scan = bpp * sizeX;
      in.seek(dataOffset + bytesPerPlane * no + scan * y + bpp * x);
      int read = bpp * w;
      for (int i=0; i<size; i+=read) {
        in.readFully(buf, i, read);
        in.skipBytes(scan - read);
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      dataOffset = 0;
      ini = null;
      gzip = false;
      if (gz != null) gz.close();
      gz = null;
      gzPos = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("LiFlimReader.initFile(" + id + ")");
    super.initFile(id);

    status("Parsing header");
    in = new RandomAccessInputStream(id);
    parseHeader();

    status("Parsing metadata");
    initOriginalMetadata();
    initCoreMetadata();
    initOMEMetadata();
  }

  // -- Helper methods --

  private void parseHeader() throws IOException {
    String headerData = in.findString("{END}");
    dataOffset = in.getFilePointer();
    IniParser parser = new IniParser();
    ini = parser.parseINI(new BufferedReader(new StringReader(headerData)));
  }

  private void initOriginalMetadata() {
    // add all INI entries to the global metadata list
    for (IniTable table : ini) {
      String name = table.get(IniTable.HEADER_KEY);
      for (String key : table.keySet()) {
        if (key.equals(IniTable.HEADER_KEY)) continue;
        String value = table.get(key);
        String metaKey = name + " - " + key;
        addGlobalMeta(metaKey, value);

        // save important values regardless of isMetadataCollected()
        if (metaKey.equals(VERSION_KEY)) version = value;
        else if (metaKey.equals(COMPRESSION_KEY)) compression = value;
        else if (metaKey.equals(DATATYPE_KEY)) datatype = value;
        else if (metaKey.equals(C_KEY)) channels = value;
        else if (metaKey.equals(X_KEY)) xLen = value;
        else if (metaKey.equals(Y_KEY)) yLen = value;
        else if (metaKey.equals(Z_KEY)) zLen = value;
        else if (metaKey.equals(P_KEY)) phases = value;
        else if (metaKey.equals(F_KEY)) frequencies = value;
        else if (metaKey.equals(T_KEY)) timestamps = value;
      }
    }
  }

  private void initCoreMetadata() throws FormatException {
    // check version number
    if (DataTools.indexOf(KNOWN_VERSIONS, version) < 0) {
      LogTools.warnDebug("Warning: unknown LI-FLIM version: " + version);
    }

    // check compression type
    if (COMPRESSION_NONE.equals(compression)) gzip = false;
    else if (COMPRESSION_GZIP.equals(compression)) gzip = true;
    else throw new FormatException("Unknown compression type: " + compression);

    // check data type
    int pixelType;
    if (DATATYPE_UINT8.equals(datatype)) pixelType = FormatTools.UINT8;
    else if (DATATYPE_INT8.equals(datatype)) pixelType = FormatTools.INT8;
    else if (DATATYPE_UINT16.equals(datatype)) pixelType = FormatTools.UINT16;
    else if (DATATYPE_INT16.equals(datatype)) pixelType = FormatTools.INT16;
    else if (DATATYPE_UINT32.equals(datatype)) pixelType = FormatTools.UINT32;
    else if (DATATYPE_INT32.equals(datatype)) pixelType = FormatTools.INT32;
    else if (DATATYPE_REAL32.equals(datatype)) pixelType = FormatTools.FLOAT;
    else if (DATATYPE_REAL64.equals(datatype)) pixelType = FormatTools.DOUBLE;
    else throw new FormatException("Unknown data type: " + datatype);

    // check dimensional extents
    int sizeC = Integer.parseInt(channels);
    int sizeX = Integer.parseInt(xLen);
    int sizeY = Integer.parseInt(yLen);
    int sizeZ = Integer.parseInt(zLen);
    int sizeT = Integer.parseInt(timestamps);
    int sizeP = Integer.parseInt(phases);
    if (sizeP > 1) {
      throw new FormatException("Sorry, multiple phases not supported");
    }
    int sizeF = Integer.parseInt(frequencies);
    if (sizeF > 1) {
      throw new FormatException("Sorry, multiple frequences not supported");
    }
    int imageCount = sizeC * sizeZ * sizeP * sizeF * sizeT;

    // populate core metadata
    core[0].sizeX = sizeX;
    core[0].sizeY = sizeY;
    core[0].sizeZ = sizeZ;
    core[0].sizeC = sizeC;
    core[0].sizeT = sizeT;
    core[0].imageCount = imageCount;
    core[0].rgb = false;
    core[0].indexed = false;
    core[0].dimensionOrder = "XYZCT";
    core[0].pixelType = pixelType;
    core[0].littleEndian = true;
  }

  private void initOMEMetadata() {
    int times = timestamps == null ? 0 : Integer.parseInt(timestamps);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, times > 0);

    // timestamps
    long firstStamp = 0;
    for (int t=0; t<times; t++) {
      String stampValue = (String)
        getGlobalMeta("FLIMIMAGE: TIMESTAMPS - t" + t);
      if (stampValue == null) break;
      String[] stampWords = stampValue.split(" ");
      long stampHi = Long.parseLong(stampWords[0]);
      long stampLo = Long.parseLong(stampWords[1]);
      long stamp = DateTools.getMillisFromTicks(stampHi, stampLo);
      Float deltaT;
      if (t == 0) {
        String date = DateTools.convertDate(stamp, DateTools.COBOL);
        store.setImageCreationDate(date, 0);
        firstStamp = stamp;
        deltaT = new Float(0);
      }
      else {
        long ms = stamp - firstStamp;
        deltaT = new Float(ms / 1000f);
      }
      for (int c=0; c<core[0].sizeC; c++) {
        for (int z=0; z<core[0].sizeZ; z++) {
          int index = getIndex(z, c, t);
          store.setPlaneTimingDeltaT(deltaT, 0, 0, index);
        }
      }
    }

    if (firstStamp == 0) {
      MetadataTools.setDefaultCreationDate(store, currentId, 0);
    }

    // regions of interest
    StringBuilder points = new StringBuilder();
    String numRegionsValue = (String) getGlobalMeta("ROI: INFO - numregions");
    int numRegions = numRegionsValue == null ?
      0 : Integer.parseInt(numRegionsValue);
    for (int roi=0; roi<numRegions; roi++) {
      String roiPrefix = "ROI: ROI" + roi + " - ";
      String roiName = (String) getGlobalMeta(roiPrefix + "name");
      String numPointsValue = (String) getGlobalMeta(roiPrefix + "numpoints");
      int numPoints = numPointsValue == null ?
        0 : Integer.parseInt(numPointsValue);
      points.setLength(0);
      for (int p=0; p<numPoints; p++) {
        String point = (String) getGlobalMeta(roiPrefix + "p" + p);
        if (point == null) continue;
        if (p > 0) points.append(" ");
        points.append(point.replaceAll(" ", ","));
      }
      store.setPolygonPoints(points.toString(), 0, roi, 0);
    }
  }

  private void prepareGZipStream(int no) throws IOException {
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int bytesPerPlane = getSizeX() * getSizeY() * bpp;

    if (gz == null || no < gzPos) {
      // reinitialize gzip stream
      if (gz != null) gz.close();

      // seek to start of pixel data
      String path = Location.getMappedId(currentId);
      FileInputStream fis = new FileInputStream(path);
      skip(fis, dataOffset);

      // create gzip stream
      gz = new DataInputStream(new GZIPInputStream(fis));
      gzPos = 0;
    }

    // seek to correct image number
    skip(gz, bytesPerPlane * (no - gzPos));
    gzPos = no + 1;
  }

  private void skip(InputStream is, long num) throws IOException {
    long skipLeft = num;
    while (skipLeft > 0) {
      long skip = is.skip(skipLeft);
      if (skip <= 0) throw new IOException("Cannot skip bytes");
      skipLeft -= skip;
    }
  }

}

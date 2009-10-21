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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.LogTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
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

  public static final String TIMESTAMP_KEY = "FLIMIMAGE: TIMESTAMPS - t";

  // INI keys for the background image
  public static final String BG_DATATYPE_KEY =
    "FLIMIMAGE: BACKGROUND - datatype";
  public static final String BG_C_KEY = "FLIMIMAGE: BACKGROUND - channels";
  public static final String BG_X_KEY = "FLIMIMAGE: BACKGROUND - x";
  public static final String BG_Y_KEY = "FLIMIMAGE: BACKGROUND - y";
  public static final String BG_Z_KEY = "FLIMIMAGE: BACKGROUND - z";
  public static final String BG_P_KEY = "FLIMIMAGE: BACKGROUND - phases";
  public static final String BG_F_KEY = "FLIMIMAGE: BACKGROUND - frequencies";
  public static final String BG_T_KEY = "FLIMIMAGE: BACKGROUND - timestamps";

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

  private String backgroundDatatype;
  private String backgroundX;
  private String backgroundY;
  private String backgroundC;
  private String backgroundZ;
  private String backgroundT;
  private String backgroundP;
  private String backgroundF;

  private int numRegions = 0;
  private Hashtable<Integer, ROI> rois;
  private Hashtable<Integer, String> stampValues;
  private Float exposureTime;

  /** True if gzip compression was used to deflate the pixels. */
  private boolean gzip;

  /** Stream to use for reading gzip-compressed pixel data. */
  private DataInputStream gz;

  /** Image number indicating position in gzip stream. */
  private int gzPos;

  /** Series number indicating position in gzip stream. */
  private int gzSeries;

  // -- Constructor --

  /** Constructs a new LI-FLIM reader. */
  public LiFlimReader() {
    super("LI-FLIM", "fli");
    domains = new String[] {FormatTools.FLIM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int bytesPerPlane = FormatTools.getPlaneSize(this);

    if (gzip) {
      prepareGZipStream(no);

      // read compressed data
      byte[] bytes = new byte[bytesPerPlane];
      gz.readFully(bytes);

      RandomAccessInputStream s = new RandomAccessInputStream(bytes);
      readPlane(s, x, y, w, h, buf);
      s.close();
    }
    else {
      in.seek(dataOffset + bytesPerPlane * no);
      if (getSeries() == 1) {
        setSeries(0);
        int imageCount = getImageCount();
        int planeSize = FormatTools.getPlaneSize(this);
        setSeries(1);
        in.skipBytes(imageCount * planeSize);
      }
      readPlane(in, x, y, w, h, buf);
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
      gzSeries = 0;
      version = null;
      compression = null;
      datatype = null;
      channels = null;
      xLen = null;
      yLen = null;
      zLen = null;
      phases = null;
      frequencies = null;
      timestamps = null;
      backgroundDatatype = null;
      backgroundX = null;
      backgroundY = null;
      backgroundC = null;
      backgroundZ = null;
      backgroundT = null;
      backgroundP = null;
      backgroundF = null;
      numRegions = 0;
      rois = null;
      stampValues = null;
      exposureTime = null;
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
    rois = new Hashtable<Integer, ROI>();
    stampValues = new Hashtable<Integer, String>();

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
        else if (metaKey.equals(BG_DATATYPE_KEY)) backgroundDatatype = value;
        else if (metaKey.equals(BG_C_KEY)) backgroundC = value;
        else if (metaKey.equals(BG_X_KEY)) backgroundX = value;
        else if (metaKey.equals(BG_Y_KEY)) backgroundY = value;
        else if (metaKey.equals(BG_Z_KEY)) backgroundZ = value;
        else if (metaKey.equals(BG_T_KEY)) backgroundT = value;
        else if (metaKey.equals(BG_P_KEY)) backgroundP = value;
        else if (metaKey.equals(BG_F_KEY)) backgroundF = value;
        else if (metaKey.startsWith(TIMESTAMP_KEY)) {
          int index = Integer.parseInt(metaKey.replaceAll(TIMESTAMP_KEY, ""));
          stampValues.put(new Integer(index), value);
        }
        else if (metaKey.equals("ROI: INFO - numregions")) {
          numRegions = Integer.parseInt(value);
        }
        else if (metaKey.startsWith("ROI: ROI")) {
          int start = metaKey.lastIndexOf("ROI") + 3;
          int end = metaKey.indexOf(" ", start);
          Integer index = new Integer(metaKey.substring(start, end));
          ROI roi = rois.get(index);
          if (roi == null) roi = new ROI();

          if (metaKey.endsWith("name")) {
            roi.name = value;
          }
          else if (metaKey.indexOf(" - p") >= 0) {
            String p = metaKey.substring(metaKey.indexOf(" - p") + 4);
            Integer pointIndex = new Integer(p);
            roi.points.put(pointIndex, value.replaceAll(" ", ","));
          }
          rois.put(index, roi);
        }
        else if (metaKey.endsWith("ExposureTime")) {
          String exp = value;
          float expTime = Float.parseFloat(exp.substring(0, exp.indexOf(" ")));
          String units = exp.substring(exp.indexOf(" ") + 1).toLowerCase();
          if (units.equals("ms")) {
            expTime /= 1000;
          }
          exposureTime = new Float(expTime);
        }
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

    // check dimensional extents
    int sizeP = Integer.parseInt(phases);
    if (sizeP > 1) {
      throw new FormatException("Sorry, multiple phases not supported");
    }
    int sizeF = Integer.parseInt(frequencies);
    if (sizeF > 1) {
      throw new FormatException("Sorry, multiple frequencies not supported");
    }

    int p = backgroundP == null ? 1 : Integer.parseInt(backgroundP);
    if (p > 1) {
      throw new FormatException("Sorry, multiple phases not supported");
    }
    int f = backgroundF == null ? 1 : Integer.parseInt(backgroundF);
    if (f > 1) {
      throw new FormatException("Sorry, multiple frequencies not supported");
    }

    if (backgroundDatatype != null) {
      core = new CoreMetadata[2];
      core[0] = new CoreMetadata();
      core[1] = new CoreMetadata();
    }

    // populate core metadata
    core[0].sizeX = Integer.parseInt(xLen);
    core[0].sizeY = Integer.parseInt(yLen);
    core[0].sizeZ = Integer.parseInt(zLen);
    core[0].sizeC = Integer.parseInt(channels);
    core[0].sizeT = Integer.parseInt(timestamps);
    core[0].imageCount = getSizeZ() * getSizeT() * sizeP * sizeF;
    core[0].rgb = getSizeC() > 1;
    core[0].indexed = false;
    core[0].dimensionOrder = "XYCZT";
    core[0].pixelType = getPixelType(datatype);
    core[0].littleEndian = true;
    core[0].interleaved = true;
    core[0].falseColor = false;

    if (core.length > 1) {
      core[1].sizeX = Integer.parseInt(backgroundX);
      core[1].sizeY = Integer.parseInt(backgroundY);
      core[1].sizeZ = Integer.parseInt(backgroundZ);
      core[1].sizeC = Integer.parseInt(backgroundC);
      core[1].sizeT = Integer.parseInt(backgroundT);
      core[1].imageCount = core[1].sizeZ * core[1].sizeT * p * f;
      core[1].rgb = core[1].sizeC > 1;
      core[1].indexed = false;
      core[1].dimensionOrder = "XYCZT";
      core[1].pixelType = getPixelType(backgroundDatatype);
      core[1].littleEndian = true;
      core[1].interleaved = true;
      core[1].falseColor = false;
    }
  }

  private void initOMEMetadata() {
    int times = timestamps == null ? 0 : Integer.parseInt(timestamps);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, times > 0);

    // image data
    store.setImageName(getCurrentFile() + " Primary Image", 0);
    if (getSeriesCount() > 1) {
      store.setImageName(getCurrentFile() + " Background Image", 1);
    }

    // timestamps
    long firstStamp = 0;
    for (int t=0; t<times; t++) {
      if (stampValues.get(t) == null) break;
      String[] stampWords = stampValues.get(t).split(" ");
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
      for (int c=0; c<getEffectiveSizeC(); c++) {
        for (int z=0; z<getSizeZ(); z++) {
          int index = getIndex(z, c, t);
          store.setPlaneTimingDeltaT(deltaT, 0, 0, index);
          store.setPlaneTimingExposureTime(exposureTime, 0, 0, index);
        }
      }
    }

    if (firstStamp == 0) {
      MetadataTools.setDefaultCreationDate(store, currentId, 0);
    }

    // regions of interest
    StringBuilder points = new StringBuilder();
    Integer[] roiIndices = rois.keySet().toArray(new Integer[rois.size()]);
    Arrays.sort(roiIndices);
    for (int roi=0; roi<roiIndices.length; roi++) {
      points.setLength(0);
      ROI r = rois.get(roiIndices[roi]);
      Integer[] pointIndices = r.points.keySet().toArray(new Integer[0]);
      Arrays.sort(pointIndices);
      for (Integer point : pointIndices) {
        if (point == null) continue;
        String p = r.points.get(point);
        if (points.length() > 0) points.append(" ");
        points.append(p);
      }
      store.setPolygonPoints(points.toString(), 0, roi, 0);
    }
  }

  private int getPixelType(String type) throws FormatException {
    // check data type
    if (DATATYPE_UINT8.equals(type)) return FormatTools.UINT8;
    else if (DATATYPE_INT8.equals(type)) return FormatTools.INT8;
    else if (DATATYPE_UINT16.equals(type)) return FormatTools.UINT16;
    else if (DATATYPE_INT16.equals(type)) return FormatTools.INT16;
    else if (DATATYPE_UINT32.equals(type)) return FormatTools.UINT32;
    else if (DATATYPE_INT32.equals(type)) return FormatTools.INT32;
    else if (DATATYPE_REAL32.equals(type)) return FormatTools.FLOAT;
    else if (DATATYPE_REAL64.equals(type)) return FormatTools.DOUBLE;
    throw new FormatException("Unknown data type: " + type);
  }

  private void prepareGZipStream(int no) throws IOException {
    int bytesPerPlane = FormatTools.getPlaneSize(this);

    if (gz == null || (no < gzPos && getSeries() == gzSeries) ||
      gzSeries > getSeries())
    {
      // reinitialize gzip stream
      if (gz != null) gz.close();

      // seek to start of pixel data
      String path = Location.getMappedId(currentId);
      FileInputStream fis = new FileInputStream(path);
      skip(fis, dataOffset);

      // create gzip stream
      gz = new DataInputStream(new GZIPInputStream(fis));
      gzPos = 0;
      gzSeries = 0;
    }

    // seek to correct image number
    if (getSeries() == 1 && gzSeries == 0) {
      setSeries(0);
      int nPlanes = getImageCount() - gzPos;
      int nBytes = FormatTools.getPlaneSize(this) * nPlanes;
      setSeries(1);
      skip(gz, nBytes);
      gzSeries = 1;
      gzPos = 0;
    }
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

  // -- Helper class --

  private class ROI {
    public String name;
    public Hashtable<Integer, String> points = new Hashtable<Integer, String>();
  }

}

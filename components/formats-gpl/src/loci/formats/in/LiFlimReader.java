/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import ome.xml.model.primitives.Timestamp;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.meta.MetadataStore;

import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * LiFlimReader is the file format reader for LI-FLIM files.
 */
public class LiFlimReader extends FormatReader {

  // -- Constants --

  // INI tables
  public static final String INFO_TABLE = "FLIMIMAGE: INFO";
  public static final String LAYOUT_TABLE = "FLIMIMAGE: LAYOUT";
  public static final String BACKGROUND_TABLE = "FLIMIMAGE: BACKGROUND";

  // relevant keys in info table
  public static final String VERSION_KEY = "version";
  public static final String COMPRESSION_KEY = "compression";

  // relevant keys in layout and background tables
  public static final String DATATYPE_KEY = "datatype";
  public static final String C_KEY = "channels";
  public static final String X_KEY = "x";
  public static final String Y_KEY = "y";
  public static final String Z_KEY = "z";
  public static final String P_KEY = "phases";
  public static final String F_KEY = "frequencies";
  public static final String T_KEY = "timestamps";

  // relevant keys in timestamp table
  public static final String TIMESTAMP_KEY = "FLIMIMAGE: TIMESTAMPS - t";

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

  private int numRegions = 0;  /* note: set but not used */
  private Map<Integer, ROI> rois;
  private Map<Integer, String> stampValues;
  private Double exposureTime;

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
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int bytesPerPlane = FormatTools.getPlaneSize(this);

    if (gzip) {
      prepareGZipStream(no);

      // read compressed data
      byte[] bytes = new byte[bytesPerPlane];
      try {
        gz.readFully(bytes);
      }
      catch (EOFException e) {
        LOGGER.debug("Could not read full plane", e);
      }

      RandomAccessInputStream s = new RandomAccessInputStream(bytes);
      readPlane(s, x, y, w, h, buf);
      s.close();
    }
    else {
      in.seek(dataOffset + bytesPerPlane * no);

      int thisSeries = getSeries();
      for (int i=0; i<thisSeries; i++) {
        setSeries(i);
        in.skipBytes(getImageCount() * FormatTools.getPlaneSize(this));
      }
      setSeries(thisSeries);
      readPlane(in, x, y, w, h, buf);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
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
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    LOGGER.info("Parsing header");
    in = new RandomAccessInputStream(id);
    parseHeader();

    LOGGER.info("Parsing metadata");
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
    rois = new HashMap<Integer, ROI>();
    stampValues = new HashMap<Integer, String>();

    IniTable layoutTable = ini.getTable(LAYOUT_TABLE);
    datatype = layoutTable.get(DATATYPE_KEY);
    channels = layoutTable.get(C_KEY);
    xLen = layoutTable.get(X_KEY);
    yLen = layoutTable.get(Y_KEY);
    zLen = layoutTable.get(Z_KEY);
    phases = layoutTable.get(P_KEY);
    frequencies = layoutTable.get(F_KEY);
    timestamps = layoutTable.get(T_KEY);

    IniTable backgroundTable = ini.getTable(BACKGROUND_TABLE);
    if (backgroundTable != null) {
      backgroundDatatype = backgroundTable.get(DATATYPE_KEY);
      backgroundC = backgroundTable.get(C_KEY);
      backgroundX = backgroundTable.get(X_KEY);
      backgroundY = backgroundTable.get(Y_KEY);
      backgroundZ = backgroundTable.get(Z_KEY);
      backgroundT = backgroundTable.get(T_KEY);
      backgroundP = backgroundTable.get(P_KEY);
      backgroundF = backgroundTable.get(F_KEY);
    }

    IniTable infoTable = ini.getTable(INFO_TABLE);
    version = infoTable.get(VERSION_KEY);
    compression = infoTable.get(COMPRESSION_KEY);
    MetadataLevel level = getMetadataOptions().getMetadataLevel();

    if (level != MetadataLevel.MINIMUM) {
      // add all INI entries to the global metadata list
      for (IniTable table : ini) {
        String name = table.get(IniTable.HEADER_KEY);
        for (String key : table.keySet()) {
          if (key.equals(IniTable.HEADER_KEY)) continue;
          String value = table.get(key);
          String metaKey = name + " - " + key;
          addGlobalMeta(metaKey, value);

          if (metaKey.startsWith(TIMESTAMP_KEY)) {
            Integer index = new Integer(metaKey.replaceAll(TIMESTAMP_KEY, ""));
            stampValues.put(index, value);
          }
          else if (metaKey.equals("ROI: INFO - numregions")) {
            numRegions = Integer.parseInt(value);
          }
          else if (metaKey.startsWith("ROI: ROI") &&
            level != MetadataLevel.NO_OVERLAYS)
          {
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
              roi.points.put(new Integer(p), value.replaceAll(" ", ","));
            }
            rois.put(index, roi);
          }
          else if (metaKey.endsWith("ExposureTime")) {
            int space = value.indexOf(" ");
            double expTime = Double.parseDouble(value.substring(0, space));
            String units = value.substring(space + 1).toLowerCase();
            if (units.equals("ms")) {
              expTime /= 1000;
            }
            exposureTime = new Double(expTime);
          }
        }
      }
    }
  }

  private void initCoreMetadata() throws FormatException {
    // check version number
    if (DataTools.indexOf(KNOWN_VERSIONS, version) < 0) {
      LOGGER.warn("Unknown LI-FLIM version: {}", version);
    }

    // check compression type
    if (COMPRESSION_NONE.equals(compression)) gzip = false;
    else if (COMPRESSION_GZIP.equals(compression)) gzip = true;
    else {
      throw new UnsupportedCompressionException(
        "Unknown compression type: " + compression);
    }

    // check dimensional extents
    int sizeP = Integer.parseInt(phases);
    int sizeF = Integer.parseInt(frequencies);

    int p = backgroundP == null ? 1 : Integer.parseInt(backgroundP);
    int f = backgroundF == null ? 1 : Integer.parseInt(backgroundF);

    // populate core metadata
    CoreMetadata ms = core.get(0);
    ms.sizeX = Integer.parseInt(xLen);
    ms.sizeY = Integer.parseInt(yLen);
    ms.sizeZ = Integer.parseInt(zLen) * sizeF;
    ms.sizeC = Integer.parseInt(channels);
    ms.sizeT = Integer.parseInt(timestamps) * sizeP;
    ms.imageCount = getSizeZ() * getSizeT();
    ms.rgb = getSizeC() > 1;
    ms.indexed = false;
    ms.dimensionOrder = "XYCZT";
    ms.pixelType = getPixelTypeFromString(datatype);
    ms.littleEndian = true;
    ms.interleaved = true;
    ms.falseColor = false;

    ms.moduloZ.type = FormatTools.FREQUENCY;
    ms.moduloZ.step = ms.sizeZ / sizeF;
    ms.moduloZ.start = 0;
    ms.moduloZ.end = ms.sizeZ - 1;
    ms.moduloT.type = FormatTools.PHASE;
    ms.moduloT.step = ms.sizeT / sizeP;
    ms.moduloT.start = 0;
    ms.moduloT.end = ms.sizeT - 1;

    if (backgroundX != null) {
      ms = new CoreMetadata();
      ms.sizeX = Integer.parseInt(backgroundX);
      ms.sizeY = Integer.parseInt(backgroundY);
      ms.sizeZ = Integer.parseInt(backgroundZ) * f;
      ms.sizeC = Integer.parseInt(backgroundC);
      ms.sizeT = Integer.parseInt(backgroundT) * p;
      ms.imageCount = ms.sizeZ * ms.sizeT;
      ms.rgb = ms.sizeC > 1;
      ms.indexed = false;
      ms.dimensionOrder = "XYCZT";
      ms.pixelType = getPixelTypeFromString(backgroundDatatype);
      ms.littleEndian = true;
      ms.interleaved = true;
      ms.falseColor = false;

      ms.moduloZ.type = FormatTools.FREQUENCY;
      ms.moduloZ.step = ms.sizeZ / f;
      ms.moduloZ.start = 0;
      ms.moduloZ.end = ms.sizeZ - 1;
      ms.moduloT.type = FormatTools.PHASE;
      ms.moduloT.step = ms.sizeT / p;
      ms.moduloT.start = 0;
      ms.moduloT.end = ms.sizeT - 1;
      core.add(ms);
    }
  }

  private void initOMEMetadata() {
    int times = timestamps == null ? 0 : Integer.parseInt(timestamps);

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, times > 0);

    String path = new Location(getCurrentFile()).getName();

    store.setImageName(path + " Primary Image #1", 0);
    if (getSeriesCount() > 1) {
      store.setImageName(path + " Background Image #1", 1);
    }

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }

    // timestamps
    long firstStamp = 0;
    for (int t=0; t<times; t++) {
      if (stampValues.get(t) == null) break;
      String[] stampWords = stampValues.get(t).split(" ");
      long stampHi = Long.parseLong(stampWords[0]);
      long stampLo = Long.parseLong(stampWords[1]);
      long stamp = DateTools.getMillisFromTicks(stampHi, stampLo);
      Double deltaT;
      if (t == 0) {
        String date = DateTools.convertDate(stamp, DateTools.COBOL);
        if (date != null) {
          store.setImageAcquisitionDate(new Timestamp(date), 0);
        }
        firstStamp = stamp;
        deltaT = Double.valueOf(0);
      }
      else {
        long ms = stamp - firstStamp;
        deltaT = new Double(ms / 1000.0);
      }
      for (int c=0; c<getEffectiveSizeC(); c++) {
        for (int z=0; z<getSizeZ(); z++) {
          int index = getIndex(z, c, t);
          if (deltaT != null) {
            store.setPlaneDeltaT(new Time(deltaT, UNITS.S), 0, index);
          }
          if (exposureTime != null) {
            store.setPlaneExposureTime(new Time(exposureTime, UNITS.S), 0, index);
          }
        }
      }
    }

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.NO_OVERLAYS) {
      return;
    }

    // regions of interest
    Integer[] roiIndices = rois.keySet().toArray(new Integer[rois.size()]);
    Arrays.sort(roiIndices);
    for (int roi=0; roi<roiIndices.length; roi++) {
      ROI r = rois.get(roiIndices[roi]);
      String polylineID = MetadataTools.createLSID("Shape", roi, 0);
      store.setPolygonID(polylineID, roi, 0);
      store.setPolygonPoints(r.pointsToString(), roi, 0);
      String roiID = MetadataTools.createLSID("ROI", roi);
      store.setROIID(roiID, roi);
      for (int s=0; s<getSeriesCount(); s++) {
        store.setImageROIRef(roiID, s, roi);
      }
    }
  }

  private int getPixelTypeFromString(String type) throws FormatException {
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
    if (getSeries() >= 1 && gzSeries < getSeries()) {
      int originalSeries = getSeries();
      for (int i=gzSeries; i<originalSeries; i++) {
        setSeries(i);
        int nPlanes = getImageCount() - gzPos;
        int nBytes = FormatTools.getPlaneSize(this) * nPlanes;
        skip(gz, nBytes);
        gzPos = 0;
      }
      setSeries(originalSeries);
      gzSeries = getSeries();
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
    public final Map<Integer, String> points = new HashMap<Integer, String>();

    public String pointsToString() {
      StringBuilder s = new StringBuilder();
      Integer[] pointIndices = points.keySet().toArray(new Integer[0]);
      Arrays.sort(pointIndices);
      for (Integer point : pointIndices) {
        if (point == null) continue;
        String p = points.get(point);
        if (s.length() > 0) s.append(" ");
        s.append(p);
      }
      return s.toString();
    }
  }

}

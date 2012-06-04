//
// ZeissZVIReader.java
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.DummyMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.services.POIService;

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

/**
 * ZeissZVIReader is the file format reader for Zeiss ZVI files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ZeissZVIReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ZeissZVIReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ZeissZVIReader extends FormatReader {

  // -- Constants --

  public static final int ZVI_MAGIC_BYTES = 0xd0cf11e0;

  private static final long ROI_SIGNATURE = 0x21fff6977547000dL;

  /** ROI types. */
  private static final int ELLIPSE = 15;
  private static final int CURVE = 12;
  private static final int OUTLINE = 16;
  private static final int RECTANGLE = 13;
  private static final int LINE = 2;
  private static final int TEXT = 17;
  private static final int SCALE_BAR = 10;
  private static final int OUTLINE_SPLINE = 50;

  // -- Fields --

  /** Number of bytes per pixel. */
  private int bpp;

  private String[] imageFiles;
  private int[] offsets;
  private int[][] coordinates;

  private Hashtable<Integer, String> timestamps, exposureTime;
  private int cIndex = -1;
  private boolean isJPEG, isZlib;

  private int realWidth, realHeight;

  private POIService poi;

  private Vector<String> tagsToParse;
  private int nextEmWave = 0, nextExWave = 0, nextChName = 0;
  private Hashtable<Integer, Double> stageX = new Hashtable<Integer, Double>();
  private Hashtable<Integer, Double> stageY = new Hashtable<Integer, Double>();
  private int timepoint = 0;

  private int[] channelColors;
  private int lastPlane = 0;
  private Hashtable<Integer, Integer> tiles = new Hashtable<Integer, Integer>();

  private Hashtable<Integer, Double> detectorGain =
    new Hashtable<Integer, Double>();
  private Hashtable<Integer, Double> detectorOffset =
    new Hashtable<Integer, Double>();
  private Hashtable<Integer, PositiveInteger> emWavelength =
    new Hashtable<Integer, PositiveInteger>();
  private Hashtable<Integer, PositiveInteger> exWavelength =
    new Hashtable<Integer, PositiveInteger>();
  private Hashtable<Integer, String> channelName =
    new Hashtable<Integer, String>();
  private Double physicalSizeX, physicalSizeY, physicalSizeZ;
  private String imageDescription;

  private Vector<String> roiIDs = new Vector<String>();

  // -- Constructor --

  /** Constructs a new ZeissZVI reader. */
  public ZeissZVIReader() {
    super("Zeiss Vision Image (ZVI)", "zvi");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 65536;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    int magic = stream.readInt();
    if (magic != ZVI_MAGIC_BYTES) return false;
    return true;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    int pixelType = getPixelType();
    if ((pixelType != FormatTools.INT8 && pixelType != FormatTools.UINT8) ||
      !isIndexed())
    {
      return null;
    }
    byte[][] lut = new byte[3][256];
    int channel = getZCTCoords(lastPlane)[1];
    if (channel >= channelColors.length) return null;
    int color = channelColors[channel];

    float red = (color & 0xff) / 255f;
    float green = ((color & 0xff00) >> 8) / 255f;
    float blue = ((color & 0xff0000) >> 16) / 255f;

    for (int i=0; i<lut[0].length; i++) {
      lut[0][i] = (byte) (red * i);
      lut[1][i] = (byte) (green * i);
      lut[2][i] = (byte) (blue * i);
    }

    return lut;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    int pixelType = getPixelType();
    if ((pixelType != FormatTools.INT16 && pixelType != FormatTools.UINT16) ||
      !isIndexed())
    {
      return null;
    }
    short[][] lut = new short[3][65536];
    int channel = getZCTCoords(lastPlane)[1];
    if (channel >= channelColors.length) return null;
    int color = channelColors[channel];

    float red = (color & 0xff) / 255f;
    float green = ((color & 0xff00) >> 8) / 255f;
    float blue = ((color & 0xff0000) >> 16) / 255f;

    for (int i=0; i<lut[0].length; i++) {
      lut[0][i] = (short) (red * i);
      lut[1][i] = (short) (green * i);
      lut[2][i] = (short) (blue * i);
    }
    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    lastPlane = no;

    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bytes * getRGBChannelCount();

    CodecOptions options = new CodecOptions();
    options.littleEndian = isLittleEndian();
    options.interleaved = isInterleaved();

    int index = no;
    if (getSeriesCount() > 1) {
      index += getSeries() * getImageCount();
    }

    if (index >= imageFiles.length) {
      return buf;
    }

    RandomAccessInputStream s = poi.getDocumentStream(imageFiles[index]);
    s.seek(offsets[index]);

    int len = w * pixel;
    int row = getSizeX() * pixel;

    if (isJPEG) {
      byte[] t = new JPEGCodec().decompress(s, options);

      for (int yy=0; yy<h; yy++) {
        System.arraycopy(t, (yy + y) * row + x * pixel, buf, yy*len, len);
      }
    }
    else if (isZlib) {
      byte[] t = new ZlibCodec().decompress(s, options);
      for (int yy=0; yy<h; yy++) {
        int src = (yy + y) * row + x * pixel;
        int dest = yy * len;
        if (src + len <= t.length && dest + len <= buf.length) {
          System.arraycopy(t, src, buf, dest, len);
        }
        else break;
      }
    }
    else {
      readPlane(s, x, y, w, h, buf);
    }
    s.close();

    if (isRGB() && !isJPEG) {
      // reverse bytes in groups of 3 to account for BGR storage
      byte[] bb = new byte[bytes];
      for (int i=0; i<buf.length; i+=bpp) {
        System.arraycopy(buf, i + 2*bytes, bb, 0, bytes);
        System.arraycopy(buf, i, buf, i + 2*bytes, bytes);
        System.arraycopy(bb, 0, buf, i, bytes);
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      timestamps = exposureTime = null;
      offsets = null;
      coordinates = null;
      imageFiles = null;
      cIndex = -1;
      bpp = 0;
      isJPEG = isZlib = false;
      if (poi != null) poi.close();
      poi = null;
      tagsToParse = null;
      nextEmWave = nextExWave = nextChName = 0;
      realWidth = realHeight = 0;
      stageX.clear();
      stageY.clear();
      channelColors = null;
      lastPlane = 0;
      tiles.clear();
      detectorGain.clear();
      detectorOffset.clear();
      emWavelength.clear();
      exWavelength.clear();
      channelName.clear();
      physicalSizeX = physicalSizeY = physicalSizeZ = null;
      imageDescription = null;
      timepoint = 0;
      roiIDs.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    try {
      ServiceFactory factory = new ServiceFactory();
      poi = factory.getInstance(POIService.class);
    }
    catch (DependencyException de) {
      throw new FormatException("POI library not found", de);
    }
    poi.initialize(Location.getMappedId(id));

    timestamps = new Hashtable<Integer, String>();
    exposureTime = new Hashtable<Integer, String>();
    tagsToParse = new Vector<String>();

    // count number of images

    String[] files = (String[]) poi.getDocumentList().toArray(new String[0]);
    Arrays.sort(files, new Comparator() {
      public int compare(Object o1, Object o2) {
        int n1 = getImageNumber((String) o1, -1);
        int n2 = getImageNumber((String) o2, -1);

        return new Integer(n1).compareTo(new Integer(n2));
      }
    });
    core[0].imageCount = 0;

    for (String file : files) {
      String uname = file.toUpperCase();
      uname = uname.substring(uname.indexOf(File.separator) + 1);
      if (uname.endsWith("CONTENTS") && (uname.startsWith("IMAGE") ||
        uname.indexOf("ITEM") != -1) && poi.getFileSize(file) > 1024)
      {
        int imageNumber = getImageNumber(file, 0);
        if (imageNumber >= getImageCount()) {
          core[0].imageCount++;
        }
      }
    }

    offsets = new int[getImageCount()];
    coordinates = new int[getImageCount()][3];
    imageFiles = new String[getImageCount()];

    // parse each embedded file

    Vector<Integer> cIndices = new Vector<Integer>();
    Vector<Integer> zIndices = new Vector<Integer>();
    Vector<Integer> tIndices = new Vector<Integer>();

    MetadataStore store = makeFilterMetadata();

    for (String name : files) {
      String relPath = name.substring(name.lastIndexOf(File.separator) + 1);
      if (!relPath.toUpperCase().equals("CONTENTS")) continue;
      String dirName = name.substring(0, name.lastIndexOf(File.separator));
      if (dirName.indexOf(File.separator) != -1) {
        dirName = dirName.substring(dirName.lastIndexOf(File.separator) + 1);
      }

      if (name.indexOf("Scaling") == -1 && dirName.equals("Tags")) {
        int imageNum = getImageNumber(name, -1);
        if (imageNum == -1) {
          parseTags(imageNum, name, new DummyMetadata());
        }
        else tagsToParse.add(name);
      }
      else if (dirName.equals("Shapes") && name.indexOf("Item") != -1) {
        int imageNum = getImageNumber(name, -1);
        if (imageNum != -1) {
          try {
            parseROIs(imageNum, name, store);
          }
          catch (IOException e) {
            LOGGER.debug("Could not parse all ROIs.", e);
          }
        }
      }
      else if (dirName.equals("Image") ||
        dirName.toUpperCase().indexOf("ITEM") != -1)
      {
        int imageNum = getImageNumber(dirName, getImageCount() == 1 ? 0 : -1);
        if (imageNum == -1) continue;

        // found a valid image stream
        RandomAccessInputStream s = poi.getDocumentStream(name);
        s.order(true);

        if (s.length() <= 1024) {
          s.close();
          continue;
        }

        for (int q=0; q<11; q++) {
          getNextTag(s);
        }

        s.skipBytes(2);
        int len = s.readInt() - 20;
        s.skipBytes(8);

        int zidx = s.readInt();
        int cidx = s.readInt();
        int tidx = s.readInt();

        if (!zIndices.contains(zidx)) zIndices.add(zidx);
        if (!tIndices.contains(tidx)) tIndices.add(tidx);
        if (!cIndices.contains(cidx)) cIndices.add(cidx);

        s.skipBytes(len);

        for (int q=0; q<5; q++) {
          getNextTag(s);
        }

        s.skipBytes(4);
        if (getSizeX() == 0) {
          core[0].sizeX = s.readInt();
          core[0].sizeY = s.readInt();
        }
        else s.skipBytes(8);
        s.skipBytes(4);

        if (bpp == 0) {
          bpp = s.readInt();
        }
        else s.skipBytes(4);
        s.skipBytes(4);

        int valid = s.readInt();

        String check = s.readString(4).trim();
        isZlib = (valid == 0 || valid == 1) && check.equals("WZL");
        isJPEG = (valid == 0 || valid == 1) && !isZlib;

        // save the offset to the pixel data

        offsets[imageNum] = (int) s.getFilePointer() - 4;

        if (isZlib) offsets[imageNum] += 8;
        coordinates[imageNum][0] = zidx;
        coordinates[imageNum][1] = cidx;
        coordinates[imageNum][2] = tidx;
        imageFiles[imageNum] = name;
        s.close();
      }
    }

    LOGGER.info("Populating metadata");

    stageX.clear();
    stageY.clear();

    core[0].sizeZ = zIndices.size();
    core[0].sizeT = tIndices.size();
    core[0].sizeC = cIndices.size();

    core[0].littleEndian = true;
    core[0].interleaved = true;
    core[0].falseColor = true;
    core[0].metadataComplete = true;

    core[0].imageCount = getSizeZ() * getSizeT() * getSizeC();
    core[0].rgb = (bpp % 3) == 0;
    if (isRGB()) core[0].sizeC *= 3;

    for (String name : tagsToParse) {
      int imageNum = getImageNumber(name, -1);
      parseTags(imageNum, name, store);
    }

    // calculate tile dimensions and number of tiles
    if (core.length > 1) {
      Integer[] t = tiles.keySet().toArray(new Integer[tiles.size()]);
      Arrays.sort(t);
      Vector<Integer> tmpOffsets = new Vector<Integer>();
      Vector<String> tmpFiles = new Vector<String>();
      int index = 0;
      for (Integer key : t) {
        int nTiles = tiles.get(key).intValue();
        if (nTiles < getImageCount()) {
          tiles.remove(key);
        }
        else {
          for (int p=0; p<nTiles; p++) {
            tmpOffsets.add(new Integer(offsets[index + p]));
            tmpFiles.add(imageFiles[index + p]);
          }
        }
        index += nTiles;
      }

      offsets = new int[tmpOffsets.size()];
      for (int i=0; i<offsets.length; i++) {
        offsets[i] = tmpOffsets.get(i).intValue();
      }
      imageFiles = tmpFiles.toArray(new String[tmpFiles.size()]);
    }

    if (getSizeX() == 0) {
      core[0].sizeX = 1;
    }
    if (getSizeY() == 0) {
      core[0].sizeY = 1;
    }
    if (getImageCount() == 0) {
      core[0].imageCount = 1;
      core[0].sizeZ = 1;
      core[0].sizeC = 1;
      core[0].sizeT = 1;
    }

    int totalTiles = offsets.length / getImageCount();

    if (totalTiles < 1) {
      totalTiles = 1;
    }

    if (totalTiles > 1) {
      CoreMetadata originalCore = core[0];
      core = new CoreMetadata[totalTiles];

      core[0] = originalCore;
    }

    core[0].dimensionOrder = "XY";
    if (isRGB()) core[0].dimensionOrder += "C";
    for (int i=0; i<coordinates.length-1; i++) {
      int[] zct1 = coordinates[i];
      int[] zct2 = coordinates[i + 1];
      int deltaZ = zct2[0] - zct1[0];
      int deltaC = zct2[1] - zct1[1];
      int deltaT = zct2[2] - zct1[2];
      if (deltaZ > 0 && getDimensionOrder().indexOf("Z") == -1) {
        core[0].dimensionOrder += "Z";
      }
      if (deltaC > 0 && getDimensionOrder().indexOf("C") == -1) {
        core[0].dimensionOrder += "C";
      }
      if (deltaT > 0 && getDimensionOrder().indexOf("T") == -1) {
        core[0].dimensionOrder += "T";
      }
    }
    core[0].dimensionOrder =
      MetadataTools.makeSaneDimensionOrder(getDimensionOrder());

    if (bpp == 1 || bpp == 3) core[0].pixelType = FormatTools.UINT8;
    else if (bpp == 2 || bpp == 6) core[0].pixelType = FormatTools.UINT16;
    if (isJPEG) core[0].pixelType = FormatTools.UINT8;

    core[0].indexed = !isRGB() && channelColors != null;

    for (int i=1; i<core.length; i++) {
      core[i] = new CoreMetadata(this, 0);
    }

    MetadataTools.populatePixels(store, this, true);

    for (int i=0; i<getSeriesCount(); i++) {
      long firstStamp = 0;
      if (timestamps.size() > 0) {
        String timestamp = timestamps.get(new Integer(0));
        firstStamp = parseTimestamp(timestamp);
        String date =
          DateTools.convertDate((long) (firstStamp / 1600), DateTools.ZVI);
        store.setImageAcquisitionDate(new Timestamp(date), i);
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // link Instrument and Image
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);

      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
      store.setObjectiveImmersion(getImmersion("Other"), 0, 0);

      Integer[] channelKeys = channelName.keySet().toArray(new Integer[0]);
      Arrays.sort(channelKeys);

      // link DetectorSettings to an actual Detector
      for (int i=0; i<getEffectiveSizeC(); i++) {
        String detectorID = MetadataTools.createLSID("Detector", 0, i);
        store.setDetectorID(detectorID, 0, i);
        store.setDetectorType(getDetectorType("Other"), 0, i);

        for (int s=0; s<getSeriesCount(); s++) {
          int c = i;
          if (i < channelKeys.length) {
            c = channelKeys[i];
          }

          store.setDetectorSettingsID(detectorID, s, i);
          store.setDetectorSettingsGain(detectorGain.get(c), s, i);
          store.setDetectorSettingsOffset(detectorOffset.get(c), s, i);

          store.setChannelName(channelName.get(c), s, i);
          store.setChannelEmissionWavelength(emWavelength.get(c), s, i);
          store.setChannelExcitationWavelength(exWavelength.get(c), s, i);
        }
      }

      for (int i=0; i<getSeriesCount(); i++) {
        store.setImageInstrumentRef(instrumentID, i);
        store.setObjectiveSettingsID(objectiveID, i);

        if (imageDescription != null) {
          store.setImageDescription(imageDescription, i);
        }
        if (getSeriesCount() > 1) {
          store.setImageName("Tile #" + (i + 1), i);
        }

        if (physicalSizeX != null && physicalSizeX > 0) {
          store.setPixelsPhysicalSizeX(new PositiveFloat(physicalSizeX), i);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
            physicalSizeX);
        }
        if (physicalSizeY != null && physicalSizeY > 0) {
          store.setPixelsPhysicalSizeY(new PositiveFloat(physicalSizeY), i);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
            physicalSizeY);
        }
        if (physicalSizeZ != null && physicalSizeZ > 0) {
          store.setPixelsPhysicalSizeZ(new PositiveFloat(physicalSizeZ), i);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeZ; got {}",
            physicalSizeZ);
        }

        long firstStamp = parseTimestamp(timestamps.get(new Integer(0)));

        for (int plane=0; plane<getImageCount(); plane++) {
          int[] zct = getZCTCoords(plane);
          int expIndex = zct[1];
          if (channelKeys.length > 0) {
            expIndex += channelKeys[0];
          }
          String exposure = exposureTime.get(expIndex);
          if (exposure == null && exposureTime.size() == 1) {
            exposure = exposureTime.get(exposureTime.keys().nextElement());
          }
          Double exp = new Double(0.0);
          try { exp = new Double(exposure); }
          catch (NumberFormatException e) { }
          catch (NullPointerException e) { }
          store.setPlaneExposureTime(exp, i, plane);

          int posIndex = i * getImageCount() + plane;

          if (posIndex < timestamps.size()) {
            String timestamp = timestamps.get(new Integer(posIndex));
            long stamp = parseTimestamp(timestamp);
            stamp -= firstStamp;
            store.setPlaneDeltaT(new Double(stamp / 1600000), i, plane);
          }

          if (stageX.get(posIndex) != null) {
            store.setPlanePositionX(stageX.get(posIndex), i, plane);
          }
          if (stageY.get(posIndex) != null) {
            store.setPlanePositionY(stageY.get(posIndex), i, plane);
          }
        }
      }

      for (int i=0; i<getSeriesCount(); i++) {
        for (int roi=0; roi<roiIDs.size(); roi++) {
          store.setImageROIRef(roiIDs.get(roi), i, roi);
        }
      }
    }
  }

  private int getImageNumber(String dirName, int defaultNumber) {
    if (dirName.toUpperCase().indexOf("ITEM") != -1) {
      int open = dirName.indexOf("(");
      int close = dirName.indexOf(")");
      if (open < 0 || close < 0 || close < open) return defaultNumber;
      return Integer.parseInt(dirName.substring(open + 1, close));
    }
    return defaultNumber;
  }

  private String getNextTag(RandomAccessInputStream s) throws IOException {
    int type = s.readShort();
    switch (type) {
      case 0:
      case 1:
        return "";
      case 2:
        return String.valueOf(s.readShort());
      case 3:
      case 22:
      case 23:
        return String.valueOf(s.readInt());
      case 4:
        return String.valueOf(s.readFloat());
      case 5:
        return String.valueOf(s.readDouble());
      case 7:
      case 20:
      case 21:
        return String.valueOf(s.readLong());
      case 8:
      case 69:
        int len = s.readInt();
        return s.readString(len);
      case 9:
      case 13:
        s.skipBytes(16);
        return "";
      case 63:
      case 65:
        len = s.readInt();
        s.skipBytes(len);
        return "";
      case 66:
        len = s.readShort();
        return s.readString(len);
      default:
        long old = s.getFilePointer();
        while (s.readShort() != 3 &&
          s.getFilePointer() + 2 < s.length());
        long fp = s.getFilePointer() - 2;
        s.seek(old - 2);
        return s.readString((int) (fp - old + 2));
    }
  }

  /** Parse all of the tags in a stream. */
  private void parseTags(int image, String file, MetadataStore store)
    throws FormatException, IOException
  {
    RandomAccessInputStream s = poi.getDocumentStream(file);
    s.order(true);

    s.seek(8);

    int count = s.readInt();

    int effectiveSizeC = 0;
    try {
      effectiveSizeC = getEffectiveSizeC();
    }
    catch (ArithmeticException e) { }

    for (int i=0; i<count; i++) {
      if (s.getFilePointer() + 2 >= s.length()) break;
      String value = DataTools.stripString(getNextTag(s));

      s.skipBytes(2);
      int tagID = s.readInt();

      s.skipBytes(6);

      try {
        String key = getKey(tagID);
        if (key.equals("Image Channel Index")) {
          cIndex = Integer.parseInt(value);
          int v = 0;
          while (getGlobalMeta(key + " " + v) != null) v++;
          if (!getGlobalMetadata().containsValue(cIndex)) {
            addGlobalMeta(key + " " + v, cIndex);
          }
          continue;
        }
        else if (key.equals("ImageWidth")) {
          int v = Integer.parseInt(value);
          if (getSizeX() == 0 || v < getSizeX()) {
            core[0].sizeX = v;
          }
          if (realWidth == 0 && v > realWidth) realWidth = v;
        }
        else if (key.equals("ImageHeight")) {
          int v = Integer.parseInt(value);
          if (getSizeY() == 0 || v < getSizeY()) core[0].sizeY = v;
          if (realHeight == 0 || v > realHeight) realHeight = v;
        }

        if (cIndex != -1) key += " " + cIndex;
        addGlobalMeta(key, value);

        if (key.startsWith("ImageTile") && !(store instanceof DummyMetadata)) {
          if (!tiles.containsKey(new Integer(value))) {
            tiles.put(new Integer(value), new Integer(1));
          }
          else {
            int v = tiles.get(new Integer(value)).intValue() + 1;
            tiles.put(new Integer(value), new Integer(v));
          }
        }

        if (key.startsWith("MultiChannel Color")) {
          if (cIndex >= 0 && cIndex < effectiveSizeC) {
            if (channelColors == null ||
              effectiveSizeC > channelColors.length)
            {
              channelColors = new int[effectiveSizeC];
            }
            channelColors[cIndex] = Integer.parseInt(value);
          }
        }
        else if (key.startsWith("Scale Factor for X") && physicalSizeX == null)
        {
          physicalSizeX = Double.parseDouble(value);
        }
        else if (key.startsWith("Scale Factor for Y") && physicalSizeY == null)
        {
          physicalSizeY = Double.parseDouble(value);
        }
        else if (key.startsWith("Scale Factor for Z") && physicalSizeZ == null)
        {
          physicalSizeZ = Double.parseDouble(value);
        }
        else if (key.startsWith("Emission Wavelength")) {
          if (cIndex != -1) {
            Integer wave = new Integer(value);
            if (wave.intValue() > 0) {
              emWavelength.put(cIndex, new PositiveInteger(wave));
            }
            else {
              LOGGER.warn(
                "Expected positive value for EmissionWavelength; got {}", wave);
            }
          }
        }
        else if (key.startsWith("Excitation Wavelength")) {
          if (cIndex != -1) {
            Integer wave = new Integer((int) Double.parseDouble(value));
            if (wave.intValue() > 0) {
              exWavelength.put(cIndex, new PositiveInteger(wave));
            }
            else {
              LOGGER.warn(
                "Expected positive value for ExcitationWavelength; got {}",
                wave);
            }
          }
        }
        else if (key.startsWith("Channel Name")) {
          if (cIndex != -1) {
            channelName.put(cIndex, value);
          }
        }
        else if (key.startsWith("Exposure Time [ms]")) {
          if (exposureTime.get(new Integer(cIndex)) == null) {
            double exp = Double.parseDouble(value) / 1000;
            exposureTime.put(new Integer(cIndex), String.valueOf(exp));
          }
        }
        else if (key.startsWith("User Name")) {
          String[] username = value.split(" ");
          if (username.length >= 2) {
            store.setExperimenterFirstName(username[0], 0);
            store.setExperimenterLastName(username[username.length - 1], 0);
          }
        }
        else if (key.equals("User company")) {
          store.setExperimenterInstitution(value, 0);
        }
        else if (key.startsWith("Objective Magnification")) {
          int magnification = (int) Double.parseDouble(value);
          if (magnification > 0) {
            store.setObjectiveNominalMagnification(
              new PositiveInteger(magnification), 0, 0);
          }
          else {
            LOGGER.warn(
              "Expected positive value for NominalMagnification; got {}",
              magnification);
          }
        }
        else if (key.startsWith("Objective ID")) {
          store.setObjectiveID("Objective:" + value, 0, 0);
          store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
          store.setObjectiveImmersion(getImmersion("Other"), 0, 0);
        }
        else if (key.startsWith("Objective N.A.")) {
          store.setObjectiveLensNA(new Double(value), 0, 0);
        }
        else if (key.startsWith("Objective Name")) {
          String[] tokens = value.split(" ");
          for (int q=0; q<tokens.length; q++) {
            int slash = tokens[q].indexOf("/");
            if (slash != -1 && slash - q > 0) {
              int mag = (int)
                Double.parseDouble(tokens[q].substring(0, slash - q));
              String na = tokens[q].substring(slash + 1);
              if (mag > 0) {
                store.setObjectiveNominalMagnification(
                  new PositiveInteger(mag), 0, 0);
              }
              else {
                LOGGER.warn(
                  "Expected positive value for NominalMagnification; got {}",
                  mag);
              }
              store.setObjectiveLensNA(new Double(na), 0, 0);
              store.setObjectiveCorrection(getCorrection(tokens[q - 1]), 0, 0);
              break;
            }
          }
        }
        else if (key.startsWith("Objective Working Distance")) {
          store.setObjectiveWorkingDistance(new Double(value), 0, 0);
        }
        else if (key.startsWith("Objective Immersion Type")) {
          String immersion = "Other";
          switch (Integer.parseInt(value)) {
            // case 1: no immersion
            case 2:
              immersion = "Oil";
              break;
            case 3:
              immersion = "Water";
              break;
          }
          store.setObjectiveImmersion(getImmersion(immersion), 0, 0);
        }
        else if (key.startsWith("Stage Position X")) {
          stageX.put(image, new Double(value));
          addGlobalMeta("X position for position #" + stageX.size(), value);
        }
        else if (key.startsWith("Stage Position Y")) {
          stageY.put(image, new Double(value));
          addGlobalMeta("Y position for position #" + stageY.size(), value);
        }
        else if (key.startsWith("Orca Analog Gain")) {
          detectorGain.put(cIndex, new Double(value));
        }
        else if (key.startsWith("Orca Analog Offset")) {
          detectorOffset.put(cIndex, new Double(value));
        }
        else if (key.startsWith("Comments")) {
          imageDescription = value;
        }
        else if (key.startsWith("Acquisition Date")) {
          if (timepoint > 0) {
            timestamps.put(new Integer(timepoint - 1), value);
            addGlobalMeta("Timestamp " + timepoint, value);
          }
          timepoint++;
        }
      }
      catch (NumberFormatException e) { }
    }
    s.close();
  }

  /**
   * Parse ROI data from the given RandomAccessInputStream and store it in the
   * given MetadataStore.
   */
  private void parseROIs(int imageNum, String name, MetadataStore store)
    throws IOException
  {
    MetadataLevel level = getMetadataOptions().getMetadataLevel();
    if (level == MetadataLevel.MINIMUM || level == MetadataLevel.NO_OVERLAYS) {
      return;
    }

    RandomAccessInputStream s = poi.getDocumentStream(name);
    s.order(true);

    // scan stream for offsets to each ROI

    Vector<Long> roiOffsets = new Vector<Long>();
    s.seek(0);
    while (s.getFilePointer() < s.length() - 8) {
      // find next ROI signature
      long signature = s.readLong() & 0xffffffffffffffffL;
      while (signature != ROI_SIGNATURE) {
        if (s.getFilePointer() >= s.length()) break;
        s.seek(s.getFilePointer() - 6);
        signature = s.readLong() & 0xffffffffffffffffL;
      }
      if (s.getFilePointer() < s.length()) {
        roiOffsets.add(new Long(s.getFilePointer()));
      }
    }

    int shapeIndex = 0;
    for (int shape=0; shape<roiOffsets.size(); shape++) {
      s.seek(roiOffsets.get(shape).longValue() + 18);

      int length = s.readInt();
      s.skipBytes(length + 10);
      int roiType = s.readInt();
      s.skipBytes(8);

      // read the bounding box
      int x = s.readInt();
      int y = s.readInt();
      int w = s.readInt() - x;
      int h = s.readInt() - y;

      // read text label and font data
      long nextOffset = shape < roiOffsets.size() - 1 ?
        roiOffsets.get(shape + 1).longValue() : s.length();

      long nameBlock = s.getFilePointer();
      long fontBlock = s.getFilePointer();
      long lastBlock = s.getFilePointer();
      while (s.getFilePointer() < nextOffset - 1) {
        while (s.readShort() != 8) {
          if (s.getFilePointer() >= nextOffset) break;
        }
        if (s.getFilePointer() >= nextOffset) break;
        if (s.getFilePointer() - lastBlock > 64 && lastBlock != fontBlock) {
          break;
        }
        nameBlock = fontBlock;
        fontBlock = lastBlock;
        lastBlock = s.getFilePointer();
      }

      s.seek(nameBlock);
      int strlen = s.readInt();
      if (strlen + s.getFilePointer() > s.length()) continue;
      String roiName = DataTools.stripString(s.readString(strlen));

      s.seek(fontBlock);
      int fontLength = s.readInt();
      String fontName = DataTools.stripString(s.readString(fontLength));
      s.skipBytes(2);
      int typeLength = s.readInt();
      s.skipBytes(typeLength);

      // read list of points that define this ROI
      s.skipBytes(10);
      int nPoints = s.readInt();
      s.skipBytes(6);

      String roiID = MetadataTools.createLSID("ROI", imageNum);
      String shapeID = MetadataTools.createLSID("Shape", imageNum, shapeIndex);

      roiIDs.add(roiID);

      if (roiType == ELLIPSE) {
        store.setROIID(roiID, imageNum);
        store.setEllipseID(shapeID, imageNum, shapeIndex);
        store.setEllipseX(new Double(x + (w / 2)), imageNum, shapeIndex);
        store.setEllipseY(new Double(y + (h / 2)), imageNum, shapeIndex);
        store.setEllipseRadiusX(new Double(w / 2), imageNum, shapeIndex);
        store.setEllipseRadiusY(new Double(h / 2), imageNum, shapeIndex);
        shapeIndex++;
      }
      else if (roiType == CURVE || roiType == OUTLINE ||
        roiType == OUTLINE_SPLINE)
      {
        store.setROIID(roiID, imageNum);
        StringBuffer points = new StringBuffer();
        for (int p=0; p<nPoints; p++) {
          double px = s.readDouble();
          double py = s.readDouble();
          points.append(px);
          points.append(",");
          points.append(py);
          if (p < nPoints - 1) points.append(" ");
        }

        if (roiType == CURVE) {
          store.setPolylineID(shapeID, imageNum, shapeIndex);
          store.setPolylinePoints(points.toString(), imageNum, shapeIndex);
        }
        else {
          store.setPolygonID(shapeID, imageNum, shapeIndex);
          store.setPolygonPoints(points.toString(), imageNum, shapeIndex);
        }
        shapeIndex++;
      }
      else if (roiType == RECTANGLE || roiType == TEXT) {
        store.setROIID(roiID, imageNum);
        store.setRectangleID(shapeID, imageNum, shapeIndex);
        store.setRectangleX(new Double(x), imageNum, shapeIndex);
        store.setRectangleY(new Double(y), imageNum, shapeIndex);
        store.setRectangleWidth(new Double(w), imageNum, shapeIndex);
        store.setRectangleHeight(new Double(h), imageNum, shapeIndex);
        shapeIndex++;
      }
      else if (roiType == LINE || roiType == SCALE_BAR) {
        store.setROIID(roiID, imageNum);
        double x1 = s.readDouble();
        double y1 = s.readDouble();
        double x2 = s.readDouble();
        double y2 = s.readDouble();
        store.setLineID(shapeID, imageNum, shapeIndex);
        store.setLineX1(x1, imageNum, shapeIndex);
        store.setLineY1(y1, imageNum, shapeIndex);
        store.setLineX2(x2, imageNum, shapeIndex);
        store.setLineY2(y2, imageNum, shapeIndex);
        shapeIndex++;
      }
    }
    s.close();
  }

  /** Return the string corresponding to the given ID. */
  private String getKey(int tagID) {
    switch (tagID) {
      case 222: return "Compression";
      case 258: return "BlackValue";
      case 259: return "WhiteValue";
      case 260: return "ImageDataMappingAutoRange";
      case 261: return "Thumbnail";
      case 262: return "GammaValue";
      case 264: return "ImageOverExposure";
      case 265: return "ImageRelativeTime1";
      case 266: return "ImageRelativeTime2";
      case 267: return "ImageRelativeTime3";
      case 268: return "ImageRelativeTime4";
      case 333: return "RelFocusPosition1";
      case 334: return "RelFocusPosition2";
      case 513: return "ObjectType";
      case 515: return "ImageWidth";
      case 516: return "ImageHeight";
      case 517: return "Number Raw Count";
      case 518: return "PixelType";
      case 519: return "NumberOfRawImages";
      case 520: return "ImageSize";
      case 523: return "Acquisition pause annotation";
      case 530: return "Document Subtype";
      case 531: return "Acquisition Bit Depth";
      case 532: return "Image Memory Usage (RAM)";
      case 534: return "Z-Stack single representative";
      case 769: return "Scale Factor for X";
      case 770: return "Scale Unit for X";
      case 771: return "Scale Width";
      case 772: return "Scale Factor for Y";
      case 773: return "Scale Unit for Y";
      case 774: return "Scale Height";
      case 775: return "Scale Factor for Z";
      case 776: return "Scale Unit for Z";
      case 777: return "Scale Depth";
      case 778: return "Scaling Parent";
      case 1001: return "Date";
      case 1002: return "code";
      case 1003: return "Source";
      case 1004: return "Message";
      case 1025: return "Acquisition Date";
      case 1026: return "8-bit acquisition";
      case 1027: return "Camera Bit Depth";
      case 1029: return "MonoReferenceLow";
      case 1030: return "MonoReferenceHigh";
      case 1031: return "RedReferenceLow";
      case 1032: return "RedReferenceHigh";
      case 1033: return "GreenReferenceLow";
      case 1034: return "GreenReferenceHigh";
      case 1035: return "BlueReferenceLow";
      case 1036: return "BlueReferenceHigh";
      case 1041: return "FrameGrabber Name";
      case 1042: return "Camera";
      case 1044: return "CameraTriggerSignalType";
      case 1045: return "CameraTriggerEnable";
      case 1046: return "GrabberTimeout";
      case 1281: return "MultiChannelEnabled";
      case 1282: return "MultiChannel Color";
      case 1283: return "MultiChannel Weight";
      case 1284: return "Channel Name";
      case 1536: return "DocumentInformationGroup";
      case 1537: return "Title";
      case 1538: return "Author";
      case 1539: return "Keywords";
      case 1540: return "Comments";
      case 1541: return "SampleID";
      case 1542: return "Subject";
      case 1543: return "RevisionNumber";
      case 1544: return "Save Folder";
      case 1545: return "FileLink";
      case 1546: return "Document Type";
      case 1547: return "Storage Media";
      case 1548: return "File ID";
      case 1549: return "Reference";
      case 1550: return "File Date";
      case 1551: return "File Size";
      case 1553: return "Filename";
      case 1792: return "ProjectGroup";
      case 1793: return "Acquisition Date";
      case 1794: return "Last modified by";
      case 1795: return "User company";
      case 1796: return "User company logo";
      case 1797: return "Image";
      case 1800: return "User ID";
      case 1801: return "User Name";
      case 1802: return "User City";
      case 1803: return "User Address";
      case 1804: return "User Country";
      case 1805: return "User Phone";
      case 1806: return "User Fax";
      case 2049: return "Objective Name";
      case 2050: return "Optovar";
      case 2051: return "Reflector";
      case 2052: return "Condenser Contrast";
      case 2053: return "Transmitted Light Filter 1";
      case 2054: return "Transmitted Light Filter 2";
      case 2055: return "Reflected Light Shutter";
      case 2056: return "Condenser Front Lens";
      case 2057: return "Excitation Filter Name";
      case 2060: return "Transmitted Light Fieldstop Aperture";
      case 2061: return "Reflected Light Aperture";
      case 2062: return "Condenser N.A.";
      case 2063: return "Light Path";
      case 2064: return "HalogenLampOn";
      case 2065: return "Halogen Lamp Mode";
      case 2066: return "Halogen Lamp Voltage";
      case 2068: return "Fluorescence Lamp Level";
      case 2069: return "Fluorescence Lamp Intensity";
      case 2070: return "LightManagerEnabled";
      case 2071: return "tag_ID_2071";
      case 2072: return "Focus Position";
      case 2073: return "Stage Position X";
      case 2074: return "Stage Position Y";
      case 2075: return "Microscope Name";
      case 2076: return "Objective Magnification";
      case 2077: return "Objective N.A.";
      case 2078: return "MicroscopeIllumination";
      case 2079: return "External Shutter 1";
      case 2080: return "External Shutter 2";
      case 2081: return "External Shutter 3";
      case 2082: return "External Filter Wheel 1 Name";
      case 2083: return "External Filter Wheel 2 Name";
      case 2084: return "Parfocal Correction";
      case 2086: return "External Shutter 4";
      case 2087: return "External Shutter 5";
      case 2088: return "External Shutter 6";
      case 2089: return "External Filter Wheel 3 Name";
      case 2090: return "External Filter Wheel 4 Name";
      case 2103: return "Objective Turret Position";
      case 2104: return "Objective Contrast Method";
      case 2105: return "Objective Immersion Type";
      case 2107: return "Reflector Position";
      case 2109: return "Transmitted Light Filter 1 Position";
      case 2110: return "Transmitted Light Filter 2 Position";
      case 2112: return "Excitation Filter Position";
      case 2113: return "Lamp Mirror Position";
      case 2114: return "External Filter Wheel 1 Position";
      case 2115: return "External Filter Wheel 2 Position";
      case 2116: return "External Filter Wheel 3 Position";
      case 2117: return "External Filter Wheel 4 Position";
      case 2118: return "Lightmanager Mode";
      case 2119: return "Halogen Lamp Calibration";
      case 2120: return "CondenserNAGoSpeed";
      case 2121: return "TransmittedLightFieldstopGoSpeed";
      case 2122: return "OptovarGoSpeed";
      case 2123: return "Focus calibrated";
      case 2124: return "FocusBasicPosition";
      case 2125: return "FocusPower";
      case 2126: return "FocusBacklash";
      case 2127: return "FocusMeasurementOrigin";
      case 2128: return "FocusMeasurementDistance";
      case 2129: return "FocusSpeed";
      case 2130: return "FocusGoSpeed";
      case 2131: return "FocusDistance";
      case 2132: return "FocusInitPosition";
      case 2133: return "Stage calibrated";
      case 2134: return "StagePower";
      case 2135: return "StageXBacklash";
      case 2136: return "StageYBacklash";
      case 2137: return "StageSpeedX";
      case 2138: return "StageSpeedY";
      case 2139: return "StageSpeed";
      case 2140: return "StageGoSpeedX";
      case 2141: return "StageGoSpeedY";
      case 2142: return "StageStepDistanceX";
      case 2143: return "StageStepDistanceY";
      case 2144: return "StageInitialisationPositionX";
      case 2145: return "StageInitialisationPositionY";
      case 2146: return "MicroscopeMagnification";
      case 2147: return "ReflectorMagnification";
      case 2148: return "LampMirrorPosition";
      case 2149: return "FocusDepth";
      case 2150: return "MicroscopeType";
      case 2151: return "Objective Working Distance";
      case 2152: return "ReflectedLightApertureGoSpeed";
      case 2153: return "External Shutter";
      case 2154: return "ObjectiveImmersionStop";
      case 2155: return "Focus Start Speed";
      case 2156: return "Focus Acceleration";
      case 2157: return "ReflectedLightFieldstop";
      case 2158: return "ReflectedLightFieldstopGoSpeed";
      case 2159: return "ReflectedLightFilter 1";
      case 2160: return "ReflectedLightFilter 2";
      case 2161: return "ReflectedLightFilter1Position";
      case 2162: return "ReflectedLightFilter2Position";
      case 2163: return "TransmittedLightAttenuator";
      case 2164: return "ReflectedLightAttenuator";
      case 2165: return "Transmitted Light Shutter";
      case 2166: return "TransmittedLightAttenuatorGoSpeed";
      case 2167: return "ReflectedLightAttenuatorGoSpeed";
      case 2176: return "TransmittedLightVirtualFilterPosition";
      case 2177: return "TransmittedLightVirtualFilter";
      case 2178: return "ReflectedLightVirtualFilterPosition";
      case 2179: return "ReflectedLightVirtualFilter";
      case 2180: return "ReflectedLightHalogenLampMode";
      case 2181: return "ReflectedLightHalogenLampVoltage";
      case 2182: return "ReflectedLightHalogenLampColorTemperature";
      case 2183: return "ContrastManagerMode";
      case 2184: return "Dazzle Protection Active";
      case 2195: return "Zoom";
      case 2196: return "ZoomGoSpeed";
      case 2197: return "LightZoom";
      case 2198: return "LightZoomGoSpeed";
      case 2199: return "LightZoomCoupled";
      case 2200: return "TransmittedLightHalogenLampMode";
      case 2201: return "TransmittedLightHalogenLampVoltage";
      case 2202: return "TransmittedLightHalogenLampColorTemperature";
      case 2203: return "Reflected Coldlight Mode";
      case 2204: return "Reflected Coldlight Intensity";
      case 2205: return "Reflected Coldlight Color Temperature";
      case 2206: return "Transmitted Coldlight Mode";
      case 2207: return "Transmitted Coldlight Intensity";
      case 2208: return "Transmitted Coldlight Color Temperature";
      case 2209: return "Infinityspace Portchanger Position";
      case 2210: return "Beamsplitter Infinity Space";
      case 2211: return "TwoTv VisCamChanger Position";
      case 2212: return "Beamsplitter Ocular";
      case 2213: return "TwoTv CamerasChanger Position";
      case 2214: return "Beamsplitter Cameras";
      case 2215: return "Ocular Shutter";
      case 2216: return "TwoTv CamerasChangerCube";
      case 2218: return "Ocular Magnification";
      case 2219: return "Camera Adapter Magnification";
      case 2220: return "Microscope Port";
      case 2221: return "Ocular Total Magnification";
      case 2222: return "Field of View";
      case 2223: return "Ocular";
      case 2224: return "CameraAdapter";
      case 2225: return "StageJoystickEnabled";
      case 2226: return "ContrastManager Contrast Method";
      case 2229: return "CamerasChanger Beamsplitter Type";
      case 2235: return "Rearport Slider Position";
      case 2236: return "Rearport Source";
      case 2237: return "Beamsplitter Type Infinity Space";
      case 2238: return "Fluorescence Attenuator";
      case 2239: return "Fluorescence Attenuator Position";
      case 2261: return "Objective ID";
      case 2262: return "Reflector ID";
      case 2307: return "Camera Framestart Left";
      case 2308: return "Camera Framestart Top";
      case 2309: return "Camera Frame Width";
      case 2310: return "Camera Frame Height";
      case 2311: return "Camera Binning";
      case 2312: return "CameraFrameFull";
      case 2313: return "CameraFramePixelDistance";
      case 2318: return "DataFormatUseScaling";
      case 2319: return "CameraFrameImageOrientation";
      case 2320: return "VideoMonochromeSignalType";
      case 2321: return "VideoColorSignalType";
      case 2322: return "MeteorChannelInput";
      case 2323: return "MeteorChannelSync";
      case 2324: return "WhiteBalanceEnabled";
      case 2325: return "CameraWhiteBalanceRed";
      case 2326: return "CameraWhiteBalanceGreen";
      case 2327: return "CameraWhiteBalanceBlue";
      case 2331: return "CameraFrameScalingFactor";
      case 2562: return "Meteor Camera Type";
      case 2564: return "Exposure Time [ms]";
      case 2568: return "CameraExposureTimeAutoCalculate";
      case 2569: return "Meteor Gain Value";
      case 2571: return "Meteor Gain Automatic";
      case 2572: return "MeteorAdjustHue";
      case 2573: return "MeteorAdjustSaturation";
      case 2574: return "MeteorAdjustRedLow";
      case 2575: return "MeteorAdjustGreenLow";
      case 2576: return "Meteor Blue Low";
      case 2577: return "MeteorAdjustRedHigh";
      case 2578: return "MeteorAdjustGreenHigh";
      case 2579: return "MeteorBlue High";
      case 2582: return "CameraExposureTimeCalculationControl";
      case 2585: return "AxioCamFadingCorrectionEnable";
      case 2587: return "CameraLiveImage";
      case 2588: return "CameraLiveEnabled";
      case 2589: return "LiveImageSyncObjectName";
      case 2590: return "CameraLiveSpeed";
      case 2591: return "CameraImage";
      case 2592: return "CameraImageWidth";
      case 2593: return "CameraImageHeight";
      case 2594: return "CameraImagePixelType";
      case 2595: return "CameraImageShMemoryName";
      case 2596: return "CameraLiveImageWidth";
      case 2597: return "CameraLiveImageHeight";
      case 2598: return "CameraLiveImagePixelType";
      case 2599: return "CameraLiveImageShMemoryName";
      case 2600: return "CameraLiveMaximumSpeed";
      case 2601: return "CameraLiveBinning";
      case 2602: return "CameraLiveGainValue";
      case 2603: return "CameraLiveExposureTimeValue";
      case 2604: return "CameraLiveScalingFactor";
      case 2819: return "Image Index Z";
      case 2820: return "Image Channel Index";
      case 2821: return "Image Index T";
      case 2822: return "ImageTile Index";
      case 2823: return "Image acquisition Index";
      case 2827: return "Image IndexS";
      case 2841: return "Original Stage Position X";
      case 2842: return "Original Stage Position Y";
      case 3088: return "LayerDrawFlags";
      case 3334: return "RemainingTime";
      case 3585: return "User Field 1";
      case 3586: return "User Field 2";
      case 3587: return "User Field 3";
      case 3588: return "User Field 4";
      case 3589: return "User Field 5";
      case 3590: return "User Field 6";
      case 3591: return "User Field 7";
      case 3592: return "User Field 8";
      case 3593: return "User Field 9";
      case 3594: return "User Field 10";
      case 3840: return "ID";
      case 3841: return "Name";
      case 3842: return "Value";
      case 5501: return "PvCamClockingMode";
      case 8193: return "Autofocus Status Report";
      case 8194: return "Autofocus Position";
      case 8195: return "Autofocus Position Offset";
      case 8196: return "Autofocus Empty Field Threshold";
      case 8197: return "Autofocus Calibration Name";
      case 8198: return "Autofocus Current Calibration Item";
      case 20478: return "tag_ID_20478";
      case 65537: return "CameraFrameFullWidth";
      case 65538: return "CameraFrameFullHeight";
      case 65541: return "AxioCam Shutter Signal";
      case 65542: return "AxioCam Delay Time";
      case 65543: return "AxioCam Shutter Control";
      case 65544: return "AxioCam BlackRefIsCalculated";
      case 65545: return "AxioCam Black Reference";
      case 65547: return "Camera Shading Correction";
      case 65550: return "AxioCam Enhance Color";
      case 65551: return "AxioCam NIR Mode";
      case 65552: return "CameraShutterCloseDelay";
      case 65553: return "CameraWhiteBalanceAutoCalculate";
      case 65556: return "AxioCam NIR Mode Available";
      case 65557: return "AxioCam Fading Correction Available";
      case 65559: return "AxioCam Enhance Color Available";
      case 65565: return "MeteorVideoNorm";
      case 65566: return "MeteorAdjustWhiteReference";
      case 65567: return "MeteorBlackReference";
      case 65568: return "MeteorChannelInputCountMono";
      case 65570: return "MeteorChannelInputCountRGB";
      case 65571: return "MeteorEnableVCR";
      case 65572: return "Meteor Brightness";
      case 65573: return "Meteor Contrast";
      case 65575: return "AxioCam Selector";
      case 65576: return "AxioCam Type";
      case 65577: return "AxioCam Info";
      case 65580: return "AxioCam Resolution";
      case 65581: return "AxioCam Color Model";
      case 65582: return "AxioCam MicroScanning";
      case 65585: return "Amplification Index";
      case 65586: return "Device Command";
      case 65587: return "BeamLocation";
      case 65588: return "ComponentType";
      case 65589: return "ControllerType";
      case 65590: return "CameraWhiteBalanceCalculationRedPaint";
      case 65591: return "CameraWhiteBalanceCalculationBluePaint";
      case 65592: return "CameraWhiteBalanceSetRed";
      case 65593: return "CameraWhiteBalanceSetGreen";
      case 65594: return "CameraWhiteBalanceSetBlue";
      case 65595: return "CameraWhiteBalanceSetTargetRed";
      case 65596: return "CameraWhiteBalanceSetTargetGreen";
      case 65597: return "CameraWhiteBalanceSetTargetBlue";
      case 65598: return "ApotomeCamCalibrationMode";
      case 65599: return "ApoTome Grid Position";
      case 65600: return "ApotomeCamScannerPosition";
      case 65601: return "ApoTome Full Phase Shift";
      case 65602: return "ApoTome Grid Name";
      case 65603: return "ApoTome Staining";
      case 65604: return "ApoTome Processing Mode";
      case 65605: return "ApotomeCamLiveCombineMode";
      case 65606: return "ApoTome Filter Name";
      case 65607: return "Apotome Filter Strength";
      case 65608: return "ApotomeCamFilterHarmonics";
      case 65609: return "ApoTome Grating Period";
      case 65610: return "ApoTome Auto Shutter Used";
      case 65611: return "Apotome Cam Status";
      case 65612: return "ApotomeCamNormalize";
      case 65613: return "ApotomeCamSettingsManager";
      case 65614: return "DeepviewCamSupervisorMode";
      case 65615: return "DeepView Processing";
      case 65616: return "DeepviewCamFilterName";
      case 65617: return "DeepviewCamStatus";
      case 65618: return "DeepviewCamSettingsManager";
      case 65619: return "DeviceScalingName";
      case 65620: return "CameraShadingIsCalculated";
      case 65621: return "CameraShadingCalculationName";
      case 65622: return "CameraShadingAutoCalculate";
      case 65623: return "CameraTriggerAvailable";
      case 65626: return "CameraShutterAvailable";
      case 65627: return "AxioCam ShutterMicroScanningEnable";
      case 65628: return "ApotomeCamLiveFocus";
      case 65629: return "DeviceInitStatus";
      case 65630: return "DeviceErrorStatus";
      case 65631: return "ApotomeCamSliderInGridPosition";
      case 65632: return "Orca NIR Mode Used";
      case 65633: return "Orca Analog Gain";
      case 65634: return "Orca Analog Offset";
      case 65635: return "Orca Binning";
      case 65636: return "Orca Bit Depth";
      case 65637: return "ApoTome Averaging Count";
      case 65638: return "DeepView DoF";
      case 65639: return "DeepView EDoF";
      case 65643: return "DeepView Slider Name";
      case 65655: return "DeepView Slider Name";
      case 5439491: return "Acquisition Sofware";
      case 16777488: return "Excitation Wavelength";
      case 16777489: return "Emission Wavelength";
      case 101515267: return "File Name";
      case 101253123:
      case 101777411:
        return "Image Name";
      default: return "" + tagID;
    }
  }

  private long parseTimestamp(String s) {
    long stamp = 0;
    try {
      stamp = Long.parseLong(s);
    }
    catch (NumberFormatException exc) {
      if (s != null) {
        stamp = DateTools.getTime(s, "M/d/y h:mm:ss aa");
      }
    }
    return stamp;
  }

}

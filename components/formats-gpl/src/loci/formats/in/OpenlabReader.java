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

import java.io.IOException;
import java.util.Vector;

import loci.common.ByteArrayHandle;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.ChannelSeparator;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.LZOCodec;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;

/**
 * OpenlabReader is the file format reader for Openlab LIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/OpenlabReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/OpenlabReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Jim Paris jim at jtan.com
 */
public class OpenlabReader extends FormatReader {

  // -- Constants --

  public static final long LIFF_MAGIC_BYTES = 0xffff696d7072L;

  /** Image types. */
  private static final int MAC_1_BIT = 1;
  private static final int MAC_4_GREYS = 2;
  private static final int MAC_16_GREYS = 3;
  private static final int MAC_16_COLORS = 4;
  private static final int MAC_256_GREYS = 5;
  private static final int MAC_256_COLORS = 6;
  private static final int MAC_16_BIT_COLOR = 7;
  private static final int MAC_24_BIT_COLOR = 8;
  private static final int DEEP_GREY_9 = 9;
  private static final int DEEP_GREY_10 = 10;
  private static final int DEEP_GREY_11 = 11;
  private static final int DEEP_GREY_12 = 12;
  private static final int DEEP_GREY_13 = 13;
  private static final int DEEP_GREY_14 = 14;
  private static final int DEEP_GREY_15 = 15;
  private static final int DEEP_GREY_16 = 16;

  /** Tag types. */
  private static final int IMAGE_TYPE_1 = 67;
  private static final int IMAGE_TYPE_2 = 68;
  private static final int CALIBRATION = 69;
  private static final int USER = 72;

  // -- Static fields --

  /** Helper reader to read PICT data. */
  private PictReader pict = new PictReader();

  // -- Fields --

  /** LIFF version (should be 2 or 5). */
  private int version;

  /** Number of series. */
  private int numSeries;

  private PlaneInfo[] planes;
  private float xcal, ycal;

  private long nextTag = 0;
  private int tag = 0, subTag = 0;
  private String fmt = "";
  private int[][] planeOffsets;

  private Vector<byte[][]> luts;
  private int lastPlane = 0;

  private String gain, detectorOffset, xPos, yPos, zPos;
  private boolean specialPlateNames = false;

  // -- Constructor --

  /** Constructs a new OpenlabReader. */
  public OpenlabReader() {
    super("Openlab LIFF", "liff");
    suffixNecessary = false;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readLong() == LIFF_MAGIC_BYTES;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    if (luts != null) {
      if (getSeries() < planeOffsets.length &&
        lastPlane < planeOffsets[getSeries()].length)
      {
        return luts.get(planeOffsets[getSeries()][lastPlane]);
      }
      else if (lastPlane < luts.size()) {
        return luts.get(lastPlane);
      }
    }
    return null;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    lastPlane = no;

    PlaneInfo planeInfo = null;
    if (specialPlateNames) {
      planeInfo = getPlane(getZCTCoords(no));
    }
    else if (no < planeOffsets[getSeries()].length) {
      int index = planeOffsets[getSeries()][no];
      planeInfo = planes[index];
    }
    if (planeInfo == null) return buf;

    long first = planeInfo.planeOffset;
    long last = first + FormatTools.getPlaneSize(this) * 2;

    int bpp = FormatTools.getBytesPerPixel(getPixelType());

    if (!planeInfo.pict) {
      if (version == 2) {
        in.seek(first);
        readPlane(in, x, y, w, h, buf);
      }
      else {
        in.seek(first + 16);
        int bytes = bpp * getRGBChannelCount();
        byte[] b = new byte[(int) (last - first)];
        in.read(b);

        CodecOptions options = new CodecOptions();
        options.width = getSizeX();
        options.height = getSizeY();
        options.bitsPerSample = bytes * 8;
        options.maxBytes = getSizeX() * getSizeY() * bytes;
        b = new LZOCodec().decompress(b, options);

        if (getSizeX() * getSizeY() * 4 <= b.length) {
          for (int yy=y; yy<h + y; yy++) {
            for (int xx=x; xx<w + x; xx++) {
              System.arraycopy(b, (yy*(getSizeX()+4) + xx)*4 + 1, buf,
                ((yy - y)*w + xx - x)*3, 3);
            }
          }
        }
        else {
          int src = b.length / getSizeY();
          if (src - (getSizeX() * bytes) != 16) src = getSizeX() * bytes;
          int dest = w * bytes;
          for (int row=0; row<h; row++) {
            System.arraycopy(b, (row + y)*src + x*bytes, buf, row*dest, dest);
          }
        }
        b = null;
      }
      if (planeInfo.volumeType == MAC_256_GREYS ||
        planeInfo.volumeType == MAC_256_COLORS)
      {
        for (int i=0; i<buf.length; i++) {
          buf[i] = (byte) (~buf[i] & 0xff);
        }
      }
    }
    else {
      // PICT plane
      Exception exc = null;
      if (getPixelType() == FormatTools.UINT8) {
        in.seek(first);
        byte[] b = new byte[(int) (last - first) + 512];
        in.read(b, 512, b.length - 512);

        IFormatReader r =
          getRGBChannelCount() == 1 ? new ChannelSeparator(pict) : pict;
        try {
          Location.mapFile("OPENLAB_PICT", new ByteArrayHandle(b));
          r.setId("OPENLAB_PICT");

          if (getPixelType() != r.getPixelType()) {
            throw new FormatException("Pixel type of inner PICT does not " +
              "match pixel type of Openlab file");
          }

          if (isIndexed()) {
            int index = no;
            if (getSeries() < planeOffsets.length) {
              index = planeOffsets[getSeries()][lastPlane];
            }
            luts.setElementAt(pict.get8BitLookupTable(), index);
          }

          r.openBytes(0, buf, x, y, w, h);
        }
        catch (FormatException e) { exc = e; }
        catch (IOException e) { exc = e; }
        finally {
          r.close();
          // remove file from map
          Location.mapFile("OPENLAB_PICT", null);
        }
        b = null;
      }
      if (exc != null || getPixelType() != FormatTools.UINT8) {
        LOGGER.debug("", exc);
        in.seek(planeInfo.planeOffset - 298);

        if (in.readByte() == 1) in.skipBytes(297);
        else in.skipBytes(169);

        int size = 0, expectedBlock = 0, totalBlocks = -1, pixPos = 0;

        byte[] plane = new byte[FormatTools.getPlaneSize(this)];

        while (expectedBlock != totalBlocks && pixPos < plane.length &&
          in.getFilePointer() + 32 < last)
        {
          findNextBlock();

          if (in.getFilePointer() + 4 >= in.length()) break;

          int num = in.readInt();
          if (num != expectedBlock) {
            throw new FormatException("Expected iPic block not found");
          }

          expectedBlock++;
          if (totalBlocks == -1) {
            totalBlocks = in.readInt();
            in.skipBytes(8);
          }
          else in.skipBytes(12);

          size = in.readInt();
          in.skipBytes(4);

          if (size + pixPos > plane.length) size = plane.length - pixPos;

          in.read(plane, pixPos, size);
          pixPos += size;
        }

        RandomAccessInputStream pix = new RandomAccessInputStream(plane);
        readPlane(pix, x, y, w, h, buf);
        pix.close();
        plane = null;
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (pict != null) pict.close(fileOnly);
    if (!fileOnly) {
      planes = null;
      luts = null;
      lastPlane = 0;
      version = 0;
      numSeries = 0;
      xcal = ycal = 0f;
      nextTag = 0;
      tag = subTag = 0;
      fmt = "";
      planeOffsets = null;
      gain = detectorOffset = null;
      xPos = yPos = zPos = null;
      specialPlateNames = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    luts = new Vector<byte[][]>();

    LOGGER.info("Verifying Openlab LIFF format");

    in.order(false);
    in.seek(4);
    if (!in.readString(4).equals("impr")) {
      throw new FormatException("Invalid LIFF file.");
    }

    version = in.readInt();

    if (version != 2 && version != 5) {
      throw new FormatException("Invalid version : " + version);
    }

    // total number of planes in the file
    int planeCount = in.readShort();

    planes = new PlaneInfo[planeCount];

    // skip the ID seed
    in.skipBytes(2);

    // read offset to first plane
    int offset = in.readInt();
    in.seek(offset);

    LOGGER.info("Finding image offsets");

    xcal = ycal = 0.0f;

    // scan through the file, and read image information

    int imagesFound = 0;

    Vector<PlaneInfo> representativePlanes = new Vector<PlaneInfo>();

    while (in.getFilePointer() + 8 < in.length()) {
      long fp = in.getFilePointer();
      readTagHeader();
      while (tag < IMAGE_TYPE_1 || tag > 76) {
        in.seek(--fp);
        readTagHeader();
      }

      if (tag == IMAGE_TYPE_1 || tag == IMAGE_TYPE_2) {
        // found an image

        planes[imagesFound] = new PlaneInfo();

        planes[imagesFound].pict = fmt.toLowerCase().equals("pict");
        planes[imagesFound].compressed = subTag == 0;

        in.skipBytes(24);
        planes[imagesFound].volumeType = in.readShort();
        in.skipBytes(16);
        long pointer = in.getFilePointer();
        planes[imagesFound].planeName = in.readCString().trim();
        in.skipBytes((int) (256 - in.getFilePointer() + pointer));

        planes[imagesFound].planeOffset = in.getFilePointer();

        // read the image dimensions
        if (version == 2) {
          in.skipBytes(2);
          int top = in.readShort();
          int left = in.readShort();
          int bottom = in.readShort();
          int right = in.readShort();

          planes[imagesFound].width = right - left;
          planes[imagesFound].height = bottom - top;
        }
        else {
          planes[imagesFound].width = in.readInt();
          planes[imagesFound].height = in.readInt();
        }

        for (int i=0; i<representativePlanes.size(); i++) {
          PlaneInfo p = representativePlanes.get(i);
          if (planes[imagesFound].width == p.width &&
            planes[imagesFound].height == p.height &&
            (planes[imagesFound].volumeType == p.volumeType ||
            (planes[imagesFound].volumeType >= DEEP_GREY_9 &&
            p.volumeType >= DEEP_GREY_9)))
          {
            planes[imagesFound].series = i;
            break;
          }
        }

        if (planes[imagesFound].series == -1 &&
          !planes[imagesFound].planeName.equals("Original Image"))
        {
          planes[imagesFound].series = representativePlanes.size();
          representativePlanes.add(planes[imagesFound]);
        }

        // read the LUT, if present

        if (planes[imagesFound].volumeType == MAC_256_COLORS) {
          in.seek(nextTag - (257 * 8));
          byte[][] lut = new byte[3][256];
          for (int i=0; i<256; i++) {
            in.skipBytes(2);
            lut[0][255 - i] = (byte) (in.readShort() >> 8);
            lut[1][255 - i] = (byte) (in.readShort() >> 8);
            lut[2][255 - i] = (byte) (in.readShort() >> 8);
          }
          luts.add(lut);
        }
        else luts.add(null);

        imagesFound++;
      }
      else if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM)
      {
        if (tag == CALIBRATION) {
          in.skipBytes(4);
          short units = in.readShort();
          float scaling = units == 3 ? 0.001f : 1.0f;
          in.skipBytes(12);

          xcal = in.readFloat() * scaling;
          ycal = in.readFloat() * scaling;
        }
        else if (tag == USER) {
          String className = in.readCString();

          if (className.equals("CVariableList")) {
            char achar = in.readChar();

            if (achar == 1) {
              int numVars = in.readShort();
              for (int i=0; i<numVars; i++) {
                readVariable();
              }
            }
          }
        }
      }

      in.seek(nextTag);
    }

    int nSeries = representativePlanes.size();
    planeOffsets = new int[nSeries][];
    Vector<Integer> tmpOffsets = new Vector<Integer>();
    Vector<String> names = new Vector<String>();

    core.clear();
    for (int i=0; i<nSeries; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      setSeries(i);

      for (int q=0; q<planes.length; q++) {
        if (planes[q] != null && planes[q].series == i) {
          tmpOffsets.add(q);
          names.add(planes[q].planeName);
        }
      }
      planeOffsets[i] = new int[tmpOffsets.size()];
      for (int q=0; q<planeOffsets[i].length; q++) {
        planeOffsets[i][q] = tmpOffsets.get(q);
        addSeriesMetaList("Plane Name", names.get(q));
      }
      tmpOffsets.clear();
      names.clear();
    }

    // populate core metadata

    for (int i=0; i<nSeries; i++) {
      setSeries(i);
      CoreMetadata ms = core.get(i);

      ms.indexed = false;
      ms.sizeX = planes[planeOffsets[i][0]].width;
      ms.sizeY = planes[planeOffsets[i][0]].height;
      ms.imageCount = planeOffsets[i].length;
      ms.sizeC = 1;

      switch (planes[planeOffsets[i][0]].volumeType) {
        case MAC_1_BIT:
        case MAC_4_GREYS:
        case MAC_256_GREYS:
          ms.pixelType = FormatTools.UINT8;
          ms.indexed = planes[planeOffsets[i][0]].pict;
          break;
        case MAC_256_COLORS:
          ms.pixelType = FormatTools.UINT8;
          ms.indexed = true;
          break;
        case MAC_16_COLORS:
        case MAC_16_BIT_COLOR:
        case MAC_24_BIT_COLOR:
          ms.pixelType = FormatTools.UINT8;
          ms.sizeC = 3;
          break;
        case DEEP_GREY_9:
          ms.bitsPerPixel = 9;
          break;
        case DEEP_GREY_10:
          ms.bitsPerPixel = 10;
          break;
        case DEEP_GREY_11:
          ms.bitsPerPixel = 11;
          break;
        case DEEP_GREY_12:
          ms.bitsPerPixel = 12;
          break;
        case DEEP_GREY_13:
          ms.bitsPerPixel = 13;
          break;
        case DEEP_GREY_14:
          ms.bitsPerPixel = 14;
          break;
        case DEEP_GREY_15:
          ms.bitsPerPixel = 15;
          break;
        case MAC_16_GREYS:
        case DEEP_GREY_16:
          ms.pixelType = FormatTools.UINT16;
          break;
        default:
          throw new FormatException("Unsupported plane type: " +
            planes[planeOffsets[i][0]].volumeType);
      }

      if (getBitsPerPixel() > 8) ms.pixelType = FormatTools.UINT16;
      ms.rgb = getSizeC() > 1;
      ms.interleaved = isRGB() && version == 5;
      ms.sizeT = 1;
      ms.sizeZ = getImageCount();
      ms.dimensionOrder = "XYCZT";
      ms.littleEndian = false;
      ms.falseColor = false;
      ms.metadataComplete = true;
    }

    int seriesCount = getSeriesCount();
    for (int s=0; s<seriesCount; s++) {
      setSeries(s);
      parseImageNames(s);
    }
    setSeries(0);

    MetadataStore store = makeFilterMetadata();

    MetadataLevel level = getMetadataOptions().getMetadataLevel();
    boolean planeInfoNeeded = level != MetadataLevel.MINIMUM &&
      (xPos != null || yPos != null || zPos != null);

    MetadataTools.populatePixels(store, this, planeInfoNeeded);

    if (level != MetadataLevel.MINIMUM) {
      // populate MetadataStore

      PositiveFloat sizeX = FormatTools.getPhysicalSizeX(new Double(xcal));
      PositiveFloat sizeY = FormatTools.getPhysicalSizeY(new Double(ycal));

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }

      // link Instrument and Image
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      try {
        if (gain != null) {
          store.setDetectorSettingsGain(new Double(gain), 0, 0);
        }
      }
      catch (NumberFormatException e) { }
      try {
        if (detectorOffset != null) {
          store.setDetectorSettingsOffset(new Double(detectorOffset), 0, 0);
        }
      }
      catch (NumberFormatException e) { }

      // link DetectorSettings to an actual Detector
      String detectorID = MetadataTools.createLSID("Detector", 0, 0);
      store.setDetectorID(detectorID, 0, 0);

      store.setDetectorType(getDetectorType("Other"), 0, 0);

      for (int c=0; c<getEffectiveSizeC(); c++) {
        PlaneInfo plane = getPlane(new int[] {0, c, 0});
        if (plane != null) {
          store.setChannelName(plane.channelName, 0, c);
          store.setDetectorSettingsID(detectorID, 0, c);
        }
      }

      Double stageX = xPos == null ? null : new Double(xPos);
      Double stageY = yPos == null ? null : new Double(yPos);
      Double stageZ = zPos == null ? null : new Double(zPos);

      if (stageX != null || stageY != null || stageZ != null) {
        for (int series=0; series<getSeriesCount(); series++) {
          setSeries(series);
          for (int plane=0; plane<getImageCount(); plane++) {
            if (stageX != null) {
              store.setPlanePositionX(stageX, series, plane);
            }
            if (stageY != null) {
              store.setPlanePositionY(stageY, series, plane);
            }
            if (stageZ != null) {
              store.setPlanePositionZ(stageZ, series, plane);
            }
          }
        }
      }
    }
    setSeries(0);
  }

  // -- Helper methods --

  /** Read the next tag. */
  private void readTagHeader() throws IOException {
    tag = in.readShort();
    subTag = in.readShort();

    nextTag = (version == 2 ? in.readInt() : in.readLong());

    fmt = in.readString(4);
    in.skipBytes(version == 2 ? 4 : 8);
  }

  private void readVariable() throws FormatException, IOException {
    String className = in.readCString();

    String name = "", value = "";

    int derivedClassVersion = in.read();
    if (derivedClassVersion != 1) {
      throw new FormatException("Invalid revision");
    }

    if (className.equals("CStringVariable")) {
      int strSize = in.readInt();
      value = in.readString(strSize);
      in.skipBytes(1);
    }
    else if (className.equals("CFloatVariable")) {
      value = String.valueOf(in.readDouble());
    }

    int baseClassVersion = in.read();
    if (baseClassVersion == 1 || baseClassVersion == 2) {
      int strSize = in.readInt();
      name = in.readString(strSize);
      in.skipBytes(baseClassVersion * 2 + 1);
    }
    else {
      throw new FormatException("Invalid revision: " + baseClassVersion);
    }

    addGlobalMeta(name, value);

    if (name.equals("Gain")) gain = value;
    else if (name.equals("Offset")) detectorOffset = value;
    else if (name.equals("X-Y Stage: X Position")) {
      xPos = value;
      addGlobalMeta("X position for position #1", xPos);
    }
    else if (name.equals("X-Y Stage: Y Position")) {
      yPos = value;
      addGlobalMeta("Y position for position #1", yPos);
    }
    else if (name.equals("ZPosition")) {
      zPos = value;
      addGlobalMeta("Z position for position #1", zPos);
    }
  }

  private void parseImageNames(int s) {
    Vector<String> uniqueF = new Vector<String>();
    Vector<String> uniqueC = new Vector<String>();
    Vector<String> uniqueZ = new Vector<String>();
    Vector<String> uniqueT = new Vector<String>();
    String[] axes = new String[] {"Z", "C", "T"};

    CoreMetadata m = core.get(s);

    m.dimensionOrder = "XY";
    for (PlaneInfo plane : planes) {
      if (plane == null || plane.series != s) continue;

      String name = plane.planeName;

      // check for a specific name format:
      // <channel name><optional Z section>_<plate>_<well>_<field>

      String[] tokens = name.split("_");
      boolean validField = false;
      try {
        Integer.parseInt(tokens[tokens.length - 1]);
        validField = true;
      }
      catch (NumberFormatException e) { }

      if (tokens.length == 4 && !tokens[0].toLowerCase().endsWith("xy") &&
        validField)
      {
        specialPlateNames = true;

        if (!uniqueF.contains(tokens[3])) {
          uniqueF.add(tokens[3]);
        }
        plane.channelName = tokens[0];
        int endIndex = 0;
        while (endIndex < plane.channelName.length() &&
          !Character.isDigit(plane.channelName.charAt(endIndex)))
        {
          endIndex++;
        }
        String zSection = plane.channelName.substring(endIndex);
        if (zSection.equals("")) zSection = "1";
        plane.channelName = plane.channelName.substring(0, endIndex);

        if (!uniqueC.contains(plane.channelName)) {
          uniqueC.add(plane.channelName);
        }
        if (!uniqueZ.contains(zSection)) {
          uniqueZ.add(zSection);
        }

        m.dimensionOrder = "XYCTZ";
        plane.wavelength = uniqueC.indexOf(plane.channelName);
        plane.timepoint = 0;
        plane.zPosition = uniqueZ.indexOf(zSection);
        plane.series = uniqueF.indexOf(tokens[3]);
      }
      else {
        for (String axis : axes) {
          Vector<String> unique = null;
          if (axis.equals("Z")) unique = uniqueZ;
          else if (axis.equals("C")) unique = uniqueC;
          else if (axis.equals("T")) unique = uniqueT;

          int index = name.indexOf(axis + "=");
          if (index == -1) index = name.indexOf(axis + " =");
          if (index != -1) {
            int nextEqual = name.indexOf("=", index + 3);
            if (nextEqual < 0) {
              nextEqual = (int) Math.min(index + 3, name.length());
            }
            int end = name.lastIndexOf(" ", nextEqual);
            if (end < index) end = name.length();

            String i = name.substring(name.indexOf("=", index), end);
            if (!unique.contains(i)) {
              unique.add(i);
              if (unique.size() > 1 &&
                m.dimensionOrder.indexOf(axis) == -1)
              {
                m.dimensionOrder += axis;
              }
            }
          }
        }
      }
    }

    if (specialPlateNames) {
      m.sizeC *= uniqueC.size();
      m.sizeT = uniqueT.size();
      if (m.sizeT == 0) m.sizeT = 1;
      m.sizeZ = uniqueZ.size();
      if (m.sizeZ == 0) m.sizeZ = 1;
      m.imageCount = m.sizeC * m.sizeZ * m.sizeT;
      if (uniqueF.size() > core.size()) {
        CoreMetadata currentSeries = core.get(s);
        int seriesCount = uniqueF.size();
        core.clear();
        for (int i=0; i<seriesCount; i++) {
          core.add(currentSeries);
        }
      }
      return;
    }

    m = core.get(s); // Just in case the resize changed it

    if (m.rgb && uniqueC.size() <= 1) {
      m.dimensionOrder = m.dimensionOrder.replaceAll("C", "");
      m.dimensionOrder = "XYC" + m.dimensionOrder.substring(2);
    }

    for (String axis : axes) {
      if (m.dimensionOrder.indexOf(axis) == -1) {
        m.dimensionOrder += axis;
      }
    }
    if (uniqueC.size() > 1) {
      m.sizeC *= uniqueC.size();
      m.sizeZ /= uniqueC.size();
    }
    if (uniqueT.size() > 1) {
      m.sizeT = uniqueT.size();
      m.sizeZ /= m.sizeT;
    }

    int newCount = getSizeZ() * getSizeT();
    if (!isRGB()) newCount *= getSizeC();

    if (newCount < getImageCount()) {
      char firstAxis = getDimensionOrder().charAt(2);
      if (firstAxis == 'Z') m.sizeZ++;
      else if (firstAxis == 'C' && !isRGB()) m.sizeC++;
      else m.sizeT++;
      m.imageCount = getSizeZ() * getSizeT();
      if (!isRGB()) m.imageCount *= getSizeC();
    }
    else if (newCount > getImageCount()) m.imageCount = newCount;
  }

  private PlaneInfo getPlane(int[] zct) {
    for (PlaneInfo plane : planes) {
      if (plane != null && plane.zPosition == zct[0] &&
        plane.wavelength == zct[1] && plane.timepoint == zct[2] &&
        plane.series == getSeries())
      {
        return plane;
      }
    }
    return null;
  }

  private void findNextBlock() throws IOException {
    byte[] buf = new byte[8192];
    in.read(buf);
    boolean found = false;

    while (!found && in.getFilePointer() < in.length()) {
      for (int i=0; i<buf.length-7; i++) {
        if (buf[i] == 0x49 && buf[i + 1] == 0x56 && buf[i + 2] == 0x45 &&
          buf[i + 3] == 0x41 && buf[i + 4] == 0x64 && buf[i + 5] == 0x62 &&
          buf[i + 6] == 0x70 && buf[i + 7] == 0x71)
        {
          found = true;
          in.seek(in.getFilePointer() - buf.length + i + 8);
          break;
        }
      }
      if (!found) {
        for (int i=0; i<7; i++) {
          buf[i] = buf[buf.length - (7 - i)];
        }
        in.read(buf, 7, buf.length - 7);
      }
    }
  }

  // -- Helper classes --

  /** Helper class for storing plane info. */
  protected class PlaneInfo {
    protected long planeOffset;
    protected int zPosition;
    protected int wavelength;
    protected int timepoint;
    protected String planeName;
    protected long timestamp;
    protected boolean pict;
    protected boolean compressed;
    protected int volumeType;
    protected int width;
    protected int height;
    protected int series = -1;
    protected String channelName;
  }

}

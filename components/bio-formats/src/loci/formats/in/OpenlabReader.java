//
// OpenlabReader.java
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
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * OpenlabReader is the file format reader for Openlab LIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/OpenlabReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/OpenlabReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
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
  private static PictReader pict = new PictReader();

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

  private Vector luts;
  private int lastPlane;

  private String gain, detectorOffset, xPos, yPos, zPos;

  // -- Constructor --

  /** Constructs a new OpenlabReader. */
  public OpenlabReader() {
    super("Openlab LIFF", "liff");
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readLong() == LIFF_MAGIC_BYTES;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    Object lut = luts.get(planeOffsets[series][lastPlane]);
    if (lut == null) return null;
    return lut instanceof byte[][] ? (byte[][]) lut : null;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    lastPlane = no;

    int index = planeOffsets[series][no];

    long first = planes[index].planeOffset;
    long last = no == getImageCount() - 1 ? in.length() :
      planes[planeOffsets[series][no + 1]].planeOffset;
    in.seek(first);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());

    if (!planes[index].pict) {
      if (version == 2) {
        readPlane(in, x, y, w, h, buf);
      }
      else {
        in.skipBytes(16);
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
      if (planes[index].volumeType == MAC_256_GREYS ||
        planes[index].volumeType == MAC_256_COLORS)
      {
        for (int i=0; i<buf.length; i++) {
          buf[i] = (byte) (~buf[i] & 0xff);
        }
      }
    }
    else {
      // PICT plane
      byte[] b = new byte[(int) (last - first) + 512];
      in.read(b, 512, b.length - 512);
      Exception exc = null;
      IFormatReader r =
        getRGBChannelCount() == 1 ? new ChannelSeparator(pict) : pict;
      try {
        Location.mapFile("OPENLAB_PICT", new ByteArrayHandle(b));
        r.setId("OPENLAB_PICT");

        if (getPixelType() != pict.getPixelType()) {
          throw new FormatException("Pixel type of inner PICT does not match " +
            "pixel type of Openlab file");
        }

        if (isIndexed()) {
          luts.setElementAt(pict.get8BitLookupTable(),
            planeOffsets[series][lastPlane]);
        }

        r.openBytes(0, buf, x, y, w, h);
        r.close();
        // remove file from map
        Location.mapFile("OPENLAB_PICT", null);
      }
      catch (FormatException e) { exc = e; }
      catch (IOException e) { exc = e; }
      b = null;

      if (exc != null) {
        r.close();
        traceDebug(exc);
        in.seek(planes[index].planeOffset - 298);

        if (in.readByte() == 1) in.skipBytes(128);
        in.skipBytes(169);

        int size = 0, expectedBlock = 0, totalBlocks = -1, pixPos = 0;

        byte[] plane = new byte[FormatTools.getPlaneSize(this)];

        while (expectedBlock != totalBlocks &&
          in.getFilePointer() + 32 < in.length())
        {
          while (in.readLong() != 0x4956454164627071L &&
            in.getFilePointer() < in.length())
          {
            in.seek(in.getFilePointer() - 7);
          }

          if (in.getFilePointer() + 4 >= in.length()) break;

          int num = in.readInt();
          if (num != expectedBlock) {
            throw new FormatException("Expected iPic block not found");
          }

          expectedBlock++;
          if (totalBlocks == -1) totalBlocks = in.readInt();
          else in.skipBytes(4);

          in.skipBytes(8);
          size = in.readInt();
          in.skipBytes(4);

          if (size + pixPos > plane.length) size = plane.length - pixPos;

          in.read(plane, pixPos, size);
          pixPos += size;
        }

        int srcRow = getSizeX() * bpp * getRGBChannelCount();
        int rowLen = w * bpp * getRGBChannelCount();
        for (int row=0; row<h; row++) {
          System.arraycopy(plane, (row + y) * srcRow +
            x * bpp * getRGBChannelCount(), buf, row*rowLen, rowLen);
        }
        plane = null;
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly) {
      if (in != null) in.close();
      if (pict != null) pict.close(fileOnly);
    }
    else close();
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (pict != null) pict.close();
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
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("OpenlabReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    luts = new Vector();

    status("Verifying Openlab LIFF format");

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

    status("Finding image offsets");

    xcal = ycal = 0.0f;

    // scan through the file, and read image information

    int imagesFound = 0;

    Vector representativePlanes = new Vector();

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
          PlaneInfo p = (PlaneInfo) representativePlanes.get(i);
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
      else if (tag == CALIBRATION) {
        in.skipBytes(4);
        short units = in.readShort();
        in.skipBytes(12);

        xcal = in.readFloat();
        ycal = in.readFloat();

        float scaling = units == 3 ? 0.001f : 1.0f;

        xcal *= scaling;
        ycal *= scaling;
      }
      else if (tag == USER) {
        String className = in.readCString();

        if (className.equals("CVariableList")) {
          char achar = in.readChar();

          if (achar == 1) {
            int numVars = in.readShort();
            for (int i=0; i<numVars; i++) {
              className = in.readCString();

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
                throw new FormatException("Invalid revision: " +
                  baseClassVersion);
              }

              addGlobalMeta(name, value);

              if (name.equals("Gain")) gain = value;
              else if (name.equals("Offset")) detectorOffset = value;
              else if (name.equals("X-Y Stage: X Position")) xPos = value;
              else if (name.equals("X-Y Stage: Y Position")) yPos = value;
              else if (name.equals("ZPosition")) zPos = value;
            }
          }
        }
      }

      in.seek(nextTag);
    }

    int nSeries = representativePlanes.size();
    planeOffsets = new int[nSeries][];
    Vector<Integer> tmpOffsets = new Vector<Integer>();
    Vector names = new Vector();

    core = new CoreMetadata[nSeries];

    for (int i=0; i<nSeries; i++) {
      setSeries(i);
      core[i] = new CoreMetadata();

      for (int q=0; q<planes.length; q++) {
        if (planes[q] != null && planes[q].series == i) {
          tmpOffsets.add(q);
          names.add(planes[q].planeName);
        }
      }
      planeOffsets[i] = new int[tmpOffsets.size()];
      for (int q=0; q<planeOffsets[i].length; q++) {
        planeOffsets[i][q] = tmpOffsets.get(q);
        addSeriesMeta("Plane " + q + " Name", names.get(q));
      }
      tmpOffsets.clear();
      names.clear();
    }
    setSeries(0);

    // populate core metadata

    for (int i=0; i<nSeries; i++) {
      core[i].indexed = false;
      core[i].sizeX = planes[planeOffsets[i][0]].width;
      core[i].sizeY = planes[planeOffsets[i][0]].height;
      core[i].imageCount = planeOffsets[i].length;

      switch (planes[planeOffsets[i][0]].volumeType) {
        case MAC_1_BIT:
        case MAC_4_GREYS:
        case MAC_256_GREYS:
          core[i].pixelType = FormatTools.UINT8;
          core[i].rgb = false;
          core[i].sizeC = 1;
          core[i].interleaved = false;
          core[i].indexed = planes[planeOffsets[i][0]].pict;
          break;
        case MAC_256_COLORS:
          core[i].pixelType = FormatTools.UINT8;
          core[i].rgb = false;
          core[i].sizeC = 1;
          core[i].interleaved = false;
          core[i].indexed = true;
          break;
        case MAC_16_COLORS:
        case MAC_16_BIT_COLOR:
        case MAC_24_BIT_COLOR:
          core[i].pixelType = FormatTools.UINT8;
          core[i].rgb = true;
          core[i].sizeC = 3;
          core[i].interleaved = version == 5;
          break;
        case MAC_16_GREYS:
        case DEEP_GREY_9:
        case DEEP_GREY_10:
        case DEEP_GREY_11:
        case DEEP_GREY_12:
        case DEEP_GREY_13:
        case DEEP_GREY_14:
        case DEEP_GREY_15:
        case DEEP_GREY_16:
          core[i].pixelType = FormatTools.UINT16;
          core[i].rgb = false;
          core[i].sizeC = 1;
          core[i].interleaved = false;
          break;
        default:
          throw new FormatException("Unsupported plane type: " +
            planes[planeOffsets[i][0]].volumeType);
      }

      core[i].sizeT = 1;
      core[i].sizeZ = core[i].imageCount;
      core[i].dimensionOrder = "XYCZT";
      core[i].littleEndian = false;
      core[i].falseColor = false;
      core[i].metadataComplete = true;
      //core[i].seriesMetadata = getMetadata();
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    MetadataTools.populatePixels(store, this);

    // populate MetadataStore

    store.setDimensionsPhysicalSizeX(new Float(xcal), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(ycal), 0, 0);

    // link Instrument and Image
    store.setInstrumentID("Instrument:0", 0);
    store.setImageInstrumentRef("Instrument:0", 0);

    try {
      if (gain != null) store.setDetectorSettingsGain(new Float(gain), 0, 0);
    }
    catch (NumberFormatException e) { }
    try {
      if (detectorOffset != null) {
        store.setDetectorSettingsOffset(new Float(detectorOffset), 0, 0);
      }
    }
    catch (NumberFormatException e) { }

    // link DetectorSettings to an actual Detector
    store.setDetectorID("Detector:0", 0, 0);
    store.setDetectorSettingsDetector("Detector:0", 0, 0);

    store.setDetectorType("Unknown", 0, 0);

    if (xPos != null) store.setStagePositionPositionX(new Float(xPos), 0, 0, 0);
    if (yPos != null) store.setStagePositionPositionY(new Float(yPos), 0, 0, 0);
    if (zPos != null) store.setStagePositionPositionZ(new Float(zPos), 0, 0, 0);
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

  // -- Helper classes --

  /** Helper class for storing plane info. */
  protected class PlaneInfo {
    protected long planeOffset;
    protected int zPosition;
    protected int wavelength;
    protected String planeName;
    protected long timestamp;
    protected boolean pict;
    protected boolean compressed;
    protected int volumeType;
    protected int width;
    protected int height;
    protected int series = -1;
  }

}

//
// LIFReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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

import java.io.*;
import java.util.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.*;

/**
 * LIFReader is the file format reader for Leica LIF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/LIFReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/LIFReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LIFReader extends FormatReader {

  // -- Constants --

  private static final Hashtable CHANNEL_PRIORITIES = createChannelPriorities();

  private static Hashtable createChannelPriorities() {
    Hashtable h = new Hashtable();

    h.put("red", new Integer(0));
    h.put("green", new Integer(1));
    h.put("blue", new Integer(2));
    h.put("cyan", new Integer(3));
    h.put("magenta", new Integer(4));
    h.put("yellow", new Integer(5));
    h.put("black", new Integer(6));
    h.put("gray", new Integer(7));
    h.put("", new Integer(8));

    return h;
  }

  private static final byte[][][] BYTE_LUTS = createByteLUTs();

  private static byte[][][] createByteLUTs() {
    byte[][][] lut = new byte[6][3][256];
    for (int i=0; i<256; i++) {
      lut[0][0][i] = (byte) (i & 0xff);
      lut[1][1][i] = (byte) (i & 0xff);
      lut[2][2][i] = (byte) (i & 0xff);

      lut[3][1][i] = (byte) (i & 0xff);
      lut[3][2][i] = (byte) (i & 0xff);

      lut[4][0][i] = (byte) (i & 0xff);
      lut[4][2][i] = (byte) (i & 0xff);

      lut[5][0][i] = (byte) (i & 0xff);
      lut[5][1][i] = (byte) (i & 0xff);
    }
    return lut;
  }

  private static final short[][][] SHORT_LUTS = createShortLUTs();

  private static short[][][] createShortLUTs() {
    short[][][] lut = new short[6][3][65536];
    for (int i=0; i<65536; i++) {
      lut[0][0][i] = (short) (i & 0xffff);
      lut[1][1][i] = (short) (i & 0xffff);
      lut[2][2][i] = (short) (i & 0xffff);

      lut[3][1][i] = (short) (i & 0xffff);
      lut[3][2][i] = (short) (i & 0xffff);

      lut[4][0][i] = (short) (i & 0xffff);
      lut[4][2][i] = (short) (i & 0xffff);

      lut[5][0][i] = (short) (i & 0xffff);
      lut[5][1][i] = (short) (i & 0xffff);
    }
    return lut;
  }

  // -- Fields --

  /** Offsets to memory blocks, paired with their corresponding description. */
  private Vector offsets;

  /** Bits per pixel. */
  private int[] bitsPerPixel;

  /** Extra dimensions. */
  private int[] extraDimensions;

  private int numDatasets;

  private int[][] channelMap;
  private int[][] realChannel;
  private int lastChannel = -1;

  // -- Constructor --

  /** Constructs a new Leica LIF reader. */
  public LIFReader() { super("Leica Image File Format", "lif"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return stream.read() == 0x70;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT8 || lastChannel == -1) return null;
    return lastChannel < BYTE_LUTS.length ? BYTE_LUTS[lastChannel] : null;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT16 || lastChannel == -1) return null;
    return lastChannel < SHORT_LUTS.length ? SHORT_LUTS[lastChannel] : null;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    if (!isRGB()) {
      int[] pos = getZCTCoords(no);
      lastChannel = realChannel[series][pos[1]];
      pos[1] = DataTools.indexOf(channelMap[series], pos[1]);
      no = getIndex(pos[0], pos[1], pos[2]);
    }

    long offset = ((Long) offsets.get(series)).longValue();
    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int bpp = bytes * getRGBChannelCount();

    long nextOffset = series + 1 < offsets.size() ?
      ((Long) offsets.get(series + 1)).longValue() : in.length();
    int bytesToSkip =
      (int) (nextOffset - offset - bpp * getSizeX() * getSizeY());
    bytesToSkip /= getSizeY();
    if ((getSizeX() % 4) == 0) bytesToSkip = 0;

    in.seek(offset + getSizeX() * getSizeY() * (long) no * bpp);
    in.skipBytes(bytesToSkip * getSizeY() * no);

    if (bytesToSkip == 0) {
      readPlane(in, x, y, w, h, buf);
    }
    else {
      in.skipBytes(y * getSizeX() * bpp + y * bytesToSkip);
      for (int row=0; row<h; row++) {
        in.skipBytes(x * bpp);
        in.read(buf, row * w * bpp, w * bpp);
        in.skipBytes(bpp * (getSizeX() - w - x) + bytesToSkip);
      }
    }

    // color planes are stored in BGR order
    if (getRGBChannelCount() == 3) {
      for (int i=0; i<buf.length; i+=bpp) {
        for (int b=0; b<bytes; b++) {
          byte tmp = buf[i + b];
          buf[i + b] = buf[i + bytes*2];
          buf[i + bytes*2] = tmp;
        }
      }
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    offsets = null;
    bitsPerPixel = null;
    extraDimensions = null;
    numDatasets = -1;
    channelMap = null;
    realChannel = null;
    lastChannel = -1;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LIFReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    offsets = new Vector();

    core[0].littleEndian = true;
    in.order(isLittleEndian());

    // read the header

    status("Reading header");

    byte checkOne = (byte) in.read();
    in.skipBytes(2);
    byte checkTwo = (byte) in.read();
    if (checkOne != 0x70 && checkTwo != 0x70) {
      throw new FormatException(id + " is not a valid Leica LIF file");
    }

    in.skipBytes(4);

    // read and parse the XML description

    if (in.read() != 0x2a) {
      throw new FormatException("Invalid XML description");
    }

    // number of Unicode characters in the XML block
    int nc = in.readInt();
    String xml = DataTools.stripString(in.readString(nc * 2));

    status("Finding image offsets");

    while (in.getFilePointer() < in.length()) {
      if (in.readInt() != 0x70) {
        throw new FormatException("Invalid Memory Block");
      }

      in.skipBytes(4);
      if (in.read() != 0x2a) {
        throw new FormatException("Invalid Memory Description");
      }

      long blockLength = in.readInt();
      if (in.read() != 0x2a) {
        in.seek(in.getFilePointer() - 5);
        blockLength = in.readLong();
        if (in.read() != 0x2a) {
          throw new FormatException("Invalid Memory Description");
        }
      }

      int descrLength = in.readInt();
      in.skipBytes(descrLength * 2);

      if (blockLength > 0) {
        offsets.add(new Long(in.getFilePointer()));
      }
      long skipped = 0;
      while (skipped < blockLength) {
        if (blockLength - skipped > 4096) {
          skipped += in.skipBytes(4096);
        }
        else {
          skipped += in.skipBytes((int) (blockLength - skipped));
        }
      }
    }
    initMetadata(xml);
  }

  // -- Helper methods --

  /** Parses a string of XML and puts the values in a Hashtable. */
  private void initMetadata(String xml) throws FormatException, IOException {
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    LeicaHandler handler = new LeicaHandler(new DummyMetadata());

    // the XML blocks stored in a LIF file are invalid,
    // because they don't have a root node

    xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><LEICA>" + xml +
      "</LEICA>";

    xml = DataTools.sanitizeXML(xml);
    DataTools.parseXML(xml, handler);

    Vector widths = handler.getWidths();
    Vector heights = handler.getHeights();
    Vector zs = handler.getZs();
    Vector ts = handler.getTs();
    Vector channels = handler.getChannels();
    Vector bps = handler.getBPS();
    Vector extraDims = handler.getExtraDims();
    metadata = handler.getMetadata();
    Vector containerNames = handler.getContainerNames();
    Vector containerCounts = handler.getContainerCounts();
    Vector seriesNames = handler.getSeriesNames();
    Vector xcal = handler.getXCal();
    Vector ycal = handler.getYCal();
    Vector zcal = handler.getZCal();
    Vector bits = handler.getBits();
    Vector lutNames = handler.getLutNames();

    numDatasets = widths.size();

    bitsPerPixel = new int[numDatasets];
    extraDimensions = new int[numDatasets];

    // set up mapping to rearrange channels
    // for instance, the green channel may be #0, and the red channel may be #1
    channelMap = new int[numDatasets][];
    realChannel = new int[numDatasets][];
    int nextLut = 0;
    for (int i=0; i<channelMap.length; i++) {
      channelMap[i] = new int[((Integer) channels.get(i)).intValue()];
      realChannel[i] = new int[((Integer) channels.get(i)).intValue()];

      String[] luts = new String[channelMap[i].length];
      for (int q=0; q<luts.length; q++) {
        luts[q] = ((String) lutNames.get(nextLut++)).toLowerCase();
      }

      for (int q=0; q<channelMap[i].length; q++) {
        if (!CHANNEL_PRIORITIES.containsKey(luts[q])) luts[q] = "";
        realChannel[i][q] =
          ((Integer) CHANNEL_PRIORITIES.get(luts[q])).intValue();
      }

      int[] sorted = new int[channelMap[i].length];
      Arrays.fill(sorted, -1);

      for (int q=0; q<sorted.length; q++) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int n=0; n<channelMap[i].length; n++) {
          if (realChannel[i][n] < min && !DataTools.containsValue(sorted, n)) {
            min = realChannel[i][n];
            minIndex = n;
          }
        }

        sorted[q] = minIndex;
      }

      for (int q=0; q<channelMap[i].length; q++) {
        channelMap[i][sorted[q]] = q;
      }
    }

    // Populate metadata store

    status("Populating metadata");

    core = new CoreMetadata[numDatasets];

    // populate Pixels data

    for (int i=0; i<numDatasets; i++) {
      core[i] = new CoreMetadata();
      core[i].orderCertain = true;
      core[i].dimensionOrder = "XYCZT";
      core[i].sizeX = ((Integer) widths.get(i)).intValue();
      core[i].sizeY = ((Integer) heights.get(i)).intValue();
      core[i].sizeZ = ((Integer) zs.get(i)).intValue();
      core[i].sizeC = ((Integer) channels.get(i)).intValue();
      core[i].sizeT = ((Integer) ts.get(i)).intValue();

      bitsPerPixel[i] = ((Integer) bps.get(i)).intValue();
      extraDimensions[i] = ((Integer) extraDims.get(i)).intValue();

      if (extraDimensions[i] > 1) {
        if (core[i].sizeZ == 1) core[i].sizeZ = extraDimensions[i];
        else core[i].sizeT *= extraDimensions[i];
        extraDimensions[i] = 1;
      }

      int nBits = ((Integer) bits.get(i)).intValue();

      core[i].metadataComplete = true;
      core[i].littleEndian = true;
      core[i].rgb =
        nBits == (bitsPerPixel[i] * core[i].sizeC) && core[i].sizeC > 1;
      core[i].interleaved = core[i].rgb;
      core[i].imageCount = core[i].sizeZ * core[i].sizeT;
      if (!core[i].rgb) core[i].imageCount *= core[i].sizeC;
      core[i].indexed = realChannel[i][0] < BYTE_LUTS.length ||
        realChannel[i][0] < SHORT_LUTS.length;
      core[i].falseColor = true;

      while (bitsPerPixel[i] % 8 != 0) bitsPerPixel[i]++;
      switch (bitsPerPixel[i]) {
        case 8:
          core[i].pixelType = FormatTools.UINT8;
          break;
        case 16:
          core[i].pixelType = FormatTools.UINT16;
          break;
        case 32:
          core[i].pixelType = FormatTools.FLOAT;
          break;
      }
    }
    MetadataTools.populatePixels(store, this, true);

    store.setInstrumentID("Instrument:0", 0);

    for (int i=0; i<numDatasets; i++) {
      // populate Dimensions data
      Float xf = i < xcal.size() ? (Float) xcal.get(i) : null;
      Float yf = i < ycal.size() ? (Float) ycal.get(i) : null;
      Float zf = i < zcal.size() ? (Float) zcal.get(i) : null;

      store.setDimensionsPhysicalSizeX(xf, i, 0);
      store.setDimensionsPhysicalSizeY(yf, i, 0);
      store.setDimensionsPhysicalSizeZ(zf, i, 0);

      // populate Image data

      String seriesName = (String) seriesNames.get(i);
      if (seriesName == null || seriesName.trim().length() == 0) {
        seriesName = "Series " + (i + 1);
      }
      store.setImageName(seriesName, i);
      MetadataTools.setDefaultCreationDate(store, getCurrentFile(), i);

      // link Instrument and Image
      store.setImageInstrumentRef("Instrument:0", i);

      // CTR CHECK
//      String zoom = (String) getMeta(seriesName + " - dblZoom");
//      store.setDisplayOptions(zoom == null ? null : new Float(zoom),
//        new Boolean(core[i].sizeC > 1), new Boolean(core[i].sizeC > 1),
//        new Boolean(core[i].sizeC > 2), new Boolean(isRGB()), null,
//        null, null, null, null, ii, null, null, null, null, null);

      Enumeration keys = metadata.keys();
      while (keys.hasMoreElements()) {
        String k = (String) keys.nextElement();
        if (k.startsWith(seriesName + " ")) {
          core[i].seriesMetadata.put(k, metadata.get(k));
        }
      }
    }

    DataTools.parseXML(xml, new LeicaHandler(store));
  }

}

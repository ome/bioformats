//
// LIFReader.java
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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.meta.DummyMetadata;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

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

  public static final byte LIF_MAGIC_BYTE = 0x70;
  public static final byte LIF_MEMORY_BYTE = 0x2a;

  private static final Hashtable<String, Integer> CHANNEL_PRIORITIES =
    createChannelPriorities();

  private static Hashtable<String, Integer> createChannelPriorities() {
    Hashtable<String, Integer> h = new Hashtable<String, Integer>();

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
  private Vector<Long> offsets;

  private int[][] realChannel;
  private int lastChannel = -1;

  // -- Constructor --

  /** Constructs a new Leica LIF reader. */
  public LIFReader() { super("Leica Image File Format", "lif"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;
    return stream.read() == LIF_MAGIC_BYTE;
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
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (!isRGB()) {
      int[] pos = getZCTCoords(no);
      lastChannel = realChannel[series][pos[1]];
    }

    long offset = offsets.get(series).longValue();
    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int bpp = bytes * getRGBChannelCount();

    long planeSize = (long) getSizeX() * getSizeY() * bpp;

    long nextOffset = series + 1 < offsets.size() ?
      offsets.get(series + 1).longValue() : in.length();
    int bytesToSkip = (int) (nextOffset - offset - planeSize * getImageCount());
    bytesToSkip /= getSizeY();
    if ((getSizeX() % 4) == 0) bytesToSkip = 0;

    in.seek(offset + planeSize * no);
    in.skipBytes(bytesToSkip * getSizeY() * no);

    if (bytesToSkip == 0) {
      readPlane(in, x, y, w, h, buf);
    }
    else {
      in.skipBytes(y * (getSizeX() * bpp + bytesToSkip));
      for (int row=0; row<h; row++) {
        in.skipBytes(x * bpp);
        in.read(buf, row * w * bpp, w * bpp);
        in.skipBytes(bpp * (getSizeX() - w - x) + bytesToSkip);
      }
    }

    // color planes are stored in BGR order
    if (getRGBChannelCount() == 3) {
      ImageTools.bgrToRgb(buf, isInterleaved(), bytes);
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    offsets = null;
    realChannel = null;
    lastChannel = -1;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("LIFReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    offsets = new Vector<Long>();

    in.order(true);

    // read the header

    status("Reading header");

    byte checkOne = in.readByte();
    in.skipBytes(2);
    byte checkTwo = in.readByte();
    if (checkOne != LIF_MAGIC_BYTE && checkTwo != LIF_MAGIC_BYTE) {
      throw new FormatException(id + " is not a valid Leica LIF file");
    }

    in.skipBytes(4);

    // read and parse the XML description

    if (in.read() != LIF_MEMORY_BYTE) {
      throw new FormatException("Invalid XML description");
    }

    // number of Unicode characters in the XML block
    int nc = in.readInt();
    String xml = DataTools.stripString(in.readString(nc * 2));

    status("Finding image offsets");

    while (in.getFilePointer() < in.length()) {
      if (in.readInt() != LIF_MAGIC_BYTE) {
        throw new FormatException("Invalid Memory Block");
      }

      in.skipBytes(4);
      if (in.read() != LIF_MEMORY_BYTE) {
        throw new FormatException("Invalid Memory Description");
      }

      long blockLength = in.readInt();
      if (in.read() != LIF_MEMORY_BYTE) {
        in.seek(in.getFilePointer() - 5);
        blockLength = in.readLong();
        if (in.read() != LIF_MEMORY_BYTE) {
          throw new FormatException("Invalid Memory Description");
        }
      }

      int descrLength = in.readInt() * 2;

      if (blockLength > 0) {
        offsets.add(new Long(in.getFilePointer() + descrLength));
      }

      in.seek(in.getFilePointer() + descrLength + blockLength);
    }
    initMetadata(xml);
  }

  // -- Helper methods --

  /** Parses a string of XML and puts the values in a Hashtable. */
  private void initMetadata(String xml) throws FormatException, IOException {
    LeicaHandler handler = new LeicaHandler(new DummyMetadata());

    // the XML blocks stored in a LIF file are invalid,
    // because they don't have a root node

    xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><LEICA>" + xml +
      "</LEICA>";

    xml = XMLTools.sanitizeXML(xml);
    XMLTools.parseXML(xml, handler);

    metadata = handler.getGlobalMetadata();
    Vector<String> lutNames = handler.getLutNames();

    core = handler.getCoreMetadata().toArray(new CoreMetadata[0]);

    // set up mapping to rearrange channels
    // for instance, the green channel may be #0, and the red channel may be #1
    realChannel = new int[getSeriesCount()][];
    int nextLut = 0;
    for (int i=0; i<getSeriesCount(); i++) {
      realChannel[i] = new int[core[i].sizeC];

      for (int q=0; q<core[i].sizeC; q++) {
        String lut = lutNames.get(nextLut++).toLowerCase();
        if (!CHANNEL_PRIORITIES.containsKey(lut)) lut = "";
        realChannel[i][q] = CHANNEL_PRIORITIES.get(lut).intValue();
      }

      int[] sorted = new int[core[i].sizeC];
      Arrays.fill(sorted, -1);

      for (int q=0; q<sorted.length; q++) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int n=0; n<core[i].sizeC; n++) {
          if (realChannel[i][n] < min && !DataTools.containsValue(sorted, n)) {
            min = realChannel[i][n];
            minIndex = n;
          }
        }

        sorted[q] = minIndex;
      }
    }

    // Populate metadata store

    status("Populating metadata");

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    // populate Pixels/Image data

    MetadataTools.populatePixels(store, this, true);

    for (int i=0; i<getSeriesCount(); i++) {
      MetadataTools.setDefaultCreationDate(store, getCurrentFile(), i);
    }
    XMLTools.parseXML(xml, new LeicaHandler(store));
  }

}

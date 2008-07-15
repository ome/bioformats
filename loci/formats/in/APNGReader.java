//
// APNGReader.java
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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.imageio.ImageIO;
import loci.formats.*;
import loci.formats.codec.ByteVector;
import loci.formats.meta.*;

/**
 * APNGReader is the file format reader for
 * Animated Portable Network Graphics (APNG) images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/APNGReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/APNGReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class APNGReader extends FormatReader {

  // -- Constants --

  /** Valid values for dispose operation field. */
  private static final int DISPOSE_OP_NONE = 0;
  private static final int DISPOSE_OP_BACKGROUND = 1;
  private static final int DISPOSE_OP_PREVIOUS = 2;

  private static final byte[] PNG_SIGNATURE = new byte[] {
    (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a
  };

  // -- Fields --

  private Vector blocks;
  private Vector frameCoordinates;

  // -- Constructor --

  /** Constructs a new APNGReader. */
  public APNGReader() {
    super("Animated PNG", "png");
    blockCheckLen = 64;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return true;
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

    byte[] t = ImageTools.getBytes(openImage(no, x, y, w, h), false, no);
    int bytesPerChannel = w * h;
    if (t.length > bytesPerChannel) {
      buf = new byte[bytesPerChannel * 3];
      for (int i=0; i<3; i++) {
        System.arraycopy(t, i * bytesPerChannel, buf, i * bytesPerChannel,
          bytesPerChannel);
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int, int, int, int) */
  public BufferedImage openImage(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    if (no == 0) {
      in.seek(0);
      DataInputStream dis =
        new DataInputStream(new BufferedInputStream(in, 4096));
      return ImageIO.read(dis).getSubimage(x, y, w, h);
    }

    ByteVector stream = new ByteVector();
    stream.add(PNG_SIGNATURE);

    byte[] len = new byte[4];

    boolean fdatValid = false;
    int fctlCount = 0;

    int[] coords = (int[]) frameCoordinates.get(no);

    for (int i=0; i<blocks.size(); i++) {
      PNGBlock block = (PNGBlock) blocks.get(i);
      if (!block.type.equals("IDAT") && !block.type.equals("fdAT") &&
        !block.type.equals("acTL") && !block.type.equals("fcTL"))
      {
        DataTools.unpackBytes(block.length, len, 0, 4, isLittleEndian());
        stream.add(len);
        byte[] b = new byte[block.length + 8];
        byte[] typeBytes = block.type.getBytes();
        System.arraycopy(typeBytes, 0, b, 0, 4);
        in.seek(block.offset);
        in.read(b, 4, b.length - 8);
        if (block.type.equals("IHDR")) {
          DataTools.unpackBytes(coords[2], b, 4, 4, isLittleEndian());
          DataTools.unpackBytes(coords[3], b, 8, 4, isLittleEndian());
        }
        int crc = (int) computeCRC(b, b.length - 4);
        DataTools.unpackBytes(crc, b, b.length - 4, 4, isLittleEndian());
        stream.add(b);
        b = null;
      }
      else if (block.type.equals("fcTL")) {
        fdatValid = fctlCount == no;
        fctlCount++;
      }
      else if (block.type.equals("fdAT")) {
        in.seek(block.offset + 4);
        if (fdatValid) {
          DataTools.unpackBytes(block.length - 4, len, 0, 4, isLittleEndian());
          stream.add(len);
          byte[] b = new byte[block.length + 4];
          b[0] = 'I';
          b[1] = 'D';
          b[2] = 'A';
          b[3] = 'T';
          in.read(b, 4, b.length - 8);
          int crc = (int) computeCRC(b, b.length - 4);
          DataTools.unpackBytes(crc, b, b.length - 4, 4, isLittleEndian());
          stream.add(b);
          b = null;
        }
      }
    }

    RandomAccessStream s = new RandomAccessStream(stream.toByteArray());
    DataInputStream dis = new DataInputStream(new BufferedInputStream(s, 4096));
    BufferedImage b = ImageIO.read(dis);
    dis.close();
    BufferedImage first = openImage(0, 0, 0, getSizeX(), getSizeY());

    // paste current image onto first image

    WritableRaster firstRaster = first.getRaster();
    WritableRaster currentRaster = b.getRaster();

    firstRaster.setDataElements(coords[0], coords[1], currentRaster);
    return new BufferedImage(first.getColorModel(), firstRaster, false, null);
  }

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close();
  }

  // -- Internal FormatReader methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("APNGReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    // check that this is a valid PNG file
    byte[] signature = new byte[8];
    in.read(signature);

    if (signature[0] != (byte) 0x89 || signature[1] != 0x50 ||
      signature[2] != 0x4e || signature[3] != 0x47 || signature[4] != 0x0d ||
      signature[5] != 0x0a || signature[6] != 0x1a || signature[7] != 0x0a)
    {
      throw new FormatException("Invalid PNG signature.");
    }

    // read data chunks - each chunk consists of the following:
    // 1) 32 bit length
    // 2) 4 char type
    // 3) 'length' bytes of data
    // 4) 32 bit CRC

    blocks = new Vector();
    frameCoordinates = new Vector();

    int currentImage = 0;

    while (in.getFilePointer() < in.length()) {
      int length = in.readInt();
      String type = in.readString(4);

      PNGBlock block = new PNGBlock();
      block.length = length;
      block.type = type;
      block.offset = in.getFilePointer();
      blocks.add(block);

      if (type.equals("acTL")) {
        // APNG-specific chunk
        core.imageCount[0] = in.readInt();
        int loop = in.readInt();
        addMeta("Loop count", String.valueOf(loop));
      }
      else if (type.equals("fcTL")) {
        in.skipBytes(4);
        int w = in.readInt();
        int h = in.readInt();
        int x = in.readInt();
        int y = in.readInt();
        frameCoordinates.add(new int[] {x, y, w, h});
        in.skipBytes(length - 20);
      }
      else in.skipBytes(length);

      in.skipBytes(4); // skip the CRC
    }

    if (core.imageCount[0] == 0) core.imageCount[0] = 1;
    core.sizeZ[0] = 1;
    core.sizeT[0] = core.imageCount[0];

    core.currentOrder[0] = "XYCTZ";
    core.interleaved[0] = true;

    BufferedImage img =
      ImageIO.read(new DataInputStream(new RandomAccessStream(currentId)));
    core.sizeX[0] = img.getWidth();
    core.sizeY[0] = img.getHeight();
    core.rgb[0] = img.getRaster().getNumBands() > 1;
    core.sizeC[0] = isRGB() ? 3 : 1;
    core.pixelType[0] = ImageTools.getPixelType(img);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  private long computeCRC(byte[] buf, int len) {
    CRC32 crc = new CRC32();
    crc.update(buf, 0, len);
    return crc.getValue();
  }

  // -- Helper class --

  class PNGBlock {
    public long offset;
    public int length;
    public String type;
  }

}

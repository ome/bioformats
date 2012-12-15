/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.gui.AWTImageTools;
import loci.formats.meta.MetadataStore;

/**
 * APNGReader is the file format reader for
 * Animated Portable Network Graphics (APNG) images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/APNGReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/APNGReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class APNGReader extends BIFormatReader {

  // -- Constants --

  private static final byte[] PNG_SIGNATURE = new byte[] {
    (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a
  };

  // -- Fields --

  private Vector<PNGBlock> blocks;
  private Vector<int[]> frameCoordinates;

  private byte[][] lut;

  private BufferedImage lastImage;
  private int lastImageIndex = -1;

  // -- Constructor --

  /** Constructs a new APNGReader. */
  public APNGReader() {
    super("Animated PNG", "png");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;

    byte[] signature = new byte[blockLen];
    stream.read(signature);

    if (signature[0] != (byte) 0x89 || signature[1] != 0x50 ||
      signature[2] != 0x4e || signature[3] != 0x47 || signature[4] != 0x0d ||
      signature[5] != 0x0a || signature[6] != 0x1a || signature[7] != 0x0a)
    {
      return false;
    }
    return true;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    return lut;
  }

  /* @see loci.formats.IFormatReader#openPlane(int, int, int, int, int int) */
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, -1, x, y, w, h);

    if (no == lastImageIndex && lastImage != null) {
      return AWTImageTools.getSubimage(lastImage, isLittleEndian(), x, y, w, h);
    }

    if (no == 0) {
      in.seek(0);
      DataInputStream dis =
        new DataInputStream(new BufferedInputStream(in, 4096));
      lastImage = ImageIO.read(dis);
      lastImageIndex = 0;
      if (x == 0 && y == 0 && w == getSizeX() && h == getSizeY()) {
        return lastImage;
      }
      return AWTImageTools.getSubimage(lastImage, isLittleEndian(), x, y, w, h);
    }

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    stream.write(PNG_SIGNATURE);

    boolean fdatValid = false;
    int fctlCount = 0;

    int[] coords = frameCoordinates.get(no);

    for (PNGBlock block : blocks) {
      if (!block.type.equals("IDAT") && !block.type.equals("fdAT") &&
        !block.type.equals("acTL") && !block.type.equals("fcTL") &&
        block.length > 0)
      {
        byte[] b = new byte[block.length + 12];
        DataTools.unpackBytes(block.length, b, 0, 4, isLittleEndian());
        byte[] typeBytes = block.type.getBytes(Constants.ENCODING);
        System.arraycopy(typeBytes, 0, b, 4, 4);
        in.seek(block.offset);
        in.read(b, 8, b.length - 12);
        if (block.type.equals("IHDR")) {
          DataTools.unpackBytes(coords[2], b, 8, 4, isLittleEndian());
          DataTools.unpackBytes(coords[3], b, 12, 4, isLittleEndian());
        }
        int crc = (int) computeCRC(b, b.length - 4);
        DataTools.unpackBytes(crc, b, b.length - 4, 4, isLittleEndian());
        stream.write(b);
        b = null;
      }
      else if (block.type.equals("fcTL")) {
        fdatValid = fctlCount == no;
        fctlCount++;
      }
      else if (block.type.equals("fdAT")) {
        in.seek(block.offset + 4);
        if (fdatValid) {
          byte[] b = new byte[block.length + 8];
          DataTools.unpackBytes(block.length - 4, b, 0, 4, isLittleEndian());
          b[4] = 'I';
          b[5] = 'D';
          b[6] = 'A';
          b[7] = 'T';
          in.read(b, 8, b.length - 12);
          int crc = (int) computeCRC(b, b.length - 4);
          DataTools.unpackBytes(crc, b, b.length - 4, 4, isLittleEndian());
          stream.write(b);
          b = null;
        }
      }
    }

    RandomAccessInputStream s =
      new RandomAccessInputStream(stream.toByteArray());
    DataInputStream dis = new DataInputStream(new BufferedInputStream(s, 4096));
    BufferedImage b = ImageIO.read(dis);
    dis.close();

    lastImage = null;
    openPlane(0, 0, 0, getSizeX(), getSizeY());

    // paste current image onto first image

    WritableRaster firstRaster = lastImage.getRaster();
    WritableRaster currentRaster = b.getRaster();

    firstRaster.setDataElements(coords[0], coords[1], currentRaster);
    lastImage =
      new BufferedImage(lastImage.getColorModel(), firstRaster, false, null);
    lastImageIndex = no;
    return lastImage;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      lut = null;
      frameCoordinates = null;
      blocks = null;
      lastImage = null;
      lastImageIndex = -1;
    }
  }

  // -- Internal FormatReader methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

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

    blocks = new Vector<PNGBlock>();
    frameCoordinates = new Vector<int[]>();

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
        core[0].imageCount = in.readInt();
        int loop = in.readInt();
        addGlobalMeta("Loop count", loop);
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

      if (in.getFilePointer() < in.length() - 4) {
        in.skipBytes(4); // skip the CRC
      }
    }

    if (core[0].imageCount == 0) core[0].imageCount = 1;
    core[0].sizeZ = 1;
    core[0].sizeT = getImageCount();

    core[0].dimensionOrder = "XYCTZ";
    core[0].interleaved = false;

    RandomAccessInputStream ras = new RandomAccessInputStream(currentId);
    DataInputStream dis = new DataInputStream(ras);
    BufferedImage img = ImageIO.read(dis);
    dis.close();

    core[0].sizeX = img.getWidth();
    core[0].sizeY = img.getHeight();
    core[0].rgb = img.getRaster().getNumBands() > 1;
    core[0].sizeC = img.getRaster().getNumBands();
    core[0].pixelType = AWTImageTools.getPixelType(img);
    core[0].indexed = img.getColorModel() instanceof IndexColorModel;
    core[0].falseColor = false;

    if (isIndexed()) {
      lut = new byte[3][256];
      IndexColorModel model = (IndexColorModel) img.getColorModel();
      model.getReds(lut[0]);
      model.getGreens(lut[1]);
      model.getBlues(lut[2]);
    }

    MetadataStore store = makeFilterMetadata();
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

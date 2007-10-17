//
// BMPReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.IOException;
import loci.formats.*;

/**
 * BMPReader is the file format reader for Microsoft Bitmap (BMP) files.
 * See http://astronomy.swin.edu.au/~pbourke/dataformats/bmp/ for a nice
 * description of the BMP file format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/BMPReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/BMPReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class BMPReader extends FormatReader {

  // -- Fields --

  /** Offset to the image data. */
  protected int offset;

  /** Number of bits per pixel. */
  protected int bpp;

  /** The palette for indexed color images. */
  protected byte[][] palette;

  /**
   * Compression type:
   * 0 = no compression,
   * 1 = 8 bit run length encoding,
   * 2 = 4 bit run length encoding,
   * 3 = RGB bitmap with mask.
   */
  protected int compression;

  /** Offset to image data. */
  private long global;

  // -- Constructor --

  /** Constructs a new BMP reader. */
  public BMPReader() { super("Windows Bitmap", "bmp"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return new String(block).startsWith("BM");
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return palette;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    if (compression != 0) {
      throw new FormatException("Compression type " + compression +
        " not supported");
    }

    in.seek(global);

    if ((palette != null && palette[0].length > 0) || core.sizeC[0] == 1) {
      for (int y=core.sizeY[0]-1; y>=0; y--) {
        in.read(buf, y*core.sizeX[0], core.sizeX[0]);
      }
    }
    else {
      for (int y=core.sizeY[0]-1; y>=0; y--) {
        in.read(buf, y*core.sizeX[0]*3, core.sizeX[0]*3);
      }
      for (int i=0; i<buf.length/3; i++) {
        byte tmp = buf[i*3 + 2];
        buf[i*3 + 2] = buf[i*3];
        buf[i*3] = tmp;
      }
    }
    return buf;
  }

  // -- Internel FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("BMPReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Reading bitmap header");

    in.order(true);

    // read the first header - 14 bytes

    addMeta("Magic identifier", in.readString(2));

    addMeta("File size (in bytes)", "" + in.readInt());
    in.skipBytes(4); // reserved

    // read the offset to the image data
    offset = in.readInt();

    // read the second header - 40 bytes

    in.skipBytes(4);

    // get the dimensions

    core.sizeX[0] = in.readInt();
    core.sizeY[0] = in.readInt();

    if (core.sizeX[0] < 1 || core.sizeY[0] < 1) {
      throw new FormatException("Invalid image dimensions: " +
        core.sizeX[0] + " x " + core.sizeY[0]);
    }
    addMeta("Image width", "" + core.sizeX[0]);
    addMeta("Image height", "" + core.sizeY[0]);

    addMeta("Color planes", "" + in.readShort());
    bpp = in.readShort();
    addMeta("Bits per pixel", "" + bpp);

    compression = in.readInt();
    String comp = "invalid";

    switch (compression) {
      case 0:
        comp = "None";
        break;
      case 1:
        comp = "8 bit run length encoding";
        break;
      case 2:
        comp = "4 bit run length encoding";
        break;
      case 3:
        comp = "RGB bitmap with mask";
        break;
    }

    addMeta("Compression type", comp);

    in.skipBytes(4);
    int pixelSizeX = in.readInt();
    int pixelSizeY = in.readInt();
    addMeta("X resolution", "" + pixelSizeX);
    addMeta("Y resolution", "" + pixelSizeY);
    int nColors = in.readInt();
    in.skipBytes(4);

    // read the palette, if it exists

    if (offset != in.getFilePointer() && nColors > 0) {
      palette = new byte[3][nColors];

      for (int i=0; i<nColors; i++) {
        for (int j=palette.length; j>0; j--) {
          palette[j][i] = in.readByte();
        }
        in.skipBytes(1);
      }
    }

    global = in.getFilePointer();
    addMeta("Indexed color", palette == null ? "false" : "true");

    status("Populating metadata");

    core.sizeC[0] = (palette == null && bpp == 8) ? 1 : 3;
    if (bpp > 8) bpp /= 3;
    while (bpp % 8 != 0) bpp++;

    switch (bpp) {
      case 8:
        core.pixelType[0] = FormatTools.UINT8;
        break;
      case 16:
        core.pixelType[0] = FormatTools.UINT16;
        break;
      case 32:
        core.pixelType[0] = FormatTools.UINT32;
        break;
    }

    if (core.sizeX[0] % 2 == 1) core.sizeX[0]++;
    core.rgb[0] = core.sizeC[0] > 1;
    core.littleEndian[0] = true;
    core.interleaved[0] = true;
    core.imageCount[0] = 1;
    core.sizeZ[0] = 1;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYCTZ";
    core.metadataComplete[0] = true;
    core.indexed[0] = palette != null;
    core.falseColor[0] = false;

    // Populate metadata store.

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    store.setImage(currentId, null, null, null);

    FormatTools.populatePixels(store, this);

    // resolution is stored as pixels per meter; we want to convert to
    // microns per pixel

    float correctedX = (1 / (float) pixelSizeX) * 1000000;
    float correctedY = (1 / (float) pixelSizeY) * 1000000;

    store.setDimensions(new Float(correctedX), new Float(correctedY), null,
      null, null, null);

    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
       null, null, null, null, null, null, null, null, null, null, null, null,
       null, null, null, null);
    }
  }

}

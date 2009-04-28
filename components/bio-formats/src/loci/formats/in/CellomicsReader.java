//
// CellomicsReader.java
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

import java.io.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * Reader for Cellomics C01 files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/CellomicsReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/CellomicsReader.java">SVN</a></dd></dl>
 */
public class CellomicsReader extends FormatReader {

  // -- Constructor --

  /** Constructs a new Cellomics reader. */
  public CellomicsReader() {
    super("Cellomics C01", "c01");
    blockCheckLen = 4;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    return stream.readInt() == 16;
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

    int planeSize = getSizeX() * getSizeY() * getRGBChannelCount() *
      FormatTools.getBytesPerPixel(getPixelType());
    in.seek(52 + no * planeSize);
    readPlane(in, x, y, w, h, buf);

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("CellomicsReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Decompressing file");

    in.seek(4);
    ZlibCodec codec = new ZlibCodec();
    byte[] file = codec.decompress(in, null);
    in.close();

    status("Reading header data");

    in = new RandomAccessStream(file);

    in.order(true);
    in.skipBytes(4);

    int x = in.readInt();
    int y = in.readInt();
    int nPlanes = in.readShort();
    int nBits = in.readShort();

    int compression = in.readInt();
    if (compression != 0) {
      throw new FormatException("Compressed pixel data is not yet supported.");
    }

    in.skipBytes(4);
    int pixelWidth = in.readInt();
    int pixelHeight = in.readInt();
    int colorUsed = in.readInt();
    int colorImportant = in.readInt();

    status("Populating metadata hashtable");

    addMeta("Image width", x);
    addMeta("Image height", y);
    addMeta("Number of planes", nPlanes);
    addMeta("Bits per pixel", nBits);
    addMeta("Compression", compression);
    addMeta("Pixels per meter (X)", pixelWidth);
    addMeta("Pixels per meter (Y)", pixelHeight);
    addMeta("Color used", colorUsed);
    addMeta("Color important", colorImportant);

    status("Populating core metadata");

    core[0].sizeX = x;
    core[0].sizeY = y;
    core[0].sizeZ = nPlanes;
    core[0].sizeT = 1;
    core[0].sizeC = 1;
    core[0].imageCount = getSizeZ();
    core[0].littleEndian = true;
    core[0].dimensionOrder = "XYCZT";

    switch (nBits) {
      case 8:
       core[0].pixelType = FormatTools.UINT8;
       break;
      case 16:
        core[0].pixelType = FormatTools.UINT16;
        break;
      default:
        throw new FormatException("Unsupported bits per pixel: " + nBits);
    }

    status("Populating metadata store");

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);

    // physical dimensions are stored as pixels per meter - we want them
    // in microns per pixel
    float width = pixelWidth == 0 ? 0 : 1000000f / pixelWidth;
    float height = pixelHeight == 0 ? 0 : 1000000f / pixelHeight;

    store.setDimensionsPhysicalSizeX(new Float(width), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(height), 0, 0);
  }

}

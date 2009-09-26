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

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
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

  // -- Constants --

  public static final int C01_MAGIC_BYTES = 16;

  // -- Constructor --

  /** Constructs a new Cellomics reader. */
  public CellomicsReader() {
    super("Cellomics C01", new String[] {"c01", "dib"});
    domains = new String[] {FormatTools.LM_DOMAIN, FormatTools.HCS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readInt() == C01_MAGIC_BYTES;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int planeSize = FormatTools.getPlaneSize(this);
    in.seek(52 + no * planeSize);
    readPlane(in, x, y, w, h, buf);

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("CellomicsReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    if (checkSuffix(id, "c01")) {
      status("Decompressing file");
      in.seek(4);
      ZlibCodec codec = new ZlibCodec();
      byte[] file = codec.decompress(in, null);
      in.close();

      in = new RandomAccessInputStream(file);
    }
    status("Reading header data");

    in.order(true);
    in.skipBytes(4);

    int x = in.readInt();
    int y = in.readInt();
    int nPlanes = in.readShort();
    int nBits = in.readShort();

    int compression = in.readInt();

    if (x * y * nPlanes * (nBits / 8) + 52 > in.length()) {
      throw new FormatException("Compressed pixel data is not yet supported.");
    }

    in.skipBytes(4);
    int pixelWidth = in.readInt();
    int pixelHeight = in.readInt();
    int colorUsed = in.readInt();
    int colorImportant = in.readInt();

    status("Populating metadata hashtable");

    addGlobalMeta("Image width", x);
    addGlobalMeta("Image height", y);
    addGlobalMeta("Number of planes", nPlanes);
    addGlobalMeta("Bits per pixel", nBits);
    addGlobalMeta("Compression", compression);
    addGlobalMeta("Pixels per meter (X)", pixelWidth);
    addGlobalMeta("Pixels per meter (Y)", pixelHeight);
    addGlobalMeta("Color used", colorUsed);
    addGlobalMeta("Color important", colorImportant);

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
    MetadataTools.setDefaultCreationDate(store, id, 0);

    // physical dimensions are stored as pixels per meter - we want them
    // in microns per pixel
    float width = pixelWidth == 0 ? 0 : 1000000f / pixelWidth;
    float height = pixelHeight == 0 ? 0 : 1000000f / pixelHeight;

    store.setDimensionsPhysicalSizeX(new Float(width), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(height), 0, 0);
  }

}

//
// BioRadGelReader.java
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

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * BioRadGelReader is the file format reader for Bio-Rad gel files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/BioRadGelReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/BioRadGelReader.java">SVN</a></dd></dl>
 */
public class BioRadGelReader extends FormatReader {

  // -- Constants --

  private static final long PIXEL_OFFSET = 59654;
  private static final long BIG_ENDIAN_OFFSET = 348;
  private static final long LITTLE_ENDIAN_OFFSET = 268;

  // -- Fields --

  private long offset;

  // -- Constructor --

  /** Constructs a new Bio-Rad gel reader. */
  public BioRadGelReader() {
    super("Bio-Rad GEL", "1sc");
    domains = new String[] {FormatTools.GEL_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readShort() & 0xffff) == 0xafaf;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int planeSize = FormatTools.getPlaneSize(this);

    if (offset > PIXEL_OFFSET) {
      in.seek(offset + 1285);
    }
    else if (PIXEL_OFFSET + planeSize <= in.length()) {
      in.seek(PIXEL_OFFSET);
    }
    else in.seek(in.length() - planeSize);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bpp * getSizeC();

    in.skipBytes(pixel * getSizeX() * y);

    for (int row=h-1; row>=0; row--) {
      in.skipBytes(x * pixel);
      in.read(buf, row * w * pixel, w * pixel);
      in.skipBytes(pixel * (getSizeX() - w - x));
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    String check = in.readString(48);
    if (check.indexOf("Intel Format") != -1) {
      in.order(true);
    }

    long headerOffset =
      in.isLittleEndian() ? LITTLE_ENDIAN_OFFSET : BIG_ENDIAN_OFFSET;

    in.seek(headerOffset);
    int skip = in.readInt() - 28;
    headerOffset = BIG_ENDIAN_OFFSET;

    double physicalWidth = 0d, physicalHeight = 0d;
    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      in.seek(headerOffset + skip - 8187);
      String scannerName = in.readCString();
      in.skipBytes(8);
      in.readCString();
      in.skipBytes(8);
      String imageArea = in.readCString();

      scannerName = scannerName.substring(scannerName.indexOf(":") + 1).trim();
      addGlobalMeta("Scanner name", scannerName);

      imageArea = imageArea.substring(imageArea.indexOf(":") + 1).trim();
      int xIndex = imageArea.indexOf("x");
      if (xIndex > 0) {
        String width = imageArea.substring(1, imageArea.indexOf(" "));
        String height =
          imageArea.substring(xIndex + 1, imageArea.indexOf(" ", xIndex + 2));
        physicalWidth = Double.parseDouble(width.trim()) * 1000;
        physicalHeight = Double.parseDouble(height.trim()) * 1000;
      }
    }

    in.seek(headerOffset + skip - 273);
    String date = in.readCString();
    date = DateTools.formatDate(date, "dd-MMM-yyyy HH:mm");

    in.seek(headerOffset + skip);

    core[0].sizeX = in.readShort() & 0xffff;
    core[0].sizeY = in.readShort() & 0xffff;
    if (getSizeX() * getSizeY() > in.length()) {
      in.order(true);
      in.seek(in.getFilePointer() - 4);
      core[0].sizeX = in.readShort();
      core[0].sizeY = in.readShort();
    }
    in.skipBytes(2);

    int bpp = in.readShort();
    core[0].pixelType = FormatTools.pixelTypeFromBytes(bpp, false, false);

    offset = in.getFilePointer();

    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].indexed = false;
    core[0].littleEndian = in.isLittleEndian();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    store.setImageAcquiredDate(date, 0);
    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setPixelsPhysicalSizeX(physicalWidth / getSizeX(), 0);
      store.setPixelsPhysicalSizeY(physicalHeight / getSizeY(), 0);
    }
  }

}

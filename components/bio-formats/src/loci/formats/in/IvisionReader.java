//
// IvisionReader.java
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

import java.io.IOException;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * IvisionReader is the file format reader for IVision (.IPM) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/IvisionReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/IvisionReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class IvisionReader extends FormatReader {

  // -- Fields --

  private boolean color16;
  private boolean squareRoot;
  private byte[] lut;
  private long imageOffset;

  // -- Constructor --

  /** Constructs a new Ivision reader. */
  public IvisionReader() {
    super("IVision", "ipm");
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
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

    int planeSize = getSizeX() * getSizeY() * getSizeC();
    if (color16) planeSize = 2 * (planeSize / 3);
    else if (squareRoot) planeSize *= 2;
    else planeSize *= FormatTools.getBytesPerPixel(getPixelType());

    in.seek(imageOffset + planeSize * no);

    if (color16) {
      // TODO
      throw new FormatException("16-bit color iVision files are not supported");
    }
    else if (squareRoot) {
      // TODO
      throw new FormatException("Square-root iVision files are not supported");
    }
    else readPlane(in, x, y, w, h, buf);

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("IvisionReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Populating metadata");

    String version = in.readString(4);
    int fileFormat = in.read();
    int dataType = in.read();

    core[0].sizeC = 1;
    switch (dataType) {
      case 0:
        core[0].pixelType = FormatTools.UINT8;
        break;
      case 1:
        core[0].pixelType = FormatTools.INT16;
        break;
      case 2:
        core[0].pixelType = FormatTools.INT32;
        break;
      case 3:
        core[0].pixelType = FormatTools.FLOAT;
        break;
      case 4:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC = 3;
        color16 = true;
        break;
      case 5:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC = 4;
        break;
      case 6:
        core[0].pixelType = FormatTools.UINT16;
        break;
      case 7:
        core[0].pixelType = FormatTools.FLOAT;
        squareRoot = true;
        break;
      case 8:
        core[0].pixelType = FormatTools.UINT16;
        core[0].sizeC = 3;
        break;
    }

    core[0].sizeX = in.readInt();
    core[0].sizeY = in.readInt();
    in.skipBytes(6);

    core[0].sizeZ = in.readShort();
    in.skipBytes(50);

    core[0].sizeT = 1;

    if (getSizeX() > 1 && getSizeY() > 1) {
      lut = new byte[2048];
      in.read(lut);
    }

    imageOffset = in.getFilePointer();

    core[0].rgb = getSizeC() > 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].littleEndian = false;
    core[0].interleaved = true;
    core[0].indexed = false;
    core[0].imageCount = getSizeZ() * getSizeT();

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);
  }

}

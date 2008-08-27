//
// LIMReader.java
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
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * LIMReader is the file format reader for Laboratory Imaging/Nikon LIM files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/LIMReader.java">Trac</a>
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/LIMReader.java">SVN</a></dd></dl>
 */
public class LIMReader extends FormatReader {

  // -- Fields --

  private boolean isCompressed;

  // -- Constructor --

  /** Constructs a new LIM reader. */
  public LIMReader() { super("Laboratory Imaging", "lim"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return false;
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

    in.seek(0x94b);
    DataTools.readPlane(in, x, y, w, h, this, buf);

    // swap red and blue channels
    if (isRGB()) {
      for (int i=0; i<buf.length/3; i++) {
        byte tmp = buf[i*3];
        buf[i*3] = buf[i*3 + 2];
        buf[i*3 + 2] = tmp;
      }
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    isCompressed = false;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LIMReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    core[0].littleEndian = true;
    in.order(isLittleEndian());

    core[0].sizeX = in.readShort() & 0x7fff;
    core[0].sizeY = in.readShort();
    int bits = in.readShort();

    while (bits % 8 != 0) bits++;
    switch (bits) {
      case 8:
        core[0].pixelType = FormatTools.UINT8;
        break;
      case 16:
        core[0].pixelType = FormatTools.UINT16;
        break;
      case 24:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC = 3;
        break;
      case 32:
        core[0].pixelType = FormatTools.UINT32;
        break;
      case 48:
        core[0].pixelType = FormatTools.UINT16;
        core[0].sizeC = 3;
        break;
      default:
        throw new FormatException("Unsupported bits per pixel : " + bits);
    }

    isCompressed = in.readShort() != 0;
    if (isCompressed) {
      throw new FormatException("Compressed LIM files not supported.");
    }

    core[0].imageCount = 1;
    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    if (getSizeC() == 0) core[0].sizeC = 1;
    core[0].rgb = getSizeC() > 1;
    core[0].inputOrder = "XYZCT";
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].interleaved = true;
    core[0].metadataComplete = true;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    MetadataTools.populatePixels(store, this);
  }

}

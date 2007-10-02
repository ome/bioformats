//
// LIMReader.java
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

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    in.seek(0x94b);
    in.read(buf);

    // swap red and blue channels
    if (core.rgb[0]) {
      for (int i=0; i<buf.length/3; i++) {
        byte tmp = buf[i*3];
        buf[i*3] = buf[i*3 + 2];
        buf[i*3 + 2] = tmp;
      }
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LIMReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    core.littleEndian[0] = true;
    in.order(core.littleEndian[0]);

    core.sizeX[0] = in.readShort() & 0x7fff;
    core.sizeY[0] = in.readShort();
    int bits = in.readShort();

    while (bits % 8 != 0) bits++;
    switch (bits) {
      case 8:
        core.pixelType[0] = FormatTools.UINT8;
        break;
      case 16:
        core.pixelType[0] = FormatTools.UINT16;
        break;
      case 24:
        core.pixelType[0] = FormatTools.UINT8;
        core.sizeC[0] = 3;
        break;
      case 32:
        core.pixelType[0] = FormatTools.UINT32;
        break;
      case 48:
        core.pixelType[0] = FormatTools.UINT16;
        core.sizeC[0] = 3;
        break;
      default:
        throw new FormatException("Unsupported bits per pixel : " + bits);
    }

    isCompressed = in.readShort() != 0;
    if (isCompressed) {
      throw new FormatException("Compressed LIM files not supported.");
    }

    core.imageCount[0] = 1;
    core.sizeZ[0] = 1;
    core.sizeT[0] = 1;
    if (core.sizeC[0] == 0) core.sizeC[0] = 1;
    core.rgb[0] = core.sizeC[0] > 1;
    core.currentOrder[0] = "XYZCT";
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.interleaved[0] = true;
    core.metadataComplete[0] = true;

    MetadataStore store = getMetadataStore();
    FormatTools.populatePixels(store, this);
    store.setImage(currentId, null, null, null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null);
    }

  }

}

//
// LIMReader.java
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
import loci.formats.UnsupportedCompressionException;
import loci.formats.meta.MetadataStore;

/**
 * LIMReader is the file format reader for Laboratory Imaging/Nikon LIM files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/LIMReader.java">Trac</a>
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/LIMReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class LIMReader extends FormatReader {

  // -- Constants --

  private static final int PIXELS_OFFSET = 0x94b;

  // -- Fields --

  private boolean isCompressed;

  // -- Constructor --

  /** Constructs a new LIM reader. */
  public LIMReader() {
    super("Laboratory Imaging", "lim");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(PIXELS_OFFSET);
    readPlane(in, x, y, w, h, buf);

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

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) isCompressed = false;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    core[0].littleEndian = true;
    in.order(isLittleEndian());

    core[0].sizeX = in.readShort() & 0x7fff;
    core[0].sizeY = in.readShort();
    int bits = in.readShort();

    while (bits % 8 != 0) bits++;
    if ((bits % 3) == 0) {
      core[0].sizeC = 3;
      bits /= 3;
    }
    core[0].pixelType = FormatTools.pixelTypeFromBytes(bits / 8, false, false);

    isCompressed = in.readShort() != 0;
    addGlobalMeta("Is compressed", isCompressed);
    if (isCompressed) {
      throw new UnsupportedCompressionException(
        "Compressed LIM files not supported.");
    }

    core[0].imageCount = 1;
    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    if (getSizeC() == 0) core[0].sizeC = 1;
    core[0].rgb = getSizeC() > 1;
    core[0].dimensionOrder = "XYZCT";
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].interleaved = true;
    core[0].metadataComplete = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}

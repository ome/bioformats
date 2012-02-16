//
// JPEGReader.java
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
import loci.formats.DelegateReader;
import loci.formats.FormatException;
import loci.formats.FormatTools;

/**
 * JPEGReader is the file format reader for JPEG images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/JPEGReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/JPEGReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class JPEGReader extends DelegateReader {

  // -- Constants --

  private static final int MAX_SIZE = 8192;

  // -- Constructor --

  /** Constructs a new JPEGReader. */
  public JPEGReader() {
    super("JPEG", new String[] {"jpg", "jpeg", "jpe"});
    nativeReader = new DefaultJPEGReader();
    legacyReader = new TileJPEGReader();
    nativeReaderInitialized = false;
    legacyReaderInitialized = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
    if (getSizeX() > MAX_SIZE && getSizeY() > MAX_SIZE &&
      !legacyReaderInitialized)
    {
      close();
      useLegacy = true;
      super.setId(id);
    }
  }

  // -- Helper reader --

  class DefaultJPEGReader extends ImageIOReader {
    public DefaultJPEGReader() {
      super("JPEG", new String[] {"jpg", "jpeg", "jpe"});
      suffixNecessary = false;
      suffixSufficient = false;
    }

    /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
    public boolean isThisType(String name, boolean open) {
      if (open) {
        return super.isThisType(name, open);
      }

      return checkSuffix(name, getSuffixes());
    }

    /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
    public boolean isThisType(RandomAccessInputStream stream) throws IOException
    {
      final int blockLen = 4;
      if (!FormatTools.validStream(stream, blockLen, false)) return false;

      byte[] signature = new byte[blockLen];
      stream.read(signature);

      if (signature[0] != (byte) 0xff || signature[1] != (byte) 0xd8 ||
        signature[2] != (byte) 0xff || ((int) signature[3] & 0xf0) == 0)
      {
        return false;
      }

      return true;
    }
  }

}

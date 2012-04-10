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

import java.awt.color.CMMException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.Location;
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
    try {
      super.setId(id);
    }
    catch (CMMException e) {
      // strip out all but the first application marker
      // ImageIO isn't too keen on supporting multiple application markers
      // in the same stream, as evidenced by:
      //
      // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6488904

      in = new RandomAccessInputStream(id);
      ByteArrayOutputStream v = new ByteArrayOutputStream();

      byte[] tag = new byte[2];
      in.read(tag);
      v.write(tag);

      in.read(tag);
      int tagValue = DataTools.bytesToShort(tag, false) & 0xffff;
      boolean appNoteFound = false;
      while (tagValue != 0xffdb) {
        if (!appNoteFound || (tagValue < 0xffe0 && tagValue >= 0xfff0)) {
          v.write(tag);

          in.read(tag);
          int len = DataTools.bytesToShort(tag, false) & 0xffff;
          byte[] tagContents = new byte[len - 2];
          in.read(tagContents);
          v.write(tag);
          v.write(tagContents);
        }
        else {
          in.read(tag);
          int len = DataTools.bytesToShort(tag, false) & 0xffff;
          in.skipBytes(len - 2);
        }

        if (tagValue >= 0xffe0 && tagValue < 0xfff0 && !appNoteFound) {
          appNoteFound = true;
        }
        in.read(tag);
        tagValue = DataTools.bytesToShort(tag, false) & 0xffff;
      }
      v.write(tag);
      byte[] remainder = new byte[(int) (in.length() - in.getFilePointer())];
      in.read(remainder);
      v.write(remainder);

      ByteArrayHandle bytes = new ByteArrayHandle(v.toByteArray());
 
      Location.mapFile(currentId + ".fixed", bytes);
      super.setId(currentId + ".fixed");
    }
    if (getSizeX() > MAX_SIZE && getSizeY() > MAX_SIZE &&
      !legacyReaderInitialized)
    {
      close();
      useLegacy = true;
      super.setId(id);
    }
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    Location.mapId(currentId, null);
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

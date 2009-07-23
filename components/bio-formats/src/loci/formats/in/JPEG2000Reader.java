//
// JPEG2000Reader.java
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
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * JPEG2000Reader is the file format reader for JPEG-2000 images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/JPEG2000Reader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/JPEG2000Reader.java">SVN</a></dd></dl>
 */
public class JPEG2000Reader extends FormatReader {

  // -- Constructor --

  /** Constructs a new JPEG2000Reader. */
  public JPEG2000Reader() {
    super("JPEG-2000", new String[] {"jp2", "j2k"});
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    int b1 = stream.read() & 0xff;
    int b2 = stream.read() & 0xff;
    boolean validStart = b1 == 0xff && b2 == 0x4f;
    if (!validStart) {
      stream.skipBytes(2);
      validStart = stream.readInt() == 0x6a502020;
    }
    stream.seek(stream.length() - 2);
    b1 = stream.read() & 0xff;
    b2 = stream.read() & 0xff;
    boolean validEnd = b1 == 0xff && b2 == 0xd9;
    return validStart && validEnd;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    CodecOptions options = new CodecOptions();
    options.interleaved = isInterleaved();
    options.littleEndian = isLittleEndian();

    in.seek(0);
    return new JPEG2000Codec().decompress(in, options);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("JPEG2000Reader.initFile(" + id + ")");
    super.initFile(id);

    in = new RandomAccessInputStream(id);

    long pos = in.getFilePointer();
    int length = 0, box = 0;
    boolean lastBoxFound = false;

    while (pos < in.length() && !lastBoxFound) {
      pos = in.getFilePointer();
      length = in.readInt();
      long nextPos = pos + length;
      if (nextPos < 0 || nextPos >= in.length() || length == 0) {
        lastBoxFound = true;
      }
      box = in.readInt();
      pos = in.getFilePointer();
      length -= 8;

      if (box == 0x6a703268) {
        in.skipBytes(4);
        String s = in.readString(4);
        if (s.equals("ihdr")) {
          core[0].sizeY = in.readInt();
          core[0].sizeX = in.readInt();
          core[0].sizeC = in.readShort();
          int type = in.readInt();
          if (type == 0xf070100 || type == 0xf070000) {
            core[0].pixelType = FormatTools.UINT16;
          }
          else core[0].pixelType = FormatTools.UINT8;
          lastBoxFound = true;
        }
      }
      else if ((length + 8) == 0xff4fff51) {
        core[0].sizeX = in.readInt();
        core[0].sizeY = in.readInt();
        in.skipBytes(24);
        core[0].sizeC = in.readShort();
        int type = in.readInt();
        if (type == 0xf070100 || type == 0xf070000) {
          core[0].pixelType = FormatTools.UINT16;
        }
        else core[0].pixelType = FormatTools.UINT8;

        lastBoxFound = true;
      }
      if (!lastBoxFound) in.seek(pos + length);
    }

    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].rgb = getSizeC() > 1;
    core[0].interleaved = true;
    core[0].littleEndian = false;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);
    MetadataTools.setDefaultCreationDate(store, currentId, 0);
  }

}

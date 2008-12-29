//
// PackbitsCodec.java
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

package loci.formats.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import loci.common.RandomAccessStream;
import loci.formats.FormatException;

/**
 * This class implements packbits decompression. Compression is not yet
 * implemented.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/codec/PackbitsCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/codec/PackbitsCodec.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class PackbitsCodec extends BaseCodec {

  /* @see Codec#compress(byte[], CodecOptions) */
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    // TODO: Add compression support.
    throw new FormatException("Packbits Compression not currently supported");
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#maxBytes maxBytes}
   *
   * @see Codec#decompress(RandomAccessStream, CodecOptions)
   */
  public byte[] decompress(RandomAccessStream in, CodecOptions options)
    throws FormatException, IOException
  {
    // Adapted from the TIFF 6.0 specification, page 42.
    ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
    while (output.size() < options.maxBytes &&
      in.getFilePointer() < in.length())
    {
      byte n = in.readByte();
      if (n >= 0) { // 0 <= n <= 127
        byte[] b = new byte[n + 1];
        in.read(b);
        output.write(b);
        b = null;
      }
      else if (n != -128) { // -127 <= n <= -1
        int len = -n + 1;
        byte inp = in.readByte();
        for (int i=0; i<len; i++) output.write(inp);
      }
    }
    return output.toByteArray();
  }
}

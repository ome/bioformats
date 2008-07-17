//
// ZlibCodec.java
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

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import loci.formats.FormatException;

/**
 * This class implements ZLIB decompression. Compression is not yet
 * implemented.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/ZlibCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/ZlibCodec.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ZlibCodec extends BaseCodec implements Codec {

  /**
   * Compresses a block of ZLIB data. Currently not supported.
   *
   * @param data the data to be compressed
   * @param x length of the x dimension of the image data, if appropriate
   * @param y length of the y dimension of the image data, if appropriate
   * @param dims the dimensions of the image data, if appropriate
   * @param options options to be used during compression, if appropriate
   * @return The compressed data
   * @throws FormatException If input is not an Adobe data block.
   */
  public byte[] compress(byte[] data, int x, int y,
      int[] dims, Object options) throws FormatException
  {
    // TODO: Add compression support.
    throw new FormatException("ZLIB Compression not currently supported");
  }

  /** Decodes an ZLIB compressed image strip.
   *
   * @param input the data to be decompressed
   * @return The decompressed data
   * @throws FormatException if data is not valid compressed data for this
   *                         decompressor
   */
  public byte[] decompress(byte[] input, Object options)
    throws FormatException
  {
    try {
      Inflater inf = new Inflater(false);
      inf.setInput(input);
      InflaterInputStream i =
        new InflaterInputStream(new PipedInputStream(), inf);
      ByteVector bytes = new ByteVector();
      byte[] buf = new byte[8192];
      while (true) {
        int r = i.read(buf, 0, buf.length);
        if (r == -1) break; // eof
        bytes.add(buf, 0, r);
      }
      return bytes.toByteArray();
    }
    catch (IOException e) {
      throw new FormatException("Error uncompressing " +
        "ZLIB compressed image strip.", e);
    }
  }

}

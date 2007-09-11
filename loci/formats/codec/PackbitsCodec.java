//
// PackbitsCodec.java
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

package loci.formats.codec;

import loci.formats.FormatException;

/**
 * This class implements packbits decompression. Compression is not yet
 * implemented.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/PackbitsCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/PackbitsCodec.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class PackbitsCodec extends BaseCodec implements Codec {

  /**
   * Compresses a block of Packbits data. Currently not supported.
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
    throw new FormatException("Packbits Compression not currently supported");
  }

  /**
   * Decodes a PackBits (Macintosh RLE) compressed image.
   * Adapted from the TIFF 6.0 specification, page 42.
   *
   * @param input input data to be decompressed
   * @return The decompressed data
   * @throws FormatException if data is not valid compressed data for this
   *                         decompressor
   */
  public byte[] decompress(byte[] input, Object options) throws FormatException
  {
    ByteVector output = new ByteVector(input.length);
    int pt = 0;
    while (pt < input.length) {
      byte n = input[pt++];
      if (n >= 0) { // 0 <= n <= 127
        int len = pt + n + 1 > input.length ? (input.length - pt) : (n + 1);
        output.add(input, pt, len);
        pt += len;
      }
      else if (n != -128) { // -127 <= n <= -1
        if (pt >= input.length) break;
        int len = -n + 1;
        byte inp = input[pt++];
        for (int i=0; i<len; i++) output.add(inp);
      }
    }
    return output.toByteArray();
  }
}

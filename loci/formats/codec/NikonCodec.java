//
// NikonCodec.java
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
 * This class implements Nikon decompression. Compression is not yet
 * implemented.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/NikonCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/NikonCodec.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class NikonCodec extends BaseCodec implements Codec {

  /** Huffman tree for the Nikon decoder. */
  private static final int[] NIKON_TREE = {
    0, 1, 5, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0,
    0, 5, 4, 3, 6, 2, 7, 1, 0, 8, 9, 11, 10, 12
  };

  /**
   * Compresses a block of Nikon data. Currently not supported.
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
    throw new FormatException("Nikon Compression not currently supported");
  }

  /**
   * Decodes an image strip using Nikon's compression algorithm (a variant on
   * Huffman coding).
   *
   * TODO : this is broken
   *
   * @param input input data to be decompressed
   * @return The decompressed data
   * @throws FormatException if data is not valid compressed data for this
   *                         decompressor
   */
  public byte[] decompress(byte[] input, Object options) throws FormatException
  {
    BitWriter out = new BitWriter(input.length);
    BitBuffer bb = new BitBuffer(input);
    boolean eof = false;
    while (!eof) {
      boolean codeFound = false;
      int code = 0;
      int bitsRead = 0;
      while (!codeFound) {
        int bit = bb.getBits(1);
        if (bit == -1) {
          eof = true;
          break;
        }
        bitsRead++;
        code >>= 1;
        code += bit;
        for (int i=16; i<NIKON_TREE.length; i++) {
          if (code == NIKON_TREE[i]) {
            int ndx = i;
            int count = 0;
            while (ndx > 16) {
              ndx -= NIKON_TREE[count];
              count++;
            }
            if (ndx < 16) count--;
            if (bitsRead == count + 1) {
              codeFound = true;
              i = NIKON_TREE.length;
              break;
            }
          }
        }
      }
      while (code > 0) {
        out.write(bb.getBits(1), 1);
        code--;
      }
    }
    byte[] b = out.toByteArray();
    return b;
  }
}

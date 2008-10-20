//
// NikonCodec.java
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
import loci.common.RandomAccessStream;
import loci.formats.FormatException;

/**
 * This class implements Nikon decompression. Compression is not yet
 * implemented.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/codec/NikonCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/codec/NikonCodec.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class NikonCodec extends BaseCodec implements Codec {

  /** Huffman tree for the Nikon decoder. */
  private static final int[] NIKON_TREE = {
    0, 1, 5, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 0, 0,
    0, 5, 4, 3, 6, 2, 7, 1, 0, 8, 9, 11, 10, 12
  };

  /* @see Codec#compress(byte[], int, int, int[], Object) */
  public byte[] compress(byte[] data, int x, int y,
    int[] dims, Object options) throws FormatException
  {
    // TODO: Add compression support.
    throw new FormatException("Nikon Compression not currently supported");
  }

  /* @see Codec#decompress(RandomAccessStream, Object) */
  public byte[] decompress(RandomAccessStream in, Object options)
    throws FormatException, IOException
  {
    // TODO
    throw new FormatException("Nikon decompression not currently supported");
  }
}

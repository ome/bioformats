//
// JPEGCodec.java
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

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import loci.formats.*;

/**
 * This class implements JPEG decompression. Compression is not yet
 * implemented.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/JPEGCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/JPEGCodec.java">SVN</a></dd></dl>
 */
public class JPEGCodec extends BaseCodec implements Codec {

  /**
   * Compresses a block of JPEG data. Currently not supported.
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
    throw new FormatException("JPEG Compression not currently supported");
  }

  /**
   * Decodes an image strip using JPEG compression algorithm.
   *
   * @param input input data to be decompressed
   * @return The decompressed data
   * @throws FormatException if data is not valid compressed data for this
   *                         decompressor
   */
  public byte[] decompress(byte[] input, Object options) throws FormatException
  {
    BufferedImage b;
    try {
      RandomAccessStream s = new RandomAccessStream(input);
      while (s.read() != (byte) 0xff || s.read() != (byte) 0xd8);
      int offset = (int) s.getFilePointer() - 2;
      b = ImageIO.read(new BufferedInputStream(new ByteArrayInputStream(input,
        offset, input.length - offset)));
    }
    catch (IOException exc) {
      LogTools.println(
        "An I/O error occurred decompressing image. Stack dump follows:");
      LogTools.trace(exc);
      return null;
    }

    byte[][] buf = ImageTools.getBytes(b);
    byte[] rtn = new byte[buf.length * buf[0].length];
    if (buf.length == 1) rtn = buf[0];
    else {
      for (int i=0; i<buf.length; i++) {
        System.arraycopy(buf[i], 0, rtn, i*buf[0].length, buf[i].length);
      }
    }
    return rtn;
  }
}

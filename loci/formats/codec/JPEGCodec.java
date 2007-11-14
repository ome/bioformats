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
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.spi.*;
import javax.imageio.stream.ImageInputStream;
import loci.formats.*;

import com.sun.media.imageioimpl.plugins.clib.CLibImageReader;

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
   * @param in The stream from which to read compressed data.
   * @return The decompressed data
   * @throws FormatException if data is not valid compressed data for this
   *                         decompressor
   */
  public byte[] decompress(RandomAccessStream in, Object options)
    throws FormatException
  {
    BufferedImage b;
    long fp = in.getFilePointer();
    try {
      while (in.read() != (byte) 0xff || in.read() != (byte) 0xd8);
      in.seek(in.getFilePointer() - 2);
      b = ImageIO.read(new BufferedInputStream(in));
    }
    catch (IOException exc) {
      //LogTools.println(
      //  "An I/O error occurred decompressing image. Stack dump follows:");
      //LogTools.trace(exc);
      //return null;

      System.setProperty("com.sun.media.imageio.disableCodecLib", "false");
      try {
        Class jpegSpi = Class.forName(
          "com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageReaderSpi");
        IIORegistry registry = IIORegistry.getDefaultInstance();
        Object jpeg = registry.getServiceProviderByClass(jpegSpi);

        javax.imageio.ImageReader r =
          ((ImageReaderSpi) jpeg).createReaderInstance();

        in.seek(fp);
        while (in.read() != (byte) 0xff || in.read() != (byte) 0xd8);
        in.seek(in.getFilePointer() - 2);

        ImageInputStream ii = ImageIO.createImageInputStream(in);
        r.setInput(ii);
        b = r.read(0);
      }
      catch (IOException e) {
        LogTools.println("An I/O error occurred while decompressing image. " +
          "Stack dump follows:");
        LogTools.trace(e);
        return null;
      }
      catch (ClassNotFoundException e) {
        LogTools.println("An I/O error occurred while decompressing image. " +
          "Stack dump follows:");
        LogTools.trace(e);
        return null;
      }
    }
    byte[][] buf = ImageTools.getPixelBytes(b,
      ((Boolean) options).booleanValue());
    /* debug */ System.out.println("bytes per channel : " + buf[0].length);
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

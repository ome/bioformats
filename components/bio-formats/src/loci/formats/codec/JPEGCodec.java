//
// JPEGCodec.java
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

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.spi.*;
import javax.imageio.stream.ImageInputStream;
import loci.common.*;
import loci.formats.*;

/**
 * This class implements JPEG compression and decompression.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/codec/JPEGCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/codec/JPEGCodec.java">SVN</a></dd></dl>
 */
public class JPEGCodec extends BaseCodec {

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#width width}
   *  {@link CodecOptions#height height}
   *  {@link CodecOptions#channels channels}
   *  {@link CodecOptions#bitsPerSample bitsPerSample}
   *  {@link CodecOptions#interleaved interleaved}
   *  {@link CodecOptions#littleEndian littleEndian}
   *
   * @see Codec#compress(byte[], CodecOptions)
   */
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    if (options == null) options = CodecOptions.getDefaultOptions();

    if (options.bitsPerSample > 8) {
      throw new FormatException("> 8 bit data cannot be compressed with JPEG.");
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BufferedImage img = AWTImageTools.makeImage(data, options.width,
      options.height, options.channels, options.interleaved,
      options.bitsPerSample / 8, options.littleEndian);

    try {
      ImageIO.write(img, "jpeg", out);
    }
    catch (IOException e) {
      throw new FormatException("Could not write JPEG data", e);
    }
    return out.toByteArray();
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#interleaved interleaved}
   *  {@link CodecOptions#littleEndian littleEndian}
   *
   * @see Codec#decompress(RandomAccessStream, CodecOptions)
   */
  public byte[] decompress(RandomAccessStream in, CodecOptions options)
    throws FormatException, IOException
  {
    BufferedImage b;
    long fp = in.getFilePointer();
    try {
      try {
        while (in.read() != (byte) 0xff || in.read() != (byte) 0xd8);
        in.seek(in.getFilePointer() - 2);
      }
      catch (EOFException e) {
        in.seek(fp);
      }

      Iterator it = ImageIO.getImageReadersBySuffix("jpg");
      javax.imageio.ImageReader r = null;
      String cname = "com.sun.imageio.plugins.jpeg.JPEGImageReader";
      while (it.hasNext()) {
        Object tmp = it.next();
        if (tmp.getClass().getName().equals(cname)) {
          r = (javax.imageio.ImageReader) tmp;
        }
      }

      if (r == null) throw new IOException("");

      ImageInputStream ii =
        ImageIO.createImageInputStream(new BufferedInputStream(in));
      r.setInput(ii);
      b = r.read(0);
      ii.close();
      r.dispose();
    }
    catch (IOException exc) {
      try {
        // NB: the following comment facilitates dependency detection:
        // import com.sun.media.imageioimpl.plugins.jpeg

        Class jpegSpi = Class.forName(
          "com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageReaderSpi");
        IIORegistry registry = IIORegistry.getDefaultInstance();
        Object jpeg = registry.getServiceProviderByClass(jpegSpi);

        if (jpeg == null) {
          throw new FormatException("Cannot locate JPEG decoder");
        }
        javax.imageio.ImageReader r =
          ((ImageReaderSpi) jpeg).createReaderInstance();

        in.seek(fp);
        while (in.read() != (byte) 0xff || in.read() != (byte) 0xd8);
        in.seek(in.getFilePointer() - 2);

        ImageInputStream ii =
          ImageIO.createImageInputStream(new BufferedInputStream(in));
        r.setInput(ii);
        b = r.read(0);
        ii.close();
        r.dispose();
      }
      catch (ClassNotFoundException e) {
        throw new FormatException(
          "An I/O error occurred while decompressing the image", e);
      }
    }

    if (options == null) options = CodecOptions.getDefaultOptions();

    byte[][] buf = AWTImageTools.getPixelBytes(b, options.littleEndian);
    byte[] rtn = new byte[buf.length * buf[0].length];
    if (buf.length == 1) rtn = buf[0];
    else {
      if (options.interleaved) {
        int next = 0;
        for (int i=0; i<buf[0].length; i++) {
          for (int j=0; j<buf.length; j++) {
            rtn[next++] = buf[j][i];
          }
        }
      }
      else {
        for (int i=0; i<buf.length; i++) {
          System.arraycopy(buf[i], 0, rtn, i*buf[0].length, buf[i].length);
        }
      }
    }
    return rtn;
  }
}

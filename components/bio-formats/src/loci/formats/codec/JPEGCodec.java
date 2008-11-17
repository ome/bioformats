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
public class JPEGCodec extends BaseCodec implements Codec {

  /* @see Codec#compress(byte[], int, int, int[], Object) */
  public byte[] compress(byte[] data, int x, int y,
      int[] dims, Object options) throws FormatException
  {
	  boolean littleEndian = false, interleaved = false;
	    if (options instanceof Boolean) {
	      littleEndian = ((Boolean) options).booleanValue();
	    }
	    else {
	      Object[] o = (Object[]) options;
	      littleEndian = ((Boolean) o[0]).booleanValue();
	      interleaved = ((Boolean) o[1]).booleanValue();
	    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BufferedImage img = AWTImageTools.makeImage(data, x, y, dims[0],
      interleaved, dims[1], littleEndian);

    try {
      ImageIO.write(img, "jpeg", out);
    }
    catch (IOException e) {
      throw new FormatException("Could not write JPEG data", e);
    }
    return out.toByteArray();
  }

  /* @see Codec#decompress(RandomAccessStream, Object) */
  public byte[] decompress(RandomAccessStream in, Object options)
    throws FormatException, IOException
  {
    BufferedImage b;
    long fp = in.getFilePointer();
    try {
      while (in.read() != (byte) 0xff || in.read() != (byte) 0xd8);
      in.seek(in.getFilePointer() - 2);

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

    boolean littleEndian = true, interleaved = true;
    if (options instanceof Boolean) {
      littleEndian = ((Boolean) options).booleanValue();
    }
    else {
      Object[] o = (Object[]) options;
      littleEndian = ((Boolean) o[0]).booleanValue();
      interleaved = ((Boolean) o[1]).booleanValue();
    }

    byte[][] buf = AWTImageTools.getPixelBytes(b, littleEndian);
    byte[] rtn = new byte[buf.length * buf[0].length];
    if (buf.length == 1) rtn = buf[0];
    else {
      if (interleaved) {
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

//
// JPEG2000Codec.java
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
import javax.imageio.spi.*;
import javax.imageio.stream.MemoryCacheImageInputStream;
import loci.formats.*;

/**
 * This class implements JPEG 2000 decompression.  Compression is not yet
 * implemented.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/JPEG2000Codec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/JPEGCodec.java">SVN</a></dd></dl>
 */
public class JPEG2000Codec extends BaseCodec implements Codec {

  // -- Constants --

  private static final String NO_J2K_MSG =
    "The JAI Image I/O Tools are required to read JPEG-2000 files.  Please " +
    "obtain jai_imageio.jar from http://loci.wisc.edu/ome/formats.html";

  private static final String J2K_READER =
    "com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReader";

  // -- Static fields --

  private static boolean noJ2k = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    // NB: ImageJ does not access the jai_imageio classes with the normal
    // class loading scheme, and thus the necessary service provider stuff is
    // not automatically registered. Instead, we register the J2KImageReader
    // with the IIORegistry manually, merely so that we can obtain a
    // J2KImageReaderSpi object from the IIORegistry's service provider lookup
    // function, then use it to construct a J2KImageReader object directly.

    ReflectedUniverse ru = null;
    try {
      // register J2KImageReader with IIORegistry
      String j2kReaderSpi = J2K_READER + "Spi";
      Class j2kSpiClass = null;
      try {
        j2kSpiClass = Class.forName(j2kReaderSpi);
      }
      catch (ClassNotFoundException exc) {
        LogTools.trace(exc);
        noJ2k = true;
      }
      catch (NoClassDefFoundError err) {
        LogTools.trace(err);
        noJ2k = true;
      }
      catch (RuntimeException exc) {
        // HACK: workaround for bug in Apache Axis2
        String msg = exc.getMessage();
        if (msg != null && msg.indexOf("ClassNotFound") < 0) throw exc;
        LogTools.trace(exc);
        noJ2k = true;
      }
      IIORegistry registry = IIORegistry.getDefaultInstance();
      if (j2kSpiClass != null) {
        Iterator providers = ServiceRegistry.lookupProviders(j2kSpiClass);
        registry.registerServiceProviders(providers);
      }

      // obtain J2KImageReaderSpi instance from IIORegistry
      Object j2kSpi = registry.getServiceProviderByClass(j2kSpiClass);
      ru = new ReflectedUniverse();

      ru.exec("import " + J2K_READER);
      ru.exec("import com.sun.media.imageio.plugins.jpeg2000.J2KImageReadParam");
      ru.setVar("j2kSpi", j2kSpi);
      ru.exec("j2kReader = new J2KImageReader(j2kSpi)");
    }
    catch (Throwable t) {
      noJ2k = true;
      LogTools.trace(t);
    }
    return ru;
  }

  /**
   * Compresses a block of JPEG 2000 data.  Currently not supported.
   *
   * @param data the data to be compressed
   * @param x length of the x dimension of the image data, if appropriate
   * @param y length of the y dimension of the image data, if appropriate
   * @param dims the dimensions of the image data, if appropriate
   * @param options options to be used during compression, if appropriate
   * @return The compressed data
   * @throws FormatException If input is not a proper data block.
   */
  public byte[] compress(byte[] data, int x, int y, int[] dims, Object options)
    throws FormatException
  {
    // TODO
    throw new FormatException("JPEG 2000 compression not currently supported");
  }

  /**
   * Decodes an image strip using JPEG 2000.
   *
   * @param in The stream from which to read compressed data.
   * @return The decompressed data
   * @throws FormatException if data is not valid
   */
  public byte[] decompress(RandomAccessStream in, Object options)
    throws FormatException
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

    byte[][] single = null, half = null;
    BufferedImage b = null;
    try {
      byte[] buf = new byte[(int) (in.length() - in.getFilePointer())];
      in.read(buf);

      // HACK
      for (int i=0; i<buf.length-1; i++) {
        if (buf[i] == (byte) 0xff && buf[i + 1] == 0x51) {
          int ch = DataTools.bytesToInt(buf, i + 38, 2, false);
          for (int c=0; c<ch; c++) {
            buf[i + 41 + c*3] = (byte) 1;
          }

          break;
        }
      }

      ByteArrayInputStream bis = new ByteArrayInputStream(buf);
      MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(bis);

      r.setVar("mciis", mciis);
      r.exec("j2kReader.setInput(mciis)");
      r.setVar("zero", 0);
      b = (BufferedImage) r.exec("j2kReader.read(zero)");
      single = ImageTools.getPixelBytes(b, littleEndian);

      bis.close();
      mciis.close();
      buf = null;
    }
    catch (ReflectException exc) {
      throw new FormatException(exc);
    }
    catch (IOException exc) {
      throw new FormatException(exc);
    }

    if (single.length == 1) return single[0];
    byte[] rtn = new byte[single.length * single[0].length];
    if (interleaved) {
      int next = 0;
      for (int i=0; i<single[0].length; i++) {
        for (int j=0; j<single.length; j++) {
          rtn[next++] = single[j][i];
        }
      }
    }
    else {
      for (int i=0; i<single.length; i++) {
        System.arraycopy(single[i], 0, rtn, i*single[0].length, single[i].length);
      }
    }
    return rtn;
  }

}

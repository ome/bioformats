//
// JPEG2000Codec.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
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

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.spi.*;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import loci.common.*;
import loci.formats.*;

/**
 * This class implements JPEG 2000 decompression.  Compression is not yet
 * implemented.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/codec/JPEG2000Codec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/JPEGCodec.java">SVN</a></dd></dl>
 */
public class JPEG2000Codec extends BaseCodec {

  // -- Constants --

  private static final String NO_J2K_MSG =
    "The JAI Image I/O Tools are required to read JPEG-2000 files. Please " +
    "obtain jai_imageio.jar from http://loci.wisc.edu/ome/formats-library.html";

  private static final String J2K_READER =
    "com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReader";

  private static final String J2K_WRITER =
    "com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriter";

  // -- Static fields --

  private static boolean noJ2k = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static void registerClass(String className) {
    String spi = className + "Spi";
    Class spiClass = null;
    try {
      spiClass = Class.forName(spi);
    }
    catch (ClassNotFoundException exc) {
      LogTools.trace(exc);
      noJ2k = true;
      return;
    }
    catch (NoClassDefFoundError err) {
      LogTools.trace(err);
      noJ2k = true;
      return;
    }
    catch (RuntimeException exc) {
      // HACK: workaround for bug in Apache Axis2
      String msg = exc.getMessage();
      if (msg != null && msg.indexOf("ClassNotFound") < 0) throw exc;
      LogTools.trace(exc);
      noJ2k = true;
      return;
    }
    IIORegistry registry = IIORegistry.getDefaultInstance();
    if (spiClass != null) {
      Iterator providers = ServiceRegistry.lookupProviders(spiClass);
      registry.registerServiceProviders(providers);
    }
  }

  private static ReflectedUniverse createReflectedUniverse() {
    // NB: ImageJ does not access the jai_imageio classes with the normal
    // class loading scheme, and thus the necessary service provider stuff is
    // not automatically registered. Instead, we register the J2KImageReader
    // with the IIORegistry manually, merely so that we can obtain a
    // J2KImageReaderSpi object from the IIORegistry's service provider lookup
    // function, then use it to construct a J2KImageReader object directly.

    ReflectedUniverse ru = null;
    try {
      // NB: the following comment facilitates dependency detection:
      // import com.sun.media.imageioimpl.plugins.jpeg2000

      // register J2KImageReader with IIORegistry
      registerClass(J2K_READER);

      if (noJ2k) {
        throw new FormatException("Could not compress JPEG-2000 data.\n" +
          NO_J2K_MSG);
      }

      IIORegistry registry = IIORegistry.getDefaultInstance();

      // obtain J2KImageReaderSpi instance from IIORegistry
      Class j2kSpiClass = Class.forName(J2K_READER + "Spi");
      Object j2kSpi = registry.getServiceProviderByClass(j2kSpiClass);
      ru = new ReflectedUniverse();

      ru.exec("import " + J2K_READER);
      ru.setVar("j2kSpi", j2kSpi);
      ru.exec("j2kReader = new J2KImageReader(j2kSpi)");

      // register J2KImageWriter with IIORegistry
      registerClass(J2K_WRITER);
      j2kSpiClass = Class.forName(J2K_WRITER + "Spi");
      j2kSpi = registry.getServiceProviderByClass(j2kSpiClass);
      ru.exec("import " + J2K_WRITER);
      ru.setVar("j2kSpi", j2kSpi);
      ru.exec("j2kWriter = new J2KImageWriter(j2kSpi)");
    }
    catch (Throwable t) {
      noJ2k = true;
      LogTools.trace(t);
    }
    return ru;
  }

  // -- Codec API methods --

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#width width}
   *  {@link CodecOptions#height height}
   *  {@link CodecOptions#bitsPerSample bitsPerSample}
   *  {@link CodecOptions#channels channels}
   *  {@link CodecOptions#interleaved interleaved}
   *  {@link CodecOptions#littleEndian littleEndian}
   *
   * @see Codec#compress(byte[], CodecOptions)
   */
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    if (options == null) options = CodecOptions.getDefaultOptions();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BufferedImage img = null;

    int next = 0;

    // NB: Construct BufferedImages manually, rather than using
    // AWTImageTools.makeImage. The AWTImageTools.makeImage methods construct
    // images that are not properly handled by the JPEG2000 writer.
    // Specifically, 8-bit multi-channel images are constructed with type
    // DataBuffer.TYPE_INT (so a single int is used to store all of the
    // channels for a specific pixel).

    int plane = options.width * options.height;

    if (options.bitsPerSample == 8) {
      byte[][] b = new byte[options.channels][plane];
      if (options.interleaved) {
        for (int q=0; q<plane; q++) {
          for (int c=0; c<options.channels; c++) {
            b[c][q] = data[next++];
          }
        }
      }
      else {
        for (int c=0; c<options.channels; c++) {
          System.arraycopy(data, c * plane, b[c], 0, plane);
        }
      }
      DataBuffer buffer = new DataBufferByte(b, plane);
      img = AWTImageTools.constructImage(b.length, DataBuffer.TYPE_BYTE,
        options.width, options.height, false, true, buffer);
    }
    else if (options.bitsPerSample == 16) {
      short[][] s = new short[options.channels][plane];
      if (options.interleaved) {
        for (int q=0; q<plane; q++) {
          for (int c=0; c<options.channels; c++) {
            s[c][q] =
              DataTools.bytesToShort(data, next, 2, options.littleEndian);
            next += 2;
          }
        }
      }
      else {
        for (int c=0; c<options.channels; c++) {
          for (int q=0; q<plane; q++) {
            s[c][q] =
              DataTools.bytesToShort(data, next, 2, options.littleEndian);
            next += 2;
          }
        }
      }
      DataBuffer buffer = new DataBufferUShort(s, plane);
      img = AWTImageTools.constructImage(s.length, DataBuffer.TYPE_USHORT,
        options.width, options.height, false, true, buffer);
    }

    try {
      ImageOutputStream ios = ImageIO.createImageOutputStream(out);
      r.setVar("img", img);
      r.setVar("out", ios);
      r.exec("j2kWriter.setOutput(out)");
      r.exec("j2kWriter.write(img)");
      ios.close();
    }
    catch (ReflectException e) {
      throw new FormatException("Could not compress JPEG-2000 data.", e);
    }
    catch (IOException e) {
      throw new FormatException("Could not compress JPEG-2000 data.", e);
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
    if (options == null) options = CodecOptions.getDefaultOptions();

    byte[][] single = null, half = null;
    BufferedImage b = null;
    Exception exception = null;
    long fp = in.getFilePointer();
    try {
      byte[] buf = null;
      if (options.maxBytes == 0) {
        buf = new byte[(int) (in.length() - fp)];
      }
      else buf = new byte[(int) (options.maxBytes - fp)];
      in.read(buf);

      ByteArrayInputStream bis = new ByteArrayInputStream(buf);
      MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(bis);

      r.setVar("mciis", mciis);
      r.exec("j2kReader.setInput(mciis)");
      r.setVar("zero", 0);
      b = (BufferedImage) r.exec("j2kReader.read(zero)");
      single = AWTImageTools.getPixelBytes(b, options.littleEndian);

      bis.close();
      mciis.close();
      buf = null;
      b = null;
    }
    catch (ReflectException exc) {
      exception = exc;
    }
    catch (IOException exc) {
      exception = exc;
    }

    if (exception != null) {
      throw new FormatException("Could not decompress JPEG2000 image. Please " +
        "make sure that jai_imageio.jar is installed.", exception);
    }

    if (single.length == 1) return single[0];
    byte[] rtn = new byte[single.length * single[0].length];
    if (options.interleaved) {
      int next = 0;
      for (int i=0; i<single[0].length; i++) {
        for (int j=0; j<single.length; j++) {
          rtn[next++] = single[j][i];
        }
      }
    }
    else {
      for (int i=0; i<single.length; i++) {
        System.arraycopy(single[i], 0,
          rtn, i*single[0].length, single[i].length);
      }
    }
    single = null;

    return rtn;
  }

}

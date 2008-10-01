//
// JPEG2000Codec.java
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

import java.awt.Image;
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
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/codec/JPEG2000Codec.java">Trac</a>,
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
      ru.setVar("j2kSpi", j2kSpi);
      ru.exec("j2kReader = new J2KImageReader(j2kSpi)");
    }
    catch (Throwable t) {
      noJ2k = true;
      LogTools.trace(t);
    }
    return ru;
  }

  // -- Codec API methods --

  /* @see Codec#compress(byte[], int, int, int[], Object) */
  public byte[] compress(byte[] data, int x, int y, int[] dims, Object options)
    throws FormatException
  {
    // TODO
    throw new FormatException("JPEG 2000 compression not currently supported");
  }

  /* @see Codec#decompress(RandomAccessStream, Object) */
  public byte[] decompress(RandomAccessStream in, Object options)
    throws FormatException, IOException
  {
    boolean littleEndian = false, interleaved = false;
    long maxFP = 0;
    if (options instanceof Boolean) {
      littleEndian = ((Boolean) options).booleanValue();
    }
    else {
      Object[] o = (Object[]) options;
      littleEndian = ((Boolean) o[0]).booleanValue();
      interleaved = ((Boolean) o[1]).booleanValue();
      if (o.length == 3) maxFP = ((Long) o[2]).longValue();
    }

    byte[][] single = null, half = null;
    BufferedImage b = null;
    Exception exception = null;
    long fp = in.getFilePointer();
    try {
      if (maxFP == 0) maxFP = in.length();
      byte[] buf = new byte[(int) (maxFP - fp)];
      in.read(buf);

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
      b = null;
    }
    catch (ReflectException exc) {
      exception = exc;
    }
    catch (IOException exc) {
      exception = exc;
    }

    if (exception != null) {
      // decoding may have failed if this is a more exotic flavor of JP2K
      // we will now try to use JMagick (Java wrappers for ImageMagick)

      try {
        in.seek(fp);
        if (maxFP == 0) maxFP = in.length();
        byte[] buf = new byte[(int) (maxFP - fp)];
        in.read(buf);

        // modified from code contributed by Toby Cornish
        ReflectedUniverse jmagick = new ReflectedUniverse();
        jmagick.exec("import magick.util.MagickCanvas");
        jmagick.exec("import magick.MagickImage");
        jmagick.exec("import magick.ColorspaceType");
        jmagick.exec("import magick.MagickProducer");
        jmagick.exec("import magick.ImageInfo");

        jmagick.setVar("buf", buf);
        jmagick.exec("info = new ImageInfo()");
        jmagick.exec("image = new MagickImage(info, buf)");
        jmagick.exec("image.transformRgbImage(ColorspaceType.YCbCrColorspace)");
        jmagick.exec("canvas = new MagickCanvas()");
        jmagick.exec("producer = new MagickProducer(image)");
        Image img = (Image) jmagick.exec("canvas.createImage(producer)");
        b = ImageTools.makeBuffered(img);
        // don't use getPixelBytes, because JMagick always gives us
        // 32 bits per channel
        single = ImageTools.getBytes(b);
      }
      catch (ReflectException exc) {
        throw new FormatException(exc);
      }
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
    single = null;

    return rtn;
  }

}

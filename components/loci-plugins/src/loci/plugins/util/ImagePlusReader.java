//
// ImagePlusReader.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins.util;

import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;

import loci.common.DataTools;
import loci.formats.ClassList;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.ImageTools;
import loci.formats.ReaderWrapper;
import loci.formats.in.ND2Reader;
import loci.formats.in.PictReader;
import loci.formats.in.QTReader;
import loci.formats.in.SDTReader;

/**
 * Utility methods for working with ImagePlus objects.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/util/ImagePlusReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/util/ImagePlusReader.java">SVN</a></dd></dl>
 */
public class ImagePlusReader extends ReaderWrapper {

  // -- Utility methods --

  /**
   * Creates an image reader according to the current configuration settings,
   * including which format readers are currently enabled, as well as
   * format-specific configuration settings.
   */
  public static ImageReader makeImageReader() {
    // include only enabled classes
    Class[] c = null;
    try {
      ClassList defaultClasses =
        new ClassList("readers.txt", IFormatReader.class);
      c = defaultClasses.getClasses();
    }
    catch (IOException exc) {
      return new ImageReader();
    }
    ClassList enabledClasses = new ClassList(IFormatReader.class);
    for (int i=0; i<c.length; i++) {
      boolean on = LociPrefs.isReaderEnabled(c[i]);
      if (on) {
        try {
          enabledClasses.addClass(c[i]);
        }
        catch (FormatException exc) {
          exc.printStackTrace();
        }
      }
    }
    ImageReader reader = new ImageReader(enabledClasses);

    // toggle reader-specific options
    boolean nd2Nikon = LociPrefs.isND2Nikon();
    boolean pictQTJava = LociPrefs.isPictQTJava();
    boolean qtQTJava = LociPrefs.isQTQTJava();
    boolean sdtIntensity = LociPrefs.isSDTIntensity();
    IFormatReader[] r = reader.getReaders();
    for (int i=0; i<r.length; i++) {
      if (r[i] instanceof ND2Reader) {
        ND2Reader nd2 = (ND2Reader) r[i];
        nd2.setLegacy(nd2Nikon);
      }
      else if (r[i] instanceof PictReader) {
        PictReader pict = (PictReader) r[i];
        pict.setLegacy(pictQTJava);
      }
      else if (r[i] instanceof QTReader) {
        QTReader qt = (QTReader) r[i];
        qt.setLegacy(qtQTJava);
      }
      else if (r[i] instanceof SDTReader) {
        SDTReader sdt = (SDTReader) r[i];
        sdt.setIntensity(sdtIntensity);
      }
    }

    return reader;
  }

  // -- Constructors --

  /** Constructs an ImagePlusReader around a new image reader. */
  public ImagePlusReader() { super(makeImageReader()); }

  /** Constructs an ImagePlusReader with the given reader. */
  public ImagePlusReader(IFormatReader r) { super(r); }

  // -- ImagePlusReader methods --

  /**
   * Creates an ImageJ image processor object
   * for the image plane at the given position.
   *
   * @param no Position of image plane.
   */
  public ImageProcessor[] openProcessors(int no)
    throws FormatException, IOException
  {
    return openProcessors(no, null);
  }

  /**
   * Returns an array of ImageProcessors that represent the given slice.
   * There is one ImageProcessor per RGB channel, i.e.
   * length of returned array == getRGBChannelCount().
   *
   * @param no Position of image plane.
   * @param crop Image cropping specifications, or null if no cropping
   *   is to be done.
   */
  public ImageProcessor[] openProcessors(int no, Rectangle crop)
    throws FormatException, IOException
  {
    // read byte array
    byte[] b = null;
    boolean first = true;
    if (crop == null) crop = new Rectangle(0, 0, getSizeX(), getSizeY());
    while (true) {
      // read LuraWave license code, if available
      String code = LuraWave.initLicenseCode();
      try {
        b = openBytes(no, crop.x, crop.y, crop.width, crop.height);
        break;
      }
      catch (FormatException exc) {
        if (LuraWave.isLicenseCodeException(exc)) {
          // prompt user for LuraWave license code
          code = LuraWave.promptLicenseCode(code, first);
          if (code == null) return null;
          if (first) first = false;
        }
        else throw exc;
      }
    }

    int w = crop.width;
    int h = crop.height;
    int c = getRGBChannelCount();
    int type = getPixelType();
    int bpp = FormatTools.getBytesPerPixel(type);
    boolean interleave = isInterleaved();

    if (b.length != w * h * c * bpp && b.length != w * h * bpp) {
      // HACK - byte array dimensions are incorrect - image is probably
      // a different size, but we have no way of knowing what size;
      // so open this plane as a BufferedImage to find out
      BufferedImage bi =
        openImage(no, crop.x, crop.y, crop.width, crop.height);
      b = ImageTools.padImage(b, isInterleaved(), c,
        bi.getWidth() * bpp, w, h);
    }

    // convert byte array to appropriate primitive array type
    boolean isFloat = type == FormatTools.FLOAT || type == FormatTools.DOUBLE;
    boolean isLittle = isLittleEndian();
    boolean isSigned = type == FormatTools.INT8 || type == FormatTools.INT16 ||
      type == FormatTools.INT32;

    IndexColorModel cm = null;
    if (isIndexed()) {
      byte[][] byteTable = get8BitLookupTable();
      if (byteTable != null) {
        cm = new IndexColorModel(8, byteTable[0].length, byteTable[0],
          byteTable[1], byteTable[2]);
      }
      short[][] shortTable = get16BitLookupTable();
      if (shortTable != null) {
        byteTable = new byte[3][256];

        for (int i=0; i<byteTable[0].length; i++) {
          int index = (int) (((float) i / byteTable[0].length) * 65535);
          byteTable[0][i] = (byte) ((shortTable[0][index] / 65535f) * 255);
          byteTable[1][i] = (byte) ((shortTable[1][index] / 65535f) * 255);
          byteTable[2][i] = (byte) ((shortTable[2][index] / 65535f) * 255);
        }

        cm = new IndexColorModel(8, byteTable[0].length, byteTable[0],
          byteTable[1], byteTable[2]);
      }
    }

    // construct image processors
    ImageProcessor[] ip = new ImageProcessor[c];
    for (int i=0; i<c; i++) {
      byte[] channel =
        ImageTools.splitChannels(b, i, c, bpp, false, interleave);
      Object pixels = DataTools.makeDataArray(channel, bpp, isFloat, isLittle);
      if (pixels instanceof byte[]) {
        byte[] q = (byte[]) pixels;
        if (q.length > w * h) {
          byte[] tmp = q;
          q = new byte[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        if (isSigned) q = DataTools.makeSigned(q);

        ip[i] = new ByteProcessor(w, h, q, null);
        if (cm != null) ip[i].setColorModel(cm);
      }
      else if (pixels instanceof short[]) {
        short[] q = (short[]) pixels;
        if (q.length > w * h) {
          short[] tmp = q;
          q = new short[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        if (isSigned) q = DataTools.makeSigned(q);

        ip[i] = new ShortProcessor(w, h, q, cm);
      }
      else if (pixels instanceof int[]) {
        int[] q = (int[]) pixels;
        if (q.length > w * h) {
          int[] tmp = q;
          q = new int[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }

        ip[i] = new FloatProcessor(w, h, q);
      }
      else if (pixels instanceof float[]) {
        float[] q = (float[]) pixels;
        if (q.length > w * h) {
          float[] tmp = q;
          q = new float[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        ip[i] = new FloatProcessor(w, h, q, null);
      }
      else if (pixels instanceof double[]) {
        double[] q = (double[]) pixels;
        if (q.length > w * h) {
          double[] tmp = q;
          q = new double[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        ip[i] = new FloatProcessor(w, h, q);
      }
    }

    return ip;
  }

}

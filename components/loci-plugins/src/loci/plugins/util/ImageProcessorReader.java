//
// ImageProcessorReader.java
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
import java.awt.image.IndexColorModel;
import java.io.IOException;

import loci.common.DataTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageTools;
import loci.formats.MinMaxCalculator;

/**
 * A low-level reader for {@link ij.process.ImageProcessor} objects.
 * For a higher-level reader that returns {@link ij.ImagePlus} objects,
 * see {@link loci.plugins.importer.ImagePlusReader} instead.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/util/ImageProcessorReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/util/ImageProcessorReader.java">SVN</a></dd></dl>
 */
public class ImageProcessorReader extends MinMaxCalculator {

  // -- Utility methods --

  /**
   * Converts the given reader into a ImageProcessorReader, wrapping if needed.
   */
  public static ImageProcessorReader makeImageProcessorReader(IFormatReader r) {
    if (r instanceof ImageProcessorReader) return (ImageProcessorReader) r;
    return new ImageProcessorReader(r);
  }

  // -- Constructors --

  /** Constructs an ImageProcessorReader around a new image reader. */
  public ImageProcessorReader() { super(LociPrefs.makeImageReader()); }

  /** Constructs an ImageProcessorReader with the given reader. */
  public ImageProcessorReader(IFormatReader r) { super(r); }

  // -- ImageProcessorReader methods --

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
   * There is one ImageProcessor per RGB channel;
   * i.e., length of returned array == getRGBChannelCount().
   *
   * @param no Position of image plane.
   * @param crop Image cropping specifications,
   *   or null if no cropping is to be done.
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
      throw new FormatException("Invalid byte array length: " + b.length +
        " (expected w=" + w + ", h=" + h + ", c=" + c + ", bpp=" + bpp + ")");
    }

    // convert byte array to appropriate primitive array type
    boolean isFloat = FormatTools.isFloatingPoint(type);
    boolean isLittle = isLittleEndian();
    boolean isSigned = FormatTools.isSigned(type);

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
          byteTable[0][i] = (byte) shortTable[0][i];
          byteTable[1][i] = (byte) shortTable[1][i];
          byteTable[2][i] = (byte) shortTable[2][i];
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
        if (q.length != w * h) {
          byte[] tmp = q;
          q = new byte[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        if (isSigned) q = DataTools.makeSigned(q);

        ip[i] = new ByteProcessor(w, h, q, null);
        if (cm != null) ip[i].setColorModel(cm);
      }
      else if (pixels instanceof short[]) {
        short[] q = (short[]) pixels;
        if (q.length != w * h) {
          short[] tmp = q;
          q = new short[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        if (isSigned) q = DataTools.makeSigned(q);

        ip[i] = new ShortProcessor(w, h, q, cm);
      }
      else if (pixels instanceof int[]) {
        int[] q = (int[]) pixels;
        if (q.length != w * h) {
          int[] tmp = q;
          q = new int[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }

        ip[i] = new FloatProcessor(w, h, q);
      }
      else if (pixels instanceof float[]) {
        float[] q = (float[]) pixels;
        if (q.length != w * h) {
          float[] tmp = q;
          q = new float[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        ip[i] = new FloatProcessor(w, h, q, null);
      }
      else if (pixels instanceof double[]) {
        double[] q = (double[]) pixels;
        if (q.length != w * h) {
          double[] tmp = q;
          q = new double[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        ip[i] = new FloatProcessor(w, h, q);
      }
    }

    return ip;
  }
  
  // -- IFormatReader methods --
  
  /** @Override */
  public Class<?> getNativeDataType() {
    return ImageProcessor[].class;
  }
  
  /** @Override */
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException 
  {
    return openProcessors(no, new Rectangle(x, y, w, h));
  }

}

//
// ImageTools.java
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

package loci.formats;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

/**
 * A utility class with convenience methods for manipulating images.
 *
 * Much code was stolen and adapted from DrLaszloJamf's posts at:
 *   http://forum.java.sun.com/thread.jspa?threadID=522483
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ImageTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ImageTools.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public final class ImageTools {

  // -- Constants --

  /** ImageObserver for working with AWT images. */
  protected static final Component OBS = new Container();

  // -- Constructor --

  private ImageTools() { }

  // -- Image construction - from 1D (single channel) data arrays --

  /**
   * Creates an image from the given single-channel unsigned byte data.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(byte[] data, int w, int h) {
    return makeImage(new byte[][] {data}, w, h);
  }

  /**
   * Creates an image from the given single-channel unsigned short data.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(short[] data, int w, int h) {
    return makeImage(new short[][] {data}, w, h);
  }

  /**
   * Creates an image from the given single-channel signed int data.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(int[] data, int w, int h) {
    return makeImage(new int[][] {data}, w, h);
  }

  /**
   * Creates an image from the given single-channel float data.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(float[] data, int w, int h) {
    return makeImage(new float[][] {data}, w, h);
  }

  /**
   * Creates an image from the given single-channel double data.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(double[] data, int w, int h) {
    return makeImage(new double[][] {data}, w, h);
  }

  // -- Image construction - from 1D (interleaved or banded) data arrays --

  /**
   * Creates an image from the given unsigned byte data.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(byte[] data,
    int w, int h, int c, boolean interleaved)
  {
    if (c == 1) return makeImage(data, w, h);
    int dataType = DataBuffer.TYPE_BYTE;
    DataBuffer buffer = new DataBufferByte(data, c * w * h);
    return constructImage(c, dataType, w, h, interleaved, false, buffer);
  }

  /**
   * Creates an image from the given unsigned short data.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(short[] data,
    int w, int h, int c, boolean interleaved)
  {
    if (c == 1) return makeImage(data, w, h);
    int dataType = DataBuffer.TYPE_USHORT;
    DataBuffer buffer = new DataBufferUShort(data, c * w * h);
    return constructImage(c, dataType, w, h, interleaved, false, buffer);
  }

  /**
   * Creates an image from the given signed int data.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(int[] data,
    int w, int h, int c, boolean interleaved)
  {
    if (c == 1) return makeImage(data, w, h);
    int dataType = DataBuffer.TYPE_INT;
    DataBuffer buffer = new DataBufferInt(data, c * w * h);
    return constructImage(c, dataType, w, h, interleaved, false, buffer);
  }

  /**
   * Creates an image from the given float data.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(float[] data,
    int w, int h, int c, boolean interleaved)
  {
    if (c == 1) return makeImage(data, w, h);
    int dataType = DataBuffer.TYPE_FLOAT;
    DataBuffer buffer = new DataBufferFloat(data, c * w * h);
    return constructImage(c, dataType, w, h, interleaved, false, buffer);
  }

  /**
   * Creates an image from the given double data.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(double[] data,
    int w, int h, int c, boolean interleaved)
  {
    if (c == 1) return makeImage(data, w, h);
    int dataType = DataBuffer.TYPE_DOUBLE;
    DataBuffer buffer = new DataBufferDouble(data, c * w * h);
    return constructImage(c, dataType, w, h, interleaved, false, buffer);
  }

  // -- Image construction - from 2D (banded) data arrays --

  /**
   * Creates an image from the given unsigned byte data.
   * @param data Array containing image data.
   *   It is assumed that each channel corresponds to one element of the array.
   *   For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(byte[][] data, int w, int h) {
    int dataType = DataBuffer.TYPE_BYTE;
    DataBuffer buffer = new DataBufferByte(data, data[0].length);
    return constructImage(data.length, dataType, w, h, false, true, buffer);
  }

  /**
   * Creates an image from the given unsigned short data.
   * @param data Array containing image data.
   *   It is assumed that each channel corresponds to one element of the array.
   *   For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(short[][] data, int w, int h) {
    int dataType = DataBuffer.TYPE_USHORT;
    DataBuffer buffer = new DataBufferUShort(data, data[0].length);
    return constructImage(data.length, dataType, w, h, false, true, buffer);
  }

  /**
   * Creates an image from the given signed int data.
   * @param data Array containing image data.
   *   It is assumed that each channel corresponds to one element of the array.
   *   For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(int[][] data, int w, int h) {
    int dataType = DataBuffer.TYPE_INT;
    DataBuffer buffer = new DataBufferInt(data, data[0].length);
    return constructImage(data.length, dataType, w, h, false, true, buffer);
  }

  /**
   * Creates an image from the given single-precision floating point data.
   * @param data Array containing image data.
   *   It is assumed that each channel corresponds to one element of the array.
   *   For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(float[][] data, int w, int h) {
    int dataType = DataBuffer.TYPE_FLOAT;
    DataBuffer buffer = new DataBufferFloat(data, data[0].length);
    return constructImage(data.length, dataType, w, h, false, true, buffer);
  }

  /**
   * Creates an image from the given double-precision floating point data.
   * @param data Array containing image data.
   *   It is assumed that each channel corresponds to one element of the array.
   *   For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(double[][] data, int w, int h) {
    int dataType = DataBuffer.TYPE_DOUBLE;
    DataBuffer buffer = new DataBufferDouble(data, data[0].length);
    return constructImage(data.length, dataType, w, h, false, true, buffer);
  }

  // -- Image construction - with type conversion --

  /**
   * Creates an image from the given data,
   * performing type conversions as necessary.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   * @param bpp Denotes the number of bytes in the returned primitive type
   *   (e.g. if bpp == 2, we should return an array of type short).
   * @param little Whether byte array is in little-endian order.
   */
  public static BufferedImage makeImage(byte[] data, int w, int h, int c,
    boolean interleaved, int bpp, boolean little)
  {
    return makeImage(data, w, h, c, interleaved, bpp, false, little);
  }

  /**
   * Creates an image from the given data,
   * performing type conversions as necessary.
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   * @param bpp Denotes the number of bytes in the returned primitive type
   *   (e.g. if bpp == 2, we should return an array of type short).
   * @param fp If set and bpp == 4 or bpp == 8, then return floats or doubles.
   * @param little Whether byte array is in little-endian order.
   */
  public static BufferedImage makeImage(byte[] data, int w, int h, int c,
    boolean interleaved, int bpp, boolean fp, boolean little)
  {
    Object pixels = DataTools.makeDataArray(data,
      bpp % 3 == 0 ? bpp / 3 : bpp, fp, little);

    if (pixels instanceof byte[]) {
      return makeImage((byte[]) pixels, w, h, c, interleaved);
    }
    else if (pixels instanceof short[]) {
      return makeImage((short[]) pixels, w, h, c, interleaved);
    }
    else if (pixels instanceof int[]) {
      return makeImage((int[]) pixels, w, h, c, interleaved);
    }
    else if (pixels instanceof float[]) {
      return makeImage((float[]) pixels, w, h, c, interleaved);
    }
    else if (pixels instanceof double[]) {
      return makeImage((double[]) pixels, w, h, c, interleaved);
    }
    return null;
  }

  /**
   * Creates an image from the given data,
   * performing type conversions as necessary.
   * @param data Array containing image data, one channel per element.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param bpp Denotes the number of bytes in the returned primitive type
   *   (e.g. if bpp == 2, we should return an array of type short).
   * @param little Whether byte array is in little-endian order.
   */
  public static BufferedImage makeImage(byte[][] data,
    int w, int h, int bpp, boolean little)
  {
    return makeImage(data, w, h, bpp, false, little);
  }

  /**
   * Creates an image from the given data,
   * performing type conversions as necessary.
   * @param data Array containing image data, one channel per element.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param bpp Denotes the number of bytes in the returned primitive type
   *   (e.g. if bpp == 2, we should return an array of type short).
   * @param fp If set and bpp == 4 or bpp == 8, then return floats or doubles.
   * @param little Whether byte array is in little-endian order.
   */
  public static BufferedImage makeImage(byte[][] data,
    int w, int h, int bpp, boolean fp, boolean little)
  {
    int c = data.length;
    Object v = null;
    for (int i=0; i<c; i++) {
      Object pixels = DataTools.makeDataArray(data[i],
        bpp % 3 == 0 ? bpp / 3 : bpp, fp, little);
      if (pixels instanceof byte[]) {
        if (v == null) v = new byte[c][];
        ((byte[][]) v)[i] = (byte[]) pixels;
      }
      else if (pixels instanceof short[]) {
        if (v == null) v = new short[c][];
        ((short[][]) v)[i] = (short[]) pixels;
      }
      else if (pixels instanceof int[]) {
        if (v == null) v = new int[c][];
        ((int[][]) v)[i] = (int[]) pixels;
      }
      else if (pixels instanceof float[]) {
        if (v == null) v = new float[c][];
        ((float[][]) v)[i] = (float[]) pixels;
      }
      else if (pixels instanceof double[]) {
        if (v == null) v = new double[c][];
        ((double[][]) v)[i] = (double[]) pixels;
      }
    }
    if (v instanceof byte[][]) return makeImage((byte[][]) v, w, h);
    else if (v instanceof short[][]) return makeImage((short[][]) v, w, h);
    else if (v instanceof int[][]) return makeImage((int[][]) v, w, h);
    else if (v instanceof float[][]) return makeImage((float[][]) v, w, h);
    else if (v instanceof double[][]) return makeImage((double[][]) v, w, h);
    return null;
  }

  // -- Image construction - miscellaneous --

  /**
   * Creates a blank image with the given dimensions and transfer type.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param type One of the following types:<ul>
   *   <li>FormatReader.INT8</li>
   *   <li>FormatReader.UINT8</li>
   *   <li>FormatReader.INT16</li>
   *   <li>FormatReader.UINT16</li>
   *   <li>FormatReader.INT32</li>
   *   <li>FormatReader.UINT32</li>
   *   <li>FormatReader.FLOAT</li>
   *   <li>FormatReader.DOUBLE</li>
   * </ul>
   */
  public static BufferedImage blankImage(int w, int h, int c, int type) {
    switch (type) {
      case FormatTools.INT8:
      case FormatTools.UINT8:
        return makeImage(new byte[c][w * h], w, h);
      case FormatTools.INT16:
      case FormatTools.UINT16:
        return makeImage(new short[c][w * h], w, h);
      case FormatTools.INT32:
      case FormatTools.UINT32:
        return makeImage(new int[c][w * h], w, h);
      case FormatTools.FLOAT:
        return makeImage(new float[c][w * h], w, h);
      case FormatTools.DOUBLE:
        return makeImage(new double[c][w * h], w, h);
    }
    return null;
  }

  /** Creates an image with the given DataBuffer. */
  private static BufferedImage constructImage(int c, int type, int w,
    int h, boolean interleaved, boolean banded, DataBuffer buffer)
  {
    ColorModel colorModel = makeColorModel(c, type);
    if (colorModel == null) return null;

    SampleModel model;
    if (banded) model = new BandedSampleModel(type, w, h, c);
    else if (interleaved) {
      int[] bandOffsets = new int[c];
      for (int i=0; i<c; i++) bandOffsets[i] = i;
      model = new PixelInterleavedSampleModel(type,
        w, h, c, c * w, bandOffsets);
    }
    else {
      int[] bandOffsets = new int[c];
      for (int i=0; i<c; i++) bandOffsets[i] = i * w * h;
      model = new ComponentSampleModel(type, w, h, 1, w, bandOffsets);
    }

    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  // -- Data extraction --

  /**
   * Gets the image's pixel data as arrays of primitives, one per channel.
   * The returned type will be either byte[][], short[][], int[][], float[][]
   * or double[][], depending on the image's transfer type.
   */
  public static Object getPixels(BufferedImage image) {
    WritableRaster raster = image.getRaster();
    int tt = raster.getTransferType();
    if (tt == DataBuffer.TYPE_BYTE) return getBytes(image);
    else if (tt == DataBuffer.TYPE_USHORT) return getShorts(image);
    else if (tt == DataBuffer.TYPE_INT) return getInts(image);
    else if (tt == DataBuffer.TYPE_FLOAT) return getFloats(image);
    else if (tt == DataBuffer.TYPE_DOUBLE) return getDoubles(image);
    else return null;
  }

  /** Extracts pixel data as arrays of unsigned bytes, one per channel. */
  public static byte[][] getBytes(BufferedImage image) {
    WritableRaster r = image.getRaster();
    if (canUseBankDataDirectly(image, 1,
      DataBuffer.TYPE_BYTE, DataBufferByte.class))
    {
      return ((DataBufferByte) r.getDataBuffer()).getBankData();
    }
    //return getBytes(makeType(image, dataType));
    int w = image.getWidth(), h = image.getHeight(), c = r.getNumBands();
    byte[][] samples = new byte[c][w * h];
    int[] buf = new int[w * h];
    for (int i=0; i<c; i++) {
      r.getSamples(0, 0, w, h, i, buf);
      for (int j=0; j<buf.length; j++) samples[i][j] = (byte) buf[j];
    }
    return samples;
  }

  /** Extracts pixel data as arrays of unsigned shorts, one per channel. */
  public static short[][] getShorts(BufferedImage image) {
    WritableRaster r = image.getRaster();
    if (canUseBankDataDirectly(image, 2,
      DataBuffer.TYPE_USHORT, DataBufferUShort.class))
    {
      return ((DataBufferUShort) r.getDataBuffer()).getBankData();
    }
    //return getShorts(makeType(image, DataBuffer.TYPE_USHORT));
    int w = image.getWidth(), h = image.getHeight(), c = r.getNumBands();
    short[][] samples = new short[c][w * h];
    int[] buf = new int[w * h];
    for (int i=0; i<c; i++) {
      r.getSamples(0, 0, w, h, i, buf);
      for (int j=0; j<buf.length; j++) samples[i][j] = (short) buf[j];
    }
    return samples;
  }

  /** Extracts pixel data as arrays of signed integers, one per channel. */
  public static int[][] getInts(BufferedImage image) {
    WritableRaster r = image.getRaster();
    if (canUseBankDataDirectly(image, 4,
      DataBuffer.TYPE_INT, DataBufferInt.class))
    {
      return ((DataBufferInt) r.getDataBuffer()).getBankData();
    }
    // NB: an order of magnitude faster than the naive makeType solution
    int w = image.getWidth(), h = image.getHeight(), c = r.getNumBands();
    int[][] samples = new int[c][w * h];
    for (int i=0; i<c; i++) r.getSamples(0, 0, w, h, i, samples[i]);
    return samples;
  }

  /** Extracts pixel data as arrays of floats, one per channel. */
  public static float[][] getFloats(BufferedImage image) {
    WritableRaster r = image.getRaster();
    if (canUseBankDataDirectly(image, 4,
      DataBuffer.TYPE_FLOAT, DataBufferFloat.class))
    {
      return ((DataBufferFloat) r.getDataBuffer()).getBankData();
    }
    // NB: an order of magnitude faster than the naive makeType solution
    int w = image.getWidth(), h = image.getHeight(), c = r.getNumBands();
    float[][] samples = new float[c][w * h];
    for (int i=0; i<c; i++) r.getSamples(0, 0, w, h, i, samples[i]);
    return samples;
  }

  /** Extracts pixel data as arrays of doubles, one per channel. */
  public static double[][] getDoubles(BufferedImage image) {
    WritableRaster r = image.getRaster();
    if (canUseBankDataDirectly(image, 8,
      DataBuffer.TYPE_DOUBLE, DataBufferDouble.class))
    {
      return ((DataBufferDouble) r.getDataBuffer()).getBankData();
    }
    // NB: an order of magnitude faster than the naive makeType solution
    int w = image.getWidth(), h = image.getHeight(), c = r.getNumBands();
    double[][] samples = new double[c][w * h];
    for (int i=0; i<c; i++) r.getSamples(0, 0, w, h, i, samples[i]);
    return samples;
  }

  /**
   * Whether we can return the data buffer's bank data
   * without performing any copy or conversion operations.
   */
  private static boolean canUseBankDataDirectly(BufferedImage image,
    int bytesPerPixel, int transferType, Class dataBufferClass)
  {
    WritableRaster r = image.getRaster();
    int tt = r.getTransferType();
    if (tt != transferType) return false;
    DataBuffer buffer = r.getDataBuffer();
    if (!dataBufferClass.isInstance(buffer)) return false;
    SampleModel model = r.getSampleModel();
    if (!(model instanceof ComponentSampleModel)) return false;
    ComponentSampleModel csm = (ComponentSampleModel) model;
    int pixelStride = csm.getPixelStride();
    if (pixelStride != 1) return false;
    int w = r.getWidth();
    int scanlineStride = csm.getScanlineStride();
    if (scanlineStride != w) return false;
    int c = r.getNumBands();
    int[] bandOffsets = csm.getBandOffsets();
    if (bandOffsets.length != c) return false;
    for (int i=0; i<bandOffsets.length; i++) {
      if (bandOffsets[i] != 0) return false;
    }
    int[] bankIndices = csm.getBankIndices();
    for (int i=0; i<bandOffsets.length; i++) {
      if (bandOffsets[i] != i) return false;
    }
    return true;
  }

  /**
   * Return a 2D array of bytes representing the image.  If the transfer type
   * is something other than DataBuffer.TYPE_BYTE, then each pixel value is
   * converted to the appropriate number of bytes.  In other words, if we
   * are given an image with 16-bit data, each channel of the resulting array
   * will have width * height * 2 bytes.
   */
  public static byte[][] getPixelBytes(BufferedImage img, boolean little) {
    Object pixels = getPixels(img);

    if (pixels instanceof byte[][]) {
      byte[][] b = (byte[][]) pixels;
      return (byte[][]) pixels;
    }
    else if (pixels instanceof short[][]) {
      short[][] s = (short[][]) pixels;
      byte[][] b = new byte[s.length][s[0].length * 2];
      for (int i=0; i<b.length; i++) {
        for (int j=0; j<s[0].length; j++) {
          short v = s[i][j];
          if (little) {
            b[i][j*2] = (byte) (v & 0xff);
            b[i][j*2+1] = (byte) ((v >>> 8) & 0xff);
          }
          else {
            b[i][j*2] = (byte) ((v >>> 8) & 0xff);
            b[i][j*2+1] = (byte) (v & 0xff);
          }
        }
      }
      return b;
    }
    else if (pixels instanceof int[][]) {
      int[][] in = (int[][]) pixels;
      byte[][] b = new byte[in.length][in[0].length * 4];
      for (int i=0; i<b.length; i++) {
        for (int j=0; j<in[0].length; j++) {
          int v = in[i][j];
          if (little) {
            b[i][j*4] = (byte) (v & 0xff);
            b[i][j*4+1] = (byte) ((v >> 8) & 0xff);
            b[i][j*4+2] = (byte) ((v >> 16) & 0xff);
            b[i][j*4+3] = (byte) ((v >> 24) & 0xff);
          }
          else {
            b[i][j*4] = (byte) ((v >> 24) & 0xff);
            b[i][j*4+1] = (byte) ((v >> 16) & 0xff);
            b[i][j*4+2] = (byte) ((v >> 8) & 0xff);
            b[i][j*4+3] = (byte) (v & 0xff);
          }
        }
      }
      return b;
    }
    else if (pixels instanceof float[][]) {
      float[][] in = (float[][]) pixels;
      byte[][] b = new byte[in.length][in[0].length * 4];
      for (int i=0; i<b.length; i++) {
        for (int j=0; j<in[0].length; j++) {
          int v = Float.floatToIntBits(in[i][j]);
          if (little) {
            b[i][j*4] = (byte) (v & 0xff);
            b[i][j*4+1] = (byte) ((v >> 8) & 0xff);
            b[i][j*4+2] = (byte) ((v >> 16) & 0xff);
            b[i][j*4+3] = (byte) ((v >> 24) & 0xff);
          }
          else {
            b[i][j*4] = (byte) ((v >> 24) & 0xff);
            b[i][j*4+1] = (byte) ((v >> 16) & 0xff);
            b[i][j*4+2] = (byte) ((v >> 8) & 0xff);
            b[i][j*4+3] = (byte) (v & 0xff);
          }
        }
      }
      return b;
    }
    else if (pixels instanceof double[][]) {

    }
    return null;
  }

  /**
   * Gets the pixel type of the given image.
   * @return One of the following types:<ul>
   *   <li>FormatReader.INT8</li>
   *   <li>FormatReader.UINT8</li>
   *   <li>FormatReader.INT16</li>
   *   <li>FormatReader.UINT16</li>
   *   <li>FormatReader.INT32</li>
   *   <li>FormatReader.UINT32</li>
   *   <li>FormatReader.FLOAT</li>
   *   <li>FormatReader.DOUBLE</li>
   *   <li>-1 (unknown type)</li>
   * </ul>
   */
  public static int getPixelType(BufferedImage image) {
    int type = image.getRaster().getDataBuffer().getDataType();
    switch (type) {
      case DataBuffer.TYPE_BYTE:
        return FormatTools.UINT8;
      case DataBuffer.TYPE_DOUBLE:
        return FormatTools.DOUBLE;
      case DataBuffer.TYPE_FLOAT:
        return FormatTools.FLOAT;
      case DataBuffer.TYPE_INT:
        return FormatTools.INT32;
      case DataBuffer.TYPE_SHORT:
        return FormatTools.INT16;
      case DataBuffer.TYPE_USHORT:
        return FormatTools.UINT16;
      default:
        return -1;
    }
  }

  // -- Image conversion --

  // NB: The commented out makeType method below is broken in that it results
  // in rescaled data in some circumstances. We were using it for getBytes and
  // getShorts, but due to this problem we implemented a different solution
  // using Raster.getPixels instead. But we have left the makeType method here
  // in case we decide to explore this issue any further in the future.

  ///** Copies the given image into a result with the specified data type. */
  //public static BufferedImage makeType(BufferedImage image, int type) {
  //  WritableRaster r = image.getRaster();
  //  int w = image.getWidth(), h = image.getHeight(), c = r.getNumBands();
  //  ColorModel colorModel = makeColorModel(c, type);
  //  if (colorModel == null) return null;
  //
  //  int s = w * h;
  //  DataBuffer buf = null;
  //  if (type == DataBuffer.TYPE_BYTE) buf = new DataBufferByte(s, c);
  //  else if (type == DataBuffer.TYPE_USHORT) buf = new DataBufferUShort(s, c);
  //  else if (type == DataBuffer.TYPE_INT) buf = new DataBufferInt(s, c);
  //  else if (type == DataBuffer.TYPE_SHORT) buf = new DataBufferShort(s, c);
  //  else if (type == DataBuffer.TYPE_FLOAT) buf = new DataBufferFloat(s, c);
  //  else if (type == DataBuffer.TYPE_DOUBLE) buf = new DataBufferDouble(s, c);
  //  if (buf == null) return null;
  //
  //  SampleModel model = new BandedSampleModel(type, w, h, c);
  //  WritableRaster raster = Raster.createWritableRaster(model, buf, null);
  //  BufferedImage target = new BufferedImage(colorModel, raster, false, null);
  //  Graphics2D g2 = target.createGraphics();
  //  g2.drawRenderedImage(image, null);
  //  g2.dispose();
  //  return target;
  //}

  /** Get the bytes from an image, merging the channels as necessary. */
  public static byte[] getBytes(BufferedImage img, boolean separated, int c) {
    byte[][] p = getBytes(img);
    if (separated || p.length == 1) return p[0];
    else {
      byte[] rtn = new byte[p.length * p[0].length];
      for (int i=0; i<p.length; i++) {
        System.arraycopy(p[i], 0, rtn, i * p[0].length, p[i].length);
      }
      return rtn;
    }
  }

  /**
   * Convert an arbitrary primitive type array with 3 samples per pixel to
   * a 3 x (width * height) byte array.
   */
  public static byte[][] make24Bits(Object pixels, int w, int h,
    boolean interleaved, boolean reverse)
  {
    int[] pix = make24Bits(pixels, w, h, interleaved);
    byte[][] rtn = new byte[3][pix.length];
    for (int i=0; i<pix.length; i++) {
      byte r = (byte) ((pix[i] >> 16) & 0xff);
      rtn[1][i] = (byte) ((pix[i] >> 8) & 0xff);
      byte b = (byte) (pix[i] & 0xff);
      rtn[0][i] = reverse ? b : r;
      rtn[2][i] = reverse ? r : b;
    }
    return rtn;
  }

  /**
   * Convert an arbitrary primitive type array with 3 samples per pixel to
   * an int array, i.e. RGB color with 8 bits per pixel.
   * Does not perform any scaling.
   */
  public static int[] make24Bits(Object pixels, int w, int h,
    boolean interleaved)
  {
    int[] rtn = new int[w * h];

    byte[] b = null;

    // adapted from ImageJ's TypeConverter methods

    if (pixels instanceof byte[]) b = (byte[]) pixels;
    else if (pixels instanceof short[]) {
      short[] s = (short[]) pixels;
      b = new byte[s.length];
      for (int i=0; i<s.length; i++) {
        int v = s[i] & 0xffff;
        b[i] = (byte) v;
      }
    }
    else if (pixels instanceof int[]) {
      int[] s = (int[]) pixels;
      b = new byte[s.length];
      for (int i=0; i<s.length; i++) {
        int value = s[i] & 0xffffffff;
        b[i] = (byte) value;
      }
    }
    else if (pixels instanceof float[]) {
      float[] s = (float[]) pixels;
      b = new byte[s.length];
      for (int i=0; i<s.length; i++) {
        float value = s[i];
        b[i] = (byte) (255 * value);
      }
    }
    else if (pixels instanceof double[]) {
      double[] s = (double[]) pixels;
      b = new byte[s.length];
      for (int i=0; i<s.length; i++) {
        double value = s[i];
        b[i] = (byte) Math.round(value);
      }
    }

    int c = b.length / rtn.length;

    for (int i=0; i<rtn.length; i++) {
      byte[] a = new byte[4];
      for (int j=c-1; j>=0; j--) {
        a[j] = b[interleaved ? i*c + j : i + j*w*h];
      }
      if (c == 1) {
        for (int j=1; j<a.length; j++) {
          a[j] = a[0];
        }
      }

      byte tmp = a[0];
      a[0] = a[2];
      a[2] = tmp;
      rtn[i] = DataTools.bytesToInt(a, true);
    }

    return rtn;
  }

  // -- Image manipulation --

  /**
   * Splits the given multi-channel array into a 2D array.
   * The "reverse" parameter is false if channels are in RGB order, true if
   * channels are in BGR order.
   */
  public static byte[][] splitChannels(byte[] array, int c, int bytes,
    boolean reverse, boolean interleaved)
  {
    byte[][] rtn = new byte[c][array.length / c];

    if (interleaved) {
      if (reverse) {
        int offset = 0;
        for (int i=c-1; i>=0; i--) {
          System.arraycopy(array, offset, rtn[i], 0, rtn[i].length);
          offset += rtn[c].length;
        }
      }
      else {
        for (int i=0; i<c; i++) {
          System.arraycopy(array, i * rtn[i].length, rtn[i], 0, rtn[i].length);
        }
      }
    }
    else {
      if (reverse) {
        int next = 0;
        for (int i=0; i<array.length; i+=c*bytes) {
          for (int j=c-1; j>=0; j--) {
            for (int k=0; k<bytes; k++) {
              if (next < rtn[j].length) {
                rtn[c - j - 1][next] = array[i + j*bytes + k];
              }
              next++;
            }
            next -= bytes;
          }
          next += bytes;
        }
      }
      else {
        int next = 0;
        for (int i=0; i<array.length; i+=c*bytes) {
          for (int j=0; j<c; j++) {
            for (int k=0; k<bytes; k++) {
              if (next < rtn[j].length) rtn[j][next] = array[i + j*bytes + k];
              next++;
            }
            next -= bytes;
          }
          next += bytes;
        }
      }
    }
    return rtn;
  }

  /** Splits the given multi-channel image into single-channel images. */
  public static BufferedImage[] splitChannels(BufferedImage image) {
    int w = image.getWidth(), h = image.getHeight();
    int c = image.getRaster().getNumBands();
    if (c == 1) return new BufferedImage[] {image};
    BufferedImage[] results = new BufferedImage[c];

    Object o = getPixels(image);
    if (o instanceof byte[][]) {
      byte[][] pix = (byte[][]) o;
      for (int i=0; i<c; i++) results[i] = makeImage(pix[i], w, h);
    }
    else if (o instanceof short[][]) {
      short[][] pix = (short[][]) o;
      for (int i=0; i<c; i++) results[i] = makeImage(pix[i], w, h);
    }
    else if (o instanceof int[][]) {
      int[][] pix = (int[][]) o;
      for (int i=0; i<c; i++) results[i] = makeImage(pix[i], w, h);
    }
    else if (o instanceof float[][]) {
      float[][] pix = (float[][]) o;
      for (int i=0; i<c; i++) results[i] = makeImage(pix[i], w, h);
    }
    else if (o instanceof double[][]) {
      double[][] pix = (double[][]) o;
      for (int i=0; i<c; i++) results[i] = makeImage(pix[i], w, h);
    }

    return results;
  }

  /** Merges the given images into a single multi-channel image. */
  public static BufferedImage mergeChannels(BufferedImage[] images) {
    if (images == null || images.length == 0) return null;

    // create list of pixels arrays
    Object[] list = new Object[images.length];
    int c = 0, type = 0;
    for (int i=0; i<images.length; i++) {
      Object o = getPixels(images[i]);
      if (o instanceof byte[][]) {
        if (i == 0) type = DataBuffer.TYPE_BYTE;
        else if (type != DataBuffer.TYPE_BYTE) return null;
        c += ((byte[][]) o).length;
      }
      else if (o instanceof short[][]) {
        if (i == 0) type = DataBuffer.TYPE_USHORT;
        else if (type != DataBuffer.TYPE_USHORT) return null;
        c += ((short[][]) o).length;
      }
      else if (o instanceof int[][]) {
        if (i == 0) type = DataBuffer.TYPE_INT;
        else if (type != DataBuffer.TYPE_INT) return null;
        c += ((int[][]) o).length;
      }
      else if (o instanceof float[][]) {
        if (i == 0) type = DataBuffer.TYPE_FLOAT;
        else if (type != DataBuffer.TYPE_FLOAT) return null;
        c += ((float[][]) o).length;
      }
      else if (o instanceof double[][]) {
        if (i == 0) type = DataBuffer.TYPE_DOUBLE;
        else if (type != DataBuffer.TYPE_DOUBLE) return null;
        c += ((double[][]) o).length;
      }
      if (c > 4) return null;
      list[i] = o;
    }
    if (c < 1 || c > 4) return null;

    // compile results into a single array
    int w = images[0].getWidth(), h = images[0].getHeight();
    if (type == DataBuffer.TYPE_BYTE) {
      byte[][] pix = new byte[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        byte[][] b = (byte[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      while (ndx < pix.length) pix[ndx++] = new byte[w * h]; // blank channel
      return makeImage(pix, w, h);
    }
    if (type == DataBuffer.TYPE_USHORT) {
      short[][] pix = new short[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        short[][] b = (short[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      while (ndx < pix.length) pix[ndx++] = new short[w * h]; // blank channel
      return makeImage(pix, w, h);
    }
    if (type == DataBuffer.TYPE_INT) {
      int[][] pix = new int[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        int[][] b = (int[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      while (ndx < pix.length) pix[ndx++] = new int[w * h]; // blank channel
      return makeImage(pix, w, h);
    }
    if (type == DataBuffer.TYPE_FLOAT) {
      float[][] pix = new float[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        float[][] b = (float[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      while (ndx < pix.length) pix[ndx++] = new float[w * h]; // blank channel
      return makeImage(pix, w, h);
    }
    if (type == DataBuffer.TYPE_DOUBLE) {
      double[][] pix = new double[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        double[][] b = (double[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      while (ndx < pix.length) pix[ndx++] = new double[w * h]; // blank channel
      return makeImage(pix, w, h);
    }

    return null;
  }

  /**
   * Pads (or crops) the image to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static BufferedImage padImage(BufferedImage img, int width, int height)
  {
    if (img == null) {
      byte[][] data = new byte[1][width * height];
      return makeImage(data, width, height);
    }

    boolean needsPadding = img.getWidth() != width || img.getHeight() != height;

    if (needsPadding) {
      Object pixels = getPixels(img);

      if (pixels instanceof byte[][]) {
        byte[][] b = (byte[][]) pixels;
        byte[][] newBytes = new byte[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newBytes[i] = padImage(b[i], false, 1, img.getWidth(), width, height);
        }
        return makeImage(newBytes, width, height);
      }
      else if (pixels instanceof short[][]) {
        short[][] b = (short[][]) pixels;
        short[][] newShorts = new short[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newShorts[i] =
            padImage(b[i], false, 1, img.getWidth(), width, height);
        }
        return makeImage(newShorts, width, height);
      }
      else if (pixels instanceof int[][]) {
        int[][] b = (int[][]) pixels;
        int[][] newInts = new int[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newInts[i] = padImage(b[i], false, 1, img.getWidth(), width, height);
        }
        return makeImage(newInts, width, height);
      }
      else if (pixels instanceof float[][]) {
        float[][] b = (float[][]) pixels;
        float[][] newFloats = new float[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newFloats[i] =
            padImage(b[i], false, 1, img.getWidth(), width, height);
        }
        return makeImage(newFloats, width, height);
      }
      else if (pixels instanceof double[][]) {
        double[][] b = (double[][]) pixels;
        double[][] newDoubles = new double[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newDoubles[i] =
            padImage(b[i], false, 1, img.getWidth(), width, height);
        }
        return makeImage(newDoubles, width, height);
      }
      return null;
    }
    return img;
  }

  /**
   * Pads (or crops) the byte array to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static byte[] padImage(byte[] b, boolean interleaved, int c,
    int oldWidth, int width, int height)
  {
    int oldHeight = b.length / (oldWidth * c);
    byte[] padded = new byte[height * width * c];

    int wClip = (width - oldWidth) / 2;
    int hClip = (height - oldHeight) / 2;

    int h = height < oldHeight ? height : oldHeight;

    if (interleaved) {
      int len = oldWidth < width ? oldWidth : width;
      if (h == oldHeight) {
        for (int y=0; y<h*c; y++) {
          int oldIndex = oldWidth * y;
          int index = width * y;
          System.arraycopy(b, oldIndex, padded, index, len);
        }
      }
      else {
        for (int ch=0; ch<c; ch++) {
          for (int y=0; y<h; y++) {
            int oldIndex = oldWidth * ch * oldHeight + oldWidth * y;
            int index = width * ch * height + width * y;
            System.arraycopy(b, oldIndex, padded, index, len);
          }
        }
      }
    }
    else {
      int len = oldWidth < width ? oldWidth * c : width * c;
      for (int oy=0, y=0; oy<oldHeight; oy++, y++) {
        int oldIndex = oldWidth * c * y;
        int index = width * c * (y + hClip) + c * wClip;
        System.arraycopy(b, oldIndex, padded, index, len);
      }
    }
    return padded;
  }

  /**
   * Pads (or crops) the short array to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static short[] padImage(short[] b, boolean interleaved, int c,
    int oldWidth, int width, int height)
  {
    int oldHeight = b.length / (oldWidth * c);
    short[] padded = new short[height * width * c];

    int wClip = (width - oldWidth) / 2;
    int hClip = (height - oldHeight) / 2;

    int h = height < oldHeight ? height : oldHeight;

    if (interleaved) {
      int len = oldWidth < width ? oldWidth : width;
      if (h == oldHeight) {
        for (int y=0; y<h*c; y++) {
          int oldIndex = oldWidth * y;
          int index = width * y;
          System.arraycopy(b, oldIndex, padded, index, len);
        }
      }
      else {
        for (int ch=0; ch<c; ch++) {
          for (int y=0; y<h; y++) {
            int oldIndex = oldWidth * ch * oldHeight + oldWidth * y;
            int index = width * ch * height + width * y;
            System.arraycopy(b, oldIndex, padded, index, len);
          }
        }
      }
    }
    else {
      int len = oldWidth < width ? oldWidth * c : width * c;
      for (int oy=0, y=0; oy<oldHeight; oy++, y++) {
        int oldIndex = oldWidth * c * y;
        int index = width * c * (y + hClip) + c * wClip;
        System.arraycopy(b, oldIndex, padded, index, len);
      }
    }
    return padded;
  }

  /**
   * Pads (or crops) the int array to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static int[] padImage(int[] b, boolean interleaved, int c,
    int oldWidth, int width, int height)
  {
    int oldHeight = b.length / (oldWidth * c);
    int[] padded = new int[height * width * c];

    int wClip = (width - oldWidth) / 2;
    int hClip = (height - oldHeight) / 2;

    int h = height < oldHeight ? height : oldHeight;

    if (interleaved) {
      int len = oldWidth < width ? oldWidth : width;
      if (h == oldHeight) {
        for (int y=0; y<h*c; y++) {
          int oldIndex = oldWidth * y;
          int index = width * y;
          System.arraycopy(b, oldIndex, padded, index, len);
        }
      }
      else {
        for (int ch=0; ch<c; ch++) {
          for (int y=0; y<h; y++) {
            int oldIndex = oldWidth * ch * oldHeight + oldWidth * y;
            int index = width * ch * height + width * y;
            System.arraycopy(b, oldIndex, padded, index, len);
          }
        }
      }
    }
    else {
      int len = oldWidth < width ? oldWidth * c : width * c;
      for (int oy=0, y=0; oy<oldHeight; oy++, y++) {
        int oldIndex = oldWidth * c * y;
        int index = width * c * (y + hClip) + c * wClip;
        System.arraycopy(b, oldIndex, padded, index, len);
      }
    }
    return padded;
  }

  /**
   * Pads (or crops) the float array to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static float[] padImage(float[] b, boolean interleaved, int c,
    int oldWidth, int width, int height)
  {
    int oldHeight = b.length / (oldWidth * c);
    float[] padded = new float[height * width * c];

    int wClip = (width - oldWidth) / 2;
    int hClip = (height - oldHeight) / 2;

    int h = height < oldHeight ? height : oldHeight;

    if (interleaved) {
      int len = oldWidth < width ? oldWidth : width;
      if (h == oldHeight) {
        for (int y=0; y<h*c; y++) {
          int oldIndex = oldWidth * y;
          int index = width * y;
          System.arraycopy(b, oldIndex, padded, index, len);
        }
      }
      else {
        for (int ch=0; ch<c; ch++) {
          for (int y=0; y<h; y++) {
            int oldIndex = oldWidth * ch * oldHeight + oldWidth * y;
            int index = width * ch * height + width * y;
            System.arraycopy(b, oldIndex, padded, index, len);
          }
        }
      }
    }
    else {
      int len = oldWidth < width ? oldWidth * c : width * c;
      for (int oy=0, y=0; oy<oldHeight; oy++, y++) {
        int oldIndex = oldWidth * c * y;
        int index = width * c * (y + hClip) + c * wClip;
        System.arraycopy(b, oldIndex, padded, index, len);
      }
    }
    return padded;
  }

  /**
   * Pads (or crops) the double array to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static double[] padImage(double[] b, boolean interleaved, int c,
    int oldWidth, int width, int height)
  {
    int oldHeight = b.length / (oldWidth * c);
    double[] padded = new double[height * width * c];

    int wClip = (width - oldWidth) / 2;
    int hClip = (height - oldHeight) / 2;

    int h = height < oldHeight ? height : oldHeight;

    if (interleaved) {
      int len = oldWidth < width ? oldWidth : width;
      if (h == oldHeight) {
        for (int y=0; y<h*c; y++) {
          int oldIndex = oldWidth * y;
          int index = width * y;
          System.arraycopy(b, oldIndex, padded, index, len);
        }
      }
      else {
        for (int ch=0; ch<c; ch++) {
          for (int y=0; y<h; y++) {
            int oldIndex = oldWidth * ch * oldHeight + oldWidth * y;
            int index = width * ch * height + width * y;
            System.arraycopy(b, oldIndex, padded, index, len);
          }
        }
      }
    }
    else {
      int len = oldWidth < width ? oldWidth * c : width * c;
      for (int oy=0, y=0; oy<oldHeight; oy++, y++) {
        int oldIndex = oldWidth * c * y;
        int index = width * c * (y + hClip) + c * wClip;
        System.arraycopy(b, oldIndex, padded, index, len);
      }
    }
    return padded;
  }

  /** Perform demosaicing on a byte array, assuming a {B, G, G, R} mosaic. */
  public static short[][] demosaic(short[][] input, int w, int h) {
    for (int i=0; i<input[0].length; i++) {
      // determine which color components need to be calculated

      boolean needsRed = !(((i / w) % 2 == 1) && ((i % w) % 2 == 1));
      boolean needsBlue = !(((i / w) % 2 == 0) && ((i % w) % 2 == 0));
      boolean needsGreen = needsBlue ^ needsRed;

      if (needsRed) {
        int sum = 0;
        int count = 0;

        int[] indices = null;

        if (!needsBlue) {
          indices = new int[] {i - w - 1, i - w + 1, i + w - 1, i + w + 1};
        }
        else if ((i / w) % 2 == 1) indices = new int[] {i - 1, i + 1};
        else indices = new int[] {i - w, i + w};

        for (int j=0; j<indices.length; j++) {
          if (indices[j] < input[0].length && indices[j] >= 0) {
            sum += (int) input[0][indices[j]];
            count++;
          }
        }

        if (count > 0) {
          input[0][i] = (short) (sum / count);
        }
      }

      if (needsGreen) {
        int sum = 0;
        int count = 0;

        int[] indices = {i - w, i - 1, i + 1, i + w};

        for (int j=0; j<indices.length; j++) {
          if (indices[j] < input[0].length && indices[j] >= 0) {
            sum += (int) input[1][indices[j]];
            count++;
          }
        }

        if (count > 0) {
          input[1][i] = (short) (sum / count);
        }
      }

      if (needsBlue) {
        int sum = 0;
        int count = 0;

        int[] indices = null;

        if (!needsRed) {
          indices = new int[] {i - w - 1, i - w + 1, i + w - 1, i + w + 1};
        }
        else if ((i / w) % 2 == 1) indices = new int[] {i - w, i + w};
        else indices = new int[] {i - 1, i + 1};

        for (int j=0; j<indices.length; j++) {
          if (indices[j] < input[0].length && indices[j] >= 0) {
            sum += (int) input[2][indices[j]];
            count++;
          }
        }

        if (count > 0) {
          input[2][i] = (short) (sum / count);
        }
      }
    }

    // Before returning, perform convolution with a 5x5 pseudo-Gaussian filter.
    // This is *not* an optimal filter, but it works reasonably well.

    short[] newRed = new short[w * h];
    short[] newGreen = new short[w * h];
    short[] newBlue = new short[w * h];

    float[][] kernel = new float[5][5];
    kernel[0] = new float[] {0.458f, 0.823f, 1f, 0.823f, 0.458f};
    kernel[1] = new float[] {0.823f, 1f, 1.09f, 1f, 0.823f};
    kernel[2] = new float[] {1f, 1.09f, 1.135f, 1.09f, 1f};
    kernel[3] = new float[] {0.823f, 1f, 1.09f, 1f, 0.823f};
    kernel[4] = new float[] {0.458f, 0.823f, 1f, 0.823f, 0.458f};

    for (int i=0; i<h; i++) {
      for (int j=0; j<w; j++) {
        float redRow = 0, greenRow = 0, blueRow = 0;
        float[] kernelRow = kernel[i % 5];

        int diff = w - j;
        float sum = 0;

        if (diff < 5) {
          while (diff > 0) {
            int off = i*w + j + diff - 1;
            redRow += kernelRow[diff - 1] * input[0][off];
            greenRow += kernelRow[diff - 1] * input[1][off];
            blueRow += kernelRow[diff - 1] * input[2][off];
            sum += kernelRow[diff - 1];
            diff--;
          }
        }
        else {
          for (int m=0; m<5; m++) {
            int off = i*w + j + m;
            redRow += kernelRow[m] * input[0][off];
            greenRow += kernelRow[m] * input[1][off];
            blueRow += kernelRow[m] * input[2][off];
            sum += kernelRow[m];
          }
        }
        if (sum == 0) sum = 1;
        newRed[i*w + j] = (short) (redRow / sum);
        newGreen[i*w + j] = (short) (greenRow / sum);
        newBlue[i*w + j] = (short) (blueRow / sum);
      }
    }

    for (int i=0; i<h; i++) {
      for (int j=0; j<w; j++) {
        float redCol = 0, greenCol = 0, blueCol = 0;
        float[] kernelCol = kernel[j % 5];

        int diff = h - i;
        float sum = 0;

        if (diff < 5) {
          while (diff > 0) {
            int off = (i + diff - 1) * w + j;
            redCol += kernelCol[diff - 1] * newRed[off];
            greenCol += kernelCol[diff - 1] * newGreen[off];
            blueCol += kernelCol[diff - 1] * newBlue[off];
            sum += kernelCol[diff - 1];
            diff--;
          }
        }
        else {
          for (int m=0; m<5; m++) {
            int off = (i + m) * w + j;
            redCol += kernelCol[m] * newRed[off];
            greenCol += kernelCol[m] * newGreen[off];
            blueCol += kernelCol[m] * newBlue[off];
            sum += kernelCol[m];
          }
        }

        if (sum == 0) sum = 1;
        input[0][i*w + j] = (short) (redCol / sum);
        input[1][i*w + j] = (short) (greenCol / sum);
        input[2][i*w + j] = (short) (blueCol / sum);
      }
    }

    return input;
  }

  /**
   * Perform autoscaling on the given BufferedImage;
   * map min to 0 and max to 255.  If the BufferedImage has 8 bit data, then
   * nothing happens.
   */
  public static BufferedImage autoscale(BufferedImage img, int min, int max) {
    Object pixels = getPixels(img);

    if (pixels instanceof byte[][]) return img;
    else if (pixels instanceof short[][]) {
      short[][] shorts = (short[][]) pixels;
      byte[][] out = new byte[shorts.length][shorts[0].length];

      for (int i=0; i<out.length; i++) {
        for (int j=0; j<out[i].length; j++) {
          if (shorts[i][j] < 0) shorts[i][j] += 32767;

          float diff = (float) max - (float) min;
          float dist = (float) (shorts[i][j] - min) / diff;

          if (shorts[i][j] >= max) out[i][j] = (byte) 255;
          else if (shorts[i][j] <= min) out[i][j] = 0;
          else out[i][j] = (byte) (dist * 256);
        }
      }

      return makeImage(out, img.getWidth(), img.getHeight());
    }
    else if (pixels instanceof int[][]) {
      int[][] ints = (int[][]) pixels;
      byte[][] out = new byte[ints.length][ints[0].length];

      for (int i=0; i<out.length; i++) {
        for (int j=0; j<out[i].length; j++) {
          if (ints[i][j] >= max) out[i][j] = (byte) 255;
          else if (ints[i][j] <= min) out[i][j] = 0;
          else {
            int diff = max - min;
            float dist = (ints[i][j] - min) / diff;
            out[i][j] = (byte) (dist * 256);
          }
        }
      }

      return makeImage(out, img.getWidth(), img.getHeight());
    }
    else if (pixels instanceof float[][]) {
      float[][] floats = (float[][]) pixels;
      byte[][] out = new byte[floats.length][floats[0].length];

      for (int i=0; i<out.length; i++) {
        for (int j=0; j<out[i].length; j++) {
          if (floats[i][j] >= max) out[i][j] = (byte) 255;
          else if (floats[i][j] <= min) out[i][j] = 0;
          else {
            int diff = max - min;
            float dist = (floats[i][j] - min) / diff;
            out[i][j] = (byte) (dist * 256);
          }
        }
      }

      return makeImage(out, img.getWidth(), img.getHeight());
    }
    else if (pixels instanceof double[][]) {
      double[][] doubles = (double[][]) pixels;
      byte[][] out = new byte[doubles.length][doubles[0].length];

      for (int i=0; i<out.length; i++) {
        for (int j=0; j<out[i].length; j++) {
          if (doubles[i][j] >= max) out[i][j] = (byte) 255;
          else if (doubles[i][j] <= min) out[i][j] = 0;
          else {
            int diff = max - min;
            float dist = (float) (doubles[i][j] - min) / diff;
            out[i][j] = (byte) (dist * 256);
          }
        }
      }

      return makeImage(out, img.getWidth(), img.getHeight());
    }
    return img;
  }

  /**
   * Perform autoscaling on the given byte array;
   * map min to 0 and max to 255.  If the number of bytes per pixel is 1, then
   * nothing happens.
   */
  public static byte[] autoscale(byte[] b, int min, int max, int bpp,
    boolean little)
  {
    if (bpp == 1) return b;

    byte[] out = new byte[b.length / bpp];

    for (int i=0; i<b.length; i+=bpp) {
      int s = DataTools.bytesToInt(b, i, bpp, little);

      if (s >= max) s = 255;
      else if (s <= min) s = 0;
      else {
        int diff = max - min;
        float dist = (s - min) / diff;

        s = (int) dist * 256;
      }

      out[i / bpp] = (byte) s;
    }
    return out;
  }

  /** Scan a plane for the channel min and max values. */
  public static Double[] scanData(byte[] plane, int bits, boolean littleEndian)
  {
    int max = 0;
    int min = Integer.MAX_VALUE;

    if (bits <= 8) {
      for (int j=0; j<plane.length; j++) {
        if (plane[j] < min) min = plane[j];
        if (plane[j] > max) max = plane[j];
      }
    }
    else if (bits == 16) {
      for (int j=0; j<plane.length; j+=2) {
        short s = DataTools.bytesToShort(plane, j, 2, littleEndian);
        if (s < min) min = s;
        if (s > max) max = s;
      }
    }
    else if (bits == 32) {
      for (int j=0; j<plane.length; j+=4) {
        int s = DataTools.bytesToInt(plane, j, 4, littleEndian);
        if (s < min) min = s;
        if (s > max) max = s;
      }
    }

    Double[] rtn = new Double[2];
    rtn[0] = new Double(min);
    rtn[1] = new Double(max);
    return rtn;
  }

  // -- Image scaling --

  /** Copies the source image into the target, applying scaling. */
  public static BufferedImage copyScaled(BufferedImage source,
    BufferedImage target, Object hint)
  {
    if (hint == null) hint = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
    Graphics2D g2 = target.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
    double scalex = (double) target.getWidth() / source.getWidth();
    double scaley = (double) target.getHeight() / source.getHeight();
    AffineTransform xform = AffineTransform.getScaleInstance(scalex, scaley);
    g2.drawRenderedImage(source, xform);
    g2.dispose();
    return target;
  }

  /**
   * Scales the image using the Java2D API, with the resultant
   * image optimized for the given graphics configuration.
   */
  public static BufferedImage scale2D(BufferedImage image,
    int width, int height, Object hint, GraphicsConfiguration gc)
  {
    if (gc == null) gc = getDefaultConfiguration();
    int trans = image.getColorModel().getTransparency();
    return copyScaled(image,
      gc.createCompatibleImage(width, height, trans), hint);
  }

  /**
   * Scales the image using the Java2D API, with the
   * resultant image having the given color model.
   */
  public static BufferedImage scale2D(BufferedImage image,
    int width, int height, Object hint, ColorModel cm)
  {
    WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
    boolean isRasterPremultiplied = cm.isAlphaPremultiplied();
    return copyScaled(image, new BufferedImage(cm,
      raster, isRasterPremultiplied, null), hint);
  }

  /** Scales the image using the AWT Image API. */
  public static Image scaleAWT(BufferedImage source, int width,
    int height, int hint)
  {
    return source.getScaledInstance(width, height, hint);
  }

  /**
   * Scales the image using the most appropriate API, with the resultant image
   * having the same color model as the original image.
   */
  public static BufferedImage scale(BufferedImage source,
    int width, int height, boolean pad)
  {
    int w = source.getWidth();
    int h = source.getHeight();
    if (w == width && h == height) return source;

    int finalWidth = width, finalHeight = height;

    if (pad) {
      // keep aspect ratio the same
      double r = (double) w / h;
      double ratio = (double) width / height;
      if (r > ratio) {
        // bounded by width; adjust height
        height = (h * width) / w;
      }
      else {
        // bounded by height; adjust width
        width = (w * height) / h;
      }
    }

    BufferedImage result = null;
    Image scaled = scaleAWT(source, width, height, Image.SCALE_AREA_AVERAGING);
    result = makeBuffered(scaled, source.getColorModel());
    return padImage(result, finalWidth, finalHeight);
  }

  // -- AWT images --

  /**
   * Creates a buffered image from the given AWT image object.
   * If the AWT image is already a buffered image, no new object is created.
   */
  public static BufferedImage makeBuffered(Image image) {
    if (image instanceof BufferedImage) return (BufferedImage) image;

    // TODO: better way to handle color model (don't just assume RGB)
    loadImage(image);
    BufferedImage img = new BufferedImage(image.getWidth(OBS),
      image.getHeight(OBS), BufferedImage.TYPE_INT_RGB);
    Graphics g = img.getGraphics();
    g.drawImage(image, 0, 0, OBS);
    g.dispose();
    return img;
  }

  /**
   * Creates a buffered image possessing the given color model,
   * from the specified AWT image object. If the AWT image is already a
   * buffered image with the given color model, no new object is created.
   */
  public static BufferedImage makeBuffered(Image image, ColorModel cm) {
    if (image instanceof BufferedImage) {
      BufferedImage bi = (BufferedImage) image;
      if (cm.equals(bi.getColorModel())) return bi;
    }
    loadImage(image);
    int w = image.getWidth(OBS), h = image.getHeight(OBS);
    boolean alphaPremultiplied = cm.isAlphaPremultiplied();
    WritableRaster raster = cm.createCompatibleWritableRaster(w, h);
    BufferedImage result = new BufferedImage(cm,
      raster, alphaPremultiplied, null);
    Graphics2D g = result.createGraphics();
    g.drawImage(image, 0, 0, OBS);
    g.dispose();
    return result;
  }

  /** Ensures the given AWT image is fully loaded. */
  public static boolean loadImage(Image image) {
    if (image instanceof BufferedImage) return true;
    MediaTracker tracker = new MediaTracker(OBS);
    tracker.addImage(image, 0);
    try { tracker.waitForID(0); }
    catch (InterruptedException exc) { return false; }
    if (MediaTracker.COMPLETE != tracker.statusID(0, false)) return false;
    return true;
  }

  /**
   * Gets the width and height of the given AWT image,
   * waiting for it to finish loading if necessary.
   */
  public static Dimension getSize(Image image) {
    if (image == null) return new Dimension(0, 0);
    if (image instanceof BufferedImage) {
      BufferedImage bi = (BufferedImage) image;
      return new Dimension(bi.getWidth(), bi.getHeight());
    }
    loadImage(image);
    return new Dimension(image.getWidth(OBS), image.getHeight(OBS));
  }

  // -- Graphics configuration --

  /**
   * Creates a buffered image compatible with the given graphics
   * configuration, using the given buffered image as a source.
   * If gc is null, the default graphics configuration is used.
   */
  public static BufferedImage makeCompatible(BufferedImage image,
    GraphicsConfiguration gc)
  {
    if (gc == null) gc = getDefaultConfiguration();
    int w = image.getWidth(), h = image.getHeight();
    int trans = image.getColorModel().getTransparency();
    BufferedImage result = gc.createCompatibleImage(w, h, trans);
    Graphics2D g2 = result.createGraphics();
    g2.drawRenderedImage(image, null);
    g2.dispose();
    return result;
  }

  /** Gets the default graphics configuration for the environment. */
  public static GraphicsConfiguration getDefaultConfiguration() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    return gd.getDefaultConfiguration();
  }

  // -- Color model --

  /** Gets a color space for the given number of color components. */
  public static ColorModel makeColorModel(int c, int dataType) {
    int type;
    switch (c) {
      case 1:
        type = ColorSpace.CS_GRAY;
        break;
      case 2:
        type = TwoChannelColorSpace.CS_2C;
        break;
      case 3:
        type = ColorSpace.CS_sRGB;
        break;
      case 4:
        type = ColorSpace.CS_sRGB;
        break;
      default:
        return null;
    }
    return new ComponentColorModel(TwoChannelColorSpace.getInstance(type),
      c == 4, false, ColorModel.TRANSLUCENT, dataType);
  }

  // -- Indexed color conversion --

  /** Converts an indexed color BufferedImage to an RGB BufferedImage. */
  public static BufferedImage indexedToRGB(BufferedImage img, boolean le) {
    byte[][] indices = getPixelBytes(img, le);
    if (indices.length > 1) return img;
    if (getPixelType(img) == FormatTools.UINT8) {
      IndexedColorModel model = (IndexedColorModel) img.getColorModel();
      byte[][] b = new byte[3][indices[0].length];
      for (int i=0; i<indices[0].length; i++) {
        b[0][i] = (byte) (model.getRed(indices[0][i] & 0xff) & 0xff);
        b[1][i] = (byte) (model.getGreen(indices[0][i] & 0xff) & 0xff);
        b[2][i] = (byte) (model.getBlue(indices[0][i] & 0xff) & 0xff);
      }
      return makeImage(b, img.getWidth(), img.getHeight());
    }
    else if (getPixelType(img) == FormatTools.UINT16) {
      IndexedColorModel model = (IndexedColorModel) img.getColorModel();
      short[][] s = new short[3][indices[0].length / 2];
      for (int i=0; i<s[0].length; i++) {
        int ndx = DataTools.bytesToInt(indices[0], i*2, 2, le) & 0xffff;
        s[0][i] = (short) (model.getRed(ndx) & 0xffff);
        s[1][i] = (short) (model.getRed(ndx) & 0xffff);
        s[2][i] = (short) (model.getRed(ndx) & 0xffff);
      }
      return makeImage(s, img.getWidth(), img.getHeight());
    }
    return null;
  }

  /** Converts a LUT and an array of indices into an array of RGB tuples. */
  public static byte[][] indexedToRGB(byte[][] lut, byte[] b) {
    byte[][] rtn = new byte[lut.length][b.length];

    for (int i=0; i<b.length; i++) {
      for (int j=0; j<lut.length; j++) {
        rtn[j][i] = lut[j][b[i]];
      }
    }
    return rtn;
  }

  /** Converts a LUT and an array of indices into an array of RGB tuples. */
  public static short[][] indexedToRGB(short[][] lut, byte[] b, boolean le) {
    short[][] rtn = new short[lut.length][b.length / 2];
    for (int i=0; i<b.length/2; i++) {
      for (int j=0; j<lut.length; j++) {
        rtn[j][i] = lut[j][DataTools.bytesToShort(b, i*2, 2, le)];
      }
    }
    return rtn;
  }

}

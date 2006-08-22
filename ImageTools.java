//
// ImageTools.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * A utility class with convenience methods for manipulating images.
 *
 * Much code was stolen and adapted from DrLaszloJamf's posts at:
 *   http://forum.java.sun.com/thread.jspa?threadID=522483
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class ImageTools {

  // -- Constants --

  /** ImageObserver for working with AWT images. */
  protected static final Component OBS = new Container();


  // -- Image construction --

  /**
   * Creates an image from the given data, performing type conversions as
   * necessary.
   * If the interleaved flag is set, the channels are assumed to be
   * interleaved; otherwise they are assumed to be sequential.
   * For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   * while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(byte[] data, int w, int h, int c,
    boolean interleaved, int bps, boolean little)
  {
    if (bps == 1) return makeImage(data, w, h, c, interleaved);
    else if (bps == 2) {
      short[] shorts = new short[data.length / bps];
      for (int i=0; i<shorts.length; i++) {
        shorts[i] = DataTools.bytesToShort(data, i*2, 2, little);
      }
      return makeImage(shorts, w, h, c, interleaved);
    }
    else if (bps == 3) {
      return ImageTools.makeImage(data, w, h, 3, interleaved);
    }
    else if (bps == 4) {
      float[] floats = new float[data.length / bps];
      for (int i=0; i<floats.length; i++) {
        floats[i] =
          Float.intBitsToFloat(DataTools.bytesToInt(data, i*4, 4, little));
      }
      return makeImage(floats, w, h, c, interleaved);
    }
    else if (bps == 6) {
      short[][] shorts = new short[3][data.length / 6];
      int next = 0;
      if (interleaved) {
       for (int i=0; i<shorts[0].length; i++) {
           for (int j=0; j<shorts.length; j++) {
            shorts[j][i] = DataTools.bytesToShort(data, next, 2, little);
            next += 2;
          }
        }
      }
      else {
        for (int i=0; i<shorts.length; i++) {
          for (int j=0; j<shorts[i].length; j++) {
            shorts[i][j] = DataTools.bytesToShort(data, next, 2, little);
            next += 2;
          }
        }
      }
      return makeImage(shorts, w, h);
    }
    else {
      double[] doubles = new double[data.length / bps];
      for (int i=0; i<doubles.length; i++) {
         doubles[i] = Double.longBitsToDouble(
           DataTools.bytesToLong(data, i*bps, bps, little));
      }
      return makeImage(doubles, w, h, c, interleaved);
    }
  }

  /**
   * Creates an image from the given unsigned byte data.
   * If the interleaved flag is set, the channels are assumed to be
   * interleaved; otherwise they are assumed to be sequential.
   * For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   * while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(byte[] data,
    int w, int h, int c, boolean interleaved)
  {
    if (c == 1) return makeImage(data, w, h);
    int dataType = DataBuffer.TYPE_BYTE;
    ColorModel colorModel = makeColorModel(c, dataType);
    if (colorModel == null) return null;
    int pixelStride = interleaved ? c : 1;
    int scanlineStride = interleaved ? (c * w) : w;
    int[] bandOffsets = new int[c];
    for (int i=0; i<c; i++) bandOffsets[i] = interleaved ? i : (i * w * h);
    SampleModel model = new ComponentSampleModel(dataType,
      w, h, pixelStride, scanlineStride, bandOffsets);
    DataBuffer buffer = new DataBufferByte(data, c * w * h);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  /**
   * Creates an image from the given unsigned short data.
   * If the interleaved flag is set, the channels are assumed to be
   * interleaved; otherwise they are assumed to be sequential.
   * For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   * while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(short[] data,
    int w, int h, int c, boolean interleaved)
  {
    if (c == 1) return makeImage(data, w, h);
    int dataType = DataBuffer.TYPE_USHORT;
    ColorModel colorModel = makeColorModel(c, dataType);
    if (colorModel == null) return null;
    int pixelStride = interleaved ? c : 1;
    int scanlineStride = interleaved ? (c * w) : w;
    int[] bandOffsets = new int[c];
    for (int i=0; i<c; i++) bandOffsets[i] = interleaved ? i : (i * w * h);
    SampleModel model = new ComponentSampleModel(dataType,
      w, h, pixelStride, scanlineStride, bandOffsets);
    DataBuffer buffer = new DataBufferUShort(data, c * w * h);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  /**
   * Creates an image from the given signed int data.
   * If the interleaved flag is set, the channels are assumed to be
   * interleaved; otherwise they are assumed to be sequential.
   * For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   * while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(int[] data,
    int w, int h, int c, boolean interleaved)
  {
    if (c == 1) return makeImage(data, w, h);
    int dataType = DataBuffer.TYPE_INT;
    ColorModel colorModel = makeColorModel(c, dataType);
    if (colorModel == null) return null;
    int pixelStride = interleaved ? c : 1;
    int scanlineStride = interleaved ? (c * w) : w;
    int[] bandOffsets = new int[c];
    for (int i=0; i<c; i++) bandOffsets[i] = interleaved ? i : (i * w * h);
    SampleModel model = new ComponentSampleModel(dataType,
      w, h, pixelStride, scanlineStride, bandOffsets);
    DataBuffer buffer = new DataBufferInt(data, c * w * h);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  /**
   * Creates an image from the given float data.
   * If the interleaved flag is set, the channels are assumed to be
   * interleaved; otherwise they are assumed to be sequential.
   * For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   * while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(float[] data,
    int w, int h, int c, boolean interleaved)
  {
    if (c == 1) return makeImage(data, w, h);
    int dataType = DataBuffer.TYPE_FLOAT;
    ColorModel colorModel = makeColorModel(c, dataType);
    if (colorModel == null) return null;
    int pixelStride = interleaved ? c : 1;
    int scanlineStride = interleaved ? (c * w) : w;
    int[] bandOffsets = new int[c];
    for (int i=0; i<c; i++) bandOffsets[i] = interleaved ? i : (i * w * h);
    SampleModel model = new ComponentSampleModel(dataType,
      w, h, pixelStride, scanlineStride, bandOffsets);
    DataBuffer buffer = new DataBufferFloat(data, c * w * h);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  /**
   * Creates an image from the given double data.
   * If the interleaved flag is set, the channels are assumed to be
   * interleaved; otherwise they are assumed to be sequential.
   * For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   * while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(double[] data,
    int w, int h, int c, boolean interleaved)
  {
    if (c == 1) return makeImage(data, w, h);
    int dataType = DataBuffer.TYPE_DOUBLE;
    ColorModel colorModel = makeColorModel(c, dataType);
    if (colorModel == null) return null;
    int pixelStride = interleaved ? c : 1;
    int scanlineStride = interleaved ? (c * w) : w;
    int[] bandOffsets = new int[c];
    for (int i=0; i<c; i++) bandOffsets[i] = interleaved ? i : (i * w * h);
    SampleModel model = new ComponentSampleModel(dataType,
      w, h, pixelStride, scanlineStride, bandOffsets);
    DataBuffer buffer = new DataBufferDouble(data, c * w * h);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  /**
   * Creates an image from the given data, performing type conversions as
   * necessary.
   * If the interleaved flag is set, the channels are assumed to be
   * interleaved; otherwise they are assumed to be sequential.
   * For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   * while "RRR...GGG...BBB..." is sequential.
   */
  public static BufferedImage makeImage(byte[][] data, int w, int h,
    int bps, boolean little)
  {
    if (bps == 1) return makeImage(data, w, h);
    else if (bps == 2) {
      short[][] shorts = new short[data.length][data[0].length / bps];
      for (int i=0; i<shorts.length; i++) {
        for (int j=0; j<shorts[0].length; j++) {
          shorts[i][j] = DataTools.bytesToShort(data[i], j*2, 2, little);
        }
      }
      return makeImage(shorts, w, h);
    }
    else if (bps == 4) {
      float[][] floats = new float[data.length][data[0].length / bps];
      for (int i=0; i<floats.length; i++) {
        for (int j=0; j<floats[0].length; j++) {
            floats[i][j] = Float.intBitsToFloat(
              DataTools.bytesToInt(data[i], j*4, 4, little));
        }
      }
      return makeImage(floats, w, h);
    }
    else {
      double[][] doubles = new double[data.length][data[0].length / bps];
      for (int i=0; i<doubles.length; i++) {
        for (int j=0; j<doubles[0].length; j++) {
           doubles[i][j] = Double.longBitsToDouble(
             DataTools.bytesToLong(data[i], j*bps, bps, little));
        }
      }
      return makeImage(doubles, w, h);
    }
  }

  /**
   * Creates an image from the given unsigned byte data.
   * It is assumed that each channel corresponds to one element of the array.
   * For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   */
  public static BufferedImage makeImage(byte[][] data, int w, int h) {
    int dataType = DataBuffer.TYPE_BYTE;
    ColorModel colorModel = makeColorModel(data.length, dataType);
    if (colorModel == null) return null;
    SampleModel model = new BandedSampleModel(dataType, w, h, data.length);
    DataBuffer buffer = new DataBufferByte(data, data[0].length);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  /**
   * Creates an image from the given unsigned short data.
   * It is assumed that each channel corresponds to one element of the array.
   * For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   */
  public static BufferedImage makeImage(short[][] data, int w, int h) {
    int dataType = DataBuffer.TYPE_USHORT;
    ColorModel colorModel = makeColorModel(data.length, dataType);
    if (colorModel == null) return null;
    SampleModel model = new BandedSampleModel(dataType, w, h, data.length);
    DataBuffer buffer = new DataBufferUShort(data, data[0].length);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  /**
   * Creates an image from the given signed int data.
   * It is assumed that each channel corresponds to one element of the array.
   * For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   */
  public static BufferedImage makeImage(int[][] data, int w, int h) {
    int dataType = DataBuffer.TYPE_INT;
    ColorModel colorModel = makeColorModel(data.length, dataType);
    if (colorModel == null) return null;
    SampleModel model = new BandedSampleModel(dataType, w, h, data.length);
    DataBuffer buffer = new DataBufferInt(data, data[0].length);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  /**
   * Creates an image from the given single-precision floating point data.
   * It is assumed that each channel corresponds to one element of the array.
   * For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   */
  public static BufferedImage makeImage(float[][] data, int w, int h) {
    int dataType = DataBuffer.TYPE_FLOAT;
    ColorModel colorModel = makeColorModel(data.length, dataType);
    if (colorModel == null) return null;
    SampleModel model = new BandedSampleModel(dataType, w, h, data.length);
    DataBuffer buffer = new DataBufferFloat(data, data[0].length);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  /**
   * Creates an image from the given double-precision floating point data.
   * It is assumed that each channel corresponds to one element of the array.
   * For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   */
  public static BufferedImage makeImage(double[][] data, int w, int h) {
    int dataType = DataBuffer.TYPE_DOUBLE;
    ColorModel colorModel = makeColorModel(data.length, dataType);
    if (colorModel == null) return null;
    SampleModel model = new BandedSampleModel(dataType, w, h, data.length);
    DataBuffer buffer = new DataBufferDouble(data, data[0].length);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    return new BufferedImage(colorModel, raster, false, null);
  }

  /** Creates an image from the given single-channel unsigned byte data. */
  public static BufferedImage makeImage(byte[] data, int w, int h) {
    return makeImage(new byte[][] {data}, w, h);
  }

  /** Creates an image from the given single-channel unsigned short data. */
  public static BufferedImage makeImage(short[] data, int w, int h) {
    return makeImage(new short[][] {data}, w, h);
  }

  /** Creates an image from the given single-channel signed int data. */
  public static BufferedImage makeImage(int[] data, int w, int h) {
    return makeImage(new int[][] {data}, w, h);
  }

  /** Creates an image from the given single-channel float data. */
  public static BufferedImage makeImage(float[] data, int w, int h) {
    return makeImage(new float[][] {data}, w, h);
  }

  /** Creates an image from the given single-channel double data. */
  public static BufferedImage makeImage(double[] data, int w, int h) {
    return makeImage(new double[][] {data}, w, h);
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
    int dataType = DataBuffer.TYPE_BYTE;
    WritableRaster r = image.getRaster();
    int tt = r.getTransferType();
    if (tt == dataType) {
      DataBuffer buffer = r.getDataBuffer();
      if (buffer instanceof DataBufferByte) {
        SampleModel model = r.getSampleModel();
        if (model instanceof BandedSampleModel) {
          // return bytes directly, with no copy
          return ((DataBufferByte) buffer).getBankData();
        }
      }
    }

    // convert to image with DataBufferByte and BandedSampleModel
    int w = image.getWidth(), h = image.getHeight(), c = r.getNumBands();
    ColorModel colorModel = makeColorModel(c, dataType);
    if (colorModel == null) return null;
    SampleModel model = new BandedSampleModel(dataType, w, h, c);
    DataBuffer buffer = new DataBufferByte(w * h, c);
    WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
    BufferedImage target = new BufferedImage(colorModel, raster, false, null);
    Graphics2D g2 = target.createGraphics();
    g2.drawRenderedImage(image, null);
    g2.dispose();
    return getBytes(target);
  }

  /** Extracts pixel data as arrays of unsigned shorts, one per channel. */
  public static short[][] getShorts(BufferedImage image) {
    WritableRaster r = image.getRaster();
    int tt = r.getTransferType();
    if (tt == DataBuffer.TYPE_USHORT) {
      DataBuffer buffer = r.getDataBuffer();
      if (buffer instanceof DataBufferUShort) {
        SampleModel model = r.getSampleModel();
        if (model instanceof BandedSampleModel) {
          // return shorts directly, with no copy
          return ((DataBufferUShort) buffer).getBankData();
        }
      }
    }
    return getShorts(makeType(image, DataBuffer.TYPE_USHORT));
  }

  /** Extracts pixel data as arrays of signed integers, one per channel. */
  public static int[][] getInts(BufferedImage image) {
    WritableRaster r = image.getRaster();
    int tt = r.getTransferType();
    if (tt == DataBuffer.TYPE_INT) {
      DataBuffer buffer = r.getDataBuffer();
      if (buffer instanceof DataBufferInt) {
        SampleModel model = r.getSampleModel();
        if (model instanceof BandedSampleModel) {
          // return ints directly, with no copy
          return ((DataBufferInt) buffer).getBankData();
        }
      }
    }
    return getInts(makeType(image, DataBuffer.TYPE_INT));
  }

  /** Extracts pixel data as arrays of floats, one per channel. */
  public static float[][] getFloats(BufferedImage image) {
    WritableRaster r = image.getRaster();
    int tt = r.getTransferType();
    if (tt == DataBuffer.TYPE_FLOAT) {
      DataBuffer buffer = r.getDataBuffer();
      if (buffer instanceof DataBufferFloat) {
        SampleModel model = r.getSampleModel();
        if (model instanceof BandedSampleModel) {
          // return ints directly, with no copy
          return ((DataBufferFloat) buffer).getBankData();
        }
      }
    }
    // an order of magnitude faster than the naive makeType solution
    int w = image.getWidth(), h = image.getHeight(), c = r.getNumBands();
    float[][] samples = new float[c][w * h];
    for (int i=0; i<c; i++) r.getSamples(0, 0, w, h, i, samples[i]);
    return samples;
    //return getFloats(makeType(image, DataBuffer.TYPE_FLOAT));
  }

  /** Extracts pixel data as arrays of doubles, one per channel. */
  public static double[][] getDoubles(BufferedImage image) {
    WritableRaster r = image.getRaster();
    int tt = r.getTransferType();
    if (tt == DataBuffer.TYPE_DOUBLE) {
      DataBuffer buffer = r.getDataBuffer();
      if (buffer instanceof DataBufferDouble) {
        SampleModel model = r.getSampleModel();
        if (model instanceof BandedSampleModel) {
          // return ints directly, with no copy
          return ((DataBufferDouble) buffer).getBankData();
        }
      }
    }
    // an order of magnitude faster than the naive makeType solution
    int w = image.getWidth(), h = image.getHeight(), c = r.getNumBands();
    double[][] samples = new double[c][w * h];
    for (int i=0; i<c; i++) r.getSamples(0, 0, w, h, i, samples[i]);
    return samples;
    //return getDoubles(makeType(image, DataBuffer.TYPE_DOUBLE));
  }


  // -- Image conversion --

  /** Copies the given image into a result with the specified data type. */
  public static BufferedImage makeType(BufferedImage image, int type) {
    WritableRaster r = image.getRaster();
    int w = image.getWidth(), h = image.getHeight(), c = r.getNumBands();
    ColorModel colorModel = makeColorModel(c, type);
    if (colorModel == null) return null;

    int s = w * h;
    DataBuffer buf = null;
    if (type == DataBuffer.TYPE_BYTE) buf = new DataBufferByte(s, c);
    else if (type == DataBuffer.TYPE_USHORT) buf = new DataBufferUShort(s, c);
    else if (type == DataBuffer.TYPE_INT) buf = new DataBufferInt(s, c);
    else if (type == DataBuffer.TYPE_SHORT) buf = new DataBufferShort(s, c);
    else if (type == DataBuffer.TYPE_FLOAT) buf = new DataBufferFloat(s, c);
    else if (type == DataBuffer.TYPE_DOUBLE) buf = new DataBufferDouble(s, c);
    if (buf == null) return null;

    SampleModel model = new BandedSampleModel(type, w, h, c);
    WritableRaster raster = Raster.createWritableRaster(model, buf, null);
    BufferedImage target = new BufferedImage(colorModel, raster, false, null);
    Graphics2D g2 = target.createGraphics();
    g2.drawRenderedImage(image, null);
    g2.dispose();
    return target;
  }

  /**
   * Get the bytes from an image, merging the channels as necessary.
   */
  public static byte[] getBytes(BufferedImage img, boolean separated, int c) {
    byte[][] p = getBytes(img);
    if (separated) return p[0];
    else {
      byte[] rtn = new byte[p.length * p[0].length];
      for (int i=0; i<p.length; i++) {
        System.arraycopy(p[i], 0, rtn, i * p[0].length, p[i].length);
      }
      return rtn;
    }
  }

  /**
   * Splits the given multi-channel array into a 2D array.
   * The "reverse" parameter is false if channels are in RGB order, true if
   * channels are in BGR order.
   */
  public static byte[][] splitChannels(byte[] array, int c, boolean reverse,
    boolean interleaved)
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
        for (int i=0; i<array.length; i+=c) {
          for (int j=c-1; j>=0; j--) {
            rtn[j][next] = array[i + j];
          }
          next++;
        }
      }
      else {
        int next = 0;
        for (int i=0; i<array.length; i+=c) {
          for (int j=0; j<c; j++) {
            rtn[j][next] = array[i + j];
          }
          next++;
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
    if (c != 1 && c != 3 && c != 4) return null;

    // compile results into a single array
    int w = images[0].getWidth(), h = images[0].getHeight();
    if (type == DataBuffer.TYPE_BYTE) {
      byte[][] pix = new byte[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        byte[][] b = (byte[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      return makeImage(pix, w, h);
    }
    if (type == DataBuffer.TYPE_USHORT) {
      short[][] pix = new short[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        short[][] b = (short[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      return makeImage(pix, w, h);
    }
    if (type == DataBuffer.TYPE_INT) {
      int[][] pix = new int[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        int[][] b = (int[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      return makeImage(pix, w, h);
    }
    if (type == DataBuffer.TYPE_FLOAT) {
      float[][] pix = new float[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        float[][] b = (float[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      return makeImage(pix, w, h);
    }
    if (type == DataBuffer.TYPE_DOUBLE) {
      double[][] pix = new double[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        double[][] b = (double[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      return makeImage(pix, w, h);
    }

    return null;
  }

  /** 
   * Pad the image to the given width and height. The image will be centered
   * within the new bounds.
   */
  public static BufferedImage padImage(BufferedImage img, int width, int height)
  {
    if (img == null) {
      byte[][] data = new byte[1][width * height];
      return makeImage(data, width, height);
    }
    
    boolean needsPadding = img.getWidth() != width || img.getHeight() != height;

    if (needsPadding) {
      int totalX = width - img.getWidth();
      int totalY = height - img.getHeight();

      int xpad = totalX / 2;
      int ypad = totalY / 2;

      if (xpad == 0 && totalX > 0) xpad = totalX;
      if (ypad == 0 && totalY > 0) ypad = totalY;

      Object pixels = getPixels(img);

      if (pixels instanceof byte[][]) {
        byte[][] b = (byte[][]) pixels;
        byte[][] newBytes = new byte[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newBytes[i] = padImage(b[i], img.getWidth(), width, height);
        }
        return makeImage(newBytes, width, height);
      }
      else if (pixels instanceof short[][]) {
        short[][] b = (short[][]) pixels;
        short[][] newShorts = new short[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newShorts[i] = padImage(b[i], img.getWidth(), width, height);
        }
        return makeImage(newShorts, width, height);
      }
      else if (pixels instanceof int[][]) {
        int[][] b = (int[][]) pixels;
        int[][] newInts = new int[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newInts[i] = padImage(b[i], img.getWidth(), width, height);
        }
        return makeImage(newInts, width, height);
      }
      else if (pixels instanceof float[][]) {
        float[][] b = (float[][]) pixels;
        float[][] newFloats = new float[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newFloats[i] = padImage(b[i], img.getWidth(), width, height);
        }
        return makeImage(newFloats, width, height);
      }
      else if (pixels instanceof double[][]) {
        double[][] b = (double[][]) pixels;
        double[][] newDoubles = new double[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newDoubles[i] = padImage(b[i], img.getWidth(), width, height);
        }
        return makeImage(newDoubles, width, height);
      }
      return null;
    }
    return img;
  }

  /**
   * Pad the byte array to the given width and height. The image will be
   * centered within the new bounds.
   */
  public static byte[] padImage(byte[] b, int oldWidth, int width, int height) {
    boolean needsPadding = 
      (oldWidth != width) || ((b.length / oldWidth) != height);

    if (needsPadding) {
      int totalX = width - oldWidth;
      int totalY = height - (b.length / oldWidth);

      int xpad = totalX / 2;
      int ypad = totalY / 2;

      if (xpad == 0 && totalX > 0) xpad = totalX;
      if (ypad == 0 && totalY > 0) ypad = totalY;

      byte[] padded = new byte[width * height];

      for (int i=ypad; i<height - ypad; i++) {
        System.arraycopy(b, (i - ypad) * oldWidth, padded, i*width + xpad, 
          oldWidth); 
      }
      return padded;
    }
    return b;
  }

  /**
   * Pad the short array to the given width and height. The image will be
   * centered within the new bounds.
   */
  public static short[] padImage(short[] b, int oldWidth, int width, int height)
  {
    boolean needsPadding = oldWidth != width || (b.length / oldWidth) != height;

    if (needsPadding) {
      int totalX = width - oldWidth;
      int totalY = height - (b.length / oldWidth);

      int xpad = totalX / 2;
      int ypad = totalY / 2;

      if (xpad == 0 && totalX > 0) xpad = totalX;
      if (ypad == 0 && totalY > 0) ypad = totalY;

      short[] padded = new short[width * height];

      for (int i=ypad; i<height - ypad; i++) {
        System.arraycopy(b, (i - ypad) * oldWidth, padded, i*width + xpad, 
          oldWidth); 
      }
      return padded;
    }
    return b;
  }

  /**
   * Pad the int array to the given width and height. The image will be
   * centered within the new bounds.
   */
  public static int[] padImage(int[] b, int oldWidth, int width, int height) {
    boolean needsPadding = oldWidth != width || (b.length / oldWidth) != height;

    if (needsPadding) {
      int totalX = width - oldWidth;
      int totalY = height - (b.length / oldWidth);

      int xpad = totalX / 2;
      int ypad = totalY / 2;

      if (xpad == 0 && totalX > 0) xpad = totalX;
      if (ypad == 0 && totalY > 0) ypad = totalY;

      int[] padded = new int[width * height];

      for (int i=ypad; i<height - ypad; i++) {
        System.arraycopy(b, (i - ypad) * oldWidth, padded, i*width + xpad, 
          oldWidth); 
      }
      return padded;
    }
    return b;
  }

  /**
   * Pad the float array to the given width and height. The image will be
   * centered within the new bounds.
   */
  public static float[] padImage(float[] b, int oldWidth, int width, int height)
  {
    boolean needsPadding = oldWidth != width || (b.length / oldWidth) != height;

    if (needsPadding) {
      int totalX = width - oldWidth;
      int totalY = height - (b.length / oldWidth);

      int xpad = totalX / 2;
      int ypad = totalY / 2;

      if (xpad == 0 && totalX > 0) xpad = totalX;
      if (ypad == 0 && totalY > 0) ypad = totalY;

      float[] padded = new float[width * height];

      for (int i=ypad; i<height - ypad; i++) {
        System.arraycopy(b, (i - ypad) * oldWidth, padded, i*width + xpad, 
          oldWidth); 
      }
      return padded;
    }
    return b;
  }

  /**
   * Pad the double array to the given width and height. The image will be
   * centered within the new bounds.
   */
  public static double[] padImage(double[] b, int oldWidth, int width, 
    int height) 
  {
    boolean needsPadding = oldWidth != width || (b.length / oldWidth) != height;

    if (needsPadding) {
      int totalX = width - oldWidth;
      int totalY = height - (b.length / oldWidth);

      int xpad = totalX / 2;
      int ypad = totalY / 2;

      if (xpad == 0 && totalX > 0) xpad = totalX;
      if (ypad == 0 && totalY > 0) ypad = totalY;

      double[] padded = new double[width * height];

      for (int i=ypad; i<height - ypad; i++) {
        System.arraycopy(b, (i - ypad) * oldWidth, padded, i*width + xpad, 
          oldWidth); 
      }
      return padded;
    }
    return b;
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
    int width, int height)
  {
    int w = source.getWidth();
    int h = source.getHeight();
    if (w == width && h == height) return source;
    if ((width * height) / (w * h) > 0) {
      // use Java2D to enlarge
      return scale2D(source, width, height, null, source.getColorModel());
    }
    else {
      // use AWT to shrink
      return makeBuffered(scaleAWT(source, width, height,
        Image.SCALE_AREA_AVERAGING), source.getColorModel());
    }
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
    int w = image.getWidth(OBS), h = image.getHeight(OBS);
    int[] pixels = new int[w * h];
    PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
    try { pg.grabPixels(); }
    catch (InterruptedException exc) { exc.printStackTrace(); }
    BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    result.setRGB(0, 0, w, h, pixels, 0, w);
    return result;
    //return makeBuffered(image, makeColorModel(3, DataBuffer.TYPE_INT));
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
      case 1: type = ColorSpace.CS_GRAY; break;
      case 3: type = ColorSpace.CS_sRGB; break;
      case 4: type = ColorSpace.CS_sRGB; break;
      default: return null;
    }
    return new ComponentColorModel(ColorSpace.getInstance(type),
      c == 4, false, ColorModel.TRANSLUCENT, dataType);
  }

}

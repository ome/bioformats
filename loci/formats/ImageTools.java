//
// ImageTools.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class ImageTools {

  // -- Constants --

  /** ImageObserver for working with AWT images. */
  protected static final Component OBS = new Container();


  // -- Image construction --

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
    return makeBuffered(image, makeColorModel(3, DataBuffer.TYPE_INT));
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

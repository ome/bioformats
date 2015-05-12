/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;

import loci.common.DataTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;
import ome.xml.model.primitives.PositiveInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class with convenience methods for manipulating images
 * in {@link java.awt.image.BufferedImage} form.
 *
 * To work with images in primitive array form,
 * use the {@link loci.formats.ImageTools} class.
 *
 * Much code was stolen and adapted from
 * <a href="http://forum.java.sun.com/thread.jspa?threadID=522483">
 * DrLaszloJamf's posts</a>
 * on the Java forums.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public final class AWTImageTools {

  // -- Constants --

  /** ImageObserver for working with AWT images. */
  protected static final Component OBS = new Container();

  private static final Logger LOGGER =
    LoggerFactory.getLogger(AWTImageTools.class);

  // -- Constructor --

  private AWTImageTools() { }

  // -- Image construction - from 1D (single channel) data arrays --

  /**
   * Creates an image from the given single-channel byte data.
   *
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param signed Whether the byte values should be treated as signed
   *   (-128 to 127) instead of unsigned (0 to 255).
   */
  public static BufferedImage makeImage(byte[] data,
    int w, int h, boolean signed)
  {
    return makeImage(new byte[][] {data}, w, h, signed);
  }

  /**
   * Creates an image from the given single-channel short data.
   *
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param signed Whether the short values should be treated as signed
   *   (-32768 to 32767) instead of unsigned (0 to 65535).
   */
  public static BufferedImage makeImage(short[] data,
    int w, int h, boolean signed)
  {
    return makeImage(new short[][] {data}, w, h, signed);
  }

  /**
   * Creates an image from the given single-channel int data.
   *
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param signed Whether the int values should be treated as signed
   *   (-2^31 to 2^31-1) instead of unsigned (0 to 2^32-1).
   */
  public static BufferedImage makeImage(int[] data,
    int w, int h, boolean signed)
  {
    return makeImage(new int[][] {data}, w, h, signed);
  }

  /**
   * Creates an image from the given single-channel float data.
   *
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(float[] data, int w, int h) {
    return makeImage(new float[][] {data}, w, h);
  }

  /**
   * Creates an image from the given single-channel double data.
   *
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   */
  public static BufferedImage makeImage(double[] data, int w, int h) {
    return makeImage(new double[][] {data}, w, h);
  }

  // -- Image construction - from 1D (interleaved or banded) data arrays --

  /**
   * Creates an image from the given byte data.
   *
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   * @param signed Whether the byte values should be treated as signed
   *   (-128 to 127) instead of unsigned (0 to 255).
   */
  public static BufferedImage makeImage(byte[] data,
    int w, int h, int c, boolean interleaved, boolean signed)
  {
    if (c == 1) return makeImage(data, w, h, signed);
    if (c > 2) return makeRGBImage(data, c, w, h, interleaved);
    int dataType;
    DataBuffer buffer;
    dataType = DataBuffer.TYPE_BYTE;
    if (signed) {
      buffer = new SignedByteBuffer(data, c * w * h);
    }
    else {
      buffer = new DataBufferByte(data, c * w * h);
    }
    return constructImage(c, dataType, w, h, interleaved, false, buffer);
  }

  /**
   * Creates an image from the given short data.
   *
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   * @param signed Whether the short values should be treated as signed
   *   (-32768 to 32767) instead of unsigned (0 to 65535).
   */
  public static BufferedImage makeImage(short[] data,
    int w, int h, int c, boolean interleaved, boolean signed)
  {
    if (c == 1) return makeImage(data, w, h, signed);
    int dataType;
    DataBuffer buffer;
    if (signed) {
      dataType = DataBuffer.TYPE_SHORT;
      buffer = new SignedShortBuffer(data, c * w * h);
    }
    else {
      dataType = DataBuffer.TYPE_USHORT;
      buffer = new DataBufferUShort(data, c * w * h);
    }
    return constructImage(c, dataType, w, h, interleaved, false, buffer);
  }

  /**
   * Creates an image from the given int data.
   *
   * @param data Array containing image data.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   * @param signed Whether the int values should be treated as signed
   *   (-2^31 to 2^31-1) instead of unsigned (0 to 2^32-1).
   */
  public static BufferedImage makeImage(int[] data,
    int w, int h, int c, boolean interleaved, boolean signed)
  {
    if (c == 1) return makeImage(data, w, h, signed);
    int dataType = DataBuffer.TYPE_INT;
    DataBuffer buffer;
    if (signed) {
      buffer = new DataBufferInt(data, c * w * h);
    }
    else {
      buffer = new UnsignedIntBuffer(data, c * w * h);
    }
    return constructImage(c, dataType, w, h, interleaved, false, buffer);
  }

  /**
   * Creates an image from the given float data.
   *
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
   *
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
   * Creates an image from the given byte data.
   *
   * @param data Array containing image data.
   *   It is assumed that each channel corresponds to one element of the array.
   *   For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param signed Whether the byte values should be treated as signed
   *   (-128 to 127) instead of unsigned (0 to 255).
   */
  public static BufferedImage makeImage(byte[][] data,
    int w, int h, boolean signed)
  {
    if (data.length > 2) return makeRGBImage(data, w, h);
    int dataType;
    DataBuffer buffer;
    dataType = DataBuffer.TYPE_BYTE;
    if (signed) {
      buffer = new SignedByteBuffer(data, data[0].length);
    }
    else {
      buffer = new DataBufferByte(data, data[0].length);
    }
    return constructImage(data.length, dataType, w, h, false, true, buffer);
  }

  /**
   * Creates an image from the given short data.
   *
   * @param data Array containing image data.
   *   It is assumed that each channel corresponds to one element of the array.
   *   For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param signed Whether the short values should be treated as signed
   *   (-32768 to 32767) instead of unsigned (0 to 65535).
   */
  public static BufferedImage makeImage(short[][] data,
    int w, int h, boolean signed)
  {
    int dataType;
    DataBuffer buffer;
    if (signed) {
      dataType = DataBuffer.TYPE_SHORT;
      buffer = new SignedShortBuffer(data, data[0].length);
    }
    else {
      dataType = DataBuffer.TYPE_USHORT;
      buffer = new DataBufferUShort(data, data[0].length);
    }
    return constructImage(data.length, dataType, w, h, false, true, buffer);
  }

  /**
   * Creates an image from the given int data.
   *
   * @param data Array containing image data.
   *   It is assumed that each channel corresponds to one element of the array.
   *   For example, for RGB data, data[0] is R, data[1] is G, and data[2] is B.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param signed Whether the int values should be treated as signed
   *   (-2^31 to 2^31-1) instead of unsigned (0 to 2^32-1).
   */
  public static BufferedImage makeImage(int[][] data,
    int w, int h, boolean signed)
  {
    int dataType = DataBuffer.TYPE_INT;
    DataBuffer buffer;
    if (signed) {
      buffer = new DataBufferInt(data, data[0].length);
    }
    else {
      buffer = new UnsignedIntBuffer(data, data[0].length);
    }
    return constructImage(data.length, dataType, w, h, false, true, buffer);
  }

  /**
   * Creates an image from the given single-precision floating point data.
   *
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
   *
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
   * Creates an image from the given raw byte array, obtaining the
   * dimensional parameters from the specified metadata object.
   *
   * @param data Array containing image data.
   * @param interleaved If set, the channels are assumed to be interleaved;
   *   otherwise they are assumed to be sequential.
   *   For example, for RGB data, the pattern "RGBRGBRGB..." is interleaved,
   *   while "RRR...GGG...BBB..." is sequential.
   * @param meta Metadata object containing dimensional parameters.
   * @param series Relevant image series number of metadata object.
   */
  public static BufferedImage makeImage(byte[] data, boolean interleaved,
    MetadataRetrieve meta, int series) throws FormatException
  {
    MetadataTools.verifyMinimumPopulated(meta, series);
    int width = meta.getPixelsSizeX(series).getValue().intValue();
    int height = meta.getPixelsSizeY(series).getValue().intValue();
    String pixelType = meta.getPixelsType(series).toString();
    int type = FormatTools.pixelTypeFromString(pixelType);
    PositiveInteger nChannels = meta.getChannelSamplesPerPixel(series, 0);
    if (nChannels == null) {
      LOGGER.warn("SamplesPerPixel is null; it is assumed to be 1.");
    }
    int channels = nChannels == null ? 1 : nChannels.getValue();
    boolean littleEndian = false;
    if (meta.getPixelsBigEndian(series) != null)
    {
      littleEndian = !meta.getPixelsBigEndian(series).booleanValue();
    }
    else if (meta.getPixelsBinDataCount(series) == 0) {
      littleEndian = !meta.getPixelsBinDataBigEndian(series, 0).booleanValue();
    }
    return makeImage(data, width, height, channels,
      interleaved, FormatTools.getBytesPerPixel(type),
      FormatTools.isFloatingPoint(type), littleEndian,
      FormatTools.isSigned(type));
  }

  /**
   * Creates an image from the given raw byte array,
   * performing any necessary type conversions.
   *
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
   * @param signed Whether the data values should be treated as signed
   *   instead of unsigned.
   */
  public static BufferedImage makeImage(byte[] data, int w, int h, int c,
    boolean interleaved, int bpp, boolean fp, boolean little, boolean signed)
  {
    Object pixels = DataTools.makeDataArray(data,
      bpp % 3 == 0 ? bpp / 3 : bpp, fp, little);

    if (pixels instanceof byte[]) {
      return makeImage((byte[]) pixels, w, h, c, interleaved, signed);
    }
    else if (pixels instanceof short[]) {
      return makeImage((short[]) pixels, w, h, c, interleaved, signed);
    }
    else if (pixels instanceof int[]) {
      return makeImage((int[]) pixels, w, h, c, interleaved, signed);
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
   * Creates an image from the given raw byte array,
   * performing any necessary type conversions.
   *
   * @param data Array containing image data, one channel per element.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param bpp Denotes the number of bytes in the returned primitive type
   *   (e.g. if bpp == 2, we should return an array of type short).
   * @param fp If set and bpp == 4 or bpp == 8, then return floats or doubles.
   * @param little Whether byte array is in little-endian order.
   * @param signed Whether the data values should be treated as signed
   *   instead of unsigned.
   */
  public static BufferedImage makeImage(byte[][] data,
    int w, int h, int bpp, boolean fp, boolean little, boolean signed)
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
    if (v instanceof byte[][]) {
      return makeImage((byte[][]) v, w, h, signed);
    }
    else if (v instanceof short[][]) {
      return makeImage((short[][]) v, w, h, signed);
    }
    else if (v instanceof int[][]) {
      return makeImage((int[][]) v, w, h, signed);
    }
    else if (v instanceof float[][]) {
      return makeImage((float[][]) v, w, h);
    }
    else if (v instanceof double[][]) {
      return makeImage((double[][]) v, w, h);
    }
    return null;
  }

  public static BufferedImage makeRGBImage(byte[] data, int c, int w, int h,
    boolean interleaved)
  {
    int cc = Math.min(c, 4); // throw away channels beyond 4
    int[] buf = new int[data.length / c];
    int nBits = (cc - 1) * 8;

    for (int i=0; i<buf.length; i++) {
      for (int q=0; q<cc; q++) {
        if (interleaved) {
          buf[i] |= ((data[i*c + q] & 0xff) << (nBits - q*8));
        }
        else {
          buf[i] |= ((data[q*buf.length + i] & 0xff) << (nBits - q*8));
        }
      }
    }

    DataBuffer buffer = new DataBufferInt(buf, buf.length);
    return constructImage(cc, DataBuffer.TYPE_INT, w, h, false, false, buffer);
  }

  public static BufferedImage makeRGBImage(byte[][] data, int w, int h) {
    int[] buf = new int[data[0].length];
    int nBits = (data.length - 1) * 8;

    for (int i=0; i<buf.length; i++) {
      for (int q=0; q<data.length; q++) {
        buf[i] |= ((data[q][i] & 0xff) << (nBits - q*8));
      }
    }

    DataBuffer buffer = new DataBufferInt(buf, buf.length);
    return constructImage(data.length, DataBuffer.TYPE_INT, w, h, false,
      false, buffer);
  }

  // -- Image construction - miscellaneous --

  /**
   * Creates a blank image with the given dimensions and transfer type.
   * @param w Width of image plane.
   * @param h Height of image plane.
   * @param c Number of channels.
   * @param type One of the following types:<ul>
   *   <li>FormatTools.INT8 <b>** unsupported for now **</b></li>
   *   <li>FormatTools.UINT8</li>
   *   <li>FormatTools.INT16</li>
   *   <li>FormatTools.UINT16</li>
   *   <li>FormatTools.INT32</li>
   *   <li>FormatTools.UINT32 <b>** unsupported for now **</b></li>
   *   <li>FormatTools.FLOAT</li>
   *   <li>FormatTools.DOUBLE</li>
   * </ul>
   */
  public static BufferedImage blankImage(int w, int h, int c, int type) {
    switch (type) {
      case FormatTools.INT8:
        return makeImage(new byte[c][w * h], w, h, true);
      case FormatTools.UINT8:
        return makeImage(new byte[c][w * h], w, h, false);
      case FormatTools.INT16:
        return makeImage(new short[c][w * h], w, h, true);
      case FormatTools.UINT16:
        return makeImage(new short[c][w * h], w, h, false);
      case FormatTools.INT32:
        return makeImage(new int[c][w * h], w, h, true);
      case FormatTools.UINT32:
        return makeImage(new int[c][w * h], w, h, false);
      case FormatTools.FLOAT:
        return makeImage(new float[c][w * h], w, h);
      case FormatTools.DOUBLE:
        return makeImage(new double[c][w * h], w, h);
    }
    return null;
  }

  /** Creates an image with the given DataBuffer. */
  public static BufferedImage constructImage(int c, int type, int w,
    int h, boolean interleaved, boolean banded, DataBuffer buffer)
  {
    return constructImage(c, type, w, h, interleaved, banded, buffer, null);
  }

  /** Creates an image with the given DataBuffer. */
  public static BufferedImage constructImage(int c, int type, int w,
    int h, boolean interleaved, boolean banded, DataBuffer buffer,
    ColorModel colorModel)
  {
    if (c > 4) {
      throw new IllegalArgumentException(
        "Cannot construct image with " + c + " channels");
    }
    if (colorModel == null || colorModel instanceof DirectColorModel) {
      colorModel = makeColorModel(c, type);
      if (colorModel == null) return null;
      if (buffer instanceof UnsignedIntBuffer) {
        try {
          colorModel = new UnsignedIntColorModel(32, type, c);
        }
        catch (IOException e) {
          return null;
        }
      }
    }

    SampleModel model;
    if (c > 2 && type == DataBuffer.TYPE_INT && buffer.getNumBanks() == 1 &&
      !(buffer instanceof UnsignedIntBuffer))
    {
      int[] bitMasks = new int[c];
      for (int i=0; i<c; i++) {
        bitMasks[i] = 0xff << ((c - i - 1) * 8);
      }
      model =
        new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, w, h, bitMasks);
    }
    else if (banded) model = new BandedSampleModel(type, w, h, c);
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

    BufferedImage b = null;

    if (c == 1 && type == DataBuffer.TYPE_BYTE &&
      !(buffer instanceof SignedByteBuffer))
    {
      if (colorModel instanceof IndexColorModel) {
        b = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED);
      }
      else {
        b = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
      }
      b.setData(raster);
    }
    else if (c == 1 && type == DataBuffer.TYPE_USHORT) {
      if (!(colorModel instanceof IndexColorModel)) {
        b = new BufferedImage(w, h, BufferedImage.TYPE_USHORT_GRAY);
        b.setData(raster);
      }
    }
    else if (c > 2 && type == DataBuffer.TYPE_INT && buffer.getNumBanks() == 1
      && !(buffer instanceof UnsignedIntBuffer))
    {
      if (c == 3) {
        b = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      }
      else if (c == 4) {
        b = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      }

      if (b != null) b.setData(raster);
    }

    if (b == null) b = new BufferedImage(colorModel, raster, false, null);

    return b;
  }

  /**
   * Creates an image from the given byte array, using the given
   * IFormatReader to retrieve additional information.
   */
  public static BufferedImage openImage(byte[] buf, IFormatReader r,
    int w, int h) throws FormatException, IOException
  {
    int pixelType = r.getPixelType();
    boolean little = r.isLittleEndian();
    boolean normal = r.isNormalized();
    int rgbChanCount = r.getRGBChannelCount();
    boolean interleaved = r.isInterleaved();
    boolean indexed = r.isIndexed();

    if (pixelType == FormatTools.FLOAT) {
      float[] f = (float[]) DataTools.makeDataArray(buf, 4, true, little);
      if (normal) f = DataTools.normalizeFloats(f);
      return makeImage(f, w, h, rgbChanCount, interleaved);
    }
    else if (pixelType == FormatTools.DOUBLE) {
      double[] d = (double[]) DataTools.makeDataArray(buf, 8, true, little);
      if (normal) d = DataTools.normalizeDoubles(d);
      return makeImage(d, w, h, rgbChanCount, interleaved);
    }

    boolean signed = FormatTools.isSigned(pixelType);
    ColorModel model = null;

    if (signed) {
      if (pixelType == FormatTools.INT8) {
        model = new SignedColorModel(8, DataBuffer.TYPE_BYTE, rgbChanCount);
      }
      else if (pixelType == FormatTools.INT16) {
        model = new SignedColorModel(16, DataBuffer.TYPE_SHORT, rgbChanCount);
      }
      else if (pixelType == FormatTools.INT32) {
        model = new SignedColorModel(32, DataBuffer.TYPE_INT, rgbChanCount);
      }
    }

    int bpp = FormatTools.getBytesPerPixel(pixelType);
    BufferedImage b = makeImage(buf, w, h, rgbChanCount,
      interleaved, bpp, false, little, signed);
    if (b == null) {
      throw new FormatException("Could not construct BufferedImage");
    }

    if (indexed && rgbChanCount == 1) {
      if (pixelType == FormatTools.UINT8 || pixelType == FormatTools.INT8) {
        byte[][] table = r.get8BitLookupTable(0);
        if (table != null && table.length > 0 && table[0] != null) {
          int len = table[0].length;
          byte[] dummy = table.length < 3 ? new byte[len] : null;
          byte[] red = table.length >= 1 ? table[0] : dummy;
          byte[] green = table.length >= 2 ? table[1] : dummy;
          byte[] blue = table.length >= 3 ? table[2] : dummy;
          model = new IndexColorModel(8, len, red, green, blue);
        }
      }
      else if (pixelType == FormatTools.UINT16 ||
        pixelType == FormatTools.INT16)
      {
        short[][] table = r.get16BitLookupTable(0);
        if (table != null && table.length > 0 && table[0] != null) {
          model = new Index16ColorModel(16, table[0].length, table,
            r.isLittleEndian());
        }
      }
    }

    if (model != null) {
      WritableRaster raster = Raster.createWritableRaster(b.getSampleModel(),
        b.getRaster().getDataBuffer(), null);
      b = new BufferedImage(model, raster, false, null);
    }

    return b;
  }

  // -- Data extraction --

  /**
   * Gets the image's pixel data as arrays of primitives, one per channel.
   * The returned type will be either byte[][], short[][], int[][], float[][]
   * or double[][], depending on the image's transfer type.
   */
  public static Object getPixels(BufferedImage image) {
    return getPixels(image, 0, 0, image.getWidth(), image.getHeight());
  }

  /**
   *
   * Gets the image's pixel data as arrays of primitives, one per channel.
   * The returned type will be either byte[][], short[][], int[][], float[][]
   * or double[][], depending on the image's transfer type.
   */
  public static Object getPixels(BufferedImage image, int x, int y,
    int w, int h)
  {
    WritableRaster raster = image.getRaster();
    return getPixels(raster, x, y, w, h);
  }

  /**
   * Gets the raster's pixel data as arrays of primitives, one per channel.
   * The returned type will be either byte[][], short[][], int[][], float[][]
   * or double[][], depending on the raster's transfer type.
   */
  public static Object getPixels(WritableRaster raster) {
    return getPixels(raster, 0, 0, raster.getWidth(), raster.getHeight());
  }

  /**
   * Gets the raster's pixel data as arrays of primitives, one per channel.
   * The returned type will be either byte[][], short[][], int[][], float[][]
   * or double[][], depending on the raster's transfer type.
   */
  public static Object getPixels(WritableRaster raster, int x, int y,
    int w, int h)
  {
    int tt = raster.getTransferType();
    if (tt == DataBuffer.TYPE_BYTE) return getBytes(raster, x, y, w, h);
    else if (tt == DataBuffer.TYPE_USHORT || tt == DataBuffer.TYPE_SHORT) {
      return getShorts(raster, x, y, w, h);
    }
    else if (tt == DataBuffer.TYPE_INT) return getInts(raster, x, y, w, h);
    else if (tt == DataBuffer.TYPE_FLOAT) return getFloats(raster, x, y, w, h);
    else if (tt == DataBuffer.TYPE_DOUBLE) {
      return getDoubles(raster, x, y, w, h);
    }
    else return null;
  }

  /** Extracts pixel data as arrays of unsigned bytes, one per channel. */
  public static byte[][] getBytes(BufferedImage image) {
    WritableRaster r = image.getRaster();
    return getBytes(r);
  }

  /** Extracts pixel data as arrays of unsigned bytes, one per channel. */
  public static byte[][] getBytes(WritableRaster r) {
    return getBytes(r, 0, 0, r.getWidth(), r.getHeight());
  }

  /** Extracts pixel data as arrays of unsigned bytes, one per channel. */
  public static byte[][] getBytes(WritableRaster r, int x, int y, int w, int h)
  {
    if (canUseBankDataDirectly(r, DataBuffer.TYPE_BYTE, DataBufferByte.class) &&
      x == 0 && y == 0 && w == r.getWidth() && h == r.getHeight())
    {
      return ((DataBufferByte) r.getDataBuffer()).getBankData();
    }
    int c = r.getNumBands();
    byte[][] samples = new byte[c][w * h];
    int[] buf = new int[w * h];
    for (int i=0; i<c; i++) {
      r.getSamples(x, y, w, h, i, buf);
      for (int j=0; j<buf.length; j++) samples[i][j] = (byte) buf[j];
    }
    return samples;
  }

  /** Extracts pixel data as arrays of unsigned shorts, one per channel. */
  public static short[][] getShorts(BufferedImage image) {
    WritableRaster r = image.getRaster();
    return getShorts(r);
  }

  /** Extracts pixel data as arrays of unsigned shorts, one per channel. */
  public static short[][] getShorts(WritableRaster r) {
    return getShorts(r, 0, 0, r.getWidth(), r.getHeight());
  }

  /** Extracts pixel data as arrays of unsigned shorts, one per channel. */
  public static short[][] getShorts(WritableRaster r, int x, int y,
    int w, int h)
  {
    if (canUseBankDataDirectly(r,
      DataBuffer.TYPE_USHORT, DataBufferUShort.class) && x == 0 && y == 0 &&
      w == r.getWidth() && h == r.getHeight())
    {
      return ((DataBufferUShort) r.getDataBuffer()).getBankData();
    }
    int c = r.getNumBands();
    short[][] samples = new short[c][w * h];
    int[] buf = new int[w * h];
    for (int i=0; i<c; i++) {
      r.getSamples(x, y, w, h, i, buf);
      for (int j=0; j<buf.length; j++) samples[i][j] = (short) buf[j];
    }
    return samples;
  }

  /** Extracts pixel data as arrays of signed integers, one per channel. */
  public static int[][] getInts(BufferedImage image) {
    WritableRaster r = image.getRaster();
    return getInts(r);
  }

  /** Extracts pixel data as arrays of signed integers, one per channel. */
  public static int[][] getInts(WritableRaster r) {
    return getInts(r, 0, 0, r.getWidth(), r.getHeight());
  }

  /** Extracts pixel data as arrays of signed integers, one per channel. */
  public static int[][] getInts(WritableRaster r, int x, int y, int w, int h) {
    if (canUseBankDataDirectly(r, DataBuffer.TYPE_INT, DataBufferInt.class) &&
      x == 0 && y == 0 && w == r.getWidth() && h == r.getHeight())
    {
      return ((DataBufferInt) r.getDataBuffer()).getBankData();
    }
    // NB: an order of magnitude faster than the naive makeType solution
    int c = r.getNumBands();
    int[][] samples = new int[c][w * h];
    for (int i=0; i<c; i++) r.getSamples(x, y, w, h, i, samples[i]);
    return samples;
  }

  /** Extracts pixel data as arrays of floats, one per channel. */
  public static float[][] getFloats(BufferedImage image) {
    WritableRaster r = image.getRaster();
    return getFloats(r);
  }

  /** Extracts pixel data as arrays of floats, one per channel. */
  public static float[][] getFloats(WritableRaster r) {
    return getFloats(r, 0, 0, r.getWidth(), r.getHeight());
  }

  /** Extracts pixel data as arrays of floats, one per channel. */
  public static float[][] getFloats(WritableRaster r, int x, int y,
    int w, int h)
  {
    if (canUseBankDataDirectly(r, DataBuffer.TYPE_FLOAT,
      DataBufferFloat.class) && x == 0 && y == 0 && w == r.getWidth() &&
      h == r.getHeight())
    {
      return ((DataBufferFloat) r.getDataBuffer()).getBankData();
    }
    // NB: an order of magnitude faster than the naive makeType solution
    int c = r.getNumBands();
    float[][] samples = new float[c][w * h];
    for (int i=0; i<c; i++) r.getSamples(x, y, w, h, i, samples[i]);
    return samples;
  }

  /** Extracts pixel data as arrays of doubles, one per channel. */
  public static double[][] getDoubles(BufferedImage image) {
    WritableRaster r = image.getRaster();
    return getDoubles(r);
  }

  /** Extracts pixel data as arrays of doubles, one per channel. */
  public static double[][] getDoubles(WritableRaster r) {
    return getDoubles(r, 0, 0, r.getWidth(), r.getHeight());
  }

  /** Extracts pixel data as arrays of doubles, one per channel. */
  public static double[][] getDoubles(WritableRaster r, int x, int y,
    int w, int h)
  {
    if (canUseBankDataDirectly(r,
      DataBuffer.TYPE_DOUBLE, DataBufferDouble.class) && x == 0 && y == 0 &&
      w == r.getWidth() && h == r.getHeight())
    {
      return ((DataBufferDouble) r.getDataBuffer()).getBankData();
    }
    // NB: an order of magnitude faster than the naive makeType solution
    int c = r.getNumBands();
    double[][] samples = new double[c][w * h];
    for (int i=0; i<c; i++) r.getSamples(x, y, w, h, i, samples[i]);
    return samples;
  }

  /**
   * Whether we can return the data buffer's bank data
   * without performing any copy or conversion operations.
   */
  private static boolean canUseBankDataDirectly(WritableRaster r,
    int transferType, Class<? extends DataBuffer> dataBufferClass)
  {
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
    return getPixelBytes(img, little, 0, 0, img.getWidth(), img.getHeight());
  }

  /**
   * Return a 2D array of bytes representing the image.  If the transfer type
   * is something other than DataBuffer.TYPE_BYTE, then each pixel value is
   * converted to the appropriate number of bytes.  In other words, if we
   * are given an image with 16-bit data, each channel of the resulting array
   * will have width * height * 2 bytes.
   */
  public static byte[][] getPixelBytes(WritableRaster r, boolean little) {
    return getPixelBytes(r, little, 0, 0, r.getWidth(), r.getHeight());
  }

  /**
   * Return a 2D array of bytes representing the image.  If the transfer type
   * is something other than DataBuffer.TYPE_BYTE, then each pixel value is
   * converted to the appropriate number of bytes.  In other words, if we
   * are given an image with 16-bit data, each channel of the resulting array
   * will have width * height * 2 bytes.
   */
  public static byte[][] getPixelBytes(BufferedImage img, boolean little,
    int x, int y, int w, int h)
  {
    Object pixels = getPixels(img, x, y, w, h);
    int imageType = img.getType();
    byte[][] pixelBytes = null;

    if (pixels instanceof byte[][]) {
      pixelBytes = (byte[][]) pixels;
    }
    else if (pixels instanceof short[][]) {
      short[][] s = (short[][]) pixels;
      pixelBytes = new byte[s.length][s[0].length * 2];
      for (int i=0; i<pixelBytes.length; i++) {
        for (int j=0; j<s[0].length; j++) {
          DataTools.unpackBytes(s[i][j], pixelBytes[i], j * 2, 2, little);
        }
      }
    }
    else if (pixels instanceof int[][]) {
      int[][] in = (int[][]) pixels;

      if (imageType == BufferedImage.TYPE_INT_RGB ||
        imageType == BufferedImage.TYPE_INT_BGR ||
        imageType == BufferedImage.TYPE_INT_ARGB)
      {
        pixelBytes = new byte[in.length][in[0].length];
        for (int c=0; c<in.length; c++) {
          for (int i=0; i<in[0].length; i++) {
            if (imageType != BufferedImage.TYPE_INT_BGR) {
              pixelBytes[c][i] = (byte) (in[c][i] & 0xff);
            }
            else {
              pixelBytes[in.length - c - 1][i] = (byte) (in[c][i] & 0xff);
            }
          }
        }
      }
      else {
        pixelBytes = new byte[in.length][in[0].length * 4];
        for (int i=0; i<pixelBytes.length; i++) {
          for (int j=0; j<in[0].length; j++) {
            DataTools.unpackBytes(in[i][j], pixelBytes[i], j * 4, 4, little);
          }
        }
      }
    }
    else if (pixels instanceof float[][]) {
      float[][] in = (float[][]) pixels;
      pixelBytes = new byte[in.length][in[0].length * 4];
      for (int i=0; i<pixelBytes.length; i++) {
        for (int j=0; j<in[0].length; j++) {
          int v = Float.floatToIntBits(in[i][j]);
          DataTools.unpackBytes(v, pixelBytes[i], j * 4, 4, little);
        }
      }
    }
    else if (pixels instanceof double[][]) {
      double[][] in = (double[][]) pixels;
      pixelBytes = new byte[in.length][in[0].length * 8];
      for (int i=0; i<pixelBytes.length; i++) {
        for (int j=0; j<in[0].length; j++) {
          long v = Double.doubleToLongBits(in[i][j]);
          DataTools.unpackBytes(v, pixelBytes[i], j * 8, 8, little);
        }
      }
    }

    return pixelBytes;
  }

  /**
   * Return a 2D array of bytes representing the image.  If the transfer type
   * is something other than DataBuffer.TYPE_BYTE, then each pixel value is
   * converted to the appropriate number of bytes.  In other words, if we
   * are given an image with 16-bit data, each channel of the resulting array
   * will have width * height * 2 bytes.
   */
  public static byte[][] getPixelBytes(WritableRaster r, boolean little,
    int x, int y, int w, int h)
  {
    Object pixels = getPixels(r);
    byte[][] pixelBytes = null;
    int bpp = 0;

    if (pixels instanceof byte[][]) {
      pixelBytes = (byte[][]) pixels;
      bpp = 1;
    }
    else if (pixels instanceof short[][]) {
      bpp = 2;
      short[][] s = (short[][]) pixels;
      pixelBytes = new byte[s.length][s[0].length * bpp];
      for (int i=0; i<pixelBytes.length; i++) {
        for (int j=0; j<s[0].length; j++) {
          DataTools.unpackBytes(s[i][j], pixelBytes[i], j * bpp, bpp, little);
        }
      }
    }
    else if (pixels instanceof int[][]) {
      bpp = 4;
      int[][] in = (int[][]) pixels;

      pixelBytes = new byte[in.length][in[0].length * bpp];
      for (int i=0; i<pixelBytes.length; i++) {
        for (int j=0; j<in[0].length; j++) {
          DataTools.unpackBytes(in[i][j], pixelBytes[i], j * bpp, bpp, little);
        }
      }
    }
    else if (pixels instanceof float[][]) {
      bpp = 4;
      float[][] in = (float[][]) pixels;
      pixelBytes = new byte[in.length][in[0].length * bpp];
      for (int i=0; i<pixelBytes.length; i++) {
        for (int j=0; j<in[0].length; j++) {
          int v = Float.floatToIntBits(in[i][j]);
          DataTools.unpackBytes(v, pixelBytes[i], j * bpp, bpp, little);
        }
      }
    }
    else if (pixels instanceof double[][]) {
      bpp = 8;
      double[][] in = (double[][]) pixels;
      pixelBytes = new byte[in.length][in[0].length * bpp];
      for (int i=0; i<pixelBytes.length; i++) {
        for (int j=0; j<in[0].length; j++) {
          long v = Double.doubleToLongBits(in[i][j]);
          DataTools.unpackBytes(v, pixelBytes[i], j * bpp, bpp, little);
        }
      }
    }

    if (x == 0 && y == 0 && w == r.getWidth() && h == r.getHeight()) {
      return pixelBytes;
    }

    byte[][] croppedBytes = new byte[pixelBytes.length][w * h * bpp];
    for (int c=0; c<croppedBytes.length; c++) {
      for (int row=0; row<h; row++) {
        int src = (row + y) * r.getWidth() * bpp + x * bpp;
        int dest = row * w * bpp;
        System.arraycopy(pixelBytes[c], src, croppedBytes[c], dest, w * bpp);
      }
    }
    return croppedBytes;
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
    final Raster raster = image.getRaster();
    if (raster == null) return -1;
    final DataBuffer buffer = raster.getDataBuffer();
    if (buffer == null) return -1;

    if (buffer instanceof SignedByteBuffer) {
      return FormatTools.INT8;
    }
    else if (buffer instanceof SignedShortBuffer) {
      return FormatTools.INT16;
    }
    else if (buffer instanceof UnsignedIntBuffer) {
      return FormatTools.UINT32;
    }

    int type = buffer.getDataType();
    int imageType = image.getType();
    switch (type) {
      case DataBuffer.TYPE_BYTE:
        return FormatTools.UINT8;
      case DataBuffer.TYPE_DOUBLE:
        return FormatTools.DOUBLE;
      case DataBuffer.TYPE_FLOAT:
        return FormatTools.FLOAT;
      case DataBuffer.TYPE_INT:
        if (imageType == BufferedImage.TYPE_INT_RGB ||
          imageType == BufferedImage.TYPE_INT_BGR ||
          imageType == BufferedImage.TYPE_INT_ARGB)
        {
          return FormatTools.UINT8;
        }
        if (buffer instanceof UnsignedIntBuffer) {
          return FormatTools.UINT32;
        }
        return FormatTools.INT32;
      case DataBuffer.TYPE_SHORT:
        return FormatTools.INT16;
      case DataBuffer.TYPE_USHORT:
        if (imageType == BufferedImage.TYPE_USHORT_555_RGB ||
          imageType == BufferedImage.TYPE_USHORT_565_RGB)
        {
          return FormatTools.UINT8;
        }
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

  /**
   * Converts a java.awt.image.RenderedImage into a
   * java.awt.image.BufferedImage.
   *
   * This code was adapted from
   * <a href="http://www.jguru.com/faq/view.jsp?EID=114602">a jGuru post</a>.
   */
  public static BufferedImage convertRenderedImage(RenderedImage img) {
    if (img instanceof BufferedImage) return (BufferedImage) img;
    ColorModel cm = img.getColorModel();
    int width = img.getWidth();
    int height = img.getHeight();
    WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
    Hashtable<String, Object> properties = new Hashtable<String, Object>();
    String[] keys = img.getPropertyNames();
    if (keys != null) {
      for (int i=0; i<keys.length; i++) {
        properties.put(keys[i], img.getProperty(keys[i]));
      }
    }
    BufferedImage result = new BufferedImage(cm,
      raster, isAlphaPremultiplied, properties);
    img.copyData(raster);
    return result;
  }

  /** Get the bytes from an image, merging the channels as necessary. */
  public static byte[] getBytes(BufferedImage img, boolean separated) {
    byte[][] p = getBytes(img);
    if (separated || p.length == 1) return p[0];

    byte[] rtn = new byte[p.length * p[0].length];
    for (int i=0; i<p.length; i++) {
      System.arraycopy(p[i], 0, rtn, i * p[0].length, p[i].length);
    }
    return rtn;
  }

  /**
   * Converts the given BufferedImage into an image with unsigned pixel data.
   */
  public static BufferedImage makeUnsigned(BufferedImage img) {
    if (img == null) return null;
    int pixelType = getPixelType(img);
    boolean signed = FormatTools.isSigned(pixelType);
    boolean fp = FormatTools.isFloatingPoint(pixelType);
    if (!signed || fp) return img;

    int bpp = FormatTools.getBytesPerPixel(pixelType);

    byte[][] pix = getPixelBytes(img, false);
    return makeImage(pix, img.getWidth(), img.getHeight(),
      bpp, fp, false, false);
  }

  // -- Image manipulation --

  /** Returns a subimage of the specified image. */
  public static BufferedImage getSubimage(BufferedImage image,
    boolean littleEndian, int x, int y, int w, int h)
  {
    int pixelType = getPixelType(image);
    byte[][] pix = getPixelBytes(image, littleEndian, x, y, w, h);
    return makeImage(pix, w, h, FormatTools.getBytesPerPixel(pixelType),
      FormatTools.isFloatingPoint(pixelType), littleEndian,
      FormatTools.isSigned(pixelType));
  }

  /** Splits the given multi-channel image into single-channel images. */
  public static BufferedImage[] splitChannels(BufferedImage image) {
    int w = image.getWidth(), h = image.getHeight();
    int c = image.getRaster().getNumBands();
    if (c == 1) return new BufferedImage[] {image};
    BufferedImage[] results = new BufferedImage[c];

    Object o = getPixels(image);

    int pixelType = getPixelType(image);
    boolean signed = FormatTools.isSigned(pixelType);
    if (o instanceof byte[][]) {
      byte[][] pix = (byte[][]) o;
      for (int i=0; i<c; i++) results[i] = makeImage(pix[i], w, h, signed);
    }
    else if (o instanceof short[][]) {
      short[][] pix = (short[][]) o;
      for (int i=0; i<c; i++) results[i] = makeImage(pix[i], w, h, signed);
    }
    else if (o instanceof int[][]) {
      int[][] pix = (int[][]) o;
      for (int i=0; i<c; i++) results[i] = makeImage(pix[i], w, h, signed);
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
    int pixelType = getPixelType(images[0]);
    boolean signed = FormatTools.isSigned(pixelType);
    if (type == DataBuffer.TYPE_BYTE) {
      byte[][] pix = new byte[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        byte[][] b = (byte[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      while (ndx < pix.length) pix[ndx++] = new byte[w * h]; // blank channel
      return makeImage(pix, w, h, signed);
    }
    if (type == DataBuffer.TYPE_USHORT || type == DataBuffer.TYPE_SHORT) {
      short[][] pix = new short[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        short[][] b = (short[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      while (ndx < pix.length) pix[ndx++] = new short[w * h]; // blank channel
      return makeImage(pix, w, h, signed);
    }
    if (type == DataBuffer.TYPE_INT) {
      int[][] pix = new int[c][];
      int ndx = 0;
      for (int i=0; i<list.length; i++) {
        int[][] b = (int[][]) list[i];
        for (int j=0; j<b.length; j++) pix[ndx++] = b[j];
      }
      while (ndx < pix.length) pix[ndx++] = new int[w * h]; // blank channel
      return makeImage(pix, w, h, signed);
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
      return makeImage(data, width, height, false);
    }

    boolean needsPadding = img.getWidth() != width || img.getHeight() != height;

    if (needsPadding) {
      Object pixels = getPixels(img);
      int pixelType = getPixelType(img);
      boolean signed = FormatTools.isSigned(pixelType);

      if (pixels instanceof byte[][]) {
        byte[][] b = (byte[][]) pixels;
        byte[][] newBytes = new byte[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newBytes[i] = ImageTools.padImage(b[i],
            false, 1, img.getWidth(), width, height);
        }
        return makeImage(newBytes, width, height, signed);
      }
      else if (pixels instanceof short[][]) {
        short[][] b = (short[][]) pixels;
        short[][] newShorts = new short[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newShorts[i] =
            ImageTools.padImage(b[i], false, 1, img.getWidth(), width, height);
        }
        return makeImage(newShorts, width, height, signed);
      }
      else if (pixels instanceof int[][]) {
        int[][] b = (int[][]) pixels;
        int[][] newInts = new int[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newInts[i] = ImageTools.padImage(b[i],
            false, 1, img.getWidth(), width, height);
        }
        return makeImage(newInts, width, height, signed);
      }
      else if (pixels instanceof float[][]) {
        float[][] b = (float[][]) pixels;
        float[][] newFloats = new float[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newFloats[i] =
            ImageTools.padImage(b[i], false, 1, img.getWidth(), width, height);
        }
        return makeImage(newFloats, width, height);
      }
      else if (pixels instanceof double[][]) {
        double[][] b = (double[][]) pixels;
        double[][] newDoubles = new double[b.length][width * height];
        for (int i=0; i<b.length; i++) {
          newDoubles[i] =
            ImageTools.padImage(b[i], false, 1, img.getWidth(), width, height);
        }
        return makeImage(newDoubles, width, height);
      }
      return null;
    }
    return img;
  }

  /** Perform autoscaling on the given BufferedImage. */
  public static BufferedImage autoscale(BufferedImage img) {
    byte[][] pixels = getPixelBytes(img, true);
    double min = Double.MAX_VALUE;
    double max = 0.0;
    int bits = pixels[0].length / (img.getWidth() * img.getHeight()) * 8;
    for (int i=0; i<pixels.length; i++) {
      Double[] mm = ImageTools.scanData(pixels[0], bits, true);
      double tmin = mm[0].doubleValue();
      double tmax = mm[1].doubleValue();
      if (tmin < min) min = tmin;
      if (tmax > max) max = tmax;
    }

    return autoscale(img, (int) min, (int) max);
  }

  /**
   * Perform autoscaling on the given BufferedImage;
   * map min to 0 and max to 255.
   * If the BufferedImage has 8 bit data, then nothing happens.
   */
  public static BufferedImage autoscale(BufferedImage img, int min, int max) {
    Object pixels = getPixels(img);
    int pixelType = getPixelType(img);
    boolean signed = FormatTools.isSigned(pixelType);

    if (pixels instanceof byte[][]) return img;
    else if (pixels instanceof short[][]) {
      short[][] shorts = (short[][]) pixels;
      byte[][] out = new byte[shorts.length][shorts[0].length];

      for (int i=0; i<out.length; i++) {
        for (int j=0; j<out[i].length; j++) {
          if (shorts[i][j] < 0) shorts[i][j] += 32767;

          int diff = max - min;
          float dist = (float) (shorts[i][j] - min) / diff;

          if (shorts[i][j] >= max) out[i][j] = (byte) 255;
          else if (shorts[i][j] <= min) out[i][j] = 0;
          else out[i][j] = (byte) (dist * 256);
        }
      }

      return makeImage(out, img.getWidth(), img.getHeight(), signed);
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

      return makeImage(out, img.getWidth(), img.getHeight(), signed);
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

      return makeImage(out, img.getWidth(), img.getHeight(), signed);
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

      return makeImage(out, img.getWidth(), img.getHeight(), signed);
    }
    return img;
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

    int pixelType = getPixelType(source);

    BufferedImage result = null;
    ColorModel sourceModel = source.getColorModel();
    if ((sourceModel instanceof Index16ColorModel) ||
      (sourceModel instanceof IndexColorModel) ||
      (sourceModel instanceof SignedColorModel) ||
      FormatTools.isFloatingPoint(pixelType))
    {
      DataBuffer buffer = source.getData().getDataBuffer();
      WritableRaster raster = Raster.createWritableRaster(
        source.getSampleModel(), buffer, null);

      ColorModel model = makeColorModel(1, buffer.getDataType());
      if (sourceModel instanceof SignedColorModel) {
        model = sourceModel;
      }
      source = new BufferedImage(model, raster, false, null);

      Image scaled =
        scaleAWT(source, width, height, Image.SCALE_AREA_AVERAGING);
      result = makeBuffered(scaled, sourceModel);

      raster = Raster.createWritableRaster(result.getSampleModel(),
        result.getData().getDataBuffer(), null);
      result = new BufferedImage(sourceModel, raster, false, null);
    }
    else {
      if (FormatTools.isSigned(pixelType)) {
        source = makeUnsigned(source);
        sourceModel = null;
      }
      Image scaled =
        scaleAWT(source, width, height, Image.SCALE_AREA_AVERAGING);
      result = makeBuffered(scaled, sourceModel);
    }
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
    if (cm == null) return makeBuffered(image);

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
  public static ColorSpace makeColorSpace(int c) {
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
    return TwoChannelColorSpace.getInstance(type);
  }

  /** Gets a color model for the given number of color components. */
  public static ColorModel makeColorModel(int c, int dataType) {
    ColorSpace cs = makeColorSpace(c);
    return cs == null ? null : new ComponentColorModel(cs,
      c == 4, false, Transparency.TRANSLUCENT, dataType);
  }

  // -- Indexed color conversion --

  /** Converts an indexed color BufferedImage to an RGB BufferedImage. */
  public static BufferedImage indexedToRGB(BufferedImage img, boolean le) {
    byte[][] indices = getPixelBytes(img, le);
    if (indices.length > 1) return img;

    int pixelType = getPixelType(img);
    boolean signed = FormatTools.isSigned(pixelType);
    if (pixelType == FormatTools.UINT8) {
      IndexColorModel model = (IndexColorModel) img.getColorModel();
      byte[][] b = new byte[3][indices[0].length];
      for (int i=0; i<indices[0].length; i++) {
        b[0][i] = (byte) (model.getRed(indices[0][i] & 0xff) & 0xff);
        b[1][i] = (byte) (model.getGreen(indices[0][i] & 0xff) & 0xff);
        b[2][i] = (byte) (model.getBlue(indices[0][i] & 0xff) & 0xff);
      }
      return makeImage(b, img.getWidth(), img.getHeight(), signed);
    }
    else if (pixelType == FormatTools.UINT16) {
      Index16ColorModel model = (Index16ColorModel) img.getColorModel();
      short[][] s = new short[3][indices[0].length / 2];
      for (int i=0; i<s[0].length; i++) {
        int ndx = DataTools.bytesToInt(indices[0], i*2, 2, le) & 0xffff;
        s[0][i] = (short) (model.getRed(ndx) & 0xffff);
        s[1][i] = (short) (model.getRed(ndx) & 0xffff);
        s[2][i] = (short) (model.getRed(ndx) & 0xffff);
      }
      return makeImage(s, img.getWidth(), img.getHeight(), signed);
    }
    return null;
  }

  /** Converts an IndexColorModel to a 2D byte array. */
  public static byte[][] get8BitLookupTable(ColorModel model) {
    if (!(model instanceof IndexColorModel)) return null;
    IndexColorModel m = (IndexColorModel) model;
    byte[][] lut = new byte[3][m.getMapSize()];
    m.getReds(lut[0]);
    m.getGreens(lut[1]);
    m.getBlues(lut[2]);
    return lut;
  }

  /** Convers an Index16ColorModel to a 2D short array. */
  public static short[][] getLookupTable(ColorModel model) {
    if (!(model instanceof Index16ColorModel)) return null;
    Index16ColorModel m = (Index16ColorModel) model;
    short[][] lut = new short[3][];
    lut[0] = m.getReds();
    lut[1] = m.getGreens();
    lut[2] = m.getBlues();
    return lut;
  }

}

//
// ImageTools.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;

/**
 * A utility class with convenience methods for manipulating images.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class ImageTools {

  // -- Constants --

  /** ImageObserver for working with AWT images. */
  protected static final Component OBS = new Container();


  // -- Image utilities --

  /**
   * Gets the width and height of the given AWT image,
   * waiting for it to finish loading if necessary.
   */
  public static Dimension getSize(Image image) {
    MediaTracker tracker = new MediaTracker(OBS);
    tracker.addImage(image, 0);
    try { tracker.waitForID(0); }
    catch (InterruptedException exc) { exc.printStackTrace(); }
    return new Dimension(image.getWidth(OBS), image.getHeight(OBS));
  }


  // -- Image construction --

  /**
   * Creates a buffered image from the given AWT image object.
   * If the AWT image is already a buffered image, no new object is created.
   */
  public static BufferedImage makeImage(Image image) {
    if (image instanceof BufferedImage) return (BufferedImage) image;
    Dimension dim = getSize(image);
    int w = dim.width, h = dim.height;
    BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics g = result.createGraphics();
    g.drawImage(image, w, h, OBS);
    g.dispose();
    return result;
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

//
// UnsignedIntColorModel.java
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

package loci.formats;

import java.awt.image.*;
import java.io.IOException;

/**
 * ColorModel that handles unsigned 32 bit data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/UnsignedIntColorModel.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/UnsignedIntColorModel.java">SVN</a></dd></dl>
 */
public class UnsignedIntColorModel extends ColorModel {

  // -- Fields --

  private int pixelBits;
  private int nChannels;
  private ComponentColorModel helper;

  // -- Constructors --

  public UnsignedIntColorModel(int pixelBits, int dataType, int nChannels)
    throws IOException
  {
    super(pixelBits, makeBitArray(nChannels, pixelBits),
      AWTImageTools.makeColorSpace(nChannels), nChannels == 4, false,
      ColorModel.TRANSLUCENT, dataType);

    helper = new ComponentColorModel(AWTImageTools.makeColorSpace(nChannels),
      nChannels == 4, false, ColorModel.TRANSLUCENT, dataType);

    this.pixelBits = pixelBits;
    this.nChannels = nChannels;
  }

  // -- ColorModel API methods --

  /* @see java.awt.image.ColorModel#getDataElements(int, Object) */
  public synchronized Object getDataElements(int rgb, Object pixel) {
    return helper.getDataElements(rgb, pixel);
  }

  /* @see java.awt.image.ColorModel#isCompatibleRaster(Raster) */
  public boolean isCompatibleRaster(Raster raster) {
    return raster.getNumBands() == getNumComponents() &&
      raster.getTransferType() == getTransferType();
  }

  /* @see java.awt.image.ColorModel#createCompatibleWritableRaster(int, int) */
  public WritableRaster createCompatibleWritableRaster(int w, int h) {
    return helper.createCompatibleWritableRaster(w, h);
  }

  /* @see java.awt.image.ColorModel#getAlpha(int) */
  public int getAlpha(int pixel) {
    return (int) (Math.pow(2, 32) - 1);
  }

  /* @see java.awt.image.ColorModel#getBlue(int) */
  public int getBlue(int pixel) {
    return getComponent(pixel, 3);
  }

  /* @see java.awt.image.ColorModel#getGreen(int) */
  public int getGreen(int pixel) {
    return getComponent(pixel, 2);
  }

  /* @see java.awt.image.ColorModel#getRed(int) */
  public int getRed(int pixel) {
    return getComponent(pixel, 1);
  }

  /* @see java.awt.image.ColorModel#getAlpha(Object) */
  public int getAlpha(Object data) {
    int max = (int) Math.pow(2, 32) - 1;
    if (data instanceof int[]) {
      int[] i = (int[]) data;
      if (i.length == 1) return getAlpha(i[0]);
      return getAlpha(i.length == 4 ? i[0] : max);
    }
    return max;
  }

  /* @see java.awt.image.ColorModel#getRed(Object) */
  public int getRed(Object data) {
    int max = (int) Math.pow(2, 32) - 1;
    if (data instanceof int[]) {
      int[] i = (int[]) data;
      if (i.length == 1) return getRed(i[0]);
      return getRed(i.length != 4 ? i[0] : i[1]);
    }
    return max;
  }

  /* @see java.awt.image.ColorModel#getGreen(Object) */
  public int getGreen(Object data) {
    int max = (int) Math.pow(2, 32) - 1;
    if (data instanceof int[]) {
      int[] i = (int[]) data;
      if (i.length == 1) return getGreen(i[0]);
      return getGreen(i.length != 4 ? i[1] : i[2]);
    }
    return max;
  }

  /* @see java.awt.image.ColorModel#getBlue(Object) */
  public int getBlue(Object data) {
    int max = (int) Math.pow(2, 32) - 1;
    if (data instanceof int[]) {
      int[] i = (int[]) data;
      if (i.length == 1) return getBlue(i[0]);
      return getBlue(i[i.length - 1]);
    }
    return max;
  }

  // -- Helper methods --

  private int getComponent(int pixel, int index) {
    long v = pixel & 0xffffffffL;
    if (nChannels == 1) {
      double f = v / (Math.pow(2, 32) - 1);
      return (int) (255 * f);
    }
    int shift = (nChannels - index) * 8;
    return (int) (v & (0xff << shift)) >> shift;
  }

  private static int[] makeBitArray(int nChannels, int nBits) {
    int[] bits = new int[nChannels];
    for (int i=0; i<bits.length; i++) {
      bits[i] = nBits;
    }
    return bits;
  }

}

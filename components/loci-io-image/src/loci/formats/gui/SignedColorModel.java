//
// SignedColorModel.java
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

package loci.formats.gui;

import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

/**
 * ColorModel that handles 8, 16 and 32 bits per channel signed data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/gui/SignedColorModel.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/gui/SignedColorModel.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class SignedColorModel extends ColorModel {

  // -- Fields --

  private int pixelBits;
  private int nChannels;
  private ComponentColorModel helper;

  private int max;

  // -- Constructors --

  public SignedColorModel(int pixelBits, int dataType, int nChannels)
    throws IOException
  {
    super(pixelBits, makeBitArray(nChannels, pixelBits),
      AWTImageTools.makeColorSpace(nChannels), nChannels == 4, false,
      ColorModel.TRANSLUCENT, dataType);

    int type = dataType;
    if (type == DataBuffer.TYPE_SHORT) {
      type = DataBuffer.TYPE_USHORT;
    }

    helper = new ComponentColorModel(AWTImageTools.makeColorSpace(nChannels),
      nChannels == 4, false, ColorModel.TRANSLUCENT, type);

    this.pixelBits = pixelBits;
    this.nChannels = nChannels;

    max = (int) Math.pow(2, pixelBits) - 1;
  }

  // -- ColorModel API methods --

  /* @see java.awt.image.ColorModel#getDataElements(int, Object) */
  public synchronized Object getDataElements(int rgb, Object pixel) {
    return helper.getDataElements(rgb, pixel);
  }

  /* @see java.awt.image.ColorModel#isCompatibleRaster(Raster) */
  public boolean isCompatibleRaster(Raster raster) {
    if (pixelBits == 16) {
      return raster.getTransferType() == DataBuffer.TYPE_SHORT;
    }
    return helper.isCompatibleRaster(raster);
  }

  /* @see java.awt.image.ColorModel#createCompatibleWritableRaster(int, int) */
  public WritableRaster createCompatibleWritableRaster(int w, int h) {
    if (pixelBits == 16) {
      int[] bandOffsets = new int[nChannels];
      for (int i=0; i<nChannels; i++) bandOffsets[i] = i;

      SampleModel m = new ComponentSampleModel(DataBuffer.TYPE_SHORT, w, h,
        nChannels, w * nChannels, bandOffsets);
      DataBuffer db = new DataBufferShort(w * h, nChannels);
      return Raster.createWritableRaster(m, db, null);
    }
    return helper.createCompatibleWritableRaster(w, h);
  }

  /* @see java.awt.image.ColorModel#getAlpha(int) */
  public int getAlpha(int pixel) {
    if (nChannels < 4) return 255;
    return rescale(pixel, max);
  }

  /* @see java.awt.image.ColorModel#getBlue(int) */
  public int getBlue(int pixel) {
    if (nChannels == 1) return getRed(pixel);
    return rescale(pixel, max);
  }

  /* @see java.awt.image.ColorModel#getGreen(int) */
  public int getGreen(int pixel) {
    if (nChannels == 1) return getRed(pixel);
    return rescale(pixel, max);
  }

  /* @see java.awt.image.ColorModel#getRed(int) */
  public int getRed(int pixel) {
    return rescale(pixel, max);
  }

  /* @see java.awt.image.ColorModel#getAlpha(Object) */
  public int getAlpha(Object data) {
    if (data instanceof byte[]) {
      byte[] b = (byte[]) data;
      if (b.length == 1) return getAlpha(b[0]);
      return rescale(b.length == 4 ? b[0] : max, max);
    }
    else if (data instanceof short[]) {
      short[] s = (short[]) data;
      if (s.length == 1) return getAlpha(s[0]);
      return rescale(s.length == 4 ? s[0] : max, max);
    }
    else if (data instanceof int[]) {
      int[] i = (int[]) data;
      if (i.length == 1) return getAlpha(i[0]);
      return rescale(i.length == 4 ? i[0] : max, max);
    }
    return 0;
  }

  /* @see java.awt.image.ColorModel#getRed(Object) */
  public int getRed(Object data) {
    if (data instanceof byte[]) {
      byte[] b = (byte[]) data;
      if (b.length == 1) return getRed(b[0]);
      return rescale(b.length != 4 ? b[0] : b[1]);
    }
    else if (data instanceof short[]) {
      short[] s = (short[]) data;
      if (s.length == 1) return getRed(s[0]);
      return rescale(s.length != 4 ? s[0] : s[1], max);
    }
    else if (data instanceof int[]) {
      int[] i = (int[]) data;
      if (i.length == 1) return getRed(i[0]);
      return rescale(i.length != 4 ? i[0] : i[1], max);
    }
    return 0;
  }

  /* @see java.awt.image.ColorModel#getGreen(Object) */
  public int getGreen(Object data) {
    if (data instanceof byte[]) {
      byte[] b = (byte[]) data;
      if (b.length == 1) return getGreen(b[0]);
      return rescale(b.length != 4 ? b[1] : b[2]);
    }
    else if (data instanceof short[]) {
      short[] s = (short[]) data;
      if (s.length == 1) return getGreen(s[0]);
      return rescale(s.length != 4 ? s[1] : s[2], max);
    }
    else if (data instanceof int[]) {
      int[] i = (int[]) data;
      if (i.length == 1) return getGreen(i[0]);
      return rescale(i.length != 4 ? i[1] : i[2], max);
    }
    return 0;
  }

  /* @see java.awt.image.ColorModel#getBlue(Object) */
  public int getBlue(Object data) {
    if (data instanceof byte[]) {
      byte[] b = (byte[]) data;
      if (b.length == 1) return getBlue(b[0]);
      return rescale(b.length > 2 ? b[b.length - 1] : 0);
    }
    else if (data instanceof short[]) {
      short[] s = (short[]) data;
      if (s.length == 1) return getBlue(s[0]);
      return rescale(s.length > 2 ? s[s.length - 1] : 0, max);
    }
    else if (data instanceof int[]) {
      int[] i = (int[]) data;
      if (i.length == 1) return getBlue(i[0]);
      return rescale(i.length > 2 ? i[i.length - 1] : 0, max);
    }
    return 0;
  }

  // -- Helper methods --

  private int rescale(int value, int max) {
    float v = (float) value / (float) max;
    v *= 255;
    return rescale((int) v);
  }

  private int rescale(int value) {
    if (value < 128) {
      value += 128; // [0, 127] -> [128, 255]
    }
    else {
      value -= 128; // [128, 255] -> [0, 127]
    }
    return value;
  }

  private static int[] makeBitArray(int nChannels, int nBits) {
    int[] bits = new int[nChannels];
    for (int i=0; i<bits.length; i++) {
      bits[i] = nBits;
    }
    return bits;
  }

}

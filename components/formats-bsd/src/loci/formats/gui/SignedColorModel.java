/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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
  @Override
  public synchronized Object getDataElements(int rgb, Object pixel) {
    return helper.getDataElements(rgb, pixel);
  }

  /* @see java.awt.image.ColorModel#isCompatibleRaster(Raster) */
  @Override
  public boolean isCompatibleRaster(Raster raster) {
    if (pixelBits == 16) {
      return raster.getTransferType() == DataBuffer.TYPE_SHORT;
    }
    return helper.isCompatibleRaster(raster);
  }

  /* @see java.awt.image.ColorModel#createCompatibleWritableRaster(int, int) */
  @Override
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
  @Override
  public int getAlpha(int pixel) {
    if (nChannels < 4) return 255;
    return rescale(pixel, max);
  }

  /* @see java.awt.image.ColorModel#getBlue(int) */
  @Override
  public int getBlue(int pixel) {
    if (nChannels == 1) return getRed(pixel);
    return rescale(pixel, max);
  }

  /* @see java.awt.image.ColorModel#getGreen(int) */
  @Override
  public int getGreen(int pixel) {
    if (nChannels == 1) return getRed(pixel);
    return rescale(pixel, max);
  }

  /* @see java.awt.image.ColorModel#getRed(int) */
  @Override
  public int getRed(int pixel) {
    return rescale(pixel, max);
  }

  /* @see java.awt.image.ColorModel#getAlpha(Object) */
  @Override
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
  @Override
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
  @Override
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
  @Override
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

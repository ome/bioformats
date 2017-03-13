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
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

import loci.common.DataTools;

/**
 * ColorModel that handles 16 bits per channel lookup tables.
 */
public class Index16ColorModel extends ColorModel {

  // -- Fields --

  /** Lookup tables. */
  private short[] redShort, greenShort, blueShort, alphaShort;

  private int pixelBits;

  // -- Constructors --

  public Index16ColorModel(int bits, int size, short[][] table,
    boolean littleEndian) throws IOException
  {
    super(bits);

    if (table == null) throw new IOException("LUT cannot be null");
    for (int i=0; i<table.length; i++) {
      if (table[i].length < size) {
        throw new IOException("LUT " + i + " too small");
      }
    }

    if (table.length > 0) redShort = table[0];
    if (table.length > 1) greenShort = table[1];
    if (table.length > 2) blueShort = table[2];
    if (table.length > 3) alphaShort = table[3];
    pixelBits = bits;
  }

  // -- Index16ColorModel API methods --

  /** Return the array of red color components. */
  public short[] getReds() {
    return redShort;
  }

  /** Return the array of green color components. */
  public short[] getGreens() {
    return greenShort;
  }

  /** Return the array of blue color components. */
  public short[] getBlues() {
    return blueShort;
  }

  /** Return the array of alpha color components. */
  public short[] getAlphas() {
    return alphaShort;
  }

  // -- ColorModel API methods --

  /* @see java.awt.image.ColorModel#getDataElements(int, Object) */
  @Override
  public synchronized Object getDataElements(int rgb, Object pixel) {
    int red = (rgb >> 16) & 0xff;
    int green = (rgb >> 8) & 0xff;
    int blue = rgb & 0xff;
    //int alpha = (rgb >>> 24);

    short[] p = pixel == null ? new short[3] : (short[]) pixel;
    p[0] = (short) red;
    p[1] = (short) green;
    p[2] = (short) blue;
    return p;
  }

  /* @see java.awt.image.ColorModel#isCompatibleRaster(Raster) */
  @Override
  public boolean isCompatibleRaster(Raster raster) {
    return raster.getNumBands() == 1;
  }

  /* @see java.awt.image.ColorModel#createCompatibleWritableRaster(int, int) */
  @Override
  public WritableRaster createCompatibleWritableRaster(int w, int h) {
    return Raster.createInterleavedRaster(DataBuffer.TYPE_USHORT,
      w, h, 1, null);
  }

  /* @see java.awt.image.ColorModel#getAlpha(int) */
  @Override
  public int getAlpha(int pixel) {
    if (alphaShort != null) {
      return (int) (((alphaShort[pixel] & 0xffff) / 65535.0) * 255.0);
    }
    return 0xff;
  }

  /* @see java.awt.image.ColorModel#getBlue(int) */
  @Override
  public int getBlue(int pixel) {
    if (blueShort == null) return 0;
    int blue = blueShort[pixel] & 0xffff;
    return (int) ((blue / 65535.0) * 255.0);
  }

  /* @see java.awt.image.ColorModel#getGreen(int) */
  @Override
  public int getGreen(int pixel) {
    if (greenShort == null) return 0;
    int green = greenShort[pixel] & 0xffff;
    return (int) ((green / 65535.0) * 255.0);
  }

  /* @see java.awt.image.ColorModel#getRed(int) */
  @Override
  public int getRed(int pixel) {
    if (redShort == null) return 0;
    int red = redShort[pixel] & 0xffff;
    return (int) ((red / 65535.0) * 255.0);
  }

}

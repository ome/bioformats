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

package loci.formats;

import java.io.IOException;

import loci.common.DataTools;

/**
 * Logic to automatically merge channels in a file.
 */
public class ChannelMerger extends ReaderWrapper {

  // -- Utility methods --

  /** Converts the given reader into a ChannelMerger, wrapping if needed. */
  public static ChannelMerger makeChannelMerger(IFormatReader r) {
    if (r instanceof ChannelMerger) return (ChannelMerger) r;
    return new ChannelMerger(r);
  }

  // -- Constructor --

  /** Constructs a ChannelMerger around a new image reader. */
  public ChannelMerger() { super(); }

  /** Constructs a ChannelMerger with the given reader. */
  public ChannelMerger(IFormatReader r) { super(r); }

  // -- ChannelMerger API methods --

  /** Determines whether the channels in the file can be merged. */
  public boolean canMerge() {
    int c = getSizeC();
    return c > 1 && c <= 4 && !reader.isRGB();
  }

  // -- ChannelMerger API methods --

  /**
   * Returns the image number in the original dataset that corresponds to the
   * given image number.
   *
   * @param no is an image number greater than or equal to 0 and less than
   *   getImageCount()
   * @return the corresponding image number in the original (unmerged) data.
   */
  public int getOriginalIndex(int no) throws FormatException, IOException {
    int imageCount = getImageCount();
    int originalCount = reader.getImageCount();

    if (imageCount == originalCount) return no;
    int[] coords = getZCTCoords(no);
    return reader.getIndex(coords[0], coords[1], coords[2]);
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#getImageCount() */
  @Override
  public int getImageCount() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    int no = reader.getImageCount();
    if (canMerge()) no /= getSizeC();
    return no;
  }

  /* @see IFormatReader#getDimensionOrder() */
  @Override
  public String getDimensionOrder() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    String order = reader.getDimensionOrder();
    if (canMerge()) {
      final StringBuilder sb = new StringBuilder(order);
      while (order.indexOf('C') != 2) {
        char pre = order.charAt(order.indexOf('C') - 1);
        sb.setCharAt(order.indexOf('C'), pre);
        sb.setCharAt(order.indexOf(pre), 'C');
        order = sb.toString();
      }
    }
    return order;
  }

  /* @see IFormatReader#isInterleaved() */
  @Override
  public boolean isInterleaved() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return canMerge() ? false : reader.isInterleaved();
  }

  /* @see IFormatReader#isRGB() */
  @Override
  public boolean isRGB() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return canMerge() || reader.isRGB();
  }

  /* @see IFormatReader#isIndexed() */
  @Override
  public boolean isIndexed() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return !canMerge() && reader.isIndexed();
  }

  /* @see IFormatReader#openBytes(int) */
  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return openBytes(no, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return openBytes(no , buf, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int ch = getRGBChannelCount();
    byte[] newBuffer = DataTools.allocate(w, h, ch, bpp);
    return openBytes(no, newBuffer, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (!canMerge()) return super.openBytes(no, buf, x, y, w, h);
    int sizeC = getSizeC();
    int[] nos = getZCTCoords(no);
    int z = nos[0], t = nos[2];
    for (int c=0; c<sizeC; c++) {
      byte[] b = reader.openBytes(reader.getIndex(z, c, t), x, y, w, h);
      System.arraycopy(b, 0, buf, c * b.length, b.length);
    }
    return buf;
  }

  @Override
  public int getIndex(int z, int c, int t, int moduloZ, int moduloC, int moduloT) {
      return FormatTools.getIndex(this, z, c, t, moduloZ, moduloC, moduloT);
  }

  @Override
  public int getIndex(int z, int c, int t) {
    return FormatTools.getIndex(this, z, c, t);
  }

  @Override
  public int[] getZCTCoords(int index) {
    return FormatTools.getZCTCoords(this, index);
  }

  @Override
  public int[] getZCTModuloCoords(int index) {
    return FormatTools.getZCTModuloCoords(this, index);
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#getNativeDataType() */
  @Override
  public Class<?> getNativeDataType() {
    return byte[].class;
  }

}

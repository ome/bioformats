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

package loci.formats;

import java.io.IOException;

import loci.common.DataTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

import ome.xml.meta.MetadataConverter;

/**
 * Logic to automatically separate the channels in a file.
 */
public class ChannelSeparator extends ReaderWrapper {

  // -- Utility methods --

  /** Converts the given reader into a ChannelSeparator, wrapping if needed. */
  public static ChannelSeparator makeChannelSeparator(IFormatReader r) {
    if (r instanceof ChannelSeparator) return (ChannelSeparator) r;
    return new ChannelSeparator(r);
  }

  // -- Fields --

  /** Last image opened. */
  private byte[] lastImage;

  /** Index of last image opened. */
  private int lastImageIndex = -1;

  /** Series of last image opened. */
  private int lastImageSeries = -1;

  /** X index of last image opened. */
  private int lastImageX = -1;

  /** Y index of last image opened. */
  private int lastImageY = -1;

  /** Width of last image opened. */
  private int lastImageWidth = -1;

  /** Height of last image opened. */
  private int lastImageHeight = -1;

  // -- Constructors --

  /** Constructs a ChannelSeparator around a new image reader. */
  public ChannelSeparator() { super(); }

  /** Constructs a ChannelSeparator with the given reader. */
  public ChannelSeparator(IFormatReader r) { super(r); }

  // -- ChannelSeparator API methods --

  /**
   * Returns the image number in the original dataset that corresponds to the
   * given image number.  For instance, if the original dataset was a single
   * RGB image and the given image number is 2, the return value will be 0.
   *
   * @param no is an image number greater than or equal to 0 and less than
   *   getImageCount()
   * @return the corresponding image number in the original (unseparated) data.
   */
  public int getOriginalIndex(int no) {
    int imageCount = getImageCount();
    int originalCount = reader.getImageCount();

    if (imageCount == originalCount) return no;
    int[] coords = getZCTCoords(no);
    coords[1] /= reader.getRGBChannelCount();
    return reader.getIndex(coords[0], coords[1], coords[2]);
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#getImageCount() */
  @Override
  public int getImageCount() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return (reader.isRGB() && !reader.isIndexed()) ?
      reader.getRGBChannelCount() * reader.getImageCount() :
      reader.getImageCount();
  }

  /* @see IFormatReader#getDimensionOrder() */
  @Override
  public String getDimensionOrder() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    String order = super.getDimensionOrder();
    if (reader.isRGB() && !reader.isIndexed()) {
      String newOrder = "XYC";
      if (order.indexOf('Z') > order.indexOf('T')) newOrder += "TZ";
      else newOrder += "ZT";
      return newOrder;
    }
    return order;
  }

  /* @see IFormatReader#isRGB() */
  @Override
  public boolean isRGB() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return isIndexed() && !isFalseColor() && getSizeC() > 1;
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
    return openBytes(no, buf, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    byte[] buf =
      DataTools.allocate(w, h, FormatTools.getBytesPerPixel(getPixelType()));
    return openBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    FormatTools.checkPlaneNumber(this, no);

    if (reader.isRGB() && !reader.isIndexed()) {
      int c = getSizeC() / reader.getEffectiveSizeC();
      int source = getOriginalIndex(no);
      int channel = no % c;
      int series = getCoreIndex();
      int bpp = FormatTools.getBytesPerPixel(getPixelType());

      if (source != lastImageIndex || series != lastImageSeries ||
        x != lastImageX || y != lastImageY || w != lastImageWidth ||
        h != lastImageHeight)
      {
        int strips = 1;

        // check how big the original image is; if it's larger than the
        // available memory, we will need to split it into strips

        Runtime rt = Runtime.getRuntime();
        long availableMemory = rt.freeMemory();
        long planeSize = DataTools.safeMultiply64(w, h, bpp, c);

        if (availableMemory < planeSize || planeSize > Integer.MAX_VALUE) {
          strips = (int) Math.sqrt(h);
        }

        int stripHeight = h / strips;
        int lastStripHeight = stripHeight + (h - (stripHeight * strips));
        byte[] strip = strips == 1 ? buf : new byte[stripHeight * w * bpp];
        for (int i=0; i<strips; i++) {
          lastImage = reader.openBytes(source, x, y + i * stripHeight, w,
            i == strips - 1 ? lastStripHeight : stripHeight);
          lastImageIndex = source;
          lastImageSeries = series;
          lastImageX = x;
          lastImageY = y + i * stripHeight;
          lastImageWidth = w;
          lastImageHeight = i == strips - 1 ? lastStripHeight : stripHeight;

          if (strips != 1 && lastStripHeight != stripHeight && i == strips - 1)
          {
            strip = new byte[lastStripHeight * w * bpp];
          }

          ImageTools.splitChannels(lastImage, strip, channel, c, bpp,
            false, isInterleaved(), strips == 1 ? w * h * bpp : strip.length);
          if (strips != 1) {
            System.arraycopy(strip, 0, buf, i * stripHeight * w * bpp,
              strip.length);
          }
        }
      }
      else {
        ImageTools.splitChannels(lastImage, buf, channel, c, bpp,
          false, isInterleaved(), w * h * bpp);
      }

      return buf;
    }
    return reader.openBytes(no, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);

    int source = getOriginalIndex(no);
    byte[] thumb = reader.openThumbBytes(source);

    int c = getSizeC() / reader.getEffectiveSizeC();
    int channel = no % c;
    int bpp = FormatTools.getBytesPerPixel(getPixelType());

    return ImageTools.splitChannels(thumb, channel, c, bpp, false, reader.isInterleaved());
  }

  /* @see IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      lastImage = null;
      lastImageIndex = -1;
      lastImageSeries = -1;
      lastImageX = -1;
      lastImageY = -1;
      lastImageWidth = -1;
      lastImageHeight = -1;
    }
  }

  @Override
  public int getIndex(int z, int c, int t) {
    return FormatTools.getIndex(this, z, c, t);
  }

  @Override
  public int getIndex(int z, int c, int t, int moduloZ, int moduloC, int moduloT) {
      return FormatTools.getIndex(this, z, c, t, moduloZ, moduloC, moduloT);
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

  /* @see IFormatHandler#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);

    // clear last image cache
    lastImage = null;
    lastImageIndex = -1;
    lastImageSeries = -1;
    lastImageX = -1;
    lastImageY = -1;
    lastImageWidth = -1;
    lastImageHeight = -1;

    MetadataStore store = getMetadataStore();
    boolean pixelsPopulated = false;
    if (store instanceof MetadataRetrieve) {
      MetadataRetrieve retrieve = (MetadataRetrieve) store;
      for (int s=0; s<getSeriesCount(); s++) {
        setSeries(s);
        int rgbChannels = getSizeC() / reader.getEffectiveSizeC();
        if (rgbChannels == 1) {
          continue;
        }
        for (int c=0; c<reader.getEffectiveSizeC(); c++) {
          int cIndex = c * rgbChannels;
          if (cIndex >= retrieve.getChannelCount(s)) {
            break;
          }
          if (!pixelsPopulated) {
            MetadataTools.populatePixelsOnly(store, this);
            pixelsPopulated = true;
          }
          for (int i=1; i<rgbChannels; i++) {
            MetadataConverter.convertChannelMetadata(retrieve, s, cIndex,
              store, s, cIndex + i, false);
          }
        }
      }
      setSeries(0);
    }
  }

}

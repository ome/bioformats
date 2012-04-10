//
// ChannelSeparator.java
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

import java.io.IOException;

import loci.common.DataTools;

/**
 * Logic to automatically separate the channels in a file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/ChannelSeparator.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/ChannelSeparator.java;hb=HEAD">Gitweb</a></dd></dl>
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
  public int getImageCount() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return (reader.isRGB() && !reader.isIndexed()) ?
      reader.getRGBChannelCount() * reader.getImageCount() :
      reader.getImageCount();
  }

  /* @see IFormatReader#getDimensionOrder() */
  public String getDimensionOrder() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    String order = super.getDimensionOrder();
    if (reader.isRGB() && !reader.isIndexed()) {
      String newOrder = "XYC";
      if (order.indexOf("Z") > order.indexOf("T")) newOrder += "TZ";
      else newOrder += "ZT";
      return newOrder;
    }
    return order;
  }

  /* @see IFormatReader#isRGB() */
  public boolean isRGB() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return isIndexed() && !isFalseColor() && getSizeC() > 1;
  }

  /* @see IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    return openBytes(no, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return openBytes(no, buf, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    byte[] buf =
      DataTools.allocate(w, h, FormatTools.getBytesPerPixel(getPixelType()));
    return openBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    FormatTools.checkPlaneNumber(this, no);

    if (reader.isRGB() && !reader.isIndexed()) {
      int c = getSizeC() / reader.getEffectiveSizeC();
      int source = getOriginalIndex(no);
      int channel = no % c;
      int series = getSeries();
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
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);

    int source = getOriginalIndex(no);
    byte[] thumb = reader.openThumbBytes(source);

    int c = getSizeC() / reader.getEffectiveSizeC();
    int channel = no % c;
    int bpp = FormatTools.getBytesPerPixel(getPixelType());

    return ImageTools.splitChannels(thumb, channel, c, bpp, false, false);
  }

  /* @see IFormatReader#close(boolean) */
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

  public int getIndex(int z, int c, int t) {
    return FormatTools.getIndex(this, z, c, t);
  }

  public int[] getZCTCoords(int index) {
    return FormatTools.getZCTCoords(this, index);
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#getNativeDataType() */
  public Class<?> getNativeDataType() {
    return byte[].class;
  }

  /* @see IFormatHandler#setId(String) */
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
  }

}

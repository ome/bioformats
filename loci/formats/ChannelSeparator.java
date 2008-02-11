//
// ChannelSeparator.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Logic to automatically separate the channels in a file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ChannelSeparator.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ChannelSeparator.java">SVN</a></dd></dl>
 */
public class ChannelSeparator extends ReaderWrapper {

  // -- Fields --

  /** Last image opened. */
  private byte[] lastImage;

  /** Index of last image opened. */
  private int lastImageIndex = -1;

  /** Series of last image opened. */
  private int lastImageSeries = -1;

  // -- Constructors --

  /** Constructs a ChannelSeparator around a new image reader. */
  public ChannelSeparator() { super(); }

  /** Constructs a ChannelSeparator with the given reader. */
  public ChannelSeparator(IFormatReader r) { super(r); }

  // -- IFormatReader API methods --

  /* @see IFormatReader#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);

    // clear last image cache
    lastImage = null;
    lastImageIndex = -1;
    lastImageSeries = -1;
  }

  /* @see IFormatReader#setId(id, force) */
  public void setId(String id, boolean force)
    throws FormatException, IOException
  {
    super.setId(id, force);

    // clear last image cache
    lastImage = null;
    lastImageIndex = -1;
    lastImageSeries = -1;
  }

  /* @see IFormatReader#getImageCount() */
  public int getImageCount() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return (reader.isRGB() && !reader.isIndexed()) ?
      (getSizeC() / reader.getEffectiveSizeC()) * reader.getImageCount() :
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
    return isIndexed() && !isFalseColor();
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
    byte[] buf = new byte[w * h * FormatTools.getBytesPerPixel(getPixelType())];
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
      int source = no / c;
      int channel = no % c;
      int series = getSeries();

      if (source != lastImageIndex || series != lastImageSeries) {
        lastImage = reader.openBytes(source, x, y, w, h);
        lastImageIndex = source;
        lastImageSeries = series;
      }

      byte[] n = ImageTools.splitChannels(lastImage, c,
        FormatTools.getBytesPerPixel(getPixelType()), false,
        !isInterleaved())[channel];
      System.arraycopy(n, 0, buf, 0, n.length);
      return buf;
    }
    else return reader.openBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    return openImage(no, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openImage(int, int, int, int, int) */
  public BufferedImage openImage(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return ImageTools.openImage(openBytes(no, x, y, w, h), this, w, h);
  }

  /* @see IFormatReader#openThumbImage(int) */
  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return ImageTools.scale(openImage(no), getThumbSizeX(),
      getThumbSizeY(), true);
  }

  /* @see IFormatReader#close() */
  public void close() throws IOException {
    super.close();
    lastImage = null;
    lastImageIndex = -1;
    lastImageSeries = -1;
  }

  public int getIndex(int z, int c, int t) {
    return FormatTools.getIndex(this, z, c, t);
  }

  public int[] getZCTCoords(int index) {
    return FormatTools.getZCTCoords(this, index);
  }

}

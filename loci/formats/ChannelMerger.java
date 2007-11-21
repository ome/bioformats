//
// ChannelMerger.java
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
import java.io.*;

/**
 * Logic to automatically merge channels in a file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ChannelMerger.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ChannelMerger.java">SVN</a></dd></dl>
 */
public class ChannelMerger extends ReaderWrapper {

  // -- Constructor --

  /** Constructs a ChannelMerger around a new image reader. */
  public ChannelMerger() { super(); }

  /** Constructs a ChannelMerger with the given reader. */
  public ChannelMerger(IFormatReader r) { super(r); }

  // -- ChannelMerger API methods --

  /** Determines whether the channels in the file can be merged. */
  public boolean canMerge() {
    int c = getSizeC();
    return c > 1 && c <= 4 && !reader.isRGB() && !reader.isIndexed();
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#getImageCount() */
  public int getImageCount() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    int no = reader.getImageCount();
    if (canMerge()) no /= getSizeC();
    return no;
  }

  /* @see IFormatReader#getDimensionOrder() */
  public String getDimensionOrder() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    String order = reader.getDimensionOrder();
    if (canMerge()) {
      StringBuffer sb = new StringBuffer(order);
      while (order.indexOf("C") != 2) {
        char pre = order.charAt(order.indexOf("C") - 1);
        sb.setCharAt(order.indexOf("C"), pre);
        sb.setCharAt(order.indexOf(pre), 'C');
        order = sb.toString();
      }
    }
    return order;
  }

  /* @see IFormatReader#isRGB() */
  public boolean isRGB() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return canMerge() || reader.isRGB();
  }

  /* @see IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (!canMerge()) return super.openImage(no);
    int sizeC = getSizeC();
    int[] nos = getZCTCoords(no);

    int z = nos[0], t = nos[2];
    String order = reader.getDimensionOrder();
    int ic = order.indexOf("C") - 2;
    if (ic < 0 || ic > 2) {
      throw new FormatException("Invalid dimension order: " + order);
    }
    BufferedImage[] img = new BufferedImage[sizeC];
    for (int c=0; c<sizeC; c++) {
      img[c] = reader.openImage(reader.getIndex(z, c, t));
    }
    return ImageTools.mergeChannels(img);
  }

  /**
   * Obtains the specified image from the given file as a byte array.
   * For convenience, the channels are sequential, i.e. "RRR...GGG...BBB".
   */
  public byte[] openBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (!canMerge()) return super.openBytes(no);
    int sizeC = getSizeC();
    int[] nos = getZCTCoords(no);
    int z = nos[0], t = nos[2];
    byte[] bytes = null;
    for (int c=0; c<sizeC; c++) {
      byte[] b = reader.openBytes(reader.getIndex(z, c, t));

      if (c == 0) {
        // assume array lengths for each channel are equal
        bytes = new byte[sizeC * b.length];
      }
      System.arraycopy(b, 0, bytes, c * b.length, b.length);
    }
    return bytes;
  }

  /* @see IFormatReader#openThumbImage(int) */
  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (!canMerge()) return super.openThumbImage(no);
    return ImageTools.scale(openImage(no), getThumbSizeX(),
      getThumbSizeY(), true);
  }

  public int getIndex(int z, int c, int t) {
    return FormatTools.getIndex(this, z, c, t);
  }

  public int[] getZCTCoords(int index) {
    return FormatTools.getZCTCoords(this, index);
  }

}

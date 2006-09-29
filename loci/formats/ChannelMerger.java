//
// ChannelMerger.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

/** Logic to automatically merge channels in a file. */
public class ChannelMerger extends ReaderWrapper {

  // -- Constructor --

  /** Constructs a ChannelMerger with the given reader. */
  public ChannelMerger(IFormatReader r) throws FormatException {
    super(r);
  }

  // -- ChannelMerger API methods --

  /** Determines whether the channels in the file can be merged. */
  public boolean canMerge(String id) throws FormatException, IOException {
    int c = getSizeC(id);
    return c > 1 && !(c == 3 && reader.isRGB(id));
  }

  // -- IFormatReader API methods --

  /** Determines the number of images in the given file. */
  public int getImageCount(String id) throws FormatException, IOException {
    int no = reader.getImageCount(id);
    if (canMerge(id)) no /= getSizeC(id);
    return no;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return canMerge(id) || reader.isRGB(id);
  }

  /** Obtains the specified image from the given file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!canMerge(id)) return super.openImage(id, no);
    int sizeC = getSizeC(id);
    int[] nos = getZCTCoords(id, no);
    
    int z = nos[0], t = nos[2];
    String order = getDimensionOrder(id);
    int ic = order.indexOf("C") - 2;
    if (ic < 0 || ic > 2) {
      throw new FormatException("Invalid dimension order: " + order);
    }
    BufferedImage[] img = new BufferedImage[sizeC];
    for (int c=0; c<sizeC; c++) {
      img[c] = reader.openImage(id, reader.getIndex(id, z, c, t));
    }
    return ImageTools.mergeChannels(img);
  }

  /**
   * Obtains the specified image from the given file as a byte array.
   * For convenience, the channels are sequential, i.e. "RRR...GGG...BBB".
   */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!canMerge(id)) return super.openBytes(id, no);
    int sizeC = getSizeC(id);
    int[] nos = getZCTCoords(id, no);
    int z = nos[0], t = nos[2];
    String dimOrder = getDimensionOrder(id);
    int ic = dimOrder.indexOf("C") - 2;
    byte[] bytes = null;
    for (int c=0; c<sizeC; c++) {
      byte[] b = reader.openBytes(id, reader.getIndex(id, z, c, t));
      if (c == 0) {
        // assume array lengths for each channel are equal
        bytes = new byte[sizeC * b.length];
      }
      System.arraycopy(b, 0, bytes, c * b.length, b.length);
    }
    return bytes;
  }

  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    return FormatReader.getIndex(this, id, z, c, t);
  }

  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    return FormatReader.getZCTCoords(this, id, index);
  }

  public boolean testRead(String[] args) throws FormatException, IOException {
    return FormatReader.testRead(this, args);
  }

}

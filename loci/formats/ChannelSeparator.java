//
// ChannelSeparator.java
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
import java.io.IOException;

/** Logic to automatically separate the channels in a file. */
public class ChannelSeparator extends ReaderWrapper {

  // -- Constructors --

  /** Constructs a ChannelSeparator around a new image reader. */
  public ChannelSeparator() { super(); }

  /** Constructs a ChannelSeparator with the given reader. */
  public ChannelSeparator(IFormatReader r) { super(r); }

  // -- IFormatReader API methods --

  /** Determines the number of images in the given file. */
  public int getImageCount(String id) throws FormatException, IOException {
    return reader.isRGB(id) ? getSizeC(id) * reader.getImageCount(id) :
      reader.getImageCount(id);
  }

  /**
   * Gets a five-character string representing
   * the dimension order within the file.
   */
  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    String order = super.getDimensionOrder(id);
    if (reader.isRGB(id)) {
      String newOrder = "XYC";
      if (order.indexOf("Z") > order.indexOf("T")) newOrder += "TZ";
      else newOrder += "ZT";
      return newOrder;
    }
    return order;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) {
    return false;
  }

  /** Obtains the specified image from the given file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int bytes = 0;
    switch (getPixelType(id)) {
      case 0:
      case 1: bytes = 1; break;
      case 2:
      case 3: bytes = 2; break;
      case 4:
      case 5:
      case 6: bytes = 4; break;
      case 7: bytes = 8; break;
    };

    byte[] b = openBytes(id, no);

    if (getPixelType(id) == FormatReader.FLOAT) {
      float[] f = new float[b.length / 4];
      for (int i=0; i<b.length; i+=4) {
        f[i/4] = Float.intBitsToFloat(DataTools.bytesToInt(b, i, 4,
          isLittleEndian(id)));
      }
      if (isNormalized()) f = DataTools.normalizeFloats(f);
      return ImageTools.makeImage(f, getSizeX(id), getSizeY(id));
    }

    return ImageTools.makeImage(b, getSizeX(id), getSizeY(id), 1, false,
      bytes, isLittleEndian(id));
  }

  /** Obtains the specified image from the given file, as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (reader.isRGB(id)) {
      int c = getSizeC(id);
      int source = no / c;
      int channel = no % c;

      byte[] sourceBytes = reader.openBytes(id, source);
      return ImageTools.splitChannels(sourceBytes, c,
        false, isInterleaved(id))[channel];
    }
    else return reader.openBytes(id, no);
  }

  /** Obtains a thumbnail for the specified image from the given file. */
  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.scale(openImage(id, no), getThumbSizeX(id),
      getThumbSizeY(id), true, true);
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

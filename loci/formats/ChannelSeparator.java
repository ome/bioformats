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

/** Logic to automatically separate the channels in a file. */
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

  /* @see IFormatReader#getImageCount() */
  public int getImageCount() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return reader.isRGB() ?
      (getSizeC() / reader.getEffectiveSizeC()) * reader.getImageCount() :
      reader.getImageCount();
  }

  /* @see IFormatReader#getDimensionOrder() */
  public String getDimensionOrder() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    String order = super.getDimensionOrder();
    if (reader.isRGB()) {
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
    return false;
  }

  /* @see IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }

    int bytes = 0;
    switch (getPixelType()) {
      case 0:
      case 1:
        bytes = 1;
        break;
      case 2:
      case 3:
        bytes = 2;
        break;
      case 4:
      case 5:
      case 6:
        bytes = 4;
        break;
      case 7:
        bytes = 8;
        break;
    }

    byte[] b = openBytes(no);

    if (getPixelType() == FormatTools.FLOAT) {
      float[] f = new float[b.length / 4];
      for (int i=0; i<b.length; i+=4) {
        f[i/4] = Float.intBitsToFloat(DataTools.bytesToInt(b, i, 4,
          isLittleEndian()));
      }
      if (isNormalized()) f = DataTools.normalizeFloats(f);
      return ImageTools.makeImage(f, getSizeX(), getSizeY());
    }

    return ImageTools.makeImage(b, getSizeX(), getSizeY(), 1, false,
      bytes, isLittleEndian());
  }

  /* @see IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (reader.isRGB()) {
      int c = getSizeC() / reader.getEffectiveSizeC();
      int source = no / c;
      int channel = no % c;
      int series = getSeries();

      if (source != lastImageIndex || series != lastImageSeries) {
        lastImage = reader.openBytes(source);
        lastImageIndex = source;
        lastImageSeries = series;
      }

      return ImageTools.splitChannels(lastImage, c,
        false, isInterleaved())[channel];
    }
    else return reader.openBytes(no);
  }

  /* @see IFormatReader#openThumbImage(int) */
  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return ImageTools.scale(openImage(no), getThumbSizeX(),
      getThumbSizeY(), true);
  }

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

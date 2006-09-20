//
// Plane2D.java
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

package loci.formats.in;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import loci.formats.FormatReader;
import loci.formats.ImageReader;

/**
 * A class which represents an entire 2D plane.
 *
 * @author Chris Allan callan at blackcat.ca
 */
public class Plane2D {

  /** Contains the plane data. */
  private ByteBuffer    data;

  /** How many bytes make up a pixel value. */
  private int                 bytesPerPixel;

  /** The Java type that we're using for pixel value retrieval */
  private int                 type;

  /** Number of pixels along the <i>X</i>-axis. */
  private int                 sizeX;

  /** Number of pixels along the <i>Y</i>-axis. */
  private int                 sizeY;

  /**
   * Default constructor.
   *
   * @param data the raw pixels.
   * @param type the type of the pixel data from the constants in this class.
   * @param isLittleEndian <i>true</i> if the plane is of little-endian byte
   * order.
   */
  Plane2D(ByteBuffer data, int type, boolean isLittleEndian,
    int sizeX, int sizeY)
  {
    this.type = type;
    this.data = data;
    this.sizeX = sizeX;
    this.sizeY = sizeY;

    this.data.order(
        isLittleEndian? ByteOrder.LITTLE_ENDIAN: ByteOrder.BIG_ENDIAN);

    this.bytesPerPixel = ImageReader.getBytesPerPixel(type);
  }

  /**
   * Returns the pixel intensity value of the pixel at <code>(x, y)</code>.
   *
   * @param x The X coordinate.
   * @param y The Y coordinate.
   * @return The intensity value.
   */
  public double getPixelValue(int x, int y) {
    int offset = ((sizeX * y) + x) * bytesPerPixel;

    switch(type) {
    case FormatReader.INT8:
      return data.get(offset);
    case FormatReader.INT16:
      return data.getShort(offset);
    case FormatReader.INT32:
      return data.getInt(offset);
    case FormatReader.FLOAT:
      return data.getFloat(offset);
    case FormatReader.DOUBLE:
      return data.getDouble(offset);
    case FormatReader.UINT8:
      return (short) (data.get(offset) & 0xFF);
    case FormatReader.UINT16:
      return (int) (data.getShort(offset) & 0xFFFF);
    case FormatReader.UINT32:
      return (long) (data.getInt(offset) & 0xFFFFFFFFL);
    }
    // This should never happen.
    throw new RuntimeException("Woah nelly! Something is very wrong.");
  }
}

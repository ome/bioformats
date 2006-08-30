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

  /** Identifies the <i>INT8</i> data type used to store pixel values. */
  public static final int     INT8 = 0;

  /** Identifies the <i>UINT8</i> data type used to store pixel values. */
  public static final int     UINT8 = 1;

  /** Identifies the <i>INT16</i> data type used to store pixel values. */
  public static final int     INT16 = 2;

  /** Identifies the <i>UINT16</i> data type used to store pixel values. */
  public static final int     UINT16 = 3;

  /** Identifies the <i>INT32</i> data type used to store pixel values. */
  public static final int     INT32 = 4;

  /** Identifies the <i>UINT32</i> data type used to store pixel values. */
  public static final int     UINT32 = 5;

  /** Identifies the <i>FLOAT</i> data type used to store pixel values. */
  public static final int     FLOAT = 6;

  /** Identifies the <i>DOUBLE</i> data type used to store pixel values. */
  public static final int     DOUBLE = 7;

  /** Human readable pixel type. */
  private static String[] pixelsTypes;
  static {
    pixelsTypes = new String[8];
    pixelsTypes[INT8] = "INT8";
    pixelsTypes[UINT8] = "UINT8";
    pixelsTypes[INT16] = "INT16";
    pixelsTypes[UINT16] = "UINT16";
    pixelsTypes[INT32] = "INT32";
    pixelsTypes[UINT32] = "UINT32";
    pixelsTypes[FLOAT] = "FLOAT";
    pixelsTypes[DOUBLE] = "DOUBLE";
  }

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

    this.bytesPerPixel = getBytesPerPixel();
  }

  /**
   * Retrieves how many bytes per pixel the current plane or section has.
   * @return the number of bytes per pixel.
   */
  private int getBytesPerPixel() {
    switch(type) {
    case 0:
    case 1:
      return 1;  // INT8 or UINT8
    case 2:
    case 3:
      return 2;  // INT16 or UINT16
    case 4:
    case 5:
    case 6:
      return 4;  // INT32, UINT32 or FLOAT
    case 7:
      return 8;  // DOUBLE
    }
    throw new RuntimeException("Unknown type with id: '" + type + "'");
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
    case INT8:
      return data.get(offset);
    case INT16:
      return data.getShort(offset);
    case INT32:
      return data.getInt(offset);
    case FLOAT:
      return data.getFloat(offset);
    case DOUBLE:
      return data.getDouble(offset);
    case UINT8:
      return (short) (data.get(offset) & 0xFF);
    case UINT16:
      return (int) (data.getShort(offset) & 0xFFFF);
    case UINT32:
      return (long) (data.getInt(offset) & 0xFFFFFFFFL);
    }
    // This should never happen.
    throw new RuntimeException("Woah nelly! Something is very wrong.");
  }

  /**
   * Takes a string value and maps it to one of the pixel type enumerations.
   * @param typeAsString the pixel type as a string.
   * @return type enumeration value for use with class constants.
   */
  public static int typeFromString(String typeAsString) {
    String uppercaseTypeAsString = typeAsString.toUpperCase();
    for (int i = 0; i < pixelsTypes.length; i++) {
      if (pixelsTypes[i].equals(uppercaseTypeAsString))
        return i;
    }
    throw new RuntimeException("Unknown type: '" + typeAsString + "'");
  }

}

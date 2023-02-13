/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in.LeicaMicrosystemsMetadata.model;

/**
 * This class represents image dimensions extracted from LMS image xmls.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class Dimension {

  // -- Fields --
  public DimensionKey key;
  public int size;
  public long bytesInc;
  public String unit = null;
  private Double lengthPerUnit = 0d;
  private Double offByOneLengthPerPixel = 0d;
  public Double origin;
  public boolean oldPhysicalSize = false;
  public int frameIndex = 0;

  public enum DimensionKey {
    X(1, 'X'),
    Y(2, 'Y'),
    Z(3, 'Z'),
    T(4, 'T'),
    C(5, 'C'),
    S(10, 'S');

    public int id;
    public char token;

    DimensionKey(int id, char token) {
      this.id = id;
      this.token = token;
    }

    public static DimensionKey with(int id) {
      for (DimensionKey key : DimensionKey.values()) {
        if (key.id == id) {
          return key;
        }
      }
      return null;
    }
  }

  private static final long METER_MULTIPLY = 1000000;

  // -- Constructors --

  public Dimension(DimensionKey key, int size, long bytesInc, String unit, Double length, Double origin, boolean oldPhysicalSize) {
    this.key = key;
    this.size = size;
    this.bytesInc = bytesInc;
    this.unit = unit;
    this.oldPhysicalSize = oldPhysicalSize;
    setLength(length);
    this.origin = origin;
  }

  private Dimension() {
  }

  public static Dimension createChannelDimension(int channelNumber, long bytesInc) {
    Dimension dimension = new Dimension();
    dimension.bytesInc = bytesInc;
    dimension.size = channelNumber;
    dimension.key = DimensionKey.C;
    return dimension;
  }

  // -- Methods --
  public void setLength(Double length) {
    lengthPerUnit = length;
    if (size > 1) {
      lengthPerUnit /= (size - 1);
      offByOneLengthPerPixel = lengthPerUnit / size;
    } else {
      lengthPerUnit = 0d;
      offByOneLengthPerPixel = 0d;
    }

    if (unit.equals("Ks")) {
      this.lengthPerUnit /= 1000;
      offByOneLengthPerPixel /= 1000;
    } else if (unit.equals("m")) {
      this.lengthPerUnit *= METER_MULTIPLY;
      offByOneLengthPerPixel *= METER_MULTIPLY;
    }
  }

  public Double getLengthPerUnit() {
    if (key == DimensionKey.X || key == DimensionKey.Y) {
      return oldPhysicalSize ? offByOneLengthPerPixel : lengthPerUnit;
    } else {
      return lengthPerUnit;
    }
  }
}

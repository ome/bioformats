//
// IFDType.java
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
package loci.formats.tiff;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import loci.common.enumeration.CodedEnum;
import loci.common.enumeration.EnumException;

/**
 * An enumeration of IFD types.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tiff/IFDType.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tiff/IFDType.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public enum IFDType implements CodedEnum {

  // IFD types
  BYTE(1, 1),
  ASCII(2, 1),
  SHORT(3, 2),
  LONG(4, 4),
  RATIONAL(5, 8),
  SBYTE(6, 1),
  UNDEFINED(7, 1),
  SSHORT(8, 2),
  SLONG(9, 4),
  SRATIONAL(10, 8),
  FLOAT(11, 4),
  DOUBLE(12, 8),
  IFD(13, 4),
  LONG8(16, 8),
  SLONG8(17, 8),
  IFD8(18, 8);

  /** Code for the IFD type in the actual TIFF file. */
  private int code;

  /** Number of bytes per element of this type. */
  private int bytesPerElement;
  
  private static final Map<Integer,IFDType> lookup =
    new HashMap<Integer,IFDType>();

  /** Reverse lookup of code to IFD type enumerate value. */
  static {
    for(IFDType v : EnumSet.allOf(IFDType.class)) {
      lookup.put(v.getCode(), v);
    }
  }

  /**
   * Retrieves a IFD type by reverse lookup of its "code".
   * @param code The code to look up.
   * @return The <code>IFDType</code> instance for the <code>code</code> or
   * <code>null</code> if it does not exist.
   */
  public static IFDType get(int code) {
    IFDType toReturn = lookup.get(code);
    if (toReturn == null) {
      throw new EnumException("Unable to find IFDType with code: " + code);
    }
    return toReturn;
  }

  /**
   * Default constructor.
   * @param code Integer "code" for the IFD type.
   * @param bytesPerElement Number of bytes per element.
   */
  private IFDType(int code, int bytesPerElement) {
    this.code = code;
    this.bytesPerElement = bytesPerElement;
  }

  /* (non-Javadoc)
   * @see loci.common.CodedEnum#getCode()
   */
  public int getCode() {
    return code;
  }

  /**
   * Retrieves the number of bytes per element.
   * @return See above.
   */
  public int getBytesPerElement() {
    return bytesPerElement;
  }

}

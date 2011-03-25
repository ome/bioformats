//
// CompressionType.java
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

package loci.formats.codec;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import loci.common.enumeration.CodedEnum;
import loci.common.enumeration.EnumException;

/**
 * An enumeration of compression types.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/CompressionType.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/CompressionType.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public enum CompressionType implements CodedEnum {

  UNCOMPRESSED(1, "Uncompressed"),
  ZLIB(2, "zlib"),
  CINEPAK(3, "Cinepak"),
  ANIMATION(4, "Animation"),
  H_263(5, "H.263"),
  SORENSON(6, "Sorenson"),
  SORENSON_3(7, "Sorenson 3"),
  MPEG_4(8, "MPEG 4"),
  LZW(9, "LZW"),
  J2K(10, "JPEG-2000"),
  J2K_LOSSY(11, "JPEG-2000 Lossy"),
  JPEG(12, "JPEG");
  
  /** Code for the compression. */
  private int code;
  
  /** The compression used. */
  private String compression;
  
  /** Map used to retrieve the compression type corresponding to the code. */
  private static final Map<Integer, CompressionType> lookup =
    new HashMap<Integer, CompressionType>();
  
  /** Reverse lookup of code to compression type enumerate value. */
  static {
    for(CompressionType v : EnumSet.allOf(CompressionType.class)) {
      lookup.put(v.getCode(), v);
    }
  }
  
  /**
   * Retrieves the compression by reverse lookup of its "code".
   * @param code The code to look up.
   * @return The <code>CompressionType</code> instance for the
   * <code>code</code> or <code>null</code> if it does not exist.
   */
  public static CompressionType get(int code) {
    CompressionType toReturn = lookup.get(code);
    if (toReturn == null) {
      throw new EnumException("Unable to find CompressionType with code: " +
      		""+code);
    }
    return toReturn;
  }
  
  /**
   * Default constructor.
   * @param code Integer "code" for the IFD type.
   * @param compression The type of compression.
   */
  private CompressionType(int code, String compression) {
    this.code = code;
    this.compression = compression;
  }
  
  /**
   * Implemented as specified by the {@link CodedEnum} I/F.
   * @see CodedEnum#getCode()
   */
  public int getCode() {
    return code;
  }

  /**
   * Returns the compression.
   * @return See above.
   */
  public String getCompression() {
    return compression;
  }
  
}

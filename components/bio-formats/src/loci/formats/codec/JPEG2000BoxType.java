//
// JPEG2000BoxType.java
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

/**
 * An enumeration of JPEG 2000 box types.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/JPEG2000BoxType.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/JPEG2000BoxType.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public enum JPEG2000BoxType implements CodedEnum {

  SIGNATURE(0x6A502020, "Signature"),
  SIGNATURE_WRONG_ENDIANNESS(0x2020506A, "Signature (Wrong endianness)"),
  FILE(0x66747970, "File"),
  HEADER(0x6A703268, "Header"),
  IMAGE_HEADER(0x69686472, "Image header"),
  BITS_PER_COMPONENT(0x62706363, "Bits per component"),
  COLOUR_SPECIFICATION(0x636F6C72, "Colour specification"),
  PALETTE(0x70636C72, "Palette"),
  COMPONENT_MAPPING(0x636D6170, "Component mapping"),
  CHANNEL_DEFINITION(0x63646566, "Channel definition"),
  RESOLUTION(0x72657320, "Resolution"),
  CAPTURE_RESOLUTION(0x72657363, "Capture resolution"),
  DEFAULT_DISPLAY_RESOLUTION(0x72657364, "Default display resolution"),
  CONTIGUOUS_CODESTREAM(0x6A703263, "Contiguous codestream"),
  INTELLECTUAL_PROPERTY(0x6A703269, "Intellectual property"),
  XML(0x786D6C20, "XML"),
  UUID(0x75756964, "UUID"),
  UUID_INFO(0x75696E66, "UUID info"),
  UUID_LIST(0x756C7374, "UUID list"),
  URL(0x75726C20, "URL"),
  ASSOCIATION(0x61736F63, "Association"),
  LABEL(0x6C626C20, "Label"),
  PLACEHOLDER(0x70686C64, "Placeholder");

  /** Code for the box type. */
  private int code;

  /** The name of the box type. */
  private String name;

  /** Map used to retrieve the box type corresponding to the code. */
  private static final Map<Integer, JPEG2000BoxType> lookup =
    new HashMap<Integer, JPEG2000BoxType>();

  /** Reverse lookup of code to box type enumerate value. */
  static {
    for(JPEG2000BoxType v : EnumSet.allOf(JPEG2000BoxType.class)) {
      lookup.put(v.getCode(), v);
    }
  }

  /**
   * Retrieves the box type by reverse lookup of its "code".
   * @param code The code to look up.
   * @return The <code>JPEG2000BoxType</code> instance for the
   * <code>code</code> or <code>null</code> if it does not exist.
   */
  public static JPEG2000BoxType get(int code) {
    return lookup.get(code);
  }

  /**
   * Default constructor.
   * @param code Integer "code" for the box type.
   * @param compression The name of the box type.
   */
  private JPEG2000BoxType(int code, String name) {
    this.code = code;
    this.name = name;
  }

  /**
   * Implemented as specified by the {@link CodedEnum} I/F.
   * @see CodedEnum#getCode()
   */
  public int getCode() {
    return code;
  }

  /**
   * Returns the name of the box type.
   * @return See above.
   */
  public String getName() {
    return name;
  }

}

/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.codec;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import loci.common.enumeration.CodedEnum;

/**
 * An enumeration of JPEG 2000 segment markers.
 */
public enum JPEG2000SegmentMarker implements CodedEnum {

  RESERVED_DELIMITER_MARKER_MIN(0xFF30, "Reserved delimiter marker minimum"),
  RESERVED_DELIMITER_MARKER_MAX(0xFF3F, "Reserved delimiter marker maximum"),
  SOC(0xFF4F, "Start of codestream"),
  SOC_WRONG_ENDIANNESS(0x4FFF, "Start of codestream (Wrong endianness)"),
  SOT(0xFF90, "Start of tile"),
  SOD(0xFF93, "Start of data"),
  EOC(0xFFD9, "End of codestream"),
  SIZ(0xFF51, "Size"),
  COD(0xFF52, "Coding style default"),
  COC(0xFF53, "Coding style component"),
  RGN(0xFF5E, "Region of interest"),
  QCD(0xFF5C, "Quantization default"),
  QCC(0xFF5D, "Quantization component"),
  POC(0xFF5F, "Progression order change"),
  TLM(0xFF55, "Tile lengths"),
  PLM(0xFF57, "Packet length main"),
  PLT(0xFF58, "Packet length tile"),
  PPM(0xFF60, "Packed packet main"),
  PPT(0xFF61, "Packed packet tile"),
  SOP(0xFF91, "Start of packet"),
  EPH(0xFF92, "End of packet header"),
  CRG(0xFF63, "Component registration"),
  COM(0xFF64, "Comment");

  /** Code for the segment marker. */
  private int code;

  /** The name of the segment marker. */
  private String name;

  /** Map used to retrieve the segment marker corresponding to the code. */
  private static final Map<Integer, JPEG2000SegmentMarker> lookup =
    new HashMap<Integer, JPEG2000SegmentMarker>();

  /** Reverse lookup of code to segment marker enumerate value. */
  static {
    for(JPEG2000SegmentMarker v : EnumSet.allOf(JPEG2000SegmentMarker.class)) {
      lookup.put(v.getCode(), v);
    }
  }

  /**
   * Retrieves the segment marker by reverse lookup of its "code".
   * @param code The code to look up.
   * @return The <code>JPEG2000SegmentMarker</code> instance for the
   * <code>code</code> or <code>null</code> if it does not exist.
   */
  public static JPEG2000SegmentMarker get(int code) {
    return lookup.get(code);
  }

  /**
   * Default constructor.
   * @param code Integer "code" for the segment marker.
   * @param compression The name of the segment marker.
   */
  private JPEG2000SegmentMarker(int code, String name) {
    this.code = code;
    this.name = name;
  }

  /**
   * Implemented as specified by the {@link CodedEnum} I/F.
   * @see CodedEnum#getCode()
   */
  @Override
  public int getCode() {
    return code;
  }

  /**
   * Returns the name of the segment marker.
   * @return See above.
   */
  public String getName() {
    return name;
  }

}

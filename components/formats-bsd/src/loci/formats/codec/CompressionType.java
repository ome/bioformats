/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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
import loci.common.enumeration.EnumException;

/**
 * An enumeration of compression types.
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
  @Override
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

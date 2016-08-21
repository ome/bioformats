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

package loci.formats.tiff;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import loci.common.enumeration.CodedEnum;
import loci.common.enumeration.EnumException;

/**
 * Utility class for working with TIFF photometric interpretations.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tiff/PhotoInterp.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tiff/PhotoInterp.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Chris Allan callan at blackcat.ca
 */
public enum PhotoInterp implements CodedEnum {

  WHITE_IS_ZERO(0, "WhiteIsZero", "Monochrome"),
  BLACK_IS_ZERO(1, "BlackIsZero", "Monochrome"),
  RGB(2, "RGB", "RGB"),
  RGB_PALETTE(3, "Palette", "Monochrome"),
  TRANSPARENCY_MASK(4, "Transparency Mask", "RGB"),
  CMYK(5, "CMYK", "CMYK"),
  Y_CB_CR(6, "YCbCr", "RGB"),
  CIE_LAB(8, "CIELAB", "RGB"),
  CFA_ARRAY(32803, "Color Filter Array", "RGB");

  /** Default luminance values for YCbCr data. */
  public static final float LUMA_RED = 0.299f;
  public static final float LUMA_GREEN = 0.587f;
  public static final float LUMA_BLUE = 0.114f;

  /** Code for the IFD type in the actual TIFF file. */
  private int code;

  /** Given name of the photometric interpretation. */
  private String name;

  /** Metadata type of the photometric interpretation. */
  private String metadataType;

  private static final Map<Integer,PhotoInterp> lookup =
    new HashMap<Integer,PhotoInterp>();

  /** Reverse lookup of code to IFD type enumerate value. */
  static {
    for(PhotoInterp v : EnumSet.allOf(PhotoInterp.class)) {
      lookup.put(v.getCode(), v);
    }
  }

  // -- Constructor --

  /**
   * Default constructor.
   * @param code Integer "code" for the photometric interpretation.
   * @param name Given name of the photometric interpretation.
   * @param metadataType Metadata type of the photometric interpretation.
   */
  private PhotoInterp(int code, String name, String metadataType) {
    this.code = code;
    this.name = name;
    this.metadataType = metadataType;
  }

  // -- PhotoInterp methods --

  /**
   * Retrieves a photometric interpretation by reverse lookup of its "code".
   * @param code The code to look up.
   * @return The <code>PhotoInterp</code> instance for the
   * <code>code</code> or <code>null</code> if it does not exist.
   */
  public static PhotoInterp get(int code) {
    PhotoInterp toReturn = lookup.get(code);
    if (toReturn == null) {
      throw new EnumException("Unable to find PhotoInterp with code: " + code);
    }
    return toReturn;
  }

  /* (non-Javadoc)
   * @see loci.common.CodedEnum#getCode()
   */
  public int getCode() {
    return code;
  }

  /**
   * Retrieves the given name of the photometric interpretation. 
   * @return See above.
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieves the metadata type of the photometric interpretation.
   * @return See above.
   */
  public String getMetadataType() {
    return metadataType;
  }

}

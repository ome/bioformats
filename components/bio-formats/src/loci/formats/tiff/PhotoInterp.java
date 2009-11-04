//
// PhotoInterp.java
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

import loci.formats.FormatException;

/**
 * Utility class for working with TIFF photometric interpretations.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/tiff/PhotoInterp.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/tiff/PhotoInterp.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 */
public final class PhotoInterp {

  // -- Constants --

  // TODO: Investigate using Java 1.5 enum instead of int enumeration.
  //       http://javahowto.blogspot.com/2008/04/java-enum-examples.html
  public static final int WHITE_IS_ZERO = 0;
  public static final int BLACK_IS_ZERO = 1;
  public static final int RGB = 2;
  public static final int RGB_PALETTE = 3;
  public static final int TRANSPARENCY_MASK = 4;
  public static final int CMYK = 5;
  public static final int Y_CB_CR = 6;
  public static final int CIE_LAB = 8;
  public static final int CFA_ARRAY = 32803;

  // -- Constructor --

  private PhotoInterp() { }

  // -- PhotoInterp methods --

  /** Returns the name of the given photometric interpretation. */
  public static String getPIName(int photoInterp) {
    switch (photoInterp) {
      case WHITE_IS_ZERO:
        return "WhiteIsZero";
      case BLACK_IS_ZERO:
        return "BlackIsZero";
      case RGB:
        return "RGB";
      case RGB_PALETTE:
        return "Palette";
      case TRANSPARENCY_MASK:
        return "Transparency Mask";
      case CMYK:
        return "CMYK";
      case Y_CB_CR:
        return "YCbCr";
      case CIE_LAB:
        return "CIELAB";
      case CFA_ARRAY:
        return "Color Filter Array";
    }
    return null;
  }

  /**
   * Returns the metadata type for the given photometric interpretation.
   *
   * @return One of the following values:<ul>
   *  <li>Monochrome</li>
   *  <li>RGB</li>
   * </ul>
   */
  public static String getPIMeta(int photoInterp) {
    switch (photoInterp) {
      case WHITE_IS_ZERO:
        return "Monochrome";
      case BLACK_IS_ZERO:
        return "Monochrome";
      case RGB:
        return "RGB";
      case RGB_PALETTE:
        return "Monochrome";
      case TRANSPARENCY_MASK:
        return "RGB";
      case CMYK:
        return "CMYK";
      case Y_CB_CR:
        return "RGB";
      case CIE_LAB:
        return "RGB";
      case CFA_ARRAY:
        return "RGB";
    }
    return null;
  }

  /**
   * Verifies that the given photometric interpretation is supported.
   *
   * @throws FormatException if the photometric interpretation
   *   is unsupported or unknown.
   */
  public static void checkPI(int photoInterp) throws FormatException {
    if (photoInterp == TRANSPARENCY_MASK) {
      throw new FormatException(
        "Sorry, Transparency Mask PhotometricInterpretation is not supported");
    }
    else if (photoInterp == CIE_LAB) {
      throw new FormatException(
        "Sorry, CIELAB PhotometricInterpretation is not supported");
    }
    else if (photoInterp != WHITE_IS_ZERO &&
      photoInterp != BLACK_IS_ZERO &&
      photoInterp != RGB &&
      photoInterp != RGB_PALETTE &&
      photoInterp != CMYK &&
      photoInterp != Y_CB_CR &&
      photoInterp != CFA_ARRAY)
    {
      throw new FormatException("Unknown PhotometricInterpretation (" +
        photoInterp + ")");
    }
  }

}

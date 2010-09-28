//
// JPEG2000CodecOptions.java
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

/**
 * Options for compression and decompressiong JPEG-2000 data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/codec/JPEG2000CodecOptions.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/codec/JPEG2000CodecOptions.java">SVN</a></dd></dl>
 */
public class JPEG2000CodecOptions extends CodecOptions {

  // -- Fields --

  public double quality;
  public int[] codeBlockSize;
  //public String filter;

  // -- Constructors --

  public JPEG2000CodecOptions() {
    super();
  }

  public JPEG2000CodecOptions(CodecOptions options) {
    super(options);
  }

  // -- Static methods --

  /** Return JPEG2000CodecOptions with reasonable default values. */
  public static JPEG2000CodecOptions getDefaultOptions() {
    CodecOptions options = CodecOptions.getDefaultOptions();
    return getDefaultOptions(options);
  }

  /**
   * Return JPEG2000CodecOptions using the given CodecOptions as the default.
   */
  public static JPEG2000CodecOptions getDefaultOptions(CodecOptions options) {
    JPEG2000CodecOptions j2kOptions = new JPEG2000CodecOptions(options);

    j2kOptions.quality = j2kOptions.lossless ? Double.MAX_VALUE : 10;
    j2kOptions.codeBlockSize = new int[] {64, 64};

    return j2kOptions;
  }
}

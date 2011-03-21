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
 * Options for compressing and decompressing JPEG-2000 data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/JPEG2000CodecOptions.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/JPEG2000CodecOptions.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class JPEG2000CodecOptions extends CodecOptions {

  // -- Fields --

  /**
   * The maximum code-block size to use per tile-component as it would be
   * provided to:
   * {@link com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam#setCodeBlockSize(int[])}
   * (WRITE).
   */
  public int[] codeBlockSize;

  /**
   * The number of decomposition levels as would be provided to:
   * {@link com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam#setNumDecompositionLevels(int)}
   * (WRITE). Leaving this value <code>null</code> signifies that when a JPEG
   * 2000 parameter set is created for the purposes of compression the number
   * of decomposition levels will be left as the default.
   */
  public Integer numDecompositionLevels;

  /**
   * The resolution level as would be provided to:
   * {@link com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam#setResolution(int)}
   * (READ). Leaving this value <code>null</code> signifies that when a JPEG
   * 2000 parameter set is created for the purposes of compression the number
   * of decomposition levels will be left as the default.
   */
  public Integer resolution;

  // -- Constructors --

  /** Creates a new instance. */
  public JPEG2000CodecOptions() {
    super();
  }

  /**
   * Creates a new instance with options.
   * @param options The option to set.
   */
  public JPEG2000CodecOptions(CodecOptions options) {
    super(options);
    if (options instanceof JPEG2000CodecOptions) {
      JPEG2000CodecOptions j2kOptions = (JPEG2000CodecOptions) options;
      if (j2kOptions.codeBlockSize != null) {
        codeBlockSize = j2kOptions.codeBlockSize;
      }
      numDecompositionLevels = j2kOptions.numDecompositionLevels;
      resolution = j2kOptions.resolution;
    }
  }

  // -- Static methods --

  /** Return JPEG2000CodecOptions with reasonable default values. */
  public static JPEG2000CodecOptions getDefaultOptions() {
    CodecOptions options = CodecOptions.getDefaultOptions();
    return getDefaultOptions(options);
  }

  /**
   * Return JPEG2000CodecOptions using the given CodecOptions as the default.
   * @param options The specified options.
   */
  public static JPEG2000CodecOptions getDefaultOptions(CodecOptions options) {
    JPEG2000CodecOptions j2kOptions = new JPEG2000CodecOptions(options);

    j2kOptions.quality = j2kOptions.lossless ? Double.MAX_VALUE : 10;
    j2kOptions.codeBlockSize = new int[] {64, 64};

    return j2kOptions;
  }

}

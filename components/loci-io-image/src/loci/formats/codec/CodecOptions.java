//
// CodecOptions.java
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

import java.awt.image.ColorModel;

/**
 * Options for compressing and decompressing data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/CodecOptions.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/CodecOptions.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CodecOptions {

  /** Width, in pixels, of the image. (READ/WRITE) */
  public int width;

  /** Height, in pixels, of the image. (READ/WRITE) */
  public int height;

  /** Number of channels. (READ/WRITE) */
  public int channels;

  /** Number of bits per channel. (READ/WRITE) */
  public int bitsPerSample;

  /** Indicates endianness of pixel data. (READ/WRITE) */
  public boolean littleEndian;

  /** Indicates whether or not channels are interleaved. (READ/WRITE) */
  public boolean interleaved;

  /** Indicates whether or not the pixel data is signed. (READ/WRITE) */
  public boolean signed;

  /**
   * Tile width as it would be provided to:
   * {@link javax.imageio.ImageWriteParam#setTiling(int, int, int, int)}
   * (WRITE).
   */
  public int tileWidth;

  /**
   * Tile height as it would be provided to:
   * {@link javax.imageio.ImageWriteParam#setTiling(int, int, int, int)}
   * (WRITE).
   */
  public int tileHeight;

  /**
   * Horizontal offset of the tile grid as it would be provided to:
   * {@link javax.imageio.ImageWriteParam#setTiling(int, int, int, int)}
   * (WRITE).
   */
  public int tileGridXOffset;

  /**
   * Vertical offset of the tile grid as it would be provided to:
   * {@link javax.imageio.ImageWriteParam#setTiling(int, int, int, int)}
   * (WRITE).
   */
  public int tileGridYOffset;

  /**
   * If compressing, this is the maximum number of raw bytes to compress.
   * If decompressing, this is the maximum number of raw bytes to return.
   * (READ/WRITE).
   */
  public int maxBytes;

  /** Pixels for preceding image (READ/WRITE). */
  public byte[] previousImage;

  /**
   * Used with codecs allowing lossy and lossless compression.
   * Default is set to true (WRITE).
   */
  public boolean lossless;

  /** Color model to use when constructing an image (WRITE).*/
  public ColorModel colorModel;

  /** Compression quality level as it would be provided to:
   * {@link javax.imageio.ImageWriteParam#compressionQuality} (WRITE).
   */
  public double quality;

  // -- Constructors --

  /** Construct a new CodecOptions. */
  public CodecOptions() {}

  /** Construct a new CodecOptions using the given CodecOptions. */
  public CodecOptions(CodecOptions options) {
    if (options != null) {
      this.width = options.width;
      this.height = options.height;
      this.channels = options.channels;
      this.bitsPerSample = options.bitsPerSample;
      this.littleEndian = options.littleEndian;
      this.interleaved = options.interleaved;
      this.signed = options.signed;
      this.maxBytes = options.maxBytes;
      this.previousImage = options.previousImage;
      this.lossless = options.lossless;
      this.colorModel = options.colorModel;
      this.quality = options.quality;
      this.tileWidth = options.tileWidth;
      this.tileHeight = options.tileHeight;
      this.tileGridXOffset = options.tileGridXOffset;
      this.tileGridYOffset = options.tileGridYOffset;
    }
  }

  // -- Static methods --

  /** Return CodecOptions with reasonable default values. */
  public static CodecOptions getDefaultOptions() {
    CodecOptions options = new CodecOptions();
    options.littleEndian = false;
    options.interleaved = false;
    options.lossless = true;
    return options;
  }

}

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

  /** Width, in pixels, of the image. */
  public int width;

  /** Height, in pixels, of the image. */
  public int height;

  /** Number of channels. */
  public int channels;

  /** Number of bits per channel. */
  public int bitsPerSample;

  /** Indicates endianness of pixel data. */
  public boolean littleEndian;

  /** Indicates whether or not channels are interleaved. */
  public boolean interleaved;

  /** Indicates whether or not the pixel data is signed. */
  public boolean signed;

  /**
   * If compressing, this is the maximum number of raw bytes to compress.
   * If decompressing, this is the maximum number of raw bytes to return.
   */
  public int maxBytes;

  /** Pixels for preceding image. */
  public byte[] previousImage;

  /**
   * Used with codecs allowing lossy and lossless compression.
   * Default is set to true.
   */
  public boolean lossless;

  /** Color model to use when constructing an image. */
  public ColorModel colorModel;

  // -- Constructors --

  /** Construct a new CodecOptions. */
  public CodecOptions() { }

  /** Construct a new CodecOptions using the given CodecOptions. */
  public CodecOptions(CodecOptions options) {
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

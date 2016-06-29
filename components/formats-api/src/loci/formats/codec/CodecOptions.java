/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

import java.awt.image.ColorModel;

/**
 * Options for compressing and decompressing data.
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

  /**
   * Whether or not the decompressed data will be stored as YCbCr.
   */
  public boolean ycbcr;

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
      this.ycbcr = options.ycbcr;
    }
  }

  // -- Static methods --

  /** Return CodecOptions with reasonable default values. */
  public static CodecOptions getDefaultOptions() {
    CodecOptions options = new CodecOptions();
    options.littleEndian = false;
    options.interleaved = false;
    options.lossless = true;
    options.ycbcr = false;
    return options;
  }

}

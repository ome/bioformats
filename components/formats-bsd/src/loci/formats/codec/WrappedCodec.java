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

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;

/**
 * Implements encoding (compress) and decoding (decompress) methods
 * for Wrapped.  This code was adapted from the Jakarta Commons Codec source,
 * http://jakarta.apache.org/commons
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
class WrappedCodec extends BaseCodec {

  // Wrapped alphabet and codes
  protected ome.codecs.Codec codec = null;

  /**
   * Construct with a codec implementation to wrap.
   * @param codec the codec to wrap.
   */
  protected WrappedCodec(ome.codecs.Codec codec) {
    this.codec = codec;
  }

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] data, CodecOptions options) throws FormatException
  {
    try {
      return codec.compress(data, getOptions(options));
    }
    catch (ome.codecs.CodecException e) {
      throw new FormatException(e);
    }
  }

  /* @see Codec#compress(byte[][], CodecOptions) */
  @Override
  public byte[] compress(byte[][] data, CodecOptions options) throws FormatException
  {
    try {
      return codec.compress(data, getOptions(options));
    }
    catch (ome.codecs.CodecException e) {
      throw new FormatException(e);
    }
  }

  /* @see Codec#decompress(byte[], CodecOptions) */
  public byte[] decompress(byte[] data, CodecOptions options) throws FormatException
  {
    try {
      return codec.decompress(data, getOptions(options));
    }
    catch (ome.codecs.CodecException e) {
      throw new FormatException(e);
    }
  }

  /* @see Codec#decompress(byte[][], CodecOptions) */
  public byte[] decompress(byte[][] data, CodecOptions options) throws FormatException
  {
    try {
      return codec.decompress(data, getOptions(options));
    }
    catch (ome.codecs.CodecException e) {
      throw new FormatException(e);
    }
  }

  /* @see Codec#decompress(byte[]) */
  public byte[] decompress(byte[] data) throws FormatException
  {
    try {
      return codec.decompress(data);
    }
    catch (ome.codecs.CodecException e) {
      throw new FormatException(e);
    }
  }

  /* @see Codec#decompress(byte[][]) */
  public byte[] decompress(byte[][] data) throws FormatException
  {
    try {
      return codec.decompress(data);
    }
    catch (ome.codecs.CodecException e) {
      throw new FormatException(e);
    }
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    try {
      return codec.decompress(in, getOptions(options));
    }
    catch (ome.codecs.CodecException e) {
      throw new FormatException(e);
    }
  }

  private static void copyOptions(CodecOptions src, ome.codecs.CodecOptions dest)
    throws FormatException
  {
    dest.width = src.width;
    dest.height = src.height;
    dest.channels = src.channels;
    dest.bitsPerSample = src.bitsPerSample;
    dest.littleEndian = src.littleEndian;
    dest.interleaved = src.interleaved;
    dest.signed = src.signed;
    dest.maxBytes = src.maxBytes;
    dest.previousImage = src.previousImage;
    dest.lossless = src.lossless;
    dest.colorModel = src.colorModel;
    dest.quality = src.quality;
    dest.tileWidth = src.tileWidth;
    dest.tileHeight = src.tileHeight;
    dest.tileGridXOffset = src.tileGridXOffset;
    dest.tileGridYOffset = src.tileGridYOffset;
    dest.ycbcr = src.ycbcr;
  }

  protected static ome.codecs.CodecOptions getOptions(CodecOptions options)
    throws FormatException
  {
    if(options == null) {
      return null;
    }

    ome.codecs.CodecOptions newOptions = null;

    Class c = options.getClass();

    if(c.equals(HuffmanCodecOptions.class)) {
      newOptions = new ome.codecs.HuffmanCodecOptions();
      copyOptions(options, newOptions);
      ((ome.codecs.HuffmanCodecOptions) newOptions).table = ((HuffmanCodecOptions) options).table;
    }
    else if(c.equals(JPEG2000CodecOptions.class)) {
      newOptions = new ome.codecs.JPEG2000CodecOptions();
      copyOptions(options, newOptions);
      ((ome.codecs.JPEG2000CodecOptions) newOptions).codeBlockSize = ((JPEG2000CodecOptions) options).codeBlockSize;
      ((ome.codecs.JPEG2000CodecOptions) newOptions).numDecompositionLevels = ((JPEG2000CodecOptions) options).numDecompositionLevels;
      ((ome.codecs.JPEG2000CodecOptions) newOptions).resolution = ((JPEG2000CodecOptions) options).resolution;
      ((ome.codecs.JPEG2000CodecOptions) newOptions).writeBox = ((JPEG2000CodecOptions) options).writeBox;
    }
    else if(c.equals(MJPBCodecOptions.class)) {
      newOptions = new ome.codecs.MJPBCodecOptions();
      copyOptions(options, newOptions);
      ((ome.codecs.MJPBCodecOptions) newOptions).interlaced = ((MJPBCodecOptions) options).interlaced;
    }
    else if(c.equals(CodecOptions.class)) {
      newOptions = new ome.codecs.CodecOptions();
      copyOptions(options, newOptions);
    }
    else {
      throw new FormatException("Unwrapped codec: " + c.getName());
    }

    return newOptions;
  }

}

/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2023 Open Microscopy Environment:
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

package loci.formats;

import java.io.IOException;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;

/**
 * Interface for writing pre-compressed image tiles.
 */
public interface ICompressedTileWriter {

  /**
   * Save a compressed tile to the current output file.
   * The compression type of the tile should match the compression type
   * set in the writer.
   *
   * @param no plane index
   * @param buf compressed tile bytes
   * @param x pixel X coordinate of the upper-left corner of the tile
   * @param y pixel Y coordinate of the upper-left corner of the tile
   * @param w width in pixels of the compressed tile
   * @param h height in pixels of the compressed tile
   */
  default void saveCompressedBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    throw new UnsupportedOperationException("Writer does not support pre-compressed tile writing");
  }

  /**
   * @return the codec that can be used to compress tiles
   */
  default Codec getCodec() {
    throw new UnsupportedOperationException("Writer does not support pre-compressed tile writing");
  }

  /**
   * @return the codec options that can be used to compress tiles
   */
  default CodecOptions getCodecOptions() {
    throw new UnsupportedOperationException("Writer does not support pre-compressed tile writing");
  }

}

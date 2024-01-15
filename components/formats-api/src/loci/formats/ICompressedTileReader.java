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
 * Interface for reading pre-compressed image tiles.
 */
public interface ICompressedTileReader {

  /**
   * Get the number of rows of tiles in the specified plane in the current series.
   *
   * @param no plane index
   * @return tile row count
   */
  default int getTileRows(int no) {
    throw new UnsupportedOperationException("Reader does not support pre-compressed tile access");
  }

  /**
   * Get the number of columns of tiles in the specified plane in the current series.
   *
   * @param no plane index
   * @return tile column count
   */
  default int getTileColumns(int no) {
    throw new UnsupportedOperationException("Reader does not support pre-compressed tile access");
  }

  /**
   * Retrieve the specified tile without performing any decompression.
   *
   * @param no plane index
   * @param x tile X index (indexed from 0, @see getTileColumns(int))
   * @param y tile Y index (indexed frmo 0, @see getTileRows(int))
   * @return compressed tile bytes
   */
  default byte[] openCompressedBytes(int no, int x, int y) throws FormatException, IOException {
    throw new UnsupportedOperationException("Reader does not support pre-compressed tile access");
  }

  /**
   * Retrieve the specified tile without performing any decompression.
   *
   * @param no plane index
   * @param buf pre-allocated buffer in which to store compressed bytes
   * @param x tile X index (indexed from 0, @see getTileColumns(int))
   * @param y tile Y index (indexed frmo 0, @see getTileRows(int))
   * @return compressed tile bytes
   */
  default byte[] openCompressedBytes(int no, byte[] buf, int x, int y) throws FormatException, IOException {
    throw new UnsupportedOperationException("Reader does not support pre-compressed tile access");
  }

  /**
   * Retrieve a codec that can be used to decompress compressed tiles.
   *
   * @param no plane index
   * @return codec that can be used for compressed tiles in the specified plane
   * @see openCompressedBytes(int, int, int)
   */
  default Codec getTileCodec(int no) throws FormatException, IOException {
    throw new UnsupportedOperationException("Reader does not support pre-compressed tile access");
  }

  /**
   * Retrieve codec options that can be used to decompressed the specified tile.
   *
   * @param no plane index
   * @param x tile X index (indexed from 0, @see getTileColumns(int))
   * @param y tile Y index (indexed frmo 0, @see getTileRows(int))
   * @return codec options
   * @see getTileCodec(int)
   */
  default CodecOptions getTileCodecOptions(int no, int x, int y) throws FormatException, IOException {
    throw new UnsupportedOperationException("Reader does not support pre-compressed tile access");
  }

}

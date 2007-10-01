//
// Codec.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.codec;

import loci.formats.FormatException;

/**
 * This class is an interface for any kind of compression or decompression.
 * Data is presented to the compressor in a 1D or 2D byte array,
 * with (optionally, depending on the compressor) pixel dimensions and
 * an Object containing any other options the compressor may need.
 *
 * If an argument is not appropriate for the compressor type, it is expected
 * to completely ignore the argument. i.e.: Passing a compressor that does not
 * require pixel dimensions null for the dimensions must not cause the
 * compressor to throw a NullPointerException.
 *
 * Classes implementing the Codec interface are expected to either
 * implement both compression methods or neither. (The same is expected for
 * decompression).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/Codec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/Codec.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public interface Codec {

  /**
   * Compresses a block of data.
   *
   * @param data The data to be compressed.
   * @param x Length of the x dimension of the image data, if appropriate.
   * @param y Length of the y dimension of the image data, if appropriate.
   * @param dims The dimensions of the image data, if appropriate.
   * @param options Options to be used during compression, if appropriate.
   * @return The compressed data.
   * @throws FormatException If input is not a compressed data block of the
   *   appropriate type.
   */
  byte[] compress(byte[] data, int x, int y,
      int[] dims, Object options) throws FormatException;

  /**
   * Compresses a block of data.
   *
   * @param data The data to be compressed.
   * @param x Length of the x dimension of the image data, if appropriate.
   * @param y Length of the y dimension of the image data, if appropriate.
   * @param dims The dimensions of the image data, if appropriate.
   * @param options Options to be used during compression, if appropriate.
   * @return The compressed data.
   * @throws FormatException If input is not a compressed data block of the
   *   appropriate type.
   */
  byte[] compress(byte[][] data, int x, int y,
      int[] dims, Object options) throws FormatException;

  /**
   * Decompresses a block of data.
   *
   * @param data the data to be decompressed
   * @param options Options to be used during decompression.
   * @return the decompressed data.
   * @throws FormatException If data is not valid.
   */
  byte[] decompress(byte[] data, Object options) throws FormatException;

  /**
   * Decompresses a block of data.
   *
   * @param data the data to be decompressed
   * @param options Options to be used during decompression.
   * @return the decompressed data.
   * @throws FormatException If data is not valid.
   */
  byte[] decompress(byte[][] data, Object options) throws FormatException;

  /**
   * Decompresses a block of data.
   *
   * @param data the data to be decompressed.
   * @return The decompressed data.
   * @throws FormatException If data is not valid compressed data for this
   *   decompressor.
   */
  byte[] decompress(byte[] data) throws FormatException;

  /**
   * Decompresses a block of data.
   *
   * @param data The data to be decompressed.
   * @return The decompressed data.
   * @throws FormatException If data is not valid compressed data for this
   *   decompressor.
   */
  byte[] decompress(byte[][] data) throws FormatException;

}

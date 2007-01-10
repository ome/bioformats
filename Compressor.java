//
// Compressor.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

package loci.formats;

/**
 * This class is an interface for any kind of compression.
 * data is presented to the compressor in a 1D or 2D byte array,
 * with (optionally, depending on the compressor) pixel dimensions and
 * an Object containing any other options the compressor may need.
 *
 * If an argument is not appropriate for the compressor type, it is expected
 * to completely ignore the argument. i.e.: Passing a compressor that does not
 * require pixel dimensions null for the dimensions must not cause the
 * compressor to throw a NullPointerException.
 *
 * Classes implementing the Compressor interface are expected to either
 * implement both compression methods or neither. (The same is expected for
 * decompression).
 */
public interface Compressor {

  /**
   * Compresses a block of data.
   *
   * @param data the data to be compressed
   * @param dims the dimensions (if appropriate) of the contained pixel data
   * @param options an Object representing an options required by the compressor
   * @return The compressed data
   */
  byte[] compress(byte[] data, int[] dims, Object options);

  /**
   * Compresses a block of data.
   *
   * @param data the data to be compressed
   * @param dims the dimensions (if appropriate) of the contained pixel data
   * @param options an Object representing an options required by the compressor
   * @return The compressed data
   */
  byte[] compress(byte[][] data, int[] dims, Object options);

  /**
   * Decompresses a block of data.
   *
   * @param data the data to be decompressed
   * @return The decompressed data
   * @throws FormatException if data is not valid compressed data for this
   *                         decompressor
   */
  byte[] decompress(byte[] data) throws FormatException;

  /**
   * Decompresses a block of data.
   *
   * @param data the data to be decompressed
   * @return The decompressed data
   * @throws FormatException if data is not valid compressed data for this
   *                         decompressor
   */
  byte[] decompress(byte[][] data) throws FormatException;

}

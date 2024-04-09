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

package loci.formats.services;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.common.services.Service;
import loci.common.services.ServiceException;

/**
 *
 * @author Melissa Linkert <melissa at glencoesoftware.com>
 */
public interface JPEGTurboService extends Service {

  /**
   * @return the restart markers associated with the current JPEG stream
   */
  long[] getRestartMarkers();

  /**
   * @param markers precalculated restart markers associated with
   *                the current JPEG stream
   */
  void setRestartMarkers(long[] markers);

  /**
   * Initialize the given stream, which represents an image of the given
   * width and height. This service is primarily intended for very large
   * JPEG images whose width and/or height exceed 65535 (the maximum
   * that can be recorded in a JPEG stream).
   *
   * @param jpeg open stream containing JPEG data
   * @param width total image width
   * @param height total image height
   */
  void initialize(RandomAccessInputStream jpeg, int width, int height)
    throws ServiceException, IOException;

  /**
   * @return the width (in pixels) of a tile
   */
  int getTileWidth();

  /**
   * @return the height (in pixels) of a tile
   */
  int getTileHeight();

  /**
   * @return the number of rows of tiles
   */
  int getTileRows();

  /**
   * @return the number of columns of tiles
   */
  int getTileColumns();

  /**
   * Get the uncompressed bytes representing the given bounding box.
   *
   * @param buf array in which to store uncompressed bytes
   * @param xCoordinate upper-left X coordinate of bounding box
   * @param yCoordinate upper-left Y coordinate of bounding box
   * @param width width of bounding box
   * @param height height of bounding box
   * @return uncompressed bytes
   */
  byte[] getTile(byte[] buf, int xCoordinate, int yCoordinate, int width,
    int height)
    throws IOException;

  /**
   * Get the uncompressed bytes representing the given tile index.
   *
   * @param xTile column index of the tile
   * @param yTile row index of the tile
   * @return uncompressed bytes
   */
  byte[] getTile(int xTile, int yTile) throws IOException;

  /**
   * Similar to getTile(int, int), but returns the JPEG-compressed bytes.
   *
   * @param xTile column index of the tile
   * @param yTile row index of the tile
   * @return JPEG-compressed bytes
   */
  byte[] getCompressedTile(int xTile, int yTile) throws IOException;

  /**
   * Similar to getTile(int, int), but returns the JPEG-compressed bytes.
   *
   * @param data preallocated array for storing tile
   * @param xTile column index of the tile
   * @param yTile row index of the tile
   * @return JPEG-compressed bytes
   */
  byte[] getCompressedTile(byte[] data, int xTile, int yTile) throws IOException;

  /**
   * Free resources associated with the initialized stream.
   */
  void close() throws IOException;

  /**
   * @return true if the underlying native library was successfully loaded
   */
  boolean isLibraryLoaded();

}

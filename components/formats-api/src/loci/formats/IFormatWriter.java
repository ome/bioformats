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

package loci.formats;

import java.awt.image.ColorModel;
import java.io.IOException;

import loci.common.Region;
import loci.formats.codec.CodecOptions;
import loci.formats.meta.MetadataRetrieve;

/**
 * Interface for all biological file format writers.
 */
public interface IFormatWriter extends IFormatHandler {

  /**
   * Saves the given image to the current series in the current file.
   *
   * @param no the image index within the current file, starting from 0.
   * @param buf the byte array that represents the image.
   * @throws FormatException if one of the parameters is invalid.
   * @throws IOException if there was a problem writing to the file.
   */
  void saveBytes(int no, byte[] buf) throws FormatException, IOException;

  /**
   * Saves the given image tile to the current series in the current file.
   *
   * @param no the image index within the current file, starting from 0.
   * @param buf the byte array that represents the image tile.
   * @param x the X coordinate of the upper-left corner of the image tile.
   * @param y the Y coordinate of the upper-left corner of the image tile.
   * @param w the width (in pixels) of the image tile.
   * @param h the height (in pixels) of the image tile.
   * @throws FormatException if one of the parameters is invalid.
   * @throws IOException if there was a problem writing to the file.
   */
  void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException;

  /**
   * Saves the given image tile to the current series in the current file.
   *
   * @param no the image index within the current file, starting from 0.
   * @param buf the byte array that represents the image tile.
   * @param tile the Region representing the image tile to be read.
   * @throws FormatException if one of the parameters is invalid.
   * @throws IOException if there was a problem writing to the file.
   */
  void saveBytes(int no, byte[] buf, Region tile)
    throws FormatException, IOException;

  /**
   * Saves the given image plane to the current series in the current file.
   *
   * @param no the image index within the current file, starting from 0.
   * @param plane the image plane.
   * @throws FormatException if one of the parameters is invalid.
   * @throws IOException if there was a problem writing to the file.
   */
  void savePlane(int no, Object plane) throws FormatException, IOException;

  /**
   * Saves the given image plane to the current series in the current file.
   *
   * @param no the image index within the current file, starting from 0.
   * @param plane the image plane.
   * @param x the X coordinate of the upper-left corner of the image tile.
   * @param y the Y coordinate of the upper-left corner of the image tile.
   * @param w the width (in pixels) of the image tile.
   * @param h the height (in pixels) of the image tile.
   * @throws FormatException if one of the parameters is invalid.
   * @throws IOException if there was a problem writing to the file.
   */
  void savePlane(int no, Object plane, int x, int y, int w, int h)
    throws FormatException, IOException;

  /**
   * Saves the given image plane to the current series in the current file.
   *
   * @param no the image index within the current file, starting from 0.
   * @param plane the image plane.
   * @param tile the Region representing the image tile to be read.
   * @throws FormatException if one of the parameters is invalid.
   * @throws IOException if there was a problem writing to the file.
   */
  void savePlane(int no, Object plane, Region tile)
    throws FormatException, IOException;

  /**
   * Sets the current series.
   *
   * @param series the series index, starting from 0.
   * @throws FormatException if the specified series is invalid.
   */
  void setSeries(int series) throws FormatException;

  /** Returns the current series. */
  int getSeries();

  /** Sets whether or not the channels in an image are interleaved. */
  void setInterleaved(boolean interleaved);

  /** Sets the number of valid bits per pixel. */
  void setValidBitsPerPixel(int bits);

  /** Gets whether or not the channels in an image are interleaved. */
  boolean isInterleaved();

  /** Reports whether the writer can save multiple images to a single file. */
  boolean canDoStacks();

  /**
   * Sets the metadata retrieval object from
   * which to retrieve standardized metadata.
   */
  void setMetadataRetrieve(MetadataRetrieve r);

  /**
   * Retrieves the current metadata retrieval object for this writer. You can
   * be assured that this method will <b>never</b> return a <code>null</code>
   * metadata retrieval object.
   * @return A metadata retrieval object.
   */
  MetadataRetrieve getMetadataRetrieve();

  /** Sets the color model. */
  void setColorModel(ColorModel cm);

  /** Gets the color model. */
  ColorModel getColorModel();

  /** Sets the frames per second to use when writing. */
  void setFramesPerSecond(int rate);

  /** Gets the frames per second to use when writing. */
  int getFramesPerSecond();

  /** Gets the available compression types. */
  String[] getCompressionTypes();

  /** Gets the supported pixel types. */
  int[] getPixelTypes();

  /** Gets the supported pixel types for the given codec. */
  int[] getPixelTypes(String codec);

  /** Checks if the given pixel type is supported. */
  boolean isSupportedType(int type);

  /** Sets the current compression type. */
  void setCompression(String compress) throws FormatException;

  /**
   * Sets the codec options.
   * @param options The options to set.
   */
  void setCodecOptions(CodecOptions options) ;

  /** Gets the current compression type. */
  String getCompression();

  /** Switch the output file for the current dataset. */
  void changeOutputFile(String id) throws FormatException, IOException;

  /**
   * Sets whether or not we know that planes will be written sequentially.
   * If planes are written sequentially and this flag is set, then performance
   * will be slightly improved.
   */
  void setWriteSequentially(boolean sequential);

  /**
   * Retrieves the current tile width
   * Defaults to full image width if not supported
   * @return The current tile width being used
   * @throws FormatException Image metadata including Pixels Size X must be set prior to calling getTileSizeX()
   */
  int getTileSizeX() throws FormatException;

  /**
   * Will attempt to set the tile width to the desired value and return the actual value which will be used
   * @param tileSize The tile width you wish to use
   * @return The tile width which will actually be used, this may differ from the value requested.
   *         If the requested value is not supported the writer will return and use the closest appropriate value.
   * @throws FormatException Tile size must be greater than 0 and less than the image width
   */
  int setTileSizeX(int tileSize) throws FormatException;

  /**
   * Retrieves the current tile height
   * Defaults to full image height if not supported
   * @return The current tile height being used
   * @throws FormatException Image metadata including Pixels Size Y must be set prior to calling getTileSizeY()
   */
  int getTileSizeY() throws FormatException;

  /**
   * Will attempt to set the tile height to the desired value and return the actual value which will be used
   * @param tileSize The tile height you wish to use
   * @return The tile height which will actually be used, this may differ from the value requested.
   *         If the requested value is not supported the writer will return and use the closest appropriate value.
   * @throws FormatException Tile size must be greater than 0 and less than the image height
   */
  int setTileSizeY(int tileSize) throws FormatException;

}

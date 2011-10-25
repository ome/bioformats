//
// IFormatWriter.java
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

package loci.formats;

import java.awt.image.ColorModel;
import java.io.IOException;

import loci.common.Region;
import loci.formats.codec.CodecOptions;
import loci.formats.meta.MetadataRetrieve;

/**
 * Interface for all biological file format writers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/IFormatWriter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/IFormatWriter.java;hb=HEAD">Gitweb</a></dd></dl>
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

  // -- Deprecated methods --

  /** @deprecated Please use saveBytes(int, byte[]) instead. */
  void saveBytes(byte[] bytes, boolean last)
    throws FormatException, IOException;

  /**
   * @deprecated Please use saveBytes(int, byte[]) and setSeries(int) instead.
   */
  void saveBytes(byte[] bytes, int series, boolean lastInSeries, boolean last)
    throws FormatException, IOException;

  /** @deprecated Please use savePlane(int, Object) instead. */
  void savePlane(Object plane, boolean last)
    throws FormatException, IOException;

  /**
   * @deprecated Please use savePlane(int, Object) and setSeries(int) instead.
   */
  void savePlane(Object plane, int series, boolean lastInSeries, boolean last)
    throws FormatException, IOException;

}

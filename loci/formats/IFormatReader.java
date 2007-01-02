//
// IFormatReader.java
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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

/** Interface for all biological file format readers. */
public interface IFormatReader extends IFormatHandler {

  /** Checks if the given block is a valid header for this file format. */
  boolean isThisType(byte[] block);

  /** Determines the number of images in the given file. */
  int getImageCount(String id) throws FormatException, IOException;

  /** Checks if the images in the file are RGB. */
  boolean isRGB(String id) throws FormatException, IOException;

  /** Gets the size of the X dimension. */
  int getSizeX(String id) throws FormatException, IOException;

  /** Gets the size of the Y dimension. */
  int getSizeY(String id) throws FormatException, IOException;

  /** Gets the size of the Z dimension. */
  int getSizeZ(String id) throws FormatException, IOException;

  /** Gets the size of the C dimension. */
  int getSizeC(String id) throws FormatException, IOException;

  /** Gets the size of the T dimension. */
  int getSizeT(String id) throws FormatException, IOException;

  /**
   * Gets the pixel type.
   * @param id the image's filename.
   * @return the pixel type as an enumeration from <code>FormatReader</code>
   * <i>static</i> pixel types such as <code>INT8</code>.
   * @throws FormatException if there was a problem parsing file metadata.
   * @throws IOException if there was an error reading from the file.
   */
  int getPixelType(String id) throws FormatException, IOException;

  /**
   * Get the effective size of the C dimension.  If isRGB(String) returns
   * true, this will return 1; otherwise, it returns the value of getSizeC.
   */
  int getEffectiveSizeC(String id) throws FormatException, IOException;

  /**
   * Retrieves a specified channel's global minimum.
   * @param id the filename of the base image.
   * @param theC the channel whose minimum is required.
   * @return the global minimum of the channel; which is defined as the minimum
   * for the channel across all image planes/sections in the image. Will be
   * <code>null</code> if there is no channel minimum stored directly in the
   * file and channel stat calculation is disabled.
   * @throws FormatException if there was a problem parsing the metadata of the
   * file.
   * @throws IOException if there was a problem reading the file.
   * @see #setChannelStatCalculationStatus(boolean)
   */
  Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException;

  /**
   * Retrieves a specified channel's global maximum.
   * @param id the filename of the base image.
   * @param theC the channel whose maximum is required.
   * @return the global maximum of the channel; which is defined as the maximum
   * for the channel across all image planes/sections in the image. Will be
   * <code>null</code> if there is no channel maximum stored directly in the
   * file and channel stat calculation is disabled.
   * @throws FormatException if there was a problem parsing the metadata of the
   * file.
   * @throws IOException if there was a problem reading the file.
   * @see #setChannelStatCalculationStatus(boolean)
   */
  Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException;

  /** Get the size of the X dimension for the thumbnail. */
  int getThumbSizeX(String id) throws FormatException, IOException;

  /** Get the size of the Y dimension for the thumbnail. */
  int getThumbSizeY(String id) throws FormatException, IOException;

  /** Gets whether the data is in little-endian format. */
  boolean isLittleEndian(String id) throws FormatException, IOException;

  /**
   * Gets a five-character string representing the
   * dimension order within the file.
   */
  String getDimensionOrder(String id) throws FormatException, IOException;

  /**
   * Gets whether the dimension order and sizes are known, or merely guesses.
   */
  boolean isOrderCertain(String id) throws FormatException, IOException;

  /**
   * Readers can implement potentially performance hindering statistics
   * calculation as part of metadata parsing. This method allows the user of
   * the reader to turn such calculations on or off.
   * @param on <code>true</code> if statistics calculation should be turned on.
   * @see #getChannelStatCalculationStatus()
   */
  void setChannelStatCalculationStatus(boolean on);

  /**
   * Retrieves the status of channel statistics calculation.
   * @return the status.
   * @see #setChannelStatCalculationStatus(boolean)
   */
  boolean getChannelStatCalculationStatus();

  /** Gets whether or not the channels are interleaved. */
  boolean isInterleaved(String id) throws FormatException, IOException;

  /** Obtains the specified image from the given file. */
  BufferedImage openImage(String id, int no)
    throws FormatException, IOException;

  /**
   * Obtains the specified image from the given file as a byte array.
   */
  byte[] openBytes(String id, int no) throws FormatException, IOException;

  /**
   * Obtains the specified image from the given file into a pre-allocated byte
   * array of (sizeX * sizeY * bytesPerPixel).
   * @param id the filename of the base image.
   * @param no the image index within the file.
   * @param buf a pre-allocated buffer.
   * @return the pre-allocated buffer <code>buf</code> for convenience.
   * @throws FormatException if there was a problem parsing the metadata of the
   * file.
   * @throws IOException if there was a problem reading the file.
   */
  byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException;

  /** Obtains a thumbnail for the specified image from the given file. */
  BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException;

  /**
   * Obtains a thumbnail for the specified image from the given file,
   * as a byte array.
   */
  byte[] openThumbBytes(String id, int no) throws FormatException, IOException;

  /** Closes the currently open file. */
  void close() throws FormatException, IOException;

  /** Gets the number of series in this file. */
  int getSeriesCount(String id) throws FormatException, IOException;

  /** Activates the specified series. */
  void setSeries(String id, int no) throws FormatException, IOException;

  /** Gets the currently active series. */
  int getSeries(String id) throws FormatException, IOException;

  /** Specifies whether or not to ignore color tables, if present. */
  void setColorTableIgnored(boolean ignore);

  /** Returns true if we should ignore color tables, if present. */
  boolean isColorTableIgnored();

  /** Specifies whether or not to normalize float data. */
  void setNormalized(boolean normalize);

  /** Returns true if we should normalize float data. */
  boolean isNormalized();

  /** Returns an array of filenames needed to open this dataset. */
  String[] getUsedFiles(String id) throws FormatException, IOException;

  /**
   * Swaps the dimensions according to the given dimension order.  If the given
   * order is identical to the file's native order, then nothing happens.
   * Note that this method will throw an exception if X and Y do not appear in
   * positions 0 and 1 (although X and Y can be reversed).
   */
  void swapDimensions(String id, String order)
    throws FormatException, IOException;

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates.
   */
  int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException;

  /**
   * Gets the Z, C and T coordinates corresponding
   * to the given rasterized index value.
   */
  int[] getZCTCoords(String id, int index)
    throws FormatException, IOException;

  /**
   * Obtains the specified metadata field's value for the given file.
   *
   * @param field the name associated with the metadata field
   * @return the value, or null if the field doesn't exist
   */
  Object getMetadataValue(String id, String field)
    throws FormatException, IOException;

  /**
   * Obtains the hashtable containing the metadata field/value pairs from
   * the given file.
   *
   * @param id the filename
   * @return the hashtable containing all metadata from the file
   */
  Hashtable getMetadata(String id) throws FormatException, IOException;

  /**
   * Sets the default metadata store for this reader.
   *
   * @param store a metadata store implementation.
   */
  void setMetadataStore(MetadataStore store);

  /**
   * Retrieves the current metadata store for this reader. You can be
   * assured that this method will <b>never</b> return a <code>null</code>
   * metadata store.
   * @return a metadata store implementation.
   */
  MetadataStore getMetadataStore(String id) throws FormatException, IOException;

  /**
   * Retrieves the current metadata store's root object. It is guaranteed that
   * all file parsing has been performed by the reader prior to retrieval.
   * Requests for a full populated root object should be made using this method.
   * @param id a fully qualified path to the file.
   * @return current metadata store's root object fully populated.
   * @throws IOException if there is an IO error when reading the file specified
   *   by <code>path</code>.
   * @throws FormatException if the file specified by <code>path</code> is of an
   *   unsupported type.
   */
  Object getMetadataStoreRoot(String id) throws FormatException, IOException;

  /**
   * A utility method for test reading a file from the command line,
   * and displaying the results in a simple display.
   */
  boolean testRead(String[] args) throws FormatException, IOException;

}

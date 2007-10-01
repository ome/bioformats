//
// IFormatReader.java
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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Interface for all biological file format readers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/IFormatReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/IFormatReader.java">SVN</a></dd></dl>
 */
public interface IFormatReader extends IFormatHandler {

  // -- Constants --

  /** File grouping options. */
  int MUST_GROUP = 0;
  int CAN_GROUP = 1;
  int CANNOT_GROUP = 2;

  // -- IFormatReader API methods --

  /** Checks if the given block is a valid header for this file format. */
  boolean isThisType(byte[] block);

  /** Determines the number of images in the current file. */
  int getImageCount();

  /** Checks if the images in the file are RGB. */
  boolean isRGB();

  /** Gets the size of the X dimension. */
  int getSizeX();

  /** Gets the size of the Y dimension. */
  int getSizeY();

  /** Gets the size of the Z dimension. */
  int getSizeZ();

  /** Gets the size of the C dimension. */
  int getSizeC();

  /** Gets the size of the T dimension. */
  int getSizeT();

  /**
   * Gets the pixel type.
   * @return the pixel type as an enumeration from <code>FormatTools</code>
   * <i>static</i> pixel types such as <code>INT8</code>.
   */
  int getPixelType();

  /**
   * Gets the effective size of the C dimension, guaranteeing that
   * getEffectiveSizeC() * getSizeZ() * getSizeT() == getImageCount()
   * regardless of the result of isRGB().
   */
  int getEffectiveSizeC();

  /** Gets the number of channels per RGB image (if not RGB, this returns 1). */
  int getRGBChannelCount();

  /** Gets whether the images are indexed color. */
  boolean isIndexed();

  /**
   * Returns false if isIndexed is false, or if isIndexed is true and the lookup
   * table represents "real" color data.  Returns true if isIndexed is true
   * and the lookup table is only present to aid in visualization.
   */
  boolean isFalseColor();

  /**
   * Gets the 8-bit color lookup table associated with
   * the most recently opened image.
   * If no images have been opened, or if isIndexed() returns false, then
   * this returns null.  Also, if getPixelType() returns anything other than
   * <code>INT8</code> or <code>UINT8</code>, this method will return null.
   */
  byte[][] get8BitLookupTable() throws FormatException, IOException;

  /**
   * Gets the 16-bit color lookup table associated with
   * the most recently opened image.
   * If no images have been opened, or if isIndexed() returns false, then
   * this returns null.  Also, if getPixelType() returns anything other than
   * <code>INT16</code> or <code>UINT16</code>, this method will return null.
   */
  short[][] get16BitLookupTable() throws FormatException, IOException;

  /**
   * Gets the lengths of each subdimension of C,
   * in fastest-to-sloweset rasterization order.
   */
  int[] getChannelDimLengths();

  /**
   * Gets the name of each subdimension of C,
   * in fastest-to-slowest rasterization order.
   * Common subdimensional types are enumerated in {@link FormatTools}.
   */
  String[] getChannelDimTypes();

  /** Get the size of the X dimension for the thumbnail. */
  int getThumbSizeX();

  /** Get the size of the Y dimension for the thumbnail. */
  int getThumbSizeY();

  /** Gets whether the data is in little-endian format. */
  boolean isLittleEndian();

  /**
   * Gets a five-character string representing the
   * dimension order within the file. Valid orders are:<ul>
   *   <li>XYCTZ</li>
   *   <li>XYCZT</li>
   *   <li>XYTCZ</li>
   *   <li>XYTZC</li>
   *   <li>XYZCT</li>
   *   <li>XYZTC</li>
   * </ul>
   * In cases where the channels are interleaved (e.g., CXYTZ), C will be
   * the first dimension after X and Y (e.g., XYCTZ) and the
   * {@link #isInterleaved(String)} method will return true.
   */
  String getDimensionOrder();

  /**
   * Gets whether the dimension order and sizes are known, or merely guesses.
   */
  boolean isOrderCertain();

  /**
   * Gets whether or not the channels are interleaved. This method exists
   * because X and Y must appear first in the dimension order. For
   * interleaved data, XYCTZ or XYCZT is used, and this method returns true.
   */
  boolean isInterleaved();

  /**
   * Gets whether or not the given sub-channel is interleaved. This method
   * exists because some data with multiple rasterized sub-dimensions within
   * C have one sub-dimension interleaved, and the other not&mdash;e.g.,
   * {@link loci.formats.in.SDTReader} handles spectral-lifetime data with
   * the interleaved lifetime bins and non-interleaved spectral channels.
   */
  boolean isInterleaved(int subC);

  /**
   * Obtains the specified image from the current file as a byte array.
   */
  byte[] openBytes(int no) throws FormatException, IOException;

  /**
   * Obtains the specified image from the current file into a pre-allocated byte
   * array of (sizeX * sizeY * bytesPerPixel).
   * @param no the image index within the file.
   * @param buf a pre-allocated buffer.
   * @return the pre-allocated buffer <code>buf</code> for convenience.
   * @throws FormatException if there was a problem parsing the metadata of the
   * file.
   * @throws IOException if there was a problem reading the file.
   */
  byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException;

  /** Obtains the specified image from the current file. */
  BufferedImage openImage(int no)
    throws FormatException, IOException;

  /**
   * Obtains a thumbnail for the specified image from the current file,
   * as a byte array.
   */
  byte[] openThumbBytes(int no) throws FormatException, IOException;

  /** Obtains a thumbnail for the specified image from the current file. */
  BufferedImage openThumbImage(int no)
    throws FormatException, IOException;

  /**
   * Closes the currently open file. If the flag is set, this is all that
   * happens; if unset, it is equivalent to calling
   * {@link IFormatHandler#close()}.
   */
  void close(boolean fileOnly) throws IOException;

  /** Gets the number of series in this file. */
  int getSeriesCount();

  /** Activates the specified series. */
  void setSeries(int no);

  /** Gets the currently active series. */
  int getSeries();

  /** Specifies whether or not to normalize float data. */
  void setNormalized(boolean normalize);

  /** Returns true if we should normalize float data. */
  boolean isNormalized();

  /** Specifies whether or not to collect metadata. */
  void setMetadataCollected(boolean collect);

  /** Returns true if we should collect metadata. */
  boolean isMetadataCollected();

  /**
   * Specifies whether or not to save proprietary metadata
   * in the MetadataStore.
   */
  void setOriginalMetadataPopulated(boolean populate);

  /**
   * Returns true if we should save proprietary metadata
   * in the MetadataStore.
   */
  boolean isOriginalMetadataPopulated();

  /** Specifies whether or not to force grouping in multi-file formats. */
  void setGroupFiles(boolean group);

  /** Returns true if we should group files in multi-file formats.*/
  boolean isGroupFiles();

  /** Returns true if this format's metadata is completely parsed. */
  boolean isMetadataComplete();

  /**
   * Returns an int indicating that we cannot, must, or might group the files
   * in a given dataset.
   */
  int fileGroupOption(String id) throws FormatException, IOException;

  /** Returns an array of filenames needed to open this dataset. */
  String[] getUsedFiles();

  /** Returns the current file. */
  String getCurrentFile();

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates.
   */
  int getIndex(int z, int c, int t);

  /**
   * Gets the Z, C and T coordinates corresponding
   * to the given rasterized index value.
   */
  int[] getZCTCoords(int index);

  /**
   * Obtains the specified metadata field's value for the current file.
   * @param field the name associated with the metadata field
   * @return the value, or null if the field doesn't exist
   */
  Object getMetadataValue(String field);

  /**
   * Obtains the hashtable containing the metadata field/value pairs from
   * the current file.
   * @return the hashtable containing all metadata from the file
   */
  Hashtable getMetadata();

  /** Obtains the core metadata values for the current file. */
  CoreMetadata getCoreMetadata();

  /**
   * Specifies whether ugly metadata (entries with unprintable characters,
   * and extremely large entries) should be discarded from the metadata table.
   */
  void setMetadataFiltered(boolean filter);

  /**
   * Returns true if ugly metadata (entries with unprintable characters,
   * and extremely large entries) are discarded from the metadata table.
   */
  boolean isMetadataFiltered();

  /**
   * Sets the default metadata store for this reader.
   * @param store a metadata store implementation.
   */
  void setMetadataStore(MetadataStore store);

  /**
   * Retrieves the current metadata store for this reader. You can be
   * assured that this method will <b>never</b> return a <code>null</code>
   * metadata store.
   * @return A metadata store implementation.
   */
  MetadataStore getMetadataStore();

  /**
   * Retrieves the current metadata store's root object. It is guaranteed that
   * all file parsing has been performed by the reader prior to retrieval.
   * Requests for a full populated root object should be made using this method.
   * @return Current metadata store's root object fully populated.
   */
  Object getMetadataStoreRoot();

  // -- Deprecated API methods --

  /** @deprecated Replaced by {@link #getImageCount()} */
  int getImageCount(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #isRGB()} */
  boolean isRGB(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getSizeX()} */
  int getSizeX(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getSizeY()} */
  int getSizeY(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getSizeZ()} */
  int getSizeZ(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getSizeC()} */
  int getSizeC(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getSizeT()} */
  int getSizeT(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getPixelType()} */
  int getPixelType(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getEffectiveSizeC()} */
  int getEffectiveSizeC(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getRGBChannelCount()} */
  int getRGBChannelCount(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getChannelDimLengths()} */
  int[] getChannelDimLengths(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getChannelDimTypes()} */
  String[] getChannelDimTypes(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getThumbSizeX()} */
  int getThumbSizeX(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getThumbSizeY()} */
  int getThumbSizeY(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #isLittleEndian()} */
  boolean isLittleEndian(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getDimensionOrder()} */
  String getDimensionOrder(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #isOrderCertain()} */
  boolean isOrderCertain(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #isInterleaved()} */
  boolean isInterleaved(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #isInterleaved(int)} */
  boolean isInterleaved(String id, int subC)
    throws FormatException, IOException;

  /** @deprecated Replaced by {@link #openImage(int)} */
  BufferedImage openImage(String id, int no)
    throws FormatException, IOException;

  /** @deprecated Replaced by {@link #openBytes(int)} */
  byte[] openBytes(String id, int no) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #openBytes(int, byte[])} */
  byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException;

  /** @deprecated Replaced by {@link #openThumbImage(int)} */
  BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException;

  /** @deprecated Replaced by {@link #openThumbBytes(int)} */
  byte[] openThumbBytes(String id, int no) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getSeriesCount()} */
  int getSeriesCount(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #setSeries(int)} */
  void setSeries(String id, int no) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getSeries()} */
  int getSeries(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getUsedFiles()} */
  String[] getUsedFiles(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getIndex(int, int, int)} */
  int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getZCTCoords(int)} */
  int[] getZCTCoords(String id, int index)
    throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getMetadataValue(String)} */
  Object getMetadataValue(String id, String field)
    throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getMetadata()} */
  Hashtable getMetadata(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getCoreMetadata()} */
  CoreMetadata getCoreMetadata(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getMetadataStore()} */
  MetadataStore getMetadataStore(String id) throws FormatException, IOException;

  /** @deprecated Replaced by {@link #getMetadataStoreRoot()} */
  Object getMetadataStoreRoot(String id) throws FormatException, IOException;

}

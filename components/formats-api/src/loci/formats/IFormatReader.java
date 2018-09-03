/*
 * #%L
 * Top-level reader and writer APIs
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

package loci.formats;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import loci.common.RandomAccessInputStream;
import loci.formats.meta.MetadataStore;

/**
 * Interface for all biological file format readers.
 */
public interface IFormatReader extends IFormatHandler {

  // -- Constants --

  /** File grouping options. */
  int MUST_GROUP = 0;
  int CAN_GROUP = 1;
  int CANNOT_GROUP = 2;

  // -- IFormatReader API methods --

  /**
   * Checks if the given file is a valid instance of this file format.
   *
   * @param open If true, and the file extension is insufficient to determine
   *   the file type, the file may be opened for further analysis, or other
   *   relatively expensive file system operations (such as file existence
   *   tests and directory listings) may be performed.  If false, file system
   *   access is not allowed.
   */
  boolean isThisType(String name, boolean open);

  /**
   * Checks if the given block is a valid header for this file format.
   * @see #isThisType(RandomAccessInputStream)
   */
  boolean isThisType(byte[] block);

  /**
   * Checks if the given stream is a valid stream for this file format.
   * The number of bytes read is format-dependent.
   *
   * @param stream A RandomAccessInputStream representing the file to check.
   *    The first byte in the stream is assumed to be the first byte
   *    in the file.
   * @return true if the file represented by the stream can be read by this
   *    reader; false otherwise.
   */
  boolean isThisType(RandomAccessInputStream stream) throws IOException;

  /** Determines the number of image planes in the current file. */
  int getImageCount();

  /**
   * Checks if the image planes in the file have more than one channel per
   * {@link #openBytes} call.
   * This method returns true if and only if {@link #getRGBChannelCount()}
   * returns a value greater than 1.
   */
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
   * @return the pixel type as an enumeration from {@link FormatTools}
   * <i>static</i> pixel types such as {@link FormatTools#INT8}.
   */
  int getPixelType();

  /**
   * Gets the number of valid bits per pixel. The number of valid bits per
   * pixel is always less than or equal to the number of bits per pixel
   * that correspond to {@link #getPixelType()}.
   */
  int getBitsPerPixel();

  /**
   * Gets the effective size of the C dimension, guaranteeing that
   * getEffectiveSizeC() * getSizeZ() * getSizeT() == getImageCount()
   * regardless of the result of isRGB().
   */
  int getEffectiveSizeC();

  /**
   * Gets the number of channels returned with each call to openBytes.
   * The most common case where this value is greater than 1 is for interleaved
   * RGB data, such as a 24-bit color image plane. However, it is possible for
   * this value to be greater than 1 for non-interleaved data, such as an RGB
   * TIFF with Planar rather than Chunky configuration.
   */
  int getRGBChannelCount();

  /**
   * Gets whether the image planes are indexed color.
   * This value has no impact on {@link #getSizeC()},
   * {@link #getEffectiveSizeC()} or {@link #getRGBChannelCount()}.
   */
  boolean isIndexed();

  /**
   * Returns false if {@link #isIndexed()} is false, or if {@link #isIndexed()}
   * is true and the lookup table represents "real" color data. Returns true
   * if {@link #isIndexed()} is true and the lookup table is only present to aid
   * in visualization.
   */
  boolean isFalseColor();

  /**
   * Gets the 8-bit color lookup table associated with
   * the most recently opened image.
   * If no image planes have been opened, or if {@link #isIndexed()} returns
   * false, then this may return null. Also, if {@link #getPixelType()} returns
   * anything other than {@link FormatTools#INT8} or {@link FormatTools#UINT8},
   * this method will return null.
   */
  byte[][] get8BitLookupTable() throws FormatException, IOException;

  /**
   * Gets the 16-bit color lookup table associated with
   * the most recently opened image.
   * If no image planes have been opened, or if {@link #isIndexed()} returns
   * false, then this may return null. Also, if {@link #getPixelType()} returns
   * anything other than {@link FormatTools#INT16} or {@link
   * FormatTools#UINT16}, this method will return null.
   */
  short[][] get16BitLookupTable() throws FormatException, IOException;

  Modulo getModuloZ();

  Modulo getModuloC();

  Modulo getModuloT();

  /** Get the size of the X dimension for the thumbnail. */
  int getThumbSizeX();

  /** Get the size of the Y dimension for the thumbnail. */
  int getThumbSizeY();

  /** Gets whether the data is in little-endian format. */
  boolean isLittleEndian();

  /**
   * Gets a five-character string representing the
   * dimension order in which planes will be returned. Valid orders are:<ul>
   *   <li>XYCTZ</li>
   *   <li>XYCZT</li>
   *   <li>XYTCZ</li>
   *   <li>XYTZC</li>
   *   <li>XYZCT</li>
   *   <li>XYZTC</li>
   * </ul>
   * In cases where the channels are interleaved (e.g., CXYTZ), C will be
   * the first dimension after X and Y (e.g., XYCTZ) and the
   * {@link #isInterleaved()} method will return true.
   */
  String getDimensionOrder();

  /**
   * Gets whether the dimension order and sizes are known, or merely guesses.
   */
  boolean isOrderCertain();

  /**
   * Gets whether the current series is a lower resolution copy of a different
   * series.
   */
  boolean isThumbnailSeries();

  /**
   * Gets whether or not the channels are interleaved. This method exists
   * because X and Y must appear first in the dimension order. For
   * interleaved data, {@link #getDimensionOrder()} returns XYCTZ or XYCZT,
   * and this method returns true.
   *
   * Note that this flag returns whether or not the data returned by
   * {@link #openBytes(int)} is interleaved.  In most cases, this will
   * match the interleaving in the original file, but for some formats (e.g.
   * TIFF) channel re-ordering is done internally and the return value of
   * this method will not match what is in the original file.
   */
  boolean isInterleaved();

  /**
   * Gets whether or not the given sub-channel is interleaved. This method
   * exists because some data with multiple rasterized sub-dimensions within
   * C have one sub-dimension interleaved, and the other not&mdash;e.g.,
   * {@link loci.formats.in.SDTReader} handles spectral-lifetime data with
   * interleaved lifetime bins and non-interleaved spectral channels.
   */
  boolean isInterleaved(int subC);

  /**
   * Obtains the specified image plane from the current file as a byte array.
   * @see #openBytes(int, byte[])
   */
  byte[] openBytes(int no) throws FormatException, IOException;

  /**
   * Obtains a sub-image of the specified image plane,
   * whose upper-left corner is given by (x, y).
   */
  byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException;

  /**
   * Obtains the specified image plane from the current file into a
   * pre-allocated byte array of
   * (sizeX * sizeY * bytesPerPixel * RGB channel count).
   *
   * @param no the image index within the file.
   * @param buf a pre-allocated buffer.
   * @return the pre-allocated buffer <code>buf</code> for convenience.
   * @throws FormatException if there was a problem parsing the metadata of the
   *   file.
   * @throws IOException if there was a problem reading the file.
   */
  byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException;

  /**
   * Obtains a sub-image of the specified image plane
   * into a pre-allocated byte array.
   *
   * @param no the image index within the file.
   * @param buf a pre-allocated buffer.
   * @param x X coordinate of the upper-left corner of the sub-image
   * @param y Y coordinate of the upper-left corner of the sub-image
   * @param w width of the sub-image
   * @param h height of the sub-image
   * @return the pre-allocated buffer <code>buf</code> for convenience.
   * @throws FormatException if there was a problem parsing the metadata of the
   *   file.
   * @throws IOException if there was a problem reading the file.
   */
  byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException;

  /**
   * Obtains the specified image plane (or sub-image thereof) in the reader's
   * native data structure. For most readers this is a byte array; however,
   * some readers call external APIs that work with other types such as
   * {@link java.awt.image.BufferedImage}. The openPlane method exists to
   * maintain generality and efficiency while avoiding pollution of the API
   * with AWT-specific logic.
   *
   * @see loci.formats.FormatReader
   * @see loci.formats.gui.BufferedImageReader
   */
  Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException;

  /**
   * Obtains a thumbnail for the specified image plane from the current file,
   * as a byte array.
   */
  byte[] openThumbBytes(int no) throws FormatException, IOException;

  /**
   * Closes the currently open file. If the flag is set, this is all that
   * happens; if unset, it is equivalent to calling
   * {@link IFormatHandler#close()}.
   */
  void close(boolean fileOnly) throws IOException;

  /** Gets the number of series in this file. */
  int getSeriesCount();

  /** Activates the specified series. This also resets the resolution to 0. */
  void setSeries(int no);

  /** Gets the currently active series. */
  int getSeries();

  /** Specifies whether or not to normalize float data. */
  void setNormalized(boolean normalize);

  /** Returns true if we should normalize float data. */
  boolean isNormalized();

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

  /**
   * Specifies whether or not to force grouping in multi-file formats.
   *
   * @see #fileGroupOption(String)
   * @see #isGroupFiles()
   */
  void setGroupFiles(boolean group);

  /**
   * Returns true if we should group files in multi-file formats.
   *
   * @see #setGroupFiles(boolean)
   * @see #fileGroupOption(String)
   */
  boolean isGroupFiles();

  /** Returns true if this format's metadata is completely parsed. */
  boolean isMetadataComplete();

  /**
   * Returns an indication of whether the files in a multi-file dataset can
   * be handled individually.  This method is only useful for formats and
   * datasets which contain multiple files.
   *
   * @param id a file in the multi-file dataset
   * @return an int indicating that we cannot, must, or might group the files.
   *    A return value of {@link FormatTools#MUST_GROUP} indicates that the
   *    files cannot be handled separately; the reader will always detect and
   *    read all files in the dataset.  {@link FormatTools#CAN_GROUP} indicates
   *    that the files may be handled separately, but file grouping must then
   *    be disabled via {@link #setGroupFiles(boolean)}.
   *    {@link FormatTools#CANNOT_GROUP} indicates that the files must be handled
   *    separately; the reader will not attempt to read all files in the dataset
   *    (this is rare).
   *
   * @see FormatTools#MUST_GROUP
   * @see FormatTools#CAN_GROUP
   * @see FormatTools#CANNOT_GROUP
   */
  int fileGroupOption(String id) throws FormatException, IOException;

  /**
   * Returns an array of filenames needed to open this dataset.
   * The first element in the array is expected to be the path passed to
   * {@link #setId(String)}.  The remaining elements are expected to be in a
   * consistent order; if a directory listing is necessary to build the list
   * then it should be sorted first.
   */
  String[] getUsedFiles();

  /**
   * Returns an array of filenames needed to open this dataset.
   * If the 'noPixels' flag is set, then only files that do not contain
   * pixel data will be returned.
   *
   * The first element in the array is expected to be the path passed to
   * {@link #setId(String)}, if appropriate based upon 'noPixels'.
   * The remaining elements are expected to be in a consistent order;
   * if a directory listing is necessary to build the list then it should
   * be sorted first.
   */
  String[] getUsedFiles(boolean noPixels);

  /**
   * Returns an array of filenames needed to open the current series.
   *
   * The first element in the array is expected to be the path passed to
   * {@link #setId(String)}.  The remaining elements are expected to be in a
   * consistent order; if a directory listing is necessary to build the list
   * then it should be sorted first.
   */
  String[] getSeriesUsedFiles();

  /**
   * Returns an array of filenames needed to open the current series.
   * If the 'noPixels' flag is set, then only files that do not contain
   * pixel data will be returned.
   *
   * The first element in the array is expected to be the path passed to
   * {@link #setId(String)}, if appropriate based upon 'noPixels'.
   * The remaining elements are expected to be in a consistent order;
   * if a directory listing is necessary to build the list then it should
   * be sorted first.
   */
  String[] getSeriesUsedFiles(boolean noPixels);

  /**
   * Returns an array of FileInfo objects representing the files needed
   * to open this dataset.
   * If the 'noPixels' flag is set, then only files that do not contain
   * pixel data will be returned.
   */
  FileInfo[] getAdvancedUsedFiles(boolean noPixels);

  /**
   * Returns an array of FileInfo objects representing the files needed to
   * open the current series.
   * If the 'noPixels' flag is set, then only files that do not contain
   * pixel data will be returned.
   */
  FileInfo[] getAdvancedSeriesUsedFiles(boolean noPixels);

  /** Returns the current file. */
  String getCurrentFile();

  /** Returns the list of domains represented by the current file. */
  String[] getDomains();

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates (real sizes).
   */
  int getIndex(int z, int c, int t);

  /**
   * Gets the rasterized index corresponding to the given Z, C, T,
   * moduloZ, moduloC and moduloT coordinates (effective sizes).  Note
   * that the Z, C and T coordinates take the modulo dimension sizes
   * into account.  The effective size for each of these dimensions is
   * limited to the total size of the dimension divided by the modulo
   * size.
   */
  int getIndex(int z, int c, int t, int moduloZ, int moduloC, int moduloT);

  /**
   * Gets the Z, C and T coordinates (real sizes) corresponding to the
   * given rasterized index value.
   */
  int[] getZCTCoords(int index);

  /**
   * Gets the Z, C, T, moduloZ, moduloC and moduloT coordinates
   * (effective sizes) corresponding to the given rasterized index
   * value.  Note that the Z, C and T coordinates are not the same as
   * those returned by getZCTCoords(int) because the size of the
   * modulo dimensions is taken into account.  The effective size for
   * each of these dimensions is limited to the total size of the
   * dimension divided by the modulo size.
   */
  int[] getZCTModuloCoords(int index);

  /**
   * Obtains the specified metadata field's value for the current file.
   * @param field the name associated with the metadata field
   * @return the value, or null if the field doesn't exist
   */
  Object getMetadataValue(String field);

  /**
   * Obtains the specified metadata field's value for the current series
   * in the current file.
   * @param field the name associated with the metadata field
   * @return the value, or null if the field doesn't exist
   */
  Object getSeriesMetadataValue(String field);

  /**
   * Obtains the hashtable containing the metadata field/value pairs from
   * the current file.
   * @return the hashtable containing all non-series-specific metadata
   * from the file
   */
  Hashtable<String, Object> getGlobalMetadata();

  /**
   * Obtains the hashtable containing metadata field/value pairs from the
   * current series in the current file.
   */
  Hashtable<String, Object> getSeriesMetadata();

  /** Obtains the core metadata values for the current file. */
  List<CoreMetadata> getCoreMetadataList();

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

  /**
   * Retrieves all underlying readers.
   * Returns null if there are no underlying readers.
   */
  IFormatReader[] getUnderlyingReaders();

  /**
   * Returns true if the named file is expected to be the only
   * file in the dataset.  For single-file formats, always returns true.
   */
  boolean isSingleFile(String id) throws FormatException, IOException;

  /**
   * Returns the number of parent directories that are important when
   * processing the given list of files.  The number of directories is relative
   * to the common parent.  For example, given a list with these two files:
   *
   * /path/to/file/foo
   * /path/to/file/that/is/related
   *
   * A return value of 0 indicates that "/path/to/file/" is irrelevant.
   * A return value of 1 indicates that "/path/to/" is irrelevant.
   * Return values less than 0 are invalid.
   *
   * All listed files are assumed to belong to datasets of the same format.
   */
  int getRequiredDirectories(String[] files) throws FormatException, IOException;

  /** Returns a short description of the dataset structure. */
  String getDatasetStructureDescription();

  /** Returns a list of scientific domains in which this format is used. */
  String[] getPossibleDomains(String id) throws FormatException, IOException;

  /** Returns true if this format supports multi-file datasets. */
  boolean hasCompanionFiles();

  /** Returns the optimal sub-image width for use with openBytes. */
  int getOptimalTileWidth();

  /** Returns the optimal sub-image height for use with openBytes. */
  int getOptimalTileHeight();

  // -- Sub-resolution API methods --

  /** Returns the first core index corresponding to the specified series. */
  int seriesToCoreIndex(int series);

  /** Returns the series corresponding to the specified core index. */
  int coreIndexToSeries(int index);

  /** Return the index into CoreMetadata of the current resolution/series. */
  int getCoreIndex();

  /**
   * Set the current resolution/series (ignores subresolutions).
   *
   * Equivalent to setSeries, but with flattened resolutions always
   * set to false.
   */
  void setCoreIndex(int no);

  /**
   * Return the number of resolutions for the current series.
   *
   * Resolutions are stored in descending order, so the largest resolution is
   * first and the smallest resolution is last.
   */
  int getResolutionCount();

  /**
   * Set the resolution level.
   *
   * @see #getResolutionCount()
   */
  void setResolution(int resolution);

  /**
   * Get the current resolution level.
   *
   * @see #getResolutionCount()
   */
  int getResolution();

  /** Return whether or not resolution flattening is enabled. */
  boolean hasFlattenedResolutions();

  /** Set whether or not to flatten resolutions into individual series. */
  void setFlattenedResolutions(boolean flatten);

  /**
   * Reopen any files that were closed, and which are expected to be open
   * while the reader is open.  This assumes that {@link #setId} has been
   * called, but close(false) has not been called.
   */
  void reopenFile() throws IOException;
}

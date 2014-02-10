/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
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
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

#ifndef OME_BIOFORMATS_FORMATREADER_H
#define OME_BIOFORMATS_FORMATREADER_H

#include <string>
#include <vector>
#include <map>

#include <ome/bioformats/CoreMetadata.h>
#include <ome/bioformats/FileInfo.h>
#include <ome/bioformats/FormatHandler.h>
#include <ome/bioformats/MetadataConfigurable.h>
#include <ome/bioformats/PixelBuffer.h>

#include <ome/xml/meta/MetadataStore.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Interface for all biological file format readers.
     *
     * @note No IOException in C++.
     *
     * @note Metadata values can't be Object in C++; initially
     * restricted to std::string only.
     */
    class FormatReader : public FormatHandler, MetadataConfigurable
    {
    public:
      /// File grouping options.
      enum FileGroupOption
        {
          MUST_GROUP,  ///< Files must be grouped.
          CAN_GROUP,   ///< Files may be grouped.
          CANNOT_GROUP ///< Files can not be grouped.
        };

      /**
       * An array of bytes.
       *
       * @todo Note that this could be switched for a pair of pointers to allow zero-copy access.
       */
      typedef std::vector<uint8_t> byte_array;

      /// Metadata map type for key-value data.
      typedef std::map<std::string, std::string> metadata_map;

    protected:
      /// Constructor.
      FormatReader();

    public:
      /// Destructor.
      virtual
      ~FormatReader();

      /**
       * Check if the given file is a valid instance of this file format.
       *
       * @param name the file to open for checking.
       * @param open If @c true, and the file extension is
       *   insufficient to determine the file type, the file may be
       *   opened for further analysis, or other relatively expensive
       *   file system operations (such as file existence tests and
       *   directory listings) may be performed.  If @c false, file
       *   system access is not allowed.
       * @returns @c true if the file is valid, @c false otherwise.
       *
       * @todo Could this method be static and/or const?
       */
      virtual
      bool
      isThisType(const std::string& name,
                 bool               open = true) = 0;

      /**
       * Check if the given block is a valid header for this file format.
       *
       * @param block the block to check.
       * @returns @c true if the file is valid, @c false otherwise.
       *
       * @todo Could this method be static and/or const?
       */
      virtual
      bool
      isThisType(const byte_array& block) = 0;

      /**
       * Check if the given input stream is a valid stream for this file format.
       *
       * @param stream the input stream to check.
       * @returns @c true if the file is valid, @c false otherwise.
       *
       * @todo Could this method be static and/or const?
       */
      virtual
      bool
      isThisType(std::istream& stream) = 0;

      /**
       * Check if the given input stream is a valid stream for this file format.
       *
       * @param stream the input stream to check.
       * @returns @c true if the file is valid, @c false otherwise.
       *
       * @todo Could this method be static and/or const?
       */
      virtual
      bool
      isThisType(std::FILE *stream) = 0;

      /**
       * Determine the number of image planes in the current file.
       *
       * @returns the number of image planes.
       */
      virtual
      CoreMetadata::image_size_type
      getImageCount() = 0;

      /**
       * Check if the image planes in the file have more than one channel per
       * openBytes() call.
       *
       * @returns @c true if and only if getRGBChannelCount() returns
       * a value greater than 1, @c false otherwise.
       */
      virtual
      bool
      isRGB() const = 0;

      /**
       * Get the size of the X dimension.
       *
       * @returns the X dimension size.
       */
      virtual
      CoreMetadata::dimension_size_type
      getSizeX() const = 0;

      /**
       * Get the size of the Y dimension.
       *
       * @returns the Y dimension size.
       */
      virtual
      CoreMetadata::dimension_size_type
      getSizeY() const = 0;

      /**
       * Get the size of the Z dimension.
       *
       * @returns the Z dimension size.
       */
      virtual
      CoreMetadata::dimension_size_type
      getSizeZ() const = 0;

      /**
       * Get the size of the T dimension.
       *
       * @returns the T dimension size.
       */
      virtual
      CoreMetadata::dimension_size_type
      getSizeT() const = 0;

      /**
       * Get the size of the C dimension.
       *
       * @returns the C dimension size.
       */
      virtual
      CoreMetadata::dimension_size_type
      getSizeC() const = 0;

      /**
       * Get the pixel type.
       *
       * @returns the pixel type.
       */
      virtual
      ome::xml::model::enums::PixelType
      getPixelType() const = 0;

      /**
       * Get the number of valid bits per pixel.
       *
       * The number of valid bits per pixel is always less than or
       * equal to the number of bits per pixel that correspond to
       * getPixelType().
       *
       * @returns the number of valid bits per pixel.
       */
      virtual
      CoreMetadata::pixel_size_type
      getBitsPerPixel() const = 0;

      /**
       * Get the effective size of the C dimension
       *
       * This guarantees that
       * \code{.cpp}
       * getEffectiveSizeC() * getSizeZ() * getSizeT() == getImageCount()
       * \endcode
       * regardless of the result of isRGB().
       *
       * @returns the effective C dimension size.
       */
      virtual
      CoreMetadata::dimension_size_type
      getEffectiveSizeC() const = 0;

      /**
       * Get the number of channels returned with each call to openBytes().
       *
       * The most common case where this value is greater than 1 is for interleaved
       * RGB data, such as a 24-bit color image plane. However, it is possible for
       * this value to be greater than 1 for non-interleaved data, such as an RGB
       * TIFF with Planar rather than Chunky configuration.
       *
       * @returns the number of channels.
       */
      virtual
      CoreMetadata::dimension_size_type
      getRGBChannelCount() const = 0;

      /**
       * Get whether the image planes are indexed color.
       *
       * This value does not affect getSizeC(), getEffectiveSizeC() or
       * getRGBChannelCount().
       *
       * @returns @c true if indexed, @c false otherwise.
       */
      virtual
      bool
      isIndexed() const = 0;

      /**
       * Get whether the image planes are false color.
       *
       * This will be @c false if isIndexed() is @c false, or if
       * isIndexed() is @c true and the lookup table represents "real"
       * color data.  This will be @c true if isIndexed() is @c true
       * and the lookup table is only present to aid in visualization.
       *
       * @returns @c true if false color, @c false otherwise.
       */
      virtual
      bool
      isFalseColor() const = 0;

      /**
       * Get the 8-bit color lookup table associated with the most
       * recently opened image.
       *
       * If no image planes have been opened, or if isIndexed()
       * returns @c false, then this may throw an exception. If
       * getPixelType() returns anything other
       * ome::xml::model::enums::PixelType::UINT8 or
       * ome::xml::model::enums::PixelType::INT8 this method will
       * throw an exception.
       *
       * @param buf the destination pixel buffer.
       *
       * @todo use a more specific buffer type.
       * @todo throw on failure.
       */
      virtual
      void
      get8BitLookupTable(PixelBufferRaw& buf) const = 0;

      /**
       * Get the 16-bit color lookup table associated with the most
       * recently opened image.
       *
       * If no image planes have been opened, or if isIndexed()
       * returns @c false, then this may throw and exception. Also, if
       * getPixelType() returns anything other than
       * ome::xml::model::enums::PixelType::UINT16 or
       * ome::xml::model::enums::PixelType::INT16 this method will
       * throw an exception.
       *
       * @param buf the destination pixel buffer.
       *
       * @todo use a more specific buffer type.
       * @todo throw on failure.
       */
      virtual
      void
      get16BitLookupTable(PixelBufferRaw& buf) const = 0;

      /**
       * Get the Modulo subdivision of the Z dimension.
       *
       * @returns the Modulo defining the Z dimension subdivision.
       */
      virtual
      Modulo&
      getModuloZ() = 0;

      /**
       * Get the Modulo subdivision of the Z dimension.
       *
       * @returns the Modulo defining the Z dimension subdivision.
       */
      virtual
      const Modulo&
      getModuloZ() const = 0;

      /**
       * Get the Modulo subdivision of the T dimension.
       *
       * @returns the Modulo defining the T dimension subdivision.
       */
      virtual
      Modulo&
      getModuloT() = 0;

      /**
       * Get the Modulo subdivision of the T dimension.
       *
       * @returns the Modulo defining the T dimension subdivision.
       */
      virtual
      const Modulo&
      getModuloT() const = 0;

      /**
       * Get the Modulo subdivision of the C dimension.
       *
       * @returns the Modulo defining the C dimension subdivision.
       */
      virtual
      Modulo&
      getModuloC() = 0;

      /**
       * Get the Modulo subdivision of the C dimension.
       *
       * @returns the Modulo defining the C dimension subdivision.
       */
      virtual
      const Modulo&
      getModuloC() const = 0;

      /**
       * Get the thumbnail size of the X dimension.
       *
       * @returns the X dimension thumbnail size.
       */
      virtual
      CoreMetadata::dimension_size_type
      getThumbSizeX() const = 0;

      /**
       * Get the thumbnail size of the Y dimension.
       *
       * @returns the Y dimension thumbnail size.
       */
      virtual
      CoreMetadata::dimension_size_type
      getThumbSizeY() const = 0;

      /**
       * Get whether the data is in little-endian format.
       *
       * @returns @c false if big endian, @c true if little endian.
       */
      virtual
      bool
      isLittleEndian() const = 0;

      /**
       * Get the dimension order.
       *
       * The dimension order is a five-character string representing the
       * order in which planes will be returned. Valid orders are:
       *   - @c XYCTZ
       *   - @c XYCZT
       *   - @c XYTCZ
       *   - @c XYTZC
       *   - @c XYZCT
       *   - @c XYZTC
       *
       * In cases where the channels are interleaved (e.g. @c CXYTZ), @c C will be
       * the first dimension after @c X and @c Y (e.g. @c XYCTZ) and the
       * isInterleaved() method will return @c true.
       *
       * @returns the dimension order.
       */
      virtual
      const std::string&
      getDimensionOrder() const = 0;

      /**
       * Get whether the dimension order and sizes are known, or merely guesses.
       *
       * @returns @c true if the order is known, @c false otherwise.
       */
      virtual
      bool
      isOrderCertain() const = 0;

      /**
       * Get whether the current series is a lower resolution copy of a different
       * series.
       *
       * @returns @c true if a low resolution copy, @c false otherwise.
       */
      virtual
      bool
      isThumbnailSeries() const = 0;

      /**
       * Get whether or not the channels are interleaved.
       *
       * This method exists because @c X and @c Y must appear first in
       * the dimension order. For interleaved data,
       * getDimensionOrder() returns @c XYCTZ or @c XYCZT and this
       * method returns @c true.
       *
       * Note that this flag returns whether or not the data returned
       * by openBytes() is interleaved.  In most cases, this will
       * match the interleaving in the original file, but for some
       * formats (e.g. TIFF) channel re-ordering is done internally
       * and the return value of this method will not match what is in
       * the original file.
       *
       * @returns @c true if the channels are interleaved, @c false
       * otherwise.
       */
      virtual
      bool
      isInterleaved() const = 0;

      /**
       * Get whether or not the given sub-channel is interleaved.
       *
       * This method exists because some data with multiple rasterized
       * sub-dimensions within @c C have one sub-dimension
       * interleaved, and the other not.  For example, @c SDTReader
       * handles spectral-lifetime data with interleaved lifetime bins
       * and non-interleaved spectral channels.
       *
       * @param subC the subchannel index.
       * @returns @c true if the sub-channel is interleaved, @c false
       * otherwise.
       */
      virtual
      bool
      isInterleaved(CoreMetadata::dimension_size_type subC) const = 0;

      /**
       * Obtain an image plane.
       *
       * Obtain and copy the image plane from the current file into a
       * PixelBuffer of size
       *
       * \code{.cpp}
       * getSizeX * getSizeY * bytesPerPixel * getRGBChannelCount()
       * \endcode
       *
       * @param no the image index within the file.
       * @param buf the destination pixel buffer.
       * @throws FormatException if there was a problem parsing the
       *   metadata of the file.
       */
      virtual
      void
      openBytes(CoreMetadata::image_size_type no, PixelBufferRaw& buf) const = 0;

      /**
       * Obtain a sub-image of an image plane.
       *
       * Obtain and copy the sub-image of an image plane from the
       * current file into a PixelBuffer of size
       *
       * \code{.cpp}
       * w * h * bytesPerPixel * getRGBChannelCount()
       * \endcode
       *
       * @param no the image index within the file.
       * @param buf the destination pixel buffer.
       * @param x the @c X coordinate of the upper-left corner of the sub-image.
       * @param y the @c Y coordinate of the upper-left corner of the sub-image.
       * @param w the width of the sub-image.
       * @param h the height of the sub-image.
       * @throws FormatException if there was a problem parsing the metadata of the
       *   file.
       */
      virtual
      void
      openBytes(CoreMetadata::image_size_type     no,
                PixelBufferRaw&                   buf,
                CoreMetadata::dimension_size_type x,
                CoreMetadata::dimension_size_type y,
                CoreMetadata::dimension_size_type w,
                CoreMetadata::dimension_size_type h) const = 0;

      /**
       * Obtain a thumbnail of an image plane.
       *
       * Obtail and copy the thumbnail for the specified image plane
       * from the current file into a PixelBuffer.
       *
       * @param no the image index within the file.
       * @param buf the destination pixel buffer.
       */
      virtual
      void
      openThumbBytes(CoreMetadata::image_size_type no,
                     PixelBufferRaw&               buf) const = 0;

      // Documented in superclass.
      virtual
      void
      close(bool fileOnly) = 0;

      /**
       * Get the number of image series in this file.
       *
       * @returns the number of image series.
       */
      virtual
      CoreMetadata::image_size_type
      getSeriesCount() const = 0;

      /**
       * Set the active series.
       *
       * @note This also resets the resolution to 0.
       *
       * @param no the series to activate.
       */
      virtual
      void
      setSeries(CoreMetadata::image_size_type no) = 0;

      /**
       * Get the active series.
       *
       * @returns the active series.
       */
      virtual
      CoreMetadata::image_size_type
      getSeries() const = 0;

      /**
       * Set float normalization.
       *
       * @param normalize @c true to enable normalization, or @c false
       * to disable.
       */
      virtual
      void
      setNormalized(bool normalize) = 0;

      /**
       * Get float normalization
       *
       *
       * @returns @c true if float normalization is enabled, @c false
       * otherwise.
       **/
      virtual
      bool
      isNormalized() const = 0;

      /**
       * Specifies whether or not to save proprietary metadata
       * in the MetadataStore.
       */
      virtual
      void
      setOriginalMetadataPopulated(bool populate) = 0;

      /**
       * Get proprietary metadata storage.
       *
       * @returns @c true if proprietary metadata is saved in the
       * MetadataStore, @c false otherwise.
       *
       * @todo The name of this method is awful.
       */
      virtual
      bool
      isOriginalMetadataPopulated() const = 0;

      /**
       * Set file grouping.
       *
       * This specifies whether or not to force grouping in multi-file formats.
       *
       * @param group @c true to enable grouping, @c false to disable.
       */
      virtual
      void
      setGroupFiles(bool group) = 0;

      /**
       * Get file grouping.
       *
       * @returns @c true if grouping is enabled, @c false otherwise.
       */
      virtual
      bool
      isGroupFiles() const = 0;

      /**
       * Get status of metadata parsing.
       *
       * @returns @c true if this format's metadata is completely
       * parsed, @c false otherwise.
       */
      virtual
      bool
      isMetadataComplete() const = 0;

      /**
       * Returns an enum indicating that we cannot, must, or might
       * group the files in this dataset.
       *
       * @param id filename to check.
       * @returns the grouping option for the specified file.
       */
      virtual
      FileGroupOption
      fileGroupOption(const std::string& id) = 0;

      /**
       * Get the files used by this dataset.
       *
       * @param noPixels exclude pixel data files if @c true, or include
       * them if @c false.
       * @returns a list of filenames.
       */
      virtual
      const std::vector<std::string>
      getUsedFiles(bool noPixels = false) const = 0;

      /**
       * Get the files used by the active series.
       *
       * @param noPixels exclude pixel data files if @c true, or include
       * them if @c false.
       * @returns a list of filenames.
       */
      virtual
      const std::vector<std::string>
      getSeriesUsedFiles(bool noPixels = false) = 0;

      /**
       * Get the files used by this dataset.
       *
       * @param noPixels exclude pixel data files if @c true, or include
       * them if @c false.
       * @returns a list of FileInfo objects representing each used
       * file.
       */
      virtual
      std::vector<FileInfo>
      getAdvancedUsedFiles(bool noPixels) = 0;

      /**
       * Get the files used by the active series.
       *
       * @param noPixels exclude pixel data files if @c true, or include
       * them if @c false.
       * @returns a list of FileInfo objects representing each used
       * file.
       */
      virtual
      std::vector<FileInfo>
      getAdvancedSeriesUsedFiles(bool noPixels) = 0;

      /**
       * Get the currently open file.
       *
       * @returns the filename.
       */
      virtual
      const std::string&
      getCurrentFile() = 0;

      /**
       * Get the domains represented by the current file.
       *
       * @returns a list of domains.
       */
      virtual
      const std::vector<std::string>&
      getDomains() = 0;

      /**
       * Get the linear index of a @c Z, @c C and @c T coordinate.
       *
       * The index is computed using the DimensionOrder.
       *
       * @param z the @c Z coordinate.
       * @param c the @c C coordinate.
       * @param t the @c T coordinate.
       * @returns the linear index.
       *
       * @todo unify with the pixel buffer dimension indexes.
       * @todo Don't use separate values to match the return of
       * getZCTCoords.
       */
      virtual
      int
      getIndex(CoreMetadata::dimension_size_type z,
               CoreMetadata::dimension_size_type c,
               CoreMetadata::dimension_size_type t) = 0;

      /**
       * Get the @c Z, @c C and @c T coordinate of a linear index.
       *
       * @param index the linear index.
       * @returns an array containing @c Z, @c C and @c T values.
       *
       * @todo unify with the pixel buffer dimension indexes.
       */
      virtual
      std::array<int, 3>
      getZCTCoords(int index) = 0;

      /**
       * Get a global metadata value.
       *
       * Obtain the specified metadata field's value for the current
       * dataset.
       *
       * @param field the name associated with the metadata field.
       * @returns the value.
       *
       * @todo throw exception if missing.
       */
      virtual
      const std::string&
      getMetadataValue(const std::string& field) = 0;

      /**
       * Get a series metadata value.
       *
       * Obtain the specified metadata field's value for the active series
       * in the current dataset.
       *
       * @param field the name associated with the metadata field.
       * @returns the value.
       *
       * @todo throw exception if missing.
       */
      virtual
      const std::string&
      getSeriesMetadataValue(const std::string& field) = 0;

      /**
       * Get global metadata map.
       *
       * Obtain the map containing the global metadata field/value
       * pairs the current dataset.
       *
       * @returns the global metadata map.
       */
      virtual
      const metadata_map&
      getGlobalMetadata() const = 0;

      /**
       * Get series metadata map.
       *
       * Obtain the map containing the series metadata field/value
       * pairs the active series in the current dataset.
       *
       * @returns the series metadata map.
       */
      virtual
      const metadata_map&
      getSeriesMetadata() const = 0;

      /**
       * Get the core metadata.
       *
       * A CoreMetadata object exists to describe the each series in the
       * dataset.
       *
       * @returns a const reference to the core metadata.
       */
      virtual
      const std::vector<CoreMetadata>&
      getCoreMetadataList() const = 0;

      /**
       * Set metadata filtering.
       *
       * If filtering is enabled, "ugly" metadata (entries with
       * unprintable characters, and extremely large entries) should
       * be discarded from the metadata table.
       *
       * @param filter @c true to enable filtering, @c false to
       * disable.
       */
      virtual
      void
      setMetadataFiltered(bool filter) = 0;

      /**
       * Get metadata filtering.
       *
       * @returns @c true if metadata filtering is enabled, @c false
       * if disabled.
       */
      virtual
      bool
      isMetadataFiltered() = 0;

      /**
       * Set the default metadata store for this reader.
       *
       * @param store a metadata store implementation.
       */
      virtual
      void
      setMetadataStore(std::shared_ptr< ::ome::xml::meta::MetadataStore>& store) = 0;

      /**
       * Get the current metadata store for this reader.
       *
       * @returns the metadata store, which will never be @c null.
       */
      virtual
      const std::shared_ptr< ::ome::xml::meta::MetadataStore>&
      getMetadataStore() const = 0;

      /**
       * Get the current metadata store for this reader.
       *
       * @returns the metadata store, which will never be @c null.
       */
      virtual
      std::shared_ptr< ::ome::xml::meta::MetadataStore>&
      getMetadataStore() = 0;

      /**
       * Get all underlying readers.
       *
       * @note If there are no underlying readers, the list will be
       * empty.
       *
       * @returns a list of readers.
       */
      virtual
      std::vector<std::shared_ptr<FormatReader> >
      getUnderlyingReaders() const = 0;

      /**
       * Is this a single-file format?
       *
       * @param id filename to check.
       * @returns @c true if this is a single-file format, @c false
       * otherwise.
       *
       * @throws FormatException if there was a problem parsing the
       *   metadata of the file.
       */
      virtual
      bool
      isSingleFile(const std::string& id) = 0;

      /**
       * Get required parent directories.
       *
       * Get the number of parent directories that are important when
       * processing the given list of files.  The number of
       * directories is relative to the common parent.  For example,
       * given a list with these two files:
       *
       *   - @c /path/to/file/foo
       *   - @c /path/to/file/that/is/related
       *
       * A return value of 0 indicates that @c /path/to/file/ is
       * irrelevant.  A return value of 1 indicates that @c /path/to/
       * is irrelevant.  Return values less than 0 are invalid.
       *
       * All listed files are assumed to belong to datasets of the
       * same format.
       *
       * @param files the file list being processed.
       * @returns the number of important parent directories for the
       * file list.
       * @throws FormatException if there was a problem parsing the
       *   metadata of the file.
       *
       * @todo This could use a much better explanation, it's not at
       * all clear what the return value means.  Is it an index into
       * @c files?  Is it an index into the path components?  Of which
       * of the files?  In which order?  From the root or common
       * parent?  Is the order of @c files important?
       */
      virtual
      uint32_t
      getRequiredDirectories(const std::vector<std::string>& files) = 0;

      /**
       * Get a short description of the dataset structure.
       *
       * @returns a short description.
       */
      virtual
      const std::string&
      getDatasetStructureDescription() = 0;

      /**
       * Get the possible domains represented in which this format is used.
       *
       * @param id filename to check.
       * @returns a list of domains.
       * @throws FormatException if there was a problem parsing the metadata of the
       *   file.
       *
       * @todo can this be a reference to static data?
       */
      virtual
      const std::vector<std::string>
      getPossibleDomains(const std::string& id) = 0;

      /**
       * Does this format support multi-file datasets?
       *
       * @returns @c true if multiple files are supported, @c false
       * otherwise.
       */
      virtual
      bool
      hasCompanionFiles() = 0;

      /**
       * Get the optimal sub-image width.
       * This is intended for use with openBytes().
       *
       * @returns the optimal width.
       **/
      virtual
      int
      getOptimalTileWidth() = 0;

      /**
       * Get the optimal sub-image height.
       * This is intended for use with openBytes().
       *
       * @returns the optimal height.
       **/
      virtual
      int
      getOptimalTileHeight() = 0;

      // Sub-resolution API methods

      /**
       * Get the first core index corresponding to the specified series.
       *
       * @param series the series to use.
       * @returns the first for index for the series.
       */
      virtual
      CoreMetadata::image_size_type
      seriesToCoreIndex(CoreMetadata::image_size_type series) = 0;

      /**
       * Get the series corresponding to the specified core index.
       *
       * @param index the core index to use.
       * @returns the series for the index.
       */
      virtual
      CoreMetadata::image_size_type
      coreIndexToSeries(CoreMetadata::image_size_type index) = 0;

      /**
       * Get the CoreMetadata index of the current resolution/series.
       *
       * @returns the index.
       */
      virtual
      CoreMetadata::image_size_type
      getCoreIndex() = 0;

      /**
       * Set the current resolution/series (ignoring subresolutions).
       *
       * Equivalent to setSeries(), but with flattened resolutions always
       * set to @c false.
       *
       * @param no the core index to set.
       */
      virtual
      void
      setCoreIndex(CoreMetadata::image_size_type no) = 0;

      /**
       * Get the number of resolutions for the current series.
       *
       * Resolutions are stored in descending order of size, so the
       * largest resolution is first and the smallest resolution is
       * last.
       *
       * @returns the number of resolutions.
       */
      virtual
      CoreMetadata::image_size_type
      getResolutionCount() = 0;

      /**
       * Set the active resolution level.
       *
       * @param resolution the resolution to set.
       *
       * @see getResolutionCount()
       */
      virtual
      void
      setResolution(CoreMetadata::image_size_type resolution) = 0;

      /**
       * Get the active resolution level.
       *
       * @returns the resolution level.
       *
       * @see getResolutionCount()
       */
      virtual
      CoreMetadata::image_size_type
      getResolution() = 0;

      /**
       * Get resolution flattening.
       *
       * @returns @c true if flattening is enabled, @c false otherwise. 
       */
      virtual
      bool
      hasFlattenedResolutions() = 0;

      /**
       * Set resolution flattening.
       *
       * This controls whether or not resolution levels are flattened
       * into individual series.  This alters the behaviour of
       * setSeries() and getSeries() but does not affect the behaviour
       * of setCoreIndex() and getCoreIndex(), which are
       * resolution-independent.
       *
       * @param flatten @c true to enable flattening, @c false to disable.
       */
      virtual
      void
      setFlattenedResolutions(bool flatten) = 0;
    };

  }
}

#endif // OME_BIOFORMATS_FORMATREADER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

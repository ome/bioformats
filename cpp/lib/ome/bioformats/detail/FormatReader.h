/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2015 Open Microscopy Environment:
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

#ifndef OME_BIOFORMATS_DETAIL_FORMATREADER_H
#define OME_BIOFORMATS_DETAIL_FORMATREADER_H

#include <string>
#include <vector>
#include <map>

#include <ome/bioformats/FormatReader.h>
#include <ome/bioformats/FormatHandler.h>

namespace ome
{
  namespace bioformats
  {
    /**
     * Implementation details.
     *
     * Default concrete implementations of interfaces in the parent
     * namespace.
     */
    namespace detail
    {

      /**
       * Properties specific to a particular reader.
       */
      struct ReaderProperties
      {
        /// Format name.
        std::string name;
        /// Format description.
        std::string description;
        /// Filename suffixes this format can handle.
        std::vector<boost::filesystem::path> suffixes;
        /// Filename compression suffixes this format can handle.
        std::vector<boost::filesystem::path> compression_suffixes;
        /// Supported metadata levels.  A typical default is {METADATA_MINIMUM,METADATA_NO_OVERLAYS,METADATA_ALL}.
        std::set<MetadataOptions::MetadataLevel> metadata_levels;

        /**
         * Constructor.
         *
         * @param name the format name.
         * @param description a short description of the format.
         */
        ReaderProperties(const std::string& name,
                         const std::string& description):
          name(name),
          description(description),
          suffixes(),
          compression_suffixes(),
          metadata_levels()
        {
          compression_suffixes.push_back(boost::filesystem::path(""));
        }
      };

      /**
       * Interface for all biological file format readers (default behaviour).
       *
       * Differences from the Java implementation:
       * - MetadataMap methods replace the use of FormatReader
       *   add*Meta/get*Meta methods; the metadata objects themselves
       *   provide these methods.
       * - flattenHashTables isn't implemented; MetadataMaps are
       *   self-flattening when streamed, and have a flatten method
       *   and visitors to generate a flat form.
       * - No getRotationTransform; add to AffineTransform itself.
       */
      class FormatReader : public ::ome::bioformats::FormatReader,
                           virtual public ::ome::bioformats::FormatHandler
      {
      protected:
        /// List type for storing CoreMetadata.
        typedef std::vector<ome::compat::shared_ptr< ::ome::bioformats::CoreMetadata> > coremetadata_list_type;

        /// Reader properties specific to the derived file format.
        const ReaderProperties& readerProperties;

        /// The identifier (path) of the currently open file.
        boost::optional<boost::filesystem::path> currentId;

        /// Current input.
        ome::compat::shared_ptr<std::istream> in;

        /// Mapping of metadata key/value pairs.
        ::ome::bioformats::MetadataMap metadata;

        /**
         * The number of the current series (flattened).
         *
         * @todo Remove use of stateful API which requires use of
         * series switching in const methods.
         */
        mutable dimension_size_type coreIndex;

        /**
         * The number of the current series (non-flattened).
         *
         * @todo Remove use of stateful API which requires use of
         * series switching in const methods.
         */
        mutable dimension_size_type series;

        /**
         * The number of the current plane in the current series.
         *
         * @todo Remove use of stateful API which requires use of
         * series switching in const methods.
         */
        mutable dimension_size_type plane;

        /// Core metadata values.
        coremetadata_list_type core;

        /**
         * The number of the current resolution.
         *
         * @todo Remove use of stateful API which requires use of
         * series switching in const methods.
         */
        mutable dimension_size_type resolution;

        /// Whether or not resolutions are flattened.
        bool flattenedResolutions;

        /**
         * Whether the file extension matching one of the reader's
         * suffixes is necessary to identify the file as an instance
         * of this format.
         */
        bool suffixNecessary;

        /**
         * Whether the file extension matching one of the reader's suffixes
         * is sufficient to identify the file as an instance of this format.
         */
        bool suffixSufficient;

        /// Whether this format supports multi-file datasets.
        bool companionFiles;

        /// Short description of the structure of the dataset.
        std::string datasetDescription;

        /// Whether or not to normalize float data.
        bool normalizeData;

        /// Whether or not to filter out invalid metadata.
        bool filterMetadata;

        /// Whether or not to save proprietary metadata in the MetadataStore.
        bool saveOriginalMetadata;

        /// Whether or not MetadataStore sets C = 3 for indexed color images.
        bool indexedAsRGB;

        /// Whether or not to group multi-file formats.
        bool group;

        /// List of domains in which this format is used.
        std::vector<std::string> domains;

        /**
         * Current metadata store. Should never be accessed directly as the
         * semantics of getMetadataStore() prevent "null" access.
         */
        ome::compat::shared_ptr< ::ome::xml::meta::MetadataStore> metadataStore;

        /// Metadata parsing options.
        MetadataOptions metadataOptions;

        /// Constructor.
        FormatReader(const ReaderProperties&);

      public:
        /// Destructor.
        virtual
        ~FormatReader();

      private:
        /// Copy constructor (deleted).
        FormatReader (const FormatReader&);

        /// Assignment operator (deleted).
        FormatReader&
        operator= (const FormatReader&);

      protected:
        /**
         * Initialize the given file.
         *
         * This will parse header information, etc. and is called by
         * setId.  Most subclasses should override this method to
         * perform initialization operations such as parsing metadata.
         *
         * @param id the filename to open.
         *
         * @throws FormatException if a parsing error occurs
         * processing the file.
         *
         * @sa ome::bioformats::FormatHandler::setId.
         */
        virtual
        void
        initFile(const boost::filesystem::path& id);

        /**
         * Check if a file is in the used files list.
         *
         * @param file the file to check.
         * @returns @c true if the file is used, @c false if not used.
         */
        virtual
        bool
        isUsedFile(const boost::filesystem::path& file);

        /**
         * Read a raw plane.
         *
         * Note that the pixel buffer must be of the correct size to
         * store the pixel data.
         *
         * @param source the stream to read the plane from.
         * @param dest the pixel buffer in which to store the plane.
         * @param x the left edge of the plane.
         * @param y the top edge of the plane.
         * @param w the width of the plane.
         * @param h the height of the plane.
         * @param samples the number of samples per pixel.
         */
        virtual
        void
        readPlane(std::istream&       source,
                  VariantPixelBuffer& dest,
                  dimension_size_type x,
                  dimension_size_type y,
                  dimension_size_type w,
                  dimension_size_type h,
                  dimension_size_type samples);

        /**
         * Read a raw plane with scanline padding.
         *
         * Note that the pixel buffer must be of the correct size to
         * store the pixel data.
         *
         * @param source the stream to read the plane from.
         * @param dest the pixel buffer in which to store the plane.
         * @param x the left edge of the plane.
         * @param y the top edge of the plane.
         * @param w the width of the plane.
         * @param h the height of the plane.
         * @param scanlinePad the scanline padding.
         * @param samples the number of samples per pixel.
         */
        virtual
        void
        readPlane(std::istream&       source,
                  VariantPixelBuffer& dest,
                  dimension_size_type x,
                  dimension_size_type y,
                  dimension_size_type w,
                  dimension_size_type h,
                  dimension_size_type scanlinePad,
                  dimension_size_type samples);

        /**
         * Create a configured FilterMetadata instance.
         *
         * This creates, configures and returns an instance of
         * ome::xml::meta::FilterMetadata.
         *
         * @returns a FilterMetadata instance.
         */
        virtual
        ome::compat::shared_ptr< ::ome::xml::meta::MetadataStore>
        makeFilterMetadata();

        /**
         * Get CoreMetadata by core index.
         *
         * @param index the core index.
         * @returns the CoreMetadata.
         * @throws std::range_error if the core index is invalid.
         * @throws std::logic_error if the metadata is null.
         */
        const CoreMetadata&
        getCoreMetadata(dimension_size_type index) const
        {
          coremetadata_list_type::value_type cm(core.at(index));
          if (!cm)
            throw std::logic_error("CoreMetadata null");
          return *cm;
        }

        /**
         * Get CoreMetadata by core index.
         *
         * @param index the core index.
         * @returns the CoreMetadata.
         * @throws std::range_error if the core index is invalid.
         * @throws std::logic_error if the metadata is null.
         */
        CoreMetadata&
        getCoreMetadata(dimension_size_type index)
        {
          coremetadata_list_type::value_type cm(core.at(index));
          if (!cm)
            throw std::logic_error("CoreMetadata null");
          return *cm;
        }

      public:
        // Documented in superclass.
        const std::set<MetadataOptions::MetadataLevel>&
        getSupportedMetadataLevels();

        // Documented in superclass.
        void
        setMetadataOptions(const MetadataOptions& options);

        // Documented in superclass.
        const MetadataOptions&
        getMetadataOptions() const;

        MetadataOptions&
        getMetadataOptions();

        // Documented in superclass.
        const MetadataMap&
        getGlobalMetadata() const;

        // Documented in superclass.
        const MetadataMap::value_type&
        getMetadataValue(const std::string& field) const;

        // Documented in superclass.
        const MetadataMap&
        getSeriesMetadata() const;

        // Documented in superclass.
        const MetadataMap::value_type&
        getSeriesMetadataValue(const MetadataMap::key_type& field) const;

        // Documented in superclass.
        bool
        isThisType(const boost::filesystem::path& name,
                   bool                           open = true) const;

        // Documented in superclass.
        bool
        isThisType(const uint8_t *begin,
                   std::size_t    length) const;

        // Documented in superclass.
        bool
        isThisType(const uint8_t *begin,
                   const uint8_t *end) const;

        // Documented in superclass.
        bool
        isThisType(std::istream& stream) const;

      protected:
        /**
         * isThisType file implementation for readers.
         *
         * Readers which require opening a file in order to determine
         * its type should override this method with their own
         * implementation.  Reader implementations should open the
         * specified file using their preferred method and check its
         * validity.
         *
         * @param name the file to open for checking.
         * @returns @c true if the file is valid, @c false otherwise.
         */
        virtual
        bool
        isFilenameThisTypeImpl(const boost::filesystem::path& name) const;

        /**
         * isThisType stream implementation for readers.
         *
         * Readers which require opening a file in order to determine
         * its type, and which can handle @c istream data, should
         * override this method with their own implementation.  Reader
         * implementations should check the validity of the stream
         * data.
         *
         * @param stream the input stream to check.
         * @returns @c true if the stream is valid, @c false otherwise.
         */
        virtual
        bool
        isStreamThisTypeImpl(std::istream& stream) const;

      public:
        // Documented in superclass.
        dimension_size_type
        getImageCount() const;

        // Documented in superclass.
        bool
        isRGB(dimension_size_type channel) const;

        // Documented in superclass.
        dimension_size_type
        getSizeX() const;

        // Documented in superclass.
        dimension_size_type
        getSizeY() const;

        // Documented in superclass.
        dimension_size_type
        getSizeZ() const;

        // Documented in superclass.
        dimension_size_type
        getSizeT() const;

        // Documented in superclass.
        dimension_size_type
        getSizeC() const;

        // Documented in superclass.
        ome::xml::model::enums::PixelType
        getPixelType() const;

        // Documented in superclass.
        pixel_size_type
        getBitsPerPixel() const;

        // Documented in superclass.
        dimension_size_type
        getEffectiveSizeC() const;

        // Documented in superclass.
        dimension_size_type
        getRGBChannelCount(dimension_size_type channel) const;

        // Documented in superclass.
        bool
        isIndexed() const;

        // Documented in superclass.
        bool
        isFalseColor() const;

        // Documented in superclass.
        void
        getLookupTable(dimension_size_type plane,
                       VariantPixelBuffer& buf) const;

        // Documented in superclass.
        Modulo&
        getModuloZ();

        // Documented in superclass.
        const Modulo&
        getModuloZ() const;

        // Documented in superclass.
        Modulo&
        getModuloT();

        // Documented in superclass.
        const Modulo&
        getModuloT() const;

        // Documented in superclass.
        Modulo&
        getModuloC();

        // Documented in superclass.
        const Modulo&
        getModuloC() const;

      protected:
        /**
         * Get the thumbnail size of the X and Y dimensions.
         *
         * @returns an array containing the X and Y dimension
         * thumbnail size (in that order).
         */
        ome::compat::array<dimension_size_type, 2>
        getThumbSize() const;

      public:
        // Documented in superclass.
        dimension_size_type
        getThumbSizeX() const;

        // Documented in superclass.
        dimension_size_type
        getThumbSizeY() const;

        // Documented in superclass.
        bool
        isLittleEndian() const;

        // Documented in superclass.
        const std::string&
        getDimensionOrder() const;

        // Documented in superclass.
        bool
        isOrderCertain() const;

        // Documented in superclass.
        bool
        isThumbnailSeries() const;

        // Documented in superclass.
        bool
        isInterleaved() const;

        // Documented in superclass.
        bool
        isInterleaved(dimension_size_type subC) const;

        // Documented in superclass.
        void
        openBytes(dimension_size_type plane,
                  VariantPixelBuffer& buf) const;

        // Documented in superclass.
        void
        openBytes(dimension_size_type plane,
                  VariantPixelBuffer& buf,
                  dimension_size_type x,
                  dimension_size_type y,
                  dimension_size_type w,
                  dimension_size_type h) const;

      protected:
        /**
         * @copydoc ome::bioformats::FormatReader::openBytes(dimension_size_type,VariantPixelBuffer&,dimension_size_type,dimension_size_type,dimension_size_type,dimension_size_type)const
         */
        virtual
        void
        openBytesImpl(dimension_size_type plane,
                      VariantPixelBuffer& buf,
                      dimension_size_type x,
                      dimension_size_type y,
                      dimension_size_type w,
                      dimension_size_type h) const = 0;

      public:
        // Documented in superclass.
        void
        openThumbBytes(dimension_size_type plane,
                       VariantPixelBuffer& buf) const;

        // Documented in superclass.
        void
        close(bool fileOnly = false);

        // Documented in superclass.
        dimension_size_type
        getSeriesCount() const;

        // Documented in superclass.
        void
        setSeries(dimension_size_type series) const;

        // Documented in superclass.
        dimension_size_type
        getSeries() const;

        // Documented in superclass.
        void
        setPlane(dimension_size_type plane) const;

        // Documented in superclass.
        dimension_size_type
        getPlane() const;

        // Documented in superclass.
        void
        setGroupFiles(bool group);

        // Documented in superclass.
        bool
        isGroupFiles() const;

        // Documented in superclass.
        FileGroupOption
        fileGroupOption(const std::string& id);

        // Documented in superclass.
        bool
        isMetadataComplete() const;

        // Documented in superclass.
        void
        setNormalized(bool normalize);

        // Documented in superclass.
        bool
        isNormalized() const;

        // Documented in superclass.
        void
        setOriginalMetadataPopulated(bool populate);

        // Documented in superclass.
        bool
        isOriginalMetadataPopulated() const;

        // Documented in superclass.
        const std::vector<boost::filesystem::path>
        getUsedFiles(bool noPixels = false) const;

        // Documented in superclass.
        const std::vector<boost::filesystem::path>
        getSeriesUsedFiles(bool noPixels = false) const;

        // Documented in superclass.
        std::vector<FileInfo>
        getAdvancedUsedFiles(bool noPixels = false) const;

        // Documented in superclass.
        std::vector<FileInfo>
        getAdvancedSeriesUsedFiles(bool noPixels = false) const;

        // Documented in superclass.
        const boost::optional<boost::filesystem::path>&
        getCurrentFile() const;

        // Documented in superclass.
        dimension_size_type
        getIndex(dimension_size_type z,
                 dimension_size_type c,
                 dimension_size_type t) const;

        // Documented in superclass.
        dimension_size_type
        getIndex(dimension_size_type z,
                 dimension_size_type c,
                 dimension_size_type t,
                 dimension_size_type moduloZ,
                 dimension_size_type moduloC,
                 dimension_size_type moduloT) const;

        // Documented in superclass.
        ome::compat::array<dimension_size_type, 3>
        getZCTCoords(dimension_size_type index) const;

        // Documented in superclass.
        ome::compat::array<dimension_size_type, 6>
        getZCTModuloCoords(dimension_size_type index) const;

        // Documented in superclass.
        const std::vector<ome::compat::shared_ptr< ::ome::bioformats::CoreMetadata> >&
        getCoreMetadataList() const;

        // Documented in superclass.
        void
        setMetadataFiltered(bool filter);

        // Documented in superclass.
        bool
        isMetadataFiltered() const;

        // Documented in superclass.
        void
        setMetadataStore(ome::compat::shared_ptr< ::ome::xml::meta::MetadataStore>& store);

        // Documented in superclass.
        const ome::compat::shared_ptr< ::ome::xml::meta::MetadataStore>&
        getMetadataStore() const;

        // Documented in superclass.
        ome::compat::shared_ptr< ::ome::xml::meta::MetadataStore>&
        getMetadataStore();

        // Documented in superclass.
        std::vector<ome::compat::shared_ptr< ::ome::bioformats::FormatReader> >
        getUnderlyingReaders() const;

        // Documented in superclass.
        bool
        isSingleFile(const boost::filesystem::path& id) const;

        // Documented in superclass.
        uint32_t
        getRequiredDirectories(const std::vector<std::string>& files) const;

        // Documented in superclass.
        const std::string&
        getDatasetStructureDescription() const;

        // Documented in superclass.
        const std::vector<std::string>&
        getPossibleDomains(const std::string& id) const;

        // Documented in superclass.
        bool
        hasCompanionFiles() const;

        // Documented in superclass.
        const std::vector<std::string>&
        getDomains() const;

        // Documented in superclass.
        dimension_size_type
        getOptimalTileWidth(dimension_size_type channel) const;

        // Documented in superclass.
        dimension_size_type
        getOptimalTileHeight(dimension_size_type channel) const;

        // Documented in superclass.
        dimension_size_type
        getOptimalTileWidth() const;

        // Documented in superclass.
        dimension_size_type
        getOptimalTileHeight() const;

        // Documented in superclass.
        dimension_size_type
        seriesToCoreIndex(dimension_size_type series) const;

        // Documented in superclass.
        dimension_size_type
        coreIndexToSeries(dimension_size_type index) const;

        // Documented in superclass.
        dimension_size_type
        getCoreIndex() const;

        // Documented in superclass.
        void
        setCoreIndex(dimension_size_type index) const;

        // Documented in superclass.
        dimension_size_type
        getResolutionCount() const;

        // Documented in superclass.
        void
        setResolution(dimension_size_type resolution) const;

        // Documented in superclass.
        dimension_size_type
        getResolution() const;

        // Documented in superclass.
        bool
        hasFlattenedResolutions() const;

        // Documented in superclass.
        void
        setFlattenedResolutions(bool flatten);

        // Documented in superclass.
        void
        setId(const boost::filesystem::path& id);

        // Documented in superclass.
        const std::string&
        getFormat() const;

        // Documented in superclass.
        const std::string&
        getFormatDescription() const;

        // Documented in superclass.
        const std::vector<boost::filesystem::path>&
        getSuffixes() const;

        // Documented in superclass.
        const std::vector<boost::filesystem::path>&
        getCompressionSuffixes() const;
      };

    }
  }
}

#endif // OME_BIOFORMATS_DETAIL_FORMATREADER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

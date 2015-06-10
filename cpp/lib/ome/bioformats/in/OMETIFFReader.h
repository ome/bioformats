/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2015 Open Microscopy Environment:
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

#ifndef OME_BIOFORMATS_IN_OMETIFFREADER_H
#define OME_BIOFORMATS_IN_OMETIFFREADER_H

#include <ome/bioformats/in/MinimalTIFFReader.h>
#include <ome/bioformats/tiff/ImageJMetadata.h>

#include <ome/common/log.h>

#include <ome/xml/meta/BaseMetadata.h>

namespace ome
{

  namespace xml
  {
    namespace meta
    {
      class OMEXMLMetadata;
    }
  }

  namespace bioformats
  {
    namespace in
    {

      /**
       * TIFF reader with support for OME-XML metadata.
       */
      class OMETIFFReader : public ::ome::bioformats::detail::FormatReader
      {
        using detail::FormatReader::isThisType;
        using ::ome::bioformats::FormatReader::getOptimalTileWidth;
        using ::ome::bioformats::FormatReader::getOptimalTileHeight;

      protected:
        /// Message logger.
        ome::common::Logger logger;

        /// Map UUID to filename.
        typedef std::map<std::string, boost::filesystem::path> uuid_file_map;

        /// Map filename to another file.
        typedef std::map<boost::filesystem::path, boost::filesystem::path> invalid_file_map;

        /// Map filename to open TIFF handle.
        typedef std::map<boost::filesystem::path, ome::compat::shared_ptr<ome::bioformats::tiff::TIFF> > tiff_map;

        /// UUID to filename mapping.
        uuid_file_map files;

        /// Invalid filename to valid filename mapping.
        invalid_file_map invalidFiles;

        // Mutable to allow opening TIFFs when const.
        /// Open TIFF files
        mutable tiff_map tiffs;

        /// Metadata file.
        boost::filesystem::path metadataFile;

        /// Used files.
        std::vector<boost::filesystem::path> usedFiles;

        /// Has screen-plate-well metadata.
        bool hasSPW;

      public:
        /// Constructor.
        OMETIFFReader();

        /// Destructor.
        virtual
        ~OMETIFFReader();

        // Documented in superclass.
        bool
        isSingleFile(const boost::filesystem::path& id) const;

        // Documented in superclass.
        bool
        isThisType(const boost::filesystem::path& name,
                   bool                           open) const;

      protected:
        // Documented in superclass.
        bool
        isFilenameThisTypeImpl(const boost::filesystem::path& name) const;

        // Documented in superclass.
        void
        getLookupTable(dimension_size_type plane,
                       VariantPixelBuffer& buf) const;

        // Documented in superclass.
        void
        openBytesImpl(dimension_size_type plane,
                      VariantPixelBuffer& buf,
                      dimension_size_type x,
                      dimension_size_type y,
                      dimension_size_type w,
                      dimension_size_type h) const;

        /**
         * Get the IFD index for a plane in the current series.
         *
         * @param plane the plane index within the series.
         * @returns the IFD index.
         * @throws FormatException if out of range.
         */
        const ome::compat::shared_ptr<const tiff::IFD>
        ifdAtIndex(dimension_size_type plane) const;

        /**
         * Add a TIFF file to the internal TIFF map.
         *
         * @param tiff the TIFF file to add.
         */
        void
        addTIFF(const boost::filesystem::path& tiff);

        /**
         * Get a an open TIFF file from the internal TIFF map.
         *
         * If the file does not exist in the map, the file will be
         * added to the internal map first.  If the file is not
         * currently open it will be opened.
         *
         * @param tiff the TIFF file to get.
         * @returns the open TIFF.
         * @throws FormatException if invalid.
         */
        const ome::compat::shared_ptr<const ome::bioformats::tiff::TIFF>
        getTIFF(const boost::filesystem::path& tiff) const;

        /**
         * Close an open TIFF file from the internal TIFF map.
         *
         * If the file is currently open, it will be closed.
         *
         * @param tiff the TIFF file to add.
         */
        void
        closeTIFF(const boost::filesystem::path& tiff);

      public:
        // Documented in superclass.
        void
        close(bool fileOnly = false);

        const std::vector<std::string>&
        getDomains() const;

        // Documented in superclass.
        const std::vector<boost::filesystem::path>
        getSeriesUsedFiles(bool noPixels) const;

        // Documented in superclass.
        FormatReader::FileGroupOption
        fileGroupOption(const std::string& id);

        // Documented in superclass.
        dimension_size_type
        getOptimalTileWidth(dimension_size_type channel) const;

        // Documented in superclass.
        dimension_size_type
        getOptimalTileHeight(dimension_size_type channel) const;

        // Documented in superclass.
        void
        initFile(const boost::filesystem::path& id);

      private:
        /**
         * Get UUID to file associations and used files.
         *
         * Updates both the files map and the used files list.
         *
         * @param meta the metadata store to use.
         * @param currentId the current file.
         * @param currentDir the current directory.
         * @param currentUUID the current UUID (if any).
         */
        void
        findUsedFiles(const ome::xml::meta::OMEXMLMetadata& meta,
                      const boost::filesystem::path&        currentId,
                      const boost::filesystem::path&        currentDir,
                      const boost::optional<std::string>&   currentUUID);

        /**
         * Get acquisition dates for each image.
         *
         * If no date was specified for the image, the timestamp will
         * be unset.
         *
         * @param meta the metadata store to use.
         * @param timestamps the acquisition dates, indexed by image.
         */
        void
        getAcquisitionDates(const ome::xml::meta::OMEXMLMetadata&                                  meta,
                            std::vector<boost::optional<ome::xml::model::primitives::Timestamp> >& timestamps);

        /**
         * Clean up OME-XML metadata.
         *
         * Remove invalid channels.
         *
         * @param meta the metadata store to clean up.
         */
        void
        cleanMetadata(ome::xml::meta::OMEXMLMetadata& meta);

        /**
         * Get the samples per pixel from the first IFD for a series.
         *
         * @param meta the metadata store to query.
         * @param series the series to check.
         * @returns the samples per pixel.
         */
        dimension_size_type
        seriesFileSamplesPerPixel(const ome::xml::meta::OMEXMLMetadata&    meta,
                                  ome::xml::meta::BaseMetadata::index_type series);

        /**
         * Get starting index for each dimension.
         *
         * This is to cater for files which have been incorrectly
         * written, where the starting index is not zero.
         *
         * @param meta the metadata store to query.
         * @param series the series to check.
         * @param zIndexStart the Z starting index.
         * @param tIndexStart the T starting index.
         * @param cIndexStart the C starting index.
         */
        void
        seriesIndexStart(const ome::xml::meta::OMEXMLMetadata&                             meta,
                         ome::xml::meta::BaseMetadata::index_type                          series,
                         boost::optional<ome::xml::model::primitives::NonNegativeInteger>& zIndexStart,
                         boost::optional<ome::xml::model::primitives::NonNegativeInteger>& tIndexStart,
                         boost::optional<ome::xml::model::primitives::NonNegativeInteger>& cIndexStart);

        /**
         * Get values from a TiffData element.
         *
         * @param meta the metadata store to query.
         * @param series the series to check.
         * @param tiffData the TiffData index to check.
         * @param tdIFD the starting IFD.
         * @param numPlanes the number of planes.
         * @param firstZ the first Z plane.
         * @param firstT the first T plane.
         * @param firstC the first C plane.
         * @returns @c true if read successfully, @c false otherwise.
         */
        bool
        getTiffDataValues(const ome::xml::meta::OMEXMLMetadata&                             meta,
                          ome::xml::meta::BaseMetadata::index_type                          series,
                          ome::xml::meta::BaseMetadata::index_type                          tiffData,
                          boost::optional<ome::xml::model::primitives::NonNegativeInteger>& tdIFD,
                          ome::xml::model::primitives::NonNegativeInteger&                  numPlanes,
                          ome::xml::model::primitives::NonNegativeInteger&                  firstZ,
                          ome::xml::model::primitives::NonNegativeInteger&                  firstT,
                          ome::xml::model::primitives::NonNegativeInteger&                  firstC);

        /**
         * Fix invalid OMERO OME-TIFF metadata.
         *
         * OMERO has in the past written OME-TIFF with incorrect
         * DimensionOrder.  Attempt to identify such data and reset
         * the dimension order to XYZCT.
         *
         * @param meta the metadata store to query.
         * @param series the series to correct.
         */
        void
        fixOMEROMetadata(ome::xml::meta::OMEXMLMetadata&          meta,
                         ome::xml::meta::BaseMetadata::index_type series);

        /**
         * Attempt to correct logically inconsistent dimensions.
         *
         * If the product of SizeZ, SizeT and SizeC is not equal to
         * the total image count, attempt to correct it by finding the
         * dimension equal to the image count, and setting all other
         * dimension sizes to 1.  If a match isn't found, fall back to
         * setting SizeT to the image count.
         *
         * @param series the series to correct.
         */
        void
        fixDimensions(ome::xml::meta::BaseMetadata::index_type series);

      public:
        /**
         * Get a MetadataStore suitable for writing.
         *
         * @note This will be suitable for use with FormatWriter, but
         * will likely not generate valid OME-XML due to the
         * likelihood of containing both BinData and TiffData
         * elements.
         *
         * @returns the metadata store.
         */
        ome::compat::shared_ptr< ome::xml::meta::MetadataStore>
        getMetadataStoreForConversion();

        /**
         * Get a MetadataStore suitable for display.
         *
         * @note This will not be suitable for use with FormatWriter
         * due to not containing required BinData BigEndian
         * attributes.
         *
         * @returns the metadata store.
         */
        ome::compat::shared_ptr< ome::xml::meta::MetadataStore>
        getMetadataStoreForDisplay();
      };


    }
  }
}

#endif // OME_BIOFORMATS_IN_OMETIFFREADER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

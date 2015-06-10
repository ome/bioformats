/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2014 Open Microscopy Environment:
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

#ifndef OME_BIOFORMATS_OUT_OMETIFFWRITER_H
#define OME_BIOFORMATS_OUT_OMETIFFWRITER_H

#include <ome/bioformats/detail/FormatWriter.h>
#include <ome/bioformats/detail/OMETIFF.h>

#include <ome/common/filesystem.h>
#include <ome/common/log.h>

#include <ome/xml/meta/OMEXMLMetadata.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      class TIFF;
      class IFD;

    }

    namespace out
    {

      /**
       * TIFF writer with support for OME-XML metadata.
       */
      class OMETIFFWriter : public ::ome::bioformats::detail::FormatWriter
      {
      protected:
        /// Message logger.
        ome::common::Logger logger;

        /// Map filename to UUID.
        typedef std::map<boost::filesystem::path, std::string> file_uuid_map;

        // In the Java reader, this is uuids + ifdCounts
        /// State of TIFF file.
        struct TIFFState
        {
          /// UUID of file.
          std::string uuid;
          /// TIFF file handle.
          ome::compat::shared_ptr<ome::bioformats::tiff::TIFF> tiff;
          /// Number of IFDs written.
          dimension_size_type ifdCount;

          /**
           * Constructor.
           *
           * @param tiff the TIFF file for which to cache state.
           */
          TIFFState(ome::compat::shared_ptr<ome::bioformats::tiff::TIFF>& tiff);

          /// Destructor.
          ~TIFFState();
        };

        /// Map filename to TIFF state.
        typedef std::map<boost::filesystem::path, TIFFState> tiff_map;

        // In the Java reader, this is imageLocations.
        /// Current state of an image series.
        struct SeriesState
        {
          /// Current state of each plane in an image series.
          std::vector<detail::OMETIFFPlane> planes;
        };

        /// Vector of SeriesState objects.
        typedef std::vector<SeriesState> series_list;

        /// Base path for computing relative paths in the OME-XML.
        boost::filesystem::path baseDir;

        /// UUID to filename mapping.
        file_uuid_map files;

        // Mutable to allow opening TIFFs when const.
        /// Open TIFF files
        mutable tiff_map tiffs;

        /// Current TIFF file.
        tiff_map::iterator currentTIFF;

        /// TIFF flags.
        std::string flags;
        
        /// State of each series.
        series_list seriesState;

        /**
         * Original MetadataRetrieve.
         *
         * We replace it with the generated OME-XML metadata store.
         *
         * @todo Overriding getMetadataRetrieve will be a cleaner
         * solution, but need to eliminate all direct use of
         * metadataRetrieve in all writers first.
         */
        ome::compat::shared_ptr< ::ome::xml::meta::MetadataRetrieve> originalMetadataRetrieve;

        /// OME-XML metadata for embedding in the TIFF.
        ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadata> omeMeta;

      private:
        /// Write a Big TIFF
        boost::optional<bool> bigTIFF;

      public:
        /// Constructor.
        OMETIFFWriter();

        /// Destructor.
        virtual
        ~OMETIFFWriter();

        // Documented in superclass.
        void
        setId(const boost::filesystem::path& id);

        // Documented in superclass.
        void
        close(bool fileOnly = false);

        using FormatWriter::saveBytes;

        // Documented in superclass.
        void
        setSeries(dimension_size_type series) const;

        // Documented in superclass.
        void
        setPlane(dimension_size_type plane) const;

      protected:
        /// Flush current IFD and create new IFD.
        void
        nextIFD() const;

        /// Set IFD parameters for the current series.
        void
        setupIFD() const;

      public:
        // Documented in superclass.
        void
        saveBytes(dimension_size_type plane,
                  VariantPixelBuffer& buf,
                  dimension_size_type x,
                  dimension_size_type y,
                  dimension_size_type w,
                  dimension_size_type h);

      private:
        /**
         * Fill MetadataStore with cached metadata.
         *
         * Set Image, Channel and TiffData elements.
         */
        void
        fillMetadata();

        /**
         * Get OME-XML for embedding into the specified TIFF file.
         *
         * @param id the TIFF in which to embed the OME-XML.
         * @returns the OME-XML text for embedding.
         */
        std::string
        getOMEXML(const boost::filesystem::path& id);

        /**
         * Save OME-XML text in the first IFD of the specified TIFF file.
         *
         * @param id the TIFF in which to embed the OME-XML.
         * @param xml the OME-XML text to embed.
         */
        void
        saveComment(const boost::filesystem::path& id,
                    const std::string&             xml);

        // Java getUUID unimplemented; see uuid member of TIFFState.

        // Java planeCount() unimplemented; use getImageCount()
        // instead.  Note the java implementation special-cases
        // certain behaviour such as interleaving for certain pixel
        // types; here the caller can specify exactly what they want.

      public:
        /**
         * @copydoc MinimalTIFFWriter::setBigTIFF(boost::optional<bool>)
         */
        void
        setBigTIFF(boost::optional<bool> big = true);

        /**
         * @copydoc MinimalTIFFWriter::getBigTIFF() const
         */
        boost::optional<bool>
        getBigTIFF() const;
      };

    }
  }
}

#endif // OME_BIOFORMATS_OUT_OMETIFFWRITER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

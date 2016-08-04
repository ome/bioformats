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

#ifndef OME_BIOFORMATS_DETAIL_FORMATWRITER_H
#define OME_BIOFORMATS_DETAIL_FORMATWRITER_H

#include <ome/bioformats/FormatWriter.h>
#include <ome/bioformats/FormatHandler.h>

#include <map>

namespace ome
{
  namespace bioformats
  {
    namespace detail
    {

      /**
       * Properties specific to a particular writer.
       */
      struct WriterProperties
      {
        /// Map of codec to pixel types.
        typedef std::map<std::string,
                         std::set<ome::xml::model::enums::PixelType> > codec_pixel_type_map;

        /// Format name.
        std::string name;
        /// Format description.
        std::string description;
        /// Filename suffixes this format can handle.
        std::vector<boost::filesystem::path> suffixes;
        /// Filename compression suffixes this format can handle.
        std::vector<boost::filesystem::path> compression_suffixes;
        /// Supported compression types.
        std::set<std::string> compression_types;
        /// Supported pixel types.
        codec_pixel_type_map codec_pixel_types;
        /// Stacks are supported.
        bool stacks;

        /**
         * Constructor.
         *
         * @param name the format name.
         * @param description a short description of the format.
         */
        WriterProperties(const std::string& name,
                         const std::string& description):
          name(name),
          description(description),
          suffixes(),
          compression_suffixes(),
          compression_types(),
          codec_pixel_types(),
          stacks()
        {
          compression_suffixes.push_back(boost::filesystem::path(""));
        }
      };

      /**
       * Interface for all biological file format writers (default behaviour).
       *
       * @note The @c ColorModel isn't stored here; this is
       * Java-specific and not implemented in C++.
       *
       * @note The current output stream isn't stored here; this is
       * the responsibility of the individual writer.  Having a
       * reference to the base ostream here and keeping this in sync
       * with the derived writer is an unnecessary complication.
       */
      class FormatWriter : public ::ome::bioformats::FormatWriter,
                           virtual public ::ome::bioformats::FormatHandler
      {
      protected:
        /// Writer properties specific to the derived file format.
        const WriterProperties& writerProperties;

        /// The identifier (path) of the currently open file.
        boost::optional<boost::filesystem::path> currentId;

        /// Current output.
        ome::compat::shared_ptr<std::ostream> out;

        /// Current series.
        mutable dimension_size_type series;

        /// Current plane.
        mutable dimension_size_type plane;

        /// The compression type to use.
        boost::optional<std::string> compression;

        /// Subchannel interleaving enabled.
        boost::optional<bool> interleaved;

        /// Planes are written sequentially.
        bool sequential;

        /// The frames per second to use when writing.
        frame_rate_type framesPerSecond;

        /**
         * Current metadata store. Should never be accessed directly as the
         * semantics of getMetadataRetrieve() prevent "null" access.
         */
        ome::compat::shared_ptr< ::ome::xml::meta::MetadataRetrieve> metadataRetrieve;

      protected:
        /// Constructor.
        FormatWriter(const WriterProperties&);

      private:
        /// Copy constructor (deleted).
        FormatWriter (const FormatWriter&);

        /// Assignment operator (deleted).
        FormatWriter&
        operator= (const FormatWriter&);

      public:
        /// Destructor.
        virtual
        ~FormatWriter();

        // Documented in superclass.
        bool
        isThisType(const boost::filesystem::path& name,
                   bool                           open = true) const;

        /**
         * Get the number of image series in this file.
         *
         * @returns the number of image series.
         * @throws std::logic_error if the sub-resolution metadata (if
         * any) is invalid; this will only occur if the reader sets
         * invalid metadata.
         */
        virtual
        dimension_size_type
        getSeriesCount() const;

        // Documented in superclass.
        void
        setLookupTable(dimension_size_type       plane,
                       const VariantPixelBuffer& buf);

        using bioformats::FormatWriter::saveBytes;

        // Documented in superclass.
        void
        saveBytes(dimension_size_type plane,
                  VariantPixelBuffer& buf);

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
        bool
        canDoStacks() const;

        // Documented in superclass.
        void
        setMetadataRetrieve(ome::compat::shared_ptr< ::ome::xml::meta::MetadataRetrieve>& retrieve);

        // Documented in superclass.
        const ome::compat::shared_ptr< ::ome::xml::meta::MetadataRetrieve>&
        getMetadataRetrieve() const;

        // Documented in superclass.
        ome::compat::shared_ptr< ::ome::xml::meta::MetadataRetrieve>&
        getMetadataRetrieve();

        /**
         * Determine the number of image planes in the current series.
         *
         * @returns the number of image planes.
         */
        virtual
        dimension_size_type
        getImageCount() const;

        /**
         * Does a channel contain subchannels?
         *
         * Check if the image planes in the file have more than one subchannel per
         * openBytes() call for the specified channel.
         *
         * @param channel the channel to use, range [0, EffectiveSizeC).
         * @returns @c true if and only if @c getRGBChannelCount(channel) returns
         * a value greater than 1, @c false otherwise.
         */
        virtual
        bool
        isRGB(dimension_size_type channel) const;

        /**
         * Get the size of the X dimension.
         *
         * @returns the X dimension size.
         */
        virtual
        dimension_size_type
        getSizeX() const;

        /**
         * Get the size of the Y dimension.
         *
         * @returns the Y dimension size.
         */
        virtual
        dimension_size_type
        getSizeY() const;

        /**
         * Get the size of the Z dimension.
         *
         * @returns the Z dimension size.
         */
        virtual
        dimension_size_type
        getSizeZ() const;

        /**
         * Get the size of the T dimension.
         *
         * @returns the T dimension size.
         */
        virtual
        dimension_size_type
        getSizeT() const;

        /**
         * Get the size of the C dimension.
         *
         * @returns the C dimension size.
         */
        virtual
        dimension_size_type
        getSizeC() const;

        /**
         * Get the pixel type.
         *
         * @returns the pixel type.
         */
        virtual
        ome::xml::model::enums::PixelType
        getPixelType() const;

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
        pixel_size_type
        getBitsPerPixel() const;

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
        dimension_size_type
        getEffectiveSizeC() const;

        /**
         * Get the number of channels required for a call to saveBytes().
         *
         * The most common case where this value is greater than 1 is for interleaved
         * RGB data, such as a 24-bit color image plane. However, it is possible for
         * this value to be greater than 1 for non-interleaved data, such as an RGB
         * TIFF with Planar rather than Chunky configuration.
         *
         * @param channel the channel to use, range [0, EffectiveSizeC).
         * @returns the number of channels.
         */
        virtual
        dimension_size_type
        getRGBChannelCount(dimension_size_type channel) const;

        /**
         * @copydoc ome::bioformats::FormatReader::getDimensionOrder() const
         */
        virtual
        const std::string&
        getDimensionOrder() const;

        /**
         * @copydoc ome::bioformats::FormatReader::getIndex(dimension_size_type,dimension_size_type,dimension_size_type) const
         */
        virtual
        dimension_size_type
        getIndex(dimension_size_type z,
                 dimension_size_type c,
                 dimension_size_type t) const;

        /**
         * @copydoc ome::bioformats::FormatReader::getZCTCoords(dimension_size_type) const
         */
        virtual
        ome::compat::array<dimension_size_type, 3>
        getZCTCoords(dimension_size_type index) const;

        // Documented in superclass.
        void
        setFramesPerSecond(frame_rate_type rate);

        // Documented in superclass.
        frame_rate_type
        getFramesPerSecond() const;

        // Documented in superclass.
        const std::set<ome::xml::model::enums::PixelType>&
        getPixelTypes() const;

        // Documented in superclass.
        const std::set<ome::xml::model::enums::PixelType>&
        getPixelTypes(const std::string& codec) const;

        // Documented in superclass.
        bool
        isSupportedType(ome::xml::model::enums::PixelType type) const;

        // Documented in superclass.
        bool
        isSupportedType(ome::xml::model::enums::PixelType type,
                        const std::string&                codec) const;

        // Documented in superclass.
        const std::set<std::string>&
        getCompressionTypes() const ;

        // Documented in superclass.
        void
        setCompression(const std::string& compression);

        // Documented in superclass.
        const boost::optional<std::string>&
        getCompression() const;

        // Documented in superclass.
        void
        setInterleaved(bool interleaved);

        // Documented in superclass.
        const boost::optional<bool>&
        getInterleaved() const;

        // Documented in superclass.
        void
        changeOutputFile(const boost::filesystem::path& id);

        // Documented in superclass.
        void
        setWriteSequentially(bool sequential = true);

        // Documented in superclass.
        bool
        getWriteSequentially() const;

        // Documented in superclass.
        void
        setId(const boost::filesystem::path& id);

        // Documented in superclass.
        void
        close(bool fileOnly = false);

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

#endif // OME_BIOFORMATS_DETAIL_FORMATWRITER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

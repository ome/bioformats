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
        std::vector<std::string> suffixes;
        /// Filename compression suffixes this format can handle.
        std::vector<std::string> compression_suffixes;
        /// Supported compression types.
        std::set<std::string> compression_types;
        /// Supported pixel types.
        codec_pixel_type_map codec_pixel_types;
        /// Stacks are supported.
        bool stacks;
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
        boost::optional<std::string> currentId;

        /// Current output.
        std::shared_ptr<std::ostream> out;

        /// Current series.
        mutable dimension_size_type series;

        /// The compression type to use.
        boost::optional<std::string> compression;

        /// Planes are written sequentially.
        bool sequential;

        /// The frames per second to use when writing.
        frame_rate_type framesPerSecond;

        /**
         * Current metadata store. Should never be accessed directly as the
         * semantics of getMetadataRetrieve() prevent "null" access.
         */
        std::shared_ptr< ::ome::xml::meta::MetadataRetrieve> metadataRetrieve;

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
        isThisType(const std::string& name,
                   bool               open = true) const;

        // Documented in superclass.
        void
        setLookupTable(const VariantPixelBuffer& buf);

        // Documented in superclass.
        void
        saveBytes(dimension_size_type no,
                  VariantPixelBuffer& buf);

        // Documented in superclass.
        void
        saveBytes(dimension_size_type no,
                  VariantPixelBuffer& buf,
                  dimension_size_type x,
                  dimension_size_type y,
                  dimension_size_type w,
                  dimension_size_type h) = 0;

        // Documented in superclass.
        void
        setSeries(dimension_size_type no) const;

        // Documented in superclass.
        dimension_size_type
        getSeries() const;

        // Documented in superclass.
        bool
        canDoStacks() const;

        // Documented in superclass.
        void
        setMetadataRetrieve(std::shared_ptr< ::ome::xml::meta::MetadataRetrieve>& retrieve);

        // Documented in superclass.
        const std::shared_ptr< ::ome::xml::meta::MetadataRetrieve>&
        getMetadataRetrieve() const;

        // Documented in superclass.
        std::shared_ptr< ::ome::xml::meta::MetadataRetrieve>&
        getMetadataRetrieve();

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
        changeOutputFile(const std::string& id);

        // Documented in superclass.
        void
        setWriteSequentially(bool sequential = true);

        // Documented in superclass.
        bool
        getWriteSequentially() const;

        // Documented in superclass.
        void
        setId(const std::string& id);

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
        const std::vector<std::string>&
        getSuffixes() const;

        // Documented in superclass.
        const std::vector<std::string>&
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

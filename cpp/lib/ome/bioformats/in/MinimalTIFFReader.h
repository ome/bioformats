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

#ifndef OME_BIOFORMATS_IN_MINIMALTIFFREADER_H
#define OME_BIOFORMATS_IN_MINIMALTIFFREADER_H

#include <ome/bioformats/detail/FormatReader.h>

#include <ome/bioformats/tiff/Util.h>

#include <vector>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      class TIFF;
      class IFD;

    }

    /// Reader implementations.
    namespace in
    {

      /**
       * Basic TIFF reader.
       *
       * @note Any derived reader which does not implement its own
       * openBytesImpl() must fill @c seriesIFDRange.
       */
      class MinimalTIFFReader : public ::ome::bioformats::detail::FormatReader
      {
      protected:
        /// Underlying TIFF file.
        ome::compat::shared_ptr<ome::bioformats::tiff::TIFF> tiff;

        /// Mapping between series index and start and end IFD as a half-open range.
        tiff::SeriesIFDRange seriesIFDRange;

      public:
        /// Constructor.
        MinimalTIFFReader();

        /// Constructor with reader properties (for derived readers).
        MinimalTIFFReader(const ome::bioformats::detail::ReaderProperties& readerProperties);

        /// Destructor.
        virtual
        ~MinimalTIFFReader();

      protected:
        // Documented in superclass.
        void
        initFile(const boost::filesystem::path& id);

        /**
         * Read metadata from IFDs.
         */
        virtual
        void
        readIFDs();

        // Documented in superclass.
        bool
        isFilenameThisTypeImpl(const boost::filesystem::path& name) const;

        /**
         * Get the IFD index for a plane in the current series.
         *
         * @param plane the plane index within the series.
         * @returns the IFD index.
         * @throws FormatException if out of range.
         */
        const ome::compat::shared_ptr<const tiff::IFD>
        ifdAtIndex(dimension_size_type plane) const;

      public:
        // Documented in superclass.
        void
        close(bool fileOnly = false);

        // Documented in superclass.
        void
        getLookupTable(dimension_size_type plane,
                       VariantPixelBuffer& buf) const;

      protected:
        // Documented in superclass.
        void
        openBytesImpl(dimension_size_type plane,
                      VariantPixelBuffer& buf,
                      dimension_size_type x,
                      dimension_size_type y,
                      dimension_size_type w,
                      dimension_size_type h) const;

      public:
        /**
         * Get open TIFF file.
         *
         * @note This will be null if setId has not been called.
         *
         * @returns a reference to the TIFF file.
         */
        ome::compat::shared_ptr<ome::bioformats::tiff::TIFF>
        getTIFF();

        /**
         * Get open TIFF file.
         *
         * @note This will be null if setId has not been called.
         *
         * @returns a reference to the TIFF file.
         */
        const ome::compat::shared_ptr<ome::bioformats::tiff::TIFF>
        getTIFF() const;
      };

    }
  }
}

#endif // OME_BIOFORMATS_IN_MINIMALTIFFREADER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

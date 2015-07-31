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

#ifndef OME_BIOFORMATS_OUT_MINIMALTIFFWRITER_H
#define OME_BIOFORMATS_OUT_MINIMALTIFFWRITER_H

#include <ome/bioformats/detail/FormatWriter.h>
#include <ome/bioformats/tiff/Util.h>

#include <ome/common/log.h>

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

    namespace out
    {

      /**
       * Basic TIFF writer.
       *
       * @note Any derived writer which does not implement its own
       * openBytesImpl() must fill @c seriesIFDRange.
       */
      class MinimalTIFFWriter : public ::ome::bioformats::detail::FormatWriter
      {
      protected:
        /// Message logger.
        ome::common::Logger logger;

        /// Underlying TIFF file.
        mutable ome::compat::shared_ptr<ome::bioformats::tiff::TIFF> tiff;

        /// Current IFD.
        mutable ome::compat::shared_ptr<ome::bioformats::tiff::IFD> ifd;

        /// Current plane.
        mutable dimension_size_type ifdIndex;

        /// Mapping between series index and start and end IFD as a half-open range.
        tiff::SeriesIFDRange seriesIFDRange;

      private:
        /// Write a Big TIFF
        boost::optional<bool> bigTIFF;

      public:
        /// Constructor.
        MinimalTIFFWriter();

        /// Constructor with writer properties (for derived writers).
        MinimalTIFFWriter(const ome::bioformats::detail::WriterProperties& writerProperties);

        /// Destructor.
        virtual
        ~MinimalTIFFWriter();

        // Documented in superclass.
        void
        setId(const boost::filesystem::path& id);

        // Documented in superclass.
        void
        close(bool fileOnly = false);

      public:
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
        using FormatWriter::saveBytes;

        // Documented in superclass.
        void
        saveBytes(dimension_size_type plane,
                  VariantPixelBuffer& buf,
                  dimension_size_type x,
                  dimension_size_type y,
                  dimension_size_type w,
                  dimension_size_type h);

        /**
         * Set use of BigTIFF support.
         *
         * Enable or disable use of BigTIFF support for writing files
         * larger than 4GiB.
         *
         * @note libtiff must be compiled with BigTIFF support enabled
         * for this option to have any effect.
         *
         * @param big @c true to enable or @c false to disable BigTIFF
         * support.
         */
        void
        setBigTIFF(boost::optional<bool> big = true);

        /**
         * Query use of BigTIFF support.
         *
         * If setBigTIFF has not been used to enable or disable
         * BigTIFF explicitly, BigTIFF support will be disabled by
         * default for data less than 4GiB, unless the data would be
         * larger than 4GiB, in which case it will be enabled by
         * default.
         *
         * @returns @c true if BigTIFF support are enabled, or @c
         * false if disabled.
         */
        boost::optional<bool>
        getBigTIFF() const;
      };

    }
  }
}

#endif // OME_BIOFORMATS_OUT_MINIMALTIFFWRITER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

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

#include <algorithm>
#include <cmath>
#include <cstdarg>

#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/Sentry.h>
#include <ome/bioformats/tiff/Exception.h>

#include <ome/compat/thread.h>
#include <ome/compat/string.h>

#include <tiffio.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      namespace
      {

        class IFDConcrete : public IFD
        {
        public:
          IFDConcrete(std::shared_ptr<TIFF>& tiff,
                      offset_type            offset):
            IFD(tiff, offset)
          {
          }

          virtual
          ~IFDConcrete()
          {
          }
        };

      }

      /**
       * Internal implementation details of OffsetIFD.
       */
      class IFD::Impl
      {
      public:
        /// Reference to the parent TIFF.
        std::shared_ptr<TIFF> tiff;
        /// Offset of this IFD.
        offset_type offset;

        /**
         * Constructor.
         *
         * @param tiff the parent TIFF.
         * @param offset the IFD offset.
         */
        Impl(std::shared_ptr<TIFF>& tiff,
             offset_type            offset):
          tiff(tiff),
          offset(offset)
        {
        }

        /// Destructor.
        ~Impl()
        {
        }

      private:
        /// Copy constructor (deleted).
        Impl (const Impl&);

        /// Assignment operator (deleted).
        Impl&
        operator= (const Impl&);
      };

      IFD::IFD(std::shared_ptr<TIFF>& tiff,
               offset_type            offset):
        // Note boost::make_shared makes arguments const, so can't use
        // here.
        impl(std::shared_ptr<Impl>(new Impl(tiff, offset)))
      {
      }

      IFD::~IFD()
      {
      }

      std::shared_ptr<IFD>
      IFD::openIndex(std::shared_ptr<TIFF>& tiff,
                     directory_index_type   index)
      {
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        if (!TIFFSetDirectory(tiffraw, index))
          sentry.error();

        // Note boost::make_shared makes arguments const, so can't use
        // here.
        return openOffset(tiff, static_cast<uint64_t>(TIFFCurrentDirOffset(tiffraw)));
      }

      std::shared_ptr<IFD>
      IFD::openOffset(std::shared_ptr<TIFF>& tiff,
                      offset_type            offset)
      {
        // Note boost::make_shared makes arguments const, so can't use
        // here.
        return std::shared_ptr<IFD>(new IFDConcrete(tiff, offset));
      }

      void
      IFD::makeCurrent() const
      {
        std::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        if (static_cast<offset_type>(TIFFCurrentDirOffset(tiffraw)) != impl->offset)
          {
#if TIFF_HAVE_BIGTIFF
            if (!TIFFSetSubDirectory(tiffraw, impl->offset))
              sentry.error();
#else // !TIFF_HAVE_BIGTIFF
            if (!TIFFSetSubDirectory(tiffraw, static_cast<uint32_t>(impl->offset)))
              sentry.error();
#endif // TIFF_HAVE_BIGTIFF
          }
      }

      std::shared_ptr<TIFF>&
      IFD::getTIFF() const
      {
        return impl->tiff;
      }

      offset_type
      IFD::getOffset() const
      {
        return impl->offset;
      }

      void
      IFD::getField(tag_type tag,
                    ...) const
      {
        std::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        va_list ap;
        va_start(ap, tag);

        if (!tag)
          throw Exception("Error getting field: Tag is not valid");

        if (!TIFFVGetField(tiffraw, tag, ap))
          sentry.error();
      }

      void
      IFD::setField(tag_type tag,
                    ...)
      {
        std::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        va_list ap;
        va_start(ap, tag);

        if (!tag)
          throw Exception("Error getting field: Tag is not valid");

        if (!TIFFVSetField(tiffraw, tag, ap))
          sentry.error();
      }

      std::shared_ptr<IFD>
      IFD::next() const
      {
        std::shared_ptr<IFD> ret;

        std::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        if (TIFFReadDirectory(tiffraw) == 1)
          {
            uint64_t offset = static_cast<uint64_t>(TIFFCurrentDirOffset(tiffraw));
            ret = openOffset(tiff, offset);
          }

        return ret;
      }

      bool
      IFD::last() const
      {
        std::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        return TIFFLastDirectory(tiffraw) != 0;
      }

    }
  }
}

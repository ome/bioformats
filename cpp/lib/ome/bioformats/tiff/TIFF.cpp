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

#include <algorithm>
#include <cmath>
#include <cstdarg>

#include <boost/range/size.hpp>
#include <boost/thread.hpp>

#include <ome/bioformats/tiff/Field.h>
#include <ome/bioformats/tiff/Tags.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/Sentry.h>
#include <ome/bioformats/tiff/Exception.h>
#include <ome/bioformats/detail/tiff/Tags.h>

#include <ome/common/string.h>

#include <ome/internal/config.h>
#include <ome/internal/version.h>

#include <tiffio.h>

using boost::mutex;
using boost::lock_guard;

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      /// @copydoc IFDIterator::increment()
      template<>
      void
      IFDIterator<IFD>::increment()
      {
        pos = pos->next();
      }

      /// @copydoc IFDIterator::increment()
      template<>
      void
      IFDIterator<const IFD>::increment()
      {
        pos = pos->next();
      }

      namespace
      {

        class TIFFConcrete : public TIFF
        {
        public:
          TIFFConcrete(const boost::filesystem::path& filename,
                       const std::string&             mode):
            TIFF(filename, mode)
          {
          }

          virtual
          ~TIFFConcrete()
          {
          }
        };

      }

      /**
       * Internal implementation details of TIFF.
       */
      class TIFF::Impl
      {
      public:
        /// The libtiff file handle.
        ::TIFF *tiff;
        /// The number of IFDs.
        directory_index_type directoryCount;

        /**
         * The constructor.
         *
         * Opens the TIFF using TIFFOpen().
         *
         * @param filename the filename to open.
         * @param mode the file open mode.
         */
        Impl(const boost::filesystem::path& filename,
             const std::string&             mode):
          tiff(),
          directoryCount(0)
        {
          Sentry sentry;

#ifdef _MSC_VER
          tiff = TIFFOpenW(filename.wstring().c_str(), mode.c_str());
#else
          tiff = TIFFOpen(filename.string().c_str(), mode.c_str());
#endif
          if (!tiff)
            sentry.error();
        }

        /**
         * The destructor.
         *
         * The open TIFF will be closed if open.
         */
        ~Impl()
        {
          try
            {
              close();
            }
          catch (const Exception&)
            {
              /// @todo Log the error elsewhere.
            }
          catch (...)
            {
              // Catch any exception thrown by closing.
            }
        }

      private:
        /// Copy constructor (deleted).
        Impl (const Impl&);

        /// Assignment operator (deleted).
        Impl&
        operator= (const Impl&);

      public:
        /**
         * Close the libtiff file handle.
         *
         * If open, the file handle will be closed with TIFFClose.
         */
        void
        close()
        {
          if (tiff)
            {
              Sentry sentry;

              TIFFClose(tiff);
              if (!sentry.getMessage().empty())
                sentry.error();
              tiff = 0;
            }
        }
      };

      // Note boost::make_shared can't be used here.
      TIFF::TIFF(const boost::filesystem::path& filename,
                 const std::string&             mode):
        impl(ome::compat::shared_ptr<Impl>(new Impl(filename, mode)))
      {
        registerImageJTags();
      }

      TIFF::~TIFF()
      {
      }

      TIFF::wrapped_type *
      TIFF::getWrapped() const
      {
        return reinterpret_cast<wrapped_type *>(impl->tiff);
      }

      ome::compat::shared_ptr<TIFF>
      TIFF::open(const boost::filesystem::path& filename,
                 const std::string& mode)
      {
        ome::compat::shared_ptr<TIFF> ret;
        try
          {
            // Note boost::make_shared can't be used here.
            ret = ome::compat::shared_ptr<TIFF>(new TIFFConcrete(filename, mode));
          }
        catch (const std::exception& e)
          {
            // All exception types are propagated as an Exception.
            throw Exception(e.what());
          }
        return ret;
      }

      void
      TIFF::close()
      {
        impl->close();
      }

      TIFF::operator bool ()
      {
        return impl && impl->tiff;
      }

      directory_index_type
      TIFF::directoryCount() const
      {
        if (!impl->directoryCount)
          {
            directory_index_type nIFD = 0U;
            for (const_iterator i = begin();
                 i != end();
                 ++i, ++nIFD);
            impl->directoryCount = nIFD;
          }
        return impl->directoryCount;
      }

      ome::compat::shared_ptr<IFD>
      TIFF::getDirectoryByIndex(directory_index_type index) const
      {
        Sentry sentry;

        if (!TIFFSetDirectory(impl->tiff, index))
          sentry.error();

        ome::compat::shared_ptr<TIFF> t(ome::compat::const_pointer_cast<TIFF>(shared_from_this()));
        return IFD::openIndex(t, index);
      }

      ome::compat::shared_ptr<IFD>
      TIFF::getDirectoryByOffset(offset_type offset) const
      {
        Sentry sentry;

#if TIFF_HAVE_BIGTIFF
        if (!TIFFSetSubDirectory(impl->tiff, offset))
          sentry.error();
#else // !TIFF_HAVE_BIGTIFF
        if (!TIFFSetSubDirectory(impl->tiff, static_cast<uint32_t>(offset)))
          sentry.error();
#endif // TIFF_HAVE_BIGTIFF

        ome::compat::shared_ptr<TIFF> t(ome::compat::const_pointer_cast<TIFF>(shared_from_this()));
        return IFD::openOffset(t, offset);
      }

      ome::compat::shared_ptr<IFD>
      TIFF::getCurrentDirectory() const
      {
        ome::compat::shared_ptr<TIFF> t(ome::compat::const_pointer_cast<TIFF>(shared_from_this()));
        return IFD::current(t);
      }

      void
      TIFF::writeCurrentDirectory()
      {
        Sentry sentry;

        static const std::string software("OME Bio-Formats (C++) " OME_VERSION_MAJOR_S "." OME_VERSION_MINOR_S "." OME_VERSION_PATCH_S);
        getCurrentDirectory()->getField(SOFTWARE).set(software);

        if (!TIFFWriteDirectory(impl->tiff))
          sentry.error("Failed to write current directory");
      }

      TIFF::iterator
      TIFF::begin()
      {
        ome::compat::shared_ptr<IFD> ifd(getDirectoryByIndex(0U));
        return iterator(ifd);
      }

      TIFF::const_iterator
      TIFF::begin() const
      {
        ome::compat::shared_ptr<IFD> ifd(getDirectoryByIndex(0U));
        return const_iterator(ifd);
      }

      TIFF::iterator
      TIFF::end()
      {
        return iterator();
      }

      TIFF::const_iterator
      TIFF::end() const
      {
        return const_iterator();
      }

      void
      TIFF::registerImageJTags()
      {
        // This is optional, used for quieting libtiff messages about
        // unknown tags by registering them.  This doesn't work
        // completely since some warnings will be issued reading the
        // first directory, before we can register them.  This is
        // deprecated in libtiff4, so guard against future removal.
#ifdef TIFF_HAVE_MERGEFIELDINFO
        // These static strings are to provide a writable string to
        // comply with the TIFFFieldInfo interface which can't be
        // assigned const string literals.  They must outlive the
        // registered field info.
        static std::string ijbc("ImageJMetadataByteCounts");
        static std::string ij("ImageJMetadata");
        static const TIFFFieldInfo ImageJFieldInfo[] =
          {
            {
              TIFFTAG_IMAGEJ_META_DATA_BYTE_COUNTS,
              TIFF_VARIABLE2, TIFF_VARIABLE2, TIFF_LONG, FIELD_CUSTOM,
              true, true, const_cast<char *>(ijbc.c_str())
            },
            {
              TIFFTAG_IMAGEJ_META_DATA,
              TIFF_VARIABLE2, TIFF_VARIABLE2, TIFF_BYTE, FIELD_CUSTOM,
              true, true, const_cast<char *>(ij.c_str())
            }
          };

        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(getWrapped());

        Sentry sentry;

# if TIFF_HAVE_MERGEFIELDINFO_RETURN
        int e = TIFFMergeFieldInfo(tiffraw, ImageJFieldInfo, boost::size(ImageJFieldInfo));
        if (e)
          sentry.error();
# else // !TIFF_HAVE_MERGEFIELDINFO_RETURN
        TIFFMergeFieldInfo(tiffraw, ImageJFieldInfo, boost::size(ImageJFieldInfo));
#endif // TIFF_HAVE_MERGEFIELDINFO_RETURN
#endif // TIFF_HAVE_MERGEFIELDINFO
      }

    }
  }
}

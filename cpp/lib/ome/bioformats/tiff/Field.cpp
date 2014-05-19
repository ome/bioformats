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

#include <limits>

#include <ome/bioformats/tiff/Field.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/Sentry.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/detail/tiff/Tags.h>

#include <tiffio.h>

#include <boost/algorithm/string.hpp>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      /**
       * Internal implementation details of FieldBase.
       */
      class FieldBase::Impl
      {
      public:
        /// Weak reference to the parent IFD.
        std::weak_ptr<IFD>  ifd;
        /// The tag being wrapped.
        tag_type            tag;
        /// Field information for this tag.
        const ::TIFFField  *fieldinfo;

        /**
         * Constructor.
         *
         * @param ifd the directory the field belongs to.
         * @param tag the tag identifying this field.
         */
        Impl(std::shared_ptr<IFD>& ifd,
             tag_type              tag):
          ifd(ifd),
          tag(tag),
          fieldinfo()
        {
        }

        /// Destructor.
        ~Impl()
        {
        }

        /**
         * Get the directory this field belongs to.
         *
         * @returns the directory.
         */
        std::shared_ptr<IFD>
        getIFD() const
        {
          std::shared_ptr<IFD> sifd = std::shared_ptr<IFD>(ifd);
          if (!sifd)
            throw Exception("Field reference to IFD no longer valid");

          return sifd;
        }

        /**
         * Get the TIFF this field belongs to.
         *
         * @note Needs wrapping in a sentry by the caller.
         *
         * @returns a pointer to the underlying TIFF.
         */
        ::TIFF *
        getTIFF()
        {
          getIFD()->makeCurrent();
          ::TIFF *tiff = reinterpret_cast< ::TIFF *>(getIFD()->getTIFF()->getWrapped());
          return tiff;
        }

        /**
         * Get information from libtiff about this field.
         *
         * libtiff is not aware of every tag, or special-purpose
         * private tags.
         *
         * @returns the field information, or null if the tag was
         * unknown.
         */
        const ::TIFFField *
        getFieldInfo()
        {
          if (!fieldinfo)
            {
              Sentry sentry;

              fieldinfo = TIFFFindField(getTIFF(), tag, TIFF_ANY);
              if (!fieldinfo)
                std::cerr << "getFieldInfo: tag " << tag << " unknown\n";
            }

          return fieldinfo;
        }
      };

      FieldBase::FieldBase(std::shared_ptr<IFD> ifd,
                           tag_type             tag):
        impl(std::shared_ptr<Impl>(new Impl(ifd, tag)))
      {
      }

      FieldBase::~FieldBase()
      {
      }

      std::string
      FieldBase::name() const
      {
        Sentry sentry;

        std::string ret;

        const ::TIFFField *field = impl->getFieldInfo();
        if (field)
          {
            ret = TIFFFieldName(field);
          }
        else
          {
            std::ostringstream os;
            os << impl->tag;
            ret = os.str();
          }

        return ret;
      }

      Type
      FieldBase::type() const
      {
        Sentry sentry;

        Type ret = TYPE_UNDEFINED;

        const ::TIFFField *field = impl->getFieldInfo();
        if (field)
          ret = static_cast<Type>(TIFFFieldDataType(field));

        return ret;
      }

      bool
      FieldBase::passCount() const
      {
        Sentry sentry;

        bool ret = false;

        const ::TIFFField *field = impl->getFieldInfo();
        if (field)
          ret = (TIFFFieldPassCount(field));

        return ret;
      }

      int
      FieldBase::readCount() const
      {
        Sentry sentry;

        int ret = 1;

        const ::TIFFField *field = impl->getFieldInfo();
        if (field)
          ret = TIFFFieldReadCount(field);

        return ret;
      }

      int
      FieldBase::writeCount() const
      {
        Sentry sentry;

        int ret = 1;

        const ::TIFFField *field = impl->getFieldInfo();
        if (field)
          ret = TIFFFieldWriteCount(field);

        return ret;
      }

      tag_type
      FieldBase::tagNumber() const
      {
        return impl->tag;
      }

      std::shared_ptr<IFD>
      FieldBase::getIFD() const
      {
        return impl->getIFD();
      }

      namespace
      {

        template<typename T>
        void
        generic_get1(std::shared_ptr<IFD> ifd,
                     tag_type tag,
                     bool /* passcount */,
                     int /* readcount */,
                     T& value)
        {
          ifd->getField(tag, &value);
        }

        template<typename T>
        void
        generic_set1(std::shared_ptr<IFD> ifd,
                     tag_type tag,
                     bool /* passcount */,
                     int /* writecount */,
                     const T& value)
        {
          ifd->setField(tag, value);
        }

        template<typename T>
        void
        generic_get2(std::shared_ptr<IFD> ifd,
                     tag_type tag,
                     bool /* passcount */,
                     int /* readcount */,
                     T& value)
        {
          ifd->getField(tag, &value[0], &value[1]);
        }

        template<typename T>
        void
        generic_set2(std::shared_ptr<IFD> ifd,
                     tag_type tag,
                     bool /* passcount */,
                     int /* writecount */,
                     const T& value)
        {
          ifd->setField(tag, value[0], value[1]);
        }

        template<typename T>
        void
        generic_get3(std::shared_ptr<IFD> ifd,
                     tag_type tag,
                     bool /* passcount */,
                     int /* readcount */,
                     T& value)
        {
          ifd->getField(tag, &value[0], &value[1], &value[2]);
        }

        template<typename T>
        void
        generic_set3(std::shared_ptr<IFD> ifd,
                     tag_type tag,
                     bool /* passcount */,
                     int /* writecount */,
                     const T& value)
        {
          ifd->setField(tag, value[0], value[1], value[2]);
        }

        template<typename T>
        void
        generic_get6(std::shared_ptr<IFD> ifd,
                     tag_type tag,
                     bool /* passcount */,
                     int /* readcount */,
                     T& value)
        {
          ifd->getField(tag, &value[0], &value[1], &value[2], &value[3], &value[4], &value[5]);
        }

        template<typename T>
        void
        generic_set6(std::shared_ptr<IFD> ifd,
                     tag_type tag,
                     bool /* passcount */,
                     int /* writecount */,
                     const T& value)
        {
          ifd->setField(tag, value[0], value[1], value[2], value[3], value[4], value[5]);
        }

        template<typename T>
        void
        generic_enum16_get1(std::shared_ptr<IFD> ifd,
                            tag_type tag,
                            Type type,
                            bool passcount,
                            int readcount,
                            T& value)
        {
          if (type != TYPE_SHORT &&
              passcount != false &&
              readcount != 1)
            throw Exception("FieldInfo mismatch with Field handler");

          uint16_t v;
          generic_get1(ifd, tag, passcount, readcount, v);
          value = static_cast<T>(v);
        }

        template<typename T>
        void
        generic_enum16_set1(std::shared_ptr<IFD> ifd,
                            tag_type tag,
                            Type type,
                            bool passcount,
                            int writecount,
                            const T& value)
        {
          if (type != TYPE_SHORT &&
              passcount != false &&
              writecount != 1)
            throw Exception("FieldInfo mismatch with Field handler");

          uint16_t v = static_cast<uint16_t>(value);
          generic_set1(ifd, tag, passcount, writecount, v);
        }

        template<typename T>
        void
        generic_array_get1(std::shared_ptr<IFD> ifd,
                           tag_type tag,
                           int readcount,
                           T& value)
        {
          // Special case:
          if (tag == TIFFTAG_IMAGEJ_META_DATA_BYTE_COUNTS ||
              tag == TIFFTAG_IMAGEJ_META_DATA)
            {
              readcount = TIFF_VARIABLE2;
            }
          else if (tag == TIFFTAG_STRIPOFFSETS ||
                   tag == TIFFTAG_STRIPBYTECOUNTS)
            {
              uint16_t pc;
              ifd->getField(TIFFTAG_PLANARCONFIG, &pc);
              uint16_t spp;
              ifd->getField(TIFFTAG_SAMPLESPERPIXEL, &spp);
              uint32_t ilen;
              ifd->getField(TIFFTAG_IMAGELENGTH, &ilen);
              uint32_t rps;
              ifd->getField(TIFFTAG_ROWSPERSTRIP, &rps);
              uint32_t spi = static_cast<uint32_t>(std::floor((static_cast<float>(ilen + rps - 1)) / static_cast<float>(rps)));
              if (pc == PLANARCONFIG_CONTIG)
                readcount = spi;
              else if (pc == PLANARCONFIG_SEPARATE)
                readcount = spp * spi;
            }
          else if (tag == TIFFTAG_TILEOFFSETS ||
                   tag == TIFFTAG_TILEBYTECOUNTS)
            {
              uint16_t pc;
              ifd->getField(TIFFTAG_PLANARCONFIG, &pc);
              uint16_t spp;
              ifd->getField(TIFFTAG_SAMPLESPERPIXEL, &spp);
              uint32_t ilen, iwid, twid, tlen;
              ifd->getField(TIFFTAG_IMAGELENGTH, &ilen);
              ifd->getField(TIFFTAG_IMAGEWIDTH, &iwid);
              ifd->getField(TIFFTAG_TILELENGTH, &tlen);
              ifd->getField(TIFFTAG_TILEWIDTH, &twid);
              uint32_t tacross = (iwid + twid - 1) / twid;
              uint32_t tdown = (ilen + tlen - 1) / tlen;
              uint32_t tpi = tacross * tdown;
              if (pc == PLANARCONFIG_CONTIG)
                readcount = tpi;
              else if (pc == PLANARCONFIG_SEPARATE)
                readcount = spp * tpi;
            }

          typename T::value_type *valueptr;
          uint32_t count;

          if (readcount == TIFF_SPP)
            {
              uint16_t spp;
              ifd->getField(TIFFTAG_SAMPLESPERPIXEL, &spp);
              ifd->getField(tag, &valueptr);
              count = static_cast<uint32_t>(spp);
            }
          else if (readcount == TIFF_VARIABLE)
            {
              uint16_t n;
              ifd->getField(tag, &n, &valueptr);
              count = static_cast<uint32_t>(n);
            }
          else if (readcount == TIFF_VARIABLE2)
            {
              ifd->getField(tag, &count, &valueptr);
            }
          else
            {
              ifd->getField(tag, &valueptr);
              count = static_cast<uint32_t>(readcount);
            }

          value = T(valueptr, valueptr + count);
        }

        template<typename T>
        void
        generic_array_set1(std::shared_ptr<IFD> ifd,
                           tag_type tag,
                           int writecount,
                           const T& value)
        {
          if (writecount == TIFF_SPP)
            {
              uint16_t spp;
              ifd->getField(TIFFTAG_SAMPLESPERPIXEL, &spp);
              if (value.size() != spp)
                throw Exception("Field array size does not match SamplesPerPixel");
              ifd->setField(tag, value.data());
            }
          else if (writecount == TIFF_VARIABLE)
            {
              if (value.size() > std::numeric_limits<uint16_t>::max())
                throw Exception("Field array size is greater than maximum write count");
              uint16_t n = static_cast<uint16_t>(value.size());
              ifd->setField(tag, n, value.data());
            }
          else if (writecount == TIFF_VARIABLE2)
            {
              if (value.size() > std::numeric_limits<uint32_t>::max())
                throw Exception("Field array size is greater than maximum write count");
              ifd->setField(tag, value.size(), value.data());
            }
          else
            {
              ifd->setField(tag, value.data());
            }
        }

        template<typename T>
        void
        generic_array_get3(std::shared_ptr<IFD> ifd,
                           tag_type tag,
                           int readcount,
                           T& value)
        {
          bool limit = false; // Special case for TIFFTAG_TRANSFERFUNCTION which can used 1 or 3 vectors.
          typename T::value_type::value_type *valueptr0, *valueptr1, *valueptr2;
          uint32_t count;

          // Special case:
          if (tag == TIFFTAG_COLORMAP ||
              tag == TIFFTAG_TRANSFERFUNCTION)
            {
              uint16_t spp;
              ifd->getField(TIFFTAG_SAMPLESPERPIXEL, &spp);
              uint16_t bps;
              ifd->getField(TIFFTAG_BITSPERSAMPLE, &bps);
              count = 1U << bps;
              if (spp == 1)
                limit = true;
            }

          if (readcount == TIFF_SPP)
            {
              uint16_t spp;
              ifd->getField(TIFFTAG_SAMPLESPERPIXEL, &spp);
              ifd->getField(tag, &valueptr0, &valueptr1, &valueptr2);
              count = static_cast<uint32_t>(spp);
            }
          else if (readcount == TIFF_VARIABLE)
            {
              uint16_t n;
              ifd->getField(tag, &n, &valueptr0, &valueptr1, &valueptr2);
              count = static_cast<uint32_t>(n);
            }
          else if (readcount == TIFF_VARIABLE2)
            {
              ifd->getField(tag, &count, &valueptr0, &valueptr1, &valueptr2);
            }
          else
            {
              if (!limit)
                ifd->getField(tag, &valueptr0, &valueptr1, &valueptr2);
              else
                ifd->getField(tag, &valueptr0);
              count = static_cast<uint32_t>(readcount);
            }

          value.at(0) = typename T::value_type(valueptr0, valueptr0 + count);
          if (!limit)
            {
              value[1] = typename T::value_type(valueptr1, valueptr1 + count);
              value[2] = typename T::value_type(valueptr2, valueptr2 + count);
            }
          else
            {
              value[1].clear();
              value[2].clear();
            }
        }

        template<typename T>
        void
        generic_array_set3(std::shared_ptr<IFD> ifd,
                           tag_type tag,
                           int writecount,
                           const T& value)
        {
          if (value[0].size() != value[1].size() ||
              value[0].size() != value[2].size())
            throw Exception("Field array sizes are not equal");

          bool limit = false; // Special case for TIFFTAG_TRANSFERFUNCTION which can used 1 or 3 vectors.

          // Special case:
          if (tag == TIFFTAG_COLORMAP ||
              tag == TIFFTAG_TRANSFERFUNCTION)
            {
              uint16_t spp;
              ifd->getField(TIFFTAG_SAMPLESPERPIXEL, &spp);
              if (spp == 1)
                limit = true;
            }

          if (writecount == TIFF_SPP)
            {
              uint16_t spp;
              ifd->getField(TIFFTAG_SAMPLESPERPIXEL, &spp);
              if (value.size() != spp)
                throw Exception("Field array size does not match SamplesPerPixel");
              ifd->setField(tag, value[0].data(), value[1].data(), value[2].data());
            }
          else if (writecount == TIFF_VARIABLE)
            {
              if (value[0].size() > std::numeric_limits<uint16_t>::max())
                throw Exception("Field array size is greater than maximum write count");
              uint16_t n = static_cast<uint16_t>(value.size());
              ifd->setField(tag, n, value[0].data(), value[1].data(), value[2].data());
            }
          else if (writecount == TIFF_VARIABLE2)
            {
              if (value[0].size() > std::numeric_limits<uint32_t>::max())
                throw Exception("Field array size is greater than maximum write count");
              ifd->setField(tag, value.size(), value[0].data(), value[1].data(), value[2].data());
            }
          else
            {
              if (!limit)
                ifd->setField(tag, value[0].data(), value[1].data(), value[2].data());
              else
                ifd->setField(tag, value[0].data());
            }
        }

        template<typename T>
        void
        generic_enum16_array_get1(std::shared_ptr<IFD> ifd,
                                  tag_type tag,
                                  int readcount,
                                  T& value)
        {
          std::vector<uint16_t> v;
          generic_array_get1(ifd, tag, readcount, v);
          value.clear();
          for (std::vector<uint16_t>::const_iterator i = v.begin();
               i != v.end();
               ++i)
            value.push_back(static_cast<typename T::value_type>(*i));
        }

        template<typename T>
        void
        generic_enum16_array_set1(std::shared_ptr<IFD> ifd,
                                  tag_type tag,
                                  int writecount,
                                  const T& value)
        {
          std::vector<uint16_t> v;
          for(typename T::const_iterator i = value.begin();
              i != value.end();
              ++i)
            v.push_back(static_cast<uint16_t>(*i));
          generic_array_set1(ifd, tag, writecount, v);
        }

      }

      /// @copydoc Field::get()
      template<>
      void
      Field<StringTag1>::get(value_type& value) const
      {
        if (type() != TYPE_ASCII &&
            passCount() != false)
          throw Exception("FieldInfo mismatch with Field handler");

        int rc = readCount();

        if (rc == TIFF_VARIABLE || rc == TIFF_VARIABLE2)
          {
            char *text;
            getIFD()->getField(impl->tag, &text);
            value = text;
          }
        else
          {
            std::vector<char> text(rc);
            getIFD()->getField(impl->tag, text.data());
            value = std::string(text.begin(), text.end());
          }
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<StringTag1>::set(const value_type& value)
      {
        if (type() != TYPE_ASCII &&
            passCount() != false &&
            writeCount() != TIFF_VARIABLE)
          throw Exception("FieldInfo mismatch with Field handler");


        getIFD()->setField(impl->tag, value.c_str());
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<StringTagArray1>::get(value_type& value) const
      {
        if (type() != TYPE_ASCII &&
            passCount() != true)
          throw Exception("FieldInfo mismatch with Field handler");

        const char *text;
        getIFD()->getField(impl->tag, text);

        boost::algorithm::split(value, text, boost::is_any_of("\0"), boost::token_compress_on);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<StringTagArray1>::set(const value_type& value)
      {
        if (type() != TYPE_ASCII &&
            passCount() != false &&
            writeCount() != TIFF_VARIABLE)
          throw Exception("FieldInfo mismatch with Field handler");

        std::string s(boost::algorithm::join(value, "\0"));
        // Split value vector into a null-terminated string.
        getIFD()->setField(impl->tag, s.c_str());
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16Tag1>::get(value_type& value) const
      {
        if (type() != TYPE_SHORT &&
            passCount() != false &&
            ((impl->tag != TIFFTAG_BITSPERSAMPLE &&  // broken in libtiff
              impl->tag != TIFFTAG_COMPRESSION &&    // broken in libtiff
              impl->tag != TIFFTAG_DATATYPE &&       // broken in libtiff
              impl->tag != TIFFTAG_MINSAMPLEVALUE && // broken in libtiff
              impl->tag != TIFFTAG_MAXSAMPLEVALUE && // broken in libtiff
              impl->tag != TIFFTAG_SAMPLEFORMAT) &&  // broken in libtiff
             readCount() != 1))
          throw Exception("FieldInfo mismatch with Field handler");

        generic_get1(getIFD(), impl->tag, passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16Tag1>::set(const value_type& value)
      {
        if (type() != TYPE_SHORT &&
            passCount() != false &&
            ((impl->tag != TIFFTAG_BITSPERSAMPLE) &&
             writeCount() != 1))
          throw Exception("FieldInfo mismatch with Field handler");

        generic_set1(getIFD(), impl->tag, passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16Orientation1>::get(value_type& value) const
      {
        generic_enum16_get1(getIFD(), impl->tag, type(), passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16Orientation1>::set(const value_type& value)
      {
        generic_enum16_set1(getIFD(), impl->tag, type(), passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16PhotometricInterpretation1>::get(value_type& value) const
      {
        generic_enum16_get1(getIFD(), impl->tag, type(), passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16PhotometricInterpretation1>::set(const value_type& value)
      {
        generic_enum16_set1(getIFD(), impl->tag, type(), passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16Predictor1>::get(value_type& value) const
      {
        generic_enum16_get1(getIFD(), impl->tag, type(), passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16Predictor1>::set(const value_type& value)
      {
        generic_enum16_set1(getIFD(), impl->tag, type(), passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16FillOrder1>::get(value_type& value) const
      {
        generic_enum16_get1(getIFD(), impl->tag, type(), passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16FillOrder1>::set(const value_type& value)
      {
        generic_enum16_set1(getIFD(), impl->tag, type(), passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16SampleFormat1>::get(value_type& value) const
      {
        generic_enum16_get1(getIFD(), impl->tag, type(), passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16SampleFormat1>::set(const value_type& value)
      {
        generic_enum16_set1(getIFD(), impl->tag, type(), passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16Threshholding1>::get(value_type& value) const
      {
        generic_enum16_get1(getIFD(), impl->tag, type(), passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16Threshholding1>::set(const value_type& value)
      {
        generic_enum16_set1(getIFD(), impl->tag, type(), passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16YCbCrPosition1>::get(value_type& value) const
      {
        generic_enum16_get1(getIFD(), impl->tag, type(), passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16YCbCrPosition1>::set(const value_type& value)
      {
        generic_enum16_set1(getIFD(), impl->tag, type(), passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16Tag2>::get(value_type& value) const
      {
        if (type() != TYPE_SHORT &&
            passCount() != false &&
            ((impl->tag != BITSPERSAMPLE && readCount() != TIFF_VARIABLE) &&
             readCount() != 2))
          throw Exception("FieldInfo mismatch with Field handler");

        generic_get2(getIFD(), impl->tag, passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16Tag2>::set(const value_type& value)
      {
        if (type() != TYPE_SHORT &&
            passCount() != false &&
            writeCount() != 2)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_set2(getIFD(), impl->tag, passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<FloatTag1>::get(value_type& value) const
      {
        if (type() != TYPE_RATIONAL &&
            passCount() != false &&
            readCount() != 1)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_get1(getIFD(), impl->tag, passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<FloatTag1>::set(const value_type& value)
      {
        if (type() != TYPE_RATIONAL &&
            passCount() != false &&
            writeCount() != 1)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_set1(getIFD(), impl->tag, passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<FloatTag2>::get(value_type& value) const
      {
        if (type() != TYPE_RATIONAL &&
            passCount() != false &&
            readCount() != 2)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_get2(getIFD(), impl->tag, passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<FloatTag2>::set(const value_type& value)
      {
        if (type() != TYPE_RATIONAL &&
            passCount() != false &&
            writeCount() != 2)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_set2(getIFD(), impl->tag, passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<FloatTag3>::get(value_type& value) const
      {
        if (type() != TYPE_RATIONAL &&
            passCount() != false &&
            readCount() != 3)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_get3(getIFD(), impl->tag, passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<FloatTag3>::set(const value_type& value)
      {
        if (type() != TYPE_RATIONAL &&
            passCount() != false &&
            writeCount() != 3)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_set3(getIFD(), impl->tag, passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<FloatTag6>::get(value_type& value) const
      {
        if (type() != TYPE_RATIONAL &&
            passCount() != false &&
            readCount() != 6)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_get6(getIFD(), impl->tag, passCount(), readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<FloatTag6>::set(const value_type& value)
      {
        if (type() != TYPE_RATIONAL &&
            passCount() != false &&
            writeCount() != 6)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_set6(getIFD(), impl->tag, passCount(), writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16ExtraSamplesArray1>::get(value_type& value) const
      {
        if (impl->tag != TIFFTAG_IMAGEJ_META_DATA && // private
            type() != TYPE_SHORT)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_enum16_array_get1(getIFD(), impl->tag, readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16ExtraSamplesArray1>::set(const value_type& value)
      {
        if (impl->tag != TIFFTAG_IMAGEJ_META_DATA && // private
            type() != TYPE_SHORT)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_enum16_array_set1(getIFD(), impl->tag, writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt16TagArray3>::get(value_type& value) const
      {
        if (type() != TYPE_SHORT)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_array_get3(getIFD(), impl->tag, readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt16TagArray3>::set(const value_type& value)
      {
        if (type() != TYPE_SHORT)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_array_set3(getIFD(), impl->tag, writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt32TagArray1>::get(value_type& value) const
      {
        if (impl->tag != TIFFTAG_IMAGEJ_META_DATA_BYTE_COUNTS && // private
            type() != TYPE_LONG)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_array_get1(getIFD(), impl->tag, readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt32TagArray1>::set(const value_type& value)
      {
        if (impl->tag != TIFFTAG_IMAGEJ_META_DATA_BYTE_COUNTS && // private
            type() != TYPE_LONG)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_array_set1(getIFD(), impl->tag, writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<UInt64TagArray1>::get(value_type& value) const
      {
        if (type() != TYPE_LONG8 && type() != TYPE_IFD8)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_array_get1(getIFD(), impl->tag, readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<UInt64TagArray1>::set(const value_type& value)
      {
        if (type() != TYPE_LONG8 && type() != TYPE_IFD8)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_array_set1(getIFD(), impl->tag, writeCount(), value);
      }

      /// @copydoc Field::get()
      template<>
      void
      Field<RawDataTag1>::get(value_type& value) const
      {
        if (type() != TYPE_BYTE && type() != TYPE_UNDEFINED)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_array_get1(getIFD(), impl->tag, readCount(), value);
      }

      /// @copydoc Field::set()
      template<>
      void
      Field<RawDataTag1>::set(const value_type& value)
      {
        if (type() != TYPE_BYTE && type() != TYPE_UNDEFINED)
          throw Exception("FieldInfo mismatch with Field handler");

        generic_array_set1(getIFD(), impl->tag, writeCount(), value);
      }

    }
  }
}

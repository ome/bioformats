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

#ifndef OME_BIOFORMATS_PIXELPROPERTIES_H
#define OME_BIOFORMATS_PIXELPROPERTIES_H

#include <ome/compat/array.h>
#include <ome/compat/cstdint.h>

#include <ome/bioformats/Types.h>

#include <ome/xml/model/enums/PixelType.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Map a given PixelPropertiesType enum to the corresponding language type.
     */
    template<int>
    struct PixelProperties;

    /**
     * Properties common to all pixel types.
     */
    template<class P>
    struct PixelPropertiesBase
    {
      /**
       * Get size of pixel type, in bytes.
       * @returns pixel size, in bytes.
       */
      static pixel_size_type
      pixel_byte_size()
      {
        return sizeof(typename P::type);
      }

      /**
       * Get size of pixel type, in bits.
       * @returns pixel size, in bits.
       */
      static pixel_size_type
      pixel_bit_size()
      {
        return pixel_byte_size() * 8;
      }
    };

    /// Properties of INT8 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::INT8> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::INT8> >
    {
      /// INT8 native pixel type.
      typedef int8_t type;
    };

    /// Properties of INT16 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::INT16> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::INT16> >
    {
      /// INT16 native pixel type.
      typedef int16_t type;
    };

    /// Properties of INT32 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::INT32> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::INT32> >
    {
      /// INT32 native pixel type.
      typedef int32_t type;
    };

    /// Properties of UINT8 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::UINT8> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::UINT8> >
    {
      /// UINT8 native pixel type.
      typedef uint8_t type;
    };

    /// Properties of UINT16 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::UINT16> :
      public PixelPropertiesBase<struct PixelProperties< ::ome::xml::model::enums::PixelType::UINT16> >
    {
      /// UINT16 native pixel type.
      typedef uint16_t type;
    };

    /// Properties of UINT32 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::UINT32> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::UINT32> >
    {
      /// UINT32 native pixel type.
      typedef uint32_t type;
    };

    /// Properties of FLOAT pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT> >
    {
      /// FLOAT native pixel type.
      typedef float type;
    };

    /// Properties of DOUBLE pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE> >
    {
      /// DOUBLE native pixel type.
      typedef double type;
    };

    /// Properties of BIT pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::BIT> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::BIT> >
    {
      /// BIT native pixel type.
      typedef bool type;
    };

    /// Properties of COMPLEX pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX> >
    {
      /// COMPLEX native pixel type.
      typedef std::array<float,2> type;
    };

    /// Properties of DOUBLECOMPLEX pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX> >
    {
      /// DOUBLECOMPLEX native pixel type.
      typedef std::array<double,2> type;
    };

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

    /**
     * Get the size of a PixelType, in bytes.
     *
     * @param pixeltype the PixelType to query.
     *
     * @returns the size, in bytes
     */
    inline pixel_size_type
    bytesPerPixel(::ome::xml::model::enums::PixelType pixeltype)
    {
      pixel_size_type size = 0;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::INT8>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::INT16>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::INT32>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::UINT8>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::UINT16>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::UINT32>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::BIT>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::pixel_byte_size();
          break;
        }

      return size;
    }

    /**
     * Get the size of a PixelType, in bits.
     *
     * @param pixeltype the PixelType to query.
     *
     * @returns the size, in bits
     */
    inline pixel_size_type
    bitsPerPixel(::ome::xml::model::enums::PixelType pixeltype)
    {
      pixel_size_type size = 0;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::INT8>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::INT16>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::INT32>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::UINT8>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::UINT16>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::UINT32>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::BIT>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          size = PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::pixel_bit_size();
          break;
        }

      return size;
    }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

  }
}

#endif // OME_BIOFORMATS_PIXELPROPERTIES_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

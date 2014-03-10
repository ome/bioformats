/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2013 Open Microscopy Environment:
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

#ifndef OME_BIOFORMATS_PIXEL_H
#define OME_BIOFORMATS_PIXEL_H

#include <ome/compat/array.h>
#include <ome/compat/cstdint.h>

#include <ome/xml/model/enums/PixelType.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Map a given PixelType enum to the corresponding language type.
     */
    template<int>
    struct Pixel;

    template<class P>
    struct PixelBase
    {
      static uint32_t
      pixel_byte_size()
      {
        return sizeof(typename P::type);
      }

      static uint32_t
      pixel_bit_size()
      {
        return pixel_byte_size() * 8;
      }
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::INT8> :
      public PixelBase<Pixel< ::ome::xml::model::enums::PixelType::INT8> >
    {
      typedef int8_t type;
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::INT16> :
      public PixelBase<Pixel< ::ome::xml::model::enums::PixelType::INT16> >
    {
      typedef int16_t type;
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::INT32> :
      public PixelBase<Pixel< ::ome::xml::model::enums::PixelType::INT32> >
    {
      typedef int32_t type;
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::UINT8> :
      public PixelBase<Pixel< ::ome::xml::model::enums::PixelType::UINT8> >
    {
      typedef uint8_t type;
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::UINT16> :
      public PixelBase<struct Pixel< ::ome::xml::model::enums::PixelType::UINT16> >
    {
      typedef uint16_t type;
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::UINT32> :
      public PixelBase<Pixel< ::ome::xml::model::enums::PixelType::UINT32> >
    {
      typedef uint32_t type;
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::FLOAT> :
      public PixelBase<Pixel< ::ome::xml::model::enums::PixelType::FLOAT> >
    {
      typedef float type;
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::DOUBLE> :
      public PixelBase<Pixel< ::ome::xml::model::enums::PixelType::DOUBLE> >
    {
      typedef double type;
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::BIT> :
      public PixelBase<Pixel< ::ome::xml::model::enums::PixelType::BIT> >
    {
      typedef bool type;
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::COMPLEX> :
      public PixelBase<Pixel< ::ome::xml::model::enums::PixelType::COMPLEX> >
    {
      typedef std::array<float,2> type;
    };

    template<>
    struct Pixel< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX> :
      public PixelBase<Pixel< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX> >
    {
      typedef std::array<double,2> type;
    };

    inline uint32_t
    bytesPerPixel(::ome::xml::model::enums::PixelType pixeltype)
    {
      uint32_t size = 0;
      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          size = Pixel< ::ome::xml::model::enums::PixelType::INT8>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          size = Pixel< ::ome::xml::model::enums::PixelType::INT16>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          size = Pixel< ::ome::xml::model::enums::PixelType::INT32>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          size = Pixel< ::ome::xml::model::enums::PixelType::UINT8>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          size = Pixel< ::ome::xml::model::enums::PixelType::UINT16>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          size = Pixel< ::ome::xml::model::enums::PixelType::UINT32>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          size = Pixel< ::ome::xml::model::enums::PixelType::FLOAT>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          size = Pixel< ::ome::xml::model::enums::PixelType::DOUBLE>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          size = Pixel< ::ome::xml::model::enums::PixelType::BIT>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          size = Pixel< ::ome::xml::model::enums::PixelType::COMPLEX>::pixel_byte_size();
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          size = Pixel< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::pixel_byte_size();
          break;
        default:
          break;
        }
      return size;
    }

    inline uint32_t
    bitsPerPixel(::ome::xml::model::enums::PixelType pixeltype)
    {
      uint32_t size = 0;
      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          size = Pixel< ::ome::xml::model::enums::PixelType::INT8>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          size = Pixel< ::ome::xml::model::enums::PixelType::INT16>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          size = Pixel< ::ome::xml::model::enums::PixelType::INT32>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          size = Pixel< ::ome::xml::model::enums::PixelType::UINT8>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          size = Pixel< ::ome::xml::model::enums::PixelType::UINT16>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          size = Pixel< ::ome::xml::model::enums::PixelType::UINT32>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          size = Pixel< ::ome::xml::model::enums::PixelType::FLOAT>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          size = Pixel< ::ome::xml::model::enums::PixelType::DOUBLE>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          size = Pixel< ::ome::xml::model::enums::PixelType::BIT>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          size = Pixel< ::ome::xml::model::enums::PixelType::COMPLEX>::pixel_bit_size();
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          size = Pixel< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::pixel_bit_size();
          break;
        default:
          break;
        }
      return size;
    }

  }
}

#endif // OME_BIOFORMATS_PIXEL_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

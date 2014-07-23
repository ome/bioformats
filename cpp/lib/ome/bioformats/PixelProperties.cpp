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

#include <ome/bioformats/PixelProperties.h>

#include <stdexcept>

namespace ome
{
  namespace bioformats
  {

    pixel_size_type
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

    pixel_size_type
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

    bool
    isSigned(::ome::xml::model::enums::PixelType pixeltype)
    {
      bool is_signed = false;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::INT8>::is_signed;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::INT16>::is_signed;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::INT32>::is_signed;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::UINT8>::is_signed;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::UINT16>::is_signed;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::UINT32>::is_signed;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::is_signed;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::is_signed;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::BIT>::is_signed;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::is_signed;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          is_signed = PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::is_signed;
          break;
        }

      return is_signed;
    }

    bool
    isFloating(::ome::xml::model::enums::PixelType pixeltype)
    {
      bool is_floating = false;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::INT8>::is_floating;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::INT16>::is_floating;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::INT32>::is_floating;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::UINT8>::is_floating;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::UINT16>::is_floating;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::UINT32>::is_floating;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::is_floating;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::is_floating;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::BIT>::is_floating;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::is_floating;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          is_floating = PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::is_floating;
          break;
        }

      return is_floating;
    }

    bool
    isComplex(::ome::xml::model::enums::PixelType pixeltype)
    {
      bool is_complex = false;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::INT8>::is_complex;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::INT16>::is_complex;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::INT32>::is_complex;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::UINT8>::is_complex;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::UINT16>::is_complex;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::UINT32>::is_complex;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::is_complex;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::is_complex;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::BIT>::is_complex;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::is_complex;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          is_complex = PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::is_complex;
          break;
        }

      return is_complex;
    }

    ::ome::xml::model::enums::PixelType
    pixelTypeFromBytes(pixel_size_type size,
                       bool            is_signed,
                       bool            is_floating,
                       bool            is_complex)
    {
      ::ome::xml::model::enums::PixelType type(::ome::xml::model::enums::PixelType::UINT8);

      if (!is_signed) // integer only
        {
          if (is_floating || is_complex)
            throw std::logic_error("Unsigned pixel types can't be floating point or complex");

          if (size == bytesPerPixel(::ome::xml::model::enums::PixelType::UINT8))
            type = ::ome::xml::model::enums::PixelType::UINT8;
          if (size == bytesPerPixel(::ome::xml::model::enums::PixelType::UINT16))
            type = ::ome::xml::model::enums::PixelType::UINT16;
          if (size == bytesPerPixel(::ome::xml::model::enums::PixelType::UINT32))
            type = ::ome::xml::model::enums::PixelType::UINT32;
          else
            throw std::runtime_error("No suitable unsigned integer pixel type found");
        }
      else // is_signed
        {
          if (is_complex)
            {
              if (!is_floating)
                throw std::logic_error("Complex pixel types must be floating point");

              if (size == bytesPerPixel(::ome::xml::model::enums::PixelType::COMPLEX))
                type = ::ome::xml::model::enums::PixelType::COMPLEX;
              if (size == bytesPerPixel(::ome::xml::model::enums::PixelType::DOUBLECOMPLEX))
                type = ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX;
              else
                throw std::runtime_error("No suitable complex pixel type found");
            }
          else if (is_floating)
            {
              if (size == bytesPerPixel(::ome::xml::model::enums::PixelType::FLOAT))
                type = ::ome::xml::model::enums::PixelType::FLOAT;
              if (size == bytesPerPixel(::ome::xml::model::enums::PixelType::DOUBLE))
                type = ::ome::xml::model::enums::PixelType::DOUBLE;
              else
                throw std::runtime_error("No suitable floating point pixel type found");
            }
          else // integer
            {
              if (size == bytesPerPixel(::ome::xml::model::enums::PixelType::INT8))
                type = ::ome::xml::model::enums::PixelType::INT8;
              if (size == bytesPerPixel(::ome::xml::model::enums::PixelType::INT16))
                type = ::ome::xml::model::enums::PixelType::INT16;
              if (size == bytesPerPixel(::ome::xml::model::enums::PixelType::INT32))
                type = ::ome::xml::model::enums::PixelType::INT32;
              else
                throw std::runtime_error("No suitable signed integer pixel type found");
            }
        }

      return type;
    }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

    ::ome::xml::model::enums::PixelType
    pixelTypeFromBits(pixel_size_type size,
                      bool            is_signed,
                      bool            is_floating,
                      bool            is_complex)
    {
      return pixelTypeFromBytes(size * 8, is_signed, is_floating, is_complex);
    }

  }
}

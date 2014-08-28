/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
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

#ifndef TEST_PIXEL_H
#define TEST_PIXEL_H

#include <ome/bioformats/PixelProperties.h>

#include <ome/compat/cstdint.h>

/// Helpers to create pixel values of all supported types from integers.

template<typename P>
P
pixel_value(uint32_t value)
{
  return static_cast<P>(value);
}

template<typename C>
C
pixel_value_complex(uint32_t value)
{
  return C(typename C::value_type(value),
           typename C::value_type(0.0f));
}

template<>
::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                          ::ome::bioformats::ENDIAN_BIG>::type
pixel_value< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                                       ::ome::bioformats::ENDIAN_BIG>::type>(uint32_t value)
{
  return pixel_value_complex< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                                                        ::ome::bioformats::ENDIAN_BIG>::type>(value);
}

template<>
::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                          ::ome::bioformats::ENDIAN_LITTLE>::type
pixel_value< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                                       ::ome::bioformats::ENDIAN_LITTLE>::type>(uint32_t value)
{
  return pixel_value_complex< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                                                        ::ome::bioformats::ENDIAN_LITTLE>::type>(value);
}

template<>
::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                          ::ome::bioformats::ENDIAN_BIG>::type
pixel_value< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                                       ::ome::bioformats::ENDIAN_BIG>::type>(uint32_t value)
{
  return pixel_value_complex< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                                                        ::ome::bioformats::ENDIAN_BIG>::type>(value);
}

template<>
::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                          ::ome::bioformats::ENDIAN_LITTLE>::type
pixel_value< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                                       ::ome::bioformats::ENDIAN_LITTLE>::type>(uint32_t value)
{
  return pixel_value_complex< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                                                        ::ome::bioformats::ENDIAN_LITTLE>::type>(value);
}

namespace std
{
  template<class charT, class traits>
  inline std::basic_ostream<charT,traits>&
  operator<< (std::basic_ostream<charT,traits>& os,
              const ::ome::bioformats::PixelBufferBase::storage_order_type& order)
  {
    os << '(';
    for (uint16_t i = 0; i < ::ome::bioformats::PixelBufferBase::dimensions; ++i)
    {
      os << order.ordering(i) << '/' << order.ascending(i);
      if (i + 1 != ::ome::bioformats::PixelBufferBase::dimensions)
        os << ',';
    }
    os << ')';
    return os;
  }
}

#endif // TEST_PIXEL_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
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

#ifndef TEST_PIXEL_H
#define TEST_PIXEL_H

#include <ome/bioformats/PixelBuffer.h>
#include <ome/bioformats/PixelProperties.h>
#include <ome/bioformats/VariantPixelBuffer.h>

#include <ome/compat/cstdint.h>

/// Helpers to create pixel values of all supported types from integers.

template<typename P>
inline
P
pixel_value(uint32_t value)
{
  return static_cast<P>(value);
}

template<typename C>
inline
C
pixel_value_complex(uint32_t value)
{
  return C(typename C::value_type(value),
           typename C::value_type(0.0f));
}

template<>
inline
::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                          ::ome::bioformats::ENDIAN_BIG>::type
pixel_value< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                                       ::ome::bioformats::ENDIAN_BIG>::type>(uint32_t value)
{
  return pixel_value_complex< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                                                        ::ome::bioformats::ENDIAN_BIG>::type>(value);
}

template<>
inline
::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                          ::ome::bioformats::ENDIAN_LITTLE>::type
pixel_value< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                                       ::ome::bioformats::ENDIAN_LITTLE>::type>(uint32_t value)
{
  return pixel_value_complex< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,
                                                                        ::ome::bioformats::ENDIAN_LITTLE>::type>(value);
}

template<>
inline
::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                          ::ome::bioformats::ENDIAN_BIG>::type
pixel_value< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                                       ::ome::bioformats::ENDIAN_BIG>::type>(uint32_t value)
{
  return pixel_value_complex< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                                                        ::ome::bioformats::ENDIAN_BIG>::type>(value);
}

template<>
inline
::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                          ::ome::bioformats::ENDIAN_LITTLE>::type
pixel_value< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                                       ::ome::bioformats::ENDIAN_LITTLE>::type>(uint32_t value)
{
  return pixel_value_complex< ::ome::bioformats::PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX,
                                                                        ::ome::bioformats::ENDIAN_LITTLE>::type>(value);
}

/*
 * Assign buffer with values from a buffer of a different pixel type
 * and check.  This is for testing loading and saving of all pixel
 * types.
 */
template<int P>
struct PixelTypeConversionVisitor : public boost::static_visitor<>
{
  typedef typename ::ome::bioformats::PixelProperties<P>::std_type src_type;
  typedef ::ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::BIT>::std_type bit_type;

  const ome::compat::shared_ptr< ::ome::bioformats::PixelBuffer<src_type> > *src;
  ::ome::bioformats::VariantPixelBuffer& dest;

  PixelTypeConversionVisitor(const ::ome::bioformats::VariantPixelBuffer& src,
                             ::ome::bioformats::VariantPixelBuffer& dest):
    src(boost::get<ome::compat::shared_ptr< ::ome::bioformats::PixelBuffer<src_type> > >(&src.vbuffer())),
    dest(dest)
  {

    if (!(this->src && *this->src))
      throw std::runtime_error("Null source buffer or incorrect pixel type");

    if((*this->src)->num_elements() != dest.num_elements())
      throw std::runtime_error("Array size mismatch");
  }

  // Expand pixel values to fill the positive pixel value range for
  // the destination pixel type.
  template <typename T>
  typename boost::enable_if_c<
    boost::is_integral<T>::value, void
    >::type
  operator() (ome::compat::shared_ptr< ::ome::bioformats::PixelBuffer<T> >& lhs)
  {
    const src_type *src_buf = (*src)->data();
    T *dest_buf = lhs->data();

    float oldmin = static_cast<float>(std::numeric_limits<src_type>::min());
    float oldmax = static_cast<float>(std::numeric_limits<src_type>::max());
    float newmin = static_cast<float>(std::numeric_limits<T>::min());
    float newmax = static_cast<float>(std::numeric_limits<T>::max());

    for (::ome::bioformats::VariantPixelBuffer::size_type i = 0;
         i != (*src)->num_elements();
         ++i)
      {

        dest_buf[i] = static_cast<T>((static_cast<float>(src_buf[i] - oldmin) *
                                      ((newmax - newmin) / (oldmax - oldmin))) + newmin);
      }
  }

  // Normalise pixel values to fill the pixel value range 0..1 for the
  // destination pixel type.
  template <typename T>
  typename boost::enable_if_c<
    boost::is_floating_point<T>::value, void
    >::type
  operator() (ome::compat::shared_ptr< ::ome::bioformats::PixelBuffer<T> >& lhs)
  {
    const src_type *src_buf = (*src)->data();
    T *dest_buf = lhs->data();

    float oldmin = static_cast<float>(std::numeric_limits<src_type>::min());
    float oldmax = static_cast<float>(std::numeric_limits<src_type>::max());
    float newmin = 0.0f;
    float newmax = 1.0f;

    for (::ome::bioformats::VariantPixelBuffer::size_type i = 0;
         i != (*src)->num_elements();
         ++i)
      {
        dest_buf[i] = static_cast<T>((static_cast<float>(src_buf[i] - oldmin) *
                                      ((newmax - newmin) / (oldmax - oldmin))) + newmin);
      }
  }

  // Normalise pixel values to fill the pixel value range 0..1+0.0i for the
  // destination complex pixel type.
  template <typename T>
  typename boost::enable_if_c<
    boost::is_complex<T>::value, void
    >::type
  operator() (ome::compat::shared_ptr< ::ome::bioformats::PixelBuffer<T> >& lhs)
  {
    const src_type *src_buf = (*src)->data();
    T *dest_buf = lhs->data();

    float oldmin = static_cast<float>(std::numeric_limits<src_type>::min());
    float oldmax = static_cast<float>(std::numeric_limits<src_type>::max());
    float newmin = 0.0f;
    float newmax = 1.0f;

    for (::ome::bioformats::VariantPixelBuffer::size_type i = 0;
         i != (*src)->num_elements();
         ++i)
      {
        dest_buf[i] = T((static_cast<typename T::value_type>((src_buf[i] - oldmin) *
                                                             ((newmax - newmin) / (oldmax - oldmin))) + newmin),
                        0.0f);
      }
  }

  // Split the pixel range into two, the lower part being set to false
  // and the upper part being set to true for the destination boolean
  // pixel type.
  void
  operator() (ome::compat::shared_ptr< ::ome::bioformats::PixelBuffer<bit_type> >& lhs)
  {
    const src_type *src_buf = (*src)->data();
    bit_type *dest_buf = lhs->data();

    for (::ome::bioformats::VariantPixelBuffer::size_type i = 0;
         i != (*src)->num_elements();
         ++i)
      {
        dest_buf[i] = (static_cast<float>(src_buf[i] - std::numeric_limits<src_type>::min()) /
                       static_cast<float>(std::numeric_limits<src_type>::max())) < 0.3 ? false : true;
      }
  }
};

struct PixelSubrangeVisitor : public boost::static_visitor<>
{
  ::ome::bioformats::dimension_size_type x;
  ::ome::bioformats::dimension_size_type y;

  PixelSubrangeVisitor(::ome::bioformats::dimension_size_type x,
                       ::ome::bioformats::dimension_size_type y):
    x(x),
    y(y)
  {}

  template<typename T, typename U>
  void
  operator() (const T& /*src*/,
              U& /* dest */) const
  {}

  template<typename T>
  void
  operator() (const T& src,
              T& dest) const
  {
    const ::ome::bioformats::VariantPixelBuffer::size_type *shape = dest->shape();

    ::ome::bioformats::dimension_size_type width = shape[::ome::bioformats::DIM_SPATIAL_X];
    ::ome::bioformats::dimension_size_type height = shape[::ome::bioformats::DIM_SPATIAL_Y];
    ::ome::bioformats::dimension_size_type subchannels = shape[::ome::bioformats::DIM_SUBCHANNEL];

    for (::ome::bioformats::dimension_size_type dx = 0; dx < width; ++dx)
      for (::ome::bioformats::dimension_size_type dy = 0; dy < height; ++dy)
        for (::ome::bioformats::dimension_size_type ds = 0; ds < subchannels; ++ds)
          {
            ::ome::bioformats::VariantPixelBuffer::indices_type srcidx;
            srcidx[::ome::bioformats::DIM_SPATIAL_X] = x + dx;
            srcidx[::ome::bioformats::DIM_SPATIAL_Y] = y + dy;
            srcidx[::ome::bioformats::DIM_SUBCHANNEL] = ds;
            srcidx[2] = srcidx[3] = srcidx[4] = srcidx[6] = srcidx[7] = srcidx[8] = 0;

            ::ome::bioformats::VariantPixelBuffer::indices_type destidx;
            destidx[::ome::bioformats::DIM_SPATIAL_X] = dx;
            destidx[::ome::bioformats::DIM_SPATIAL_Y] = dy;
            destidx[::ome::bioformats::DIM_SUBCHANNEL] = ds;
            destidx[2] = destidx[3] = destidx[4] = destidx[6] = destidx[7] = destidx[8] = 0;

            dest->at(destidx) = src->at(srcidx);
          }
  }
};

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

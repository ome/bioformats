/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2015 Open Microscopy Environment:
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
#include <cstdlib>
#include <iostream>

#include <ome/bioformats/PixelBuffer.h>
#include <ome/bioformats/PixelProperties.h>
#include <ome/bioformats/VariantPixelBuffer.h>

#include <ome/xml/model/enums/DimensionOrder.h>
#include <ome/xml/model/enums/PixelType.h>

using ome::bioformats::PixelBuffer;
using ome::bioformats::PixelBufferBase;
using ome::bioformats::PixelProperties;
using ome::bioformats::VariantPixelBuffer;
using ome::xml::model::enums::DimensionOrder;
using ome::xml::model::enums::PixelType;

namespace
{

  void
  createPixelBuffer()
  {
    /* create-example-start */
    // Language type for FLOAT pixel data
    typedef PixelProperties<PixelType::FLOAT>::std_type float_pixel_type;
    // Create PixelBuffer for floating point data
    // X=512 Y=512 Z=16 T=1 C=3 S/z/t/c=1
    PixelBuffer<float_pixel_type> buffer
      (boost::extents[512][512][16][1][3][1][1][1][1], PixelType::FLOAT);
    /* create-example-end */

    /* at-example-start */
    // Set all pixel values for Z=2 and C=1 to 0.5
    // 9D index, default values to zero if unused
    PixelBuffer<float_pixel_type>::indices_type idx;
    // Set Z and C indices
    idx[ome::bioformats::DIM_SPATIAL_Z] = 2;
    idx[ome::bioformats::DIM_CHANNEL] = 1;
    idx[ome::bioformats::DIM_TEMPORAL_T] =
      idx[ome::bioformats::DIM_SUBCHANNEL] =
      idx[ome::bioformats::DIM_MODULO_Z] =
      idx[ome::bioformats::DIM_MODULO_T] =
      idx[ome::bioformats::DIM_MODULO_C] = 0;

    for (uint16_t x = 0; x < 512; ++x)
      {
        idx[ome::bioformats::DIM_SPATIAL_X] = x;
        for (uint16_t y = 0; y < 512; ++y)
          {
            idx[ome::bioformats::DIM_SPATIAL_Y] = y;
            buffer.at(idx) = 0.5f;
          }
      }
    /* at-example-end */
  }

  void
  createPixelBufferOrdered()
  {
    /* create-ordered-example-start */
    // Language type for UINT16 pixel data
    typedef PixelProperties<PixelType::UINT16>::std_type uint16_pixel_type;
    // Storage order is XYSCTZztc; subchannels are not interleaved
    // ("planar") after XY; lowercase letters are unused Modulo
    // dimensions
    PixelBufferBase::storage_order_type order1
      (PixelBufferBase::make_storage_order(DimensionOrder::XYCTZ, false));
    // Create PixelBuffer for unsigned 16-bit data with specified
    // storage order
    // X=512 Y=512 Z=16 T=1 C=3 S/z/t/c=1
    PixelBuffer<uint16_pixel_type> buffer1
      (boost::extents[512][512][16][1][3][1][1][1][1],
       PixelType::UINT16,
       ome::bioformats::ENDIAN_NATIVE,
       order1);

    // Language type for INT8 pixel data
    typedef PixelProperties<PixelType::INT8>::std_type int8_pixel_type;
    // Storage order is SXYZCTztc; subchannels are interleaved
    // ("chunky") before XY; lowercase letters are unused Modulo
    // dimensions
    PixelBufferBase::storage_order_type order2
      (PixelBufferBase::make_storage_order(DimensionOrder::XYZCT, true));
    // Create PixelBuffer for signed 8-bit RGB data with specified storage
    // order
    // X=1024 Y=1024 Z=1 T=1 C=1 S=3 z/t/c=1
    PixelBuffer<int8_pixel_type> buffer2
      (boost::extents[1024][1024][1][1][1][3][1][1][1],
       PixelType::INT8,
       ome::bioformats::ENDIAN_NATIVE,
       order2);
    /* create-ordered-example-end */
  }

  /* visitor-example-start */
  // Visitor to compute min and max pixel value for pixel buffer of
  // any pixel type
  // The static_visitor specialization is the required return type of
  // the operator() methods and boost::apply_visitor()
  struct MinMaxVisitor : public boost::static_visitor<std::pair<double, double> >
  {
    // The min and max values will be returned in a pair.  double is
    // used since it can contain the value for any pixel type
    typedef std::pair<double, double> result_type;

    // Get min and max for any non-complex pixel type
    template<typename T>
    result_type
    operator() (const T& v)
    {
      typedef typename T::element_type::value_type value_type;

      value_type *min = std::min_element(v->data(),
                                         v->data() + v->num_elements());
      value_type *max = std::max_element(v->data(),
                                         v->data() + v->num_elements());

      return result_type(static_cast<double>(*min),
                         static_cast<double>(*max));
    }

    // Less than comparison for real part of complex numbers
    template <typename T>
    static bool
    complex_real_less(const T& lhs, const T& rhs)
    {
      return std::real(lhs) < std::real(rhs);
    }

    // Greater than comparison for real part of complex numbers
    template <typename T>
    static bool
    complex_real_greater(const T& lhs, const T& rhs)
    {
      return std::real(lhs) > std::real(rhs);
    }

    // Get min and max for complex pixel types (COMPLEX and
    // DOUBLECOMPLEX)
    // This is the same as for simple pixel types, except for the
    // addition of custom comparison functions and conversion of the
    // result to the real part.
    template <typename T>
    typename boost::enable_if_c<
      boost::is_complex<T>::value, result_type
      >::type
    operator() (const ome::compat::shared_ptr<PixelBuffer<T> >& v)
    {
      typedef T value_type;

      value_type *min = std::min_element(v->data(),
                                         v->data() + v->num_elements(),
                                         complex_real_less<T>);
      value_type *max = std::max_element(v->data(),
                                         v->data() + v->num_elements(),
                                         complex_real_greater<T>);

      return result_type(static_cast<double>(std::real(*min)),
                         static_cast<double>(std::real(*max)));
    }
  };

  void
  applyVariant()
  {
    // Make variant buffer (int32, 16×16 single plane)
    VariantPixelBuffer variant(boost::extents[16][16][1][1][1][1][1][1][1],
                               PixelType::INT32);
    // Get buffer size
    VariantPixelBuffer::size_type size = variant.num_elements();
    // Create sample random-ish data
    std::vector<int32_t> vec;
    for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
      {
        int32_t val = static_cast<int32_t>(i + 42);
        vec.push_back(val);
      }
    std::random_shuffle(vec.begin(), vec.end());
    // Assign sample data to buffer.
    variant.assign(vec.begin(), vec.end());

    // Create and apply visitor
    MinMaxVisitor visitor;
    MinMaxVisitor::result_type result = boost::apply_visitor(visitor, variant.vbuffer());

    std::cout << "Min is " << result.first
              << ", max is " << result.second << '\n';
  }
  /* visitor-example-end */

}

int
main()
{
  try
    {
      createPixelBuffer();
      createPixelBufferOrdered();
      applyVariant();
    }
  catch (const std::exception& e)
    {
      std::cerr << "Caught exception: " << e.what() << '\n';
      std::exit(1);
    }
  catch (...)
    {
      std::cerr << "Caught unknown exception\n";
      std::exit(1);
    }
}

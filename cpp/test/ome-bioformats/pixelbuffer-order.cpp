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

#include <sstream>
#include <stdexcept>
#include <iostream>

#include <ome/bioformats/PixelBuffer.h>

#include <ome/test/test.h>

#include "pixel.h"

using ome::bioformats::Dimensions;
using ome::bioformats::PixelEndianProperties;
using ome::bioformats::PixelBufferBase;
using ome::bioformats::PixelBuffer;
typedef ome::xml::model::enums::DimensionOrder DO;
typedef ome::xml::model::enums::PixelType PT;

/*
 * NOTE: Update equivalent tests in variantpixelbuffer.cpp when making
 * changes.
 */

class DimensionOrderTestParameters
{
public:
  DO                                  order;
  bool                                interleaved;
  bool                                is_default;
  PixelBufferBase::storage_order_type expected_order;

  DimensionOrderTestParameters(DO                                  order,
                               bool                                interleaved,
                               bool                                is_default,
                               PixelBufferBase::storage_order_type expected_order):
    order(order),
    interleaved(interleaved),
    is_default(is_default),
    expected_order(expected_order)
  {}
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const DimensionOrderTestParameters& params)
{
  return os << DO(params.order) << (params.interleaved ? "/chunky" : "/planar");
}

class DimensionOrderTest : public ::testing::TestWithParam<DimensionOrderTestParameters>
{
};

TEST_P(DimensionOrderTest, OrderCorrect)
{
  const DimensionOrderTestParameters& params = GetParam();

  ASSERT_EQ(params.expected_order, PixelBufferBase::make_storage_order(params.order, params.interleaved));
}

TEST_P(DimensionOrderTest, Default)
{
  const DimensionOrderTestParameters& params = GetParam();

  if (params.is_default)
    ASSERT_EQ(PixelBufferBase::default_storage_order(), PixelBufferBase::make_storage_order(params.order, params.interleaved));
  else
    ASSERT_FALSE(PixelBufferBase::default_storage_order() == PixelBufferBase::make_storage_order(params.order, params.interleaved));
}

namespace
{
  PixelBufferBase::storage_order_type
  make_order(ome::bioformats::Dimensions d0,
             ome::bioformats::Dimensions d1,
             ome::bioformats::Dimensions d2,
             ome::bioformats::Dimensions d3,
             ome::bioformats::Dimensions d4,
             ome::bioformats::Dimensions d5,
             ome::bioformats::Dimensions d6,
             ome::bioformats::Dimensions d7,
             ome::bioformats::Dimensions d8)
  {
    PixelBufferBase::size_type ordering[PixelBufferBase::dimensions] = {d0, d1, d2, d3, d4, d5, d6, d7, d8};
    bool ascending[PixelBufferBase::dimensions] = {true, true, true, true, true, true, true, true, true};
    return PixelBufferBase::storage_order_type(ordering, ascending);
  }
}

DimensionOrderTestParameters dimension_params[] =
  { //                           DimensionOrder interleaved default
    //                           expected-order
    DimensionOrderTestParameters(DO::XYZTC, true, true,
                                 make_order(ome::bioformats::DIM_SUBCHANNEL, ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL)),
    DimensionOrderTestParameters(DO::XYZTC, false, false,
                                 make_order(ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y, ome::bioformats::DIM_SUBCHANNEL,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL)),

    DimensionOrderTestParameters(DO::XYZCT, true, false,
                                 make_order(ome::bioformats::DIM_SUBCHANNEL, ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T)),
    DimensionOrderTestParameters(DO::XYZCT, false, false,
                                 make_order(ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y, ome::bioformats::DIM_SUBCHANNEL,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T)),

    DimensionOrderTestParameters(DO::XYTZC, true, false,
                                 make_order(ome::bioformats::DIM_SUBCHANNEL, ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL)),
    DimensionOrderTestParameters(DO::XYTZC, false, false,
                                 make_order(ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y, ome::bioformats::DIM_SUBCHANNEL,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL)),

    DimensionOrderTestParameters(DO::XYTCZ, true, false,
                                 make_order(ome::bioformats::DIM_SUBCHANNEL, ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z)),
    DimensionOrderTestParameters(DO::XYTCZ, false, false,
                                 make_order(ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y, ome::bioformats::DIM_SUBCHANNEL,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z)),

    DimensionOrderTestParameters(DO::XYCZT, true, false,
                                 make_order(ome::bioformats::DIM_SUBCHANNEL, ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T)),
    DimensionOrderTestParameters(DO::XYCZT, false, false,
                                 make_order(ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y, ome::bioformats::DIM_SUBCHANNEL,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T)),

    DimensionOrderTestParameters(DO::XYCTZ, true, false,
                                 make_order(ome::bioformats::DIM_SUBCHANNEL, ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z)),
    DimensionOrderTestParameters(DO::XYCTZ, false, false,
                                 make_order(ome::bioformats::DIM_SPATIAL_X, ome::bioformats::DIM_SPATIAL_Y, ome::bioformats::DIM_SUBCHANNEL,
                                            ome::bioformats::DIM_MODULO_C, ome::bioformats::DIM_CHANNEL,
                                            ome::bioformats::DIM_MODULO_T, ome::bioformats::DIM_TEMPORAL_T,
                                            ome::bioformats::DIM_MODULO_Z, ome::bioformats::DIM_SPATIAL_Z))
  };

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(DimensionOrderVariants, DimensionOrderTest, ::testing::ValuesIn(dimension_params));

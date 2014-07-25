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

#include <ome/bioformats/Version.h>

#include <ome/internal/version.h>

#include <sstream>
#include <stdexcept>
#include <iostream>

#include <ome/bioformats/PixelBuffer.h>

#include <gtest/gtest.h>
#include <gtest/gtest-death-test.h>

using ome::bioformats::Dimensions;
using ome::bioformats::PixelBufferBase;
using ome::bioformats::PixelBuffer;
using ome::bioformats::VariantPixelBuffer;
typedef ome::xml::model::enums::DimensionOrder DO;

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

DimensionOrderTestParameters property_params[] =
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


TEST(VariantPixelBuffer, ConstructSize)
{
  VariantPixelBuffer buf(boost::extents[5][2][1][1][1][1][1][1][1]);

  ASSERT_EQ(buf.num_elements(), 10U);
  ASSERT_TRUE(buf.data());
}

TEST(VariantPixelBuffer, ConstructEmpty)
{
  /// @todo Construct with null reference.
  VariantPixelBuffer buf;

  ASSERT_EQ(buf.num_elements(), 1U);
  ASSERT_TRUE(buf.data());
}

TEST(VariantPixelBuffer, ConstructRange)
{
  std::vector<boost::endian::native_uint8_t> source;
  for (uint8_t i = 0U; i < 10U; ++i)
    source.push_back(i);

  VariantPixelBuffer buf(boost::extents[5][2][1][1][1][1][1][1][1]);
  buf.assign(source.begin(), source.end());

  ASSERT_EQ(buf.num_elements(), 10U);
  ASSERT_TRUE(buf.data());
  for (boost::endian::native_uint8_t i = 0U; i < 10U; ++i)
    {
      ASSERT_EQ(*(buf.data()+i), i);
    }
}

TEST(VariantPixelBuffer, ConstructCopy)
{
  std::vector<boost::endian::native_uint8_t> source1;
  for (boost::endian::native_uint8_t i = 0U; i < 10U; ++i)
    source1.push_back(i);

  std::vector<boost::endian::native_uint8_t> source2;
  for (boost::endian::native_uint8_t i = 10U; i < 20U; ++i)
    source2.push_back(i);

  VariantPixelBuffer buf1(boost::extents[5][2][1][1][1][1][1][1][1]);
  buf1.assign(source1.begin(), source1.end());
  VariantPixelBuffer buf2(boost::extents[5][2][1][1][1][1][1][1][1]);
  buf2.assign(source2.begin(), source2.end());

  ASSERT_EQ(buf1, buf1);
  ASSERT_EQ(buf2, buf2);
  ASSERT_NE(buf1, buf2);

  VariantPixelBuffer buf3(buf2);
  ASSERT_EQ(buf2, buf3);
  ASSERT_NE(buf1, buf2);
}

TEST(VariantPixelBuffer, GetIndex)
{
  std::vector<boost::endian::native_uint8_t> source;
  for (boost::endian::native_uint8_t i = 0U; i < 100U; ++i)
    source.push_back(i);

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1]);
  buf.assign(source.begin(), source.end());
  const VariantPixelBuffer& cbuf(buf);

  ASSERT_EQ(buf.num_elements(), 100U);
  ASSERT_TRUE(buf.data());
  for (boost::endian::native_uint8_t i = 0U; i < 10U; ++i)
    for (boost::endian::native_uint8_t j = 0U; j < 10U; ++j)
      {
        VariantPixelBuffer::indices_type idx;
        idx[0] = i;
        idx[1] = j;
        idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;
        EXPECT_EQ(buf.at<boost::endian::native_uint8_t>(idx), (j * 10) + i);
        EXPECT_EQ(cbuf.at<boost::endian::native_uint8_t>(idx), (j * 10) + i);
      }
}

TEST(VariantPixelBuffer, SetIndex)
{
  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1]);
  const VariantPixelBuffer& cbuf(buf);

  ASSERT_EQ(buf.num_elements(), 100U);
  ASSERT_TRUE(buf.data());
  for (boost::endian::native_uint8_t i = 0U; i < 10U; ++i)
    for (boost::endian::native_uint8_t j = 0U; j < 10U; ++j)
      {
        VariantPixelBuffer::indices_type idx;
        idx[0] = i;
        idx[1] = j;
        idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;

        boost::endian::native_uint8_t val = i + j + j;

        buf.at<boost::endian::native_uint8_t>(idx) = val;

        ASSERT_EQ(buf.at<boost::endian::native_uint8_t>(idx), val);
        ASSERT_EQ(cbuf.at<boost::endian::native_uint8_t>(idx), val);
    }
}

TEST(VariantPixelBuffer, SetIndexDeathTest)
{
  ::testing::FLAGS_gtest_death_test_style = "threadsafe";

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1]);
  const VariantPixelBuffer& cbuf(buf);

  VariantPixelBuffer::indices_type badidx;
  badidx[0] = 13;
  badidx[1] = 2;
  badidx[2] = badidx[3] = badidx[4] = badidx[5] = badidx[6] = badidx[7] = badidx[8] = 0;

  ASSERT_DEATH(buf.at<boost::endian::native_uint8_t>(badidx) = 4U, "Assertion.*failed");
  ASSERT_DEATH(cbuf.at<boost::endian::native_uint8_t>(badidx), "Assertion.*failed");
}

TEST(VariantPixelBuffer, StreamInput)
{
  VariantPixelBuffer buf(boost::extents[2][2][3][4][1][1][1][1][1]);
  VariantPixelBuffer::size_type size = buf.num_elements();
  std::stringstream ss;

  for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
    {
      uint8_t val = i;
      ss.write(reinterpret_cast<const char *>(&val), sizeof(uint8_t));
    }

  ss.seekg(0, std::ios::beg);
  ss >> buf;
  EXPECT_TRUE(ss);

  VariantPixelBuffer::indices_type idx;
  idx[0] = idx[1] = idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;
  std::vector<int>::size_type i = 0;
  for (idx[3] = 0; idx[3] < 4; ++idx[3])
    for (idx[2] = 0; idx[2] < 3; ++idx[2])
      for (idx[1] = 0; idx[1] < 2; ++idx[1])
        for (idx[0] = 0; idx[0] < 2; ++idx[0])
          EXPECT_EQ(i++, buf.at<boost::endian::native_uint8_t>(idx));
}

TEST(VariantPixelBuffer, StreamOutput)
{
  VariantPixelBuffer buf(boost::extents[2][2][3][4][1][1][1][1][1]);
  VariantPixelBuffer::size_type size = buf.num_elements();
  std::stringstream ss;

  std::vector<boost::endian::native_uint8_t> v;
  for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
    {
      uint8_t val = i;
      v.push_back(val);
    }

  buf.assign(v.begin(), v.end());
  ss << buf;
  EXPECT_TRUE(ss);
  ss.seekg(0, std::ios::beg);

  VariantPixelBuffer::indices_type idx;
  idx[0] = idx[1] = idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;
  std::vector<int>::size_type i = 0;
  for (idx[3] = 0; idx[3] < 4; ++idx[3])
    for (idx[2] = 0; idx[2] < 3; ++idx[2])
      for (idx[1] = 0; idx[1] < 2; ++idx[1])
        for (idx[0] = 0; idx[0] < 2; ++idx[0])
          {
            EXPECT_EQ(i, buf.at<boost::endian::native_uint8_t>(idx));
            boost::endian::native_uint8_t sval;
            ss.read(reinterpret_cast<char *>(&sval), sizeof(boost::endian::native_uint8_t));
            EXPECT_TRUE(ss);
            EXPECT_EQ(i, sval);
            ++i;
          }
}

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#endif

INSTANTIATE_TEST_CASE_P(DimensionOrderVariants, DimensionOrderTest, ::testing::ValuesIn(property_params));

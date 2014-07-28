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
using ome::bioformats::EndianType;
using ome::bioformats::PixelEndianProperties;
using ome::bioformats::PixelBufferBase;
using ome::bioformats::PixelBuffer;
using ome::bioformats::VariantPixelBuffer;
typedef ome::xml::model::enums::DimensionOrder DO;
typedef ome::xml::model::enums::PixelType PT;

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


template <typename T>
class PixelBufferType : public ::testing::Test
{};

TYPED_TEST_CASE_P(PixelBufferType);

TYPED_TEST_P(PixelBufferType, DefaultConstruct)
{
  ASSERT_NO_THROW(PixelBuffer<TypeParam> buf);
}

TYPED_TEST_P(PixelBufferType, ConstructSize)
{
  ASSERT_NO_THROW(PixelBuffer<TypeParam> buf(boost::extents[5][2][1][1][1][1][1][1][1]));
}

TYPED_TEST_P(PixelBufferType, ConstructRange)
{
  std::vector<TypeParam> source;
  for (int i = 0; i < 10; ++i)
    source.push_back(TypeParam(i));

  PixelBuffer<TypeParam> buf(boost::extents[5][2][1][1][1][1][1][1][1]);
  buf.assign(source.begin(), source.end());

  ASSERT_EQ(buf.num_elements(), 10U);
  ASSERT_TRUE(buf.data());
  for (int i = 0; i < 10; ++i)
    {
      ASSERT_EQ(TypeParam(i), *(buf.data()+i));
    }
}

TYPED_TEST_P(PixelBufferType, ConstructCopy)
{
  std::vector<TypeParam> source1;
  for (int i = 0; i < 10; ++i)
    source1.push_back(TypeParam(i));

  std::vector<TypeParam> source2;
  for (int i = 10U; i < 20U; ++i)
    source2.push_back(TypeParam(i));

  PixelBuffer<TypeParam> buf1(boost::extents[5][2][1][1][1][1][1][1][1]);
  buf1.assign(source1.begin(), source1.end());
  PixelBuffer<TypeParam> buf2(boost::extents[5][2][1][1][1][1][1][1][1]);
  buf2.assign(source2.begin(), source2.end());

  ASSERT_EQ(buf1, buf1);
  ASSERT_EQ(buf2, buf2);
  ASSERT_NE(buf1, buf2);

  PixelBuffer<TypeParam> buf3(buf2);
  ASSERT_EQ(buf2, buf3);
  ASSERT_NE(buf1, buf2);
}

template<typename T>
void test_operators(const PixelBuffer<T>& buf1,
                    const PixelBuffer<T>& buf2)
{
  EXPECT_GT(buf2, buf1);
  EXPECT_GE(buf2, buf1);
  EXPECT_GE(buf1, buf1);
  EXPECT_LT(buf1, buf2);
  EXPECT_LE(buf1, buf2);
  EXPECT_LE(buf2, buf2);
}

template<typename T>
void test_operators(const PixelBuffer<std::complex<T> >& /* buf1 */,
                    const PixelBuffer<std::complex<T> >& /* buf2 */)
{
}

TYPED_TEST_P(PixelBufferType, Operators)
{
  std::vector<TypeParam> source1;
  for (int i = 0; i < 10; ++i)
    source1.push_back(TypeParam(i));

  std::vector<TypeParam> source2;
  for (int i = 100U; i < 120U; ++i)
    source2.push_back(TypeParam(i));

  PixelBuffer<TypeParam> buf1(boost::extents[5][2][1][1][1][1][1][1][1]);
  buf1.assign(source1.begin(), source1.end());
  PixelBuffer<TypeParam> buf2(boost::extents[5][2][1][1][1][1][1][1][1]);
  buf2.assign(source2.begin(), source2.end());

  EXPECT_EQ(buf1, buf1);
  EXPECT_EQ(buf2, buf2);
  EXPECT_NE(buf1, buf2);
  test_operators(buf1, buf2);
}

TYPED_TEST_P(PixelBufferType, GetIndex)
{
  std::vector<TypeParam> source;
  for (int i = 0; i < 100; ++i)
    source.push_back(TypeParam(i));

  PixelBuffer<TypeParam> buf(boost::extents[10][10][1][1][1][1][1][1][1]);
  buf.assign(source.begin(), source.end());
  const PixelBuffer<TypeParam>& cbuf(buf);

  ASSERT_EQ(buf.num_elements(), 100U);
  ASSERT_TRUE(buf.data());
  for (int i = 0; i < 10; ++i)
    for (int j = 0; j < 10; ++j)
      {
        VariantPixelBuffer::indices_type idx;
        idx[0] = i;
        idx[1] = j;
        idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;
        EXPECT_EQ(TypeParam((j * 10) + i), buf.at(idx));
        EXPECT_EQ(TypeParam((j * 10) + i), cbuf.at(idx));
      }
}

TYPED_TEST_P(PixelBufferType, SetIndex)
{
  PixelBuffer<TypeParam> buf(boost::extents[10][10][1][1][1][1][1][1][1]);
  const PixelBuffer<TypeParam>& cbuf(buf);

  ASSERT_EQ(buf.num_elements(), 100U);
  ASSERT_TRUE(buf.data());
  for (int i = 0; i < 10; ++i)
    for (int j = 0; j < 10; ++j)
      {
        VariantPixelBuffer::indices_type idx;
        idx[0] = i;
        idx[1] = j;
        idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;

        TypeParam val(i + j + j);

        buf.at(idx) = val;

        ASSERT_EQ(val, buf.at(idx));
        ASSERT_EQ(val, cbuf.at(idx));
    }
}

TYPED_TEST_P(PixelBufferType, SetIndexDeathTest)
{
  ::testing::FLAGS_gtest_death_test_style = "threadsafe";

  PixelBuffer<TypeParam> buf(boost::extents[10][10][1][1][1][1][1][1][1]);
  const PixelBuffer<TypeParam>& cbuf(buf);

  typename PixelBuffer<TypeParam>::indices_type badidx;
  badidx[0] = 13;
  badidx[1] = 2;
  badidx[2] = badidx[3] = badidx[4] = badidx[5] = badidx[6] = badidx[7] = badidx[8] = 0;

  ASSERT_DEATH(buf.at(badidx) = 4U, "Assertion.*failed");
  ASSERT_DEATH(cbuf.at(badidx), "Assertion.*failed");
}

TYPED_TEST_P(PixelBufferType, StreamInput)
{
  PixelBuffer<TypeParam> buf(boost::extents[2][2][3][4][1][1][1][1][1]);
  typename PixelBuffer<TypeParam>::size_type size = buf.num_elements();
  std::stringstream ss;

  for (typename PixelBuffer<TypeParam>::size_type i = 0; i < size; ++i)
    {
      TypeParam val(i);
      ss.write(reinterpret_cast<const char *>(&val), sizeof(TypeParam));
    }

  ss.seekg(0, std::ios::beg);
  ss >> buf;
  EXPECT_FALSE(!ss);

  typename PixelBuffer<TypeParam>::indices_type idx;
  idx[0] = idx[1] = idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;
  std::vector<int>::size_type i = 0;
  for (idx[3] = 0; idx[3] < 4; ++idx[3])
    for (idx[2] = 0; idx[2] < 3; ++idx[2])
      for (idx[1] = 0; idx[1] < 2; ++idx[1])
        for (idx[0] = 0; idx[0] < 2; ++idx[0])
          EXPECT_EQ(TypeParam(i++), buf.at(idx));
}

TYPED_TEST_P(PixelBufferType, StreamOutput)
{
  PixelBuffer<TypeParam> buf(boost::extents[2][2][3][4][1][1][1][1][1]);
  typename PixelBuffer<TypeParam>::size_type size = buf.num_elements();
  std::stringstream ss;

  std::vector<TypeParam> v;
  for (typename PixelBuffer<TypeParam>::size_type i = 0; i < size; ++i)
    {
      TypeParam val(i);
      v.push_back(val);
    }

  buf.assign(v.begin(), v.end());
  ss << buf;
  EXPECT_FALSE(!ss);
  ss.seekg(0, std::ios::beg);

  typename PixelBuffer<TypeParam>::indices_type idx;
  idx[0] = idx[1] = idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;
  typename std::vector<TypeParam>::size_type i = 0;
  for (idx[3] = 0; idx[3] < 4; ++idx[3])
    for (idx[2] = 0; idx[2] < 3; ++idx[2])
      for (idx[1] = 0; idx[1] < 2; ++idx[1])
        for (idx[0] = 0; idx[0] < 2; ++idx[0])
          {
            EXPECT_EQ(TypeParam(i), buf.at(idx));
            TypeParam sval;
            ss.read(reinterpret_cast<char *>(&sval), sizeof(TypeParam));
            EXPECT_FALSE(!ss);
            EXPECT_EQ(TypeParam(i), sval);
            ++i;
          }
}

REGISTER_TYPED_TEST_CASE_P(PixelBufferType,
                           DefaultConstruct,
                           ConstructSize,
                           ConstructRange,
                           ConstructCopy,
                           Operators,
                           GetIndex,
                           SetIndex,
                           SetIndexDeathTest,
                           StreamInput,
                           StreamOutput);

typedef ::testing::Types<
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT8,          ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT8,          ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT8,          ome::bioformats::NATIVE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT16,         ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT16,         ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT16,         ome::bioformats::NATIVE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT32,         ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT32,         ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::INT32,         ome::bioformats::NATIVE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT8,         ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT8,         ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT8,         ome::bioformats::NATIVE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT16,        ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT16,        ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT16,        ome::bioformats::NATIVE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT32,        ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT32,        ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::UINT32,        ome::bioformats::NATIVE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::FLOAT,         ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::FLOAT,         ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::FLOAT,         ome::bioformats::NATIVE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLE,        ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLE,        ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLE,        ome::bioformats::NATIVE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::BIT,           ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::BIT,           ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::BIT,           ome::bioformats::NATIVE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,       ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,       ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::COMPLEX,       ome::bioformats::NATIVE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX, ome::bioformats::BIG   >::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX, ome::bioformats::LITTLE>::type,
  PixelEndianProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX, ome::bioformats::NATIVE>::type> TestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(PixelBufferTypeTest, PixelBufferType, TestTypes);


class VariantPixelBufferTestParameters
{
public:
  PT         type;
  EndianType endian;

  VariantPixelBufferTestParameters(PT         type,
                                   EndianType endian):
    type(type),
    endian(endian)
  {}
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const VariantPixelBufferTestParameters& params)
{
  return os << PT(params.type) << '/'<< params.endian;
}

class VariantPixelBufferTest : public ::testing::TestWithParam<VariantPixelBufferTestParameters>
{
};

TEST_P(VariantPixelBufferTest, DefaultConstruct)
{
  VariantPixelBuffer buf;

  ASSERT_EQ(buf.num_elements(), 1U);
  ASSERT_TRUE(buf.data());
}

TEST_P(VariantPixelBufferTest, ConstructSize)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[5][2][1][1][1][1][1][1][1],
                         params.type, params.endian);

  ASSERT_EQ(buf.num_elements(), 10U);
  ASSERT_TRUE(buf.data());
}

/**
 * Assign buffer and check.
 */
struct AssignTestVisitor : public boost::static_visitor<>
{
  VariantPixelBuffer& buf;

  AssignTestVisitor(VariantPixelBuffer& buf):
    buf(buf)
  {}

  template<typename T>
  void
  operator() (const T& v)
  {
    typedef typename T::element_type::value_type value_type;

    VariantPixelBuffer::size_type size(buf.num_elements());
    std::vector<value_type> data;
    for (int i = 0; i < size; ++i)
      data.push_back(value_type(i));
    buf.assign(data.begin(), data.end());

    ASSERT_TRUE(buf.data());
    ASSERT_TRUE(buf.data<value_type>());
    ASSERT_TRUE(v->data());
    for (int i = 0; i < size; ++i)
      {
        ASSERT_EQ(*(buf.data<value_type>()+i), value_type(i));
      }
  }
};

TEST_P(VariantPixelBufferTest, ConstructRange)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[5][2][1][1][1][1][1][1][1],
                         params.type, params.endian);
  ASSERT_EQ(buf.num_elements(), 10U);

  AssignTestVisitor v(buf);
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, ConstructCopy)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  std::vector<boost::endian::native_uint8_t> source1;
  for (boost::endian::native_uint8_t i = 0U; i < 10U; ++i)
    source1.push_back(i);

  std::vector<boost::endian::native_uint8_t> source2;
  for (boost::endian::native_uint8_t i = 10U; i < 20U; ++i)
    source2.push_back(i);

  VariantPixelBuffer buf1(boost::extents[5][2][1][1][1][1][1][1][1],
                          params.type, params.endian);
  buf1.assign(source1.begin(), source1.end());
  VariantPixelBuffer buf2(boost::extents[5][2][1][1][1][1][1][1][1],
                          params.type, params.endian);
  buf2.assign(source2.begin(), source2.end());

  ASSERT_EQ(buf1, buf1);
  ASSERT_EQ(buf2, buf2);
  ASSERT_NE(buf1, buf2);

  VariantPixelBuffer buf3(buf2);
  ASSERT_EQ(buf2, buf3);
  ASSERT_NE(buf1, buf2);
}

TEST_P(VariantPixelBufferTest, GetIndex)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  std::vector<boost::endian::native_uint8_t> source;
  for (boost::endian::native_uint8_t i = 0U; i < 100U; ++i)
    source.push_back(i);

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                         params.type, params.endian);
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

TEST_P(VariantPixelBufferTest, SetIndex)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                         params.type, params.endian);
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

TEST_P(VariantPixelBufferTest, SetIndexDeathTest)
{
  ::testing::FLAGS_gtest_death_test_style = "threadsafe";

  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                         params.type, params.endian);
  const VariantPixelBuffer& cbuf(buf);

  VariantPixelBuffer::indices_type badidx;
  badidx[0] = 13;
  badidx[1] = 2;
  badidx[2] = badidx[3] = badidx[4] = badidx[5] = badidx[6] = badidx[7] = badidx[8] = 0;

  ASSERT_DEATH(buf.at<boost::endian::native_uint8_t>(badidx) = 4U, "Assertion.*failed");
  ASSERT_DEATH(cbuf.at<boost::endian::native_uint8_t>(badidx), "Assertion.*failed");
}

TEST_P(VariantPixelBufferTest, StreamInput)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[2][2][3][4][1][1][1][1][1],
                         params.type, params.endian);
  VariantPixelBuffer::size_type size = buf.num_elements();
  std::stringstream ss;

  for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
    {
      uint8_t val = i;
      ss.write(reinterpret_cast<const char *>(&val), sizeof(uint8_t));
    }

  ss.seekg(0, std::ios::beg);
  ss >> buf;
  EXPECT_FALSE(!ss);

  VariantPixelBuffer::indices_type idx;
  idx[0] = idx[1] = idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;
  std::vector<int>::size_type i = 0;
  for (idx[3] = 0; idx[3] < 4; ++idx[3])
    for (idx[2] = 0; idx[2] < 3; ++idx[2])
      for (idx[1] = 0; idx[1] < 2; ++idx[1])
        for (idx[0] = 0; idx[0] < 2; ++idx[0])
          EXPECT_EQ(i++, buf.at<boost::endian::native_uint8_t>(idx));
}

TEST_P(VariantPixelBufferTest, StreamOutput)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[2][2][3][4][1][1][1][1][1],
                         params.type, params.endian);
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
  EXPECT_FALSE(!ss);
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
            EXPECT_FALSE(!ss);
            EXPECT_EQ(i, sval);
            ++i;
          }
}

VariantPixelBufferTestParameters variant_params[] =
  { //                               PixelType          EndianType
    VariantPixelBufferTestParameters(PT::INT8,          ome::bioformats::BIG),
    VariantPixelBufferTestParameters(PT::INT8,          ome::bioformats::LITTLE),
    VariantPixelBufferTestParameters(PT::INT8,          ome::bioformats::NATIVE),

    VariantPixelBufferTestParameters(PT::INT16,         ome::bioformats::BIG),
    VariantPixelBufferTestParameters(PT::INT16,         ome::bioformats::LITTLE),
    VariantPixelBufferTestParameters(PT::INT16,         ome::bioformats::NATIVE),

    VariantPixelBufferTestParameters(PT::INT32,         ome::bioformats::BIG),
    VariantPixelBufferTestParameters(PT::INT32,         ome::bioformats::LITTLE),
    VariantPixelBufferTestParameters(PT::INT32,         ome::bioformats::NATIVE),

    VariantPixelBufferTestParameters(PT::UINT8,         ome::bioformats::BIG),
    VariantPixelBufferTestParameters(PT::UINT8,         ome::bioformats::LITTLE),
    VariantPixelBufferTestParameters(PT::UINT8,         ome::bioformats::NATIVE),

    VariantPixelBufferTestParameters(PT::UINT16,        ome::bioformats::BIG),
    VariantPixelBufferTestParameters(PT::UINT16,        ome::bioformats::LITTLE),
    VariantPixelBufferTestParameters(PT::UINT16,        ome::bioformats::NATIVE),

    VariantPixelBufferTestParameters(PT::UINT32,        ome::bioformats::BIG),
    VariantPixelBufferTestParameters(PT::UINT32,        ome::bioformats::LITTLE),
    VariantPixelBufferTestParameters(PT::UINT32,        ome::bioformats::NATIVE),

    VariantPixelBufferTestParameters(PT::FLOAT,         ome::bioformats::BIG),
    VariantPixelBufferTestParameters(PT::FLOAT,         ome::bioformats::LITTLE),
    VariantPixelBufferTestParameters(PT::FLOAT,         ome::bioformats::NATIVE),

    VariantPixelBufferTestParameters(PT::DOUBLE,        ome::bioformats::BIG),
    VariantPixelBufferTestParameters(PT::DOUBLE,        ome::bioformats::LITTLE),
    VariantPixelBufferTestParameters(PT::DOUBLE,        ome::bioformats::NATIVE),

    VariantPixelBufferTestParameters(PT::COMPLEX,       ome::bioformats::BIG),
    VariantPixelBufferTestParameters(PT::COMPLEX,       ome::bioformats::LITTLE),
    VariantPixelBufferTestParameters(PT::COMPLEX,       ome::bioformats::NATIVE),

    VariantPixelBufferTestParameters(PT::DOUBLECOMPLEX, ome::bioformats::BIG),
    VariantPixelBufferTestParameters(PT::DOUBLECOMPLEX, ome::bioformats::LITTLE),
    VariantPixelBufferTestParameters(PT::DOUBLECOMPLEX, ome::bioformats::NATIVE)
  };

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#endif

INSTANTIATE_TEST_CASE_P(DimensionOrderVariants, DimensionOrderTest, ::testing::ValuesIn(dimension_params));
INSTANTIATE_TEST_CASE_P(VariantPixelBufferVariants, VariantPixelBufferTest, ::testing::ValuesIn(variant_params));

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

#include <ome/bioformats/PixelProperties.h>

#include <ome/test/test.h>

#include "pixel.h"

using ome::bioformats::pixel_size_type;
using ome::bioformats::PixelProperties;
using ome::bioformats::bytesPerPixel;
using ome::bioformats::bitsPerPixel;
typedef ome::xml::model::enums::PixelType PT;

template<int Type, class Native>
struct PixelTypeParam
{
  static const int type = Type;
  typedef Native expected;
};

template <typename T>
class PixelPropertiesType : public ::testing::Test
{};

TYPED_TEST_CASE_P(PixelPropertiesType);

TYPED_TEST_P(PixelPropertiesType, DefaultConstruct)
{
  typename PixelProperties<TypeParam::type>::native_type tn;
  typename PixelProperties<TypeParam::type>::big_type    tb;
  typename PixelProperties<TypeParam::type>::little_type tl;

  tn = pixel_value<typename PixelProperties<TypeParam::type>::native_type>(0);
  tb = pixel_value<typename PixelProperties<TypeParam::type>::big_type>(0);
  tl = pixel_value<typename PixelProperties<TypeParam::type>::little_type>(0);
}

TYPED_TEST_P(PixelPropertiesType, NativeSize)
{
  typename PixelProperties<TypeParam::type>::native_type tn;
  typename PixelProperties<TypeParam::type>::big_type    tb;
  typename PixelProperties<TypeParam::type>::little_type tl;

  ASSERT_EQ(sizeof(tn), sizeof(typename TypeParam::expected));
  ASSERT_EQ(sizeof(tb), sizeof(typename TypeParam::expected));
  ASSERT_EQ(sizeof(tl), sizeof(typename TypeParam::expected));
}

REGISTER_TYPED_TEST_CASE_P(PixelPropertiesType,
                           DefaultConstruct, NativeSize);

typedef ::testing::Types<PixelTypeParam<PT::INT8,         int8_t>,
                         PixelTypeParam<PT::INT16,        int16_t>,
                         PixelTypeParam<PT::INT32,        int32_t>,
                         PixelTypeParam<PT::UINT8,        uint8_t>,
                         PixelTypeParam<PT::UINT16,       uint16_t>,
                         PixelTypeParam<PT::UINT32,       uint32_t>,
                         PixelTypeParam<PT::BIT,          bool>,
                         PixelTypeParam<PT::FLOAT,        float>,
                         PixelTypeParam<PT::DOUBLE,       double>,
                         PixelTypeParam<PT::COMPLEX,      std::complex<float> >,
                         PixelTypeParam<PT::DOUBLECOMPLEX,std::complex<double> > > TestTypes;
INSTANTIATE_TYPED_TEST_CASE_P(PixelPropertiesTypeTest, PixelPropertiesType, TestTypes);

class PixelPropertiesTestParameters
{
public:
  PT              type;
  pixel_size_type byte_size;
  pixel_size_type bit_size;
  bool            is_signed;
  bool            is_integer;
  bool            is_complex;

  PixelPropertiesTestParameters(PT          type,
                                std::size_t byte_size,
                                std::size_t bit_size,
                                bool        is_signed,
                                bool        is_integer,
                                bool        is_complex):
    type(type),
    byte_size(static_cast<pixel_size_type>(byte_size)),
    bit_size(static_cast<pixel_size_type>(bit_size)),
    is_signed(is_signed),
    is_integer(is_integer),
    is_complex(is_complex)
  {}
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const PixelPropertiesTestParameters& params)
{
  return os << PT(params.type);
}

class PixelPropertiesTest : public ::testing::TestWithParam<PixelPropertiesTestParameters>
{
};

TEST_P(PixelPropertiesTest, ByteSize)
{
  const PixelPropertiesTestParameters& params = GetParam();

  ASSERT_EQ(params.byte_size, bytesPerPixel(params.type));
}

TEST_P(PixelPropertiesTest, BitSize)
{
  const PixelPropertiesTestParameters& params = GetParam();

  ASSERT_EQ(params.bit_size, bitsPerPixel(params.type));
}

TEST_P(PixelPropertiesTest, Signed)
{
  const PixelPropertiesTestParameters& params = GetParam();

  if (params.is_signed)
    ASSERT_TRUE(ome::bioformats::isSigned(params.type));
  else
    ASSERT_FALSE(ome::bioformats::isSigned(params.type));
}

TEST_P(PixelPropertiesTest, Integer)
{
  const PixelPropertiesTestParameters& params = GetParam();

  if (params.is_integer)
    ASSERT_TRUE(ome::bioformats::isInteger(params.type));
  else
    ASSERT_FALSE(ome::bioformats::isInteger(params.type));
}

TEST_P(PixelPropertiesTest, FloatingPoint)
{
  const PixelPropertiesTestParameters& params = GetParam();

  if (!params.is_integer)
    ASSERT_TRUE(ome::bioformats::isFloatingPoint(params.type));
  else
    ASSERT_FALSE(ome::bioformats::isFloatingPoint(params.type));
}

TEST_P(PixelPropertiesTest, Complex)
{
  const PixelPropertiesTestParameters& params = GetParam();

  if (params.is_complex)
    ASSERT_TRUE(ome::bioformats::isComplex(params.type));
  else
    ASSERT_FALSE(ome::bioformats::isComplex(params.type));
}


PixelPropertiesTestParameters property_params[] =
  { //                            PixelType          byte size                     bit size                        signed integer complex
    PixelPropertiesTestParameters(PT::INT8,          sizeof(int8_t),               sizeof(int8_t)*8,               true,  true,   false),
    PixelPropertiesTestParameters(PT::INT16,         sizeof(int16_t),              sizeof(int16_t)*8,              true,  true,   false),
    PixelPropertiesTestParameters(PT::INT32,         sizeof(int32_t),              sizeof(int32_t)*8,              true,  true,   false),
    PixelPropertiesTestParameters(PT::UINT8,         sizeof(uint8_t),              sizeof(uint8_t)*8,              false, true,   false),
    PixelPropertiesTestParameters(PT::UINT16,        sizeof(uint16_t),             sizeof(uint16_t)*8,             false, true,   false),
    PixelPropertiesTestParameters(PT::UINT32,        sizeof(uint32_t),             sizeof(uint32_t)*8,             false, true,   false),
    PixelPropertiesTestParameters(PT::BIT,           sizeof(bool),                 sizeof(bool)*8,                 false, true,   false),
    PixelPropertiesTestParameters(PT::FLOAT,         sizeof(float),                sizeof(float)*8,                true,  false,  false),
    PixelPropertiesTestParameters(PT::DOUBLE,        sizeof(double),               sizeof(double)*8,               true,  false,  false),
    PixelPropertiesTestParameters(PT::COMPLEX,       sizeof(std::complex<float>),  sizeof(std::complex<float>)*8,  true,  false,  true),
    PixelPropertiesTestParameters(PT::DOUBLECOMPLEX, sizeof(std::complex<double>), sizeof(std::complex<double>)*8, true,  false,  true)
  };

class FindPixelTypeTestParameters
{
public:
  PT              type;
  pixel_size_type byte_size;
  pixel_size_type bit_size;
  bool            is_signed;
  bool            is_integer;
  bool            is_complex;
  bool            throws;

  FindPixelTypeTestParameters(PT          type,
                              std::size_t byte_size,
                              std::size_t bit_size,
                              bool        is_signed,
                              bool        is_integer,
                              bool        is_complex,
                              bool        throws):
    type(type),
    byte_size(static_cast<pixel_size_type>(byte_size)),
    bit_size(static_cast<pixel_size_type>(bit_size)),
    is_signed(is_signed),
    is_integer(is_integer),
    is_complex(is_complex),
    throws(throws)
  {}
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const FindPixelTypeTestParameters& params)
{
  return os << PT(params.type);
}

class FindPixelTypeTest : public ::testing::TestWithParam<FindPixelTypeTestParameters>
{
};

TEST_P(FindPixelTypeTest, FindFromBytes)
{
  const FindPixelTypeTestParameters& params = GetParam();

  if (params.throws)
    {
      ASSERT_THROW(ome::bioformats::pixelTypeFromBytes(params.byte_size, params.is_signed, params.is_integer, params.is_complex), std::exception);
    }
  else
    {
      ASSERT_EQ(params.type, ome::bioformats::pixelTypeFromBytes(params.byte_size, params.is_signed, params.is_integer, params.is_complex));
    }
}

TEST_P(FindPixelTypeTest, FindFromBits)
{
  const FindPixelTypeTestParameters& params = GetParam();

  if (params.throws)
    {
      ASSERT_THROW(ome::bioformats::pixelTypeFromBytes(params.byte_size, params.is_signed, params.is_integer, params.is_complex), std::exception);
    }
  else
    {
      ASSERT_EQ(params.type, ome::bioformats::pixelTypeFromBytes(params.byte_size, params.is_signed, params.is_integer, params.is_complex));
    }
}

FindPixelTypeTestParameters find_params[] =
  { //                          PixelType          byte size                     bit size                        signed integer complex throws
    FindPixelTypeTestParameters(PT::INT8,          sizeof(int8_t),               sizeof(int8_t)*8,               true,  true,   false,  false),
    FindPixelTypeTestParameters(PT::INT16,         sizeof(int16_t),              sizeof(int16_t)*8,              true,  true,   false,  false),
    FindPixelTypeTestParameters(PT::INT32,         sizeof(int32_t),              sizeof(int32_t)*8,              true,  true,   false,  false),
    FindPixelTypeTestParameters(PT::UINT8,         sizeof(uint8_t),              sizeof(uint8_t)*8,              false, true,   false,  false),
    FindPixelTypeTestParameters(PT::UINT16,        sizeof(uint16_t),             sizeof(uint16_t)*8,             false, true,   false,  false),
    FindPixelTypeTestParameters(PT::UINT32,        sizeof(uint32_t),             sizeof(uint32_t)*8,             false, true,   false,  false),
    FindPixelTypeTestParameters(PT::FLOAT,         sizeof(float),                sizeof(float)*8,                true,  false,  false,  false),
    FindPixelTypeTestParameters(PT::DOUBLE,        sizeof(double),               sizeof(double)*8,               true,  false,  false,  false),
    FindPixelTypeTestParameters(PT::COMPLEX,       sizeof(std::complex<float>),  sizeof(std::complex<float>)*8,  true,  false,  true,   false),
    FindPixelTypeTestParameters(PT::DOUBLECOMPLEX, sizeof(std::complex<double>), sizeof(std::complex<double>)*8, true,  false,  true,   false),
    FindPixelTypeTestParameters(PT::INT8,          0,                            0,                              true,  true,   false,  true),
    FindPixelTypeTestParameters(PT::INT8,          0,                            3,                              true,  true,   false,  true),
    FindPixelTypeTestParameters(PT::INT8,          0,                            3,                              true,  true,   true,   true),
    FindPixelTypeTestParameters(PT::INT8,          0,                            3,                              false, true,   true,   true),
    FindPixelTypeTestParameters(PT::INT8,          0,                            3,                              false, false,  false,  true)
  };

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(PixelPropertiesVariants, PixelPropertiesTest, ::testing::ValuesIn(property_params));
INSTANTIATE_TEST_CASE_P(FindPixelTypeVariants, FindPixelTypeTest, ::testing::ValuesIn(find_params));

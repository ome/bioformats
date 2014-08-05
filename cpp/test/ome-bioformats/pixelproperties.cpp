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

#include <ome/bioformats/PixelProperties.h>

#include <gtest/gtest.h>

using ome::bioformats::PixelProperties;
using ome::bioformats::bytesPerPixel;
using ome::bioformats::bitsPerPixel;
typedef ome::xml::model::enums::PixelType PT;

TEST(PixelProperties, ConstructSignedInt8)
{
  PixelProperties<PT::INT8>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(int8_t));

  PixelProperties<PT::INT8>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(int8_t));

  PixelProperties<PT::INT8>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(int8_t));
}

TEST(PixelProperties, ConstructSignedInt16)
{
  PixelProperties<PT::INT16>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(int16_t));

  PixelProperties<PT::INT16>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(int16_t));

  PixelProperties<PT::INT16>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(int16_t));
}

TEST(PixelProperties, ConstructSignedInt32)
{
  PixelProperties<PT::INT32>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(int32_t));

  PixelProperties<PT::INT32>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(int32_t));

  PixelProperties<PT::INT32>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(int32_t));
}

TEST(PixelProperties, ConstructUnsignedInt8)
{
  PixelProperties<PT::UINT8>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(uint8_t));

  PixelProperties<PT::UINT8>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(uint8_t));

  PixelProperties<PT::UINT8>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(uint8_t));
}

TEST(PixelProperties, ConstructUnsignedInt16)
{
  PixelProperties<PT::UINT16>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(uint16_t));

  PixelProperties<PT::UINT16>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(uint16_t));

  PixelProperties<PT::UINT16>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(uint16_t));
}

TEST(PixelProperties, ConstructUnsignedInt32)
{
  PixelProperties<PT::UINT32>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(uint32_t));

  PixelProperties<PT::UINT32>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(uint32_t));

  PixelProperties<PT::UINT32>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(uint32_t));
}

TEST(PixelProperties, ConstructFloat)
{
  PixelProperties<PT::FLOAT>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(float));

  PixelProperties<PT::FLOAT>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(float));

  PixelProperties<PT::FLOAT>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(float));
}

TEST(PixelProperties, ConstructDouble)
{
  PixelProperties<PT::DOUBLE>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(double));

  PixelProperties<PT::DOUBLE>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(double));

  PixelProperties<PT::DOUBLE>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(double));
}

TEST(PixelProperties, ConstructBit)
{
  PixelProperties<PT::BIT>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(bool));

  PixelProperties<PT::BIT>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(bool));

  PixelProperties<PT::BIT>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(bool));
}

TEST(PixelProperties, ConstructComplex)
{
  PixelProperties<PT::COMPLEX>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(std::complex<float>));

  PixelProperties<PT::COMPLEX>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(std::complex<float>));

  PixelProperties<PT::COMPLEX>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(std::complex<float>));
}

TEST(PixelProperties, ConstructDoubleComplex)
{
  PixelProperties<PT::DOUBLECOMPLEX>::native_type tn;
  ASSERT_EQ(sizeof(tn), sizeof(std::complex<double>));

  PixelProperties<PT::DOUBLECOMPLEX>::big_type tb;
  ASSERT_EQ(sizeof(tb), sizeof(std::complex<double>));

  PixelProperties<PT::DOUBLECOMPLEX>::little_type tl;
  ASSERT_EQ(sizeof(tl), sizeof(std::complex<double>));
}

TEST(PixelProperties, SizeSignedInt8)
{
  ASSERT_EQ(bytesPerPixel(PT::INT8), sizeof(int8_t));
  ASSERT_EQ(bitsPerPixel(PT::INT8), sizeof(int8_t) * 8);
}

TEST(PixelProperties, SizeSignedInt16)
{
  ASSERT_EQ(bytesPerPixel(PT::INT16), sizeof(int16_t));
  ASSERT_EQ(bitsPerPixel(PT::INT16), sizeof(int16_t) * 8);
}

TEST(PixelProperties, SizeSignedInt32)
{
  ASSERT_EQ(bytesPerPixel(PT::INT32), sizeof(int32_t));
  ASSERT_EQ(bitsPerPixel(PT::INT32), sizeof(int32_t) * 8);
}

TEST(PixelProperties, SizeUnsignedInt8)
{
  ASSERT_EQ(bytesPerPixel(PT::UINT8), sizeof(uint8_t));
  ASSERT_EQ(bitsPerPixel(PT::UINT8), sizeof(uint8_t) * 8);
}

TEST(PixelProperties, SizeUnsignedInt16)
{
  ASSERT_EQ(bytesPerPixel(PT::UINT16), sizeof(uint16_t));
  ASSERT_EQ(bitsPerPixel(PT::UINT16), sizeof(uint16_t) * 8);
}

TEST(PixelProperties, SizeUnsignedInt32)
{
  ASSERT_EQ(bytesPerPixel(PT::UINT32), sizeof(uint32_t));
  ASSERT_EQ(bitsPerPixel(PT::UINT32), sizeof(uint32_t) * 8);
}

TEST(PixelProperties, SizeFloat)
{
  ASSERT_EQ(bytesPerPixel(PT::FLOAT), sizeof(float));
  ASSERT_EQ(bitsPerPixel(PT::FLOAT), sizeof(float) * 8);
}

TEST(PixelProperties, SizeDouble)
{
  ASSERT_EQ(bytesPerPixel(PT::DOUBLE), sizeof(double));
  ASSERT_EQ(bitsPerPixel(PT::DOUBLE), sizeof(double) * 8);
}

TEST(PixelProperties, SizeBit)
{
  ASSERT_EQ(bytesPerPixel(PT::BIT), sizeof(bool));
  ASSERT_EQ(bitsPerPixel(PT::BIT), sizeof(bool) * 8);
}

TEST(PixelProperties, SizeComplex)
{
  ASSERT_EQ(bytesPerPixel(PT::COMPLEX), sizeof(std::complex<float>));
  ASSERT_EQ(bitsPerPixel(PT::COMPLEX), sizeof(std::complex<float>) * 8);
}

TEST(PixelProperties, SizeDoubleComplex)
{
  ASSERT_EQ(bytesPerPixel(PT::DOUBLECOMPLEX), sizeof(std::complex<double>));
  ASSERT_EQ(bitsPerPixel(PT::DOUBLECOMPLEX), sizeof(std::complex<double>) * 8);
}

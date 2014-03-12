/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
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

#include <ome/bioformats/PixelProperties.h>

#include <gtest/gtest.h>

using ome::bioformats::PixelProperties;
using ome::bioformats::bytesPerPixel;
using ome::bioformats::bitsPerPixel;
typedef ome::xml::model::enums::PixelType PT;

TEST(PixelProperties, ConstructSignedInt8)
{
  PixelProperties<PT::INT8>::type t;
  ASSERT_EQ(sizeof(t), sizeof(int8_t));
}

TEST(PixelProperties, ConstructSignedInt16)
{
  PixelProperties<PT::INT16>::type t;
  ASSERT_EQ(sizeof(t), sizeof(int16_t));
}

TEST(PixelProperties, ConstructSignedInt32)
{
  PixelProperties<PT::INT32>::type t;
  ASSERT_EQ(sizeof(t), sizeof(int32_t));
}

TEST(PixelProperties, ConstructUnsignedInt8)
{
  PixelProperties<PT::UINT8>::type t;
  ASSERT_EQ(sizeof(t), sizeof(uint8_t));
}

TEST(PixelProperties, ConstructUnsignedInt16)
{
  PixelProperties<PT::UINT16>::type t;
  ASSERT_EQ(sizeof(t), sizeof(uint16_t));
}

TEST(PixelProperties, ConstructUnsignedInt32)
{
  PixelProperties<PT::UINT32>::type t;
  ASSERT_EQ(sizeof(t), sizeof(uint32_t));
}

TEST(PixelProperties, ConstructFloat)
{
  PixelProperties<PT::FLOAT>::type t;
  ASSERT_EQ(sizeof(t), sizeof(float));
}

TEST(PixelProperties, ConstructDouble)
{
  PixelProperties<PT::DOUBLE>::type t;
  ASSERT_EQ(sizeof(t), sizeof(double));
}

TEST(PixelProperties, ConstructBit)
{
  PixelProperties<PT::BIT>::type t;
  ASSERT_EQ(sizeof(t), sizeof(bool));
}

TEST(PixelProperties, ConstructComplex)
{
  PixelProperties<PT::COMPLEX>::type t;
  ASSERT_EQ(sizeof(t), sizeof(std::array<float,2>));
}

TEST(PixelProperties, ConstructDoubleComplex)
{
  PixelProperties<PT::DOUBLECOMPLEX>::type t;
  ASSERT_EQ(sizeof(t), sizeof(std::array<double,2>));
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
  ASSERT_EQ(bytesPerPixel(PT::COMPLEX), sizeof(std::array<float,2>));
  ASSERT_EQ(bitsPerPixel(PT::COMPLEX), sizeof(std::array<float,2>) * 8);
}

TEST(PixelProperties, SizeDoubleComplex)
{
  ASSERT_EQ(bytesPerPixel(PT::DOUBLECOMPLEX), sizeof(std::array<double,2>));
  ASSERT_EQ(bitsPerPixel(PT::DOUBLECOMPLEX), sizeof(std::array<double,2>) * 8);
}

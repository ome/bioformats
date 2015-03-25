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

#include <ome/common/endian.h>

#include <ome/compat/cstdint.h>

#include <ome/test/test.h>

using namespace ome;

TEST(Endian, UInt8)
{
  big_uint8_t b(87);
  little_uint8_t l(87);
  native_uint8_t n(87);

  uint8_t s(87);

  ASSERT_EQ(b, l);
  ASSERT_EQ(b, n);
  ASSERT_EQ(b, s);
  ASSERT_EQ(l, n);
  ASSERT_EQ(l, s);
  ASSERT_EQ(n, s);
}

TEST(Endian, UInt16)
{
  big_uint16_t b(8722);
  little_uint16_t l(8722);
  native_uint16_t n(8722);

  uint16_t s(8722);

  ASSERT_EQ(b, l);
  ASSERT_EQ(b, n);
  ASSERT_EQ(b, s);
  ASSERT_EQ(l, n);
  ASSERT_EQ(l, s);
  ASSERT_EQ(n, s);
}

TEST(Endian, UInt32)
{
  big_uint32_t b(928722);
  little_uint32_t l(928722);
  native_uint32_t n(928722);

  uint32_t s(928722);

  ASSERT_EQ(b, l);
  ASSERT_EQ(b, n);
  ASSERT_EQ(b, s);
  ASSERT_EQ(l, n);
  ASSERT_EQ(l, s);
  ASSERT_EQ(n, s);
}

TEST(Endian, UInt64)
{
  big_uint64_t b(238742724);
  little_uint64_t l(238742724);
  native_uint64_t n(238742724);

  uint64_t s(238742724);

  ASSERT_EQ(b, l);
  ASSERT_EQ(b, n);
  ASSERT_EQ(b, s);
  ASSERT_EQ(l, n);
  ASSERT_EQ(l, s);
  ASSERT_EQ(n, s);
}

TEST(Endian, Int8)
{
  big_int8_t b(-117);
  little_int8_t l(-117);
  native_int8_t n(-117);

  int8_t s(-117);

  ASSERT_EQ(b, l);
  ASSERT_EQ(b, n);
  ASSERT_EQ(b, s);
  ASSERT_EQ(l, n);
  ASSERT_EQ(l, s);
  ASSERT_EQ(n, s);
}

TEST(Endian, Int16)
{
  big_int16_t b(-8372);
  little_int16_t l(-8372);
  native_int16_t n(-8372);

  int16_t s(-8372);

  ASSERT_EQ(b, l);
  ASSERT_EQ(b, n);
  ASSERT_EQ(b, s);
  ASSERT_EQ(l, n);
  ASSERT_EQ(l, s);
  ASSERT_EQ(n, s);
}

TEST(Endian, Int32)
{
  big_int32_t b(-8328729);
  little_int32_t l(-8328729);
  native_int32_t n(-8328729);

  int32_t s(-8328729);

  ASSERT_EQ(b, l);
  ASSERT_EQ(b, n);
  ASSERT_EQ(b, s);
  ASSERT_EQ(l, n);
  ASSERT_EQ(l, s);
  ASSERT_EQ(n, s);
}

TEST(Endian, Int64)
{
  big_int64_t b(-8372922);
  little_int64_t l(-8372922);
  native_int64_t n(-8372922);

  int64_t s(-8372922);

  ASSERT_EQ(b, l);
  ASSERT_EQ(b, n);
  ASSERT_EQ(b, s);
  ASSERT_EQ(l, n);
  ASSERT_EQ(l, s);
  ASSERT_EQ(n, s);
}

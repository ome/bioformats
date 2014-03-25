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

using ome::bioformats::PixelBufferRaw;

TEST(PixelBufferRaw, ConstructSize)
{
  PixelBufferRaw buf(10);

  ASSERT_EQ(buf.size(), 10);
  ASSERT_TRUE(buf.buffer());
}

TEST(PixelBufferRaw, ConstructEmpty)
{
  PixelBufferRaw buf(0);

  ASSERT_EQ(buf.size(), 0);
  ASSERT_FALSE(buf.buffer());
}

TEST(PixelBufferRaw, ConstructRange)
{
  std::vector<uint8_t> source;
  for (uint8_t i = 0; i < 10; ++i)
    source.push_back(i);

  PixelBufferRaw buf(source.begin(), source.end());

  ASSERT_EQ(buf.size(), 10);
  ASSERT_TRUE(buf.buffer());
  for (uint8_t i = 0; i < 10; ++i)
    {
      ASSERT_EQ(*(buf.buffer()+i), i);
    }
}

TEST(PixelBufferRaw, ConstructCopy)
{
  std::vector<uint8_t> source1;
  for (uint8_t i = 0; i < 10; ++i)
    source1.push_back(i);

  std::vector<uint8_t> source2;
  for (uint8_t i = 10; i < 20; ++i)
    source2.push_back(i);

  PixelBufferRaw buf1(source1.begin(), source1.end());
  PixelBufferRaw buf2(source2.begin(), source2.end());

  ASSERT_EQ(buf1, buf1);
  ASSERT_EQ(buf2, buf2);
  ASSERT_NE(buf1, buf2);

  PixelBufferRaw buf3(buf2);
  ASSERT_EQ(buf2, buf3);
  ASSERT_NE(buf1, buf2);
}

TEST(PixelBufferRaw, GetIndex)
{
  std::vector<uint8_t> source;
  for (uint8_t i = 0; i < 100; ++i)
    source.push_back(i);

  PixelBufferRaw buf(source.begin(), source.end());
  const PixelBufferRaw& cbuf(buf);

  ASSERT_EQ(buf.size(), 100);
  ASSERT_TRUE(buf.buffer());
  for (uint8_t i = 0; i < 100; ++i)
    {
      ASSERT_EQ(cbuf[i], i);
      ASSERT_EQ(cbuf.at(i), i);
    }
}

TEST(PixelBufferRaw, SetIndex)
{
  std::vector<uint8_t> source;
  for (uint8_t i = 0; i < 100; ++i)
    source.push_back(i);

  PixelBufferRaw buf(100);
  const PixelBufferRaw& cbuf(buf);

  ASSERT_EQ(buf.size(), 100);
  ASSERT_TRUE(buf.buffer());
  for (uint8_t i = 0; i < 100; ++i)
    {
      buf[i] = i;

      ASSERT_EQ(cbuf[i], i);
      ASSERT_EQ(cbuf.at(i), i);

      buf.at(i) = i;

      ASSERT_EQ(cbuf[i], i);
      ASSERT_EQ(cbuf.at(i), i);
    }

  ASSERT_THROW(buf.at(400) = 4, std::out_of_range);
  ASSERT_THROW(cbuf.at(400), std::out_of_range);
}

TEST(PixelBufferRaw, StreamOutput)
{
  PixelBufferRaw buf(0);

  std::ostringstream os;
  os << buf;

  ASSERT_EQ(os.str(), "PixelBufferRaw size = 0");
}

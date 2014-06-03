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

using ome::bioformats::PixelBuffer;
using ome::bioformats::VariantPixelBuffer;

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
        idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = idx[9] = 0;
        ASSERT_EQ(buf.at<boost::endian::native_uint8_t>(idx), (i * 10) + j);
        ASSERT_EQ(cbuf.at<boost::endian::native_uint8_t>(idx), (i * 10) + j);
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
        idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = idx[9] = 0;

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
  badidx[2] = badidx[3] = badidx[4] = badidx[5] = badidx[6] = badidx[7] = badidx[8] = badidx[9] = 0;

  ASSERT_DEATH(buf.at<boost::endian::native_uint8_t>(badidx) = 4U, "Assertion.*failed");
  ASSERT_DEATH(cbuf.at<boost::endian::native_uint8_t>(badidx), "Assertion.*failed");
}

TEST(VariantPixelBuffer, StreamOutput)
{
  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1]);

  std::ostringstream os;
  os << buf;

  ASSERT_EQ("", os.str());
}

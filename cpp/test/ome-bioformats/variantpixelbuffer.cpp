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

#include <ome/bioformats/VariantPixelBuffer.h>

#include <ome/test/test.h>

#include "pixel.h"

using ome::bioformats::PixelBuffer;
using ome::bioformats::PixelBufferBase;
using ome::bioformats::PixelProperties;
using ome::bioformats::VariantPixelBuffer;
typedef ome::xml::model::enums::PixelType PT;

/*
 * NOTE: Update equivalent tests in pixelbuffer.cpp when making
 * changes.
 */

class VariantPixelBufferTestParameters
{
public:
  PT type;

  VariantPixelBufferTestParameters(PT type):
    type(type)
  {}
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const VariantPixelBufferTestParameters& params)
{
  return os << PT(params.type);
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
                         params.type);

  ASSERT_EQ(buf.num_elements(), 10U);
  ASSERT_TRUE(buf.data());
}

/*
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
    for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
      data.push_back(pixel_value<value_type>(i));
    buf.assign(data.begin(), data.end());

    ASSERT_TRUE(buf.data());
    ASSERT_TRUE(buf.data<value_type>());
    ASSERT_TRUE(v->data());
    for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
      {
        ASSERT_EQ(*(buf.data<value_type>()+i), pixel_value<value_type>(i));
      }
  }
};

/*
 * Assign buffer with (not quite) random values and check.  The
 * purpose is to fill a buffer with a unique arrangement of values for
 * checking storage ordering.
 */
struct RandomAssignTestVisitor : public boost::static_visitor<>
{
  VariantPixelBuffer& buf;

  RandomAssignTestVisitor(VariantPixelBuffer& buf):
    buf(buf)
  {}

  template<typename T>
  void
  operator() (const T& v)
  {
    typedef typename T::element_type::value_type value_type;

    VariantPixelBuffer::size_type size(buf.num_elements());
    std::vector<value_type> data;
    for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
      data.push_back(pixel_value<value_type>(i * 13));
    buf.assign(data.begin(), data.end());

    ASSERT_TRUE(buf.data());
    ASSERT_TRUE(buf.data<value_type>());
    ASSERT_TRUE(v->data());
    for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
      {
        ASSERT_EQ(*(buf.data<value_type>()+i), pixel_value<value_type>(i * 13));
      }
  }

  void
  operator() (const ome::compat::shared_ptr<PixelBuffer<PixelProperties<PT::BIT>::std_type> >& v)
  {
    typedef PixelProperties<PT::BIT>::std_type value_type;

    VariantPixelBuffer::size_type size(buf.num_elements());
    std::vector<value_type> data;
    // For bool, we split the range into 0 and 1 to give a useful set
    // of values or else all would be 1.  Must not give the same
    // ordering for a different storage order.
    for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
      {
        uint32_t v = i * 13; // prime multiplied
        uint32_t s = i % 8;  // shift to select bit
        v = (v >> s) & 1;
        data.push_back(pixel_value<value_type>(v));
      }
    buf.assign(data.begin(), data.end());

    ASSERT_TRUE(buf.data());
    ASSERT_TRUE(buf.data<value_type>());
    ASSERT_TRUE(v->data());
    for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
      {
        uint32_t v = i * 13; // prime multiplied
        uint32_t s = i % 8;  // shift to select bit
        v = (v >> s) & 1;
        ASSERT_EQ(*(buf.data<value_type>()+i), pixel_value<value_type>(v));
      }
  }
};

/*
 * Array test.
 */
struct ArrayTestVisitor : public boost::static_visitor<>
{
  VariantPixelBuffer& buf;
  const VariantPixelBuffer& cbuf;

  ArrayTestVisitor(VariantPixelBuffer& buf):
    buf(buf),
    cbuf(buf)
  {}

  template<typename T>
  void
  operator() (const T& /* v */)
  {
    typedef typename T::element_type::value_type value_type;

    ASSERT_NO_THROW(buf.array<value_type>());
    ASSERT_NO_THROW(cbuf.array<value_type>());
    ASSERT_EQ(100U, buf.array<value_type>().num_elements());
    ASSERT_EQ(100U, cbuf.array<value_type>().num_elements());
  }
};

/*
 * Construct unmanaged buffer with extents.
 */
struct ConstructExtentRefTestVisitor : public boost::static_visitor<>
{
  template<typename T>
  void
  operator() (const T& /* v */)
  {
    typedef typename T::element_type::value_type value_type;

    ome::compat::array<typename PixelBuffer<value_type>::size_type, 9> extents;
    extents[0] = 5;
    extents[1] = 2;
    extents[2] = extents[3] = extents[4] = extents[5] = extents[6] = extents[7] = extents[8] = 1;

    // VariantPixelBuffer with unmanaged backing store.
    value_type backing[10];
    ome::compat::shared_ptr<PixelBuffer<value_type> > buf
      (new PixelBuffer<value_type>(&backing[0], extents));
    VariantPixelBuffer mbuf(buf);

    ASSERT_EQ(10U, mbuf.num_elements());

    AssignTestVisitor av(mbuf);
    boost::apply_visitor(av, mbuf.vbuffer());
  }
};

/*
 * Construct unmanaged buffer with ranges.
 */
struct ConstructRangeRefTestVisitor : public boost::static_visitor<>
{
  template<typename T>
  void
  operator() (const T& /* v */)
  {
    typedef typename T::element_type::value_type value_type;

    // VariantPixelBuffer with unmanaged backing store.
    value_type backing[100];
    ome::compat::shared_ptr<PixelBuffer<value_type> > buf
      (new PixelBuffer<value_type>(&backing[0],
                                   boost::extents[10][10][1][1][1][1][1][1][1]));
    VariantPixelBuffer mbuf(buf);

    ASSERT_EQ(100U, mbuf.num_elements());

    AssignTestVisitor av(mbuf);
    boost::apply_visitor(av, mbuf.vbuffer());
  }
};

/*
 * Data test.
 */
struct DataTestVisitor : public boost::static_visitor<>
{
  VariantPixelBuffer& buf;
  const VariantPixelBuffer& cbuf;

  DataTestVisitor(VariantPixelBuffer& buf):
    buf(buf),
    cbuf(buf)
  {}

  template<typename T>
  void
  operator() (const T& /* v */)
  {
    typedef typename T::element_type::value_type value_type;

    ASSERT_TRUE(buf.data());
    ASSERT_TRUE(cbuf.data());
    ASSERT_TRUE(buf.data<value_type>());
    ASSERT_TRUE(cbuf.data<value_type>());
    ASSERT_EQ(buf.array<value_type>().data(), reinterpret_cast<value_type *>(buf.data()));
    ASSERT_EQ(cbuf.array<value_type>().data(), reinterpret_cast<const value_type *>(cbuf.data()));
    ASSERT_EQ(buf.array<value_type>().data(), buf.data<value_type>());
    ASSERT_EQ(cbuf.array<value_type>().data(), cbuf.data<value_type>());
  }
};

/*
 * Managed test.
 */
struct ManagedTestVisitor : public boost::static_visitor<>
{
  const VariantPixelBufferTestParameters& params;

  ManagedTestVisitor(const VariantPixelBufferTestParameters& params):
    params(params)
  {}

  template<typename T>
  void
  operator() (const T& /* v */)
  {
    typedef typename T::element_type::value_type value_type;

    {
      // VariantPixelBuffer with managed backing store.
      VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                             params.type);
      const VariantPixelBuffer& cbuf(buf);

      EXPECT_TRUE(buf.managed());
      EXPECT_TRUE(cbuf.managed());
    }

    {
      // VariantPixelBuffer with unmanaged backing store.
      value_type backing[100];
      ome::compat::shared_ptr<PixelBuffer<value_type> > buf
        (new PixelBuffer<value_type>(&backing[0],
                                     boost::extents[10][10][1][1][1][1][1][1][1]));
      VariantPixelBuffer mbuf(buf);
      const VariantPixelBuffer& cmbuf(mbuf);

      EXPECT_FALSE(mbuf.managed());
      EXPECT_FALSE(cmbuf.managed());
    }
  }
};

/*
 * Origin test.
 */
struct OriginTestVisitor : public boost::static_visitor<>
{
  VariantPixelBuffer& buf;
  const VariantPixelBuffer& cbuf;

  OriginTestVisitor(VariantPixelBuffer& buf):
    buf(buf),
    cbuf(buf)
  {}

  template<typename T>
  void
  operator() (const T& /* v */)
  {
    typedef typename T::element_type::value_type value_type;

    const value_type *origin = cbuf.origin<value_type>();
    EXPECT_EQ(reinterpret_cast<const value_type *>(cbuf.data()), origin);
    EXPECT_EQ(cbuf.data<value_type>(), origin);
  }
};

/*
 * Get index.
 */
struct GetIndexTestVisitor : public boost::static_visitor<>
{
  VariantPixelBuffer& buf;
  const VariantPixelBuffer& cbuf;

  GetIndexTestVisitor(VariantPixelBuffer& buf):
    buf(buf),
    cbuf(buf)
  {}

  template<typename T>
  void
  operator() (const T& v)
  {
    typedef typename T::element_type::value_type value_type;

    ASSERT_EQ(buf.num_elements(), 100U);
    ASSERT_TRUE(buf.data());
    for (uint32_t i = 0U; i < 10U; ++i)
      for (uint32_t j = 0U; j < 10U; ++j)
        {
          VariantPixelBuffer::indices_type idx;
          idx[0] = i;
          idx[1] = j;
          idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;

          value_type val = pixel_value<value_type>((j * 10) + i);

          EXPECT_EQ(val, v->at(idx));
          EXPECT_EQ(val, v->at(idx));
        }
  }
};

/*
 * Set index.
 */
struct SetIndexTestVisitor : public boost::static_visitor<>
{
  VariantPixelBuffer& buf;
  const VariantPixelBuffer& cbuf;

  SetIndexTestVisitor(VariantPixelBuffer& buf):
    buf(buf),
    cbuf(buf)
  {}

  template<typename T>
  void
  operator() (T& v)
  {
    typedef typename T::element_type::value_type value_type;

    const T& cv(v);

    for (uint32_t i = 0U; i < 10U; ++i)
      for (uint32_t j = 0U; j < 10U; ++j)
        {
          VariantPixelBuffer::indices_type idx;
          idx[0] = i;
          idx[1] = j;
          idx[2] = idx[3] = idx[4] = idx[5] = idx[6] = idx[7] = idx[8] = 0;

          value_type val = pixel_value<value_type>(i + j + j);

          v->at(idx) = val;

          ASSERT_EQ(val, v->at(idx));
          ASSERT_EQ(val, cv->at(idx));
        }
  }
};

/*
 * Set index death test.
 */
struct SetIndexDeathTestVisitor : public boost::static_visitor<>
{
  VariantPixelBuffer& buf;
  const VariantPixelBuffer& cbuf;

  SetIndexDeathTestVisitor(VariantPixelBuffer& buf):
    buf(buf),
    cbuf(buf)
  {}

  template<typename T>
  void
  operator() (const T& v)
  {
    typedef typename T::element_type::value_type value_type;

    const T& cv(v);

    VariantPixelBuffer::indices_type badidx;
    badidx[0] = 13;
    badidx[1] = 2;
    badidx[2] = badidx[3] = badidx[4] = badidx[5] = badidx[6] = badidx[7] = badidx[8] = 0;

    ASSERT_DEATH_IF_SUPPORTED(v->at(badidx) = value_type(4), "Assertion.*failed");
    ASSERT_DEATH_IF_SUPPORTED(value_type obs = cv->at(badidx), "Assertion.*failed");
  }
};

/*
 * Stream input test.
 */
struct StreamInputTestVisitor : public boost::static_visitor<>
{
  VariantPixelBuffer& buf;
  const VariantPixelBuffer& cbuf;

  StreamInputTestVisitor(VariantPixelBuffer& buf):
    buf(buf),
    cbuf(buf)
  {}

  template<typename T>
  void
  operator() (const T& v)
  {
    typedef typename T::element_type::value_type value_type;

    VariantPixelBuffer::size_type size = buf.num_elements();
    std::stringstream ss;

    for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
      {
        value_type val = pixel_value<value_type>(i);
        ss.write(reinterpret_cast<const char *>(&val), sizeof(value_type));
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
            EXPECT_EQ(pixel_value<value_type>(i++), v->at(idx));
  }
};

/*
 * Stream output test.
 */
struct StreamOutputTestVisitor : public boost::static_visitor<>
{
  VariantPixelBuffer& buf;
  const VariantPixelBuffer& cbuf;

  StreamOutputTestVisitor(VariantPixelBuffer& buf):
    buf(buf),
    cbuf(buf)
  {}

  template<typename T>
  void
  operator() (const T& v)
  {
    typedef typename T::element_type::value_type value_type;

    VariantPixelBuffer::size_type size = buf.num_elements();
    std::stringstream ss;

    std::vector<value_type> vec;
    for (VariantPixelBuffer::size_type i = 0; i < size; ++i)
      {
        value_type val = pixel_value<value_type>(i);
        vec.push_back(val);
      }

    buf.assign(vec.begin(), vec.end());
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
              EXPECT_EQ(pixel_value<value_type>(i), v->at(idx));
              value_type sval;
              ss.read(reinterpret_cast<char *>(&sval), sizeof(value_type));
              EXPECT_FALSE(!ss);
              EXPECT_EQ(sval, pixel_value<value_type>(i));
              ++i;
            }
  }
};

TEST_P(VariantPixelBufferTest, ConstructExtent)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  ome::compat::array<VariantPixelBuffer::size_type, 9> extents;
  extents[0] = 5;
  extents[1] = 2;
  extents[2] = extents[3] = extents[4] = extents[5] = extents[6] = extents[7] = extents[8] = 1;

  VariantPixelBuffer buf(extents, params.type);
  ASSERT_EQ(buf.num_elements(), 10U);

  AssignTestVisitor v(buf);
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, ConstructExtentRef)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  // Dummy, for type selection.
  VariantPixelBuffer buf(boost::extents[5][2][1][1][1][1][1][1][1],
                         params.type);

  ConstructExtentRefTestVisitor v;
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, ConstructRange)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[5][2][1][1][1][1][1][1][1],
                         params.type);
  ASSERT_EQ(buf.num_elements(), 10U);

  AssignTestVisitor v(buf);
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, ConstructRangeRef)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  // Dummy, for type selection.
  VariantPixelBuffer buf(boost::extents[5][2][1][1][1][1][1][1][1],
                         params.type);

  ConstructRangeRefTestVisitor v;
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, ConstructCopy)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  std::vector<uint8_t> source1;
  for (uint8_t i = 0U; i < 10U; ++i)
    source1.push_back(i);

  std::vector<uint8_t> source2;
  for (uint8_t i = 10U; i < 20U; ++i)
    source2.push_back(i);

  VariantPixelBuffer buf1(boost::extents[5][2][1][1][1][1][1][1][1],
                          params.type);
  ASSERT_EQ(buf1.num_elements(), 10U);
  AssignTestVisitor v1(buf1);
  boost::apply_visitor(v1, buf1.vbuffer());

  VariantPixelBuffer buf2(boost::extents[5][2][1][1][1][1][1][1][1],
                          params.type);
  ASSERT_EQ(buf1.num_elements(), 10U);
  AssignTestVisitor v2(buf1);
  boost::apply_visitor(v2, buf1.vbuffer());

  ASSERT_EQ(buf1, buf1);
  ASSERT_EQ(buf2, buf2);
  ASSERT_NE(buf1, buf2);

  VariantPixelBuffer buf3(buf2);
  ASSERT_EQ(buf2, buf3);
  ASSERT_NE(buf1, buf2);
}

TEST_P(VariantPixelBufferTest, OperatorEquals)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf1(boost::extents[5][2][3][4][1][3][1][1][1],
                          params.type,
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, true));
  VariantPixelBuffer buf2(boost::extents[5][2][3][4][1][3][1][1][1],
                          params.type,
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, true));

  RandomAssignTestVisitor v1(buf1);
  boost::apply_visitor(v1, buf1.vbuffer());
  RandomAssignTestVisitor v2(buf2);
  boost::apply_visitor(v2, buf2.vbuffer());

  EXPECT_TRUE(buf1 == buf2);
  EXPECT_FALSE(buf1 != buf2);
}

TEST_P(VariantPixelBufferTest, OperatorEqualsIncompatibleTypes)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf1(boost::extents[5][2][3][4][1][3][1][1][1],
                          params.type,
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, true));
  VariantPixelBuffer buf2(boost::extents[5][2][3][4][1][3][1][1][1],
                          (params.type == PT::UINT8 ? PT::UINT16 : PT::UINT8),
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, true));

  RandomAssignTestVisitor v1(buf1);
  boost::apply_visitor(v1, buf1.vbuffer());
  RandomAssignTestVisitor v2(buf2);
  boost::apply_visitor(v2, buf2.vbuffer());

  EXPECT_FALSE(buf1 == buf2);
  EXPECT_FALSE(buf2 == buf1);
  EXPECT_TRUE(buf1 != buf2);
  EXPECT_TRUE(buf2 != buf1);
}

TEST_P(VariantPixelBufferTest, OperatorNotEquals)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf1(boost::extents[5][2][3][4][1][3][1][1][1],
                          params.type,
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, true));
  VariantPixelBuffer buf2(boost::extents[5][2][3][4][1][3][1][1][1],
                          params.type,
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, false));

  RandomAssignTestVisitor v1(buf1);
  boost::apply_visitor(v1, buf1.vbuffer());

  EXPECT_TRUE(buf1 != buf2);
  EXPECT_TRUE(buf2 != buf1);
  EXPECT_FALSE(buf1 == buf2);
  EXPECT_FALSE(buf2 == buf1);
}

TEST_P(VariantPixelBufferTest, OperatorNotEqualsIncompatibleTypes)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf1(boost::extents[5][2][3][4][1][3][1][1][1],
                          params.type,
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, true));
  VariantPixelBuffer buf2(boost::extents[5][2][3][4][1][3][1][1][1],
                          (params.type == PT::UINT8 ? PT::UINT16 : PT::UINT8),
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, false));

  RandomAssignTestVisitor v1(buf1);
  boost::apply_visitor(v1, buf1.vbuffer());

  EXPECT_TRUE(buf1 != buf2);
  EXPECT_FALSE(buf1 == buf2);
}

TEST_P(VariantPixelBufferTest, OperatorAssign)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf1(boost::extents[5][2][3][4][1][3][1][1][1],
                          params.type,
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, true));
  VariantPixelBuffer buf2(boost::extents[5][2][3][4][1][3][1][1][1],
                          params.type,
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, false));
  VariantPixelBuffer buf3(boost::extents[5][2][3][4][1][3][1][1][1],
                          params.type,
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYTCZ, true));
  VariantPixelBuffer buf4(boost::extents[5][2][3][4][1][3][1][1][1],
                          params.type,
                          PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYTCZ, false));

  RandomAssignTestVisitor v1(buf1);
  boost::apply_visitor(v1, buf1.vbuffer());
  RandomAssignTestVisitor v2(buf2);
  boost::apply_visitor(v2, buf2.vbuffer());
  RandomAssignTestVisitor v3(buf3);
  boost::apply_visitor(v3, buf3.vbuffer());
  RandomAssignTestVisitor v4(buf4);
  boost::apply_visitor(v4, buf4.vbuffer());

  EXPECT_TRUE(buf1 == buf1);
  EXPECT_TRUE(buf1 != buf2);
  EXPECT_TRUE(buf1 != buf3);
  EXPECT_TRUE(buf1 != buf4);

  buf2 = buf1;
  buf3 = buf1;
  buf4 = buf1;

  EXPECT_TRUE(buf1 == buf1);
  EXPECT_TRUE(buf1 == buf2);
  EXPECT_TRUE(buf1 == buf3);
  EXPECT_TRUE(buf1 == buf4);
}

TEST_P(VariantPixelBufferTest, Array)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                         params.type);

  ArrayTestVisitor v(buf);
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, Data)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                         params.type);

  DataTestVisitor v(buf);
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, Valid)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                         params.type);
  const VariantPixelBuffer& cbuf(buf);

  ASSERT_TRUE(buf.valid());
  ASSERT_TRUE(cbuf.valid());
}

TEST_P(VariantPixelBufferTest, Managed)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                         params.type);

  ManagedTestVisitor v(GetParam());
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, NumElements)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][10][1][1][1][1],
                         params.type);
  const VariantPixelBuffer& cbuf(buf);

  ASSERT_EQ(1000U, buf.num_elements());
  ASSERT_EQ(1000U, cbuf.num_elements());
}

TEST_P(VariantPixelBufferTest, NumDimensions)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][10][1][1][1][1],
                         params.type);
  const VariantPixelBuffer& cbuf(buf);

  ASSERT_EQ(9U, buf.num_dimensions());
  ASSERT_EQ(9U, cbuf.num_dimensions());
}

TEST_P(VariantPixelBufferTest, Shape)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][3][1][1][10][1][4][1][1],
                         params.type);
  const VariantPixelBuffer& cbuf(buf);

  const VariantPixelBuffer::size_type *shape = cbuf.shape();
  EXPECT_EQ(10U, *(shape+0));
  EXPECT_EQ( 3U, *(shape+1));
  EXPECT_EQ( 1U, *(shape+2));
  EXPECT_EQ( 1U, *(shape+3));
  EXPECT_EQ(10U, *(shape+4));
  EXPECT_EQ( 1U, *(shape+5));
  EXPECT_EQ( 4U, *(shape+6));
  EXPECT_EQ( 1U, *(shape+7));
  EXPECT_EQ( 1U, *(shape+8));
}

TEST_P(VariantPixelBufferTest, Strides)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][3][1][1][10][1][4][1][1],
                         params.type);
  const VariantPixelBuffer& cbuf(buf);

  const boost::multi_array_types::index *strides = cbuf.strides();
  EXPECT_EQ(  1U, *(strides+0));
  EXPECT_EQ( 10U, *(strides+1));
  EXPECT_EQ(120U, *(strides+2));
  EXPECT_EQ(120U, *(strides+3));
  EXPECT_EQ(120U, *(strides+4));
  EXPECT_EQ(  1U, *(strides+5));
  EXPECT_EQ( 30U, *(strides+6));
  EXPECT_EQ(120U, *(strides+7));
  EXPECT_EQ(120U, *(strides+8));
}

TEST_P(VariantPixelBufferTest, IndexBases)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][3][1][1][10][1][4][1][1],
                         params.type);
  const VariantPixelBuffer& cbuf(buf);

  const boost::multi_array_types::index *bases = cbuf.index_bases();
  EXPECT_EQ(0U, *(bases+0));
  EXPECT_EQ(0U, *(bases+1));
  EXPECT_EQ(0U, *(bases+2));
  EXPECT_EQ(0U, *(bases+3));
  EXPECT_EQ(0U, *(bases+4));
  EXPECT_EQ(0U, *(bases+5));
  EXPECT_EQ(0U, *(bases+6));
  EXPECT_EQ(0U, *(bases+7));
  EXPECT_EQ(0U, *(bases+8));
}

TEST_P(VariantPixelBufferTest, Origin)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][3][1][1][10][1][4][1][1],
                         params.type);

  OriginTestVisitor v(buf);
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, StorageOrder)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  {
    VariantPixelBuffer buf(boost::extents[10][3][1][1][10][1][4][1][1],
                           params.type);
    const VariantPixelBuffer& cbuf(buf);

    const VariantPixelBuffer::storage_order_type& order = cbuf.storage_order();

    EXPECT_EQ(5U, order.ordering(0));
    EXPECT_EQ(0U, order.ordering(1));
    EXPECT_EQ(1U, order.ordering(2));
    EXPECT_EQ(6U, order.ordering(3));
    EXPECT_EQ(2U, order.ordering(4));
    EXPECT_EQ(7U, order.ordering(5));
    EXPECT_EQ(3U, order.ordering(6));
    EXPECT_EQ(8U, order.ordering(7));
    EXPECT_EQ(4U, order.ordering(8));

    EXPECT_TRUE(order.ascending(0));
    EXPECT_TRUE(order.ascending(1));
    EXPECT_TRUE(order.ascending(2));
    EXPECT_TRUE(order.ascending(3));
    EXPECT_TRUE(order.ascending(4));
    EXPECT_TRUE(order.ascending(5));
    EXPECT_TRUE(order.ascending(6));
    EXPECT_TRUE(order.ascending(7));
    EXPECT_TRUE(order.ascending(8));
  }
}


TEST_P(VariantPixelBufferTest, GetIndex)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                         params.type);
  ASSERT_EQ(buf.num_elements(), 100U);
  ASSERT_TRUE(buf.data());

  AssignTestVisitor v1(buf);
  boost::apply_visitor(v1, buf.vbuffer());
  GetIndexTestVisitor v2(buf);
  boost::apply_visitor(v2, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, SetIndex)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                         params.type);
  ASSERT_EQ(buf.num_elements(), 100U);
  ASSERT_TRUE(buf.data());

  SetIndexTestVisitor v(buf);
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, SetIndexDeathTest)
{
#ifndef NDEBUG
  ::testing::FLAGS_gtest_death_test_style = "threadsafe";

  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[10][10][1][1][1][1][1][1][1],
                         params.type);

  SetIndexDeathTestVisitor v(buf);
  boost::apply_visitor(v, buf.vbuffer());
#endif // ! NDEBUG
}

TEST_P(VariantPixelBufferTest, StreamInput)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[2][2][3][4][1][1][1][1][1],
                         params.type);

  StreamInputTestVisitor v(buf);
  boost::apply_visitor(v, buf.vbuffer());
}

TEST_P(VariantPixelBufferTest, StreamOutput)
{
  const VariantPixelBufferTestParameters& params = GetParam();

  VariantPixelBuffer buf(boost::extents[2][2][3][4][1][1][1][1][1],
                         params.type);

  StreamOutputTestVisitor v(buf);
  boost::apply_visitor(v, buf.vbuffer());
}

VariantPixelBufferTestParameters variant_params[] =
  { //                               PixelType
    VariantPixelBufferTestParameters(PT::INT8),
    VariantPixelBufferTestParameters(PT::INT16),
    VariantPixelBufferTestParameters(PT::INT32),
    VariantPixelBufferTestParameters(PT::UINT8),
    VariantPixelBufferTestParameters(PT::UINT16),
    VariantPixelBufferTestParameters(PT::UINT32),
    VariantPixelBufferTestParameters(PT::FLOAT),
    VariantPixelBufferTestParameters(PT::DOUBLE),
    VariantPixelBufferTestParameters(PT::BIT),
    VariantPixelBufferTestParameters(PT::COMPLEX),
    VariantPixelBufferTestParameters(PT::DOUBLECOMPLEX)
  };

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(VariantPixelBufferVariants, VariantPixelBufferTest, ::testing::ValuesIn(variant_params));

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

#include <ome/bioformats/MetadataMap.h>

#include <sstream>
#include <stdexcept>
#include <iostream>

#include <ome/test/test.h>

// Include last due to side effect of MPL vector limit setting which can change the default
#include <boost/lexical_cast.hpp>

using ome::bioformats::MetadataMap;
using boost::lexical_cast;

class MetadataMapTest : public ::testing::Test
{
public:
  MetadataMap m;
  const MetadataMap& cm;

  MetadataMapTest():
    ::testing::Test(),
    m(),
    cm(m)
  {
  }

  void SetUp()
  {
    MetadataMap::value_type v;
    m.set("int1", int32_t(82));
    v = 272;
    m.set("int2", v);

    std::vector<std::string> vs;
    vs.push_back("s1");
    vs.push_back("s2");
    vs.push_back("s3");
    m.set("vector<string>1", vs);

    ASSERT_EQ(m.size(), 3U);
  }
};

TEST_F(MetadataMapTest, DefaultConstruct)
{
  MetadataMap m2;
}

TEST_F(MetadataMapTest, Copy)
{
  MetadataMap m2(m);
  ASSERT_EQ(m, m2);
}

TEST_F(MetadataMapTest, Assign)
{
  MetadataMap m2;
  m2 = m;
  ASSERT_EQ(m, m2);
}

TEST_F(MetadataMapTest, SetVariant)
{
  // Setting using boost::variant.

  m.clear();
  MetadataMap::value_type v, vget;
  uint32_t ui = 43;
  v = ui;
  m.set("uint", v);

  ASSERT_TRUE(m.get("uint", vget));
  ASSERT_EQ(boost::get<uint32_t>(vget), ui);

  uint32_t uiget = 345;
  ASSERT_TRUE(m.get("uint", uiget));
  ASSERT_EQ(uiget, ui);

  v = std::string("str");
  m.set("stringval", v);

  ASSERT_TRUE(m.get("stringval", vget));
  ASSERT_EQ(boost::get<std::string>(vget), "str");

  std::string sget("invalid");
  ASSERT_TRUE(m.get("stringval", sget));
  ASSERT_EQ(sget, "str");
}

TEST_F(MetadataMapTest, SetSpecific)
{
  // Setting using specific types directly, which will be converted to
  // boost::variant on the fly.

  m.clear();

  double d(43.234523);
  m.set("double1", d);

  double dget = 3.2342;
  ASSERT_TRUE(m.get("double1", dget));

  m.set("int", int16_t(45));
  ASSERT_EQ(m.get<int16_t>("int"), 45);

  std::vector<uint32_t> vd;
  vd.push_back(43);
  vd.push_back(234);
  vd.push_back(3);
  m.set("vector<uint32_t>", vd);

  std::vector<uint32_t> vdget;
  ASSERT_TRUE(m.get("vector<uint32_t>", vdget));
  ASSERT_EQ(vd, vdget);
}

TEST_F(MetadataMapTest, SetOperator)
{
  // Set as an associative array.

  m.clear();

  uint32_t v = 2342;
  m["uint32"] = v;

  v = 0;
  ASSERT_TRUE(m.get("uint32", v));
  ASSERT_EQ(v, 2342U);
}

TEST_F(MetadataMapTest, Get)
{
  // Safe value-copying getter.

  MetadataMap::value_type v;
  v = 43;
  ASSERT_TRUE(m.get("int1", v));
  ASSERT_EQ(boost::get<int32_t>(v), 82);

  std::vector<double> vd;
  vd.push_back(43.2342);
  vd.push_back(234.23423342);
  vd.push_back(3.234);
  m.set("vector<double>", vd);

  std::vector<double> vdget;
  ASSERT_TRUE(m.get("vector<double>", vdget));
  ASSERT_EQ(vd, vdget);
}

TEST_F(MetadataMapTest, SetReference)
{
  // Fast-but-potentially-throwing setter.

  int32_t v1 = 324;

  ASSERT_NO_THROW(m.get<int32_t>("int1") = 4390);
  ASSERT_NO_THROW(v1 = m.get<int32_t>("int1"));
  ASSERT_EQ(v1, 4390);

  int32_t& vref(m.get<int32_t>("int1"));
  vref = 723;
  ASSERT_NO_THROW(v1 = m.get<int32_t>("int1"));
  ASSERT_EQ(vref, 723);
  ASSERT_EQ(v1, 723);
}

TEST_F(MetadataMapTest, GetReference)
{
  // Fast-but-potentially-throwing getter.

  int32_t v1 = 23;
  ASSERT_NO_THROW(v1 = m.get<int32_t>("int1"));
  ASSERT_EQ(v1, 82);

  int32_t& v2 = m.get<int32_t>("int1");
  ASSERT_EQ(v2, 82);

  MetadataMap::value_type& v3 = m.get<MetadataMap::value_type>("int1");
  ASSERT_EQ(boost::get<int32_t>(v3), 82);
}

TEST_F(MetadataMapTest, GetConstReference)
{
  // Fast-but-potentially-throwing constant getter.

  int32_t v1 = 23;
  ASSERT_NO_THROW(v1 = m.get<int32_t>("int1"));
  ASSERT_EQ(v1, 82);

  const int32_t& v2 = cm.get<int32_t>("int1");
  ASSERT_EQ(v2, 82);

  const MetadataMap::value_type& v3 = cm.get<MetadataMap::value_type>("int1");
  ASSERT_EQ(boost::get<int32_t>(v3), 82);
}

TEST_F(MetadataMapTest, GetReferenceFail)
{
  ASSERT_THROW(m.get<int32_t>("invalid"), boost::bad_get);
  ASSERT_THROW(m.get<double>("int1"), boost::bad_get);
}

TEST_F(MetadataMapTest, Append)
{
  // Vector appender.

  int8_t v = -3;
  m.append("neg", v);

  std::vector<int8_t> iget;
  ASSERT_TRUE(m.get("neg", iget));
  ASSERT_EQ(iget.size(), 1U);

  v = 2;
  m.append("neg", v);
  ASSERT_TRUE(m.get("neg", iget));
  ASSERT_EQ(iget.size(), 2U);
}

TEST_F(MetadataMapTest, GetInvalidFail)
{
  // Fetching a nonexistent key fails, and does not modifiy the
  // original value.
  MetadataMap::value_type v;
  v = 64;
  ASSERT_FALSE(m.get("invalid", v));
  ASSERT_EQ(boost::get<int>(v), 64);
}

TEST_F(MetadataMapTest, GetBadTypeFail)
{
  // Fetching the wrong type will fail and leave the original value
  // unmodified.
  uint32_t expected = 13;
  uint32_t iv(expected);
  ASSERT_FALSE(m.get("int1", iv));
  ASSERT_EQ(iv, expected);
}

TEST_F(MetadataMapTest, Find)
{
  MetadataMap::iterator it = m.find("int1");
  ASSERT_TRUE(it != cm.end());

  MetadataMap::iterator itf = m.find("invalid");
  ASSERT_TRUE(itf == cm.end());
}

TEST_F(MetadataMapTest, FindConst)
{
  MetadataMap::const_iterator it = cm.find("int1");
  ASSERT_TRUE(it != cm.end());

  MetadataMap::const_iterator itf = cm.find("invalid");
  ASSERT_TRUE(itf == cm.end());
}

TEST_F(MetadataMapTest, Insert)
{
  MetadataMap::map_type::value_type i1("int1", int32_t(4332));
  std::pair<MetadataMap::iterator, bool> r1 = m.insert(i1);

  ASSERT_FALSE(r1.second);
  ASSERT_EQ(m.get<int32_t>("int1"), 82);

  MetadataMap::map_type::value_type i2("int3", int32_t(7823));
  std::pair<MetadataMap::iterator, bool> r2 = m.insert(i2);

  ASSERT_TRUE(r2.first != m.end());
  ASSERT_TRUE(r2.second);
  ASSERT_EQ(m.get<int32_t>("int3"), 7823);
}

TEST_F(MetadataMapTest, EraseKey)
{
  m.erase("int1");
  ASSERT_EQ(m.size(), 2U);
  m.erase("int2");
  ASSERT_EQ(m.size(), 1U);
}

TEST_F(MetadataMapTest, EraseIter)
{
  MetadataMap::iterator i1 = m.find("int1");
  m.erase(i1);
  ASSERT_EQ(m.size(), 2U);

  MetadataMap::iterator i2 = m.find("int2");
  m.erase(i2);
  ASSERT_EQ(m.size(), 1U);
}

TEST_F(MetadataMapTest, Keys)
{
  std::vector<std::string> keys = m.keys();
  ASSERT_EQ(m.size(), keys.size());
}

TEST_F(MetadataMapTest, Merge)
{
  MetadataMap m2;
  MetadataMap::map_type::value_type i1("merge1", int32_t(12));
  m2.insert(i1);
  MetadataMap::map_type::value_type i2("merge2", int32_t(13));
  m2.insert(i2);
  MetadataMap::map_type::value_type i3("int1", int32_t(14));
  m2.insert(i3);

  ASSERT_EQ(m.size(), 3U);
  m.merge(m2, "merge-");
  ASSERT_EQ(m.size(), 6U);

  MetadataMap::iterator f1 = m.find("merge-merge1");
  ASSERT_TRUE(f1 != m.end());
  MetadataMap::iterator f2 = m.find("merge-merge2");
  ASSERT_TRUE(f2 != m.end());
  MetadataMap::iterator f3 = m.find("merge-int1");
  ASSERT_TRUE(f3 != m.end());

}

TEST_F(MetadataMapTest, Flatten)
{
  // Vector flattening with suffix padding.

  for (uint32_t i = 40; i >= 24; --i)
    m.append("padtest", i);

  ASSERT_EQ(m.size(), 4U);

  MetadataMap flat = m.flatten();
  ASSERT_EQ(flat.size(), 22U);

  std::ostringstream os;
  os << flat;

  std::string expected("int1 = 82\n"
                       "int2 = 272\n"
                       "padtest #01 = 40\n"
                       "padtest #02 = 39\n"
                       "padtest #03 = 38\n"
                       "padtest #04 = 37\n"
                       "padtest #05 = 36\n"
                       "padtest #06 = 35\n"
                       "padtest #07 = 34\n"
                       "padtest #08 = 33\n"
                       "padtest #09 = 32\n"
                       "padtest #10 = 31\n"
                       "padtest #11 = 30\n"
                       "padtest #12 = 29\n"
                       "padtest #13 = 28\n"
                       "padtest #14 = 27\n"
                       "padtest #15 = 26\n"
                       "padtest #16 = 25\n"
                       "padtest #17 = 24\n"
                       "vector<string>1 #1 = s1\n"
                       "vector<string>1 #2 = s2\n"
                       "vector<string>1 #3 = s3\n");

  ASSERT_EQ(os.str(), expected);
}

TEST_F(MetadataMapTest, Size)
{
  ASSERT_EQ(m.size(), 3U);

  for (uint32_t i = 0U; i < 20U; ++i)
    m[std::string("label-") + lexical_cast<std::string>(i)] = i;

  ASSERT_EQ(m.size(), 23U);
}

TEST_F(MetadataMapTest, Empty)
{
  ASSERT_FALSE(m.empty());
  m.clear();
  ASSERT_TRUE(m.empty());
}

TEST_F(MetadataMapTest, Clear)
{
  ASSERT_EQ(m.size(), 3U);
  m.clear();
  ASSERT_EQ(m.size(), 0U);
}

TEST_F(MetadataMapTest, OperatorEquals)
{
  MetadataMap eq(m);
  MetadataMap ne;

  ASSERT_EQ(m, eq);
  ASSERT_NE(m, ne);
  ASSERT_NE(eq, ne);
}

TEST_F(MetadataMapTest, OperatorCompare)
{
  MetadataMap l;
  l.set("a", 1);

  MetadataMap g;
  g.set("x", 23);

  ASSERT_LT(l, m);
  ASSERT_LE(l, m);
  ASSERT_GT(m, l);
  ASSERT_GE(m, l);

  ASSERT_LT(m, g);
  ASSERT_LE(m, g);
  ASSERT_GT(g, m);
  ASSERT_GE(g, m);

  ASSERT_LT(l, g);
  ASSERT_LE(l, g);
  ASSERT_GT(g, l);
  ASSERT_GE(g, l);
}

TEST_F(MetadataMapTest, StreamOutput)
{
  std::ostringstream os;
  os << m;

  std::string expected("int1 = 82\n"
                       "int2 = 272\n"
                       "vector<string>1 #1 = s1\n"
                       "vector<string>1 #2 = s2\n"
                       "vector<string>1 #3 = s3\n");

  ASSERT_EQ(os.str(), expected);
}

TEST_F(MetadataMapTest, StreamOutputPad)
{
  // Check padding of suffixes is correct.

  m.clear();
  for (uint32_t i = 0; i < 15; ++i)
    m.append("padflat", i);

  std::ostringstream os;
  os << m;

  std::string expected("padflat #01 = 0\n"
                       "padflat #02 = 1\n"
                       "padflat #03 = 2\n"
                       "padflat #04 = 3\n"
                       "padflat #05 = 4\n"
                       "padflat #06 = 5\n"
                       "padflat #07 = 6\n"
                       "padflat #08 = 7\n"
                       "padflat #09 = 8\n"
                       "padflat #10 = 9\n"
                       "padflat #11 = 10\n"
                       "padflat #12 = 11\n"
                       "padflat #13 = 12\n"
                       "padflat #14 = 13\n"
                       "padflat #15 = 14\n");

  ASSERT_EQ(os.str(), expected);
}

TEST_F(MetadataMapTest, StreamValueOutput)
{
  MetadataMap::value_type v;
  v = uint32_t(43);

  std::ostringstream os;
  os << v;

  std::string expected("43");

  ASSERT_EQ(os.str(), expected);
}

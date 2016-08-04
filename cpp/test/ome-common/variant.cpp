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

#include <ome/common/variant.h>

#include <ome/test/test.h>

typedef boost::variant<int,double,std::string> var;

TEST(Variant, Create)
{
  var v;
}

TEST(Variant, SetInt)
{
  var v1(int(32354));
  ASSERT_EQ(32354, boost::get<int>(v1));

  var v2;
  v2 = int(32354);
  ASSERT_EQ(32354, boost::get<int>(v2));
}

TEST(Variant, SetString)
{
  var v1(std::string("test"));
  ASSERT_EQ(std::string("test"), boost::get<std::string>(v1));

  var v2;
  v2 = std::string("test");
  ASSERT_EQ(std::string("test"), boost::get<std::string>(v2));
}

class test_visitor : public boost::static_visitor<>
{
public:
  template<typename T>
  void
  operator()(const T& value)
  {
    std::cout << value << std::endl;
  }
};

TEST(Variant, ApplyStaticVisitor)
{
  test_visitor visitor;
  var v;

  v = 32;
  boost::apply_visitor(visitor, v);

  v = std::string("V");
  boost::apply_visitor(visitor, v);
}

// Now test more complex MPL variant types.

#include <string>

typedef boost::mpl::vector<std::string,
                           bool> non_numeric_types;
typedef boost::make_variant_over<non_numeric_types>::type non_numeric_variant;

TEST(Variant, MPLVectorNonNumeric)
{
  non_numeric_variant v1(std::string("String value"));
  ASSERT_EQ(std::string("String value"), boost::get<std::string>(v1));
  non_numeric_variant v2(false);
  ASSERT_EQ(false, boost::get<bool>(v2));
}

#include <ome/compat/cstdint.h>

typedef boost::mpl::vector<uint8_t,
                           uint16_t,
                           uint32_t,
                           uint64_t,
                           int8_t,
                           int16_t,
                           int32_t,
                           int64_t> integer_types;
typedef boost::make_variant_over<integer_types>::type integer_variant;

TEST(Variant, MPLVectorInteger)
{
  integer_variant v1(uint64_t(238220U));
  ASSERT_EQ(uint64_t(238220U), boost::get<uint64_t>(v1));
  integer_variant v2(int16_t(432));
  ASSERT_EQ(int16_t(432), boost::get<int16_t>(v2));
}

typedef boost::mpl::joint_view<non_numeric_types,
                               integer_types>::type joint_types_view;

typedef boost::mpl::insert_range<boost::mpl::vector0<>, boost::mpl::end<boost::mpl::vector0<> >::type, joint_types_view>::type joint_types;
typedef boost::make_variant_over<joint_types>::type joint_variant;

TEST(Variant, MPLVectorJointView)
{
  joint_variant v1(std::string("String value"));
  ASSERT_EQ(std::string("String value"), boost::get<std::string>(v1));
  joint_variant v2(int16_t(432));
  ASSERT_EQ(int16_t(432), boost::get<int16_t>(v2));
}

/// Convert T into a std::vector<T>.
template<typename T>
struct make_vector
{
  typedef std::vector<T> type;
};

typedef boost::mpl::transform_view<joint_types_view, make_vector<boost::mpl::_1> >::type list_types_view;
typedef boost::mpl::insert_range<boost::mpl::vector0<>, boost::mpl::end<boost::mpl::vector0<> >::type, list_types_view>::type list_types;
typedef boost::make_variant_over<list_types>::type list_variant;

TEST(Variant, MPLVectorTransformList)
{
  std::vector<std::string> strings;
  strings.push_back("s1");
  strings.push_back("s2");
  strings.push_back("s3");

  list_variant v1(strings);
  ASSERT_EQ(3, boost::get<std::vector< std::string> >(v1).size());

  std::vector<uint8_t> ints;
  ints.push_back(3);
  ints.push_back(8);
  ints.push_back(9);
  ints.push_back(43);

  list_variant v2(ints);
  ASSERT_EQ(4, boost::get<std::vector<uint8_t> >(v2).size());
}

/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
 * Copyright © 2015 Open Microscopy Environment:
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

#ifndef TEST_UNITS_H
#define TEST_UNITS_H

#include <iomanip>
#include <map>
#include <string>

#include <boost/algorithm/string.hpp>
#include <boost/iostreams/device/file_descriptor.hpp>
#include <boost/iostreams/stream.hpp>
#include <boost/lexical_cast.hpp>
#include <boost/math/constants/constants.hpp>
#include <boost/range/size.hpp>
#include <boost/units/io.hpp>
#include <boost/units/systems/si/io.hpp>

#include <ome/common/units/types.h>
#include <ome/common/filesystem.h>

#include <ome/test/test.h>

using namespace ome::common::units;

// Test data for a single test
struct test_op
{
  double initial;
  double expected;
  std::string expected_output;
};

// From unit name, to unit name, test data
typedef std::map<std::pair<std::string, std::string>, std::vector<test_op> > test_map;

extern test_map test_data;

template<class Unit, class T>
std::string unit_name(const quantity<boost::units::absolute<Unit>, T>&)
{
  return name_string(Unit());
}

template<class Unit, class T>
std::string unit_name(const quantity<Unit, T>&)
{
  return name_string(Unit());
}

template <typename T>
class UnitConv : public ::testing::Test
{
public:
  std::string from_name;
  std::string to_name;
  std::vector<test_op> ops;

  void
  SetUp()
  {
    from_name = unit_name(typename T::from_type());
    to_name = unit_name(typename T::to_type());
    test_map::key_type k(from_name, to_name);

    test_map::const_iterator i = test_data.find(k);
    if (i == test_data.end())
      std::cerr << "Failed to find tests for " << this->from_name << " to " << this->to_name << '\n';
    ASSERT_TRUE(i != test_data.end());

    ops = i->second;
  }
};

TYPED_TEST_CASE_P(UnitConv);

TYPED_TEST_P(UnitConv, DefaultConstruct)
{
  typename TypeParam::to_type t;
  typename TypeParam::from_type f;
}

TYPED_TEST_P(UnitConv, Conversion)
{
  std::cerr << "Testing unit conversion from " << this->from_name << " to " << this->to_name << " (" << this->ops.size() << " tests)\n";

  for (typename std::vector<test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    {
      typename TypeParam::to_type obs(TypeParam::from_type::from_value(i->initial));

      // Use EXPECT_NEAR rather than EXPECT_DOUBLE_EQUAL due to
      // precision loss with angle conversions and pi, and unit
      // conversions between imperial and metric, and the worst
      // culprits are torr and mmHg conversion to Pa (in the mmHg
      // case, the constant used is of low precision)
      EXPECT_NEAR(i->expected, quantity_cast<double>(obs), 1.0e-4);
    }
}

TYPED_TEST_P(UnitConv, StreamOutput)
{
  std::cerr << "Testing unit stream output from " << this->from_name << " to " << this->to_name << " (" << this->ops.size() << " tests)\n";

  for (typename std::vector<test_op>::const_iterator i = this->ops.begin();
       i != this->ops.end();
       ++i)
    {
      typename TypeParam::to_type obs(TypeParam::from_type::from_value(i->initial));

      std::ostringstream os;
      os.imbue(std::locale::classic());
      // Output with reduced precision (since floating point rounding
      // errors lead to unpredicable output)
      os << std::setprecision(4) << obs;

      EXPECT_EQ(i->expected_output, os.str());
    }
}

REGISTER_TYPED_TEST_CASE_P(UnitConv,
                           DefaultConstruct,
                           Conversion,
                           StreamOutput);

template<typename From, typename To>
struct UnitConversion
{
  typedef From from_type;
  typedef To to_type;
};

#endif // TEST_UNITS_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

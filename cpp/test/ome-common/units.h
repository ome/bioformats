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

#include <cmath>
#include <iomanip>
#include <map>
#include <string>

#include <boost/algorithm/string.hpp>
#include <boost/iostreams/device/file_descriptor.hpp>
#include <boost/iostreams/stream.hpp>
#include <boost/math/constants/constants.hpp>
#include <boost/range/size.hpp>
#include <boost/units/io.hpp>
#include <boost/units/systems/si/io.hpp>

#include <ome/compat/regex.h>

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
  std::string model_enum;
  std::string expected_model_output;
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
  typename T::value_type precision;

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

    precision = T::getPrecision();
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
  std::cerr << "Testing unit conversion from " << this->from_name << " to " << this->to_name << " (" << this->ops.size() << " tests, precision=" << this->precision << ")\n";

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
      EXPECT_NEAR(i->expected, quantity_cast<typename TypeParam::value_type>(obs), this->precision);
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

      std::string obsstr(os.str());

      // MSVC stream output uses a slightly different format than GCC
      // and Clang; it outputs three digits for the exponent instead
      // of two.  Drop the leading zero to make it compatible with the
      // expected test output.
      ome::compat::regex repl("e([+-]?)0([0-9][0-9])", ome::compat::regex::extended);

      std::string obsstr_fixed(ome::compat::regex_replace(obsstr, repl, "e$1$2"));

      EXPECT_EQ(i->expected_output, obsstr_fixed);
    }
}

REGISTER_TYPED_TEST_CASE_P(UnitConv,
                           DefaultConstruct,
                           Conversion,
                           StreamOutput);

// Test conversion using the custom _quantity typedefs,
// i.e. defaulting to double-precision float.  The tests default to
// 10e-10 precision.

template<typename From, typename To, int Precision>
struct UnitConversion
{
  typedef From from_type;
  typedef To to_type;
  typedef double value_type;

  static value_type getPrecision() { return std::pow(10.0, Precision); }
};

// Test conversion using a quantity of specified value type.  float
// tests default to 1e-4 precision, but higher precision will be seen
// for tests using small values.  These tests are not repeated for
// most unit types; they are primarily to demonstrate use of different
// quantity value types in place of the double default.  Note the huge
// inaccuracy for large values compared with double, where all but
// extremely large results have a precision in excess of 1e-10.

template<typename From, typename To, typename Value, int Precision>
struct UnitTypeConversion
{
  typedef typename boost::units::quantity<From, Value> from_type;
  typedef boost::units::quantity<To, Value> to_type;
  typedef Value value_type;

  static value_type getPrecision() { return static_cast<Value>(std::pow(10.0, Precision)); }
};

#endif // TEST_UNITS_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

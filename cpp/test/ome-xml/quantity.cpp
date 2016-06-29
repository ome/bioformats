/*
 * #%L
 * OME-COMMON C++ library for C++ compatibility/portability
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

#include <ome/common/filesystem.h>

#include <ome/test/test.h>

// Include last due to side effect of MPL vector limit setting which can change the default
#include <boost/lexical_cast.hpp>

#include <ome/xml/model/enums/UnitsElectricPotential.h>
#include <ome/xml/model/enums/UnitsFrequency.h>
#include <ome/xml/model/enums/UnitsLength.h>
#include <ome/xml/model/enums/UnitsPower.h>
#include <ome/xml/model/enums/UnitsPressure.h>
#include <ome/xml/model/enums/UnitsTemperature.h>
#include <ome/xml/model/enums/UnitsTime.h>
#include <ome/xml/model/primitives/Quantity.h>

using namespace ome::xml::model::enums;
using ome::xml::model::primitives::Quantity;
using ome::xml::model::primitives::convert;

namespace
{

  // Test data for a single test
  struct test_op
  {
    double initial;
    double expected;
    std::string expected_output;
    std::string from_symbol;
    std::string to_symbol;
    std::string expected_model_output;
    double precision;
  };

  // From unit name, to unit name, test data
  typedef std::map<std::pair<std::string, std::string>, std::vector<test_op> > test_map;

  test_map
  read_test_data()
  {
    const boost::filesystem::path testdatafile(PROJECT_SOURCE_DIR "/cpp/test/ome-xml/data/units");

    boost::iostreams::stream<boost::iostreams::file_descriptor> is(testdatafile);
    is.imbue(std::locale::classic());

    test_map ret;

    std::string line;
    while (std::getline(is, line))
      {
        if (line.length() == 0) // Empty line; do nothing.
          {
          }
        else if (line[0] == '#') // Comment line
          {
          }
        else
          {
            std::vector<std::string> tokens;
            boost::split(tokens, line, boost::is_any_of("\t"), boost::token_compress_off);

            if (tokens.size() == 9)
              {
                try
                  {
                    std::string from_unit = tokens[0];
                    std::string to_unit = tokens[1];
                    double to_value, from_value;
                    try
                      {
                        from_value = boost::lexical_cast<double>(tokens[2]);
                      }
                    catch (const std::exception& e)
                      {
                        std::cerr << "Parse fail [f2]: " << tokens[2] << '\n';
                        throw;
                      }
                    try
                      {
                        to_value = boost::lexical_cast<double>(tokens[3]);
                      }
                    catch (const std::exception& e)
                      {
                        std::cerr << "Parse fail [f3]: " << tokens[3] << '\n';
                        throw;
                      }
                    std::string to_output = tokens[4];
                    std::string from_symbol = tokens[5];
                    std::string to_symbol = tokens[6];
                    std::string model_output = tokens[7];
                    double precision;
                    try
                      {
                        precision = boost::lexical_cast<double>(tokens[8]);
                      }
                    catch (const std::exception& e)
                      {
                        std::cerr << "Parse fail [f8]: " << tokens[8] << '\n';
                        throw;
                      }
                    

                    test_map::key_type k(from_symbol, to_symbol);
                    test_op v = { from_value, to_value, to_output, from_symbol, to_symbol, model_output, std::pow(10.0, precision) };

                    test_map::iterator i = ret.find(k);
                    if (i != ret.end())
                      {
                        i->second.push_back(v);
                      }
                    else
                      {
                        test_map::mapped_type vec;
                        vec.push_back(v);
                        ret.insert(test_map::value_type(k, vec));
                      }
                  }
                catch (const std::exception& e)
                  {
                    std::cerr << "Bad line " << line << ": " << e.what() << '\n';
                  }
              }
            else
              {
                std::cerr << "Bad line with "
                          << tokens.size() << " tokens: "
                          << line << '\n';
              }

          }
      }

    std::cerr << "Created " << ret.size() << " unique test sets\n";

    return ret;
  }

  test_map test_data(read_test_data());
}

template <typename T>
class QuantityConv : public ::testing::Test
{
};

TYPED_TEST_CASE_P(QuantityConv);

TYPED_TEST_P(QuantityConv, DefaultConstruct)
{
  Quantity<TypeParam> q;
}

TYPED_TEST_P(QuantityConv, Conversion)
{
  for (test_map::const_iterator i = test_data.begin();
       i != test_data.end();
       ++i)
    {
      if (TypeParam::strings().find(i->first.first) == TypeParam::strings().end())
        continue; // Wrong symbol for this unit.
      
      std::cout << "Testing unit conversion from " << i->first.first << " to " << i->first.second << " (" << i->second.size() << " tests)\n";

      for (std::vector<test_op>::const_iterator j = i->second.begin();
           j != i->second.end();
           ++j)
        {
          Quantity<TypeParam> initial(j->initial, i->first.first);
          Quantity<TypeParam> expected(j->expected, i->first.second);

          Quantity<TypeParam> obs(convert(initial, i->first.second));


          std::cout << "  " << initial << " to " << expected
                    << " (observed " << obs << ")\n";
          // Use EXPECT_NEAR rather than EXPECT_DOUBLE_EQUAL due to
          // precision loss with angle conversions and pi, and unit
          // conversions between imperial and metric, and the worst
          // culprits are torr and mmHg conversion to Pa (in the mmHg
          // case, the constant used is of low precision)
          EXPECT_EQ(expected.getUnit(), obs.getUnit());
          EXPECT_NEAR(expected.getValue(), obs.getValue(), j->precision);
        }
    }
         
}

TYPED_TEST_P(QuantityConv, StreamOutput)
{
  // std::cerr << "Testing unit stream output from " << this->from_name << " to " << this->to_name << " (" << this->ops.size() << " tests)\n";

      // typename TypeParam::to_type obs(TypeParam::from_type::from_value(i->initial));

      // std::ostringstream os;
      // os.imbue(std::locale::classic());
      // Output with reduced precision (since floating point rounding
      // errors lead to unpredicable output)
      // os << std::setprecision(4) << obs;

      // std::string obsstr(os.str());

      // MSVC stream output uses a slightly different format than GCC
      // and Clang; it outputs three digits for the exponent instead
      // of two.  Drop the leading zero to make it compatible with the
      // expected test output.
      // ome::compat::regex repl("e([+-]?)0([0-9][0-9])", ome::compat::regex::extended);

      // std::string obsstr_fixed(ome::compat::regex_replace(obsstr, repl, "e$1$2"));

      // EXPECT_EQ(i->expected_output, obsstr_fixed);
}

REGISTER_TYPED_TEST_CASE_P(QuantityConv,
                           DefaultConstruct,
                           Conversion,
                           StreamOutput);

typedef ::testing::Types<
  UnitsElectricPotential,
  UnitsFrequency,
  UnitsLength,
  UnitsPower,
  UnitsPressure,
  UnitsTemperature,
  UnitsTime
  > QuantityTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(QuantityTest, QuantityConv, QuantityTestTypes);

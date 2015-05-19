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

#include <ome/common/units.h>
#include <ome/common/filesystem.h>

#include <ome/test/test.h>

using namespace ome::common::units;

namespace
{

  // Test data for a single test
  struct test_op
  {
    double initial;
    double expected;
    std::string expected_output;
  };

  // From unit name, to unit name, test data
  typedef std::map<std::pair<std::string, std::string>, std::vector<test_op> > test_map;

  test_map
  read_test_data()
  {
    const boost::filesystem::path testdatafile(PROJECT_SOURCE_DIR "/cpp/test/ome-common/data/units");

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

            if (tokens.size() == 5)
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
                        std::cerr << "Parse fail: " << tokens[2] << '\n';
                        throw;
                      }
                    try
                      {
                        to_value = boost::lexical_cast<double>(tokens[3]);
                      }
                    catch (const std::exception& e)
                      {
                        std::cerr << "Parse fail: " << tokens[3] << '\n';
                        throw;
                      }
                    std::string to_output = tokens[4];

                    test_map::key_type k(from_unit, to_unit);
                    test_op v = { from_value, to_value, to_output };

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

}

test_map test_data(read_test_data());

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

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctohertz_quantity, hertz_quantity>,
  UnitConversion<zeptohertz_quantity, hertz_quantity>,
  UnitConversion<attohertz_quantity, hertz_quantity>,
  UnitConversion<femtohertz_quantity, hertz_quantity>,
  UnitConversion<picohertz_quantity, hertz_quantity>,
  UnitConversion<nanohertz_quantity, hertz_quantity>,
  UnitConversion<microhertz_quantity, hertz_quantity>,
  UnitConversion<millihertz_quantity, hertz_quantity>,
  UnitConversion<centihertz_quantity, hertz_quantity>,
  UnitConversion<decihertz_quantity, hertz_quantity>,
  UnitConversion<hertz_quantity, hertz_quantity>,
  UnitConversion<dekahertz_quantity, hertz_quantity>,
  UnitConversion<decahertz_quantity, hertz_quantity>,
  UnitConversion<hectohertz_quantity, hertz_quantity>,
  UnitConversion<kilohertz_quantity, hertz_quantity>,
  UnitConversion<megahertz_quantity, hertz_quantity>,
  UnitConversion<gigahertz_quantity, hertz_quantity>,
  UnitConversion<terahertz_quantity, hertz_quantity>,
  UnitConversion<petahertz_quantity, hertz_quantity>,
  UnitConversion<exahertz_quantity, hertz_quantity>,
  UnitConversion<zettahertz_quantity, hertz_quantity>,
  UnitConversion<yottahertz_quantity, hertz_quantity>,
  // All conversions from base unit.
  UnitConversion<hertz_quantity, yoctohertz_quantity>,
  UnitConversion<hertz_quantity, zeptohertz_quantity>,
  UnitConversion<hertz_quantity, attohertz_quantity>,
  UnitConversion<hertz_quantity, femtohertz_quantity>,
  UnitConversion<hertz_quantity, picohertz_quantity>,
  UnitConversion<hertz_quantity, nanohertz_quantity>,
  UnitConversion<hertz_quantity, microhertz_quantity>,
  UnitConversion<hertz_quantity, millihertz_quantity>,
  UnitConversion<hertz_quantity, centihertz_quantity>,
  UnitConversion<hertz_quantity, decihertz_quantity>,
  UnitConversion<hertz_quantity, hertz_quantity>,
  UnitConversion<hertz_quantity, dekahertz_quantity>,
  UnitConversion<hertz_quantity, decahertz_quantity>,
  UnitConversion<hertz_quantity, hectohertz_quantity>,
  UnitConversion<hertz_quantity, kilohertz_quantity>,
  UnitConversion<hertz_quantity, megahertz_quantity>,
  UnitConversion<hertz_quantity, gigahertz_quantity>,
  UnitConversion<hertz_quantity, terahertz_quantity>,
  UnitConversion<hertz_quantity, petahertz_quantity>,
  UnitConversion<hertz_quantity, exahertz_quantity>,
  UnitConversion<hertz_quantity, zettahertz_quantity>,
  UnitConversion<hertz_quantity, yottahertz_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<kilohertz_quantity, gigahertz_quantity>,
  UnitConversion<dekahertz_quantity, nanohertz_quantity>,
  UnitConversion<attohertz_quantity, megahertz_quantity>
  > FrequencyTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(FrequencyTest, UnitConv, FrequencyTestTypes);

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctometre_quantity, metre_quantity>,
  UnitConversion<zeptometre_quantity, metre_quantity>,
  UnitConversion<attometre_quantity, metre_quantity>,
  UnitConversion<femtometre_quantity, metre_quantity>,
  UnitConversion<picometre_quantity, metre_quantity>,
  UnitConversion<nanometre_quantity, metre_quantity>,
  UnitConversion<micrometre_quantity, metre_quantity>,
  UnitConversion<millimetre_quantity, metre_quantity>,
  UnitConversion<centimetre_quantity, metre_quantity>,
  UnitConversion<decimetre_quantity, metre_quantity>,
  UnitConversion<metre_quantity, metre_quantity>,
  UnitConversion<dekametre_quantity, metre_quantity>,
  UnitConversion<decametre_quantity, metre_quantity>,
  UnitConversion<hectometre_quantity, metre_quantity>,
  UnitConversion<kilometre_quantity, metre_quantity>,
  UnitConversion<megametre_quantity, metre_quantity>,
  UnitConversion<gigametre_quantity, metre_quantity>,
  UnitConversion<terametre_quantity, metre_quantity>,
  UnitConversion<petametre_quantity, metre_quantity>,
  UnitConversion<exametre_quantity, metre_quantity>,
  UnitConversion<zettametre_quantity, metre_quantity>,
  UnitConversion<yottametre_quantity, metre_quantity>,
  // All conversions from base unit.
  UnitConversion<metre_quantity, yoctometre_quantity>,
  UnitConversion<metre_quantity, zeptometre_quantity>,
  UnitConversion<metre_quantity, attometre_quantity>,
  UnitConversion<metre_quantity, femtometre_quantity>,
  UnitConversion<metre_quantity, picometre_quantity>,
  UnitConversion<metre_quantity, nanometre_quantity>,
  UnitConversion<metre_quantity, micrometre_quantity>,
  UnitConversion<metre_quantity, millimetre_quantity>,
  UnitConversion<metre_quantity, centimetre_quantity>,
  UnitConversion<metre_quantity, decimetre_quantity>,
  UnitConversion<metre_quantity, metre_quantity>,
  UnitConversion<metre_quantity, dekametre_quantity>,
  UnitConversion<metre_quantity, decametre_quantity>,
  UnitConversion<metre_quantity, hectometre_quantity>,
  UnitConversion<metre_quantity, kilometre_quantity>,
  UnitConversion<metre_quantity, megametre_quantity>,
  UnitConversion<metre_quantity, gigametre_quantity>,
  UnitConversion<metre_quantity, terametre_quantity>,
  UnitConversion<metre_quantity, petametre_quantity>,
  UnitConversion<metre_quantity, exametre_quantity>,
  UnitConversion<metre_quantity, zettametre_quantity>,
  UnitConversion<metre_quantity, yottametre_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanometre_quantity, micrometre_quantity>,
  UnitConversion<millimetre_quantity, centimetre_quantity>,
  UnitConversion<micrometre_quantity, millimetre_quantity>
  > LengthTestTypes;

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctometer_quantity, meter_quantity>,
  UnitConversion<zeptometer_quantity, meter_quantity>,
  UnitConversion<attometer_quantity, meter_quantity>,
  UnitConversion<femtometer_quantity, meter_quantity>,
  UnitConversion<picometer_quantity, meter_quantity>,
  UnitConversion<nanometer_quantity, meter_quantity>,
  UnitConversion<micrometer_quantity, meter_quantity>,
  UnitConversion<millimeter_quantity, meter_quantity>,
  UnitConversion<centimeter_quantity, meter_quantity>,
  UnitConversion<decimeter_quantity, meter_quantity>,
  UnitConversion<meter_quantity, meter_quantity>,
  UnitConversion<dekameter_quantity, meter_quantity>,
  UnitConversion<decameter_quantity, meter_quantity>,
  UnitConversion<hectometer_quantity, meter_quantity>,
  UnitConversion<kilometer_quantity, meter_quantity>,
  UnitConversion<megameter_quantity, meter_quantity>,
  UnitConversion<gigameter_quantity, meter_quantity>,
  UnitConversion<terameter_quantity, meter_quantity>,
  UnitConversion<petameter_quantity, meter_quantity>,
  UnitConversion<exameter_quantity, meter_quantity>,
  UnitConversion<zettameter_quantity, meter_quantity>,
  UnitConversion<yottameter_quantity, meter_quantity>,
  // All conversions from base unit.
  UnitConversion<meter_quantity, yoctometer_quantity>,
  UnitConversion<meter_quantity, zeptometer_quantity>,
  UnitConversion<meter_quantity, attometer_quantity>,
  UnitConversion<meter_quantity, femtometer_quantity>,
  UnitConversion<meter_quantity, picometer_quantity>,
  UnitConversion<meter_quantity, nanometer_quantity>,
  UnitConversion<meter_quantity, micrometer_quantity>,
  UnitConversion<meter_quantity, millimeter_quantity>,
  UnitConversion<meter_quantity, centimeter_quantity>,
  UnitConversion<meter_quantity, decimeter_quantity>,
  UnitConversion<meter_quantity, meter_quantity>,
  UnitConversion<meter_quantity, dekameter_quantity>,
  UnitConversion<meter_quantity, decameter_quantity>,
  UnitConversion<meter_quantity, hectometer_quantity>,
  UnitConversion<meter_quantity, kilometer_quantity>,
  UnitConversion<meter_quantity, megameter_quantity>,
  UnitConversion<meter_quantity, gigameter_quantity>,
  UnitConversion<meter_quantity, terameter_quantity>,
  UnitConversion<meter_quantity, petameter_quantity>,
  UnitConversion<meter_quantity, exameter_quantity>,
  UnitConversion<meter_quantity, zettameter_quantity>,
  UnitConversion<meter_quantity, yottameter_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanometer_quantity, micrometer_quantity>,
  UnitConversion<millimeter_quantity, centimeter_quantity>,
  UnitConversion<micrometer_quantity, millimeter_quantity>
  > ConvenienceLengthTestTypes;

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<angstrom_quantity, nanometre_quantity>,
  UnitConversion<micrometre_quantity, angstrom_quantity>,
  UnitConversion<thou_quantity, millimetre_quantity>,
  UnitConversion<centimetre_quantity, thou_quantity>,
  UnitConversion<inch_quantity, centimetre_quantity>,
  UnitConversion<metre_quantity, inch_quantity>,
  UnitConversion<foot_quantity, metre_quantity>,
  UnitConversion<millimetre_quantity, foot_quantity>,
  UnitConversion<yard_quantity, centimetre_quantity>,
  UnitConversion<decimetre_quantity, yard_quantity>,
  UnitConversion<mile_quantity, decametre_quantity>,
  UnitConversion<kilometre_quantity, mile_quantity>,
  UnitConversion<astronomical_unit_quantity, gigametre_quantity>,
  UnitConversion<terametre_quantity, astronomical_unit_quantity>,
  UnitConversion<light_year_quantity, terametre_quantity>,
  UnitConversion<petametre_quantity, light_year_quantity>,
  UnitConversion<parsec_quantity, petametre_quantity>,
  UnitConversion<exametre_quantity, parsec_quantity>
  > NonstandardLengthTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(LengthTest, UnitConv, LengthTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(ConvenienceLengthTest, UnitConv, ConvenienceLengthTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(NonstandardLengthTest, UnitConv, NonstandardLengthTestTypes);

  typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctowatt_quantity, watt_quantity>,
  UnitConversion<zeptowatt_quantity, watt_quantity>,
  UnitConversion<attowatt_quantity, watt_quantity>,
  UnitConversion<femtowatt_quantity, watt_quantity>,
  UnitConversion<picowatt_quantity, watt_quantity>,
  UnitConversion<nanowatt_quantity, watt_quantity>,
  UnitConversion<microwatt_quantity, watt_quantity>,
  UnitConversion<milliwatt_quantity, watt_quantity>,
  UnitConversion<centiwatt_quantity, watt_quantity>,
  UnitConversion<deciwatt_quantity, watt_quantity>,
  UnitConversion<watt_quantity, watt_quantity>,
  UnitConversion<dekawatt_quantity, watt_quantity>,
  UnitConversion<decawatt_quantity, watt_quantity>,
  UnitConversion<hectowatt_quantity, watt_quantity>,
  UnitConversion<kilowatt_quantity, watt_quantity>,
  UnitConversion<megawatt_quantity, watt_quantity>,
  UnitConversion<gigawatt_quantity, watt_quantity>,
  UnitConversion<terawatt_quantity, watt_quantity>,
  UnitConversion<petawatt_quantity, watt_quantity>,
  UnitConversion<exawatt_quantity, watt_quantity>,
  UnitConversion<zettawatt_quantity, watt_quantity>,
  UnitConversion<yottawatt_quantity, watt_quantity>,
  // All conversions from base unit.
  UnitConversion<watt_quantity, yoctowatt_quantity>,
  UnitConversion<watt_quantity, zeptowatt_quantity>,
  UnitConversion<watt_quantity, attowatt_quantity>,
  UnitConversion<watt_quantity, femtowatt_quantity>,
  UnitConversion<watt_quantity, picowatt_quantity>,
  UnitConversion<watt_quantity, nanowatt_quantity>,
  UnitConversion<watt_quantity, microwatt_quantity>,
  UnitConversion<watt_quantity, milliwatt_quantity>,
  UnitConversion<watt_quantity, centiwatt_quantity>,
  UnitConversion<watt_quantity, deciwatt_quantity>,
  UnitConversion<watt_quantity, watt_quantity>,
  UnitConversion<watt_quantity, dekawatt_quantity>,
  UnitConversion<watt_quantity, decawatt_quantity>,
  UnitConversion<watt_quantity, hectowatt_quantity>,
  UnitConversion<watt_quantity, kilowatt_quantity>,
  UnitConversion<watt_quantity, megawatt_quantity>,
  UnitConversion<watt_quantity, gigawatt_quantity>,
  UnitConversion<watt_quantity, terawatt_quantity>,
  UnitConversion<watt_quantity, petawatt_quantity>,
  UnitConversion<watt_quantity, exawatt_quantity>,
  UnitConversion<watt_quantity, zettawatt_quantity>,
  UnitConversion<watt_quantity, yottawatt_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanowatt_quantity, microwatt_quantity>,
  UnitConversion<milliwatt_quantity, centiwatt_quantity>,
  UnitConversion<microwatt_quantity, milliwatt_quantity>
  > PowerTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(PowerTest, UnitConv, PowerTestTypes);

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctopascal_quantity, pascal_quantity>,
  UnitConversion<zeptopascal_quantity, pascal_quantity>,
  UnitConversion<attopascal_quantity, pascal_quantity>,
  UnitConversion<femtopascal_quantity, pascal_quantity>,
  UnitConversion<picopascal_quantity, pascal_quantity>,
  UnitConversion<nanopascal_quantity, pascal_quantity>,
  UnitConversion<micropascal_quantity, pascal_quantity>,
  UnitConversion<millipascal_quantity, pascal_quantity>,
  UnitConversion<centipascal_quantity, pascal_quantity>,
  UnitConversion<decipascal_quantity, pascal_quantity>,
  UnitConversion<pascal_quantity, pascal_quantity>,
  UnitConversion<dekapascal_quantity, pascal_quantity>,
  UnitConversion<decapascal_quantity, pascal_quantity>,
  UnitConversion<hectopascal_quantity, pascal_quantity>,
  UnitConversion<kilopascal_quantity, pascal_quantity>,
  UnitConversion<megapascal_quantity, pascal_quantity>,
  UnitConversion<gigapascal_quantity, pascal_quantity>,
  UnitConversion<terapascal_quantity, pascal_quantity>,
  UnitConversion<petapascal_quantity, pascal_quantity>,
  UnitConversion<exapascal_quantity, pascal_quantity>,
  UnitConversion<zettapascal_quantity, pascal_quantity>,
  UnitConversion<yottapascal_quantity, pascal_quantity>,
  // All conversions from base unit.
  UnitConversion<pascal_quantity, yoctopascal_quantity>,
  UnitConversion<pascal_quantity, zeptopascal_quantity>,
  UnitConversion<pascal_quantity, attopascal_quantity>,
  UnitConversion<pascal_quantity, femtopascal_quantity>,
  UnitConversion<pascal_quantity, picopascal_quantity>,
  UnitConversion<pascal_quantity, nanopascal_quantity>,
  UnitConversion<pascal_quantity, micropascal_quantity>,
  UnitConversion<pascal_quantity, millipascal_quantity>,
  UnitConversion<pascal_quantity, centipascal_quantity>,
  UnitConversion<pascal_quantity, decipascal_quantity>,
  UnitConversion<pascal_quantity, pascal_quantity>,
  UnitConversion<pascal_quantity, dekapascal_quantity>,
  UnitConversion<pascal_quantity, decapascal_quantity>,
  UnitConversion<pascal_quantity, hectopascal_quantity>,
  UnitConversion<pascal_quantity, kilopascal_quantity>,
  UnitConversion<pascal_quantity, megapascal_quantity>,
  UnitConversion<pascal_quantity, gigapascal_quantity>,
  UnitConversion<pascal_quantity, terapascal_quantity>,
  UnitConversion<pascal_quantity, petapascal_quantity>,
  UnitConversion<pascal_quantity, exapascal_quantity>,
  UnitConversion<pascal_quantity, zettapascal_quantity>,
  UnitConversion<pascal_quantity, yottapascal_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanopascal_quantity, micropascal_quantity>,
  UnitConversion<millipascal_quantity, centipascal_quantity>,
  UnitConversion<micropascal_quantity, millipascal_quantity>
  > PressureTestTypes;

typedef ::testing::Types<
  // Bar
  UnitConversion<millibar_quantity, pascal_quantity>,
  UnitConversion<kilopascal_quantity, millibar_quantity>,
  UnitConversion<centibar_quantity, decapascal_quantity>,
  UnitConversion<hectopascal_quantity, centibar_quantity>,
  UnitConversion<decibar_quantity, kilopascal_quantity>,
  UnitConversion<megapascal_quantity, decibar_quantity>,
  UnitConversion<bar_quantity, megapascal_quantity>,
  UnitConversion<gigapascal_quantity, bar_quantity>,
  UnitConversion<decabar_quantity, gigapascal_quantity>,
  UnitConversion<kilopascal_quantity, decabar_quantity>,
  UnitConversion<hectobar_quantity, kilopascal_quantity>,
  UnitConversion<gigapascal_quantity, hectobar_quantity>,
  UnitConversion<kilobar_quantity, terapascal_quantity>,
  UnitConversion<gigapascal_quantity, kilobar_quantity>,
  UnitConversion<megabar_quantity, exapascal_quantity>,
  UnitConversion<gigapascal_quantity, megabar_quantity>,
  // Other
  UnitConversion<atmosphere_quantity, kilopascal_quantity>,
  UnitConversion<megapascal_quantity, atmosphere_quantity>,
  UnitConversion<psi_quantity, kilopascal_quantity>,
  UnitConversion<hectopascal_quantity, psi_quantity>,
  UnitConversion<torr_quantity, pascal_quantity>,
  UnitConversion<decapascal_quantity, torr_quantity>,
  UnitConversion<millitorr_quantity, pascal_quantity>,
  UnitConversion<centipascal_quantity, millitorr_quantity>,
  UnitConversion<mmHg_quantity, pascal_quantity>,
  UnitConversion<hectopascal_quantity, mmHg_quantity>
  > NonstandardPressureTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(PressureTest, UnitConv, PressureTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(NonstandardPressureTest, UnitConv, NonstandardPressureTestTypes);

typedef ::testing::Types<
  UnitConversion<celsius_absolute_quantity, kelvin_absolute_quantity>,
  UnitConversion<kelvin_absolute_quantity, celsius_absolute_quantity>,
  UnitConversion<fahrenheit_absolute_quantity, kelvin_absolute_quantity>,
  UnitConversion<kelvin_absolute_quantity, fahrenheit_absolute_quantity>,
  UnitConversion<rankine_absolute_quantity, kelvin_absolute_quantity>,
  UnitConversion<kelvin_absolute_quantity, rankine_absolute_quantity>,
  UnitConversion<celsius_absolute_quantity, fahrenheit_absolute_quantity>,
  UnitConversion<fahrenheit_absolute_quantity, celsius_absolute_quantity>
  > TemperatureTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(TemperatureTest, UnitConv, TemperatureTestTypes);

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctosecond_quantity, second_quantity>,
  UnitConversion<zeptosecond_quantity, second_quantity>,
  UnitConversion<attosecond_quantity, second_quantity>,
  UnitConversion<femtosecond_quantity, second_quantity>,
  UnitConversion<picosecond_quantity, second_quantity>,
  UnitConversion<nanosecond_quantity, second_quantity>,
  UnitConversion<microsecond_quantity, second_quantity>,
  UnitConversion<millisecond_quantity, second_quantity>,
  UnitConversion<centisecond_quantity, second_quantity>,
  UnitConversion<decisecond_quantity, second_quantity>,
  UnitConversion<second_quantity, second_quantity>,
  UnitConversion<dekasecond_quantity, second_quantity>,
  UnitConversion<decasecond_quantity, second_quantity>,
  UnitConversion<hectosecond_quantity, second_quantity>,
  UnitConversion<kilosecond_quantity, second_quantity>,
  UnitConversion<megasecond_quantity, second_quantity>,
  UnitConversion<gigasecond_quantity, second_quantity>,
  UnitConversion<terasecond_quantity, second_quantity>,
  UnitConversion<petasecond_quantity, second_quantity>,
  UnitConversion<exasecond_quantity, second_quantity>,
  UnitConversion<zettasecond_quantity, second_quantity>,
  UnitConversion<yottasecond_quantity, second_quantity>,
  // All conversions from base unit.
  UnitConversion<second_quantity, yoctosecond_quantity>,
  UnitConversion<second_quantity, zeptosecond_quantity>,
  UnitConversion<second_quantity, attosecond_quantity>,
  UnitConversion<second_quantity, femtosecond_quantity>,
  UnitConversion<second_quantity, picosecond_quantity>,
  UnitConversion<second_quantity, nanosecond_quantity>,
  UnitConversion<second_quantity, microsecond_quantity>,
  UnitConversion<second_quantity, millisecond_quantity>,
  UnitConversion<second_quantity, centisecond_quantity>,
  UnitConversion<second_quantity, decisecond_quantity>,
  UnitConversion<second_quantity, second_quantity>,
  UnitConversion<second_quantity, dekasecond_quantity>,
  UnitConversion<second_quantity, decasecond_quantity>,
  UnitConversion<second_quantity, hectosecond_quantity>,
  UnitConversion<second_quantity, kilosecond_quantity>,
  UnitConversion<second_quantity, megasecond_quantity>,
  UnitConversion<second_quantity, gigasecond_quantity>,
  UnitConversion<second_quantity, terasecond_quantity>,
  UnitConversion<second_quantity, petasecond_quantity>,
  UnitConversion<second_quantity, exasecond_quantity>,
  UnitConversion<second_quantity, zettasecond_quantity>,
  UnitConversion<second_quantity, yottasecond_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanosecond_quantity, microsecond_quantity>,
  UnitConversion<millisecond_quantity, centisecond_quantity>,
  UnitConversion<microsecond_quantity, millisecond_quantity>
  > TimeTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(TimeTest, UnitConv, TimeTestTypes);

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctovolt_quantity, volt_quantity>,
  UnitConversion<zeptovolt_quantity, volt_quantity>,
  UnitConversion<attovolt_quantity, volt_quantity>,
  UnitConversion<femtovolt_quantity, volt_quantity>,
  UnitConversion<picovolt_quantity, volt_quantity>,
  UnitConversion<nanovolt_quantity, volt_quantity>,
  UnitConversion<microvolt_quantity, volt_quantity>,
  UnitConversion<millivolt_quantity, volt_quantity>,
  UnitConversion<centivolt_quantity, volt_quantity>,
  UnitConversion<decivolt_quantity, volt_quantity>,
  UnitConversion<volt_quantity, volt_quantity>,
  UnitConversion<dekavolt_quantity, volt_quantity>,
  UnitConversion<decavolt_quantity, volt_quantity>,
  UnitConversion<hectovolt_quantity, volt_quantity>,
  UnitConversion<kilovolt_quantity, volt_quantity>,
  UnitConversion<megavolt_quantity, volt_quantity>,
  UnitConversion<gigavolt_quantity, volt_quantity>,
  UnitConversion<teravolt_quantity, volt_quantity>,
  UnitConversion<petavolt_quantity, volt_quantity>,
  UnitConversion<exavolt_quantity, volt_quantity>,
  UnitConversion<zettavolt_quantity, volt_quantity>,
  UnitConversion<yottavolt_quantity, volt_quantity>,
  // All conversions from base unit.
  UnitConversion<volt_quantity, yoctovolt_quantity>,
  UnitConversion<volt_quantity, zeptovolt_quantity>,
  UnitConversion<volt_quantity, attovolt_quantity>,
  UnitConversion<volt_quantity, femtovolt_quantity>,
  UnitConversion<volt_quantity, picovolt_quantity>,
  UnitConversion<volt_quantity, nanovolt_quantity>,
  UnitConversion<volt_quantity, microvolt_quantity>,
  UnitConversion<volt_quantity, millivolt_quantity>,
  UnitConversion<volt_quantity, centivolt_quantity>,
  UnitConversion<volt_quantity, decivolt_quantity>,
  UnitConversion<volt_quantity, volt_quantity>,
  UnitConversion<volt_quantity, dekavolt_quantity>,
  UnitConversion<volt_quantity, decavolt_quantity>,
  UnitConversion<volt_quantity, hectovolt_quantity>,
  UnitConversion<volt_quantity, kilovolt_quantity>,
  UnitConversion<volt_quantity, megavolt_quantity>,
  UnitConversion<volt_quantity, gigavolt_quantity>,
  UnitConversion<volt_quantity, teravolt_quantity>,
  UnitConversion<volt_quantity, petavolt_quantity>,
  UnitConversion<volt_quantity, exavolt_quantity>,
  UnitConversion<volt_quantity, zettavolt_quantity>,
  UnitConversion<volt_quantity, yottavolt_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanovolt_quantity, microvolt_quantity>,
  UnitConversion<millivolt_quantity, centivolt_quantity>,
  UnitConversion<microvolt_quantity, millivolt_quantity>
  > ElectricPotentialTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(ElectricPotentialTest, UnitConv, ElectricPotentialTestTypes);

typedef ::testing::Types<
  UnitConversion<radian_quantity, gradian_quantity>,
  UnitConversion<radian_quantity, degree_quantity>,
  UnitConversion<gradian_quantity, radian_quantity>,
  UnitConversion<gradian_quantity, degree_quantity>,
  UnitConversion<degree_quantity, radian_quantity>,
  UnitConversion<degree_quantity, gradian_quantity>
  > AngleTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(AngleTest, UnitConv, AngleTestTypes);

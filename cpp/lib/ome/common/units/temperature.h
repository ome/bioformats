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

/**
 * @file ome/common/units/temperature.h Temperature units of measurement.
 *
 * This header contains unit definition types, unit constants and
 * measured quantity types for SI and other standard temperature units
 * of measurement.
 */

#ifndef OME_COMMON_UNITS_TEMPERATURE_H
#define OME_COMMON_UNITS_TEMPERATURE_H

#include <ome/common/config.h>
#include <ome/common/units/types.h>

#include <boost/units/base_units/si/kelvin.hpp>
#include <boost/units/base_units/temperature/celsius.hpp>
#include <boost/units/base_units/temperature/fahrenheit.hpp>
#include <boost/units/unit.hpp>
#include <boost/units/quantity.hpp>
#include <boost/units/systems/si.hpp>

namespace ome
{
  namespace common
  {
    namespace units
    {

      // Temperature types, constants and quantities.

      /// Unit definition for temperature.
      typedef si::temperature temperature_unit;
      /// Measured quantity in temperature.
      typedef quantity<si::temperature> temperature_quantity;

      /// Unit definition for relative temperature in kelvin.
      typedef boost::units::si::kelvin_base_unit::unit_type kelvin_unit;
      /// Numeric constant for kelvin.
      BOOST_UNITS_STATIC_CONSTANT(kelvin, kelvin_unit);
      /// Numeric constant for kelvin.
      BOOST_UNITS_STATIC_CONSTANT(kelvins, kelvin_unit);
      /// Measured quantity in kelvin.
      typedef quantity<kelvin_unit> kelvin_quantity;

      /// Unit definition for relative temperature in Celsius (not convertible to or from other units due to relative scale).
      typedef boost::units::temperature::celsius_base_unit::unit_type celsius_unit;
      /// Numeric constant for Celsius.
      BOOST_UNITS_STATIC_CONSTANT(celsius, celsius_unit);
      /// Measured quantity in Celsius.
      typedef quantity<celsius_unit> celsius_quantity;

      /// Unit definition for relative temperature in Fahrenheit (not convertible to or from other units due to relative scale).
      typedef boost::units::temperature::fahrenheit_base_unit::unit_type fahrenheit_unit;
      /// Numeric constant for Fahrenheit.
      BOOST_UNITS_STATIC_CONSTANT(fahrenheit, fahrenheit_unit);
      /// Measured quantity in Fahrenheit.
      typedef quantity<fahrenheit_unit> fahrenheit_quantity;

      /// Base unit for temperature in Rankine.
      struct rankine_base_unit : boost::units::base_unit<rankine_base_unit,
                                                         boost::units::temperature_dimension, 4> { };
      /// Unit definition for relative temperature in Rankine.
      typedef rankine_base_unit::unit_type rankine_unit;
      /// Numeric constant for Rankine.
      BOOST_UNITS_STATIC_CONSTANT(rankine, rankine_unit);
      /// Measured quantity in Rankine.
      typedef quantity<rankine_unit> rankine_quantity;

      /// Unit definition for absolute temperature in kelvin.
      typedef boost::units::absolute<kelvin_unit> kelvin_absolute_unit;
      /// Numeric constant for absolute kelvin.
      BOOST_UNITS_STATIC_CONSTANT(kelvin_absolute, kelvin_absolute_unit);
      /// Numeric constant for absolute kelvin.
      BOOST_UNITS_STATIC_CONSTANT(kelvins_absolute, kelvin_absolute_unit);
      /// Measured quantity in absolute kelvin.
      typedef quantity<kelvin_absolute_unit> kelvin_absolute_quantity;

      /// Unit definition for absolute temperature in Celsius.
      typedef boost::units::absolute<celsius_unit> celsius_absolute_unit;
      /// Numeric constant for absolute Celsius.
      BOOST_UNITS_STATIC_CONSTANT(celsius_absolute, celsius_absolute_unit);
      /// Measured quantity in absolute Celsius.
      typedef quantity<celsius_absolute_unit> celsius_absolute_quantity;

      /// Unit definition for absolute temperature in Fahrenheit.
      typedef boost::units::absolute<fahrenheit_unit> fahrenheit_absolute_unit;
      /// Numeric constant for absolute Fahrenheit
      BOOST_UNITS_STATIC_CONSTANT(fahrenheit_absolute, fahrenheit_absolute_unit);
      /// Measured quantity in absolute Fahrenheit
      typedef quantity<fahrenheit_absolute_unit> fahrenheit_absolute_quantity;

      /// Unit definition for absolute temperature in Rankine.
      typedef boost::units::absolute<rankine_unit> rankine_absolute_unit;
      /// Numeric constant for absolute Rankine.
      BOOST_UNITS_STATIC_CONSTANT(rankine_absolute, rankine_absolute_unit);
      /// Measured quantity in absolute Rankine.
      typedef quantity<rankine_absolute_unit> rankine_absolute_quantity;

    }
  }
}

/// Conversion factor for Rankine to kelvin.
BOOST_UNITS_DEFINE_CONVERSION_FACTOR(ome::common::units::rankine_base_unit, boost::units::si::kelvin_base_unit, double, 5.0 / 9.0); // exact conversion
/// Conversion offset for Rankine to kelvin.
BOOST_UNITS_DEFINE_CONVERSION_OFFSET(ome::common::units::rankine_base_unit, boost::units::si::kelvin_base_unit, double, 0.0);
/// Default conversion for Rankine is to SI temperature units (kelvin).
BOOST_UNITS_DEFAULT_CONVERSION(ome::common::units::rankine_base_unit, boost::units::si::kelvin_base_unit);

namespace boost
{
  namespace units
  {

    /// Unit information for Rankine.
    template<> struct base_unit_info<ome::common::units::rankine_base_unit>
    {
      /// Unit name.
      static std::string name()   { return "rankine"; }
      /// Unit symbol.
      static std::string symbol() { return "R"; }
    };

  }
}

#endif // OME_COMMON_UNITS_TEMPERATURE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

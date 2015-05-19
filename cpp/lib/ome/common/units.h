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
 * @file ome/common/units.h Units of measurement.
 *
 * This header contains unit definition types, unit constants and
 * measured quantity types for SI and other standard units of
 * measurement.
 */

#ifndef OME_COMMON_UNITS_H
# define OME_COMMON_UNITS_H

# include <ome/common/config.h>

#include <boost/units/unit.hpp>
#include <boost/units/base_units/angle/degree.hpp>
#include <boost/units/base_units/angle/gradian.hpp>
#include <boost/units/base_units/angle/radian.hpp>
#include <boost/units/base_units/astronomical/astronomical_unit.hpp>
#include <boost/units/base_units/astronomical/light_year.hpp>
#include <boost/units/base_units/astronomical/parsec.hpp>
#include <boost/units/base_units/imperial/thou.hpp>
#include <boost/units/base_units/imperial/inch.hpp>
#include <boost/units/base_units/imperial/foot.hpp>
#include <boost/units/base_units/imperial/yard.hpp>
#include <boost/units/base_units/imperial/mile.hpp>
#include <boost/units/base_units/metric/micron.hpp>
#include <boost/units/base_units/metric/angstrom.hpp>
#include <boost/units/base_units/metric/bar.hpp>
#include <boost/units/base_units/metric/atmosphere.hpp>
#include <boost/units/base_units/metric/torr.hpp>
#include <boost/units/base_units/metric/mmHg.hpp>
#include <boost/units/base_units/si/kelvin.hpp>
#include <boost/units/base_units/temperature/celsius.hpp>
#include <boost/units/base_units/temperature/fahrenheit.hpp>
#include <boost/units/base_units/us/pound_force.hpp>
#include <boost/units/quantity.hpp>
#include <boost/units/systems/si.hpp>
#include <boost/units/systems/si/prefixes.hpp>

namespace ome
{
  namespace common
  {
    namespace units
    {

      using boost::units::absolute;
      using boost::units::quantity;
      using boost::units::quantity_cast;
      using boost::units::make_scaled_unit;
      using boost::units::scaled_base_unit;
      using boost::units::scale;
      using boost::units::static_rational;
      namespace si = boost::units::si;
      namespace metric = boost::units::metric;

      // Frequency types, constants and quantities.

      /// Unit definition for frequency.
      typedef si::frequency frequency_unit;
      /// Measured quantity (frequency, hertz).
      typedef quantity<si::frequency> frequency_quantity;

      /// Unit definition for yoctohertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<-24> > >::type yoctohertz_unit;
      /// Unit definition for zeptohertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<-21> > >::type zeptohertz_unit;
      /// Unit definition for attohertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<-18> > >::type attohertz_unit;
      /// Unit definition for femtohertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<-15> > >::type femtohertz_unit;
      /// Unit definition for picohertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<-12> > >::type picohertz_unit;
      /// Unit definition for nanohertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational< -9> > >::type nanohertz_unit;
      /// Unit definition for microhertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational< -6> > >::type microhertz_unit;
      /// Unit definition for millihertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational< -3> > >::type millihertz_unit;
      /// Unit definition for centihertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational< -2> > >::type centihertz_unit;
      /// Unit definition for decihertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational< -1> > >::type decihertz_unit;
      /// Unit definition for hertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<  0> > >::type hertz_unit;
      /// Unit definition for dekahertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<  1> > >::type dekahertz_unit;
      /// Unit definition for decahertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<  1> > >::type decahertz_unit;
      /// Unit definition for hectohertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<  2> > >::type hectohertz_unit;
      /// Unit definition for kilohertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<  3> > >::type kilohertz_unit;
      /// Unit definition for megahertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<  6> > >::type megahertz_unit;
      /// Unit definition for gigahertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational<  9> > >::type gigahertz_unit;
      /// Unit definition for terahertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational< 12> > >::type terahertz_unit;
      /// Unit definition for petahertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational< 15> > >::type petahertz_unit;
      /// Unit definition for exahertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational< 18> > >::type exahertz_unit;
      /// Unit definition for zettahertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational< 21> > >::type zettahertz_unit;
      /// Unit definition for yottahertz frequency.
      typedef make_scaled_unit<si::frequency,scale<10,static_rational< 24> > >::type yottahertz_unit;

      /// Numeric constant for yoctohertz.
      BOOST_UNITS_STATIC_CONSTANT(yoctohertz, yoctohertz_unit);
      /// Numeric constant for zeptohertz.
      BOOST_UNITS_STATIC_CONSTANT(zeptohertz, zeptohertz_unit);
      /// Numeric constant for attohertz.
      BOOST_UNITS_STATIC_CONSTANT(attohertz, attohertz_unit);
      /// Numeric constant for femtohertz.
      BOOST_UNITS_STATIC_CONSTANT(femtohertz, femtohertz_unit);
      /// Numeric constant for picohertz.
      BOOST_UNITS_STATIC_CONSTANT(picohertz, picohertz_unit);
      /// Numeric constant for nanohertz.
      BOOST_UNITS_STATIC_CONSTANT(nanohertz, nanohertz_unit);
      /// Numeric constant for microhertz.
      BOOST_UNITS_STATIC_CONSTANT(microhertz, microhertz_unit);
      /// Numeric constant for millihertz.
      BOOST_UNITS_STATIC_CONSTANT(millihertz, millihertz_unit);
      /// Numeric constant for centihertz.
      BOOST_UNITS_STATIC_CONSTANT(centihertz, centihertz_unit);
      /// Numeric constant for decihertz.
      BOOST_UNITS_STATIC_CONSTANT(decihertz, decihertz_unit);
      /// Numeric constant for hertz.
      BOOST_UNITS_STATIC_CONSTANT(hertz, hertz_unit);
      /// Numeric constant for dekahertz.
      BOOST_UNITS_STATIC_CONSTANT(dekahertz, dekahertz_unit);
      /// Numeric constant for decahertz.
      BOOST_UNITS_STATIC_CONSTANT(decahertz, decahertz_unit);
      /// Numeric constant for hectohertz.
      BOOST_UNITS_STATIC_CONSTANT(hectohertz, hectohertz_unit);
      /// Numeric constant for kilohertz.
      BOOST_UNITS_STATIC_CONSTANT(kilohertz, kilohertz_unit);
      /// Numeric constant for megahertz.
      BOOST_UNITS_STATIC_CONSTANT(megahertz, megahertz_unit);
      /// Numeric constant for gigahertz.
      BOOST_UNITS_STATIC_CONSTANT(gigahertz, gigahertz_unit);
      /// Numeric constant for terahertz.
      BOOST_UNITS_STATIC_CONSTANT(terahertz, terahertz_unit);
      /// Numeric constant for petahertz.
      BOOST_UNITS_STATIC_CONSTANT(petahertz, petahertz_unit);
      /// Numeric constant for exahertz.
      BOOST_UNITS_STATIC_CONSTANT(exahertz, exahertz_unit);
      /// Numeric constant for zettahertz.
      BOOST_UNITS_STATIC_CONSTANT(zettahertz, zettahertz_unit);
      /// Numeric constant for yottahertz.
      BOOST_UNITS_STATIC_CONSTANT(yottahertz, yottahertz_unit);

      /// Measured quantity in yoctohertz.
      typedef quantity<yoctohertz_unit> yoctohertz_quantity;
      /// Measured quantity in zeptohertz.
      typedef quantity<zeptohertz_unit> zeptohertz_quantity;
      /// Measured quantity in attohertz.
      typedef quantity<attohertz_unit> attohertz_quantity;
      /// Measured quantity in femtohertz.
      typedef quantity<femtohertz_unit> femtohertz_quantity;
      /// Measured quantity in picohertz.
      typedef quantity<picohertz_unit> picohertz_quantity;
      /// Measured quantity in nanohertz.
      typedef quantity<nanohertz_unit> nanohertz_quantity;
      /// Measured quantity in microhertz.
      typedef quantity<microhertz_unit> microhertz_quantity;
      /// Measured quantity in millihertz.
      typedef quantity<millihertz_unit> millihertz_quantity;
      /// Measured quantity in centihertz.
      typedef quantity<centihertz_unit> centihertz_quantity;
      /// Measured quantity in decihertz.
      typedef quantity<decihertz_unit> decihertz_quantity;
      /// Measured quantity in hertz.
      typedef quantity<hertz_unit> hertz_quantity;
      /// Measured quantity in dekahertz.
      typedef quantity<dekahertz_unit> dekahertz_quantity;
      /// Measured quantity in decahertz.
      typedef quantity<decahertz_unit> decahertz_quantity;
      /// Measured quantity in hectohertz.
      typedef quantity<hectohertz_unit> hectohertz_quantity;
      /// Measured quantity in kilohertz.
      typedef quantity<kilohertz_unit> kilohertz_quantity;
      /// Measured quantity in megahertz.
      typedef quantity<megahertz_unit> megahertz_quantity;
      /// Measured quantity in gigahertz.
      typedef quantity<gigahertz_unit> gigahertz_quantity;
      /// Measured quantity in terahertz.
      typedef quantity<terahertz_unit> terahertz_quantity;
      /// Measured quantity in petahertz.
      typedef quantity<petahertz_unit> petahertz_quantity;
      /// Measured quantity in exahertz.
      typedef quantity<exahertz_unit> exahertz_quantity;
      /// Measured quantity in zettahertz.
      typedef quantity<zettahertz_unit> zettahertz_quantity;
      /// Measured quantity in yottahertz.
      typedef quantity<yottahertz_unit> yottahertz_quantity;

      // Length types, constants and quantities.

      /// Unit definition for length.
      typedef si::length length_unit;
      /// Measured quantity (length, metres).
      typedef quantity<si::length> length_quantity;

      /// Unit definition for yoctometre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<-24> > >::type yoctometre_unit;
      /// Unit definition for zeptometre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<-21> > >::type zeptometre_unit;
      /// Unit definition for attometre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<-18> > >::type attometre_unit;
      /// Unit definition for femtometre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<-15> > >::type femtometre_unit;
      /// Unit definition for picometre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<-12> > >::type picometre_unit;
      /// Unit definition for nanometre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational< -9> > >::type nanometre_unit;
      /// Unit definition for micrometre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational< -6> > >::type micrometre_unit;
      /// Unit definition for millimetre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational< -3> > >::type millimetre_unit;
      /// Unit definition for centimetre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational< -2> > >::type centimetre_unit;
      /// Unit definition for decimetre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational< -1> > >::type decimetre_unit;
      /// Unit definition for metre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<  0> > >::type metre_unit;
      /// Unit definition for dekametre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<  1> > >::type dekametre_unit;
      /// Unit definition for decametre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<  1> > >::type decametre_unit;
      /// Unit definition for hectometre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<  2> > >::type hectometre_unit;
      /// Unit definition for kilometre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<  3> > >::type kilometre_unit;
      /// Unit definition for megametre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<  6> > >::type megametre_unit;
      /// Unit definition for gigametre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational<  9> > >::type gigametre_unit;
      /// Unit definition for terametre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational< 12> > >::type terametre_unit;
      /// Unit definition for petametre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational< 15> > >::type petametre_unit;
      /// Unit definition for exametre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational< 18> > >::type exametre_unit;
      /// Unit definition for zettametre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational< 21> > >::type zettametre_unit;
      /// Unit definition for yottametre length.
      typedef make_scaled_unit<si::length,scale<10,static_rational< 24> > >::type yottametre_unit;

      /// Unit definition for yoctometer length.
      typedef yoctometre_unit yoctometer_unit;
      /// Unit definition for zeptometer length.
      typedef zeptometre_unit zeptometer_unit;
      /// Unit definition for attometer length.
      typedef attometre_unit attometer_unit;
      /// Unit definition for femtometer length.
      typedef femtometre_unit femtometer_unit;
      /// Unit definition for picometer length.
      typedef picometre_unit picometer_unit;
      /// Unit definition for nanometer length.
      typedef nanometre_unit nanometer_unit;
      /// Unit definition for micrometer length.
      typedef micrometre_unit micrometer_unit;
      /// Unit definition for millimeter length.
      typedef millimetre_unit millimeter_unit;
      /// Unit definition for centimeter length.
      typedef centimetre_unit centimeter_unit;
      /// Unit definition for decimeter length.
      typedef decimetre_unit decimeter_unit;
      /// Unit definition for meter length.
      typedef metre_unit meter_unit;
      /// Unit definition for dekameter length.
      typedef dekametre_unit dekameter_unit;
      /// Unit definition for decameter length.
      typedef decametre_unit decameter_unit;
      /// Unit definition for hectometer length.
      typedef hectometre_unit hectometer_unit;
      /// Unit definition for kilometer length.
      typedef kilometre_unit kilometer_unit;
      /// Unit definition for megameter length.
      typedef megametre_unit megameter_unit;
      /// Unit definition for gigameter length.
      typedef gigametre_unit gigameter_unit;
      /// Unit definition for terameter length.
      typedef terametre_unit terameter_unit;
      /// Unit definition for petameter length.
      typedef petametre_unit petameter_unit;
      /// Unit definition for exameter length.
      typedef exametre_unit exameter_unit;
      /// Unit definition for zettameter length.
      typedef zettametre_unit zettameter_unit;
      /// Unit definition for yottameter length.
      typedef yottametre_unit yottameter_unit;

      /// Numeric constant for yoctometre.
      BOOST_UNITS_STATIC_CONSTANT(yoctometre, yoctometre_unit);
      /// Numeric constant for zeptometre.
      BOOST_UNITS_STATIC_CONSTANT(zeptometre, zeptometre_unit);
      /// Numeric constant for attometre.
      BOOST_UNITS_STATIC_CONSTANT(attometre, attometre_unit);
      /// Numeric constant for femtometre.
      BOOST_UNITS_STATIC_CONSTANT(femtometre, femtometre_unit);
      /// Numeric constant for picometre.
      BOOST_UNITS_STATIC_CONSTANT(picometre, picometre_unit);
      /// Numeric constant for nanometre.
      BOOST_UNITS_STATIC_CONSTANT(nanometre, nanometre_unit);
      /// Numeric constant for micrometre.
      BOOST_UNITS_STATIC_CONSTANT(micrometre, micrometre_unit);
      /// Numeric constant for millimetre.
      BOOST_UNITS_STATIC_CONSTANT(millimetre, millimetre_unit);
      /// Numeric constant for centimetre.
      BOOST_UNITS_STATIC_CONSTANT(centimetre, centimetre_unit);
      /// Numeric constant for decimetre.
      BOOST_UNITS_STATIC_CONSTANT(decimetre, decimetre_unit);
      /// Numeric constant for metre.
      BOOST_UNITS_STATIC_CONSTANT(metre, metre_unit);
      /// Numeric constant for dekametre.
      BOOST_UNITS_STATIC_CONSTANT(dekametre, dekametre_unit);
      /// Numeric constant for decametre.
      BOOST_UNITS_STATIC_CONSTANT(decametre, decametre_unit);
      /// Numeric constant for hectometre.
      BOOST_UNITS_STATIC_CONSTANT(hectometre, hectometre_unit);
      /// Numeric constant for kilometre.
      BOOST_UNITS_STATIC_CONSTANT(kilometre, kilometre_unit);
      /// Numeric constant for megametre.
      BOOST_UNITS_STATIC_CONSTANT(megametre, megametre_unit);
      /// Numeric constant for gigametre.
      BOOST_UNITS_STATIC_CONSTANT(gigametre, gigametre_unit);
      /// Numeric constant for terametre.
      BOOST_UNITS_STATIC_CONSTANT(terametre, terametre_unit);
      /// Numeric constant for petametre.
      BOOST_UNITS_STATIC_CONSTANT(petametre, petametre_unit);
      /// Numeric constant for exametre.
      BOOST_UNITS_STATIC_CONSTANT(exametre, exametre_unit);
      /// Numeric constant for zettametre.
      BOOST_UNITS_STATIC_CONSTANT(zettametre, zettametre_unit);
      /// Numeric constant for yottametre.
      BOOST_UNITS_STATIC_CONSTANT(yottametre, yottametre_unit);

      /// Numeric constant for yoctometre.
      BOOST_UNITS_STATIC_CONSTANT(yoctometres, yoctometre_unit);
      /// Numeric constant for zeptometre.
      BOOST_UNITS_STATIC_CONSTANT(zeptometres, zeptometre_unit);
      /// Numeric constant for attometre.
      BOOST_UNITS_STATIC_CONSTANT(attometres, attometre_unit);
      /// Numeric constant for femtometre.
      BOOST_UNITS_STATIC_CONSTANT(femtometres, femtometre_unit);
      /// Numeric constant for picometre.
      BOOST_UNITS_STATIC_CONSTANT(picometres, picometre_unit);
      /// Numeric constant for nanometre.
      BOOST_UNITS_STATIC_CONSTANT(nanometres, nanometre_unit);
      /// Numeric constant for micrometre.
      BOOST_UNITS_STATIC_CONSTANT(micrometres, micrometre_unit);
      /// Numeric constant for millimetre.
      BOOST_UNITS_STATIC_CONSTANT(millimetres, millimetre_unit);
      /// Numeric constant for centimetre.
      BOOST_UNITS_STATIC_CONSTANT(centimetres, centimetre_unit);
      /// Numeric constant for decimetre.
      BOOST_UNITS_STATIC_CONSTANT(decimetres, decimetre_unit);
      /// Numeric constant for metre.
      BOOST_UNITS_STATIC_CONSTANT(metres, metre_unit);
      /// Numeric constant for dekametre.
      BOOST_UNITS_STATIC_CONSTANT(dekametres, dekametre_unit);
      /// Numeric constant for decametre.
      BOOST_UNITS_STATIC_CONSTANT(decametres, decametre_unit);
      /// Numeric constant for hectometre.
      BOOST_UNITS_STATIC_CONSTANT(hectometres, hectometre_unit);
      /// Numeric constant for kilometre.
      BOOST_UNITS_STATIC_CONSTANT(kilometres, kilometre_unit);
      /// Numeric constant for megametre.
      BOOST_UNITS_STATIC_CONSTANT(megametres, megametre_unit);
      /// Numeric constant for gigametre.
      BOOST_UNITS_STATIC_CONSTANT(gigametres, gigametre_unit);
      /// Numeric constant for terametre.
      BOOST_UNITS_STATIC_CONSTANT(terametres, terametre_unit);
      /// Numeric constant for petametre.
      BOOST_UNITS_STATIC_CONSTANT(petametres, petametre_unit);
      /// Numeric constant for exametre.
      BOOST_UNITS_STATIC_CONSTANT(exametres, exametre_unit);
      /// Numeric constant for zettametre.
      BOOST_UNITS_STATIC_CONSTANT(zettametres, zettametre_unit);
      /// Numeric constant for yottametre.
      BOOST_UNITS_STATIC_CONSTANT(yottametres, yottametre_unit);

      /// Numeric constant for yoctometer.
      BOOST_UNITS_STATIC_CONSTANT(yoctometer, yoctometer_unit);
      /// Numeric constant for zeptometer.
      BOOST_UNITS_STATIC_CONSTANT(zeptometer, zeptometer_unit);
      /// Numeric constant for attometer.
      BOOST_UNITS_STATIC_CONSTANT(attometer, attometer_unit);
      /// Numeric constant for femtometer.
      BOOST_UNITS_STATIC_CONSTANT(femtometer, femtometer_unit);
      /// Numeric constant for picometer.
      BOOST_UNITS_STATIC_CONSTANT(picometer, picometer_unit);
      /// Numeric constant for nanometer.
      BOOST_UNITS_STATIC_CONSTANT(nanometer, nanometer_unit);
      /// Numeric constant for micrometer.
      BOOST_UNITS_STATIC_CONSTANT(micrometer, micrometer_unit);
      /// Numeric constant for millimeter.
      BOOST_UNITS_STATIC_CONSTANT(millimeter, millimeter_unit);
      /// Numeric constant for centimeter.
      BOOST_UNITS_STATIC_CONSTANT(centimeter, centimeter_unit);
      /// Numeric constant for decimeter.
      BOOST_UNITS_STATIC_CONSTANT(decimeter, decimeter_unit);
      /// Numeric constant for meter.
      BOOST_UNITS_STATIC_CONSTANT(meter, meter_unit);
      /// Numeric constant for dekameter.
      BOOST_UNITS_STATIC_CONSTANT(dekameter, dekameter_unit);
      /// Numeric constant for decameter.
      BOOST_UNITS_STATIC_CONSTANT(decameter, decameter_unit);
      /// Numeric constant for hectometer.
      BOOST_UNITS_STATIC_CONSTANT(hectometer, hectometer_unit);
      /// Numeric constant for kilometer.
      BOOST_UNITS_STATIC_CONSTANT(kilometer, kilometer_unit);
      /// Numeric constant for megameter.
      BOOST_UNITS_STATIC_CONSTANT(megameter, megameter_unit);
      /// Numeric constant for gigameter.
      BOOST_UNITS_STATIC_CONSTANT(gigameter, gigameter_unit);
      /// Numeric constant for terameter.
      BOOST_UNITS_STATIC_CONSTANT(terameter, terameter_unit);
      /// Numeric constant for petameter.
      BOOST_UNITS_STATIC_CONSTANT(petameter, petameter_unit);
      /// Numeric constant for exameter.
      BOOST_UNITS_STATIC_CONSTANT(exameter, exameter_unit);
      /// Numeric constant for zettameter.
      BOOST_UNITS_STATIC_CONSTANT(zettameter, zettameter_unit);
      /// Numeric constant for yottameter.
      BOOST_UNITS_STATIC_CONSTANT(yottameter, yottameter_unit);

      /// Numeric constant for yoctometer.
      BOOST_UNITS_STATIC_CONSTANT(yoctometers, yoctometer_unit);
      /// Numeric constant for zeptometer.
      BOOST_UNITS_STATIC_CONSTANT(zeptometers, zeptometer_unit);
      /// Numeric constant for attometer.
      BOOST_UNITS_STATIC_CONSTANT(attometers, attometer_unit);
      /// Numeric constant for femtometer.
      BOOST_UNITS_STATIC_CONSTANT(femtometers, femtometer_unit);
      /// Numeric constant for picometer.
      BOOST_UNITS_STATIC_CONSTANT(picometers, picometer_unit);
      /// Numeric constant for nanometer.
      BOOST_UNITS_STATIC_CONSTANT(nanometers, nanometer_unit);
      /// Numeric constant for micrometer.
      BOOST_UNITS_STATIC_CONSTANT(micrometers, micrometer_unit);
      /// Numeric constant for millimeter.
      BOOST_UNITS_STATIC_CONSTANT(millimeters, millimeter_unit);
      /// Numeric constant for centimeter.
      BOOST_UNITS_STATIC_CONSTANT(centimeters, centimeter_unit);
      /// Numeric constant for decimeter.
      BOOST_UNITS_STATIC_CONSTANT(decimeters, decimeter_unit);
      /// Numeric constant for meter.
      BOOST_UNITS_STATIC_CONSTANT(meters, meter_unit);
      /// Numeric constant for dekameter.
      BOOST_UNITS_STATIC_CONSTANT(dekameters, dekameter_unit);
      /// Numeric constant for decameter.
      BOOST_UNITS_STATIC_CONSTANT(decameters, decameter_unit);
      /// Numeric constant for hectometer.
      BOOST_UNITS_STATIC_CONSTANT(hectometers, hectometer_unit);
      /// Numeric constant for kilometer.
      BOOST_UNITS_STATIC_CONSTANT(kilometers, kilometer_unit);
      /// Numeric constant for megameter.
      BOOST_UNITS_STATIC_CONSTANT(megameters, megameter_unit);
      /// Numeric constant for gigameter.
      BOOST_UNITS_STATIC_CONSTANT(gigameters, gigameter_unit);
      /// Numeric constant for terameter.
      BOOST_UNITS_STATIC_CONSTANT(terameters, terameter_unit);
      /// Numeric constant for petameter.
      BOOST_UNITS_STATIC_CONSTANT(petameters, petameter_unit);
      /// Numeric constant for exameter.
      BOOST_UNITS_STATIC_CONSTANT(exameters, exameter_unit);
      /// Numeric constant for zettameter.
      BOOST_UNITS_STATIC_CONSTANT(zettameters, zettameter_unit);
      /// Numeric constant for yottameter.
      BOOST_UNITS_STATIC_CONSTANT(yottameters, yottameter_unit);

      /// Measured quantity in yoctometres.
      typedef quantity<yoctometre_unit> yoctometre_quantity;
      /// Measured quantity in zeptometres.
      typedef quantity<zeptometre_unit> zeptometre_quantity;
      /// Measured quantity in attometres.
      typedef quantity<attometre_unit> attometre_quantity;
      /// Measured quantity in femtometres.
      typedef quantity<femtometre_unit> femtometre_quantity;
      /// Measured quantity in picometres.
      typedef quantity<picometre_unit> picometre_quantity;
      /// Measured quantity in nanometres.
      typedef quantity<nanometre_unit> nanometre_quantity;
      /// Measured quantity in micrometres.
      typedef quantity<micrometre_unit> micrometre_quantity;
      /// Measured quantity in millimetres.
      typedef quantity<millimetre_unit> millimetre_quantity;
      /// Measured quantity in centimetres.
      typedef quantity<centimetre_unit> centimetre_quantity;
      /// Measured quantity in decimetres.
      typedef quantity<decimetre_unit> decimetre_quantity;
      /// Measured quantity in metres.
      typedef quantity<metre_unit> metre_quantity;
      /// Measured quantity in dekametres.
      typedef quantity<dekametre_unit> dekametre_quantity;
      /// Measured quantity in decametres.
      typedef quantity<decametre_unit> decametre_quantity;
      /// Measured quantity in hectometres.
      typedef quantity<hectometre_unit> hectometre_quantity;
      /// Measured quantity in kilometres.
      typedef quantity<kilometre_unit> kilometre_quantity;
      /// Measured quantity in megametres.
      typedef quantity<megametre_unit> megametre_quantity;
      /// Measured quantity in gigametres.
      typedef quantity<gigametre_unit> gigametre_quantity;
      /// Measured quantity in terametres.
      typedef quantity<terametre_unit> terametre_quantity;
      /// Measured quantity in petametres.
      typedef quantity<petametre_unit> petametre_quantity;
      /// Measured quantity in exametres.
      typedef quantity<exametre_unit> exametre_quantity;
      /// Measured quantity in zettametres.
      typedef quantity<zettametre_unit> zettametre_quantity;
      /// Measured quantity in yottametres.
      typedef quantity<yottametre_unit> yottametre_quantity;

      /// Measured quantity in yoctometers.
      typedef quantity<yoctometer_unit> yoctometer_quantity;
      /// Measured quantity in zeptometers.
      typedef quantity<zeptometer_unit> zeptometer_quantity;
      /// Measured quantity in attometers.
      typedef quantity<attometer_unit> attometer_quantity;
      /// Measured quantity in femtometers.
      typedef quantity<femtometer_unit> femtometer_quantity;
      /// Measured quantity in picometers.
      typedef quantity<picometer_unit> picometer_quantity;
      /// Measured quantity in nanometers.
      typedef quantity<nanometer_unit> nanometer_quantity;
      /// Measured quantity in micrometers.
      typedef quantity<micrometer_unit> micrometer_quantity;
      /// Measured quantity in millimeters.
      typedef quantity<millimeter_unit> millimeter_quantity;
      /// Measured quantity in centimeters.
      typedef quantity<centimeter_unit> centimeter_quantity;
      /// Measured quantity in decimeters.
      typedef quantity<decimeter_unit> decimeter_quantity;
      /// Measured quantity in meters.
      typedef quantity<meter_unit> meter_quantity;
      /// Measured quantity in dekameters.
      typedef quantity<dekameter_unit> dekameter_quantity;
      /// Measured quantity in decameters.
      typedef quantity<decameter_unit> decameter_quantity;
      /// Measured quantity in hectometers.
      typedef quantity<hectometer_unit> hectometer_quantity;
      /// Measured quantity in kilometers.
      typedef quantity<kilometer_unit> kilometer_quantity;
      /// Measured quantity in megameters.
      typedef quantity<megameter_unit> megameter_quantity;
      /// Measured quantity in gigameters.
      typedef quantity<gigameter_unit> gigameter_quantity;
      /// Measured quantity in terameters.
      typedef quantity<terameter_unit> terameter_quantity;
      /// Measured quantity in petameters.
      typedef quantity<petameter_unit> petameter_quantity;
      /// Measured quantity in exameters.
      typedef quantity<exameter_unit> exameter_quantity;
      /// Measured quantity in zettameters.
      typedef quantity<zettameter_unit> zettameter_quantity;
      /// Measured quantity in yottameters.
      typedef quantity<yottameter_unit> yottameter_quantity;

      /// Unit definition for angstrom length.
      typedef metric::angstrom_base_unit::unit_type angstrom_unit;
      /// Numeric constant for angstrom.
      BOOST_UNITS_STATIC_CONSTANT(angstrom, angstrom_unit);
      /// Numeric constant for angstrom.
      BOOST_UNITS_STATIC_CONSTANT(angstroms, angstrom_unit);
      /// Measured quantity in angstroms.
      typedef quantity<angstrom_unit> angstrom_quantity;

      /// Unit definition for thou length.
      typedef boost::units::imperial::thou_base_unit::unit_type thou_unit;
      /// Numeric constant for thou.
      BOOST_UNITS_STATIC_CONSTANT(thou, thou_unit);
      /// Numeric constant for thou.
      BOOST_UNITS_STATIC_CONSTANT(thous, thou_unit);
      /// Measured quantity in thous.
      typedef quantity<thou_unit> thou_quantity;

      /// Unit definition for line length (defined as 1/12 inch, used in botany).
      typedef scaled_base_unit<boost::units::imperial::inch_base_unit, scale<12, static_rational<-1> > >::unit_type line_unit;
      /// Numeric constant for line.
      BOOST_UNITS_STATIC_CONSTANT(line, line_unit);
      /// Numeric constant for line.
      BOOST_UNITS_STATIC_CONSTANT(lines, line_unit);
      /// Measured quantity in lines.
      typedef quantity<line_unit> line_quantity;

      /// Unit definition for inch length.
      typedef boost::units::imperial::inch_base_unit::unit_type inch_unit;
      /// Numeric constant for inch.
      BOOST_UNITS_STATIC_CONSTANT(inch, inch_unit);
      /// Numeric constant for inch.
      BOOST_UNITS_STATIC_CONSTANT(inches, inch_unit);
      /// Measured quantity in inches.
      typedef quantity<inch_unit> inch_quantity;

      /// Unit definition for foot length.
      typedef boost::units::imperial::foot_base_unit::unit_type foot_unit;
      /// Numeric constant for foot.
      BOOST_UNITS_STATIC_CONSTANT(foot, foot_unit);
      /// Numeric constant for foot.
      BOOST_UNITS_STATIC_CONSTANT(feet, foot_unit);
      /// Measured quantity in feet.
      typedef quantity<foot_unit> foot_quantity;

      /// Unit definition for yard length.
      typedef boost::units::imperial::yard_base_unit::unit_type yard_unit;
      /// Numeric constant for yard.
      BOOST_UNITS_STATIC_CONSTANT(yard, yard_unit);
      /// Numeric constant for yard.
      BOOST_UNITS_STATIC_CONSTANT(yards, yard_unit);
      /// Measured quantity in yards.
      typedef quantity<yard_unit> yard_quantity;

      /// Unit definition for mile length.
      typedef boost::units::imperial::mile_base_unit::unit_type mile_unit;
      /// Numeric constant for mile.
      BOOST_UNITS_STATIC_CONSTANT(mile, mile_unit);
      /// Numeric constant for mile.
      BOOST_UNITS_STATIC_CONSTANT(miles, mile_unit);
      /// Measured quantity in miles.
      typedef quantity<mile_unit> mile_quantity;

      /// Unit definition for astronomical unit length.
      typedef boost::units::astronomical::astronomical_unit_base_unit::unit_type astronomical_unit_type;
      /// Numeric constant for astronomical unit.
      BOOST_UNITS_STATIC_CONSTANT(astronomical_unit, astronomical_unit_type);
      /// Numeric constant for astronomical unit.
      BOOST_UNITS_STATIC_CONSTANT(astronomical_units, astronomical_unit_type);
      /// Measured quantity in astronomical_unit.
      typedef quantity<astronomical_unit_type> astronomical_unit_quantity;

      /// Unit definition for light year length.
      typedef boost::units::astronomical::light_year_base_unit::unit_type light_year_unit;
      /// Numeric constant for light year.
      BOOST_UNITS_STATIC_CONSTANT(light_year, light_year_unit);
      /// Numeric constant for light year.
      BOOST_UNITS_STATIC_CONSTANT(light_years, light_year_unit);
      /// Measured quantity in light years.
      typedef quantity<light_year_unit> light_year_quantity;

      /// Unit definition for parsec length.
      typedef boost::units::astronomical::parsec_base_unit::unit_type parsec_unit;
      /// Numeric constant for parsec.
      BOOST_UNITS_STATIC_CONSTANT(parsec, parsec_unit);
      /// Numeric constant for parsec.
      BOOST_UNITS_STATIC_CONSTANT(parsecs, parsec_unit);
      /// Measured quantity in parsecs.
      typedef quantity<parsec_unit> parsec_quantity;

      /// Unit definition for point length.
      typedef scaled_base_unit<boost::units::imperial::inch_base_unit, scale<72, static_rational<-1> > >::unit_type point_unit;
      /// Numeric constant for point.
      BOOST_UNITS_STATIC_CONSTANT(point, point_unit);
      /// Numeric constant for point.
      BOOST_UNITS_STATIC_CONSTANT(points, point_unit);
      /// Measured quantity in points.
      typedef quantity<point_unit> point_quantity;

      /// Base unit for pixel length.
      struct pixel_base_unit : boost::units::base_unit<pixel_base_unit,
                                                       boost::units::length_dimension, 1> { };
      /// Unit system for pixel length.
      typedef boost::units::make_system<
        pixel_base_unit>::type pixel_system;
      /// Unit definition for pixel length (undefined length, not convertible to other length units).
      typedef boost::units::unit<boost::units::length_dimension, pixel_system> pixel_unit;
      /// Numeric constant for pixel.
      BOOST_UNITS_STATIC_CONSTANT(pixel, pixel_unit);
      /// Numeric constant for pixel.
      BOOST_UNITS_STATIC_CONSTANT(pixels, pixel_unit);
      /// Measured quantity in pixels.
      typedef quantity<pixel_unit> pixel_quantity;

      /// Base unit for reference frame.
      struct reference_frame_base_unit : boost::units::base_unit<reference_frame_base_unit,
                                                                 boost::units::length_dimension, 2> { };
      /// Unit system for reference frame length.
      typedef boost::units::make_system<
        reference_frame_base_unit>::type reference_frame_system;
      /// Unit definition for reference frame unit length (undefined unit length, not convertible to other length units).
      typedef boost::units::unit<boost::units::length_dimension, reference_frame_system> reference_frame_unit;
      /// Numeric constant for reference frame unit.
      BOOST_UNITS_STATIC_CONSTANT(reference_frame, reference_frame_unit);
      /// Measured quantity in reference frame units.
      typedef quantity<reference_frame_unit> reference_frame_quantity;

      // Power types, constants and quantities.

      /// Unit definition for power.
      typedef si::power power_unit;
      /// Measured quantity (power, watts).
      typedef quantity<si::power> power_quantity;

      /// Unit definition for yoctowatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<-24> > >::type yoctowatt_unit;
      /// Unit definition for zeptowatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<-21> > >::type zeptowatt_unit;
      /// Unit definition for attowatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<-18> > >::type attowatt_unit;
      /// Unit definition for femtowatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<-15> > >::type femtowatt_unit;
      /// Unit definition for picowatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<-12> > >::type picowatt_unit;
      /// Unit definition for nanowatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational< -9> > >::type nanowatt_unit;
      /// Unit definition for microwatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational< -6> > >::type microwatt_unit;
      /// Unit definition for milliwatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational< -3> > >::type milliwatt_unit;
      /// Unit definition for centiwatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational< -2> > >::type centiwatt_unit;
      /// Unit definition for deciwatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational< -1> > >::type deciwatt_unit;
      /// Unit definition for watt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<  0> > >::type watt_unit;
      /// Unit definition for dekawatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<  1> > >::type dekawatt_unit;
      /// Unit definition for decawatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<  1> > >::type decawatt_unit;
      /// Unit definition for hectowatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<  2> > >::type hectowatt_unit;
      /// Unit definition for kilowatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<  3> > >::type kilowatt_unit;
      /// Unit definition for megawatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<  6> > >::type megawatt_unit;
      /// Unit definition for gigawatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational<  9> > >::type gigawatt_unit;
      /// Unit definition for terawatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational< 12> > >::type terawatt_unit;
      /// Unit definition for petawatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational< 15> > >::type petawatt_unit;
      /// Unit definition for exawatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational< 18> > >::type exawatt_unit;
      /// Unit definition for zettawatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational< 21> > >::type zettawatt_unit;
      /// Unit definition for yottawatt power.
      typedef make_scaled_unit<si::power,scale<10,static_rational< 24> > >::type yottawatt_unit;

      /// Numeric constant for yoctowatt.
      BOOST_UNITS_STATIC_CONSTANT(yoctowatt, yoctowatt_unit);
      /// Numeric constant for zeptowatt.
      BOOST_UNITS_STATIC_CONSTANT(zeptowatt, zeptowatt_unit);
      /// Numeric constant for attowatt.
      BOOST_UNITS_STATIC_CONSTANT(attowatt, attowatt_unit);
      /// Numeric constant for femtowatt.
      BOOST_UNITS_STATIC_CONSTANT(femtowatt, femtowatt_unit);
      /// Numeric constant for picowatt.
      BOOST_UNITS_STATIC_CONSTANT(picowatt, picowatt_unit);
      /// Numeric constant for nanowatt.
      BOOST_UNITS_STATIC_CONSTANT(nanowatt, nanowatt_unit);
      /// Numeric constant for microwatt.
      BOOST_UNITS_STATIC_CONSTANT(microwatt, microwatt_unit);
      /// Numeric constant for milliwatt.
      BOOST_UNITS_STATIC_CONSTANT(milliwatt, milliwatt_unit);
      /// Numeric constant for centiwatt.
      BOOST_UNITS_STATIC_CONSTANT(centiwatt, centiwatt_unit);
      /// Numeric constant for deciwatt.
      BOOST_UNITS_STATIC_CONSTANT(deciwatt, deciwatt_unit);
      /// Numeric constant for watt.
      BOOST_UNITS_STATIC_CONSTANT(watt, watt_unit);
      /// Numeric constant for dekawatt.
      BOOST_UNITS_STATIC_CONSTANT(dekawatt, dekawatt_unit);
      /// Numeric constant for decawatt.
      BOOST_UNITS_STATIC_CONSTANT(decawatt, decawatt_unit);
      /// Numeric constant for hectowatt.
      BOOST_UNITS_STATIC_CONSTANT(hectowatt, hectowatt_unit);
      /// Numeric constant for kilowatt.
      BOOST_UNITS_STATIC_CONSTANT(kilowatt, kilowatt_unit);
      /// Numeric constant for megawatt.
      BOOST_UNITS_STATIC_CONSTANT(megawatt, megawatt_unit);
      /// Numeric constant for gigawatt.
      BOOST_UNITS_STATIC_CONSTANT(gigawatt, gigawatt_unit);
      /// Numeric constant for terawatt.
      BOOST_UNITS_STATIC_CONSTANT(terawatt, terawatt_unit);
      /// Numeric constant for petawatt.
      BOOST_UNITS_STATIC_CONSTANT(petawatt, petawatt_unit);
      /// Numeric constant for exawatt.
      BOOST_UNITS_STATIC_CONSTANT(exawatt, exawatt_unit);
      /// Numeric constant for zettawatt.
      BOOST_UNITS_STATIC_CONSTANT(zettawatt, zettawatt_unit);
      /// Numeric constant for yottawatt.
      BOOST_UNITS_STATIC_CONSTANT(yottawatt, yottawatt_unit);

      /// Numeric constant for yoctowatt.
      BOOST_UNITS_STATIC_CONSTANT(yoctowatts, yoctowatt_unit);
      /// Numeric constant for zeptowatt.
      BOOST_UNITS_STATIC_CONSTANT(zeptowatts, zeptowatt_unit);
      /// Numeric constant for attowatt.
      BOOST_UNITS_STATIC_CONSTANT(attowatts, attowatt_unit);
      /// Numeric constant for femtowatt.
      BOOST_UNITS_STATIC_CONSTANT(femtowatts, femtowatt_unit);
      /// Numeric constant for picowatt.
      BOOST_UNITS_STATIC_CONSTANT(picowatts, picowatt_unit);
      /// Numeric constant for nanowatt.
      BOOST_UNITS_STATIC_CONSTANT(nanowatts, nanowatt_unit);
      /// Numeric constant for microwatt.
      BOOST_UNITS_STATIC_CONSTANT(microwatts, microwatt_unit);
      /// Numeric constant for milliwatt.
      BOOST_UNITS_STATIC_CONSTANT(milliwatts, milliwatt_unit);
      /// Numeric constant for centiwatt.
      BOOST_UNITS_STATIC_CONSTANT(centiwatts, centiwatt_unit);
      /// Numeric constant for deciwatt.
      BOOST_UNITS_STATIC_CONSTANT(deciwatts, deciwatt_unit);
      /// Numeric constant for watt.
      BOOST_UNITS_STATIC_CONSTANT(watts, watt_unit);
      /// Numeric constant for dekawatt.
      BOOST_UNITS_STATIC_CONSTANT(dekawatts, dekawatt_unit);
      /// Numeric constant for decawatt.
      BOOST_UNITS_STATIC_CONSTANT(decawatts, decawatt_unit);
      /// Numeric constant for hectowatt.
      BOOST_UNITS_STATIC_CONSTANT(hectowatts, hectowatt_unit);
      /// Numeric constant for kilowatt.
      BOOST_UNITS_STATIC_CONSTANT(kilowatts, kilowatt_unit);
      /// Numeric constant for megawatt.
      BOOST_UNITS_STATIC_CONSTANT(megawatts, megawatt_unit);
      /// Numeric constant for gigawatt.
      BOOST_UNITS_STATIC_CONSTANT(gigawatts, gigawatt_unit);
      /// Numeric constant for terawatt.
      BOOST_UNITS_STATIC_CONSTANT(terawatts, terawatt_unit);
      /// Numeric constant for petawatt.
      BOOST_UNITS_STATIC_CONSTANT(petawatts, petawatt_unit);
      /// Numeric constant for exawatt.
      BOOST_UNITS_STATIC_CONSTANT(exawatts, exawatt_unit);
      /// Numeric constant for zettawatt.
      BOOST_UNITS_STATIC_CONSTANT(zettawatts, zettawatt_unit);
      /// Numeric constant for yottawatt.
      BOOST_UNITS_STATIC_CONSTANT(yottawatts, yottawatt_unit);

      /// Measured quantity in yoctowatts.
      typedef quantity<yoctowatt_unit> yoctowatt_quantity;
      /// Measured quantity in zeptowatts.
      typedef quantity<zeptowatt_unit> zeptowatt_quantity;
      /// Measured quantity in attowatts.
      typedef quantity<attowatt_unit> attowatt_quantity;
      /// Measured quantity in femtowatts.
      typedef quantity<femtowatt_unit> femtowatt_quantity;
      /// Measured quantity in picowatts.
      typedef quantity<picowatt_unit> picowatt_quantity;
      /// Measured quantity in nanowatts.
      typedef quantity<nanowatt_unit> nanowatt_quantity;
      /// Measured quantity in microwatts.
      typedef quantity<microwatt_unit> microwatt_quantity;
      /// Measured quantity in milliwatts.
      typedef quantity<milliwatt_unit> milliwatt_quantity;
      /// Measured quantity in centiwatts.
      typedef quantity<centiwatt_unit> centiwatt_quantity;
      /// Measured quantity in deciwatts.
      typedef quantity<deciwatt_unit> deciwatt_quantity;
      /// Measured quantity in watts.
      typedef quantity<watt_unit> watt_quantity;
      /// Measured quantity in dekawatts.
      typedef quantity<dekawatt_unit> dekawatt_quantity;
      /// Measured quantity in decawatts.
      typedef quantity<decawatt_unit> decawatt_quantity;
      /// Measured quantity in hectowatts.
      typedef quantity<hectowatt_unit> hectowatt_quantity;
      /// Measured quantity in kilowatts.
      typedef quantity<kilowatt_unit> kilowatt_quantity;
      /// Measured quantity in megawatts.
      typedef quantity<megawatt_unit> megawatt_quantity;
      /// Measured quantity in gigawatts.
      typedef quantity<gigawatt_unit> gigawatt_quantity;
      /// Measured quantity in terawatts.
      typedef quantity<terawatt_unit> terawatt_quantity;
      /// Measured quantity in petawatts.
      typedef quantity<petawatt_unit> petawatt_quantity;
      /// Measured quantity in exawatts.
      typedef quantity<exawatt_unit> exawatt_quantity;
      /// Measured quantity in zettawatts.
      typedef quantity<zettawatt_unit> zettawatt_quantity;
      /// Measured quantity in yottawatts.
      typedef quantity<yottawatt_unit> yottawatt_quantity;

      // Pressure types, constants and quantities.

      /// Unit definition for pressure.
      typedef si::pressure pressure_unit;
      /// Measured quantity (pressure, pascals).
      typedef quantity<si::pressure> pressure_quantity;

      /// Unit definition for yoctopascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<-24> > >::type yoctopascal_unit;
      /// Unit definition for zeptopascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<-21> > >::type zeptopascal_unit;
      /// Unit definition for attopascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<-18> > >::type attopascal_unit;
      /// Unit definition for femtopascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<-15> > >::type femtopascal_unit;
      /// Unit definition for picopascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<-12> > >::type picopascal_unit;
      /// Unit definition for nanopascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational< -9> > >::type nanopascal_unit;
      /// Unit definition for micropascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational< -6> > >::type micropascal_unit;
      /// Unit definition for millipascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational< -3> > >::type millipascal_unit;
      /// Unit definition for centipascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational< -2> > >::type centipascal_unit;
      /// Unit definition for decipascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational< -1> > >::type decipascal_unit;
      /// Unit definition for pascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<  0> > >::type pascal_unit;
      /// Unit definition for dekapascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<  1> > >::type dekapascal_unit;
      /// Unit definition for decapascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<  1> > >::type decapascal_unit;
      /// Unit definition for hectopascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<  2> > >::type hectopascal_unit;
      /// Unit definition for kilopascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<  3> > >::type kilopascal_unit;
      /// Unit definition for megapascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<  6> > >::type megapascal_unit;
      /// Unit definition for gigapascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational<  9> > >::type gigapascal_unit;
      /// Unit definition for terapascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational< 12> > >::type terapascal_unit;
      /// Unit definition for petapascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational< 15> > >::type petapascal_unit;
      /// Unit definition for exapascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational< 18> > >::type exapascal_unit;
      /// Unit definition for zettapascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational< 21> > >::type zettapascal_unit;
      /// Unit definition for yottapascal pressure.
      typedef make_scaled_unit<si::pressure,scale<10,static_rational< 24> > >::type yottapascal_unit;

      /// Numeric constant for yoctopascal.
      BOOST_UNITS_STATIC_CONSTANT(yoctopascal, yoctopascal_unit);
      /// Numeric constant for zeptopascal.
      BOOST_UNITS_STATIC_CONSTANT(zeptopascal, zeptopascal_unit);
      /// Numeric constant for attopascal.
      BOOST_UNITS_STATIC_CONSTANT(attopascal, attopascal_unit);
      /// Numeric constant for femtopascal.
      BOOST_UNITS_STATIC_CONSTANT(femtopascal, femtopascal_unit);
      /// Numeric constant for picopascal.
      BOOST_UNITS_STATIC_CONSTANT(picopascal, picopascal_unit);
      /// Numeric constant for nanopascal.
      BOOST_UNITS_STATIC_CONSTANT(nanopascal, nanopascal_unit);
      /// Numeric constant for micropascal.
      BOOST_UNITS_STATIC_CONSTANT(micropascal, micropascal_unit);
      /// Numeric constant for millipascal.
      BOOST_UNITS_STATIC_CONSTANT(millipascal, millipascal_unit);
      /// Numeric constant for centipascal.
      BOOST_UNITS_STATIC_CONSTANT(centipascal, centipascal_unit);
      /// Numeric constant for decipascal.
      BOOST_UNITS_STATIC_CONSTANT(decipascal, decipascal_unit);
      /// Numeric constant for pascal.
      BOOST_UNITS_STATIC_CONSTANT(pascal, pascal_unit);
      /// Numeric constant for dekapascal.
      BOOST_UNITS_STATIC_CONSTANT(dekapascal, dekapascal_unit);
      /// Numeric constant for decapascal.
      BOOST_UNITS_STATIC_CONSTANT(decapascal, decapascal_unit);
      /// Numeric constant for hectopascal.
      BOOST_UNITS_STATIC_CONSTANT(hectopascal, hectopascal_unit);
      /// Numeric constant for kilopascal.
      BOOST_UNITS_STATIC_CONSTANT(kilopascal, kilopascal_unit);
      /// Numeric constant for megapascal.
      BOOST_UNITS_STATIC_CONSTANT(megapascal, megapascal_unit);
      /// Numeric constant for gigapascal.
      BOOST_UNITS_STATIC_CONSTANT(gigapascal, gigapascal_unit);
      /// Numeric constant for terapascal.
      BOOST_UNITS_STATIC_CONSTANT(terapascal, terapascal_unit);
      /// Numeric constant for petapascal.
      BOOST_UNITS_STATIC_CONSTANT(petapascal, petapascal_unit);
      /// Numeric constant for exapascal.
      BOOST_UNITS_STATIC_CONSTANT(exapascal, exapascal_unit);
      /// Numeric constant for zettapascal.
      BOOST_UNITS_STATIC_CONSTANT(zettapascal, zettapascal_unit);
      /// Numeric constant for yottapascal.
      BOOST_UNITS_STATIC_CONSTANT(yottapascal, yottapascal_unit);

      /// Numeric constant for yoctopascal.
      BOOST_UNITS_STATIC_CONSTANT(yoctopascals, yoctopascal_unit);
      /// Numeric constant for zeptopascal.
      BOOST_UNITS_STATIC_CONSTANT(zeptopascals, zeptopascal_unit);
      /// Numeric constant for attopascal.
      BOOST_UNITS_STATIC_CONSTANT(attopascals, attopascal_unit);
      /// Numeric constant for femtopascal.
      BOOST_UNITS_STATIC_CONSTANT(femtopascals, femtopascal_unit);
      /// Numeric constant for picopascal.
      BOOST_UNITS_STATIC_CONSTANT(picopascals, picopascal_unit);
      /// Numeric constant for nanopascal.
      BOOST_UNITS_STATIC_CONSTANT(nanopascals, nanopascal_unit);
      /// Numeric constant for micropascal.
      BOOST_UNITS_STATIC_CONSTANT(micropascals, micropascal_unit);
      /// Numeric constant for millipascal.
      BOOST_UNITS_STATIC_CONSTANT(millipascals, millipascal_unit);
      /// Numeric constant for centipascal.
      BOOST_UNITS_STATIC_CONSTANT(centipascals, centipascal_unit);
      /// Numeric constant for decipascal.
      BOOST_UNITS_STATIC_CONSTANT(decipascals, decipascal_unit);
      /// Numeric constant for pascal.
      BOOST_UNITS_STATIC_CONSTANT(pascals, pascal_unit);
      /// Numeric constant for dekapascal.
      BOOST_UNITS_STATIC_CONSTANT(dekapascals, dekapascal_unit);
      /// Numeric constant for decapascal.
      BOOST_UNITS_STATIC_CONSTANT(decapascals, decapascal_unit);
      /// Numeric constant for hectopascal.
      BOOST_UNITS_STATIC_CONSTANT(hectopascals, hectopascal_unit);
      /// Numeric constant for kilopascal.
      BOOST_UNITS_STATIC_CONSTANT(kilopascals, kilopascal_unit);
      /// Numeric constant for megapascal.
      BOOST_UNITS_STATIC_CONSTANT(megapascals, megapascal_unit);
      /// Numeric constant for gigapascal.
      BOOST_UNITS_STATIC_CONSTANT(gigapascals, gigapascal_unit);
      /// Numeric constant for terapascal.
      BOOST_UNITS_STATIC_CONSTANT(terapascals, terapascal_unit);
      /// Numeric constant for petapascal.
      BOOST_UNITS_STATIC_CONSTANT(petapascals, petapascal_unit);
      /// Numeric constant for exapascal.
      BOOST_UNITS_STATIC_CONSTANT(exapascals, exapascal_unit);
      /// Numeric constant for zettapascal.
      BOOST_UNITS_STATIC_CONSTANT(zettapascals, zettapascal_unit);
      /// Numeric constant for yottapascal.
      BOOST_UNITS_STATIC_CONSTANT(yottapascals, yottapascal_unit);

      /// Measured quantity in yoctopascals.
      typedef quantity<yoctopascal_unit> yoctopascal_quantity;
      /// Measured quantity in zeptopascals.
      typedef quantity<zeptopascal_unit> zeptopascal_quantity;
      /// Measured quantity in attopascals.
      typedef quantity<attopascal_unit> attopascal_quantity;
      /// Measured quantity in femtopascals.
      typedef quantity<femtopascal_unit> femtopascal_quantity;
      /// Measured quantity in picopascals.
      typedef quantity<picopascal_unit> picopascal_quantity;
      /// Measured quantity in nanopascals.
      typedef quantity<nanopascal_unit> nanopascal_quantity;
      /// Measured quantity in micropascals.
      typedef quantity<micropascal_unit> micropascal_quantity;
      /// Measured quantity in millipascals.
      typedef quantity<millipascal_unit> millipascal_quantity;
      /// Measured quantity in centipascals.
      typedef quantity<centipascal_unit> centipascal_quantity;
      /// Measured quantity in decipascals.
      typedef quantity<decipascal_unit> decipascal_quantity;
      /// Measured quantity in pascals.
      typedef quantity<pascal_unit> pascal_quantity;
      /// Measured quantity in dekapascals.
      typedef quantity<dekapascal_unit> dekapascal_quantity;
      /// Measured quantity in decapascals.
      typedef quantity<decapascal_unit> decapascal_quantity;
      /// Measured quantity in hectopascals.
      typedef quantity<hectopascal_unit> hectopascal_quantity;
      /// Measured quantity in kilopascals.
      typedef quantity<kilopascal_unit> kilopascal_quantity;
      /// Measured quantity in megapascals.
      typedef quantity<megapascal_unit> megapascal_quantity;
      /// Measured quantity in gigapascals.
      typedef quantity<gigapascal_unit> gigapascal_quantity;
      /// Measured quantity in terapascals.
      typedef quantity<terapascal_unit> terapascal_quantity;
      /// Measured quantity in petapascals.
      typedef quantity<petapascal_unit> petapascal_quantity;
      /// Measured quantity in exapascals.
      typedef quantity<exapascal_unit> exapascal_quantity;
      /// Measured quantity in zettapascals.
      typedef quantity<zettapascal_unit> zettapascal_quantity;
      /// Measured quantity in yottapascals.
      typedef quantity<yottapascal_unit> yottapascal_quantity;

      /// Unit definition for millibar pressure.
      typedef scaled_base_unit<boost::units::metric::bar_base_unit,scale<10,static_rational< -3> > >::unit_type millibar_unit;
      /// Unit definition for centibar pressure.
      typedef scaled_base_unit<boost::units::metric::bar_base_unit,scale<10,static_rational< -2> > >::unit_type centibar_unit;
      /// Unit definition for decibar pressure.
      typedef scaled_base_unit<boost::units::metric::bar_base_unit,scale<10,static_rational< -1> > >::unit_type decibar_unit;
      /// Unit definition for bar pressure.
      typedef scaled_base_unit<boost::units::metric::bar_base_unit,scale<10,static_rational<  0> > >::unit_type bar_unit;
      /// Unit definition for dekabar pressure.
      typedef scaled_base_unit<boost::units::metric::bar_base_unit,scale<10,static_rational<  1> > >::unit_type dekabar_unit;
      /// Unit definition for decabar pressure.
      typedef scaled_base_unit<boost::units::metric::bar_base_unit,scale<10,static_rational<  1> > >::unit_type decabar_unit;
      /// Unit definition for hectobar pressure.
      typedef scaled_base_unit<boost::units::metric::bar_base_unit,scale<10,static_rational<  2> > >::unit_type hectobar_unit;
      /// Unit definition for kilobar pressure.
      typedef scaled_base_unit<boost::units::metric::bar_base_unit,scale<10,static_rational<  3> > >::unit_type kilobar_unit;
      /// Unit definition for megabar pressure.
      typedef scaled_base_unit<boost::units::metric::bar_base_unit,scale<10,static_rational<  6> > >::unit_type megabar_unit;

      /// Numeric constant for millibar.
      BOOST_UNITS_STATIC_CONSTANT(millibar, millibar_unit);
      /// Numeric constant for centibar.
      BOOST_UNITS_STATIC_CONSTANT(centibar, centibar_unit);
      /// Numeric constant for decibar.
      BOOST_UNITS_STATIC_CONSTANT(decibar, decibar_unit);
      /// Numeric constant for bar.
      BOOST_UNITS_STATIC_CONSTANT(bar, bar_unit);
      /// Numeric constant for dekabar.
      BOOST_UNITS_STATIC_CONSTANT(dekabar, dekabar_unit);
      /// Numeric constant for decabar.
      BOOST_UNITS_STATIC_CONSTANT(decabar, decabar_unit);
      /// Numeric constant for hectobar.
      BOOST_UNITS_STATIC_CONSTANT(hectobar, hectobar_unit);
      /// Numeric constant for kilobar.
      BOOST_UNITS_STATIC_CONSTANT(kilobar, kilobar_unit);
      /// Numeric constant for megabar.
      BOOST_UNITS_STATIC_CONSTANT(megabar, megabar_unit);

      /// Numeric constant for millibar.
      BOOST_UNITS_STATIC_CONSTANT(millibars, millibar_unit);
      /// Numeric constant for centibar.
      BOOST_UNITS_STATIC_CONSTANT(centibars, centibar_unit);
      /// Numeric constant for decibar.
      BOOST_UNITS_STATIC_CONSTANT(decibars, decibar_unit);
      /// Numeric constant for bar.
      BOOST_UNITS_STATIC_CONSTANT(bars, bar_unit);
      /// Numeric constant for dekabar.
      BOOST_UNITS_STATIC_CONSTANT(dekabars, dekabar_unit);
      /// Numeric constant for decabar.
      BOOST_UNITS_STATIC_CONSTANT(decabars, decabar_unit);
      /// Numeric constant for hectobar.
      BOOST_UNITS_STATIC_CONSTANT(hectobars, hectobar_unit);
      /// Numeric constant for kilobar.
      BOOST_UNITS_STATIC_CONSTANT(kilobars, kilobar_unit);
      /// Numeric constant for megabar.
      BOOST_UNITS_STATIC_CONSTANT(megabars, megabar_unit);

      /// Measured quantity in millibars.
      typedef quantity<millibar_unit> millibar_quantity;
      /// Measured quantity in centibars.
      typedef quantity<centibar_unit> centibar_quantity;
      /// Measured quantity in decibars.
      typedef quantity<decibar_unit> decibar_quantity;
      /// Measured quantity in bars.
      typedef quantity<bar_unit> bar_quantity;
      /// Measured quantity in dekabars.
      typedef quantity<dekabar_unit> dekabar_quantity;
      /// Measured quantity in decabars.
      typedef quantity<decabar_unit> decabar_quantity;
      /// Measured quantity in hectobars.
      typedef quantity<hectobar_unit> hectobar_quantity;
      /// Measured quantity in kilobars.
      typedef quantity<kilobar_unit> kilobar_quantity;
      /// Measured quantity in megabars.
      typedef quantity<megabar_unit> megabar_quantity;

      /// Unit definition for atmosphere pressure.
      typedef boost::units::metric::atmosphere_base_unit::unit_type atmosphere_unit;
      /// Numeric constant for atmosphere.
      BOOST_UNITS_STATIC_CONSTANT(atmosphere, atmosphere_unit);
      /// Numeric constant for atmosphere.
      BOOST_UNITS_STATIC_CONSTANT(atmospheres, atmosphere_unit);
      /// Measured quantity in atmospheres.
      typedef quantity<atmosphere_unit> atmosphere_quantity;

      /// Base unit for pound-force per square inch (psi) pressure.
      struct psi_base_unit : boost::units::base_unit<psi_base_unit,
                                                     boost::units::pressure_dimension, 3> { };
      /// Unit definition for pound-force per square inch (psi) pressure.
      typedef psi_base_unit::unit_type psi_unit;
      /// Numeric constant for pound-force per square inch (psi).
      BOOST_UNITS_STATIC_CONSTANT(psi, psi_unit);
      /// Measured quantity in pound-force per square inch (psi).
      typedef quantity<psi_unit> psi_quantity;

      /// Unit definition for Torr pressure.
      typedef boost::units::metric::torr_base_unit::unit_type torr_unit;
      /// Numeric constant for Torr.
      BOOST_UNITS_STATIC_CONSTANT(torr, torr_unit);
      /// Measured quantity in Torrs.
      typedef quantity<torr_unit> torr_quantity;

      /// Unit definition for milliTorr pressure.
      typedef scaled_base_unit<boost::units::metric::torr_base_unit,scale<10,static_rational< -3> > >::unit_type millitorr_unit;
      /// Numeric constant for milliTorr.
      BOOST_UNITS_STATIC_CONSTANT(millitorr, millitorr_unit);
      /// Measured quantity in milliTorrs.
      typedef quantity<millitorr_unit> millitorr_quantity;

      /// Unit definition for mmHg pressure.
      typedef boost::units::metric::mmHg_base_unit::unit_type mmHg_unit;
      /// Numeric constant for mmHg.
      BOOST_UNITS_STATIC_CONSTANT(mmHg, mmHg_unit);
      /// Measured quantity in mmHg.
      typedef quantity<mmHg_unit> mmHg_quantity;

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
      typedef absolute<kelvin_unit> kelvin_absolute_unit;
      /// Numeric constant for absolute kelvin.
      BOOST_UNITS_STATIC_CONSTANT(kelvin_absolute, kelvin_absolute_unit);
      /// Numeric constant for absolute kelvin.
      BOOST_UNITS_STATIC_CONSTANT(kelvins_absolute, kelvin_absolute_unit);
      /// Measured quantity in absolute kelvin.
      typedef quantity<kelvin_absolute_unit> kelvin_absolute_quantity;

      /// Unit definition for absolute temperature in Celsius.
      typedef absolute<celsius_unit> celsius_absolute_unit;
      /// Numeric constant for absolute Celsius.
      BOOST_UNITS_STATIC_CONSTANT(celsius_absolute, celsius_absolute_unit);
      /// Measured quantity in absolute Celsius.
      typedef quantity<celsius_absolute_unit> celsius_absolute_quantity;

      /// Unit definition for absolute temperature in Fahrenheit.
      typedef absolute<fahrenheit_unit> fahrenheit_absolute_unit;
      /// Numeric constant for absolute Fahrenheit
      BOOST_UNITS_STATIC_CONSTANT(fahrenheit_absolute, fahrenheit_absolute_unit);
      /// Measured quantity in absolute Fahrenheit
      typedef quantity<fahrenheit_absolute_unit> fahrenheit_absolute_quantity;

      /// Unit definition for absolute temperature in Rankine.
      typedef absolute<rankine_unit> rankine_absolute_unit;
      /// Numeric constant for absolute Rankine.
      BOOST_UNITS_STATIC_CONSTANT(rankine_absolute, rankine_absolute_unit);
      /// Measured quantity in absolute Rankine.
      typedef quantity<rankine_absolute_unit> rankine_absolute_quantity;

      // Time types, constants and quantities.

      /// Unit definition for time.
      typedef si::time time_unit;
      /// Measured quantity (time, seconds).
      typedef quantity<si::time> time_quantity;

      /// Unit definition for yoctosecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<-24> > >::type yoctosecond_unit;
      /// Unit definition for zeptosecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<-21> > >::type zeptosecond_unit;
      /// Unit definition for attosecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<-18> > >::type attosecond_unit;
      /// Unit definition for femtosecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<-15> > >::type femtosecond_unit;
      /// Unit definition for picosecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<-12> > >::type picosecond_unit;
      /// Unit definition for nanosecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational< -9> > >::type nanosecond_unit;
      /// Unit definition for microsecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational< -6> > >::type microsecond_unit;
      /// Unit definition for millisecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational< -3> > >::type millisecond_unit;
      /// Unit definition for centisecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational< -2> > >::type centisecond_unit;
      /// Unit definition for decisecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational< -1> > >::type decisecond_unit;
      /// Unit definition for second time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<  0> > >::type second_unit;
      /// Unit definition for dekasecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<  1> > >::type dekasecond_unit;
      /// Unit definition for decasecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<  1> > >::type decasecond_unit;
      /// Unit definition for hectosecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<  2> > >::type hectosecond_unit;
      /// Unit definition for kilosecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<  3> > >::type kilosecond_unit;
      /// Unit definition for megasecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<  6> > >::type megasecond_unit;
      /// Unit definition for gigasecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational<  9> > >::type gigasecond_unit;
      /// Unit definition for terasecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational< 12> > >::type terasecond_unit;
      /// Unit definition for petasecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational< 15> > >::type petasecond_unit;
      /// Unit definition for exasecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational< 18> > >::type exasecond_unit;
      /// Unit definition for zettasecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational< 21> > >::type zettasecond_unit;
      /// Unit definition for yottasecond time.
      typedef make_scaled_unit<si::time,scale<10,static_rational< 24> > >::type yottasecond_unit;

      /// Numeric constant for yoctosecond.
      BOOST_UNITS_STATIC_CONSTANT(yoctosecond, yoctosecond_unit);
      /// Numeric constant for zeptosecond.
      BOOST_UNITS_STATIC_CONSTANT(zeptosecond, zeptosecond_unit);
      /// Numeric constant for attosecond.
      BOOST_UNITS_STATIC_CONSTANT(attosecond, attosecond_unit);
      /// Numeric constant for femtosecond.
      BOOST_UNITS_STATIC_CONSTANT(femtosecond, femtosecond_unit);
      /// Numeric constant for picosecond.
      BOOST_UNITS_STATIC_CONSTANT(picosecond, picosecond_unit);
      /// Numeric constant for nanosecond.
      BOOST_UNITS_STATIC_CONSTANT(nanosecond, nanosecond_unit);
      /// Numeric constant for microsecond.
      BOOST_UNITS_STATIC_CONSTANT(microsecond, microsecond_unit);
      /// Numeric constant for millisecond.
      BOOST_UNITS_STATIC_CONSTANT(millisecond, millisecond_unit);
      /// Numeric constant for centisecond.
      BOOST_UNITS_STATIC_CONSTANT(centisecond, centisecond_unit);
      /// Numeric constant for decisecond.
      BOOST_UNITS_STATIC_CONSTANT(decisecond, decisecond_unit);
      /// Numeric constant for second.
      BOOST_UNITS_STATIC_CONSTANT(second, second_unit);
      /// Numeric constant for dekasecond.
      BOOST_UNITS_STATIC_CONSTANT(dekasecond, dekasecond_unit);
      /// Numeric constant for decasecond.
      BOOST_UNITS_STATIC_CONSTANT(decasecond, decasecond_unit);
      /// Numeric constant for hectosecond.
      BOOST_UNITS_STATIC_CONSTANT(hectosecond, hectosecond_unit);
      /// Numeric constant for kilosecond.
      BOOST_UNITS_STATIC_CONSTANT(kilosecond, kilosecond_unit);
      /// Numeric constant for megasecond.
      BOOST_UNITS_STATIC_CONSTANT(megasecond, megasecond_unit);
      /// Numeric constant for gigasecond.
      BOOST_UNITS_STATIC_CONSTANT(gigasecond, gigasecond_unit);
      /// Numeric constant for terasecond.
      BOOST_UNITS_STATIC_CONSTANT(terasecond, terasecond_unit);
      /// Numeric constant for petasecond.
      BOOST_UNITS_STATIC_CONSTANT(petasecond, petasecond_unit);
      /// Numeric constant for exasecond.
      BOOST_UNITS_STATIC_CONSTANT(exasecond, exasecond_unit);
      /// Numeric constant for zettasecond.
      BOOST_UNITS_STATIC_CONSTANT(zettasecond, zettasecond_unit);
      /// Numeric constant for yottasecond.
      BOOST_UNITS_STATIC_CONSTANT(yottasecond, yottasecond_unit);

      /// Numeric constant for yoctosecond.
      BOOST_UNITS_STATIC_CONSTANT(yoctoseconds, yoctosecond_unit);
      /// Numeric constant for zeptosecond.
      BOOST_UNITS_STATIC_CONSTANT(zeptoseconds, zeptosecond_unit);
      /// Numeric constant for attosecond.
      BOOST_UNITS_STATIC_CONSTANT(attoseconds, attosecond_unit);
      /// Numeric constant for femtosecond.
      BOOST_UNITS_STATIC_CONSTANT(femtoseconds, femtosecond_unit);
      /// Numeric constant for picosecond.
      BOOST_UNITS_STATIC_CONSTANT(picoseconds, picosecond_unit);
      /// Numeric constant for nanosecond.
      BOOST_UNITS_STATIC_CONSTANT(nanoseconds, nanosecond_unit);
      /// Numeric constant for microsecond.
      BOOST_UNITS_STATIC_CONSTANT(microseconds, microsecond_unit);
      /// Numeric constant for millisecond.
      BOOST_UNITS_STATIC_CONSTANT(milliseconds, millisecond_unit);
      /// Numeric constant for centisecond.
      BOOST_UNITS_STATIC_CONSTANT(centiseconds, centisecond_unit);
      /// Numeric constant for decisecond.
      BOOST_UNITS_STATIC_CONSTANT(deciseconds, decisecond_unit);
      /// Numeric constant for second.
      BOOST_UNITS_STATIC_CONSTANT(seconds, second_unit);
      /// Numeric constant for dekasecond.
      BOOST_UNITS_STATIC_CONSTANT(dekaseconds, dekasecond_unit);
      /// Numeric constant for decasecond.
      BOOST_UNITS_STATIC_CONSTANT(decaseconds, decasecond_unit);
      /// Numeric constant for hectosecond.
      BOOST_UNITS_STATIC_CONSTANT(hectoseconds, hectosecond_unit);
      /// Numeric constant for kilosecond.
      BOOST_UNITS_STATIC_CONSTANT(kiloseconds, kilosecond_unit);
      /// Numeric constant for megasecond.
      BOOST_UNITS_STATIC_CONSTANT(megaseconds, megasecond_unit);
      /// Numeric constant for gigasecond.
      BOOST_UNITS_STATIC_CONSTANT(gigaseconds, gigasecond_unit);
      /// Numeric constant for terasecond.
      BOOST_UNITS_STATIC_CONSTANT(teraseconds, terasecond_unit);
      /// Numeric constant for petasecond.
      BOOST_UNITS_STATIC_CONSTANT(petaseconds, petasecond_unit);
      /// Numeric constant for exasecond.
      BOOST_UNITS_STATIC_CONSTANT(exaseconds, exasecond_unit);
      /// Numeric constant for zettasecond.
      BOOST_UNITS_STATIC_CONSTANT(zettaseconds, zettasecond_unit);
      /// Numeric constant for yottasecond.
      BOOST_UNITS_STATIC_CONSTANT(yottaseconds, yottasecond_unit);

      /// Measured quantity in yoctoseconds.
      typedef quantity<yoctosecond_unit> yoctosecond_quantity;
      /// Measured quantity in zeptoseconds.
      typedef quantity<zeptosecond_unit> zeptosecond_quantity;
      /// Measured quantity in attoseconds.
      typedef quantity<attosecond_unit> attosecond_quantity;
      /// Measured quantity in femtoseconds.
      typedef quantity<femtosecond_unit> femtosecond_quantity;
      /// Measured quantity in picoseconds.
      typedef quantity<picosecond_unit> picosecond_quantity;
      /// Measured quantity in nanoseconds.
      typedef quantity<nanosecond_unit> nanosecond_quantity;
      /// Measured quantity in microseconds.
      typedef quantity<microsecond_unit> microsecond_quantity;
      /// Measured quantity in milliseconds.
      typedef quantity<millisecond_unit> millisecond_quantity;
      /// Measured quantity in centiseconds.
      typedef quantity<centisecond_unit> centisecond_quantity;
      /// Measured quantity in deciseconds.
      typedef quantity<decisecond_unit> decisecond_quantity;
      /// Measured quantity in seconds.
      typedef quantity<second_unit> second_quantity;
      /// Measured quantity in dekaseconds.
      typedef quantity<dekasecond_unit> dekasecond_quantity;
      /// Measured quantity in decaseconds.
      typedef quantity<decasecond_unit> decasecond_quantity;
      /// Measured quantity in hectoseconds.
      typedef quantity<hectosecond_unit> hectosecond_quantity;
      /// Measured quantity in kiloseconds.
      typedef quantity<kilosecond_unit> kilosecond_quantity;
      /// Measured quantity in megaseconds.
      typedef quantity<megasecond_unit> megasecond_quantity;
      /// Measured quantity in gigaseconds.
      typedef quantity<gigasecond_unit> gigasecond_quantity;
      /// Measured quantity in teraseconds.
      typedef quantity<terasecond_unit> terasecond_quantity;
      /// Measured quantity in petaseconds.
      typedef quantity<petasecond_unit> petasecond_quantity;
      /// Measured quantity in exaseconds.
      typedef quantity<exasecond_unit> exasecond_quantity;
      /// Measured quantity in zettaseconds.
      typedef quantity<zettasecond_unit> zettasecond_quantity;
      /// Measured quantity in yottaseconds.
      typedef quantity<yottasecond_unit> yottasecond_quantity;

      // Electric potential types, constants and quantities.

      /// Unit definition for electric potential.
      typedef si::electric_potential electric_potential_unit;
      /// Measured quantity (electric potential, volts).
      typedef quantity<si::electric_potential> electric_potential_quantity;

      /// Unit definition for yoctovolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<-24> > >::type yoctovolt_unit;
      /// Unit definition for zeptovolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<-21> > >::type zeptovolt_unit;
      /// Unit definition for attovolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<-18> > >::type attovolt_unit;
      /// Unit definition for femtovolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<-15> > >::type femtovolt_unit;
      /// Unit definition for picovolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<-12> > >::type picovolt_unit;
      /// Unit definition for nanovolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational< -9> > >::type nanovolt_unit;
      /// Unit definition for microvolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational< -6> > >::type microvolt_unit;
      /// Unit definition for millivolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational< -3> > >::type millivolt_unit;
      /// Unit definition for centivolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational< -2> > >::type centivolt_unit;
      /// Unit definition for decivolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational< -1> > >::type decivolt_unit;
      /// Unit definition for volt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<  0> > >::type volt_unit;
      /// Unit definition for dekavolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<  1> > >::type dekavolt_unit;
      /// Unit definition for decavolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<  1> > >::type decavolt_unit;
      /// Unit definition for hectovolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<  2> > >::type hectovolt_unit;
      /// Unit definition for kilovolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<  3> > >::type kilovolt_unit;
      /// Unit definition for megavolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<  6> > >::type megavolt_unit;
      /// Unit definition for gigavolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational<  9> > >::type gigavolt_unit;
      /// Unit definition for teravolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational< 12> > >::type teravolt_unit;
      /// Unit definition for petavolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational< 15> > >::type petavolt_unit;
      /// Unit definition for exavolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational< 18> > >::type exavolt_unit;
      /// Unit definition for zettavolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational< 21> > >::type zettavolt_unit;
      /// Unit definition for yottavolt electric potential.
      typedef make_scaled_unit<si::electric_potential,scale<10,static_rational< 24> > >::type yottavolt_unit;

      /// Numeric constant for yoctovolt.
      BOOST_UNITS_STATIC_CONSTANT(yoctovolt, yoctovolt_unit);
      /// Numeric constant for zeptovolt.
      BOOST_UNITS_STATIC_CONSTANT(zeptovolt, zeptovolt_unit);
      /// Numeric constant for attovolt.
      BOOST_UNITS_STATIC_CONSTANT(attovolt, attovolt_unit);
      /// Numeric constant for femtovolt.
      BOOST_UNITS_STATIC_CONSTANT(femtovolt, femtovolt_unit);
      /// Numeric constant for picovolt.
      BOOST_UNITS_STATIC_CONSTANT(picovolt, picovolt_unit);
      /// Numeric constant for nanovolt.
      BOOST_UNITS_STATIC_CONSTANT(nanovolt, nanovolt_unit);
      /// Numeric constant for microvolt.
      BOOST_UNITS_STATIC_CONSTANT(microvolt, microvolt_unit);
      /// Numeric constant for millivolt.
      BOOST_UNITS_STATIC_CONSTANT(millivolt, millivolt_unit);
      /// Numeric constant for centivolt.
      BOOST_UNITS_STATIC_CONSTANT(centivolt, centivolt_unit);
      /// Numeric constant for decivolt.
      BOOST_UNITS_STATIC_CONSTANT(decivolt, decivolt_unit);
      /// Numeric constant for volt.
      BOOST_UNITS_STATIC_CONSTANT(volt, volt_unit);
      /// Numeric constant for dekavolt.
      BOOST_UNITS_STATIC_CONSTANT(dekavolt, dekavolt_unit);
      /// Numeric constant for decavolt.
      BOOST_UNITS_STATIC_CONSTANT(decavolt, decavolt_unit);
      /// Numeric constant for hectovolt.
      BOOST_UNITS_STATIC_CONSTANT(hectovolt, hectovolt_unit);
      /// Numeric constant for kilovolt.
      BOOST_UNITS_STATIC_CONSTANT(kilovolt, kilovolt_unit);
      /// Numeric constant for megavolt.
      BOOST_UNITS_STATIC_CONSTANT(megavolt, megavolt_unit);
      /// Numeric constant for gigavolt.
      BOOST_UNITS_STATIC_CONSTANT(gigavolt, gigavolt_unit);
      /// Numeric constant for teravolt.
      BOOST_UNITS_STATIC_CONSTANT(teravolt, teravolt_unit);
      /// Numeric constant for petavolt.
      BOOST_UNITS_STATIC_CONSTANT(petavolt, petavolt_unit);
      /// Numeric constant for exavolt.
      BOOST_UNITS_STATIC_CONSTANT(exavolt, exavolt_unit);
      /// Numeric constant for zettavolt.
      BOOST_UNITS_STATIC_CONSTANT(zettavolt, zettavolt_unit);
      /// Numeric constant for yottavolt.
      BOOST_UNITS_STATIC_CONSTANT(yottavolt, yottavolt_unit);

      /// Numeric constant for yoctovolt.
      BOOST_UNITS_STATIC_CONSTANT(yoctovolts, yoctovolt_unit);
      /// Numeric constant for zeptovolt.
      BOOST_UNITS_STATIC_CONSTANT(zeptovolts, zeptovolt_unit);
      /// Numeric constant for attovolt.
      BOOST_UNITS_STATIC_CONSTANT(attovolts, attovolt_unit);
      /// Numeric constant for femtovolt.
      BOOST_UNITS_STATIC_CONSTANT(femtovolts, femtovolt_unit);
      /// Numeric constant for picovolt.
      BOOST_UNITS_STATIC_CONSTANT(picovolts, picovolt_unit);
      /// Numeric constant for nanovolt.
      BOOST_UNITS_STATIC_CONSTANT(nanovolts, nanovolt_unit);
      /// Numeric constant for microvolt.
      BOOST_UNITS_STATIC_CONSTANT(microvolts, microvolt_unit);
      /// Numeric constant for millivolt.
      BOOST_UNITS_STATIC_CONSTANT(millivolts, millivolt_unit);
      /// Numeric constant for centivolt.
      BOOST_UNITS_STATIC_CONSTANT(centivolts, centivolt_unit);
      /// Numeric constant for decivolt.
      BOOST_UNITS_STATIC_CONSTANT(decivolts, decivolt_unit);
      /// Numeric constant for volt.
      BOOST_UNITS_STATIC_CONSTANT(volts, volt_unit);
      /// Numeric constant for dekavolt.
      BOOST_UNITS_STATIC_CONSTANT(dekavolts, dekavolt_unit);
      /// Numeric constant for decavolt.
      BOOST_UNITS_STATIC_CONSTANT(decavolts, decavolt_unit);
      /// Numeric constant for hectovolt.
      BOOST_UNITS_STATIC_CONSTANT(hectovolts, hectovolt_unit);
      /// Numeric constant for kilovolt.
      BOOST_UNITS_STATIC_CONSTANT(kilovolts, kilovolt_unit);
      /// Numeric constant for megavolt.
      BOOST_UNITS_STATIC_CONSTANT(megavolts, megavolt_unit);
      /// Numeric constant for gigavolt.
      BOOST_UNITS_STATIC_CONSTANT(gigavolts, gigavolt_unit);
      /// Numeric constant for teravolt.
      BOOST_UNITS_STATIC_CONSTANT(teravolts, teravolt_unit);
      /// Numeric constant for petavolt.
      BOOST_UNITS_STATIC_CONSTANT(petavolts, petavolt_unit);
      /// Numeric constant for exavolt.
      BOOST_UNITS_STATIC_CONSTANT(exavolts, exavolt_unit);
      /// Numeric constant for zettavolt.
      BOOST_UNITS_STATIC_CONSTANT(zettavolts, zettavolt_unit);
      /// Numeric constant for yottavolt.
      BOOST_UNITS_STATIC_CONSTANT(yottavolts, yottavolt_unit);

      /// Measured quantity in yoctovolts.
      typedef quantity<yoctovolt_unit> yoctovolt_quantity;
      /// Measured quantity in zeptovolts.
      typedef quantity<zeptovolt_unit> zeptovolt_quantity;
      /// Measured quantity in attovolts.
      typedef quantity<attovolt_unit> attovolt_quantity;
      /// Measured quantity in femtovolts.
      typedef quantity<femtovolt_unit> femtovolt_quantity;
      /// Measured quantity in picovolts.
      typedef quantity<picovolt_unit> picovolt_quantity;
      /// Measured quantity in nanovolts.
      typedef quantity<nanovolt_unit> nanovolt_quantity;
      /// Measured quantity in microvolts.
      typedef quantity<microvolt_unit> microvolt_quantity;
      /// Measured quantity in millivolts.
      typedef quantity<millivolt_unit> millivolt_quantity;
      /// Measured quantity in centivolts.
      typedef quantity<centivolt_unit> centivolt_quantity;
      /// Measured quantity in decivolts.
      typedef quantity<decivolt_unit> decivolt_quantity;
      /// Measured quantity in volts.
      typedef quantity<volt_unit> volt_quantity;
      /// Measured quantity in dekavolts.
      typedef quantity<dekavolt_unit> dekavolt_quantity;
      /// Measured quantity in decavolts.
      typedef quantity<decavolt_unit> decavolt_quantity;
      /// Measured quantity in hectovolts.
      typedef quantity<hectovolt_unit> hectovolt_quantity;
      /// Measured quantity in kilovolts.
      typedef quantity<kilovolt_unit> kilovolt_quantity;
      /// Measured quantity in megavolts.
      typedef quantity<megavolt_unit> megavolt_quantity;
      /// Measured quantity in gigavolts.
      typedef quantity<gigavolt_unit> gigavolt_quantity;
      /// Measured quantity in teravolts.
      typedef quantity<teravolt_unit> teravolt_quantity;
      /// Measured quantity in petavolts.
      typedef quantity<petavolt_unit> petavolt_quantity;
      /// Measured quantity in exavolts.
      typedef quantity<exavolt_unit> exavolt_quantity;
      /// Measured quantity in zettavolts.
      typedef quantity<zettavolt_unit> zettavolt_quantity;
      /// Measured quantity in yottavolts.
      typedef quantity<yottavolt_unit> yottavolt_quantity;

      // Angle types, constants and quantities.

      /// Unit definition for angle.
      typedef boost::units::angle::radian_base_unit::unit_type angle_unit;
      /// Measured quantity in angle.
      typedef quantity<boost::units::angle::radian_base_unit::unit_type> angle_quantity;

      /// Unit definition for radian.
      typedef boost::units::angle::radian_base_unit::unit_type radian_unit;
      /// Numeric constant for radian.
      BOOST_UNITS_STATIC_CONSTANT(radian, radian_unit);
      /// Numeric constant for radian.
      BOOST_UNITS_STATIC_CONSTANT(radians, radian_unit);
      /// Measured quantity in radians.
      typedef quantity<radian_unit> radian_quantity;

      /// Unit definition for degree.
      typedef boost::units::angle::degree_base_unit::unit_type degree_unit;
      /// Numeric constant for degree.
      BOOST_UNITS_STATIC_CONSTANT(degree, degree_unit);
      /// Numeric constant for degree.
      BOOST_UNITS_STATIC_CONSTANT(degrees, degree_unit);
      /// Measured quantity in degrees.
      typedef quantity<degree_unit> degree_quantity;

      /// Unit definition for gradian.
      typedef boost::units::angle::gradian_base_unit::unit_type gradian_unit;
      /// Numeric constant for gradian.
      BOOST_UNITS_STATIC_CONSTANT(gradian, gradian_unit);
      /// Numeric constant for gradian.
      BOOST_UNITS_STATIC_CONSTANT(gradians, gradian_unit);
      /// Numeric constant for gradian.
      BOOST_UNITS_STATIC_CONSTANT(gon, gradian_unit);
      /// Numeric constant for gradian.
      BOOST_UNITS_STATIC_CONSTANT(gons, gradian_unit);
      /// Measured quantity in gradians.
      typedef quantity<gradian_unit> gradian_quantity;
      /// Measured quantity in gons.
      typedef quantity<gradian_unit> gon_quantity;

    }
  }
}

/// Conversion factor for pound-force per square inch (psi) to pascal.
BOOST_UNITS_DEFINE_CONVERSION_FACTOR(ome::common::units::psi_base_unit, si::pressure, double, 6894.7573); // exact conversion
/// Default conversion for pound-force per square inch (psi) to is to SI pressure units (pascals).
BOOST_UNITS_DEFAULT_CONVERSION(ome::common::units::psi_base_unit, si::pressure);

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

    /// Unit information for pixels.
    template<> struct base_unit_info<ome::common::units::pixel_base_unit>
    {
      /// Unit name.
      static std::string name()   { return "pixel"; }
      /// Unit symbol.
      static std::string symbol() { return "px"; }
    };

    /// Unit information for reference frame.
    template<> struct base_unit_info<ome::common::units::reference_frame_base_unit>
    {
      /// Unit name.
      static std::string name()   { return "reference frame"; }
      /// Unit symbol.
      static std::string symbol() { return "r.f."; }
    };

    /// Unit information for pound-force per square inch (psi).
    template<> struct base_unit_info<ome::common::units::psi_base_unit>
    {
      /// Unit name.
      static std::string name()   { return "pound-force per square inch"; }
      /// Unit symbol.
      static std::string symbol() { return "lbf/in^2"; }
    };

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

#endif // OME_COMMON_UNITS_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

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
 * @file ome/common/units/length.h Length units of measurement.
 *
 * This header contains unit definition types, unit constants and
 * measured quantity types for SI and other standard length units
 * of measurement.
 */

#ifndef OME_COMMON_UNITS_LENGTH_H
#define OME_COMMON_UNITS_LENGTH_H

#include <ome/common/config.h>
#include <ome/common/units/types.h>

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
#include <boost/units/unit.hpp>
#include <boost/units/quantity.hpp>
#include <boost/units/systems/si.hpp>

namespace ome
{
  namespace common
  {
    namespace units
    {

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
      typedef boost::units::metric::angstrom_base_unit::unit_type angstrom_unit;
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
      typedef boost::units::astronomical::astronomical_unit_base_unit::unit_type astronomical_unit_unit;
      /// Numeric constant for astronomical unit.
      BOOST_UNITS_STATIC_CONSTANT(astronomical_unit, astronomical_unit_unit);
      /// Numeric constant for astronomical unit.
      BOOST_UNITS_STATIC_CONSTANT(astronomical_units, astronomical_unit_unit);
      /// Measured quantity in astronomical_unit.
      typedef quantity<astronomical_unit_unit> astronomical_unit_quantity;

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

    }
  }
}

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

  }
}

#endif // OME_COMMON_UNITS_LENGTH_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

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
 * @file ome/common/units/power.h Power units of measurement.
 *
 * This header contains unit definition types, unit constants and
 * measured quantity types for SI and other standard power units
 * of measurement.
 */

#ifndef OME_COMMON_UNITS_POWER_H
#define OME_COMMON_UNITS_POWER_H

#include <ome/common/config.h>
#include <ome/common/units/types.h>

#include <boost/units/unit.hpp>
#include <boost/units/quantity.hpp>
#include <boost/units/systems/si.hpp>

namespace ome
{
  namespace common
  {
    namespace units
    {

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

    }
  }
}

#endif // OME_COMMON_UNITS_POWER_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

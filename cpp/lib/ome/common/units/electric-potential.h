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
 * @file ome/common/units/time.h Time units of measurement.
 *
 * This header contains unit definition types, unit constants and
 * measured quantity types for SI and other standard time units
 * of measurement.
 */

#ifndef OME_COMMON_UNITS_ELECTRIC_POTENTIAL_H
#define OME_COMMON_UNITS_ELECTRIC_POTENTIAL_H

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

    }
  }
}

#endif // OME_COMMON_UNITS_ELECTRIC_POTENTIAL_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

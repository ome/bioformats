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
 * @file ome/common/units/frequency.h Frequency units of measurement.
 *
 * This header contains unit definition types, unit constants and
 * measured quantity types for SI and other standard frequency units
 * of measurement.
 */

#ifndef OME_COMMON_UNITS_FREQUENCY_H
#define OME_COMMON_UNITS_FREQUENCY_H

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

    }
  }
}

#endif // OME_COMMON_UNITS_FREQUENCY_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

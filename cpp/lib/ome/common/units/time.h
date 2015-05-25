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

#ifndef OME_COMMON_UNITS_TIME_H
#define OME_COMMON_UNITS_TIME_H

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

    }
  }
}

#endif // OME_COMMON_UNITS_TIME_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

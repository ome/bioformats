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
 * @file ome/common/units/pressure.h Pressure units of measurement.
 *
 * This header contains unit definition types, unit constants and
 * measured quantity types for SI and other standard pressure units
 * of measurement.
 */

#ifndef OME_COMMON_UNITS_PRESSURE_H
#define OME_COMMON_UNITS_PRESSURE_H

#include <ome/common/config.h>
#include <ome/common/units/types.h>

#include <boost/units/base_units/metric/bar.hpp>
#include <boost/units/base_units/metric/atmosphere.hpp>
#include <boost/units/base_units/metric/torr.hpp>
#include <boost/units/base_units/metric/mmHg.hpp>
#include <boost/units/unit.hpp>
#include <boost/units/quantity.hpp>
#include <boost/units/systems/si.hpp>

namespace ome
{
  namespace common
  {
    namespace units
    {

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

    }
  }
}

/// Conversion factor for pound-force per square inch (psi) to pascal.
BOOST_UNITS_DEFINE_CONVERSION_FACTOR(ome::common::units::psi_base_unit, si::pressure, double, 6894.7573); // exact conversion
/// Default conversion for pound-force per square inch (psi) to is to SI pressure units (pascals).
BOOST_UNITS_DEFAULT_CONVERSION(ome::common::units::psi_base_unit, si::pressure);

namespace boost
{
  namespace units
  {

    /// Unit information for pound-force per square inch (psi).
    template<> struct base_unit_info<ome::common::units::psi_base_unit>
    {
      /// Unit name.
      static std::string name()   { return "pound-force per square inch"; }
      /// Unit symbol.
      static std::string symbol() { return "lbf/in^2"; }
    };

  }
}

#endif // OME_COMMON_UNITS_PRESSURE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

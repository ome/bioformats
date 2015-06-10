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
 * @file ome/common/units/angle.h Angle units of measurement.
 *
 * This header contains unit definition types, unit constants and
 * measured quantity types for SI and other standard angle units
 * of measurement.
 */

#ifndef OME_COMMON_UNITS_ANGLE_H
#define OME_COMMON_UNITS_ANGLE_H

#include <ome/common/config.h>
#include <ome/common/units/types.h>

#include <boost/units/base_units/angle/degree.hpp>
#include <boost/units/base_units/angle/gradian.hpp>
#include <boost/units/base_units/angle/radian.hpp>
#include <boost/units/unit.hpp>
#include <boost/units/quantity.hpp>
#include <boost/units/systems/si.hpp>

namespace ome
{
  namespace common
  {
    namespace units
    {

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

#endif // OME_COMMON_UNITS_ANGLE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

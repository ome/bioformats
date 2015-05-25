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
 * @file ome/common/units/angle.h Common units of measurement types.
 *
 * This header contains types used by all units of measurement.
 */

#ifndef OME_COMMON_UNITS_COMMON_H
#define OME_COMMON_UNITS_COMMON_H

#include <ome/common/config.h>

#include <boost/units/unit.hpp>
#include <boost/units/make_scaled_unit.hpp>
#include <boost/units/quantity.hpp>
#include <boost/units/systems/si.hpp>

namespace ome
{
  namespace common
  {
    namespace units
    {

      using boost::units::quantity;
      using boost::units::quantity_cast;
      using boost::units::make_scaled_unit;
      using boost::units::scaled_base_unit;
      using boost::units::scale;
      using boost::units::static_rational;
      namespace si = boost::units::si;

    }
  }
}

#endif // OME_COMMON_UNITS_COMMON_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

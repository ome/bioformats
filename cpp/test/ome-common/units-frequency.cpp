/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
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

#include "units.h"

#include <ome/common/units/frequency.h>

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctohertz_quantity, hertz_quantity>,
  UnitConversion<zeptohertz_quantity, hertz_quantity>,
  UnitConversion<attohertz_quantity, hertz_quantity>,
  UnitConversion<femtohertz_quantity, hertz_quantity>,
  UnitConversion<picohertz_quantity, hertz_quantity>,
  UnitConversion<nanohertz_quantity, hertz_quantity>,
  UnitConversion<microhertz_quantity, hertz_quantity>,
  UnitConversion<millihertz_quantity, hertz_quantity>,
  UnitConversion<centihertz_quantity, hertz_quantity>,
  UnitConversion<decihertz_quantity, hertz_quantity>,
  UnitConversion<hertz_quantity, hertz_quantity>,
  UnitConversion<dekahertz_quantity, hertz_quantity>,
  UnitConversion<decahertz_quantity, hertz_quantity>,
  UnitConversion<hectohertz_quantity, hertz_quantity>,
  UnitConversion<kilohertz_quantity, hertz_quantity>,
  UnitConversion<megahertz_quantity, hertz_quantity>,
  UnitConversion<gigahertz_quantity, hertz_quantity>,
  UnitConversion<terahertz_quantity, hertz_quantity>,
  UnitConversion<petahertz_quantity, hertz_quantity>,
  UnitConversion<exahertz_quantity, hertz_quantity>,
  UnitConversion<zettahertz_quantity, hertz_quantity>,
  UnitConversion<yottahertz_quantity, hertz_quantity>,
  // All conversions from base unit.
  UnitConversion<hertz_quantity, yoctohertz_quantity>,
  UnitConversion<hertz_quantity, zeptohertz_quantity>,
  UnitConversion<hertz_quantity, attohertz_quantity>,
  UnitConversion<hertz_quantity, femtohertz_quantity>,
  UnitConversion<hertz_quantity, picohertz_quantity>,
  UnitConversion<hertz_quantity, nanohertz_quantity>,
  UnitConversion<hertz_quantity, microhertz_quantity>,
  UnitConversion<hertz_quantity, millihertz_quantity>,
  UnitConversion<hertz_quantity, centihertz_quantity>,
  UnitConversion<hertz_quantity, decihertz_quantity>,
  UnitConversion<hertz_quantity, hertz_quantity>,
  UnitConversion<hertz_quantity, dekahertz_quantity>,
  UnitConversion<hertz_quantity, decahertz_quantity>,
  UnitConversion<hertz_quantity, hectohertz_quantity>,
  UnitConversion<hertz_quantity, kilohertz_quantity>,
  UnitConversion<hertz_quantity, megahertz_quantity>,
  UnitConversion<hertz_quantity, gigahertz_quantity>,
  UnitConversion<hertz_quantity, terahertz_quantity>,
  UnitConversion<hertz_quantity, petahertz_quantity>,
  UnitConversion<hertz_quantity, exahertz_quantity>,
  UnitConversion<hertz_quantity, zettahertz_quantity>,
  UnitConversion<hertz_quantity, yottahertz_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<kilohertz_quantity, gigahertz_quantity>,
  UnitConversion<dekahertz_quantity, nanohertz_quantity>,
  UnitConversion<attohertz_quantity, megahertz_quantity>
  > FrequencyTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(FrequencyTest, UnitConv, FrequencyTestTypes);

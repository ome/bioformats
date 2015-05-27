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

#include <ome/common/units/time.h>

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctosecond_quantity, second_quantity>,
  UnitConversion<zeptosecond_quantity, second_quantity>,
  UnitConversion<attosecond_quantity, second_quantity>,
  UnitConversion<femtosecond_quantity, second_quantity>,
  UnitConversion<picosecond_quantity, second_quantity>,
  UnitConversion<nanosecond_quantity, second_quantity>,
  UnitConversion<microsecond_quantity, second_quantity>,
  UnitConversion<millisecond_quantity, second_quantity>,
  UnitConversion<centisecond_quantity, second_quantity>,
  UnitConversion<decisecond_quantity, second_quantity>,
  UnitConversion<second_quantity, second_quantity>,
  UnitConversion<dekasecond_quantity, second_quantity>,
  UnitConversion<decasecond_quantity, second_quantity>,
  UnitConversion<hectosecond_quantity, second_quantity>,
  UnitConversion<kilosecond_quantity, second_quantity>,
  UnitConversion<megasecond_quantity, second_quantity>,
  UnitConversion<gigasecond_quantity, second_quantity>,
  UnitConversion<terasecond_quantity, second_quantity>,
  UnitConversion<petasecond_quantity, second_quantity>,
  UnitConversion<exasecond_quantity, second_quantity>,
  UnitConversion<zettasecond_quantity, second_quantity>,
  UnitConversion<yottasecond_quantity, second_quantity>,
  // All conversions from base unit.
  UnitConversion<second_quantity, yoctosecond_quantity>,
  UnitConversion<second_quantity, zeptosecond_quantity>,
  UnitConversion<second_quantity, attosecond_quantity>,
  UnitConversion<second_quantity, femtosecond_quantity>,
  UnitConversion<second_quantity, picosecond_quantity>,
  UnitConversion<second_quantity, nanosecond_quantity>,
  UnitConversion<second_quantity, microsecond_quantity>,
  UnitConversion<second_quantity, millisecond_quantity>,
  UnitConversion<second_quantity, centisecond_quantity>,
  UnitConversion<second_quantity, decisecond_quantity>,
  UnitConversion<second_quantity, second_quantity>,
  UnitConversion<second_quantity, dekasecond_quantity>,
  UnitConversion<second_quantity, decasecond_quantity>,
  UnitConversion<second_quantity, hectosecond_quantity>,
  UnitConversion<second_quantity, kilosecond_quantity>,
  UnitConversion<second_quantity, megasecond_quantity>,
  UnitConversion<second_quantity, gigasecond_quantity>,
  UnitConversion<second_quantity, terasecond_quantity>,
  UnitConversion<second_quantity, petasecond_quantity>,
  UnitConversion<second_quantity, exasecond_quantity>,
  UnitConversion<second_quantity, zettasecond_quantity>,
  UnitConversion<second_quantity, yottasecond_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanosecond_quantity, microsecond_quantity>,
  UnitConversion<millisecond_quantity, centisecond_quantity>,
  UnitConversion<microsecond_quantity, millisecond_quantity>
  > TimeTestTypes;

typedef ::testing::Types<
  // Other
  UnitConversion<minute_quantity, second_quantity>,
  UnitConversion<dekasecond_quantity, minute_quantity>,
  UnitConversion<hour_quantity, decasecond_quantity>,
  UnitConversion<hectosecond_quantity, hour_quantity>,
  UnitConversion<day_quantity, kilosecond_quantity>,
  UnitConversion<centisecond_quantity, day_quantity>
  > NonstandardTimeTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(TimeTest, UnitConv, TimeTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(NonstandardTimeTest, UnitConv, NonstandardTimeTestTypes);

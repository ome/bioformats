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
  UnitConversion<yoctosecond_quantity, second_quantity,      -10>,
  UnitConversion<zeptosecond_quantity, second_quantity,      -10>,
  UnitConversion<attosecond_quantity,  second_quantity,      -10>,
  UnitConversion<femtosecond_quantity, second_quantity,      -10>,
  UnitConversion<picosecond_quantity,  second_quantity,      -10>,
  UnitConversion<nanosecond_quantity,  second_quantity,      -10>,
  UnitConversion<microsecond_quantity, second_quantity,      -10>,
  UnitConversion<millisecond_quantity, second_quantity,      -10>,
  UnitConversion<centisecond_quantity, second_quantity,      -10>,
  UnitConversion<decisecond_quantity,  second_quantity,      -10>,
  UnitConversion<second_quantity,      second_quantity,      -10>,
  UnitConversion<dekasecond_quantity,  second_quantity,      -10>,
  UnitConversion<decasecond_quantity,  second_quantity,      -10>,
  UnitConversion<hectosecond_quantity, second_quantity,      -10>,
  UnitConversion<kilosecond_quantity,  second_quantity,      -10>,
  UnitConversion<megasecond_quantity,  second_quantity,      -10>,
  UnitConversion<gigasecond_quantity,  second_quantity,      -10>,
  UnitConversion<terasecond_quantity,  second_quantity,      -10>,
  UnitConversion<petasecond_quantity,  second_quantity,      -10>,
  UnitConversion<exasecond_quantity,   second_quantity,      -10>,
  UnitConversion<zettasecond_quantity, second_quantity,      -10>,
  UnitConversion<yottasecond_quantity, second_quantity,      -10>,
  // All conversions from base unit.
  UnitConversion<second_quantity,      yoctosecond_quantity, -10>,
  UnitConversion<second_quantity,      zeptosecond_quantity, -10>,
  UnitConversion<second_quantity,      attosecond_quantity,  -10>,
  UnitConversion<second_quantity,      femtosecond_quantity, -10>,
  UnitConversion<second_quantity,      picosecond_quantity,  -10>,
  UnitConversion<second_quantity,      nanosecond_quantity,  -10>,
  UnitConversion<second_quantity,      microsecond_quantity, -10>,
  UnitConversion<second_quantity,      millisecond_quantity, -10>,
  UnitConversion<second_quantity,      centisecond_quantity, -10>,
  UnitConversion<second_quantity,      decisecond_quantity,  -10>,
  UnitConversion<second_quantity,      second_quantity,      -10>,
  UnitConversion<second_quantity,      dekasecond_quantity,  -10>,
  UnitConversion<second_quantity,      decasecond_quantity,  -10>,
  UnitConversion<second_quantity,      hectosecond_quantity, -10>,
  UnitConversion<second_quantity,      kilosecond_quantity,  -10>,
  UnitConversion<second_quantity,      megasecond_quantity,  -10>,
  UnitConversion<second_quantity,      gigasecond_quantity,  -10>,
  UnitConversion<second_quantity,      terasecond_quantity,  -10>,
  UnitConversion<second_quantity,      petasecond_quantity,  -10>,
  UnitConversion<second_quantity,      exasecond_quantity,   -10>,
  UnitConversion<second_quantity,      zettasecond_quantity, -10>,
  UnitConversion<second_quantity,      yottasecond_quantity, -10>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanosecond_quantity,  microsecond_quantity, -10>,
  UnitConversion<millisecond_quantity, centisecond_quantity, -10>,
  UnitConversion<microsecond_quantity, millisecond_quantity, -10>
  > TimeTestTypes;

typedef ::testing::Types<
  // Other
  UnitConversion<minute_quantity,      second_quantity,      -10>,
  UnitConversion<dekasecond_quantity,  minute_quantity,      -10>,
  UnitConversion<hour_quantity,        decasecond_quantity,  -10>,
  UnitConversion<hectosecond_quantity, hour_quantity,        -10>,
  UnitConversion<day_quantity,         kilosecond_quantity,  -10>,
  UnitConversion<centisecond_quantity, day_quantity,         -10>
  > NonstandardTimeTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(TimeTest, UnitConv, TimeTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(NonstandardTimeTest, UnitConv, NonstandardTimeTestTypes);

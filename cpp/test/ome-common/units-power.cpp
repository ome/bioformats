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

#include <ome/common/units/power.h>

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctowatt_quantity, watt_quantity,      -10>,
  UnitConversion<zeptowatt_quantity, watt_quantity,      -10>,
  UnitConversion<attowatt_quantity,  watt_quantity,      -10>,
  UnitConversion<femtowatt_quantity, watt_quantity,      -10>,
  UnitConversion<picowatt_quantity,  watt_quantity,      -10>,
  UnitConversion<nanowatt_quantity,  watt_quantity,      -10>,
  UnitConversion<microwatt_quantity, watt_quantity,      -10>,
  UnitConversion<milliwatt_quantity, watt_quantity,      -10>,
  UnitConversion<centiwatt_quantity, watt_quantity,      -10>,
  UnitConversion<deciwatt_quantity,  watt_quantity,      -10>,
  UnitConversion<watt_quantity,      watt_quantity,      -10>,
  UnitConversion<dekawatt_quantity,  watt_quantity,      -10>,
  UnitConversion<decawatt_quantity,  watt_quantity,      -10>,
  UnitConversion<hectowatt_quantity, watt_quantity,      -10>,
  UnitConversion<kilowatt_quantity,  watt_quantity,      -10>,
  UnitConversion<megawatt_quantity,  watt_quantity,      -10>,
  UnitConversion<gigawatt_quantity,  watt_quantity,      -10>,
  UnitConversion<terawatt_quantity,  watt_quantity,      -10>,
  UnitConversion<petawatt_quantity,  watt_quantity,      -10>,
  UnitConversion<exawatt_quantity,   watt_quantity,      -10>,
  UnitConversion<zettawatt_quantity, watt_quantity,      -10>,
  UnitConversion<yottawatt_quantity, watt_quantity,      -10>,
  // All conversions from base unit.
  UnitConversion<watt_quantity,      yoctowatt_quantity, -10>,
  UnitConversion<watt_quantity,      zeptowatt_quantity, -10>,
  UnitConversion<watt_quantity,      attowatt_quantity,  -10>,
  UnitConversion<watt_quantity,      femtowatt_quantity, -10>,
  UnitConversion<watt_quantity,      picowatt_quantity,  -10>,
  UnitConversion<watt_quantity,      nanowatt_quantity,  -10>,
  UnitConversion<watt_quantity,      microwatt_quantity, -10>,
  UnitConversion<watt_quantity,      milliwatt_quantity, -10>,
  UnitConversion<watt_quantity,      centiwatt_quantity, -10>,
  UnitConversion<watt_quantity,      deciwatt_quantity,  -10>,
  UnitConversion<watt_quantity,      watt_quantity,      -10>,
  UnitConversion<watt_quantity,      dekawatt_quantity,  -10>,
  UnitConversion<watt_quantity,      decawatt_quantity,  -10>,
  UnitConversion<watt_quantity,      hectowatt_quantity, -10>,
  UnitConversion<watt_quantity,      kilowatt_quantity,  -10>,
  UnitConversion<watt_quantity,      megawatt_quantity,  -10>,
  UnitConversion<watt_quantity,      gigawatt_quantity,  -10>,
  UnitConversion<watt_quantity,      terawatt_quantity,  -10>,
  UnitConversion<watt_quantity,      petawatt_quantity,  -10>,
  UnitConversion<watt_quantity,      exawatt_quantity,   -10>,
  UnitConversion<watt_quantity,      zettawatt_quantity, -10>,
  UnitConversion<watt_quantity,      yottawatt_quantity, -10>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanowatt_quantity,  microwatt_quantity, -10>,
  UnitConversion<milliwatt_quantity, centiwatt_quantity, -10>,
  UnitConversion<microwatt_quantity, milliwatt_quantity, -10>
  > PowerTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(PowerTest, UnitConv, PowerTestTypes);

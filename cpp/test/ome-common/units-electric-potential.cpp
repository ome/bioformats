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

#include <ome/common/units/electric-potential.h>

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctovolt_quantity, volt_quantity,      -10>,
  UnitConversion<zeptovolt_quantity, volt_quantity,      -10>,
  UnitConversion<attovolt_quantity,  volt_quantity,      -10>,
  UnitConversion<femtovolt_quantity, volt_quantity,      -10>,
  UnitConversion<picovolt_quantity,  volt_quantity,      -10>,
  UnitConversion<nanovolt_quantity,  volt_quantity,      -10>,
  UnitConversion<microvolt_quantity, volt_quantity,      -10>,
  UnitConversion<millivolt_quantity, volt_quantity,      -10>,
  UnitConversion<centivolt_quantity, volt_quantity,      -10>,
  UnitConversion<decivolt_quantity,  volt_quantity,      -10>,
  UnitConversion<volt_quantity,      volt_quantity,      -10>,
  UnitConversion<dekavolt_quantity,  volt_quantity,      -10>,
  UnitConversion<decavolt_quantity,  volt_quantity,      -10>,
  UnitConversion<hectovolt_quantity, volt_quantity,      -10>,
  UnitConversion<kilovolt_quantity,  volt_quantity,      -10>,
  UnitConversion<megavolt_quantity,  volt_quantity,      -10>,
  UnitConversion<gigavolt_quantity,  volt_quantity,      -10>,
  UnitConversion<teravolt_quantity,  volt_quantity,      -10>,
  UnitConversion<petavolt_quantity,  volt_quantity,      -10>,
  UnitConversion<exavolt_quantity,   volt_quantity,      -10>,
  UnitConversion<zettavolt_quantity, volt_quantity,      -10>,
  UnitConversion<yottavolt_quantity, volt_quantity,      -10>,
  // All conversions from base unit.
  UnitConversion<volt_quantity,      yoctovolt_quantity, -10>,
  UnitConversion<volt_quantity,      zeptovolt_quantity, -10>,
  UnitConversion<volt_quantity,      attovolt_quantity,  -10>,
  UnitConversion<volt_quantity,      femtovolt_quantity, -10>,
  UnitConversion<volt_quantity,      picovolt_quantity,  -10>,
  UnitConversion<volt_quantity,      nanovolt_quantity,  -10>,
  UnitConversion<volt_quantity,      microvolt_quantity, -10>,
  UnitConversion<volt_quantity,      millivolt_quantity, -10>,
  UnitConversion<volt_quantity,      centivolt_quantity, -10>,
  UnitConversion<volt_quantity,      decivolt_quantity,  -10>,
  UnitConversion<volt_quantity,      volt_quantity,      -10>,
  UnitConversion<volt_quantity,      dekavolt_quantity,  -10>,
  UnitConversion<volt_quantity,      decavolt_quantity,  -10>,
  UnitConversion<volt_quantity,      hectovolt_quantity, -10>,
  UnitConversion<volt_quantity,      kilovolt_quantity,  -10>,
  UnitConversion<volt_quantity,      megavolt_quantity,  -10>,
  UnitConversion<volt_quantity,      gigavolt_quantity,  -10>,
  UnitConversion<volt_quantity,      teravolt_quantity,  -10>,
  UnitConversion<volt_quantity,      petavolt_quantity,  -10>,
  UnitConversion<volt_quantity,      exavolt_quantity,   -10>,
  UnitConversion<volt_quantity,      zettavolt_quantity, -10>,
  UnitConversion<volt_quantity,      yottavolt_quantity, -10>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanovolt_quantity,  microvolt_quantity, -10>,
  UnitConversion<millivolt_quantity, centivolt_quantity, -10>,
  UnitConversion<microvolt_quantity, millivolt_quantity, -10>
  > ElectricPotentialTestTypes;

typedef ::testing::Types<
  // All conversions to base unit.
  UnitTypeConversion<yoctovolt_unit, volt_unit,      float, -4>,
  UnitTypeConversion<zeptovolt_unit, volt_unit,      float, -4>,
  UnitTypeConversion<attovolt_unit,  volt_unit,      float, -4>,
  UnitTypeConversion<femtovolt_unit, volt_unit,      float, -4>,
  UnitTypeConversion<picovolt_unit,  volt_unit,      float, -4>,
  UnitTypeConversion<nanovolt_unit,  volt_unit,      float, -4>,
  UnitTypeConversion<microvolt_unit, volt_unit,      float, -4>,
  UnitTypeConversion<millivolt_unit, volt_unit,      float, -4>,
  UnitTypeConversion<centivolt_unit, volt_unit,      float, -4>,
  UnitTypeConversion<decivolt_unit,  volt_unit,      float, -4>,
  UnitTypeConversion<volt_unit,      volt_unit,      float, -4>,
  UnitTypeConversion<dekavolt_unit,  volt_unit,      float, -4>,
  UnitTypeConversion<decavolt_unit,  volt_unit,      float, -4>,
  UnitTypeConversion<hectovolt_unit, volt_unit,      float, -4>,
  UnitTypeConversion<kilovolt_unit,  volt_unit,      float, -4>,
  UnitTypeConversion<megavolt_unit,  volt_unit,      float,  4>,
  UnitTypeConversion<gigavolt_unit,  volt_unit,      float,  8>,
  UnitTypeConversion<teravolt_unit,  volt_unit,      float, 11>,
  UnitTypeConversion<petavolt_unit,  volt_unit,      float, 14>,
  UnitTypeConversion<exavolt_unit,   volt_unit,      float, 17>,
  UnitTypeConversion<zettavolt_unit, volt_unit,      float, 20>,
  UnitTypeConversion<yottavolt_unit, volt_unit,      float, 24>,
  // All conversions from base unit.
  UnitTypeConversion<volt_unit,      yoctovolt_unit, float, 23>,
  UnitTypeConversion<volt_unit,      zeptovolt_unit, float, 20>,
  UnitTypeConversion<volt_unit,      attovolt_unit,  float, 17>,
  UnitTypeConversion<volt_unit,      femtovolt_unit, float, 14>,
  UnitTypeConversion<volt_unit,      picovolt_unit,  float, 11>,
  UnitTypeConversion<volt_unit,      nanovolt_unit,  float, 8>,
  UnitTypeConversion<volt_unit,      microvolt_unit, float, 4>,
  UnitTypeConversion<volt_unit,      millivolt_unit, float, -4>,
  UnitTypeConversion<volt_unit,      centivolt_unit, float, -4>,
  UnitTypeConversion<volt_unit,      decivolt_unit,  float, -4>,
  UnitTypeConversion<volt_unit,      volt_unit,      float, -4>,
  UnitTypeConversion<volt_unit,      dekavolt_unit,  float, -4>,
  UnitTypeConversion<volt_unit,      decavolt_unit,  float, -4>,
  UnitTypeConversion<volt_unit,      hectovolt_unit, float, -4>,
  UnitTypeConversion<volt_unit,      kilovolt_unit,  float, -4>,
  UnitTypeConversion<volt_unit,      megavolt_unit,  float, -4>,
  UnitTypeConversion<volt_unit,      gigavolt_unit,  float, -4>,
  UnitTypeConversion<volt_unit,      teravolt_unit,  float, -4>,
  UnitTypeConversion<volt_unit,      petavolt_unit,  float, -4>,
  UnitTypeConversion<volt_unit,      exavolt_unit,   float, -4>,
  UnitTypeConversion<volt_unit,      zettavolt_unit, float, -4>,
  UnitTypeConversion<volt_unit,      yottavolt_unit, float, -4>,
  // Selected conversions between unit prefixes.
  UnitTypeConversion<nanovolt_unit,  microvolt_unit, float, -4>,
  UnitTypeConversion<millivolt_unit, centivolt_unit, float, -4>,
  UnitTypeConversion<microvolt_unit, millivolt_unit, float, -4>
  > ElectricPotentialFloatTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(ElectricPotentialTest, UnitConv, ElectricPotentialTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(ElectricPotentialFloatTest, UnitConv, ElectricPotentialFloatTestTypes);

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
  UnitConversion<yoctovolt_quantity, volt_quantity>,
  UnitConversion<zeptovolt_quantity, volt_quantity>,
  UnitConversion<attovolt_quantity, volt_quantity>,
  UnitConversion<femtovolt_quantity, volt_quantity>,
  UnitConversion<picovolt_quantity, volt_quantity>,
  UnitConversion<nanovolt_quantity, volt_quantity>,
  UnitConversion<microvolt_quantity, volt_quantity>,
  UnitConversion<millivolt_quantity, volt_quantity>,
  UnitConversion<centivolt_quantity, volt_quantity>,
  UnitConversion<decivolt_quantity, volt_quantity>,
  UnitConversion<volt_quantity, volt_quantity>,
  UnitConversion<dekavolt_quantity, volt_quantity>,
  UnitConversion<decavolt_quantity, volt_quantity>,
  UnitConversion<hectovolt_quantity, volt_quantity>,
  UnitConversion<kilovolt_quantity, volt_quantity>,
  UnitConversion<megavolt_quantity, volt_quantity>,
  UnitConversion<gigavolt_quantity, volt_quantity>,
  UnitConversion<teravolt_quantity, volt_quantity>,
  UnitConversion<petavolt_quantity, volt_quantity>,
  UnitConversion<exavolt_quantity, volt_quantity>,
  UnitConversion<zettavolt_quantity, volt_quantity>,
  UnitConversion<yottavolt_quantity, volt_quantity>,
  // All conversions from base unit.
  UnitConversion<volt_quantity, yoctovolt_quantity>,
  UnitConversion<volt_quantity, zeptovolt_quantity>,
  UnitConversion<volt_quantity, attovolt_quantity>,
  UnitConversion<volt_quantity, femtovolt_quantity>,
  UnitConversion<volt_quantity, picovolt_quantity>,
  UnitConversion<volt_quantity, nanovolt_quantity>,
  UnitConversion<volt_quantity, microvolt_quantity>,
  UnitConversion<volt_quantity, millivolt_quantity>,
  UnitConversion<volt_quantity, centivolt_quantity>,
  UnitConversion<volt_quantity, decivolt_quantity>,
  UnitConversion<volt_quantity, volt_quantity>,
  UnitConversion<volt_quantity, dekavolt_quantity>,
  UnitConversion<volt_quantity, decavolt_quantity>,
  UnitConversion<volt_quantity, hectovolt_quantity>,
  UnitConversion<volt_quantity, kilovolt_quantity>,
  UnitConversion<volt_quantity, megavolt_quantity>,
  UnitConversion<volt_quantity, gigavolt_quantity>,
  UnitConversion<volt_quantity, teravolt_quantity>,
  UnitConversion<volt_quantity, petavolt_quantity>,
  UnitConversion<volt_quantity, exavolt_quantity>,
  UnitConversion<volt_quantity, zettavolt_quantity>,
  UnitConversion<volt_quantity, yottavolt_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanovolt_quantity, microvolt_quantity>,
  UnitConversion<millivolt_quantity, centivolt_quantity>,
  UnitConversion<microvolt_quantity, millivolt_quantity>
  > ElectricPotentialTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(ElectricPotentialTest, UnitConv, ElectricPotentialTestTypes);

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

#include <ome/common/units/angle.h>

typedef ::testing::Types<
  UnitConversion<radian_quantity,  gradian_quantity, -10>,
  UnitConversion<radian_quantity,  degree_quantity,  -10>,
  UnitConversion<gradian_quantity, radian_quantity,  -10>,
  UnitConversion<gradian_quantity, degree_quantity,  -10>,
  UnitConversion<degree_quantity,  radian_quantity,  -10>,
  UnitConversion<degree_quantity,  gradian_quantity, -10>
  > AngleTestTypes;

typedef ::testing::Types<
  UnitTypeConversion<radian_unit,  gradian_unit, float, -7>,
  UnitTypeConversion<radian_unit,  degree_unit,  float, -7>,
  UnitTypeConversion<gradian_unit, radian_unit,  float, -7>,
  UnitTypeConversion<gradian_unit, degree_unit,  float, -7>,
  UnitTypeConversion<degree_unit,  radian_unit,  float, -7>,
  UnitTypeConversion<degree_unit,  gradian_unit, float, -7>
  > AngleFloatTestTypes;

typedef ::testing::Types<
  UnitTypeConversion<radian_unit,  gradian_unit, long double, -10>,
  UnitTypeConversion<radian_unit,  degree_unit,  long double, -10>,
  UnitTypeConversion<gradian_unit, radian_unit,  long double, -10>,
  UnitTypeConversion<gradian_unit, degree_unit,  long double, -10>,
  UnitTypeConversion<degree_unit,  radian_unit,  long double, -10>,
  UnitTypeConversion<degree_unit,  gradian_unit, long double, -10>
  > AngleLongDoubleTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(AngleTest, UnitConv, AngleTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(AngleFloatTest, UnitConv, AngleFloatTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(AngleLongDoubleTest, UnitConv, AngleLongDoubleTestTypes);

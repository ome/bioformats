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

#include <ome/common/units/length.h>

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctometre_quantity, metre_quantity>,
  UnitConversion<zeptometre_quantity, metre_quantity>,
  UnitConversion<attometre_quantity, metre_quantity>,
  UnitConversion<femtometre_quantity, metre_quantity>,
  UnitConversion<picometre_quantity, metre_quantity>,
  UnitConversion<nanometre_quantity, metre_quantity>,
  UnitConversion<micrometre_quantity, metre_quantity>,
  UnitConversion<millimetre_quantity, metre_quantity>,
  UnitConversion<centimetre_quantity, metre_quantity>,
  UnitConversion<decimetre_quantity, metre_quantity>,
  UnitConversion<metre_quantity, metre_quantity>,
  UnitConversion<dekametre_quantity, metre_quantity>,
  UnitConversion<decametre_quantity, metre_quantity>,
  UnitConversion<hectometre_quantity, metre_quantity>,
  UnitConversion<kilometre_quantity, metre_quantity>,
  UnitConversion<megametre_quantity, metre_quantity>,
  UnitConversion<gigametre_quantity, metre_quantity>,
  UnitConversion<terametre_quantity, metre_quantity>,
  UnitConversion<petametre_quantity, metre_quantity>,
  UnitConversion<exametre_quantity, metre_quantity>,
  UnitConversion<zettametre_quantity, metre_quantity>,
  UnitConversion<yottametre_quantity, metre_quantity>,
  // All conversions from base unit.
  UnitConversion<metre_quantity, yoctometre_quantity>,
  UnitConversion<metre_quantity, zeptometre_quantity>,
  UnitConversion<metre_quantity, attometre_quantity>,
  UnitConversion<metre_quantity, femtometre_quantity>,
  UnitConversion<metre_quantity, picometre_quantity>,
  UnitConversion<metre_quantity, nanometre_quantity>,
  UnitConversion<metre_quantity, micrometre_quantity>,
  UnitConversion<metre_quantity, millimetre_quantity>,
  UnitConversion<metre_quantity, centimetre_quantity>,
  UnitConversion<metre_quantity, decimetre_quantity>,
  UnitConversion<metre_quantity, metre_quantity>,
  UnitConversion<metre_quantity, dekametre_quantity>,
  UnitConversion<metre_quantity, decametre_quantity>,
  UnitConversion<metre_quantity, hectometre_quantity>,
  UnitConversion<metre_quantity, kilometre_quantity>,
  UnitConversion<metre_quantity, megametre_quantity>,
  UnitConversion<metre_quantity, gigametre_quantity>,
  UnitConversion<metre_quantity, terametre_quantity>,
  UnitConversion<metre_quantity, petametre_quantity>,
  UnitConversion<metre_quantity, exametre_quantity>,
  UnitConversion<metre_quantity, zettametre_quantity>,
  UnitConversion<metre_quantity, yottametre_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanometre_quantity, micrometre_quantity>,
  UnitConversion<millimetre_quantity, centimetre_quantity>,
  UnitConversion<micrometre_quantity, millimetre_quantity>
  > LengthTestTypes;

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctometer_quantity, meter_quantity>,
  UnitConversion<zeptometer_quantity, meter_quantity>,
  UnitConversion<attometer_quantity, meter_quantity>,
  UnitConversion<femtometer_quantity, meter_quantity>,
  UnitConversion<picometer_quantity, meter_quantity>,
  UnitConversion<nanometer_quantity, meter_quantity>,
  UnitConversion<micrometer_quantity, meter_quantity>,
  UnitConversion<millimeter_quantity, meter_quantity>,
  UnitConversion<centimeter_quantity, meter_quantity>,
  UnitConversion<decimeter_quantity, meter_quantity>,
  UnitConversion<meter_quantity, meter_quantity>,
  UnitConversion<dekameter_quantity, meter_quantity>,
  UnitConversion<decameter_quantity, meter_quantity>,
  UnitConversion<hectometer_quantity, meter_quantity>,
  UnitConversion<kilometer_quantity, meter_quantity>,
  UnitConversion<megameter_quantity, meter_quantity>,
  UnitConversion<gigameter_quantity, meter_quantity>,
  UnitConversion<terameter_quantity, meter_quantity>,
  UnitConversion<petameter_quantity, meter_quantity>,
  UnitConversion<exameter_quantity, meter_quantity>,
  UnitConversion<zettameter_quantity, meter_quantity>,
  UnitConversion<yottameter_quantity, meter_quantity>,
  // All conversions from base unit.
  UnitConversion<meter_quantity, yoctometer_quantity>,
  UnitConversion<meter_quantity, zeptometer_quantity>,
  UnitConversion<meter_quantity, attometer_quantity>,
  UnitConversion<meter_quantity, femtometer_quantity>,
  UnitConversion<meter_quantity, picometer_quantity>,
  UnitConversion<meter_quantity, nanometer_quantity>,
  UnitConversion<meter_quantity, micrometer_quantity>,
  UnitConversion<meter_quantity, millimeter_quantity>,
  UnitConversion<meter_quantity, centimeter_quantity>,
  UnitConversion<meter_quantity, decimeter_quantity>,
  UnitConversion<meter_quantity, meter_quantity>,
  UnitConversion<meter_quantity, dekameter_quantity>,
  UnitConversion<meter_quantity, decameter_quantity>,
  UnitConversion<meter_quantity, hectometer_quantity>,
  UnitConversion<meter_quantity, kilometer_quantity>,
  UnitConversion<meter_quantity, megameter_quantity>,
  UnitConversion<meter_quantity, gigameter_quantity>,
  UnitConversion<meter_quantity, terameter_quantity>,
  UnitConversion<meter_quantity, petameter_quantity>,
  UnitConversion<meter_quantity, exameter_quantity>,
  UnitConversion<meter_quantity, zettameter_quantity>,
  UnitConversion<meter_quantity, yottameter_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanometer_quantity, micrometer_quantity>,
  UnitConversion<millimeter_quantity, centimeter_quantity>,
  UnitConversion<micrometer_quantity, millimeter_quantity>
  > ConvenienceLengthTestTypes;

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<angstrom_quantity, nanometre_quantity>,
  UnitConversion<micrometre_quantity, angstrom_quantity>,
  UnitConversion<thou_quantity, millimetre_quantity>,
  UnitConversion<centimetre_quantity, thou_quantity>,
  UnitConversion<inch_quantity, centimetre_quantity>,
  UnitConversion<metre_quantity, inch_quantity>,
  UnitConversion<foot_quantity, metre_quantity>,
  UnitConversion<millimetre_quantity, foot_quantity>,
  UnitConversion<yard_quantity, centimetre_quantity>,
  UnitConversion<decimetre_quantity, yard_quantity>,
  UnitConversion<mile_quantity, decametre_quantity>,
  UnitConversion<kilometre_quantity, mile_quantity>,
  UnitConversion<astronomical_unit_quantity, gigametre_quantity>,
  UnitConversion<terametre_quantity, astronomical_unit_quantity>,
  UnitConversion<light_year_quantity, terametre_quantity>,
  UnitConversion<petametre_quantity, light_year_quantity>,
  UnitConversion<parsec_quantity, petametre_quantity>,
  UnitConversion<exametre_quantity, parsec_quantity>
  > NonstandardLengthTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(LengthTest, UnitConv, LengthTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(ConvenienceLengthTest, UnitConv, ConvenienceLengthTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(NonstandardLengthTest, UnitConv, NonstandardLengthTestTypes);

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

#include <ome/common/units.h>

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctopascal_quantity, pascal_quantity>,
  UnitConversion<zeptopascal_quantity, pascal_quantity>,
  UnitConversion<attopascal_quantity, pascal_quantity>,
  UnitConversion<femtopascal_quantity, pascal_quantity>,
  UnitConversion<picopascal_quantity, pascal_quantity>,
  UnitConversion<nanopascal_quantity, pascal_quantity>,
  UnitConversion<micropascal_quantity, pascal_quantity>,
  UnitConversion<millipascal_quantity, pascal_quantity>,
  UnitConversion<centipascal_quantity, pascal_quantity>,
  UnitConversion<decipascal_quantity, pascal_quantity>,
  UnitConversion<pascal_quantity, pascal_quantity>,
  UnitConversion<dekapascal_quantity, pascal_quantity>,
  UnitConversion<decapascal_quantity, pascal_quantity>,
  UnitConversion<hectopascal_quantity, pascal_quantity>,
  UnitConversion<kilopascal_quantity, pascal_quantity>,
  UnitConversion<megapascal_quantity, pascal_quantity>,
  UnitConversion<gigapascal_quantity, pascal_quantity>,
  UnitConversion<terapascal_quantity, pascal_quantity>,
  UnitConversion<petapascal_quantity, pascal_quantity>,
  UnitConversion<exapascal_quantity, pascal_quantity>,
  UnitConversion<zettapascal_quantity, pascal_quantity>,
  UnitConversion<yottapascal_quantity, pascal_quantity>,
  // All conversions from base unit.
  UnitConversion<pascal_quantity, yoctopascal_quantity>,
  UnitConversion<pascal_quantity, zeptopascal_quantity>,
  UnitConversion<pascal_quantity, attopascal_quantity>,
  UnitConversion<pascal_quantity, femtopascal_quantity>,
  UnitConversion<pascal_quantity, picopascal_quantity>,
  UnitConversion<pascal_quantity, nanopascal_quantity>,
  UnitConversion<pascal_quantity, micropascal_quantity>,
  UnitConversion<pascal_quantity, millipascal_quantity>,
  UnitConversion<pascal_quantity, centipascal_quantity>,
  UnitConversion<pascal_quantity, decipascal_quantity>,
  UnitConversion<pascal_quantity, pascal_quantity>,
  UnitConversion<pascal_quantity, dekapascal_quantity>,
  UnitConversion<pascal_quantity, decapascal_quantity>,
  UnitConversion<pascal_quantity, hectopascal_quantity>,
  UnitConversion<pascal_quantity, kilopascal_quantity>,
  UnitConversion<pascal_quantity, megapascal_quantity>,
  UnitConversion<pascal_quantity, gigapascal_quantity>,
  UnitConversion<pascal_quantity, terapascal_quantity>,
  UnitConversion<pascal_quantity, petapascal_quantity>,
  UnitConversion<pascal_quantity, exapascal_quantity>,
  UnitConversion<pascal_quantity, zettapascal_quantity>,
  UnitConversion<pascal_quantity, yottapascal_quantity>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanopascal_quantity, micropascal_quantity>,
  UnitConversion<millipascal_quantity, centipascal_quantity>,
  UnitConversion<micropascal_quantity, millipascal_quantity>
  > PressureTestTypes;

typedef ::testing::Types<
  // Bar
  UnitConversion<millibar_quantity, pascal_quantity>,
  UnitConversion<kilopascal_quantity, millibar_quantity>,
  UnitConversion<centibar_quantity, decapascal_quantity>,
  UnitConversion<hectopascal_quantity, centibar_quantity>,
  UnitConversion<decibar_quantity, kilopascal_quantity>,
  UnitConversion<megapascal_quantity, decibar_quantity>,
  UnitConversion<bar_quantity, megapascal_quantity>,
  UnitConversion<gigapascal_quantity, bar_quantity>,
  UnitConversion<decabar_quantity, gigapascal_quantity>,
  UnitConversion<kilopascal_quantity, decabar_quantity>,
  UnitConversion<hectobar_quantity, kilopascal_quantity>,
  UnitConversion<gigapascal_quantity, hectobar_quantity>,
  UnitConversion<kilobar_quantity, terapascal_quantity>,
  UnitConversion<gigapascal_quantity, kilobar_quantity>,
  UnitConversion<megabar_quantity, exapascal_quantity>,
  UnitConversion<gigapascal_quantity, megabar_quantity>,
  // Other
  UnitConversion<atmosphere_quantity, kilopascal_quantity>,
  UnitConversion<megapascal_quantity, atmosphere_quantity>,
  UnitConversion<psi_quantity, kilopascal_quantity>,
  UnitConversion<hectopascal_quantity, psi_quantity>,
  UnitConversion<torr_quantity, pascal_quantity>,
  UnitConversion<decapascal_quantity, torr_quantity>,
  UnitConversion<millitorr_quantity, pascal_quantity>,
  UnitConversion<centipascal_quantity, millitorr_quantity>,
  UnitConversion<mmHg_quantity, pascal_quantity>,
  UnitConversion<hectopascal_quantity, mmHg_quantity>
  > NonstandardPressureTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(PressureTest, UnitConv, PressureTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(NonstandardPressureTest, UnitConv, NonstandardPressureTestTypes);

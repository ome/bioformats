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

#include <ome/common/units/pressure.h>

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<yoctopascal_quantity, pascal_quantity,      -10>,
  UnitConversion<zeptopascal_quantity, pascal_quantity,      -10>,
  UnitConversion<attopascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<femtopascal_quantity, pascal_quantity,      -10>,
  UnitConversion<picopascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<nanopascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<micropascal_quantity, pascal_quantity,      -10>,
  UnitConversion<millipascal_quantity, pascal_quantity,      -10>,
  UnitConversion<centipascal_quantity, pascal_quantity,      -10>,
  UnitConversion<decipascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<pascal_quantity,      pascal_quantity,      -10>,
  UnitConversion<dekapascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<decapascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<hectopascal_quantity, pascal_quantity,      -10>,
  UnitConversion<kilopascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<megapascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<gigapascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<terapascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<petapascal_quantity,  pascal_quantity,      -10>,
  UnitConversion<exapascal_quantity,   pascal_quantity,      -10>,
  UnitConversion<zettapascal_quantity, pascal_quantity,      -10>,
  UnitConversion<yottapascal_quantity, pascal_quantity,      -10>,
  // All conversions from base unit.
  UnitConversion<pascal_quantity,      yoctopascal_quantity, -10>,
  UnitConversion<pascal_quantity,      zeptopascal_quantity, -10>,
  UnitConversion<pascal_quantity,      attopascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      femtopascal_quantity, -10>,
  UnitConversion<pascal_quantity,      picopascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      nanopascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      micropascal_quantity, -10>,
  UnitConversion<pascal_quantity,      millipascal_quantity, -10>,
  UnitConversion<pascal_quantity,      centipascal_quantity, -10>,
  UnitConversion<pascal_quantity,      decipascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      pascal_quantity,      -10>,
  UnitConversion<pascal_quantity,      dekapascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      decapascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      hectopascal_quantity, -10>,
  UnitConversion<pascal_quantity,      kilopascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      megapascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      gigapascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      terapascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      petapascal_quantity,  -10>,
  UnitConversion<pascal_quantity,      exapascal_quantity,   -10>,
  UnitConversion<pascal_quantity,      zettapascal_quantity, -10>,
  UnitConversion<pascal_quantity,      yottapascal_quantity, -10>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanopascal_quantity,  micropascal_quantity, -10>,
  UnitConversion<millipascal_quantity, centipascal_quantity, -10>,
  UnitConversion<micropascal_quantity, millipascal_quantity, -10>
  > PressureTestTypes;

typedef ::testing::Types<
  // Bar
  UnitConversion<millibar_quantity,    pascal_quantity,      -10>,
  UnitConversion<kilopascal_quantity,  millibar_quantity,    -10>,
  UnitConversion<centibar_quantity,    decapascal_quantity,  -10>,
  UnitConversion<hectopascal_quantity, centibar_quantity,    -10>,
  UnitConversion<decibar_quantity,     kilopascal_quantity,  -10>,
  UnitConversion<megapascal_quantity,  decibar_quantity,     -10>,
  UnitConversion<bar_quantity,         megapascal_quantity,  -10>,
  UnitConversion<gigapascal_quantity,  bar_quantity,         -10>,
  UnitConversion<decabar_quantity,     gigapascal_quantity,  -10>,
  UnitConversion<kilopascal_quantity,  decabar_quantity,     -10>,
  UnitConversion<hectobar_quantity,    kilopascal_quantity,  -10>,
  UnitConversion<gigapascal_quantity,  hectobar_quantity,    -10>,
  UnitConversion<kilobar_quantity,     terapascal_quantity,  -10>,
  UnitConversion<gigapascal_quantity,  kilobar_quantity,     -10>,
  UnitConversion<megabar_quantity,     exapascal_quantity,   -10>,
  UnitConversion<gigapascal_quantity,  megabar_quantity,     -10>,
  // Other
  UnitConversion<atmosphere_quantity,  kilopascal_quantity,  -10>,
  UnitConversion<megapascal_quantity,  atmosphere_quantity,  -10>,
  UnitConversion<psi_quantity,         kilopascal_quantity,   -6>,
  UnitConversion<hectopascal_quantity, psi_quantity,          -8>,
  UnitConversion<torr_quantity,        pascal_quantity,       -4>,
  UnitConversion<decapascal_quantity,  torr_quantity,         -7>,
  UnitConversion<millitorr_quantity,   pascal_quantity,       -6>,
  UnitConversion<centipascal_quantity, millitorr_quantity,    -6>,
  UnitConversion<mmHg_quantity,        pascal_quantity,      -10>,
  UnitConversion<hectopascal_quantity, mmHg_quantity,         -5>
  > NonstandardPressureTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(PressureTest, UnitConv, PressureTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(NonstandardPressureTest, UnitConv, NonstandardPressureTestTypes);

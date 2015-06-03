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
  UnitConversion<yoctometre_quantity, metre_quantity,      -10>,
  UnitConversion<zeptometre_quantity, metre_quantity,      -10>,
  UnitConversion<attometre_quantity,  metre_quantity,      -10>,
  UnitConversion<femtometre_quantity, metre_quantity,      -10>,
  UnitConversion<picometre_quantity,  metre_quantity,      -10>,
  UnitConversion<nanometre_quantity,  metre_quantity,      -10>,
  UnitConversion<micrometre_quantity, metre_quantity,      -10>,
  UnitConversion<millimetre_quantity, metre_quantity,      -10>,
  UnitConversion<centimetre_quantity, metre_quantity,      -10>,
  UnitConversion<decimetre_quantity,  metre_quantity,      -10>,
  UnitConversion<metre_quantity,      metre_quantity,      -10>,
  UnitConversion<dekametre_quantity,  metre_quantity,      -10>,
  UnitConversion<decametre_quantity,  metre_quantity,      -10>,
  UnitConversion<hectometre_quantity, metre_quantity,      -10>,
  UnitConversion<kilometre_quantity,  metre_quantity,      -10>,
  UnitConversion<megametre_quantity,  metre_quantity,      -10>,
  UnitConversion<gigametre_quantity,  metre_quantity,      -10>,
  UnitConversion<terametre_quantity,  metre_quantity,      -10>,
  UnitConversion<petametre_quantity,  metre_quantity,      -10>,
  UnitConversion<exametre_quantity,   metre_quantity,      -10>,
  UnitConversion<zettametre_quantity, metre_quantity,      -10>,
  UnitConversion<yottametre_quantity, metre_quantity,      -10>,
  // All conversions from base unit.
  UnitConversion<metre_quantity,      yoctometre_quantity, -10>,
  UnitConversion<metre_quantity,      zeptometre_quantity, -10>,
  UnitConversion<metre_quantity,      attometre_quantity,  -10>,
  UnitConversion<metre_quantity,      femtometre_quantity, -10>,
  UnitConversion<metre_quantity,      picometre_quantity,  -10>,
  UnitConversion<metre_quantity,      nanometre_quantity,  -10>,
  UnitConversion<metre_quantity,      micrometre_quantity, -10>,
  UnitConversion<metre_quantity,      millimetre_quantity, -10>,
  UnitConversion<metre_quantity,      centimetre_quantity, -10>,
  UnitConversion<metre_quantity,      decimetre_quantity,  -10>,
  UnitConversion<metre_quantity,      metre_quantity,      -10>,
  UnitConversion<metre_quantity,      dekametre_quantity,  -10>,
  UnitConversion<metre_quantity,      decametre_quantity,  -10>,
  UnitConversion<metre_quantity,      hectometre_quantity, -10>,
  UnitConversion<metre_quantity,      kilometre_quantity,  -10>,
  UnitConversion<metre_quantity,      megametre_quantity,  -10>,
  UnitConversion<metre_quantity,      gigametre_quantity,  -10>,
  UnitConversion<metre_quantity,      terametre_quantity,  -10>,
  UnitConversion<metre_quantity,      petametre_quantity,  -10>,
  UnitConversion<metre_quantity,      exametre_quantity,   -10>,
  UnitConversion<metre_quantity,      zettametre_quantity, -10>,
  UnitConversion<metre_quantity,      yottametre_quantity, -10>,
  // Selected conversions between unit prefixes.
  UnitConversion<nanometre_quantity,  micrometre_quantity, -10>,
  UnitConversion<millimetre_quantity, centimetre_quantity, -10>,
  UnitConversion<micrometre_quantity, millimetre_quantity, -10>
  > LengthTestTypes;

typedef ::testing::Types<
  // All conversions to base unit.
  UnitConversion<angstrom_quantity,          nanometre_quantity,         -10>,
  UnitConversion<micrometre_quantity,        angstrom_quantity,          -10>,
  UnitConversion<thou_quantity,              millimetre_quantity,        -10>,
  UnitConversion<centimetre_quantity,        thou_quantity,              -10>,
  UnitConversion<inch_quantity,              centimetre_quantity,        -10>,
  UnitConversion<metre_quantity,             inch_quantity,              -10>,
  UnitConversion<foot_quantity,              metre_quantity,             -10>,
  UnitConversion<millimetre_quantity,        foot_quantity,              -10>,
  UnitConversion<yard_quantity,              centimetre_quantity,        -10>,
  UnitConversion<decimetre_quantity,         yard_quantity,              -10>,
  UnitConversion<mile_quantity,              decametre_quantity,         -10>,
  UnitConversion<kilometre_quantity,         mile_quantity,              -10>,
  UnitConversion<astronomical_unit_quantity, gigametre_quantity,          -6>,
  UnitConversion<terametre_quantity,         astronomical_unit_quantity,  -7>,
  UnitConversion<light_year_quantity,        terametre_quantity,         -10>,
  UnitConversion<petametre_quantity,         light_year_quantity,        -10>,
  UnitConversion<parsec_quantity,            petametre_quantity,          -5>,
  UnitConversion<exametre_quantity,          parsec_quantity,             -7>,
  UnitConversion<pixel_quantity,             pixel_quantity,             -10>,
  UnitConversion<reference_frame_quantity,   reference_frame_quantity,   -10>
  > NonstandardLengthTestTypes;

INSTANTIATE_TYPED_TEST_CASE_P(LengthTest, UnitConv, LengthTestTypes);
INSTANTIATE_TYPED_TEST_CASE_P(NonstandardLengthTest, UnitConv, NonstandardLengthTestTypes);

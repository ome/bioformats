/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2006 - 2015 Open Microscopy Environment:
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

#include <ome/xml/model/primitives/PercentFraction.h>

#include "constrained-numeric.h"

using ome::xml::model::primitives::PercentFraction;

// Floating point types don't implement modulo, so make it a no-op.
template<>
struct OperationModulo<PercentFraction>
{
  PercentFraction eval(PercentFraction lhs,             PercentFraction rhs) { return lhs; }
  PercentFraction eval(PercentFraction lhs, PercentFraction::value_type rhs) { return lhs; }
};

template<>
struct OperationModuloAssign<PercentFraction>
{
  PercentFraction eval(PercentFraction lhs,             PercentFraction rhs) { return lhs; }
  PercentFraction eval(PercentFraction lhs, PercentFraction::value_type rhs) { return lhs; }
};


INSTANTIATE_TYPED_TEST_CASE_P(PercentFraction, NumericTest, PercentFraction);

namespace
{

  NumericTest<PercentFraction>::test_str init_strings[] =
    { // str     pos       strpass pospass
      {"-42.12", -42.12,   false,  false},
      {"1.0",    -53.0,    true,   false},
      {"82.232",  82.232,  false,  false},
      {"0.23",     0.23,   true,   true},
      {"0.0001",   0.0001, true,   true},
      {"0",        0,      true,   true},
      {"1",        1,      true,   true},
      {"1.001",    1.001,  false,  false},
      {"1.0",     -0.1,    true,   false},
      {"invalid",  1.0,    false,  true},
    };

  NumericTest<PercentFraction>::test_op init_ops[] =
    { // v1     v2       expected         operation         pass   except rhsexcept
      {0.0,     0.0,              1.0,    EQUAL,            true,  false, false},
      {1.0,     1.0,              1.0,    EQUAL,            true,  false, false},
      {0.822,   0.822,            1.0,    EQUAL,            true,  false, false},
      {0.230,   0.230,            1.0,    EQUAL,            true,  false, false},
      {0.23,    0.48,             0.0,    EQUAL,            false, false, false},
      {0.23,    0.35,             1.0,    NOT_EQUAL,        true,  false, false},
      {0.54,    0.54,             0.0,    NOT_EQUAL,        false, false, false},

      {0.5432,  0.8272,           1.0,    LESS,             true,  false, false},
      {0.5340,  0.5340,           0.0,    LESS,             false, false, false},
      {0.9470,  0.340,            0.0,    LESS,             false, false, false},
      {0.54320, 0.82720,          1.0,    LESS_OR_EQUAL,    true,  false, false},
      {0.5340,  0.5340,           1.0,    LESS_OR_EQUAL,    true,  false, false},
      {0.9470,  0.340,            0.0,    LESS_OR_EQUAL,    false, false, false},

      {0.908,   0.842,            1.0,    GREATER,          true,  false, false},
      {0.3622,  0.3622,           0.0,    GREATER,          false, false, false},
      {0.0872,  0.2701,           0.0,    GREATER,          false, false, false},
      {0.908,   0.842,            1.0,    GREATER_OR_EQUAL, true,  false, false},
      {0.3622,  0.3622,           1.0,    GREATER_OR_EQUAL, true,  false, false},
      {0.0872,  0.2701,           0.0,    GREATER_OR_EQUAL, false, false, false},

      {0.432,   0.0743,  0.432 +  0.0743, ADD,              true,  false, false},
      {0.432,  -0.074,   0.432 -  0.074,  ADD,              true,  false, true},
      {0.432,  -0.743,   0.432 -  0.743,  ADD,              false, true,  true},
      {0.432,   0.8,     0.432 +  0.8,    ADD,              false, true,  false},
      {0.432,   0.0743,           1.0,    ADD,              false, false, false},
      {0.823,   0.093,   0.823 +  0.093,  ADD_ASSIGN,       true,  false, false},
      {0.823,  -0.093,   0.823 -  0.093,  ADD_ASSIGN,       true,  false, true},
      {0.823,  -0.932,   0.823 -  0.932,  ADD_ASSIGN,       false, true,  true},
      {0.823,   0.93,    0.823 +  0.93,   ADD_ASSIGN,       false, true,  false},
      {0.823,   0.093,            0.1,    ADD_ASSIGN,       false, false, false},

      {0.432,   0.285,   0.432 -  0.285,  SUBTRACT,         true,  false, false},
      {0.432,  -0.285,   0.432 +  0.285,  SUBTRACT,         true,  false, true},
      {0.432,   0.763,   0.432 -  0.763,  SUBTRACT,         false, true,  false},
      {0.432,   0.285,            0.1,    SUBTRACT,         false, false, false},
      {0.432,   0.431,            0.001,  SUBTRACT,         true,  false, false},
      {0.432,   0.432,            0.0,    SUBTRACT,         true,  false, false},
      {0.432,   0.433,           -0.001,  SUBTRACT,         false, true,  false},
      {0.823,   0.093,   0.823 -  0.093,  SUBTRACT_ASSIGN,  true,  false, false},
      {0.823,  -0.0932,  0.823 +  0.0932, SUBTRACT_ASSIGN,  true,  false, true},
      {0.823,   0.9139,  0.823 -  0.9139, SUBTRACT_ASSIGN,  false, true,  false},
      {0.823,   0.093,            0.1,    SUBTRACT_ASSIGN,  false, false, false},
      {0.823,   0.822,            0.001,  SUBTRACT_ASSIGN,  true,  false, false},
      {0.823,   0.823,            0.0,    SUBTRACT_ASSIGN,  true,  false, false},

      {0.40,    0.12,    0.40 *   0.12,   MULTIPLY,         true,  false, false},
      {0.23,   -0.8,     0.23 *  -0.8,    MULTIPLY,         false, true,  true},
      {0.40,    0.12,             0.1,    MULTIPLY,         false, false, false},
      {0.18,    0.4,     0.18 *   0.4,    MULTIPLY_ASSIGN,  true,  false, false},
      {0.2,    -0.232,   0.2  *  -0.232,  MULTIPLY_ASSIGN,  false, true,  true},
      {0.18,    0.4,              0.1,    MULTIPLY_ASSIGN,  false, false, false},

      {0.900,   2.0,     0.900 /  2.0,    DIVIDE,           true,  false, true},
      {0.900,  -2.0,     0.900 / -2.0,    DIVIDE,           false, true,  true},
      {0.2,     0.900,   0.2 /    0.900,  DIVIDE,           true,  false, false},
      {0.5,     0.901,   0.5 /    0.901,  DIVIDE,           true,  false, false},
      {0.5,     0.900,            0.2,    DIVIDE,           false, false, false},
      {0.480,   2.0,     0.480 /  2.0,    DIVIDE_ASSIGN,    true,  false, true},
      {0.480,  -2.0,     0.480 / -2.0,    DIVIDE_ASSIGN,    false, true,  true},
      {0.480,   0.480,   0.480 /  0.480,  DIVIDE_ASSIGN,    true,  false, false},
      {0.480,   0.481,   0.480 /  0.481,  DIVIDE_ASSIGN,    true,  false, false},
      {0.480,   0.480,            0.2,    DIVIDE_ASSIGN,    false, false, false},

    };

}

template<>
const std::vector<NumericTest<PercentFraction>::test_str>
NumericTest<PercentFraction>::strings(init_strings,
                                      init_strings + (sizeof(init_strings) / sizeof(init_strings[0])));

template<>
const std::vector<NumericTest<PercentFraction>::test_op>
NumericTest<PercentFraction>::ops(init_ops,
                                  init_ops + (sizeof(init_ops) / sizeof(init_ops[0])));

template<>
const PercentFraction::value_type NumericTest<PercentFraction>::error(0.0005);

template<>
const PercentFraction NumericTest<PercentFraction>::safedefault(0.9);

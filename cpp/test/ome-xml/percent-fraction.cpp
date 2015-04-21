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

#include <boost/range/size.hpp>

#include <ome/xml/model/primitives/PercentFraction.h>

#include "constrained-numeric.h"

using ome::xml::model::primitives::PercentFraction;

// Direct equality comparisons are not safe for floating point types.
// The tests here use an allowed error of 0.05.

template<>
struct CompareEqual<PercentFraction>
{
  bool compare(PercentFraction lhs, PercentFraction rhs)
  { return lhs > static_cast<PercentFraction::value_type>(rhs) - 0.05F && lhs < static_cast<PercentFraction::value_type>(rhs) + 0.05F; }

  bool compare(PercentFraction lhs, PercentFraction::value_type rhs)
  { return lhs > static_cast<PercentFraction::value_type>(rhs) - 0.05F && lhs < static_cast<PercentFraction::value_type>(rhs) + 0.05F; }

  bool compare(PercentFraction::value_type lhs, PercentFraction rhs)
  { return lhs > static_cast<PercentFraction::value_type>(rhs) - 0.05F && lhs < static_cast<PercentFraction::value_type>(rhs) + 0.05F; }
};

template<>
struct CompareNotEqual<PercentFraction>
{
  bool compare(PercentFraction lhs, PercentFraction rhs)
  { return lhs < static_cast<PercentFraction::value_type>(rhs) - 0.05F || lhs > static_cast<PercentFraction::value_type>(rhs) + 0.05F; }

  bool compare(PercentFraction lhs, PercentFraction::value_type rhs)
  { return lhs < static_cast<PercentFraction::value_type>(rhs) - 0.05F || lhs > static_cast<PercentFraction::value_type>(rhs) + 0.05F; }

  bool compare(PercentFraction::value_type lhs, PercentFraction rhs)
  { return lhs < static_cast<PercentFraction::value_type>(rhs) - 0.05F || lhs > static_cast<PercentFraction::value_type>(rhs) + 0.05F; }
};

template<>
struct CompareLessOrEqual<PercentFraction>
{
  bool compare(PercentFraction lhs, PercentFraction rhs)
  { return lhs < static_cast<PercentFraction::value_type>(rhs) + 0.05F; }

  bool compare(PercentFraction lhs, PercentFraction::value_type rhs)
  { return lhs < static_cast<PercentFraction::value_type>(rhs) + 0.05F; }

  bool compare(PercentFraction::value_type lhs, PercentFraction rhs)
  { return lhs < static_cast<PercentFraction::value_type>(rhs) + 0.05F; }
};

template<>
struct CompareGreaterOrEqual<PercentFraction>
{
  bool compare(PercentFraction lhs, PercentFraction rhs)
  { return lhs > static_cast<PercentFraction::value_type>(rhs) - 0.05F; }

  bool compare(PercentFraction lhs, PercentFraction::value_type rhs)
  { return lhs > static_cast<PercentFraction::value_type>(rhs) - 0.05F; }

  bool compare(PercentFraction::value_type lhs, PercentFraction rhs)
  { return lhs > static_cast<PercentFraction::value_type>(rhs) - 0.05F; }
};

// Floating point types don't implement modulo, increment or
// decrement, so make them a no-op.
template<>
struct OperationModulo<PercentFraction>
{
  PercentFraction eval(PercentFraction lhs,             PercentFraction /* rhs */) { return lhs; }
  PercentFraction eval(PercentFraction lhs, PercentFraction::value_type /* rhs */) { return lhs; }
};

template<>
struct OperationModuloAssign<PercentFraction>
{
  PercentFraction eval(PercentFraction lhs,             PercentFraction /* rhs */) { return lhs; }
  PercentFraction eval(PercentFraction lhs, PercentFraction::value_type /* rhs */) { return lhs; }
};

template<>
struct OperationIncrement<PercentFraction>
{
  PercentFraction eval(PercentFraction lhs,             PercentFraction /* rhs */) { return lhs; }
  PercentFraction eval(PercentFraction lhs, PercentFraction::value_type /* rhs */) { return lhs; }
};

template<>
struct OperationDecrement<PercentFraction>
{
  PercentFraction eval(PercentFraction lhs,             PercentFraction /* rhs */) { return lhs; }
  PercentFraction eval(PercentFraction lhs, PercentFraction::value_type /* rhs */) { return lhs; }
};


INSTANTIATE_TYPED_TEST_CASE_P(PercentFraction, NumericTest, PercentFraction);

namespace
{

  NumericTest<PercentFraction>::test_str init_strings[] =
    { // str     pos        strpass pospass
      {"-42.12", -42.12F,   false,  false},
      {"1.0",    -53.0F,    true,   false},
      {"82.232",  82.232F,  false,  false},
      {"0.23",     0.23F,   true,   true},
      {"0.0001",   0.0001F, true,   true},
      {"0",        0.0F,    true,   true},
      {"1",        1.0F,    true,   true},
      {"1.001",    1.001F,  false,  false},
      {"1.0",     -0.1F,    true,   false},
      {"invalid",  1.0F,    false,  true},
    };

  NumericTest<PercentFraction>::test_op init_ops[] =
    { // v1      v2        expected           operation         pass   except rhsexcept
      {0.0F,     0.0F,               1.0F,    EQUAL,            true,  false, false},
      {1.0F,     1.0F,               1.0F,    EQUAL,            true,  false, false},
      {0.822F,   0.822F,             1.0F,    EQUAL,            true,  false, false},
      {0.230F,   0.230F,             1.0F,    EQUAL,            true,  false, false},
      {0.23F,    0.48F,              0.0F,    EQUAL,            false, false, false},
      {0.23F,    0.35F,              1.0F,    NOT_EQUAL,        true,  false, false},
      {0.54F,    0.54F,              0.0F,    NOT_EQUAL,        false, false, false},

      {0.5432F,  0.8272F,            1.0F,    LESS,             true,  false, false},
      {0.5340F,  0.5340F,            0.0F,    LESS,             false, false, false},
      {0.9470F,  0.340F,             0.0F,    LESS,             false, false, false},
      {0.54320F, 0.82720F,           1.0F,    LESS_OR_EQUAL,    true,  false, false},
      {0.5340F,  0.5340F,            1.0F,    LESS_OR_EQUAL,    true,  false, false},
      {0.9470F,  0.340F,             0.0F,    LESS_OR_EQUAL,    false, false, false},

      {0.908F,   0.842F,             1.0F,    GREATER,          true,  false, false},
      {0.3622F,  0.3622F,            0.0F,    GREATER,          false, false, false},
      {0.0872F,  0.2701F,            0.0F,    GREATER,          false, false, false},
      {0.908F,   0.842F,             1.0F,    GREATER_OR_EQUAL, true,  false, false},
      {0.3622F,  0.3622F,            1.0F,    GREATER_OR_EQUAL, true,  false, false},
      {0.0872F,  0.2701F,            0.0F,    GREATER_OR_EQUAL, false, false, false},

      {0.432F,   0.0743F,  0.432F +  0.0743F, ADD,              true,  false, false},
      {0.432F,  -0.074F,   0.432F -  0.074F,  ADD,              true,  false, true},
      {0.432F,  -0.743F,   0.432F -  0.743F,  ADD,              false, true,  true},
      {0.432F,   0.8F,     0.432F +  0.8F,    ADD,              false, true,  false},
      {0.432F,   0.0743F,            1.0F,    ADD,              false, false, false},
      {0.823F,   0.093F,   0.823F +  0.093F,  ADD_ASSIGN,       true,  false, false},
      {0.823F,  -0.093F,   0.823F -  0.093F,  ADD_ASSIGN,       true,  false, true},
      {0.823F,  -0.932F,   0.823F -  0.932F,  ADD_ASSIGN,       false, true,  true},
      {0.823F,   0.93F,    0.823F +  0.93F,   ADD_ASSIGN,       false, true,  false},
      {0.823F,   0.093F,             0.1F,    ADD_ASSIGN,       false, false, false},

      {0.432F,   0.285F,   0.432F -  0.285F,  SUBTRACT,         true,  false, false},
      {0.432F,  -0.285F,   0.432F +  0.285F,  SUBTRACT,         true,  false, true},
      {0.432F,   0.763F,   0.432F -  0.763F,  SUBTRACT,         false, true,  false},
      {0.432F,   0.285F,             0.1F,    SUBTRACT,         false, false, false},
      {0.432F,   0.431F,             0.001F,  SUBTRACT,         true,  false, false},
      {0.432F,   0.432F,             0.0F,    SUBTRACT,         true,  false, false},
      {0.432F,   0.433F,            -0.001F,  SUBTRACT,         false, true,  false},
      {0.823F,   0.093F,   0.823F -  0.093F,  SUBTRACT_ASSIGN,  true,  false, false},
      {0.823F,  -0.0932F,  0.823F +  0.0932F, SUBTRACT_ASSIGN,  true,  false, true},
      {0.823F,   0.9139F,  0.823F -  0.9139F, SUBTRACT_ASSIGN,  false, true,  false},
      {0.823F,   0.093F,             0.1F,    SUBTRACT_ASSIGN,  false, false, false},
      {0.823F,   0.822F,             0.001F,  SUBTRACT_ASSIGN,  true,  false, false},
      {0.823F,   0.823F,             0.0F,    SUBTRACT_ASSIGN,  true,  false, false},

      {0.40F,    0.12F,    0.40F *   0.12F,   MULTIPLY,         true,  false, false},
      {0.23F,   -0.8F,     0.23F *  -0.8F,    MULTIPLY,         false, true,  true},
      {0.40F,    0.12F,              0.1F,    MULTIPLY,         false, false, false},
      {0.18F,    0.4F,     0.18F *   0.4F,    MULTIPLY_ASSIGN,  true,  false, false},
      {0.2F,    -0.232F,   0.2F  *  -0.232F,  MULTIPLY_ASSIGN,  false, true,  true},
      {0.18F,    0.4F,               0.1F,    MULTIPLY_ASSIGN,  false, false, false},

      {0.900F,   2.0F,     0.900F /  2.0F,    DIVIDE,           true,  false, true},
      {0.900F,  -2.0F,     0.900F / -2.0F,    DIVIDE,           false, true,  true},
      {0.2F,     0.900F,   0.2F   /  0.900F,  DIVIDE,           true,  false, false},
      {0.5F,     0.901F,   0.5F   /  0.901F,  DIVIDE,           true,  false, false},
      {0.5F,     0.900F,             0.2F,    DIVIDE,           false, false, false},
      {0.480F,   2.0F,     0.480F /  2.0F,    DIVIDE_ASSIGN,    true,  false, true},
      {0.480F,  -2.0F,     0.480F / -2.0F,    DIVIDE_ASSIGN,    false, true,  true},
      {0.480F,   0.480F,   0.480F /  0.480F,  DIVIDE_ASSIGN,    true,  false, false},
      {0.480F,   0.481F,   0.480F /  0.481F,  DIVIDE_ASSIGN,    true,  false, false},
      {0.480F,   0.480F,             0.2F,    DIVIDE_ASSIGN,    false, false, false},

    };

}

template<>
const std::vector<NumericTest<PercentFraction>::test_str>
NumericTest<PercentFraction>::strings(init_strings,
                                      init_strings + boost::size(init_strings));

template<>
const std::vector<NumericTest<PercentFraction>::test_op>
NumericTest<PercentFraction>::ops(init_ops,
                                  init_ops + boost::size(init_ops));

template<>
const PercentFraction::value_type NumericTest<PercentFraction>::error(0.0005F);

template<>
const PercentFraction NumericTest<PercentFraction>::safedefault(0.9F);

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

#include <ome/xml/model/primitives/NonNegativeFloat.h>

#include "constrained-numeric.h"

using ome::xml::model::primitives::NonNegativeFloat;

// Floating point types don't implement modulo, so make it a no-op.
template<>
struct OperationModulo<NonNegativeFloat>
{
  NonNegativeFloat eval(NonNegativeFloat lhs,             NonNegativeFloat rhs) { return lhs; }
  NonNegativeFloat eval(NonNegativeFloat lhs, NonNegativeFloat::value_type rhs) { return lhs; }
};

template<>
struct OperationModuloAssign<NonNegativeFloat>
{
  NonNegativeFloat eval(NonNegativeFloat lhs,             NonNegativeFloat rhs) { return lhs; }
  NonNegativeFloat eval(NonNegativeFloat lhs, NonNegativeFloat::value_type rhs) { return lhs; }
};


INSTANTIATE_TYPED_TEST_CASE_P(NonNegativeFloat, NumericTest, NonNegativeFloat);

namespace
{

  NumericTest<NonNegativeFloat>::test_str init_strings[] =
    { // str     pos      strpass pospass
      {"23",      23.0,   true,   true},
      {"-42.12", -42.12,  false,  false},
      {"1.0",    -53.0,   true,   false},
      {"82.232",  82.232, true,   true},
      {"0",        0,     true,   true},
      {"1.0",     -0.1,   true,   false},
      {"invalid",  1.0,   false,  true},
    };

  NumericTest<NonNegativeFloat>::test_op init_ops[] =
    { // v1     v2      expected        operation         pass   except rhsexcept
      {   23.0,   23.0,            1.0, EQUAL,            true,  false, false},
      {   23.0,  432.0,            0.0, EQUAL,            false, false, false},
      {   23.0,   35.0,            1.0, NOT_EQUAL,        true,  false, false},
      {   54.0,   54.0,            0.0, NOT_EQUAL,        false, false, false},

      { 5432.0, 8272.0,            1.0, LESS,             true,  false, false},
      {  534.0,  534.0,            0.0, LESS,             false, false, false},
      {  947.0,   34.0,            0.0, LESS,             false, false, false},
      { 5432.0, 8272.0,            1.0, LESS_OR_EQUAL,    true,  false, false},
      {  534.0,  534.0,            1.0, LESS_OR_EQUAL,    true,  false, false},
      {  947.0,   34.0,            0.0, LESS_OR_EQUAL,    false, false, false},

      {10873.0, 8420.0,            1.0, GREATER,          true,  false, false},
      { 3622.0, 3622.0,            0.0, GREATER,          false, false, false},
      {  872.0, 2701.0,            0.0, GREATER,          false, false, false},
      {10873.0, 8420.0,            1.0, GREATER_OR_EQUAL, true,  false, false},
      { 3622.0, 3622.0,            1.0, GREATER_OR_EQUAL, true,  false, false},
      {  872.0, 2701.0,            0.0, GREATER_OR_EQUAL, false, false, false},

      {  432.0,  743.0, 432.0 +  743.0, ADD,              true,  false, false},
      {  432.0,  -74.0, 432.0 -   74.0, ADD,              true,  false, true},
      {  432.0, -743.0, 432.0 -  743.0, ADD,              false, true,  true},
      {  432.0,  743.0,            1.0, ADD,              false, false, false},
      {  823.0,   93.0, 823.0 +   93.0, ADD_ASSIGN,       true,  false, false},
      {  823.0,  -93.0, 823.0 -   93.0, ADD_ASSIGN,       true,  false, true},
      {  823.0, -932.0, 823.0 -  932.0, ADD_ASSIGN,       false, true,  true},
      {  823.0,   93.0,            1.0, ADD_ASSIGN,       false, false, false},

      {  432.0,  285.0, 432.0 -  285.0, SUBTRACT,         true,  false, false},
      {  432.0, -285.0, 432.0 +  285.0, SUBTRACT,         true,  false, true},
      {  432.0,  763.0, 432.0 -  763.0, SUBTRACT,         false, true,  false},
      {  432.0,  285.0,            1.0, SUBTRACT,         false, false, false},
      {  432.0,  431.0,            1.0, SUBTRACT,         true,  false, false},
      {  432.0,  432.0,            0.0, SUBTRACT,         true,  false, false},
      {  823.0,   93.0, 823.0 -   93.0, SUBTRACT_ASSIGN,  true,  false, false},
      {  823.0, -932.0, 823.0 +  932.0, SUBTRACT_ASSIGN,  true,  false, true},
      {  823.0, 1393.0, 823.0 - 1393.0, SUBTRACT_ASSIGN,  false, true,  false},
      {  823.0,   93.0,            1.0, SUBTRACT_ASSIGN,  false, false, false},
      {  823.0,  822.0,            1.0, SUBTRACT_ASSIGN,  true,  false, false},
      {  823.0,  823.0,            0.0, SUBTRACT_ASSIGN,  true,  false, false},

      {   40.0,   12.0,  40.0 *   12.0, MULTIPLY,         true,  false, false},
      {   23.0,   -8.0,  23.0 *   -8.0, MULTIPLY,         false, true,  true},
      {   40.0,   12.0,            1.0, MULTIPLY,         false, false, false},
      {   18.0,    4.0,  18.0 *    4.0, MULTIPLY_ASSIGN,  true,  false, false},
      {    2.0, -232.0,   2.0 * -232.0, MULTIPLY_ASSIGN,  false, true,  true},
      {   18.0,    4.0,            1.0, MULTIPLY_ASSIGN,  false, false, false},

      {  900.0,    5.0, 900.0 /    5.0, DIVIDE,           true,  false, false},
      {  900.0,   -5.0, 900.0 /   -5.0, DIVIDE,           false, true,  true},
      {  900.0,  900.0, 900.0 /  900.0, DIVIDE,           true,  false, false},
      {  900.0,  901.0, 900.1 /  901.0, DIVIDE,           true,  false, false},
      {  900.0,  900.0,            2.0, DIVIDE,           false, false, false},
      {  480.0,   20.0, 480.0 /   20.0, DIVIDE_ASSIGN,    true,  false, false},
      {  480.0,  -20.0, 480.0 /  -20.0, DIVIDE_ASSIGN,    false, true,  true},
      {  480.0,  480.0, 480.0 /  480.0, DIVIDE_ASSIGN,    true,  false, false},
      {  480.0,  481.0, 480.0 /  481.0, DIVIDE_ASSIGN,    true,  false, false},
      {  480.0,  480.0,            2.0, DIVIDE_ASSIGN,    false, false, false},

    };

}

template<>
const std::vector<NumericTest<NonNegativeFloat>::test_str>
NumericTest<NonNegativeFloat>::strings(init_strings,
                                       init_strings + (sizeof(init_strings) / sizeof(init_strings[0])));

template<>
const std::vector<NumericTest<NonNegativeFloat>::test_op>
NumericTest<NonNegativeFloat>::ops(init_ops,
                                   init_ops + (sizeof(init_ops) / sizeof(init_ops[0])));

template<>
const NonNegativeFloat::value_type NumericTest<NonNegativeFloat>::error(0.0005);

template<>
const NonNegativeFloat NumericTest<NonNegativeFloat>::safedefault(9999.0);

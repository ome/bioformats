/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2006 - 2016 Open Microscopy Environment:
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

#include <ome/xml/model/primitives/NonNegativeLong.h>

#include "constrained-numeric.h"

using ome::xml::model::primitives::NonNegativeLong;

INSTANTIATE_TYPED_TEST_CASE_P(NonNegativeLong, NumericTest, NonNegativeLong);

namespace
{

  NumericTest<NonNegativeLong>::test_str init_strings[] =
    { // str        pos         strpass pospass
      {"743544628", 743544628L, true,   true},
      {"23",                23, true,   true},
      {"-1",                -1, false,  false},
      {"-42",              -42, false,  false},
      {"1",                -53, true,   false},
      {"82",                82, true,   true},
      {"0",                  0,  true,  true},
      {"invalid",            1,  false, true},
    };

  NumericTest<NonNegativeLong>::test_op init_ops[] =
    { // v1   v2    expected    operation         pass   except rhsexcept
      {   23,   23,          1, EQUAL,            true,  false, false},
      {   23,  432,          0, EQUAL,            false, false, false},
      {   23,   35,          1, NOT_EQUAL,        true,  false, false},
      {   54,   54,          0, NOT_EQUAL,        false, false, false},

      { 5432, 8272,          1, LESS,             true,  false, false},
      {  534,  534,          0, LESS,             false, false, false},
      {  947,   34,          0, LESS,             false, false, false},
      { 5432, 8272,          1, LESS_OR_EQUAL,    true,  false, false},
      {  534,  534,          1, LESS_OR_EQUAL,    true,  false, false},
      {  947,   34,          0, LESS_OR_EQUAL,    false, false, false},

      {10873, 8420,          1, GREATER,          true,  false, false},
      { 3622, 3622,          0, GREATER,          false, false, false},
      {  872, 2701,          0, GREATER,          false, false, false},
      {10873, 8420,          1, GREATER_OR_EQUAL, true,  false, false},
      { 3622, 3622,          1, GREATER_OR_EQUAL, true,  false, false},
      {  872, 2701,          0, GREATER_OR_EQUAL, false, false, false},

      {  432,  743, 432 +  743, ADD,              true,  false, false},
      {  432,  -74, 432 -   74, ADD,              true,  false, true},
      {  432, -743, 432 -  743, ADD,              false, true,  true},
      {  432,  743,          1, ADD,              false, false, false},
      {  823,   93, 823 +   93, ADD_ASSIGN,       true,  false, false},
      {  823,  -93, 823 -   93, ADD_ASSIGN,       true,  false, true},
      {  823, -932, 823 -  932, ADD_ASSIGN,       false, true,  true},
      {  823,   93,          1, ADD_ASSIGN,       false, false, false},

      {  432,  285, 432 -  285, SUBTRACT,         true,  false, false},
      {  432, -285, 432 +  285, SUBTRACT,         true,  false, true},
      {  432,  763, 432 -  763, SUBTRACT,         false, true,  false},
      {  432,  285,          1, SUBTRACT,         false, false, false},
      {  432,  431,          1, SUBTRACT,         true,  false, false},
      {  432,  432,          0, SUBTRACT,         true,  false, false},
      {  432,  433,         -1, SUBTRACT,         false, true,  false},
      {  823,   93, 823 -   93, SUBTRACT_ASSIGN,  true,  false, false},
      {  823, -932, 823 +  932, SUBTRACT_ASSIGN,  true,  false, true},
      {  823, 1393, 823 - 1393, SUBTRACT_ASSIGN,  false, true,  false},
      {  823,   93,          1, SUBTRACT_ASSIGN,  false, false, false},
      {  823,  822,          1, SUBTRACT_ASSIGN,  true,  false, false},
      {  823,  823,          0, SUBTRACT_ASSIGN,  true,  false, false},
      {  823,  824,         -1, SUBTRACT_ASSIGN,  false, true,  false},

      {   40,   12,  40 *   12, MULTIPLY,         true,  false, false},
      {   23,   -8,  23 *   -8, MULTIPLY,         false, true,  true},
      {   40,   12,          1, MULTIPLY,         false, false, false},
      {   18,    4,  18 *    4, MULTIPLY_ASSIGN,  true,  false, false},
      {    2, -232,   2 * -232, MULTIPLY_ASSIGN,  false, true,  true},
      {   18,    4,          1, MULTIPLY_ASSIGN,  false, false, false},

      {  900,    5, 900 /    5, DIVIDE,           true,  false, false},
      {  900,   -5, 900 /   -5, DIVIDE,           false, true,  true},
      {  900,  900, 900 /  900, DIVIDE,           true,  false, false},
      {  900,  901,          0, DIVIDE,           true,  false, false},
      {  900,  900,          2, DIVIDE,           false, false, false},
      {  480,   20, 480 /   20, DIVIDE_ASSIGN,    true,  false, false},
      {  480,  -20, 480 /  -20, DIVIDE_ASSIGN,    false, true,  true},
      {  480,  480, 480 /  480, DIVIDE_ASSIGN,    true,  false, false},
      {  480,  481,          0, DIVIDE_ASSIGN,    true,  false, false},
      {  480,  480,          2, DIVIDE_ASSIGN,    false, false, false},

      {  901,  900,          1, MODULO,           true,  false, false},
      {  901,  900,          2, MODULO,           false, false, false},
      {   43,   43,          0, MODULO,           true,  false, false},
      {  901,  900,          1, MODULO_ASSIGN,    true,  false, false},
      {  901,  900,          2, MODULO_ASSIGN,    false, false, false},
      {   43,   43,          0, MODULO_ASSIGN,    true,  false, false},

      {  33,     0,         34, INCREMENT,        true,  false, false},
      {  33,     0,         33, INCREMENT,        false, false, false},
      {   0,     0,          1, INCREMENT,        true,  false, false},
      {  33,     0,         32, DECREMENT,        true,  false, false},
      {  33,     0,         33, DECREMENT,        false, false, false},
      {   0,     0,          0, DECREMENT,        false, true,  false}
    };

}

template<>
const std::vector<NumericTest<NonNegativeLong>::test_str>
NumericTest<NonNegativeLong>::strings(init_strings,
                                      init_strings + boost::size(init_strings));

template<>
const std::vector<NumericTest<NonNegativeLong>::test_op>
NumericTest<NonNegativeLong>::ops(init_ops,
                                  init_ops + boost::size(init_ops));

template<>
const NonNegativeLong::value_type NumericTest<NonNegativeLong>::error(0);

template<>
const NonNegativeLong NumericTest<NonNegativeLong>::safedefault(9999);

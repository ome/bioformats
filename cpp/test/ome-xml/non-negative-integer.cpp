#include <ome/xml/model/primitives/NonNegativeInteger.h>

#include "constrained-numeric.h"

using ome::xml::model::primitives::NonNegativeInteger;

INSTANTIATE_TYPED_TEST_CASE_P(NonNegativeInteger, NumericTest, NonNegativeInteger);

namespace
{

  NumericTest<NonNegativeInteger>::test_str init_strings[] =
    { // str      pos  strpass pospass
      {"23",       23, true,   true},
      {"-1",       -1, false,  false},
      {"-42",     -42, false,  false},
      {"1",       -53, true,   false},
      {"82",       82, true,   true},
      {"0",         0,  true,  true},
      {"invalid",   1,  false, true},
    };

  NumericTest<NonNegativeInteger>::test_op init_ops[] =
    { // v1   v2    expected    operation         pass   except rhsexcept
      {23,      23,          1, EQUAL,            true,  false, false},
      {23,     432,          0, EQUAL,            false, false, false},
      {23,      35,          1, NOT_EQUAL,        true,  false, false},
      {54,      54,          0, NOT_EQUAL,        false, false, false},

      {5432,  8272,          1, LESS,             true,  false, false},
      {534,    534,          0, LESS,             false, false, false},
      {947,     34,          0, LESS,             false, false, false},
      {5432,  8272,          1, LESS_OR_EQUAL,    true,  false, false},
      {534,    534,          1, LESS_OR_EQUAL,    true,  false, false},
      {947,     34,          0, LESS_OR_EQUAL,    false, false, false},

      {10873, 8420,          1, GREATER,          true,  false, false},
      {3622,  3622,          0, GREATER,          false, false, false},
      {872,   2701,          0, GREATER,          false, false, false},
      {10873, 8420,          1, GREATER_OR_EQUAL, true,  false, false},
      {3622,  3622,          1, GREATER_OR_EQUAL, true,  false, false},
      {872,   2701,          0, GREATER_OR_EQUAL, false, false, false},

      {432,    743,  432 + 743, ADD,              true,  false, false},
      {432,    -74,  432 -  74, ADD,              true,  false, true},
      {432,   -743,  432 - 743, ADD,              false, true,  true},
      {432,    743,          1, ADD,              false, false, false},
      {823,     93,  823 +  93, ADD_ASSIGN,       true,  false, false},
      {823,    -93,  823 -  93, ADD_ASSIGN,       true,  false, true},
      {823,   -932,  823 - 932, ADD_ASSIGN,       false, true,  true},
      {823,     93,          1, ADD_ASSIGN,       false, false, false},

      {432,    285, 432 -  285, SUBTRACT,         true,  false, false},
      {432,   -285, 432 +  285, SUBTRACT,         true,  false, true},
      {432,    763, 432 -  763, SUBTRACT,         false, true,  false},
      {432,    285,          1, SUBTRACT,         false, false, false},
      {432,    431,          1, SUBTRACT,         true,  false, false},
      {432,    432,          0, SUBTRACT,         true,  false, false},
      {432,    433,         -1, SUBTRACT,         false, true,  false},
      {823,     93, 823 -   93, SUBTRACT_ASSIGN,  true,  false, false},
      {823,   -932, 823 +  932, SUBTRACT_ASSIGN,  true,  false, true},
      {823,   1393, 823 - 1393, SUBTRACT_ASSIGN,  false, true,  false},
      {823,     93,          1, SUBTRACT_ASSIGN,  false, false, false},
      {823,    822,          1, SUBTRACT_ASSIGN,  true,  false, false},
      {823,    823,          0, SUBTRACT_ASSIGN,  true,  false, false},
      {823,    824,         -1, SUBTRACT_ASSIGN,  false, true,  false},

      {40,      12,  40 *   12, MULTIPLY,         true,  false, false},
      {23,      -8,  23 *   -8, MULTIPLY,         false, true,  true},
      {40,      12,          1, MULTIPLY,         false, false, false},
      {18,       4,  18 *    4, MULTIPLY_ASSIGN,  true,  false, false},
      {2,     -232,   2 * -232, MULTIPLY_ASSIGN,  false, true,  true},
      {18,       4,          1, MULTIPLY_ASSIGN,  false, false, false},

      {900,      5,  900 /   5, DIVIDE,           true,  false, false},
      {900,     -5,  900 /  -5, DIVIDE,           false, true,  true},
      {900,    900,  900 / 900, DIVIDE,           true,  false, false},
      {900,    901,          0, DIVIDE,           true,  false, false},
      {900,    900,          2, DIVIDE,           false, false, false},
      {480,     20,  480 /  20, DIVIDE_ASSIGN,    true,  false, false},
      {480,    -20,  480 / -20, DIVIDE_ASSIGN,    false, true,  true},
      {480,    480,  480 / 480, DIVIDE_ASSIGN,    true,  false, false},
      {480,    481,          0, DIVIDE_ASSIGN,    true,  false, false},
      {480,    480,          2, DIVIDE_ASSIGN,    false, false, false},

      {901,    900,          1, MODULO,           true,  false, false},
      {901,    900,          2, MODULO,           false, false, false},
      {43,      43,          0, MODULO,           true,  false, false},
      {901,    900,          1, MODULO_ASSIGN,    true,  false, false},
      {901,    900,          2, MODULO_ASSIGN,    false, false, false},
      {43,      43,          0, MODULO_ASSIGN,    true,  false, false}
    };

}

template<>
const std::vector<NumericTest<NonNegativeInteger>::test_str>
NumericTest<NonNegativeInteger>::strings(init_strings,
                                         init_strings + (sizeof(init_strings) / sizeof(init_strings[0])));

template<>
const std::vector<NumericTest<NonNegativeInteger>::test_op>
NumericTest<NonNegativeInteger>::ops(init_ops,
                                     init_ops + (sizeof(init_ops) / sizeof(init_ops[0])));

template<>
const NonNegativeInteger::value_type NumericTest<NonNegativeInteger>::error(0);

template<>
const NonNegativeInteger NumericTest<NonNegativeInteger>::safedefault(9999);

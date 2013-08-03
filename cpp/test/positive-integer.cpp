#include <ome/xml/model/primitives/PositiveInteger.h>

#include "constrained-numeric.h"

#include <cassert>
#include <cstdlib>
#include <sstream>
#include <iostream>

using ome::xml::model::primitives::PositiveInteger;

int
main ()
{
  constrained_numeric_test<PositiveInteger> init_tests[] =
    {
      // str    pos  less op  add     sub    mul    div    except opexcept exact
      {"23",    23,  12,  2,  23+2,   23-2,  23*2,  23/2,  false, false, true},
      {"23",    23,  10,  15, 23+15,  23-15, 23*15, 23/15, false, false, true},
      {"-42",   -42, 1,   1,  1,      1,     1,     1,     true,  false, true},
      {"1",     -42, 1,   1,  1,      1,     1,     1,     true,  false, true},
      {"82",    82,  23,  4,  82+4,   82-4,    82*4,     82/4,     false, false, true},
      {"0",     0,   1,   1,  1,      1,     1,     1,     true,  false, true},
      {"1",     0,   1,   1,  1,      1,     1,     1,     true,  false, true},
      {"invld", 1,   1,   1,  1,      1,     1,     1,     true,  false, true}
    };

  std::vector<constrained_numeric_test<PositiveInteger> > tests(init_tests,
                                                              init_tests + (sizeof(init_tests) / sizeof(init_tests[0])));

  for (std::vector<constrained_numeric_test<PositiveInteger> >::const_iterator t = tests.begin();
       t != tests.end();
       ++t)
    {
      std::cout << t->string_val << std::endl;
      verify(*t);
    }

  std::cout << "OK" << std::endl;
}

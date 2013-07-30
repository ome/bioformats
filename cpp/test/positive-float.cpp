#include <ome/xml/model/primitives/PositiveFloat.h>

#include <test/constrained-numeric.h>

#include <cassert>
#include <cstdlib>
#include <sstream>
#include <iostream>

using ome::xml::model::primitives::PositiveFloat;

int
main ()
{
  constrained_numeric_test<PositiveFloat> init_tests[] =
    {
      // str     pos     less    op      add            sub            mul            div            except opexcept exact
      {"23.5",   23.5,   12.543, 2.0,    23.5+2.0,      23.5-2.0,      23.5*2.0,      23.5/2.0,      false, false, false},
      {"0.12",   0.12,   0.05,   0.77,   0.12+0.77,     0.12-0.77,     0.12*0.77,     0.12/0.77,     false, true,  false},
      {"23.454", 23.454, 10.43,  15.784, 23.454+15.784, 23.454-15.784, 23.454*15.784, 23.454/15.784, false, false, false},
      {"-42.32", -42.32, 1.0,    1.0,    1.0,           1.0,           1.0,           1.0,           true,  false, false},
      {"1.0",    -42.32, 1.0,    1.0,    1.0,           1.0,           1.0,           1.0,           true,  false, false},
      {"82.54",  82.54,  23.4,   1.0,    1.0,           1.0,           1.0,           1.0,           false, false, false},
      {"0.0",    0.0,    1.0,    1.0,    1.0,           1.0,           1.0,           1.0,           true,  false, false},
      {"1.0",    0.0,    1.0,    1.0,    1.0,           1.0,           1.0,           1.0,           true,  false, false},
      {"invld",  1.0,    1.0,    1.0,    1.0,           1.0,           1.0,           1.0,           true,  false, false}
    };

  std::vector<constrained_numeric_test<PositiveFloat> > tests(init_tests,
                                                              init_tests + (sizeof(init_tests) / sizeof(init_tests[0])));

  for (std::vector<constrained_numeric_test<PositiveFloat> >::const_iterator t = tests.begin();
       t != tests.end();
       ++t)
    {
      std::cout << t->string_val << std::endl;
      verify(*t);
    }

  std::cout << "OK" << std::endl;
}

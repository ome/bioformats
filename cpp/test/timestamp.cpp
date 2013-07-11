#include <ome/xml/model/primitives/Timestamp.h>

#include <cassert>
#include <cstdlib>
#include <sstream>
#include <iostream>

using ome::xml::model::primitives::Timestamp;

int
main()
{
  Timestamp t1("2011-10-20T09:30:10-05:00");
  std::cout << t1 << std::endl;

  Timestamp t2("2011-10-20T09:30:10Z+03:00");
  std::cout << t2 << std::endl;

  try
    {
  Timestamp t3("invalid");
  std::cout << t3 << std::endl;
    }
  catch (const std::exception& e)
    {
      std::cout << typeid(e).name() << std::endl;
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  try
    {
  Timestamp t3("1200-12-04");
  std::cout << t3 << std::endl;
    }
  catch (const std::exception& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  try
    {
  Timestamp t3("187332-12-04");
  std::cout << t3 << std::endl;
    }
  catch (const std::logic_error& e)
    {
      std::cout << "Caught expected exception: " << e.what() << std::endl;
    }

  std::cout << "OK" << std::endl;
}

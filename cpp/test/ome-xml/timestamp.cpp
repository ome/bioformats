#include <ome/xml/model/primitives/Timestamp.h>

#include <sstream>
#include <stdexcept>
#include <iostream>

#include <gtest/gtest.h>

using ome::xml::model::primitives::Timestamp;

class TimestampTestParameters
{
public:
  std::string input;
  bool        throws;

  TimestampTestParameters(const std::string& input,
                          bool               throws):
    input(input),
    throws(throws)
  {}
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const TimestampTestParameters& params)
{
  return os << params.input;
}


TimestampTestParameters params[] =
  {
    // input                         throws
    TimestampTestParameters("2003-08-26T19:46:38",          false),
    TimestampTestParameters("2003-08-26T19:46:38.762",      false),
    TimestampTestParameters("2003-08-26T19:46:38.762Z",     false),
    TimestampTestParameters("2003-08-26T19:46:38.762+0400", false),
    TimestampTestParameters("invalid",                      true),
    TimestampTestParameters("2011-10-20T15:07:14",          false),
    TimestampTestParameters("2011-10-20T15:07:14",          false),
    TimestampTestParameters("2011-10-20T15:07:14Z",         false),
    TimestampTestParameters("2011-10-20T15:07:14.632Z",     false),
    TimestampTestParameters("1200-12-04",                   true), // Too far in the past
    TimestampTestParameters("187332-12-04",                 true)  // Too far in the future
  };

class TimestampTest : public ::testing::TestWithParam<TimestampTestParameters>
{
};

TEST_P(TimestampTest, Construct)
{
  const TimestampTestParameters& params = GetParam();

  if (params.throws)
    {
      ASSERT_THROW(Timestamp(params.input), std::logic_error); 
    }
  else
    {
      ASSERT_NO_THROW(Timestamp(params.input)); 
    }
  std::cout << Timestamp(params.input) << std::endl;
}

INSTANTIATE_TEST_CASE_P(TimestampVariants, TimestampTest, ::testing::ValuesIn(params));

TEST(TimestampTest, Old)
{
//  Timestamp t1("2011-10-20T09:30:10-05:00");
//  std::cout << t1 << std::endl;

  Timestamp t2("20111020T093010.654");
  //  Timestamp t2("2011-10-20T09:30:10.654");
 std::cout << t2 << std::endl;

  Timestamp t4;
  std::cout << t4 << std::endl;

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

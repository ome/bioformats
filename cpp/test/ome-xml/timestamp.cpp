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
  long        seconds;
  long        milliseconds;
  std::string output;

  TimestampTestParameters(const std::string& input,
                          bool               throws,
                          long               seconds,
                          long               milliseconds,
                          const std::string& output):
    input(input),
    throws(throws),
    seconds(seconds),
    milliseconds(milliseconds),
    output(output)
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
    //                      input                           throws seconds     ms   output
    TimestampTestParameters("2003-08-26T19:46:38",          false, 1061927198,   0, "2003-08-26T19:46:38Z"),
    TimestampTestParameters("2003-08-26T19:46:38.762",      false, 1061927198, 762, "2003-08-26T19:46:38.762000Z"),
    TimestampTestParameters("2003-08-26T19:46:38.762Z",     false, 1061927198, 762, "2003-08-26T19:46:38.762000Z"),
    TimestampTestParameters("2003-08-26T19:46:38.762-0300", false, 1061937998, 762, "2003-08-26T22:46:38.762000Z"),
    TimestampTestParameters("2003-08-26T19:46:38.762+0400", false, 1061912798, 762, "2003-08-26T15:46:38.762000Z"),
    TimestampTestParameters("2003-08-26T19:46:38.762-1130", false, 1061968598, 762, "2003-08-27T07:16:38.762000Z"),
    TimestampTestParameters("2003-08-26T19:46:38.762+",     true,  0,          762, ""), // Time difference is invalid
    TimestampTestParameters("2003-08-26T19:46:38.762-",     true,  0,          762, ""), // Time difference is invalid
    TimestampTestParameters("invalid",                      true,  0,            0, ""), // Invalid
    TimestampTestParameters("2011-10-20T09:30:10-05:00",    true,  0,            0, ""), // Time difference is invalid
    TimestampTestParameters("2011-10-20T15:07:14",          false, 1319123234,   0, "2011-10-20T15:07:14Z"),
    TimestampTestParameters("2011-10-20T15:07:14.312",      false, 1319123234, 312, "2011-10-20T15:07:14.312000Z"),
    TimestampTestParameters("2011-10-20T15:07:14Z",         false, 1319123234,   0, "2011-10-20T15:07:14Z"),
    TimestampTestParameters("2011-10-20T15:07:14.632Z",     false, 1319123234, 632, "2011-10-20T15:07:14.632000Z"),
    TimestampTestParameters("1200-12-04T13:12:12",          true,  0,            0, ""), // Too far in the past
    TimestampTestParameters("20111020T093010.654",          true,  0,            0, "")  // Invalid
  };

class TimestampTest : public ::testing::TestWithParam<TimestampTestParameters>
{
};

TEST_P(TimestampTest, Construct)
{
  const TimestampTestParameters& params = GetParam();

  if (params.throws)
    {
      // TODO: Use more specific exception
      ASSERT_THROW(Timestamp(params.input), std::exception);
    }
  else
    {
      ASSERT_NO_THROW(Timestamp(params.input));
    }
}

TEST_P(TimestampTest, StreamOutput)
{
  const TimestampTestParameters& params = GetParam();

    if (!params.throws)
    {
      Timestamp t(params.input);
      std::ostringstream os;
      os << t;
      ASSERT_EQ(os.str(), params.output);
    }
}

TEST_P(TimestampTest, StreamInput)
{
  const TimestampTestParameters& params = GetParam();

  Timestamp t;
  std::istringstream is(params.input);
  if (params.throws)
    {
      ASSERT_THROW(is >> t, std::exception);
    }
  else
    {
      ASSERT_NO_THROW(is >> t);
      std::ostringstream os;
      os << t;
      ASSERT_EQ(os.str(), params.output);
    }
}

TEST_P(TimestampTest, TimeFromEpoch)
{
  const TimestampTestParameters& params = GetParam();

  if (!params.throws)
    {
      boost::posix_time::ptime epoch(boost::posix_time::from_time_t(0));
      Timestamp ts(params.input);

      boost::posix_time::time_duration duration(static_cast<boost::posix_time::ptime>(ts) - epoch);

      long seconds(duration.total_seconds());
      long nanoseconds(duration.total_nanoseconds());
      long roundedns(seconds * 1000000000);
      long relativens(nanoseconds - roundedns);
      long milliseconds(relativens / 1000000);

      ASSERT_EQ(seconds, params.seconds);
      ASSERT_EQ(milliseconds, params.milliseconds);
    }
}

INSTANTIATE_TEST_CASE_P(TimestampVariants, TimestampTest, ::testing::ValuesIn(params));

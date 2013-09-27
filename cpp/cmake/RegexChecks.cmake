include(CheckCXXSourceRuns)
#regex
check_cxx_source_runs(
"#include <regex>

int main() {
  std::regex foo(\"^foo[bar]\$\");
  std::regex bar(\"^foo[bar]\$\", std::regex::extended);
  std::regex check(\"^[^:/,.][^:/,]*\$\", std::regex::extended);
}"
STD_REGEX)

# regex broken
check_cxx_source_runs(
"#include <regex>

int main() {
  std::regex foo(\"^foo[bar]\$\");
  std::regex bar(\"^foo[bar]\$\", std::regex::extended);
}"
STD_REGEX_BROKEN)

# tr1 regex
check_cxx_source_runs(
"#include <tr1/regex>

int main() {
  std::tr1::regex foo(\"^foo[bar]\$\");
  std::tr1::regex bar(\"^foo[bar]\$\", std::tr1::regex::extended);
  std::tr1::regex check(\"^[^:/,.][^:/,]*\$\", std::tr1::regex::extended);
}"
TR1_REGEX)

# tr1 regex broken
check_cxx_source_runs(
"#include <tr1/regex>

int main() {
  std::tr1::regex foo(\"^foo[bar]\$\");
  std::tr1::regex bar(\"^foo[bar]\$\", std::tr1::regex::extended);
}"
TR1_REGEX_BROKEN)

# boost regex
SET(CMAKE_REQUIRED_LIBRARIES_SAVE ${CMAKE_REQUIRED_LIBRARIES})
SET(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES} ${Boost_REGEX_LIBRARY_RELEASE})
check_cxx_source_runs(
"#include <boost/regex.hpp>

int main() {
  boost::regex(\"^foo[bar]\$\");
  boost::regex bar(\"^foo[bar]\$\", boost::regex::extended);
}"
BOOST_REGEX)
SET(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES_SAVE})

if(NOT STD_REGEX OR NOT STD_REGEX_BROKEN)
  if(NOT TR1_REGEX OR NOT TR1_REGEX_BROKEN)
    set(REGEX_LIBRARY ${Boost_REGEX_LIBRARY_RELEASE})
  endif(NOT TR1_REGEX OR NOT TR1_REGEX_BROKEN)
endif(NOT STD_REGEX OR NOT STD_REGEX_BROKEN)

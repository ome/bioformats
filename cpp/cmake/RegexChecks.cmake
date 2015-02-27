# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2015 Open Microscopy Environment:
#   - Massachusetts Institute of Technology
#   - National Institutes of Health
#   - University of Dundee
#   - Board of Regents of the University of Wisconsin-Madison
#   - Glencoe Software, Inc.
# %%
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# The views and conclusions contained in the software and documentation are
# those of the authors and should not be interpreted as representing official
# policies, either expressed or implied, of any organization.
# #L%

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
set(CMAKE_REQUIRED_LIBRARIES_SAVE ${CMAKE_REQUIRED_LIBRARIES})
set(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES} ${Boost_REGEX_LIBRARY_RELEASE})
check_cxx_source_runs(
"#include <boost/regex.hpp>

int main() {
  boost::regex(\"^foo[bar]\$\");
  boost::regex bar(\"^foo[bar]\$\", boost::regex::extended);
}"
BOOST_REGEX)
set(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES_SAVE})

if(NOT STD_REGEX OR NOT STD_REGEX_BROKEN)
  if(NOT TR1_REGEX OR NOT TR1_REGEX_BROKEN)
    set(REGEX_LIBRARY ${Boost_REGEX_LIBRARY_RELEASE})
  endif(NOT TR1_REGEX OR NOT TR1_REGEX_BROKEN)
endif(NOT STD_REGEX OR NOT STD_REGEX_BROKEN)

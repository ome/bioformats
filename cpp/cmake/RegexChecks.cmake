# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2016 Open Microscopy Environment:
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

function(regex_test namespace header includedirs library outvar outlib)
  set(CMAKE_REQUIRED_INCLUDES ${CMAKE_REQUIRED_INCLUDES} ${includedirs})
  set(CMAKE_REQUIRED_LIBRARIES ${CMAKE_REQUIRED_LIBRARIES} ${library})
  check_cxx_source_runs(
"#include <${header}>
#include <iostream>

int main() {
  ${namespace} foo(\"^foo[bar]\$\");
  ${namespace} bar(\"^foo[bar]\$\", ${namespace}::extended);
  ${namespace} chk(\"^[^:/,.][^:/,]*\$\", ${namespace}::extended);

  std::string test(\"foob\");
  std::string fail(\"fail:\");

  if (!${namespace}_search(test, foo)) return 1;
  if (!${namespace}_search(test, bar)) return 2;
  if (!${namespace}_search(test, chk)) return 3;
  if (${namespace}_search(fail, foo)) return 4;
  if (${namespace}_search(fail, bar)) return 5;
  if (${namespace}_search(fail, chk)) return 6;

  if (!${namespace}_match(test, foo)) return 7;
  if (!${namespace}_match(test, bar)) return 8;
  if (!${namespace}_match(test, chk)) return 9;
  if (${namespace}_match(fail, foo)) return 10;
  if (${namespace}_match(fail, bar)) return 11;
  if (${namespace}_match(fail, chk)) return 12;

  // Checks for broken support in GCC 4.9 and 5.1
  ${namespace} range1(\"^[a-z0-9][a-z0-9-]*\$\", ${namespace}::extended);
  ${namespace} range2(\"^[a-z0-9][-a-z0-9]*\$\", ${namespace}::extended);
  if (!${namespace}_match(test, range1)) return 13;
  if (!${namespace}_match(test, range2)) return 14;
  if (!${namespace}_match(\"a-\", range1)) return 15;
  if (!${namespace}_match(\"a-\", range2)) return 16;
  if (${namespace}_match(\"-a\", range1)) return 17;
  if (${namespace}_match(\"-a\", range2)) return 18;

  return 0;
}"
${outvar})

  set(${outvar} ${${outvar}} PARENT_SCOPE)
  if (${outvar})
    set(${outlib} ${library} PARENT_SCOPE)
  endif(${outvar})
endfunction(regex_test)

regex_test(std::regex regex "" "" OME_HAVE_REGEX REGEX_LIBRARY)
if(NOT OME_HAVE_REGEX)
  regex_test(std::tr1::regex tr1/regex "" "" OME_HAVE_TR1_REGEX REGEX_LIBRARY)
  if(NOT OME_HAVE_TR1_REGEX)
    regex_test(boost::regex boost/regex.hpp "${Boost_INCLUDE_DIRS}" "${Boost_REGEX_LIBRARY_RELEASE}" OME_HAVE_BOOST_REGEX REGEX_LIBRARY)
    if(NOT OME_HAVE_BOOST_REGEX)
      message(FATAL_ERROR "No working regular expression implementation found")
    endif(NOT OME_HAVE_BOOST_REGEX)
  endif(NOT OME_HAVE_TR1_REGEX)
endif(NOT OME_HAVE_REGEX)

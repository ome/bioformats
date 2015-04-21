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

if(BUILD_TESTS)
  include_directories(${OME_TOPLEVEL_INCLUDES}
                      ${Boost_INCLUDE_DIRS})

  if(extended-tests)
    header_test_from_file(ome-compat ome-compat ome/compat)
    bf_add_test(ome-compat/headers ome-compat-headers)
  endif(extended-tests)

  add_executable(array array.cpp)
  target_link_libraries(array ome-compat)
  target_link_libraries(array ome-test)

  bf_add_test(ome-compat/array array)

  add_executable(cstdint cstdint.cpp)
  target_link_libraries(cstdint ome-compat)
  target_link_libraries(cstdint ome-test)

  bf_add_test(ome-compat/cstdint cstdint)

  add_executable(memory memory.cpp)
  target_link_libraries(memory ome-compat)
  target_link_libraries(memory ome-test)

  bf_add_test(ome-compat/memory memory)

  add_executable(regex regex.cpp)
  target_link_libraries(regex ome-compat)
  target_link_libraries(regex ome-test)

  bf_add_test(ome-compat/regex regex)

  add_executable(tuple tuple.cpp)
  target_link_libraries(tuple ome-compat)
  target_link_libraries(tuple ome-test)

  bf_add_test(ome-compat/tuple tuple)

endif(BUILD_TESTS)

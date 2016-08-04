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

cmake_policy(SET CMP0007 NEW)
# Use new variable expansion policy.
if (POLICY CMP0053)
  cmake_policy(SET CMP0053 NEW)
endif(POLICY CMP0053)
if (POLICY CMP0054)
  cmake_policy(SET CMP0054 NEW)
endif(POLICY CMP0054)

# Dump headers
function(header_include_list_write source_headers binary_headers source_prefix test_dir)
  set(TEST_FILE ${test_dir}/Headers.cmake)

  foreach(header ${${source_headers}})
    get_filename_component(realpath "${header}" REALPATH)
    list(APPEND includes "${source_prefix}/${header}")
    list(APPEND files "${realpath}")
  endforeach(header)

  foreach(header ${${binary_headers}})
    get_filename_component(realpath "${header}" REALPATH)
    file(RELATIVE_PATH header ${PROJECT_BINARY_DIR}/cpp/lib ${header})
    list(APPEND includes "${header}")
    list(APPEND files "${realpath}")
  endforeach(header)

  list(SORT includes)
  list(SORT files)

  file(WRITE "${TEST_FILE}" "# Test headers and files for ${source_prefix}\n\n")

  file(APPEND "${TEST_FILE}" "set(TEST_INCLUDES\n")
  foreach(include ${includes})
    file(APPEND "${TEST_FILE}" "    ${include}\n")
  endforeach(include)
  file(APPEND "${TEST_FILE}" ")\n\n")

  file(APPEND "${TEST_FILE}" "set(TEST_FILES\n")
  foreach(file ${files})
    file(APPEND "${TEST_FILE}" "    ${file}\n")
  endforeach(file)
  file(APPEND "${TEST_FILE}" ")\n")
endfunction(header_include_list_write)

function(header_test_from_file component library path)
  include(${CMAKE_CURRENT_BINARY_DIR}/Headers.cmake)
  set(headerdir ${PROJECT_BINARY_DIR}/cpp/test/${component}/headers)
  file(MAKE_DIRECTORY ${headerdir})

  foreach(header ${TEST_INCLUDES})
    # We compile each header twice in separate compilation units.
    # Each alone is sufficient to test that the header is functional,
    # but both are needed to check for link errors, which can happen
    # if the header accidentally defines a variable, e.g. a global or
    # class static member.
    foreach(repeat 1 2)
    string(REPLACE "/" "_" genheader ${header})
      string(REPLACE "${PROJECT_SOURCE_DIR}/cpp/src/" "" include ${header})
      string(REPLACE "${headerdir}/" "" include ${include})
      string(REGEX REPLACE "\\.h$" "-${repeat}.cpp" genheader ${genheader})
      string(REGEX REPLACE "[/.]" "_" safeheader ${include})
      string(CONFIGURE "#include <@include@>

#include <ome/test/test.h>

TEST(Header, ${safeheader}_${repeat})
{
}
" src)
      file(WRITE "${headerdir}/${genheader}" "${src}")
      list(APPEND test_headers_SOURCES "${headerdir}/${genheader}")
    endforeach(repeat)
  endforeach(header)

  add_executable(${component}-headers ${test_headers_SOURCES})
  target_link_libraries(${component}-headers ${library} ome-test)
endfunction(header_test_from_file)

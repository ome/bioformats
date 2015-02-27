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

include_directories(${GTEST_INCLUDE_DIR})

if(BUILD_TESTS)
  set(test_main_sources main.cpp)

  add_library(testmain STATIC ${test_main_sources})

  set(TEST_LIBS testmain ${GTEST_LIBRARIES} ${CMAKE_THREAD_LIBS_INIT})

  function(headers_test component path)
    set(headerdir ${PROJECT_BINARY_DIR}/cpp/test/${component}/headers)
    file(MAKE_DIRECTORY ${headerdir})
    set(dirs ${PROJECT_SOURCE_DIR}/cpp/lib ${PROJECT_BINARY_DIR}/cpp/lib)
    foreach(dir ${dirs})
      file(GLOB_RECURSE headers RELATIVE
           "${dir}"
           "${dir}/${path}/*.h")

    foreach(header ${headers})
      string(REPLACE "/" "_" genheader ${header})
      string(REPLACE "${PROJECT_SOURCE_DIR}/cpp/src/" "" include ${header})
      string(REPLACE "${headerdir}/" "" include ${include})
      string(REGEX REPLACE "\\.h$" ".cpp" genheader ${genheader})
      string(REGEX REPLACE "[/.]" "_" safeheader ${include})
      string(CONFIGURE "#include <@include@>

#include <gtest/gtest.h>

TEST(Header, ${safeheader})
{
}
" src)
      file(WRITE "${headerdir}/${genheader}" "${src}")
      list(APPEND test_headers_SOURCES "${headerdir}/${genheader}")
    endforeach(header)
    endforeach(dir)

    add_executable(${component}-headers ${test_headers_SOURCES})
    target_link_libraries(${component}-headers ${TEST_LIBS})
  endfunction(headers_test)
endif(BUILD_TESTS)

add_subdirectory(ome-compat)
add_subdirectory(ome-xerces)
add_subdirectory(ome-xml)
add_subdirectory(ome-bioformats)

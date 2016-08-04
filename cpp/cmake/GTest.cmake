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

enable_testing()
option(test "Enable unit tests (requires gtest)" ON)
set(BUILD_TESTS ${test})
set(EXTENDED_TESTS ${extended-tests})

# Unit tests
find_package(Threads REQUIRED)

if(NOT embedded-gtest)
  find_package(GTest)
endif()

if(NOT GTEST_FOUND)
  message(STATUS "Using embedded GTest")
  # If not using a shared runtime, gtest hardcodes its own (which breaks linking)
  set(gtest_force_shared_crt ON CACHE BOOL "Force gtest to use shared runtime")
  # VS2012 Faux variadic templates workaround.
  if(NOT MSVC_VERSION VERSION_LESS 1700 AND MSVC_VERSION VERSION_LESS 1800)
    add_definitions(-D_VARIADIC_MAX=10)
  endif()

  # Remove warnings triggered by gtest since they aren't our responsibility.
  set(SAVED_CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
  string(REPLACE " " ";" GTEST_FLAG_LIST "${CMAKE_CXX_FLAGS}")
  list(REMOVE_ITEM GTEST_FLAG_LIST
       -Wconversion
       -Wctor-dtor-privacy
       -Wmissing-declarations)
  string(REPLACE ";" " " CMAKE_CXX_FLAGS "${GTEST_FLAG_LIST}")
  unset(GTEST_FLAG_LIST)

  # Build gtest using its own CMake support.
  add_subdirectory("${CMAKE_CURRENT_LIST_DIR}/../ext/gtest-1.7.0")
  set(GTEST_INCLUDE_DIR "${CMAKE_CURRENT_LIST_DIR}/../ext/gtest-1.7.0/include")
  set(GTEST_LIBRARIES gtest)
  set(GTEST_FOUND ON)
  set_property(TARGET gtest gtest_main PROPERTY FOLDER "External/Google Test")

  # Restore saved flags.
  set(CMAKE_CXX_FLAGS "${SAVED_CMAKE_CXX_FLAGS}")
endif()

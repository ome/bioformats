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

enable_testing()
option(test "Enable unit tests (requires gtest)" ON)
set(BUILD_TESTS ${test})
set(EXTENDED_TESTS ${extended-tests})

# Unit tests
find_package(Threads REQUIRED)

if(BUILD_TESTS)
  if(GTEST_SOURCE)
    # If not using a shared runtime, gtest hardcodes its own (which breaks linking)
    set(gtest_force_shared_crt ON CACHE BOOL "Force gtest to use shared runtime")

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
    add_subdirectory("${GTEST_SOURCE}" "${PROJECT_BINARY_DIR}/cpp/ext/gtest")

    # Restore saved flags.
    set(CMAKE_CXX_FLAGS "${SAVED_CMAKE_CXX_FLAGS}")

    set_property(TARGET gtest gtest_main PROPERTY FOLDER "External/Google Test")
    add_library(GTest::GTest ALIAS gtest)
    target_include_directories(gtest INTERFACE $<BUILD_INTERFACE:${GTEST_SOURCE}/include>)
    set(GTEST_FOUND TRUE)
  else()
    find_package(GTest)
  endif()

  if(NOT GTEST_FOUND)
    message(WARNING "GTest not found; tests disabled")
    set(BUILD_TESTS OFF)
  endif()
endif()

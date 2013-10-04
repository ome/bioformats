# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2013 Open Microscopy Environment:
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

cmake_minimum_required(VERSION 2.8)


project(bioformats)
include(cmake/Version.cmake)

if("${PROJECT_SOURCE_DIR}" STREQUAL "${PROJECT_BINARY_DIR}")
  message(FATAL_ERROR "In-tree builds are not supported; please run cmake from a separate build directory.")
endif("${PROJECT_SOURCE_DIR}" STREQUAL "${PROJECT_BINARY_DIR}")

enable_language(CXX)

include(GNUInstallDirs)
include(CheckIncludeFileCXX)
include(CheckCXXCompilerFlag)
include(CheckCXXSourceCompiles)

include(cmake/CompilerChecks.cmake)
include(cmake/BoostChecks.cmake)
include(cmake/RegexChecks.cmake)
include(cmake/XercesChecks.cmake)
include(cmake/XsdFu.cmake)

find_package(Threads REQUIRED)

# Unit tests
find_package(GTest)
set(BUILD_TESTS OFF)
if(GTEST_FOUND)
  set(BUILD_TESTS ON)
endif(GTEST_FOUND)
option(test "Enable unit tests (requires gtest)" ${BUILD_TESTS})
set(BUILD_TESTS ${test})

# Doxygen documentation
include(FindDoxygen)
find_package(Doxygen)
set(DOXYGEN_DEFAULT OFF)
if (DOXYGEN_FOUND AND DOXYGEN_DOT_FOUND)
  set (DOXYGEN_DEFAULT ON)
endif (DOXYGEN_FOUND AND DOXYGEN_DOT_FOUND)
option(doxygen "Enable doxygen documentation" ${DOXYGEN_DEFAULT})
set(BUILD_DOXYGEN ${doxygen})

set(OME_TOPLEVEL_INCLUDES ${PROJECT_SOURCE_DIR}/lib ${PROJECT_BINARY_DIR}/lib)

add_subdirectory(doc)
add_subdirectory(lib/ome/compat)
add_subdirectory(lib/ome/xerces)
add_subdirectory(lib/ome/xml)

enable_testing()
add_subdirectory(test)

set(LIBRARY_PREFIX OME)
configure_file(${PROJECT_SOURCE_DIR}/cmake/TemplateShellConfig.cmake.in
               ${CMAKE_CURRENT_BINARY_DIR}/config)

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

# Default policy is from 2.8.0
cmake_policy(VERSION 2.8.0)
# Set MacOSX @rpath usage globally.
if (POLICY CMP0042)
  cmake_policy(SET CMP0042 NEW)
endif(POLICY CMP0042)

project(bioformats)
include(cpp/cmake/Version.cmake)

if("${PROJECT_SOURCE_DIR}" STREQUAL "${PROJECT_BINARY_DIR}")
  message(FATAL_ERROR "In-tree builds are not supported; please run cmake from a separate build directory.")
endif("${PROJECT_SOURCE_DIR}" STREQUAL "${PROJECT_BINARY_DIR}")

enable_language(CXX)

list(APPEND CMAKE_MODULE_PATH
     "${PROJECT_SOURCE_DIR}/cpp/cmake")

include(CheckIncludeFileCXX)
include(CheckCXXCompilerFlag)
include(CheckCXXSourceCompiles)

include(GNUInstallDirs)
include(CompilerChecks)
include(BoostChecks)
include(RegexChecks)
include(ThreadChecks)
include(XercesChecks)
include(ImageLibraries)
include(XsdFu)
include(GTest)
include(Doxygen)
include(HeaderTest)

add_subdirectory(docs/doxygen)
add_subdirectory(cpp)

set(LIBRARY_PREFIX OME)
configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateShellConfig.cmake.in
               ${PROJECT_BINARY_DIR}/config)
if(WIN32)
configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateCmdConfig.cmake.in
               ${PROJECT_BINARY_DIR}/config.bat)
endif()

message("NOTE: The Bio-Formats ${OME_VERSION_SHORT} C++ library is new in version 5.1")
message("NOTE: API stability will not be guaranteed until version 5.2")

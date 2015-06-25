# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2014 Open Microscopy Environment:
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

project(bioformats-superbuild)
# Given that the SuperBuild infrastructure is primarily for Windows,
# we do require a recent cmake for this.
cmake_minimum_required(VERSION 3.2.0)

message(STATUS "Configuring Bio-Formats Super-Build")

list(APPEND CMAKE_MODULE_PATH
     "${PROJECT_SOURCE_DIR}/cpp/cmake")

include(GNUInstallDirs)
include(ExternalProject)
include("${CMAKE_CURRENT_LIST_DIR}/ExternalProjectHelpers.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/External_zlib.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/External_bzip2.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/External_png.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/External_tiff.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/External_boost.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/External_xerces.cmake")

install(DIRECTORY ${BIOFORMATS_EP_INSTALL_DIR}/
        DESTINATION ${CMAKE_INSTALL_PREFIX})

# Re-run top-level CMakeLists.txt as an external project.
set(proj bioformats)

set(bioformats_DEPENDENCIES zlib bzip2 png tiff boost xerces)
set(bioformats_ARGS)

set(EP_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR})
set(EP_BINARY_DIR ${CMAKE_BINARY_DIR}/${proj}-build)
set(EP_INSTALL_DIR ${CMAKE_BINARY_DIR}/${proj}-install)
ExternalProject_Add(${proj}
  ${BIOFORMATS_EP_COMMON_ARGS}
  DOWNLOAD_COMMAND ""
  SOURCE_DIR ${EP_SOURCE_DIR}
  BINARY_DIR ${EP_BINARY_DIR}
  INSTALL_DIR ""
  INSTALL_COMMAND "make;install;DESTDIR=${EP_INSTALL_DIR}"
  CMAKE_ARGS
    ${bioformats_ARGS}
  CMAKE_CACHE_ARGS
    -Dbioformats-superbuild:BOOL=OFF
  DEPENDS
    ${bioformats_DEPENDENCIES}
  )

# Force rebuilding of the main subproject every time building from super structure
ExternalProject_Add_Step(${proj} forcebuild
    COMMAND ${CMAKE_COMMAND} -E remove
    ${CMAKE_CURRENT_BUILD_DIR}/${proj}-prefix/src/${proj}-stamp/${proj}-build
    DEPENDEES configure
    DEPENDERS build
    ALWAYS 1
  )

install(DIRECTORY ${EP_INSTALL_DIR}/
        DESTINATION ${CMAKE_INSTALL_PREFIX})

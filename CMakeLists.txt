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

cmake_minimum_required(VERSION 2.8.12)

# Default policy is from 2.8.12
cmake_policy(VERSION 2.8.12)
# Set MacOSX @rpath usage globally.
if (POLICY CMP0020)
  cmake_policy(SET CMP0020 NEW)
endif(POLICY CMP0020)
if (POLICY CMP0042)
  cmake_policy(SET CMP0042 NEW)
endif(POLICY CMP0042)
# Use new variable expansion policy.
if (POLICY CMP0053)
  cmake_policy(SET CMP0053 NEW)
endif(POLICY CMP0053)
if (POLICY CMP0054)
  cmake_policy(SET CMP0054 NEW)
endif(POLICY CMP0054)

# For MSVC
enable_language(CXX)
# For feature tests with CMake 2.8.x
enable_language(C)

list(APPEND CMAKE_MODULE_PATH
     "${CMAKE_CURRENT_LIST_DIR}/cpp/cmake")

include("${CMAKE_CURRENT_LIST_DIR}/cpp/cmake/BioFormatsCommon.cmake")

project(bioformats)
message(STATUS "Configuring Bio-Formats main build")

include(cpp/cmake/Version.cmake)

if("${PROJECT_SOURCE_DIR}" STREQUAL "${PROJECT_BINARY_DIR}")
  message(FATAL_ERROR "In-tree builds are not supported; please run cmake from a separate build directory.")
endif("${PROJECT_SOURCE_DIR}" STREQUAL "${PROJECT_BINARY_DIR}")

enable_language(CXX)
  set(BUILD_SHARED_LIBS_DEFAULT ON)
if(MSVC)
  set(BUILD_SHARED_LIBS_DEFAULT OFF)
endif()
set(BUILD_SHARED_LIBS ${BUILD_SHARED_LIBS_DEFAULT} CACHE BOOL "Use shared libraries")

include(GNUInstallDirs)
include(CompilerChecks)
include(PlatformChecks)
include(BoostChecks)
include(RegexChecks)
include(ThreadChecks)
include(XercesChecks)
include(ImageLibraries)
include(QtGLChecks)
include(XsdFu)
include(GTest)
include(Doxygen)
include(HeaderTest)

if(MSVC)
  # Debug library suffix.
  set(CMAKE_DEBUG_POSTFIX "d")
  # Require dynamic linking and disable autolinking.
  add_definitions(-DBOOST_ALL_DYN_LINK -DBOOST_ALL_NO_LIB)
  # To prevent a lot of spam warnings about standard POSIX functions
  # and unsafe use of the standard library.
  add_definitions(-D_CRT_SECURE_NO_WARNINGS -D_SCL_SECURE_NO_WARNINGS)
endif()


if(NOT DEFINED OME_BIOFORMATS_INSTALL_DATADIR)
  set(OME_BIOFORMATS_INSTALL_DATADIR "${CMAKE_INSTALL_DATADIR}/ome/bioformats"
      CACHE PATH "Bio-Formats-specific datadir")
endif()

# Use standard path if using a prefix.
if(CMAKE_INSTALL_PREFIX)
  if (NOT DEFINED OME_BIOFORMATS_INSTALL_LIBEXECDIR)
    set(OME_BIOFORMATS_INSTALL_LIBEXECDIR "${CMAKE_INSTALL_LIBEXECDIR}/ome/bioformats"
        CACHE PATH "Bio-Formats-specific libexecdir")
  endif()
  if (NOT DEFINED OME_BIOFORMATS_INSTALL_ICONDIR)
    set(OME_BIOFORMATS_INSTALL_ICONDIR "${CMAKE_INSTALL_DATADIR}/icons/hicolor/scalable"
        CACHE PATH "Icon directory")
  endif()
else()
  if (NOT DEFINED OME_BIOFORMATS_INSTALL_LIBEXECDIR)
    set(OME_BIOFORMATS_INSTALL_LIBEXECDIR "${CMAKE_INSTALL_LIBEXECDIR}"
        CACHE PATH "Bio-Formats-specific libexecdir")
  endif()
  if (NOT DEFINED OME_BIOFORMATS_INSTALL_ICONDIR)
    set(OME_BIOFORMATS_INSTALL_ICONDIR "${CMAKE_INSTALL_DATADIR}/icons"
        CACHE PATH "Icon directory")
  endif()
endif()

if (NOT DEFINED OME_BIOFORMATS_INSTALL_SCHEMADIR)
  set(OME_BIOFORMATS_INSTALL_SCHEMADIR "${CMAKE_INSTALL_DATADIR}/xml/ome"
      CACHE PATH "Bio-Formats schema directory")
endif()
if (NOT DEFINED OME_BIOFORMATS_INSTALL_TRANSFORMDIR)
  set(OME_BIOFORMATS_INSTALL_TRANSFORMDIR "${CMAKE_INSTALL_DATADIR}/xsl/ome"
      CACHE PATH "Bio-Formats transform directory")
endif()

foreach(dir
    DATADIR
    LIBEXECDIR
    ICONDIR
    SCHEMADIR
    TRANSFORMDIR
    )
  mark_as_advanced(OME_BIOFORMATS_INSTALL_${dir})
  if(NOT IS_ABSOLUTE ${OME_BIOFORMATS_INSTALL_${dir}})
    set(OME_BIOFORMATS_INSTALL_FULL_${dir} "${CMAKE_INSTALL_PREFIX}/${OME_BIOFORMATS_INSTALL_${dir}}")
  else()
    set(OME_BIOFORMATS_INSTALL_FULL_${dir} "${OME_BIOFORMATS_INSTALL_${dir}}")
  endif()
endforeach()

add_subdirectory(docs/doxygen)
add_subdirectory(docs/sphinx)
add_subdirectory(cpp)

set(LIBRARY_PREFIX OME)
file(MAKE_DIRECTORY "${PROJECT_BINARY_DIR}/cpp/bin")
configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateShellConfig.cmake.in
               ${PROJECT_BINARY_DIR}/config @ONLY)
configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateInternalShellWrapper.cmake.in
               ${PROJECT_BINARY_DIR}/cpp/cmake/bf-test @ONLY)
configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateShellWrapper.cmake.in
               ${PROJECT_BINARY_DIR}/cpp/bin/bf-test @ONLY)
file(COPY ${PROJECT_BINARY_DIR}/cpp/cmake/bf-test
     DESTINATION ${PROJECT_BINARY_DIR}
     FILE_PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE GROUP_READ
     GROUP_EXECUTE WORLD_READ WORLD_EXECUTE)
install(PROGRAMS ${PROJECT_BINARY_DIR}/cpp/bin/bf-test
        DESTINATION ${CMAKE_INSTALL_FULL_BINDIR})
if(WIN32)
  configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateCmdConfig.cmake.in
                 ${PROJECT_BINARY_DIR}/config.bat @ONLY)
  configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateInternalCmdWrapper.cmake.in
                 ${PROJECT_BINARY_DIR}/bf-test.bat @ONLY)
  configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateCmdWrapper.cmake.in
                 ${PROJECT_BINARY_DIR}/cpp/bin/bf-test.bat @ONLY)
  install(PROGRAMS ${PROJECT_BINARY_DIR}/cpp/bin/bf-test.bat
          DESTINATION ${CMAKE_INSTALL_FULL_BINDIR})
  endif()
return()

message("NOTE: The Bio-Formats ${OME_VERSION_SHORT} C++ library is new in version 5.1")
message("NOTE: API stability will not be guaranteed until version 5.2")

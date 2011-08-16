# CMakeLists.txt
#

# OME Bio-Formats ITK plugin for calling Bio-Formats from the Insight Toolkit.
# Copyright (c) 2008-@year@, UW-Madison LOCI.
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#     * Redistributions of source code must retain the above copyright
#       notice, this list of conditions and the following disclaimer.
#     * Redistributions in binary form must reproduce the above copyright
#       notice, this list of conditions and the following disclaimer in the
#       documentation and/or other materials provided with the distribution.
#     * Neither the name of the UW-Madison LOCI nor the
#       names of its contributors may be used to endorse or promote products
#       derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY UW-MADISON LOCI ''AS IS'' AND ANY
# EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL UW-MADISON LOCI BE LIABLE FOR ANY
# DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
# (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

# IMPORTANT NOTE: Although this software is distributed according to a
# "BSD-style" license, it requires the Bio-Formats Java library to do
# anything useful, which is licensed under the GPL v2 or later.
# As such, if you wish to distribute this software with Bio-Formats itself,
# your combined work must be distributed under the terms of the GPL.

# Adapted from the Slicer3 project: http://www.slicer.org/
# http://viewvc.slicer.org/viewcvs.cgi/trunk/Libs/MGHImageIO/
#
# See slicer-license.txt for Slicer3's licensing information.
#
# For more information about the ITK Plugin IO mechanism, see:
# http://www.itk.org/Wiki/Plugin_IO_mechanisms

project(BioFormatsImageIO)

cmake_minimum_required(VERSION 2.8)

if(COMMAND cmake_policy)
 cmake_policy(SET CMP0003 NEW)
endif(COMMAND cmake_policy)

find_package(ITK REQUIRED)
include(${ITK_USE_FILE})

set(include_dirs
  ${CMAKE_CURRENT_SOURCE_DIR}
  ${CMAKE_CURRENT_BINARY_DIR}
)
include_directories(${include_dirs})

configure_file(
  ${CMAKE_CURRENT_SOURCE_DIR}/itkBioFormatsImageIOConfigure.h.in
  ${CMAKE_CURRENT_BINARY_DIR}/itkBioFormatsImageIOConfigure.h
)

file(GLOB headers "${CMAKE_CURRENT_SOURCE_DIR}/*.h")

if(NOT DEFINED BUILD_SHARED_LIBS)
  option(BUILD_SHARED_LIBS "Build with shared libraries." ON)
endif(NOT DEFINED BUILD_SHARED_LIBS)

find_package(Java REQUIRED)

# --------------------------------------------------------------------------
# Sources

set(BioFormatsImageIO_SRCS
  itkBioFormatsImageIO.cxx
  itkBioFormatsImageIOFactory.cxx
)

# --------------------------------------------------------------------------
# Build and install the library

set(lib_name BioFormatsIO)
add_library(${lib_name}
  ${BioFormatsImageIO_SRCS}
)

target_link_libraries(${lib_name}
#  ${ITK_LIBRARIES}
  ITKIO
  ITKIOReview
)

# --------------------------------------------------------------------------
# Shared library that when placed in ITK_AUTOLOAD_PATH, will add
# BioFormatsImageIO as an ImageIOFactory.  Need to have separate shared
# library for each new format. Note that the plugin library is placed
# in a special directory to speed up the searching for ImageIO
# factories (which improves the speed at which plugins run).

if(NOT DEFINED BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR)
  set(BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR lib/)
endif(NOT DEFINED BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR)

if(NOT DEFINED BioFormatsImageIO_ARTIFACT_DIR)
  set(BioFormatsImageIO_ARTIFACT_DIR
    ${PROJECT_SOURCE_DIR}/../../../artifacts)
endif(NOT DEFINED BioFormatsImageIO_ARTIFACT_DIR)

if(NOT DEFINED BIO_FORMATS_DIST_DIR)
  set(BIO_FORMATS_DIST_DIR dist/bf-itk)
endif(NOT DEFINED BIO_FORMATS_DIST_DIR)

if(NOT DEFINED BF_BUILD_TYPE)
  set(BF_BUILD_TYPE "")
endif(NOT DEFINED BF_BUILD_TYPE)

if(NOT DEFINED BF_LIB_PREFIX)
  set(BF_LIB_PREFIX lib)
endif(NOT DEFINED BF_LIB_PREFIX)

add_library(BioFormatsIOPlugin MODULE
  itkBioFormatsIOPlugin.cxx
)
set_target_properties(BioFormatsIOPlugin
  PROPERTIES
  RUNTIME_OUTPUT_DIRECTORY
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}"
  LIBRARY_OUTPUT_DIRECTORY
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}"
  ARCHIVE_OUTPUT_DIRECTORY
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}"
)
target_link_libraries(BioFormatsIOPlugin
  ${lib_name}
)

# --------------------------------------------------------------------------
# Add some tests

add_executable(itkBioFormatsImageIOTest itkBioFormatsImageIOTest.cxx)
target_link_libraries(itkBioFormatsImageIOTest BioFormatsIO)

add_executable(itkRGBBioFormatsImageIOTest itkRGBBioFormatsImageIOTest.cxx)
target_link_libraries(itkRGBBioFormatsImageIOTest BioFormatsIO)

add_executable(itkVectorImageBioFormatsImageIOTest itkVectorImageBioFormatsImageIOTest.cxx)
target_link_libraries(itkVectorImageBioFormatsImageIOTest BioFormatsIO)

add_executable(perf perf.cxx)
target_link_libraries(perf BioFormatsIO)

if(APPLE)
  if(NOT DEFINED BF_SUFFIX)
    set(BF_SUFFIX .dylib)
  endif(NOT DEFINED BF_SUFFIX)
endif(APPLE)

if(UNIX)
  if(NOT DEFINED BF_SUFFIX)
    set(BF_SUFFIX .so)
  endif(NOT DEFINED BF_SUFFIX)
endif(UNIX)

if(WIN32)
  set(BF_LIB_PREFIX "")
  set(BF_BUILD_TYPE /Release)

  if(NOT DEFINED BF_SUFFIX)
    set(BF_SUFFIX .dll)
  endif(NOT DEFINED BF_SUFFIX)
endif(WIN32)

if(WIN64)
  set(BF_LIB_PREFIX "")
  set(BF_BUILD_TYPE /Release)

  if(NOT DEFINED BF_SUFFIX)
    set(BF_SUFFIX .dll)
  endif(NOT DEFINED BF_SUFFIX)
endif(WIN64)


if(APPLE)
  add_custom_command(
    TARGET BioFormatsIOPlugin
    DEPENDS "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
    POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy
    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
    VERBATIM
  )

  add_custom_command(
    TARGET BioFormatsIOPlugin
    DEPENDS "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
    POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy
    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
    "${CMAKE_BINARY_DIR}/${BIO_FORMATS_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
    VERBATIM
  )
  install(FILES
    "${CMAKE_BINARY_DIR}/${BIO_FORMATS_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
    DESTINATION "${BIO_FORMATS_DIST_DIR}"
  )
endif(APPLE)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  "${CMAKE_BINARY_DIR}/${BIO_FORMATS_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BIO_FORMATS_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  DESTINATION "${BIO_FORMATS_DIST_DIR}"
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${CMAKE_BINARY_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${CMAKE_BINARY_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  "${CMAKE_BINARY_DIR}/${BIO_FORMATS_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BIO_FORMATS_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  DESTINATION "${BIO_FORMATS_DIST_DIR}"
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BioFormatsImageIO_ARTIFACT_DIR}/loci_tools.jar"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${BioFormatsImageIO_ARTIFACT_DIR}/loci_tools.jar"
  "${CMAKE_BINARY_DIR}/${BIO_FORMATS_DIST_DIR}/loci_tools.jar"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BIO_FORMATS_DIST_DIR}/loci_tools.jar"
  DESTINATION "${BIO_FORMATS_DIST_DIR}"
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BioFormatsImageIO_ARTIFACT_DIR}/loci_tools.jar"
  POST_BUILD
  COMMAND ${Java_JAVA_EXECUTABLE} -cp ${BioFormatsImageIO_ARTIFACT_DIR}/loci_tools.jar
  "loci.formats.tools.BioFormatsExtensionPrinter"
  VERBATIM
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${CMAKE_BINARY_DIR}/BioFormatsSuffixes.txt"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${CMAKE_BINARY_DIR}/BioFormatsSuffixes.txt"
  "${CMAKE_BINARY_DIR}/${BIO_FORMATS_DIST_DIR}/BioFormatsSuffixes.txt"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BIO_FORMATS_DIST_DIR}/BioFormatsSuffixes.txt"
  DESTINATION "${BIO_FORMATS_DIST_DIR}"
)

SET(CPACK_PACKAGE_FILE_NAME "bf-itk")
SET(CPACK_GENERATOR "TGZ;ZIP")
INCLUDE( CPack )

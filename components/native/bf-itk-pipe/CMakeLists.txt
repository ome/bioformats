###
# #%L
# Bio-Formats plugin for the Insight Toolkit.
# %%
# Copyright (C) 2010 - 2012 Insight Software Consortium, and Open Microscopy
# Environment:
#   - Board of Regents of the University of Wisconsin-Madison
#   - Glencoe Software, Inc.
#   - University of Dundee
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
# 
# ----------------------------------------------------------------
# Adapted from the Slicer3 project: http://www.slicer.org/
# http://viewvc.slicer.org/viewcvs.cgi/trunk/Libs/MGHImageIO/
# 
# See slicer-license.txt for Slicer3's licensing information.
# 
# For more information about the ITK Plugin IO mechanism, see:
# http://www.itk.org/Wiki/Plugin_IO_mechanisms
# #L%
###

project(BioFormatsImageIO)

cmake_minimum_required(VERSION 2.8)

# --------------------------------------------------------------------------
# Initial setup

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

option(BFITK_WIN_BUILD_DEBUG "Build Debug version? If false, builds Release. Windows only." ON)
if(${BFITK_WIN_BUILD_DEBUG} STREQUAL "ON")
  if(NOT DEFINED BFITK_WIN_BUILD_VALUE)
    set(BFITK_WIN_BUILD_VALUE \\Debug)
  endif(NOT DEFINED BFITK_WIN_BUILD_VALUE)
elseif(${BFITK_WIN_BUILD_DEBUG} STREQUAL "OFF")
  if(NOT DEFINED BFITK_WIN_BUILD_VALUE)
    set(BFITK_WIN_BUILD_VALUE \\Release)
  endif(NOT DEFINED BFITK_WIN_BUILD_VALUE)
endif(${BFITK_WIN_BUILD_DEBUG} STREQUAL "ON")

# --------------------------------------------------------------------------
# Find ITK

find_package(ITK REQUIRED)
include("${ITK_USE_FILE}")

# --------------------------------------------------------------------------
# Check ITK version

# ITK 3.20.0 or newer is required.
# For ITK 3.x, ITK_USE_REVIEW must be turned ON.

if(ITK_VERSION_MAJOR LESS "3")
  message(FATAL_ERROR "The BF-ITK plugin requires ITK 3.20.0 or newer. You are using ${ITK_VERSION_MAJOR}.${ITK_VERSION_MINOR}.${ITK_VERSION_PATCH}.")
endif(ITK_VERSION_MAJOR LESS "3")

if(ITK_VERSION_MAJOR EQUAL "3")
  if(ITK_VERSION_MINOR LESS "20")
    message(FATAL_ERROR "The BF-ITK plugin requires ITK 3.20.0 or newer. You are using ${ITK_VERSION_MAJOR}.${ITK_VERSION_MINOR}.${ITK_VERSION_PATCH}.")
  endif(ITK_VERSION_MINOR LESS "20")
  if(NOT ITK_USE_REVIEW)
    message(FATAL_ERROR "For ITK 3.x, ITK_USE_REVIEW must be turned ON.")
  endif(NOT ITK_USE_REVIEW)
  set(BF_LINK_REVIEW ON)
endif(ITK_VERSION_MAJOR EQUAL "3")

if(ITK_VERSION_MAJOR GREATER "3")
  set(BF_LINK_REVIEW OFF)
endif(ITK_VERSION_MAJOR GREATER "3")

# --------------------------------------------------------------------------
# Find Java native library

find_package(Java REQUIRED)

# --------------------------------------------------------------------------
# Sources

set(BF_SRCS
  itkBioFormatsImageIO.cxx
  itkBioFormatsImageIOFactory.cxx
)

# --------------------------------------------------------------------------
# Build and install the library

set(lib_name BioFormatsIO)
add_library(${lib_name}
  ${BF_SRCS}
)

if(BF_LINK_REVIEW)
  # Review libraries required
  target_link_libraries(${lib_name}
    ${ITK_LIBRARIES}
    ITKIOReview
  )
else(BF_LINK_REVIEW)
  target_link_libraries(${lib_name}
    ${ITK_LIBRARIES}
  )
endif(BF_LINK_REVIEW)

# --------------------------------------------------------------------------
# Shared library that when placed in ITK_AUTOLOAD_PATH, will add
# BioFormatsImageIO as an ImageIOFactory.  Need to have separate shared
# library for each new format. Note that the plugin library is placed
# in a special directory to speed up the searching for ImageIO
# factories (which improves the speed at which plugins run).

set(BF_LIB_DIR "${CMAKE_BINARY_DIR}/lib")

add_library(BioFormatsIOPlugin MODULE
  itkBioFormatsIOPlugin.cxx
)
set_target_properties(BioFormatsIOPlugin
  PROPERTIES
  RUNTIME_OUTPUT_DIRECTORY
  "${BF_LIB_DIR}"
  LIBRARY_OUTPUT_DIRECTORY
  "${BF_LIB_DIR}"
  ARCHIVE_OUTPUT_DIRECTORY
  "${BF_LIB_DIR}"
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

add_executable(itkBFImageInfo itkBioFormatsImageInfo.cxx)
target_link_libraries(itkBFImageInfo BioFormatsIO)

add_executable(itkVectorImageBioFormatsImageIOTest
  itkVectorImageBioFormatsImageIOTest.cxx
)
target_link_libraries(itkVectorImageBioFormatsImageIOTest BioFormatsIO)

add_executable(perf perf.cxx)
target_link_libraries(perf BioFormatsIO)

# --------------------------------------------------------------------------
# Copy targets into distribution folder (dist/bf-itk)

set(BF_DIST_DIR "dist/bf-itk")

if(APPLE)
  set(BF_LIB_PREFIX "lib")
  set(BF_SUFFIX ".dylib")
  set(BF_BUILD_TYPE "")
elseif(WIN32 OR WIN64)
  set(BF_LIB_PREFIX "")
  set(BF_BUILD_TYPE ${BFITK_WIN_BUILD_VALUE})
  set(BF_SUFFIX ".dll")
else(APPLE) # assume UNIX
  set(BF_LIB_PREFIX "lib")
  set(BF_SUFFIX ".so")
  set(BF_BUILD_TYPE "")
endif(APPLE)

if(APPLE)
  add_custom_command(
    TARGET BioFormatsIOPlugin
    DEPENDS "${BF_LIB_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
    POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy
      "${BF_LIB_DIR}/${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
      "${BF_LIB_DIR}/${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
    VERBATIM
  )

  add_custom_command(
    TARGET BioFormatsIOPlugin
    DEPENDS "${BF_LIB_DIR}/${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
    POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy
      "${BF_LIB_DIR}/${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
      "${CMAKE_BINARY_DIR}/${BF_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
    VERBATIM
  )
  install(FILES
    "${CMAKE_BINARY_DIR}/${BF_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
    DESTINATION "."
  )
endif(APPLE)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_LIB_DIR}/${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
    "${BF_LIB_DIR}/${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
    "${CMAKE_BINARY_DIR}/${BF_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BF_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  DESTINATION "."
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${CMAKE_BINARY_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${CMAKE_BINARY_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  "${CMAKE_BINARY_DIR}/${BF_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BF_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  DESTINATION "."
)

# --------------------------------------------------------------------------
# Define location of loci_tools.jar file. It is assumed that loci_tools.jar
# has been generated using "ant tools" from the Bio-Formats base folder.

set(BF_ARTIFACT_DIR "${PROJECT_SOURCE_DIR}/../../../artifacts")
set(BF_LOCI_TOOLS_JAR "${BF_ARTIFACT_DIR}/loci_tools.jar")

# --------------------------------------------------------------------------
# Copy loci_tools.jar to the distribution folder (dist/bf-itk).

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_LOCI_TOOLS_JAR}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${BF_LOCI_TOOLS_JAR}"
  "${CMAKE_BINARY_DIR}/${BF_DIST_DIR}/loci_tools.jar"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BF_DIST_DIR}/loci_tools.jar"
  DESTINATION "."
)

# --------------------------------------------------------------------------
# Generate a list of extensions supported by Bio-Formats, and copy the
# resultant BioFormatsSuffixes.txt to the distribution folder (dist/bf-itk).

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_LOCI_TOOLS_JAR}"
  POST_BUILD
  COMMAND "${JAVA_RUNTIME}" -cp "${BF_LOCI_TOOLS_JAR}"
  "loci.formats.tools.BioFormatsExtensionPrinter"
  VERBATIM
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${CMAKE_BINARY_DIR}/BioFormatsSuffixes.txt"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${CMAKE_BINARY_DIR}/BioFormatsSuffixes.txt"
  "${CMAKE_BINARY_DIR}/${BF_DIST_DIR}/BioFormatsSuffixes.txt"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BF_DIST_DIR}/BioFormatsSuffixes.txt"
  DESTINATION "."
)

# --------------------------------------------------------------------------
# Package distribution folder (dist/bf-itk) into .zip and .tar.gz archives,
# which can be generated using the "make package" command.

set(CPACK_PACKAGE_FILE_NAME "bf-itk")
set(CPACK_GENERATOR "TGZ;ZIP")
include(CPack)

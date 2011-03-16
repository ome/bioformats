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

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -framework JavaVM")

if(WIN32)
  cmake_minimum_required(VERSION 2.8)
else(WIN32)
  cmake_minimum_required(VERSION 2.6)
endif(WIN32)

if(COMMAND cmake_policy)
 cmake_policy(SET CMP0003 NEW)
endif(COMMAND cmake_policy)

find_package(ITK REQUIRED)
include(${ITK_USE_FILE})

if(NOT DEFINED BUILD_SHARED_LIBS)
 option(BUILD_SHARED_LIBS "Build with shared libraries." ON)
endif(NOT DEFINED BUILD_SHARED_LIBS)

# --------------------------------------------------------------------------
# Include dirs

find_package(JNI REQUIRED)

set(include_dirs
  ${CMAKE_CURRENT_SOURCE_DIR}
  ${CMAKE_CURRENT_BINARY_DIR}
  ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2}
)

#if(EXISTS "${ITK_DIR}/CMakeCache.txt")
#  set(include_dirs ${include_dirs} "${ITK_DIR}/Utilities/zlib")
#else(EXISTS "${ITK_DIR}/CMakeCache.txt")
#endif(EXISTS "${ITK_DIR}/CMakeCache.txt")

include_directories(${include_dirs})

configure_file(
  ${CMAKE_CURRENT_SOURCE_DIR}/itkBioFormatsImageIOConfigure.h.in
  ${CMAKE_CURRENT_BINARY_DIR}/itkBioFormatsImageIOConfigure.h
)

file(GLOB headers "${CMAKE_CURRENT_SOURCE_DIR}/*.h")
install(FILES
  ${headers}
  "${CMAKE_CURRENT_BINARY_DIR}/itkBioFormatsImageIOConfigure.h"
  DESTINATION include/${PROJECT_NAME} COMPONENT Development
)

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
  ITKIO
  ${JAVA_JVM_LIBRARY}
)

install(TARGETS ${lib_name}
  RUNTIME DESTINATION bin COMPONENT RuntimeLibraries
  LIBRARY DESTINATION lib/${PROJECT_NAME} COMPONENT RuntimeLibraries
  ARCHIVE DESTINATION lib/${PROJECT_NAME} COMPONENT Development
)

# Shared library that when placed in ITK_AUTOLOAD_PATH, will add
# BioFormatsImageIO as an ImageIOFactory.  Need to have separate shared
# library for each new format. Note that the plugin library is placed
# in a special directory to speed up the searching for ImageIO
# factories (which improves the speed at which plugins run).

if(NOT DEFINED BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR)
  set(BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR lib/)
endif(NOT DEFINED BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR)

add_library(BioFormatsIOPlugin SHARED
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
  ${JAVA_JVM_LIBRARY}
)

install(TARGETS BioFormatsIOPlugin
  RUNTIME DESTINATION ${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR} COMPONENT RuntimeLibraries
  LIBRARY DESTINATION ${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR} COMPONENT RuntimeLibraries
  ARCHIVE DESTINATION lib/${PROJECT_NAME} COMPONENT Development
)

# make a copy named '.so' for compatibility with the expectations
# of the ITK Factory mechanism on macs

if(APPLE)
  add_custom_command(
    TARGET BioFormatsIOPlugin
    DEPENDS "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}/libBioFormatsIOPlugin.dylib"
    POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy
    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}/libBioFormatsIOPlugin.dylib"
    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}/libBioFormatsIOPlugin.so"
    VERBATIM
  )
  install(FILES
    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}/libBioFormatsIOPlugin.so"
    DESTINATION ${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}
  )
endif(APPLE)

#add_custom_command(
#  TARGET BioFormatsIOPlugin
#  DEPENDS "${CMAKE_BINARY_DIR}/libBioFormatsIOPlugin.dylib"
#  POST_BUILD
#  COMMAND ${CMAKE_COMMAND} -E make_directory
#  COMMAND ${CMAKE_COMMAND} copy
#  "${CMAKE_BINARY_DIR}/libBioFormatsIOPlugin.dylib"
#  "${CMAKE_BINARY_DIR}/dist/bf-itk/libBioFormatsIOPlugin.dylib"
#)

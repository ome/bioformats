###
# #%L
# Bio-Formats plugin for the Insight Toolkit.
# %%
# Copyright (C) 2009 - 2013 Open Microscopy Environment:
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

set(BF_CPP_DIR ${PROJECT_SOURCE_DIR}/../bf-cpp/bfcpp)

include(${BF_CPP_DIR}/jace/Prerequisites.cmake)

message(STATUS "-- Jace:")
set(JACE_DIR NOTFOUND CACHE PATH
  "The path to toplevel directory of your Jace checkout")
if(IS_DIRECTORY "${JACE_DIR}")
  message(STATUS "jace root     : ${JACE_DIR}")
else(IS_DIRECTORY "${JACE_DIR}")
  message(FATAL_ERROR "Cannot build without Jace. Please set JACE_DIR.")
endif(IS_DIRECTORY "${JACE_DIR}")

set(include_dirs
  ${CMAKE_CURRENT_SOURCE_DIR}
  ${CMAKE_CURRENT_BINARY_DIR}
  ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2} ${Boost_INCLUDE_DIR}
  ${JACE_DIR}/source/c++/include
  ${BF_CPP_DIR}/include
  ${BF_CPP_DIR}/proxies/include
  ${BF_CPP_DIR}/jtools/include
)

#if(EXISTS "${ITK_DIR}/CMakeCache.txt")
#  set(include_dirs ${include_dirs} "${ITK_DIR}/Utilities/zlib")
#else(EXISTS "${ITK_DIR}/CMakeCache.txt")
#endif(EXISTS "${ITK_DIR}/CMakeCache.txt")

include_directories(${include_dirs})

link_directories(${BF_CPP_DIR}/build ${BF_CPP_DIR}/build/jace)

configure_file(
  ${CMAKE_CURRENT_SOURCE_DIR}/itkBioFormatsImageIOConfigure.h.in
  ${CMAKE_CURRENT_BINARY_DIR}/itkBioFormatsImageIOConfigure.h
)

file(GLOB headers "${CMAKE_CURRENT_SOURCE_DIR}/*.h")
#install(FILES
#  ${headers}
#  "${CMAKE_CURRENT_BINARY_DIR}/itkBioFormatsImageIOConfigure.h"
#  DESTINATION include/${PROJECT_NAME} COMPONENT Development
#)

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
  bfcpp
  jace
  jtools
  ${JAVA_JVM_LIBRARY}
)

#install(TARGETS ${lib_name}
#  RUNTIME DESTINATION bin COMPONENT RuntimeLibraries
#  LIBRARY DESTINATION lib/${PROJECT_NAME} COMPONENT RuntimeLibraries
#  ARCHIVE DESTINATION lib/${PROJECT_NAME} COMPONENT Development
#)

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
  bfcpp
  jace
  jtools
  ${JAVA_JVM_LIBRARY}
)

#install(TARGETS BioFormatsIOPlugin
#  RUNTIME DESTINATION ${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR} COMPONENT RuntimeLibraries
#  LIBRARY DESTINATION ${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR} COMPONENT RuntimeLibraries
#  ARCHIVE DESTINATION lib/${PROJECT_NAME} COMPONENT Development
#)

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
#  install(FILES
#    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}/libBioFormatsIOPlugin.so"
#    DESTINATION ${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}
#  )
endif(APPLE)


if(NOT DEFINED BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR)
  set(BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR lib/)
endif(NOT DEFINED BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR)

if(NOT DEFINED BF_CPP_DIST_DIR)
  set(BF_CPP_DIST_DIR ../../bf-cpp/bfcpp/build/dist/bfcpp)
endif(NOT DEFINED BF_CPP_DIST_DIR)

if(NOT DEFINED BioFormatsImageIO_DIST_DIR)
  set(BioFormatsImageIO_DIST_DIR dist/bf-itk)
endif(NOT DEFINED BioFormatsImageIO_DIST_DIR)

if(NOT DEFINED BF_BUILD_TYPE)
  set(BF_BUILD_TYPE "")
endif(NOT DEFINED BF_BUILD_TYPE)

if(NOT DEFINED BF_LIB_PREFIX)
  set(BF_LIB_PREFIX lib)
endif(NOT DEFINED BF_LIB_PREFIX)

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

# Building in Debug mode is not currently supported in this script
#  set(BF_BUILD TYPE /Debug)


  if(NOT DEFINED BF_SUFFIX)

    set(BF_SUFFIX .dll)

  endif(NOT DEFINED BF_SUFFIX)

endif(WIN32)

if(WIN64)
  set(BF_LIB_PREFIX "")
  set(BF_BUILD_TYPE /Release)

#  Builing with Debug mode on Windows is not currently supported in this script.
#  set(BF_BUILD TYPE /Debug)

  if(NOT DEFINED BF_SUFFIX)

    set(BF_SUFFIX .dll)

  endif(NOT DEFINED BF_SUFFIX)

endif(WIN64)

#install(TARGETS BioFormatsIOPlugin
#  RUNTIME DESTINATION ${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR} COMPONENT RuntimeLibraries
#  LIBRARY DESTINATION ${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR} COMPONENT RuntimeLibraries
#  ARCHIVE DESTINATION lib/${PROJECT_NAME} COMPONENT Development
#)

# make a copy named '.so' for compatibility with the expectations
# of the ITK Factory mechanism on macs

#if(APPLE)
#  add_custom_command(
#    TARGET BioFormatsIOPlugin
#    DEPENDS "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
#    POST_BUILD
#    COMMAND ${CMAKE_COMMAND} -E copy
#    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
#    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
#    VERBATIM
#  )
#
#  add_custom_command(
#    TARGET BioFormatsIOPlugin
#    DEPENDS "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
#    POST_BUILD
#    COMMAND ${CMAKE_COMMAND} -E copy
#    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
#    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
#    VERBATIM
#  )
#  install(FILES
#    "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin.so"
#    DESTINATION "${BioFormatsImageIO_DIST_DIR}"
#  )
#endif(APPLE)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIOPlugin${BF_SUFFIX}"
  DESTINATION "${BioFormatsImageIO_DIST_DIR}"
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${CMAKE_BINARY_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${CMAKE_BINARY_DIR}${BF_BUILD_TYPE}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}BioFormatsIO${BF_SUFFIX}"
  DESTINATION "${BioFormatsImageIO_DIST_DIR}"
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_CPP_DIST_DIR}/${BF_LIB_PREFIX}bfcpp${BF_SUFFIX}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${BF_CPP_DIST_DIR}/${BF_LIB_PREFIX}bfcpp${BF_SUFFIX}"
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}bfcpp${BF_SUFFIX}"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}bfcpp${BF_SUFFIX}"
  DESTINATION "${BioFormatsImageIO_DIST_DIR}"
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_CPP_DIST_DIR}/${BF_LIB_PREFIX}jace${BF_SUFFIX}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${BF_CPP_DIST_DIR}/${BF_LIB_PREFIX}jace${BF_SUFFIX}"
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}jace${BF_SUFFIX}"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}jace${BF_SUFFIX}"
  DESTINATION "${BioFormatsImageIO_DIST_DIR}"
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_CPP_DIST_DIR}/${BF_LIB_PREFIX}jtools${BF_SUFFIX}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${BF_CPP_DIST_DIR}/${BF_LIB_PREFIX}jtools${BF_SUFFIX}"
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}jtools${BF_SUFFIX}"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}jtools${BF_SUFFIX}"
  DESTINATION "${BioFormatsImageIO_DIST_DIR}"
)

if(APPLE)
add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_CPP_DIST_DIR}/${BF_LIB_PREFIX}jace.jni${BF_LIB_PREFIX}"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${BF_CPP_DIST_DIR}/${BF_LIB_PREFIX}jace.jni${BF_LIB_PREFIX}"
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}jace.jni${BF_LIB_PREFIX}"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/${BF_LIB_PREFIX}jace.jni${BF_LIB_PREFIX}"
  DESTINATION "${BioFormatsImageIO_DIST_DIR}"
)
endif(APPLE)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_CPP_DIST_DIR}/jace-runtime.jar"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${BF_CPP_DIST_DIR}/jace-runtime.jar"
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/jace-runtime.jar"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/jace-runtime.jar"
  DESTINATION "${BioFormatsImageIO_DIST_DIR}"
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_CPP_DIST_DIR}/bio-formats.jar"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${BF_CPP_DIST_DIR}/bio-formats.jar"
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/bio-formats.jar"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/bio-formats.jar"
  DESTINATION "${BioFormatsImageIO_DIST_DIR}"
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_CPP_DIST_DIR}/loci_tools.jar"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${BF_CPP_DIST_DIR}/loci_tools.jar"
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/loci_tools.jar"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/loci_tools.jar"
  DESTINATION "${BioFormatsImageIO_DIST_DIR}"
)

add_custom_command(
  TARGET BioFormatsIOPlugin
  DEPENDS "${BF_CPP_DIST_DIR}/BioFormatsSuffixes.txt"
  POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E copy
  "${BF_CPP_DIST_DIR}/BioFormatsSuffixes.txt"
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/BioFormatsSuffixes.txt"
  VERBATIM
)
install(FILES
  "${CMAKE_BINARY_DIR}/${BioFormatsImageIO_DIST_DIR}/BioFormatsSuffixes.txt"
  DESTINATION "${BioFormatsImageIO_DIST_DIR}"
)

SET(CPACK_PACKAGE_FILE_NAME "bf-itk")
SET(CPACK_GENERATOR "TGZ;ZIP")
INCLUDE( CPack )

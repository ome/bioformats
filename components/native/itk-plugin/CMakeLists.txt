#
# CMakeLists.txt
#

# OME Bio-Formats package for reading and converting biological file formats.
# Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

# Adapted from the Slicer3 project: http://www.slicer.org/
# http://viewvc.slicer.org/viewcvs.cgi/trunk/Libs/MGHImageIO/
#
# See slicer-license.txt for Slicer3's licensing information.
#
# For more information about the ITK Plugin IO mechanism, see:
# http://www.itk.org/Wiki/Plugin_IO_mechanisms

project(BioFormatsImageIO)

cmake_minimum_required(VERSION 2.4)
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
message(STATUS "-- Java Native Interface:")
message(STATUS "jawt lib         : ${JAVA_AWT_LIBRARY}")
message(STATUS "jvm lib          : ${JAVA_JVM_LIBRARY}")
message(STATUS "jni.h            : ${JAVA_INCLUDE_PATH}")
message(STATUS "jni_md.h         : ${JAVA_INCLUDE_PATH2}")
message(STATUS "jawt.h           : ${JAVA_AWT_INCLUDE_PATH}")
message(STATUS "")

# HACK - CMake on Windows refuses to find the thread library unless BOOST_ROOT
#        is set, even though it can locate the Boost directory tree.
#        So we first look for base Boost, then set BOOST_ROOT and look again
#        for Boost Thread specifically.

message(STATUS "-- Boost:")
set(Boost_USE_STATIC_LIBS OFF)
set(Boost_USE_MULTITHREAD ON)
set(Boost_ADDITIONAL_VERSIONS "1.37" "1.37.0" "1.37.1" "1.38" "1.38.0" "1.38.1"
  "1.39" "1.39.0" "1.39.1" "1.40" "1.40.0" "1.40.1" "1.41" "1.41.0" "1.41.1"
  "1.42" "1.42.0" "1.42.1" "1.43" "1.43.0" "1.43.1" "1.44" "1.44.0" "1.44.1")
set(Boost_FIND_QUIETLY ON)
find_package(Boost)
if(IS_DIRECTORY "${Boost_INCLUDE_DIR}")
  message(STATUS "boost headers : ${Boost_INCLUDE_DIR}")
else(IS_DIRECTORY "${Boost_INCLUDE_DIR}")
  if(UNIX)
    message(FATAL_ERROR "Cannot build without Boost Thread library. Please install libboost-thread-dev package or visit www.boost.org.")
  else(UNIX)
    message(FATAL_ERROR "Cannot build without Boost Thread library. Please install Boost from www.boost.org.")
  endif(UNIX)
endif(IS_DIRECTORY "${Boost_INCLUDE_DIR}")
set(Boost_FIND_QUIETLY OFF)
if(WIN32)
  set(BOOST_ROOT ${Boost_INCLUDE_DIR})
endif(WIN32)
find_package(Boost COMPONENTS thread REQUIRED)

# NB: Necessary on Windows to test for Boost Thread when
#     multiple semicolon-separated files are identified.
string(REGEX REPLACE ".*;" ""
  Boost_THREAD_LIB_CHECK "${Boost_THREAD_LIBRARY}")
string(REGEX REPLACE "/[^/]*$" ""
  Boost_STRIPPED_LIB_DIR "${Boost_THREAD_LIB_CHECK}")

if(EXISTS "${Boost_THREAD_LIB_CHECK}")
  message(STATUS "boost lib dir : ${Boost_STRIPPED_LIB_DIR}")
  message(STATUS "thread lib    : ${Boost_THREAD_LIBRARY}")
else(EXISTS "${Boost_THREAD_LIB_CHECK}")
  message(FATAL_ERROR "Cannot build without Boost Thread library. Please install libboost-thread-dev package or visit www.boost.org.")
endif(EXISTS "${Boost_THREAD_LIB_CHECK}")
message(STATUS "")

set(JACE_DIR NOTFOUND CACHE PATH "The path to toplevel directory of your Jace checkout")
if(NOT JACE_DIR)
  message(FATAL_ERROR "Please set JACE_DIR to the toplevel directory of your Jace checkout")
endif()

set(BF_JACE_DIR ${PROJECT_SOURCE_DIR}/../bf-cpp)

set(include_dirs
  ${CMAKE_CURRENT_SOURCE_DIR}
  ${CMAKE_CURRENT_BINARY_DIR}
  ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2} ${Boost_INCLUDE_DIR}
  ${JACE_DIR}/source/c++/include
  ${BF_JACE_DIR}/include
  ${BF_JACE_DIR}/proxies/include
)

#if(EXISTS "${ITK_DIR}/CMakeCache.txt")
#  set(include_dirs ${include_dirs} "${ITK_DIR}/Utilities/zlib")
#else(EXISTS "${ITK_DIR}/CMakeCache.txt")
#endif(EXISTS "${ITK_DIR}/CMakeCache.txt")

include_directories(${include_dirs})

link_directories(${BF_JACE_DIR}/build)

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
  jace
  bfjace
  ${JAVA_JVM_LIBRARY} ${Boost_LIBRARIES}
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
  set(BioFormatsImageIO_INSTALL_ITKFACTORIES_DIR lib/ITKFactories)
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
  jace
  bfjace
  ${JAVA_JVM_LIBRARY} ${Boost_LIBRARIES}
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

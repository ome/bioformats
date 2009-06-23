#
# CMakeLists.txt
#

# OME Bio-Formats C++ bindings for native access to Bio-Formats Java library.
# Copyright (C) 2008-@year@ UW-Madison LOCI and Glencoe Software, Inc.
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

# CMake build file for the Bio-Formats C++ bindings.

cmake_minimum_required(VERSION 2.6.4)

project(bfjace)

### search for prerequisite libraries ###

message(STATUS "")

#message("-- Java Runtime:")
#find_package(Java REQUIRED)
#message("java          : ${JAVA_RUNTIME}")
#message("javac         : ${JAVA_COMPILE}")
#message("jar           : ${JAVA_ARCHIVE}")
#message("")

message(STATUS "-- Java Native Interface:")
find_package(JNI REQUIRED)
message(STATUS "jawt lib      : ${JAVA_AWT_LIBRARY}")
message(STATUS "jvm lib       : ${JAVA_JVM_LIBRARY}")
message(STATUS "jni.h         : ${JAVA_INCLUDE_PATH}")
message(STATUS "jni_md.h      : ${JAVA_INCLUDE_PATH2}")
message(STATUS "jawt.h        : ${JAVA_AWT_INCLUDE_PATH}")
message(STATUS "")

# HACK - CMake on Windows refuses to find the thread library unless BOOST_ROOT
#        is set, even though it can locate the Boost directory tree.
#        So we first look for base Boost, then set BOOST_ROOT and look again
#        for Boost Thread specifically.

message(STATUS "-- Boost:")
set(Boost_USE_STATIC_LIBS OFF)
set(Boost_USE_MULTITHREADED ON)
set(Boost_ADDITIONAL_VERSIONS "1.37" "1.37.0" "1.37.1" "1.38" "1.38.0" "1.38.1"
  "1.39" "1.39.0" "1.39.1" "1.40" "1.40.0" "1.40.1" "1.41" "1.41.0" "1.41.1"
  "1.42" "1.42.0" "1.42.1" "1.43" "1.43.0" "1.43.1" "1.44" "1.44.0" "1.44.1")
#set(Boost_FIND_QUIETLY ON)
find_package(Boost)
  message(STATUS "boost headers : ${Boost_INCLUDE_DIR}")
if(IS_DIRECTORY "${Boost_INCLUDE_DIR}")
  message(STATUS "boost headers : ${Boost_INCLUDE_DIR}")
else(IS_DIRECTORY "${Boost_INCLUDE_DIR}")
  if(UNIX)
    message(FATAL_ERROR "Cannot build without Boost Thread library. Please install libboost-thread-dev package or visit www.boost.org.")
  else(UNIX)
    message(FATAL_ERROR "Cannot build without Boost Thread library. Please install Boost from www.boost.org.")
  endif(UNIX)
endif(IS_DIRECTORY "${Boost_INCLUDE_DIR}")
#set(Boost_FIND_QUIETLY OFF)
if(WIN32)
  set(BOOST_ROOT ${Boost_INCLUDE_DIR})
endif(WIN32)
find_package(Boost COMPONENTS thread REQUIRED)

# NB: Necessary on Windows to test for Boost Thread when
#     multiple semicolon-separated files are identified.
string(REGEX REPLACE "/[^/]*$" ""
  Boost_STRIPPED_LIB_DIR "${Boost_THREAD_LIBRARY_DEBUG}")

if(EXISTS "${Boost_THREAD_LIBRARY_DEBUG}")
  message(STATUS "boost lib dir : ${Boost_STRIPPED_LIB_DIR}")
  message(STATUS "thread lib    : ${Boost_THREAD_LIBRARY}")
else(EXISTS "${Boost_THREAD_LIBRARY_DEBUG}")
  message(FATAL_ERROR "Cannot build without Boost Thread library. Please install libboost-thread-dev package or visit www.boost.org.")
endif(EXISTS "${Boost_THREAD_LIBRARY_DEBUG}")
message(STATUS "")

message(STATUS "-- Jace:")
if(IS_DIRECTORY "${JACE_DIR}")
  message(STATUS "jace root     : ${JACE_DIR}")
else(IS_DIRECTORY "${JACE_DIR}")
  message(FATAL_ERROR "Cannot build without Jace. Please set JACE_DIR.")
endif(IS_DIRECTORY "${JACE_DIR}")

include_directories(include
  "${JACE_DIR}/source/c++/include" "proxies/include"
  "${JAVA_INCLUDE_PATH}" "${JAVA_INCLUDE_PATH2}"
  "${Boost_INCLUDE_DIR}")

# HACK: Make linking to Boost work on Windows systems.
if(WIN32)
  link_directories(${Boost_STRIPPED_LIB_DIR})
endif(WIN32)

### build Jace C++ library ###

file(GLOB_RECURSE JACE_SRC "${JACE_DIR}/source/c++/source/*.cpp")
#message("jace source   : ${JACE_SRC}")
message(STATUS "")

add_library(jace SHARED ${JACE_SRC})

# HACK: Make linking to Boost work on Windows systems.
if(WIN32)
  target_link_libraries(jace "${JAVA_JVM_LIBRARY}")
else(WIN32)
  target_link_libraries(jace "${JAVA_JVM_LIBRARY}" ${Boost_THREAD_LIBRARY})
endif(WIN32)

if(CMAKE_COMPILER_IS_GNUCXX)
  set_target_properties(jace PROPERTIES
    COMPILE_FLAGS "-fpermissive")
else(CMAKE_COMPILER_IS_GNUCXX)
  set_target_properties(jace PROPERTIES
    DEFINE_SYMBOL JACE_EXPORTS)
endif(CMAKE_COMPILER_IS_GNUCXX)

# NB: Mac OS X 10.4 and earlier requires .jnilib extension rather than
#     .dylib when loading native code with System.loadLibrary(String).
if(APPLE)
  set_target_properties(jace PROPERTIES SUFFIX ".jnilib")
endif(APPLE)

### build Bio-Formats C++ bindings ###

#message("-- Bio-Formats C++ bindings:")
file(GLOB_RECURSE BFJACE_SRC "proxies/source/*.cpp")
#message("bfjace source : ${BFJACE_SRC}")
#message("")

add_library(bfjace SHARED ${BFJACE_SRC})

target_link_libraries(bfjace jace)

if(CMAKE_COMPILER_IS_GNUCXX)
  set_target_properties(bfjace PROPERTIES
    COMPILE_FLAGS "-fpermissive")
else(CMAKE_COMPILER_IS_GNUCXX)
  set_target_properties(bfjace PROPERTIES
    DEFINE_SYMBOL JACE_PROXY_EXPORTS)
endif(CMAKE_COMPILER_IS_GNUCXX)

target_link_libraries(bfjace jace)

### build showinf example for testing bfjace ###

add_executable(showinf source/showinf.cpp)

target_link_libraries(showinf bfjace jace "${JAVA_JVM_LIBRARY}")

### TEMP - build some examples for testing ###

add_executable(example1 source/example1.cpp)

target_link_libraries(example1 bfjace jace "${JAVA_JVM_LIBRARY}")

add_executable(array_example source/array_example.cpp)

target_link_libraries(array_example bfjace jace "${JAVA_JVM_LIBRARY}")

#
# BoostThread.cmake
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

# CMake build file for cross-platform location of prerequisite libraries,
# including Boost Thread and Java's jni.h.

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
set(Boost_USE_STATIC_LIBS ON)
set(Boost_USE_MULTITHREADED ON)
set(Boost_ADDITIONAL_VERSIONS "1.37" "1.37.0" "1.37.1" "1.38" "1.38.0" "1.38.1"
  "1.39" "1.39.0" "1.39.1" "1.40" "1.40.0" "1.40.1" "1.41" "1.41.0" "1.41.1"
  "1.42" "1.42.0" "1.42.1" "1.43" "1.43.0" "1.43.1" "1.44" "1.44.0" "1.44.1")
#set(Boost_FIND_QUIETLY ON)
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
#set(Boost_FIND_QUIETLY OFF)
if(WIN32)
  set(BOOST_ROOT ${Boost_INCLUDE_DIR})
endif(WIN32)
find_package(Boost COMPONENTS thread REQUIRED)

# HACK - Make linking to Boost work on Windows systems.
string(REGEX REPLACE "/[^/]*$" ""
  Boost_STRIPPED_LIB_DIR "${Boost_THREAD_LIBRARY_DEBUG}")

if(EXISTS "${Boost_THREAD_LIBRARY_DEBUG}")
  message(STATUS "boost lib dir : ${Boost_STRIPPED_LIB_DIR}")
  message(STATUS "thread lib    : ${Boost_THREAD_LIBRARY_DEBUG}")
else(EXISTS "${Boost_THREAD_LIBRARY_DEBUG}")
  message(FATAL_ERROR "Cannot build without Boost Thread library. Please install libboost-thread-dev package or visit www.boost.org.")
endif(EXISTS "${Boost_THREAD_LIBRARY_DEBUG}")
message(STATUS "")

# HACK - Make linking to Boost work on Windows systems.
if(WIN32)
  link_directories(${Boost_STRIPPED_LIB_DIR})
endif(WIN32)

message(STATUS "-- Jace:")
set(JACE_DIR NOTFOUND CACHE PATH
  "The path to toplevel directory of your Jace checkout")
if(IS_DIRECTORY "${JACE_DIR}")
  message(STATUS "jace root     : ${JACE_DIR}")
else(IS_DIRECTORY "${JACE_DIR}")
  message(FATAL_ERROR "Cannot build without Jace. Please set JACE_DIR.")
endif(IS_DIRECTORY "${JACE_DIR}")

set(JACE_CPP_DIR "${JACE_DIR}/source/c++")
# For Jace r46 and later, use instead (& see build.properties):
#set(JACE_CPP_DIR "${JACE_DIR}/core/cpp")


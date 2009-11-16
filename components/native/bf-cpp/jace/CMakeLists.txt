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

# CMake build file for Jace C++ code.

if(WIN32)
cmake_minimum_required(VERSION 2.8)
else(WIN32)
cmake_minimum_required(VERSION 2.6)
endif(WIN32)

project(jace)

include(Prerequisites.cmake)

### build Jace C++ library ###

message(STATUS "-- Jace:")
set(JACE_DIR NOTFOUND CACHE PATH "The path to toplevel directory of your Jace checkout")
if(IS_DIRECTORY "${JACE_DIR}")
  message(STATUS "jace root     : ${JACE_DIR}")
else(IS_DIRECTORY "${JACE_DIR}")
  message(FATAL_ERROR "Cannot build without Jace. Please set JACE_DIR.")
endif(IS_DIRECTORY "${JACE_DIR}")

include_directories("${JACE_DIR}/source/c++/include"
  "${JAVA_INCLUDE_PATH}" "${JAVA_INCLUDE_PATH2}"
  "${Boost_INCLUDE_DIR}")

file(GLOB_RECURSE JACE_SRC "${JACE_DIR}/source/c++/source/*.cpp")
#message("jace source   : ${JACE_SRC}")
message(STATUS "")

add_library(jace SHARED ${JACE_SRC})

# NB: Mac OS X 10.4 and earlier requires .jnilib extension rather than
#     .dylib when loading native code with System.loadLibrary(String).
if(APPLE)
  set_target_properties(jace PROPERTIES SUFFIX ".jnilib")
endif(APPLE)

# HACK - Make linking to Boost work on Windows systems.
if(WIN32)
  target_link_libraries(jace "${JAVA_JVM_LIBRARY}")
else(WIN32)
  target_link_libraries(jace
    "${JAVA_JVM_LIBRARY}" ${Boost_THREAD_LIBRARY_DEBUG})
endif(WIN32)

if(CMAKE_COMPILER_IS_GNUCXX)
  set_target_properties(jace PROPERTIES COMPILE_FLAGS "-fpermissive")
else(CMAKE_COMPILER_IS_GNUCXX)
  set_target_properties(jace PROPERTIES DEFINE_SYMBOL JACE_EXPORTS)
endif(CMAKE_COMPILER_IS_GNUCXX)

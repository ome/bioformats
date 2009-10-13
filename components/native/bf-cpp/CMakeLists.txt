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

if(WIN32)
cmake_minimum_required(VERSION 2.6.4)
else(WIN32)
cmake_minimum_required(VERSION 2.6)
endif(WIN32)

project(bfcpp)

add_subdirectory(jace)

### build Bio-Formats C++ bindings ###

include_directories(include
  "proxies/include" "${JACE_DIR}/source/c++/include" "${Boost_INCLUDE_DIR}")

#message("-- Bio-Formats C++ bindings:")
file(GLOB_RECURSE BFJACE_SRC "proxies/source/*.cpp")
#message("bfcpp source  : ${BFJACE_SRC}")
#message("")

add_library(bfcpp SHARED ${BFJACE_SRC})

target_link_libraries(bfcpp jace)

if(CMAKE_COMPILER_IS_GNUCXX)
  set_target_properties(bfcpp PROPERTIES
    COMPILE_FLAGS "-fpermissive")
else(CMAKE_COMPILER_IS_GNUCXX)
  set_target_properties(bfcpp PROPERTIES
    DEFINE_SYMBOL JACE_PROXY_EXPORTS)
endif(CMAKE_COMPILER_IS_GNUCXX)

target_link_libraries(bfcpp jace)

### build some examples for testing bfcpp ###

add_executable(showinf source/showinf.cpp)

target_link_libraries(showinf bfcpp jace "${JAVA_JVM_LIBRARY}")

#add_executable(minimum_writer source/minimum_writer.cpp)

#target_link_libraries(minimum_writer bfcpp jace "${JAVA_JVM_LIBRARY}")

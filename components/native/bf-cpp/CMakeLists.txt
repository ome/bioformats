#
# CMakeLists.txt
#

# OME Bio-Formats C++ bindings for native access to Bio-Formats Java library.
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

# CMake build file for the Bio-Formats C++ bindings.

if(WIN32)
  cmake_minimum_required(VERSION 2.8)
else(WIN32)
  cmake_minimum_required(VERSION 2.6)
endif(WIN32)

project(bfcpp)

include(jace/Prerequisites.cmake)

add_subdirectory(jace)

#message(STATUS "-- Jace:")
#set(JACE_DIR NOTFOUND CACHE PATH
#  "The path to toplevel directory of your Jace checkout")
#if(IS_DIRECTORY "${JACE_DIR}")
#  message(STATUS "jace root     : ${JACE_DIR}")
#else(IS_DIRECTORY "${JACE_DIR}")
#  message(FATAL_ERROR "Cannot build without Jace. Please set JACE_DIR.")
#endif(IS_DIRECTORY "${JACE_DIR}")

### build Bio-Formats C++ bindings ###

include_directories(include
  "proxies/include" "${JACE_CPP_DIR}/include"
  "${JAVA_INCLUDE_PATH}" "${JAVA_INCLUDE_PATH2}" "${Boost_INCLUDE_DIR}")

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

add_executable(minimum_writer source/minimum_writer.cpp)

target_link_libraries(minimum_writer bfcpp jace "${JAVA_JVM_LIBRARY}")

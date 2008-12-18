#
# CMakeLists.txt
#

# CMake build file for Jace library
# Author: Curtis Rueden

cmake_minimum_required(VERSION 2.6)

project(jace)

find_package(JNI)

file(GLOB_RECURSE JACE_SRC source/*.cpp)
message("  jace source   : ${JACE_SRC}")

include_directories(include ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2})

add_library(jace SHARED ${JACE_SRC})

set_target_properties(jace PROPERTIES
  COMPILE_FLAGS "-Wall -fpermissive"
  LINK_FLAGS "-mthread")

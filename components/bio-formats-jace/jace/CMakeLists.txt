#
# CMakeLists.txt
#

# CMake build file for Jace library
# Author: Curtis Rueden

cmake_minimum_required(VERSION 2.6)

project(jace)

#message("")

#find_package(Java)
#message("-- Java Runtime:")
#message("java             : ${JAVA_RUNTIME}")
#message("javac            : ${JAVA_COMPILE}")
#message("jar              : ${JAVA_ARCHIVE}")

message("")

find_package(JNI)
message("-- Java Native Interface:")
message("jawt lib         : ${JAVA_AWT_LIBRARY}")
message("jvm lib          : ${JAVA_JVM_LIBRARY}")
message("jni.h            : ${JAVA_INCLUDE_PATH}")
message("jni_md.h         : ${JAVA_INCLUDE_PATH2}")
message("jawt.h           : ${JAVA_AWT_INCLUDE_PATH}")

message("")

set(Boost_USE_STATIC_LIBS OFF)
set(Boost_USE_MULTITHREAD ON)
find_package(Boost COMPONENTS thread)
message("-- Boost:")
message("boost/thread.hpp : ${Boost_INCLUDE_DIR}")
message("boost libs       : ${Boost_LIBRARIES}")

message("")

file(GLOB_RECURSE JACE_SRC source/*.cpp)
message("-- Jace source files:")
message("jace source      : ${JACE_SRC}")

message("")

include_directories(include
  ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2} ${Boost_INCLUDE_DIR})

add_library(jace SHARED ${JACE_SRC})

set_target_properties(jace PROPERTIES
  COMPILE_FLAGS "-pthread -Wall -fpermissive"
  LINK_FLAGS "-pthread")

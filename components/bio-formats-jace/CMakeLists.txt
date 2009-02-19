#
# CMakeLists.txt
#

# CMake build file for bfjace, the Bio-Formats C++ bindings.

cmake_minimum_required(VERSION 2.6)

project(bfjace)

### determine configuration ###

message("")

#find_package(Java REQUIRED)
#message("-- Java Runtime:")
#message("java             : ${JAVA_RUNTIME}")
#message("javac            : ${JAVA_COMPILE}")
#message("jar              : ${JAVA_ARCHIVE}")
#message("")

find_package(JNI REQUIRED)
message("-- Java Native Interface:")
message("jawt lib         : ${JAVA_AWT_LIBRARY}")
message("jvm lib          : ${JAVA_JVM_LIBRARY}")
message("jni.h            : ${JAVA_INCLUDE_PATH}")
message("jni_md.h         : ${JAVA_INCLUDE_PATH2}")
message("jawt.h           : ${JAVA_AWT_INCLUDE_PATH}")
message("")

set(Boost_USE_STATIC_LIBS OFF)
set(Boost_USE_MULTITHREAD ON)
find_package(Boost COMPONENTS thread REQUIRED)
message("-- Boost:")
if(EXISTS ${Boost_LIBRARIES})
message("boost/thread.hpp : ${Boost_INCLUDE_DIR}")
message("boost libs       : ${Boost_LIBRARIES}")
message("")
else(EXISTS ${Boost_LIBRARIES})
  message(FATAL_ERROR "Cannot build without Boost Thread library. Please install libboost-thread-dev package or visit www.boost.org.")
endif(EXISTS ${Boost_LIBRARIES})

if(IS_DIRECTORY ${JACE_DIR})
else(IS_DIRECTORY ${JACE_DIR})
  message(FATAL_ERROR "Cannot build without Jace. Please set JACE_DIR.")
endif(IS_DIRECTORY ${JACE_DIR})

include_directories(include ${JACE_DIR}/source/c++/include proxies/include
  ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2} ${Boost_INCLUDE_DIR})

### build Jace C++ library ###

message("-- Jace source files:")
message("jace directory   : ${JACE_DIR}")
file(GLOB_RECURSE JACE_SRC ${JACE_DIR}/source/c++/source/*.cpp)
#message("jace source      : ${JACE_SRC}")
message("")

add_library(jace SHARED ${JACE_SRC})

set_target_properties(jace PROPERTIES
  COMPILE_FLAGS "-pthread -Wall -fpermissive"
  LINK_FLAGS "-pthread")

### build Bio-Formats C++ bindings ###

message("-- Bio-Formats C++ source files:")
file(GLOB_RECURSE BFJACE_SRC proxies/source/*.cpp)
#message("  bfjace source : ${BFJACE_SRC}")
message("")

add_library(bfjace SHARED ${BFJACE_SRC})

set_target_properties(bfjace PROPERTIES
  COMPILE_FLAGS "-pthread -Wall -fpermissive"
  LINK_FLAGS "-pthread")

target_link_libraries(bfjace jace)

### build showinf example for testing bfjace ###

add_executable(showinf source/showinf.cpp)

target_link_libraries(showinf bfjace jace
  ${JAVA_JVM_LIBRARY} ${Boost_LIBRARIES})

### TEMP - build some examples for testing ###

add_executable(example1 source/example1.cpp)

target_link_libraries(example1 bfjace jace
  ${JAVA_JVM_LIBRARY} ${Boost_LIBRARIES})

add_executable(array_example source/array_example.cpp)

target_link_libraries(array_example bfjace jace
  ${JAVA_JVM_LIBRARY} ${Boost_LIBRARIES})

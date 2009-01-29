#
# CMakeLists.txt
#

# CMake build file for bfjace, the Bio-Formats Jace C++ bindings.

cmake_minimum_required(VERSION 2.6)

project(bio-formats-jace)

### determine configuration ###

find_package(JNI)

set(Boost_USE_STATIC_LIBS OFF)
set(Boost_USE_MULTITHREAD ON)
find_package(Boost COMPONENTS thread)

include_directories(jace/include proxies/include
  ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2} ${Boost_INCLUDE_DIR})

### build Jace C++ library ###

add_subdirectory(jace)

### build Bio-Formats Jace C++ bindings (bfjace) ###

message("-- Bio-Formats Jace source files:")
file(GLOB_RECURSE BFJACE_SRC proxies/source/*.cpp)
message("  bfjace source : ${BFJACE_SRC}")

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

### TEMP - build some Jace examples for testing ###

add_executable(example1 source/example1.cpp)

target_link_libraries(example1 bfjace jace
  ${JAVA_JVM_LIBRARY} ${Boost_LIBRARIES})

add_executable(array_example source/array_example.cpp)

target_link_libraries(array_example bfjace jace
  ${JAVA_JVM_LIBRARY} ${Boost_LIBRARIES})

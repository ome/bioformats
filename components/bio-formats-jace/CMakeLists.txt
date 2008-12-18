#
# CMakeLists.txt
#

# CMake build file for bfjace, the Bio-Formats Jace C++ bindings.

cmake_minimum_required(VERSION 2.6)

project(bio-formats-jace)

### determine configuration ###

message("Configuration:")

#find_package(Java)
#message("  java          : ${JAVA_RUNTIME}")
#message("  javac         : ${JAVA_COMPILE}")
#message("  jar           : ${JAVA_ARCHIVE}")

find_package(JNI)
message("  jawt lib      : ${JAVA_AWT_LIBRARY}")
message("  jvm lib       : ${JAVA_JVM_LIBRARY}")
message("  jni.h         : ${JAVA_INCLUDE_PATH}")
message("  jni_md.h      : ${JAVA_INCLUDE_PATH2}")
message("  jawt.h        : ${JAVA_AWT_INCLUDE_PATH}")

include_directories(jace/include proxies/include
  ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2})

### build Jace C++ library ###

add_subdirectory(jace)

### build Bio-Formats Jace C++ bindings (bfjace) ###

file(GLOB_RECURSE BFJACE_SRC proxies/source/*.cpp)
message("  bfjace source : ${BFJACE_SRC}")

add_library(bfjace SHARED ${BFJACE_SRC})

set_target_properties(bfjace PROPERTIES
  COMPILE_FLAGS "-Wall -fpermissive"
  LINK_FLAGS "-mthread")

target_link_libraries(bfjace jace)

### build showinf example for testing bfjace ###

add_executable(showinf source/showinf.cpp)

target_link_libraries(showinf bfjace jace ${JAVA_JVM_LIBRARY})

### TEMP - build some Jace examples for testing ###

add_executable(example1 source/example1.cpp)
target_link_libraries(example1 bfjace jace ${JAVA_JVM_LIBRARY})

add_executable(array_example source/array_example.cpp)
target_link_libraries(array_example bfjace jace ${JAVA_JVM_LIBRARY})

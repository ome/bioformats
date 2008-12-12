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
message("  jawt lib      : ${JAVA_AWT_LIB_PATH}")
message("  jvm lib       : ${JAVA_JVM_LIB_PATH}")
message("  jni.h         : ${JAVA_INCLUDE_PATH}")
message("  jni_md.h      : ${JAVA_INCLUDE_PATH2}")
message("  jawt.h        : ${JAVA_AWT_INCLUDE_PATH}")

include_directories(jace/include proxies/include)

### build Jace C++ library ###

add_subdirectory(jace)

### build Bio-Formats Jace C++ bindings (bfjace) ###

file(GLOB_RECURSE BFJACE_SRC proxies/source/*.cpp)
message("  bfjace source : ${BFJACE_SRC}")

add_library(bfjace SHARED ${BFJACE_SRC})

set_target_properties(bfjace PROPERTIES
  COMPILE_FLAGS "-Wall -fpermissive"
  LINK_FLAGS "-mthread -fPIC")

target_link_libraries(bfjace jace)

### build showinf example for testing bfjace ###

add_executable(showinf source/showinf.cpp)

target_link_libraries(showinf bfjace jace)

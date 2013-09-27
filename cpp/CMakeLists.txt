cmake_minimum_required(VERSION 2.8)


project(bioformats)
include(cmake/Version.cmake)

if("${PROJECT_SOURCE_DIR}" STREQUAL "${PROJECT_BINARY_DIR}")
  message(FATAL_ERROR "In-tree builds are not supported; please run cmake from a separate build directory.")
endif("${PROJECT_SOURCE_DIR}" STREQUAL "${PROJECT_BINARY_DIR}")

enable_language(CXX)

include(GNUInstallDirs)
include(CheckIncludeFileCXX)
include(CheckCXXCompilerFlag)
include(CheckCXXSourceCompiles)

include(FindBoost)
find_package(Boost REQUIRED COMPONENTS date_time filesystem system iostreams program_options regex)

include(cmake/CompilerChecks.cmake)
include(cmake/BoostChecks.cmake)
include(cmake/RegexChecks.cmake)
include(cmake/XsdFu.cmake)

find_package(Threads REQUIRED)
find_package(GTest REQUIRED)

set(OME_TOPLEVEL_INCLUDES ${PROJECT_SOURCE_DIR}/src ${GEN_DIR})

add_subdirectory(src/ome/compat)
add_subdirectory(src/ome/xerces)
add_subdirectory(src/ome/xml)

set(test_headers_SOURCES "")
file(MAKE_DIRECTORY ${GEN_DIR}/test/headers)
foreach(header ${MODEL_GENERATED_MODEL_HEADERS} ${MODEL_GENERATED_ENUM_HEADERS} ${OME_XML_STATIC_HEADERS})
  string(REPLACE "/" "_" genheader ${header})
  string(REPLACE "${PROJECT_SOURCE_DIR}/src/" "" include ${header})
  string(REPLACE "${GEN_DIR}/" "" include ${include})
  string(REGEX REPLACE "\\.h$" ".cpp" genheader ${genheader})
#  string(REGEX REPLACE "^(gen|src)/" "" include ${include})
  string(REGEX REPLACE "[/.]" "_" safeheader ${include})
  string(CONFIGURE "#include <@include@>

#include <gtest/gtest.h>

TEST(Header, ${safeheader})
{
}
" src)
  file(WRITE "${GEN_DIR}/test/headers/${genheader}" "${src}")
  list(APPEND test_headers_SOURCES "${GEN_DIR}/test/headers/${genheader}")
#  set(test_headers_SOURCES ${test_headers_SOURCES} PARENT_SCOPE )
endforeach(header)
add_executable(test/headers ${PROJECT_SOURCE_DIR}/test/main.cpp ${test_headers_SOURCES})
target_link_libraries(test/headers ome-xml)
target_link_libraries(test/headers ${GTEST_LIBRARIES} ${CMAKE_THREAD_LIBS_INIT})

enable_testing()
add_test(headers test/headers)

add_subdirectory(test)

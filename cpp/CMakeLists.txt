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

set(OME_TOPLEVEL_INCLUDES ${PROJECT_SOURCE_DIR}/lib ${PROJECT_BINARY_DIR}/lib)

add_subdirectory(lib/ome/compat)
add_subdirectory(lib/ome/xerces)
add_subdirectory(lib/ome/xml)

enable_testing()
add_subdirectory(test)

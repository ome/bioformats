# CMake build for bzip2
# Copyright © 2015 Roger Leigh <rleigh@codelibre.net>

cmake_minimum_required(VERSION 2.8.12)

# Default policy is from 2.8.12
cmake_policy(VERSION 2.8.12)
# Set MacOSX @rpath usage globally.
if (POLICY CMP0020)
  cmake_policy(SET CMP0020 NEW)
endif(POLICY CMP0020)
if (POLICY CMP0042)
  cmake_policy(SET CMP0042 NEW)
endif(POLICY CMP0042)
# Use new variable expansion policy.
if (POLICY CMP0053)
  cmake_policy(SET CMP0053 NEW)
endif(POLICY CMP0053)
if (POLICY CMP0054)
  cmake_policy(SET CMP0054 NEW)
endif(POLICY CMP0054)

project(bzip2 C)

set(VERSION "1.0.6")

include(GNUInstallDirs)
enable_testing()

add_definitions(-D_FILE_OFFSET_BITS=64)

include_directories("${CMAKE_SOURCE_DIR}")

#============================================================================
# libbz2
#============================================================================

set(BZIP2_PUBLIC_HEADERS
    bzlib.h)

set(BZIP2_PRIVATE_HEADERS
    bzlib_private.h)

set(BZIP2_SOURCES
    blocksort.c
    huffman.c
    crctable.c
    randtable.c
    compress.c
    decompress.c
    bzlib.c)

set(BZIP2_MANPAGES
    bzdiff.1
    bzgrep.1
    bzip2.1
    bzmore.1)

add_library(bz2 SHARED
            ${BZIP2_SOURCES}
            ${BZIP2_PUBLIC_HEADERS} ${BZIP2_PRIVATE_HEADERS}
            libbz2.def)
set_target_properties(bz2 PROPERTIES SOVERSION 1)

if(NOT CYGWIN)
    # This property causes shared libraries on Linux to have the full version
    # encoded into their final filename.  We disable this on Cygwin because
    # it causes cygz-${ZLIB_FULL_VERSION}.dll to be created when cygz.dll
    # seems to be the default.
    set_target_properties(bz2 PROPERTIES VERSION ${VERSION})
endif()

add_library(bz2static STATIC
            ${BZIP2_SOURCES}
            ${BZIP2_PUBLIC_HEADERS} ${BZIP2_PRIVATE_HEADERS}
            libbz2.def)
if (NOT WIN32)
  # Windows static and DLL import libraries both use .lib so the
  # static lib can't use the same name as the DLL
  set_target_properties(bz2static PROPERTIES OUTPUT_NAME bz2)
endif()

install(TARGETS bz2 bz2static
        RUNTIME DESTINATION "${CMAKE_INSTALL_FULL_BINDIR}"
        ARCHIVE DESTINATION "${CMAKE_INSTALL_FULL_LIBDIR}"
        LIBRARY DESTINATION "${CMAKE_INSTALL_FULL_LIBDIR}")

install(FILES ${BZIP2_PUBLIC_HEADERS}
        DESTINATION "${CMAKE_INSTALL_FULL_INCLUDEDIR}")

install(FILES ${BZIP2_MANPAGES}
        DESTINATION "${CMAKE_INSTALL_FULL_MANDIR}/man1")

#============================================================================
# Binaries
#============================================================================

add_executable(bzip2 bzip2.c)
target_link_libraries(bzip2 bz2)

add_executable(bzip2recover bzip2recover.c)
target_link_libraries(bzip2recover bz2)
set_target_properties(bzip2recover PROPERTIES OUTPUT_NAME bzip2recover)

install(TARGETS bzip2 bzip2recover
        RUNTIME
        DESTINATION "${CMAKE_INSTALL_FULL_BINDIR}")

# Copy or symlink bunzip2 and bzcat to bzip2
if (WIN32)
    install(CODE "execute_process(COMMAND \"${CMAKE_COMMAND}\" -E
            copy \"${CMAKE_INSTALL_FULL_BINDIR}/bzip2.exe\"
            \"${CMAKE_INSTALL_FULL_BINDIR}/bunzip2.exe\")")
    install(CODE "execute_process(COMMAND \"${CMAKE_COMMAND}\" -E
            copy \"${CMAKE_INSTALL_FULL_BINDIR}/bzip2.exe\"
            \"${CMAKE_INSTALL_FULL_BINDIR}/bzcat.exe\")")
else()
    install(CODE "execute_process(COMMAND \"${CMAKE_COMMAND}\" -E
            create_symlink bzip2
            \"\$ENV{DESTDIR}/${CMAKE_INSTALL_FULL_BINDIR}/bunzip2\")")
    install(CODE "execute_process(COMMAND \"${CMAKE_COMMAND}\" -E
            create_symlink bzip2
            \"\$ENV{DESTDIR}/${CMAKE_INSTALL_FULL_BINDIR}/bzcat\")")
endif()

set(SCRIPTS
    bzgrep
    bzmore
    bzdiff)

set(SCRIPT_ALIASES
    bzgrep:bzegrep
    bzgrep:bzfgrep
    bzmore:bzless
    bzdiff:bzcmp)

install(PROGRAMS ${SCRIPTS}
        DESTINATION ${CMAKE_INSTALL_FULL_BINDIR})

foreach(alias ${SCRIPT_ALIASES})
  string(REPLACE ":" ";" alias ${alias})
  list(GET alias 0 target)
  list(GET alias 1 dest)

  # Copy or symlink bunzip2 and bzcat to bzip2
  if (WIN32)
    install(CODE "execute_process(COMMAND \"${CMAKE_COMMAND}\" -E
            copy \"${CMAKE_INSTALL_FULL_BINDIR}/${target}\"
            \"${CMAKE_INSTALL_FULL_BINDIR}/${dest}\")")
    install(CODE "file(WRITE
                       \"${CMAKE_INSTALL_FULL_MANDIR}/man1/${dest}.1\"
                       \".so man1/${target}.1\")")
  else()
    install(CODE "execute_process(COMMAND \"${CMAKE_COMMAND}\" -E
            create_symlink ${target}
            \"\$ENV{DESTDIR}/${CMAKE_INSTALL_FULL_BINDIR}/${dest}\")")
    install(CODE "file(WRITE
                       \"\$ENV{DESTDIR}/${CMAKE_INSTALL_FULL_MANDIR}/man1/${dest}.1\"
                       \".so man1/${target}.1\")")
  endif()
endforeach()

#============================================================================
# Tests
#============================================================================

macro(add_bzip_test name arg input output expected_output)
  add_test(NAME "${name}"
           COMMAND "${CMAKE_COMMAND}"
           "-DBZIP2_COMMAND:FILEPATH=$<TARGET_FILE:bzip2>"
           "-DBZIP2_ARG=${arg}"
           "-DBZIP2_INPUT:FILEPATH=${CMAKE_SOURCE_DIR}/${input}"
           "-DBZIP2_OUTPUT:FILEPATH=${output}"
           "-DBZIP2_EXPECTED_OUTPUT:FILEPATH=${CMAKE_SOURCE_DIR}/${expected_output}"
           -P "${CMAKE_CURRENT_LIST_DIR}/RunTest.cmake")
endmacro()

# Test        name  opt input       output      expected-output
add_bzip_test(test0 -1  sample1.ref sample1.rb2 sample1.bz2)
add_bzip_test(test1 -2  sample2.ref sample2.rb2 sample2.bz2)
add_bzip_test(test2 -3  sample3.ref sample3.rb2 sample3.bz2)
add_bzip_test(test3 -d  sample1.bz2 sample1.tst sample1.ref)
add_bzip_test(test4 -d  sample2.bz2 sample2.tst sample2.ref)
add_bzip_test(test5 -ds sample3.bz2 sample3.tst sample3.ref)

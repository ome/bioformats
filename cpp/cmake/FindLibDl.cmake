#.rst:
# FindLibDl
# ---------
#
# Find the libdl headers and libraries.
#
# This module reports information about the libdl library used to
# load and introspect libraries and loadable modules on platforms
# using ELF- and Mach-O-linkers.
#
#   LibDl_FOUND - true if the libdl headers and libraries were found
#   LibDl_INCLUDE_DIRS - the directory containing the libdl headers
#   LibDl_LIBRARIES - libdl libraries to be linked
#
# The following cache variables may also be set::
#
#   LibDl_INCLUDE_DIR - the directory containing the libdl headers
#   LibDl_LIBRARY - the libdl library (if any)
#
# .. note::
#   On some platforms, such as FreeBSD, the libdl functions are
#   present in the C standard library and libdl is not required.
#   ``LibDl_LIBRARIES`` will be empty in this case.


# Written by Roger Leigh <rleigh@codelibre.net>

#=============================================================================
# Copyright 2014 Roger Leigh <rleigh@codelibre.net>
#
# Distributed under the OSI-approved BSD License (the "License");
# see accompanying file Copyright.txt for details.
#
# This software is distributed WITHOUT ANY WARRANTY; without even the
# implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the License for more information.
#=============================================================================
# (To distribute this file outside of CMake, substitute the full
#  License text for the above reference.)

# Find include directory
find_path(LibDl_INCLUDE_DIR
          NAMES "dlfcn.h"
          DOC "libdl include directory")
mark_as_advanced(LibDl_INCLUDE_DIR)

# Find all LibDl libraries
find_library(LibDl_LIBRARY "dl"
  DOC "libdl libraries (if not in the C library)")
mark_as_advanced(LibDl_LIBRARY)

include(FindPackageHandleStandardArgs)
FIND_PACKAGE_HANDLE_STANDARD_ARGS(LibDl
                                  REQUIRED_VARS LibDl_INCLUDE_DIR
                                  FAIL_MESSAGE "Failed to find libdl")

if(LIBDL_FOUND)
  set(LibDl_INCLUDE_DIRS "${LibDl_INCLUDE_DIR}")
  if(LibDl_LIBRARY)
    set(LibDl_LIBRARIES "${LibDl_LIBRARY}")
  else()
    unset(LibDl_LIBRARIES)
  endif()
endif()

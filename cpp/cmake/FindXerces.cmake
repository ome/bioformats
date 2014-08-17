#.rst:
# FindXerces
# -------
#
# Find the Apache Xerces-C++ validating XML parser headers and libraries.
#
# Use this module by invoking find_package with the form::
#
#   find_package(Xerces
#     [version] [EXACT]       # Minimum or EXACT version e.g. 3.1.1
#     [REQUIRED])             # Fail with error if Xerces is not found
#
# This module reports information about the Xerces installation in
# several variables.  General variables::
#
#   Xerces_VERSION - Xerces release version
#   Xerces_FOUND - true if the main programs and libraries were found
#   Xerces_INCLUDE_DIRS - the directory containing the Xerces headers
#   Xerces_LIBRARIES - Xerces libraries to be linked
#
# The following cache variables may also be set::
#
#   Xerces_INCLUDE_DIR - the directory containing the Xerces headers
#   Xerces_LIBRARY - the Xerces library

# Written by Roger Leigh <rleigh@codelibre.net>

#=============================================================================
# Copyright 2014 University of Dundee
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

# Derived from FindGTK2 _GTK2_GET_VERSION, modified for Xerces.
function(_Xerces_GET_VERSION _OUT_major _OUT_minor _OUT_revision _xercesversion_hdr)
    file(STRINGS ${_xercesversion_hdr} _contents REGEX "^[ \t]*#define XERCES_VERSION_.*")
    if(_contents)
        string(REGEX REPLACE ".*#define XERCES_VERSION_MAJOR[ \t]+([0-9]+).*" "\\1" ${_OUT_major} "${_contents}")
        string(REGEX REPLACE ".*#define XERCES_VERSION_MINOR[ \t]+([0-9]+).*" "\\1" ${_OUT_minor} "${_contents}")
        string(REGEX REPLACE ".*#define XERCES_VERSION_REVISION[ \t]+([0-9]+).*" "\\1" ${_OUT_revision} "${_contents}")

        if(NOT ${_OUT_major} MATCHES "^[0-9]+$")
            message(FATAL_ERROR "Version parsing failed for XERCES_VERSION_MAJOR!")
        endif()
        if(NOT ${_OUT_minor} MATCHES "^[0-9]+$")
            message(FATAL_ERROR "Version parsing failed for XERCES_VERSION_MINOR!")
        endif()
        if(NOT ${_OUT_revision} MATCHES "^[0-9]+$")
            message(FATAL_ERROR "Version parsing failed for XERCES_VERSION_REVISION!")
        endif()

        set("${_OUT_major}" "${${_OUT_major}}" PARENT_SCOPE)
        set("${_OUT_minor}" "${${_OUT_minor}}" PARENT_SCOPE)
        set("${_OUT_revision}" "${${_OUT_revision}}" PARENT_SCOPE)
    else()
        message(FATAL_ERROR "Include file ${_xercesversion_hdr} does not exist")
    endif()
endfunction(_Xerces_GET_VERSION)

function(_Xerces_FIND)
  # Find include directory
  find_path(Xerces_INCLUDE_DIR
            NAMES "xercesc/util/PlatformUtils.hpp"
            DOC "Xerces-C++ include directory")
  mark_as_advanced(Xerces_INCLUDE_DIR)
  set(Xerces_INCLUDE_DIR "${Xerces_INCLUDE_DIR}" PARENT_SCOPE)

  # Find all Xerces libraries
  find_library(Xerces_LIBRARY "xerces-c"
    DOC "Xerces-C++ libraries")
  mark_as_advanced(Xerces_LIBRARY)
  set(Xerces_LIBRARY "${Xerces_LIBRARY}" PARENT_SCOPE)

  _Xerces_GET_VERSION(Xerces_VERSION_MAJOR Xerces_VERSION_MINOR Xerces_VERSION_REVISION "${Xerces_INCLUDE_DIR}/xercesc/util/XercesVersion.hpp")
  set(Xerces_VERSION "${Xerces_VERSION_MAJOR}.${Xerces_VERSION_MINOR}.${Xerces_VERSION_REVISION}" PARENT_SCOPE)
endfunction(_Xerces_FIND)

_Xerces_FIND()

include(FindPackageHandleStandardArgs)
FIND_PACKAGE_HANDLE_STANDARD_ARGS(Xerces
                                  FOUND_VAR Xerces_FOUND
                                  REQUIRED_VARS Xerces_LIBRARY
                                                Xerces_INCLUDE_DIR
                                                Xerces_VERSION
                                  VERSION_VAR Xerces_VERSION
                                  FAIL_MESSAGE "Failed to find Xerces")

if(Xerces_FOUND)
  set(Xerces_INCLUDE_DIRS "${Xerces_INCLUDE_DIR}")
  set(Xerces_LIBRARIES "${Xerces_LIBRARY}")
endif(Xerces_FOUND)

# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2015 Open Microscopy Environment:
#   - Massachusetts Institute of Technology
#   - National Institutes of Health
#   - University of Dundee
#   - Board of Regents of the University of Wisconsin-Madison
#   - Glencoe Software, Inc.
# %%
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# The views and conclusions contained in the software and documentation are
# those of the authors and should not be interpreted as representing official
# policies, either expressed or implied, of any organization.
# #L%

cmake_policy(SET CMP0007 NEW)

function(ome_version)
  set(OME_VERSION UNKNOWN)
  set(OME_VERSION_SHORT UNKNOWN)
  set(OME_VCS_REVISION UNKNOWN)
  set(OME_VCS_DATE UNKNOWN)

  if(EXISTS "${PROJECT_SOURCE_DIR}/cpp/cmake/GitVersion.cmake")
    message(STATUS "Obtaining release version from source")
    include("${PROJECT_SOURCE_DIR}/cpp/cmake/GitVersion.cmake")
  else(EXISTS "${PROJECT_SOURCE_DIR}/cpp/cmake/GitVersion.cmake")
    message(STATUS "Obtaining version from git")
    execute_process(COMMAND git show -s --abbrev-commit HEAD
                    OUTPUT_VARIABLE show_output RESULT_VARIABLE show_fail ERROR_QUIET
                    WORKING_DIRECTORY ${PROJECT_SOURCE_DIR})
    if(NOT show_fail)
      string(REGEX REPLACE "\n" ";" show_output "${show_output}")
      foreach(line ${show_output})
        string(REGEX MATCH "^commit ([a-f0-9]+)" commit_valid ${line})
        if (commit_valid)
          string(REGEX REPLACE "^commit ([a-f0-9]+)" "\\1" OME_VCS_REVISION ${line})
        endif (commit_valid)
        string(REGEX MATCH "^Date: +([^\n]*)" date_valid ${line})
        if (date_valid)
          string(REGEX REPLACE "^Date: +([^\n]*)" "\\1" OME_VCS_DATE ${line})
        endif (date_valid)
      endforeach(line)
    endif(NOT show_fail)

    execute_process(COMMAND git describe --match=v[0-9]* --exact
                    OUTPUT_VARIABLE describe_exact_output
                    RESULT_VARIABLE describe_exact_fail
                    ERROR_QUIET
                    WORKING_DIRECTORY ${PROJECT_SOURCE_DIR})
    string(REPLACE "\n" "" describe_exact_output "${describe_exact_output}")

    execute_process(COMMAND git describe --match=v[0-9]*
                    OUTPUT_VARIABLE describe_output
                    RESULT_VARIABLE describe_fail
                    ERROR_QUIET
                    WORKING_DIRECTORY ${PROJECT_SOURCE_DIR})
    string(REPLACE "\n" "" describe_output "${describe_output}")

    if(NOT describe_exact_fail)
      set(OME_VERSION ${describe_exact_output})
    else(NOT describe_exact_fail)
      if(NOT describe_fail)
        set(OME_VERSION ${describe_output})
      else(NOT describe_fail)
        # ARGH!  We need a valid version for library and release
        # versioning, so this isn't acceptable.
        message(FATAL_ERROR "Release version is not known")
      endif(NOT describe_fail)
    endif(NOT describe_exact_fail)
  endif(EXISTS "${PROJECT_SOURCE_DIR}/cpp/cmake/GitVersion.cmake")

  string(REGEX MATCH "^v(.*)" commit_valid1 ${OME_VERSION})
  if (commit_valid1)
    string(REGEX REPLACE "^v(.*)" "\\1" OME_VERSION ${OME_VERSION})
  endif (commit_valid1)

  set(OME_VCS_REVISION ${OME_VCS_REVISION} PARENT_SCOPE)
  set(OME_VCS_DATE ${OME_VCS_DATE} PARENT_SCOPE)

  set(OME_VERSION ${OME_VERSION} PARENT_SCOPE)

  string(REGEX MATCH "([0-9]+)\\.([0-9]+)\\.([0-9]+)(.*)?" commit_valid ${OME_VERSION})
  if (commit_valid)
    string(REGEX REPLACE "^([0-9]+)\\.([0-9]+)\\.([0-9]+)(.*)?" "\\1;\\2;\\3;\\4" version_parts ${OME_VERSION})
    list(GET version_parts 0 version_major)
    set(OME_VERSION_MAJOR ${version_major} PARENT_SCOPE)
    list(GET version_parts 1 version_minor)
    set(OME_VERSION_MINOR ${version_minor} PARENT_SCOPE)
    list(GET version_parts 2 version_patch)
    set(OME_VERSION_PATCH ${version_patch} PARENT_SCOPE)
    list(GET version_parts 3 version_extra)
    set(OME_VERSION_EXTRA ${version_extra} PARENT_SCOPE)
    set(OME_VERSION_SHORT "${version_major}.${version_minor}.${version_patch}" PARENT_SCOPE)
  else(commit_valid)
    set(OME_VERSION_MAJOR UNKNOWN PARENT_SCOPE)
    set(OME_VERSION_MINOR UNKNOWN PARENT_SCOPE)
    set(OME_VERSION_PATCH UNKNOWN PARENT_SCOPE)
    set(OME_VERSION_EXTRA UNKNOWN PARENT_SCOPE)
    set(OME_VERSION_SHORT UNKNOWN PARENT_SCOPE)
  endif(commit_valid)
endfunction(ome_version)

ome_version()

message(STATUS "Configuring Bio-Formats version ${OME_VERSION}")
if(OME_VCS_REVISION AND OME_VCS_DATE)
  message(STATUS "Using git commit ${OME_VCS_REVISION} on ${OME_VCS_DATE}")
endif(OME_VCS_REVISION AND OME_VCS_DATE)

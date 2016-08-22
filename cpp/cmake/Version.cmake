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

function(ome_version GITDIR)
  set(OME_VERSION UNKNOWN)
  set(OME_VERSION_SHORT UNKNOWN)
  set(OME_VCS_SHORTREVISION UNKNOWN)
  set(OME_VCS_REVISION UNKNOWN)
  set(OME_VCS_DATE UNKNOWN)
  set(OME_VCS_DATE_S UNKNOWN)

  if(EXISTS "${GITDIR}/cpp/cmake/GitVersion.cmake")
    message(STATUS "Obtaining release version from source")
    include("${GITDIR}/cpp/cmake/GitVersion.cmake")
  else(EXISTS "${GITDIR}/cpp/cmake/GitVersion.cmake")
    message(STATUS "Obtaining version from git")

    find_package(Git)

    if(NOT GIT_FOUND)
      message(FATAL_ERROR "No git executable found for getting version")
    endif(NOT GIT_FOUND)

    execute_process(COMMAND "${GIT_EXECUTABLE}" log -1 HEAD --pretty=%h
      OUTPUT_VARIABLE commit_hash_short RESULT_VARIABLE git_log_fail ERROR_QUIET
      WORKING_DIRECTORY ${GITDIR})
    if (git_log_fail)
      message(FATAL_ERROR "Could not obtain release commit hash from git")
    endif (git_log_fail)
    string(REPLACE "\n" "" commit_hash_short "${commit_hash_short}")

    execute_process(COMMAND "${GIT_EXECUTABLE}" log -1 HEAD --pretty=%H
      OUTPUT_VARIABLE commit_hash RESULT_VARIABLE git_log_fail ERROR_QUIET
      WORKING_DIRECTORY ${GITDIR})
    if (git_log_fail)
      message(FATAL_ERROR "Could not obtain release commit hash from git")
    endif (git_log_fail)
    string(REPLACE "\n" "" commit_hash "${commit_hash}")

    execute_process(COMMAND "${GIT_EXECUTABLE}" log -1 "${commit_hash}" --pretty=%ai
      OUTPUT_VARIABLE commit_date_string RESULT_VARIABLE git_log_fail ERROR_QUIET
      WORKING_DIRECTORY ${GITDIR})
    if (git_log_fail)
      message(FATAL_ERROR "Could not obtain release commit timestamp string from git")
    endif (git_log_fail)
    string(REPLACE "\n" "" commit_date_string "${commit_date_string}")

    execute_process(COMMAND "${GIT_EXECUTABLE}" log -1 "${commit_hash}" --pretty=%at
      OUTPUT_VARIABLE commit_date_unix RESULT_VARIABLE git_log_fail ERROR_QUIET
      WORKING_DIRECTORY ${GITDIR})
    if (git_log_fail)
      message(FATAL_ERROR "Could not obtain release commit timestamp from git")
    endif (git_log_fail)
    string(REPLACE "\n" "" commit_date_unix "${commit_date_unix}")

    set(OME_VCS_SHORTREVISION ${commit_hash_short})
    set(OME_VCS_REVISION ${commit_hash})
    set(OME_VCS_DATE ${commit_date_unix})
    set(OME_VCS_DATE_S ${commit_date_string})

    execute_process(COMMAND "${GIT_EXECUTABLE}" describe --match=v[0-9]* --exact
                    OUTPUT_VARIABLE describe_exact_output
                    RESULT_VARIABLE describe_exact_fail
                    ERROR_QUIET
                    WORKING_DIRECTORY ${GITDIR})
    string(REPLACE "\n" "" describe_exact_output "${describe_exact_output}")

    execute_process(COMMAND "${GIT_EXECUTABLE}" describe --match=v[0-9]*
                    OUTPUT_VARIABLE describe_output
                    RESULT_VARIABLE describe_fail
                    ERROR_QUIET
                    WORKING_DIRECTORY ${GITDIR})
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
  endif(EXISTS "${GITDIR}/cpp/cmake/GitVersion.cmake")

  set(OME_VCS_SHORTREVISION "${OME_VCS_SHORTREVISION}" PARENT_SCOPE)
  set(OME_VCS_REVISION "${OME_VCS_REVISION}" PARENT_SCOPE)
  set(OME_VCS_DATE "${OME_VCS_DATE}" PARENT_SCOPE)
  set(OME_VCS_DATE_S "${OME_VCS_DATE_S}" PARENT_SCOPE)

  string(REGEX MATCH "^v(.*)" commit_valid1 ${OME_VERSION})
  if (commit_valid1)
    string(REGEX REPLACE "^v(.*)" "\\1" OME_VERSION ${OME_VERSION})
  endif (commit_valid1)

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

macro(ome_project_version OME_PROJECT GITDIR)
  ome_version("${GITDIR}")

  message(STATUS "Configuring ${OME_PROJECT} version ${OME_VERSION}")
  if(OME_VCS_SHORTREVISION AND OME_VCS_DATE_S)
    message(STATUS "Using git commit ${OME_VCS_SHORTREVISION} on ${OME_VCS_DATE_S}")
  endif()
endmacro()

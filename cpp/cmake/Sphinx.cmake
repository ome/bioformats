# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2015 Open Microscopy Environment:
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

# Sphinx documentation generator
find_program(SPHINX_BUILD sphinx-build)
if (SPHINX_BUILD)
  message(STATUS "Looking for sphinx-build - ${SPHINX_BUILD}")
else()
  message(STATUS "Looking for sphinx-build - not found")
endif()
if (SPHINX_BUILD)
  message(STATUS "Generating manual pages with sphinx-build")
  set(ENV{SOURCE_BRANCH})
  set(ENV{SOURCE_USER} openmicroscopy)
  set(ENV{BF_RELEASE} "${OME_VERSION}")
  set(ENV{JENKINS_JOB})
  set(ENV{JENKINS_CPP_JOB})
  set(ENV{OMERODOC_URI})
  set(ENV{SOURCE_BRANCH})
  set(ENV{SOURCE_BRANCH})
  file(MAKE_DIRECTORY "${PROJECT_BINARY_DIR}/cpp/man")
  execute_process(COMMAND ${SPHINX_BUILD} -q -b man
                          "${PROJECT_SOURCE_DIR}/docs/sphinx" cpp/man
                  RESULT_VARIABLE sphinx_man_fail
                  ERROR_FILE cpp/man/sphinx.log)
  if (sphinx_man_fail)
    message(WARNING "Failed to generate manual pages; see cpp/man/sphinx.log")
  else()
    foreach(section 1 3 5 7 8)
      file(GLOB manpages "${PROJECT_BINARY_DIR}/cpp/man/*.${section}")
      if (manpages)
      foreach (man ${manpages})
        get_filename_component(manfile "${man}" NAME)
        file(MAKE_DIRECTORY "${PROJECT_BINARY_DIR}/cpp/man/man${section}")
        file(RENAME "${man}" "${PROJECT_BINARY_DIR}/cpp/man/man${section}/${manfile}")
        install(FILES "${PROJECT_BINARY_DIR}/cpp/man/man${section}/${manfile}"
                DESTINATION "${CMAKE_INSTALL_FULL_MANDIR}/man${section}")
        endforeach()
      endif()
    endforeach()
  endif()
else()
  message(WARNING "Manual pages will not be generated or installed")
endif()

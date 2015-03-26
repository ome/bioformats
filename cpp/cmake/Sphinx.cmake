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
  message(STATUS "Checking manual page dependencies")
  execute_process(COMMAND python -B ${CMAKE_CURRENT_LIST_DIR}/list-manpages.py
                          "${PROJECT_SOURCE_DIR}/docs/sphinx"
                          "${PROJECT_BINARY_DIR}/cpp/man"
                  RESULT_VARIABLE sphinx_man_fail
                  OUTPUT_VARIABLE MAN_PAGES
                  ERROR_FILE cpp/man/sphinx-man.log)
  string(REPLACE "\n" ";" MAN_PAGES "${MAN_PAGES}")
  execute_process(COMMAND python -B ${CMAKE_CURRENT_LIST_DIR}/list-manpage-dependencies.py
                          "${PROJECT_SOURCE_DIR}/docs/sphinx"
                  RESULT_VARIABLE sphinx_man_dep_fail
                  OUTPUT_VARIABLE MAN_PAGE_DEPENDENCIES
                  ERROR_FILE cpp/man/sphinx-man-dep.log)
  string(REPLACE "\n" ";" MAN_PAGE_DEPENDENCIES "${MAN_PAGE_DEPENDENCIES}")

  file(MAKE_DIRECTORY "${PROJECT_BINARY_DIR}/cpp/man")
  file(MAKE_DIRECTORY "${PROJECT_BINARY_DIR}/cpp/doc/sphinx")
  add_custom_command(OUTPUT ${MAN_PAGES}
                     COMMAND ${SPHINX_BUILD}
                             -D "release=${OME_VERSION_SHORT}"
                             -D "version=${OME_VERSION_MAJOR}.${OME_VERSION_MINOR}"
                             -b man
                             "${PROJECT_SOURCE_DIR}/docs/sphinx" cpp/man
                     DEPENDS ${MAN_PAGE_DEPENDENCIES})

  add_custom_target(man ALL DEPENDS ${MAN_PAGES})

  foreach (man ${MAN_PAGES})
    string(REGEX REPLACE ".*(.)\$" "\\1" man_section "${man}")
    install(FILES "${man}"
            DESTINATION "${CMAKE_INSTALL_FULL_MANDIR}/man${man_section}")
  endforeach()
else()
  message(WARNING "Manual pages will not be generated or installed")
endif()

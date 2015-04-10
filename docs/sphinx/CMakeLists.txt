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

include(Sphinx)

if (BUILD_SPHINX AND SPHINX_BUILD)
  message(STATUS "Checking manual page dependencies")

  # Create build directory and conf.py
  set(sphinx_srcdir "${PROJECT_SOURCE_DIR}/docs/sphinx")
  set(sphinx_builddir "${PROJECT_BINARY_DIR}/docs/sphinx")
  file(MAKE_DIRECTORY "${sphinx_builddir}")
  configure_file("${sphinx_srcdir}/conf.py.in"
                 "${sphinx_builddir}/conf.py"
                 @ONLY)

  sphinx_manpages("${sphinx_srcdir}" "${sphinx_builddir}"
                  "${PROJECT_BINARY_DIR}/cpp/man" MAN_PAGES)
  sphinx_manpage_dependencies("${sphinx_srcdir}" "${sphinx_builddir}"
                              MAN_PAGE_DEPENDENCIES)
  sphinx_dependencies("${sphinx_srcdir}" SPHINX_DEPENDENCIES)

  # Generate and install man pages

  add_custom_command(OUTPUT ${MAN_PAGES}
                     COMMAND ${CMAKE_COMMAND} -E make_directory "${sphinx_builddir}/cache"
                     COMMAND ${CMAKE_COMMAND} -E make_directory "${PROJECT_BINARY_DIR}/cpp/man"
                     COMMAND ${SPHINX_BUILD}
                             -D "release=${OME_VERSION_SHORT}"
                             -D "version=${OME_VERSION_MAJOR}.${OME_VERSION_MINOR}"
                             -c "${sphinx_builddir}"
                             -d "${sphinx_builddir}/cache"
                             -b man
                             "${sphinx_srcdir}" "${PROJECT_BINARY_DIR}/cpp/man"
                     WORKING_DIRECTORY "${sphinx_srcdir}"
                     DEPENDS ${MAN_PAGE_DEPENDENCIES})

  add_custom_target(man ALL DEPENDS ${MAN_PAGES})

  foreach (man ${MAN_PAGES})
    string(REGEX REPLACE ".*(.)\$" "\\1" man_section "${man}")
    install(FILES "${man}"
            DESTINATION "${CMAKE_INSTALL_FULL_MANDIR}/man${man_section}")
  endforeach()

  # Generate and install HTML manual
  # Depends on man to allow sharing of cache with parallel build.

  add_custom_command(OUTPUT "${sphinx_builddir}/html/index.html"
                     COMMAND ${CMAKE_COMMAND} -E make_directory "${sphinx_builddir}/cache"
                     COMMAND ${CMAKE_COMMAND} -E make_directory "${sphinx_builddir}/html"
                     COMMAND ${SPHINX_BUILD}
                             -D "release=${OME_VERSION_SHORT}"
                             -D "version=${OME_VERSION_MAJOR}.${OME_VERSION_MINOR}"
                             -c "${sphinx_builddir}"
                             -d "${sphinx_builddir}/cache"
                             -b html
                             "${sphinx_srcdir}" "${sphinx_builddir}/html"
                     WORKING_DIRECTORY "${sphinx_srcdir}"
                     DEPENDS man ${SPHINX_DEPENDENCIES})

  add_custom_target(doc-html ALL DEPENDS "${sphinx_builddir}/html/index.html")

  install(DIRECTORY "${sphinx_builddir}/html"
          DESTINATION "${CMAKE_INSTALL_FULL_DOCDIR}/manual")

  add_custom_target(doc-linkcheck
                    COMMAND ${CMAKE_COMMAND} -E make_directory "${sphinx_builddir}/cache"
                    COMMAND ${CMAKE_COMMAND} -E make_directory "${sphinx_builddir}/linkcheck"
                    COMMAND ${SPHINX_BUILD}
                            -D "release=${OME_VERSION_SHORT}"
                            -D "version=${OME_VERSION_MAJOR}.${OME_VERSION_MINOR}"
                            -c "${sphinx_builddir}"
                            -d "${sphinx_builddir}/cache"
                            -b linkcheck
                            "${sphinx_srcdir}" "${sphinx_builddir}/linkcheck"
                     WORKING_DIRECTORY "${sphinx_srcdir}"
                     DEPENDS man ${SPHINX_DEPENDENCIES})

  # Generate and install PDF manual
  # Depends on man to allow sharing of cache with parallel build.

  if (BUILD_SPHINX_PDF AND XELATEX AND MAKEINDEX)
    add_custom_command(OUTPUT "${sphinx_builddir}/latex/Bio-Formats.tex" "${sphinx_builddir}/latex/preamble.tex"
                       COMMAND ${CMAKE_COMMAND} -E make_directory "${sphinx_builddir}/cache"
                       COMMAND ${CMAKE_COMMAND} -E make_directory "${sphinx_builddir}/latex"
                       COMMAND ${SPHINX_BUILD}
                               -D "release=${OME_VERSION_SHORT}"
                               -D "version=${OME_VERSION_MAJOR}.${OME_VERSION_MINOR}"
                               -c "${sphinx_builddir}"
                               -d "${sphinx_builddir}/cache"
                               -b latex
                               "${sphinx_srcdir}" "${sphinx_builddir}/latex"
                       COMMAND ${CMAKE_COMMAND} -E copy "preamble.tex" "${sphinx_builddir}/latex"
                       WORKING_DIRECTORY "${sphinx_srcdir}"
                       DEPENDS man ${SPHINX_DEPENDENCIES})
    add_custom_command(OUTPUT "${sphinx_builddir}/latex/Bio-Formats.pdf"
                       COMMAND ${XELATEX} "Bio-Formats.tex"
                       COMMAND ${XELATEX} "Bio-Formats.tex"
                       COMMAND ${MAKEINDEX} -s python.ist "Bio-Formats.idx"
                       COMMAND ${XELATEX} "Bio-Formats.tex"
                       COMMAND ${XELATEX} "Bio-Formats.tex"
                       COMMAND ${XELATEX} "Bio-Formats.tex"
                       WORKING_DIRECTORY "${sphinx_builddir}/latex"
                       DEPENDS "${sphinx_builddir}/latex/Bio-Formats.tex" "${sphinx_builddir}/latex/preamble.tex")

    add_custom_target(doc-pdf ALL DEPENDS "${sphinx_builddir}/latex/Bio-Formats.pdf")

    install(FILES "${sphinx_builddir}/latex/Bio-Formats.pdf"
            DESTINATION "${CMAKE_INSTALL_FULL_DOCDIR}/manual")
  endif (BUILD_SPHINX_PDF AND XELATEX AND MAKEINDEX)
else()
  message(WARNING "Manual pages and HTML manual will not be generated or installed")
endif()

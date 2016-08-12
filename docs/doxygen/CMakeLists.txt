# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2016 Open Microscopy Environment:
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

if(BUILD_DOXYGEN)
  set(OME_COMMON_TAGFILE "${CMAKE_INSTALL_FULL_DATADIR}/doc/ome-common/api/ome-common.tag")

  configure_file(${CMAKE_CURRENT_SOURCE_DIR}/ome-xml.dox.cmake
                 ${CMAKE_CURRENT_BINARY_DIR}/ome-xml.dox @ONLY)

  add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/ome-xml.log
                     COMMAND ${CMAKE_COMMAND} -E remove_directory ome-xml
                     COMMAND ${CMAKE_COMMAND} -E make_directory ome-xml
                     COMMAND ${DOXYGEN_EXECUTABLE} ${CMAKE_CURRENT_BINARY_DIR}/ome-xml.dox
                     DEPENDS gensrc ${CMAKE_CURRENT_BINARY_DIR}/ome-xml.dox)

  add_custom_target(doc-check
                    COMMAND ${CMAKE_COMMAND} -Dlogfile=${CMAKE_CURRENT_BINARY_DIR}/ome-xml.log -P ${PROJECT_SOURCE_DIR}/cpp/cmake/DoxygenCheck.cmake
                    DEPENDS ${CMAKE_CURRENT_BINARY_DIR}/ome-xml.log)

  add_custom_target(doc-api ALL DEPENDS ${CMAKE_CURRENT_BINARY_DIR}/ome-xml.log doc-check)
  # For backward compatibility
  add_custom_target(doc DEPENDS doc-api)

  install(DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/ome-xml/"
          DESTINATION "${CMAKE_INSTALL_DOCDIR}/api"
          COMPONENT "development")
endif(BUILD_DOXYGEN)

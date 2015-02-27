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

include_directories(${OME_TOPLEVEL_INCLUDES})

set(ome_xerces_sources
    ErrorReporter.cpp)

set(ome_xerces_headers
    ErrorReporter.h
    Platform.h
    String.h)

set(ome_xerces_dom_headers
    dom/Document.h
    dom/Element.h
    dom/Node.h
    dom/NodeList.h)

add_library(ome-xerces SHARED ${ome_xerces_sources} ${ome_xerces_headers} ${ome_xerces_dom_headers})
target_link_libraries(ome-xerces ${XERCES_LIBRARY})
set_target_properties(ome-xerces PROPERTIES VERSION ${OME_VERSION_SHORT})

set(ome_xerces_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/xerces")

install(FILES ${ome_xerces_headers}
        DESTINATION ${ome_xerces_includedir})
install(FILES ${ome_xerces_dom_headers}
        DESTINATION ${ome_xerces_includedir}/dom)
install(TARGETS ome-xerces LIBRARY
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR})

set(LIBRARY_PREFIX OME_XERCES)
set(LIBRARY_NAME ome-xerces)
set(LIBRARY_HEADER ome/xerces/Platform.h)
configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateConfig.cmake.in
               ${CMAKE_CURRENT_BINARY_DIR}/ome-xerces-config.cmake)
install(FILES ${CMAKE_CURRENT_BINARY_DIR}/ome-xerces-config.cmake
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR}/cmake)

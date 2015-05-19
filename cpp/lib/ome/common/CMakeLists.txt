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

include_directories(${OME_TOPLEVEL_INCLUDES}
                    ${Boost_INCLUDE_DIRS}
                    ${LibDl_INCLUDE_DIRS}
                    ${XercesC_INCLUDE_DIRS})

configure_file(${CMAKE_CURRENT_SOURCE_DIR}/config.h.in
               ${CMAKE_CURRENT_BINARY_DIR}/config.h)

set(ome_common_static_headers
    boolean.h
    filesystem.h
    endian.h
    log.h
    module.h
    mstream.h
    string.h
    units.h
    variant.h)

set(ome_common_endian_static_headers
    endian/conversion.hpp
    endian/std_pair.hpp
    endian/types.hpp)

set(ome_common_endian_detail_static_headers
    endian/detail/config.hpp
    endian/detail/cover_operators.hpp
    endian/detail/disable_warnings.hpp
    endian/detail/disable_warnings_pop.hpp
    endian/detail/intrinsic.hpp)

set(ome_common_xml_static_headers
    xml/EntityResolver.h
    xml/ErrorReporter.h
    xml/Platform.h
    xml/String.h)

set(ome_common_xml_dom_static_headers
    xml/dom/Base.h
    xml/dom/Document.h
    xml/dom/Element.h
    xml/dom/NamedNodeMap.h
    xml/dom/Node.h
    xml/dom/NodeList.h
    xml/dom/Wrapper.h)

set(ome_common_generated_headers
   ${CMAKE_CURRENT_BINARY_DIR}/config.h)

set(ome_common_headers
    ${ome_common_static_headers}
    ${ome_common_xml_static_headers}
    ${ome_common_xml_dom_static_headers}
    ${ome_common_generated_headers})

set(ome_common_sources
    log.cpp
    module.cpp
    xml/EntityResolver.cpp
    xml/ErrorReporter.cpp
    xml/dom/Document.cpp
    xml/dom/NamedNodeMap.cpp
    xml/dom/NodeList.cpp)

add_library(ome-common SHARED ${ome_common_sources} ${ome_common_headers})

if(CMAKE_MAJOR_VERSION GREATER 2)
  target_link_libraries(ome-common ome-compat)
else()
  add_library(ome-compat ALIAS ome-common)
  target_link_libraries(ome-common ${REGEX_LIBRARY})
endif()

target_link_libraries(ome-common
                      ${Boost_LOG_SETUP_LIBRARY_RELEASE}
                      ${Boost_LOG_LIBRARY_RELEASE}
                      ${Boost_FILESYSTEM_LIBRARY_RELEASE}
                      ${Boost_SYSTEM_LIBRARY_RELEASE}
                      ${LibDl_LIBRARIES}
                      ${XercesC_LIBRARIES})

set_target_properties(ome-common PROPERTIES LINKER_LANGUAGE CXX)
set_target_properties(ome-common PROPERTIES VERSION ${OME_VERSION_SHORT})

install(TARGETS ome-common LIBRARY
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR})

set(ome_common_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/common")

install(FILES ${ome_common_static_headers} ${ome_common_generated_headers}
        DESTINATION ${ome_common_includedir})
install(FILES ${ome_common_endian_static_headers}
        DESTINATION ${ome_common_includedir}/endian)
install(FILES ${ome_common_endian_detail_static_headers}
        DESTINATION ${ome_common_includedir}/endian/detail)
install(FILES ${ome_common_xml_static_headers}
        DESTINATION ${ome_common_includedir}/xml)
install(FILES ${ome_common_xml_dom_static_headers}
        DESTINATION ${ome_common_includedir}/xml/dom)

# Dump header list for testing
header_include_list_write(ome_common_static_headers ome_common_generated_headers ome/common ${PROJECT_BINARY_DIR}/cpp/test/ome-common)

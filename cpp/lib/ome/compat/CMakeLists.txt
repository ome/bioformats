# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2014 Open Microscopy Environment:
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
                    ${LibDl_INCLUDE_DIRS})

configure_file(${CMAKE_CURRENT_SOURCE_DIR}/config.h.in
               ${CMAKE_CURRENT_BINARY_DIR}/config.h)

set(ome_compat_static_headers
    array.h
    cstdint.h
    filesystem.h
    endian.h
    memory.h
    module.h
    mstream.h
    regex.h
    string.h
    thread.h
    tuple.h
    variant.h)

set(ome_compat_endian_static_headers
    endian/conversion.hpp
    endian/std_pair.hpp
    endian/types.hpp)

set(ome_compat_endian_detail_static_headers
    endian/detail/config.hpp
    endian/detail/cover_operators.hpp
    endian/detail/disable_warnings.hpp
    endian/detail/disable_warnings_pop.hpp
    endian/detail/intrinsic.hpp)

set(ome_compat_generated_headers
   ${CMAKE_CURRENT_BINARY_DIR}/config.h)

set(ome_compat_headers
    ${ome_compat_static_headers}
    ${ome_compat_generated_headers})

set(ome_compat_sources
    module.cpp)

add_library(ome-compat SHARED ${ome_compat_sources} ${ome_compat_headers})
target_link_libraries(ome-compat
                      ${REGEX_LIBRARY}
                      ${THREAD_LIBRARY}
                      ${Boost_FILESYSTEM_LIBRARY_RELEASE}
                      ${Boost_SYSTEM_LIBRARY_RELEASE}
                      ${LibDl_LIBRARIES})
set_target_properties(ome-compat PROPERTIES LINKER_LANGUAGE CXX)
set_target_properties(ome-compat PROPERTIES VERSION ${OME_VERSION_SHORT})

install(TARGETS ome-compat LIBRARY
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR})

set(ome_compat_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/compat")

install(FILES ${ome_compat_static_headers} ${ome_compat_generated_headers}
        DESTINATION ${ome_compat_includedir})
install(FILES ${ome_compat_endian_static_headers}
        DESTINATION ${ome_compat_includedir}/endian)
install(FILES ${ome_compat_endian_detail_static_headers}
        DESTINATION ${ome_compat_includedir}/endian/detail)

# Dump header list for testing
header_include_list_write(ome_compat_static_headers ome_compat_generated_headers ome/compat ${PROJECT_BINARY_DIR}/cpp/test/ome-compat)

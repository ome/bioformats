# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2013 Open Microscopy Environment:
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

set(OME_BIOFORMATS_SOURCES
    CoreMetadata.cpp
    FormatException.cpp
    MetadataConfigurable.cpp
    MetadataOptions.cpp
    Modulo.cpp
    UnknownFormatException.cpp
    UnsupportedCompressionException.cpp
    Version.cpp)

set(OME_BIOFORMATS_HEADERS
    CoreMetadata.h
    FileInfo.h
    FormatException.h
    MetadataMap.h
    MetadataConfigurable.h
    MetadataOptions.h
    Modulo.h
    PixelBuffer.h
    UnknownFormatException.h
    UnsupportedCompressionException.h
    Version.h)

set(BIOFORMATS_SOURCES
    ${OME_BIOFORMATS_SOURCES})

set(BIOFORMATS_STATIC_HEADERS
    ${OME_BIOFORMATS_HEADERS})

set(BIOFORMATS_HEADERS
    ${BIOFORMATS_STATIC_HEADERS})

add_library(ome-bioformats SHARED
            ${BIOFORMATS_SOURCES}
            ${BIOFORMATS_HEADERS})
target_link_libraries(ome-bioformats ome-xml)
set_target_properties(ome-bioformats PROPERTIES VERSION ${OME_VERSION_SHORT})

install(TARGETS ome-bioformats LIBRARY
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR})

set(ome_bioformats_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/bioformats")

install(FILES ${OME_BIOFORMATS_HEADERS}
        DESTINATION ${ome_bioformats_includedir})

set(LIBRARY_PREFIX OME_BIOFORMATS)
set(LIBRARY_NAME ome-bioformats)
set(LIBRARY_HEADER ome/bioformats/CoreMetadata.h)
configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateConfig.cmake.in
               ${CMAKE_CURRENT_BINARY_DIR}/ome-bioformats-config.cmake)
install(FILES ${CMAKE_CURRENT_BINARY_DIR}/ome-bioformats-config.cmake
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR}/cmake)

# Dump header list for testing
header_include_list_write(BIOFORMATS_STATIC_HEADERS BIOFORMATS_GENERATED_HEADERS ome/bioformats ${PROJECT_BINARY_DIR}/cpp/test/ome-bioformats)

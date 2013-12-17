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

execute_process(COMMAND ${XSD_FU} metadata --dry-run --file-type=source --print-generated ${XSD_FU_ARGS}
  OUTPUT_VARIABLE OME_BIOFORMATS_META_GENERATED_SOURCES)
string(REPLACE "\n" ";" OME_BIOFORMATS_META_GENERATED_SOURCES "${OME_BIOFORMATS_META_GENERATED_SOURCES}")
if(WIN32)
  string(REPLACE "\\" "/" OME_BIOFORMATS_META_GENERATED_SOURCES "${OME_BIOFORMATS_META_GENERATED_SOURCES}")
endif(WIN32)
execute_process(COMMAND ${XSD_FU} metadata --dry-run --file-type=source --print-depends ${XSD_FU_ARGS}
  OUTPUT_VARIABLE OME_BIOFORMATS_META_GENERATED_SOURCE_DEPENDENCIES)
string(REPLACE "\n" ";" OME_BIOFORMATS_META_GENERATED_SOURCE_DEPENDENCIES "${OME_BIOFORMATS_META_GENERATED_SOURCE_DEPENDENCIES}")
if(WIN32)
  string(REPLACE "\\" "/" OME_BIOFORMATS_META_GENERATED_SOURCE_DEPENDENCIES "${OME_BIOFORMATS_META_GENERATED_SOURCE_DEPENDENCIES}")
endif(WIN32)

add_custom_command(OUTPUT ${OME_BIOFORMATS_META_GENERATED_SOURCES}
                   COMMAND ${XSD_FU} metadata --quiet --file-type=source ${XSD_FU_ARGS}
                   DEPENDS ${OME_BIOFORMATS_META_GENERATED_SOURCE_DEPENDENCIES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${OME_BIOFORMATS_META_GENERATED_SOURCES})

execute_process(COMMAND ${XSD_FU} metadata --dry-run --file-type=header --print-generated ${XSD_FU_ARGS}
  OUTPUT_VARIABLE OME_BIOFORMATS_META_GENERATED_HEADERS)
string(REPLACE "\n" ";" OME_BIOFORMATS_META_GENERATED_HEADERS "${OME_BIOFORMATS_META_GENERATED_HEADERS}")
if(WIN32)
  string(REPLACE "\\" "/" OME_BIOFORMATS_META_GENERATED_HEADERS "${OME_BIOFORMATS_META_GENERATED_HEADERS}")
endif(WIN32)
execute_process(COMMAND ${XSD_FU} metadata --dry-run --file-type=header --print-depends ${XSD_FU_ARGS}
  OUTPUT_VARIABLE OME_BIOFORMATS_META_GENERATED_HEADER_DEPENDENCIES)
string(REPLACE "\n" ";" OME_BIOFORMATS_META_GENERATED_HEADER_DEPENDENCIES "${OME_BIOFORMATS_META_GENERATED_HEADER_DEPENDENCIES}")
if(WIN32)
  string(REPLACE "\\" "/" OME_BIOFORMATS_META_GENERATED_HEADER_DEPENDENCIES "${OME_BIOFORMATS_META_GENERATED_HEADER_DEPENDENCIES}")
endif(WIN32)

add_custom_command(OUTPUT ${OME_BIOFORMATS_META_GENERATED_HEADERS}
                   COMMAND ${XSD_FU} metadata --quiet --file-type=header ${XSD_FU_ARGS}
                   DEPENDS ${OME_BIOFORMATS_META_GENERATED_HEADER_DEPENDENCIES} ${XSD_FU_SCRIPT})
#execute_process(COMMAND ${CMAKE_COMMAND} -E echo Generated ${OME_BIOFORMATS_META_GENERATED_HEADERS})

set(OME_BIOFORMATS_SOURCES
    CoreMetadata.cpp
    FormatException.cpp
    MetadataOptions.cpp
    Modulo.cpp
    UnknownFormatException.cpp
    UnsupportedCompressionException.cpp)

set(OME_BIOFORMATS_HEADERS
    CoreMetadata.h
    FormatException.h
    MetadataOptions.h
    Modulo.h
    UnknownFormatException.h
    UnsupportedCompressionException.h)

set(OME_BIOFORMATS_META_STATIC_SOURCES
    meta/BaseMetadata.cpp
    meta/Metadata.cpp
    meta/MetadataRoot.cpp)

set(OME_BIOFORMATS_META_STATIC_HEADERS
    meta/BaseMetadata.h
    meta/Metadata.h
    meta/MetadataRoot.h)

set(BIOFORMATS_SOURCES
    ${OME_BIOFORMATS_SOURCES}
    ${OME_BIOFORMATS_META_STATIC_SOURCES}
    ${OME_BIOFORMATS_META_GENERATED_SOURCES})

set(BIOFORMATS_HEADERS
    ${OME_BIOFORMATS_HEADERS}
    ${OME_BIOFORMATS_META_STATIC_HEADERS}
    ${OME_BIOFORMATS_META_GENERATED_HEADERS})

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
install(FILES ${OME_BIOFORMATS_META_STATIC_HEADERS} ${OME_BIOFORMATS_META_GENERATED_HEADERS}
        DESTINATION ${ome_bioformats_includedir}/meta)

set(LIBRARY_PREFIX OME_BIOFORMATS)
set(LIBRARY_NAME ome-bioformats)
set(LIBRARY_HEADER ome/bioformats/meta/BaseMetadata.h)
configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateConfig.cmake.in
               ${CMAKE_CURRENT_BINARY_DIR}/ome-bioformats-config.cmake)
install(FILES ${CMAKE_CURRENT_BINARY_DIR}/ome-bioformats-config.cmake
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR}/cmake)

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
                    ${TIFF_INCLUDE_DIR}
                    ${XercesC_INCLUDE_DIRS})

set(OME_BIOFORMATS_SOURCES
    CoreMetadata.cpp
    FormatException.cpp
    FormatTools.cpp
    MetadataConfigurable.cpp
    MetadataOptions.cpp
    MetadataTools.cpp
    Modulo.cpp
    PixelBuffer.cpp
    PixelProperties.cpp
    TileBuffer.cpp
    TileCache.cpp
    TileCoverage.cpp
    UnknownFormatException.cpp
    UnsupportedCompressionException.cpp
    VariantPixelBuffer.cpp
    Version.cpp
    XMLTools.cpp)

set(OME_BIOFORMATS_HEADERS
    CoreMetadata.h
    FileInfo.h
    FormatException.h
    MetadataMap.h
    FormatHandler.h
    FormatReader.h
    FormatTools.h
    FormatWriter.h
    MetadataConfigurable.h
    MetadataOptions.h
    MetadataTools.h
    Modulo.h
    PixelBuffer.h
    PixelProperties.h
    TileBuffer.h
    TileCache.h
    TileCoverage.h
    Types.h
    UnknownFormatException.h
    UnsupportedCompressionException.h
    VariantPixelBuffer.h
    Version.h
    XMLTools.h)

set(OME_BIOFORMATS_DETAIL_SOURCES
    detail/FormatReader.cpp
    detail/FormatWriter.cpp)

set(OME_BIOFORMATS_DETAIL_HEADERS
    detail/FormatReader.h
    detail/FormatWriter.h
    detail/OMETIFF.h)

set(OME_BIOFORMATS_IN_SOURCES
    in/MinimalTIFFReader.cpp
    in/OMETIFFReader.cpp
    in/TIFFReader.cpp)

set(OME_BIOFORMATS_IN_HEADERS
    in/MinimalTIFFReader.h
    in/OMETIFFReader.h
    in/TIFFReader.h)

set(OME_BIOFORMATS_OUT_SOURCES
    out/MinimalTIFFWriter.cpp
    out/OMETIFFWriter.cpp)

set(OME_BIOFORMATS_OUT_HEADERS
    out/MinimalTIFFWriter.h
    out/OMETIFFWriter.h)

set(OME_BIOFORMATS_TIFF_SOURCES
    tiff/Codec.cpp
    tiff/Exception.cpp
    tiff/Field.cpp
    tiff/IFD.cpp
    tiff/ImageJMetadata.cpp
    tiff/Sentry.cpp
    tiff/Tags.cpp
    tiff/TIFF.cpp
    tiff/TileInfo.cpp
    tiff/Util.cpp)

set(OME_BIOFORMATS_TIFF_HEADERS
    tiff/Codec.h
    tiff/Exception.h
    tiff/Field.h
    tiff/IFD.h
    tiff/ImageJMetadata.h
    tiff/Sentry.h
    tiff/Tags.h
    tiff/TIFF.h
    tiff/TileInfo.h
    tiff/Types.h
    tiff/Util.h)

set(BIOFORMATS_SOURCES
    ${OME_BIOFORMATS_SOURCES}
    ${OME_BIOFORMATS_DETAIL_SOURCES}
    ${OME_BIOFORMATS_IN_SOURCES}
    ${OME_BIOFORMATS_OUT_SOURCES}
    ${OME_BIOFORMATS_TIFF_SOURCES})

set(BIOFORMATS_STATIC_HEADERS
    ${OME_BIOFORMATS_HEADERS}
    ${OME_BIOFORMATS_DETAIL_HEADERS}
    ${OME_BIOFORMATS_IN_HEADERS}
    ${OME_BIOFORMATS_OUT_HEADERS}
    ${OME_BIOFORMATS_TIFF_HEADERS})

set(BIOFORMATS_GENERATED_HEADERS)

set(BIOFORMATS_HEADERS
    ${BIOFORMATS_STATIC_HEADERS})

add_library(ome-bioformats
            ${BIOFORMATS_SOURCES}
            ${BIOFORMATS_HEADERS})
target_link_libraries(ome-bioformats ome-compat ome-common ome-xml
                      ${Boost_IOSTREAMS_LIBRARY_RELEASE}
                      ${Boost_FILESYSTEM_LIBRARY_RELEASE}
                      ${Boost_SYSTEM_LIBRARY_RELEASE}
                      ${TIFF_LIBRARIES})
set_target_properties(ome-bioformats PROPERTIES VERSION ${OME_VERSION_SHORT})

install(TARGETS ome-bioformats
        RUNTIME DESTINATION ${CMAKE_INSTALL_FULL_BINDIR}
        LIBRARY DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR}
        ARCHIVE DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR})

set(ome_bioformats_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/bioformats")

install(FILES ${OME_BIOFORMATS_HEADERS}
        DESTINATION ${ome_bioformats_includedir})
install(FILES ${OME_BIOFORMATS_DETAIL_HEADERS}
        DESTINATION ${ome_bioformats_includedir}/detail)
install(FILES ${OME_BIOFORMATS_IN_HEADERS}
        DESTINATION ${ome_bioformats_includedir}/in)
install(FILES ${OME_BIOFORMATS_OUT_HEADERS}
        DESTINATION ${ome_bioformats_includedir}/out)
install(FILES ${OME_BIOFORMATS_TIFF_HEADERS}
        DESTINATION ${ome_bioformats_includedir}/tiff)


set(LIBRARY_PREFIX OME_BIOFORMATS)
set(LIBRARY_NAME ome-bioformats)
set(LIBRARY_HEADER ome/bioformats/CoreMetadata.h)
configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateConfig.cmake.in
               ${CMAKE_CURRENT_BINARY_DIR}/ome-bioformats-config.cmake)
install(FILES ${CMAKE_CURRENT_BINARY_DIR}/ome-bioformats-config.cmake
        DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR}/cmake)

# Dump header list for testing
header_include_list_write(BIOFORMATS_STATIC_HEADERS BIOFORMATS_GENERATED_HEADERS ome/bioformats ${PROJECT_BINARY_DIR}/cpp/test/ome-bioformats)

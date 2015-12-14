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
    PlaneRegion.h
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

target_include_directories(ome-bioformats PUBLIC
                           $<BUILD_INTERFACE:${PROJECT_SOURCE_DIR}/cpp/lib>
                           $<BUILD_INTERFACE:${PROJECT_BINARY_DIR}/cpp/lib>
                           $<BUILD_INTERFACE:${TIFF_INCLUDE_DIR}>)

target_link_libraries(ome-bioformats
                      OME::Compat
                      OME::Common
                      OME::XML
                      Boost::boost
                      Boost::iostreams
                      Boost::filesystem
                      TIFF::TIFF)

set_target_properties(ome-bioformats PROPERTIES VERSION ${OME_VERSION_SHORT})

add_library(OME::BioFormats ALIAS ome-bioformats)

install(TARGETS ome-bioformats
        EXPORT OMEBioFormatsInternal
        RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
        LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
        ARCHIVE DESTINATION ${CMAKE_INSTALL_LIBDIR})
install(EXPORT OMEBioFormatsInternal
        DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/OMEBioFormats)
install(FILES OMEBioFormatsConfig.cmake
        DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/OMEBioFormats)

set(ome_bioformats_includedir "${CMAKE_INSTALL_INCLUDEDIR}/ome/bioformats")

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

# Dump header list for testing
header_include_list_write(BIOFORMATS_STATIC_HEADERS BIOFORMATS_GENERATED_HEADERS ome/bioformats ${PROJECT_BINARY_DIR}/cpp/test/ome-bioformats)

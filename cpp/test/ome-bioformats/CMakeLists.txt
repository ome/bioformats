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
                    ${PNG_INCLUDE_DIRS}
                    ${XercesC_INCLUDE_DIRS})

add_subdirectory(data)

if(BUILD_TESTS)
  if(extended-tests)
    header_test_from_file(ome-bioformats OME::BioFormats ome/bioformats)
    bf_add_test(ome-bioformats/headers ome-bioformats-headers)
  endif(extended-tests)

  add_executable(formatreader formatreader.cpp)
  target_link_libraries(formatreader OME::BioFormats)
  target_link_libraries(formatreader ome-test)

  bf_add_test(ome-bioformats/formatreader formatreader)

  add_executable(formattools formattools.cpp)
  target_link_libraries(formattools OME::BioFormats)
  target_link_libraries(formattools ome-test)

  bf_add_test(ome-bioformats/formattools formattools)

  add_executable(formatwriter formatwriter.cpp)
  target_link_libraries(formatwriter OME::BioFormats)
  target_link_libraries(formatwriter ome-test)

  bf_add_test(ome-bioformats/formatwriter formatwriter)

  add_executable(metadatamap metadatamap.cpp)
  target_link_libraries(metadatamap ome-test)

  bf_add_test(ome-bioformats/metadatamap metadatamap)

  add_executable(metadatatools metadatatools.cpp)
  target_link_libraries(metadatatools OME::BioFormats)
  target_link_libraries(metadatatools ome-test)

  bf_add_test(ome-bioformats/metadatatools metadatatools)

  add_executable(fileinfo fileinfo.cpp)
  target_link_libraries(fileinfo OME::BioFormats)
  target_link_libraries(fileinfo ome-test)

  bf_add_test(ome-bioformats/fileinfo fileinfo)

  add_executable(pixelbuffer
                 pixelbuffer.h
                 pixelbuffer-order.cpp
                 pixelbuffer-int8.cpp
                 pixelbuffer-int16.cpp
                 pixelbuffer-int32.cpp
                 pixelbuffer-uint8.cpp
                 pixelbuffer-uint16.cpp
                 pixelbuffer-uint32.cpp
                 pixelbuffer-float.cpp
                 pixelbuffer-double.cpp
                 pixelbuffer-bit.cpp
                 pixelbuffer-complex.cpp
                 pixelbuffer-doublecomplex.cpp)
  target_link_libraries(pixelbuffer OME::BioFormats)
  target_link_libraries(pixelbuffer ome-test)

  bf_add_test(ome-bioformats/pixelbuffer pixelbuffer)

  add_executable(pixelproperties pixelproperties.cpp)
  target_link_libraries(pixelproperties OME::BioFormats)
  target_link_libraries(pixelproperties ome-test)

  bf_add_test(ome-bioformats/pixelproperties pixelproperties)

  add_executable(planeregion planeregion.cpp)
  target_link_libraries(planeregion OME::BioFormats)
  target_link_libraries(planeregion ome-test)

  bf_add_test(ome-bioformats/planeregion planeregion)

  add_executable(tiff tiff.cpp tiffsamples.cpp)
  target_link_libraries(tiff OME::BioFormats)
  target_link_libraries(tiff ome-test ${PNG_LIBRARIES})
  add_dependencies(tiff gentestimages)

  bf_add_test(ome-bioformats/tiff tiff)

  add_executable(minimaltiffreader minimaltiffreader.cpp)
  target_link_libraries(minimaltiffreader OME::BioFormats)
  target_link_libraries(minimaltiffreader ome-test)

  bf_add_test(ome-bioformats/minimaltiffreader minimaltiffreader)

  add_executable(minimaltiffwriter minimaltiffwriter.cpp tiffsamples.cpp)
  target_link_libraries(minimaltiffwriter OME::BioFormats)
  target_link_libraries(minimaltiffwriter ome-test)

  bf_add_test(ome-bioformats/minimaltiffwriter minimaltiffwriter)

  add_executable(ometiffwriter ometiffwriter.cpp tiffsamples.cpp)
  target_link_libraries(ometiffwriter OME::BioFormats)
  target_link_libraries(ometiffwriter ome-test)

  bf_add_test(ome-bioformats/ometiffwriter ometiffwriter)

  add_executable(tiffreader tiffreader.cpp)
  target_link_libraries(tiffreader OME::BioFormats)
  target_link_libraries(tiffreader ome-test)

  bf_add_test(ome-bioformats/tiffreader tiffreader)

  add_executable(tilebuffer tilebuffer.cpp)
  target_link_libraries(tilebuffer OME::BioFormats)
  target_link_libraries(tilebuffer ome-test)

  bf_add_test(ome-bioformats/tilebuffer tilebuffer)

  add_executable(tilecache tilecache.cpp)
  target_link_libraries(tilecache OME::BioFormats)
  target_link_libraries(tilecache ome-test)

  bf_add_test(ome-bioformats/tilecache tilecache)

  add_executable(tilecoverage tilecoverage.cpp)
  target_link_libraries(tilecoverage OME::BioFormats)
  target_link_libraries(tilecoverage ome-test)

  bf_add_test(ome-bioformats/tilecoverage tilecoverage)

  add_executable(variantpixelbuffer variantpixelbuffer.cpp)
  target_link_libraries(variantpixelbuffer OME::BioFormats)
  target_link_libraries(variantpixelbuffer ome-test)

  bf_add_test(ome-bioformats/variantpixelbuffer variantpixelbuffer)

  add_executable(version version.cpp)
  target_link_libraries(version OME::BioFormats)
  target_link_libraries(version ome-test)

  bf_add_test(ome-bioformats/version version)

  add_executable(xmltools xmltools.cpp)
  target_link_libraries(xmltools OME::BioFormats)
  target_link_libraries(xmltools ome-test)

  bf_add_test(ome-bioformats/xmltools xmltools)

endif(BUILD_TESTS)

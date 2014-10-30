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
                    ${PNG_INCLUDE_DIRS})

add_subdirectory(data)

if(BUILD_TESTS)
  if(extended-tests)
    header_test_from_file(ome-bioformats ome-bioformats ome/bioformats)
    add_test(ome-bioformats/headers ome-bioformats-headers)
  endif(extended-tests)

  add_executable(formatreader formatreader.cpp)
  target_link_libraries(formatreader ome-bioformats)
  target_link_libraries(formatreader ome-test)

  add_test(ome-bioformats/formatreader formatreader)

  add_executable(formattools formattools.cpp)
  target_link_libraries(formattools ome-bioformats)
  target_link_libraries(formattools ome-test)

  add_test(ome-bioformats/formattools formattools)

  add_executable(formatwriter formatwriter.cpp)
  target_link_libraries(formatwriter ome-bioformats)
  target_link_libraries(formatwriter ome-test)

  add_test(ome-bioformats/formatwriter formatwriter)

  add_executable(metadatamap metadatamap.cpp)
  target_link_libraries(metadatamap ome-bioformats)
  target_link_libraries(metadatamap ome-test)

  add_test(ome-bioformats/metadatamap metadatamap)

  add_executable(metadatatools metadatatools.cpp)
  target_link_libraries(metadatatools ome-bioformats)
  target_link_libraries(metadatatools ome-test)

  add_test(ome-bioformats/metadatatools metadatatools)

  add_executable(fileinfo fileinfo.cpp)
  target_link_libraries(fileinfo ome-bioformats)
  target_link_libraries(fileinfo ome-test)

  add_test(ome-bioformats/fileinfo fileinfo)

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
  target_link_libraries(pixelbuffer ome-bioformats)
  target_link_libraries(pixelbuffer ome-test)

  add_test(ome-bioformats/pixelbuffer pixelbuffer)

  add_executable(pixelproperties pixelproperties.cpp)
  target_link_libraries(pixelproperties ome-bioformats)
  target_link_libraries(pixelproperties ome-test)

  add_test(ome-bioformats/pixelproperties pixelproperties)

  add_executable(tiff
                 tiff.cpp
                 tiff-tilecache.cpp
                 tiff-coverage.cpp)
  target_link_libraries(tiff ome-bioformats)
  target_link_libraries(tiff ome-test ${PNG_LIBRARIES})
  add_dependencies(tiff gentestimages)

  add_test(ome-bioformats/tiff tiff)

  add_executable(variantpixelbuffer variantpixelbuffer.cpp)
  target_link_libraries(variantpixelbuffer ome-bioformats)
  target_link_libraries(variantpixelbuffer ome-test)

  add_test(ome-bioformats/variantpixelbuffer variantpixelbuffer)

  add_executable(version version.cpp)
  target_link_libraries(version ome-bioformats)
  target_link_libraries(version ome-test)

  add_test(ome-bioformats/version version)

endif(BUILD_TESTS)

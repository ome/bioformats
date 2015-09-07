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
                    ${XercesC_INCLUDE_DIRS})

if(BUILD_TESTS)
  add_executable(color color.cpp)
  target_link_libraries(color ome-xml)
  target_link_libraries(color ome-test)

  add_executable(enum enum.cpp)
  target_link_libraries(enum ome-xml)
  target_link_libraries(enum ome-test)

  if(UNIX)
    configure_file(${CMAKE_CURRENT_SOURCE_DIR}/scripts/metadata-convert-coverage.cmake.in
                   ${CMAKE_CURRENT_BINARY_DIR}/scripts/metadata-convert-coverage)
    file(COPY ${CMAKE_CURRENT_BINARY_DIR}/scripts/metadata-convert-coverage
         DESTINATION ${CMAKE_CURRENT_BINARY_DIR}
         FILE_PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE GROUP_READ
         GROUP_EXECUTE WORLD_READ WORLD_EXECUTE)
    add_test(ome-xml/metadata-convert-coverage ${CMAKE_CURRENT_BINARY_DIR}/metadata-convert-coverage)
  endif()

  add_executable(constrained-numeric
                 non-negative-float.cpp
                 non-negative-integer.cpp
                 non-negative-long.cpp
                 percent-fraction.cpp
                 positive-float.cpp
                 positive-integer.cpp
                 positive-long.cpp)
  target_link_libraries(constrained-numeric ome-xml)
  target_link_libraries(constrained-numeric ome-test)

  add_executable(timestamp timestamp.cpp)
  target_link_libraries(timestamp ome-xml)
  target_link_libraries(timestamp ome-test)

  add_executable(model model.cpp)
  target_link_libraries(model ome-xml)
  target_link_libraries(model ome-test)

  bf_add_test(ome-xml/color color)
  bf_add_test(ome-xml/enum enum)
  bf_add_test(ome-xml/constrained-numeric constrained-numeric)
  bf_add_test(ome-xml/timestamp timestamp)
  bf_add_test(ome-xml/model model)


  if(extended-tests)
    header_test_from_file(ome-xml ome-xml ome/xml)
    target_link_libraries(ome-xml-headers ome-xml)
    bf_add_test(ome-xml/headers ome-xml-headers)
  endif(extended-tests)
endif(BUILD_TESTS)

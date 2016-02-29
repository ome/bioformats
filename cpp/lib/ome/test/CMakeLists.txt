# #%L
# Bio-Formats C++ libraries (cmake build infrastructure)
# %%
# Copyright © 2006 - 2016 Open Microscopy Environment:
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

if(BUILD_TESTS)
  configure_file(${CMAKE_CURRENT_SOURCE_DIR}/config.h.in
                 ${CMAKE_CURRENT_BINARY_DIR}/config.h)

  set(ome_test_static_headers
      io.h
      test.h)

  set(ome_test_generated_headers
      ${CMAKE_CURRENT_BINARY_DIR}/config.h)

  set(ome_test_sources
      io.cpp
      main.cpp)

  add_library(ome-test STATIC
              ${ome_test_sources}
              ${ome_test_static_headers}
              ${ome_test_generated_headers})

  target_include_directories(ome-test PUBLIC
                             $<BUILD_INTERFACE:${PROJECT_SOURCE_DIR}/cpp/lib>
                             $<BUILD_INTERFACE:${PROJECT_BINARY_DIR}/cpp/lib>)

  target_link_libraries(ome-test OME::Common GTest::GTest ${CMAKE_THREAD_LIBS_INIT})
  set_target_properties(ome-test PROPERTIES LINKER_LANGUAGE CXX)

  add_library(OME::Test ALIAS ome-test)

  # Dump header list for testing
  header_include_list_write(ome_test_static_headers ome_test_generated_headers ome/test ${PROJECT_BINARY_DIR}/cpp/test/ome-test)
endif(BUILD_TESTS)

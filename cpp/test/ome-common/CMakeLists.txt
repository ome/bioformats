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

if(BUILD_TESTS)
  include_directories(${OME_TOPLEVEL_INCLUDES}
                      ${Boost_INCLUDE_DIRS}
                      ${XercesC_INCLUDE_DIRS})

  if(extended-tests)
    header_test_from_file(ome-common ome-common ome/common)
    bf_add_test(ome-common/headers ome-common-headers)
  endif(extended-tests)

  add_executable(boolean boolean.cpp)
  target_link_libraries(boolean ome-common)
  target_link_libraries(boolean ome-test)

  bf_add_test(ome-common/boolean boolean)

  add_executable(endian endian.cpp)
  target_link_libraries(endian ome-common)
  target_link_libraries(endian ome-test)

  bf_add_test(ome-common/endian endian)

  add_executable(filesystem filesystem.cpp)
  target_link_libraries(filesystem ome-common)
  target_link_libraries(filesystem ome-test)

  bf_add_test(ome-common/filesystem filesystem)

  add_executable(module module.cpp)
  target_link_libraries(module ome-common)
  target_link_libraries(module ome-test)

  bf_add_test(ome-common/module module)

  add_executable(mstream mstream.cpp)
  target_link_libraries(mstream ome-common)
  target_link_libraries(mstream ome-test)

  bf_add_test(ome-common/mstream mstream)

  add_executable(thread thread.cpp)
  target_link_libraries(thread ome-common ${THREAD_LIBRARY})
  target_link_libraries(thread ome-test)

  bf_add_test(ome-common/thread thread)

  add_executable(string string.cpp)
  target_link_libraries(string ome-common)
  target_link_libraries(string ome-test)

  bf_add_test(ome-common/string string)

  add_executable(units units.cpp)
  target_link_libraries(units ome-common)
  target_link_libraries(units ome-test ${Boost_IOSTREAMS_LIBRARY_RELEASE})

  bf_add_test(ome-common/units units)

  add_executable(variant variant.cpp)
  target_link_libraries(variant ome-common)
  target_link_libraries(variant ome-test)

  bf_add_test(ome-common/variant variant)

  add_executable(xerces xerces.cpp)
  target_link_libraries(xerces ome-common)
  target_link_libraries(xerces ome-test)

  bf_add_test(ome-common/xerces xerces)

endif(BUILD_TESTS)

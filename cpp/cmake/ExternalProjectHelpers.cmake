## #%L
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

# Compute -G arg for configuring external projects with the same CMake generator:
if(CMAKE_EXTRA_GENERATOR)
  set(BIOFORMATS_EP_GENERATOR "${CMAKE_EXTRA_GENERATOR} - ${CMAKE_GENERATOR}")
else()
  set(BIOFORMATS_EP_GENERATOR "${CMAKE_GENERATOR}")
endif()

set(BIOFORMATS_EP_SOURCE_CACHE "${CMAKE_BINARY_DIR}/sourcecache" CACHE FILEPATH "Directory for cached source downloads")
file(MAKE_DIRECTORY ${BIOFORMATS_EP_SOURCE_CACHE})

set(BIOFORMATS_EP_INSTALL_DIR ${CMAKE_BINARY_DIR}/superbuild-install)

list(APPEND CMAKE_PREFIX_PATH "${BIOFORMATS_EP_INSTALL_DIR}")

string(REPLACE ";" "^^" BIOFORMATS_EP_ESCAPED_CMAKE_PREFIX_PATH "${CMAKE_PREFIX_PATH}")

set(BIOFORMATS_EP_CMAKE_ARGS
  "-DCMAKE_PREFIX_PATH=${BIOFORMATS_EP_ESCAPED_CMAKE_PREFIX_PATH}"
)

# Set CMake OSX variables needed to be passed to external projects
if(APPLE)
  list(APPEND BIOFORMATS_EP_CMAKE_ARGS
    -DCMAKE_OSX_ARCHITECTURES:STRING=${CMAKE_OSX_ARCHITECTURES}
    -DCMAKE_OSX_SYSROOT:PATH=${CMAKE_OSX_SYSROOT}
    -DCMAKE_OSX_DEPLOYMENT_TARGET:STRING=${CMAKE_OSX_DEPLOYMENT_TARGET})
endif()

set(BIOFORMATS_EP_CMAKE_CACHE_ARGS
  "-DCMAKE_INSTALL_PREFIX:PATH="
)

set(BIOFORMATS_EP_COMMON_ARGS
  LIST_SEPARATOR "^^"
  DOWNLOAD_DIR ${BIOFORMATS_EP_SOURCE_CACHE}
  CMAKE_GENERATOR ${BIOFORMATS_EP_GENERATOR}
  CMAKE_ARGS ${BIOFORMATS_EP_CMAKE_ARGS}
  CMAKE_CACHE_ARGS ${BIOFORMATS_EP_CMAKE_CACHE_ARGS}
)
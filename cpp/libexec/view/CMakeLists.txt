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

# Don't use old-style compile definitions
if (POLICY CMP0043)
  cmake_policy(SET CMP0043 NEW)
endif(POLICY CMP0043)

if (OME_QTOPENGL)

  set(CMAKE_AUTOMOC ON)
  set(CMAKE_INCLUDE_CURRENT_DIR ON)

  include_directories(${OME_TOPLEVEL_INCLUDES}
                      ${OME_LIBEXEC_INCLUDES}
                      ${OPENGL_INCLUDE_DIR}
                      ${GLM_INCLUDE_DIR}
                      ${Qt5_INCLUDE_DIRS})

  set(view_SOURCES
      view.cpp
      Window.cpp)

  set(view_HEADERS
      Window.h)

  add_executable(view
                 ${view_SOURCES}
                 ${view_HEADERS}
                 ${view_HEADERS_MOC}
                 ${view_RESOURCES})
  qt5_use_modules(view Core Gui Widgets Svg)
  target_link_libraries(view ome-bioformats ome-qtwidgets)


  install(TARGETS view RUNTIME
          DESTINATION ${OME_BIOFORMATS_INSTALL_FULL_LIBEXECDIR}
          PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE
                      GROUP_READ GROUP_EXECUTE
                      WORLD_READ WORLD_EXECUTE)

endif()

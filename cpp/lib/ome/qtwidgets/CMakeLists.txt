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

if (OME_QTOPENGL)

  set(CMAKE_AUTOMOC ON)
  set(CMAKE_INCLUDE_CURRENT_DIR ON)

  include_directories(${OME_TOPLEVEL_INCLUDES}
                      ${Boost_INCLUDE_DIRS}
                      ${OPENGL_INCLUDE_DIR}
                      ${TIFF_INCLUDE_DIR}
                      ${GLM_INCLUDE_DIR}
                      ${Qt5_INCLUDE_DIRS})

  SET(QTWIDGETS_SOURCES
      GLUtil.cpp
      GLContainer.cpp
      GLWindow.cpp
      GLFlatShader2D.cpp
      GLImageShader2D.cpp
      GLLineShader2D.cpp
      GLView2D.cpp
      Axis2D.cpp
      Grid2D.cpp
      Image2D.cpp
      TexelProperties.cpp)

  SET(QTWIDGETS_HEADERS
      GLUtil.h
      GLContainer.h
      GLWindow.h
      GLFlatShader2D.h
      GLImageShader2D.h
      GLLineShader2D.h
      GLView2D.h
      Axis2D.h
      Grid2D.h
      Image2D.h
      TexelProperties.h)

  add_library(ome-qtwidgets SHARED
              ${QTWIDGETS_SOURCES}
              ${QTWIDGETS_HEADERS}
              ${ome-qtwidgets_HEADERS_MOC}
              ${ome-qtwidgets_RESOURCES})
  qt5_use_modules(ome-qtwidgets Core Gui Widgets Svg)
  target_link_libraries(ome-qtwidgets ome-bioformats
                        ${Boost_FILESYSTEM_LIBRARY_RELEASE}
                        ${Boost_SYSTEM_LIBRARY_RELEASE}
                        ${OPENGL_gl_LIBRARY} ${TIFF_LIBRARIES})

  set_target_properties(ome-qtwidgets PROPERTIES VERSION ${OME_VERSION_SHORT})

  install(TARGETS ome-qtwidgets LIBRARY
          DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR})

  set(ome_qtwidgets_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/qtwidgets")

  install(FILES ${OME_QTWIDGETS_HEADERS}
          DESTINATION ${ome_qtwidgets_includedir})

  set(LIBRARY_PREFIX OME_QTWIDGETS)
  set(LIBRARY_NAME ome-qtwidgets)
  set(LIBRARY_HEADER ome/qtwidgets/CoreMetadata.h)
  configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateConfig.cmake.in
                 ${CMAKE_CURRENT_BINARY_DIR}/ome-qtwidgets-config.cmake)
  install(FILES ${CMAKE_CURRENT_BINARY_DIR}/ome-qtwidgets-config.cmake
          DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR}/cmake)

  # Dump header list for testing
  header_include_list_write(QTWIDGETS_HEADERS ome-qtwidgets_HEADERS_MOC ome/qtwidgets ${PROJECT_BINARY_DIR}/cpp/test/ome-qtwidgets)

else()
  message(WARNING "Not building Qt widgets and glview; Qt5.2, OpenGL or glm not found")
endif()

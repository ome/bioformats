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
                      ${Boost_INCLUDE_DIRS}
                      ${OPENGL_INCLUDE_DIR}
                      ${TIFF_INCLUDE_DIR}
                      ${GLM_INCLUDE_DIR}
                      ${Qt5_INCLUDE_DIRS})

  set(QTWIDGETS_SOURCES
      GLContainer.cpp
      GLWindow.cpp
      GLView2D.cpp
      NavigationDock2D.cpp
      TexelProperties.cpp)

  set(QTWIDGETS_HEADERS
      GLContainer.h
      GLWindow.h
      GLView2D.h
      NavigationDock2D.h
      TexelProperties.h)

  set(QTWIDGETS_GL_SOURCES
      gl/Axis2D.cpp
      gl/Grid2D.cpp
      gl/Image2D.cpp
      gl/Util.cpp)

  set(QTWIDGETS_GL_HEADERS
      gl/Axis2D.h
      gl/Grid2D.h
      gl/Image2D.h
      gl/Util.h)

  set(QTWIDGETS_GL_V20_SOURCES
      gl/v20/V20Axis2D.cpp
      gl/v20/V20Grid2D.cpp
      gl/v20/V20Image2D.cpp)

  set(QTWIDGETS_GL_V20_HEADERS
      gl/v20/V20Axis2D.h
      gl/v20/V20Grid2D.h
      gl/v20/V20Image2D.h)

  set(QTWIDGETS_GLSL_V110_SOURCES
      glsl/v110/GLFlatShader2D.cpp
      glsl/v110/GLImageShader2D.cpp
      glsl/v110/GLLineShader2D.cpp)

  set(QTWIDGETS_GLSL_V110_HEADERS
      glsl/v110/GLFlatShader2D.h
      glsl/v110/GLImageShader2D.h
      glsl/v110/GLLineShader2D.h)

  add_library(ome-qtwidgets
              ${QTWIDGETS_SOURCES}
              ${QTWIDGETS_HEADERS}
              ${QTWIDGETS_GL_SOURCES}
              ${QTWIDGETS_GL_HEADERS}
              ${QTWIDGETS_GL_V20_SOURCES}
              ${QTWIDGETS_GL_V20_HEADERS}
              ${QTWIDGETS_GLSL_V110_SOURCES}
              ${QTWIDGETS_GLSL_V110_HEADERS}
              ${ome-qtwidgets_HEADERS_MOC}
              ${ome-qtwidgets_RESOURCES})
  qt5_use_modules(ome-qtwidgets Core Gui Widgets Svg)
  target_link_libraries(ome-qtwidgets ome-bioformats
                        ${Boost_FILESYSTEM_LIBRARY_RELEASE}
                        ${Boost_SYSTEM_LIBRARY_RELEASE}
                        ${OPENGL_gl_LIBRARY} ${TIFF_LIBRARIES})

  set_target_properties(ome-qtwidgets PROPERTIES VERSION ${OME_VERSION_SHORT})

  install(TARGETS ome-qtwidgets
          RUNTIME DESTINATION ${CMAKE_INSTALL_FULL_BINDIR}
          LIBRARY DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR}
          ARCHIVE DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR})

  set(ome_qtwidgets_includedir "${CMAKE_INSTALL_FULL_INCLUDEDIR}/ome/qtwidgets")

  install(FILES ${OME_QTWIDGETS_HEADERS}
          DESTINATION ${ome_qtwidgets_includedir})
  install(FILES ${OME_QTWIDGETS_GL__HEADERS}
          DESTINATION ${ome_qtwidgets_includedir}/gl)
  install(FILES ${OME_QTWIDGETS_GL_V20_HEADERS}
          DESTINATION ${ome_qtwidgets_includedir}/gl/v20)
  install(FILES ${OME_QTWIDGETS_GLSL_V110_HEADERS}
          DESTINATION ${ome_qtwidgets_includedir}/glsl/v110)

  set(LIBRARY_PREFIX OME_QTWIDGETS)
  set(LIBRARY_NAME ome-qtwidgets)
  set(LIBRARY_HEADER ome/qtwidgets/CoreMetadata.h)
  configure_file(${PROJECT_SOURCE_DIR}/cpp/cmake/TemplateConfig.cmake.in
                 ${CMAKE_CURRENT_BINARY_DIR}/ome-qtwidgets-config.cmake)
  install(FILES ${CMAKE_CURRENT_BINARY_DIR}/ome-qtwidgets-config.cmake
          DESTINATION ${CMAKE_INSTALL_FULL_LIBDIR}/cmake)

  # Dump header list for testing
  header_include_list_write(QTWIDGETS_HEADERS "" ome/qtwidgets ${PROJECT_BINARY_DIR}/cpp/test/ome-qtwidgets)

else()
  message(WARNING "Not building Qt widgets and view program; Qt5.2, OpenGL or glm not found, or explicitly disabled")
endif()

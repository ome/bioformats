/*
 * #%L
 * OME-QTWIDGETS C++ library for display of Bio-Formats pixel data and metadata.
 * %%
 * Copyright © 2014 - 2015 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

#include <ome/qtwidgets/glsl/v110/GLFlatShader2D.h>
#include <ome/qtwidgets/gl/Util.h>

#include <glm/gtc/type_ptr.hpp>

#include <iostream>

using ome::qtwidgets::gl::check_gl;

namespace ome
{
  namespace qtwidgets
  {
    namespace glsl
    {
      namespace v110
      {

        GLFlatShader2D::GLFlatShader2D(QObject *parent):
          QOpenGLShaderProgram(parent),
          vshader(),
          fshader(),
          attr_coords(),
          uniform_colour(),
          uniform_offset(),
          uniform_mvp()
        {
          initializeOpenGLFunctions();

          vshader = new QOpenGLShader(QOpenGLShader::Vertex, this);
          vshader->compileSourceCode
            ("#version 110\n"
             "\n"
             "attribute vec2 coord2d;\n"
             "varying vec4 f_colour;\n"
             "uniform vec4 colour;\n"
             "uniform vec2 offset;\n"
             "uniform mat4 mvp;\n"
             "\n"
             "void main(void) {\n"
             "  gl_Position = mvp * vec4(coord2d+offset, 2.0, 1.0);\n"
             "  f_colour = colour;\n"
             "}\n");
          if (!vshader->isCompiled())
            {
              std::cerr << "Failed to compile vertex shader\n" << vshader->log().toStdString() << std::endl;
            }

          fshader = new QOpenGLShader(QOpenGLShader::Fragment, this);
          fshader->compileSourceCode
            ("#version 110\n"
             "\n"
             "varying vec4 f_colour;\n"
             "\n"
             "void main(void) {\n"
             "  gl_FragColor = f_colour;\n"
             "}\n");
          if (!fshader->isCompiled())
            {
              std::cerr << "Failed to compile fragment shader\n" << fshader->log().toStdString() << std::endl;
            }

          addShader(vshader);
          addShader(fshader);
          link();

          if (!isLinked())
            {
              std::cerr << "Failed to link shader program\n" << log().toStdString() << std::endl;
            }

          attr_coords = attributeLocation("coord2d");
          if (attr_coords == -1)
            std::cerr << "Failed to bind coordinate location" << std::endl;

          uniform_colour = uniformLocation("colour");
          if (uniform_colour == -1)
            std::cerr << "Failed to bind transform" << std::endl;

          uniform_offset = uniformLocation("offset");
          if (uniform_offset == -1)
            std::cerr << "Failed to bind offset" << std::endl;

          uniform_mvp = uniformLocation("mvp");
          if (uniform_mvp == -1)
            std::cerr << "Failed to bind transform" << std::endl;
        }

        GLFlatShader2D::~GLFlatShader2D()
        {
        }

        void
        GLFlatShader2D::enableCoords()
        {
          enableAttributeArray(attr_coords);
        }

        void
        GLFlatShader2D::disableCoords()
        {
          disableAttributeArray(attr_coords);
        }

        void
        GLFlatShader2D::setCoords(const GLfloat *offset, int tupleSize, int stride)
        {
          setAttributeArray(attr_coords, offset, tupleSize, stride);
          check_gl("Set flatcoords");
        }

        void
        GLFlatShader2D::setCoords(QOpenGLBuffer& coords, const GLfloat *offset, int tupleSize, int stride)
        {
          coords.bind();
          setCoords(offset, tupleSize, stride);
          coords.release();
        }

        void
        GLFlatShader2D::setColour(const glm::vec4& colour)
        {
          glUniform4fv(uniform_colour, 1, glm::value_ptr(colour));
          check_gl("Set flat uniform colour");
        }

        void
        GLFlatShader2D::setOffset(const glm::vec2& offset)
        {
          glUniform2fv(uniform_offset, 1, glm::value_ptr(offset));
          check_gl("Set flat uniform offset");
        }

        void
        GLFlatShader2D::setModelViewProjection(const glm::mat4& mvp)
        {
          glUniformMatrix4fv(uniform_mvp, 1, GL_FALSE, glm::value_ptr(mvp));
          check_gl("Set flat uniform mvp");
        }

      }
    }
  }
}

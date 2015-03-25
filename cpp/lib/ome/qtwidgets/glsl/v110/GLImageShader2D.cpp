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

#include <ome/qtwidgets/glsl/v110/GLImageShader2D.h>
#include <ome/qtwidgets/gl/Util.h>

#define GLM_FORCE_RADIANS
#include <glm/gtc/type_ptr.hpp>

#include <iostream>
#include <sstream>

using ome::qtwidgets::gl::check_gl;

namespace ome
{
  namespace qtwidgets
  {
    namespace glsl
    {
      namespace v110
      {

        GLImageShader2D::GLImageShader2D(QObject *parent):
          QOpenGLShaderProgram(parent),
          vshader(),
          fshader(),
          attr_coords(),
          attr_texcoords(),
          uniform_mvp(),
          uniform_texture(),
          uniform_lut(),
          uniform_min(),
          uniform_max()
        {
          initializeOpenGLFunctions();

          vshader = new QOpenGLShader(QOpenGLShader::Vertex, this);

          std::string vsource("#version 110\n"
                              "\n"
                              "attribute vec2 coord2d;\n"
                              "attribute vec2 texcoord;\n"
                              "varying vec2 f_texcoord;\n"
                              "uniform mat4 mvp;\n"
                              "\n"
                              "void main(void) {\n"
                              "  gl_Position = mvp * vec4(coord2d, 0.0, 1.0);\n"
                              "  f_texcoord = texcoord;\n"
                              "}\n");

          vshader->compileSourceCode(vsource.c_str());
          if (!vshader->isCompiled())
            {
              std::cerr << "Failed to compile vertex shader\n" << vshader->log().toStdString() << std::endl;
            }

          fshader = new QOpenGLShader(QOpenGLShader::Fragment, this);
          std::string fsource("#version 110\n"
                              "#extension GL_EXT_texture_array : enable\n"
                              "\n"
                              "varying vec2 f_texcoord;\n"
                              "uniform sampler2D tex;\n"
                              "uniform sampler1DArray lut;\n"
                              "uniform vec3 texmin;\n"
                              "uniform vec3 texmax;\n"
                              "uniform vec3 correction;\n"
                              "\n"
                              "void main(void) {\n"
                              "  vec2 flipped_texcoord = vec2(f_texcoord.x, 1.0 - f_texcoord.y);\n"
                              "  vec4 texval = texture2D(tex, flipped_texcoord);\n"
                              "\n"
                              "  gl_FragColor = texture1DArray(lut, vec2(((((texval[0] * correction[0]) - texmin[0]) / (texmax[0] - texmin[0]))), 0.0));\n"
                              "}\n");

          fshader->compileSourceCode(fsource.c_str());
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
            std::cerr << "Failed to bind coordinates" << std::endl;

          attr_texcoords = attributeLocation("texcoord");
          if (attr_texcoords == -1)
            std::cerr << "Failed to bind texture coordinates" << std::endl;

          uniform_mvp = uniformLocation("mvp");
          if (uniform_mvp == -1)
            std::cerr << "Failed to bind transform" << std::endl;

          uniform_texture = uniformLocation("tex");
          if (uniform_texture == -1)
            std::cerr << "Failed to bind texture uniform " << std::endl;

          uniform_lut = uniformLocation("lut");
          if (uniform_lut == -1)
            std::cerr << "Failed to bind lut uniform " << std::endl;

          uniform_min = uniformLocation("texmin");
          if (uniform_min == -1)
            std::cerr << "Failed to bind min uniform " << std::endl;

          uniform_max = uniformLocation("texmax");
          if (uniform_max == -1)
            std::cerr << "Failed to bind max uniform " << std::endl;

          uniform_corr = uniformLocation("correction");
          if (uniform_corr == -1)
            std::cerr << "Failed to bind correction uniform " << std::endl;
        }

        GLImageShader2D::~GLImageShader2D()
        {
        }

        void
        GLImageShader2D::enableCoords()
        {
          enableAttributeArray(attr_coords);
        }

        void
        GLImageShader2D::disableCoords()
        {
          disableAttributeArray(attr_coords);
        }

        void
        GLImageShader2D::setCoords(const GLfloat *offset, int tupleSize, int stride)
        {
          setAttributeArray(attr_coords, offset, tupleSize, stride);
        }

        void
        GLImageShader2D::setCoords(QOpenGLBuffer& coords, const GLfloat *offset, int tupleSize, int stride)
        {
          coords.bind();
          setCoords(offset, tupleSize, stride);
          coords.release();
        }

        void
        GLImageShader2D::enableTexCoords()
        {
          enableAttributeArray(attr_texcoords);
        }

        void
        GLImageShader2D::disableTexCoords()
        {
          disableAttributeArray(attr_texcoords);
        }

        void
        GLImageShader2D::setTexCoords(const GLfloat *offset,
                                      int            tupleSize,
                                      int            stride)
        {
          setAttributeArray(attr_texcoords, offset, tupleSize, stride);
        }

        void
        GLImageShader2D::setTexCoords(QOpenGLBuffer&  texcoords,
                                      const GLfloat  *offset,
                                      int             tupleSize,
                                      int             stride)
        {
          texcoords.bind();
          setTexCoords(offset, tupleSize, stride);
          texcoords.release();
        }

        void
        GLImageShader2D::setTexture(int texunit)
        {
          glUniform1i(uniform_texture, texunit);
          check_gl("Set image texture");
        }

        void
        GLImageShader2D::setMin(const glm::vec3& min)
        {
          glUniform3fv(uniform_min, 1, glm::value_ptr(min));
          check_gl("Set min range");
        }

        void
        GLImageShader2D::setMax(const glm::vec3& max)
        {
          glUniform3fv(uniform_max, 1, glm::value_ptr(max));
          check_gl("Set max range");
        }

        void
        GLImageShader2D::setCorrection(const glm::vec3& corr)
        {
          glUniform3fv(uniform_corr, 1, glm::value_ptr(corr));
          check_gl("Set correction multiplier");
        }

        void
        GLImageShader2D::setLUT(int texunit)
        {
          glUniform1i(uniform_lut, texunit);
          check_gl("Set LUT texture");
        }

        void
        GLImageShader2D::setModelViewProjection(const glm::mat4& mvp)
        {
          glUniformMatrix4fv(uniform_mvp, 1, GL_FALSE, glm::value_ptr(mvp));
          check_gl("Set image2d uniform mvp");
        }

      }
    }
  }
}

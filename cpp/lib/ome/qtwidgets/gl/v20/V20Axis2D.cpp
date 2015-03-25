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

#include <ome/qtwidgets/gl/v20/V20Axis2D.h>
#include <ome/qtwidgets/gl/Util.h>

#include <iostream>

namespace ome
{
  namespace qtwidgets
  {
    namespace gl
    {
      namespace v20
      {

        Axis2D::Axis2D(ome::compat::shared_ptr<ome::bioformats::FormatReader>  reader,
                       ome::bioformats::dimension_size_type                    series,
                       QObject                                                *parent):
          gl::Axis2D(reader, series, parent),
          axis_shader(new glsl::v110::GLFlatShader2D(this))
        {
        }

        Axis2D::~Axis2D()
        {
        }

        void
        Axis2D::render(const glm::mat4& mvp)
        {
          axis_shader->bind();

          // Render x axis
          axis_shader->setModelViewProjection(mvp);
          axis_shader->setColour(glm::vec4(1.0, 0.0, 0.0, 1.0));
          axis_shader->setOffset(glm::vec2(0.0, -40.0));
          axis_shader->enableCoords();
          axis_shader->setCoords(xaxis_vertices, 0, 2, 0);

          // Push each element to the vertex shader
          axis_elements.bind();
          glDrawElements(GL_TRIANGLES, axis_elements.size()/sizeof(GLushort), GL_UNSIGNED_SHORT, 0);
          check_gl("Axis X draw elements");

          axis_shader->disableCoords();

          // Render y axis
          axis_shader->bind();
          axis_shader->setModelViewProjection(mvp);
          axis_shader->setColour(glm::vec4(0.0, 1.0, 0.0, 1.0));
          axis_shader->setOffset(glm::vec2(-40.0, 0.0));
          axis_shader->enableCoords();
          axis_shader->setCoords(yaxis_vertices, 0, 2, 0);

          // Push each element to the vertex shader
          axis_elements.bind();
          glDrawElements(GL_TRIANGLES, axis_elements.size()/sizeof(GLushort), GL_UNSIGNED_SHORT, 0);
          check_gl("Axis Y draw elements");

          axis_shader->disableCoords();

          axis_shader->release();
        }

      }
    }
  }
}

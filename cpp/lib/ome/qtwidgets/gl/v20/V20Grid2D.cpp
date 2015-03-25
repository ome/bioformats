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

#include <ome/qtwidgets/gl/v20/V20Grid2D.h>
#include <ome/qtwidgets/gl/Util.h>

#include <cmath>

namespace ome
{
  namespace qtwidgets
  {
    namespace gl
    {
      namespace v20
      {

        Grid2D::Grid2D(ome::compat::shared_ptr<ome::bioformats::FormatReader>  reader,
                       ome::bioformats::dimension_size_type                    series,
                       QObject                                                *parent):
          gl::Grid2D(reader, series, parent),
          grid_shader(new glsl::v110::GLLineShader2D(this))
        {
        }

        Grid2D::~Grid2D()
        {
        }

        void
        Grid2D::render(const glm::mat4& mvp,
                       float zoom)
        {
          grid_shader->bind();

          // Render grid
          grid_shader->setModelViewProjection(mvp);
          grid_shader->setZoom(zoom);

          grid_shader->enableCoords();
          grid_shader->setCoords(grid_vertices, 0, 3, 6 * sizeof(GLfloat));

          grid_shader->enableColour();
          grid_shader->setColour(grid_vertices, reinterpret_cast<const GLfloat *>(0)+3, 3, 6 * sizeof(GLfloat));

          // Push each element to the vertex shader
          grid_elements.bind();
          glDrawElements(GL_LINES, grid_elements.size()/sizeof(GLushort), GL_UNSIGNED_SHORT, 0);
          check_gl("Grid draw elements");

          grid_shader->disableColour();
          grid_shader->disableCoords();

          grid_shader->release();
        }

      }
    }
  }
}

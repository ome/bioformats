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

#include <ome/qtwidgets/gl/v20/V20Image2D.h>
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

        Image2D::Image2D(ome::compat::shared_ptr<ome::bioformats::FormatReader>  reader,
                         ome::bioformats::dimension_size_type                    series,
                         QObject                                                *parent):
          gl::Image2D(reader, series, parent),
          image_shader(new glsl::v110::GLImageShader2D(this))
        {
        }

        Image2D::~Image2D()
        {
        }

        void
        Image2D::render(const glm::mat4& mvp)
        {
          image_shader->bind();

          image_shader->setMin(texmin);
          image_shader->setMax(texmax);
          image_shader->setCorrection(texcorr);
          image_shader->setModelViewProjection(mvp);

          glActiveTexture(GL_TEXTURE0);
          check_gl("Activate texture");
          glBindTexture(GL_TEXTURE_2D, textureid);
          check_gl("Bind texture");
          image_shader->setTexture(0);

          glActiveTexture(GL_TEXTURE1);
          check_gl("Activate texture");
          glBindTexture(GL_TEXTURE_1D_ARRAY, lutid);
          check_gl("Bind texture");
          image_shader->setLUT(1);

          image_shader->enableCoords();
          image_shader->setCoords(image_vertices, 0, 2);

          image_shader->enableTexCoords();
          image_shader->setTexCoords(image_texcoords, 0, 2);

          // Push each element to the vertex shader
          image_elements.bind();
          glDrawElements(GL_TRIANGLES, image_elements.size()/sizeof(GLushort), GL_UNSIGNED_SHORT, 0);
          check_gl("Image2D draw elements");

          image_shader->disableCoords();
          image_shader->disableTexCoords();
          image_shader->release();
        }

      }
    }
  }
}

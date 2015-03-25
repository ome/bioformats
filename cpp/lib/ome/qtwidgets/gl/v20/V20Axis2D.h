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

#ifndef OME_QTWIDGETS_GL_V20_V20AXIS2D_H
#define OME_QTWIDGETS_GL_V20_V20AXIS2D_H

#include <QtCore/QObject>
#include <QtGui/QOpenGLBuffer>
#include <QtGui/QOpenGLShader>
#include <QtGui/QOpenGLFunctions>

#include <ome/bioformats/Types.h>
#include <ome/bioformats/FormatReader.h>

#include <ome/qtwidgets/gl/Axis2D.h>
#include <ome/qtwidgets/glsl/v110/GLFlatShader2D.h>

namespace ome
{
  namespace qtwidgets
  {
    namespace gl
    {
      namespace v20
      {

        /**
         * 2D (xy) axis renderer.
         *
         * Draws x and y axes for the specified image.
         */
        class Axis2D : public gl::Axis2D
        {
          Q_OBJECT

        public:
          /**
           * Create a 2D axis.
           *
           * The size and position will be taken from the specified image.
           *
           * @param reader the image reader.
           * @param series the image series.
           * @param parent the parent of this object.
           */
          explicit Axis2D(ome::compat::shared_ptr<ome::bioformats::FormatReader>  reader,
                          ome::bioformats::dimension_size_type                    series,
                          QObject                                                *parent = 0);

          /// Destructor.
          ~Axis2D();

          /**
           * Render the axis.
           *
           * @param mvp the model view projection matrix.
           */
          void
          render(const glm::mat4& mvp);

        private:
          /// The shader program for axis rendering.
          glsl::v110::GLFlatShader2D *axis_shader;
        };

      }
    }
  }
}

#endif // OME_QTWIDGETS_GL_V20_V20AXIS2D_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

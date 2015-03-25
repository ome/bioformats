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

#ifndef OME_QTWIDGETS_GLSL_V110_GLLINESHADER2D_H
#define OME_QTWIDGETS_GLSL_V110_GLLINESHADER2D_H

#include <QOpenGLShader>
#include <QOpenGLBuffer>
#include <QtGui/QOpenGLFunctions>

#include <glm/glm.hpp>

namespace ome
{
  namespace qtwidgets
  {
    namespace glsl
    {
      namespace v110
      {

        /**
         * 2D line shader program.
         */
        class GLLineShader2D : public QOpenGLShaderProgram, protected QOpenGLFunctions
        {
          Q_OBJECT

        public:
          /**
           * Constructor.
           *
           * @param parent the parent of this object.
           */
          explicit GLLineShader2D(QObject *parent = 0);

          /// Destructor.
          ~GLLineShader2D();

          /// @copydoc GLImageShader2D::enableCoords()
          void
          enableCoords();

          /// @copydoc GLImageShader2D::enableCoords()
          void
          disableCoords();

          /// @copydoc GLImageShader2D::setCoords(const GLfloat*, int, int)
          void
          setCoords(const GLfloat *offset,
                    int            tupleSize,
                    int            stride = 0);

          /// @copydoc GLImageShader2D::setCoords(QOpenGLBuffer&, const GLfloat *, int, int)
          void
          setCoords(QOpenGLBuffer&  coords,
                    const GLfloat  *offset,
                    int             tupleSize,
                    int             stride = 0);

          /// Enable colour array.
          void
          enableColour();

          /// Disable colour array.
          void
          disableColour();

          /**
           * Set colours from array.
           *
           * @param offset data offset if using a buffer object otherwise
           * the colour values.
           * @param tupleSize the tuple size of the data.
           * @param stride the stride of the data.
           */
          void
          setColour(const GLfloat *offset,
                    int            tupleSize,
                    int            stride = 0);

          /**
           * Set colours from buffer object.
           *
           * @param colours the colour values; null if using a buffer
           * object.
           * @param offset the offset into the colours buffer.
           * @param tupleSize the tuple size of the data.
           * @param stride the stride of the data.
           */
          void
          setColour(QOpenGLBuffer&  colours,
                    const GLfloat  *offset,
                    int             tupleSize,
                    int             stride = 0);

          /// @copydoc GLImageShader2D::setModelViewProjection(const glm::mat4& mvp)
          void
          setModelViewProjection(const glm::mat4& mvp);

          /**
           * Set zoom level.
           *
           * @param zoom the zoom level.
           */
          void
          setZoom(float zoom);

        private:
          /// @copydoc GLImageShader2D::vshader
          QOpenGLShader *vshader;
          /// @copydoc GLImageShader2D::fshader
          QOpenGLShader *fshader;

          /// @copydoc GLImageShader2D::attr_coords
          int attr_coords;
          /// Vertex colour attribute
          int attr_colour;
          /// @copydoc GLImageShader2D::uniform_mvp
          int uniform_mvp;
          /// Zoom uniform.
          int uniform_zoom;
        };

      }
    }
  }
}

#endif // OME_QTWIDGETS_GLSL_V110_GLLINESHADER2D_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

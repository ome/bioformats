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

#ifndef OME_QTWIDGETS_GLSL_V110_GLIMAGESHADER2D_H
#define OME_QTWIDGETS_GLSL_V110_GLIMAGESHADER2D_H

#include <QOpenGLShader>
#include <QOpenGLBuffer>
#include <QtGui/QOpenGLFunctions>

#include <glm/glm.hpp>

#include <ome/bioformats/Types.h>

namespace ome
{
  namespace qtwidgets
  {
    /// OpenGL Shader Language (GLSL) program wrappers.
    namespace glsl
    {
      /// GLSL v1.10 compatible.
      namespace v110
      {

        /**
         * 2D image shader program (simple, up to three channels).
         */
        class GLImageShader2D : public QOpenGLShaderProgram,
                                protected QOpenGLFunctions
        {
          Q_OBJECT

        public:
          /**
           * Constructor.
           *
           * @param parent the parent of this object.
           */
          explicit GLImageShader2D(QObject *parent = 0);

          /// Destructor.
          ~GLImageShader2D();

          /// Enable vertex coordinates.
          void
          enableCoords();

          /// Disable vertex coordinates.
          void
          disableCoords();

          /**
           * Set vertex coordinates from array.
           *
           * @param offset data offset if using a buffer object otherwise
           * the coordinate values.
           * @param tupleSize the tuple size of the data.
           * @param stride the stride of the data.
           */
          void
          setCoords(const GLfloat *offset = 0,
                    int            tupleSize = 2,
                    int            stride = 0);

          /**
           * Set vertex coordinates from buffer object.
           *
           * @param coords the coordinate values; null if using a buffer object.
           * @param offset the offset into the coords buffer.
           * @param tupleSize the tuple size of the data.
           * @param stride the stride of the data.
           */
          void
          setCoords(QOpenGLBuffer&  coords,
                    const GLfloat  *offset = 0,
                    int             tupleSize = 2,
                    int             stride = 0);

          /// Enable texture coordinates.
          void
          enableTexCoords();

          /// Disable texture coordinates.
          void
          disableTexCoords();

          /**
           * Set texture coordinates from array.
           *
           * @param offset data offset if using a buffer object otherwise
           * the coordinate values.
           * @param tupleSize the tuple size of the data.
           * @param stride the stride of the data.
           */
          void
          setTexCoords(const GLfloat *offset = 0,
                       int            tupleSize = 2,
                       int            stride = 0);

          /**
           * Set texture coordinates from buffer object.
           *
           * @param coords the coordinate values; null if using a buffer
           * object.
           * @param offset the offset into the coords buffer.
           * @param tupleSize the tuple size of the data.
           * @param stride the stride of the data.
           */
          void
          setTexCoords(QOpenGLBuffer&  coords,
                       const GLfloat  *offset = 0,
                       int             tupleSize = 2,
                       int             stride = 0);

          /**
           * Set the texture to render.
           *
           * @param texunit the texture unit to use.
           */
          void
          setTexture(int texunit);

          /**
           * Set minimum limits for linear contrast.
           *
           * @param min the RGB channel limits.
           */
          void
          setMin(const glm::vec3& min);

          /**
           * Set maximum limits for linear contrast.
           *
           * @param max the RGB channel limits.
           */
          void
          setMax(const glm::vec3& max);

          /**
           * Set correction multipliers to normalise pixel intensity.
           *
           * Use to correct the pixel value limits to the storage size
           * limits, for example when using data with 12 bits per
           * sample with a 16-bit storage type it will require
           * multiplying by 2^(16-12) = 2^4 = 16.  To leave
           * uncorrected, e.g. for float and complex types, and
           * integer types where the bits per sample is the same as
           * the storage size, set to 1.0.
           *
           * @param corr the RGB channel correction multipliers.
           */
          void
          setCorrection(const glm::vec3& corr);
          
          /**
           * Set the LUT to use.
           *
           * @param texunit the texture unit to use.
           */
          void
          setLUT(int texunit);

          /**
           * Set model view projection matrix.
           *
           * @param mvp the model view projection matrix.
           */
          void
          setModelViewProjection(const glm::mat4& mvp);

        private:
          /// The vertex shader.
          QOpenGLShader *vshader;
          /// The fragment shader.
          QOpenGLShader *fshader;

          /// Vertex coordinates attribute.
          int attr_coords;
          /// Texture coordinates attribute.
          int attr_texcoords;
          /// Model view projection uniform.
          int uniform_mvp;
          /// Texture uniform.
          int uniform_texture;
          /// LUT uniform.
          int uniform_lut;
          /// Minimum limits for linear contrast uniform.
          int uniform_min;
          /// Maximum limits for linear contrast uniform.
          int uniform_max;
          /// Correction multiplier for linear contrast uniform.
          int uniform_corr;
        };

      }
    }
  }
}

#endif // OME_QTWIDGETS_GLSL_V110_GLIMAGESHADER2D_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

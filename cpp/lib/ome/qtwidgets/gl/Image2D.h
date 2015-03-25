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

#ifndef OME_QTWIDGETS_GL_IMAGE2D_H
#define OME_QTWIDGETS_GL_IMAGE2D_H

#include <glm/glm.hpp>

#include <QtCore/QObject>
#include <QtGui/QOpenGLBuffer>
#include <QtGui/QOpenGLShader>
#include <QtGui/QOpenGLFunctions>

#include <ome/bioformats/Types.h>
#include <ome/bioformats/FormatReader.h>

#include <ome/compat/memory.h>

namespace ome
{
  namespace qtwidgets
  {
    namespace gl
    {

      /**
       * 2D (xy) image renderer.
       *
       * Draws the specified image, using a user-selectable plane.
       *
       * The render is greyscale with a per-channel min/max for linear
       * contrast.
       */
      class Image2D : public QObject,
                      protected QOpenGLFunctions
      {
        Q_OBJECT

      public:
        /**
         * Create a 2D image.
         *
         * The size and position will be taken from the specified image.
         *
         * @param reader the image reader.
         * @param series the image series.
         * @param parent the parent of this object.
         */
        explicit
        Image2D(ome::compat::shared_ptr<ome::bioformats::FormatReader>  reader,
                ome::bioformats::dimension_size_type                    series,
                QObject                                                *parent = 0);

        /// Destructor.
        virtual
        ~Image2D() = 0;

        /**
         * Create GL buffers.
         *
         * @note Requires a valid GL context.  Must be called before
         * rendering.
         */
        virtual
        void
        create();

      protected:
        /**
         * Set the size of the x and y dimensions.
         *
         * @param xlim the x axis limits (range).
         * @param ylim the y axis limits (range).
         */
        virtual
        void
        setSize(const glm::vec2& xlim,
                const glm::vec2& ylim);

      public:
        /**
         * Set the plane to render.
         *
         * @param plane the plane number.
         */
        void
        setPlane(ome::bioformats::dimension_size_type plane);

        /**
         * Get minimum limit for linear contrast.
         *
         * @returns the limits for three channels.
         */
        const glm::vec3&
        getMin() const;

        /**
         * Set minimum limit for linear contrast.
         *
         * Note that depending upon the image type, not all channels may
         * be used.
         *
         * @param min the limits for three channels.
         */
        void
        setMin(const glm::vec3& min);

        /**
         * Get maximum limit for linear contrast.
         *
         * @returns the limits for three channels.
         */
        const glm::vec3&
        getMax() const;

        /**
         * Set maximum limit for linear contrast.
         *
         * Note that depending upon the image type, not all channels may
         * be used.
         *
         * @param max the limits for three channels.
         */
        void
        setMax(const glm::vec3& max);

        /**
         * Range of min/max adjustment for linear contrast.
         */
        enum RangePolicy
          {
            StorageRange, ///< Range of storage type.
            BPPRange,     ///< Range of pixel type and bits per pixel.
            PlaneRange,   ///< Range of samples on the current plane.
            ImageRange    ///< Range of samples in the current image.
          };

        /**
         * Render the image.
         *
         * @param mvp the model view projection matrix.
         */
        virtual
        void
        render(const glm::mat4& mvp) = 0;

        /**
         * Get texture ID.
         *
         * This is the identifier of the texture for the plane being
         * rendered.
         *
         * @returns the texture ID.
         */
        unsigned int
        texture();

        /**
         * Get LUT ID.
         *
         * This is the identifier of the LUT for the plane being
         * rendered.
         *
         * @returns the LUT ID.
         */
        unsigned int
        lut();

      protected:
        /// The image vertices.
        QOpenGLBuffer image_vertices;
        /// The image texture coordinates.
        QOpenGLBuffer image_texcoords;
        /// The image elements.
        QOpenGLBuffer image_elements;
        /// The identifier of the texture owned and used by this object.
        unsigned int textureid;
        /// The identifier of the LUTs owned and used by this object.
        unsigned int lutid;
        /// Linear contrast minimum limits.
        glm::vec3 texmin;
        /// Linear contrast maximum limits.
        glm::vec3 texmax;
        /// Linear contrast correction multipliers.
        glm::vec3 texcorr;
        /// The image reader.
        ome::compat::shared_ptr<ome::bioformats::FormatReader> reader;
        /// The image series.
        ome::bioformats::dimension_size_type series;
        /// The current image plane.
        ome::bioformats::dimension_size_type plane;
      };

    }
  }
}

#endif // OME_QTWIDGETS_GL_IMAGE2D_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

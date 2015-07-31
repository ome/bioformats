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

#ifndef OME_QTWIDGETS_GLVIEW2D_H
#define OME_QTWIDGETS_GLVIEW2D_H

#include <ome/bioformats/FormatReader.h>

#include <ome/compat/memory.h>

#include <ome/qtwidgets/GLWindow.h>
#include <ome/qtwidgets/gl/Image2D.h>
#include <ome/qtwidgets/gl/Grid2D.h>
#include <ome/qtwidgets/gl/Axis2D.h>

#include <QElapsedTimer>

#include <glm/glm.hpp>

namespace ome
{
  /// Qt5 widgets for image display with OpenGL.
  namespace qtwidgets
  {

    /**
     * 2D GL view of an image with axes and gridlines.
     */
    class GLView2D : public GLWindow
    {
      Q_OBJECT

    public:
      /// Mouse behaviour.
      enum MouseMode
        {
          MODE_ZOOM,  ///< Zoom in and out.
          MODE_PAN,   ///< Pan in x and y.
          MODE_ROTATE ///< Rotate around point in z.
        };

      /**
       * Create a 2D image view.
       *
       * The size and position will be taken from the specified image.
       *
       * @param reader the image reader.
       * @param series the image series.
       * @param parent the parent of this object.
       */
      GLView2D(ome::compat::shared_ptr<ome::bioformats::FormatReader>  reader,
               ome::bioformats::dimension_size_type                    series,
               QWidget                                                *parent = 0);

      /// Destructor.
      ~GLView2D();

      /**
       * Get window minimum size hint.
       *
       * @returns the size hint.
       */
      QSize minimumSizeHint() const;

      /**
       * Get window size hint.
       *
       * @returns the size hint.
       */
      QSize sizeHint() const;

    public slots:
      /**
       * Set zoom factor.
       *
       * @param zoom the zoom factor (pixel drag distance).
       */
      void
      setZoom(int zoom);

      /**
       * Set x translation factor.
       *
       * @param xtran x translation factor (pixels).
       */
      void
      setXTranslation(int xtran);

      /**
       * Set y translation factor.
       *
       * @param ytran y translation factor (pixels).
       */
      void
      setYTranslation(int ytran);

      /**
       * Set z rotation factor.
       *
       * @param angle z rotation factor (pixel drag distance).
       */
      void
      setZRotation(int angle);

      /**
       * Set minimum value for linear contrast (all channels).
       *
       * @param min the minimum value (scaled normalized).
       */
      void
      setChannelMin(int min);

      /**
       * Set maximum value for linear contrast (all channels).
       *
       * @param max the maximum value (scaled normalized).
       */
      void
      setChannelMax(int max);

      /**
       * Set plane to render.
       *
       * @param plane the plane number to render.
       */
      void
      setPlane(ome::bioformats::dimension_size_type plane);

      /**
       * Set mouse behaviour mode.
       *
       * @param mode the behaviour mode to set.
       */
      void
      setMouseMode(MouseMode mode);

    public:
      /**
       * Get reader.
       *
       * @returns the reader.
       */
      ome::compat::shared_ptr<ome::bioformats::FormatReader>
      getReader();

      /**
       * Get series.
       *
       * @returns the series.
       */
      ome::bioformats::dimension_size_type
      getSeries();

      /**
       * Get zoom factor.
       *
       * @returns the zoom factor.
       */
      int
      getZoom() const;

      /**
       * Get x translation factor.
       *
       * @returns the x translation factor.
       */
      int
      getXTranslation() const;

      /**
       * Get y translation factor.
       *
       * @returns the y translation factor.
       */
      int
      getYTranslation() const;

      /**
       * Get z rotation factor.
       *
       * @returns the z rotation factor.
       */
      int
      getZRotation() const;

      /**
       * Get minimum value for linear contrast (all channels).
       *
       * @returns the minimum value.
       */
      int
      getChannelMin() const;

      /**
       * Get maximum value for linear contrast (all channels).
       *
       * @returns the maximum value.
       */
      int
      getChannelMax() const;

      /**
       * Get plane to render.
       *
       * @returns the plane number to render.
       */
      ome::bioformats::dimension_size_type
      getPlane() const;

      /**
       * Get mouse behaviour mode.
       *
       * @returns the behaviour mode.
       */
      MouseMode
      getMouseMode() const;

    signals:
      /**
       * Signal zoom level changed.
       *
       * @param zoom the new zoom level.
       */
      void
      zoomChanged(int zoom);

      /**
       * Signal x translation changed.
       *
       * @param xtran the new x translation.
       */
      void
      xTranslationChanged(int xtran);

      /**
       * Signal y translation changed.
       *
       * @param ytran the new y translation.
       */
      void
      yTranslationChanged(int ytran);

      /**
       * Signal z rotation changed.
       *
       * @param angle the new z rotation.
       */
      void
      zRotationChanged(int angle);

      /**
       * Signal minimum value for linear contrast changed.
       *
       * @param min the new minimum value.
       */
      void
      channelMinChanged(int min);

      /**
       * Signal maximum value for linear contrast changed.
       *
       * @param max the new maximum value.
       */
      void
      channelMaxChanged(int max);

      /**
       * Signal current plane changed.
       *
       * @param plane the new plane.
       */
      void
      planeChanged(ome::bioformats::dimension_size_type plane);

    protected:
      /// Set up GL context and subsidiary objects.
      void
      initialize();

      using GLWindow::render;

      /// Render the scene with the current view settings.
      void
      render();

      /// Resize the view.
      void
      resize();

      /**
       * Handle mouse button press events.
       *
       * Action depends upon the mouse behaviour mode.
       *
       * @param event the event to handle.
       */
      void
      mousePressEvent(QMouseEvent *event);

      /**
       * Handle mouse button movement events.
       *
       * Action depends upon the mouse behaviour mode.
       *
       * @param event the event to handle.
       */
      void
      mouseMoveEvent(QMouseEvent *event);

      /**
       * Handle timer events.
       *
       * Used to update scene properties and trigger a render pass.
       *
       * @param event the event to handle.
       */
      void
      timerEvent (QTimerEvent *event);

    private:
      /**
       * Camera (modelview projection matrix manipulation)
       */
      struct Camera
      {
        /// Projection type
        enum ProjectionType
          {
            ORTHOGRAPHIC, ///< Orthographic projection.
            PERSPECTIVE   ///< Perspective projection.
          };

        Camera():
          projectionType(ORTHOGRAPHIC),
          zoom(0),
          xTran(0),
          yTran(0),
          zRot(0),
          model(1.0f),
          view(1.0f),
          projection(1.0f)
        {}

        /// Projection type.
        ProjectionType projectionType;
        /// Zoom factor.
        int zoom;
        /// x translation
        int xTran;
        /// y translation.
        int yTran;
        /// Rotation factor.
        int zRot;
        /// Current model.
        glm::mat4 model;
        /// Current view.
        glm::mat4 view;
        /// Current projection.
        glm::mat4 projection;

        /**
         * Get zoom factor.
         *
         * Convert linear signed zoom value to a factor (to the 10th
         * power of the zoom value).
         *
         * @returns the zoom factor.
         */
        float
        zoomfactor() const
        {
          return std::pow(10.0f, static_cast<float>(zoom)/1024.0f); /// @todo remove fixed size.
        }

        /**
         * Get rotation factor.
         *
         * @returns the rotation factor (in radians).
         */
        float
        rotation() const
        {
          return glm::radians(-static_cast<float>(zRot)/16.0f);
        }

        /**
         * Get modelview projection matrix.
         *
         * The separate model, view and projection matrices are
         * combined to form a single matrix.
         *
         * @returns the modelview projection matrix.
         */
        glm::mat4
        mvp() const
        {
          return projection * view * model;
        }
      };

      /// Current projection
      Camera camera;
      /// Current mouse behaviour.
      MouseMode mouseMode;
      /// Rendering timer.
      QElapsedTimer etimer;
      /// Minimum level for linear contrast.
      glm::vec3 cmin;
      /// Maximum level for linear contrast.
      glm::vec3 cmax;
      /// Current plane.
      ome::bioformats::dimension_size_type plane;
      /// Previous plane.
      ome::bioformats::dimension_size_type oldplane;
      /// Last mouse position.
      QPoint lastPos;
      /// Image to render.
      gl::Image2D *image;
      /// Axes to render.
      gl::Axis2D *axes;
      /// Grid to render.
      gl::Grid2D *grid;
      /// The image reader.
      ome::compat::shared_ptr<ome::bioformats::FormatReader> reader;
      /// The image series.
      ome::bioformats::dimension_size_type series;
    };

  }
}

#endif // OME_QTWIDGETS_GLVIEW2D_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

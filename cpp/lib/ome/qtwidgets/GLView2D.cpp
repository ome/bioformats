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

#include <QtGui/QMouseEvent>

#include <cmath>

#include <ome/qtwidgets/GLView2D.h>
#include <ome/qtwidgets/gl/Util.h>

#include <ome/qtwidgets/gl/v20/V20Image2D.h>
#include <ome/qtwidgets/gl/v20/V20Grid2D.h>
#include <ome/qtwidgets/gl/v20/V20Axis2D.h>

#define GLM_FORCE_RADIANS
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <glm/gtx/rotate_vector.hpp>

#include <iostream>

// Only Microsoft issue warnings about correct behaviour...
#ifdef _MSVC_VER
#pragma warning(disable : 4351)
#endif

namespace
{

  void
  qNormalizeAngle(int &angle)
  {
    while (angle < 0)
      angle += 360 * 16;
    while (angle > 360 * 16)
      angle -= 360 * 16;
  }

}

namespace ome
{
  namespace qtwidgets
  {

    GLView2D::GLView2D(ome::compat::shared_ptr<ome::bioformats::FormatReader>  reader,
                       ome::bioformats::dimension_size_type                    series,
                       QWidget                                                * /* parent */):
      GLWindow(),
      camera(),
      mouseMode(MODE_ZOOM),
      etimer(),
      cmin(0.0f),
      cmax(1.0f),
      plane(0),
      oldplane(-1),
      lastPos(0, 0),
      image(),
      axes(),
      grid(),
      reader(reader),
      series(series)
    {
    }

    GLView2D::~GLView2D()
    {
      makeCurrent();
    }

    QSize GLView2D::minimumSizeHint() const
    {
      return QSize(800, 600);
    }

    QSize GLView2D::sizeHint() const
    {
      return QSize(800, 600);
    }

    ome::compat::shared_ptr<ome::bioformats::FormatReader>
    GLView2D::getReader()
    {
      return reader;
    }

    ome::bioformats::dimension_size_type
    GLView2D::getSeries()
    {
      return series;
    }

    int
    GLView2D::getZoom() const
    {
      return camera.zoom;
    }

    int
    GLView2D::getXTranslation() const
    {
      return camera.xTran;
    }

    int
    GLView2D::getYTranslation() const
    {
      return camera.yTran;
    }

    int
    GLView2D::getZRotation() const
    {
      return camera.zRot;
    }

    int
    GLView2D::getChannelMin() const
    {
      return static_cast<int>(cmin[0] * 255.0*16.0);
    }

    int
    GLView2D::getChannelMax() const
    {
      return static_cast<int>(cmax[0] * 255.0*16.0);
    }

    ome::bioformats::dimension_size_type
    GLView2D::getPlane() const
    {
      return plane;
    }

    void
    GLView2D::setZoom(int zoom)
    {
      if (zoom != camera.zoom) {
        camera.zoom = zoom;
        emit zoomChanged(zoom);
        renderLater();
      }
    }

    void
    GLView2D::setXTranslation(int xtran)
    {
      if (xtran != camera.xTran) {
        camera.xTran = xtran;
        emit xTranslationChanged(xtran);
        renderLater();
      }
    }

    void
    GLView2D::setYTranslation(int ytran)
    {
      if (ytran != camera.yTran) {
        camera.yTran = ytran;
        emit yTranslationChanged(ytran);
        renderLater();
      }
    }

    void
    GLView2D::setZRotation(int angle)
    {
      qNormalizeAngle(angle);
      if (angle != camera.zRot) {
        camera.zRot = angle;
        emit zRotationChanged(angle);
        renderLater();
      }
    }

    void
    GLView2D::setMouseMode(MouseMode mode)
    {
      mouseMode = mode;
    }

    GLView2D::MouseMode
    GLView2D::getMouseMode() const
    {
      return mouseMode;
    }


    // Note fixed to one channel at the moment.

    void GLView2D::setChannelMin(int min)
    {
      float v = min / (255.0*16.0);
      if (cmin[0] != v)
        {
          cmin = glm::vec3(v);
          emit channelMinChanged(min);
          renderLater();
        }
      if (cmin[0] > cmax[0])
        setChannelMax(min);
    }

    void
    GLView2D::setChannelMax(int max)
    {
      float v = max / (255.0*16.0);
      if (cmax[0] != v)
        {
          cmax = glm::vec3(v);
          emit channelMaxChanged(max);
          renderLater();
        }
      if (cmax[0] < cmin[0])
        setChannelMin(max);
    }

    void
    GLView2D::setPlane(ome::bioformats::dimension_size_type plane)
    {
      if (this->plane != plane)
        {
          this->plane = plane;
          emit planeChanged(plane);
          renderLater();
        }
    }

    void
    GLView2D::initialize()
    {
      makeCurrent();

      glEnable(GL_DEPTH_TEST);
      gl::check_gl("Enable depth test");
      glEnable(GL_CULL_FACE);
      gl::check_gl("Enable cull face");
      glEnable(GL_MULTISAMPLE);
      gl::check_gl("Enable multisampling");
      glEnable(GL_BLEND);
      gl::check_gl("Enable blending");
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
      gl::check_gl("Set blend function");

      image = new gl::v20::Image2D(reader, series, this);
      axes = new gl::v20::Axis2D(reader, series, this);
      grid = new gl::v20::Grid2D(reader, series, this);

      GLint max_combined_texture_image_units;
      glGetIntegerv(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, &max_combined_texture_image_units);
      std::cout << "Texture unit count: " << max_combined_texture_image_units << std::endl;

      image->create();
      axes->create();
      grid->create();

      // Start timers
      startTimer(0);
      etimer.start();

      // Size viewport
      resize();
    }

    void
    GLView2D::render()
    {
      makeCurrent();

      glClearColor(1.0, 1.0, 1.0, 1.0);
      gl::check_gl("Clear colour");
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      gl::check_gl("Clear buffers");

      // Render image
      glm::mat4 mvp = camera.mvp();
      image->render(mvp);
      axes->render(mvp);
      grid->render(mvp, camera.zoomfactor());
    }

    void
    GLView2D::resize()
    {
      makeCurrent();

      QSize newsize = size();
      glViewport(0, 0, newsize.width(), newsize.height());
    }


    void
    GLView2D::mousePressEvent(QMouseEvent *event)
    {
      lastPos = event->pos();
    }

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

    void
    GLView2D::mouseMoveEvent(QMouseEvent *event)
    {
      int dx = event->x() - lastPos.x();
      int dy = event->y() - lastPos.y();

      if (event->buttons() & Qt::LeftButton) {
        switch (mouseMode)
          {
          case MODE_ZOOM:
            setZoom(camera.zoom + 8 * dy);
            break;
          case MODE_PAN:
            setXTranslation(camera.xTran + 2 * -dx);
            setYTranslation(camera.yTran + 2 *  dy);
            break;
          case MODE_ROTATE:
            setZRotation(camera.zRot + 8 * -dy);
            break;
          }
      }
      lastPos = event->pos();
    }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

    void
    GLView2D::timerEvent (QTimerEvent *event)
    {
      makeCurrent();

      float zoomfactor = camera.zoomfactor();

      float xtr(static_cast<float>(camera.xTran) / zoomfactor);
      float ytr(static_cast<float>(camera.yTran) / zoomfactor);

      glm::vec3 tr(glm::rotateZ(glm::vec3(xtr, ytr, 0.0), camera.rotation()));

      camera.view = glm::lookAt(glm::vec3(tr[0], tr[1], 5.0),
                                glm::vec3(tr[0], tr[1], 0.0),
                                glm::rotateZ(glm::vec3(0.0, 1.0, 0.0), camera.rotation()));

      // Window size.  Size may be zero if the window is not yet mapped.
      QSize s = size();
      float xrange = static_cast<float>(s.width()) / zoomfactor;
      float yrange = static_cast<float>(s.height()) / zoomfactor;

      camera.projection = glm::ortho(-xrange, xrange,
                                     -yrange, yrange,
                                     0.0f, 10.0f);

      image->setPlane(getPlane());
      image->setMin(cmin);
      image->setMax(cmax);

      GLWindow::timerEvent(event);

      renderLater();
    }

  }
}

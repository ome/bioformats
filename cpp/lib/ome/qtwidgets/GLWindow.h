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

#ifndef OME_QTWIDGETS_GLWINDOW_H
#define OME_QTWIDGETS_GLWINDOW_H

#include <QtGui/QWindow>
#include <QtGui/QOpenGLFunctions>
#include <QtGui/QOpenGLDebugMessage>

QT_BEGIN_NAMESPACE
class QPainter;
class QOpenGLContext;
class QOpenGLPaintDevice;
class QOpenGLDebugLogger;
QT_END_NAMESPACE

namespace ome
{
  namespace qtwidgets
  {

    /**
     * Top level GL window.
     *
     * This is a standard QWindow, however it contains no child
     * widgets; it's simply a surface on which to paint GL rendered
     * content.
     */
    class GLWindow : public QWindow,
                     protected QOpenGLFunctions
    {
      Q_OBJECT

    public:
      /**
       * Create a GL window.
       *
       * @param parent the parent of this object.
       */
      explicit GLWindow(QWindow *parent = 0);

      /// Destructor.
      ~GLWindow();

      /**
       * Render using a QPainter.
       *
       * Does nothing by default.  Subclass to paint with QPainter on
       * the OpenGL paint device.
       *
       * @param painter the painter for rendering.
       */
      virtual void
      render(QPainter *painter);

      /**
       * Render using OpenGL.
       *
       * By default sets up a QOpenGLPaintDevice and calls
       * render(QPainter*).  Subclass and reimplement to handle
       * rendering directly.
       */
      virtual void
      render();

      /**
       * Handle initialization of the window.
       */
      virtual void
      initialize();

      /**
       * Handle resizing of the window.
       */
      virtual void
      resize();

      /**
       * Enable or disable animating.
       *
       * If enabled, this will trigger a full render pass for every
       * frame using a timer.  If disabled, rendering must be
       * triggered by hand.
       *
       * @param animating @c true to enable continuous animation or @c
       * false to disable.
       */
      void
      setAnimating(bool animating);

    public slots:
      /**
       * Render a frame at the next opportunity.
       *
       * Mark the window for requiring a full render pass at a future
       * point in time.  This will usually be for the next frame.
       */
      void
      renderLater();

      /**
       * Render a frame immediately.
       *
       * This method also handles initialization of the GL context if
       * it has not been called previously.
       */
      void
      renderNow();

      /**
       * Log a GL debug message.
       *
       * This currently logs to stderr due to the high log volume when
       * debugging is enabled.
       *
       * @param message the message to log.
       */
      void
      logMessage(QOpenGLDebugMessage message);

    protected:
      /**
       * Handle events.
       *
       * Used to handle timer events and trigger a rendering pass.
       *
       * @param event the event to handle.
       * @returns @c true if the event was handled.
       */
      bool
      event(QEvent *event);

      /**
       * Handle expose events.
       *
       * Trigger a rendering pass on exposure.
       *
       * @param event the event to handle.
       */
      void
      exposeEvent(QExposeEvent *event);

      /**
       * Handle resize events.
       *
       * @param event the event to handle.
       */
      void
      resizeEvent(QResizeEvent *event);

      /**
       * Get GL context.
       *
       * @returns the GL context.
       */
      QOpenGLContext *
      context() const;

      /**
       * Make the GL context for this window the current context.
       */
      void
      makeCurrent();

    private:
      /// Update at next opportunity?
      bool update_pending;
      /// Animation enabled?
      bool animating;
      /// OpenGL context.
      QOpenGLContext *glcontext;
      /// OpenGL paint device (if render is not reimplemented in subclass).
      QOpenGLPaintDevice *device;
      /// OpenGL debug logger (if logging enabled).
      QOpenGLDebugLogger *logger;
    };

  }
}

#endif // OME_QTWIDGETS_GLWINDOW_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

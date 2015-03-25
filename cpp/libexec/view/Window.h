/*
 * #%L
 * GLVIEW program for display of Bio-Formats pixel data and metadata.
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

#ifndef WINDOW_H
#define WINDOW_H

#include <QMainWindow>

#include <ome/qtwidgets/GLView2D.h>
#include <ome/qtwidgets/NavigationDock2D.h>

QT_BEGIN_NAMESPACE
class QSlider;
class QMenu;
class QAction;
class QActionGroup;
QT_END_NAMESPACE

namespace view
{

  class Window : public QMainWindow
  {
    Q_OBJECT

  public:
    Window();

  private slots:
    void open();
    void open(const QString& file);
    void quit();
    void view_reset();
    void view_zoom();
    void view_pan();
    void view_rotate();
    void viewFocusChanged(ome::qtwidgets::GLView2D *glView);
    void tabChanged(int index);

  private:
    void createActions();
    void createMenus();
    void createToolbars();
    void createDockWindows();

    QMenu *fileMenu;
    QMenu *viewMenu;

    QToolBar *Cam2DTools;

    QAction *openAction;
    QAction *quitAction;

    QAction *viewResetAction;

    QActionGroup *viewActionGroup;
    QAction *viewZoomAction;
    QAction *viewPanAction;
    QAction *viewRotateAction;

    QSlider *createAngleSlider();
    QSlider *createRangeSlider();
    ome::qtwidgets::NavigationDock2D *navigation;

    QTabWidget *tabs;
    ome::qtwidgets::GLView2D *glView;
    QSlider *minSlider;
    QSlider *maxSlider;

    QMetaObject::Connection minSliderChanged;
    QMetaObject::Connection minSliderUpdate;
    QMetaObject::Connection maxSliderChanged;
    QMetaObject::Connection maxSliderUpdate;
    QMetaObject::Connection navigationChanged;
    QMetaObject::Connection navigationUpdate;
  };

}

#endif // WINDOW_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

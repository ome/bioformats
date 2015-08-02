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

#include <view/Window.h>

#include <ome/bioformats/FormatReader.h>
#include <ome/bioformats/in/OMETIFFReader.h>

#include <ome/common/module.h>

#include <ome/compat/memory.h>

#include <ome/qtwidgets/GLContainer.h>

#include <QtWidgets>

using namespace ome::qtwidgets;
using ome::bioformats::dimension_size_type;

namespace view
{

  Window::Window()
  {
    createActions();
    createMenus();
    createToolbars();
    createDockWindows();
    setDockOptions(AllowTabbedDocks);

    tabs = new QTabWidget(this);

    QHBoxLayout *mainLayout = new QHBoxLayout;
    mainLayout->addWidget(tabs);

    QWidget *central = new QWidget(this);
    central->setLayout(mainLayout);

    setCentralWidget(central);

    connect(tabs, SIGNAL(currentChanged(int)), this, SLOT(tabChanged(int)));


    setWindowTitle(tr("Bio-Formats GLView"));
  }

  void Window::createActions()
  {
    boost::filesystem::path iconpath(ome::common::module_runtime_path("bf-icon"));

    openAction = new QAction(tr("&Open image..."), this);
    openAction->setShortcuts(QKeySequence::Open);
    openAction->setStatusTip(tr("Open an existing image file"));
    connect(openAction, SIGNAL(triggered()), this, SLOT(open()));

    quitAction = new QAction(tr("&Quit"), this);
    quitAction->setShortcuts(QKeySequence::Quit);
    quitAction->setStatusTip(tr("Quit the application"));
    connect(quitAction, SIGNAL(triggered()), this, SLOT(quit()));

    viewResetAction = new QAction(tr("&Reset"), this);
    viewResetAction->setShortcut(QKeySequence(Qt::CTRL + Qt::SHIFT + Qt::Key_R));
    viewResetAction->setStatusTip(tr("Reset the current view"));
    QIcon reset_icon(QString((iconpath / "actions/ome-reset2d.svg").c_str()));
    viewResetAction->setIcon(reset_icon);
    viewResetAction->setEnabled(false);
    connect(viewResetAction, SIGNAL(triggered()), this, SLOT(view_reset()));

    viewZoomAction = new QAction(tr("&Zoom"), this);
    viewZoomAction->setCheckable(true);
    viewZoomAction->setShortcut(QKeySequence(Qt::CTRL + Qt::SHIFT + Qt::Key_Z));
    viewZoomAction->setStatusTip(tr("Zoom the current view"));
    QIcon zoom_icon(QString((iconpath / "actions/ome-zoom2d.svg").c_str()));
    viewZoomAction->setIcon(zoom_icon);
    viewZoomAction->setEnabled(false);
    connect(viewZoomAction, SIGNAL(triggered()), this, SLOT(view_zoom()));

    viewPanAction = new QAction(tr("&Pan"), this);
    viewPanAction->setCheckable(true);
    viewPanAction->setShortcut(QKeySequence(Qt::CTRL + Qt::SHIFT + Qt::Key_P));
    viewPanAction->setStatusTip(tr("Pan the current view"));
    QIcon pan_icon(QString((iconpath / "actions/ome-pan2d.svg").c_str()));
    viewPanAction->setIcon(pan_icon);
    viewPanAction->setEnabled(false);
    connect(viewPanAction, SIGNAL(triggered()), this, SLOT(view_pan()));

    viewRotateAction = new QAction(tr("Rota&te"), this);
    viewRotateAction->setCheckable(true);
    viewRotateAction->setShortcut(QKeySequence(Qt::CTRL + Qt::SHIFT + Qt::Key_T));
    viewRotateAction->setStatusTip(tr("Rotate the current view"));
    QIcon rotate_icon(QString((iconpath / "actions/ome-rotate2d.svg").c_str()));
    viewRotateAction->setIcon(rotate_icon);
    viewRotateAction->setEnabled(false);
    connect(viewRotateAction, SIGNAL(triggered()), this, SLOT(view_rotate()));

    viewActionGroup = new QActionGroup(this);
    viewActionGroup->addAction(viewZoomAction);
    viewActionGroup->addAction(viewPanAction);
    viewActionGroup->addAction(viewRotateAction);
    viewZoomAction->setChecked(true);
  }

  void Window::createMenus()
  {
    fileMenu = menuBar()->addMenu(tr("&File"));
    fileMenu->addAction(openAction);
    fileMenu->addSeparator();
    fileMenu->addAction(quitAction);

    viewMenu = menuBar()->addMenu(tr("&View"));
    viewMenu->addAction(viewResetAction);
    fileMenu->addSeparator();
    viewMenu->addAction(viewZoomAction);
    viewMenu->addAction(viewPanAction);
    viewMenu->addAction(viewRotateAction);
  }

  void Window::createToolbars()
  {
    Cam2DTools = new QToolBar("2D Camera", this);
    addToolBar(Qt::TopToolBarArea, Cam2DTools);
    Cam2DTools->addAction(viewResetAction);
    Cam2DTools->addAction(viewZoomAction);
    Cam2DTools->addAction(viewPanAction);
    Cam2DTools->addAction(viewRotateAction);

    viewMenu->addSeparator();
    viewMenu->addAction(Cam2DTools->toggleViewAction());
  }

  void Window::createDockWindows()
  {
    navigation = new ome::qtwidgets::NavigationDock2D(this);
    navigation->setAllowedAreas(Qt::AllDockWidgetAreas);
    addDockWidget(Qt::BottomDockWidgetArea, navigation);

    viewMenu->addSeparator();
    viewMenu->addAction(navigation->toggleViewAction());

    QDockWidget *dock = new QDockWidget(tr("Rendering"), this);
    dock->setAllowedAreas(Qt::AllDockWidgetAreas);

    QGridLayout *layout = new QGridLayout;

    QLabel *minLabel = new QLabel(tr("Min"));
    QLabel *maxLabel = new QLabel(tr("Max"));
    minSlider=createRangeSlider();
    maxSlider=createRangeSlider();

    layout->addWidget(minLabel, 0, 0);
    layout->addWidget(minSlider, 0, 1);
    layout->addWidget(maxLabel, 1, 0);
    layout->addWidget(maxSlider, 1, 1);

    QWidget *mainWidget = new QWidget(this);
    mainWidget->setLayout(layout);
    dock->setWidget(mainWidget);
    addDockWidget(Qt::BottomDockWidgetArea, dock);

    viewMenu->addAction(dock->toggleViewAction());
  }

  QSlider *Window::createAngleSlider()
  {
    QSlider *slider = new QSlider(Qt::Vertical);
    slider->setRange(0, 365 * 16);
    slider->setSingleStep(16);
    slider->setPageStep(8 * 16);
    slider->setTickInterval(8 * 16);
    slider->setTickPosition(QSlider::TicksRight);
    return slider;
  }

  QSlider *Window::createRangeSlider()
  {
    QSlider *slider = new QSlider(Qt::Horizontal);
    slider->setRange(0, 255 * 16);
    slider->setSingleStep(16);
    slider->setPageStep(8 * 16);
    slider->setTickInterval(8 * 16);
    slider->setTickPosition(QSlider::TicksRight);
    return slider;
  }

  void Window::open()
  {
    QString file = QFileDialog::getOpenFileName(this,
                                                tr("Open Image"),
                                                QString(),
                                                QString(),
                                                0,
                                                QFileDialog::DontResolveSymlinks);

    if (!file.isEmpty())
      open(file);
  }

  void Window::open(const QString& file)
  {
    QFileInfo info(file);
    if (info.exists())
      {
        ome::compat::shared_ptr<ome::bioformats::FormatReader> reader(ome::compat::make_shared<ome::bioformats::in::OMETIFFReader>());
        reader->setId(file.toStdString());
        GLView2D *newGlView = new GLView2D(reader, 0, this);
        QWidget *glContainer = new GLContainer(this, newGlView);
        newGlView->setObjectName("glcontainer");
        // We need a minimum size or else the size defaults to zero.
        glContainer->setMinimumSize(512, 512);
        tabs->addTab(glContainer, info.fileName());
        newGlView->setChannelMin(6 *16);
        newGlView->setChannelMax(100 * 16);
        newGlView->setPlane(0);
      }
  }

  void Window::viewFocusChanged(GLView2D *newGlView)
  {
    if (glView == newGlView)
      return;

    disconnect(minSliderChanged);
    disconnect(minSliderUpdate);
    disconnect(maxSliderChanged);
    disconnect(maxSliderUpdate);
    disconnect(navigationChanged);
    disconnect(navigationUpdate);

    viewResetAction->setEnabled(false);
    viewZoomAction->setEnabled(false);
    viewPanAction->setEnabled(false);
    viewRotateAction->setEnabled(false);

    if (newGlView)
      {
        minSliderChanged = connect(minSlider, SIGNAL(valueChanged(int)), newGlView, SLOT(setChannelMin(int)));
        minSliderUpdate = connect(newGlView, SIGNAL(channelMinChanged(int)), minSlider, SLOT(setValue(int)));
        maxSliderChanged = connect(maxSlider, SIGNAL(valueChanged(int)), newGlView, SLOT(setChannelMax(int)));
        maxSliderUpdate = connect(newGlView, SIGNAL(channelMaxChanged(int)), maxSlider, SLOT(setValue(int)));

        navigation->setReader(newGlView->getReader(), newGlView->getSeries(), newGlView->getPlane());
        navigationChanged = connect(navigation, SIGNAL(planeChanged(ome::bioformats::dimension_size_type)), newGlView, SLOT(setPlane(ome::bioformats::dimension_size_type)));
        navigationUpdate = connect(newGlView, SIGNAL(planeChanged(ome::bioformats::dimension_size_type)), navigation, SLOT(setPlane(ome::bioformats::dimension_size_type)));

        minSlider->setValue(newGlView->getChannelMin());
        maxSlider->setValue(newGlView->getChannelMax());
        navigation->setPlane(newGlView->getPlane());
      }
    else
      {
        navigation->setReader(ome::compat::shared_ptr<ome::bioformats::FormatReader>(), 0, 0);
      }

    bool enable(newGlView != 0);
    minSlider->setEnabled(enable);
    maxSlider->setEnabled(enable);

    viewResetAction->setEnabled(enable);
    viewZoomAction->setEnabled(enable);
    viewPanAction->setEnabled(enable);
    viewRotateAction->setEnabled(enable);

    glView = newGlView;
  }

  void Window::tabChanged(int index)
  {
    GLView2D *current = 0;
    if (index >= 0)
      {
        QWidget *w = tabs->currentWidget();
        if (w)
          {
            GLContainer *container = static_cast<GLContainer *>(w);
            if (container)
              current = static_cast<GLView2D *>(container->getWindow());
          }
      }
    viewFocusChanged(current);
  }

  void Window::quit()
  {
    close();
  }

  void Window::view_reset()
  {
    if (glView)
      {
        glView->setZoom(0);
        glView->setXTranslation(0);
        glView->setYTranslation(0);
        glView->setZRotation(0);
      }
  }

  void Window::view_zoom()
  {
    if (glView)
      glView->setMouseMode(GLView2D::MODE_ZOOM);
  }

  void Window::view_pan()
  {
    if (glView)
      glView->setMouseMode(GLView2D::MODE_PAN);
  }

  void Window::view_rotate()
  {
    if (glView)
      glView->setMouseMode(GLView2D::MODE_ROTATE);
  }

}

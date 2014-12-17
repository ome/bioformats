/*
 * #%L
 * OME-QTWIDGETS C++ library for display of Bio-Formats pixel data and metadata.
 * %%
 * Copyright © 2014 Open Microscopy Environment:
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

#ifndef OME_QTWIDGETS_NAVIGATIONDOCK2D_H
#define OME_QTWIDGETS_NAVIGATIONDOCK2D_H

#include <ome/bioformats/FormatReader.h>

#include <ome/qtwidgets/GLWindow.h>
#include <ome/qtwidgets/gl/Image2D.h>
#include <ome/qtwidgets/gl/Grid2D.h>
#include <ome/qtwidgets/gl/Axis2D.h>

#include <QtWidgets/QDockWidget>
#include <QtWidgets/QLabel>
#include <QtWidgets/QSlider>
#include <QtWidgets/QSpinBox>

#include <glm/glm.hpp>

namespace ome
{
  namespace qtwidgets
  {

    /**
     * 2D GL view of an image with axes and gridlines.
     */
    class NavigationDock2D : public QDockWidget
    {
      Q_OBJECT

    public:

      /**
       * Create a 2D navigation view.
       *
       * The size and position will be taken from the specified image.
       *
       * @param parent the parent of this object.
       */
      NavigationDock2D(QWidget *parent = 0);

      /// Destructor.
      ~NavigationDock2D();

      void
      setReader(std::shared_ptr<ome::bioformats::FormatReader> reader,
                ome::bioformats::dimension_size_type           series = 0,
                ome::bioformats::dimension_size_type           plane = 0);

      ome::bioformats::dimension_size_type
      plane() const;

    public slots:
      void setPlane(ome::bioformats::dimension_size_type plane);

    signals:
      void planeChanged(ome::bioformats::dimension_size_type plane);

    private slots:
      void sliderChangedPlane(int plane);

      void spinBoxChangedPlane(int plane);

      void sliderChangedDimension(int dim);

      void spinBoxChangedDimension(int dim);

    private:
      std::shared_ptr<ome::bioformats::FormatReader> reader;
      ome::bioformats::dimension_size_type series;
      ome::bioformats::dimension_size_type currentPlane;

      QLabel *labels[7];
      QSlider *sliders[7];
      QSpinBox *spinboxes[7];
    };

  }
}

#endif // OME_QTWIDGETS_NAVIGATIONDOCK2D_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

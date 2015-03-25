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

#ifndef OME_QTWIDGETS_NAVIGATIONDOCK2D_H
#define OME_QTWIDGETS_NAVIGATIONDOCK2D_H

#include <ome/bioformats/FormatReader.h>

#include <ome/compat/memory.h>

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
     * 2D dock widget for plane nagivation.
     *
     * Sliders will be created for each usable dimension, including
     * for Modulo annotations.
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

      /**
       * Set reader, including current series and plane position.
       *
       * @param reader the image reader.
       * @param series the image series.
       * @param plane the image plane.
       */
      void
      setReader(ome::compat::shared_ptr<ome::bioformats::FormatReader> reader,
                ome::bioformats::dimension_size_type                   series = 0,
                ome::bioformats::dimension_size_type                   plane = 0);

      /**
       * Get the current plane for the series.
       *
       * @returns the current plane.
       */
      ome::bioformats::dimension_size_type
      plane() const;

    public slots:
      /**
       * Set the current plane for the series.
       *
       * @param plane the image plane.
       */
      void
      setPlane(ome::bioformats::dimension_size_type plane);

    signals:
      /**
       * Signal change of plane.
       *
       * @param plane the new image plane.
       */
      void
      planeChanged(ome::bioformats::dimension_size_type plane);

    private slots:
      /**
       * Update the current plane number (from slider).
       *
       * @param plane the new image plane.
       */
      void
      sliderChangedPlane(int plane);

      /**
       * Update the current plane number (from spinbox).
       *
       * @param plane the new image plane.
       */
      void
      spinBoxChangedPlane(int plane);

      /**
       * Update the current plane number (from dimension slider).
       *
       * @param dim the index of the dimension slider.
       */
      void
      sliderChangedDimension(int dim);

      /**
       * Update the current plane number (from dimension spinbox).
       *
       * @param dim the index of the dimension spinbox.
       */
      void
      spinBoxChangedDimension(int dim);

    private:
      /// The image reader.
      ome::compat::shared_ptr<ome::bioformats::FormatReader> reader;
      /// The image series.
      ome::bioformats::dimension_size_type series;
      /// The image plane.
      ome::bioformats::dimension_size_type currentPlane;

      /// Slider labels [NZTCmZmTmC].
      QLabel *labels[7];
      /// Sliders [NZTCmZmTmC].
      QSlider *sliders[7];
      /// Numeric entries [NZTCmZmTmC].
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

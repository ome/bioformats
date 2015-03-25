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

#include <ome/qtwidgets/gl/Axis2D.h>
#include <ome/qtwidgets/gl/Util.h>

#include <iostream>

namespace ome
{
  namespace qtwidgets
  {
    namespace gl
    {

      Axis2D::Axis2D(ome::compat::shared_ptr<ome::bioformats::FormatReader>  reader,
                     ome::bioformats::dimension_size_type                    series,
                     QObject                                                *parent):
        QObject(parent),
        xaxis_vertices(QOpenGLBuffer::VertexBuffer),
        yaxis_vertices(QOpenGLBuffer::VertexBuffer),
        axis_elements(QOpenGLBuffer::IndexBuffer),
        reader(reader),
        series(series)
      {
        initializeOpenGLFunctions();
      }

      Axis2D::~Axis2D()
      {
      }

      void
      Axis2D::create()
      {
        ome::bioformats::dimension_size_type oldseries = reader->getSeries();
        reader->setSeries(series);
        setSize(glm::vec2(-(reader->getSizeX()/2.0f), reader->getSizeX()/2.0f),
                glm::vec2(-(reader->getSizeY()/2.0f), reader->getSizeY()/2.0f),
                glm::vec2(-(reader->getSizeX()/2.0f)-12.0, -(reader->getSizeY()/2.0f)-12.0),
                glm::vec2(-6.0, 6.0));
        reader->setSeries(oldseries);
      }

      void
      Axis2D::setSize(glm::vec2 xlim,
                      glm::vec2 ylim,
                      glm::vec2 soff,
                      glm::vec2 slim)
      {
        GLfloat swid(slim[1] - slim[0]);
        GLfloat smid(slim[0] + (swid/2.0));
        GLfloat arrowwid(swid * 5);
        GLfloat arrowlen(arrowwid * 1.5);
        GLfloat xoff(static_cast<GLfloat>(soff[0]));
        GLfloat yoff(static_cast<GLfloat>(soff[1]));

        GLfloat xaxis_vertices_a[] = {
          // Arrow shaft
          xlim[0], yoff+slim[0],
          xlim[1]-arrowlen, yoff+slim[0],
          xlim[1]-arrowlen, yoff+slim[1],
          xlim[0], yoff+slim[1],
          // Arrow head
          xlim[1]-arrowlen, yoff+smid+(arrowwid/2.0f),
          xlim[1]-arrowlen, yoff+smid-(arrowwid/2.0f),
          xlim[1], yoff+smid
        };

        GLfloat yaxis_vertices_a[] = {
          // Arrow shaft
          xoff+slim[1], ylim[0],
          xoff+slim[1], ylim[1]-arrowlen,
          xoff+slim[0], ylim[1]-arrowlen,
          xoff+slim[0], ylim[0],
          // Arrow head
          xoff+smid-(arrowwid/2.0f), ylim[1]-arrowlen,
          xoff+smid+(arrowwid/2.0f), ylim[1]-arrowlen,
          xoff+smid, ylim[1]
        };

        xaxis_vertices.create();
        xaxis_vertices.setUsagePattern(QOpenGLBuffer::StaticDraw);
        xaxis_vertices.bind();
        xaxis_vertices.allocate(xaxis_vertices_a, sizeof(xaxis_vertices_a));

        yaxis_vertices.create();
        yaxis_vertices.setUsagePattern(QOpenGLBuffer::StaticDraw);
        yaxis_vertices.bind();
        yaxis_vertices.allocate(yaxis_vertices_a, sizeof(yaxis_vertices_a));

        GLushort axis_elements_a[] = {
          // Arrow shaft
          0,  1,  2,
          2,  3,  0,
          // Arrow head
          4,  5,  6
        };

        axis_elements.create();
        axis_elements.setUsagePattern(QOpenGLBuffer::StaticDraw);
        axis_elements.bind();
        axis_elements.allocate(axis_elements_a, sizeof(axis_elements_a));
      }

    }
  }
}

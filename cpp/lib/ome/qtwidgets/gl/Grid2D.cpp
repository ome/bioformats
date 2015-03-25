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

#include <ome/qtwidgets/gl/Grid2D.h>
#include <ome/qtwidgets/gl/Util.h>

#include <ome/compat/array.h>
#include <cmath>
#include <iostream>

// Defined on some unix systems.
#undef major
#undef minor

namespace
{
  struct vert
  {
    glm::vec3 coords; // x, y, scalefactor
    glm::vec3 colour;

    vert(const glm::vec3& coords,
         const glm::vec3& colour):
      coords(coords),
      colour(colour)
    {}
  };

  /**
   * Calculate major gridline.
   *
   * The value is lowered to the previous order of magnitude.
   *
   * @param v the value to use as the initial boundary.
   * @returns the new boundary.
   */
  int
  major(float v)
  {
    return static_cast<int>(std::floor(std::log10(v > 0.0f ? v : -v) - 0.5f));
  }

  float
  majorlimit(int   mag,
             float v,
             bool  high)
  {
    float f = pow(10.0f, static_cast<float>(mag));
    v /= f;

    if (high) // high limit
      {
        v += 1.0f;
        v = std::ceil(v);
      }
    else
      {
        v -= 1.0f;
        v = std::floor(v);
      }
    v *= f;

    return v;
  }
}

namespace ome
{
  namespace qtwidgets
  {
    namespace gl
    {

      Grid2D::Grid2D(ome::compat::shared_ptr<ome::bioformats::FormatReader>  reader,
                     ome::bioformats::dimension_size_type                    series,
                     QObject                                                *parent):
        QObject(parent),
        grid_vertices(QOpenGLBuffer::VertexBuffer),
        grid_elements(QOpenGLBuffer::IndexBuffer),
        reader(reader),
        series(series)
      {
        initializeOpenGLFunctions();

        ome::bioformats::dimension_size_type oldseries = reader->getSeries();
        reader->setSeries(series);
        setSize(glm::vec2(-(static_cast<float>(reader->getSizeX())), reader->getSizeX()), glm::vec2(-(static_cast<float>(reader->getSizeY())), reader->getSizeY()));
        reader->setSeries(oldseries);
      }

      Grid2D::~Grid2D()
      {
      }

      void Grid2D::create()
      {
      }

      void
      Grid2D::setSize(const glm::vec2& xlim,
                      const glm::vec2& ylim)
      {
        ome::compat::array<glm::vec3, 3> gridcol;
        gridcol[0] = glm::vec3(0.5f, 0.5f, 0.5f);
        gridcol[1] = glm::vec3(0.7f, 0.7f, 0.7f);
        gridcol[2] = glm::vec3(0.9f, 0.9f, 0.9f);
        glm::vec3 xcol(0.5f, 0.0f, 0.0f);
        glm::vec3 ycol(0.0f, 0.5f, 0.0f);

        float xdiff = xlim[1] - xlim[0];
        float ydiff = ylim[1] - ylim[0];

        int lmajor = major(std::max(xdiff, ydiff));

        float xmin = majorlimit(lmajor, xlim[0], false);
        float xmax = majorlimit(lmajor, xlim[1], true);
        float ymin = majorlimit(lmajor, ylim[0], false);
        float ymax = majorlimit(lmajor, ylim[1], true);
        std::vector<vert> verts;
        std::vector<GLushort> idxs;

        uint32_t id = 0;
        // x and y origin
        verts.push_back(vert(glm::vec3(xmin, 0.0f, 0.0f), xcol));
        verts.push_back(vert(glm::vec3(xmax, 0.0f, 0.0f), xcol));
        idxs.push_back(id++);
        idxs.push_back(id++);
        verts.push_back(vert(glm::vec3(0.0f, ymin, 0.0f), ycol));
        verts.push_back(vert(glm::vec3(0.0f, ymax, 0.0f), ycol));
        idxs.push_back(id++);
        idxs.push_back(id++);

        // Grid lines at increasing level of detail
        for (int i = 0; i < 3; ++i)
          {
            float gmajor = std::pow(10.0f, static_cast<float>(lmajor-i));
            uint32_t mf = static_cast<uint32_t>(std::pow(10.0f, static_cast<float>(i-1)));
            for (uint32_t j = 0; j < static_cast<uint32_t>((xmax-xmin)/gmajor)+1; ++j)
              {
                if (mf <= 1 || j % mf)
                  {
                    float x = xmin + (static_cast<float>(j) * gmajor);
                    verts.push_back(vert(glm::vec3(x, ymin, static_cast<float>(i)), gridcol.at(i)));
                    verts.push_back(vert(glm::vec3(x, ymax, static_cast<float>(i)), gridcol.at(i)));
                    idxs.push_back(id++);
                    idxs.push_back(id++);
                  }
              }
            for (uint32_t j = 0; j < static_cast<uint32_t>((ymax-ymin)/gmajor)+1; ++j)
              {
                if (mf <= 1 || j % mf)
                  {
                    float y = ymin + (static_cast<float>(j) * gmajor);
                    verts.push_back(vert(glm::vec3(xmin, y, static_cast<float>(i)), gridcol.at(i)));
                    verts.push_back(vert(glm::vec3(xmax, y, static_cast<float>(i)), gridcol.at(i)));
                    idxs.push_back(id++);
                    idxs.push_back(id++);
                  }
              }
          }

        grid_vertices.create();
        grid_vertices.setUsagePattern(QOpenGLBuffer::StaticDraw);
        grid_vertices.bind();
        grid_vertices.allocate(verts.data(),
                               sizeof(vert) * static_cast<size_t>(verts.size()));

        grid_elements.create();
        grid_elements.setUsagePattern(QOpenGLBuffer::StaticDraw);
        grid_elements.bind();
        grid_elements.allocate(idxs.data(),
                               sizeof(GLushort) * static_cast<size_t>(idxs.size()));
      }

    }
  }
}

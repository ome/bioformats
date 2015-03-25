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

#include <ome/bioformats/PixelBuffer.h>
#include <ome/bioformats/VariantPixelBuffer.h>

#include <ome/qtwidgets/gl/Image2D.h>
#include <ome/qtwidgets/gl/Util.h>

#include <iostream>

using ome::bioformats::PixelBuffer;
using ome::bioformats::PixelBufferBase;
using ome::bioformats::PixelProperties;
using ome::bioformats::VariantPixelBuffer;
using ome::qtwidgets::gl::check_gl;
typedef ome::xml::model::enums::PixelType PT;

namespace
{

  class TextureProperties
  {
  public:
    GLenum internal_format;
    GLenum external_format;
    GLint external_type;
    bool make_normal;
    GLint min_filter;
    GLint mag_filter;
    ome::bioformats::dimension_size_type w;
    ome::bioformats::dimension_size_type h;

    TextureProperties(const ome::bioformats::FormatReader& reader,
                      ome::bioformats::dimension_size_type series):
      internal_format(GL_R8),
      external_format(GL_RED),
      external_type(GL_UNSIGNED_BYTE),
      make_normal(false),
      min_filter(GL_LINEAR_MIPMAP_LINEAR),
      mag_filter(GL_LINEAR),
      w(0),
      h(0)
    {
      ome::bioformats::dimension_size_type oldseries = reader.getSeries();
      reader.setSeries(series);
      ome::xml::model::enums::PixelType pixeltype = reader.getPixelType();
      reader.setSeries(oldseries);

      w = reader.getSizeX();
      h = reader.getSizeY();

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          internal_format = GL_R8;
          external_type = GL_BYTE;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          internal_format = GL_R16;
          external_type = GL_SHORT;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          internal_format = GL_R16;
          external_type = GL_INT;
          make_normal = true;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          internal_format = GL_R8;
          external_type = GL_UNSIGNED_BYTE;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          internal_format = GL_R16;
          external_type = GL_UNSIGNED_SHORT;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          internal_format = GL_R16;
          external_type = GL_UNSIGNED_INT;
          make_normal = true;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          internal_format = GL_R32F;
          if (!GL_ARB_texture_float)
            internal_format = GL_R16;
          external_type = GL_FLOAT;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          internal_format = GL_R32F;
          if (!GL_ARB_texture_float)
            internal_format = GL_R16;
          external_type = GL_DOUBLE;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          internal_format = GL_R8;
          external_type = GL_UNSIGNED_BYTE;
          make_normal = true;
          min_filter = GL_NEAREST_MIPMAP_LINEAR;
          mag_filter = GL_NEAREST;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          internal_format = GL_RG32F;
          if (!GL_ARB_texture_float)
            internal_format = GL_RG16;
          external_type = GL_FLOAT;
          external_format = GL_RG;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          internal_format = GL_RG32F;
          if (!GL_ARB_texture_float)
            internal_format = GL_RG16;
          external_type = GL_DOUBLE;
          external_format = GL_RG;
          break;
        }
    }
  };

  /*
   * Assign VariantPixelBuffer to OpenGL texture buffer.
   *
   * The following buffer types are supported:
   * - RGB subchannel, single channel for simple numeric types
   * - no subchannel, single channel for simple numeric types
   * - no subchannel, single channel for complex numeric types
   *
   * The buffer may only contain a single xy plane; no higher
   * dimensions may be used.
   *
   * If OpenGL limitations require
   */
  struct GLSetBufferVisitor : public boost::static_visitor<>,
                              protected QOpenGLFunctions
  {
    unsigned int textureid;
    TextureProperties tprop;

    GLSetBufferVisitor(unsigned int textureid,
                       const TextureProperties& tprop):
      textureid(textureid),
      tprop(tprop)
    {
      initializeOpenGLFunctions();
    }

    PixelBufferBase::storage_order_type
    gl_order(const PixelBufferBase::storage_order_type& order)
    {
      PixelBufferBase::storage_order_type ret(order);
      // This makes the assumption that the order is SXY or XYS, and
      // switches XYS to SXY if needed.
      if (order.ordering(0) != ome::bioformats::DIM_SUBCHANNEL)
        {
          PixelBufferBase::size_type ordering[PixelBufferBase::dimensions];
          bool ascending[PixelBufferBase::dimensions] = {true, true, true, true, true, true, true, true, true};
          for (boost::detail::multi_array::size_type d = 0; d < PixelBufferBase::dimensions; ++d)
            {
              ordering[d] = order.ordering(d);
              ascending[d] = order.ascending(d);

              PixelBufferBase::size_type xo = ordering[0];
              PixelBufferBase::size_type yo = ordering[1];
              PixelBufferBase::size_type so = ordering[2];
              bool xa = ascending[0];
              bool ya = ascending[1];
              bool sa = ascending[2];

              ordering[0] = so;
              ordering[1] = xo;
              ordering[2] = yo;
              ascending[0] = sa;
              ascending[1] = xa;
              ascending[2] = ya;

              ret = PixelBufferBase::storage_order_type(ordering, ascending);
            }
        }
      return ret;
    }

    template<typename T>
    void
    operator() (const T& v)
    {
      typedef typename T::element_type::value_type value_type;

      T src_buffer(v);
      const PixelBufferBase::storage_order_type& orig_order(v->storage_order());
      PixelBufferBase::storage_order_type new_order(gl_order(orig_order));

      if (!(new_order == orig_order))
        {
          // Reorder as interleaved.
          const PixelBufferBase::size_type *shape = v->shape();

          T gl_buf(new typename T::element_type(boost::extents[shape[0]][shape[1]][shape[2]][shape[3]][shape[4]][shape[5]][shape[6]][shape[7]][shape[8]],
                                                v->pixelType(),
                                                v->endianType(),
                                                new_order));
          *gl_buf = *v;
          src_buffer = gl_buf;
        }

      // In interleaved order.
      glPixelStorei(GL_UNPACK_ALIGNMENT, 1); // MultiArray buffers are packed

      glBindTexture(GL_TEXTURE_2D, textureid);
      check_gl("Bind texture");
      glTexSubImage2D(GL_TEXTURE_2D, // target
                      0,  // level, 0 = base, no minimap,
                      0, 0, // x, y
                      tprop.w,  // width
                      tprop.h,  // height
                      tprop.external_format,  // format
                      tprop.external_type, // type
                      //                      testdata);
                      v->data());
      check_gl("Texture set pixels in subregion");
      glGenerateMipmap(GL_TEXTURE_2D);
      check_gl("Generate mipmaps");
    }

    template <typename T>
    typename boost::enable_if_c<
      boost::is_complex<T>::value, void
      >::type
    operator() (const ome::compat::shared_ptr<PixelBuffer<T> >& v)
    {
      /// @todo Conversion from complex.
    }

  };

}

namespace ome
{
  namespace qtwidgets
  {
    namespace gl
    {

      Image2D::Image2D(ome::compat::shared_ptr<ome::bioformats::FormatReader>  reader,
                       ome::bioformats::dimension_size_type                    series,
                       QObject                                                *parent):
        QObject(parent),
        image_vertices(QOpenGLBuffer::VertexBuffer),
        image_texcoords(QOpenGLBuffer::VertexBuffer),
        image_elements(QOpenGLBuffer::IndexBuffer),
        textureid(0),
        lutid(0),
        texmin(0.0f),
        texmax(0.1f),
        texcorr(1.0f),
        reader(reader),
        series(series),
        plane(-1)
      {
        initializeOpenGLFunctions();
      }

      Image2D::~Image2D()
      {
      }

      void Image2D::create()
      {
        TextureProperties tprop(*reader, series);

        ome::bioformats::dimension_size_type oldseries = reader->getSeries();
        reader->setSeries(series);
        ome::bioformats::dimension_size_type sizeX = reader->getSizeX();
        ome::bioformats::dimension_size_type sizeY = reader->getSizeY();
        setSize(glm::vec2(-(sizeX/2.0f), sizeX/2.0f),
                glm::vec2(-(sizeY/2.0f), sizeY/2.0f));
        ome::bioformats::dimension_size_type rbpp = reader->getBitsPerPixel();
        ome::bioformats::dimension_size_type bpp = ome::bioformats::bitsPerPixel(reader->getPixelType());
        texcorr[0] = texcorr[1] = texcorr[2] = (1 << (bpp - rbpp));
        reader->setSeries(oldseries);

        // Create image texture.
        glGenTextures(1, &textureid);
        glBindTexture(GL_TEXTURE_2D, textureid);
        check_gl("Bind texture");
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, tprop.min_filter);
        check_gl("Set texture min filter");
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, tprop.mag_filter);
        check_gl("Set texture mag filter");
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        check_gl("Set texture wrap s");
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        check_gl("Set texture wrap t");

        glTexImage2D(GL_TEXTURE_2D,         // target
                     0,                     // level, 0 = base, no minimap,
                     tprop.internal_format, // internal format
                     sizeX,                 // width
                     sizeY,                 // height
                     0,                     // border
                     tprop.external_format, // external format
                     tprop.external_type,   // external type
                     0);                    // no image data at this point
        check_gl("Texture create");

        // Create LUT texture.
        glGenTextures(1, &lutid);
        glBindTexture(GL_TEXTURE_1D_ARRAY, lutid);
        check_gl("Bind texture");
        glTexParameteri(GL_TEXTURE_1D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        check_gl("Set texture min filter");
        glTexParameteri(GL_TEXTURE_1D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        check_gl("Set texture mag filter");
        glTexParameteri(GL_TEXTURE_1D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        check_gl("Set texture wrap s");

        // HiLo
        uint8_t lut[256][3];
        for (uint16_t i = 0; i < 256; ++i)
          for (uint16_t j = 0; j < 3; ++j)
            {
              lut[i][j] = i;
            }
        lut[0][0] = 0;
        lut[0][2] = 0;
        lut[0][2] = 255;
        lut[255][0] = 255;
        lut[255][1] = 0;
        lut[255][2] = 0;

        glTexImage2D(GL_TEXTURE_1D_ARRAY, // target
                     0,                   // level, 0 = base, no minimap,
                     GL_RGB8,             // internal format
                     256,                 // width
                     1,                   // height
                     0,                   // border
                     GL_RGB,              // external format
                     GL_UNSIGNED_BYTE,    // external type
                     lut);                // LUT data
        check_gl("Texture create");
      }

      void
      Image2D::setSize(const glm::vec2& xlim,
                       const glm::vec2& ylim)
      {
        GLfloat square_vertices[] = {
          xlim[0], ylim[0],
          xlim[1], ylim[0],
          xlim[1], ylim[1],
          xlim[0], ylim[1]
        };

        if (!image_vertices.isCreated())
          image_vertices.create();
        image_vertices.setUsagePattern(QOpenGLBuffer::StaticDraw);
        image_vertices.bind();
        image_vertices.allocate(square_vertices, sizeof(square_vertices));

        glm::vec2 texxlim(0.0, 1.0);
        glm::vec2 texylim(0.0, 1.0);
        GLfloat square_texcoords[] = {
          texxlim[0], texylim[0],
          texxlim[1], texylim[0],
          texxlim[1], texylim[1],
          texxlim[0], texylim[1]
        };

        if (!image_texcoords.isCreated())
          image_texcoords.create();
        image_texcoords.setUsagePattern(QOpenGLBuffer::StaticDraw);
        image_texcoords.bind();
        image_texcoords.allocate(square_texcoords, sizeof(square_texcoords));

        GLushort square_elements[] = {
          // front
          0,  1,  2,
          2,  3,  0
        };

        if (!image_elements.isCreated())
          image_elements.create();
        image_elements.setUsagePattern(QOpenGLBuffer::StaticDraw);
        image_elements.bind();
        image_elements.allocate(square_elements, sizeof(square_elements));
      }

      void
      Image2D::setPlane(ome::bioformats::dimension_size_type plane)
      {
        if (this->plane != plane)
          {
            TextureProperties tprop(*reader, series);

            ome::bioformats::VariantPixelBuffer buf;
            ome::bioformats::dimension_size_type oldseries = reader->getSeries();
            reader->setSeries(series);
            reader->openBytes(plane, buf);
            reader->setSeries(oldseries);

            GLSetBufferVisitor v(textureid, tprop);
            boost::apply_visitor(v, buf.vbuffer());
          }
        this->plane = plane;
      }

      const glm::vec3&
      Image2D::getMin() const
      {
        return texmin;
      }

      void
      Image2D::setMin(const glm::vec3& min)
      {
        texmin = min;
      }

      const glm::vec3&
      Image2D::getMax() const
      {
        return texmax;
      }

      void
      Image2D::setMax(const glm::vec3& max)
      {
        texmax = max;
      }

      unsigned int
      Image2D::texture()
      {
        return textureid;
      }

      unsigned int
      Image2D::lut()
      {
        return lutid;
      }

    }
  }
}

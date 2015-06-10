/*
 * #%L
 * OME-QTWIDGETS C++ library for display of Bio-Formats pixel data and metadata.
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

#include <ome/qtwidgets/TexelProperties.h>

#include <stdexcept>

namespace ome
{
  namespace qtwidgets
  {

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

    GLenum
    textureInternalFormat(::ome::xml::model::enums::PixelType pixeltype)
    {
      GLenum internal_format = 0;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::INT8>::internal_format;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::INT16>::internal_format;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::INT32>::internal_format;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::UINT8>::internal_format;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::UINT16>::internal_format;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::UINT32>::internal_format;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::internal_format;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::internal_format;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::BIT>::internal_format;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::internal_format;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          internal_format = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::internal_format;
          break;
        }

      return internal_format;
    }

    GLenum
    textureExternalFormat(::ome::xml::model::enums::PixelType pixeltype)
    {
      GLenum external_format = 0;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::INT8>::external_format;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::INT16>::external_format;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::INT32>::external_format;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::UINT8>::external_format;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::UINT16>::external_format;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::UINT32>::external_format;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::external_format;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::external_format;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::BIT>::external_format;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::external_format;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          external_format = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::external_format;
          break;
        }

      return external_format;
    }

    GLint
    textureExternalType(::ome::xml::model::enums::PixelType pixeltype)
    {
      GLint external_type = 0;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::INT8>::external_type;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::INT16>::external_type;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::INT32>::external_type;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::UINT8>::external_type;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::UINT16>::external_type;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::UINT32>::external_type;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::external_type;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::external_type;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::BIT>::external_type;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::external_type;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          external_type = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::external_type;
          break;
        }

      return external_type;
    }

    ::ome::xml::model::enums::PixelType
    texturePixelTypeFallback(::ome::xml::model::enums::PixelType pixeltype)
    {
      ome::xml::model::enums::PixelType fallback_pixeltype = ome::xml::model::enums::PixelType::UINT8;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::INT8>::fallback_pixeltype;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::INT16>::fallback_pixeltype;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::INT32>::fallback_pixeltype;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::UINT8>::fallback_pixeltype;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::UINT16>::fallback_pixeltype;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::UINT32>::fallback_pixeltype;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::fallback_pixeltype;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::fallback_pixeltype;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::BIT>::fallback_pixeltype;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::fallback_pixeltype;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          fallback_pixeltype = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::fallback_pixeltype;
          break;
        }

      return fallback_pixeltype;
    }

    bool
    textureConversionRequired(::ome::xml::model::enums::PixelType pixeltype)
    {
      bool conversion_required = false;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::INT8>::conversion_required;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::INT16>::conversion_required;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::INT32>::conversion_required;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::UINT8>::conversion_required;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::UINT16>::conversion_required;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::UINT32>::conversion_required;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::conversion_required;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::conversion_required;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::BIT>::conversion_required;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::conversion_required;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          conversion_required = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::conversion_required;
          break;
        }

      return conversion_required;
    }

    bool
    textureNormalizationRequired(::ome::xml::model::enums::PixelType pixeltype)
    {
      bool normalization_required = false;

      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::INT8>::normalization_required;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::INT16>::normalization_required;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::INT32>::normalization_required;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::UINT8>::normalization_required;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::UINT16>::normalization_required;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::UINT32>::normalization_required;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::normalization_required;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::normalization_required;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::BIT>::normalization_required;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::normalization_required;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          normalization_required = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::normalization_required;
          break;
        }

      return normalization_required;
    }

    GLint
    textureMinificationFilter(::ome::xml::model::enums::PixelType pixeltype)
    {
      GLint minification_filter = false; 
      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::INT8>::minification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::INT16>::minification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::INT32>::minification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::UINT8>::minification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::UINT16>::minification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::UINT32>::minification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::minification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::minification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::BIT>::minification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::minification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          minification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::minification_filter;
          break;
        }

      return minification_filter;
    }

    GLint
    textureMagnificationFilter(::ome::xml::model::enums::PixelType pixeltype)
    {
      GLint magnification_filter = false; 
      switch(pixeltype)
        {
        case ::ome::xml::model::enums::PixelType::INT8:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::INT8>::magnification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::INT16:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::INT16>::magnification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::INT32:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::INT32>::magnification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::UINT8:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::UINT8>::magnification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::UINT16:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::UINT16>::magnification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::UINT32:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::UINT32>::magnification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::FLOAT:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::FLOAT>::magnification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLE:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>::magnification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::BIT:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::BIT>::magnification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::COMPLEX:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>::magnification_filter;
          break;
        case ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX:
          magnification_filter = TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>::magnification_filter;
          break;
        }

      return magnification_filter;
    }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

    GLenum
    textureInternalFormatFallback(GLenum format)
    {
      GLenum ret = GL_R8;

      switch(format)
        {
          // R
        case GL_R32F:
          ret = GL_R16F;
          break;
        case GL_R16F:
          ret = GL_R16;
          break;
        case GL_R16:
          ret = GL_R8;
          break;

          // RG
        case GL_RG32F:
          ret = GL_R16F;
          break;
        case GL_RG16F:
          ret = GL_RG16;
          break;
        case GL_RG16:
          ret = GL_RG8;
          break;

          // RGB
        case GL_RGB32F:
          ret = GL_RGB16F;
          break;
        case GL_RGB16F:
          ret = GL_RGB16;
          break;
        case GL_RGB16:
          ret = GL_RGB8;
          break;

          // RGBA
        case GL_RGBA32F:
          ret = GL_RGBA16F;
          break;
        case GL_RGBA16F:
          ret = GL_RGBA16;
          break;
        case GL_RGBA16:
          ret = GL_RGBA8;
          break;

        default:
          ret = GL_R8;
          break;
        }

      return ret;
    }

  }
}

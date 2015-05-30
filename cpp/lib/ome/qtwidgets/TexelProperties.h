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

#ifndef OME_QTWIDGETS_TEXELPROPERTIES_H
#define OME_QTWIDGETS_TEXELPROPERTIES_H

#include <ome/bioformats/PixelProperties.h>

#include <ome/xml/model/enums/PixelType.h>

#include <QtGui/qopengl.h>

namespace ome
{
  namespace qtwidgets
  {

    /**
     * Map a given PixelType enum to the corresponding GL texel
     * definitions.  This is an extension of PixelProperties for GL
     * texture handling.
     *
     * Internal format fallbacks provide the means to render with
     * reduced quality if the hardware does not support the internal
     * format, or if memory requirements require a smaller format.
     * External formal format fallbacks allow further regression at
     * the expense of requiring explicit format conversion by the
     * user.  In practice, the internal format fallbacks will provide
     * a similar and perhaps more extensive set of fallbacks than
     * following the external fallback chain.  However, the external
     * fallbacks may be useful if system memory is under pressure.
     *
     * All simple formats use single channel, while complex types use
     * two.  However, note these are hints for defaults only.  The
     * user may choose to pack multiple channels into a single
     * texture.
     *
     * Conversion indicates that the GL type and PixelTypes do not
     * match exactly.  The user will be required to convert the type.
     *
     * Normalization indicates that the GL implementation will not
     * automatically normalize the pixel values, and the user will be
     * required to do this for themselves, for example in a fragment
     * shader.
     *
     * Minification and magnification filters are also hints.  These
     * default to a high filtering quality for all types except BIT,
     * where the mask is clamped to the nearest pixel.
     */
    template<int>
    struct TexelProperties;

    /// Properties of INT8 texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::INT8> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::INT8>
    {
      /// Internal pixel format (single 8-bit channel).
      static const GLenum internal_format = GL_R8;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RED;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::INT8;
      /// External pixel type (@c int8_t).
      static const GLint external_type = GL_BYTE;
      /// Bio-Formats type matches the GL type exactly.
      static const bool conversion_required = false;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = false;
      /// Default minification filter.
      static const GLint minification_filter = GL_LINEAR_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_LINEAR;
    };

    /// Properties of INT16 texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::INT16> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::INT16>
    {
      /// Internal pixel format (single 16-bit channel).
      static const GLenum internal_format = GL_R16;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RED;
      /// External pixel type (@c int16_t).
      static const GLint external_type = GL_SHORT;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::INT8;
      /// Bio-Formats type matches the GL type exactly.
      static const bool conversion_required = false;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = false;
      /// Default minification filter.
      static const GLint minification_filter = GL_LINEAR_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_LINEAR;
    };

    /// Properties of INT32 texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::INT32> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::INT32>
    {
      /// Internal pixel format (single 16-bit channel; note precision loss).
      static const GLenum internal_format = GL_R16;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RED;
      /// External pixel type (@c int32_t).
      static const GLint external_type = GL_INT;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::INT16;
      /// Bio-Formats type matches the GL type exactly.
      static const bool conversion_required = false;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = false;
      /// Default minification filter.
      static const GLint minification_filter = GL_LINEAR_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_LINEAR;
    };

    /// Properties of UINT8 texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::UINT8> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::UINT8>
    {
      /// Internal pixel format (single 8-bit channel).
      static const GLenum internal_format = GL_R8;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RED;
      /// External pixel type (@c uint8_t).
      static const GLint external_type = GL_UNSIGNED_BYTE;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::UINT8;
      /// Bio-Formats type matches the GL type exactly.
      static const bool conversion_required = false;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = false;
      /// Default minification filter.
      static const GLint minification_filter = GL_LINEAR_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_LINEAR;
    };

    /// Properties of UINT16 texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::UINT16> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::UINT16>
    {
      /// Internal pixel format (single 16-bit channel).
      static const GLenum internal_format = GL_R16;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RED;
      /// External pixel type (@c uint16_t).
      static const GLint external_type = GL_UNSIGNED_SHORT;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::UINT8;
      /// Bio-Formats type matches the GL type exactly.
      static const bool conversion_required = false;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = false;
      /// Default minification filter.
      static const GLint minification_filter = GL_LINEAR_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_LINEAR;
    };

    /// Properties of UINT32 texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::UINT32> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::UINT32>
    {
      /// Internal pixel format (single 16-bit channel; note precision loss).
      static const GLenum internal_format = GL_R16;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RED;
      /// External pixel type (@c uint32_t).
      static const GLint external_type = GL_UNSIGNED_INT;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::UINT16;
      /// Bio-Formats type matches the GL type exactly.
      static const bool conversion_required = false;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = false;
      /// Default minification filter.
      static const GLint minification_filter = GL_LINEAR_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_LINEAR;
    };

    /// Properties of FLOAT texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::FLOAT> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT>
    {
      /// Internal pixel format (single 32-bit float channel).
      static const GLenum internal_format = GL_R32F;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RED;
      /// External pixel type (@c float).
      static const GLint external_type = GL_FLOAT;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::INT32;
      /// Bio-Formats type matches the GL type exactly.
      static const bool conversion_required = false;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = true;
      /// Default minification filter.
      static const GLint minification_filter = GL_LINEAR_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_LINEAR;
    };

    /// Properties of DOUBLE texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLE> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE>
    {
      /// Internal pixel format (single 32-bit float channel; note precision loss).
      static const GLenum internal_format = GL_R32F;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RED;
      /// External pixel type (@c double).
      static const GLint external_type = GL_DOUBLE;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::FLOAT;
      /// Bio-Formats type matches the GL type exactly.
      static const bool conversion_required = false;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = true;
      /// Default minification filter.
      static const GLint minification_filter = GL_LINEAR_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_LINEAR;
    };

    /// Properties of BIT texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::BIT> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::BIT>
    {
      /// Internal pixel format (single 8-bit float channel).
      static const GLenum internal_format = GL_R8;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RED;
      /// External pixel type (@c uint8_t).
      static const GLint external_type = GL_UNSIGNED_BYTE;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::BIT;
      /// Bio-Formats type does not match the GL type; convert to 0 or 255 for 0 and 1, respectively.
      static const bool conversion_required = true;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = false;
      /// Default minification filter.
      static const GLint minification_filter = GL_NEAREST_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_NEAREST;
    };

    /// Properties of COMPLEX texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::COMPLEX> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX>
    {
      /// Internal pixel format (double 32-bit float channels).
      static const GLenum internal_format = GL_RG32F;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RG;
      /// External pixel type (@c std::complex<float>).
      static const GLint external_type = GL_FLOAT;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::COMPLEX;
      /// Bio-Formats type matches the GL type exactly.
      static const bool conversion_required = false;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = true;
      /// Default minification filter.
      static const GLint minification_filter = GL_LINEAR_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_LINEAR;
    };

    /// Properties of DOUBLECOMPLEX texels.
    template<>
    struct TexelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX> :
      public ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX>
    {
      /// Internal pixel format (double 32-bit float channels; note precision loss).
      static const GLenum internal_format = GL_RG32F;
      /// External pixel format (single channel).
      static const GLenum external_format = GL_RG;
      /// External pixel type (@c std::complex<double>).
      static const GLint external_type = GL_DOUBLE;
      /// External pixel format fallback.
      static const ::ome::xml::model::enums::PixelType::enum_value fallback_pixeltype = ome::xml::model::enums::PixelType::COMPLEX;
      /// Bio-Formats type matches the GL type exactly.
      static const bool conversion_required = false;
      /// Pixel values are automatically normalized by GL.
      static const bool normalization_required = true;
      /// Default minification filter.
      static const GLint minification_filter = GL_LINEAR_MIPMAP_LINEAR;
      /// Default magnification filter.
      static const GLint magnification_filter = GL_LINEAR;
    };

    /**
     * Get the default internal (GL) pixel format of a PixelType.
     *
     * @param pixeltype the PixelType to query.
     *
     * @returns the GL internal format.
     */
    GLenum
    textureInternalFormat(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Get a fallback internal (GL) pixel format.
     *
     * The fallback format will have the same channel count as the
     * specified format, lower quality (smaller size) or different
     * type (int rather than float).
     *
     * @param format the format which is not suitable.
     * @returns a downgraded format compared with the specified format.
     */
    GLenum
    textureInternalFormatFallback(GLenum format);

    /**
     * Get the default external (GL) pixel format of a PixelType.
     *
     * @param pixeltype the PixelType to query.
     *
     * @returns the GL external format.
     */
    GLenum
    textureExternalFormat(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Get the default external (GL) type of a PixelType.
     *
     * @param pixeltype the PixelType to query.
     *
     * @returns the GL external type.
     */
    GLint
    textureExternalType(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Get a fallback pixel type.
     *
     * The fallback format will have a lower quality (smaller size) or
     * different type (int rather than float).  If no fallback is
     * available, the same type will be returned.
     *
     * @param pixeltype the PixelType to query.
     * @returns a downgraded PixelType.
     */
    ::ome::xml::model::enums::PixelType
    texturePixelTypeFallback(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Check if type conversion is required.
     *
     * Conversion will be required if the GL type does not
     * match the corresponding PixelType language type.
     *
     * @param pixeltype the PixelType to query.
     * @returns @c true if conversion is required, @c false otherwise.
     */
    bool
    textureConversionRequired(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Check if normalization is required.
     *
     * Normalization will be required if the GL type is not
     * automatically normalized.  For example, this is usually
     * automatic for integer types when using a floating point
     * internal format, but will be required when using a floating
     * point data, e.g. in a fragment shader, or before setting the
     * texture.
     *
     * @param pixeltype the PixelType to query.
     * @returns @c true if normalization is required, @c false otherwise.
     */
    bool
    textureNormalizationRequired(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Get the default minification filter.
     *
     * @param pixeltype the PixelType to query.
     * @returns the default minification filter.
     */
    GLint
    textureMinificationFilter(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Get the default magnification filter.
     *
     * @param pixeltype the PixelType to query.
     * @returns the default magnification filter.
     */
    GLint
    textureMagnificationFilter(::ome::xml::model::enums::PixelType pixeltype);

  }
}

#endif // OME_QTWIDGETS_TEXELPROPERTIES_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2014 Open Microscopy Environment:
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

#ifndef OME_BIOFORMATS_PIXELPROPERTIES_H
#define OME_BIOFORMATS_PIXELPROPERTIES_H

#include <complex>

#include <ome/compat/cstdint.h>
#include <ome/compat/endian.h>

#include <ome/bioformats/Types.h>

#include <ome/xml/model/enums/PixelType.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Map a given PixelPropertiesType enum to the corresponding language types.
     */
    template<int>
    struct PixelProperties;

    /**
     * Properties common to all pixel types.
     */
    template<class P>
    struct PixelPropertiesBase
    {
      /**
       * Get size of pixel type, in bytes.
       * @returns pixel size, in bytes.
       */
      static pixel_size_type
      pixel_byte_size()
      {
        return sizeof(typename P::native_type);
      }

      /**
       * Get size of pixel type, in bits.
       * @returns pixel size, in bits.
       */
      static pixel_size_type
      pixel_bit_size()
      {
        return pixel_byte_size() * 8;
      }
    };

    /// Properties of INT8 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::INT8> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::INT8> >
    {
      /// Pixel type (big endian).
      typedef boost::endian::big_int8_t big_type;
      /// Pixel type (little endian).
      typedef boost::endian::little_int8_t little_type;
      /// Pixel type (native endian).
      typedef boost::endian::native_int8_t native_type;

      /// This pixel type is signed.
      static const bool is_signed = true;
      /// This pixel type is not floating point.
      static const bool is_floating = false;
      /// This pixel type is not complex.
      static const bool is_complex = false;
    };

    /// Properties of INT16 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::INT16> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::INT16> >
    {
      /// Pixel type (big endian).
      typedef boost::endian::big_int16_t big_type;
      /// Pixel type (little endian).
      typedef boost::endian::little_int16_t little_type;
      /// Pixel type (native endian).
      typedef boost::endian::native_int16_t native_type;

      /// This pixel type is signed.
      static const bool is_signed = true;
      /// This pixel type is not floating point.
      static const bool is_floating = false;
      /// This pixel type is not complex.
      static const bool is_complex = false;
    };

    /// Properties of INT32 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::INT32> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::INT32> >
    {
      /// Pixel type (big endian).
      typedef boost::endian::big_int32_t big_type;
      /// Pixel type (little endian).
      typedef boost::endian::little_int32_t little_type;
      /// Pixel type (native endian).
      typedef boost::endian::native_int32_t native_type;

      /// This pixel type is signed.
      static const bool is_signed = true;
      /// This pixel type is not floating point.
      static const bool is_floating = false;
      /// This pixel type is not complex.
      static const bool is_complex = false;
    };

    /// Properties of UINT8 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::UINT8> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::UINT8> >
    {
      /// Pixel type (big endian).
      typedef boost::endian::big_uint8_t big_type;
      /// Pixel type (little endian).
      typedef boost::endian::little_uint8_t little_type;
      /// Pixel type (native endian).
      typedef boost::endian::native_uint8_t native_type;

      /// This pixel type is not signed.
      static const bool is_signed = false;
      /// This pixel type is not floating point.
      static const bool is_floating = false;
      /// This pixel type is not complex.
      static const bool is_complex = false;
    };

    /// Properties of UINT16 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::UINT16> :
      public PixelPropertiesBase<struct PixelProperties< ::ome::xml::model::enums::PixelType::UINT16> >
    {
      /// Pixel type (big endian).
      typedef boost::endian::big_uint16_t big_type;
      /// Pixel type (little endian).
      typedef boost::endian::little_uint16_t little_type;
      /// Pixel type (native endian).
      typedef boost::endian::native_uint16_t native_type;

      /// This pixel type is not signed.
      static const bool is_signed = false;
      /// This pixel type is not floating point.
      static const bool is_floating = false;
      /// This pixel type is not complex.
      static const bool is_complex = false;
    };

    /// Properties of UINT32 pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::UINT32> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::UINT32> >
    {
      /// Pixel type (big endian).
      typedef boost::endian::big_uint32_t big_type;
      /// Pixel type (little endian).
      typedef boost::endian::little_uint32_t little_type;
      /// Pixel type (native endian).
      typedef boost::endian::native_uint32_t native_type;

      /// This pixel type is not signed.
      static const bool is_signed = false;
      /// This pixel type is not floating point.
      static const bool is_floating = false;
      /// This pixel type is not complex.
      static const bool is_complex = false;
    };

    /// Properties of FLOAT pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::FLOAT> >
    {
      /// Pixel type (big endian).
      typedef boost::endian::big_float32_t big_type;
      /// Pixel type (little endian).
      typedef boost::endian::little_float32_t little_type;
      /// Pixel type (native endian).
      typedef float native_type;

      /// This pixel type is signed.
      static const bool is_signed = true;
      /// This pixel type is floating point.
      static const bool is_floating = true;
      /// This pixel type is not complex.
      static const bool is_complex = false;
    };

    /// Properties of DOUBLE pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLE> >
    {
      /// Pixel type (big endian).
      typedef boost::endian::big_float64_t big_type;
      /// Pixel type (little endian).
      typedef boost::endian::little_float64_t little_type;
      /// Pixel type (native endian).
      typedef double native_type;

      /// This pixel type is signed.
      static const bool is_signed = true;
      /// This pixel type is floating point.
      static const bool is_floating = true;
      /// This pixel type is not complex.
      static const bool is_complex = false;
    };

    /// Properties of BIT pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::BIT> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::BIT> >
    {
      /// Pixel type (big endian).
      typedef bool big_type;
      /// Pixel type (little endian).
      typedef bool little_type;
      /// Pixel type (native endian).
      typedef bool native_type;

      /// This pixel type is not signed.
      static const bool is_signed = false;
      /// This pixel type is not floating point.
      static const bool is_floating = false;
      /// This pixel type is not complex.
      static const bool is_complex = false;
    };

    /// Properties of COMPLEX pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::COMPLEX> >
    {
      /// Pixel type (big endian).
      typedef std::complex<boost::endian::big_float32_t> big_type;
      /// Pixel type (little endian).
      typedef std::complex<boost::endian::little_float32_t> little_type;
      /// Pixel type (native endian).
      typedef std::complex<float> native_type;

      /// This pixel type is signed.
      static const bool is_signed = true;
      /// This pixel type is floating point.
      static const bool is_floating = true;
      /// This pixel type is complex.
      static const bool is_complex = true;
    };

    /// Properties of DOUBLECOMPLEX pixels.
    template<>
    struct PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX> :
      public PixelPropertiesBase<PixelProperties< ::ome::xml::model::enums::PixelType::DOUBLECOMPLEX> >
    {
      /// Pixel type (big endian).
      typedef std::complex<boost::endian::big_float64_t> big_type;
      /// Pixel type (little endian).
      typedef std::complex<boost::endian::little_float64_t> little_type;
      /// Pixel type (native endian).
      typedef std::complex<double> native_type;

      /// This pixel type is signed.
      static const bool is_signed = true;
      /// This pixel type is floating point.
      static const bool is_floating = true;
      /// This pixel type is complex.
      static const bool is_complex = true;
    };

    /// Endianness.
    enum EndianType
      {
        BIG,    ///< Big endian.
        LITTLE, ///< Little endian.
        NATIVE  ///< Native endian.
      };

    /**
     * Map the given PixelPropertiesType and Endian enums to the
     * corresponding endian-specific language type.
     */
    template<int, int>
    struct PixelEndianProperties;

    /// Properties of big endian pixels.
    template<int P>
    struct PixelEndianProperties<P, BIG>
    {
      /// Pixel type (big endian).
      typedef typename PixelProperties<P>::big_type type;
    };

    /// Properties of little endian pixels.
    template<int P>
    struct PixelEndianProperties<P, LITTLE>
    {
      /// Pixel type (little endian).
      typedef typename PixelProperties<P>::little_type type;
    };

    /// Properties of native endian pixels.
    template<int P>
    struct PixelEndianProperties<P, NATIVE>
    {
      /// Pixel type (native endian).
      typedef typename PixelProperties<P>::native_type type;
    };

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

    /**
     * Get the size of a PixelType, in bytes.
     *
     * @param pixeltype the PixelType to query.
     *
     * @returns the size, in bytes
     */
    pixel_size_type
    bytesPerPixel(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Get the size of a PixelType, in bits.
     *
     * @param pixeltype the PixelType to query.
     *
     * @returns the size, in bits
     */
    pixel_size_type
    bitsPerPixel(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Check whether a PixelType is signed.
     *
     * @param pixeltype the PixelType to query.
     *
     * @returns @c true if signed, @c false otherwise.
     */
    bool
    isSigned(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Check whether a PixelType is floating point.
     *
     * @param pixeltype the PixelType to query.
     *
     * @returns @c true if floating point, @c false otherwise.
     */
    bool
    isFloating(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Check whether a PixelType is complex.
     *
     * @param pixeltype the PixelType to query.
     *
     * @returns @c true if complex, @c false otherwise.
     */
    bool
    isComplex(::ome::xml::model::enums::PixelType pixeltype);

    /**
     * Determine a likely pixel type from its the storage size in bytes.
     *
     * Note that the BIT type will never be returned due to having the
     * same storage size as other types.
     *
     * @param bytes the storage size in bytes.
     * @param signed @c true if signed, @c false if unsigned.
     * @param floating @c true if floating point, @c false otherwise.
     * @param complex :c true if complex, @c false otherwise.
     * @returns the corresponding pixel type.
     * @throws if no pixel type was identified or the parameters are
     * invalid.
     */
    ::ome::xml::model::enums::PixelType
    pixelTypeFromBytes(pixel_size_type bytes,
                       bool            is_signed = false,
                       bool            is_floating = false,
                       bool            is_complex = false);

    /**
     * Determine a likely pixel type from its the storage size in bits.
     *
     * Note that the BIT type will never be returned due to having the
     * same storage size as other types.
     *
     * @param bytes the storage size in bits.
     * @param signed @c true if signed, @c false if unsigned.
     * @param floating @c true if floating point, @c false otherwise.
     * @param complex :c true if complex, @c false otherwise.
     * @returns the corresponding pixel type.
     * @throws if no pixel type was identified or the parameters are
     * invalid.
     */
    ::ome::xml::model::enums::PixelType
    pixelTypeFromBits(pixel_size_type bytes,
                      bool            is_signed = false,
                      bool            is_floating = false,
                      bool            is_complex = false);

  }
}

#endif // OME_BIOFORMATS_PIXELPROPERTIES_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

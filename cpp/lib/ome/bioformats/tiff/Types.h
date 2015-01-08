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

#ifndef OME_BIOFORMATS_TIFF_TYPES_H
#define OME_BIOFORMATS_TIFF_TYPES_H

#include <ome/bioformats/Types.h>

#include <ome/compat/cstdint.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      /// IFD index.
      typedef uint16_t directory_index_type;

      /// IFD offset.
      typedef uint64_t offset_type;

      /// Tag number.
      typedef unsigned int tag_type;

      /// Tag types.
      enum Type
        {
          TYPE_NONE = 0,        ///< Placeholder.
          TYPE_BYTE = 1,        ///< 8-bit unsigned integer.
          TYPE_ASCII = 2,       ///< 8-bit bytes with NUL terminator.
          TYPE_SHORT = 3,       ///< 16-bit unsigned integer.
          TYPE_LONG = 4,        ///< 32-bit unsigned integer.
          TYPE_RATIONAL = 5,    ///< 64-bit unsigned fraction.
          TYPE_SBYTE = 6,       ///< 8-bit signed integer.
          TYPE_UNDEFINED = 7,   ///< 8-bit untyped data.
          TYPE_SSHORT = 8,      ///< 16-bit signed integer.
          TYPE_SLONG = 9,       ///< 32-bit signed integer.
          TYPE_SRATIONAL = 10,  ///< 64-bit signed fraction.
          TYPE_FLOAT = 11,      ///< 32-bit IEEE floating point.
          TYPE_DOUBLE = 12,     ///< 64-bit IEEE floating point.
          TYPE_IFD = 13,        ///< 32-bit unsigned integer (offset).
          TYPE_LONG8 = 16,      ///< BigTIFF 64-bit unsigned integer.
          TYPE_SLONG8 = 17,     ///< BigTIFF 64-bit signed integer.
          TYPE_IFD8 = 18        ///< BigTIFF 64-bit unsigned integer (offset).
        };

      /// Compression technique.
      enum Compression
        {
          COMPRESSION_NONE = 1,            ///< No compression.
          COMPRESSION_CCITTRLE = 2,        ///< CCITT modified Huffman RLE.
          COMPRESSION_CCITTFAX3 = 3,       ///< CCITT Group 3 fax encoding (deprecated).
          COMPRESSION_CCITT_T4 = 3,        ///< CCITT T.4 (TIFF 6 name).
          COMPRESSION_CCITTFAX4 = 4,       ///< CCITT Group 4 fax encoding (deprecated).
          COMPRESSION_CCITT_T6 = 4,        ///< CCITT T.6 (TIFF 6 name).
          COMPRESSION_LZW = 5,             ///< Lempel-Ziv & Welch.
          COMPRESSION_OJPEG = 6,           ///< JPEG (deprecated).
          COMPRESSION_JPEG = 7,            ///< JPEG DCT compression.
          COMPRESSION_ADOBE_DEFLATE = 8,   ///< Deflate compression (Adobe).
          COMPRESSION_T85 = 9,             ///< TIFF/FX T.85 JBIG compression.
          COMPRESSION_T43 = 10,            ///< TIFF/FX T.43 colour by layered JBIG compression.
          COMPRESSION_NEXT = 32766,        ///< NeXT 2-bit RLE.
          COMPRESSION_CCITTRLEW = 32771,   ///< 1 w/ word alignment.
          COMPRESSION_PACKBITS = 32773,    ///< Macintosh RLE.
          COMPRESSION_THUNDERSCAN = 32809, ///< ThunderScan RLE.
          /// codes 32895-32898 are reserved for ANSI IT8 TIFF/IT <dkelly@apago.com).
          COMPRESSION_IT8CTPAD = 32895,    ///< IT8 CT w/padding.
          COMPRESSION_IT8LW = 32896,       ///< IT8 Linework RLE.
          COMPRESSION_IT8MP = 32897,       ///< IT8 Monochrome picture.
          COMPRESSION_IT8BL = 32898,       ///< IT8 Binary line art.
          /// compression codes 32908-32911 are reserved for Pixar.
          COMPRESSION_PIXARFILM = 32908,   ///< Pixar companded 10bit LZW.
          COMPRESSION_PIXARLOG = 32909,    ///< Pixar companded 11bit ZIP.
          COMPRESSION_DEFLATE = 32946,     ///< Deflate compression.
          /// compression code 32947 is reserved for Oceana Matrix <dev@oceana.com>.
          COMPRESSION_DCS = 32947,         ///< Kodak DCS encoding.
          COMPRESSION_JBIG = 34661,        ///< ISO JBIG.
          COMPRESSION_SGILOG = 34676,      ///< SGI Log Luminance RLE.
          COMPRESSION_SGILOG24 = 34677,    ///< SGI Log 24-bit packed.
          COMPRESSION_JP2000 = 34712,      ///< Leadtools JPEG2000.
          COMPRESSION_LZMA = 34925         ///< LZMA2.
        };

      /// Extra components description.
      enum ExtraSamples
        {
          UNSPECIFIED = 0,       ///< Unspecified data.
          ASSOCIATED_ALPHA = 1,  ///< Associated alpha data with pre-multiplied color.
          UNASSOCIATED_ALPHA = 2 ///< Unassociated alpha data.
        };

      /// Fill order.
      enum FillOrder
        {
          MSB_TO_LSB = 1, ///< Most significant bit to least significant bit.
          LSB_TO_MSB = 2  ///< Least significant bit to most significant bit.
        };

      /// Image orientation.
      enum Orientation
        {
          TOP_LEFT     = 1, ///< Row 0 top, column 0 lhs.
          TOP_RIGHT    = 2, ///< Row 0 top, column 0 rhs.
          BOTTOM_RIGHT = 3, ///< Row 0 bottom, column 0 rhs.
          BOTTOM_LEFT  = 4, ///< Row 0 bottom, column 0 lhs.
          LEFT_TOP     = 5, ///< Row 0 lhs, column 0 top.
          RIGHT_TOP    = 6, ///< Row 0 rhs, column 0 top.
          RIGHT_BOTTOM = 7, ///< Row 0 rhs, column 0 bottom.
          LEFT_BOTTOM  = 8  ///< Row 0 lhs, column 0 bottom.
        };

      /// Photometric interpretation of pixel data.
      enum PhotometricInterpretation
        {
          MIN_IS_WHITE = 0,  ///< Minimum value is white.
          MIN_IS_BLACK = 1,  ///< Minimum value is black.
          RGB = 2,           ///< RGB subchannels.
          PALETTE = 3,       ///< Indexed colour with colormap.
          MASK = 4,          ///< Mask.
          SEPARATED = 5,     ///< Color separations.
          YCBCR = 6,         ///< CCIR 601.
          CIELAB = 8,        ///< 1976 CIE L*a*b*.
          ICCLAB = 9,        ///< ICC L*a*b*.
          ITULAB = 10,       ///< ITU L*a*b*.
          CFA_ARRAY = 32803, ///< Color Filter Array.
          LOGL = 32844,      ///< CIE log2(L).
          LOGLUV = 32845     ///< CIE log2(L) (u',v').
        };

      /// Planar configuration of samples.
      enum PlanarConfiguration
        {
          CONTIG = 1,  ///< Samples are contiguous (chunky).
          SEPARATE = 2 ///< Samples are separate (planar).
        };

      /// Prediction scheme.
      enum Predictor
        {
          NONE = 1,          ///< No prediction scheme used.
          HORIZONTAL = 2,    ///< Horizontal differencing.
          FLOATING_POINT = 3 ///< Floating point.
        };

      /// Sample format.
      enum SampleFormat
        {
          UNSIGNED_INT = 1, ///< Unsigned integer.
          SIGNED_INT = 2,   ///< Signed integer.
          FLOAT = 3,        ///< IEEE floating point.
          VOID = 4,         ///< Void.
          COMPLEX_INT = 5,  ///< Complex integer.
          COMPLEX_FLOAT = 6 ///< Complex IEEE floating point.
        };

      /// Thresholding scheme.
      enum Threshholding
        {
          BILEVEL = 1,     ///< Black and white.
          HALFTONE = 2,    ///< Dithered.
          ERRORDIFFUSE = 3 ///< Error diffusion (Floyd-Steinberg).
        };

      /// YCbCr positioning.
      enum YCbCrPosition
        {
          CENTERED = 1, ///< Centered.
          COSITED = 2   ///< Co-sited.
        };

      /// Type of tile.
      enum TileType
        {
          STRIP, ///< Strips.
          TILE   ///< Tiles.
        };

      /**
       * A rectangular region.
       *
       * The region is specified by top-left (x,y) coordinates plus
       * width and height.
       */
      struct PlaneRegion
      {
        /// The @c X coordinate of the upper-left corner of the region.
        dimension_size_type x;
        /// The @c Y coordinate of the upper-left corner of the region.
        dimension_size_type y;
        /// The width of the region.
        dimension_size_type w;
        /// The height of the region.
        dimension_size_type h;

      public:
        /**
         * Default construct.
         *
         * By default the region has zero width and height.
         */
        PlaneRegion():
          x(0),
          y(0),
          w(0),
          h(0)
        {}

        /**
         * Is the region valid?
         *
         * @returns @c true if the region has a nonzero width and
         * height, @c false otherwise.
         */
        bool
        valid() const {
          return w && h;
        }

        /**
         * Construct from coordinates, width and height.
         *
         * @param x the @c X coordinate of the upper-left corner of the region.
         * @param y the @c Y coordinate of the upper-left corner of the region.
         * @param w the width of the region.
         * @param h the height of the region.
         */
         PlaneRegion(dimension_size_type x,
                     dimension_size_type y,
                     dimension_size_type w,
                     dimension_size_type h):
          x(x),
          y(y),
          w(w),
          h(h)
        {}

        /**
         * Get area.
         *
         * @returns the covered area.
         */
        dimension_size_type
        area() const
        {
          return w * h;
        }
      };

      /**
       * Intersect two regions.
       *
       * If the regions do not intersect, a default-constructed region
       * of zero size will be returned.
       *
       * @param a the first region.
       * @param b the second region.
       * @returns the intersection of the two regions.
       */
      inline
      PlaneRegion
      operator&(const PlaneRegion& a,
                const PlaneRegion& b)
      {
        dimension_size_type l1 = a.x;
        dimension_size_type r1 = a.x + a.w;

        dimension_size_type l2 = b.x;
        dimension_size_type r2 = b.x + b.w;

        if (l1 > r2 || l2 > r1)
          return PlaneRegion();

        dimension_size_type t1 = a.y;
        dimension_size_type b1 = a.y + a.h;

        dimension_size_type t2 = b.y;
        dimension_size_type b2 = b.y + b.h;

        if (t1 > b2 || t2 > b1)
          return PlaneRegion();

        dimension_size_type il = std::max(l1, l2);
        dimension_size_type ir = std::min(r1, r2);
        dimension_size_type it = std::max(t1, t2);
        dimension_size_type ib = std::min(b1, b2);

        return PlaneRegion(il, it, ir-il, ib-it);
      }

      /**
       * Combine (union) two regions.
       *
       * If the regions do not abut about a common edge, a
       * default-constructed region of zero size will be returned.
       *
       * @param a the first region.
       * @param b the second region.
       * @returns the union of the two regions.
       */
      inline
      PlaneRegion
      operator|(const PlaneRegion& a,
                const PlaneRegion& b)
      {
        dimension_size_type l1 = a.x;
        dimension_size_type r1 = a.x + a.w;

        dimension_size_type l2 = b.x;
        dimension_size_type r2 = b.x + b.w;

        dimension_size_type t1 = a.y;
        dimension_size_type b1 = a.y + a.h;

        dimension_size_type t2 = b.y;
        dimension_size_type b2 = b.y + b.h;

        if (l1 == l2 && r1 == r2 &&
            (t1 == b2 || t2 == b1)) // union along top or bottom edges
          {
            dimension_size_type it = std::min(t1, t2);
            dimension_size_type ib = std::max(b1, b2);
            return PlaneRegion(l1, it, r1-l1, ib-it);
          }
        else if (t1 == t2 && b1 == b2 &&
                 (l1 == r2 || l2 == r1)) // union along left or right edges
          {
            dimension_size_type il = std::min(l1, l2);
            dimension_size_type ir = std::max(r1, r2);
            return PlaneRegion(il, t1, ir-il, b1-t1);
          }
        return PlaneRegion();
      }

        /**
         * Output PlaneRegion to output stream.
         *
         * @param os the output stream.
         * @param region the PlaneRegion to output.
         * @returns the output stream.
         */
        template<class charT, class traits>
        inline std::basic_ostream<charT,traits>&
        operator<< (std::basic_ostream<charT,traits>& os,
                    const PlaneRegion& region)
        {
          return os << "x=" << region.x
                    << " y=" << region.y
                    << " w=" << region.w
                    << " h=" << region.h;
        }

    }
  }
}

#endif // OME_BIOFORMATS_TIFF_TYPES_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

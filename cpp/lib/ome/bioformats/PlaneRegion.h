/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2015 Open Microscopy Environment:
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

#ifndef OME_BIOFORMATS_PLANEREGION_H
#define OME_BIOFORMATS_PLANEREGION_H

#include <ome/bioformats/Types.h>

#include <ome/xml/model/enums/PixelType.h>

namespace ome
{
  namespace bioformats
  {

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

#endif // OME_BIOFORMATS_PLANEREGION_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

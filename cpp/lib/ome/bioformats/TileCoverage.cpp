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

#include <ome/common/config.h>

#include <boost/geometry.hpp>
#include <boost/geometry/geometries/point.hpp>
#include <boost/geometry/geometries/box.hpp>
#ifdef OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
# include <boost/geometry/index/rtree.hpp>
#endif

#include <algorithm>
#include <list>

#include <ome/bioformats/Types.h>
#include <ome/bioformats/TileCoverage.h>

using ome::bioformats::dimension_size_type;
namespace geom = boost::geometry;
#ifdef OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
namespace geomi = boost::geometry::index;
#endif // OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP

typedef geom::model::point<dimension_size_type, 2, geom::cs::cartesian> point;
typedef geom::model::box<point> box;

namespace
{

  box
  box_from_region(const ::ome::bioformats::PlaneRegion &r)
  {
    return box(point(r.x, r.y),
               point(r.x + r.w, r.y + r.h));
  }

  ::ome::bioformats::PlaneRegion
  region_from_box(const box& b)
  {
    const point& bmin = b.min_corner();
    const point& bmax = b.max_corner();
    return ::ome::bioformats::PlaneRegion
      (bmin.get<0>(), bmin.get<1>(),
       bmax.get<0>() - bmin.get<0>(), bmax.get<1>() - bmin.get<1>());
  }

#ifndef OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
  // Compare box
  struct BoxCompare
  {
    const box& cmp;

    BoxCompare(const box& cmp):
      cmp(cmp)
    {}

    bool
    operator()(const box& v) const
    {
      const point& amin = cmp.min_corner();
      const point& amax = cmp.max_corner();
      const point& bmin = v.min_corner();
      const point& bmax = v.max_corner();

      return (amin.get<0>() == bmin.get<0>() &&
              amin.get<1>() == bmin.get<1>() &&
              amax.get<0>() == bmax.get<0>() &&
              amax.get<1>() == bmax.get<1>());
    }
  };
#endif // ! OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP

}

namespace ome
{
  namespace bioformats
  {

    /**
     * Internal implementation details of TileCoverage.
     */
    class TileCoverage::Impl
    {
    public:
      /// Region coverage stored as box ranges.
#ifdef OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
      geomi::rtree<box, geomi::quadratic<16> > rtree;
#else // ! OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
      std::list<box> rtree;
#endif // OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP

      /**
       * Constructor.
       */
      Impl():
        rtree()
      {
      }

      /// Destructor.
      ~Impl()
      {
      }

      /**
       * Boxes intersecting with the specified box.
       *
       * @param b the box to intersect with.
       * @returns a list of intersecting boxes.
       */
      std::vector<box>
      intersecting(const box& b)
      {
        std::vector<box> results;

#ifdef OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
        rtree.query(geomi::intersects(b),
                    std::back_inserter(results));
#else // ! OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
        // Slower linear search in the absence of rtree.
        PlaneRegion region(region_from_box(b));

        for (std::list<box>::const_iterator i = rtree.begin();
             i != rtree.end();
             ++i)
          {
            PlaneRegion test(region_from_box(*i));
            PlaneRegion intersection = region & test;

            if (intersection.w && intersection.h)
              results.push_back(*i);
            else
              {
                // Also include non-intersecting but touching
                // regions for coalescing, both edges and corners.
                // These are included by the R*Tree box intersection
                // search and needed for the region coalescing logic.
                if ((region.x + region.w == test.x ||
                     test.x + test.w == region.x) &&
                    ((region.y >= test.y &&
                      region.y <= test.y + test.h) ||
                     (region.y + region.h >= test.y &&
                      region.y +region.h <= test.y + test.h)))
                  results.push_back(*i);
                else if ((region.y + region.h == test.y ||
                          test.y + test.h == region.y) &&
                         ((region.x >= test.x &&
                           region.x <= test.x + test.w) ||
                          (region.x + region.w >= test.x &&
                           region.x +region.w <= test.x + test.w)))
                  results.push_back(*i);
              }
          }
#endif // OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP

        return results;
      }
    };

    TileCoverage::TileCoverage():
      impl(ome::compat::shared_ptr<Impl>(new Impl()))
    {
    }

    TileCoverage::~TileCoverage()
    {
    }

    bool
    TileCoverage::insert(const PlaneRegion& region,
                         bool               coalesce)
    {
      bool inserted = false;

      if (coverage(region) == 0)
        {
          box b(box_from_region(region));

          if (!coalesce)
            {
#ifdef OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
              impl->rtree.insert(b);
#else // ! OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
              impl->rtree.push_back(b);
#endif // OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
              inserted = true;
            }
          else // Merge adjacent regions
            {
              // Merged regions to remove
              std::vector<box> remove;

              PlaneRegion merged_region = region;

              // Merge any adjacent regions and then loop and retry
              // with the resulting enlarged region until no further
              // merges are possible
              bool merged = true;
              while(merged)
                {
                  merged = false;
                  std::vector<box> results = impl->intersecting(b);

                  for(std::vector<box>::const_iterator i = results.begin();
                      i != results.end();
                      ++i)
                    {
                      PlaneRegion test(region_from_box(*i));
                      PlaneRegion m = merged_region | test;

                      if (m.valid())
                        {
                          merged_region = m;
                          remove.push_back(*i);
                          merged = true;
                        }
                    }
                }

              // Remove merged regions
              if (!remove.empty())
                {
                  for (std::vector<box>::const_iterator r = remove.begin();
                       r != remove.end();
                       ++r)
                    {
#ifdef OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
                      impl->rtree.remove(*r);
#else // ! OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
                      std::list<box>::iterator ib = std::find_if(impl->rtree.begin(), impl->rtree.end(), BoxCompare(*r));
                      if (ib != impl->rtree.end())
                        impl->rtree.erase(ib);
#endif // OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
                    }
                }

              // Insert merged region
              box mb(box_from_region(merged_region));

#ifdef OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
              impl->rtree.insert(mb);
#else // ! OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
              impl->rtree.push_back(mb);
#endif // OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
              inserted = true;
            }
        }

      return inserted;
    }

    bool
    TileCoverage::remove(const PlaneRegion& region)
    {
      box b(box_from_region(region));

#ifdef OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
      return (impl->rtree.remove(b));
#else // ! OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
      bool found = false;
      std::list<box>::iterator i = std::find_if(impl->rtree.begin(), impl->rtree.end(), BoxCompare(b));
      if (i != impl->rtree.end())
        {
          impl->rtree.erase(i);
          found = true;
        }
      return found;
#endif // OME_HAVE_BOOST_GEOMETRY_INDEX_RTREE_HPP
    }

    dimension_size_type
    TileCoverage::size() const
    {
      return impl->rtree.size();
    }

    void
    TileCoverage::clear()
    {
      impl->rtree.clear();
    }

    dimension_size_type
    TileCoverage::coverage(const PlaneRegion& region) const
    {
      box b(box_from_region(region));
      std::vector<box> results = impl->intersecting(b);

      dimension_size_type area = 0;
      for(std::vector<box>::const_iterator i = results.begin();
          i != results.end();
          ++i)
        {
          PlaneRegion test(region_from_box(*i));
          PlaneRegion intersection = region & test;

          if (intersection.valid())
            area += intersection.area();
        }

      return area;
    }

    bool
    TileCoverage::covered(const PlaneRegion& region) const
    {
      return (region.w * region.h) == coverage(region);
    }

  }
}

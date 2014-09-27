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

#include <boost/geometry.hpp>
#include <boost/geometry/geometries/point.hpp>
#include <boost/geometry/geometries/box.hpp>
#include <boost/geometry/geometries/polygon.hpp>
#include <boost/geometry/multi/geometries/multi_polygon.hpp>
#include <boost/geometry/index/rtree.hpp>
#include <boost/geometry/io/wkt/wkt.hpp>

#include <vector>

#include <ome/bioformats/Types.h>
#include <ome/bioformats/tiff/Types.h>
#include <ome/bioformats/tiff/TileCoverage.h>

using ome::bioformats::dimension_size_type;
namespace geom = boost::geometry;
namespace geomi = boost::geometry::index;

typedef geom::model::point<dimension_size_type, 2, geom::cs::cartesian> point;
typedef geom::model::box<point> box;
typedef geom::model::polygon<point> polygon;
typedef geom::model::multi_polygon<polygon> multi_polygon;

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      /**
       * Internal implementation details of TileCoverage.
       */
      class TileCoverage::Impl
      {
      public:
        /// R*Tree to cache regions as boxes.
        geomi::rtree<box, geomi::quadratic<16> > rtree;

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
          rtree.query(geomi::intersects(b),
                      std::back_inserter(results));

          return results;
        }
      };

      TileCoverage::TileCoverage():
        impl(std::shared_ptr<Impl>(new Impl()))
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
        box b(point(region.x, region.y),
              point(region.x + region.w, region.y + region.h));

        if (coverage(region) == 0)
          {
            if (!coalesce)
              {
                box b(point(region.x, region.y),
                      point(region.x + region.w, region.y + region.h));
                impl->rtree.insert(b);
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
                        const point& min = i->min_corner();
                        const point& max = i->max_corner();
                        PlaneRegion test(min.get<0>(), min.get<1>(),
                                         max.get<0>() - min.get<0>(), max.get<1>() - min.get<1>());

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
                      impl->rtree.remove(*r);
                  }

                // Insert merged region
                box mb(point(merged_region.x, merged_region.y),
                       point(merged_region.x + merged_region.w, merged_region.y + merged_region.h));

                impl->rtree.insert(mb);
                inserted = true;
              }
          }

        return inserted;
      }

      bool
      TileCoverage::remove(const PlaneRegion& region)
      {
        box b(point(region.x, region.y),
              point(region.x + region.w, region.y + region.h));

        return (impl->rtree.remove(b));
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
        box b(point(region.x, region.y),
              point(region.x + region.w, region.y + region.h));
        std::vector<box> results = impl->intersecting(b);

        dimension_size_type area = 0;
        for(std::vector<box>::const_iterator i = results.begin();
            i != results.end();
            ++i)
          {
            const point& min = i->min_corner();
            const point& max = i->max_corner();
            PlaneRegion test(min.get<0>(), min.get<1>(),
                             max.get<0>() - min.get<0>(), max.get<1>() - min.get<1>());

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
}

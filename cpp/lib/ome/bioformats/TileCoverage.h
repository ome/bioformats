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

#ifndef OME_BIOFORMATS_TILECOVERAGE_H
#define OME_BIOFORMATS_TILECOVERAGE_H

#include <ome/bioformats/Types.h>
#include <ome/bioformats/PlaneRegion.h>

#include <ome/compat/memory.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Tile coverage cache.
     *
     * Cache and query covered 2D regions.  Regions are stored in an
     * R*Tree as half-open ranges of [x1,y1..x2,y2).  This may be
     * used, for example, to prevent writing out incomplete tiles
     * and to output tiles in order when used with an accompanying
     * tile cache.
     */
    class TileCoverage
    {
    public:
      /// Constructor.
      TileCoverage();

      /// Destructor.
      virtual ~TileCoverage();

      /**
       * Insert a region into the coverage cache.
       *
       * The insert will only succeed if the region is completely
       * uncovered.  That is, it must not overlap with any currently
       * covered region in the cache.
       *
       * Region coalescing is enabled by default because the overhead
       * of merging adjacent regions is much less than the cost of
       * searching the cache with thousand of tiles.  If filling the
       * cache with aligned tiles of the same size in a consistent
       * order, this should result in a very low region count (less
       * than 5) and hence lookups will be very fast.  It will be
       * necessary to disable coalescing if the regions will
       * subsequently be removed, since region splitting is not
       * implemented.
       *
       * @param region the region to insert.
       * @param coalesce @c true to merge with adjacent tiles, or @c
       * false to ensure the region is kept separated.
       * @returns @c true if the region was inserted, or @c false if
       * not inserted.
       */
      bool
      insert(const PlaneRegion& region,
             bool               coalesce = true);

      /**
       * Remove a region from the coverage cache.
       *
       * A separate region of the exact size of the specified region
       * must exist in the coverage cache or else removal will fail.
       * Disable coalescing if it is preventing removal due to merging
       * adjacent tiles.
       *
       * @param region the region to remove.
       * @returns @c true if the region was removed, or @c false if
       * not removed.
       */
      bool
      remove(const PlaneRegion& region);

      /**
       * Get the number of separate regions in the coverage cache.
       *
       * @returns the number of separate regions.
       */
      dimension_size_type
      size() const;

      /**
       * Clear the coverage cache of all covered regions.
       */
      void
      clear();

      /**
       * Covered area within the region.
       *
       * This is the sum of all covered areas within the specified
       * region.  If the area is completely covered, this value will
       * equal the area of the specified region.
       *
       * @param region the region to check.
       * @returns the covered area.
       */
      dimension_size_type
      coverage(const PlaneRegion& region) const;

      /**
       * Check if a region is completely covered.
       *
       * @param region the region to check.
       * @returns @c true if completely covered, @c false otherwise.
       */
      bool
      covered(const PlaneRegion& region) const;

    protected:
      class Impl;
      /// Private implementation details.
      ome::compat::shared_ptr<Impl> impl;
    };

  }
}

#endif // OME_BIOFORMATS_TILECOVERAGE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

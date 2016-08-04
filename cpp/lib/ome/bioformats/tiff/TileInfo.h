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

#ifndef OME_BIOFORMATS_TIFF_TILEINFO_H
#define OME_BIOFORMATS_TIFF_TILEINFO_H

#include <ome/bioformats/PlaneRegion.h>
#include <ome/bioformats/tiff/Types.h>

#include <ome/compat/memory.h>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      class IFD;

      /**
       * Tile information for an IFD.
       *
       * @note Strips are considered a special case of tiles where the
       * tile width is the image width.
       */
      class TileInfo
      {
      protected:
        friend class IFD;

        /**
         * Constructor.
         *
         * @param ifd the directory the tile belongs to.
         */
        TileInfo(ome::compat::shared_ptr<IFD> ifd);

      public:
        /// Destructor.
        virtual ~TileInfo();

        /**
         * Get the type of a tile.
         *
         * @returns the tile type.
         */
        TileType
        tileType() const;

        /**
         * Get the width of a tile.
         *
         * @returns the width of a tile.
         */
        dimension_size_type
        tileWidth() const;

        /**
         * Get the height of a tile.
         *
         * @returns the height of a tile.
         */
        dimension_size_type
        tileHeight() const;

        /**
         * Get the total number of tiles.
         *
         * @returns the tile count.
         */
        dimension_size_type
        tileCount() const;

        /**
         * Get the total number of rows.
         *
         * @returns the row count.
         */
        dimension_size_type
        tileRowCount() const;

        /**
         * Get the total number of columns.
         *
         * @returns the column count.
         */
        dimension_size_type
        tileColumnCount() const;

        /**
         * Get the buffer size needed to contain a single tile.
         *
         * @returns the buffer size.
         */
        dimension_size_type
        bufferSize() const;

        /**
         * Get the tile index covering the given coordinates.
         *
         * @param x the image column.
         * @param y the image row.
         * @param s the image subchannel.
         * @returns the tile index.
         */
        dimension_size_type
        tileIndex(dimension_size_type x,
                  dimension_size_type y,
                  dimension_size_type s = 0) const;

        /**
         * Get the column index for the given tile index.
         *
         * @param index the tile index.
         * @returns the column index.
         */
        dimension_size_type
        tileColumn(dimension_size_type index) const;

        /**
         * Get the row index for the given tile index.
         *
         * @param index the tile index.
         * @returns the row index.
         */
        dimension_size_type
        tileRow(dimension_size_type index) const;

        /**
         * Get the sample index for the given tile index.
         *
         * @note Only meaningful for planar images.
         *
         * @param index the tile index.
         * @returns the sample index.
         */
        dimension_size_type
        tileSample(dimension_size_type index) const;

        /**
         * Get the region covered by the given tile index.
         *
         * If the tile index is invalid, the region will be of zero
         * size.  The clip region is a bounding box which will be
         * intersected with the tile index to compute the returned
         * region.
         *
         * @note For tiles at the image edges, the region size may
         * differ from the tile size if the image size is not an exact
         * multiple of the tile size.  Clip to the full image size to
         * obtain the usable area.
         *
         * @param index the tile index.
         * @param clip the clip region.
         * @returns the region covered.
         */
        PlaneRegion
        tileRegion(dimension_size_type index,
                   const PlaneRegion& clip) const;

        /**
         * Get the region covered by the given tile index.
         *
         * If the tile index is invalid, the region will be of zero
         * size.
         *
         * @note This method does not take into account tiles which
         * overlap the edge of the image, and will always return the
         * full tile size including areas outside the image.  Use the
         * clip variant of this method to clip tiles to the image
         * edge.
         *
         * @param index the tile index.
         * @returns the region covered.
         */
        PlaneRegion
        tileRegion(dimension_size_type index) const;

        /**
         * Get a list of the tiles covering an image region.
         *
         * @note The tiles may extend outside the specified region,
         * and the same area may be covered by multiple tiles if the
         * samples are planar.
         *
         * @param region the image region to cover.
         * @returns a list of tile indexes.
         */
        std::vector<dimension_size_type>
        tileCoverage(PlaneRegion region) const;

      protected:
        class Impl;
        /// Private implementation details.
        ome::compat::shared_ptr<Impl> impl;
      };

    }
  }
}

#endif // OME_BIOFORMATS_TIFF_TILEINFO_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */

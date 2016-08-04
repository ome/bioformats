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

#include <ome/bioformats/tiff/Field.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/Tags.h>
#include <ome/bioformats/tiff/TileInfo.h>
#include <ome/bioformats/tiff/Sentry.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/Types.h>

#include <ome/internal/config.h>

#include <tiffio.h>

#include <boost/algorithm/string.hpp>

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      /**
       * Internal implementation details of TileInfo.
       */
      class TileInfo::Impl
      {
      public:
        /// Weak reference to the parent IFD.
        ome::compat::weak_ptr<IFD>  ifd;
        /// Whether the image is chunky or planar.
        TileType type;
        /// Width of a tile.
        uint32_t tilewidth;
        /// Height of a tile.
        uint32_t tileheight;
        /// Planar configuration.
        PlanarConfiguration planarconfig;
        /// Sample count.
        uint16_t samples;
        /// Tile count.
        dimension_size_type tilecount;
        /// Tiles per row.
        dimension_size_type nrows;
        /// Tiles per column.
        dimension_size_type ncols;
        /// Tiles per plane.
        dimension_size_type ntiles;
        /// Buffer size for a tile.
        tsize_t buffersize;

        /**
         * Constructor.
         *
         * @param ifd the directory the tile belongs to.
         */
        Impl(ome::compat::shared_ptr<IFD>& ifd):
          ifd(ifd),
          tilewidth(),
          tileheight(),
          planarconfig(),
          samples(),
          tilecount(),
          nrows(),
          ncols(),
          ntiles(),
          buffersize()
        {
          Sentry sentry;
          ::TIFF *tiff = getTIFF();

          // Get basic image metadata.
          uint32_t imagewidth = ifd->getImageWidth();
          uint32_t imageheight = ifd->getImageHeight();
          planarconfig = ifd->getPlanarConfiguration();
          samples = ifd->getSamplesPerPixel();
          tilewidth = ifd->getTileWidth();
          tileheight = ifd->getTileHeight();
          type = ifd->getTileType();

          // Get tile-specific metadata, falling back to
          // strip-specific metadata if not present.
          if (type == TILE)
            {
              tilecount = TIFFNumberOfTiles(tiff);
              buffersize = TIFFTileSize(tiff);
            }
          else
            {
              tilecount = TIFFNumberOfStrips(tiff);
              buffersize = TIFFStripSize(tiff);
            }

          // Compute row and column counts.
          nrows = imagewidth / tileheight;
          if (imagewidth % tileheight)
            ++nrows;
          ncols = imageheight / tilewidth;
          if (imageheight % tilewidth)
            ++ncols;
          ntiles = nrows * ncols;
        }

        /// Destructor.
        ~Impl()
        {
        }

        /**
         * Get the directory this tile belongs to.
         *
         * @returns the directory.
         */
        ome::compat::shared_ptr<IFD>
        getIFD() const
        {
          ome::compat::shared_ptr<IFD> sifd = ome::compat::shared_ptr<IFD>(ifd);
          if (!sifd)
            throw Exception("TileInfo reference to IFD no longer valid");

          return sifd;
        }

        /**
         * Get the TIFF this tile belongs to.
         *
         * @note Needs wrapping in a sentry by the caller.
         *
         * @returns a pointer to the underlying TIFF.
         */
        ::TIFF *
        getTIFF()
        {
          getIFD()->makeCurrent();
          ::TIFF *tiff = reinterpret_cast< ::TIFF *>(getIFD()->getTIFF()->getWrapped());
          return tiff;
        }
      };

      TileInfo::TileInfo(ome::compat::shared_ptr<IFD> ifd):
        impl(ome::compat::shared_ptr<Impl>(new Impl(ifd)))
      {
      }

      TileInfo::~TileInfo()
      {
      }

      TileType
      TileInfo::tileType() const
      {
        return impl->type;
      }

      dimension_size_type
      TileInfo::tileWidth() const
      {
        return impl->tilewidth;
      }

      dimension_size_type
      TileInfo::tileHeight() const
      {
        return impl->tileheight;
      }

      dimension_size_type
      TileInfo::tileCount() const
      {
        return impl->tilecount;
      }

      dimension_size_type
      TileInfo::tileRowCount() const
      {
        return impl->nrows;
      }

      dimension_size_type
      TileInfo::tileColumnCount() const
      {
        return impl->ncols;
      }

      dimension_size_type
      TileInfo::bufferSize() const
      {
        return impl->buffersize;
      }

      dimension_size_type
      TileInfo::tileIndex(dimension_size_type x,
                          dimension_size_type y,
                          dimension_size_type s) const
      {
        Sentry sentry;
        ::TIFF *tiff = impl->getTIFF();

        return TIFFComputeTile(tiff, x, y, 0, s);
      }

      dimension_size_type
      TileInfo::tileColumn(dimension_size_type index) const
      {
        index %= impl->ntiles; // zeroth sample
        index %= impl->ncols;  // zeroth row

        return index;
      }

      dimension_size_type
      TileInfo::tileRow(dimension_size_type index) const
      {
        index %= impl->ntiles; // zeroth sample
        index /= impl->ncols;  // individual row

        return index;
      }

      dimension_size_type
      TileInfo::tileSample(dimension_size_type index) const
      {
        index /= impl->ntiles; // individual sample

        return index;
      }

      PlaneRegion
      TileInfo::tileRegion(dimension_size_type index) const
      {
        // Compute origin of tile from its row and column
        dimension_size_type row = tileRow(index);
        dimension_size_type col = tileColumn(index);

        dimension_size_type x = col * impl->tilewidth;
        dimension_size_type y = row * impl->tileheight;

        return PlaneRegion(x, y, impl->tilewidth, impl->tileheight);
      }

      PlaneRegion
      TileInfo::tileRegion(dimension_size_type index,
                           const PlaneRegion&  clip) const
      {
        // Clip to clip region boundaries
        return tileRegion(index) & clip;
      }

      std::vector<dimension_size_type>
      TileInfo::tileCoverage(PlaneRegion region) const
      {
        std::vector<dimension_size_type> ret;

        dimension_size_type samplelimit = 1;
        if (impl->planarconfig == SEPARATE) // planar
          samplelimit = impl->samples;

        // Iterate over all samples
        for (dimension_size_type sample = 0; sample < samplelimit; ++sample)
          {
            // Compute row and column subrange for the covered region
            dimension_size_type tile1 = tileIndex(region.x,                region.y,                sample);
            dimension_size_type tile2 = tileIndex(region.x + region.w - 1, region.y,                sample);
            dimension_size_type tile3 = tileIndex(region.x,                region.y + region.h - 1, sample);

            dimension_size_type colstart = tileColumn(tile1);
            dimension_size_type collimit = tileColumn(tile2) + 1;
            dimension_size_type rowstart = tileRow(tile1);
            dimension_size_type rowlimit = tileRow(tile3) + 1;

            // Iterate over row and column subranges
            for (dimension_size_type row = rowstart; row < rowlimit; ++row)
              for (dimension_size_type col = colstart; col < collimit; ++col)
                {
                  // Compute tile index for the row/column/sample indexes
                  dimension_size_type index = (sample * impl->ntiles) + (row * impl->ncols) + col;
                  ret.push_back(index);
                }
          }

        return ret;
      }

    }
  }
}

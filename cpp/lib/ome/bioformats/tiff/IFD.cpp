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

#include <algorithm>
#include <cmath>
#include <cstdarg>
#include <cassert>

#include <ome/internal/config.h>

#include <boost/format.hpp>
#include <boost/thread.hpp>

#include <ome/bioformats/PlaneRegion.h>
#include <ome/bioformats/TileBuffer.h>
#include <ome/bioformats/TileCache.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/Tags.h>
#include <ome/bioformats/tiff/Field.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/Sentry.h>
#include <ome/bioformats/tiff/Exception.h>

#include <ome/common/string.h>

#include <tiffio.h>

using ome::xml::model::enums::PixelType;

namespace
{

  using namespace ::ome::bioformats::tiff;
  using ::ome::bioformats::dimension_size_type;
  using ::ome::bioformats::PixelBuffer;
  using ::ome::bioformats::PixelProperties;
  using ::ome::bioformats::PlaneRegion;
  using ::ome::bioformats::TileBuffer;
  using ::ome::bioformats::TileCache;
  using ::ome::bioformats::TileCoverage;

  // VariantPixelBuffer tile transfer
  // ────────────────────────────────
  //
  // ReadVisitor: Transfer a set of tiles to a destination pixel buffer.
  // WriteVisitor: Transfer source pixel buffer data to a set of tiles.
  //
  // ┏━━━━━━┯━━━━━━┯━━━━━━┯━━━┓
  // ┃      │      │      │░░░┃
  // ┃      │      │      │░░░┃
  // ┃      │      │      │░░░┃
  // ┃   ╔══╪══════╪════╗ │░░░┃
  // ┃   ║  │      │    ║ │░░░┃
  // ┃   ║  │      │    ║ │░░░┃
  // ┠───╫──┼──────┼────╫─┼───┨
  // ┃   ║  │      │╔══╗║ │░░░┃
  // ┃   ║  │      │║▓▓║║ │░░░┃
  // ┃   ║  │      │║▓▓║║ │░░░┃
  // ┃   ║  │      │╚══╝║ │░░░┃
  // ┃   ║  │      │    ║ │░░░┃
  // ┃   ║  │      │    ║ │░░░┃
  // ┠───╫──┼──────┼────╫─┼───┨
  // ┃   ║  │▒▒▒▒▒▒│    ║ │░░░┃
  // ┃   ║  │▒▒▒▒▒▒│    ║ │░░░┃
  // ┃   ║  │▒▒▒▒▒▒│    ║ │░░░┃
  // ┃   ╚══╪══════╪════╝ │░░░┃
  // ┃      │      │      │░░░┃
  // ┃      │      │      │░░░┃
  // ┠──────┼──────┼──────┼───┨
  // ┃░░░░░░│░░░░░░│░░░░░░│░░░┃
  // ┃░░░░░░│░░░░░░│░░░░░░│░░░┃
  // ┗━━━━━━┷━━━━━━┷━━━━━━┷━━━┛
  //
  // ━━━━ Image region
  // ──── TIFF tile and TileBuffer region
  // ════ VariantPixelBuffer region
  //
  // ░░░░ Incomplete tiles which overlap the image region
  // ▒▒▒▒ Intersection (clip region) of pixel buffer with tile buffer
  // ▓▓▓▓ Unaligned clip region (of a smaller size than the tile
  //      dimensions)
  //
  // Both visitors iterate over the tiles partially or fully covered
  // by the pixel buffer, and use the optimal strategy to copy data
  // between the pixel buffer and tile buffer.  This will typically be
  // std::copy (usually memmove(3) internally) of whole tiles or tile
  // chunks where the tile widths are compatible, or individual
  // scanlines where they are not compatible.

  struct ReadVisitor : public boost::static_visitor<>
  {
    const IFD&                              ifd;
    const TileInfo&                         tileinfo;
    const PlaneRegion&                      region;
    const std::vector<dimension_size_type>& tiles;
    TileBuffer                              tilebuf;

    ReadVisitor(const IFD&                              ifd,
                const TileInfo&                         tileinfo,
                const PlaneRegion&                      region,
                const std::vector<dimension_size_type>& tiles):
      ifd(ifd),
      tileinfo(tileinfo),
      region(region),
      tiles(tiles),
      tilebuf(tileinfo.bufferSize())
    {}

    ~ReadVisitor()
    {
    }

    template<typename T>
    void
    transfer(ome::compat::shared_ptr<T>& buffer,
             typename T::indices_type&   destidx,
             const TileBuffer&           tilebuf,
             PlaneRegion&                rfull,
             PlaneRegion&                rclip,
             uint16_t                    copysamples)
    {
      if (rclip.w == rfull.w &&
          rclip.x == region.x &&
          rclip.w == region.w)
        {
          // Transfer contiguous block since the tile spans the
          // whole region width for both source and destination
          // buffers.

          destidx[ome::bioformats::DIM_SPATIAL_X] = rclip.x - region.x;
          destidx[ome::bioformats::DIM_SPATIAL_Y] = rclip.y - region.y;

          typename T::value_type *dest = &buffer->at(destidx);
          const typename T::value_type *src = reinterpret_cast<const typename T::value_type *>(tilebuf.data());
          std::copy(src,
                    src + (rclip.w * rclip.h * copysamples),
                    dest);
        }
      else
        {
          // Transfer discontiguous block.

          dimension_size_type xoffset = (rclip.x - rfull.x) * copysamples;

          for (dimension_size_type row = rclip.y;
               row != rclip.y + rclip.h;
               ++row)
            {
              dimension_size_type yoffset = (row - rfull.y) * (rfull.w * copysamples);

              destidx[ome::bioformats::DIM_SPATIAL_X] = rclip.x - region.x;
              destidx[ome::bioformats::DIM_SPATIAL_Y] = row - region.y;

              typename T::value_type *dest = &buffer->at(destidx);
              const typename T::value_type *src = reinterpret_cast<const typename T::value_type *>(tilebuf.data());
              std::copy(src + yoffset + xoffset,
                        src + yoffset + xoffset + (rclip.w * copysamples),
                        dest);
            }
        }
    }

    // Special case for BIT
    void
    transfer(ome::compat::shared_ptr<PixelBuffer<PixelProperties<PixelType::BIT>::std_type> >& buffer,
             PixelBuffer<PixelProperties<PixelType::BIT>::std_type>::indices_type&             destidx,
             const TileBuffer&                                                                 tilebuf,
             PlaneRegion&                                                                      rfull,
             PlaneRegion&                                                                      rclip,
             uint16_t                                                                          copysamples)
    {
      // Unpack bits from buffer.

      typedef PixelBuffer<PixelProperties<PixelType::BIT>::std_type> T;

      dimension_size_type xoffset = (rclip.x - rfull.x) * copysamples;

      for (dimension_size_type row = rclip.y;
           row != rclip.y + rclip.h;
           ++row)
        {
          dimension_size_type row_width = rfull.w * copysamples;
          if (row_width % 8U)
            row_width += 8U - (row_width % 8U); // pad to next full byte
          dimension_size_type yoffset = (row - rfull.y) * row_width;

          destidx[ome::bioformats::DIM_SPATIAL_X] = rclip.x - region.x;
          destidx[ome::bioformats::DIM_SPATIAL_Y] = row - region.y;

          T::value_type *dest = &buffer->at(destidx);
          const uint8_t *src = reinterpret_cast<const uint8_t *>(tilebuf.data());

          for (dimension_size_type sampleoffset = 0U;
               sampleoffset < (rclip.w * copysamples);
               ++sampleoffset)
            {
              dimension_size_type src_bit = yoffset + xoffset + sampleoffset;
              const uint8_t *src_byte = src + (src_bit / 8U);
              const uint8_t bit_offset = 7U - (src_bit % 8U);
              const uint8_t mask = static_cast<uint8_t>(1U << bit_offset);
              assert(src_byte >= src && src_byte < src + tilebuf.size());
              *(dest+sampleoffset) = static_cast<T::value_type>(*src_byte & mask);
            }
        }
    }

    template<typename T>
    dimension_size_type
    expected_read(const ome::compat::shared_ptr<T>& /* buffer */,
                  const PlaneRegion&                rclip,
                  uint16_t                          copysamples) const
    {
      return rclip.w * rclip.h * copysamples * sizeof(typename T::value_type);
    }

    // Special case for BIT
    dimension_size_type
    expected_read(const ome::compat::shared_ptr<PixelBuffer<PixelProperties<PixelType::BIT>::std_type> >& /* buffer */,
                  const PlaneRegion&                                                                      rclip,
                  uint16_t                                                                                copysamples) const
    {
      dimension_size_type expectedread = rclip.w;

      if (expectedread % 8)
        ++expectedread;
      expectedread *= rclip.h * copysamples;
      expectedread /= 8;

      return expectedread;
    }

    template<typename T>
    void
    operator()(ome::compat::shared_ptr<T>& buffer)
    {
      ome::compat::shared_ptr< ::ome::bioformats::tiff::TIFF>& tiff(ifd.getTIFF());
      ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());
      TileType type = tileinfo.tileType();

      uint16_t samples = ifd.getSamplesPerPixel();
      PlanarConfiguration planarconfig = ifd.getPlanarConfiguration();

      Sentry sentry;

      for(std::vector<dimension_size_type>::const_iterator i = tiles.begin();
          i != tiles.end();
          ++i)
        {
          tstrile_t tile = static_cast<tstrile_t>(*i);
          PlaneRegion rfull = tileinfo.tileRegion(tile);
          PlaneRegion rclip = tileinfo.tileRegion(tile, region);
          dimension_size_type sample = tileinfo.tileSample(tile);

          uint16_t copysamples = samples;
          dimension_size_type dest_subchannel = 0;
          if (planarconfig == SEPARATE)
            {
              copysamples = 1;
              dest_subchannel = sample;
            }

          if (type == TILE)
            {
              tmsize_t bytesread = TIFFReadEncodedTile(tiffraw, tile, tilebuf.data(), static_cast<tsize_t>(tilebuf.size()));
              if (bytesread < 0)
                sentry.error("Failed to read encoded tile");
              else if (static_cast<dimension_size_type>(bytesread) != tilebuf.size())
                sentry.error("Failed to read encoded tile fully");
            }
          else
            {
              tmsize_t bytesread = TIFFReadEncodedStrip(tiffraw, tile, tilebuf.data(), static_cast<tsize_t>(tilebuf.size()));
              dimension_size_type expectedread = expected_read(buffer, rclip, copysamples);
              if (bytesread < 0)
                sentry.error("Failed to read encoded strip");
              else if (static_cast<dimension_size_type>(bytesread) < expectedread)
                sentry.error("Failed to read encoded strip fully");
            }

          typename T::indices_type destidx;
          destidx[ome::bioformats::DIM_SPATIAL_X] = 0;
          destidx[ome::bioformats::DIM_SPATIAL_Y] = 0;
          destidx[ome::bioformats::DIM_SUBCHANNEL] = dest_subchannel;
          destidx[ome::bioformats::DIM_SPATIAL_Z] = destidx[ome::bioformats::DIM_TEMPORAL_T] =
            destidx[ome::bioformats::DIM_CHANNEL] = destidx[ome::bioformats::DIM_MODULO_Z] =
            destidx[ome::bioformats::DIM_MODULO_T] = destidx[ome::bioformats::DIM_MODULO_C] = 0;

          transfer(buffer, destidx, tilebuf, rfull, rclip, copysamples);
        }
    }
  };

  struct WriteVisitor : public boost::static_visitor<>
  {
    IFD&                                    ifd;
    std::vector<TileCoverage>&              tilecoverage;
    TileCache&                              tilecache;
    const TileInfo&                         tileinfo;
    const PlaneRegion&                      region;
    const std::vector<dimension_size_type>& tiles;

    WriteVisitor(IFD&                                    ifd,
                 std::vector<TileCoverage>&              tilecoverage,
                 TileCache&                              tilecache,
                 const TileInfo&                         tileinfo,
                 const PlaneRegion&                      region,
                 const std::vector<dimension_size_type>& tiles):
      ifd(ifd),
      tilecoverage(tilecoverage),
      tilecache(tilecache),
      tileinfo(tileinfo),
      region(region),
      tiles(tiles)
    {}

    // Flush covered tiles.
    void
    flush()
    {
      ome::compat::shared_ptr< ::ome::bioformats::tiff::TIFF>& tiff(ifd.getTIFF());
      ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());
      TileType type = tileinfo.tileType();
      PlaneRegion rimage(0, 0, ifd.getImageWidth(), ifd.getImageHeight());
      tstrile_t tile = static_cast<tstrile_t>(ifd.getCurrentTile());

      Sentry sentry;
      while(tile < tileinfo.tileCount())
        {
          dimension_size_type tile_subchannel = tileinfo.tileSample(tile);

          PlaneRegion validarea = tileinfo.tileRegion(tile) & rimage;
          if (!validarea.area())
            break;

          if (!tilecoverage.at(tile_subchannel).covered(validarea))
            break;

          assert(tilecache.find(tile));
          TileBuffer& tilebuf = *tilecache.find(tile);
          if (type == TILE)
            {
              tsize_t byteswritten = TIFFWriteEncodedTile(tiffraw, tile, tilebuf.data(), static_cast<tsize_t>(tilebuf.size()));
              if (byteswritten < 0)
                sentry.error("Failed to write encoded tile");
              else if (static_cast<dimension_size_type>(byteswritten) != tilebuf.size())
                sentry.error("Failed to write encoded tile fully");
            }
          else
            {
              tsize_t byteswritten = TIFFWriteEncodedStrip(tiffraw, tile, tilebuf.data(), static_cast<tsize_t>(tilebuf.size()));
              if (byteswritten < 0)
                sentry.error("Failed to write encoded strip");
              else if (static_cast<dimension_size_type>(byteswritten) != tilebuf.size())
                sentry.error("Failed to write encoded strip fully");
            }
          tilecache.erase(tile);
          ifd.setCurrentTile(++tile);
        }
    }

    template<typename T>
    void
    transfer(const ome::compat::shared_ptr<T>& buffer,
             typename T::indices_type&         srcidx,
             TileBuffer&                       tilebuf,
             PlaneRegion&                      rfull,
             PlaneRegion&                      rclip,
             uint16_t                          copysamples)
    {
      if (rclip.w == rfull.w &&
          rclip.x == region.x &&
          rclip.w == region.w)
        {
          // Transfer contiguous block since the tile spans the
          // whole region width for both source and destination
          // buffers.

          srcidx[ome::bioformats::DIM_SPATIAL_X] = rclip.x - region.x;
          srcidx[ome::bioformats::DIM_SPATIAL_Y] = rclip.y - region.y;

          typename T::value_type *dest = reinterpret_cast<typename T::value_type *>(tilebuf.data());
          const typename T::value_type *src = &buffer->at(srcidx);

          assert(dest + (rclip.w * rclip.h * copysamples) <= dest + tilebuf.size());
          std::copy(src,
                    src + (rclip.w * rclip.h * copysamples),
                    dest);
        }
      else
        {
          // Transfer discontiguous block.

          dimension_size_type xoffset = (rclip.x - rfull.x) * copysamples;

          for (dimension_size_type row = rclip.y;
               row < rclip.y + rclip.h;
               ++row)
            {
              dimension_size_type yoffset = (row - rfull.y) * (rfull.w * copysamples);

              srcidx[ome::bioformats::DIM_SPATIAL_X] = rclip.x - region.x;
              srcidx[ome::bioformats::DIM_SPATIAL_Y] = row - region.y;

              typename T::value_type *dest = reinterpret_cast<typename T::value_type *>(tilebuf.data());
              const typename T::value_type *src = &buffer->at(srcidx);

              assert(dest + yoffset + xoffset + (rclip.w * copysamples) <= dest + tilebuf.size());
              std::copy(src,
                        src + (rclip.w * copysamples),
                        dest + yoffset + xoffset);
            }
        }
    }

    // Special case for BIT
    void
    transfer(const ome::compat::shared_ptr<PixelBuffer<PixelProperties<PixelType::BIT>::std_type> >& buffer,
             PixelBuffer<PixelProperties<PixelType::BIT>::std_type>::indices_type&                   srcidx,
             TileBuffer&                                                                             tilebuf,
             PlaneRegion&                                                                            rfull,
             PlaneRegion&                                                                            rclip,
             uint16_t                                                                                copysamples)
    {
      // Pack bits into buffer.

      typedef PixelBuffer<PixelProperties<PixelType::BIT>::std_type> T;

      dimension_size_type xoffset = (rclip.x - rfull.x) * copysamples;

      for (dimension_size_type row = rclip.y;
           row != rclip.y + rclip.h;
           ++row)
        {
          dimension_size_type row_width = rfull.w * copysamples;
          if (row_width % 8)
            row_width += 8 - (row_width % 8); // pad to next full byte
          dimension_size_type yoffset = (row - rfull.y) * row_width;

          srcidx[ome::bioformats::DIM_SPATIAL_X] = rclip.x - region.x;
          srcidx[ome::bioformats::DIM_SPATIAL_Y] = row - region.y;

          uint8_t *dest = reinterpret_cast<uint8_t *>(tilebuf.data());
          const T::value_type *src = &buffer->at(srcidx);

          for (dimension_size_type sampleoffset = 0;
               sampleoffset < (rclip.w * copysamples);
               ++sampleoffset)
            {
              const T::value_type *srcsample = src + sampleoffset;
              dimension_size_type dest_bit = yoffset + xoffset + sampleoffset;
              uint8_t *dest_byte = dest + (dest_bit / 8);
              const uint8_t bit_offset = 7 - (dest_bit % 8);

              assert(dest_byte >= dest && dest_byte < dest + tilebuf.size());
              // Don't clear the bit since the tile will only be written once.
              *dest_byte |= static_cast<uint8_t>(*srcsample << bit_offset);
            }
        }
    }

    template<typename T>
    void
    operator()(const ome::compat::shared_ptr<T>& buffer)
    {
      uint16_t samples = ifd.getSamplesPerPixel();
      PlanarConfiguration planarconfig = ifd.getPlanarConfiguration();

      if (tilecoverage.size() != (planarconfig == CONTIG ? 1 : samples))
        tilecoverage.resize(planarconfig == CONTIG ? 1 : samples);

      for(std::vector<dimension_size_type>::const_iterator i = tiles.begin();
          i != tiles.end();
          ++i)
        {
          tstrile_t tile = static_cast<tstrile_t>(*i);
          PlaneRegion rfull = tileinfo.tileRegion(tile);
          PlaneRegion rclip = tileinfo.tileRegion(tile, region);
          dimension_size_type sample = tileinfo.tileSample(tile);

          uint16_t copysamples = samples;
          dimension_size_type dest_subchannel = 0;
          if (planarconfig == SEPARATE)
            {
              copysamples = 1;
              dest_subchannel = sample;
            }

          // Note boost::make_shared makes arguments const, so can't use
          // here.
          if (!tilecache.find(tile))
            tilecache.insert(tile, ome::compat::shared_ptr<TileBuffer>(new TileBuffer(tileinfo.bufferSize())));
          assert(tilecache.find(tile));
          TileBuffer& tilebuf = *tilecache.find(tile);

          typename T::indices_type srcidx;
          srcidx[ome::bioformats::DIM_SPATIAL_X] = 0;
          srcidx[ome::bioformats::DIM_SPATIAL_Y] = 0;
          srcidx[ome::bioformats::DIM_SUBCHANNEL] = dest_subchannel;
          srcidx[ome::bioformats::DIM_SPATIAL_Z] = srcidx[ome::bioformats::DIM_TEMPORAL_T] =
            srcidx[ome::bioformats::DIM_CHANNEL] = srcidx[ome::bioformats::DIM_MODULO_Z] =
            srcidx[ome::bioformats::DIM_MODULO_T] = srcidx[ome::bioformats::DIM_MODULO_C] = 0;

          transfer(buffer, srcidx, tilebuf, rfull, rclip, copysamples);
          tilecoverage.at(dest_subchannel).insert(rclip);
        }

      // Flush covered tiles
      flush();
    }
  };

}

namespace ome
{
  namespace bioformats
  {
    namespace tiff
    {

      namespace
      {

        class IFDConcrete : public IFD
        {
        public:
          IFDConcrete(ome::compat::shared_ptr<TIFF>& tiff,
                      offset_type            offset):
            IFD(tiff, offset)
          {
          }

          IFDConcrete(ome::compat::shared_ptr<TIFF>& tiff):
            IFD(tiff, 0)
          {
          }

          virtual
          ~IFDConcrete()
          {
          }
        };

      }

      /**
       * Internal implementation details of OffsetIFD.
       */
      class IFD::Impl
      {
      public:
        /// Reference to the parent TIFF.
        ome::compat::shared_ptr<TIFF> tiff;
        /// Offset of this IFD.
        offset_type offset;
        /// Tile coverage cache (used when writing).
        std::vector<TileCoverage> coverage;
        /// Tile cache (used when writing).
        TileCache tilecache;
        /// Tile type.
        boost::optional<TileType> tiletype;
        /// Image width.
        boost::optional<uint32_t> imagewidth;
        /// Image height.
        boost::optional<uint32_t> imageheight;
        /// Tile width.
        boost::optional<uint32_t> tilewidth;
        /// Tile height.
        boost::optional<uint32_t> tileheight;
        /// Pixel type.
        boost::optional<PixelType> pixeltype;
        /// Bits per sample.
        boost::optional<uint16_t> bits;
        /// Samples per pixel.
        boost::optional<uint16_t> samples;
        /// Planar configuration.
        boost::optional<PlanarConfiguration> planarconfig;
        /// Photometric interpretation.
        boost::optional<PhotometricInterpretation> photometric;
        /// Current tile (for writing).
        tstrile_t ctile;

        /**
         * Constructor.
         *
         * @param tiff the parent TIFF.
         * @param offset the IFD offset.
         */
        Impl(ome::compat::shared_ptr<TIFF>& tiff,
             offset_type                    offset):
          tiff(tiff),
          offset(offset),
          coverage(),
          tilecache(),
          imagewidth(),
          imageheight(),
          tilewidth(),
          tileheight(),
          pixeltype(),
          samples(),
          planarconfig(),
          ctile(0)
        {
        }

        /// Destructor.
        ~Impl()
        {
        }

      private:
        /// Copy constructor (deleted).
        Impl (const Impl&);

        /// Assignment operator (deleted).
        Impl&
        operator= (const Impl&);
      };

      IFD::IFD(ome::compat::shared_ptr<TIFF>& tiff,
               offset_type                    offset):
        // Note boost::make_shared makes arguments const, so can't use
        // here.
        impl(ome::compat::shared_ptr<Impl>(new Impl(tiff, offset)))
      {
      }

      IFD::IFD(ome::compat::shared_ptr<TIFF>& tiff):
        // Note boost::make_shared makes arguments const, so can't use
        // here.
        impl(ome::compat::shared_ptr<Impl>(new Impl(tiff, 0)))
      {
      }

      IFD::~IFD()
      {
      }

      ome::compat::shared_ptr<IFD>
      IFD::openIndex(ome::compat::shared_ptr<TIFF>& tiff,
                     directory_index_type           index)
      {
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        if (!TIFFSetDirectory(tiffraw, index))
          sentry.error();

        // Note boost::make_shared makes arguments const, so can't use
        // here.
        return openOffset(tiff, static_cast<uint64_t>(TIFFCurrentDirOffset(tiffraw)));
      }

      ome::compat::shared_ptr<IFD>
      IFD::openOffset(ome::compat::shared_ptr<TIFF>& tiff,
                      offset_type                    offset)
      {
        // Note boost::make_shared makes arguments const, so can't use
        // here.
        return ome::compat::shared_ptr<IFD>(new IFDConcrete(tiff, offset));
      }

      ome::compat::shared_ptr<IFD>
      IFD::current(ome::compat::shared_ptr<TIFF>& tiff)
      {
        // Note boost::make_shared makes arguments const, so can't use
        // here.
        return ome::compat::shared_ptr<IFD>(new IFDConcrete(tiff));
      }

      void
      IFD::makeCurrent() const
      {
        ome::compat::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        if (static_cast<offset_type>(TIFFCurrentDirOffset(tiffraw)) != impl->offset)
          {
#if TIFF_HAVE_BIGTIFF
            if (!TIFFSetSubDirectory(tiffraw, impl->offset))
              sentry.error();
#else // !TIFF_HAVE_BIGTIFF
            if (!TIFFSetSubDirectory(tiffraw, static_cast<uint32_t>(impl->offset)))
              sentry.error();
#endif // TIFF_HAVE_BIGTIFF
          }
      }

      ome::compat::shared_ptr<TIFF>&
      IFD::getTIFF() const
      {
        return impl->tiff;
      }

      offset_type
      IFD::getOffset() const
      {
        return impl->offset;
      }

      void
      IFD::getRawField(tag_type tag,
                       ...) const
      {
        ome::compat::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        va_list ap;
        va_start(ap, tag);

        if (!tag)
          {
            boost::format fmt("Error getting field: Tag %1% is not valid");
            fmt % tag;
            throw Exception(fmt.str());
          }

        if (!TIFFVGetField(tiffraw, tag, ap))
          {
            boost::format fmt("Error getting field: Tag %1% was not found");
            fmt % tag;
            sentry.error();
          }
      }

      void
      IFD::setRawField(tag_type tag,
                       ...)
      {
        ome::compat::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        va_list ap;
        va_start(ap, tag);

        if (!tag)
          {
            boost::format fmt("Error getting field: Tag %1% is not valid");
            fmt % tag;
            throw Exception(fmt.str());
          }

        if (!TIFFVSetField(tiffraw, tag, ap))
          sentry.error();
      }

      TileType
      IFD::getTileType() const
      {
        if (!impl->tiletype)
          {
            uint32_t w, h;
            try
              {
                getField(TILEWIDTH).get(w);
                getField(TILELENGTH).get(h);
                impl->tiletype = TILE;
              }
            catch (const Exception& e)
              {
                getField(ROWSPERSTRIP).get(h);
                impl->tiletype = STRIP;
              }
          }

        return impl->tiletype.get();
      }

      void
      IFD::setTileType(TileType type)
      {
        impl->tiletype = type;
      }

      dimension_size_type
      IFD::getCurrentTile() const
      {
        return impl->ctile;
      }

      void
      IFD::setCurrentTile(dimension_size_type tile)
      {
        impl->ctile = static_cast<tstrile_t>(tile);
      }

      TileInfo
      IFD::getTileInfo()
      {
        return TileInfo(this->shared_from_this());
      }

      const TileInfo
      IFD::getTileInfo() const
      {
        return TileInfo(const_cast<IFD *>(this)->shared_from_this());
      }

      std::vector<TileCoverage>&
      IFD::getTileCoverage()
      {
        return impl->coverage;
      }

      const std::vector<TileCoverage>&
      IFD::getTileCoverage() const
      {
        return impl->coverage;
      }

      uint32_t
      IFD::getImageWidth() const
      {
        if (!impl->imagewidth)
          {
            uint32_t width;
            getField(IMAGEWIDTH).get(width);
            impl->imagewidth = width;
          }
        return impl->imagewidth.get();
      }

      void
      IFD::setImageWidth(uint32_t width)
      {
        getField(IMAGEWIDTH).set(width);
        impl->imagewidth = width;
      }

      uint32_t
      IFD::getImageHeight() const
      {
        if (!impl->imageheight)
          {
            uint32_t height;
            getField(IMAGELENGTH).get(height);
            impl->imageheight = height;
          }
        return impl->imageheight.get();
      }

      void
      IFD::setImageHeight(uint32_t height)
      {
        getField(IMAGELENGTH).set(height);
        impl->imageheight = height;
      }

      uint32_t
      IFD::getTileWidth() const
      {
        if (!impl->tilewidth)
          {
            if (getTileType() == TILE)
              {
                uint32_t width;
                getField(TILEWIDTH).get(width);
                impl->tilewidth = width;
              }
            else // strip
              {
                impl->tilewidth = getImageWidth();
              }
          }
        return impl->tilewidth.get();
      }

      void
      IFD::setTileWidth(uint32_t width)
      {
        if (getTileType() == TILE)
          {
            getField(TILEWIDTH).set(width);
            impl->tilewidth = width;
          }
        else
          {
            // Do nothing for strips.
          }
      }

      uint32_t
      IFD::getTileHeight() const
      {
        if (!impl->tileheight)
          {
            if (getTileType() == TILE)
              {
                uint32_t height;
                getField(TILELENGTH).get(height);
                impl->tileheight = height;
              }
            else
              {
                uint32_t rows;
                getField(ROWSPERSTRIP).get(rows);
                impl->tileheight = rows;
              }
          }
        return impl->tileheight.get();
      }

      void
      IFD::setTileHeight(uint32_t height)
      {
        if (getTileType() == TILE)
          {
            getField(TILELENGTH).set(height);
          }
        else // strip
          {
            getField(ROWSPERSTRIP).set(height);
          }
        impl->tileheight = height;
      }

      ::ome::xml::model::enums::PixelType
      IFD::getPixelType() const
      {
        PixelType pt = PixelType::UINT8;

        if (impl->pixeltype)
          {
            pt = impl->pixeltype.get();
          }
        else
          {
            SampleFormat sampleformat;
            try
              {
                getField(SAMPLEFORMAT).get(sampleformat);
              }
            catch(const Exception& e)
              {
                // Default to unsigned integer.
                sampleformat = UNSIGNED_INT;
              }

            uint16_t bits = getBitsPerSample();

            switch(sampleformat)
              {
              case UNSIGNED_INT:
                {
                  if (bits == 1)
                    pt = PixelType::BIT;
                  else if (bits == 8)
                    pt = PixelType::UINT8;
                  else if (bits == 16)
                    pt = PixelType::UINT16;
                  else if (bits == 32)
                    pt = PixelType::UINT32;
                  else
                    {
                      boost::format fmt("Bit depth %1% unsupported for unsigned integer pixel type");
                      fmt % bits;
                      throw Exception(fmt.str());
                    }
                }
                break;
              case SIGNED_INT:
                {
                  if (bits == 8)
                    pt = PixelType::INT8;
                  else if (bits == 16)
                    pt = PixelType::INT16;
                  else if (bits == 32)
                pt = PixelType::INT32;
                  else
                    {
                      boost::format fmt("Bit depth %1% unsupported for signed integer pixel type");
                      fmt % bits;
                      throw Exception(fmt.str());
                    }
                }
                break;
              case FLOAT:
                {
                  if (bits == 32)
                    pt = PixelType::FLOAT;
                  else if (bits == 64)
                    pt = PixelType::DOUBLE;
                  else
                    {
                      boost::format fmt("Bit depth %1% unsupported for floating point pixel type");
                      fmt % bits;
                      throw Exception(fmt.str());
                    }
                }
                break;
              case COMPLEX_FLOAT:
                {
                  if (bits == 64)
                    pt = PixelType::COMPLEX;
                  else if (bits == 128)
                    pt = PixelType::DOUBLECOMPLEX;
                  else
                    {
                      boost::format fmt("Bit depth %1% unsupported for complex floating point pixel type");
                      fmt % bits;
                      throw Exception(fmt.str());
                    }
                }
                break;
              default:
                {
                  boost::format fmt("TIFF SampleFormat %1% unsupported by OME data model PixelType");
                  fmt % sampleformat;
                  throw Exception(fmt.str());
                }
                break;
              }
          }
        return pt;
      }

      void
      IFD::setPixelType(::ome::xml::model::enums::PixelType type)
      {
        SampleFormat fmt = UNSIGNED_INT;

        switch(type)
          {
          case PixelType::BIT:
          case PixelType::UINT8:
          case PixelType::UINT16:
          case PixelType::UINT32:
            fmt = UNSIGNED_INT;
            break;

          case PixelType::INT8:
          case PixelType::INT16:
          case PixelType::INT32:
            fmt = SIGNED_INT;
            break;

          case PixelType::FLOAT:
          case PixelType::DOUBLE:
            fmt = FLOAT;
            break;

          case PixelType::COMPLEX:
          case PixelType::DOUBLECOMPLEX:
            fmt = COMPLEX_FLOAT;
            break;

          default:
            {
              boost::format fmt("Unsupported OME data model PixelType %1%");
              fmt % type;
              throw Exception(fmt.str());
            }
            break;
          }

        getField(SAMPLEFORMAT).set(fmt);
        impl->pixeltype = type;
      }

      uint16_t
      IFD::getBitsPerSample() const
      {
        if (!impl->bits)
          {
            uint16_t bits;
            getField(BITSPERSAMPLE).get(bits);
            impl->bits = bits;
          }
        return impl->bits.get();
      }

      void
      IFD::setBitsPerSample(uint16_t bits)
      {
        uint16 max_bits = significantBitsPerPixel(getPixelType());
        if (bits > max_bits)
          bits = max_bits;

        getField(BITSPERSAMPLE).set(bits);
        impl->bits = bits;
      }

      uint16_t
      IFD::getSamplesPerPixel() const
      {
        if (!impl->samples)
          {
            uint16_t samples;
            getField(SAMPLESPERPIXEL).get(samples);
            impl->samples = samples;
          }
        return impl->samples.get();
      }

      void
      IFD::setSamplesPerPixel(uint16_t samples)
      {
        getField(SAMPLESPERPIXEL).set(samples);
        impl->samples = samples;
      }

      PlanarConfiguration
      IFD::getPlanarConfiguration() const
      {
        if (!impl->planarconfig)
          {
            PlanarConfiguration config;
            getField(PLANARCONFIG).get(config);
            impl->planarconfig = config;
          }
        return impl->planarconfig.get();
      }

      void
      IFD::setPlanarConfiguration(PlanarConfiguration planarconfig)
      {
        getField(PLANARCONFIG).set(planarconfig);
        impl->planarconfig = planarconfig;
      }

      PhotometricInterpretation
      IFD::getPhotometricInterpretation() const
      {
        if (!impl->photometric)
          {
            PhotometricInterpretation photometric;
            getField(PHOTOMETRIC).get(photometric);
            impl->photometric = photometric;
          }
        return impl->photometric.get();
      }

      void
      IFD::setPhotometricInterpretation(PhotometricInterpretation photometric)
      {
        getField(PHOTOMETRIC).set(photometric);
        impl->photometric = photometric;
      }

      void
      IFD::readImage(VariantPixelBuffer& buf) const
      {
        readImage(buf, 0, 0, getImageWidth(), getImageHeight());
      }

      void
      IFD::readImage(VariantPixelBuffer& buf,
                     dimension_size_type subC) const
      {
        readImage(buf, 0, 0, getImageWidth(), getImageHeight(), subC);
      }

      void
      IFD::readImage(VariantPixelBuffer& dest,
                     dimension_size_type x,
                     dimension_size_type y,
                     dimension_size_type w,
                     dimension_size_type h) const
      {
        PixelType type = getPixelType();
        PlanarConfiguration planarconfig = getPlanarConfiguration();
        uint16_t subC = getSamplesPerPixel();

        ome::compat::array<VariantPixelBuffer::size_type, 9> shape, dest_shape;
        shape[DIM_SPATIAL_X] = w;
        shape[DIM_SPATIAL_Y] = h;
        shape[DIM_SUBCHANNEL] = subC;
        shape[DIM_SPATIAL_Z] = shape[DIM_TEMPORAL_T] = shape[DIM_CHANNEL] =
          shape[DIM_MODULO_Z] = shape[DIM_MODULO_T] = shape[DIM_MODULO_C] = 1;

        const VariantPixelBuffer::size_type *dest_shape_ptr(dest.shape());
        std::copy(dest_shape_ptr, dest_shape_ptr + PixelBufferBase::dimensions,
                  dest_shape.begin());

        PixelBufferBase::storage_order_type order(PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, planarconfig == SEPARATE ? false : true));

        if (type != dest.pixelType() ||
            shape != dest_shape ||
            !(order == dest.storage_order()))
          dest.setBuffer(shape, type, order);

        TileInfo info = getTileInfo();

        PlaneRegion region(x, y, w, h);
        std::vector<dimension_size_type> tiles(info.tileCoverage(region));

        ReadVisitor v(*this, info, region, tiles);
        boost::apply_visitor(v, dest.vbuffer());
      }

      void
      IFD::readImage(VariantPixelBuffer& dest,
                     dimension_size_type x,
                     dimension_size_type y,
                     dimension_size_type w,
                     dimension_size_type h,
                     dimension_size_type subC) const
      {
        // Copy the desired subchannel into the destination buffer.
        VariantPixelBuffer tmp;
        readImage(tmp, x, y, w, h);

        detail::CopySubchannelVisitor v(dest, subC);
        boost::apply_visitor(v, tmp.vbuffer());
      }

      void
      IFD::readLookupTable(VariantPixelBuffer& buf) const
      {
        ome::compat::array<std::vector<uint16_t>, 3> cmap;
        getField(tiff::COLORMAP).get(cmap);

        ome::compat::array<VariantPixelBuffer::size_type, 9> shape;
        shape[DIM_SPATIAL_X] = cmap.at(0).size();
        shape[DIM_SPATIAL_Y] = 1;
        shape[DIM_SUBCHANNEL] = cmap.size();
        shape[DIM_SPATIAL_Z] = shape[DIM_TEMPORAL_T] = shape[DIM_CHANNEL] =
          shape[DIM_MODULO_Z] = shape[DIM_MODULO_T] = shape[DIM_MODULO_C] = 1;

        ::ome::bioformats::PixelBufferBase::storage_order_type order_planar(::ome::bioformats::PixelBufferBase::make_storage_order(::ome::xml::model::enums::DimensionOrder::XYZTC, false));

        buf.setBuffer(shape, PixelType::UINT16, order_planar);

        ome::compat::shared_ptr<PixelBuffer<PixelProperties<PixelType::UINT16>::std_type> > uint16_buffer
          (boost::get<ome::compat::shared_ptr<PixelBuffer<PixelProperties<PixelType::UINT16>::std_type> > >(buf.vbuffer()));
        assert(uint16_buffer);

        for (VariantPixelBuffer::size_type s = 0U; s < shape[DIM_SUBCHANNEL]; ++s)
          {
            const std::vector<uint16_t>& channel(cmap.at(s));

            VariantPixelBuffer::indices_type coord;
            coord[DIM_SPATIAL_X] = 0;
            coord[DIM_SPATIAL_Y] = 0;
            coord[DIM_SUBCHANNEL] = s;
            coord[DIM_SPATIAL_Z] = coord[DIM_TEMPORAL_T] =
              coord[DIM_CHANNEL] = coord[DIM_MODULO_Z] =
              coord[DIM_MODULO_T] = coord[DIM_MODULO_C] = 0;

            std::copy(channel.begin(), channel.end(),
                      &uint16_buffer->at(coord));
          }

      }

      void
      IFD::writeImage(const VariantPixelBuffer& buf,
                      dimension_size_type       subC)
      {
        writeImage(buf, 0, 0, getImageWidth(), getImageHeight(), subC);
      }

      void
      IFD::writeImage(const VariantPixelBuffer& buf)
      {
        writeImage(buf, 0, 0, getImageWidth(), getImageHeight());
      }

      void
      IFD::writeImage(const VariantPixelBuffer& source,
                      dimension_size_type       x,
                      dimension_size_type       y,
                      dimension_size_type       w,
                      dimension_size_type       h)
      {
        PixelType type = getPixelType();
        PlanarConfiguration planarconfig = getPlanarConfiguration();
        uint16_t subC = getSamplesPerPixel();

        ome::compat::array<VariantPixelBuffer::size_type, 9> shape, source_shape;
        shape[DIM_SPATIAL_X] = w;
        shape[DIM_SPATIAL_Y] = h;
        shape[DIM_SUBCHANNEL] = subC;
        shape[DIM_SPATIAL_Z] = shape[DIM_TEMPORAL_T] = shape[DIM_CHANNEL] =
          shape[DIM_MODULO_Z] = shape[DIM_MODULO_T] = shape[DIM_MODULO_C] = 1;

        const VariantPixelBuffer::size_type *source_shape_ptr(source.shape());
        std::copy(source_shape_ptr, source_shape_ptr + PixelBufferBase::dimensions,
                  source_shape.begin());

        PixelBufferBase::storage_order_type order(PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, planarconfig == SEPARATE ? false : true));
        PixelBufferBase::storage_order_type source_order(source.storage_order());

        if (type != source.pixelType())
          {
            boost::format fmt("VariantPixelBuffer %1% pixel type is incompatible with TIFF %2% sample format and bit depth");
            fmt % source.pixelType() % type;
            throw Exception(fmt.str());
          }

        if (shape != source_shape)
          {
            if (shape[DIM_SPATIAL_X] != source_shape[DIM_SPATIAL_X] ||
                shape[DIM_SPATIAL_Y] != source_shape[DIM_SPATIAL_Y] ||
                shape[DIM_SUBCHANNEL] != source_shape[DIM_SUBCHANNEL])
              {
                boost::format fmt("VariantPixelBuffer dimensions (%1%×%2%, %3% samples) incompatible with TIFF image size (%4%×%5%, %6% samples)");
                fmt % source_shape[DIM_SPATIAL_X] % source_shape[DIM_SPATIAL_Y] % source_shape[DIM_SUBCHANNEL];
                fmt % shape[DIM_SPATIAL_X] % shape[DIM_SPATIAL_Y] % shape[DIM_SUBCHANNEL];
                throw Exception(fmt.str());
              }
            else
              {
                boost::format fmt("VariantPixelBuffer dimensions (%1%×%2%×%3%, %4%t, %5%c, %6% samples, %7%mz, %8%mt, %9%mc) incompatible with TIFF image size (%10%×%11%, %12% samples)");
                fmt % source_shape[DIM_SPATIAL_X] % source_shape[DIM_SPATIAL_Y] % source_shape[DIM_SPATIAL_Z];
                fmt % source_shape[DIM_TEMPORAL_T] % source_shape[DIM_CHANNEL] % source_shape[DIM_SUBCHANNEL];
                fmt % source_shape[DIM_MODULO_Z] % source_shape[DIM_MODULO_T] % source_shape[DIM_MODULO_C];
                fmt % shape[DIM_SPATIAL_X] % shape[DIM_SPATIAL_Y] % shape[DIM_SUBCHANNEL];
                throw Exception(fmt.str());
              }
          }

        if (!(order == source_order))
          {
            boost::format fmt("VariantPixelBuffer storage order (%1%%2%%3%%4%%5%%6%%7%%8%%9%) incompatible with %10% TIFF planar configuration (%11%%12%%13%%14%%15%%16%%17%%18%%19%");
            fmt % source_order.ordering(0) % source_order.ordering(1) % source_order.ordering(2);
            fmt % source_order.ordering(3) % source_order.ordering(4) % source_order.ordering(5);
            fmt % source_order.ordering(6) % source_order.ordering(7) % source_order.ordering(8);
            fmt % (planarconfig == SEPARATE ? "separate" : "contiguous");
            fmt % order.ordering(0) % order.ordering(1) % order.ordering(2);
            fmt % order.ordering(3) % order.ordering(4) % order.ordering(5);
            fmt % order.ordering(6) % order.ordering(7) % order.ordering(8);
            throw Exception(fmt.str());
          }

        TileInfo info = getTileInfo();

        PlaneRegion region(x, y, w, h);
        std::vector<dimension_size_type> tiles(info.tileCoverage(region));

        WriteVisitor v(*this, impl->coverage, impl->tilecache, info, region, tiles);
        boost::apply_visitor(v, source.vbuffer());
      }

      void
      IFD::writeImage(const VariantPixelBuffer& /* source*/,
                      dimension_size_type       /* x*/,
                      dimension_size_type       /* y*/,
                      dimension_size_type       /* w*/,
                      dimension_size_type       /* h*/,
                      dimension_size_type       /* subC */)
      {
        throw Exception("Writing subchannels separately is not yet implemented (requires TileCache and WriteVisitor to handle writing and caching of interleaved and non-interleaved subchannels; currently it handles writing all subchannels in one call only and can not combine separate subchannels from separate calls");
      }

      ome::compat::shared_ptr<IFD>
      IFD::next() const
      {
        ome::compat::shared_ptr<IFD> ret;

        ome::compat::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        if (TIFFReadDirectory(tiffraw) == 1)
          {
            uint64_t offset = static_cast<uint64_t>(TIFFCurrentDirOffset(tiffraw));
            ret = openOffset(tiff, offset);
          }

        return ret;
      }

      bool
      IFD::last() const
      {
        ome::compat::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        return TIFFLastDirectory(tiffraw) != 0;
      }

    }
  }
}

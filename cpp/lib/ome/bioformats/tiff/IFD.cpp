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

#include <iostream>
#include <algorithm>
#include <cmath>
#include <cstdarg>
#include <cassert>

#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/Tags.h>
#include <ome/bioformats/tiff/Field.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/Sentry.h>
#include <ome/bioformats/tiff/Exception.h>
#include <ome/bioformats/tiff/TileBuffer.h>
#include <ome/bioformats/tiff/TileCache.h>

#include <ome/compat/thread.h>
#include <ome/compat/string.h>

#include <tiffio.h>

using ome::xml::model::enums::PixelType;

namespace
{

  using namespace ::ome::bioformats::tiff;
  using ::ome::bioformats::dimension_size_type;

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
    operator()(std::shared_ptr<T>& buffer)
    {
      std::shared_ptr< ::ome::bioformats::tiff::TIFF>& tiff(ifd.getTIFF());
      ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());
      TileInfo::TileType type = tileinfo.tileType();

      uint32_t imagewidth;
      uint32_t imageheight;
      ifd.getField(IMAGEWIDTH).get(imagewidth);
      ifd.getField(IMAGELENGTH).get(imageheight);

      uint16_t samples;
      ifd.getField(SAMPLESPERPIXEL).get(samples);
      PlanarConfiguration planarconfig;
      ifd.getField(PLANARCONFIG).get(planarconfig);

      Sentry sentry;

      for(std::vector<dimension_size_type>::const_iterator i = tiles.begin();
          i != tiles.end();
          ++i)
        {
          tstrile_t tile = *i;
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
          if (type == TileInfo::TILE)
            {
              int bytesread = TIFFReadEncodedTile(tiffraw, tile, tilebuf.data(), tilebuf.size());
              if (bytesread < 0)
                sentry.error("Failed to read encoded tile");
              if (bytesread != tilebuf.size())
                sentry.error("Failed to read encoded tile fully");
            }
          else
            {
              int bytesread = TIFFReadEncodedStrip(tiffraw, tile, tilebuf.data(), tilebuf.size());
              if (bytesread < 0)
                sentry.error("Failed to read encoded strip");
              if (bytesread < rclip.w * rclip.h * copysamples * sizeof(typename T::value_type))
                sentry.error("Failed to read encoded strip fully");
            }

          typename T::indices_type destidx;
          destidx[ome::bioformats::DIM_SPATIAL_X] = 0;
          destidx[ome::bioformats::DIM_SPATIAL_Y] = 0;
          destidx[ome::bioformats::DIM_SUBCHANNEL] = dest_subchannel;
          destidx[ome::bioformats::DIM_SPATIAL_Z] = destidx[ome::bioformats::DIM_TEMPORAL_T] =
            destidx[ome::bioformats::DIM_CHANNEL] = destidx[ome::bioformats::DIM_MODULO_Z] =
            destidx[ome::bioformats::DIM_MODULO_T] = destidx[ome::bioformats::DIM_MODULO_C] = 0;

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
              std::copy(src, src + (rclip.w * rclip.h * copysamples), dest);
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
    }
  };

  struct WriteVisitor : public boost::static_visitor<>
  {
    IFD&                                    ifd;
    TileCache&                              tilecache;
    const TileInfo&                         tileinfo;
    const PlaneRegion&                      region;
    const std::vector<dimension_size_type>& tiles;

    WriteVisitor(IFD&                                    ifd,
                 TileCache&                              tilecache,
                 const TileInfo&                         tileinfo,
                 const PlaneRegion&                      region,
                 const std::vector<dimension_size_type>& tiles):
      ifd(ifd),
      tilecache(tilecache),
      tileinfo(tileinfo),
      region(region),
      tiles(tiles)
    {}

    template<typename T>
    void
    operator()(const std::shared_ptr<T>& buffer)
    {
      std::shared_ptr< ::ome::bioformats::tiff::TIFF>& tiff(ifd.getTIFF());
      ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());
      TileInfo::TileType type = tileinfo.tileType();

      uint32_t imagewidth;
      uint32_t imageheight;
      ifd.getField(IMAGEWIDTH).get(imagewidth);
      ifd.getField(IMAGELENGTH).get(imageheight);

      uint16_t samples;
      ifd.getField(SAMPLESPERPIXEL).get(samples);
      PlanarConfiguration planarconfig;
      ifd.getField(PLANARCONFIG).get(planarconfig);

      Sentry sentry;
      for(std::vector<dimension_size_type>::const_iterator i = tiles.begin();
          i != tiles.end();
          ++i)
        {
          tstrile_t tile = *i;
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

          if (!tilecache.find(tile))
            tilecache.insert(tile, std::make_shared<TileBuffer>(tileinfo.bufferSize()));
          assert(tilecache.find(tile));
          TileBuffer& tilebuf = *tilecache.find(tile);

          typename T::indices_type srcidx;
          srcidx[ome::bioformats::DIM_SPATIAL_X] = 0;
          srcidx[ome::bioformats::DIM_SPATIAL_Y] = 0;
          srcidx[ome::bioformats::DIM_SUBCHANNEL] = dest_subchannel;
          srcidx[ome::bioformats::DIM_SPATIAL_Z] = srcidx[ome::bioformats::DIM_TEMPORAL_T] =
            srcidx[ome::bioformats::DIM_CHANNEL] = srcidx[ome::bioformats::DIM_MODULO_Z] =
            srcidx[ome::bioformats::DIM_MODULO_T] = srcidx[ome::bioformats::DIM_MODULO_C] = 0;

          if (rclip.w == rfull.w &&
              rclip.x == region.x &&
              rclip.w == region.w)
            {
              // Transfer contiguous block since the tile spans the
              // whole region width for both source and destination
              // buffers.

              srcidx[ome::bioformats::DIM_SPATIAL_X] = rclip.x - region.x;
              srcidx[ome::bioformats::DIM_SPATIAL_Y] = rclip.y - region.y;

              typename T::value_type *dest = &buffer->at(srcidx);
              const typename T::value_type *src = reinterpret_cast<const typename T::value_type *>(tilebuf.data());
              std::copy(src, src + (rclip.w * rclip.h * copysamples), dest);
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

                  srcidx[ome::bioformats::DIM_SPATIAL_X] = rclip.x - region.x;
                  srcidx[ome::bioformats::DIM_SPATIAL_Y] = row - region.y;

                  typename T::value_type *dest = &buffer->at(srcidx);
                  const typename T::value_type *src = reinterpret_cast<const typename T::value_type *>(tilebuf.data());
                  std::copy(src + yoffset + xoffset,
                            src + yoffset + xoffset + (rclip.w * copysamples),
                            dest);
                }
            }


        }
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
          IFDConcrete(std::shared_ptr<TIFF>& tiff,
                      offset_type            offset):
            IFD(tiff, offset)
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
        std::shared_ptr<TIFF> tiff;
        /// Offset of this IFD.
        offset_type offset;
        /// Tile cache (used when writing).
        TileCache tilecache;

        /**
         * Constructor.
         *
         * @param tiff the parent TIFF.
         * @param offset the IFD offset.
         */
        Impl(std::shared_ptr<TIFF>& tiff,
             offset_type            offset):
          tiff(tiff),
          offset(offset),
          tilecache()
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

      IFD::IFD(std::shared_ptr<TIFF>& tiff,
               offset_type            offset):
        // Note boost::make_shared makes arguments const, so can't use
        // here.
        impl(std::shared_ptr<Impl>(new Impl(tiff, offset)))
      {
      }

      IFD::~IFD()
      {
      }

      std::shared_ptr<IFD>
      IFD::openIndex(std::shared_ptr<TIFF>& tiff,
                     directory_index_type   index)
      {
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        if (!TIFFSetDirectory(tiffraw, index))
          sentry.error();

        // Note boost::make_shared makes arguments const, so can't use
        // here.
        return openOffset(tiff, static_cast<uint64_t>(TIFFCurrentDirOffset(tiffraw)));
      }

      std::shared_ptr<IFD>
      IFD::openOffset(std::shared_ptr<TIFF>& tiff,
                      offset_type            offset)
      {
        // Note boost::make_shared makes arguments const, so can't use
        // here.
        return std::shared_ptr<IFD>(new IFDConcrete(tiff, offset));
      }

      void
      IFD::makeCurrent() const
      {
        std::shared_ptr<TIFF>& tiff = getTIFF();
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

      std::shared_ptr<TIFF>&
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
        std::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        va_list ap;
        va_start(ap, tag);

        if (!tag)
          throw Exception("Error getting field: Tag is not valid");

        if (!TIFFVGetField(tiffraw, tag, ap))
          sentry.error("Error getting field: Tag was not found");
      }

      void
      IFD::setRawField(tag_type tag,
                       ...)
      {
        std::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        va_list ap;
        va_start(ap, tag);

        if (!tag)
          throw Exception("Error getting field: Tag is not valid");

        if (!TIFFVSetField(tiffraw, tag, ap))
          sentry.error();
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

      ::ome::xml::model::enums::PixelType
      IFD::getPixelType() const
      {
        PixelType pt = PixelType::UINT8;

        SampleFormat fmt;
        try
          {
            getField(SAMPLEFORMAT).get(fmt);
          }
        catch(const Exception& e)
          {
            // Default to unsigned integer.
            fmt = UNSIGNED_INT;
          }

        uint16_t bits;
        getField(BITSPERSAMPLE).get(bits);

        switch(fmt)
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
                throw Exception("Unsupported bit depth for unsigned integer pixel type");
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
                throw Exception("Unsupported bit depth for signed integer pixel type");
            }
            break;
          case FLOAT:
            {
              if (bits == 32)
                pt = PixelType::FLOAT;
              else if (bits == 64)
                pt = PixelType::DOUBLE;
              else
                throw Exception("Unsupported bit depth for floating point pixel type");
            }
            break;
          case COMPLEX_FLOAT:
            {
              if (bits == 64)
                pt = PixelType::COMPLEX;
              else if (bits == 12)
                pt = PixelType::DOUBLECOMPLEX;
              else
                throw Exception("Unsupported bit depth for complex floating point pixel type");
            }
            break;
          default:
            throw Exception("TIFF SampleFormat unsupported by OME data model PixelType");
            break;
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
            throw Exception("Unsupported OME data model PixelType");
            break;
          }

        getField(SAMPLEFORMAT).set(fmt);

        uint16_t bits = 0;

        switch(type)
          {
          case PixelType::BIT:
            bits = 1;
            break;

          case PixelType::UINT8:
          case PixelType::INT8:
            bits = 8;
            break;

          case PixelType::UINT16:
          case PixelType::INT16:
            bits = 16;
            break;

          case PixelType::UINT32:
          case PixelType::INT32:
          case PixelType::FLOAT:
            bits = 32;
            break;

          case PixelType::DOUBLE:
          case PixelType::COMPLEX:
            bits = 64;
            break;

          case PixelType::DOUBLECOMPLEX:
            bits = 128;
            break;

          default:
            throw Exception("Unsupported OME data model PixelType");
            break;
          }

        getField(BITSPERSAMPLE).set(bits);
      }

      void
      IFD::readImage(VariantPixelBuffer& buf) const
      {
        uint32_t w = 0, h = 0;
        getField(IMAGEWIDTH).get(w);
        getField(IMAGELENGTH).get(h);

        readImage(buf, 0, 0, w, h);
      }

      void
      IFD::readImage(VariantPixelBuffer& dest,
                     dimension_size_type x,
                     dimension_size_type y,
                     dimension_size_type w,
                     dimension_size_type h) const
      {
        PixelType type = getPixelType();

        PlanarConfiguration planarconfig;
        getField(PLANARCONFIG).get(planarconfig);

        uint16_t subC;
        getField(SAMPLESPERPIXEL).get(subC);

        std::array<VariantPixelBuffer::size_type, 9> shape, dest_shape;
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
      IFD::writeImage(const VariantPixelBuffer& buf)
      {
        uint32_t w = 0, h = 0;
        getField(ome::bioformats::tiff::IMAGEWIDTH).get(w);
        getField(ome::bioformats::tiff::IMAGELENGTH).get(h);

        writeImage(buf, 0, 0, w, h);
      }

      void
      IFD::writeImage(const VariantPixelBuffer& source,
                      dimension_size_type x,
                      dimension_size_type y,
                      dimension_size_type w,
                      dimension_size_type h)
      {
        PixelType type = getPixelType();

        PlanarConfiguration planarconfig;
        getField(PLANARCONFIG).get(planarconfig);

        uint16_t subC;
        getField(SAMPLESPERPIXEL).get(subC);

        std::array<VariantPixelBuffer::size_type, 9> shape, source_shape;
        shape[DIM_SPATIAL_X] = w;
        shape[DIM_SPATIAL_Y] = h;
        shape[DIM_SUBCHANNEL] = subC;
        shape[DIM_SPATIAL_Z] = shape[DIM_TEMPORAL_T] = shape[DIM_CHANNEL] =
          shape[DIM_MODULO_Z] = shape[DIM_MODULO_T] = shape[DIM_MODULO_C] = 1;

        const VariantPixelBuffer::size_type *source_shape_ptr(source.shape());
        std::copy(source_shape_ptr, source_shape_ptr + PixelBufferBase::dimensions,
                  source_shape.begin());

        PixelBufferBase::storage_order_type order(PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, planarconfig == SEPARATE ? false : true));

        if (type != source.pixelType())
          throw Exception("VariantPixelBuffer pixel type is incompatible with TIFF sample format and bit depth");

        if (shape != source_shape)
          throw Exception("VariantPixelBuffer dimensions incompatible with TIFF image size");

        if (!(order == source.storage_order()))
          throw Exception("VariantPixelBuffer storage order incompatible with TIFF planar configuration");

        TileInfo info = getTileInfo();

        PlaneRegion region(x, y, w, h);
        std::vector<dimension_size_type> tiles(info.tileCoverage(region));

        WriteVisitor v(*this, impl->tilecache, info, region, tiles);
        boost::apply_visitor(v, source.vbuffer());
      }

      std::shared_ptr<IFD>
      IFD::next() const
      {
        std::shared_ptr<IFD> ret;

        std::shared_ptr<TIFF>& tiff = getTIFF();
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
        std::shared_ptr<TIFF>& tiff = getTIFF();
        ::TIFF *tiffraw = reinterpret_cast< ::TIFF *>(tiff->getWrapped());

        Sentry sentry;

        makeCurrent();

        return TIFFLastDirectory(tiffraw) != 0;
      }

    }
  }
}

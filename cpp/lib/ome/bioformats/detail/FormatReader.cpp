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

#include <cmath>
#include <fstream>

#include <boost/filesystem/operations.hpp>
#include <boost/filesystem/path.hpp>

#include <ome/common/filesystem.h>
#include <ome/common/mstream.h>
#include <ome/common/string.h>

#include <ome/compat/regex.h>

#include <ome/bioformats/FormatTools.h>
#include <ome/bioformats/MetadataTools.h>
#include <ome/bioformats/PixelBuffer.h>
#include <ome/bioformats/PixelProperties.h>
#include <ome/bioformats/VariantPixelBuffer.h>
#include <ome/bioformats/detail/FormatReader.h>

#include <ome/xml/meta/DummyMetadata.h>
#include <ome/xml/meta/FilterMetadata.h>
#include <ome/xml/meta/MetadataStore.h>
#include <ome/xml/meta/OMEXMLMetadata.h>

using boost::filesystem::path;
using ome::xml::meta::DummyMetadata;
using ome::xml::meta::FilterMetadata;
using ome::bioformats::CoreMetadata;

namespace ome
{
  namespace bioformats
  {
    namespace detail
    {

      namespace
      {
        // Default thumbnail width and height.
        const dimension_size_type THUMBNAIL_DIMENSION = 128;
      }

      FormatReader::FormatReader(const ReaderProperties& readerProperties):
        readerProperties(readerProperties),
        currentId(boost::none),
        in(),
        metadata(),
        coreIndex(0),
        series(0),
        plane(0),
        core(),
        resolution(0),
        flattenedResolutions(true),
        suffixNecessary(true),
        suffixSufficient(true),
        companionFiles(false),
        datasetDescription("Single file"),
        normalizeData(false),
        filterMetadata(false),
        saveOriginalMetadata(false),
        indexedAsRGB(false),
        group(true),
        domains(),
        metadataStore(ome::compat::make_shared<DummyMetadata>()),
        metadataOptions()
      {
        assertId(currentId, false);
      }

      FormatReader::~FormatReader()
      {
      }

      const std::string&
      FormatReader::getFormat() const
      {
        return readerProperties.name;
      }

      const std::string&
      FormatReader::getFormatDescription() const
      {
        return readerProperties.description;
      }

      const std::vector<boost::filesystem::path>&
      FormatReader::getSuffixes() const
      {
        return readerProperties.suffixes;
      }

      const std::vector<boost::filesystem::path>&
      FormatReader::getCompressionSuffixes() const
      {
        return readerProperties.compression_suffixes;
      }

      const std::set<MetadataOptions::MetadataLevel>&
      FormatReader::getSupportedMetadataLevels()
      {
        return readerProperties.metadata_levels;
      }

      void
      FormatReader::initFile(const boost::filesystem::path& id)
      {
        if (currentId)
          {
            const std::vector<path>& s = getUsedFiles();
            for (std::vector<path>::const_iterator i = s.begin();
                 i != s.end();
                 ++i)
              {
                if (id == *i) return;
              }
          }

        coreIndex = 0;
        series = 0;
        close();
        currentId = id;
        metadata.clear();

        core.clear();
        core.push_back(ome::compat::make_shared<CoreMetadata>());

        // reinitialize the MetadataStore
        // NB: critical for metadata conversion to work properly!
        getMetadataStore()->createRoot();
      }

      bool
      FormatReader::isUsedFile(const boost::filesystem::path& file)
      {
        bool used = false;

        try
          {
            path thisfile = ome::common::canonical(path(file));

            /// @todo: Use a set rather than a list?
            const std::vector<path>& s = getUsedFiles();
            for (std::vector<path>::const_iterator i = s.begin();
                 i != s.end();
                 ++i)
              {
                try
                  {
                    path usedfile = ome::common::canonical(path(*i));
                    if (thisfile == usedfile)
                      {
                        used = true;
                        break;
                      }
                  }
                catch (const boost::filesystem::filesystem_error& e)
                  {
                  }
              }
          }
        catch (const boost::filesystem::filesystem_error& e)
          {
          }

        return used;
      }

      void
      FormatReader::readPlane(std::istream&       source,
                              VariantPixelBuffer& dest,
                              dimension_size_type x,
                              dimension_size_type y,
                              dimension_size_type w,
                              dimension_size_type h,
                              dimension_size_type samples)
      {
        return readPlane(source, dest, x, y, w, h, 0, samples);
      }

      namespace
      {

        struct PlaneVisitor : public boost::static_visitor<>
        {
          std::istream&       source;
          FormatReader&       reader;
          dimension_size_type x;
          dimension_size_type y;
          dimension_size_type w;
          dimension_size_type h;
          dimension_size_type samples;
          dimension_size_type scanlinePad;

          PlaneVisitor(std::istream&       source,
                       FormatReader&       reader,
                       dimension_size_type x,
                       dimension_size_type y,
                       dimension_size_type w,
                       dimension_size_type h,
                       dimension_size_type samples,
                       dimension_size_type scanlinePad):
            source(source),
            reader(reader),
            x(x),
            y(y),
            w(w),
            h(h),
            samples(samples),
            scanlinePad(scanlinePad)
          {}

          template<typename T>
          void
          operator()(T& v)
          {
            EndianType endian = reader.isLittleEndian() ? ENDIAN_LITTLE : ENDIAN_BIG;

            const uint32_t bpp(bytesPerPixel(reader.getPixelType()));
            const dimension_size_type sizeX(reader.getSizeX());
            const dimension_size_type sizeY(reader.getSizeY());
            const bool interleaved(reader.isInterleaved());

            if (x == 0 && y == 0 &&
                w == sizeX &&
                h == sizeY &&
                scanlinePad == 0)
              {
                // Read whole plane into the buffer directly.
                source.read(reinterpret_cast<char *>(v->data()),
                            static_cast<std::streamsize>(v->num_elements() * bpp));
              }
            else if (x == 0 &&
                     w == sizeX &&
                     scanlinePad == 0)
              {
                if (interleaved)
                  {
                    source.seekg(static_cast<std::istream::off_type>(y * w * bpp * samples), std::ios::cur);
                    source.read(reinterpret_cast<char *>(v->data()),
                                static_cast<std::streamsize>(v->num_elements() * bpp));
                  }
                else
                  {
                    dimension_size_type rowLen = w * bpp;
                    for (dimension_size_type sample = 0; sample < samples; ++sample)
                      {
                        source.seekg(static_cast<std::istream::off_type>(y * rowLen), std::ios::cur);
                        source.read(reinterpret_cast<char *>(v->data())
                                    + (sample * h * rowLen),
                                    static_cast<std::streamsize>(h * rowLen));
                        // no need to skip bytes after reading final sample
                        if (sample < samples - 1)
                          source.seekg(static_cast<std::istream::off_type>((sizeY - y - h) * rowLen), std::ios::cur);
                      }
                  }
              }
            else
              {
                dimension_size_type scanlineWidth = sizeX + scanlinePad;
                if (interleaved)
                  {
                    source.seekg(static_cast<std::istream::off_type>(y * scanlineWidth * bpp * samples), std::ios::cur);
                    for (dimension_size_type row = 0; row < h; ++row)
                      {
                        source.seekg(static_cast<std::istream::off_type>(x * bpp * samples), std::ios::cur);
                        source.read(reinterpret_cast<char *>(v->data())
                                    + (row * w * bpp * samples),
                                    static_cast<std::streamsize>(w * bpp * samples));
                        // no need to skip bytes after reading final row
                        if (row < h - 1)
                          source.seekg(static_cast<std::istream::off_type>(bpp * samples * (scanlineWidth - w - x)), std::ios::cur);
                      }
                  }
                else
                  {
                    for (dimension_size_type sample = 0; sample < samples; ++sample)
                      {
                        source.seekg(static_cast<std::istream::off_type>(y * scanlineWidth * bpp), std::ios::cur);
                        for (dimension_size_type row = 0; row < h; ++row)
                          {
                            source.seekg(static_cast<std::istream::off_type>(x * bpp), std::ios::cur);
                            source.read(reinterpret_cast<char *>(v->data())
                                        + (sample * w * h * bpp + row * w * bpp),
                                        static_cast<std::streamsize>(w * bpp));
                            // no need to skip bytes after reading final row of final sample
                            if (row < h - 1 || sample < samples - 1)
                              source.seekg(static_cast<std::istream::off_type>(bpp * (scanlineWidth - w - x)), std::ios::cur);
                          }
                        if (sample < samples - 1)
                          // no need to skip bytes after reading final sample
                          source.seekg(static_cast<std::istream::off_type>(scanlineWidth * bpp * (sizeY - y - h)), std::ios::cur);
                      }
                  }
              }
            if (!source)
              throw std::runtime_error("readPlane: Error reading bytes from stream");

            // If the endianness of the data doesn't match the
            // endianness of the machine, byteswap the buffer.
            if ((endian == ome::bioformats::ENDIAN_BIG &&
                 boost::endian::order::big != boost::endian::order::native) ||
                (endian == ome::bioformats::ENDIAN_LITTLE &&
                 boost::endian::order::little != boost::endian::order::native))
              {
                typename T::element_type::value_type *data = v->data();
                typename T::element_type::size_type num_elements = v->num_elements();
                for (typename T::element_type::size_type i = 0; i < num_elements; ++i)
                  byteswap(data[i]);
              }
          }
        };

      }

      void
      FormatReader::readPlane(std::istream&       source,
                              VariantPixelBuffer& dest,
                              dimension_size_type x,
                              dimension_size_type y,
                              dimension_size_type w,
                              dimension_size_type h,
                              dimension_size_type scanlinePad,
                              dimension_size_type samples)
      {
        ome::compat::array<VariantPixelBuffer::size_type, 9> shape, dest_shape;
        shape[DIM_SPATIAL_X] = w;
        shape[DIM_SPATIAL_Y] = h;
        shape[DIM_SUBCHANNEL] = samples;
        shape[DIM_SPATIAL_Z] = shape[DIM_TEMPORAL_T] = shape[DIM_CHANNEL] =
          shape[DIM_MODULO_Z] = shape[DIM_MODULO_T] = shape[DIM_MODULO_C] = 1;
        const VariantPixelBuffer::size_type *dest_shape_ptr(dest.shape());
        std::copy(dest_shape_ptr, dest_shape_ptr + PixelBufferBase::dimensions,
                  dest_shape.begin());

        const ome::xml::model::enums::DimensionOrder order(getDimensionOrder());
        const bool interleaved(isInterleaved());
        const VariantPixelBuffer::storage_order_type storage_order
          (PixelBufferBase::make_storage_order(order, interleaved));

        const ome::xml::model::enums::PixelType type(getPixelType());

        // If the buffer is incorrectly sized, ordered or typed, reset
        // to the correct buffer size, order and type.
        if (type != dest.pixelType() ||
            !(storage_order == dest.storage_order()) ||
            shape != dest_shape)
          dest.setBuffer(shape, type, storage_order);

        // Fill the buffer according to its type.
        PlaneVisitor v(source, *this,
                       x, y, w, h, samples, scanlinePad);
        boost::apply_visitor(v, dest.vbuffer());
      }

      ome::compat::shared_ptr< ::ome::xml::meta::MetadataStore>
      FormatReader::makeFilterMetadata()
      {
        // While ome::compat::make_shared<> works, here, boost::make_shared<>
        // does not, so use new directly.
        return ome::compat::shared_ptr< ::ome::xml::meta::MetadataStore>
          (new FilterMetadata(getMetadataStore(), isMetadataFiltered()));
      }

      void
      FormatReader::setMetadataOptions(const MetadataOptions& options)
      {
        this->metadataOptions = options;
      }

      const MetadataOptions&
      FormatReader::getMetadataOptions() const
      {
        return metadataOptions;
      }

      MetadataOptions&
      FormatReader::getMetadataOptions()
      {
        return metadataOptions;
      }

      bool
      FormatReader::isThisType(const boost::filesystem::path& name,
                               bool                           open) const
      {
        // if file extension ID is insufficient and we can't open the file, give up
        if (!suffixSufficient && !open)
          return false;

        if (suffixNecessary || suffixSufficient) {
          // it's worth checking the file extension
          bool suffixMatch = checkSuffix(name,
                                         readerProperties.suffixes,
                                         readerProperties.compression_suffixes);

          // if suffix match is required but it doesn't match, failure
          if (suffixNecessary && !suffixMatch)
            return false;

          // if suffix matches and that's all we need, green light it
          if (suffixMatch && suffixSufficient)
            return true;
        }

        // suffix matching was inconclusive; we need to analyze the file contents
        if (!open)
          return false; // not allowed to open any files

        return isFilenameThisTypeImpl(name);
      }

      bool
      FormatReader::isThisType(const uint8_t *begin,
                               const uint8_t *end) const
      {
        ome::common::imstream ims(reinterpret_cast<const char *>(begin),
                                  reinterpret_cast<const char *>(end));
        return isThisType(ims);
      }

      bool
      FormatReader::isThisType(const uint8_t *begin,
                               std::size_t    length) const
      {
        ome::common::imstream ims(reinterpret_cast<const char *>(begin), length);
        return isThisType(ims);
      }

      bool
      FormatReader::isThisType(std::istream& stream) const
      {
        return isStreamThisTypeImpl(stream);
      }

      bool
      FormatReader::isFilenameThisTypeImpl(const boost::filesystem::path& /* name */) const
      {
        return false;
      }

      bool
      FormatReader::isStreamThisTypeImpl(std::istream& /* stream */) const
      {
        return false;
      }

      dimension_size_type
      FormatReader::getImageCount() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).imageCount;
      }

      bool
      FormatReader::isRGB(dimension_size_type channel) const
      {
        assertId(currentId, true);
        return getRGBChannelCount(channel) > 1U;
      }

      dimension_size_type
      FormatReader::getSizeX() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).sizeX;
      }

      dimension_size_type
      FormatReader::getSizeY() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).sizeY;
      }

      dimension_size_type
      FormatReader::getSizeZ() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).sizeZ;
      }

      dimension_size_type
      FormatReader::getSizeT() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).sizeT;
      }

      dimension_size_type
      FormatReader::getSizeC() const
      {
        assertId(currentId, true);

        const std::vector<dimension_size_type>& c(getCoreMetadata(getCoreIndex()).sizeC);

        return std::accumulate(c.begin(), c.end(), dimension_size_type(0));
      }

      ome::xml::model::enums::PixelType
      FormatReader::getPixelType() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).pixelType;
      }

      pixel_size_type
      FormatReader::getBitsPerPixel() const
      {
        assertId(currentId, true);
        if (getCoreMetadata(getCoreIndex()).bitsPerPixel == 0) {
          return bitsPerPixel(getPixelType());
          /**
           * @todo: Move this logic into a CoreMetadata accessor. Why
           * are we modifying coremetadata during an explicitly
           * nonmodifying operation??
           */
          //   getCoreMetadata(getCoreIndex()).bitsPerPixel =
          //   FormatTools.getBytesPerPixel(getPixelType()) * 8;
        }
        return getCoreMetadata(getCoreIndex()).bitsPerPixel;
      }

      dimension_size_type
      FormatReader::getEffectiveSizeC() const
      {
        return getCoreMetadata(getCoreIndex()).sizeC.size();
      }

      dimension_size_type
      FormatReader::getRGBChannelCount(dimension_size_type channel) const
      {
        return getCoreMetadata(getCoreIndex()).sizeC.at(channel);
      }

      bool
      FormatReader::isIndexed() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).indexed;
      }

      bool
      FormatReader::isFalseColor() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).falseColor;
      }

      void
      FormatReader::getLookupTable(dimension_size_type /* plane */,
                                   VariantPixelBuffer& /* buf */) const
      {
        assertId(currentId, true);

        throw std::runtime_error("Reader does not implement lookup tables");
      }

      Modulo&
      FormatReader::getModuloZ()
      {
        return getCoreMetadata(getCoreIndex()).moduloZ;
      }

      const Modulo&
      FormatReader::getModuloZ() const
      {
        return getCoreMetadata(getCoreIndex()).moduloZ;
      }

      Modulo&
      FormatReader::getModuloT()
      {
        return getCoreMetadata(getCoreIndex()).moduloT;
      }

      const Modulo&
      FormatReader::getModuloT() const
      {
        return getCoreMetadata(getCoreIndex()).moduloT;
      }

      Modulo&
      FormatReader::getModuloC()
      {
        return getCoreMetadata(getCoreIndex()).moduloC;
      }

      const Modulo&
      FormatReader::getModuloC() const
      {
        return getCoreMetadata(getCoreIndex()).moduloC;
      }

      ome::compat::array<dimension_size_type, 2>
      FormatReader::getThumbSize() const
      {
        assertId(currentId, true);

        ome::compat::array<dimension_size_type, 2> ret;
        ret[0] = getCoreMetadata(getCoreIndex()).thumbSizeX;
        ret[1] = getCoreMetadata(getCoreIndex()).thumbSizeY;

        if (ret[0] == 0 || ret[1] == 0)
          {
            dimension_size_type sx = getSizeX();
            dimension_size_type sy = getSizeY();

            if (sx < THUMBNAIL_DIMENSION && sy < THUMBNAIL_DIMENSION)
              {
                ret[0] = sx;
                ret[1] = sy;
              }
            else
              {
                // Assume sx and sy are equal initially.
                ret[0] = ret[1] = THUMBNAIL_DIMENSION;
                if (sx > sy)
                  {
                    if (sx > 0)
                      ret[1] = (sy * THUMBNAIL_DIMENSION) / sx;
                  }
                else if (sx < sy)
                  {
                    if (sy > 0)
                      ret[0] = (sx * THUMBNAIL_DIMENSION) / sy;
                  }
              }
          }

        if (ret[0] == 0)
          ret[0] = 1;
        if (ret[1] == 0)
          ret[1] = 1;

        return ret;
      }

      dimension_size_type
      FormatReader::getThumbSizeX() const
      {
        return getThumbSize()[0];
      }

      dimension_size_type
      FormatReader::getThumbSizeY() const
      {
        return getThumbSize()[1];
      }

      bool
      FormatReader::isLittleEndian() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).littleEndian;
      }

      const std::string&
      FormatReader::getDimensionOrder() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).dimensionOrder;
      }

      bool
      FormatReader::isOrderCertain() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).orderCertain;
      }

      bool
      FormatReader::isThumbnailSeries() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).thumbnail;
      }

      bool
      FormatReader::isInterleaved() const
      {
        return isInterleaved(0);
      }

      bool
      FormatReader::isInterleaved(dimension_size_type /* subC */) const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).interleaved;
      }

      void
      FormatReader::openBytes(dimension_size_type plane,
                              VariantPixelBuffer& buf) const
      {
        openBytes(plane, buf, 0, 0, getSizeX(), getSizeY());
      }

      void
      FormatReader::openBytes(dimension_size_type plane,
                              VariantPixelBuffer& buf,
                              dimension_size_type x,
                              dimension_size_type y,
                              dimension_size_type w,
                              dimension_size_type h) const
      {
        setPlane(plane);
        openBytesImpl(plane, buf, x, y, w, h);
      }

      void
      FormatReader::openThumbBytes(dimension_size_type /* plane */,
                                   VariantPixelBuffer& /* buf */) const
      {
        assertId(currentId, true);
        /**
         * @todo Implement openThumbBytes.  This requires implementing
         * image rescaling/resampling.
         */
        throw std::runtime_error("Thumbnail rescaling is not currently implemented");
      }

      void
      FormatReader::close(bool fileOnly)
      {
        if (in)
          in = ome::compat::shared_ptr<std::istream>(); // set to null.
        if (!fileOnly)
          {
            currentId = boost::none;
            coreIndex = series = resolution = plane = 0;
            core.clear();
          }
      }

      dimension_size_type
      FormatReader::getSeriesCount() const
      {
        assertId(currentId, true);
        dimension_size_type size = core.size();
        if (!hasFlattenedResolutions()) {
          size = coreIndexToSeries(core.size() - 1) + 1;
        }
        return size;
      }

      void
      FormatReader::setSeries(dimension_size_type series) const
      {
        this->coreIndex = seriesToCoreIndex(series);
        this->series = series;
        this->resolution = 0;
        this->plane = 0;
      }

      dimension_size_type
      FormatReader::getSeries() const
      {
        return series;
      }

      void
      FormatReader::setPlane(dimension_size_type plane) const
      {
        assertId(currentId, true);

        if (plane >= getImageCount())
          {
            boost::format fmt("Invalid plane: %1%");
            fmt % plane;
            throw std::logic_error(fmt.str());
          }

        this->plane = plane;
      }

      dimension_size_type
      FormatReader::getPlane() const
      {
        assertId(currentId, true);

        return plane;
      }

      void
      FormatReader::setGroupFiles(bool group)
      {
        assertId(currentId, false);
        this->group = group;
      }

      bool
      FormatReader::isGroupFiles() const
      {
        return group;
      }

      FormatReader::FileGroupOption
      FormatReader::fileGroupOption(const std::string& /* id */)
      {
        return CANNOT_GROUP;
      }

      bool
      FormatReader::isMetadataComplete() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).metadataComplete;
      }

      void
      FormatReader::setNormalized(bool normalize)
      {
        assertId(currentId, false);
        normalizeData = normalize;
      }

      bool
      FormatReader::isNormalized() const
      {
        return normalizeData;
      }

      void
      FormatReader::setOriginalMetadataPopulated(bool populate)
      {
        assertId(currentId, false);
        saveOriginalMetadata = populate;
      }

      bool
      FormatReader::isOriginalMetadataPopulated() const
      {
        return saveOriginalMetadata;
      }

      const std::vector<boost::filesystem::path>
      FormatReader::getUsedFiles(bool noPixels) const
      {
        SaveSeries sentry(*this);
        std::set<path> files;
        for (dimension_size_type i = 0; i < getSeriesCount(); ++i)
          {
            setSeries(i);
            std::vector<path> s = getSeriesUsedFiles(noPixels);
            for (std::vector<path>::const_iterator file = s.begin();
                 file != s.end();
                 ++file)
              {
                files.insert(*file);
              }
          }
        return std::vector<path>(files.begin(), files.end());
      }

      const std::vector<boost::filesystem::path>
      FormatReader::getSeriesUsedFiles(bool noPixels) const
      {
        std::vector<path> ret;
        if (!noPixels && currentId)
          ret.push_back(currentId.get());
        return ret;
      }

      std::vector<FileInfo>
      FormatReader::getAdvancedUsedFiles(bool noPixels) const
      {
        std::vector<path> files = getUsedFiles(noPixels);
        std::vector<FileInfo> infos(files.size());

        for (std::vector<path>::iterator file = files.begin();
             file != files.end();
             ++file)
          {
            FileInfo info;
            info.filename = *file;
            info.reader = getFormat();
            info.usedToInitialize = false;

            const boost::optional<path> currentid = getCurrentFile();
            if (currentid)
              {
                path current = ome::common::canonical(currentid.get());
                path thisfile = ome::common::canonical(*file);

                info.usedToInitialize = (thisfile == current);
              }

            infos.push_back(info);
          }
        return infos;
      }

      std::vector<FileInfo>
      FormatReader::getAdvancedSeriesUsedFiles(bool noPixels) const
      {
        std::vector<path> files = getSeriesUsedFiles(noPixels);
        std::vector<FileInfo> infos(files.size());

        for (std::vector<path>::iterator file = files.begin();
             file != files.end();
             ++file)
          {
            FileInfo info;
            info.filename = *file;
            info.reader = getFormat();
            info.usedToInitialize = false;

            const boost::optional<path> currentid = getCurrentFile();
            if (currentid)
              {
                path current = ome::common::canonical(currentid.get());
                path thisfile = ome::common::canonical(*file);

                info.usedToInitialize = (thisfile == current);
              }

            infos.push_back(info);
          }
        return infos;
      }

      const boost::optional<boost::filesystem::path>&
      FormatReader::getCurrentFile() const
      {
        return currentId;
      }

      dimension_size_type
      FormatReader::getIndex(dimension_size_type z,
                             dimension_size_type c,
                             dimension_size_type t) const
      {
        assertId(currentId, true);
        return ome::bioformats::getIndex(getDimensionOrder(),
                                         getSizeZ(),
                                         getEffectiveSizeC(),
                                         getSizeT(),
                                         getImageCount(),
                                         z, c, t);
      }

      dimension_size_type
      FormatReader::getIndex(dimension_size_type z,
                             dimension_size_type c,
                             dimension_size_type t,
                             dimension_size_type moduloZ,
                             dimension_size_type moduloC,
                             dimension_size_type moduloT) const
      {
        assertId(currentId, true);
        return ome::bioformats::getIndex(getDimensionOrder(),
                                         getSizeZ(),
                                         getEffectiveSizeC(),
                                         getSizeT(),
                                         getModuloZ().size(),
                                         getModuloC().size(),
                                         getModuloT().size(),
                                         getImageCount(),
                                         z, c, t,
                                         moduloZ, moduloC, moduloT);
      }

      ome::compat::array<dimension_size_type, 3>
      FormatReader::getZCTCoords(dimension_size_type index) const
      {
        assertId(currentId, true);
        return ome::bioformats::getZCTCoords(getDimensionOrder(),
                                             getSizeZ(),
                                             getEffectiveSizeC(),
                                             getSizeT(),
                                             getImageCount(),
                                             index);
      }

      ome::compat::array<dimension_size_type, 6>
      FormatReader::getZCTModuloCoords(dimension_size_type index) const
      {
        assertId(currentId, true);
        return ome::bioformats::getZCTCoords(getDimensionOrder(),
                                             getSizeZ(),
                                             getEffectiveSizeC(),
                                             getSizeT(),
                                             getModuloZ().size(),
                                             getModuloC().size(),
                                             getModuloT().size(),
                                             getImageCount(),
                                             index);
      }

      const MetadataMap::value_type&
      FormatReader::getMetadataValue(const std::string& field) const
      {
        return metadata.get<MetadataMap::value_type>(field);
      }

      const MetadataMap::value_type&
      FormatReader::getSeriesMetadataValue(const MetadataMap::key_type& field) const
      {
        assertId(currentId, true);
        return getSeriesMetadata().get<MetadataMap::value_type>(field);
      }

      const MetadataMap&
      FormatReader::getGlobalMetadata() const
      {
        return metadata;
      }

      const MetadataMap&
      FormatReader::getSeriesMetadata() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).seriesMetadata;
      }

      const std::vector<ome::compat::shared_ptr< ::ome::bioformats::CoreMetadata> >&
      FormatReader::getCoreMetadataList() const
      {
        assertId(currentId, true);
        return core;
      }

      void
      FormatReader::setMetadataFiltered(bool filter)
      {
        assertId(currentId, false);
        filterMetadata = filter;
      }

      bool
      FormatReader::isMetadataFiltered() const
      {
        return filterMetadata;
      }

      void
      FormatReader::setMetadataStore(ome::compat::shared_ptr< ::ome::xml::meta::MetadataStore>& store)
      {
        assertId(currentId, false);

        if (!store)
          throw std::logic_error("MetadataStore can not be null");

        metadataStore = store;
      }

      const ome::compat::shared_ptr< ::ome::xml::meta::MetadataStore>&
      FormatReader::getMetadataStore() const
      {
        return metadataStore;
      }

      ome::compat::shared_ptr< ::ome::xml::meta::MetadataStore>&
      FormatReader::getMetadataStore()
      {
        return metadataStore;
      }

      std::vector<ome::compat::shared_ptr< ::ome::bioformats::FormatReader> >
      FormatReader::getUnderlyingReaders() const
      {
        return std::vector<ome::compat::shared_ptr< ::ome::bioformats::FormatReader> >();
      }

      bool
      FormatReader::isSingleFile(const boost::filesystem::path& /* id */) const
      {
        return true;
      }

      uint32_t
      FormatReader::getRequiredDirectories(const std::vector<std::string>& /* files */) const
      {
        return 0;
      }

      const std::string&
      FormatReader::getDatasetStructureDescription() const
      {
        return datasetDescription;
      }

      bool
      FormatReader::hasCompanionFiles() const
      {
        return companionFiles;
      }

      const std::vector<std::string>&
      FormatReader::getPossibleDomains(const std::string& /* id */) const
      {
        return domains;
      }

      const std::vector<std::string>&
      FormatReader::getDomains() const
      {
        assertId(currentId, true);
        return domains;
      }

      dimension_size_type
      FormatReader::getOptimalTileWidth(dimension_size_type /* channel */) const
      {
        assertId(currentId, true);
        return getSizeX();
      }

      dimension_size_type
      FormatReader::getOptimalTileHeight(dimension_size_type channel) const
      {
        assertId(currentId, true);
        uint32_t bpp = bytesPerPixel(getPixelType());
        dimension_size_type maxHeight = (1024U * 1024U) / (getSizeX() * getRGBChannelCount(channel) * bpp);
        if (!maxHeight)
          maxHeight = 1U;

        return std::min(maxHeight, getSizeY());
      }

      dimension_size_type
      FormatReader::getOptimalTileWidth() const
      {
        assertId(currentId, true);

        dimension_size_type csize = getEffectiveSizeC();
        std::vector<dimension_size_type> widths;
        widths.reserve(csize);
        for (dimension_size_type c = 0; c < csize; ++c)
          widths.push_back(getOptimalTileWidth(c));
        return *std::min_element(widths.begin(), widths.end());
      }

      dimension_size_type
      FormatReader::getOptimalTileHeight() const
      {
        assertId(currentId, true);

        dimension_size_type csize = getEffectiveSizeC();
        std::vector<dimension_size_type> heights;
        heights.reserve(csize);
        for (dimension_size_type c = 0; c < csize; ++c)
          heights.push_back(getOptimalTileHeight(c));
        return *std::min_element(heights.begin(), heights.end());
      }

      dimension_size_type
      FormatReader::seriesToCoreIndex(dimension_size_type series) const
      {
        dimension_size_type index = 0;

        if (hasFlattenedResolutions())
          {
            // coreIndex and series are identical
            if (series >= core.size())
              {
                boost::format fmt("Invalid series: %1%");
                fmt % series;
                throw std::logic_error(fmt.str());
              }
            index = series;
          }
        else if (this->series == series)
          {
            // Use corresponding coreIndex
            index = coreIndex - resolution;
          }
        else
          {
            dimension_size_type idx = 0;
            for (coremetadata_list_type::const_iterator i = core.begin();
                 i != core.end();
                 ++i, ++idx)
              {
                if (series == idx)
                  break;

                if (*i)
                  index += (*i)->resolutionCount;
                else
                  {
                    boost::format fmt("Invalid series (null core[%1%]): %2%");
                    fmt % idx % series;
                    throw std::logic_error(fmt.str());
                  }

                if (index >= core.size())
                  {
                    boost::format fmt("Invalid series: %1%, coreIndex=%2%");
                    fmt % series % index;
                    throw std::logic_error(fmt.str());
                  }
              }
            if (series != idx)
              {
                boost::format fmt("Invalid series: %1%");
                fmt % series;
                throw std::logic_error(fmt.str());
              }
          }

        return index;
      }

      dimension_size_type
      FormatReader::coreIndexToSeries(dimension_size_type index) const
      {
        dimension_size_type series = 0;

        if (index >= core.size())
          {
            boost::format fmt("Invalid index: %1%");
            fmt % index;
            throw std::logic_error(fmt.str());
          }

        if (hasFlattenedResolutions())
          {
            // coreIndex and series are identical
            series = index;
          }
        else if (coreIndex == index)
          {
            // Use corresponding series
            series = this->series;
          }
        else
          {
            // Convert from non-flattened coreIndex to flattened series
            dimension_size_type idx = 0;

            for (coremetadata_list_type::size_type i = 0; i < index;)
              {
                const coremetadata_list_type::value_type& v(core.at(i));
                if (v)
                  {
                    dimension_size_type nextSeries = i + v->resolutionCount;
                    if (index < nextSeries)
                      break;
                    i = nextSeries;
                  }
                else
                  {
                    boost::format fmt("Invalid series (null core[%1%]): %2%");
                    fmt % idx % series;
                    throw std::logic_error(fmt.str());
                  }
                ++series;
              }
          }
        return series;
      }

      dimension_size_type
      FormatReader::getResolutionCount() const
      {
        assertId(currentId, true);

        dimension_size_type count = 1;
        if (!hasFlattenedResolutions())
          count = core.at(seriesToCoreIndex(getSeries()))->resolutionCount;

        return count;
      }

      void
      FormatReader::setResolution(dimension_size_type resolution) const
      {
        if (resolution >= getResolutionCount())
          {
            boost::format fmt("Invalid resolution: %1%");
            fmt % resolution;
            throw std::logic_error(fmt.str());
          }
        this->coreIndex = seriesToCoreIndex(getSeries()) + resolution;
        // this->series unchanged.
        this->resolution = resolution;
        this->plane = 0;
      }

      dimension_size_type
      FormatReader::getResolution() const
      {
        return resolution;
      }

      bool
      FormatReader::hasFlattenedResolutions() const
      {
        return flattenedResolutions;
      }

      void
      FormatReader::setFlattenedResolutions(bool flatten)
      {
        assertId(currentId, false);
        flattenedResolutions = flatten;
      }

      dimension_size_type
      FormatReader::getCoreIndex() const
      {
        return coreIndex;
      }

      void
      FormatReader::setCoreIndex(dimension_size_type index) const
      {
        if (index >= core.size())
          {
            boost::format fmt("Invalid core index: %1%");
            fmt % index;
            throw std::logic_error(fmt.str());
          }
        this->series = coreIndexToSeries(index);
        this->coreIndex = index;
        this->resolution = index - seriesToCoreIndex(this->series);
        this->plane = 0;
      }

      void
      FormatReader::setId(const boost::filesystem::path& id)
      {
        // Attempt to canonicalize the path.
        path canonicalpath = id;
        try
          {
            canonicalpath = ome::common::canonical(id);
          }
        catch (const std::exception& /* e */)
          {
          }

        //    LOGGER.debug("{} initializing {}", getFormat(), id);
        if (!currentId || canonicalpath != currentId.get())
          {
            initFile(canonicalpath);

            const ome::compat::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>& store =
              ome::compat::dynamic_pointer_cast< ::ome::xml::meta::OMEXMLMetadata>(getMetadataStore());
            if(store)
              {
                if(saveOriginalMetadata)
                  {
                    MetadataMap allMetadata(metadata);

                    setSeries(0);
                    {
                      SaveSeries(*this);
                      for (dimension_size_type series = 0;
                           series < getSeriesCount();
                           ++series)
                        {
                          boost::format fmt("Series %1%");
                          fmt % series;
                          std::string name(fmt.str());

                          try
                            {
                              std::string imageName = store->getImageName(series);
                              if (!imageName.empty() && ome::common::trim(imageName).size() != 0)
                                name = imageName;
                            }
                          catch (std::exception& e)
                            {
                            }
                          setSeries(series);
                          const MetadataMap& sm(getSeriesMetadata());
                          for (MetadataMap::const_iterator i = sm.begin();
                               i != sm.end();
                               ++i)
                            allMetadata.set(name + " " + i->first, i->second);
                        }
                    }

                    fillOriginalMetadata(*store, allMetadata);
                  }

                setSeries(0);
                {
                  SaveSeries(*this);

                  for (dimension_size_type series = 0;
                       series < getSeriesCount();
                       ++series)
                    {
                      setSeries(series);

                      if (getModuloZ().size() > 0 || getModuloC().size() > 0 ||
                          getModuloT().size() > 0)
                        {
                          /**
                           * @todo Implement addModuloAlong.  Requires bits of MetadataTools OMEXMLServiceImpl.
                           */
                          // addModuloAlong(store, core.get(series), series);
                        }
                    }
                }
              }
          }
      }

    }
  }
}

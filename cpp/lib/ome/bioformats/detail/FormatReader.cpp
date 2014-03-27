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

#include <cmath>
#include <fstream>

#include <boost/filesystem/operations.hpp>
#include <boost/filesystem/path.hpp>

#include <ome/compat/mstream.h>
#include <ome/compat/regex.h>
#include <ome/compat/string.h>

#include <ome/bioformats/FormatTools.h>
#include <ome/bioformats/PixelBuffer.h>
#include <ome/bioformats/PixelProperties.h>
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
        metadataStore(std::make_shared<DummyMetadata>()),
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

      const std::vector<std::string>&
      FormatReader::getSuffixes() const
      {
        return readerProperties.suffixes;
      }

      const std::vector<std::string>&
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
      FormatReader::initFile(const std::string& id)
      {
        if (currentId)
          {
            const std::vector<std::string>& s = getUsedFiles();
            for (std::vector<std::string>::const_iterator i = s.begin();
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
        core.push_back(std::make_shared<CoreMetadata>());

        // reinitialize the MetadataStore
        // NB: critical for metadata conversion to work properly!
        getMetadataStore()->createRoot();
      }

      bool
      FormatReader::isUsedFile(const std::string& file)
      {
        bool used = false;
        path thisfile = boost::filesystem::canonical(path(file));

        /// @todo: Use a set rather than a list?
        const std::vector<std::string>& s = getUsedFiles();
        for (std::vector<std::string>::const_iterator i = s.begin();
             i != s.end();
             ++i)
          {
            path usedfile = boost::filesystem::canonical(path(*i));
            if (thisfile == usedfile)
              {
                used = true;
                break;
              }
          }

        return used;
      }

      void
      FormatReader::readPlane(std::istream& source,
                              PixelBufferRaw& dest,
                              dimension_size_type x,
                              dimension_size_type y,
                              dimension_size_type w,
                              dimension_size_type h)
      {
        return readPlane(source, dest, x, y, w, h, 0);
      }

      void
      FormatReader::readPlane(std::istream& source,
                              PixelBufferRaw& dest,
                              dimension_size_type x,
                              dimension_size_type y,
                              dimension_size_type w,
                              dimension_size_type h,
                              dimension_size_type scanlinePad)
      {
        /**
         * @todo There is a safety issue ensuring that each of the
         * stream reads doesn't overflow the buffer.  Currently this
         * is not checked.
         */
        dimension_size_type c = getRGBChannelCount();
        uint32_t bpp = bytesPerPixel(getPixelType());

        if (dest.size() <  h * w * bpp * c)
          throw std::logic_error("readPlane: Buffer too small");
        if (dest.size() >  h * w * bpp * c)
          throw std::logic_error("readPlane: Buffer too large");

        if (x == 0 && y == 0 &&
            w == getSizeX() &&
            h == getSizeY() &&
            scanlinePad == 0)
          {
            // Read whole plane into the buffer directly.
            source >> dest;
          }
        else if (x == 0 &&
                 w == getSizeX() &&
                 scanlinePad == 0)
          {
            if (isInterleaved())
              {
                source.seekg(static_cast<std::istream::off_type>(y * w * bpp * c), std::ios::cur);
                source.read(reinterpret_cast<char *>(dest.buffer()),
                            static_cast<std::streamsize>(h * w * bpp * c));
              }
            else
              {
                dimension_size_type rowLen = w * bpp;
                for (dimension_size_type channel = 0; channel < c; ++channel)
                  {
                    source.seekg(static_cast<std::istream::off_type>(y * rowLen), std::ios::cur);
                    source.read(reinterpret_cast<char *>(dest.buffer())
                                + (channel * h * rowLen),
                                static_cast<std::streamsize>(h * rowLen));
                    // no need to skip bytes after reading final channel
                    if (channel < c - 1)
                      source.seekg(static_cast<std::istream::off_type>((getSizeY() - y - h) * rowLen), std::ios::cur);
                  }
              }
          }
        else
          {
            dimension_size_type scanlineWidth = getSizeX() + scanlinePad;
            if (isInterleaved())
              {
                source.seekg(static_cast<std::istream::off_type>(y * scanlineWidth * bpp * c), std::ios::cur);
                for (dimension_size_type row = 0; row < h; ++row)
                  {
                    source.seekg(static_cast<std::istream::off_type>(x * bpp * c), std::ios::cur);
                    source.read(reinterpret_cast<char *>(dest.buffer())
                                + (row * w * bpp * c),
                                static_cast<std::streamsize>(w * bpp * c));
                      // no need to skip bytes after reading final row
                    if (row < h - 1)
                      source.seekg(static_cast<std::istream::off_type>(bpp * c * (scanlineWidth - w - x)), std::ios::cur);
                  }
              }
            else
              {
                for (dimension_size_type channel = 0; channel < c; ++channel)
                  {
                    source.seekg(static_cast<std::istream::off_type>(y * scanlineWidth * bpp), std::ios::cur);
                    for (dimension_size_type row = 0; row < h; ++row)
                      {
                        source.seekg(static_cast<std::istream::off_type>(x * bpp), std::ios::cur);
                        source.read(reinterpret_cast<char *>(dest.buffer())
                                    + (channel * w * h * bpp + row * w * bpp),
                                    static_cast<std::streamsize>(w * bpp));
                        // no need to skip bytes after reading final row of final channel
                        if (row < h - 1 || channel < c - 1)
                          source.seekg(static_cast<std::istream::off_type>(bpp * (scanlineWidth - w - x)), std::ios::cur);
                      }
                    if (channel < c - 1)
                      // no need to skip bytes after reading final channel
                      source.seekg(static_cast<std::istream::off_type>(scanlineWidth * bpp * (getSizeY() - y - h)), std::ios::cur);
                  }
              }
          }
        if (!source)
          throw std::runtime_error("readPlane: Error reading bytes from stream");
      }

      std::shared_ptr< ::ome::xml::meta::MetadataStore>
      FormatReader::makeFilterMetadata()
      {
        // While std::make_shared<> works, here, boost::make_shared<>
        // does not, so use new directly.
        return std::shared_ptr< ::ome::xml::meta::MetadataStore>
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
      FormatReader::isThisType(const std::string& name,
                               bool               open)
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

        std::ifstream ifs(name.c_str());
        if (!ifs)
          return false;

        return isThisType(ifs);
      }

      bool
      FormatReader::isThisType(const uint8_t *begin,
                               const uint8_t *end)
      {
        imstream ims(reinterpret_cast<const char *>(begin),
                     reinterpret_cast<const char *>(end));
        return isThisType(ims);
      }

      bool
      FormatReader::isThisType(const uint8_t *begin,
                               std::size_t    length)
      {
        imstream ims(reinterpret_cast<const char *>(begin), length);
        return isThisType(ims);
      }

      bool
      FormatReader::isThisType(std::istream& /* stream */)
      {
        return false;
      }

      image_size_type
      FormatReader::getImageCount() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).imageCount;
      }

      bool
      FormatReader::isRGB() const
      {
        assertId(currentId, true);
        return getCoreMetadata(getCoreIndex()).rgb;
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
        return getCoreMetadata(getCoreIndex()).sizeC;
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
        // NB: by definition, imageCount == effectiveSizeC * sizeZ * sizeT
        dimension_size_type sizeZT = getSizeZ() * getSizeT();
        dimension_size_type effC = 0;

        if (sizeZT)
          effC = getImageCount() / sizeZT;

        return effC;
      }

      dimension_size_type
      FormatReader::getRGBChannelCount() const
      {
        dimension_size_type effC = getEffectiveSizeC();
        dimension_size_type rgbC = 0;

        if (effC)
          rgbC = getSizeC() / effC;

        return rgbC;
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
      FormatReader::get8BitLookupTable(PixelBufferRaw& /* buf */) const
      {
        throw std::runtime_error("Reader does not implement 8-bit lookup tables");
      }

      void
      FormatReader::get16BitLookupTable(PixelBufferRaw& /* buf */) const
      {
        throw std::runtime_error("Reader does not implement 16-bit lookup tables");
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

      std::array<dimension_size_type, 2>
      FormatReader::getThumbSize() const
      {
        assertId(currentId, true);

        std::array<dimension_size_type, 2> ret;
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
      FormatReader::openBytes(image_size_type no,
                              PixelBufferRaw& buf) const
      {
        openBytes(no, buf, 0, 0, getSizeX(), getSizeY());
      }

      void
      FormatReader::openThumbBytes(image_size_type /* no */,
                                   PixelBufferRaw& /* buf */) const
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
          in = std::shared_ptr<std::istream>(); // set to null.
        if (!fileOnly)
          {
            currentId = boost::none;
            coreIndex = series = resolution = 0;
            core.clear();
          }
      }

      image_size_type
      FormatReader::getSeriesCount() const
      {
        assertId(currentId, true);
        image_size_type size = core.size();
        if (!hasFlattenedResolutions()) {
          size = coreIndexToSeries(core.size() - 1) + 1;
        }
        return size;
      }

      void
      FormatReader::setSeries(image_size_type no) const
      {
        coreIndex = seriesToCoreIndex(no);
        series = no;
        resolution = 0;
      }

      image_size_type
      FormatReader::getSeries() const
      {
        return series;
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

      const std::vector<std::string>
      FormatReader::getUsedFiles(bool noPixels) const
      {
        SaveSeries sentry(*this);
        image_size_type oldSeries = getSeries();
        std::set<std::string> files;
        for (image_size_type i = 0; i < getSeriesCount(); ++i)
          {
            setSeries(i);
            std::vector<std::string> s = getSeriesUsedFiles(noPixels);
            for (std::vector<std::string>::const_iterator file = s.begin();
                 file != s.end();
                 ++file)
              {
                files.insert(*file);
              }
          }
        setSeries(oldSeries);
        return std::vector<std::string>(files.begin(), files.end());
      }

      const std::vector<std::string>
      FormatReader::getSeriesUsedFiles(bool noPixels) const
      {
        std::vector<std::string> ret;
        if (!noPixels && currentId)
          ret.push_back(currentId.get());
        return ret;
      }

      std::vector<FileInfo>
      FormatReader::getAdvancedUsedFiles(bool noPixels) const
      {
        std::vector<std::string> files = getUsedFiles(noPixels);
        std::vector<FileInfo> infos(files.size());

        for (std::vector<std::string>::iterator file = files.begin();
             file != files.end();
             ++file)
          {
            FileInfo info;
            info.filename = *file;
            info.reader = getFormat();
            info.usedToInitialize = false;

            const boost::optional<std::string> currentid = getCurrentFile();
            if (currentid)
              {
                path current = boost::filesystem::canonical(path(currentid.get()));
                path thisfile = boost::filesystem::canonical(path(*file));

                info.usedToInitialize = (thisfile == current);
              }

            infos.push_back(info);
          }
        return infos;
      }

      std::vector<FileInfo>
      FormatReader::getAdvancedSeriesUsedFiles(bool noPixels) const
      {
        std::vector<std::string> files = getSeriesUsedFiles(noPixels);
        std::vector<FileInfo> infos(files.size());

        for (std::vector<std::string>::iterator file = files.begin();
             file != files.end();
             ++file)
          {
            FileInfo info;
            info.filename = *file;
            info.reader = getFormat();
            info.usedToInitialize = false;

            const boost::optional<std::string> currentid = getCurrentFile();
            if (currentid)
              {
                path current = boost::filesystem::canonical(path(currentid.get()));
                path thisfile = boost::filesystem::canonical(path(*file));

                info.usedToInitialize = (thisfile == current);
              }

            infos.push_back(info);
          }
        return infos;
      }

      const boost::optional<std::string>&
      FormatReader::getCurrentFile() const
      {
        return currentId;
      }

      dimension_size_type
      FormatReader::getIndex(dimension_size_type z,
                             dimension_size_type c,
                             dimension_size_type t)
      {
        assertId(currentId, true);
        return ome::bioformats::getIndex(getDimensionOrder(),
                                         getSizeZ(),
                                         getEffectiveSizeC(),
                                         getSizeT(),
                                         getImageCount(),
                                         z, c, t);
      }

      std::array<dimension_size_type, 3>
      FormatReader::getZCTCoords(dimension_size_type index)
      {
        assertId(currentId, true);
        return ome::bioformats::getZCTCoords(getDimensionOrder(),
                                             getSizeZ(),
                                             getEffectiveSizeC(),
                                             getSizeT(),
                                             getImageCount(),
                                             index);
      }

      const MetadataMap::value_type&
      FormatReader::getMetadataValue(const std::string& field)
      {
        return metadata.get<MetadataMap::value_type>(field);
      }

      const MetadataMap::value_type&
      FormatReader::getSeriesMetadataValue(const MetadataMap::key_type& field)
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

      const std::vector<std::shared_ptr< ::ome::bioformats::CoreMetadata> >&
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
      FormatReader::setMetadataStore(std::shared_ptr< ::ome::xml::meta::MetadataStore>& store)
      {
        assertId(currentId, false);

        if (!store)
          throw std::logic_error("MetadataStore can not be null");

        metadataStore = store;
      }

      const std::shared_ptr< ::ome::xml::meta::MetadataStore>&
      FormatReader::getMetadataStore() const
      {
        return metadataStore;
      }

      std::shared_ptr< ::ome::xml::meta::MetadataStore>&
      FormatReader::getMetadataStore()
      {
        return metadataStore;
      }

      std::vector<std::shared_ptr< ::ome::bioformats::FormatReader> >
      FormatReader::getUnderlyingReaders() const
      {
        return std::vector<std::shared_ptr< ::ome::bioformats::FormatReader> >();
      }

      bool
      FormatReader::isSingleFile(const std::string& /* id */) const
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
      FormatReader::getOptimalTileWidth() const
      {
        assertId(currentId, true);
        return getSizeX();
      }

      dimension_size_type
      FormatReader::getOptimalTileHeight() const
      {
        assertId(currentId, true);
        uint32_t bpp = bytesPerPixel(getPixelType());
        dimension_size_type maxHeight = (1024 * 1024) / (getSizeX() * getRGBChannelCount() * bpp);
        return std::min(maxHeight, getSizeY());
      }

      image_size_type
      FormatReader::seriesToCoreIndex(image_size_type series) const
      {
        image_size_type index = 0;

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
            image_size_type idx = 0;
            for (coremetadata_list_type::const_iterator i = core.begin();
                 i != core.end();
                 ++i, ++idx)
              {
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
          }

        return index;
      }

      image_size_type
      FormatReader::coreIndexToSeries(image_size_type index) const
      {
        image_size_type series = 0;

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
            image_size_type idx = 0;

            for (coremetadata_list_type::size_type i = 0; i < index;)
              {
                const coremetadata_list_type::value_type& v(core.at(i));
                if (v)
                  {
                    image_size_type nextSeries = i + v->resolutionCount;
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

      image_size_type
      FormatReader::getResolutionCount() const
      {
        assertId(currentId, true);

        image_size_type count = 1;
        if (!hasFlattenedResolutions())
          count = core.at(seriesToCoreIndex(getSeries()))->resolutionCount;

        return count;
      }

      void
      FormatReader::setResolution(image_size_type resolution) const
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
      }

      image_size_type
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

      image_size_type
      FormatReader::getCoreIndex() const
      {
        return coreIndex;
      }

      void
      FormatReader::setCoreIndex(image_size_type index) const
      {
        if (index >= core.size())
          {
            boost::format fmt("Invalid core index: %1%");
            fmt % index;
            throw std::logic_error(fmt.str());
          }
        this->coreIndex = index;
        this->series = coreIndexToSeries(index);
        this->resolution = index - seriesToCoreIndex(this->series);
      }

      void
      FormatReader::setId(const std::string& id)
      {
        //    LOGGER.debug("{} initializing {}", getFormat(), id);
        if (!currentId || id != currentId.get())
          {
            initFile(id);

            const std::shared_ptr< ::ome::xml::meta::OMEXMLMetadata>& store =
              std::dynamic_pointer_cast< ::ome::xml::meta::OMEXMLMetadata>(getMetadataStore());
            if(store)
              {
                if(saveOriginalMetadata)
                  {
                    MetadataMap allMetadata(metadata);

                    setSeries(0);
                    {
                      SaveSeries(*this);
                      for (image_size_type series = 0;
                           series < getSeriesCount();
                           ++series)
                        {
                          boost::format fmt("Series %1%");
                          fmt % series;
                          std::string name(fmt.str());

                          try
                            {
                              std::string imageName = store->getImageName(series);
                              if (!imageName.empty() && trim(imageName).size() != 0)
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

                    /**
                     * @todo Implement populateOriginalMetadata.  Requires bits of MetadataTools and OMEXMLServiceImpl.
                     */
                    // populateOriginalMetadata(store, allMetadata.flatten());
                  }

                setSeries(0);
                {
                  SaveSeries(*this);

                  for (image_size_type series = 0;
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

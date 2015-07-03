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
#include <ome/bioformats/PixelBuffer.h>
#include <ome/bioformats/PixelProperties.h>
#include <ome/bioformats/VariantPixelBuffer.h>
#include <ome/bioformats/detail/FormatWriter.h>

#include <ome/xml/meta/DummyMetadata.h>
#include <ome/xml/meta/FilterMetadata.h>
#include <ome/xml/meta/MetadataStore.h>
#include <ome/xml/meta/OMEXMLMetadata.h>

using boost::filesystem::path;
using ome::xml::meta::DummyMetadata;
using ome::xml::meta::FilterMetadata;
using ome::xml::meta::MetadataException;
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

      FormatWriter::FormatWriter(const WriterProperties& writerProperties):
        writerProperties(writerProperties),
        currentId(boost::none),
        out(),
        series(0),
        plane(0),
        compression(boost::none),
        interleaved(boost::none),
        sequential(false),
        framesPerSecond(0),
        metadataRetrieve(ome::compat::make_shared<DummyMetadata>())
      {
        assertId(currentId, false);
      }

      FormatWriter::~FormatWriter()
      {
        try
          {
            close();
          }
        catch (...)
          {
          }
      }

      void
      FormatWriter::setId(const boost::filesystem::path& id)
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

        if (!currentId || canonicalpath != currentId.get())
          {
            if (out)
              out = ome::compat::shared_ptr<std::ostream>();

            currentId = canonicalpath;
          }
      }

      void
      FormatWriter::close(bool fileOnly)
      {
        if (out)
          out.reset(); // set to null.
        if (!fileOnly)
          {
            currentId = boost::none;
            series = 0;
            plane = 0;
            compression = boost::none;
            sequential = false;
            framesPerSecond = 0;
            metadataRetrieve.reset();
          }
      }

      bool
      FormatWriter::isThisType(const boost::filesystem::path& name,
                               bool                           /* open */) const
      {
        return checkSuffix(name,
                           writerProperties.suffixes,
                           writerProperties.compression_suffixes);
      }

      dimension_size_type
      FormatWriter::getSeriesCount() const
      {
        return metadataRetrieve->getImageCount();
      }

      void
      FormatWriter::setLookupTable(dimension_size_type       /* plane */,
                                   const VariantPixelBuffer& /* buf */)
      {
        assertId(currentId, true);

        throw std::runtime_error("Writer does not implement lookup tables");
      }

      void
      FormatWriter::saveBytes(dimension_size_type plane,
                              VariantPixelBuffer& buf)
      {
        assertId(currentId, true);

        dimension_size_type width = metadataRetrieve->getPixelsSizeX(getSeries());
        dimension_size_type height = metadataRetrieve->getPixelsSizeY(getSeries());
        saveBytes(plane, buf, 0, 0, width, height);
      }

      void
      FormatWriter::setSeries(dimension_size_type series) const
      {
        assertId(currentId, true);

        if (series >= getSeriesCount())
          {
            boost::format fmt("Invalid series: %1%");
            fmt % series;
            throw std::logic_error(fmt.str());
          }

        const dimension_size_type currentSeries = getSeries();
        if (currentSeries != series &&
            (series > 0 && currentSeries != series - 1))
          {
            boost::format fmt("Series set out of order: %1% (currently %2%)");
            fmt % series % currentSeries;
            throw std::logic_error(fmt.str());
          }

        this->series = series;
        this->plane = 0U;
      }

      dimension_size_type
      FormatWriter::getSeries() const
      {
        assertId(currentId, true);

        return series;
      }

      void
      FormatWriter::setPlane(dimension_size_type plane) const
      {
        assertId(currentId, true);

        if (plane >= getImageCount())
          {
            boost::format fmt("Invalid plane: %1%");
            fmt % plane;
            throw std::logic_error(fmt.str());
          }

        const dimension_size_type currentPlane = getPlane();
        if (currentPlane != plane &&
            (plane > 0 && currentPlane != plane - 1))
          {
            boost::format fmt("Plane set out of order: %1% (currently %2%)");
            fmt % plane % currentPlane;
            throw std::logic_error(fmt.str());
          }

        this->plane = plane;
      }

      dimension_size_type
      FormatWriter::getPlane() const
      {
        assertId(currentId, true);

        return plane;
      }

      void
      FormatWriter::setFramesPerSecond(frame_rate_type rate)
      {
        framesPerSecond = rate;
      }

      FormatWriter::frame_rate_type
      FormatWriter::getFramesPerSecond() const
      {
        return framesPerSecond;
      }

      const std::set<ome::xml::model::enums::PixelType>&
      FormatWriter::getPixelTypes() const
      {
        return getPixelTypes("default");
      }

      const std::set<ome::xml::model::enums::PixelType>&
      FormatWriter::getPixelTypes(const std::string& codec) const
      {
        static std::set<ome::xml::model::enums::PixelType> empty;
        WriterProperties::codec_pixel_type_map::const_iterator ci =
          writerProperties.codec_pixel_types.find(codec);
        if (ci != writerProperties.codec_pixel_types.end())
          return ci->second;
        return empty;
      }

      bool
      FormatWriter::isSupportedType(ome::xml::model::enums::PixelType type) const
      {
        return isSupportedType(type, "default");
      }

      bool
      FormatWriter::isSupportedType(ome::xml::model::enums::PixelType type,
                                    const std::string&                codec) const
      {
        std::set<ome::xml::model::enums::PixelType> pixel_types = getPixelTypes(codec);
        std::set<ome::xml::model::enums::PixelType>::const_iterator i = pixel_types.find(type);
        return i != pixel_types.end();
      }

      void
      FormatWriter::setCompression(const std::string& compression)
      {
        std::set<std::string>::const_iterator i = writerProperties.compression_types.find(compression);
        if (i == writerProperties.compression_types.end())
          {
            boost::format fmt("Invalid compression type: %1%");
            fmt % compression;
            throw std::logic_error(fmt.str());
          }

        this->compression = compression;
      }

      const boost::optional<std::string>&
      FormatWriter::getCompression() const
      {
        return this->compression;
      }

      void
      FormatWriter::setInterleaved(bool interleaved)
      {
        this->interleaved = interleaved;
      }

      const boost::optional<bool>&
      FormatWriter::getInterleaved() const
      {
        return interleaved;
      }

      void
      FormatWriter::changeOutputFile(const boost::filesystem::path& id)
      {
        assertId(currentId, true);

        setId(id);
      }

      void
      FormatWriter::setWriteSequentially(bool sequential)
      {
        this->sequential = sequential;
      }

      bool
      FormatWriter::getWriteSequentially() const
      {
        return sequential;
      }

      void
      FormatWriter::setMetadataRetrieve(ome::compat::shared_ptr< ::ome::xml::meta::MetadataRetrieve>& retrieve)
      {
        assertId(currentId, false);

        if (!retrieve)
          throw std::logic_error("MetadataStore can not be null");

        metadataRetrieve = retrieve;
      }

      const ome::compat::shared_ptr< ::ome::xml::meta::MetadataRetrieve>&
      FormatWriter::getMetadataRetrieve() const
      {
        return metadataRetrieve;
      }

      ome::compat::shared_ptr< ::ome::xml::meta::MetadataRetrieve>&
      FormatWriter::getMetadataRetrieve()
      {
        return metadataRetrieve;
      }

      dimension_size_type
      FormatWriter::getImageCount() const
      {
        return getSizeZ() * getSizeT() * getEffectiveSizeC();
      }

      bool
      FormatWriter::isRGB(dimension_size_type channel) const
      {
        return getRGBChannelCount(channel) > 1U;
      }

      dimension_size_type
      FormatWriter::getSizeX() const
      {
        dimension_size_type series = getSeries();
        dimension_size_type sizeX = metadataRetrieve->getPixelsSizeX(series);
        if (sizeX == 0U)
          sizeX = 1U;
        return sizeX;
      }

      dimension_size_type
      FormatWriter::getSizeY() const
      {
        dimension_size_type series = getSeries();
        dimension_size_type sizeY = metadataRetrieve->getPixelsSizeY(series);
        if (sizeY == 0U)
          sizeY = 1U;
        return sizeY;
      }

      dimension_size_type
      FormatWriter::getSizeZ() const
      {
        dimension_size_type series = getSeries();
        dimension_size_type sizeZ = metadataRetrieve->getPixelsSizeZ(series);
        if (sizeZ == 0U)
          sizeZ = 1U;
        return sizeZ;
      }

      dimension_size_type
      FormatWriter::getSizeT() const
      {
        dimension_size_type series = getSeries();
        dimension_size_type sizeT = metadataRetrieve->getPixelsSizeT(series);
        if (sizeT == 0U)
          sizeT = 1U;
        return sizeT;
      }

      dimension_size_type
      FormatWriter::getSizeC() const
      {
        dimension_size_type series = getSeries();
        dimension_size_type sizeC = metadataRetrieve->getPixelsSizeC(series);
        if (sizeC == 0U)
          sizeC = 1U;
        return sizeC;
      }

      ome::xml::model::enums::PixelType
      FormatWriter::getPixelType() const
      {
        dimension_size_type series = getSeries();
        return metadataRetrieve->getPixelsType(series);
      }

      pixel_size_type
      FormatWriter::getBitsPerPixel() const
      {
        dimension_size_type series = getSeries();
        return metadataRetrieve->getPixelsSignificantBits(series);
      }

      dimension_size_type
      FormatWriter::getEffectiveSizeC() const
      {
        dimension_size_type series = getSeries();
        return metadataRetrieve->getChannelCount(series);
      }

      dimension_size_type
      FormatWriter::getRGBChannelCount(dimension_size_type channel) const
      {
        dimension_size_type series = getSeries();

        dimension_size_type samples = 1U;

        try
          {
            samples = metadataRetrieve->getChannelSamplesPerPixel(series, channel);
          }
        catch (const MetadataException& e)
          {
            // No SamplesPerPixel; default to 1.
          }

        return samples;
      }

      const std::string&
      FormatWriter::getDimensionOrder() const
      {
        dimension_size_type series = getSeries();
        return metadataRetrieve->getPixelsDimensionOrder(series);
      }

      dimension_size_type
      FormatWriter::getIndex(dimension_size_type z,
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

      ome::compat::array<dimension_size_type, 3>
      FormatWriter::getZCTCoords(dimension_size_type index) const
      {
        assertId(currentId, true);
        return ome::bioformats::getZCTCoords(getDimensionOrder(),
                                             getSizeZ(),
                                             getEffectiveSizeC(),
                                             getSizeT(),
                                             getImageCount(),
                                             index);
      }

      const std::string&
      FormatWriter::getFormat() const
      {
        return writerProperties.name;
      }

      const std::string&
      FormatWriter::getFormatDescription() const
      {
        return writerProperties.description;
      }

      const std::vector<boost::filesystem::path>&
      FormatWriter::getSuffixes() const
      {
        return writerProperties.suffixes;
      }

      const std::vector<boost::filesystem::path>&
      FormatWriter::getCompressionSuffixes() const
      {
        return writerProperties.compression_suffixes;
      }

      const std::set<std::string>&
      FormatWriter::getCompressionTypes() const
      {
        return writerProperties.compression_types;
      }

      bool
      FormatWriter::canDoStacks() const
      {
        return writerProperties.stacks;
      }

    }
  }
}

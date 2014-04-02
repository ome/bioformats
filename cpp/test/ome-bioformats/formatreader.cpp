/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
 * Copyright © 2006 - 2013 Open Microscopy Environment:
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

#include <stdexcept>

#include <ome/bioformats/FormatReader.h>
#include <ome/bioformats/PixelBuffer.h>
#include <ome/bioformats/PixelProperties.h>
#include <ome/bioformats/detail/FormatReader.h>

#include <ome/xml/meta/MetadataStore.h>
#include <ome/xml/meta/OMEXMLMetadata.h>

#include <ome/test/config.h>

#include <boost/filesystem/operations.hpp>

#include <gtest/gtest.h>

using ome::bioformats::CoreMetadata;
using ome::bioformats::FormatReader;
using ome::bioformats::PixelBufferRaw;
using ome::bioformats::detail::ReaderProperties;
using ome::bioformats::MetadataMap;
using ome::bioformats::MetadataOptions;
using ome::bioformats::dimension_size_type;
using ome::xml::meta::MetadataStore;
using ome::xml::meta::OMEXMLMetadata;
using ome::xml::model::enums::PixelType;

namespace
{

  ReaderProperties
  test_properties()
  {
    ReaderProperties p;

    p.name = "TestReader";
    p.description = "Reader for unit testing";
    p.suffixes.push_back(".test");
    p.compression_suffixes.push_back("gz");
    p.metadata_levels.insert(MetadataOptions::METADATA_MINIMUM);
    p.metadata_levels.insert(MetadataOptions::METADATA_NO_OVERLAYS);
    p.metadata_levels.insert(MetadataOptions::METADATA_ALL);

    return p;
  }

  const ReaderProperties props(test_properties());

  std::shared_ptr<CoreMetadata>
  makeCore()
  {
    std::shared_ptr<CoreMetadata> c(std::make_shared<CoreMetadata>());

    c->sizeX = 512;
    c->sizeY = 1024;
    c->sizeZ = 20;
    c->sizeT = 4;
    c->sizeC = 2;
    c->pixelType = PixelType::FLOAT;
    c->imageCount = c->sizeZ * c->sizeT * c->sizeC;
    c->orderCertain = true;
    c->rgb = false;
    c->littleEndian = false;
    c->interleaved = false;
    c->indexed = false;
    c->falseColor = true;
    c->metadataComplete = false;
    c->thumbnail = false;
    c->resolutionCount = 1;

    return c;
  }
}

class FormatReaderCustom : public ::ome::bioformats::detail::FormatReader
{
public:
  FormatReaderCustom():
    ::ome::bioformats::detail::FormatReader(props)
  {
    domains.push_back("Test domain");
  }

  virtual ~FormatReaderCustom()
  {
  }

  void
  openBytes(dimension_size_type no,
            PixelBufferRaw&     buf) const
  {
    ::ome::bioformats::detail::FormatReader::openBytes(no, buf);
  }

  void
  openBytes(dimension_size_type /* no */,
            PixelBufferRaw&     /* buf */,
            dimension_size_type /* x */,
            dimension_size_type /* y */,
            dimension_size_type /* w */,
            dimension_size_type /*h */) const
  {
    assertId(currentId, true);
    return;
  }

  void
  initFile(const std::string& id)
  {
    ::ome::bioformats::detail::FormatReader::initFile(id);

    if (id == "test" || id == "flat")
      {
        metadata["Institution"] = "University of Dundee";

        // 4 series
        core.clear();
        core.push_back(makeCore());
        core.back()->seriesMetadata["Organism"] = "Mus musculus";
        core.push_back(makeCore());
        core.push_back(makeCore());
        core.push_back(makeCore());
      }
    else if (id == "subres")
      {
        // 5 series, 3 with subresolutions
        core.clear();
        {
          std::shared_ptr<CoreMetadata> c(makeCore());
          c->resolutionCount = 3;
          core.push_back(c);
          core.push_back(makeCore());
          core.push_back(makeCore());
        }

        {
          std::shared_ptr<CoreMetadata> c(makeCore());
          c->resolutionCount = 2;
          core.push_back(c);
          core.push_back(makeCore());
        }

        core.push_back(makeCore());
        core.push_back(makeCore());

        {
          std::shared_ptr<CoreMetadata> c(makeCore());
          c->resolutionCount = 2;
          core.push_back(c);
          core.push_back(makeCore());
        }
      }
  }

  bool
  isUsedFile(const std::string& file)
  {
    return ::ome::bioformats::detail::FormatReader::isUsedFile(file);
  }

  void
  readPlane(std::istream& source,
            PixelBufferRaw& dest,
            dimension_size_type x,
            dimension_size_type y,
            dimension_size_type w,
            dimension_size_type h)
  {
    ::ome::bioformats::detail::FormatReader::readPlane(source, dest, x, y, w, h);
  }

  void
  readPlane(std::istream& source,
            PixelBufferRaw& dest,
            dimension_size_type x,
            dimension_size_type y,
            dimension_size_type w,
            dimension_size_type h,
            dimension_size_type scanlinePad)
  {
    ::ome::bioformats::detail::FormatReader::readPlane(source, dest, x, y, w, h, scanlinePad);
  }

};

class FormatReaderTest : public ::testing::Test
{
public:
  FormatReaderCustom r;
  const FormatReader& cr;

  FormatReaderTest():
    ::testing::Test(),
    r(),
    cr(r)
  {
  }

  void SetUp()
  {
  }
};

TEST_F(FormatReaderTest, DefaultConstruct)
{
  FormatReaderCustom r;
}

TEST_F(FormatReaderTest, ReaderProperties)
{
  r.setId("test");
  ASSERT_EQ(props.name, r.getFormat());
  ASSERT_EQ(props.description, r.getFormatDescription());
  ASSERT_EQ(props.suffixes, r.getSuffixes());
  ASSERT_EQ(props.compression_suffixes, r.getCompressionSuffixes());
}

TEST_F(FormatReaderTest, IsThisType)
{
  std::string content("Invalid file content");
  std::istringstream iscontent(content);

  EXPECT_FALSE(r.isThisType("invalid.file"));
  EXPECT_FALSE(r.isThisType("invalid.file", true));
  EXPECT_FALSE(r.isThisType("invalid.file", false));

  EXPECT_FALSE(r.isThisType("valid.test"));
  EXPECT_FALSE(r.isThisType("valid.test", true));
  EXPECT_FALSE(r.isThisType("valid.test", false));

  EXPECT_FALSE(r.isThisType(reinterpret_cast<uint8_t *>(&*content.begin()),
                             reinterpret_cast<uint8_t *>(&*content.end())));
  EXPECT_FALSE(r.isThisType(reinterpret_cast<uint8_t *>(&*content.begin()),
                             content.size()));
  EXPECT_FALSE(r.isThisType(content));
}

TEST_F(FormatReaderTest, DefaultClose)
{
  EXPECT_NO_THROW(r.close());
}

TEST_F(FormatReaderTest, FlatClose)
{
  r.setId("flat");
  EXPECT_NO_THROW(r.close());
}

TEST_F(FormatReaderTest, DefaultCoreMetadata)
{
  EXPECT_THROW(r.getImageCount(), std::logic_error);
  EXPECT_THROW(r.isRGB(), std::logic_error);
  EXPECT_THROW(r.getSizeX(), std::logic_error);
  EXPECT_THROW(r.getSizeY(), std::logic_error);
  EXPECT_THROW(r.getSizeZ(), std::logic_error);
  EXPECT_THROW(r.getSizeT(), std::logic_error);
  EXPECT_THROW(r.getSizeC(), std::logic_error);
  EXPECT_THROW(r.getPixelType(), std::logic_error);
  EXPECT_THROW(r.getBitsPerPixel(), std::logic_error);
  EXPECT_THROW(r.getEffectiveSizeC(), std::logic_error);
  EXPECT_THROW(r.getRGBChannelCount(), std::logic_error);
  EXPECT_THROW(r.isIndexed(), std::logic_error);
  EXPECT_THROW(r.isFalseColor(), std::logic_error);
  EXPECT_THROW(r.getModuloZ(), std::logic_error);
  EXPECT_THROW(r.getModuloT(), std::logic_error);
  EXPECT_THROW(r.getModuloC(), std::logic_error);
  EXPECT_THROW(r.getThumbSizeX(), std::logic_error);
  EXPECT_THROW(r.getThumbSizeY(), std::logic_error);
  EXPECT_THROW(r.isLittleEndian(), std::logic_error);
  EXPECT_THROW(r.getDimensionOrder(), std::logic_error);
  EXPECT_THROW(r.isOrderCertain(), std::logic_error);
  EXPECT_THROW(r.isThumbnailSeries(), std::logic_error);
  EXPECT_THROW(r.isInterleaved(), std::logic_error);
  EXPECT_THROW(r.isInterleaved(0), std::logic_error);
  EXPECT_THROW(r.isMetadataComplete(), std::logic_error);
  EXPECT_THROW(r.getOptimalTileWidth(), std::logic_error);
  EXPECT_THROW(r.getOptimalTileHeight(), std::logic_error);
  EXPECT_THROW(r.getResolutionCount(), std::logic_error);
}

TEST_F(FormatReaderTest, FlatCoreMetadata)
{
  r.setId("flat");

  EXPECT_EQ(160U, r.getImageCount());
  EXPECT_EQ(false, r.isRGB());
  EXPECT_EQ(512U, r.getSizeX());
  EXPECT_EQ(1024U, r.getSizeY());
  EXPECT_EQ(20U, r.getSizeZ());
  EXPECT_EQ(4U, r.getSizeT());
  EXPECT_EQ(2U, r.getSizeC());
  EXPECT_EQ(PixelType::FLOAT, r.getPixelType());
  EXPECT_EQ(32U, r.getBitsPerPixel());
  EXPECT_EQ(2U, r.getEffectiveSizeC());
  EXPECT_EQ(1U, r.getRGBChannelCount());
  EXPECT_EQ(false, r.isIndexed());
  EXPECT_EQ(true, r.isFalseColor());
  EXPECT_NO_THROW(r.getModuloZ());
  EXPECT_NO_THROW(r.getModuloT());
  EXPECT_NO_THROW(r.getModuloC());
  EXPECT_EQ(1U, r.getThumbSizeX());
  EXPECT_EQ(1U, r.getThumbSizeY());
  EXPECT_EQ(false, r.isLittleEndian());
  EXPECT_EQ(std::string("XYZTC"), r.getDimensionOrder());
  EXPECT_EQ(true, r.isOrderCertain());
  EXPECT_EQ(false, r.isThumbnailSeries());
  EXPECT_EQ(false, r.isInterleaved());
  EXPECT_EQ(false, r.isInterleaved(0));
  EXPECT_EQ(false, r.isMetadataComplete());
  EXPECT_EQ(512U, r.getOptimalTileWidth());
  EXPECT_EQ(512U, r.getOptimalTileHeight());
  EXPECT_EQ(1U, r.getResolutionCount());
}

TEST_F(FormatReaderTest, SubresolutionFlattenedCoreMetadata)
{
  EXPECT_NO_THROW(r.setFlattenedResolutions(true));
  r.setId("subres");

  EXPECT_EQ(160U, r.getImageCount());
  EXPECT_EQ(false, r.isRGB());
  EXPECT_EQ(512U, r.getSizeX());
  EXPECT_EQ(1024U, r.getSizeY());
  EXPECT_EQ(20U, r.getSizeZ());
  EXPECT_EQ(4U, r.getSizeT());
  EXPECT_EQ(2U, r.getSizeC());
  EXPECT_EQ(PixelType::FLOAT, r.getPixelType());
  EXPECT_EQ(32U, r.getBitsPerPixel());
  EXPECT_EQ(2U, r.getEffectiveSizeC());
  EXPECT_EQ(1U, r.getRGBChannelCount());
  EXPECT_EQ(false, r.isIndexed());
  EXPECT_EQ(true, r.isFalseColor());
  EXPECT_NO_THROW(r.getModuloZ());
  EXPECT_NO_THROW(r.getModuloT());
  EXPECT_NO_THROW(r.getModuloC());
  EXPECT_EQ(1U, r.getThumbSizeX());
  EXPECT_EQ(1U, r.getThumbSizeY());
  EXPECT_EQ(false, r.isLittleEndian());
  EXPECT_EQ(std::string("XYZTC"), r.getDimensionOrder());
  EXPECT_EQ(true, r.isOrderCertain());
  EXPECT_EQ(false, r.isThumbnailSeries());
  EXPECT_EQ(false, r.isInterleaved());
  EXPECT_EQ(false, r.isInterleaved(0));
  EXPECT_EQ(false, r.isMetadataComplete());
  EXPECT_EQ(512U, r.getOptimalTileWidth());
  EXPECT_EQ(512U, r.getOptimalTileHeight());
  EXPECT_EQ(1U, r.getResolutionCount());
}

TEST_F(FormatReaderTest, SubresolutionUnflattenedCoreMetadata)
{
  EXPECT_NO_THROW(r.setFlattenedResolutions(false));
  r.setId("subres");

  EXPECT_EQ(160U, r.getImageCount());
  EXPECT_EQ(false, r.isRGB());
  EXPECT_EQ(512U, r.getSizeX());
  EXPECT_EQ(1024U, r.getSizeY());
  EXPECT_EQ(20U, r.getSizeZ());
  EXPECT_EQ(4U, r.getSizeT());
  EXPECT_EQ(2U, r.getSizeC());
  EXPECT_EQ(PixelType::FLOAT, r.getPixelType());
  EXPECT_EQ(32U, r.getBitsPerPixel());
  EXPECT_EQ(2U, r.getEffectiveSizeC());
  EXPECT_EQ(1U, r.getRGBChannelCount());
  EXPECT_EQ(false, r.isIndexed());
  EXPECT_EQ(true, r.isFalseColor());
  EXPECT_NO_THROW(r.getModuloZ());
  EXPECT_NO_THROW(r.getModuloT());
  EXPECT_NO_THROW(r.getModuloC());
  EXPECT_EQ(1U, r.getThumbSizeX());
  EXPECT_EQ(1U, r.getThumbSizeY());
  EXPECT_EQ(false, r.isLittleEndian());
  EXPECT_EQ(std::string("XYZTC"), r.getDimensionOrder());
  EXPECT_EQ(true, r.isOrderCertain());
  EXPECT_EQ(false, r.isThumbnailSeries());
  EXPECT_EQ(false, r.isInterleaved());
  EXPECT_EQ(false, r.isInterleaved(0));
  EXPECT_EQ(false, r.isMetadataComplete());
  EXPECT_EQ(512U, r.getOptimalTileWidth());
  EXPECT_EQ(512U, r.getOptimalTileHeight());
  EXPECT_EQ(3U, r.getResolutionCount());
}

TEST_F(FormatReaderTest, DefaultLUT)
{
  PixelBufferRaw buf(256U * 3U);
  EXPECT_THROW(r.get8BitLookupTable(buf), std::runtime_error);
  EXPECT_THROW(r.get16BitLookupTable(buf), std::runtime_error);
}

TEST_F(FormatReaderTest, FlatLUT)
{
  r.setId("flat");

  PixelBufferRaw buf(256U * 3U);
  EXPECT_THROW(r.get8BitLookupTable(buf), std::runtime_error);
  EXPECT_THROW(r.get16BitLookupTable(buf), std::runtime_error);
}

TEST_F(FormatReaderTest, DefaultSeries)
{
  EXPECT_THROW(r.getSeriesCount(), std::logic_error);
  EXPECT_THROW(r.setSeries(0), std::logic_error);
  EXPECT_EQ(0U, r.getSeries());
  EXPECT_THROW(r.seriesToCoreIndex(0), std::logic_error);
  EXPECT_THROW(r.coreIndexToSeries(0), std::logic_error);
  EXPECT_EQ(0U, r.getCoreIndex());
  EXPECT_THROW(r.setCoreIndex(0), std::logic_error);
  EXPECT_THROW(r.getResolutionCount(), std::logic_error);
  EXPECT_EQ(0U, r.getResolution());
  EXPECT_THROW(r.setResolution(0), std::logic_error);

  EXPECT_THROW(r.getIndex(0U, 0U, 0U), std::logic_error);
  EXPECT_THROW(r.getZCTCoords(0U), std::logic_error);
}

TEST_F(FormatReaderTest, FlatSeries)
{
  r.setId("flat");

  EXPECT_EQ(4U, r.getSeriesCount());
  EXPECT_NO_THROW(r.setSeries(0));
  EXPECT_EQ(0U, r.getSeries());
  EXPECT_EQ(0U, r.seriesToCoreIndex(0));
  EXPECT_EQ(0U, r.coreIndexToSeries(0));
  EXPECT_EQ(0U, r.getCoreIndex());
  EXPECT_NO_THROW(r.setCoreIndex(0));
  EXPECT_EQ(1U, r.getResolutionCount());
  EXPECT_EQ(0U, r.getResolution());
  EXPECT_NO_THROW(r.setResolution(0));

  EXPECT_EQ(0U, r.getIndex(0U, 0U, 0U));
  std::array<dimension_size_type, 3> coords;
  coords[0] = coords[1] = coords[2] = 0;
  EXPECT_EQ(coords, r.getZCTCoords(0U));
}

TEST_F(FormatReaderTest, SubresolutionFlattenedSeries)
{
  EXPECT_NO_THROW(r.setFlattenedResolutions(true));
  r.setId("subres");

  EXPECT_EQ(9U, r.getSeriesCount());
  EXPECT_NO_THROW(r.setSeries(0));
  EXPECT_EQ(0U, r.getSeries());
  EXPECT_EQ(0U, r.seriesToCoreIndex(0));
  EXPECT_EQ(0U, r.coreIndexToSeries(0));
  EXPECT_EQ(0U, r.getCoreIndex());
  EXPECT_NO_THROW(r.setCoreIndex(0));
  EXPECT_EQ(1U, r.getResolutionCount());
  EXPECT_EQ(0U, r.getResolution());
  EXPECT_NO_THROW(r.setResolution(0));

  EXPECT_EQ(0U, r.getIndex(0U, 0U, 0U));
  std::array<dimension_size_type, 3> coords;
  coords[0] = coords[1] = coords[2] = 0;
  EXPECT_EQ(coords, r.getZCTCoords(0U));
}

TEST_F(FormatReaderTest, SubresolutionUnflattenedSeries)
{
  EXPECT_NO_THROW(r.setFlattenedResolutions(false));
  r.setId("subres");

  EXPECT_EQ(5U, r.getSeriesCount());
  EXPECT_NO_THROW(r.setSeries(0));
  EXPECT_EQ(0U, r.getSeries());
  EXPECT_EQ(0U, r.seriesToCoreIndex(0));
  EXPECT_EQ(0U, r.coreIndexToSeries(0));
  EXPECT_EQ(0U, r.getCoreIndex());
  EXPECT_NO_THROW(r.setCoreIndex(0));
  EXPECT_EQ(3U, r.getResolutionCount());
  EXPECT_EQ(0U, r.getResolution());
  EXPECT_NO_THROW(r.setResolution(0));

  EXPECT_EQ(0U, r.getIndex(0U, 0U, 0));
  std::array<dimension_size_type, 3> coords;
  coords[0] = coords[1] = coords[2] = 0;
  EXPECT_EQ(coords, r.getZCTCoords(0U));
}

TEST_F(FormatReaderTest, DefaultGroupFiles)
{
  EXPECT_TRUE(r.isGroupFiles());
  EXPECT_NO_THROW(r.setGroupFiles(false));
  EXPECT_FALSE(r.isGroupFiles());
  EXPECT_EQ(FormatReader::CANNOT_GROUP, r.fileGroupOption("id"));
}

TEST_F(FormatReaderTest, DefaultFlatGroupFiles)
{
  EXPECT_TRUE(r.isGroupFiles());
  EXPECT_NO_THROW(r.setGroupFiles(false));
  EXPECT_FALSE(r.isGroupFiles());
  EXPECT_EQ(FormatReader::CANNOT_GROUP, r.fileGroupOption("id"));
}

TEST_F(FormatReaderTest, DefaultProperties)
{
  EXPECT_FALSE(r.isNormalized());
  EXPECT_NO_THROW(r.setNormalized(true));
  EXPECT_TRUE(r.isNormalized());

  EXPECT_FALSE(r.isOriginalMetadataPopulated());
  EXPECT_NO_THROW(r.setOriginalMetadataPopulated(true));
  EXPECT_TRUE(r.isOriginalMetadataPopulated());

  EXPECT_THROW(r.getDomains(), std::logic_error);

  std::vector<std::string> domains;
  domains.push_back("Test domain");
  EXPECT_EQ(domains, r.getPossibleDomains("id"));

  EXPECT_EQ(std::string("Single file"), r.getDatasetStructureDescription());

  EXPECT_TRUE(r.hasFlattenedResolutions());
  EXPECT_NO_THROW(r.setFlattenedResolutions(false));
  EXPECT_FALSE(r.hasFlattenedResolutions());
}

TEST_F(FormatReaderTest, FlatProperties)
{
  r.setId("flat");

  EXPECT_FALSE(r.isNormalized());
  EXPECT_THROW(r.setNormalized(true), std::logic_error);
  EXPECT_FALSE(r.isNormalized());

  EXPECT_FALSE(r.isOriginalMetadataPopulated());
  EXPECT_THROW(r.setOriginalMetadataPopulated(true), std::logic_error);
  EXPECT_FALSE(r.isOriginalMetadataPopulated());

  std::vector<std::string> domains;
  domains.push_back("Test domain");
  EXPECT_EQ(domains, r.getDomains());
  EXPECT_EQ(domains, r.getPossibleDomains("id"));

  EXPECT_EQ(std::string("Single file"), r.getDatasetStructureDescription());

  EXPECT_TRUE(r.hasFlattenedResolutions());
  EXPECT_THROW(r.setFlattenedResolutions(false), std::logic_error);
  EXPECT_TRUE(r.hasFlattenedResolutions());
}

TEST_F(FormatReaderTest, SubresolutionFlattenedProperties)
{
  EXPECT_NO_THROW(r.setNormalized(true));
  EXPECT_NO_THROW(r.setOriginalMetadataPopulated(true));
  EXPECT_NO_THROW(r.setFlattenedResolutions(true));
  r.setId("subres");

  EXPECT_TRUE(r.isNormalized());
  EXPECT_TRUE(r.isOriginalMetadataPopulated());

  std::vector<std::string> domains;
  domains.push_back("Test domain");
  EXPECT_EQ(domains, r.getDomains());
  EXPECT_EQ(domains, r.getPossibleDomains("id"));

  EXPECT_EQ(std::string("Single file"), r.getDatasetStructureDescription());

  EXPECT_TRUE(r.hasFlattenedResolutions());
}

TEST_F(FormatReaderTest, SubresolutionUnflattenedProperties)
{
  EXPECT_NO_THROW(r.setNormalized(false));
  EXPECT_NO_THROW(r.setOriginalMetadataPopulated(false));
  EXPECT_NO_THROW(r.setFlattenedResolutions(false));
  r.setId("subres");

  EXPECT_FALSE(r.isNormalized());
  EXPECT_FALSE(r.isOriginalMetadataPopulated());

  std::vector<std::string> domains;
  domains.push_back("Test domain");
  EXPECT_EQ(domains, r.getDomains());
  EXPECT_EQ(domains, r.getPossibleDomains("id"));

  EXPECT_EQ(std::string("Single file"), r.getDatasetStructureDescription());

  EXPECT_FALSE(r.hasFlattenedResolutions());
}

TEST_F(FormatReaderTest, UsedFiles)
{
  EXPECT_THROW(r.getUsedFiles(), std::logic_error);
  EXPECT_THROW(r.getUsedFiles(true), std::logic_error);
  EXPECT_THROW(r.getUsedFiles(false), std::logic_error);
  EXPECT_TRUE(r.getSeriesUsedFiles().empty());
  EXPECT_TRUE(r.getSeriesUsedFiles(true).empty());
  EXPECT_TRUE(r.getSeriesUsedFiles(false).empty());
  EXPECT_THROW(r.getAdvancedUsedFiles(), std::logic_error);
  EXPECT_THROW(r.getAdvancedUsedFiles(true), std::logic_error);
  EXPECT_THROW(r.getAdvancedUsedFiles(false), std::logic_error);
  EXPECT_TRUE(r.getAdvancedSeriesUsedFiles().empty());
  EXPECT_TRUE(r.getAdvancedSeriesUsedFiles(true).empty());
  EXPECT_TRUE(r.getAdvancedSeriesUsedFiles(false).empty());
}

TEST_F(FormatReaderTest, DefaultFile)
{
  EXPECT_FALSE(r.getCurrentFile());

  EXPECT_TRUE(r.isSingleFile("id"));
  EXPECT_FALSE(r.hasCompanionFiles());

  std::vector<std::string> files;
  EXPECT_EQ(0U, r.getRequiredDirectories(files));

  // Invalid file; no check possible.
  EXPECT_FALSE(r.isUsedFile("unused-nonexistent-file"));

  // Valid but unused file, getUsedFiles throws.
  EXPECT_THROW(r.isUsedFile(PROJECT_SOURCE_DIR "/components/specification/samples/2012-06/18x24y5z5t2c8b-text.ome"), std::logic_error);
}

TEST_F(FormatReaderTest, FlatFile)
{
  r.setId("flat");

  EXPECT_TRUE(r.getCurrentFile());

  EXPECT_TRUE(r.isSingleFile("id"));
  EXPECT_FALSE(r.hasCompanionFiles());

  std::vector<std::string> files;
  EXPECT_EQ(0U, r.getRequiredDirectories(files));

  // Invalid file
  EXPECT_FALSE(r.isUsedFile("unused-nonexistent-file"));

  // Valid but unused file
 EXPECT_FALSE(r.isUsedFile(PROJECT_SOURCE_DIR "/components/specification/samples/2012-06/18x24y5z5t2c8b-text.ome"));
}

TEST_F(FormatReaderTest, DefaultMetadata)
{
  EXPECT_THROW(r.getMetadataValue("Key"), boost::bad_get);
  EXPECT_THROW(r.getSeriesMetadataValue("Key"), std::logic_error);
  EXPECT_TRUE(r.getGlobalMetadata().empty());
  EXPECT_THROW(r.getSeriesMetadata(), std::logic_error);
  EXPECT_THROW(r.getCoreMetadataList(), std::logic_error);

  EXPECT_FALSE(r.isMetadataFiltered());
  EXPECT_NO_THROW(r.setMetadataFiltered(true));
  EXPECT_TRUE(r.isMetadataFiltered());
}

TEST_F(FormatReaderTest, FlatMetadata)
{
  EXPECT_NO_THROW(r.setMetadataFiltered(true));
  r.setId("flat");

  EXPECT_THROW(r.getMetadataValue("Key"), boost::bad_get);
  EXPECT_EQ(r.getMetadataValue("Institution"), MetadataMap::value_type("University of Dundee"));
  EXPECT_EQ(r.getSeriesMetadataValue("Organism"), MetadataMap::value_type("Mus musculus"));
  EXPECT_THROW(r.getSeriesMetadataValue("Key"), boost::bad_get);
  EXPECT_EQ(1U, r.getGlobalMetadata().size());
  EXPECT_EQ(1U, r.getSeriesMetadata().size());
  EXPECT_EQ(4U, r.getCoreMetadataList().size());

  EXPECT_TRUE(r.isMetadataFiltered());
  EXPECT_THROW(r.setMetadataFiltered(false), std::logic_error);
  EXPECT_TRUE(r.isMetadataFiltered());
}

TEST_F(FormatReaderTest, DefaultMetadataStore)
{
  std::shared_ptr<MetadataStore> store(std::make_shared<OMEXMLMetadata>());

  EXPECT_NO_THROW(r.setMetadataStore(store));
  EXPECT_EQ(store, std::dynamic_pointer_cast<OMEXMLMetadata>(r.getMetadataStore()));
}

TEST_F(FormatReaderTest, FlatMetadataStore)
{
  r.setId("flat");

  std::shared_ptr<MetadataStore> store(std::make_shared<OMEXMLMetadata>());

  EXPECT_THROW(r.setMetadataStore(store), std::logic_error);
}

TEST_F(FormatReaderTest, Readers)
{
  EXPECT_TRUE(r.getUnderlyingReaders().empty());
}

TEST_F(FormatReaderTest, DefaultPixels)
{
  std::istringstream is("");
  PixelBufferRaw buf(512U * 512U * 8U);

  EXPECT_THROW(r.readPlane(is, buf, 0, 0, 512, 512), std::logic_error);
  EXPECT_THROW(r.readPlane(is, buf, 0, 0, 512, 512, 0), std::logic_error);

  EXPECT_THROW(r.openBytes(0, buf), std::logic_error);
  EXPECT_THROW(r.openBytes(0, buf, 0, 0, 512, 512), std::logic_error);
  EXPECT_THROW(r.openThumbBytes(0, buf), std::logic_error);
}

TEST_F(FormatReaderTest, FlatPixels)
{
  r.setId("flat");

  /// @todo Add tests for readPlane from stream for all variants.

  std::istringstream is("");
  PixelBufferRaw buf(512U * 512U * 8U);

  EXPECT_THROW(r.readPlane(is, buf, 0, 0, 512, 512), std::logic_error);
  EXPECT_THROW(r.readPlane(is, buf, 0, 0, 512, 512, 0), std::logic_error);

  EXPECT_NO_THROW(r.openBytes(0, buf));
  EXPECT_NO_THROW(r.openBytes(0, buf, 0, 0, 512, 512));
  EXPECT_THROW(r.openThumbBytes(0, buf), std::runtime_error);
}

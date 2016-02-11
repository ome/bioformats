/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
 * Copyright © 2013 - 2015 Open Microscopy Environment:
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
#include <ome/bioformats/VariantPixelBuffer.h>
#include <ome/bioformats/PixelProperties.h>
#include <ome/bioformats/detail/FormatReader.h>

#include <ome/xml/meta/MetadataStore.h>
#include <ome/xml/meta/OMEXMLMetadata.h>

#include <ome/test/config.h>

#include <boost/filesystem/operations.hpp>
#include <boost/range/size.hpp>

#include <ome/test/test.h>

#include "pixel.h"

using ome::bioformats::CoreMetadata;
using ome::bioformats::EndianType;
using ome::bioformats::FormatReader;
using ome::bioformats::VariantPixelBuffer;
using ome::bioformats::detail::ReaderProperties;
using ome::bioformats::MetadataMap;
using ome::bioformats::MetadataOptions;
using ome::bioformats::dimension_size_type;
using ome::xml::meta::MetadataStore;
using ome::xml::meta::OMEXMLMetadata;
using ome::xml::model::enums::PixelType;

typedef ome::xml::model::enums::PixelType PT;
typedef ome::compat::array<dimension_size_type, 3> dim;
typedef ome::compat::array<dimension_size_type, 6> moddim;

class FormatReaderTestParameters
{
public:
  PT         type;
  EndianType endian;

  FormatReaderTestParameters(PT         type,
                             EndianType endian):
    type(type),
    endian(endian)
  {}
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const FormatReaderTestParameters& params)
{
  return os << PT(params.type) << '/'<< params.endian;
}

namespace
{

  ReaderProperties
  test_properties()
  {
    ReaderProperties p("TestReader", "Reader for unit testing");
    p.suffixes.push_back("test");
    p.compression_suffixes.push_back("gz");
    p.metadata_levels.insert(MetadataOptions::METADATA_MINIMUM);
    p.metadata_levels.insert(MetadataOptions::METADATA_NO_OVERLAYS);
    p.metadata_levels.insert(MetadataOptions::METADATA_ALL);

    return p;
  }

  const ReaderProperties props(test_properties());

}

class FormatReaderCustom : public ::ome::bioformats::detail::FormatReader
{
private:
  const FormatReaderTestParameters& test_params;

public:
  FormatReaderCustom(const FormatReaderTestParameters& test_params):
    ::ome::bioformats::detail::FormatReader(props),
    test_params(test_params)
  {
    domains.push_back("Test domain");
  }

  virtual ~FormatReaderCustom()
  {
  }

protected:
  bool
  isStreamThisTypeImpl(std::istream& stream) const
  {
    std::istreambuf_iterator<char> eos;
    std::string in(std::istreambuf_iterator<char>(stream), eos);

    // std::cout << "IN: " << in << std::endl;

    return in == "Valid file content";
  }

  ome::compat::shared_ptr<CoreMetadata>
  makeCore()
  {
    ome::compat::shared_ptr<CoreMetadata> c(ome::compat::make_shared<CoreMetadata>());

    c->sizeX = 512;
    c->sizeY = 1024;
    c->sizeZ = 20;
    c->sizeT = 5;

    // SizeC is 2 channels each containing 1 and 3 subchannels, respectively.
    c->sizeC.clear();
    c->sizeC.push_back(1);
    c->sizeC.push_back(3);

    c->pixelType = test_params.type;
    c->imageCount = c->sizeZ * c->sizeT * c->sizeC.size();
    c->orderCertain = true;
    c->littleEndian = test_params.endian == ::ome::bioformats::ENDIAN_LITTLE;
    c->interleaved = false;
    c->indexed = false;
    c->falseColor = true;
    c->metadataComplete = false;
    c->thumbnail = false;
    c->resolutionCount = 1;
    c->moduloZ.start = 0.0f;
    c->moduloZ.end = 8.0f;
    c->moduloZ.step = 2.0f;

    return c;
  }

protected:
  void
  openBytesImpl(dimension_size_type /* no */,
                VariantPixelBuffer& /* buf */,
                dimension_size_type /* x */,
                dimension_size_type /* y */,
                dimension_size_type /* w */,
                dimension_size_type /* h */) const
  {
    assertId(currentId, true);
    return;
  }

  void
  initFile(const boost::filesystem::path& id)
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
          ome::compat::shared_ptr<CoreMetadata> c(makeCore());
          c->resolutionCount = 3;
          core.push_back(c);
          core.push_back(makeCore());
          core.push_back(makeCore());
        }

        {
          ome::compat::shared_ptr<CoreMetadata> c(makeCore());
          c->resolutionCount = 2;
          core.push_back(c);
          core.push_back(makeCore());
        }

        core.push_back(makeCore());
        core.push_back(makeCore());

        {
          ome::compat::shared_ptr<CoreMetadata> c(makeCore());
          c->resolutionCount = 2;
          core.push_back(c);
          core.push_back(makeCore());
        }
      }
  }

public:
  bool
  isUsedFile(const boost::filesystem::path& file)
  {
    return ::ome::bioformats::detail::FormatReader::isUsedFile(file);
  }

  void
  readPlane(std::istream&       source,
            VariantPixelBuffer& dest,
            dimension_size_type x,
            dimension_size_type y,
            dimension_size_type w,
            dimension_size_type h,
            dimension_size_type samples)
  {
    ::ome::bioformats::detail::FormatReader::readPlane(source, dest, x, y, w, h, samples);
  }

  void
  readPlane(std::istream&       source,
            VariantPixelBuffer& dest,
            dimension_size_type x,
            dimension_size_type y,
            dimension_size_type w,
            dimension_size_type h,
            dimension_size_type scanlinePad,
            dimension_size_type samples)
  {
    ::ome::bioformats::detail::FormatReader::readPlane(source, dest, x, y, w, h, scanlinePad, samples);
  }

};

class FormatReaderTest : public ::testing::TestWithParam<FormatReaderTestParameters>
{
public:
  FormatReaderCustom r;
  const FormatReader& cr;

  FormatReaderTest():
    ::testing::TestWithParam<FormatReaderTestParameters>(),
    r(GetParam()),
    cr(r)
  {
  }

  void SetUp()
  {
  }
};

TEST_P(FormatReaderTest, DefaultConstruct)
{
  FormatReaderCustom r(GetParam());
}

TEST_P(FormatReaderTest, ReaderProperties)
{
  r.setId("test");
  ASSERT_EQ(props.name, r.getFormat());
  ASSERT_EQ(props.description, r.getFormatDescription());
  ASSERT_TRUE(props.suffixes == r.getSuffixes());
  ASSERT_TRUE(props.compression_suffixes == r.getCompressionSuffixes());
}

TEST_P(FormatReaderTest, IsThisType)
{
  std::string icontent("Invalid file content");
  std::string vcontent("Valid file content");
  std::istringstream isicontent(icontent);
  std::istringstream isvcontent(vcontent);

  EXPECT_FALSE(r.isThisType("invalid.file"));
  EXPECT_FALSE(r.isThisType("invalid.file", true));
  EXPECT_FALSE(r.isThisType("invalid.file", false));

  EXPECT_FALSE(r.isThisType("invalid.file.gz"));
  EXPECT_FALSE(r.isThisType("invalid.file.gz", true));
  EXPECT_FALSE(r.isThisType("invalid.file.gz", false));

  EXPECT_TRUE(r.isThisType("valid.test"));
  EXPECT_TRUE(r.isThisType("valid.test", true));
  EXPECT_TRUE(r.isThisType("valid.test", false));

  EXPECT_TRUE(r.isThisType("valid.test.gz"));
  EXPECT_TRUE(r.isThisType("valid.test.gz", true));
  EXPECT_TRUE(r.isThisType("valid.test.gz", false));

  EXPECT_FALSE(r.isThisType(reinterpret_cast<uint8_t *>(&icontent[0]),
                            reinterpret_cast<uint8_t *>(&icontent[0] + icontent.size())));
  EXPECT_FALSE(r.isThisType(reinterpret_cast<uint8_t *>(&*icontent.begin()),
                            icontent.size()));
  EXPECT_FALSE(r.isThisType(isicontent));

  EXPECT_TRUE(r.isThisType(reinterpret_cast<uint8_t *>(&vcontent[0]),
                           reinterpret_cast<uint8_t *>(&vcontent[0] + vcontent.size())));
  EXPECT_TRUE(r.isThisType(reinterpret_cast<uint8_t *>(&*vcontent.begin()),
                           vcontent.size()));
  EXPECT_TRUE(r.isThisType(isvcontent));
}

TEST_P(FormatReaderTest, DefaultClose)
{
  EXPECT_NO_THROW(r.close());
}

TEST_P(FormatReaderTest, FlatClose)
{
  r.setId("flat");
  EXPECT_NO_THROW(r.close());
}

TEST_P(FormatReaderTest, DefaultCoreMetadata)
{
  EXPECT_THROW(r.getImageCount(), std::logic_error);
  EXPECT_THROW(r.isRGB(0), std::logic_error);
  EXPECT_THROW(r.isRGB(1), std::logic_error);
  EXPECT_THROW(r.getSizeX(), std::logic_error);
  EXPECT_THROW(r.getSizeY(), std::logic_error);
  EXPECT_THROW(r.getSizeZ(), std::logic_error);
  EXPECT_THROW(r.getSizeT(), std::logic_error);
  EXPECT_THROW(r.getSizeC(), std::logic_error);
  EXPECT_THROW(r.getPixelType(), std::logic_error);
  EXPECT_THROW(r.getBitsPerPixel(), std::logic_error);
  EXPECT_THROW(r.getEffectiveSizeC(), std::logic_error);
  EXPECT_THROW(r.getRGBChannelCount(0), std::logic_error);
  EXPECT_THROW(r.getRGBChannelCount(1), std::logic_error);
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
  EXPECT_THROW(r.getOptimalTileWidth(0), std::logic_error);
  EXPECT_THROW(r.getOptimalTileHeight(0), std::logic_error);
  EXPECT_THROW(r.getOptimalTileWidth(1), std::logic_error);
  EXPECT_THROW(r.getOptimalTileHeight(1), std::logic_error);
  EXPECT_THROW(r.getOptimalTileWidth(), std::logic_error);
  EXPECT_THROW(r.getOptimalTileHeight(), std::logic_error);
  EXPECT_THROW(r.getResolutionCount(), std::logic_error);
}

TEST_P(FormatReaderTest, FlatCoreMetadata)
{
  const FormatReaderTestParameters& params(GetParam());

  r.setId("flat");

  EXPECT_EQ(200U, r.getImageCount());
  EXPECT_FALSE(r.isRGB(0));
  EXPECT_TRUE(r.isRGB(1));
  EXPECT_EQ(512U, r.getSizeX());
  EXPECT_EQ(1024U, r.getSizeY());
  EXPECT_EQ(20U, r.getSizeZ());
  EXPECT_EQ(5U, r.getSizeT());
  EXPECT_EQ(4U, r.getSizeC());
  EXPECT_EQ(params.type, r.getPixelType());
  EXPECT_EQ(::ome::bioformats::bitsPerPixel(params.type), r.getBitsPerPixel());
  EXPECT_EQ(2U, r.getEffectiveSizeC());
  EXPECT_EQ(1U, r.getRGBChannelCount(0));
  EXPECT_EQ(3U, r.getRGBChannelCount(1));
  EXPECT_FALSE(r.isIndexed());
  EXPECT_TRUE(r.isFalseColor());
  EXPECT_NO_THROW(r.getModuloZ());
  EXPECT_NO_THROW(r.getModuloT());
  EXPECT_NO_THROW(r.getModuloC());
  EXPECT_EQ(5U, r.getModuloZ().size());
  EXPECT_EQ(1U, r.getModuloT().size());
  EXPECT_EQ(1U, r.getModuloC().size());
  EXPECT_EQ(64U, r.getThumbSizeX());
  EXPECT_EQ(128U, r.getThumbSizeY());
  EXPECT_EQ(params.endian == ::ome::bioformats::ENDIAN_LITTLE, r.isLittleEndian());
  EXPECT_EQ(std::string("XYZTC"), r.getDimensionOrder());
  EXPECT_TRUE(r.isOrderCertain());
  EXPECT_FALSE(r.isThumbnailSeries());
  EXPECT_FALSE(r.isInterleaved());
  EXPECT_FALSE(r.isInterleaved(0));
  EXPECT_FALSE(r.isMetadataComplete());
  EXPECT_EQ(512U, r.getOptimalTileWidth(0));
  EXPECT_EQ(std::min((1024U * 1024U) / (512U * r.getRGBChannelCount(0) * ::ome::bioformats::bytesPerPixel(params.type)),
                     dimension_size_type(1024U)),
            r.getOptimalTileHeight(0));
  EXPECT_EQ(512U, r.getOptimalTileWidth(1));
  EXPECT_EQ(std::min((1024U * 1024U) / (512U * r.getRGBChannelCount(1) * ::ome::bioformats::bytesPerPixel(params.type)),
                     dimension_size_type(1024U)),
            r.getOptimalTileHeight(1));
  EXPECT_EQ(512U, r.getOptimalTileWidth());
  EXPECT_EQ(std::min((1024U * 1024U) / (512U * r.getRGBChannelCount(1) * ::ome::bioformats::bytesPerPixel(params.type)),
                     dimension_size_type(1024U)),
            r.getOptimalTileHeight());
  EXPECT_EQ(1U, r.getResolutionCount());
}

TEST_P(FormatReaderTest, SubresolutionFlattenedCoreMetadata)
{
  const FormatReaderTestParameters& params(GetParam());

  EXPECT_NO_THROW(r.setFlattenedResolutions(true));
  r.setId("subres");

  EXPECT_EQ(200U, r.getImageCount());
  EXPECT_FALSE(r.isRGB(0));
  EXPECT_TRUE(r.isRGB(1));
  EXPECT_EQ(512U, r.getSizeX());
  EXPECT_EQ(1024U, r.getSizeY());
  EXPECT_EQ(20U, r.getSizeZ());
  EXPECT_EQ(5U, r.getSizeT());
  EXPECT_EQ(4U, r.getSizeC());
  EXPECT_EQ(params.type, r.getPixelType());
  EXPECT_EQ(::ome::bioformats::bitsPerPixel(params.type), r.getBitsPerPixel());
  EXPECT_EQ(2U, r.getEffectiveSizeC());
  EXPECT_EQ(1U, r.getRGBChannelCount(0));
  EXPECT_EQ(3U, r.getRGBChannelCount(1));
  EXPECT_FALSE(r.isIndexed());
  EXPECT_TRUE(r.isFalseColor());
  EXPECT_NO_THROW(r.getModuloZ());
  EXPECT_NO_THROW(r.getModuloT());
  EXPECT_NO_THROW(r.getModuloC());
  EXPECT_EQ(5U, r.getModuloZ().size());
  EXPECT_EQ(1U, r.getModuloT().size());
  EXPECT_EQ(1U, r.getModuloC().size());
  EXPECT_EQ(64U, r.getThumbSizeX());
  EXPECT_EQ(128U, r.getThumbSizeY());
  EXPECT_EQ(params.endian == ::ome::bioformats::ENDIAN_LITTLE, r.isLittleEndian());
  EXPECT_EQ(std::string("XYZTC"), r.getDimensionOrder());
  EXPECT_TRUE(r.isOrderCertain());
  EXPECT_FALSE(r.isThumbnailSeries());
  EXPECT_FALSE(r.isInterleaved());
  EXPECT_FALSE(r.isInterleaved(0));
  EXPECT_FALSE(r.isMetadataComplete());
  EXPECT_EQ(512U, r.getOptimalTileWidth(0));
  EXPECT_EQ(std::min((1024U * 1024U) / (512U * r.getRGBChannelCount(0) * ::ome::bioformats::bytesPerPixel(params.type)),
                     dimension_size_type(1024U)),
            r.getOptimalTileHeight(0));
  EXPECT_EQ(512U, r.getOptimalTileWidth(1));
  EXPECT_EQ(std::min((1024U * 1024U) / (512U * r.getRGBChannelCount(1) * ::ome::bioformats::bytesPerPixel(params.type)),
                     dimension_size_type(1024U)),
            r.getOptimalTileHeight(1));
  EXPECT_EQ(512U, r.getOptimalTileWidth());
  EXPECT_EQ(std::min((1024U * 1024U) / (512U * r.getRGBChannelCount(1) * ::ome::bioformats::bytesPerPixel(params.type)),
                     dimension_size_type(1024U)),
            r.getOptimalTileHeight());
  EXPECT_EQ(1U, r.getResolutionCount());
}

TEST_P(FormatReaderTest, SubresolutionUnflattenedCoreMetadata)
{
  const FormatReaderTestParameters& params(GetParam());

  EXPECT_NO_THROW(r.setFlattenedResolutions(false));
  r.setId("subres");

  EXPECT_EQ(200U, r.getImageCount());
  EXPECT_FALSE(r.isRGB(0));
  EXPECT_TRUE(r.isRGB(1));
  EXPECT_EQ(512U, r.getSizeX());
  EXPECT_EQ(1024U, r.getSizeY());
  EXPECT_EQ(20U, r.getSizeZ());
  EXPECT_EQ(5U, r.getSizeT());
  EXPECT_EQ(4U, r.getSizeC());
  EXPECT_EQ(params.type, r.getPixelType());
  EXPECT_EQ(::ome::bioformats::bitsPerPixel(params.type), r.getBitsPerPixel());
  EXPECT_EQ(2U, r.getEffectiveSizeC());
  EXPECT_EQ(1U, r.getRGBChannelCount(0));
  EXPECT_EQ(3U, r.getRGBChannelCount(1));
  EXPECT_FALSE(r.isIndexed());
  EXPECT_TRUE(r.isFalseColor());
  EXPECT_NO_THROW(r.getModuloZ());
  EXPECT_NO_THROW(r.getModuloT());
  EXPECT_NO_THROW(r.getModuloC());
  EXPECT_EQ(5U, r.getModuloZ().size());
  EXPECT_EQ(1U, r.getModuloT().size());
  EXPECT_EQ(1U, r.getModuloC().size());
  EXPECT_EQ(64U, r.getThumbSizeX());
  EXPECT_EQ(128U, r.getThumbSizeY());
  EXPECT_EQ(params.endian == ::ome::bioformats::ENDIAN_LITTLE, r.isLittleEndian());
  EXPECT_EQ(std::string("XYZTC"), r.getDimensionOrder());
  EXPECT_TRUE(r.isOrderCertain());
  EXPECT_FALSE(r.isThumbnailSeries());
  EXPECT_FALSE(r.isInterleaved());
  EXPECT_FALSE(r.isInterleaved(0));
  EXPECT_FALSE(r.isMetadataComplete());
  EXPECT_EQ(512U, r.getOptimalTileWidth(0));
  EXPECT_EQ(std::min((1024U * 1024U) / (512U * r.getRGBChannelCount(0) * ::ome::bioformats::bytesPerPixel(params.type)),
                     dimension_size_type(1024U)),
            r.getOptimalTileHeight(0));
  EXPECT_EQ(512U, r.getOptimalTileWidth(1));
  EXPECT_EQ(std::min((1024U * 1024U) / (512U * r.getRGBChannelCount(1) * ::ome::bioformats::bytesPerPixel(params.type)),
                     dimension_size_type(1024U)),
            r.getOptimalTileHeight(1));
  EXPECT_EQ(512U, r.getOptimalTileWidth());
  EXPECT_EQ(std::min((1024U * 1024U) / (512U * r.getRGBChannelCount(1) * ::ome::bioformats::bytesPerPixel(params.type)),
                     dimension_size_type(1024U)),
            r.getOptimalTileHeight());
  EXPECT_EQ(3U, r.getResolutionCount());
}

TEST_P(FormatReaderTest, DefaultLUT)
{
  VariantPixelBuffer buf;
  EXPECT_THROW(r.getLookupTable(0U, buf), std::logic_error);
}

TEST_P(FormatReaderTest, FlatLUT)
{
  r.setId("flat");

  VariantPixelBuffer buf;
  EXPECT_THROW(r.getLookupTable(0U, buf), std::runtime_error);
}

TEST_P(FormatReaderTest, DefaultSeries)
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
  EXPECT_THROW(r.getIndex(0U, 0U, 0U, 0U, 0U, 0U), std::logic_error);
  EXPECT_THROW(r.getZCTCoords(0U), std::logic_error);
  EXPECT_THROW(r.getZCTModuloCoords(0U), std::logic_error);
}

struct dims
{
  dimension_size_type z;
  dimension_size_type t;
  dimension_size_type c;

  dims(dimension_size_type z,
       dimension_size_type t,
       dimension_size_type c):
    z(z), t(t), c(c)
  {}

  operator ome::compat::array<dimension_size_type, 3>() const
  {
    ome::compat::array<dimension_size_type, 3> ret;
    ret[0] = z;
    ret[1] = c;
    ret[2] = t;
    return ret;
  }
};

struct moddims
{
  dimension_size_type z;
  dimension_size_type t;
  dimension_size_type c;
  dimension_size_type mz;
  dimension_size_type mt;
  dimension_size_type mc;

  moddims(dimension_size_type z,
          dimension_size_type t,
          dimension_size_type c,
          dimension_size_type mz,
          dimension_size_type mt,
          dimension_size_type mc):
    z(z), t(t), c(c), mz(mz), mt(mt), mc(mc)
  {}

  operator ome::compat::array<dimension_size_type, 6>() const
  {
    ome::compat::array<dimension_size_type, 6> ret;
    ret[0] = z;
    ret[1] = c;
    ret[2] = t;
    ret[3] = mz;
    ret[4] = mc;
    ret[5] = mt;
    return ret;
  }
};

TEST_P(FormatReaderTest, FlatSeries)
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

  dims coords[] =
    {
      dims(0,  0, 0),
      dims(1,  0, 0),
      dims(0,  1, 0),
      dims(0,  0, 1),
      dims(1,  1, 0),
      dims(1,  0, 1),
      dims(0,  1, 1),
      dims(1,  1, 1),
      dims(3,  2, 1),
      dims(12, 3, 0),
      dims(8,  2, 1),
      dims(19, 4, 1)
    };

    moddims modcoords[] =
    {
      moddims(0, 0, 0, 0, 0, 0),
      moddims(0, 0, 0, 1, 0, 0),
      moddims(0, 1, 0, 0, 0, 0),
      moddims(0, 0, 1, 0, 0, 0),
      moddims(0, 1, 0, 1, 0, 0),
      moddims(0, 0, 1, 1, 0, 0),
      moddims(0, 1, 1, 0, 0, 0),
      moddims(0, 1, 1, 1, 0, 0),
      moddims(0, 2, 1, 3, 0, 0),
      moddims(2, 3, 0, 2, 0, 0),
      moddims(1, 2, 1, 3, 0, 0),
      moddims(3, 4, 1, 4, 0, 0)
    };

  dimension_size_type indexes[] =
    {
      0,
      1,
      20,
      100,
      21,
      101,
      120,
      121,
      143,
      72,
      148,
      199
    };

  for (unsigned int i = 0;
       i < boost::size(coords);
       ++i)
    {
      const dim coord(static_cast<dim>(coords[i]));

      EXPECT_EQ(indexes[i], r.getIndex(coord[0], coord[1], coord[2]));

      dim ncoord = r.getZCTCoords(indexes[i]);
      // EXPECT_EQ should work here, but fails for Boost 1.42; works
      // in 1.46.
      EXPECT_TRUE(coord == ncoord);
    }

  for (unsigned int i = 0;
       i < boost::size(modcoords);
       ++i)
    {
      const moddim coord(static_cast<moddim>(modcoords[i]));

      EXPECT_EQ(indexes[i], r.getIndex(coord[0], coord[1], coord[2],
                                       coord[3], coord[4], coord[5]));

      moddim ncoord = r.getZCTModuloCoords(indexes[i]);
      // EXPECT_EQ should work here, but fails for Boost 1.42; works
      // in 1.46.
      EXPECT_TRUE(coord == ncoord);
    }
}

TEST_P(FormatReaderTest, SubresolutionFlattenedSeries)
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

  EXPECT_EQ(0U, r.getIndex(0, 0, 0));
  EXPECT_EQ(0U, r.getIndex(0, 0, 0, 0, 0, 0));
  ome::compat::array<dimension_size_type, 3> coords;
  coords[0] = coords[1] = coords[2] = 0;
  ome::compat::array<dimension_size_type, 6> modcoords;
  modcoords[0] = modcoords[1] = modcoords[2] = modcoords[3] = modcoords[4] = modcoords[5] = 0;

  // EXPECT_EQ should work here, but fails for Boost 1.42; works
  // in 1.46.
  dim ncoords = r.getZCTCoords(0U);
  EXPECT_TRUE(coords == ncoords);
  moddim modncoords = r.getZCTModuloCoords(0U);
  EXPECT_TRUE(modcoords == modncoords);
}

TEST_P(FormatReaderTest, SubresolutionUnflattenedSeries)
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

  EXPECT_EQ(0U, r.getIndex(0, 0, 0));
  EXPECT_EQ(0U, r.getIndex(0, 0, 0, 0, 0, 0));
  ome::compat::array<dimension_size_type, 3> coords;
  coords[0] = coords[1] = coords[2] = 0;
  ome::compat::array<dimension_size_type, 6> modcoords;
  modcoords[0] = modcoords[1] = modcoords[2] = modcoords[3] = modcoords[4] = modcoords[5] = 0;

  // EXPECT_EQ should work here, but fails for Boost 1.42; works
  // in 1.46.
  dim ncoords = r.getZCTCoords(0U);
  EXPECT_TRUE(coords == ncoords);
  moddim modncoords = r.getZCTModuloCoords(0U);
  EXPECT_TRUE(modcoords == modncoords);
}

TEST_P(FormatReaderTest, DefaultGroupFiles)
{
  EXPECT_TRUE(r.isGroupFiles());
  EXPECT_NO_THROW(r.setGroupFiles(false));
  EXPECT_FALSE(r.isGroupFiles());
  EXPECT_EQ(FormatReader::CANNOT_GROUP, r.fileGroupOption("id"));
}

TEST_P(FormatReaderTest, DefaultFlatGroupFiles)
{
  EXPECT_TRUE(r.isGroupFiles());
  EXPECT_NO_THROW(r.setGroupFiles(false));
  EXPECT_FALSE(r.isGroupFiles());
  EXPECT_EQ(FormatReader::CANNOT_GROUP, r.fileGroupOption("id"));
}

TEST_P(FormatReaderTest, DefaultProperties)
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

TEST_P(FormatReaderTest, FlatProperties)
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

TEST_P(FormatReaderTest, SubresolutionFlattenedProperties)
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

TEST_P(FormatReaderTest, SubresolutionUnflattenedProperties)
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

TEST_P(FormatReaderTest, UsedFiles)
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

TEST_P(FormatReaderTest, DefaultFile)
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

TEST_P(FormatReaderTest, FlatFile)
{
  r.setId("flat");

  EXPECT_TRUE(!!r.getCurrentFile());

  EXPECT_TRUE(r.isSingleFile("id"));
  EXPECT_FALSE(r.hasCompanionFiles());

  std::vector<std::string> files;
  EXPECT_EQ(0U, r.getRequiredDirectories(files));

  // Invalid file
  EXPECT_FALSE(r.isUsedFile("unused-nonexistent-file"));

  // Valid but unused file
  EXPECT_FALSE(r.isUsedFile(PROJECT_SOURCE_DIR "/components/specification/samples/2012-06/18x24y5z5t2c8b-text.ome"));
}

TEST_P(FormatReaderTest, DefaultMetadata)
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

TEST_P(FormatReaderTest, FlatMetadata)
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

TEST_P(FormatReaderTest, DefaultMetadataStore)
{
  ome::compat::shared_ptr<MetadataStore> store(ome::compat::make_shared<OMEXMLMetadata>());

  EXPECT_NO_THROW(r.setMetadataStore(store));
  EXPECT_EQ(store, ome::compat::dynamic_pointer_cast<OMEXMLMetadata>(r.getMetadataStore()));
}

TEST_P(FormatReaderTest, FlatMetadataStore)
{
  r.setId("flat");

  ome::compat::shared_ptr<MetadataStore> store(ome::compat::make_shared<OMEXMLMetadata>());

  EXPECT_THROW(r.setMetadataStore(store), std::logic_error);
}

TEST_P(FormatReaderTest, Readers)
{
  EXPECT_TRUE(r.getUnderlyingReaders().empty());
}

TEST_P(FormatReaderTest, DefaultPixels)
{
  const FormatReaderTestParameters& params(GetParam());

  std::istringstream is("");
  VariantPixelBuffer buf(boost::extents[512][512][1][1][2][1][1][1][1],
                         params.type);

  EXPECT_THROW(r.readPlane(is, buf, 0, 0, 512, 512, 1), std::logic_error);
  EXPECT_THROW(r.readPlane(is, buf, 0, 0, 512, 512, 0, 1), std::logic_error);

  EXPECT_THROW(r.openBytes(0, buf), std::logic_error);
  EXPECT_THROW(r.openBytes(0, buf, 0, 0, 512, 512), std::logic_error);
  EXPECT_THROW(r.openThumbBytes(0, buf), std::logic_error);
}

namespace
{

  struct FlatPixelsTest : public boost::static_visitor<>
  {
    FormatReaderCustom& reader;
    VariantPixelBuffer& buf;

    FlatPixelsTest(FormatReaderCustom& reader,
                   VariantPixelBuffer& buf):
      reader(reader),
      buf(buf)
    {}

    template<typename T>
    void
    operator()(T& /* v */) const
    {
      typedef typename T::element_type::value_type value_type;
      EndianType endian = reader.isLittleEndian() ? ome::bioformats::ENDIAN_LITTLE : ome::bioformats::ENDIAN_BIG;

      std::stringstream ss;
      std::vector<value_type> expected;
      for (uint32_t x = 0; x < 512; ++x)
        for (uint32_t y = 0; y < 512; ++y)
          {
            value_type val(pixel_value<value_type>(x*y));

            expected.push_back(val);

            if ((endian == ome::bioformats::ENDIAN_BIG &&
                 boost::endian::order::big != boost::endian::order::native) ||
                (endian == ome::bioformats::ENDIAN_LITTLE &&
                 boost::endian::order::little != boost::endian::order::native))
              ome::bioformats::byteswap(val);

            ss.write(reinterpret_cast<char *>(&val), sizeof(val));
          }

      VariantPixelBuffer buf(boost::extents[512][512][1][1][2][1][1][1][1],
                             reader.getPixelType());

      ss.seekg(0, std::ios::beg);
      EXPECT_NO_THROW(reader.readPlane(ss, buf, 0, 0, 512, 512, 1));
      ss.seekg(0, std::ios::beg);
      EXPECT_NO_THROW(reader.readPlane(ss, buf, 0, 0, 512, 512, 0, 1));

      ASSERT_EQ(expected.size(), buf.num_elements());
      for (uint32_t i = 0; i < expected.size(); ++i)
        ASSERT_EQ(expected.at(i), *(buf.data<value_type>()+i));

      EXPECT_NO_THROW(reader.openBytes(0, buf));
      EXPECT_NO_THROW(reader.openBytes(0, buf, 0, 0, 512, 512));
      EXPECT_THROW(reader.openThumbBytes(0, buf), std::runtime_error);
    }
  };

}

TEST_P(FormatReaderTest, FlatPixels)
{
  r.setId("flat");

  VariantPixelBuffer buf(boost::extents[512][512][1][1][2][1][1][1][1],
                         r.getPixelType());

  FlatPixelsTest v(r, buf);
  boost::apply_visitor(v, buf.vbuffer());
}

FormatReaderTestParameters variant_params[] =
  { //                         PixelType          EndianType
    FormatReaderTestParameters(PT::INT8,          ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::INT8,          ome::bioformats::ENDIAN_LITTLE),

    FormatReaderTestParameters(PT::INT16,         ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::INT16,         ome::bioformats::ENDIAN_LITTLE),

    FormatReaderTestParameters(PT::INT32,         ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::INT32,         ome::bioformats::ENDIAN_LITTLE),

    FormatReaderTestParameters(PT::UINT8,         ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::UINT8,         ome::bioformats::ENDIAN_LITTLE),

    FormatReaderTestParameters(PT::UINT16,        ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::UINT16,        ome::bioformats::ENDIAN_LITTLE),

    FormatReaderTestParameters(PT::UINT32,        ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::UINT32,        ome::bioformats::ENDIAN_LITTLE),

    FormatReaderTestParameters(PT::FLOAT,         ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::FLOAT,         ome::bioformats::ENDIAN_LITTLE),

    FormatReaderTestParameters(PT::DOUBLE,        ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::DOUBLE,        ome::bioformats::ENDIAN_LITTLE),

    FormatReaderTestParameters(PT::BIT,           ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::BIT,           ome::bioformats::ENDIAN_LITTLE),

    FormatReaderTestParameters(PT::COMPLEX,       ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::COMPLEX,       ome::bioformats::ENDIAN_LITTLE),

    FormatReaderTestParameters(PT::DOUBLECOMPLEX, ome::bioformats::ENDIAN_BIG),
    FormatReaderTestParameters(PT::DOUBLECOMPLEX, ome::bioformats::ENDIAN_LITTLE),
  };

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(FormatReaderVariants, FormatReaderTest, ::testing::ValuesIn(variant_params));

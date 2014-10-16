/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
 * Copyright © 2014 Open Microscopy Environment:
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

#include <cstdio>
#include <stdexcept>
#include <vector>

#include <boost/filesystem.hpp>
#include <boost/type_traits.hpp>

#include <ome/bioformats/tiff/TileInfo.h>
#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/Field.h>
#include <ome/bioformats/tiff/Exception.h>

#include <ome/compat/regex.h>

#include <ome/internal/config.h>

#include <ome/test/config.h>

#ifdef PNG_FOUND
#include <png.h>
#endif // PNG_FOUND

#include <gtest/gtest.h>

#include "pixel.h"

using ome::bioformats::tiff::directory_index_type;
using ome::bioformats::tiff::TileInfo;
using ome::bioformats::tiff::TIFF;
using ome::bioformats::tiff::IFD;
using ome::bioformats::tiff::PlaneRegion;
using ome::bioformats::dimension_size_type;
using ome::bioformats::VariantPixelBuffer;
using ome::bioformats::PixelBuffer;
using ome::bioformats::PixelProperties;
typedef ome::xml::model::enums::PixelType PT;

using namespace boost::filesystem;

struct TileTestParameters
{
  bool tile;
  std::string file;
  std::string wfile;
  bool imageplanar;
  dimension_size_type imagewidth;
  dimension_size_type imagelength;
  dimension_size_type tilewidth;
  dimension_size_type tilelength;
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const TileTestParameters& p)
{
  return os << p.file << " [" << p.wfile << "] ("
            << p.imagewidth << "x" << p.imagelength
            << (p.imageplanar ? " planar" : " chunky")
            << (p.tile ? " tiled " : " strips ")
            << p.tilewidth << "x" << p.tilelength
            << ")";
}

namespace
{

  std::vector<TileTestParameters>
  find_tile_tests()
  {
    std::vector<TileTestParameters> params;

    path dir(PROJECT_BINARY_DIR "/cpp/test/ome-bioformats/data");
    if (exists(dir) && is_directory(dir))
      {
        for(directory_iterator i(dir); i != directory_iterator(); ++i)
          {
            static std::regex tile_match(".*/data-layout-([[:digit:]]+)x([[:digit:]]+)-([[:alpha:]]+)-tiles-([[:digit:]]+)x([[:digit:]]+)\\.tiff");
            static std::regex strip_match(".*/data-layout-([[:digit:]]+)x([[:digit:]]+)-([[:alpha:]]+)-strips-([[:digit:]]+)\\.tiff");

            std::smatch found;
            std::string file(i->path().string());
            path wpath(i->path().parent_path());
            wpath /= std::string("w-") + i->path().filename().string();
            std::string wfile(wpath.string());
            if (std::regex_match(file, found, tile_match))
              {
                TileTestParameters p;
                p.tile = true;
                p.file = file;
                p.wfile = wfile;

                std::istringstream iwid(found[1]);
                if (!(iwid >> p.imagewidth))
                  continue;

                std::istringstream iht(found[2]);
                if (!(iht >> p.imagelength))
                  continue;

                p.imageplanar = false;
                if (found[3] == "planar")
                  p.imageplanar = true;

                std::istringstream twid(found[4]);
                if (!(twid >> p.tilewidth))
                  continue;

                std::istringstream tht(found[5]);
                if (!(tht >> p.tilelength))
                  continue;

                params.push_back(p);
              }
            else if (std::regex_match(file, found, strip_match))
              {
                TileTestParameters p;
                p.tile = false;
                p.file = file;
                p.wfile = wfile;

                std::istringstream iwid(found[1]);
                if (!(iwid >> p.imagewidth))
                  continue;

                std::istringstream iht(found[2]);
                if (!(iht >> p.imagelength))
                  continue;

                p.imageplanar = false;
                if (found[3] == "planar")
                  p.imageplanar = true;

                p.tilewidth = p.imagewidth;

                std::istringstream srow(found[4]);
                if (!(srow >> p.tilelength))
                  continue;

                params.push_back(p);
              }
          }
      }

    return params;
  }

  struct DumpPixelBufferVisitor : public boost::static_visitor<>
  {
    typedef ::ome::bioformats::PixelProperties< ::ome::xml::model::enums::PixelType::BIT>::std_type bit_type;

    std::ostream& stream;

    DumpPixelBufferVisitor(std::ostream& stream):
      stream(stream)
    {}

    template <typename T>
    typename boost::enable_if_c<
      boost::is_integral<T>::value, float
      >::type
    dump(const std::shared_ptr< ::ome::bioformats::PixelBuffer<T> >& buf,
         const typename ::ome::bioformats::PixelBuffer<T>::indices_type& idx) const
    {
      float v = static_cast<float>(buf->at(idx));
      float max = static_cast<float>(std::numeric_limits<T>::max());

      return v / max;
    }

    template <typename T>
    typename boost::enable_if_c<
      boost::is_floating_point<T>::value, float
      >::type
    dump(const std::shared_ptr< ::ome::bioformats::PixelBuffer<T> >& buf,
         const typename ::ome::bioformats::PixelBuffer<T>::indices_type& idx) const
    {
      // Assume float is already normalised.
      return static_cast<float>(buf->at(idx));
    }

    template <typename T>
    typename boost::enable_if_c<
      boost::is_complex<T>::value, float
      >::type
    dump(const std::shared_ptr< ::ome::bioformats::PixelBuffer<T> >& buf,
         const typename ::ome::bioformats::PixelBuffer<T>::indices_type& idx) const
    {
      // Assume float is already normalised.
      return static_cast<float>(buf->at(idx).real());
    }

    // Split the pixel range into two, the lower half being set to false
    // and the upper half being set to true for the destination boolean
    // pixel type.
    float
    dump(const std::shared_ptr< ::ome::bioformats::PixelBuffer<bit_type> >& buf,
         const ::ome::bioformats::PixelBuffer<bit_type>::indices_type& idx)
    {
      return buf->at(idx) ? 1.0f : 0.0f;
    }

    template<typename T>
    void
    operator()(const T& buf) const
    {
      const VariantPixelBuffer::size_type *shape = buf->shape();
      VariantPixelBuffer::size_type w = shape[ome::bioformats::DIM_SPATIAL_X];
      VariantPixelBuffer::size_type h = shape[ome::bioformats::DIM_SPATIAL_X];
      VariantPixelBuffer::size_type s = shape[ome::bioformats::DIM_SUBCHANNEL];

      typename T::element_type::indices_type idx;
      idx[ome::bioformats::DIM_SPATIAL_X] = 0;
      idx[ome::bioformats::DIM_SPATIAL_Y] = 0;
      idx[ome::bioformats::DIM_SUBCHANNEL] = 0;
      idx[ome::bioformats::DIM_SPATIAL_Z] = idx[ome::bioformats::DIM_TEMPORAL_T] =
        idx[ome::bioformats::DIM_CHANNEL] = idx[ome::bioformats::DIM_MODULO_Z] =
        idx[ome::bioformats::DIM_MODULO_T] = idx[ome::bioformats::DIM_MODULO_C] = 0;

      const char * const shades[] = {" ", "░", "▒", "▓", "█"};

      for (VariantPixelBuffer::size_type y = 0; y < h; ++y)
        {
          std::vector<std::string> line(s);
          for (VariantPixelBuffer::size_type x = 0; x < w; ++x)
            {
              for (VariantPixelBuffer::size_type c = 0; c < s; ++c)
                {
                  idx[ome::bioformats::DIM_SPATIAL_X] = x;
                  idx[ome::bioformats::DIM_SPATIAL_Y] = y;
                  idx[ome::bioformats::DIM_SUBCHANNEL] = c;

                  uint16_t shadeidx = static_cast<uint16_t>(std::floor(dump(buf, idx) * 5.0f));
                  if (shadeidx > 4)
                    shadeidx = 4;
                  line[c] += shades[shadeidx];
                }
            }
          for (std::vector<std::string>::const_iterator i = line.begin();
               i != line.end();
               ++i)
            {
              stream << *i;
              if (i + 1 != line.end())
                stream << "  ";
            }
          stream << '\n';
        }
    }
  };

  void
  dump_image_representation(const VariantPixelBuffer& buf,
                            std::ostream&             stream)
  {
    DumpPixelBufferVisitor v(stream);
    boost::apply_visitor(v, buf.vbuffer());
  }

}

std::vector<TileTestParameters> tile_params(find_tile_tests());

TEST(TIFFTest, RegionIntersection1)
{
  PlaneRegion r1(0, 0, 50, 100);
  PlaneRegion r2(25, 30, 100, 50);
  PlaneRegion r3 = r1 & r2;
  EXPECT_EQ(25U, r3.x);
  EXPECT_EQ(30U, r3.y);
  EXPECT_EQ(25U, r3.w);
  EXPECT_EQ(50U, r3.h);
  PlaneRegion r4 = r2 & r1;
  EXPECT_EQ(25U, r4.x);
  EXPECT_EQ(30U, r4.y);
  EXPECT_EQ(25U, r4.w);
  EXPECT_EQ(50U, r4.h);
}

TEST(TIFFTest, RegionIntersection2)
{
  PlaneRegion r1(0, 0, 100, 100);
  PlaneRegion r2(25, 25, 25, 25);
  PlaneRegion r3 = r1 & r2;
  EXPECT_EQ(25U, r3.x);
  EXPECT_EQ(25U, r3.y);
  EXPECT_EQ(25U, r3.w);
  EXPECT_EQ(25U, r3.h);
  PlaneRegion r4 = r2 & r1;
  EXPECT_EQ(25U, r4.x);
  EXPECT_EQ(25U, r4.y);
  EXPECT_EQ(25U, r4.w);
  EXPECT_EQ(25U, r4.h);
}

TEST(TIFFTest, RegionIntersection3)
{
  PlaneRegion r1(40, 190, 29, 18);
  PlaneRegion r2(40, 190, 29, 18);
  PlaneRegion r3 = r1 & r2;
  EXPECT_EQ(40U, r3.x);
  EXPECT_EQ(190U, r3.y);
  EXPECT_EQ(29U, r3.w);
  EXPECT_EQ(18U, r3.h);
  PlaneRegion r4 = r2 & r1;
  EXPECT_EQ(40U, r4.x);
  EXPECT_EQ(190U, r4.y);
  EXPECT_EQ(29U, r4.w);
  EXPECT_EQ(18U, r4.h);
}

TEST(TIFFTest, RegionIntersection4)
{
  PlaneRegion r1(20, 30, 80, 50);
  PlaneRegion r2(200, 25, 60, 20);
  PlaneRegion r3 = r1 & r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(0U, r3.w);
  EXPECT_EQ(0U, r3.h);
  PlaneRegion r4 = r2 & r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(0U, r4.w);
  EXPECT_EQ(0U, r4.h);
}

TEST(TIFFTest, RegionIntersection5)
{
  PlaneRegion r1(20, 400, 80, 50);
  PlaneRegion r2(30, 25, 60, 45);
  PlaneRegion r3 = r1 & r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(0U, r3.w);
  EXPECT_EQ(0U, r3.h);
  PlaneRegion r4 = r2 & r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(0U, r4.w);
  EXPECT_EQ(0U, r4.h);
}

TEST(TIFFTest, RegionUnion1)
{
  PlaneRegion r1(0, 0, 16, 16);
  PlaneRegion r2(16, 0, 16, 16);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(32U, r3.w);
  EXPECT_EQ(16U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(32U, r4.w);
  EXPECT_EQ(16U, r4.h);
}

TEST(TIFFTest, RegionUnion2)
{
  PlaneRegion r1(0, 0, 16, 16);
  PlaneRegion r2(0, 16, 16, 16);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(16U, r3.w);
  EXPECT_EQ(32U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(16U, r4.w);
  EXPECT_EQ(32U, r4.h);
}

TEST(TIFFTest, RegionUnion3)
{
  PlaneRegion r1(43, 23, 12, 15);
  PlaneRegion r2(55, 23, 44, 15);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(43U, r3.x);
  EXPECT_EQ(23U, r3.y);
  EXPECT_EQ(56U, r3.w);
  EXPECT_EQ(15U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(43U, r4.x);
  EXPECT_EQ(23U, r4.y);
  EXPECT_EQ(56U, r4.w);
  EXPECT_EQ(15U, r4.h);
}

TEST(TIFFTest, RegionUnion4)
{
  PlaneRegion r1(22, 19, 27, 80);
  PlaneRegion r2(22, 99, 27, 11);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(22U, r3.x);
  EXPECT_EQ(19U, r3.y);
  EXPECT_EQ(27U, r3.w);
  EXPECT_EQ(91U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(22U, r4.x);
  EXPECT_EQ(19U, r4.y);
  EXPECT_EQ(27U, r4.w);
  EXPECT_EQ(91U, r4.h);
}

TEST(TIFFTest, RegionUnion5)
{
  // No overlap or common edge
  PlaneRegion r1(43, 23, 12, 15);
  PlaneRegion r2(95, 83, 43, 15);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(0U, r3.w);
  EXPECT_EQ(0U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(0U, r4.w);
  EXPECT_EQ(0U, r4.h);
}

TEST(TIFFTest, RegionUnion6)
{
  // Overlap
  PlaneRegion r1(43, 23, 12, 15);
  PlaneRegion r2(50, 28, 12, 15);
  PlaneRegion r3 = r1 | r2;
  EXPECT_EQ(0U, r3.x);
  EXPECT_EQ(0U, r3.y);
  EXPECT_EQ(0U, r3.w);
  EXPECT_EQ(0U, r3.h);
  PlaneRegion r4 = r2 | r1;
  EXPECT_EQ(0U, r4.x);
  EXPECT_EQ(0U, r4.y);
  EXPECT_EQ(0U, r4.w);
  EXPECT_EQ(0U, r4.h);
}

TEST(TIFFTest, RegionValid)
{
  // Overlap
  PlaneRegion r1;
  ASSERT_TRUE(!r1.valid());
  ASSERT_FALSE(r1.valid());

  PlaneRegion r2(0, 0, 5, 2);
  ASSERT_TRUE(r2.valid());
  ASSERT_FALSE(!r2.valid());
}

TEST(TIFFTest, RegionArea)
{
  PlaneRegion r1;
  ASSERT_EQ(0U, r1.area());

  PlaneRegion r2(0, 0, 4, 2);
  ASSERT_EQ(4U*2U, r2.area());
}

TEST(TIFFTest, Construct)
{
  ASSERT_NO_THROW(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));
}

TEST(TIFFTest, ConstructFailMode)
{
  ASSERT_THROW(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "XK"),
               ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, ConstructFailFile)
{
  ASSERT_THROW(TIFF::open(PROJECT_SOURCE_DIR "/CMakeLists.txt", "r"), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, IFDsByIndex)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  for (directory_index_type i = 0; i < 10; ++i)
    {
      std::shared_ptr<IFD> ifd;
      ASSERT_NO_THROW(ifd = t->getDirectoryByIndex(i));
    }

  ASSERT_THROW(t->getDirectoryByIndex(40), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, IFDsByOffset)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  for (directory_index_type i = 0; i < 10; ++i)
    {
      uint64_t offset = t->getDirectoryByIndex(i)->getOffset();
      std::shared_ptr<IFD> ifd;
      ASSERT_NO_THROW(ifd = t->getDirectoryByOffset(offset));
      ASSERT_EQ(ifd->getOffset(), offset);
    }

  ASSERT_THROW(t->getDirectoryByOffset(0), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, IFDSimpleIter)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd = t->getDirectoryByIndex(0);

  while(ifd)
    {
      ifd = ifd->next();
    }
}

TEST(TIFFTest, TIFFIter)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  for (TIFF::iterator pos = t->begin(); pos != t->end(); ++pos)
    {
    }
}

TEST(TIFFTest, TIFFConstIter)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  for (TIFF::const_iterator pos = t->begin(); pos != t->end(); ++pos)
    {
    }
}

TEST(TIFFTest, TIFFConstIter2)
{
  std::shared_ptr<const TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  for (TIFF::const_iterator pos = t->begin(); pos != t->end(); ++pos)
    {
    }
}

TEST(TIFFTest, RawField)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  char *text;
  ifd->getRawField(270, &text);
}

TEST(TIFFTest, RawField0)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  char *text;
  ASSERT_THROW(ifd->getRawField(0, &text), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapString)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::string text;
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::ARTIST).get(text), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::COPYRIGHT).get(text), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::DATETIME).get(text), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::DOCUMENTNAME).get(text), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::HOSTCOMPUTER).get(text), ome::bioformats::tiff::Exception);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::IMAGEDESCRIPTION).get(text));
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::MAKE).get(text), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::MODEL).get(text), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::PAGENAME).get(text), ome::bioformats::tiff::Exception);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::SOFTWARE).get(text));
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::TARGETPRINTER).get(text), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapStringArray)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::vector<std::string> text;
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::INKNAMES).get(text), ome::bioformats::tiff::Exception);
  for (std::vector<std::string>::const_iterator i = text.begin(); i != text.end(); ++i)
    {
    }
}

TEST(TIFFTest, FieldWrapUInt16)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  uint16_t value;

  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::BITSPERSAMPLE).get(value));
  ASSERT_EQ(8, value);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::CLEANFAXDATA).get(value), ome::bioformats::tiff::Exception);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::COMPRESSION).get(value));
  ASSERT_EQ(1, value);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::DATATYPE).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::INDEXED).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::INKSET).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::MATTEING).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::MAXSAMPLEVALUE).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::MINSAMPLEVALUE).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::RESOLUTIONUNIT).get(value), ome::bioformats::tiff::Exception);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::SAMPLESPERPIXEL).get(value));
  ASSERT_EQ(1, value);
}

TEST(TIFFTest, FieldWrapFillOrder)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  ome::bioformats::tiff::FillOrder value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::FILLORDER).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapOrientation)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  ome::bioformats::tiff::Orientation value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::ORIENTATION).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapPlanarConfiguration)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  ome::bioformats::tiff::PlanarConfiguration value;

  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::PLANARCONFIG).get(value));
  ASSERT_EQ(ome::bioformats::tiff::SEPARATE, value);
}

TEST(TIFFTest, FieldWrapPhotometricInterpretation)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  ome::bioformats::tiff::PhotometricInterpretation value;

  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::PHOTOMETRIC).get(value));
  ASSERT_EQ(ome::bioformats::tiff::MIN_IS_BLACK, value);
}

TEST(TIFFTest, FieldWrapPredictor)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  ome::bioformats::tiff::Predictor value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::PREDICTOR).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapSampleFormat)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  ome::bioformats::tiff::SampleFormat value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::SAMPLEFORMAT).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapThreshholding)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  ome::bioformats::tiff::Threshholding value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::THRESHHOLDING).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapYCbCrPosition)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  ome::bioformats::tiff::YCbCrPosition value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::YCBCRPOSITIONING).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapUInt16Pair)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::array<uint16_t, 2> value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::DOTRANGE).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::HALFTONEHINTS).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::PAGENUMBER).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::YCBCRSUBSAMPLING).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapFloat)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  float value = -1.0f;

  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::XRESOLUTION).get(value));
  ASSERT_FLOAT_EQ(1.0f, value);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::YRESOLUTION).get(value));
  ASSERT_FLOAT_EQ(1.0f, value);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::XPOSITION).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::YPOSITION).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapFloat2)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::array<float, 2> value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::WHITEPOINT).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapFloat3)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::array<float, 3> value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::YCBCRCOEFFICIENTS).get(value), ome::bioformats::tiff::Exception);
}


TEST(TIFFTest, FieldWrapFloat6)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::array<float, 6> value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::PRIMARYCHROMATICITIES).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::REFERENCEBLACKWHITE).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapUInt16ExtraSamplesArray)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::vector<ome::bioformats::tiff::ExtraSamples> value;
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::EXTRASAMPLES).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapUInt16Array3)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::array<std::vector<uint16_t>, 3> value;
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::COLORMAP).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::TRANSFERFUNCTION).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapUInt32)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  uint32_t value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::BADFAXLINES).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::CONSECUTIVEBADFAXLINES).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::GROUP3OPTIONS).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::GROUP4OPTIONS).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::IMAGEDEPTH).get(value), ome::bioformats::tiff::Exception);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::IMAGELENGTH).get(value));
  ASSERT_EQ(24U, value);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::IMAGEWIDTH).get(value));
  ASSERT_EQ(18U, value);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::ROWSPERSTRIP).get(value));
  ASSERT_EQ(1U, value);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::SUBFILETYPE).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::TILEDEPTH).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::TILELENGTH).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::TILEWIDTH).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapUInt32Array)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::vector<uint32_t> value;
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::IMAGEJ_META_DATA_BYTE_COUNTS).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::RICHTIFFIPTC).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapUInt64Array)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::vector<uint64_t> value;
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::SUBIFD).get(value), ome::bioformats::tiff::Exception);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::STRIPBYTECOUNTS).get(value));
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::STRIPOFFSETS).get(value));
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::TILEBYTECOUNTS).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::TILEOFFSETS).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, FieldWrapByteArray)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::vector<uint8_t> value;

  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::ICCPROFILE).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::JPEGTABLES).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::PHOTOSHOP).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::XMLPACKET).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::IMAGEJ_META_DATA).get(value), ome::bioformats::tiff::Exception);
}

TEST(TIFFTest, ValueProxy)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::string text;
  ome::bioformats::tiff::ValueProxy<std::string> d(text);
  d = ifd->getField(ome::bioformats::tiff::IMAGEDESCRIPTION);
}

TEST(TIFFTest, Value)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  ome::bioformats::tiff::Value<std::string> text;
  text = ifd->getField(ome::bioformats::tiff::IMAGEDESCRIPTION);
}

TEST(TIFFTest, FieldName)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::string name;
  name = ifd->getField(ome::bioformats::tiff::IMAGEDESCRIPTION).name();

#if defined(TIFF_HAVE_FIELD) || defined(TIFF_HAVE_FIELDINFO)
  ASSERT_EQ(std::string("ImageDescription"), name);
#else
  ASSERT_EQ(std::string("Unknown"), name);
#endif
}

TEST(TIFFTest, FieldCount)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  std::string name;
  bool count = ifd->getField(ome::bioformats::tiff::IMAGEDESCRIPTION).passCount();

  ASSERT_FALSE(count);
}

TEST(TIFFTest, PixelType)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  ASSERT_EQ(PT::UINT8, ifd->getPixelType());
}

class TIFFTileTest : public ::testing::TestWithParam<TileTestParameters>
{
public:
  std::shared_ptr<TIFF> tiff;
  std::shared_ptr<IFD> ifd;
  uint32_t iwidth;
  uint32_t iheight;
  ome::bioformats::tiff::PlanarConfiguration planarconfig;
  uint16_t samples;
#ifdef PNG_FOUND
  static VariantPixelBuffer pngdata_chunky;
  static VariantPixelBuffer pngdata_planar;
  typedef std::map<std::pair<PT,ome::bioformats::tiff::PlanarConfiguration>,VariantPixelBuffer> pngdata_map_type;
  static pngdata_map_type pngdata_map;
  static bool pngdata_init;
#endif // PNG_FOUND
  static uint32_t pwidth;
  static uint32_t pheight;

#ifdef PNG_FOUND
  static void
  readPNGData()
  {
    // Sample image to check validity of TIFF reading.
    const char * const pngfile = PROJECT_SOURCE_DIR "/cpp/test/ome-bioformats/data/data-layout.png";

    std::FILE *png = std::fopen(pngfile, "rb");
    ASSERT_TRUE(png);
    uint8_t header[8];
    std::fread(header, 1, 8, png);
    ASSERT_FALSE((png_sig_cmp(header, 0, 8)));

    png_structp pngptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
    ASSERT_TRUE(pngptr != 0);

    png_infop infoptr = png_create_info_struct(pngptr);
    if (!infoptr)
      png_destroy_read_struct(&pngptr, 0, 0);
    ASSERT_TRUE(infoptr != 0);

    png_infop endinfoptr = png_create_info_struct(pngptr);
    if (!endinfoptr)
      png_destroy_read_struct(&pngptr, 0, 0);
    ASSERT_TRUE(endinfoptr != 0);

    int result = setjmp(png_jmpbuf(pngptr));
    ASSERT_FALSE((result));

    png_init_io(pngptr, png);
    png_set_sig_bytes(pngptr, 8);

    png_read_info(pngptr, infoptr);

    pwidth = png_get_image_width(pngptr, infoptr);
    pheight = png_get_image_height(pngptr, infoptr);
    png_byte color_type = png_get_color_type(pngptr, infoptr);
    ASSERT_EQ(PNG_COLOR_TYPE_RGB, color_type);
    png_byte bit_depth = png_get_bit_depth(pngptr, infoptr);
    ASSERT_EQ(8U, bit_depth);

    png_set_interlace_handling(pngptr);
    png_read_update_info(pngptr, infoptr);

    std::array<VariantPixelBuffer::size_type, 9> shape;
    shape[::ome::bioformats::DIM_SPATIAL_X] = pwidth;
    shape[::ome::bioformats::DIM_SPATIAL_Y] = pheight;
    shape[::ome::bioformats::DIM_SUBCHANNEL] = 3U;
    shape[::ome::bioformats::DIM_SPATIAL_Z] = shape[::ome::bioformats::DIM_TEMPORAL_T] = shape[::ome::bioformats::DIM_CHANNEL] =
      shape[::ome::bioformats::DIM_MODULO_Z] = shape[::ome::bioformats::DIM_MODULO_T] = shape[::ome::bioformats::DIM_MODULO_C] = 1;

    ::ome::bioformats::PixelBufferBase::storage_order_type order_chunky(::ome::bioformats::PixelBufferBase::default_storage_order());
    ::ome::bioformats::PixelBufferBase::storage_order_type order_planar(::ome::bioformats::PixelBufferBase::make_storage_order(::ome::xml::model::enums::DimensionOrder::XYZTC, false));

    pngdata_chunky.setBuffer(shape, PT::UINT8, order_chunky);
    pngdata_planar.setBuffer(shape, PT::UINT8, order_planar);

    std::vector<png_bytep> row_pointers(pheight);
    for (dimension_size_type y = 0; y < pheight; ++y)
      {
        VariantPixelBuffer::indices_type coord;
        coord[ome::bioformats::DIM_SPATIAL_X] = 0;
        coord[ome::bioformats::DIM_SPATIAL_Y] = y;
        coord[ome::bioformats::DIM_SUBCHANNEL] = 0;
        coord[ome::bioformats::DIM_SPATIAL_Z] = coord[ome::bioformats::DIM_TEMPORAL_T] =
          coord[ome::bioformats::DIM_CHANNEL] = coord[ome::bioformats::DIM_MODULO_Z] =
          coord[ome::bioformats::DIM_MODULO_T] = coord[ome::bioformats::DIM_MODULO_C] = 0;

        row_pointers[y] = reinterpret_cast<png_bytep>(&pngdata_chunky.at< PixelProperties<PT::UINT8>::std_type>(coord));
      }

    png_read_image(pngptr, &row_pointers[0]);

    png_read_end(pngptr, infoptr);

    png_destroy_read_struct(&pngptr, &infoptr, &endinfoptr);

    std::fclose(png);
    png = 0;

    pngdata_planar = pngdata_chunky;
    ASSERT_TRUE(pngdata_chunky == pngdata_planar);

    pngdata_init = true;
  }

  static const VariantPixelBuffer&
  getPNGDataChunky()
  {
    if (!pngdata_init)
      readPNGData();
    return pngdata_chunky;
  }

  static const VariantPixelBuffer&
  getPNGDataPlanar()
  {
    if (!pngdata_init)
      readPNGData();
    return pngdata_planar;
  }

  static const VariantPixelBuffer&
  getPNGData(PT pixeltype,
             ome::bioformats::tiff::PlanarConfiguration planarconfig)
  {
    pngdata_map_type::const_iterator found = pngdata_map.find(std::make_pair(pixeltype, planarconfig));
    if (found == pngdata_map.end())
      {
        const VariantPixelBuffer& src(planarconfig == ome::bioformats::tiff::CONTIG ? getPNGDataChunky() : getPNGDataPlanar());

        const VariantPixelBuffer::size_type *shape = src.shape();

        VariantPixelBuffer dest(boost::extents[shape[0]][shape[1]][shape[2]][shape[3]][shape[4]][shape[5]][shape[6]][shape[7]][shape[8]], pixeltype, src.storage_order());

        PixelTypeConversionVisitor<PT::UINT8> v(src, dest);
        boost::apply_visitor(v, dest.vbuffer());

        pngdata_map_type::key_type key(pixeltype, planarconfig);
        std::pair<pngdata_map_type::iterator,bool> ins
          (pngdata_map.insert(pngdata_map_type::value_type(key, dest)));

        if (!ins.second)
          throw std::runtime_error("Failed to cache test pixel data");

        found = ins.first;
      }

    return found->second;
  }
#endif // PNG_FOUND

  virtual void SetUp()
  {
    const TileTestParameters& params = GetParam();

    tiff = TIFF::open(params.file, "r");
    ifd = tiff->getDirectoryByIndex(0);

    ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::IMAGEWIDTH).get(iwidth));
    ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::IMAGELENGTH).get(iheight));
    ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::PLANARCONFIG).get(planarconfig));
    ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::SAMPLESPERPIXEL).get(samples));
  }

};

#ifdef PNG_FOUND
VariantPixelBuffer TIFFTileTest::pngdata_chunky;
VariantPixelBuffer TIFFTileTest::pngdata_planar;
TIFFTileTest::pngdata_map_type TIFFTileTest::pngdata_map;
bool TIFFTileTest::pngdata_init = false;
#endif // PNG_FOUND
uint32_t TIFFTileTest::pwidth = 0;
uint32_t TIFFTileTest::pheight = 0;

// Check basic tile metadata
TEST_P(TIFFTileTest, TileInfo)
{
  const TileTestParameters& params = GetParam();
  TileInfo info = ifd->getTileInfo();

  EXPECT_EQ(params.tilewidth, info.tileWidth());
  EXPECT_EQ(params.tilelength, info.tileHeight());
  EXPECT_NE(0U, info.bufferSize());

  dimension_size_type ecol = iwidth / params.tilewidth;
  if (iwidth % params.tilewidth)
    ++ecol;
  dimension_size_type erow = iheight / params.tilelength;
  if (iheight % params.tilelength)
    ++erow;
  EXPECT_EQ(erow, info.tileRowCount());
  EXPECT_EQ(ecol, info.tileColumnCount());

  if (params.imageplanar)
    ASSERT_EQ(ome::bioformats::tiff::SEPARATE, planarconfig);
  else
    ASSERT_EQ(ome::bioformats::tiff::CONTIG, planarconfig);

  if (params.tile)
    ASSERT_EQ(ome::bioformats::tiff::TILE, info.tileType());
}

// Check that the first tile matches the expected tile size
TEST_P(TIFFTileTest, TilePlaneRegion0)
{
  const TileTestParameters& params = GetParam();
  TileInfo info = ifd->getTileInfo();

  PlaneRegion full(0, 0, iwidth, iheight);

  PlaneRegion region0 = info.tileRegion(0, full);
  EXPECT_EQ(0U, region0.x);
  EXPECT_EQ(0U, region0.y);
  EXPECT_EQ(params.tilewidth, region0.w);
  EXPECT_EQ(params.tilelength, region0.h);
}

// Check tiling of whole image including edge overlaps being correctly
// computed and all tiles being accounted for.
TEST_P(TIFFTileTest, PlaneArea1)
{
  const TileTestParameters& params = GetParam();
  TileInfo info = ifd->getTileInfo();

  PlaneRegion full(0, 0, iwidth, iheight);
  std::vector<dimension_size_type> tiles = info.tileCoverage(full);
  EXPECT_EQ(info.tileCount(), tiles.size());

  dimension_size_type area = 0;
  std::vector<PlaneRegion> regions;
  for (std::vector<dimension_size_type>::const_iterator i = tiles.begin();
       i != tiles.end();
       ++i)
    {
      PlaneRegion r = info.tileRegion(*i, full);
      regions.push_back(r);
      area += (r.w * r.h);
    }
  if (params.imageplanar)
    {
      EXPECT_EQ(0U, area % samples);
      area /= samples;
    }

  EXPECT_EQ((full.w * full.h), area);

  // Check there are no overlaps.
  for (std::vector<PlaneRegion>::size_type i = 0; i < regions.size(); ++i)
    for (std::vector<PlaneRegion>::size_type j = 0; j < regions.size(); ++j)
      {
        if (i == j)
          continue;

        // Overlaps expected between different subchannels
        if (info.tileSample(tiles.at(i)) != info.tileSample(tiles.at(j)))
          continue;

        PlaneRegion overlap = regions.at(i) & regions.at(j);

        EXPECT_EQ(0U, overlap.w * overlap.h);
      }
}

// Check tiling of multiple-of-16 subrange including edge overlaps
// being correctly computed and all tiles being accounted for.
TEST_P(TIFFTileTest, PlaneArea2)
{
  const TileTestParameters& params = GetParam();
  TileInfo info = ifd->getTileInfo();

  PlaneRegion partial(16U, 16U, iwidth - 32U, iheight - 32U);
  std::vector<dimension_size_type> tiles = info.tileCoverage(partial);

  dimension_size_type area = 0;
  std::vector<PlaneRegion> regions;
  for (std::vector<dimension_size_type>::const_iterator i = tiles.begin();
       i != tiles.end();
       ++i)
    {
      PlaneRegion r = info.tileRegion(*i, partial);
      regions.push_back(r);
      area += (r.w * r.h);
    }
  if (params.imageplanar)
    {
      EXPECT_EQ(0U, area % samples);
      area /= samples;
    }

  EXPECT_EQ((partial.w * partial.h), area);

  // Check there are no overlaps.
  for (std::vector<PlaneRegion>::size_type i = 0; i < regions.size(); ++i)
    for (std::vector<PlaneRegion>::size_type j = 0; j < regions.size(); ++j)
      {
        if (i == j)
          continue;

        if (info.tileSample(tiles.at(i)) != info.tileSample(tiles.at(j)))
          continue;

        PlaneRegion overlap = regions.at(i) & regions.at(j);

        EXPECT_EQ(0U, overlap.w * overlap.h);
      }
}

// Check tiling of non-multiple-of-16 subrange including edge overlaps
// being correctly computed and all tiles being accounted for.
TEST_P(TIFFTileTest, PlaneArea3)
{
  const TileTestParameters& params = GetParam();
  TileInfo info = ifd->getTileInfo();

  PlaneRegion partial(19U, 31U, iwidth - 39U, iheight - 40U);
  std::vector<dimension_size_type> tiles = info.tileCoverage(partial);

  dimension_size_type area = 0;
  std::vector<PlaneRegion> regions;
  for (std::vector<dimension_size_type>::const_iterator i = tiles.begin();
       i != tiles.end();
       ++i)
    {
      PlaneRegion r = info.tileRegion(*i, partial);
      regions.push_back(r);
      area += (r.w * r.h);
    }
  if (params.imageplanar)
    {
      EXPECT_EQ(0U, area % samples);
      area /= samples;
    }

  EXPECT_EQ((partial.w * partial.h), area);

  // Check there are no overlaps.
  for (std::vector<PlaneRegion>::size_type i = 0; i < regions.size(); ++i)
    for (std::vector<PlaneRegion>::size_type j = 0; j < regions.size(); ++j)
      {
        if (i == j)
          continue;

        if (info.tileSample(tiles.at(i)) != info.tileSample(tiles.at(j)))
          continue;

        PlaneRegion overlap = regions.at(i) & regions.at(j);

        EXPECT_EQ(0U, overlap.w * overlap.h);
      }
}

namespace
{
  void
  read_test(const std::string& file,
            const VariantPixelBuffer& reference)
  {
    std::shared_ptr<TIFF> tiff(TIFF::open(file, "r"));
    std::shared_ptr<IFD> ifd(tiff->getDirectoryByIndex(0));
    EXPECT_EQ(64U, ifd->getImageWidth());
    EXPECT_EQ(64U, ifd->getImageHeight());

    VariantPixelBuffer vb;
    ifd->readImage(vb);

#ifdef PNG_FOUND
    if(reference != vb)
      {
        std::cout << "Observed\n";
        dump_image_representation(vb, std::cout);
        std::cout << "Expected\n";
        dump_image_representation(reference, std::cout);
      }
    ASSERT_TRUE(reference == vb);
#endif // PNG_FOUND
  }
}

TEST_P(TIFFTileTest, PlaneRead)
{
  const TileTestParameters& params = GetParam();

  read_test(params.file, getPNGDataChunky());
}

TEST_P(TIFFTileTest, PlaneReadAlignedTileOrdered)
{
  TileInfo info = ifd->getTileInfo();

  PlaneRegion full(0, 0, ifd->getImageWidth(), ifd->getImageHeight());
  std::vector<dimension_size_type> tiles = info.tileCoverage(full);

  VariantPixelBuffer vb;

  for (std::vector<dimension_size_type>::const_iterator i = tiles.begin();
       i != tiles.end();
       ++i)
    {
      PlaneRegion r = info.tileRegion(*i, full);

      ifd->readImage(vb, r.x, r.y, r.w, r.h);

      /// @todo Verify buffer contents once pixelbuffer subsetting is
      /// available.
    }
}

TEST_P(TIFFTileTest, PlaneReadAlignedTileRandom)
{
  TileInfo info = ifd->getTileInfo();

  PlaneRegion full(0, 0, ifd->getImageWidth(), ifd->getImageHeight());
  std::vector<dimension_size_type> tiles = info.tileCoverage(full);

  VariantPixelBuffer vb;

  std::random_shuffle(tiles.begin(), tiles.end());
  for (std::vector<dimension_size_type>::const_iterator i = tiles.begin();
       i != tiles.end();
       ++i)
    {
      PlaneRegion r = info.tileRegion(*i, full);

      ifd->readImage(vb, r.x, r.y, r.w, r.h);

      /// @todo Verify buffer contents once pixelbuffer subsetting is
      /// available.
    }
}

TEST_P(TIFFTileTest, PlaneReadUnalignedTileOrdered)
{
  TileInfo info = ifd->getTileInfo();

  PlaneRegion full(0, 0, ifd->getImageWidth(), ifd->getImageHeight());

  std::vector<PlaneRegion> tiles;
  for (dimension_size_type x = 0; x < full.w; x+= 5)
    for (dimension_size_type y = 0; y < full.h; y+= 7)
      {
        PlaneRegion r = PlaneRegion(x, y, 5, 7) & full;
        tiles.push_back(r);
      }

  VariantPixelBuffer vb;

  for (std::vector<PlaneRegion>::const_iterator i = tiles.begin();
       i != tiles.end();
       ++i)
    {
      const PlaneRegion& r = *i;

      ifd->readImage(vb, r.x, r.y, r.w, r.h);

      /// @todo Verify buffer contents once pixelbuffer subsetting is
      /// available.
    }
}

TEST_P(TIFFTileTest, PlaneReadUnalignedTileRandom)
{
  TileInfo info = ifd->getTileInfo();

  PlaneRegion full(0, 0, ifd->getImageWidth(), ifd->getImageHeight());

  std::vector<PlaneRegion> tiles;
  for (dimension_size_type x = 0; x < full.w; x+= 5)
    for (dimension_size_type y = 0; y < full.h; y+= 7)
      {
        PlaneRegion r = PlaneRegion(x, y, 5, 7) & full;
        tiles.push_back(r);
      }

  VariantPixelBuffer vb;

  std::random_shuffle(tiles.begin(), tiles.end());
  for (std::vector<PlaneRegion>::const_iterator i = tiles.begin();
       i != tiles.end();
       ++i)
    {
      const PlaneRegion& r = *i;

      ifd->readImage(vb, r.x, r.y, r.w, r.h);

      /// @todo Verify buffer contents once pixelbuffer subsetting is
      /// available.
    }
}

class PixelTestParameters
{
public:
  PT pixeltype;
  ome::bioformats::tiff::TileType                  tiletype;
  ome::bioformats::tiff::PlanarConfiguration       planarconfig;
  ome::bioformats::tiff::PhotometricInterpretation photometricinterp;
  dimension_size_type                              tilewidth;
  dimension_size_type                              tileheight;
  bool                                             optimal;
  bool                                             ordered;
  std::string                                      filename;

  PixelTestParameters(PT pixeltype,
                      ome::bioformats::tiff::TileType tiletype,
                      ome::bioformats::tiff::PlanarConfiguration planarconfig,
                      ome::bioformats::tiff::PhotometricInterpretation photometricinterp,
                      dimension_size_type tilewidth,
                      dimension_size_type tileheight,
                      bool optimal,
                      bool ordered):
    pixeltype(pixeltype),
    tiletype(tiletype),
    planarconfig(planarconfig),
    photometricinterp(photometricinterp),
    tilewidth(tilewidth),
    tileheight(tileheight),
    optimal(optimal),
    ordered(ordered)
  {
    std::ostringstream f;
    f << "data-layout-" << pixeltype << '-'
      << (planarconfig == ome::bioformats::tiff::CONTIG ? "chunky" : "planar") << '-'
      << "pi" << photometricinterp << '-'
      << (tiletype == ome::bioformats::tiff::TILE ? "tile" : "strip") << '-';
    if (tiletype == ome::bioformats::tiff::TILE)
      f << tilewidth << 'x' << tileheight;
    else
      f << tileheight;
    f << '-' << (ordered ? "ordered" : "random")
      << '-' << (optimal ? "optimal" : "suboptimal")
      << ".tiff";

    path dir(PROJECT_BINARY_DIR "/cpp/test/ome-bioformats/data");
    if (!exists(dir) && !is_directory(dir) && !create_directories(dir))
      throw std::runtime_error("Image directory unavailable and could not be created");
    dir /= f.str();
    filename = dir.string();
  }
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const PixelTestParameters& params)
{
  return os << params.filename;
}

class PixelTest : public ::testing::TestWithParam<PixelTestParameters>
{
};

TEST_P(PixelTest, WriteTIFF)
{
#ifdef PNG_FOUND
  const PixelTestParameters& params = GetParam();
  const VariantPixelBuffer& pixels(TIFFTileTest::getPNGData(params.pixeltype, params.planarconfig));
  const VariantPixelBuffer::size_type *shape = pixels.shape();

  dimension_size_type exp_size = (params.tilewidth * params.tileheight *
                                  ::ome::bioformats::bytesPerPixel(params.pixeltype) *
                                  (params.planarconfig == ::ome::bioformats::tiff::CONTIG ? shape[ome::bioformats::DIM_SUBCHANNEL] : 1));

  // Write TIFF
  {
    std::shared_ptr<TIFF> wtiff = TIFF::open(params.filename, "w");
    std::shared_ptr<IFD> wifd = wtiff->getCurrentDirectory();

    // Set IFD tags
    ASSERT_NO_THROW(wifd->setImageWidth(shape[ome::bioformats::DIM_SPATIAL_X]));
    ASSERT_NO_THROW(wifd->setImageHeight(shape[ome::bioformats::DIM_SPATIAL_Y]));
    ASSERT_NO_THROW(wifd->setTileType(params.tiletype));
    ASSERT_NO_THROW(wifd->setTileWidth(params.tilewidth));
    ASSERT_NO_THROW(wifd->setTileHeight(params.tileheight));
    ASSERT_NO_THROW(wifd->setPixelType(params.pixeltype));
    ASSERT_NO_THROW(wifd->setSamplesPerPixel(shape[ome::bioformats::DIM_SUBCHANNEL]));
    ASSERT_NO_THROW(wifd->setPlanarConfiguration(params.planarconfig));
    ASSERT_NO_THROW(wifd->setPhotometricInterpretation(params.photometricinterp));

    // Verify IFD tags
    EXPECT_EQ(shape[ome::bioformats::DIM_SPATIAL_X], wifd->getImageWidth());
    EXPECT_EQ(shape[ome::bioformats::DIM_SPATIAL_Y], wifd->getImageHeight());
    EXPECT_EQ(params.tiletype, wifd->getTileType());
    EXPECT_EQ(params.tilewidth, wifd->getTileWidth());
    EXPECT_EQ(params.tileheight, wifd->getTileHeight());
    EXPECT_EQ(params.pixeltype, wifd->getPixelType());
    EXPECT_EQ(shape[ome::bioformats::DIM_SUBCHANNEL], wifd->getSamplesPerPixel());
    EXPECT_EQ(params.planarconfig, wifd->getPlanarConfiguration());

    // Make sure our expectations about buffer size are correct
    if (params.pixeltype == PT::BIT)
      {
        dimension_size_type size;
        size = exp_size / 8;
        if (exp_size % 8)
          ++size;
        exp_size = size;
      }
    ASSERT_EQ(exp_size,
              wifd->getTileInfo().bufferSize());

    PlaneRegion full(0, 0, wifd->getImageWidth(), wifd->getImageHeight());

    dimension_size_type wtilewidth = params.tilewidth;
    dimension_size_type wtileheight = params.tileheight;
    if (!params.optimal)
      {
        wtilewidth = 5;
        wtileheight = 7;
      }

    std::vector<PlaneRegion> tiles;
    for (dimension_size_type x = 0; x < full.w; x+= wtilewidth)
      for (dimension_size_type y = 0; y < full.h; y+= wtileheight)
        {
          PlaneRegion r = PlaneRegion(x, y, wtilewidth, wtileheight) & full;
          tiles.push_back(r);
        }

    if (!params.ordered)
      std::random_shuffle(tiles.begin(), tiles.end());

    for (std::vector<PlaneRegion>::const_iterator i = tiles.begin();
         i != tiles.end();
         ++i)
      {
        const PlaneRegion& r = *i;

        std::array<VariantPixelBuffer::size_type, 9> shape;
        shape[::ome::bioformats::DIM_SPATIAL_X] = r.w;
        shape[::ome::bioformats::DIM_SPATIAL_Y] = r.h;
        shape[::ome::bioformats::DIM_SUBCHANNEL] = 3U;
        shape[::ome::bioformats::DIM_SPATIAL_Z] = shape[::ome::bioformats::DIM_TEMPORAL_T] = shape[::ome::bioformats::DIM_CHANNEL] =
          shape[::ome::bioformats::DIM_MODULO_Z] = shape[::ome::bioformats::DIM_MODULO_T] = shape[::ome::bioformats::DIM_MODULO_C] = 1;

        ::ome::bioformats::PixelBufferBase::storage_order_type order
            (::ome::bioformats::PixelBufferBase::make_storage_order(::ome::xml::model::enums::DimensionOrder::XYZTC,
                                                                    params.planarconfig == ::ome::bioformats::tiff::CONTIG));

        VariantPixelBuffer vb;
        vb.setBuffer(shape, params.pixeltype, order);

        // Temporary subrange to write into tile
        PixelSubrangeVisitor sv(r.x, r.y);
        boost::apply_visitor(sv, pixels.vbuffer(), vb.vbuffer());

        wifd->writeImage(vb, r.x, r.y, r.w, r.h);
      }

    wtiff->writeCurrentDirectory();
    wtiff->close();
  }

  // Read and validate TIFF
  {
    // Note "c" to disable automatic strip chopping so we can verify
    // the exact tag content of ROWSPERSTRIP.
    std::shared_ptr<TIFF> tiff(TIFF::open(params.filename, "rc"));
    std::shared_ptr<IFD> ifd(tiff->getDirectoryByIndex(0));

    EXPECT_EQ(shape[ome::bioformats::DIM_SPATIAL_X], ifd->getImageWidth());
    EXPECT_EQ(shape[ome::bioformats::DIM_SPATIAL_Y], ifd->getImageHeight());
    EXPECT_EQ(params.tiletype, ifd->getTileType());
    EXPECT_EQ(params.tilewidth, ifd->getTileWidth());
    EXPECT_EQ(params.tileheight, ifd->getTileHeight());
    EXPECT_EQ(params.pixeltype, ifd->getPixelType());
    EXPECT_EQ(shape[ome::bioformats::DIM_SUBCHANNEL], ifd->getSamplesPerPixel());
    EXPECT_EQ(params.planarconfig, ifd->getPlanarConfiguration());
    EXPECT_EQ(params.photometricinterp, ifd->getPhotometricInterpretation());

    VariantPixelBuffer vb;
    ifd->readImage(vb);

    if(pixels != vb)
      {
        std::cout << "Observed\n";
        dump_image_representation(vb, std::cout);
        std::cout << "Expected\n";
        dump_image_representation(pixels, std::cout);
      }
    EXPECT_TRUE(pixels == vb);
#endif // PNG_FOUND
  }

}

namespace
{

  PT
  ptkey(const std::pair<PT::enum_value, std::string>& mapval)
  {
    return PT(mapval.first);
  }

  std::vector<PixelTestParameters>
  pixel_tests()
  {
    std::vector<PixelTestParameters> ret;

    std::vector<dimension_size_type> tilesizes;
    tilesizes.push_back(16);
    tilesizes.push_back(32);
    tilesizes.push_back(48);
    tilesizes.push_back(64);

    std::vector<dimension_size_type> stripsizes;
    stripsizes.push_back(1);
    stripsizes.push_back(2);
    stripsizes.push_back(5);
    stripsizes.push_back(14);
    stripsizes.push_back(32);
    stripsizes.push_back(60);
    stripsizes.push_back(64);

    std::vector<ome::bioformats::tiff::TileType> tiletypes;
    tiletypes.push_back(ome::bioformats::tiff::TILE);
    tiletypes.push_back(ome::bioformats::tiff::STRIP);

    std::vector<ome::bioformats::tiff::PlanarConfiguration> planarconfigs;
    planarconfigs.push_back(ome::bioformats::tiff::CONTIG);
    planarconfigs.push_back(ome::bioformats::tiff::SEPARATE);

    std::vector<ome::bioformats::tiff::PhotometricInterpretation> photometricinterps;
    photometricinterps.push_back(ome::bioformats::tiff::MIN_IS_BLACK);
    photometricinterps.push_back(ome::bioformats::tiff::RGB);

    const PT::value_map_type& pixeltypemap = PT::values();
    std::vector<PT> pixeltypes;
    std::transform(pixeltypemap.begin(), pixeltypemap.end(), std::back_inserter(pixeltypes), ptkey);

    for (std::vector<PT>::const_iterator pt = pixeltypes.begin(); pt != pixeltypes.end(); ++pt)
      for (std::vector<ome::bioformats::tiff::PlanarConfiguration>::const_iterator pc = planarconfigs.begin(); pc != planarconfigs.end(); ++pc)
        for (std::vector<ome::bioformats::tiff::PhotometricInterpretation>::const_iterator pi = photometricinterps.begin(); pi != photometricinterps.end(); ++pi)
          {
            for(std::vector<dimension_size_type>::const_iterator wid = tilesizes.begin(); wid != tilesizes.end(); ++wid)
              for(std::vector<dimension_size_type>::const_iterator ht = tilesizes.begin(); ht != tilesizes.end(); ++ht)
                {
                  ret.push_back(PixelTestParameters(*pt, ome::bioformats::tiff::TILE, *pc, *pi, *wid, *ht, true, true));
#ifdef EXTENDED_TESTS
                  ret.push_back(PixelTestParameters(*pt, ome::bioformats::tiff::TILE, *pc, *pi, *wid, *ht, true, false));
                  ret.push_back(PixelTestParameters(*pt, ome::bioformats::tiff::TILE, *pc, *pi, *wid, *ht, false, true));
                  ret.push_back(PixelTestParameters(*pt, ome::bioformats::tiff::TILE, *pc, *pi, *wid, *ht, false, false));
#endif
                }
            for(std::vector<dimension_size_type>::const_iterator rows = stripsizes.begin(); rows != stripsizes.end(); ++rows)
              {
                ret.push_back(PixelTestParameters(*pt, ome::bioformats::tiff::STRIP, *pc, *pi, 64, *rows, true, true));
#ifdef EXTENDED_TESTS
                ret.push_back(PixelTestParameters(*pt, ome::bioformats::tiff::STRIP, *pc, *pi, 64, *rows, true, false));
                ret.push_back(PixelTestParameters(*pt, ome::bioformats::tiff::STRIP, *pc, *pi, 64, *rows, false, true));
                ret.push_back(PixelTestParameters(*pt, ome::bioformats::tiff::STRIP, *pc, *pi, 64, *rows, false, false));
#endif
              }
        }

    return ret;
  }

}

std::vector<PixelTestParameters> pixel_params(pixel_tests());



// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#endif

INSTANTIATE_TEST_CASE_P(TileVariants, TIFFTileTest, ::testing::ValuesIn(tile_params));
INSTANTIATE_TEST_CASE_P(PixelVariants, PixelTest, ::testing::ValuesIn(pixel_params));

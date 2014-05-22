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
#include <vector>

#include <ome/bioformats/tiff/TIFF.h>
#include <ome/bioformats/tiff/IFD.h>
#include <ome/bioformats/tiff/Field.h>
#include <ome/bioformats/tiff/Exception.h>

#include <ome/internal/config.h>

#include <ome/test/config.h>

#include <gtest/gtest.h>

using ome::bioformats::tiff::TIFF;
using ome::bioformats::tiff::IFD;
using ome::bioformats::tiff::directory_index_type;

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

TEST(TIFFTest, Field)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  char *text;
  ifd->getField(270, &text);
}

TEST(TIFFTest, Field0)
{
  std::shared_ptr<TIFF> t(TIFF::open(PROJECT_SOURCE_DIR "/components/specification/samples/2010-06/18x24y5z1t2c8b-text.ome.tiff", "r"));

  std::shared_ptr<IFD> ifd(t->getDirectoryByIndex(0));

  char *text;
  ASSERT_THROW(ifd->getField(0, &text), ome::bioformats::tiff::Exception);
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
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::INKSET).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::MATTEING).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::MAXSAMPLEVALUE).get(value), ome::bioformats::tiff::Exception);
  ASSERT_THROW(ifd->getField(ome::bioformats::tiff::MINSAMPLEVALUE).get(value), ome::bioformats::tiff::Exception);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::PLANARCONFIG).get(value));
  ASSERT_EQ(2, value);
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

  float value;

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
  ASSERT_EQ(24, value);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::IMAGEWIDTH).get(value));
  ASSERT_EQ(18, value);
  ASSERT_NO_THROW(ifd->getField(ome::bioformats::tiff::ROWSPERSTRIP).get(value));
  ASSERT_EQ(1, value);
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

#ifdef TIFF_HAVE_FIELDINFO
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

  ASSERT_EQ(false, count);
}

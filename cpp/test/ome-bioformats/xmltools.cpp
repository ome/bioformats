/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * %%
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

#include <fstream>

#include <ome/bioformats/XMLTools.h>

#include <ome/common/xml/Platform.h>

#include <ome/test/test.h>
#include <ome/test/io.h>

TEST(XMLTools, Escape)
{
  std::string original("'Test'&<\"Escape\">");
  std::string expected("&apos;Test&apos;&amp;&lt;&quot;Escape&quot;&gt;");

  std::string observed = ome::bioformats::escapeXML(original);
  ASSERT_EQ(expected, observed);
}

TEST(XMLTools, Filter)
{
  std::string original("\r\nTest\v\v\t");
  original += '\0';
  original += '\0';
  original += "\b\b&amp;&#&gt;";
  std::string expected("\r\nTest\t&amp;&amp;#&gt;");

  std::string observed = ome::bioformats::sanitizeXML(original);
  ASSERT_EQ(expected, observed);
}

class XMLToolsFileTestParameters
{
public:
  std::string filename;
  bool valid;

  XMLToolsFileTestParameters(const std::string& filename,
                             bool               valid):
    filename(filename),
    valid(valid)
  {}
};

class XMLToolsFileTest : public ::testing::TestWithParam<XMLToolsFileTestParameters>
{
public:
  ome::common::xml::Platform plat;
};

TEST_P(XMLToolsFileTest, ValidateXML)
{
  const XMLToolsFileTestParameters& params = GetParam();

  std::string data;
  readFile(params.filename, data);

  if (params.valid)
    {
      ASSERT_TRUE(ome::bioformats::validateXML(data));
    }
  else
    {
      ASSERT_FALSE(ome::bioformats::validateXML(data));
    }
}

XMLToolsFileTestParameters params[] =
  {
    XMLToolsFileTestParameters(PROJECT_SOURCE_DIR "/components/specification/samples/2012-06/18x24y5z5t2c8b-text.ome", true),
    XMLToolsFileTestParameters(PROJECT_SOURCE_DIR "/cpp/test/ome-common/data/18x24y5z5t2c8b-text-invalid.ome", false),
    XMLToolsFileTestParameters(PROJECT_SOURCE_DIR "/cpp/test/ome-common/data/18x24y5z5t2c8b-text-invalid2.ome", false)
  };

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(XMLToolsFileVariants, XMLToolsFileTest, ::testing::ValuesIn(params));

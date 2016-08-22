/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2016 Open Microscopy Environment:
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
#include <fstream>

#include <boost/filesystem.hpp>
#include <boost/filesystem/fstream.hpp>

#include <ome/common/xsl/Platform.h>

#include <ome/xml/OMEEntityResolver.h>
#include <ome/xml/OMETransform.h>
#include <ome/xml/OMETransformResolver.h>
#include <ome/xml/version.h>

#include <ome/test/test.h>
#include <ome/test/io.h>

using boost::filesystem::path;
using boost::filesystem::directory_iterator;

struct TransformTestParameters
{
  path file;
  bool throws;
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const TransformTestParameters& p)
{
  return os << p.file;
}

namespace
{

  std::vector<TransformTestParameters>
  find_transform_tests()
  {
    std::vector<TransformTestParameters> params;

    ome::xml::OMETransformResolver tr;
    std::set<std::string> versions = tr.schema_versions();

    path samplesdir(PROJECT_SOURCE_DIR "/components/specification/samples");
    if (exists(samplesdir) && is_directory(samplesdir))
      {
        for (directory_iterator si(samplesdir); si != directory_iterator(); ++si)
          {
            if (versions.find(si->path().filename().string()) == versions.end())
              continue; // Not a schema directory with transforms.
            path schemadir(si->path());
            if (exists(schemadir) && is_directory(schemadir))
              {
                for (directory_iterator fi(schemadir); fi != directory_iterator(); ++fi)
                  {
                    TransformTestParameters p;
                    p.file = *fi;
                    p.throws = false;

                    // 2008-09/instrument.ome.xml
                    if (schemadir.filename() == path("2008-09") &&
                        p.file.filename() == path("instrument.ome.xml"))
                      continue;
                    // timestampannotation.ome.xml - Contains non-POSIX timestamps.
                    if (p.file.filename() == path("timestampannotation.ome.xml"))
                      continue;

                    if (p.file.extension() == path(".ome") ||
                        p.file.extension() == path(".xml"))
                      params.push_back(p);
                  }
              }
          }
      }

    return params;
  }

}

std::vector<TransformTestParameters> transform_params(find_transform_tests());

class TransformTest : public ::testing::TestWithParam<TransformTestParameters>
{
public:
  ome::common::xsl::Platform xslplat;
  ome::xml::OMEEntityResolver entity_resolver;
  ome::xml::OMETransformResolver transform_resolver;

  TransformTest():
    ::testing::TestWithParam<TransformTestParameters>(),
    entity_resolver(),
    transform_resolver()
  {
  }

  void SetUp()
  {
    const TransformTestParameters& params = GetParam();
    std::cout << "Source file " << params.file << '\n';
  }
};

TEST_P(TransformTest, TransformFileToString)
{
  const TransformTestParameters& params = GetParam();

  std::string result;
  if(params.throws)
    {
      ASSERT_THROW(ome::xml::transform(OME_XML_MODEL_VERSION, params.file, result,
                                       entity_resolver, transform_resolver),
                   std::runtime_error);
    }
  else
    {
      ASSERT_NO_THROW(ome::xml::transform(OME_XML_MODEL_VERSION, params.file, result,
                                          entity_resolver, transform_resolver));

      ASSERT_TRUE(!result.empty());

      ome::common::xml::dom::Document resultdoc(ome::common::xml::dom::createDocument(result, entity_resolver));
      ASSERT_EQ(std::string(OME_XML_MODEL_VERSION),
                ome::xml::getModelVersion(resultdoc));
    }
}

TEST_P(TransformTest, TransformStreamToString)
{
  const TransformTestParameters& params = GetParam();

  boost::filesystem::ifstream input(params.file);

  std::string result;
  if(params.throws)
    {
      ASSERT_THROW(ome::xml::transform(OME_XML_MODEL_VERSION, params.file, result,
                                       entity_resolver, transform_resolver),
                   std::runtime_error);
    }
  else
    {
      ASSERT_NO_THROW(ome::xml::transform(OME_XML_MODEL_VERSION, params.file, result,
                                          entity_resolver, transform_resolver));

      ASSERT_TRUE(!result.empty());

      ome::common::xml::dom::Document resultdoc(ome::common::xml::dom::createDocument(result, entity_resolver));
      ASSERT_EQ(std::string(OME_XML_MODEL_VERSION),
                ome::xml::getModelVersion(resultdoc));
    }
}

TEST_P(TransformTest, TransformStringToString)
{
  const TransformTestParameters& params = GetParam();

  std::string input;
  readFile(params.file, input);
  
  std::string result;
  if(params.throws)
    {
      ASSERT_THROW(ome::xml::transform(OME_XML_MODEL_VERSION, input, result,
                                       entity_resolver, transform_resolver),
                   std::runtime_error);
    }
  else
    {
      ASSERT_NO_THROW(ome::xml::transform(OME_XML_MODEL_VERSION, input, result,
                                          entity_resolver, transform_resolver));

      ASSERT_TRUE(!result.empty());

      ome::common::xml::dom::Document resultdoc(ome::common::xml::dom::createDocument(result, entity_resolver));
      ASSERT_EQ(std::string(OME_XML_MODEL_VERSION),
                ome::xml::getModelVersion(resultdoc));
    }
}

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(TransformVariants, TransformTest, ::testing::ValuesIn(transform_params));

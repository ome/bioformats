/*
 * #%L
 * OME-XML C++ library for working with OME-XML metadata structures.
 * %%
 * Copyright © 2014 - 2015 Open Microscopy Environment:
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

#include <boost/filesystem.hpp>

#include <ome/internal/config.h>
#include <ome/internal/version.h>

#include <ome/test/config.h>
#include <ome/test/test.h>

#include <ome/common/xml/Platform.h>
#include <ome/common/xml/dom/Document.h>

#include <ome/xml/Document.h>

#include <ome/xml/meta/OMEXMLMetadata.h>

using namespace boost::filesystem;

struct ModelTestParameters
{
  path file;
};

template<class charT, class traits>
inline std::basic_ostream<charT,traits>&
operator<< (std::basic_ostream<charT,traits>& os,
            const ModelTestParameters& p)
{
  return os << p.file;
}

namespace
{

  std::vector<ModelTestParameters>
  find_model_tests()
  {
    std::vector<ModelTestParameters> params;

    /// @todo Use the correct model version when available.
    /// @todo Use all model versions when transforms available.
    path dir(PROJECT_SOURCE_DIR "/components/specification/samples/" "2013-06");
    if (exists(dir) && is_directory(dir))
      {
        for(directory_iterator i(dir); i != directory_iterator(); ++i)
          {
            ModelTestParameters p;
            p.file = *i;

            // Contains non-POSIX timestamps.
            if (p.file.filename() == path("2013-06-datetests.ome"))
              continue;

            if (p.file.extension() == path(".ome") ||
                p.file.extension() == path(".xml"))
              params.push_back(p);
          }
      }

    return params;
  }

}

std::vector<ModelTestParameters> tile_params(find_model_tests());

class ModelTest : public ::testing::TestWithParam<ModelTestParameters>
{
public:
  ome::common::xml::Platform xmlplat;
  std::string xmltext;
  ome::common::xml::dom::Document doc;

  virtual void SetUp()
  {
    const ModelTestParameters& params = GetParam();

    std::ifstream in(params.file.c_str());

    ASSERT_TRUE(!!in);
    in.seekg(0, std::ios::end);
    xmltext.reserve(in.tellg());
    in.seekg(0, std::ios::beg);

    xmltext.assign(std::istreambuf_iterator<char>(in),
                   std::istreambuf_iterator<char>());

    doc = ome::xml::createDocument(xmltext);
  }
};


TEST_P(ModelTest, Parse)
{
  const ModelTestParameters& params = GetParam();
}

TEST_P(ModelTest, Update)
{
  const ModelTestParameters& params = GetParam();

  // Read into OME model objects.
  ome::xml::meta::OMEXMLMetadata meta;
  ome::xml::model::detail::OMEModel model;
  ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(meta.getRoot()));
  ome::common::xml::dom::Element docroot(doc.getDocumentElement());
  root->update(docroot, model);
}

TEST_P(ModelTest, CreateXML)
{
  const ModelTestParameters& params = GetParam();

  // Read into OME model objects.
  ome::xml::meta::OMEXMLMetadata meta;
  ome::xml::model::detail::OMEModel model;
  ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(meta.getRoot()));
  ome::common::xml::dom::Element docroot(doc.getDocumentElement());
  root->update(docroot, model);

  // Dump as XML string.
  std::string omexml(meta.dumpXML());

  // Validate XML.
  ASSERT_NO_THROW(ome::xml::createDocument(omexml));
}

TEST_P(ModelTest, CreateXMLRoundTrip)
{
  const ModelTestParameters& params = GetParam();

  // Read into OME model objects.
  ome::xml::meta::OMEXMLMetadata meta;
  ome::xml::model::detail::OMEModel model;
  ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root(ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(meta.getRoot()));
  ome::common::xml::dom::Element docroot(doc.getDocumentElement());
  root->update(docroot, model);

  // Dump as XML string.
  std::string omexml(meta.dumpXML());

  // Validate XML.
  ASSERT_NO_THROW(ome::xml::createDocument(omexml));

  // Repeat read and write.

  // Read into OME model objects.
  ome::common::xml::dom::Document doc2(ome::xml::createDocument(omexml));
  ome::xml::meta::OMEXMLMetadata meta2;
  ome::xml::model::detail::OMEModel model2;
  ome::compat::shared_ptr<ome::xml::meta::OMEXMLMetadataRoot> root2(ome::compat::dynamic_pointer_cast<ome::xml::meta::OMEXMLMetadataRoot>(meta2.getRoot()));
  ome::common::xml::dom::Element docroot2(doc2.getDocumentElement());
  root2->update(docroot2, model2);

  // Dump as XML string.
  std::string omexml2(meta2.dumpXML());

  // Validate XML.
  ASSERT_NO_THROW(ome::xml::createDocument(omexml2));

  ASSERT_EQ(omexml, omexml2);
}

TEST(ModelObject, StripNamespacePrefix)
{
  ASSERT_EQ(std::string("OME"), ome::xml::model::detail::OMEModelObject::stripNamespacePrefix("OME:OME"));
  ASSERT_EQ(std::string("Image"), ome::xml::model::detail::OMEModelObject::stripNamespacePrefix("OME:Image"));
  ASSERT_NE(std::string("Bin:BinData"), ome::xml::model::detail::OMEModelObject::stripNamespacePrefix("Bin:BinData"));
}

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(ModelVariants, ModelTest, ::testing::ValuesIn(tile_params));

/*
 * #%L
 * OME-XERCES C++ library for working with Xerces C++.
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

#include <ome/common/xml/EntityResolver.h>
#include <ome/common/xml/String.h>
#include <ome/common/xml/Platform.h>
#include <ome/common/xml/dom/Document.h>

#include <ome/test/config.h>

#include <ome/test/test.h>

#include <fstream>
#include <stdexcept>
#include <vector>

namespace xml = ome::common::xml;

class XercesTestParameters
{
public:
  enum Resolver
    {
      NONE,
      FILES,
      CATALOG
    };

  std::string filename;
  Resolver resolver;
  bool valid;

  XercesTestParameters(const std::string& filename,
                       Resolver           resolver,
                       bool               valid):
    filename(filename),
    resolver(resolver),
    valid(valid)
  {}
};

class XercesTest : public ::testing::TestWithParam<XercesTestParameters>
{
public:
  xml::Platform plat;

  xml::EntityResolver resolver;

  virtual void SetUp()
  {
    const XercesTestParameters& params = GetParam();

    if (params.resolver == XercesTestParameters::FILES)
      {
        resolver.registerEntity("http://www.w3.org/2001/XMLSchema",
                               boost::filesystem::path(PROJECT_SOURCE_DIR "/components/specification/released-schema/external/XMLSchema.xsd"));

        resolver.registerEntity("http://www.w3.org/2001/xml.xsd",
                                boost::filesystem::path(PROJECT_SOURCE_DIR "/components/specification/released-schema/external/xml.xsd"));

        resolver.registerEntity("http://www.openmicroscopy.org/Schemas/OME/2012-06/ome.xsd",
                                boost::filesystem::path(PROJECT_SOURCE_DIR "/components/specification/released-schema/2012-06/ome.xsd"));

        resolver.registerEntity("http://www.openmicroscopy.org/Schemas/BinaryFile/2012-06/BinaryFile.xsd",
                                boost::filesystem::path(PROJECT_SOURCE_DIR "/components/specification/released-schema/2012-06/BinaryFile.xsd"));

        resolver.registerEntity("http://www.openmicroscopy.org/Schemas/SA/2012-06/SA.xsd",
                                boost::filesystem::path(PROJECT_SOURCE_DIR "/components/specification/released-schema/2012-06/SA.xsd"));

        resolver.registerEntity("http://www.openmicroscopy.org/Schemas/SPW/2012-06/SPW.xsd",
                                boost::filesystem::path(PROJECT_SOURCE_DIR "/components/specification/released-schema/2012-06/SPW.xsd"));

        resolver.registerEntity("http://www.openmicroscopy.org/Schemas/ROI/2012-06/ROI.xsd",
                                boost::filesystem::path(PROJECT_SOURCE_DIR "/components/specification/released-schema/2012-06/ROI.xsd"));
      }
    else if (params.resolver == XercesTestParameters::CATALOG)
      {
        resolver.registerCatalog(boost::filesystem::path(PROJECT_SOURCE_DIR "/components/specification/released-schema/catalog.xml"));
      }

  }
};

TEST_P(XercesTest, Node)
{
  xml::dom::Node node;
  ASSERT_FALSE(node);
}

TEST_P(XercesTest, DefaultElement)
{
  xml::dom::Element element;
  ASSERT_FALSE(element);
}

TEST_P(XercesTest, DefaultNodeList)
{
  xml::dom::NodeList nodelist;
  ASSERT_FALSE(nodelist);
}

TEST_P(XercesTest, DefaultDocument)
{
  xml::dom::Document document;
  ASSERT_FALSE(document);
}

TEST_P(XercesTest, EmptyDocument)
{
  xml::dom::Document document(ome::common::xml::dom::createEmptyDocument("root"));
  ASSERT_TRUE(document);
}

TEST_P(XercesTest, EmptyDocumentNS)
{
  xml::dom::Document document(ome::common::xml::dom::createEmptyDocument("http://example.com/test/namespace", "root"));
  ASSERT_TRUE(document);
}

TEST_P(XercesTest, EmptyDocumentCreateElement)
{
  xml::dom::Document document(ome::common::xml::dom::createEmptyDocument("root"));
  ASSERT_TRUE(document);
  xml::dom::Element e(document.createElementNS("http://example.com/test/namespace", "test"));
  xml::dom::Element root(document.getDocumentElement());
  root.appendChild(e);
}

TEST_P(XercesTest, EmptyDocumentCreateElementNS)
{
  xml::dom::Document document(ome::common::xml::dom::createEmptyDocument("http://example.com/test/namespace1", "root"));
  ASSERT_TRUE(document);
  xml::dom::Element e(document.createElementNS("http://example.com/test/namespace2", "test"));
  xml::dom::Element root(document.getDocumentElement());
  root.appendChild(e);
}

TEST_P(XercesTest, DocumentFromFile)
{
  const XercesTestParameters& params = GetParam();

  xml::dom::Document doc;
  if (params.valid)
    {
      ASSERT_NO_THROW(doc = ome::common::xml::dom::createDocument(boost::filesystem::path(params.filename), resolver));
      ASSERT_TRUE(doc != 0);
    }
  else
    {
      ASSERT_THROW(doc = ome::common::xml::dom::createDocument(boost::filesystem::path(params.filename), resolver), std::runtime_error);
      ASSERT_TRUE(doc == 0);
    }
}

TEST_P(XercesTest, DocumentFromStream)
{
  const XercesTestParameters& params = GetParam();

  std::ifstream in(params.filename.c_str());
  ASSERT_TRUE(!!in);

  xml::dom::Document doc;
  if (params.valid)
    {
      ASSERT_NO_THROW(doc = ome::common::xml::dom::createDocument(in, resolver));
      ASSERT_TRUE(doc != 0);
    }
  else
    {
      ASSERT_THROW(doc = ome::common::xml::dom::createDocument(in, resolver), std::runtime_error);
      ASSERT_TRUE(doc == 0);
    }
}

TEST_P(XercesTest, DocumentFromString)
{
  const XercesTestParameters& params = GetParam();

  std::string data;

  std::ifstream in(params.filename.c_str());

  ASSERT_TRUE(!!in);
  in.seekg(0, std::ios::end);
  data.reserve(in.tellg());
  in.seekg(0, std::ios::beg);

  data.assign(std::istreambuf_iterator<char>(in),
              std::istreambuf_iterator<char>());

  xml::dom::Document doc;
  if (params.valid)
    {
      ASSERT_NO_THROW(doc = ome::common::xml::dom::createDocument(data, resolver));
      ASSERT_TRUE(doc != 0);
    }
  else
    {
      ASSERT_THROW(doc = ome::common::xml::dom::createDocument(data, resolver), std::runtime_error);
      ASSERT_TRUE(doc == 0);
    }
}

TEST_P(XercesTest, ResetDocument)
{
  const XercesTestParameters& params = GetParam();

  if (params.valid)
    {
      xml::dom::Document doc(ome::common::xml::dom::createDocument(boost::filesystem::path(params.filename), resolver));

      ASSERT_TRUE(doc);
      ASSERT_TRUE(doc.get() != 0);
      ASSERT_NO_THROW(doc.reset());
      ASSERT_FALSE(doc);
      ASSERT_TRUE(doc.get() == 0);
    }
}

TEST_P(XercesTest, DocumentToFile)
{
  const XercesTestParameters& params = GetParam();

  if (params.valid)
    {
      std::string data;

      std::ifstream in(params.filename.c_str());
      in.seekg(0, std::ios::end);
      data.reserve(in.tellg());
      in.seekg(0, std::ios::beg);

      data.assign(std::istreambuf_iterator<char>(in),
                  std::istreambuf_iterator<char>());

      xml::dom::Document doc(ome::common::xml::dom::createDocument(data, resolver));
      boost::filesystem::path file(PROJECT_BINARY_DIR "/cpp/test/ome-common");
      std::string name("test-document-output-");
      name += boost::filesystem::path(params.filename).filename().generic_string();
      file /= name;

      std::string s;
      ome::common::xml::dom::writeDocument(doc, s);
      ome::common::xml::dom::writeDocument(doc, file);

      // Can't compare the original directly so reparse and check it
      // round-trips identically.
      xml::dom::Document doc2(ome::common::xml::dom::createDocument(file, resolver));
      std::string s2;
      ome::common::xml::dom::writeDocument(doc2, s2);

      ASSERT_EQ(s, s2);
    }
}

TEST_P(XercesTest, DocumentWriteString)
{
  const XercesTestParameters& params = GetParam();

  if (params.valid)
    {
      std::string data;

      std::ifstream in(params.filename.c_str());
      in.seekg(0, std::ios::end);
      data.reserve(in.tellg());
      in.seekg(0, std::ios::beg);

      data.assign(std::istreambuf_iterator<char>(in),
                  std::istreambuf_iterator<char>());

      xml::dom::Document doc(ome::common::xml::dom::createDocument(data, resolver));
      std::string s;
      ome::common::xml::dom::writeDocument(doc, s);

      // Can't compare the original directly so reparse and check it
      // round-trips identically.
      xml::dom::Document doc2(ome::common::xml::dom::createDocument(s, resolver));
      std::string s2;
      ome::common::xml::dom::writeDocument(doc2, s2);

      ASSERT_EQ(s, s2);
    }
}

TEST_P(XercesTest, DocumentWriteStringParameters)
{
  const XercesTestParameters& params = GetParam();

  if (params.valid)
    {
      std::string data;

      std::ifstream in(params.filename.c_str());
      in.seekg(0, std::ios::end);
      data.reserve(in.tellg());
      in.seekg(0, std::ios::beg);

      data.assign(std::istreambuf_iterator<char>(in),
                  std::istreambuf_iterator<char>());

      ome::common::xml::dom::WriteParameters p;
      p.prettyPrint=true;
      p.whitespace=false;
      p.xmlDeclaration=false;
      p.datatypeNormalization=true;
      p.canonicalForm=true;

      xml::dom::Document doc(ome::common::xml::dom::createDocument(data, resolver));
      std::string s;
      ome::common::xml::dom::writeDocument(doc, s, p);

      // Can't compare the original directly so reparse and check it
      // round-trips identically.
      xml::dom::Document doc2(ome::common::xml::dom::createDocument(s, resolver));
      std::string s2;
      ome::common::xml::dom::writeDocument(doc2, s2, p);

      ASSERT_EQ(s, s2);

      ome::common::xml::dom::WriteParameters p2;
      p.prettyPrint=false;
      p.whitespace=true;
      p.xmlDeclaration=true;
      p.datatypeNormalization=false;
      p.canonicalForm=false;

      xml::dom::Document doc3(ome::common::xml::dom::createDocument(s, resolver));
      std::string s3;
      ome::common::xml::dom::writeDocument(doc3, s3, p2);

      ASSERT_NE(s, s3);
      ASSERT_NE(s2, s3);
    }
}

TEST_P(XercesTest, DocumentWriteStream)
{
  const XercesTestParameters& params = GetParam();

  if (params.valid)
    {
      std::string data;

      std::ifstream in(params.filename.c_str());
      in.seekg(0, std::ios::end);
      data.reserve(in.tellg());
      in.seekg(0, std::ios::beg);

      data.assign(std::istreambuf_iterator<char>(in),
                  std::istreambuf_iterator<char>());

      xml::dom::Document doc(ome::common::xml::dom::createDocument(data, resolver));
      std::ostringstream os;
      ome::common::xml::dom::writeDocument(doc, os);

      // Can't compare the original directly so reparse and check it
      // round-trips identically.
      xml::dom::Document doc2(ome::common::xml::dom::createDocument(os.str(), resolver));
      std::ostringstream os2;
      ome::common::xml::dom::writeDocument(doc2, os2);

      ASSERT_EQ(os.str(), os2.str());
    }
}

TEST_P(XercesTest, NodeToFile)
{
  const XercesTestParameters& params = GetParam();

  if (params.valid)
    {
      std::string data;

      std::ifstream in(params.filename.c_str());
      in.seekg(0, std::ios::end);
      data.reserve(in.tellg());
      in.seekg(0, std::ios::beg);

      data.assign(std::istreambuf_iterator<char>(in),
                  std::istreambuf_iterator<char>());

      xml::dom::Document doc(ome::common::xml::dom::createDocument(data, resolver));
      boost::filesystem::path file(PROJECT_BINARY_DIR "/cpp/test/ome-common");
      std::string name("test-document-output-");
      name += boost::filesystem::path(params.filename).filename().generic_string();
      file /= name;

      std::string s;
      ome::common::xml::dom::writeNode(doc, s);
      ome::common::xml::dom::writeNode(doc, file);

      // Can't compare the original directly so reparse and check it
      // round-trips identically.
      xml::dom::Document doc2(ome::common::xml::dom::createDocument(file, resolver));
      std::string s2;
      ome::common::xml::dom::writeNode(doc2, s2);

      ASSERT_EQ(s, s2);
    }
}

TEST_P(XercesTest, NodeWriteString)
{
  const XercesTestParameters& params = GetParam();

  if (params.valid)
    {
      std::string data;

      std::ifstream in(params.filename.c_str());
      in.seekg(0, std::ios::end);
      data.reserve(in.tellg());
      in.seekg(0, std::ios::beg);

      data.assign(std::istreambuf_iterator<char>(in),
                  std::istreambuf_iterator<char>());

      xml::dom::Document doc(ome::common::xml::dom::createDocument(data, resolver));
      std::string s;
      ome::common::xml::dom::writeNode(doc, s);

      // Can't compare the original directly so reparse and check it
      // round-trips identically.
      xml::dom::Document doc2(ome::common::xml::dom::createDocument(s, resolver));
      std::string s2;
      ome::common::xml::dom::writeNode(doc2, s2);

      ASSERT_EQ(s, s2);
    }
}

TEST_P(XercesTest, NodeWriteStringParameters)
{
  const XercesTestParameters& params = GetParam();

  if (params.valid)
    {
      std::string data;

      std::ifstream in(params.filename.c_str());
      in.seekg(0, std::ios::end);
      data.reserve(in.tellg());
      in.seekg(0, std::ios::beg);

      data.assign(std::istreambuf_iterator<char>(in),
                  std::istreambuf_iterator<char>());

      ome::common::xml::dom::WriteParameters p;
      p.prettyPrint=true;
      p.whitespace=false;
      p.xmlDeclaration=false;
      p.datatypeNormalization=true;
      p.canonicalForm=true;

      xml::dom::Document doc(ome::common::xml::dom::createDocument(data, resolver));
      std::string s;
      ome::common::xml::dom::writeNode(doc, s, p);

      // Can't compare the original directly so reparse and check it
      // round-trips identically.
      xml::dom::Document doc2(ome::common::xml::dom::createDocument(s, resolver));
      std::string s2;
      ome::common::xml::dom::writeNode(doc2, s2, p);

      ASSERT_EQ(s, s2);

      ome::common::xml::dom::WriteParameters p2;
      p.prettyPrint=false;
      p.whitespace=true;
      p.xmlDeclaration=true;
      p.datatypeNormalization=false;
      p.canonicalForm=false;

      xml::dom::Document doc3(ome::common::xml::dom::createDocument(s, resolver));
      std::string s3;
      ome::common::xml::dom::writeNode(doc3, s3, p2);

      ASSERT_NE(s, s3);
      ASSERT_NE(s2, s3);
    }
}

TEST_P(XercesTest, NodeWriteStream)
{
  const XercesTestParameters& params = GetParam();

  if (params.valid)
    {
      std::string data;

      std::ifstream in(params.filename.c_str());
      in.seekg(0, std::ios::end);
      data.reserve(in.tellg());
      in.seekg(0, std::ios::beg);

      data.assign(std::istreambuf_iterator<char>(in),
                  std::istreambuf_iterator<char>());

      xml::dom::Document doc(ome::common::xml::dom::createDocument(data, resolver));
      std::ostringstream os;
      ome::common::xml::dom::writeNode(doc, os);

      // Can't compare the original directly so reparse and check it
      // round-trips identically.
      xml::dom::Document doc2(ome::common::xml::dom::createDocument(os.str(), resolver));
      std::ostringstream os2;
      ome::common::xml::dom::writeNode(doc2, os2);

      ASSERT_EQ(os.str(), os2.str());
    }
}

XercesTestParameters params[] =
  {
    //    XercesTestParameters(PROJECT_SOURCE_DIR "/components/specification/samples/2012-06/18x24y5z5t2c8b-text.ome", XercesTestParameters::NONE),
    XercesTestParameters(PROJECT_SOURCE_DIR "/components/specification/samples/2012-06/18x24y5z5t2c8b-text.ome", XercesTestParameters::FILES, true),
    XercesTestParameters(PROJECT_SOURCE_DIR "/components/specification/samples/2012-06/18x24y5z5t2c8b-text.ome", XercesTestParameters::CATALOG, true),
    XercesTestParameters(PROJECT_SOURCE_DIR "/cpp/test/ome-common/data/18x24y5z5t2c8b-text-invalid.ome", XercesTestParameters::CATALOG, false),
    XercesTestParameters(PROJECT_SOURCE_DIR "/cpp/test/ome-common/data/18x24y5z5t2c8b-text-invalid2.ome", XercesTestParameters::CATALOG, false)
  };

// Disable missing-prototypes warning for INSTANTIATE_TEST_CASE_P;
// this is solely to work around a missing prototype in gtest.
#ifdef __GNUC__
#  if defined __clang__ || defined __APPLE__
#    pragma GCC diagnostic ignored "-Wmissing-prototypes"
#  endif
#  pragma GCC diagnostic ignored "-Wmissing-declarations"
#endif

INSTANTIATE_TEST_CASE_P(XercesVariants, XercesTest, ::testing::ValuesIn(params));
